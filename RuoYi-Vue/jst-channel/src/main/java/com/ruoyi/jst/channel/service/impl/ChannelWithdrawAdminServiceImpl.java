package com.ruoyi.jst.channel.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.channel.domain.JstRebateLedger;
import com.ruoyi.jst.channel.domain.JstRebateSettlement;
import com.ruoyi.jst.channel.dto.WithdrawAuditDTO;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.enums.RebateSettlementStatus;
import com.ruoyi.jst.channel.mapper.JstRebateSettlementMapper;
import com.ruoyi.jst.channel.mapper.RebateLedgerMapperExt;
import com.ruoyi.jst.channel.mapper.RebateSettlementMapperExt;
import com.ruoyi.jst.channel.mapper.lookup.PaymentPayRecordLookupMapper;
import com.ruoyi.jst.channel.payout.PayoutService;
import com.ruoyi.jst.channel.service.ChannelWithdrawAdminService;
import com.ruoyi.jst.channel.vo.RebateLedgerListVO;
import com.ruoyi.jst.channel.vo.WithdrawAdminDetailVO;
import com.ruoyi.jst.channel.vo.WithdrawAdminListVO;
import com.ruoyi.jst.channel.vo.WithdrawDetailVO;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Admin withdrawal audit and payout service.
 */
@Service
public class ChannelWithdrawAdminServiceImpl implements ChannelWithdrawAdminService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private RebateSettlementMapperExt rebateSettlementMapperExt;

    @Autowired
    private RebateLedgerMapperExt rebateLedgerMapperExt;

    @Autowired
    private JstRebateSettlementMapper jstRebateSettlementMapper;

    @Autowired
    private PaymentPayRecordLookupMapper paymentPayRecordLookupMapper;

    @Autowired
    private List<PayoutService> payoutServices;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Value("${jst.payout.enabled:mock}")
    private String payoutMode;

    @Override
    public List<WithdrawAdminListVO> selectAdminList(WithdrawQueryReqDTO query) {
        WithdrawQueryReqDTO finalQuery = query == null ? new WithdrawQueryReqDTO() : query;
        return rebateSettlementMapperExt.selectAdminList(finalQuery);
    }

    @Override
    public WithdrawAdminDetailVO getAdminDetail(Long settlementId) {
        JstRebateSettlement settlement = requireSettlement(settlementId);
        WithdrawAdminDetailVO detail = rebateSettlementMapperExt.selectAdminDetail(settlementId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                    BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
        }
        detail.setPayeeAccount(parsePayeeAccount(settlement.getBankAccountSnapshot()));
        detail.setInvoiceInfo(parseInvoiceInfo(settlement.getRemark()));
        detail.setLedgerItems(rebateLedgerMapperExt.selectBySettlementId(settlementId));
        detail.setPreviewNegativeOffset(buildPreview(settlement.getChannelId(), settlement.getApplyAmount()));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long settlementId, WithdrawAuditDTO req) {
        executeWithWithdrawLock("lock:channel:withdraw:audit:" + settlementId, 3, 10, () -> {
            JstRebateSettlement previewSettlement = requireSettlement(settlementId);
            return executeWithWithdrawLock("lock:channel:withdraw:offset:" + previewSettlement.getChannelId(), 5, 10, () -> {
                Date now = DateUtils.getNowDate();
                String operator = currentOperatorName();
                JstRebateSettlement settlement = rebateSettlementMapperExt.selectBySettlementIdForUpdate(settlementId);
                assertPending(settlement);

                NegativeOffsetDecision decision = calculateNegativeOffset(settlement.getChannelId(),
                        settlement.getApplyAmount(), true);
                if (decision.actualPayAmount.compareTo(ZERO_AMOUNT) < 0) {
                    throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_NEGATIVE_OVERFLOW.message(),
                            BizErrorCode.JST_CHANNEL_WITHDRAW_NEGATIVE_OVERFLOW.code());
                }
                if (rebateSettlementMapperExt.approveSettlement(settlementId, RebateSettlementStatus.PENDING.dbValue(),
                        decision.offsetAmount, decision.actualPayAmount, req.getAuditRemark(), operator, now) == 0) {
                    throwDataTampered();
                }
                if (!decision.negativeLedgerIds.isEmpty()) {
                    int bound = rebateLedgerMapperExt.bindSettlementToNegativeLedgers(settlement.getChannelId(),
                            settlementId, decision.negativeLedgerIds, operator, now);
                    if (bound != decision.negativeLedgerIds.size()) {
                        throwDataTampered();
                    }
                }
                return null;
            });
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long settlementId, WithdrawAuditDTO req) {
        executeWithWithdrawLock("lock:channel:withdraw:audit:" + settlementId, 3, 10, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            JstRebateSettlement settlement = rebateSettlementMapperExt.selectBySettlementIdForUpdate(settlementId);
            assertPending(settlement);
            if (rebateSettlementMapperExt.rejectSettlement(settlementId, RebateSettlementStatus.PENDING.dbValue(),
                    req.getAuditRemark(), operator, now) == 0) {
                throwDataTampered();
            }
            int restored = rebateLedgerMapperExt.restoreWithdrawableBySettlementId(settlement.getChannelId(),
                    settlementId, operator, now);
            if (restored <= 0) {
                throwDataTampered();
            }
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long settlementId) {
        executeWithWithdrawLock("lock:channel:withdraw:execute:" + settlementId, 3, 15, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            Long operatorId = SecurityUtils.getUserId();
            JstRebateSettlement settlement = rebateSettlementMapperExt.selectBySettlementIdForUpdate(settlementId);
            requireActiveSettlement(settlement);
            RebateSettlementStatus currentStatus = RebateSettlementStatus.fromDb(settlement.getStatus());
            if (currentStatus == RebateSettlementStatus.PAID) {
                return null;
            }
            if (currentStatus != RebateSettlementStatus.APPROVED) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                        BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
            }

            BigDecimal actualPayAmount = safeAmount(settlement.getActualPayAmount());
            if (settlement.getActualPayAmount() == null) {
                actualPayAmount = safeAmount(settlement.getApplyAmount())
                        .subtract(safeAmount(settlement.getNegativeOffsetAmount()))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            String payNo = snowflakeIdWorker.nextBizNo("PO");
            PayoutService.PayoutRequest payoutRequest = new PayoutService.PayoutRequest();
            payoutRequest.setSettlementId(settlementId);
            payoutRequest.setPayNo(payNo);
            payoutRequest.setAmount(actualPayAmount);
            payoutRequest.setPayeeAccount(settlement.getBankAccountSnapshot());

            PayoutService.PayoutResult payoutResult = invokePayout(payoutRequest);
            if (paymentPayRecordLookupMapper.insertPayRecord(payNo, "rebate_withdraw", settlementId, "channel",
                    settlement.getChannelId(), actualPayAmount, settlement.getBankAccountSnapshot(), now,
                    payoutResult.getVoucherUrl(), operatorId, operator, now, operator, now,
                    payoutResult.getMessage()) != 1) {
                throwDataTampered();
            }
            if (rebateSettlementMapperExt.markPaidSettlement(settlementId, RebateSettlementStatus.APPROVED.dbValue(),
                    payoutResult.getVoucherUrl(), now, operator, now) == 0) {
                throwDataTampered();
            }
            int updated = rebateLedgerMapperExt.markPaidBySettlementId(settlement.getChannelId(), settlementId,
                    operator, now);
            if (updated <= 0) {
                throwDataTampered();
            }
            return null;
        });
    }

    private NegativeOffsetDecision calculateNegativeOffset(Long channelId, BigDecimal applyAmount, boolean forUpdate) {
        List<JstRebateLedger> negatives = forUpdate
                ? rebateLedgerMapperExt.selectUnboundNegativeByChannelIdForUpdate(channelId)
                : rebateLedgerMapperExt.selectUnboundNegativeByChannelId(channelId);
        if (negatives == null || negatives.isEmpty()) {
            return NegativeOffsetDecision.empty(applyAmount);
        }

        BigDecimal offsetAmount = ZERO_AMOUNT;
        List<Long> negativeLedgerIds = new ArrayList<>();
        List<RebateLedgerListVO> previewItems = new ArrayList<>();
        for (JstRebateLedger negative : negatives) {
            BigDecimal amount = safeAmount(negative.getRebateAmount()).abs();
            offsetAmount = offsetAmount.add(amount).setScale(2, RoundingMode.HALF_UP);
            negativeLedgerIds.add(negative.getLedgerId());

            RebateLedgerListVO item = new RebateLedgerListVO();
            item.setLedgerId(negative.getLedgerId());
            item.setOrderId(negative.getOrderId());
            item.setContestId(negative.getContestId());
            item.setRebateAmount(safeAmount(negative.getRebateAmount()));
            item.setDirection(negative.getDirection());
            item.setStatus(negative.getStatus());
            item.setSettlementId(negative.getSettlementId());
            item.setAccrualTime(negative.getAccrualTime());
            item.setCreateTime(negative.getCreateTime());
            previewItems.add(item);

            if (offsetAmount.compareTo(safeAmount(applyAmount)) >= 0) {
                break;
            }
        }
        BigDecimal actualPayAmount = safeAmount(applyAmount).subtract(offsetAmount).setScale(2, RoundingMode.HALF_UP);
        return new NegativeOffsetDecision(offsetAmount, actualPayAmount, negativeLedgerIds, previewItems);
    }

    private WithdrawAdminDetailVO.PreviewNegativeOffset buildPreview(Long channelId, BigDecimal applyAmount) {
        NegativeOffsetDecision decision = calculateNegativeOffset(channelId, applyAmount, false);
        WithdrawAdminDetailVO.PreviewNegativeOffset preview = new WithdrawAdminDetailVO.PreviewNegativeOffset();
        preview.setOffsetAmount(decision.offsetAmount);
        preview.setActualPayAmount(decision.actualPayAmount);
        preview.setNegativeLedgerItems(decision.previewItems);
        return preview;
    }

    private PayoutService.PayoutResult invokePayout(PayoutService.PayoutRequest request) {
        PayoutService service = resolvePayoutService();
        try {
            PayoutService.PayoutResult result = service.payout(request);
            if (result == null || !result.isSuccess()) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_PAYOUT_FAILED.message(),
                        BizErrorCode.JST_CHANNEL_PAYOUT_FAILED.code());
            }
            return result;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_PAYOUT_FAILED.message(),
                    BizErrorCode.JST_CHANNEL_PAYOUT_FAILED.code());
        }
    }

    private PayoutService resolvePayoutService() {
        Map<String, PayoutService> serviceMap = new LinkedHashMap<>();
        for (PayoutService payoutService : payoutServices) {
            serviceMap.put(payoutService.mode(), payoutService);
        }
        String mode = StringUtils.isBlank(payoutMode) ? "mock" : payoutMode;
        PayoutService service = serviceMap.get(mode);
        if (service == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_PAYOUT_FAILED.message(),
                    BizErrorCode.JST_CHANNEL_PAYOUT_FAILED.code());
        }
        return service;
    }

    private void assertPending(JstRebateSettlement settlement) {
        requireActiveSettlement(settlement);
        if (RebateSettlementStatus.fromDb(settlement.getStatus()) != RebateSettlementStatus.PENDING) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                    BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
        }
    }

    private JstRebateSettlement requireSettlement(Long settlementId) {
        JstRebateSettlement settlement = jstRebateSettlementMapper.selectJstRebateSettlementBySettlementId(settlementId);
        requireActiveSettlement(settlement);
        return settlement;
    }

    private void requireActiveSettlement(JstRebateSettlement settlement) {
        if (settlement == null || !"0".equals(defaultDelFlag(settlement.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                    BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
        }
    }

    private <T> T executeWithWithdrawLock(String lockKey, long waitSec, long leaseSec, SupplierWithException<T> supplier) {
        try {
            return jstLockTemplate.execute(lockKey, waitSec, leaseSec, supplier::get);
        } catch (ServiceException ex) {
            if (Objects.equals(ex.getCode(), BizErrorCode.JST_COMMON_LOCK_TIMEOUT.code())) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_LOCK_TIMEOUT.message(),
                        BizErrorCode.JST_CHANNEL_WITHDRAW_LOCK_TIMEOUT.code());
            }
            throw ex;
        }
    }

    private WithdrawDetailVO.PayeeAccount parsePayeeAccount(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        try {
            return JSON.parseObject(raw, WithdrawDetailVO.PayeeAccount.class);
        } catch (Exception ex) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseInvoiceInfo(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        try {
            return JSON.parseObject(raw, Map.class);
        } catch (Exception ex) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        if (StringUtils.isNotBlank(username)) {
            return username;
        }
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
    }

    private static class NegativeOffsetDecision {
        private final BigDecimal offsetAmount;
        private final BigDecimal actualPayAmount;
        private final List<Long> negativeLedgerIds;
        private final List<RebateLedgerListVO> previewItems;

        private NegativeOffsetDecision(BigDecimal offsetAmount, BigDecimal actualPayAmount, List<Long> negativeLedgerIds,
                                       List<RebateLedgerListVO> previewItems) {
            this.offsetAmount = offsetAmount;
            this.actualPayAmount = actualPayAmount;
            this.negativeLedgerIds = negativeLedgerIds;
            this.previewItems = previewItems;
        }

        private static NegativeOffsetDecision empty(BigDecimal applyAmount) {
            BigDecimal actualPayAmount = applyAmount == null
                    ? ZERO_AMOUNT
                    : applyAmount.setScale(2, RoundingMode.HALF_UP);
            return new NegativeOffsetDecision(ZERO_AMOUNT, actualPayAmount,
                    Collections.emptyList(), Collections.emptyList());
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
