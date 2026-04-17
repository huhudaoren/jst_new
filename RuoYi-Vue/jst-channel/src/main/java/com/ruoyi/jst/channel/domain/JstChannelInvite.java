package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class JstChannelInvite extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long inviteId;
    private Long inviterChannelId;
    private Long inviteeChannelId;
    private String inviteCode;
    private String shareScene;
    private BigDecimal commissionRate;
    private Date invitedAt;
    private String status;

    public Long getInviteId() { return inviteId; }
    public void setInviteId(Long inviteId) { this.inviteId = inviteId; }
    public Long getInviterChannelId() { return inviterChannelId; }
    public void setInviterChannelId(Long inviterChannelId) { this.inviterChannelId = inviterChannelId; }
    public Long getInviteeChannelId() { return inviteeChannelId; }
    public void setInviteeChannelId(Long inviteeChannelId) { this.inviteeChannelId = inviteeChannelId; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public String getShareScene() { return shareScene; }
    public void setShareScene(String shareScene) { this.shareScene = shareScene; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public Date getInvitedAt() { return invitedAt; }
    public void setInvitedAt(Date invitedAt) { this.invitedAt = invitedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
