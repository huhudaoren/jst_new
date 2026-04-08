package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 赛事审核状态机枚举。
 * <p>
 * 对应状态机：SM-5a / jst_contest.audit_status。
 *
 * @author jst
 * @since 1.0.0
 */
public enum ContestAuditStatus {

    DRAFT("draft"),
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    ONLINE("online"),
    OFFLINE("offline");

    private static final Map<ContestAuditStatus, Set<ContestAuditStatus>> ALLOWED = new EnumMap<>(ContestAuditStatus.class);

    static {
        ALLOWED.put(DRAFT, EnumSet.of(PENDING));
        ALLOWED.put(PENDING, EnumSet.of(APPROVED, REJECTED));
        ALLOWED.put(APPROVED, EnumSet.of(ONLINE));
        ALLOWED.put(REJECTED, Collections.emptySet());
        ALLOWED.put(ONLINE, EnumSet.of(OFFLINE));
        ALLOWED.put(OFFLINE, Collections.emptySet());
    }

    private final String dbValue;

    ContestAuditStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(ContestAuditStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException(
                    String.format("赛事审核状态非法跃迁: %s -> %s", this.dbValue, target.dbValue),
                    BizErrorCode.JST_EVENT_CONTEST_ILLEGAL_TRANSIT.code());
        }
    }

    public static ContestAuditStatus fromDb(String dbValue) {
        for (ContestAuditStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知赛事审核状态: " + dbValue,
                BizErrorCode.JST_EVENT_CONTEST_ILLEGAL_TRANSIT.code());
    }
}
