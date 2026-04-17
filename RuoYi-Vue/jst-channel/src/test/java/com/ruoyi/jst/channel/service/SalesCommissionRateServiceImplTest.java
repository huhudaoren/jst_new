package com.ruoyi.jst.channel.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesCommissionRate;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionRateMapper;
import com.ruoyi.jst.channel.service.impl.SalesCommissionRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SalesCommissionRateServiceImplTest {

    @Mock JstSalesCommissionRateMapper rateMapper;
    @Mock SalesService salesService;
    @InjectMocks SalesCommissionRateServiceImpl service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void resolveRate_returnsRateMapping_whenDetailExists() {
        Date now = new Date();
        when(rateMapper.selectLatestRate(eq(1001L), eq("enroll"), any(Date.class)))
                .thenReturn(new BigDecimal("0.0800"));

        BigDecimal rate = service.resolveRate(1001L, "enroll", now);
        assertEquals(new BigDecimal("0.0800"), rate);
        verify(salesService, never()).getById(any());
    }

    @Test
    void resolveRate_fallsBackToDefault_whenNoDetail() {
        Date now = new Date();
        when(rateMapper.selectLatestRate(eq(1001L), eq("course"), any(Date.class))).thenReturn(null);
        JstSales s = new JstSales();
        s.setSalesId(1001L);
        s.setCommissionRateDefault(new BigDecimal("0.0500"));
        when(salesService.getById(1001L)).thenReturn(s);

        BigDecimal rate = service.resolveRate(1001L, "course", now);
        assertEquals(new BigDecimal("0.0500"), rate);
    }

    @Test
    void resolveRate_returnsZero_whenSalesHasNullDefault() {
        Date now = new Date();
        when(rateMapper.selectLatestRate(any(), any(), any())).thenReturn(null);
        JstSales s = new JstSales();
        s.setSalesId(1001L);
        s.setCommissionRateDefault(null);
        when(salesService.getById(1001L)).thenReturn(s);

        BigDecimal rate = service.resolveRate(1001L, "mall", now);
        assertEquals(BigDecimal.ZERO, rate);
    }

    @Test
    void resolveRate_throws_whenSalesNotExists() {
        Date now = new Date();
        when(rateMapper.selectLatestRate(any(), any(), any())).thenReturn(null);
        when(salesService.getById(9999L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> service.resolveRate(9999L, "enroll", now));
    }

    @Test
    void upsertRate_insertsNewRow() {
        service.upsertRate(1001L, "enroll", new BigDecimal("0.0800"));

        verify(rateMapper).insertJstSalesCommissionRate(any(JstSalesCommissionRate.class));
    }
}
