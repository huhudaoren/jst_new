package com.ruoyi.jst.finance.service;

import com.ruoyi.jst.finance.dto.PartnerSettlementQueryReqDTO;
import com.ruoyi.jst.finance.dto.SettlementDisputeReqDTO;
import com.ruoyi.jst.finance.vo.PartnerSettlementDetailResVO;
import com.ruoyi.jst.finance.vo.PartnerSettlementListResVO;

import java.util.List;

/**
 * 赛事方结算服务。
 */
public interface PartnerSettlementService {

    List<PartnerSettlementListResVO> listSettlements(PartnerSettlementQueryReqDTO query);

    PartnerSettlementDetailResVO getSettlementDetail(Long eventSettlementId);

    PartnerSettlementDetailResVO confirmSettlement(Long eventSettlementId);

    PartnerSettlementDetailResVO disputeSettlement(Long eventSettlementId, SettlementDisputeReqDTO req);
}
