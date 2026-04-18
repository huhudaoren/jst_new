package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.domain.JstChannelInvite;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;
import com.ruoyi.jst.channel.mapper.JstChannelInviteMapper;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.service.SalesChannelBindingService;
import com.ruoyi.jst.channel.service.SalesCommissionRateService;
import com.ruoyi.jst.channel.service.SalesCommissionService;
import com.ruoyi.jst.channel.service.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * 销售提成计算核心 (spec §3.2)。
 * <p>
 * 支持直属销售 + N2 穿透 1 级；单笔订单总分润上限压缩 (J)；E3 离职接收不计提；
 * 售后期等待 + Quartz 推进 accrued；退款撤销 pending。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class SalesCommissionServiceImpl implements SalesCommissionService {

    private static final Logger log = LoggerFactory.getLogger(SalesCommissionServiceImpl.class);

    /** 销售状态白名单：active / resign_apply 可计提；resigned_pending_settle / resigned_final 不计提 (E3) */
    private static final Set<String> COMMISSIONABLE_STATUS = Set.of("active", "resign_apply");

    @Autowired private JstSalesCommissionLedgerMapper ledgerMapper;
    @Autowired private SalesChannelBindingService bindingService;
    @Autowired private SalesService salesService;
    @Autowired private SalesCommissionRateService rateService;
    @Autowired private JstChannelInviteMapper inviteMapper;

    @Value("${jst.sales.aftersales_days:7}")
    private int aftersalesDays;

    @Value("${jst.sales.order.max_total_share_rate:0.15}")
    private BigDecimal maxTotalShareRate;

    public void setAftersalesDays(int v) { this.aftersalesDays = v; }
    public void setMaxTotalShareRate(BigDecimal v) { this.maxTotalShareRate = v; }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<JstSalesCommissionLedger> calculateOnOrderPaid(OrderPaidContext ctx) {
        if (ctx == null || ctx.paidAmount == null
                || ctx.paidAmount.compareTo(BigDecimal.ZERO) <= 0
                || ctx.channelId == null) {
            return Collections.emptyList();
        }
        if (ctx.payTime == null) ctx.payTime = new Date();

        // === Step 1: 收集销售提成项 (raw, compress_ratio=1.0) ===
        List<JstSalesCommissionLedger> items = new ArrayList<>();

        // 1.1 直属销售
        JstSalesChannelBinding direct = bindingService.getBindingAtTime(ctx.channelId, ctx.payTime);
        Long directSalesId = resolveEligibleSalesId(direct);
        if (directSalesId != null) {
            BigDecimal rate = rateService.resolveRate(directSalesId, ctx.businessType, ctx.payTime);
            items.add(buildLedger(ctx, directSalesId, "direct", rate));
        }

        // 1.2 N2 穿透 1 级 (直属有销售时才走)
        if (directSalesId != null) {
            JstChannelInvite invite = inviteMapper.selectActiveByInviteeAtTime(ctx.channelId, ctx.payTime);
            if (invite != null) {
                JstSalesChannelBinding parent = bindingService.getBindingAtTime(
                        invite.getInviterChannelId(), ctx.payTime);
                Long parentSalesId = resolveEligibleSalesId(parent);
                if (parentSalesId != null && !Objects.equals(parentSalesId, directSalesId)) {  // N2 防双计
                    BigDecimal rate = rateService.resolveRate(parentSalesId, ctx.businessType, ctx.payTime);
                    items.add(buildLedger(ctx, parentSalesId, "level1", rate));
                }
            }
        }

        if (items.isEmpty()) {
            log.debug("[Commission] order={} 无销售提成项（渠道无归属或销售均已离职）", ctx.orderId);
            return items;
        }

        // === Step 2: 单笔上限压缩 (J) ===
        BigDecimal sumSales = items.stream()
                .map(JstSalesCommissionLedger::getRawAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAllShares = sumSales
                .add(ctx.channelRebateAmount == null ? BigDecimal.ZERO : ctx.channelRebateAmount)
                .add(ctx.distributionAmount == null ? BigDecimal.ZERO : ctx.distributionAmount);
        BigDecimal compressRatio = computeCompressRatio(sumAllShares, ctx.paidAmount, maxTotalShareRate);

        // === Step 3: 应用压缩比 + 批量入库 ===
        for (JstSalesCommissionLedger row : items) {
            row.setCompressRatio(compressRatio);
            row.setAmount(row.getRawAmount().multiply(compressRatio).setScale(2, RoundingMode.HALF_UP));
        }
        ledgerMapper.batchInsert(items);
        log.info("[Commission] order={} 写入 {} 条销售 ledger, compressRatio={}",
                ctx.orderId, items.size(), compressRatio);
        return items;
    }

    /**
     * 从 binding 反查可计提 sales_id。
     * 返回 null 若：binding 不存在 / bind_source='transfer_resign' (E3 离职接收) /
     * 销售已进入 resigned_pending_settle 或 resigned_final 状态。
     */
    private Long resolveEligibleSalesId(JstSalesChannelBinding binding) {
        if (binding == null) return null;
        // E3 离职接收不计提（bind_source='transfer_resign'，主管接收但平台留存）
        if ("transfer_resign".equals(binding.getBindSource())) return null;
        JstSales s = salesService.getById(binding.getSalesId());
        if (s == null) return null;
        if (!COMMISSIONABLE_STATUS.contains(s.getStatus())) return null;
        return s.getSalesId();
    }

    private JstSalesCommissionLedger buildLedger(OrderPaidContext ctx, Long salesId, String type, BigDecimal rate) {
        JstSalesCommissionLedger row = new JstSalesCommissionLedger();
        row.setLedgerId(snowId());
        row.setSalesId(salesId);
        row.setOrderId(ctx.orderId);
        row.setOrderNo(ctx.orderNo);
        row.setChannelId(ctx.channelId);
        row.setBindingType(type);
        row.setBusinessType(ctx.businessType);
        row.setBaseAmount(ctx.paidAmount);
        row.setAppliedRate(rate);
        row.setRawAmount(ctx.paidAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP));
        row.setCompressRatio(BigDecimal.ONE);
        row.setAmount(row.getRawAmount());
        row.setStatus("pending");
        row.setAccrueAt(addDays(ctx.payTime, aftersalesDays));
        return row;
    }

    @Override
    public BigDecimal computeCompressRatio(BigDecimal sumAll, BigDecimal paidAmount, BigDecimal maxRate) {
        if (sumAll == null || paidAmount == null || maxRate == null
                || paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE;
        }
        BigDecimal cap = paidAmount.multiply(maxRate);
        if (sumAll.compareTo(cap) <= 0) return BigDecimal.ONE;
        return cap.divide(sumAll, 4, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelOnOrderRefunded(Long orderId) {
        if (orderId == null) return 0;
        int n = ledgerMapper.cancelPendingByOrder(orderId);
        if (n > 0) log.info("[Commission] order={} 退款撤销 {} 条 pending ledger", orderId, n);
        return n;
    }

    @Override
    public int accruePendingByCron() {
        List<Long> ids = ledgerMapper.selectPendingForAccrue();
        if (ids == null || ids.isEmpty()) return 0;
        return ledgerMapper.markAccruedBatch(ids);
    }

    private Date addDays(Date d, int days) {
        return new Date(d.getTime() + 86400_000L * days);
    }

    private Long snowId() {
        return System.currentTimeMillis() * 10_000L + new Random().nextInt(10_000);
    }
}
