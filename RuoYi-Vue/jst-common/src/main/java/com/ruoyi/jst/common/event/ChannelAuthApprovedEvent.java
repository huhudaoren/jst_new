package com.ruoyi.jst.common.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 渠道认证审核通过事件（跨模块事件，放 jst-common）
 * <p>
 * 由 jst-organizer ChannelAuthApplyServiceImpl.approve() 发布，
 * 由 jst-marketing GrantInitialRightsListener 监听，自动发放初始等级权益（Q-05）。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelAuthApprovedEvent extends ApplicationEvent {

    private final Long userId;
    private final Long channelId;
    private final Long applyId;
    private final String channelType;
    private final Long bizId;
    private final String bizType;
    private final Map<String, Object> extraData;

    /**
     * @param source      事件源
     * @param userId      认证通过的用户ID
     * @param channelId   新创建的渠道档案ID
     * @param channelType 渠道类型 teacher/organization/individual
     */
    public ChannelAuthApprovedEvent(Object source, Long userId, Long channelId, String channelType) {
        this(source, userId, channelId, null, channelType, null);
    }

    /**
     * @param source      事件源
     * @param userId      认证通过的用户ID
     * @param channelId   新创建的渠道档案ID
     * @param applyId     认证申请ID
     * @param channelType 渠道类型 teacher/organization/individual
     * @param extraData   扩展数据
     */
    public ChannelAuthApprovedEvent(Object source, Long userId, Long channelId, Long applyId,
                                    String channelType, Map<String, Object> extraData) {
        super(source);
        this.userId = userId;
        this.channelId = channelId;
        this.applyId = applyId;
        this.channelType = channelType;
        this.bizId = applyId == null ? channelId : applyId;
        this.bizType = "auth_approved";
        this.extraData = immutableExtraData(extraData, channelType);
    }

    public Long getUserId() {
        return userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getApplyId() {
        return applyId;
    }

    public String getChannelType() {
        return channelType;
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

    private static Map<String, Object> immutableExtraData(Map<String, Object> extraData, String channelType) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        if (extraData != null) {
            data.putAll(extraData);
        }
        if (!data.containsKey("channelType")) {
            data.put("channelType", channelType);
        }
        return Collections.unmodifiableMap(data);
    }
}
