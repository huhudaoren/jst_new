package com.ruoyi.jst.common.event;

import org.springframework.context.ApplicationEvent;

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
    private final String channelType;

    /**
     * @param source      事件源
     * @param userId      认证通过的用户ID
     * @param channelId   新创建的渠道档案ID
     * @param channelType 渠道类型 teacher/organization/individual
     */
    public ChannelAuthApprovedEvent(Object source, Long userId, Long channelId, String channelType) {
        super(source);
        this.userId = userId;
        this.channelId = channelId;
        this.channelType = channelType;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public String getChannelType() {
        return channelType;
    }
}
