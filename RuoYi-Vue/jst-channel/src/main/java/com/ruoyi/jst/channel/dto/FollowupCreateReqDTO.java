package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 新建跟进记录请求 DTO。
 */
public class FollowupCreateReqDTO {

    @NotNull(message = "渠道 ID 不能为空")
    private Long channelId;

    /** 跟进方式：call / visit / wechat / email 等 */
    private String followupType;

    /** 跟进时间，不传则取当前时间 */
    private Date followupAt;

    private String content;

    /** 跟进心情/评级 */
    private String mood;

    /** 下次跟进时间（用于提醒） */
    private Date nextFollowupAt;

    /** 附件 URL 列表（JSON 字符串） */
    private String attachmentUrls;

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
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
