package com.ruoyi.jst.channel.dto;

/**
 * 渠道基础信息（plan-04 invite/distribution 内部用）。
 * <p>
 * jst-channel 模块无法直接依赖 jst-user 的 JstChannel domain，
 * 因此通过 lookup mapper 查询后映射到此轻量 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelBasicDTO {

    private Long channelId;
    private Integer status;    // 1=启用
    private Integer isOfficial; // 1=官方渠道
    private String inviteCode;
    private Long parentInviteId;

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getIsOfficial() { return isOfficial; }
    public void setIsOfficial(Integer isOfficial) { this.isOfficial = isOfficial; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public Long getParentInviteId() { return parentInviteId; }
    public void setParentInviteId(Long parentInviteId) { this.parentInviteId = parentInviteId; }
}
