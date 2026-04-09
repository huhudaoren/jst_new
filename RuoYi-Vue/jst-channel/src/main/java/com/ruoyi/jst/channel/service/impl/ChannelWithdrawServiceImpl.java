package com.ruoyi.jst.channel.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.channel.domain.JstRebateLedger;
import com.ruoyi.jst.channel.domain.JstRebateSettlement;
import com.ruoyi.jst.channel.dto.RebateLedgerQueryReqDTO;
import com.ruoyi.jst.channel.dto.WithdrawApplyDTO;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.enums.RebateSettlementStatus;
import com.ruoyi.jst.channel.mapper.JstRebateSettlementMapper;
import com.ruoyi.jst.channel.mapper.RebateLedgerMapperExt;
import com.ruoyi.jst.channel.mapper.RebateSettlementMapperExt;
import com.ruoyi.jst.channel.mapper.lookup.ChannelLookupMapper;
import com.ruoyi.jst.channel.mapper.lookup.ContestLookupMapper;
import com.ruoyi.jst.channel.service.ChannelWithdrawService;
import com.ruoyi.jst.channel.vo.RebateLedgerListVO;
import com.ruoyi.jst.channel.vo.RebateSummaryVO;
import com.ruoyi.jst.channel.vo.WithdrawApplyResVO;
import com.ruoyi.jst.channel.vo.WithdrawDetailVO;
import com.ruoyi.jst.channel.vo.WithdrawListVO;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Channel withdrawal service implementation.
 */
@Service
public class ChannelWithdrawServiceImpl implements ChannelWithdrawService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal AMOUNT_TOLERANCE = new BigDecimal("0.01");

    @Autowired
    private RebateLedgerMapperExt rebateLedgerMapperExt;

    @Autowired
    private RebateSettlementMapperExt rebateSettlementMapperExt;

    @Autowired
    private JstRebateSettlementMapper jstRebateSettlementMapper;

    @Autowired
    private ContestLookupMapper contestLookupMapper;

    @Autowired
    private ChannelLookupMapper channelLookupMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Override
    public RebateSummaryVO getSummary() {
        Long channelId = requireCurrentChannelId();
        Map<String, Object> row = rebateLedgerMapperExt.selectSummaryByChannelId(channelId);
        RebateSummaryVO vo = new RebateSummaryVO();
        vo.setWithdrawableAmount(bigDecimalValue(row == null ? null : row.get("withdrawableAmount")));
        vo.setReviewingAmount(bigDecimalValue(row == null ? null : row.get("reviewingAmount")));
        vo.setPaidAmount(bigDecimalValue(row == null ? null : row.get("paidAmount")));
        vo.setRolledBackAmount(bigDecimalValue(row == null ? null : row.get("rolledBackAmount")));
        return vo;
    }

    @Override
    public List<RebateLedgerListVO> selectLedgerList(RebateLedgerQueryReqDTO query) {
        RebateLedgerQueryReqDTO finalQuery = query == null ? new RebateLedgerQueryReqDTO() : query;
        finalQuery.setChannelId(requireCurrentChannelId());
        com.ruoyi.common.utils.PageUtils.startPage();
        List<RebateLedgerListVO> rows = rebateLedgerMapperExt.selectLedgerList(finalQuery);
        fillContestNames(rows);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WithdrawApplyResVO apply(WithdrawApplyDTO req) {
        Long channelId = requireCurrentChannelId();
        return executeWithWithdrawLock("lock:channel:withdraw:apply:" + channelId, 3, 10, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            List<Long> ledgerIds = normalizeLedgerIds(req.getLedgerIds());
            if (ledgerIds.isEmpty()) {
                throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            List<JstRebateLedger> ledgers = rebateLedgerMapperExt.selectByLedgerIdsForUpdate(ledgerIds);
            validateApplyLedgers(channelId, ledgerIds, ledgers);

            BigDecimal actualAmount = ZERO_AMOUNT;
            for (JstRebateLedger ledger : ledgers) {
                actualAmount = actualAmount.add(bigDecimalValue(ledger.getRebateAmount()));
            }
            actualAmount = actualAmount.setScale(2, RoundingMode.HALF_UP);
            if (actualAmount.subtract(bigDecimalValue(req.getExpectedAmount())).abs().compareTo(AMOUNT_TOLERANCE) > 0) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_AMOUNT_MISMATCH.message(),
                        BizErrorCode.JST_CHANNEL_WITHDRAW_AMOUNT_MISMATCH.code());
            }

            JstRebateSettlement settlement = new JstRebateSettlement();
            settlement.setSettlementNo(generateSettlementNo(channelId, now));
            settlement.setChannelId(channelId);
            settlement.setApplyAmount(actualAmount);
            settlement.setNegativeOffsetAmount(ZERO_AMOUNT);
            settlement.setInvoiceStatus(req.getInvoiceInfo() == null || req.getInvoiceInfo().isEmpty() ? "none" : "pending");
            settlement.setBankAccountSnapshot(toJson(req.getPayeeAccount()));
            settlement.setStatus(RebateSettlementStatus.PENDING.dbValue());
            settlement.setApplyTime(now);
            settlement.setCreateBy(operator);
            settlement.setCreateTime(now);
            settlement.setUpdateBy(operator);
            settlement.setUpdateTime(now);
            settlement.setRemark(req.getInvoiceInfo() == null || req.getInvoiceInfo().isEmpty() ? null : toJson(req.getInvoiceInfo()));
            settlement.setDelFlag("0");
            jstRebateSettlementMapper.insertJstRebateSettlement(settlement);

            int updated = rebateLedgerMapperExt.markInReview(channelId, settlement.getSettlementId(), ledgerIds, operator, now);
            if (updated != ledgers.size()) {
                throwDataTampered();
            }

            WithdrawApplyResVO vo = new WithdrawApplyResVO();
            vo.setSettlementId(settlement.getSettlementId());
            vo.setSettlementNo(settlement.getSettlementNo());
            vo.setActualAmount(actualAmount);
            vo.setStatus(settlement.getStatus());
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long settlementId) {
        executeWithWithdrawLock("lock:channel:withdraw:cancel:" + settlementId, 3, 5, () -> {
            Long channelId = requireCurrentChannelId();
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();

            JstRebateSettlement settlement = rebateSettlementMapperExt.selectBySettlementIdForUpdate(settlementId);
            validateSettlementOwnership(channelId, settlement);
            RebateSettlementStatus.fromDb(settlement.getStatus()).assertCanTransitTo(RebateSettlementStatus.CLOSED);
            if (rebateSettlementMapperExt.updateStatusByExpected(settlementId, channelId,
                    RebateSettlementStatus.PENDING.dbValue(), RebateSettlementStatus.CLOSED.dbValue(),
                    operator, now) == 0) {
                throwDataTampered();
            }
            int restored = rebateLedgerMapperExt.restoreWithdrawableBySettlementId(channelId, settlementId, operator, now);
            if (restored <= 0) {
                throwDataTampered();
            }
            return null;
        });
    }

    @Override
    public List<WithdrawListVO> selectMyWithdrawList(WithdrawQueryReqDTO query) {
        WithdrawQueryReqDTO finalQuery = query == null ? new WithdrawQueryReqDTO() : query;
        finalQuery.setChannelId(requireCurrentChannelId());
        com.ruoyi.common.utils.PageUtils.startPage();
        return rebateSettlementMapperExt.selectMyList(finalQuery);
    }

    @Override
    public WithdrawDetailVO getWithdrawDetail(Long settlementId) {
        Long channelId = requireCurrentChannelId();
        JstRebateSettlement settlement = jstRebateSettlementMapper.selectJstRebateSettlementBySettlementId(settlementId);
        validateSettlementOwnership(channelId, settlement);

        WithdrawDetailVO vo = new WithdrawDetailVO();
        vo.setSettlementId(settlement.getSettlementId());
        vo.setSettlementNo(settlement.getSettlementNo());
        vo.setApplyAmount(bigDecimalValue(settlement.getApplyAmount()));
        vo.setNegativeOffsetAmount(bigDecimalValue(settlement.getNegativeOffsetAmount()));
        vo.setActualPayAmount(bigDecimalValue(settlement.getActualPayAmount()));
        vo.setStatus(settlement.getStatus());
        vo.setInvoiceStatus(settlement.getInvoiceStatus());
        vo.setAuditRemark(settlement.getAuditRemark());
        vo.setPayVoucherUrl(settlement.getPayVoucherUrl());
        vo.setApplyTime(settlement.getApplyTime());
        vo.setPayTime(settlement.getPayTime());
        vo.setUpdateTime(settlement.getUpdateTime());
        vo.setPayeeAccount(parsePayeeAccount(settlement.getBankAccountSnapshot()));
        vo.setInvoiceInfo(parseInvoiceInfo(settlement.getRemark()));

        List<RebateLedgerListVO> ledgerItems = rebateLedgerMapperExt.selectBySettlementId(settlementId);
        fillContestNames(ledgerItems);
        vo.setLedgerItems(ledgerItems);
        return vo;
    }

    private void validateApplyLedgers(Long channelId, List<Long> ledgerIds, List<JstRebateLedger> ledgers) {
        if (ledgers == null || ledgers.size() != ledgerIds.size()) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_LEDGER_INVALID.message(),
                    BizErrorCode.JST_CHANNEL_WITHDRAW_LEDGER_INVALID.code());
        }
        for (JstRebateLedger ledger : ledgers) {
            if (!Objects.equals(channelId, ledger.getChannelId())
                    || !"withdrawable".equals(ledger.getStatus())
                    || !"positive".equals(ledger.getDirection())) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_LEDGER_INVALID.message(),
                        BizErrorCode.JST_CHANNEL_WITHDRAW_LEDGER_INVALID.code());
            }
        }
    }

    private void validateSettlementOwnership(Long channelId, JstRebateSettlement settlement) {
        if (settlement == null || "2".equals(settlement.getDelFlag())) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                    BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
        }
        if (!Objects.equals(channelId, settlement.getChannelId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private Long requireCurrentChannelId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Map<String, Object> channel = channelLookupMapper.selectCurrentByUserId(userId);
        if (channel == null || channel.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        String authStatus = channel.get("authStatus") == null ? null : String.valueOf(channel.get("authStatus"));
        Integer status = channel.get("status") instanceof Number number ? number.intValue() : null;
        if (!"approved".equals(authStatus) || status == null || status != 1) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        return ((Number) channel.get("channelId")).longValue();
    }

    private List<Long> normalizeLedgerIds(List<Long> ledgerIds) {
        Set<Long> ordered = new LinkedHashSet<>();
        for (Long ledgerId : ledgerIds) {
            if (ledgerId != null) {
                ordered.add(ledgerId);
            }
        }
        return new ArrayList<>(ordered);
    }

    private void fillContestNames(List<RebateLedgerListVO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        List<Long> contestIds = new ArrayList<>();
        Set<Long> dedup = new LinkedHashSet<>();
        for (RebateLedgerListVO row : rows) {
            if (row.getContestId() != null && dedup.add(row.getContestId())) {
                contestIds.add(row.getContestId());
            }
        }
        if (contestIds.isEmpty()) {
            return;
        }
        List<Map<String, Object>> contestRows = contestLookupMapper.selectContestNamesByIds(contestIds);
        if (contestRows == null || contestRows.isEmpty()) {
            return;
        }
        Map<Long, String> contestNameMap = new LinkedHashMap<>();
        for (Map<String, Object> contestRow : contestRows) {
            if (contestRow == null || contestRow.get("contestId") == null) {
                continue;
            }
            contestNameMap.put(((Number) contestRow.get("contestId")).longValue(),
                    contestRow.get("contestName") == null ? null : String.valueOf(contestRow.get("contestName")));
        }
        for (RebateLedgerListVO row : rows) {
            row.setContestName(contestNameMap.get(row.getContestId()));
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

    private String toJson(Object value) {
        try {
            return JSON.toJSONString(value);
        } catch (Exception ex) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private String generateSettlementNo(Long channelId, Date now) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(now);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "WD" + channelId + timestamp + random;
    }

    private BigDecimal bigDecimalValue(Object value) {
        if (value == null) {
            return ZERO_AMOUNT;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString()).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        if (StringUtils.isNotBlank(username)) {
            return username;
        }
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
