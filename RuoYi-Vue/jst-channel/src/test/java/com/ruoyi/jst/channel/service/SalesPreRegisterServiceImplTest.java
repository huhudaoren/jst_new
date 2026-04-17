package com.ruoyi.jst.channel.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.domain.JstSalesPreRegister;
import com.ruoyi.jst.channel.mapper.JstSalesPreRegisterMapper;
import com.ruoyi.jst.channel.service.impl.SalesPreRegisterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SalesPreRegisterServiceImplTest {

    @Mock JstSalesPreRegisterMapper preRegMapper;
    @Mock SalesService salesService;
    @InjectMocks SalesPreRegisterServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.setValidDays(90);
        service.setRenewMax(2);
    }

    @Test
    void create_succeeds_writesPendingWith90Days() {
        when(preRegMapper.selectByPhonePending("13900000001")).thenReturn(null);
        when(salesService.getById(1001L)).thenReturn(salesWithPhone(1001L, "13800000000"));

        JstSalesPreRegister out = service.create(1001L, "13900000001", "海淀新东方");

        assertEquals("pending", out.getStatus());
        assertEquals(0, out.getRenewCount());
        assertNotNull(out.getExpireAt());
        verify(preRegMapper).insertJstSalesPreRegister(any());
    }

    @Test
    void create_throws_whenPhoneInvalid() {
        assertThrows(ServiceException.class,
                () -> service.create(1001L, "not-a-phone", "x"));
    }

    @Test
    void create_throws_whenPhoneIsSelfPhone_A10() {
        when(salesService.getById(1001L)).thenReturn(salesWithPhone(1001L, "13900000001"));
        assertThrows(ServiceException.class,
                () -> service.create(1001L, "13900000001", "x"));
    }

    @Test
    void create_throws_whenPhoneAlreadyPending_A2() {
        JstSalesPreRegister existing = new JstSalesPreRegister();
        existing.setPhone("13900000001");
        existing.setSalesId(2001L);
        when(preRegMapper.selectByPhonePending("13900000001")).thenReturn(existing);
        when(salesService.getById(1001L)).thenReturn(salesWithPhone(1001L, "13800000000"));

        assertThrows(ServiceException.class,
                () -> service.create(1001L, "13900000001", "x"));
    }

    @Test
    void renew_succeeds_extendsExpireBy90Days_andIncrementsCount() {
        JstSalesPreRegister r = pending(100L, 1001L, "13900000001", 0);
        when(preRegMapper.selectJstSalesPreRegisterByPreId(100L)).thenReturn(r);

        service.renew(100L, 1001L);

        verify(preRegMapper).updateJstSalesPreRegister(argThat(updated ->
            updated.getRenewCount() == 1 && updated.getExpireAt() != null
        ));
    }

    @Test
    void renew_throws_whenAtMaxCount_A7() {
        JstSalesPreRegister r = pending(100L, 1001L, "13900000001", 2);
        when(preRegMapper.selectJstSalesPreRegisterByPreId(100L)).thenReturn(r);

        assertThrows(ServiceException.class, () -> service.renew(100L, 1001L));
    }

    @Test
    void remove_throws_whenNotOwnPreReg() {
        JstSalesPreRegister r = pending(100L, 2001L, "13900000001", 0);  // 别人的
        when(preRegMapper.selectJstSalesPreRegisterByPreId(100L)).thenReturn(r);

        assertThrows(ServiceException.class, () -> service.remove(100L, 1001L));
    }

    @Test
    void expirePendingByCron_marksAllExpired() {
        when(preRegMapper.selectExpiredPending()).thenReturn(Arrays.asList(1L, 2L, 3L));
        when(preRegMapper.markExpiredBatch(anyList())).thenReturn(3);

        int n = service.expirePendingByCron();
        assertEquals(3, n);
    }

    private JstSales salesWithPhone(Long id, String phone) {
        JstSales s = new JstSales();
        s.setSalesId(id);
        s.setPhone(phone);
        return s;
    }

    private JstSalesPreRegister pending(Long id, Long salesId, String phone, int renewed) {
        JstSalesPreRegister r = new JstSalesPreRegister();
        r.setPreId(id);
        r.setSalesId(salesId);
        r.setPhone(phone);
        r.setStatus("pending");
        r.setRenewCount(renewed);
        r.setExpireAt(new java.util.Date(System.currentTimeMillis() + 86400_000L));
        return r;
    }
}
