package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Map;
import java.util.Set;

/**
 * 成绩发布状态枚举，关联 SM-19。
 *
 * @author jst
 * @since 1.0.0
 */
public enum ScorePublishStatus {

    /** 未发布。 */
    UNPUBLISHED("unpublished"),

    /** 已发布。 */
    PUBLISHED("published"),

    /** 已撤回。 */
    WITHDRAWN("withdrawn");

    private static final Map<ScorePublishStatus, Set<ScorePublishStatus>> ALLOWED = Map.of(
            UNPUBLISHED, Set.of(PUBLISHED),
            PUBLISHED, Set.of(WITHDRAWN)
    );

    private final String dbValue;

    ScorePublishStatus(String dbValue) {
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
    public static ScorePublishStatus fromDb(String value) {
        for (ScorePublishStatus status : values()) {
            if (status.dbValue.equals(value)) {
                return status;
            }
        }
        throw new ServiceException("未知成绩发布状态: " + value,
                BizErrorCode.JST_EVENT_SCORE_ILLEGAL_TRANSIT.code());
    }

    /**
     * 校验是否允许跃迁到目标状态。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(ScorePublishStatus target) {
        if (!ALLOWED.getOrDefault(this, Set.of()).contains(target)) {
            throw new ServiceException("非法成绩发布状态跃迁: " + dbValue + " -> " + target.dbValue(),
                    BizErrorCode.JST_EVENT_SCORE_ILLEGAL_TRANSIT.code());
        }
    }
}
