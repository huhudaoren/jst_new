package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Map;
import java.util.Set;

/**
 * 成绩审核状态枚举。
 *
 * @author jst
 * @since 1.0.0
 */
public enum ScoreAuditStatus {

    /** 草稿，赛事方可编辑。 */
    DRAFT("draft"),

    /** 待平台审核。 */
    PENDING("pending"),

    /** 平台审核通过。 */
    APPROVED("approved"),

    /** 平台审核驳回。 */
    REJECTED("rejected");

    private static final Map<ScoreAuditStatus, Set<ScoreAuditStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(PENDING),
            REJECTED, Set.of(PENDING),
            PENDING, Set.of(APPROVED, REJECTED)
    );

    private final String dbValue;

    ScoreAuditStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * 返回数据库状态值。
     *
     * @return 数据库状态值
     */
    public String dbValue() {
        return dbValue;
    }

    /**
     * 从数据库状态值解析枚举。
     *
     * @param value 数据库状态值
     * @return 状态枚举
     */
    public static ScoreAuditStatus fromDb(String value) {
        for (ScoreAuditStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new ServiceException("未知成绩审核状态: " + value,
                BizErrorCode.JST_EVENT_SCORE_ILLEGAL_TRANSIT.code());
    }

    /**
     * 校验是否允许跃迁到目标状态。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(ScoreAuditStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new ServiceException("非法成绩审核状态跃迁: " + dbValue + " -> " + target.dbValue(),
                    BizErrorCode.JST_EVENT_SCORE_ILLEGAL_TRANSIT.code());
        }
    }
}
