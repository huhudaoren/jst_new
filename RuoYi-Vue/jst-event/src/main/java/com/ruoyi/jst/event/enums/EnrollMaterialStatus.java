package com.ruoyi.jst.event.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 报名资料状态枚举。
 * <p>
 * 对应状态机: draft -> submitted -> supplement。
 *
 * @author jst
 * @since 1.0.0
 */
public enum EnrollMaterialStatus {

    DRAFT("draft"),
    SUBMITTED("submitted"),
    SUPPLEMENT("supplement");

    private static final Map<EnrollMaterialStatus, Set<EnrollMaterialStatus>> ALLOWED = Map.of(
            DRAFT, EnumSet.of(SUBMITTED),
            SUBMITTED, EnumSet.of(SUPPLEMENT)
    );

    private final String dbValue;

    EnrollMaterialStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    /**
     * 校验状态迁移是否合法。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(EnrollMaterialStatus target) {
        if (!ALLOWED.getOrDefault(this, EnumSet.noneOf(EnrollMaterialStatus.class)).contains(target)) {
            throw new ServiceException("报名资料状态非法跃迁: " + this.dbValue + " -> " + target.dbValue,
                    BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
        }
    }

    /**
     * 按数据库值解析枚举。
     *
     * @param dbValue 数据库值
     * @return 枚举
     */
    public static EnrollMaterialStatus fromDb(String dbValue) {
        for (EnrollMaterialStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知报名资料状态: " + dbValue,
                BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
    }
}
