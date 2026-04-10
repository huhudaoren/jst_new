package com.ruoyi.jst.finance.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Locale;

/**
 * SM-10 赛事结算单状态机。
 */
public enum EventSettlementStatus {

    PENDING_CONFIRM("pending_confirm"),
    REVIEWING("reviewing"),
    REJECTED("rejected"),
    PENDING_PAY("pending_pay"),
    PAID("paid");

    private final String code;

    EventSettlementStatus(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static EventSettlementStatus of(String code) {
        if (code == null) {
            return null;
        }
        String normalized = code.trim().toLowerCase(Locale.ROOT);
        for (EventSettlementStatus status : values()) {
            if (status.code.equals(normalized)) {
                return status;
            }
        }
        return null;
    }

    public static void assertCanTransit(String current, EventSettlementStatus target) {
        EventSettlementStatus source = of(current);
        if (source == PENDING_CONFIRM && target == REVIEWING) {
            return;
        }
        if (source == REVIEWING && (target == REJECTED || target == PENDING_PAY)) {
            return;
        }
        if (source == PENDING_PAY && target == PAID) {
            return;
        }
        throw new ServiceException(BizErrorCode.JST_FINANCE_SETTLEMENT_STATUS_INVALID.message(),
                BizErrorCode.JST_FINANCE_SETTLEMENT_STATUS_INVALID.code());
    }
}
