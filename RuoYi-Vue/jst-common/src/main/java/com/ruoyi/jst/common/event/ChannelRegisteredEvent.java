package com.ruoyi.jst.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 渠道注册成功事件。
 * <p>
 * 由 jst-user / jst-organizer 渠道注册成功路径发布；
 * 由 jst-channel SalesAutoBindingListener 订阅触发自动绑定算法 (spec §2.1)。
 * <p>
 * businessNo / inviteCode 字段 plan-02 Task 8 先传 null，plan-04 Task 8 wire 注册 DTO 字段后填。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelRegisteredEvent extends ApplicationEvent {

    private final Long channelId;
    private final String registeredPhone;
    private final String filledBusinessNo;
    private final String filledInviteCode;

    public ChannelRegisteredEvent(Object source, Long channelId, String registeredPhone,
                                   String filledBusinessNo, String filledInviteCode) {
        super(source);
        this.channelId = channelId;
        this.registeredPhone = registeredPhone;
        this.filledBusinessNo = filledBusinessNo;
        this.filledInviteCode = filledInviteCode;
    }

    public Long getChannelId() { return channelId; }
    public String getRegisteredPhone() { return registeredPhone; }
    public String getFilledBusinessNo() { return filledBusinessNo; }
    public String getFilledInviteCode() { return filledInviteCode; }
}
