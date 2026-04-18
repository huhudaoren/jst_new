package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesCommissionSettlement;

import java.util.List;

/**
 * 销售提成月结 Service (spec §3.5)。
 * <p>
 * Quartz 月初跑 {@link #generateLastMonthSettlements}：
 * 遍历可参与销售，把上月 accrued ledger 汇总成一张月结单 (status='pending_review')，
 * 同时把对应 ledger 推进到 settled。
 * <p>
 * admin 审核流程：approve → 录打款流水 → paid；reject → ledger 退回 accrued 下月重算。
 *
 * @author jst
 * @since 1.0.0
 */
public interface SalesCommissionSettlementService {

    /** Quartz 调用：为所有 active/过渡期销售生成上月结算单，返回新生成的结算单数量 */
    int generateLastMonthSettlements();

    /** admin 审核通过 */
    void approve(Long settlementId, Long approverUserId);

    /** admin 驳回（ledger 退回 accrued，下月重汇） */
    void reject(Long settlementId, String reason);

    /** admin 录入打款流水（settlement + ledger 双双推到 paid） */
    void recordPayment(Long settlementId, String voucher);

    /** 列表（admin 端用） */
    List<JstSalesCommissionSettlement> listForAdmin(JstSalesCommissionSettlement query);

    /** 按主键查单条（admin 详情用） */
    JstSalesCommissionSettlement getBySettlementId(Long settlementId);
}
