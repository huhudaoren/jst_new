package com.ruoyi.jst.user.enums;

import com.ruoyi.jst.common.statemachine.StateMachine;

import java.util.Map;
import java.util.Set;

/**
 * 学生-渠道绑定状态机（SM-15）。
 * <p>
 * 状态值与 {@code jst_student_channel_binding.status} 数据库枚举保持一致。
 *
 * @author jst
 * @since 1.0.0
 */
public enum BindingStatus implements StateMachine<BindingStatus> {

    /** 当前有效绑定。 */
    ACTIVE("active"),
    /** 系统过期。 */
    EXPIRED("expired"),
    /** 被新绑定覆盖。 */
    REPLACED("replaced"),
    /** 用户或平台人工解绑。 */
    MANUAL_UNBOUND("manual_unbound");

    private static final Map<BindingStatus, Set<BindingStatus>> ALLOWED = Map.of(
            ACTIVE, Set.of(EXPIRED, REPLACED, MANUAL_UNBOUND)
    );

    private final String dbValue;

    BindingStatus(String dbValue) {
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
     * 由数据库值转换为状态枚举。
     *
     * @param value 数据库存储值
     * @return 状态枚举
     */
    public static BindingStatus fromDb(String value) {
        if (value == null) {
            return null;
        }
        for (BindingStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知 binding_status: " + value);
    }

    @Override
    public Map<BindingStatus, Set<BindingStatus>> allowed() {
        return ALLOWED;
    }
}
