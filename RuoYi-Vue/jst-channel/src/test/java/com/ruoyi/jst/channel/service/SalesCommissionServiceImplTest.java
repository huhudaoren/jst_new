package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstChannelInvite;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;
import com.ruoyi.jst.channel.mapper.JstChannelInviteMapper;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.service.SalesCommissionService.OrderPaidContext;
import com.ruoyi.jst.channel.service.impl.SalesCommissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SalesCommissionServiceImplTest {

    @Mock JstSalesCommissionLedgerMapper ledgerMapper;
    @Mock SalesChannelBindingService bindingService;
    @Mock SalesService salesService;
    @Mock SalesCommissionRateService rateService;
    @Mock JstChannelInviteMapper inviteMapper;
    @InjectMocks SalesCommissionServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.setAftersalesDays(7);
        service.setMaxTotalShareRate(new BigDecimal("0.15"));
    }

    // ============= T1: 直属销售命中，无上级，写 1 条 ledger =============
    @Test
    void T1_directOnly_writesOneLedger() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));

        // 直属销售 1001 active
        whenBindingActive(1L, 1001L, "pre_register");
        whenSalesActive(1001L);
        when(rateService.resolveRate(1001L, "enroll", ctx.payTime)).thenReturn(new BigDecimal("0.05"));

        // 无上级邀请关系
        when(inviteMapper.selectActiveByInviteeAtTime(eq(1L), any())).thenReturn(null);

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);

        assertEquals(1, rows.size());
        assertEquals("direct", rows.get(0).getBindingType());
        assertEquals(1001L, rows.get(0).getSalesId());
        assertEquals(new BigDecimal("5.00"), rows.get(0).getAmount());  // 100 × 0.05
        assertEquals(BigDecimal.ONE, rows.get(0).getCompressRatio());
        assertEquals("pending", rows.get(0).getStatus());
        verify(ledgerMapper).batchInsert(argThat(list -> list.size() == 1));
    }

    // ============= T2: 直属 + 穿透，写 2 条 ledger =============
    @Test
    void T2_directAndLevel1_writesTwoLedgers() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("200.00"));

        whenBindingActive(1L, 1001L, "pre_register");
        whenSalesActive(1001L);
        when(rateService.resolveRate(1001L, "enroll", ctx.payTime)).thenReturn(new BigDecimal("0.05"));

        // 上级渠道 10 绑销售 2001
        JstChannelInvite invite = inviteWith(10L, 1L);
        when(inviteMapper.selectActiveByInviteeAtTime(eq(1L), any())).thenReturn(invite);
        whenBindingActive(10L, 2001L, "pre_register");
        whenSalesActive(2001L);
        when(rateService.resolveRate(2001L, "enroll", ctx.payTime)).thenReturn(new BigDecimal("0.03"));

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);

        assertEquals(2, rows.size());
        assertEquals("direct", rows.get(0).getBindingType());
        assertEquals("level1", rows.get(1).getBindingType());
        assertEquals(new BigDecimal("10.00"), rows.get(0).getAmount());  // 200 × 0.05
        assertEquals(new BigDecimal("6.00"), rows.get(1).getAmount());   // 200 × 0.03
    }

    // ============= T3: 上级销售 == 直属销售，防双计，只 1 条 =============
    @Test
    void T3_levelsSameSales_N2PreventDoubleCount() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));

        whenBindingActive(1L, 1001L, "pre_register");
        whenSalesActive(1001L);
        when(rateService.resolveRate(eq(1001L), any(), any())).thenReturn(new BigDecimal("0.05"));

        JstChannelInvite invite = inviteWith(10L, 1L);
        when(inviteMapper.selectActiveByInviteeAtTime(eq(1L), any())).thenReturn(invite);
        whenBindingActive(10L, 1001L, "pre_register");  // 同一销售 1001 也绑了 10

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);

        assertEquals(1, rows.size());  // 只 direct
    }

    // ============= T4: E3 bind_source='transfer_resign'，跳过不计提 =============
    @Test
    void T4_E3_transferResign_skipped() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));

        whenBindingActive(1L, 1001L, "transfer_resign");  // 离职接收
        // salesService.getById 应该不会被调（因为 binding 已被 transfer_resign 过滤）

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);

        assertTrue(rows.isEmpty());
        verify(ledgerMapper, never()).batchInsert(any());
    }

    // ============= T5: 渠道无归属，0 条 ledger =============
    @Test
    void T5_noBinding_noLedger() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));
        when(bindingService.getBindingAtTime(eq(1L), any())).thenReturn(null);

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);
        assertTrue(rows.isEmpty());
    }

    // ============= T6: 实付 0 元，跳过 =============
    @Test
    void T6_zeroAmount_skipsAll() {
        OrderPaidContext ctx = ctxBase(100L, 1L, BigDecimal.ZERO);

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);
        assertTrue(rows.isEmpty());
        verifyNoInteractions(bindingService);
    }

    // ============= T7: 销售 status=resigned_pending_settle，跳过 =============
    @Test
    void T7_salesResigned_skipped() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));

        whenBindingActive(1L, 1001L, "pre_register");
        JstSales s = new JstSales();
        s.setSalesId(1001L);
        s.setStatus("resigned_pending_settle");
        when(salesService.getById(1001L)).thenReturn(s);

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);
        assertTrue(rows.isEmpty());
    }

    // ============= T8: resign_apply 销售仍可计提（B3 类似，继续算业绩） =============
    @Test
    void T8_resignApply_stillCalculates() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));

        whenBindingActive(1L, 1001L, "pre_register");
        JstSales s = new JstSales();
        s.setSalesId(1001L);
        s.setStatus("resign_apply");
        when(salesService.getById(1001L)).thenReturn(s);
        when(rateService.resolveRate(1001L, "enroll", ctx.payTime)).thenReturn(new BigDecimal("0.05"));
        when(inviteMapper.selectActiveByInviteeAtTime(eq(1L), any())).thenReturn(null);

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);
        assertEquals(1, rows.size());
    }

    // ============= T9: 上限压缩触发，销售提成 + 返点 + 分销 > 15% =============
    @Test
    void T9_exceedsCap_compressionApplied() {
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));
        ctx.channelRebateAmount = new BigDecimal("10.00");  // 10%
        ctx.distributionAmount = new BigDecimal("5.00");    // 5%

        whenBindingActive(1L, 1001L, "pre_register");
        whenSalesActive(1001L);
        when(rateService.resolveRate(1001L, "enroll", ctx.payTime)).thenReturn(new BigDecimal("0.08"));  // 8%

        when(inviteMapper.selectActiveByInviteeAtTime(eq(1L), any())).thenReturn(null);

        // Total = sales(8) + rebate(10) + distribution(5) = 23, cap = 15, ratio = 15/23 ≈ 0.6522
        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);
        assertEquals(1, rows.size());
        assertTrue(rows.get(0).getCompressRatio().compareTo(BigDecimal.ONE) < 0);
        // sales amount = 8 × 0.6522 ≈ 5.22
        assertTrue(rows.get(0).getAmount().compareTo(new BigDecimal("8.00")) < 0);
    }

    // ============= T10: accrue_at = pay_time + 7 days =============
    @Test
    void T10_accrueAt_isPayTimePlus7Days() {
        Date pay = new Date(1700000000000L);  // fixed
        OrderPaidContext ctx = ctxBase(100L, 1L, new BigDecimal("100.00"));
        ctx.payTime = pay;

        whenBindingActive(1L, 1001L, "pre_register");
        whenSalesActive(1001L);
        when(rateService.resolveRate(1001L, "enroll", pay)).thenReturn(new BigDecimal("0.05"));
        when(inviteMapper.selectActiveByInviteeAtTime(eq(1L), any())).thenReturn(null);

        List<JstSalesCommissionLedger> rows = service.calculateOnOrderPaid(ctx);
        long expected = pay.getTime() + 86400_000L * 7;
        assertEquals(expected, rows.get(0).getAccrueAt().getTime());
    }

    // ============= T11: 退款撤销 pending =============
    @Test
    void T11_cancelOnOrderRefunded_updates() {
        when(ledgerMapper.cancelPendingByOrder(888L)).thenReturn(2);
        int n = service.cancelOnOrderRefunded(888L);
        assertEquals(2, n);
    }

    // ============= T12: Quartz accrue 幂等 =============
    @Test
    void T12_accruePendingByCron_onlyUpdatesPending() {
        when(ledgerMapper.selectPendingForAccrue()).thenReturn(List.of(1L, 2L));
        when(ledgerMapper.markAccruedBatch(anyList())).thenReturn(2);

        int n = service.accruePendingByCron();
        assertEquals(2, n);
    }

    // ============= T13: computeCompressRatio boundary =============
    @Test
    void T13_computeCompressRatio_boundaries() {
        // sum <= cap → 1.0
        assertEquals(BigDecimal.ONE, service.computeCompressRatio(
                new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("0.15")));

        // sum > cap → cap/sum
        BigDecimal r = service.computeCompressRatio(
                new BigDecimal("20"), new BigDecimal("100"), new BigDecimal("0.15"));
        // 15 / 20 = 0.75
        assertEquals(0, r.compareTo(new BigDecimal("0.7500")));

        // null safety
        assertEquals(BigDecimal.ONE, service.computeCompressRatio(null, null, null));
    }

    // === Helpers ===
    private OrderPaidContext ctxBase(Long orderId, Long channelId, BigDecimal amount) {
        OrderPaidContext c = new OrderPaidContext();
        c.orderId = orderId;
        c.orderNo = "SO" + orderId;
        c.channelId = channelId;
        c.businessType = "enroll";
        c.paidAmount = amount;
        c.payTime = new Date(1700000000000L);
        c.channelRebateAmount = BigDecimal.ZERO;
        c.distributionAmount = BigDecimal.ZERO;
        return c;
    }

    private void whenBindingActive(long channelId, long salesId, String bindSource) {
        JstSalesChannelBinding b = new JstSalesChannelBinding();
        b.setChannelId(channelId);
        b.setSalesId(salesId);
        b.setBindSource(bindSource);
        when(bindingService.getBindingAtTime(eq(channelId), any())).thenReturn(b);
    }

    private void whenSalesActive(long salesId) {
        JstSales s = new JstSales();
        s.setSalesId(salesId);
        s.setStatus("active");
        when(salesService.getById(salesId)).thenReturn(s);
    }

    private JstChannelInvite inviteWith(long inviterChannelId, long inviteeChannelId) {
        JstChannelInvite i = new JstChannelInvite();
        i.setInviterChannelId(inviterChannelId);
        i.setInviteeChannelId(inviteeChannelId);
        i.setStatus("active");
        return i;
    }
}
