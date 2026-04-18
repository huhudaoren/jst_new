package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesCommissionSettlement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JstSalesCommissionSettlementMapper {
    JstSalesCommissionSettlement selectJstSalesCommissionSettlementBySettlementId(Long settlementId);
    List<JstSalesCommissionSettlement> selectJstSalesCommissionSettlementList(JstSalesCommissionSettlement query);
    int insertJstSalesCommissionSettlement(JstSalesCommissionSettlement row);
    int updateJstSalesCommissionSettlement(JstSalesCommissionSettlement row);
    int deleteJstSalesCommissionSettlementBySettlementId(Long settlementId);

    /** 幂等查询：某销售某月是否已生成结算单 */
    JstSalesCommissionSettlement selectBySalesAndPeriod(@Param("salesId") Long salesId,
                                                        @Param("period") String period);
}
