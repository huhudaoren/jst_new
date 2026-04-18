package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class JstSalesPreRegister extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long preId;
    private Long salesId;
    private String phone;
    private String targetName;
    private String status;
    private Long matchedChannelId;
    private String matchedChannelName;
    private Date matchedAt;
    private Date expireAt;
    private Integer renewCount;

    public Long getPreId() { return preId; }
    public void setPreId(Long preId) { this.preId = preId; }
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getMatchedChannelId() { return matchedChannelId; }
    public void setMatchedChannelId(Long matchedChannelId) { this.matchedChannelId = matchedChannelId; }
    public String getMatchedChannelName() { return matchedChannelName; }
    public void setMatchedChannelName(String matchedChannelName) { this.matchedChannelName = matchedChannelName; }
    public Date getMatchedAt() { return matchedAt; }
    public void setMatchedAt(Date matchedAt) { this.matchedAt = matchedAt; }
    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }
    public Integer getRenewCount() { return renewCount; }
    public void setRenewCount(Integer renewCount) { this.renewCount = renewCount; }
}
