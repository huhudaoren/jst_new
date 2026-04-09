package com.ruoyi.jst.order.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Refund workflow status for {@code jst_refund_record.status}.
 */
public enum RefundStatus {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    REFUNDING("refunding"),
    COMPLETED("completed"),
    CLOSED("closed");

    private static final Map<RefundStatus, Set<RefundStatus>> ALLOWED = new EnumMap<>(RefundStatus.class);

    static {
        ALLOWED.put(PENDING, EnumSet.of(APPROVED, REJECTED, CLOSED));
        ALLOWED.put(APPROVED, EnumSet.of(REFUNDING, CLOSED));
        ALLOWED.put(REJECTED, Collections.emptySet());
        ALLOWED.put(REFUNDING, EnumSet.of(COMPLETED));
        ALLOWED.put(COMPLETED, Collections.emptySet());
        ALLOWED.put(CLOSED, Collections.emptySet());
    }

    private final String dbValue;

    RefundStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(RefundStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException("退款状态非法流转: " + this.dbValue + " -> " + target.dbValue,
                    BizErrorCode.JST_ORDER_REFUND_ILLEGAL_TRANSIT.code());
        }
    }

    public static RefundStatus fromDb(String dbValue) {
        for (RefundStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知退款状态: " + dbValue,
                BizErrorCode.JST_ORDER_REFUND_ILLEGAL_TRANSIT.code());
    }
}
