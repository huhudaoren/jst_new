package com.ruoyi.jst.marketing.enums;

import java.util.Arrays;

/**
 * Coupon type enum with backward-compatible aliases.
 */
public enum CouponType {

    FULL_REDUCE("full_reduce"),
    DIRECT_REDUCE("direct_reduce"),
    DISCOUNT("discount"),
    CONTEST_SPECIFIC("contest_specific");

    private final String dbValue;

    CouponType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static CouponType fromValue(String value) {
        if (value == null) {
            return null;
        }
        if ("direct".equalsIgnoreCase(value)) {
            return DIRECT_REDUCE;
        }
        return Arrays.stream(values())
                .filter(item -> item.dbValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
