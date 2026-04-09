package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * SM-13 团队预约状态。
 */
public enum TeamAppointmentStatus {

    BOOKED("booked"),
    PARTIAL_WRITEOFF("partial_writeoff"),
    FULLY_WRITEOFF("fully_writeoff"),
    PARTIAL_WRITEOFF_ENDED("partial_writeoff_ended"),
    NO_SHOW("no_show"),
    CANCELLED("cancelled"),
    EXPIRED("expired");

    private static final Map<TeamAppointmentStatus, Set<TeamAppointmentStatus>> ALLOWED;

    static {
        Map<TeamAppointmentStatus, Set<TeamAppointmentStatus>> mapping = new EnumMap<>(TeamAppointmentStatus.class);
        mapping.put(BOOKED, EnumSet.of(PARTIAL_WRITEOFF, FULLY_WRITEOFF, NO_SHOW, CANCELLED, EXPIRED));
        mapping.put(PARTIAL_WRITEOFF, EnumSet.of(FULLY_WRITEOFF, PARTIAL_WRITEOFF_ENDED));
        ALLOWED = Collections.unmodifiableMap(mapping);
    }

    private final String dbValue;

    TeamAppointmentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(TeamAppointmentStatus target) {
        if (target == null || !ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException("团队预约状态非法跃迁: " + dbValue + " -> "
                    + (target == null ? "null" : target.dbValue),
                    BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_ILLEGAL_TRANSIT.code());
        }
    }

    public static TeamAppointmentStatus fromDb(String dbValue) {
        for (TeamAppointmentStatus status : values()) {
            if (status.dbValue.equals(dbValue)) {
                return status;
            }
        }
        throw new ServiceException("未知团队预约状态: " + dbValue,
                BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_ILLEGAL_TRANSIT.code());
    }
}
