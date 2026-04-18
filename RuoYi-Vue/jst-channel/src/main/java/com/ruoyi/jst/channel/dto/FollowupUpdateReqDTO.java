package com.ruoyi.jst.channel.dto;

import java.util.Date;

/**
 * 更新跟进记录请求 DTO（24h 内可改，无 channelId 字段）。
 */
public class FollowupUpdateReqDTO {

    private String followupType;
    private Date followupAt;
    private String content;
    private String mood;
    private Date nextFollowupAt;
    private String attachmentUrls;

    public String getFollowupType() { return followupType; }
    public void setFollowupType(String followupType) { this.followupType = followupType; }
    public Date getFollowupAt() { return followupAt; }
    public void setFollowupAt(Date followupAt) { this.followupAt = followupAt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public Date getNextFollowupAt() { return nextFollowupAt; }
    public void setNextFollowupAt(Date nextFollowupAt) { this.nextFollowupAt = nextFollowupAt; }
    public String getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(String attachmentUrls) { this.attachmentUrls = attachmentUrls; }
}
