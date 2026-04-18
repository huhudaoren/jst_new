package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSalesChannelBinding;
import com.ruoyi.jst.channel.mapper.JstSalesChannelBindingMapper;
import com.ruoyi.jst.channel.service.impl.SalesChannelBindingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SalesChannelBindingServiceImplTest {

    @Mock JstSalesChannelBindingMapper bindingMapper;
    @InjectMocks SalesChannelBindingServiceImpl service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void bindOrTransfer_noCurrent_insertsOnly() {
        when(bindingMapper.selectCurrentByChannelId(100L)).thenReturn(null);

        JstSalesChannelBinding row = service.bindOrTransfer(100L, 1001L, "manual", "test", 999L);

        assertNotNull(row.getBindingId());
        verify(bindingMapper, never()).closeBinding(any(), any());
        verify(bindingMapper).insertJstSalesChannelBinding(any());
    }

    @Test
    void bindOrTransfer_hasCurrent_closesAndInserts() {
        JstSalesChannelBinding existing = new JstSalesChannelBinding();
        existing.setBindingId(50L);
        existing.setSalesId(2001L);
        when(bindingMapper.selectCurrentByChannelId(100L)).thenReturn(existing);

        service.bindOrTransfer(100L, 1001L, "manual_transfer", "调岗", 999L);

        verify(bindingMapper).closeBinding(eq(50L), any(Date.class));
        verify(bindingMapper).insertJstSalesChannelBinding(any());
    }

    @Test
    void getCurrentByChannelId_returnsBinding() {
        JstSalesChannelBinding b = new JstSalesChannelBinding();
        b.setSalesId(1001L);
        when(bindingMapper.selectCurrentByChannelId(100L)).thenReturn(b);

        assertEquals(1001L, service.getCurrentByChannelId(100L).getSalesId());
    }

    @Test
    void getBindingAtTime_returnsCorrectHistorical() {
        Date past = new Date(System.currentTimeMillis() - 86400_000L);
        JstSalesChannelBinding b = new JstSalesChannelBinding();
        b.setSalesId(2001L);
        when(bindingMapper.selectAtTime(eq(100L), eq(past))).thenReturn(b);

        assertEquals(2001L, service.getBindingAtTime(100L, past).getSalesId());
    }

    @Test
    void listCurrentByOwnerSales_returnsList() {
        when(bindingMapper.selectCurrentByOwnerSales(1001L))
                .thenReturn(Arrays.asList(new JstSalesChannelBinding(), new JstSalesChannelBinding()));

        assertEquals(2, service.listCurrentByOwnerSales(1001L).size());
    }

    @Test
    void transferAllToManager_emptyOwnership_returns0() {
        when(bindingMapper.selectCurrentByOwnerSales(1001L)).thenReturn(Collections.emptyList());

        assertEquals(0, service.transferAllToManager(1001L, 2001L, 999L));
    }

    @Test
    void transferAllToManager_transfersN() {
        JstSalesChannelBinding b1 = bindingWith(50L, 100L);
        JstSalesChannelBinding b2 = bindingWith(51L, 101L);
        when(bindingMapper.selectCurrentByOwnerSales(1001L)).thenReturn(Arrays.asList(b1, b2));
        // bindOrTransfer 内部又调 selectCurrentByChannelId
        when(bindingMapper.selectCurrentByChannelId(100L)).thenReturn(b1);
        when(bindingMapper.selectCurrentByChannelId(101L)).thenReturn(b2);

        int n = service.transferAllToManager(1001L, 2001L, 999L);

        assertEquals(2, n);
        verify(bindingMapper, atLeast(2)).insertJstSalesChannelBinding(any());
    }

    private JstSalesChannelBinding bindingWith(Long bindingId, Long channelId) {
        JstSalesChannelBinding b = new JstSalesChannelBinding();
        b.setBindingId(bindingId);
        b.setChannelId(channelId);
        return b;
    }
}
