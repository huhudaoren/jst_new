package com.ruoyi.jst.order.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * 订单主表退款状态枚举。
 * <p>
 * 对应 {@code jst_order_main.refund_status}。
 *
 * @author jst
 * @since 1.0.0
 */
public enum OrderRefundStatus {

    NONE("none"),
    PARTIAL("partial"),
    FULL("full");

    private final String dbValue;

    OrderRefundStatus(String dbValue) {
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
    public static OrderRefundStatus fromDb(String dbValue) {
        for (OrderRefundStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知订单退款状态: " + dbValue, BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }
}
