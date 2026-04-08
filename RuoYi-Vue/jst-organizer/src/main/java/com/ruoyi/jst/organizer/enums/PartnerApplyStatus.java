package com.ruoyi.jst.organizer.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.statemachine.StateMachine;

import java.util.Map;
import java.util.Set;

/**
 * 赛事方入驻申请状态机（SM-4）
 * <p>
 * 状态值与 {@code jst_event_partner_apply.apply_status} 字段保持一致。
 *
 * @author jst
 * @since 1.0.0
 */
public enum PartnerApplyStatus implements StateMachine<PartnerApplyStatus> {

    /** 草稿 */
    DRAFT("draft"),
    /** 待审核 */
    PENDING("pending"),
    /** 审核通过 */
    APPROVED("approved"),
    /** 驳回 */
    REJECTED("rejected"),
    /** 补件 */
    SUPPLEMENT("supplement");

    private static final Map<PartnerApplyStatus, Set<PartnerApplyStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(PENDING),
            PENDING, Set.of(APPROVED, REJECTED, SUPPLEMENT),
            SUPPLEMENT, Set.of(PENDING)
    );

    private final String dbValue;

    PartnerApplyStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 按数据库值解析状态枚举。
     *
     * @param value 数据库状态值
     * @return 状态枚举
     */
    public static PartnerApplyStatus fromDb(String value) {
        for (PartnerApplyStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知 apply_status: " + value);
    }

    @Override
    public Map<PartnerApplyStatus, Set<PartnerApplyStatus>> allowed() {
        return ALLOWED;
    }

    @Override
    public void assertCanTransitTo(PartnerApplyStatus target) {
        Set<PartnerApplyStatus> allowedSet = ALLOWED.getOrDefault(this, Set.of());
        if (!allowedSet.contains(target)) {
            throw new ServiceException(
                    String.format("赛事方入驻申请状态非法跃迁: %s → %s", dbValue, target.dbValue),
                    BizErrorCode.JST_ORG_APPLY_ILLEGAL_TRANSIT.code());
        }
    }
}
