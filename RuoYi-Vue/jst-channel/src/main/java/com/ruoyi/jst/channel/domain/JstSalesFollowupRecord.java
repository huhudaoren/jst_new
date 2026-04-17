package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class JstSalesFollowupRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Long salesId;
    private Long channelId;
    private String followupType;
    private Date followupAt;
    private String content;
    private String attachmentUrls;
    private String mood;
    private Date nextFollowupAt;
    private Date canEditUntil;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getFollowupType() { return followupType; }
    public void setFollowupType(String followupType) { this.followupType = followupType; }
    public Date getFollowupAt() { return followupAt; }
    public void setFollowupAt(Date followupAt) { this.followupAt = followupAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(String attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public Date getNextFollowupAt() { return nextFollowupAt; }
    public void setNextFollowupAt(Date nextFollowupAt) { this.nextFollowupAt = nextFollowupAt; }
    public Date getCanEditUntil() { return canEditUntil; }
    public void setCanEditUntil(Date canEditUntil) { this.canEditUntil = canEditUntil; }
}
