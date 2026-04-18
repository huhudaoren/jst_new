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

    /**
     * 兜底补偿查询：最近 N 天 paid 且 channelId 非空 但尚无 ledger 行的订单。
     * 返回字段 map: orderId/orderNo/channelId/businessType/netPayAmount/payTime。
     * 由 SalesCommissionRepairTask (每小时跑) 调用。
     */
    List<java.util.Map<String, Object>> selectPaidOrdersNeedRepair();

    /** 按销售 + 入账时间段汇总待结算行（月结用） */
    List<JstSalesCommissionLedger> selectAccruedByPeriod(@Param("salesId") Long salesId,
                                                          @Param("periodStart") java.util.Date periodStart,
                                                          @Param("periodEnd") java.util.Date periodEnd);

    /** 月结：把销售在区间内的 accrued 行绑定到结算单 */
    int bindToSettlement(@Param("salesId") Long salesId,
                         @Param("settlementId") Long settlementId,
                         @Param("periodStart") java.util.Date periodStart,
                         @Param("periodEnd") java.util.Date periodEnd);

    /** 把 settled 行推进到 paid（admin 录入打款后调） */
    int markSettledAsPaid(@Param("settlementId") Long settlementId);

    /** 驳回月结单时，把 settled 退回 accrued（等下月重新汇总） */
    int rollbackSettledToAccrued(@Param("settlementId") Long settlementId);

    /** 销售业绩聚合（按时间段 + 可选销售过滤） */
    java.util.Map<String, Object> aggregatePerformance(@Param("salesId") Long salesId,
                                                       @Param("periodStart") java.util.Date periodStart,
                                                       @Param("periodEnd") java.util.Date periodEnd);

    /** 销售业绩按业务类型聚合 */
    List<java.util.Map<String, Object>> aggregateByBusinessType(@Param("salesId") Long salesId,
                                                                 @Param("periodStart") java.util.Date periodStart,
                                                                 @Param("periodEnd") java.util.Date periodEnd);

    /** 按月结单 ID 查关联 ledger 行（admin 详情用） */
    List<JstSalesCommissionLedger> selectBySettlementId(@Param("settlementId") Long settlementId);

    /**
     * 平台 Dashboard 月度汇总：本月 orderCount / gmv / commissionCost。
     * 参数：periodStart / periodEnd。
     * 返回 map keys: orderCount, gmv, commissionCost。
     */
    java.util.Map<String, Object> aggregateMonthSummary(@Param("periodStart") java.util.Date periodStart,
                                                         @Param("periodEnd") java.util.Date periodEnd);

    /**
     * Dashboard 销售 TOP 榜（按月 commission 倒序）。
     * 返回 map keys: salesId, salesName, commission, orderCount。
     */
    List<java.util.Map<String, Object>> selectSalesRanking(@Param("periodStart") java.util.Date periodStart,
                                                            @Param("periodEnd") java.util.Date periodEnd,
                                                            @Param("limit") int limit);

    /**
     * 渠道画像：按渠道统计各业务类型订单数（accrued/settled/paid 状态）。
     * 返回 [{businessType, orderCount}]。
     */
    List<java.util.Map<String, Object>> selectOrderStatsByChannel(@Param("channelId") Long channelId);
}
