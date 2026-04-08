package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.statemachine.StateMachine;

import java.util.Map;
import java.util.Set;

/**
 * 动态表单模板审核状态机（SM-25）。
 * <p>
 * 状态值与 {@code jst_enroll_form_template.audit_status} 字段保持一致。
 *
 * @author jst
 * @since 1.0.0
 */
public enum FormTemplateAuditStatus implements StateMachine<FormTemplateAuditStatus> {

    /** 草稿。 */
    DRAFT("draft"),
    /** 待审核。 */
    PENDING("pending"),
    /** 审核通过。 */
    APPROVED("approved"),
    /** 审核驳回。 */
    REJECTED("rejected");

    private static final Map<FormTemplateAuditStatus, Set<FormTemplateAuditStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(DRAFT, PENDING),
            PENDING, Set.of(APPROVED, REJECTED),
            REJECTED, Set.of(DRAFT)
    );

    private final String dbValue;

    FormTemplateAuditStatus(String dbValue) {
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
     * 按数据库值解析审核状态。
     *
     * @param value 数据库值
     * @return 状态枚举
     */
    public static FormTemplateAuditStatus fromDb(String value) {
        for (FormTemplateAuditStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知 form_template.audit_status: " + value);
    }

    @Override
    public Map<FormTemplateAuditStatus, Set<FormTemplateAuditStatus>> allowed() {
        return ALLOWED;
    }

    @Override
    public void assertCanTransitTo(FormTemplateAuditStatus target) {
        Set<FormTemplateAuditStatus> allowedSet = ALLOWED.getOrDefault(this, Set.of());
        if (!allowedSet.contains(target)) {
            throw new ServiceException(
                    String.format("表单模板状态非法跃迁: %s → %s", dbValue, target.dbValue),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_ILLEGAL_TRANSIT.code());
        }
    }
}
