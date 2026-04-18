package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售提成计算管线（spec §3）。
 * <p>
 * 核心流程：
 * <ol>
 *   <li>订单付款时即时记账（{@link #calculateOnOrderPaid}）：写 pending ledger，accrue_at = pay_time + 售后期天数</li>
 *   <li>售后期满 pending → accrued（{@link #accruePendingByCron}）</li>
 *   <li>退款撤销 pending（{@link #cancelOnOrderRefunded}）</li>
 * </ol>
 *
 * @author jst
 * @since 1.0.0
 */
public interface SalesCommissionService {

    /**
     * 订单付款时计算并写入销售提成 ledger (直属 + N2 穿透 1 级)。
     * 同事务内应用单笔上限压缩（J）。
     * <p>
     * 返回写入的 ledger 列表（含压缩前后的金额）。不写分销提成（那由 plan-04
     * ChannelDistributionService 单独处理，本方法仅负责销售提成本身）。
     */
    List<JstSalesCommissionLedger> calculateOnOrderPaid(OrderPaidContext ctx);

    /** 单笔上限压缩比 (J) — 返回 0~1 的 BigDecimal */
    BigDecimal computeCompressRatio(BigDecimal sumAllShares, BigDecimal paidAmount, BigDecimal maxRate);

    /** 退款撤销该订单所有 pending ledger */
    int cancelOnOrderRefunded(Long orderId);

    /** Quartz 任务用：批量推进 pending → accrued（accrue_at &lt;= NOW()） */
    int accruePendingByCron();

    /**
     * 订单付款上下文（提成计算入参）。
     * <ul>
     *   <li>channelRebateAmount / distributionAmount 由调用方传入，参与单笔上限计算
     *       （但不影响销售 ledger 写入本身——那部分由 plan-04 独立 service 处理）</li>
     * </ul>
     */
    class OrderPaidContext {
        public Long orderId;
        public String orderNo;
        public Long channelId;
        /** 业务类型：enroll / course / mall / appointment_team / appointment_personal */
        public String businessType;
        public BigDecimal paidAmount;
        public Date payTime;
        /** 渠道返点金额（外部传入，参与上限压缩计算；销售 ledger 不写这项） */
        public BigDecimal channelRebateAmount;
        /** 分销提成金额（外部传入，参与上限压缩计算；销售 ledger 不写这项） */
        public BigDecimal distributionAmount;
    }
}
