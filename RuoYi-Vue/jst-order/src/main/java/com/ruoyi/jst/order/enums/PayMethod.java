package com.ruoyi.jst.order.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * 支付方式枚举。
 *
 * @author jst
 * @since 1.0.0
 */
public enum PayMethod {

    WECHAT("wechat"),
    BANK_TRANSFER("bank_transfer"),
    POINTS("points"),
    POINTS_CASH_MIX("points_cash_mix");

    private final String dbValue;

    PayMethod(String dbValue) {
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
    public static PayMethod fromDb(String dbValue) {
        for (PayMethod value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知支付方式: " + dbValue, BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }
}
