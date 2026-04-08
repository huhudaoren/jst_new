package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 赛事业务状态机枚举。
 * <p>
 * 对应状态机：SM-5b / jst_contest.status。
 *
 * @author jst
 * @since 1.0.0
 */
public enum ContestBizStatus {

    NOT_STARTED("not_started"),
    ENROLLING("enrolling"),
    CLOSED("closed"),
    SCORING("scoring"),
    PUBLISHED("published"),
    ENDED("ended");

    private static final Map<ContestBizStatus, Set<ContestBizStatus>> ALLOWED = new EnumMap<>(ContestBizStatus.class);

    static {
        ALLOWED.put(NOT_STARTED, EnumSet.of(ENROLLING));
        ALLOWED.put(ENROLLING, EnumSet.of(CLOSED));
        ALLOWED.put(CLOSED, EnumSet.of(SCORING));
        ALLOWED.put(SCORING, EnumSet.of(PUBLISHED));
        ALLOWED.put(PUBLISHED, EnumSet.of(ENDED));
        ALLOWED.put(ENDED, Collections.emptySet());
    }

    private final String dbValue;

    ContestBizStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(ContestBizStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException(
                    String.format("赛事业务状态非法跃迁: %s -> %s", this.dbValue, target.dbValue),
                    BizErrorCode.JST_EVENT_CONTEST_ILLEGAL_TRANSIT.code());
        }
    }

    public static ContestBizStatus fromDb(String dbValue) {
        for (ContestBizStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知赛事业务状态: " + dbValue,
                BizErrorCode.JST_EVENT_CONTEST_ILLEGAL_TRANSIT.code());
    }
}
