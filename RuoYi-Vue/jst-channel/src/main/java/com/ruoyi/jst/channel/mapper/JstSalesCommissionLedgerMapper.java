package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface JstSalesCommissionLedgerMapper {
    JstSalesCommissionLedger selectJstSalesCommissionLedgerByLedgerId(Long ledgerId);
    List<JstSalesCommissionLedger> selectJstSalesCommissionLedgerList(JstSalesCommissionLedger query);
    int insertJstSalesCommissionLedger(JstSalesCommissionLedger row);
    int updateJstSalesCommissionLedger(JstSalesCommissionLedger row);
    int deleteJstSalesCommissionLedgerByLedgerId(Long ledgerId);

    /** 批量入库（plan-02 Task 11 提成计算用） */
    int batchInsert(@Param("list") List<JstSalesCommissionLedger> list);

    /** 按订单查本订单产生的所有 ledger（幂等检测 + 退款撤销用） */
    List<JstSalesCommissionLedger> selectByOrderId(@Param("orderId") Long orderId);

    /** Quartz：查待入账 pending 行，LIMIT 1000 */
    List<Long> selectPendingForAccrue();

    /** 批量推进 pending → accrued */
    int markAccruedBatch(@Param("ids") List<Long> ids);

    /** 退款触发：撤销某订单所有 pending ledger → cancelled */
    int cancelPendingByOrder(@Param("orderId") Long orderId);
}
