package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * SM-12 核销子项状态。
 */
public enum WriteoffItemStatus {

    UNUSED("unused"),
    USED("used"),
    EXPIRED("expired"),
    VOIDED("voided");

    private static final Map<WriteoffItemStatus, Set<WriteoffItemStatus>> ALLOWED;

    static {
        Map<WriteoffItemStatus, Set<WriteoffItemStatus>> mapping = new EnumMap<>(WriteoffItemStatus.class);
        mapping.put(UNUSED, EnumSet.of(USED, EXPIRED, VOIDED));
        ALLOWED = Collections.unmodifiableMap(mapping);
    }

    private final String dbValue;

    WriteoffItemStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(WriteoffItemStatus target) {
        if (target == null || !ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException("核销子项状态非法跃迁: " + dbValue + " -> "
                    + (target == null ? "null" : target.dbValue),
                    BizErrorCode.JST_ORDER_WRITEOFF_ITEM_STATUS_INVALID.code());
        }
    }

    public static WriteoffItemStatus fromDb(String dbValue) {
        for (WriteoffItemStatus status : values()) {
            if (status.dbValue.equals(dbValue)) {
                return status;
            }
        }
        throw new ServiceException("未知核销子项状态: " + dbValue,
                BizErrorCode.JST_ORDER_WRITEOFF_ITEM_STATUS_INVALID.code());
    }
}
