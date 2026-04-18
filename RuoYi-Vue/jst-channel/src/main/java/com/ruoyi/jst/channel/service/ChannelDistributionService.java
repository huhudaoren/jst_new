package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstChannelDistributionLedger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 渠道分销提成计算服务 (plan-04 §4.3)。
 * <p>
 * 与 SalesCommissionService 独立；MVP 阶段 compressRatio=1.0（不共享压缩比）。
 * 费率优先级：invite.commission_rate &gt; sysParam jst.sales.distribution.default_rate (默认 1.5%)。
 * accrue_at = 付款时间 + 7 天售后期。
 *
 * @author jst
 * @since 1.0.0
 */
public interface ChannelDistributionService {

    /**
     * 订单付款时写入分销 ledger（status=pending）。
     *
     * @param ctx 订单付款上下文
     * @return 写入的 ledger，若无邀请关系则返回 null
     */
    JstChannelDistributionLedger calculateOnOrderPaid(OrderPaidContext ctx);

    /**
     * 退款时撤销该订单的 pending ledger（status=cancelled）。
     *
     * @param orderId 订单 ID
     * @return 撤销的行数
     */
    int cancelOnOrderRefunded(Long orderId);

    /**
     * Quartz 任务：批量推进 accrue_at &lt;= NOW() 的 pending ledger → accrued。
     *
     * @return 推进的行数
     */
    int accruePendingByCron();

    /**
     * 查询某上级渠道的分销 ledger 列表（可按状态过滤）。
     *
     * @param channelId 上级渠道 ID
     * @param status    状态过滤（null 则不过滤）
     * @return ledger 列表
     */
    List<JstChannelDistributionLedger> listByInviter(Long channelId, String status);

    /**
     * 汇总某上级渠道的分销收益。
     *
     * @param channelId 上级渠道 ID
     * @param status    状态过滤（null 则汇总所有状态）
     * @return 总金额
     */
    BigDecimal sumAmountByInviter(Long channelId, String status);

    /**
     * 订单付款上下文（分销计算入参）。
     */
    class OrderPaidContext {
        public Long orderId;
        public String orderNo;
        /** 下级（被邀请）渠道 ID */
        public Long channelId;
        public BigDecimal paidAmount;
        public Date payTime;
    }
}
