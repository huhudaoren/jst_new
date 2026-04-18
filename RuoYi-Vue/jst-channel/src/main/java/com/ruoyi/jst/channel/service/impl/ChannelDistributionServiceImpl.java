package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.jst.channel.domain.JstChannelDistributionLedger;
import com.ruoyi.jst.channel.domain.JstChannelInvite;
import com.ruoyi.jst.channel.dto.ChannelBasicDTO;
import com.ruoyi.jst.channel.mapper.JstChannelDistributionLedgerMapper;
import com.ruoyi.jst.channel.mapper.JstChannelInviteMapper;
import com.ruoyi.jst.channel.mapper.lookup.ChannelDetailLookupMapper;
import com.ruoyi.jst.channel.service.ChannelDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 渠道分销提成计算服务实现 (plan-04 §4.3)。
 * <p>
 * MVP 阶段 compressRatio=1.0（不与 SalesCommissionService 共享压缩比）。
 * accrue_at = payTime + ${jst.sales.distribution.aftersales_days:7} 天。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ChannelDistributionServiceImpl implements ChannelDistributionService {

    private static final Logger log = LoggerFactory.getLogger(ChannelDistributionServiceImpl.class);

    private static final int BATCH_ACCRUE_LIMIT = 1000;

    @Value("${jst.sales.distribution.default_rate:0.015}")
    private BigDecimal defaultRate;

    @Value("${jst.sales.distribution.aftersales_days:7}")
    private int aftersalesDays;

    @Autowired
    private JstChannelDistributionLedgerMapper ledgerMapper;

    @Autowired
    private JstChannelInviteMapper inviteMapper;

    @Autowired
    private ChannelDetailLookupMapper channelLookup;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstChannelDistributionLedger calculateOnOrderPaid(OrderPaidContext ctx) {
        if (ctx == null || ctx.channelId == null || ctx.paidAmount == null
                || ctx.paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (ctx.payTime == null) ctx.payTime = new Date();

        // 查该渠道（下级）当前激活的邀请关系
        JstChannelInvite invite = inviteMapper.selectActiveByInviteeAtTime(ctx.channelId, ctx.payTime);
        if (invite == null) {
            log.debug("[Distribution] order={} channel={} 无邀请关系，跳过", ctx.orderId, ctx.channelId);
            return null;
        }

        // 上级渠道必须是非官方且启用
        ChannelBasicDTO inviter = channelLookup.selectBasicByChannelId(invite.getInviterChannelId());
        if (inviter == null) {
            log.warn("[Distribution] order={} inviter={} 不存在，跳过", ctx.orderId, invite.getInviterChannelId());
            return null;
        }
        if (Integer.valueOf(1).equals(inviter.getIsOfficial())) {
            log.debug("[Distribution] order={} inviter={} 是官方渠道，不计提分销", ctx.orderId, inviter.getChannelId());
            return null;
        }
        if (!Integer.valueOf(1).equals(inviter.getStatus())) {
            log.debug("[Distribution] order={} inviter={} status={} 非 active，不计提分销",
                    ctx.orderId, inviter.getChannelId(), inviter.getStatus());
            return null;
        }

        // 费率：invite.commission_rate ?? sysParam 默认 1.5%
        BigDecimal rate = invite.getCommissionRate() != null ? invite.getCommissionRate() : defaultRate;
        BigDecimal rawAmount = ctx.paidAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        // MVP: compressRatio = 1.0（独立，不共享压缩比）
        BigDecimal compressRatio = BigDecimal.ONE;
        BigDecimal amount = rawAmount;

        // accrue_at = payTime + 7 天
        Date accrueAt = addDays(ctx.payTime, aftersalesDays);

        JstChannelDistributionLedger ledger = new JstChannelDistributionLedger();
        ledger.setInviterChannelId(invite.getInviterChannelId());
        ledger.setInviteeChannelId(ctx.channelId);
        ledger.setOrderId(ctx.orderId);
        ledger.setOrderNo(ctx.orderNo);
        ledger.setBaseAmount(ctx.paidAmount);
        ledger.setAppliedRate(rate);
        ledger.setRawAmount(rawAmount);
        ledger.setCompressRatio(compressRatio);
        ledger.setAmount(amount);
        ledger.setStatus("pending");
        ledger.setAccrueAt(accrueAt);
        ledger.setAccruedAt(null);

        ledgerMapper.insertJstChannelDistributionLedger(ledger);

        log.info("[Distribution] order={} channel={} inviter={} rate={} amount={} accrueAt={}",
                ctx.orderId, ctx.channelId, invite.getInviterChannelId(), rate, amount, accrueAt);
        return ledger;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelOnOrderRefunded(Long orderId) {
        int n = ledgerMapper.cancelPendingByOrder(orderId);
        if (n > 0) {
            log.info("[Distribution] order={} 撤销 {} 条 pending distribution ledger", orderId, n);
        }
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int accruePendingByCron() {
        List<JstChannelDistributionLedger> pending = ledgerMapper.selectPendingForAccrue(BATCH_ACCRUE_LIMIT);
        if (pending.isEmpty()) return 0;
        List<Long> ids = pending.stream()
                .map(JstChannelDistributionLedger::getLedgerId)
                .collect(Collectors.toList());
        int n = ledgerMapper.markAccruedBatch(ids);
        log.info("[Distribution] Quartz 推进 {} 条 pending → accrued", n);
        return n;
    }

    @Override
    public List<JstChannelDistributionLedger> listByInviter(Long channelId, String status) {
        if (channelId == null) return Collections.emptyList();
        return ledgerMapper.selectByInviter(channelId, status);
    }

    @Override
    public BigDecimal sumAmountByInviter(Long channelId, String status) {
        if (channelId == null) return BigDecimal.ZERO;
        BigDecimal sum = ledgerMapper.sumAmountByInviter(channelId, status);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumMonthAmountByInviter(Long channelId) {
        if (channelId == null) return BigDecimal.ZERO;
        BigDecimal sum = ledgerMapper.sumMonthAmountByInviter(channelId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    // ===

    private static Date addDays(Date base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}
