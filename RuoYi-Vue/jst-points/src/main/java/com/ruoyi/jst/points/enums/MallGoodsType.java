package com.ruoyi.jst.points.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * 积分商城商品类型。
 *
 * @author jst
 * @since 1.0.0
 */
public enum MallGoodsType {

    VIRTUAL("virtual"),
    PHYSICAL("physical");

    private final String dbValue;

    MallGoodsType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 按数据库值解析商品类型。
     *
     * @param value 数据库存储值
     * @return 商品类型
     */
    public static MallGoodsType fromDb(String value) {
        for (MallGoodsType type : values()) {
            if (type.dbValue.equalsIgnoreCase(StringUtils.trim(value))) {
                return type;
            }
        }
        throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }
}
