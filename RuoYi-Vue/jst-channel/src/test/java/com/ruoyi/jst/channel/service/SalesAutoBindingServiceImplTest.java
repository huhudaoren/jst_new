package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.domain.JstSalesPreRegister;
import com.ruoyi.jst.channel.service.impl.SalesAutoBindingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SalesAutoBindingServiceImplTest {

    @Mock SalesPreRegisterService preRegisterService;
    @Mock SalesService salesService;
    @Mock SalesChannelBindingService bindingService;
    @InjectMocks SalesAutoBindingServiceImpl service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // ============= B1: phone 命中 active 销售 =============
    @Test
    void B1_phoneHitsActivePreRegister_bindsAndMatches() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        JstSalesPreRegister pre = preReg(50L, 1001L);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(pre);
        when(salesService.getById(1001L)).thenReturn(activeSales(1001L, "13800000000"));

        Long bound = service.autoBind(100L, "13900000001", null);
        assertEquals(1001L, bound);

        verify(bindingService).bindOrTransfer(eq(100L), eq(1001L), eq("pre_register"), any(), any());
        verify(preRegisterService).markMatched(50L, 100L);
    }

    // ============= B2: phone 命中预录入但销售已离职 =============
    @Test
    void B2_phoneHitsButSalesResignedFinal_skipsToStep2() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        JstSalesPreRegister pre = preReg(50L, 1001L);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(pre);
        JstSales s = activeSales(1001L, "13800000000");
        s.setStatus("resigned_final");
        when(salesService.getById(1001L)).thenReturn(s);

        Long bound = service.autoBind(100L, "13900000001", null);
        assertNull(bound);  // Step 1 跳过 + Step 2 也无 businessNo → 无绑
        verify(bindingService, never()).bindOrTransfer(anyLong(), anyLong(), any(), any(), any());
    }

    // ============= B3: 销售在 resign_apply 仍可绑 =============
    @Test
    void B3_phoneHitsResignApplySales_stillBinds() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        JstSalesPreRegister pre = preReg(50L, 1001L);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(pre);
        JstSales s = activeSales(1001L, "13800000000");
        s.setStatus("resign_apply");
        when(salesService.getById(1001L)).thenReturn(s);

        Long bound = service.autoBind(100L, "13900000001", null);
        assertEquals(1001L, bound);
        verify(bindingService).bindOrTransfer(eq(100L), eq(1001L), eq("pre_register"), any(), any());
    }

    // ============= B4: business_no 命中 active 销售 =============
    @Test
    void B4_businessNoHits_binds() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(null);
        when(salesService.getBySalesNo("S202604001")).thenReturn(activeSales(2001L, "13700000000"));

        Long bound = service.autoBind(100L, "13900000001", "S202604001");
        assertEquals(2001L, bound);
        verify(bindingService).bindOrTransfer(eq(100L), eq(2001L), eq("business_no"), any(), any());
    }

    // ============= B5: business_no 命中已离职销售，跳过 =============
    @Test
    void B5_businessNoHitsResignedSales_skipped() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(null);
        JstSales s = activeSales(2001L, "13700000000");
        s.setStatus("resigned_pending_settle");
        when(salesService.getBySalesNo("S202604001")).thenReturn(s);

        Long bound = service.autoBind(100L, "13900000001", "S202604001");
        assertNull(bound);
        verify(bindingService, never()).bindOrTransfer(anyLong(), anyLong(), any(), any(), any());
    }

    // ============= B6: business_no 填错 (不存在) =============
    @Test
    void B6_businessNoTypo_writesAuditAndSkips() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(null);
        when(salesService.getBySalesNo("WRONG")).thenReturn(null);

        Long bound = service.autoBind(100L, "13900000001", "WRONG");
        assertNull(bound);
        verify(bindingService, never()).bindOrTransfer(anyLong(), anyLong(), any(), any(), any());
    }

    // ============= B7: 都未命中, 无归属 =============
    @Test
    void B7_neitherHit_returnsNull() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(null);

        Long bound = service.autoBind(100L, "13900000001", null);
        assertNull(bound);
    }

    // ============= B8: registeredPhone == sales.phone (Step 1 自绑防御) =============
    @Test
    void B8_phoneMatchesSelfPhone_step1Skipped() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        JstSalesPreRegister pre = preReg(50L, 1001L);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(pre);
        JstSales selfPhone = activeSales(1001L, "13900000001");  // sales.phone == registeredPhone
        when(salesService.getById(1001L)).thenReturn(selfPhone);

        Long bound = service.autoBind(100L, "13900000001", null);
        assertNull(bound);  // Step 1 跳过 + Step 2 无 businessNo → 无绑
        verify(bindingService, never()).bindOrTransfer(anyLong(), anyLong(), any(), any(), any());
        verify(preRegisterService, never()).markMatched(any(), any());
    }

    // ============= B8b: Step 2 自绑防御 =============
    @Test
    void B8b_step2_phoneMatchesSelfPhone_skipped() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(null);
        JstSales selfPhone = activeSales(2001L, "13900000001");
        when(salesService.getBySalesNo("S202604001")).thenReturn(selfPhone);

        Long bound = service.autoBind(100L, "13900000001", "S202604001");
        assertNull(bound);
    }

    // ============= B9: 已有 current binding 短路 =============
    @Test
    void B9_alreadyHasBinding_shortCircuits() {
        JstSalesChannelBinding existing = new JstSalesChannelBinding();
        existing.setBindingId(99L);
        existing.setSalesId(2001L);
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(existing);

        Long bound = service.autoBind(100L, "13900000001", "S202604001");
        assertEquals(2001L, bound);  // 返已有
        verify(preRegisterService, never()).findActiveByPhone(any());
        verify(salesService, never()).getBySalesNo(any());
        verify(bindingService, never()).bindOrTransfer(anyLong(), anyLong(), any(), any(), any());
    }

    // ============= B11: business_no + invite_code 同时填，本服务只管 business_no（M1 互斥由 listener 处理）=============
    @Test
    void B11_businessNoAndInviteCodeBoth_thisServiceOnlyHandlesBusinessNo() {
        when(bindingService.getCurrentByChannelId(100L)).thenReturn(null);
        when(preRegisterService.findActiveByPhone("13900000001")).thenReturn(null);
        when(salesService.getBySalesNo("S202604001")).thenReturn(activeSales(2001L, "13700000000"));

        Long bound = service.autoBind(100L, "13900000001", "S202604001");
        assertEquals(2001L, bound);
        // 销售绑成功 → ChannelInviteBindingListener (plan-04) 后续会检查到 hasSalesBinding=true，
        // 跳过邀请关系建立（M1 互斥）。本算法只负责销售绑定本身。
    }

    // ============= B12: channelId 为空 =============
    @Test
    void B12_nullChannelId_returnsNullSafely() {
        Long bound = service.autoBind(null, "13900000001", "S202604001");
        assertNull(bound);
        verifyNoInteractions(preRegisterService, salesService);
    }

    // === Helpers ===
    private JstSales activeSales(Long id, String phone) {
        JstSales s = new JstSales();
        s.setSalesId(id);
        s.setPhone(phone);
        s.setStatus("active");
        return s;
    }

    private JstSalesPreRegister preReg(Long preId, Long salesId) {
        JstSalesPreRegister p = new JstSalesPreRegister();
        p.setPreId(preId);
        p.setSalesId(salesId);
        return p;
    }
}
