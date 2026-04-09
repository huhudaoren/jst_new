package com.ruoyi.jst.marketing.enums;

import java.util.Arrays;

/**
 * User coupon status.
 */
public enum CouponStatus {

    NEW("new"),
    UNUSED("unused"),
    LOCKED("locked"),
    USED("used"),
    EXPIRED("expired"),
    VOIDED("voided"),
    REFUNDED("refunded");

    private final String dbValue;

    CouponStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static CouponStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.dbValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
