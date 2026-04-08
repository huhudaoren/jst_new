package com.ruoyi.jst.order.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 订单主状态枚举。
 * <p>
 * 对应状态机：SM-1 / jst_order_main.order_status。
 *
 * @author jst
 * @since 1.0.0
 */
public enum OrderStatus {

    CREATED("created"),
    PENDING_PAY("pending_pay"),
    PAID("paid"),
    REVIEWING("reviewing"),
    IN_SERVICE("in_service"),
    AFTERSALE("aftersale"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    CLOSED("closed");

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED.put(CREATED, EnumSet.of(PENDING_PAY, COMPLETED, CANCELLED));
        ALLOWED.put(PENDING_PAY, EnumSet.of(PAID, CANCELLED));
        ALLOWED.put(PAID, EnumSet.of(REVIEWING));
        ALLOWED.put(REVIEWING, EnumSet.of(IN_SERVICE, CANCELLED));
        ALLOWED.put(IN_SERVICE, EnumSet.of(AFTERSALE, COMPLETED));
        ALLOWED.put(AFTERSALE, EnumSet.of(IN_SERVICE));
        ALLOWED.put(COMPLETED, EnumSet.of(CLOSED));
        ALLOWED.put(CANCELLED, Collections.emptySet());
        ALLOWED.put(CLOSED, Collections.emptySet());
    }

    private final String dbValue;

    OrderStatus(String dbValue) {
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
     * 校验订单状态迁移是否合法。
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(OrderStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException("订单状态非法跃迁: " + this.dbValue + " -> " + target.dbValue,
                    BizErrorCode.JST_ORDER_ILLEGAL_TRANSIT.code());
        }
    }

    /**
     * 按数据库值解析枚举。
     *
     * @param dbValue 数据库值
     * @return 枚举
     */
    public static OrderStatus fromDb(String dbValue) {
        for (OrderStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知订单状态: " + dbValue, BizErrorCode.JST_ORDER_ILLEGAL_TRANSIT.code());
    }
}
