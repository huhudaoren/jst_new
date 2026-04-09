package com.ruoyi.jst.channel.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Withdrawal settlement status for {@code jst_rebate_settlement.status}.
 */
public enum RebateSettlementStatus {

    PENDING("pending"),
    REVIEWING("reviewing"),
    REJECTED("rejected"),
    APPROVED("approved"),
    PAID("paid"),
    CLOSED("closed");

    private static final Map<RebateSettlementStatus, Set<RebateSettlementStatus>> ALLOWED =
            new EnumMap<>(RebateSettlementStatus.class);

    static {
        ALLOWED.put(PENDING, EnumSet.of(REVIEWING, REJECTED, APPROVED, CLOSED));
        ALLOWED.put(REVIEWING, EnumSet.of(REJECTED, APPROVED));
        ALLOWED.put(REJECTED, Collections.emptySet());
        ALLOWED.put(APPROVED, EnumSet.of(PAID));
        ALLOWED.put(PAID, Collections.emptySet());
        ALLOWED.put(CLOSED, Collections.emptySet());
    }

    private final String dbValue;

    RebateSettlementStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(RebateSettlementStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                    BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
        }
    }

    public static RebateSettlementStatus fromDb(String dbValue) {
        for (RebateSettlementStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException(BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.message(),
                BizErrorCode.JST_CHANNEL_WITHDRAW_STATUS_INVALID.code());
    }
}
