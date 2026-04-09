package com.ruoyi.jst.marketing.enums;

/**
 * Rights quota mode with compatibility helpers.
 */
public enum QuotaMode {

    UNLIMITED("unlimited"),
    COUNT("count"),
    AMOUNT("amount"),
    TIMES("times"),
    PERIOD("period");

    private final String dbValue;

    QuotaMode(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static QuotaMode fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (QuotaMode mode : values()) {
            if (mode.dbValue.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        return null;
    }

    public static boolean isUnlimited(String value) {
        return UNLIMITED.dbValue.equalsIgnoreCase(value) || PERIOD.dbValue.equalsIgnoreCase(value);
    }

    public static boolean isCountLike(String value) {
        return COUNT.dbValue.equalsIgnoreCase(value) || TIMES.dbValue.equalsIgnoreCase(value);
    }
}
