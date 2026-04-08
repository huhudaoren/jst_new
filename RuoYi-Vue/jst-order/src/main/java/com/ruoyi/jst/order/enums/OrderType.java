package com.ruoyi.jst.order.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * 订单类型枚举。
 *
 * @author jst
 * @since 1.0.0
 */
public enum OrderType {

    NORMAL("normal"),
    ZERO_PRICE("zero_price"),
    FULL_DEDUCT("full_deduct"),
    EXCHANGE("exchange"),
    APPOINTMENT("appointment");

    private final String dbValue;

    OrderType(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * 返回数据库枚举值。
     *
     * @return 数据库值
     */
    public String dbValue() {
        return dbValue;
    }

    /**
     * 按数据库值解析枚举。
     *
     * @param dbValue 数据库值
     * @return 枚举
     */
    public static OrderType fromDb(String dbValue) {
        for (OrderType value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知订单类型: " + dbValue, BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }
}
