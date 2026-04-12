package com.ruoyi.jst.channel.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 返点台账状态枚举（SM-8）。
 *
 * @author jst
 * @since 1.0.0
 */
public enum RebateLedgerStatus {

    PENDING_ACCRUAL("pending_accrual"),
    WITHDRAWABLE("withdrawable"),
    IN_REVIEW("in_review"),
    PAID("paid"),
    ROLLED_BACK("rolled_back"),
    NEGATIVE("negative");

    private static final Map<RebateLedgerStatus, Set<RebateLedgerStatus>> ALLOWED =
            new EnumMap<>(RebateLedgerStatus.class);

    static {
        ALLOWED.put(PENDING_ACCRUAL, EnumSet.of(WITHDRAWABLE, ROLLED_BACK));
        ALLOWED.put(WITHDRAWABLE, EnumSet.of(IN_REVIEW, ROLLED_BACK));
        ALLOWED.put(IN_REVIEW, EnumSet.of(WITHDRAWABLE, PAID));
        ALLOWED.put(PAID, EnumSet.of(NEGATIVE));
        ALLOWED.put(ROLLED_BACK, Collections.emptySet());
        ALLOWED.put(NEGATIVE, Collections.emptySet());
    }

    private final String dbValue;

    RebateLedgerStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public void assertCanTransitTo(RebateLedgerStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_SETTLE_ILLEGAL_TRANSIT.message(),
                    BizErrorCode.JST_CHANNEL_SETTLE_ILLEGAL_TRANSIT.code());
        }
    }

    public static RebateLedgerStatus fromDb(String dbValue) {
        for (RebateLedgerStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException(BizErrorCode.JST_CHANNEL_SETTLE_ILLEGAL_TRANSIT.message(),
                BizErrorCode.JST_CHANNEL_SETTLE_ILLEGAL_TRANSIT.code());
    }
}
