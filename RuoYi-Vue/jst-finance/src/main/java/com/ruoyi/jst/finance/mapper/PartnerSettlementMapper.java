package com.ruoyi.jst.finance.mapper;

import com.ruoyi.jst.finance.dto.PartnerSettlementQueryReqDTO;
import com.ruoyi.jst.finance.vo.PartnerSettlementDetailResVO;
import com.ruoyi.jst.finance.vo.PartnerSettlementFileResVO;
import com.ruoyi.jst.finance.vo.PartnerSettlementListResVO;
import com.ruoyi.jst.finance.vo.PartnerSettlementOrderResVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 赛事方结算单查询与状态更新 Mapper。
 */
public interface PartnerSettlementMapper {

    List<PartnerSettlementListResVO> selectSettlementList(PartnerSettlementQueryReqDTO query);

    PartnerSettlementDetailResVO selectSettlementDetail(@Param("eventSettlementId") Long eventSettlementId,
                                                       @Param("partnerId") Long partnerId);

    List<PartnerSettlementOrderResVO> selectSettlementOrders(@Param("partnerId") Long partnerId,
                                                            @Param("contestId") Long contestId);

    List<PartnerSettlementFileResVO> selectPayoutFiles(@Param("eventSettlementId") Long eventSettlementId,
                                                      @Param("partnerId") Long partnerId);

    List<PartnerSettlementFileResVO> selectContractFiles(@Param("eventSettlementId") Long eventSettlementId,
                                                        @Param("partnerId") Long partnerId);

    List<PartnerSettlementFileResVO> selectInvoiceFiles(@Param("settlementNo") String settlementNo,
                                                       @Param("partnerId") Long partnerId);

    int updateStatus(@Param("eventSettlementId") Long eventSettlementId,
                     @Param("partnerId") Long partnerId,
                     @Param("fromStatus") String fromStatus,
                     @Param("toStatus") String toStatus,
                     @Param("auditRemark") String auditRemark,
                     @Param("updateBy") String updateBy,
                     @Param("updateTime") Date updateTime);
}
