package com.ruoyi.jst.points.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;

/**
 * 虚拟商品发放目标类型。
 *
 * @author jst
 * @since 1.0.0
 */
public enum MallVirtualTargetType {

    COUPON("coupon", true),
    RIGHTS("rights", true),
    POINTS_CARD("points_card", false);

    private final String dbValue;
    private final boolean supported;

    MallVirtualTargetType(String dbValue, boolean supported) {
        this.dbValue = dbValue;
        this.supported = supported;
    }

    public String dbValue() {
        return dbValue;
    }

    public boolean supported() {
        return supported;
    }

    /**
     * 按数据库值解析虚拟商品目标类型。
     *
     * @param value 数据库存储值
     * @return 虚拟商品目标类型
     */
    public static MallVirtualTargetType fromDb(String value) {
        for (MallVirtualTargetType type : values()) {
            if (type.dbValue.equalsIgnoreCase(StringUtils.trim(value))) {
                return type;
            }
        }
        throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }
}
