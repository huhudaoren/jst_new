package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Enrollment audit status for {@code jst_enroll_record.audit_status}.
 */
public enum EnrollAuditStatus {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    SUPPLEMENT("supplement"),
    CANCELLED("cancelled");

    private static final Map<EnrollAuditStatus, Set<EnrollAuditStatus>> ALLOWED = Map.of(
            PENDING, EnumSet.of(APPROVED, REJECTED, SUPPLEMENT),
            SUPPLEMENT, EnumSet.of(PENDING),
            APPROVED, EnumSet.of(CANCELLED)
    );

    private final String dbValue;

    EnrollAuditStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(EnrollAuditStatus target) {
        if (!ALLOWED.getOrDefault(this, EnumSet.noneOf(EnrollAuditStatus.class)).contains(target)) {
            throw new ServiceException("报名审核状态非法跃迁: " + this.dbValue + " -> " + target.dbValue,
                    BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
        }
    }

    public static EnrollAuditStatus fromDb(String dbValue) {
        for (EnrollAuditStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知报名审核状态: " + dbValue,
                BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
    }
}
