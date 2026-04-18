package com.ruoyi.jst.channel.listener;

import com.ruoyi.jst.channel.service.ChannelDistributionService;
import com.ruoyi.jst.channel.service.ChannelDistributionService.OrderPaidContext;
import com.ruoyi.jst.common.event.OrderPaidEvent;
import com.ruoyi.jst.common.event.RefundSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 监听 OrderPaidEvent / RefundSuccessEvent，触发渠道分销提成计算管线 (plan-04 §4.3)。
 * <p>
 * AFTER_COMMIT 阶段：主事务提交后才触发 ledger 写入，避免主事务回滚时残留记录。
 * catch 异常不抛：分销计算失败不影响订单主流程。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class ChannelDistributionListener {

    private static final Logger log = LoggerFactory.getLogger(ChannelDistributionListener.class);

    @Autowired
    private ChannelDistributionService distributionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaidEvent event) {
        try {
            Map<String, Object> extra = event.getExtraData();
            OrderPaidContext ctx = new OrderPaidContext();
            ctx.orderId = event.getBizId();
            ctx.orderNo = strValue(extra, "orderNo");
            ctx.channelId = longValue(extra, "channelId");
            ctx.paidAmount = decimalValue(extra, "paidAmount");
            ctx.payTime = dateValue(extra, "payTime");

            if (ctx.channelId == null) {
                log.debug("[Distribution] order={} 无渠道，跳过分销计算", ctx.orderId);
                return;
            }
            if (ctx.paidAmount == null || ctx.paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("[Distribution] order={} paidAmount 非正，跳过分销计算", ctx.orderId);
                return;
            }
            distributionService.calculateOnOrderPaid(ctx);
        } catch (Exception ex) {
            log.error("[Distribution] OrderPaidEvent 处理失败 order={}", event.getBizId(), ex);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefund(RefundSuccessEvent event) {
        try {
            Map<String, Object> extra = event.getExtraData();
            Long orderId = longValue(extra, "orderId");
            if (orderId == null) {
                log.debug("[Distribution] RefundSuccessEvent refundId={} 无 orderId extra，跳过", event.getBizId());
                return;
            }
            int n = distributionService.cancelOnOrderRefunded(orderId);
            if (n > 0) {
                log.info("[Distribution] order={} 退款撤销 {} 条 pending distribution ledger", orderId, n);
            }
        } catch (Exception ex) {
            log.error("[Distribution] RefundSuccessEvent 处理失败 refundId={}", event.getBizId(), ex);
        }
    }

    // === extra data parsers (防 null + 类型宽松) ===

    private String strValue(Map<String, Object> m, String key) {
        Object v = m == null ? null : m.get(key);
        return v == null ? null : v.toString();
    }

    private Long longValue(Map<String, Object> m, String key) {
        Object v = m == null ? null : m.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private BigDecimal decimalValue(Map<String, Object> m, String key) {
        Object v = m == null ? null : m.get(key);
        if (v == null) return null;
        if (v instanceof BigDecimal b) return b;
        if (v instanceof Number n) return new BigDecimal(n.toString());
        try { return new BigDecimal(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Date dateValue(Map<String, Object> m, String key) {
        Object v = m == null ? null : m.get(key);
        return v instanceof Date d ? d : null;
    }
}
