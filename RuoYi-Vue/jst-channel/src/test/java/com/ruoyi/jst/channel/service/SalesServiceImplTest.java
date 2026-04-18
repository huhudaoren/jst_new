package com.ruoyi.jst.channel.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.impl.SalesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class SalesServiceImplTest {

    @Mock JstSalesMapper salesMapper;
    @InjectMocks SalesServiceImpl service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void create_assignsSalesNoAndDefaults() {
        JstSales in = new JstSales();
        in.setSysUserId(100L);
        in.setSalesName("张三");
        in.setPhone("13900000001");
        in.setCommissionRateDefault(new BigDecimal("0.05"));
        when(salesMapper.selectBySysUserId(100L)).thenReturn(null);
        when(salesMapper.insertJstSales(any())).thenReturn(1);

        JstSales out = service.create(in);

        assertNotNull(out.getSalesId());
        assertNotNull(out.getSalesNo());
        assertTrue(out.getSalesNo().startsWith("S"));
        assertEquals("active", out.getStatus());
        assertEquals(0, out.getIsManager());
    }

    @Test
    void create_throws_whenSysUserAlreadyMapped() {
        JstSales existing = new JstSales();
        existing.setSysUserId(100L);
        when(salesMapper.selectBySysUserId(100L)).thenReturn(existing);

        JstSales in = new JstSales();
        in.setSysUserId(100L);
        assertThrows(ServiceException.class, () -> service.create(in));
    }

    @Test
    void resolveScopeSalesIds_returnsSelf_forPlainSales() {
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(salesWith(1001L, false, "active"));

        var ids = service.resolveScopeSalesIds(1001L);
        assertEquals(Arrays.asList(1001L), ids);
    }

    @Test
    void resolveScopeSalesIds_returnsTeam_forManager() {
        when(salesMapper.selectJstSalesBySalesId(2001L)).thenReturn(salesWith(2001L, true, "active"));
        when(salesMapper.selectActiveTeamIds(2001L)).thenReturn(Arrays.asList(2001L, 3001L, 3002L));

        var ids = service.resolveScopeSalesIds(2001L);
        assertEquals(Arrays.asList(2001L, 3001L, 3002L), ids);
    }

    @Test
    void applyResign_setsStatusAndDates() {
        JstSales s = salesWith(1001L, false, "active");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);
        service.setResignMaxDays(7);
        Date expected = new Date(System.currentTimeMillis() + 86400_000L * 5);

        service.applyResign(1001L, expected);

        verify(salesMapper).updateJstSales(argThat(row ->
            "resign_apply".equals(row.getStatus())
            && row.getResignApplyDate() != null
            && row.getExpectedResignDate().equals(expected)
        ));
    }

    @Test
    void applyResign_throws_whenIntervalExceedsMax() {
        JstSales s = salesWith(1001L, false, "active");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);
        service.setResignMaxDays(7);
        Date tooFar = new Date(System.currentTimeMillis() + 86400_000L * 30);

        assertThrows(ServiceException.class, () -> service.applyResign(1001L, tooFar));
    }

    @Test
    void executeResign_setsStatusToPendingSettle_andCalculatesTransitionEnd() {
        JstSales s = salesWith(1001L, false, "resign_apply");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);
        service.setTransitionMonths(3);

        service.executeResign(1001L);

        verify(salesMapper).updateJstSales(argThat(row ->
            "resigned_pending_settle".equals(row.getStatus())
            && row.getActualResignDate() != null
            && row.getTransitionEndDate() != null
        ));
    }

    @Test
    void endTransition_setsFinalStatus_onlyForPendingSettle() {
        JstSales s = salesWith(1001L, false, "resigned_pending_settle");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);

        service.endTransition(1001L);

        verify(salesMapper).updateJstSales(argThat(row -> "resigned_final".equals(row.getStatus())));
    }

    @Test
    void endTransition_throws_whenStatusNotPendingSettle() {
        JstSales s = salesWith(1001L, false, "active");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);

        assertThrows(ServiceException.class, () -> service.endTransition(1001L));
    }

    private JstSales salesWith(Long id, boolean isManager, String status) {
        JstSales s = new JstSales();
        s.setSalesId(id);
        s.setIsManager(isManager ? 1 : 0);
        s.setStatus(status);
        return s;
    }
}
