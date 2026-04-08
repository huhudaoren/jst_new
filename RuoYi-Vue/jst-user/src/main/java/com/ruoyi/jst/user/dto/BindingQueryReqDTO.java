package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.Pattern;

/**
 * 管理后台绑定列表查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class BindingQueryReqDTO {

    /** 学生用户ID。 */
    private Long userId;

    /** 渠道方ID。 */
    private Long channelId;

    /** 绑定状态。 */
    @Pattern(regexp = "active|expired|replaced|manual_unbound", message = "status 仅支持 active/expired/replaced/manual_unbound")
    private String status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
