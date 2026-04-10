package com.ruoyi.jst.organizer.enums;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 渠道认证状态机枚举
 * <p>
 * 关联表：
 * <ul>
 *   <li>jst_channel_auth_apply.apply_status</li>
 *   <li>jst_channel.auth_status</li>
 * </ul>
 * 关联状态机：SM-3 渠道认证
 *
 * @author jst
 * @since 1.0.0
 */
public enum ChannelAuthStatus {

    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    SUSPENDED("suspended");

    private static final Map<ChannelAuthStatus, Set<ChannelAuthStatus>> ALLOWED =
            new EnumMap<>(ChannelAuthStatus.class);

    static {
        ALLOWED.put(PENDING, EnumSet.of(APPROVED, REJECTED));
        ALLOWED.put(APPROVED, EnumSet.of(SUSPENDED));
        ALLOWED.put(REJECTED, EnumSet.of(PENDING));  // Q-02: 驳回后允许重提（受 rejectCount 限制）
        ALLOWED.put(SUSPENDED, Collections.emptySet());
    }

    private final String dbValue;

    ChannelAuthStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * 数据库存储值
     *
     * @return 小写下划线状态值
     */
    public String dbValue() {
        return dbValue;
    }

    /**
     * 从数据库状态值解析枚举
     *
     * @param dbValue 数据库存储值
     * @return 对应枚举
     */
    public static ChannelAuthStatus fromDb(String dbValue) {
        for (ChannelAuthStatus value : values()) {
            if (value.dbValue.equals(dbValue)) {
                return value;
            }
        }
        throw new ServiceException("未知渠道认证状态：" + dbValue,
                BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }

    /**
     * 校验状态机跃迁是否合法
     *
     * @param target 目标状态
     */
    public void assertCanTransitTo(ChannelAuthStatus target) {
        if (!ALLOWED.getOrDefault(this, Collections.emptySet()).contains(target)) {
            throw new ServiceException("非法渠道认证状态跃迁：" + this.dbValue + "→" + target.dbValue,
                    BizErrorCode.JST_CHANNEL_AUTH_APPLY_ILLEGAL_TRANSIT.code());
        }
    }
}
