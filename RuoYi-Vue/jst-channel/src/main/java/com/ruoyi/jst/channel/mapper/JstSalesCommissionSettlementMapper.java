package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesCommissionSettlement;
import java.util.List;

public interface JstSalesCommissionSettlementMapper {
    JstSalesCommissionSettlement selectJstSalesCommissionSettlementBySettlementId(Long settlementId);
    List<JstSalesCommissionSettlement> selectJstSalesCommissionSettlementList(JstSalesCommissionSettlement query);
    int insertJstSalesCommissionSettlement(JstSalesCommissionSettlement row);
    int updateJstSalesCommissionSettlement(JstSalesCommissionSettlement row);
    int deleteJstSalesCommissionSettlementBySettlementId(Long settlementId);
}
