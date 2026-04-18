package com.ruoyi.jst.channel.task;

import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.service.SalesCommissionService;
import com.ruoyi.jst.channel.service.SalesCommissionService.OrderPaidContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 销售提成兜底补偿（每小时 01/31 分左右）。
 * <p>
 * 扫描最近 7 天 paid 且 channelId 非空 但尚无 ledger 行的订单 → 补调
 * {@link SalesCommissionService#calculateOnOrderPaid} 重新计算写入。
 * <p>
 * 目的：补救 OrderPaidEvent Listener 偶发失败（网络/DB 超时/服务重启）
 * 遗漏的订单。Ledger uk_order_sales_type 唯一索引保证重复调用不会产生重复行。
 *
 * @author jst
 * @since 1.0.0
 */
@Component("salesCommissionRepairTask")
public class SalesCommissionRepairTask {

    private static final Logger log = LoggerFactory.getLogger(SalesCommissionRepairTask.class);

    @Autowired
    private JstSalesCommissionLedgerMapper ledgerMapper;

    @Autowired
    private SalesCommissionService commissionService;

    public void execute() {
        List<Map<String, Object>> orders = ledgerMapper.selectPaidOrdersNeedRepair();
        if (orders == null || orders.isEmpty()) {
            log.debug("[SalesCommissionRepairTask] 无待补偿订单");
            return;
        }
        int ok = 0, fail = 0;
        for (Map<String, Object> row : orders) {
            try {
                OrderPaidContext ctx = new OrderPaidContext();
                ctx.orderId = longOf(row.get("orderId"));
                ctx.orderNo = strOf(row.get("orderNo"));
                ctx.channelId = longOf(row.get("channelId"));
                ctx.businessType = strOf(row.get("businessType"));
                ctx.paidAmount = decOf(row.get("netPayAmount"));
                ctx.payTime = dateOf(row.get("payTime"));
                ctx.channelRebateAmount = BigDecimal.ZERO;
                ctx.distributionAmount = BigDecimal.ZERO;

                if (ctx.orderId == null || ctx.channelId == null
                        || ctx.paidAmount == null
                        || ctx.paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                commissionService.calculateOnOrderPaid(ctx);
                ok++;
            } catch (Exception ex) {
                fail++;
                log.error("[SalesCommissionRepairTask] order={} 补偿失败",
                        row.get("orderId"), ex);
            }
        }
        log.info("[SalesCommissionRepairTask] 补偿 {} 订单 (成功 {} / 失败 {})", orders.size(), ok, fail);
    }

    private Long longOf(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException e) { return null; }
    }
    private String strOf(Object v) { return v == null ? null : v.toString(); }
    private BigDecimal decOf(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal b) return b;
        if (v instanceof Number n) return new BigDecimal(n.toString());
        try { return new BigDecimal(v.toString()); } catch (NumberFormatException e) { return null; }
    }
    private Date dateOf(Object v) {
        if (v instanceof Date d) return d;
        if (v instanceof java.time.LocalDateTime ldt) {
            return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
}
