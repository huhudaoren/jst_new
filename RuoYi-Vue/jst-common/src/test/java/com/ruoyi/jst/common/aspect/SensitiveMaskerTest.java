package com.ruoyi.jst.common.aspect;

import com.ruoyi.jst.common.annotation.Sensitive;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveMaskerTest {

    static class Vo {
        @Sensitive(money = true)
        BigDecimal amount = new BigDecimal("123.45");

        @Sensitive(phone = true)
        String phone = "13912345678";

        @Sensitive(rate = true)
        BigDecimal rate = new BigDecimal("0.05");

        @Sensitive(idcard = true)
        String idcard = "110101199001011234";

        String name = "alice";
    }

    @Test
    void maskMoney_setsAmountAndRateToNull_keepsOthersUnchanged() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, true, false, false);
        assertNull(vo.amount);
        assertNull(vo.rate);
        assertEquals("13912345678", vo.phone);
        assertEquals("110101199001011234", vo.idcard);
        assertEquals("alice", vo.name);
    }

    @Test
    void maskPhone_replacesMiddleFourDigitsWithStars() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, false, true, false);
        assertEquals("139****5678", vo.phone);
        assertNotNull(vo.amount);
    }

    @Test
    void maskIdcard_replacesMiddleEightDigitsWithStars() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, false, false, true);
        assertEquals("110101********1234", vo.idcard);
    }

    @Test
    void maskAll_appliesAllRulesAtOnce() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, true, true, true);
        assertNull(vo.amount);
        assertNull(vo.rate);
        assertEquals("139****5678", vo.phone);
        assertEquals("110101********1234", vo.idcard);
    }

    @Test
    void maskList_iteratesAndApplies() {
        List<Vo> list = Arrays.asList(new Vo(), new Vo());
        SensitiveMasker.maskFields(list, true, false, false);
        assertNull(list.get(0).amount);
        assertNull(list.get(1).amount);
    }

    @Test
    void mask_handlesNullObjectGracefully() {
        assertDoesNotThrow(() -> SensitiveMasker.maskFields(null, true, true, true));
    }

    @Test
    void mask_handlesShortPhoneGracefully() {
        Vo vo = new Vo();
        vo.phone = "139";
        SensitiveMasker.maskFields(vo, false, true, false);
        // 太短直接保留原值，不报错
        assertEquals("139", vo.phone);
    }
}
