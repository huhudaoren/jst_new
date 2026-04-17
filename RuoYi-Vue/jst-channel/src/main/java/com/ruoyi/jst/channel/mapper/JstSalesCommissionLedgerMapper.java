package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;
import java.util.List;

public interface JstSalesCommissionLedgerMapper {
    JstSalesCommissionLedger selectJstSalesCommissionLedgerByLedgerId(Long ledgerId);
    List<JstSalesCommissionLedger> selectJstSalesCommissionLedgerList(JstSalesCommissionLedger query);
    int insertJstSalesCommissionLedger(JstSalesCommissionLedger row);
    int updateJstSalesCommissionLedger(JstSalesCommissionLedger row);
    int deleteJstSalesCommissionLedgerByLedgerId(Long ledgerId);
}
