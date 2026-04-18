package com.ruoyi.jst.channel.dto;

import java.util.Date;

/**
 * 跟进记录查询过滤参数。
 */
public class FollowupQueryReqDTO {

    private Long channelId;
    private String followupType;
    private Date dateFrom;
    private Date dateTo;

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getFollowupType() { return followupType; }
    public void setFollowupType(String followupType) { this.followupType = followupType; }
    public Date getDateFrom() { return dateFrom; }
    public void setDateFrom(Date dateFrom) { this.dateFrom = dateFrom; }
    public Date getDateTo() { return dateTo; }
    public void setDateTo(Date dateTo) { this.dateTo = dateTo; }
}
