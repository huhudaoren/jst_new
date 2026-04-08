package com.ruoyi.jst.user.enums;

import com.ruoyi.jst.common.statemachine.StateMachine;

import java.util.Map;
import java.util.Set;

/**
 * 临时参赛档案-认领状态机 (SM-14)
 * <p>
 * 状态枚举值与 jst_participant.claim_status 字段一致(小写下划线字符串)。
 * 跃迁矩阵参见 架构设计/11-状态机定义.md §SM-14
 * <p>
 * 跃迁规则：
 * <pre>
 *   unclaimed → (auto_claimed | manual_claimed | pending_manual)
 *   pending_manual → (manual_claimed | unclaimed)
 *   auto_claimed / manual_claimed → unclaimed (管理员撤销)
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
public enum ClaimStatus implements StateMachine<ClaimStatus> {

    /** 未认领 */
    UNCLAIMED("unclaimed"),
    /** 自动认领（手机号+姓名唯一命中） */
    AUTO_CLAIMED("auto_claimed"),
    /** 人工认领（管理员/用户手动） */
    MANUAL_CLAIMED("manual_claimed"),
    /** 待人工（多候选或仅手机号命中） */
    PENDING_MANUAL("pending_manual");

    private static final Map<ClaimStatus, Set<ClaimStatus>> ALLOWED = Map.of(
            UNCLAIMED,      Set.of(AUTO_CLAIMED, MANUAL_CLAIMED, PENDING_MANUAL),
            PENDING_MANUAL, Set.of(MANUAL_CLAIMED, UNCLAIMED),
            AUTO_CLAIMED,   Set.of(UNCLAIMED),
            MANUAL_CLAIMED, Set.of(UNCLAIMED)
    );

    private final String dbValue;

    ClaimStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 由 DB 字符串值还原枚举
     */
    public static ClaimStatus fromDb(String value) {
        if (value == null) return null;
        for (ClaimStatus s : values()) {
            if (s.dbValue.equals(value)) return s;
        }
        throw new IllegalArgumentException("未知 claim_status: " + value);
    }

    @Override
    public Map<ClaimStatus, Set<ClaimStatus>> allowed() {
        return ALLOWED;
    }
}
