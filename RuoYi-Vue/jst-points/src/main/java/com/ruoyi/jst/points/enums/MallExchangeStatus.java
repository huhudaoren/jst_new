package com.ruoyi.jst.points.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * SM-18 兑换订单状态。
 *
 * @author jst
 * @since 1.0.0
 */
public enum MallExchangeStatus {

    PENDING_PAY("pending_pay"),
    PAID("paid"),
    PENDING_SHIP("pending_ship"),
    SHIPPED("shipped"),
    COMPLETED("completed"),
    AFTERSALE("aftersale"),
    CLOSED("closed");

    private final String dbValue;

    MallExchangeStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 校验是否允许状态跃迁。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(MallExchangeStatus target) {
        boolean allowed = switch (this) {
            case PENDING_PAY -> target == PAID || target == CLOSED;
            case PAID -> target == PENDING_SHIP || target == COMPLETED;
            case PENDING_SHIP -> target == SHIPPED;
            case SHIPPED -> target == COMPLETED;
            case AFTERSALE -> target == CLOSED;
            case COMPLETED, CLOSED -> false;
        };
        if (!allowed) {
            throw new ServiceException(BizErrorCode.JST_MALL_EXCHANGE_STATUS_INVALID.message(),
                    BizErrorCode.JST_MALL_EXCHANGE_STATUS_INVALID.code());
        }
    }

    /**
     * 按数据库值解析状态。
     *
     * @param value 数据库存储值
     * @return 兑换状态
     */
    public static MallExchangeStatus fromDb(String value) {
        for (MallExchangeStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(StringUtils.trim(value))) {
                return status;
            }
        }
        throw new ServiceException(BizErrorCode.JST_MALL_EXCHANGE_STATUS_INVALID.message(),
                BizErrorCode.JST_MALL_EXCHANGE_STATUS_INVALID.code());
    }
}
