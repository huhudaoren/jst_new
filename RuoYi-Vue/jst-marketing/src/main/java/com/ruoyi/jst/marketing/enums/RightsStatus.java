package com.ruoyi.jst.marketing.enums;

import java.util.Arrays;

/**
 * User rights status.
 */
public enum RightsStatus {

    AVAILABLE("available"),
    APPLYING("applying"),
    APPROVED("approved"),
    USED("used"),
    EXPIRED("expired"),
    VOIDED("voided"),
    REJECTED("rejected"),
    LOCKED("locked"),
    REVOKED("revoked");

    private final String dbValue;

    RightsStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static RightsStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.dbValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
