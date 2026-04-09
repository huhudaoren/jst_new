package com.ruoyi.jst.points.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * 积分商城商品状态。
 *
 * @author jst
 * @since 1.0.0
 */
public enum MallGoodsStatus {

    DRAFT("draft"),
    ON("on"),
    OFF("off");

    private final String dbValue;

    MallGoodsStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 按数据库值解析商品状态。
     *
     * @param value 数据库存储值
     * @return 商品状态
     */
    public static MallGoodsStatus fromDb(String value) {
        for (MallGoodsStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(StringUtils.trim(value))) {
                return status;
            }
        }
        throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }
}
