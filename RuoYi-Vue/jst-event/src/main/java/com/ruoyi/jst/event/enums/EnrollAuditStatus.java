package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 报名审核状态枚举。
 * <p>
 * 对应状态机 SM-6: pending -> approved|rejected|supplement, supplement -> pending。
 *
 * @author jst
 * @since 1.0.0
 */
public enum EnrollAuditStatus {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    SUPPLEMENT("supplement");

    private static final Map<EnrollAuditStatus, Set<EnrollAuditStatus>> ALLOWED = Map.of(
            PENDING, EnumSet.of(APPROVED, REJECTED, SUPPLEMENT),
            SUPPLEMENT, EnumSet.of(PENDING)
    );

    private final String dbValue;

    EnrollAuditStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 校验状态迁移是否合法。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(EnrollAuditStatus target) {
        if (!ALLOWED.getOrDefault(this, EnumSet.noneOf(EnrollAuditStatus.class)).contains(target)) {
            throw new ServiceException("报名审核状态非法跃迁: " + this.dbValue + " -> " + target.dbValue,
                    BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
        }
    }

    /**
     * 按数据库值解析枚举。
     *
     * @param dbValue 数据库值
     * @return 枚举
     */
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
