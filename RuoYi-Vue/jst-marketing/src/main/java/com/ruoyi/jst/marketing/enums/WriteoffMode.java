package com.ruoyi.jst.marketing.enums;

/**
 * Rights writeoff mode with compatibility helpers.
 */
public enum WriteoffMode {

    SELF_APPLY("self_apply"),
    SCAN("scan"),
    ONLINE_AUTO("online_auto"),
    MANUAL_REVIEW("manual_review"),
    OFFLINE_CONFIRM("offline_confirm");

    private final String dbValue;

    WriteoffMode(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static boolean isSelfApply(String value) {
        return SELF_APPLY.dbValue.equalsIgnoreCase(value) || ONLINE_AUTO.dbValue.equalsIgnoreCase(value);
    }

    public static boolean isScan(String value) {
        return SCAN.dbValue.equalsIgnoreCase(value) || OFFLINE_CONFIRM.dbValue.equalsIgnoreCase(value);
    }
}
