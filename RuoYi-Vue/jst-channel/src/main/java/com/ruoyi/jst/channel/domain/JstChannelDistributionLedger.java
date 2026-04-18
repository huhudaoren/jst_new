package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class JstChannelDistributionLedger extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long ledgerId;
    private Long inviterChannelId;
    private Long inviteeChannelId;
    private Long orderId;
    private String orderNo;
    private BigDecimal baseAmount;
    private BigDecimal appliedRate;
    private BigDecimal rawAmount;
    private BigDecimal compressRatio;
    private BigDecimal amount;
    private String status;
    private Date accrueAt;
    private Date accruedAt;

    // ==== 冗余字段（LEFT JOIN jst_channel 带出，非持久化）====
    /** 下级渠道名称（LEFT JOIN jst_channel.channel_name） */
    private String inviteeChannelName;
    /** 下级渠道认证状态 */
    private String authType;
    /** 下级渠道头像 URL（预留） */
    private String avatarUrl;

    public Long getLedgerId() { return ledgerId; }
    public void setLedgerId(Long ledgerId) { this.ledgerId = ledgerId; }
    public Long getInviterChannelId() { return inviterChannelId; }
    public void setInviterChannelId(Long inviterChannelId) { this.inviterChannelId = inviterChannelId; }
    public Long getInviteeChannelId() { return inviteeChannelId; }
    public void setInviteeChannelId(Long inviteeChannelId) { this.inviteeChannelId = inviteeChannelId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public BigDecimal getAppliedRate() { return appliedRate; }
    public void setAppliedRate(BigDecimal appliedRate) { this.appliedRate = appliedRate; }
    public BigDecimal getRawAmount() { return rawAmount; }
    public void setRawAmount(BigDecimal rawAmount) { this.rawAmount = rawAmount; }
    public BigDecimal getCompressRatio() { return compressRatio; }
    public void setCompressRatio(BigDecimal compressRatio) { this.compressRatio = compressRatio; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getAccrueAt() { return accrueAt; }
    public void setAccrueAt(Date accrueAt) { this.accrueAt = accrueAt; }
    public Date getAccruedAt() { return accruedAt; }
    public void setAccruedAt(Date accruedAt) { this.accruedAt = accruedAt; }

    public String getInviteeChannelName() { return inviteeChannelName; }
    public void setInviteeChannelName(String inviteeChannelName) { this.inviteeChannelName = inviteeChannelName; }
    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
