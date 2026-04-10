package com.ruoyi.jst.finance.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.util.PartnerScopeUtils;
import com.ruoyi.jst.finance.dto.PartnerSettlementQueryReqDTO;
import com.ruoyi.jst.finance.dto.SettlementDisputeReqDTO;
import com.ruoyi.jst.finance.enums.EventSettlementStatus;
import com.ruoyi.jst.finance.mapper.PartnerSettlementMapper;
import com.ruoyi.jst.finance.service.PartnerSettlementService;
import com.ruoyi.jst.finance.vo.PartnerSettlementDetailResVO;
import com.ruoyi.jst.finance.vo.PartnerSettlementListResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 赛事方结算服务实现。
 */
@Service
public class PartnerSettlementServiceImpl implements PartnerSettlementService {

    private static final String DISPUTE_PREFIX = "DISPUTE:";

    @Autowired
    private PartnerSettlementMapper partnerSettlementMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Override
    public List<PartnerSettlementListResVO> listSettlements(PartnerSettlementQueryReqDTO query) {
        return partnerSettlementMapper.selectSettlementList(query);
    }

    @Override
    public PartnerSettlementDetailResVO getSettlementDetail(Long eventSettlementId) {
        Long partnerId = PartnerScopeUtils.requireCurrentPartnerId();
        PartnerSettlementDetailResVO detail = partnerSettlementMapper.selectSettlementDetail(eventSettlementId, partnerId);
        assertFound(detail);
        fillDetail(detail);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // TX: confirmSettlement
    @OperateLog(module = "赛事结算", action = "PARTNER_SETTLEMENT_CONFIRM", target = "#{eventSettlementId}")
    public PartnerSettlementDetailResVO confirmSettlement(Long eventSettlementId) {
        return transitToReviewing(eventSettlementId, "赛事方确认结算单");
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // TX: disputeSettlement
    @OperateLog(module = "赛事结算", action = "PARTNER_SETTLEMENT_DISPUTE", target = "#{eventSettlementId}")
    public PartnerSettlementDetailResVO disputeSettlement(Long eventSettlementId, SettlementDisputeReqDTO req) {
        String reason = req == null ? "" : req.getDisputeReason();
        if (StringUtils.isBlank(reason)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        return transitToReviewing(eventSettlementId, DISPUTE_PREFIX + reason.trim());
    }

    private PartnerSettlementDetailResVO transitToReviewing(Long eventSettlementId, String auditRemark) {
        Long partnerId = PartnerScopeUtils.requireCurrentPartnerId();
        PartnerSettlementDetailResVO current = partnerSettlementMapper.selectSettlementDetail(eventSettlementId, partnerId);
        assertFound(current);

        // LOCK: lock:event:settle:{partnerId}:{contestId}
        String lockKey = "lock:event:settle:" + partnerId + ":" + current.getContestId();
        jstLockTemplate.execute(lockKey, 3, 5, () -> {
            PartnerSettlementDetailResVO latest = partnerSettlementMapper.selectSettlementDetail(eventSettlementId, partnerId);
            assertFound(latest);
            // SM-10: pending_confirm -> reviewing
            EventSettlementStatus.assertCanTransit(latest.getStatus(), EventSettlementStatus.REVIEWING);
            int updated = partnerSettlementMapper.updateStatus(eventSettlementId, partnerId, latest.getStatus(),
                    EventSettlementStatus.REVIEWING.code(), auditRemark, currentUsername(), DateUtils.getNowDate());
            if (updated == 0) {
                throw new ServiceException(BizErrorCode.JST_FINANCE_SETTLEMENT_STATUS_INVALID.message(),
                        BizErrorCode.JST_FINANCE_SETTLEMENT_STATUS_INVALID.code());
            }
            return null;
        });

        return getSettlementDetail(eventSettlementId);
    }

    private void fillDetail(PartnerSettlementDetailResVO detail) {
        detail.setOrderList(partnerSettlementMapper.selectSettlementOrders(detail.getPartnerId(), detail.getContestId()));
        detail.setPayoutFiles(partnerSettlementMapper.selectPayoutFiles(detail.getEventSettlementId(), detail.getPartnerId()));
        detail.setContractFiles(partnerSettlementMapper.selectContractFiles(detail.getEventSettlementId(), detail.getPartnerId()));
        detail.setInvoiceFiles(partnerSettlementMapper.selectInvoiceFiles(detail.getSettlementNo(), detail.getPartnerId()));
    }

    private void assertFound(PartnerSettlementDetailResVO detail) {
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_FINANCE_SETTLEMENT_NOT_FOUND.message(),
                    BizErrorCode.JST_FINANCE_SETTLEMENT_NOT_FOUND.code());
        }
    }

    private String currentUsername() {
        try {
            return SecurityUtils.getUsername();
        } catch (Exception ignored) {
            return "system";
        }
    }
}
