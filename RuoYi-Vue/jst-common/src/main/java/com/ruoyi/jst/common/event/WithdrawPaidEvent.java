package com.ruoyi.jst.common.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 渠道提现打款成功事件。
 *
 * @author jst
 * @since 1.0.0
 */
public class WithdrawPaidEvent extends ApplicationEvent {

    private final Long userId;
    private final Long bizId;
    private final String bizType;
    private final Map<String, Object> extraData;

    /**
     * @param source    事件源
     * @param userId    用户ID
     * @param bizId     业务ID（settlement_id）
     * @param bizType   业务类型（withdraw_paid）
     * @param extraData 扩展数据
     */
    public WithdrawPaidEvent(Object source, Long userId, Long bizId, String bizType, Map<String, Object> extraData) {
        super(source);
        this.userId = userId;
        this.bizId = bizId;
        this.bizType = bizType;
        this.extraData = immutableExtraData(extraData);
    }

    public Long getUserId() {
        return userId;
    }

    public Long getBizId() {
        return bizId;
    }

    public String getBizType() {
        return bizType;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    private static Map<String, Object> immutableExtraData(Map<String, Object> extraData) {
        if (extraData == null || extraData.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(extraData));
    }
}

