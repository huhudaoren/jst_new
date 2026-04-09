package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * SM-11 个人预约主状态。
 */
public enum AppointmentStatus {

    UNBOOKED("unbooked"),
    BOOKED("booked"),
    PARTIAL_WRITEOFF("partial_writeoff"),
    FULLY_WRITEOFF("fully_writeoff"),
    PARTIAL_WRITEOFF_ENDED("partial_writeoff_ended"),
    CANCELLED("cancelled"),
    EXPIRED("expired"),
    NO_SHOW("no_show");

    private static final Map<AppointmentStatus, Set<AppointmentStatus>> ALLOWED;

    static {
        Map<AppointmentStatus, Set<AppointmentStatus>> mapping = new EnumMap<>(AppointmentStatus.class);
        mapping.put(UNBOOKED, EnumSet.of(BOOKED));
        mapping.put(BOOKED, EnumSet.of(PARTIAL_WRITEOFF, FULLY_WRITEOFF, CANCELLED, EXPIRED, NO_SHOW));
        mapping.put(PARTIAL_WRITEOFF, EnumSet.of(FULLY_WRITEOFF, PARTIAL_WRITEOFF_ENDED));
        ALLOWED = Collections.unmodifiableMap(mapping);
    }

    private final String dbValue;

    AppointmentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(AppointmentStatus target) {
        if (target == null || !ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException("预约状态非法跃迁: " + dbValue + " -> "
                    + (target == null ? "null" : target.dbValue),
                    BizErrorCode.JST_ORDER_APPOINTMENT_ILLEGAL_TRANSIT.code());
        }
    }

    public static AppointmentStatus fromDb(String dbValue) {
        for (AppointmentStatus status : values()) {
            if (status.dbValue.equals(dbValue)) {
                return status;
            }
        }
        throw new ServiceException("未知预约状态: " + dbValue,
                BizErrorCode.JST_ORDER_APPOINTMENT_ILLEGAL_TRANSIT.code());
    }
}
