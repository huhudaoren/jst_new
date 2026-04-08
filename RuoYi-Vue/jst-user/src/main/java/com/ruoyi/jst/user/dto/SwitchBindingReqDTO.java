package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 切换学生-渠道绑定请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class SwitchBindingReqDTO {

    /** 目标渠道方ID。 */
    @NotNull(message = "channelId 不能为空")
    private Long channelId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
