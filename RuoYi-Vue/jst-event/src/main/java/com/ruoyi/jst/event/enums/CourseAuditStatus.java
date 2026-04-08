package com.ruoyi.jst.event.enums;

import com.ruoyi.jst.common.statemachine.StateMachine;

import java.util.Map;
import java.util.Set;

/**
 * 课程审核状态机（SM-21）。
 * <p>
 * 状态值与 {@code jst_course.audit_status} 数据库枚举保持一致。
 *
 * @author jst
 * @since 1.0.0
 */
public enum CourseAuditStatus implements StateMachine<CourseAuditStatus> {

    /** 草稿态。 */
    DRAFT("draft"),
    /** 待审核。 */
    PENDING("pending"),
    /** 审核通过。 */
    APPROVED("approved"),
    /** 审核驳回。 */
    REJECTED("rejected");

    private static final Map<CourseAuditStatus, Set<CourseAuditStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(PENDING),
            PENDING, Set.of(APPROVED, REJECTED)
    );

    private final String dbValue;

    CourseAuditStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * 返回数据库枚举值。
     *
     * @return 数据库存储值
     */
    public String dbValue() {
        return dbValue;
    }

    /**
     * 当前状态是否允许编辑课程基础信息。
     *
     * @return true-允许编辑
     */
    public boolean canEdit() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * 由数据库值转换为状态枚举。
     *
     * @param value 数据库存储值
     * @return 状态枚举
     */
    public static CourseAuditStatus fromDb(String value) {
        if (value == null) {
            return null;
        }
        for (CourseAuditStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知 course_audit_status: " + value);
    }

    @Override
    public Map<CourseAuditStatus, Set<CourseAuditStatus>> allowed() {
        return ALLOWED;
    }
}
