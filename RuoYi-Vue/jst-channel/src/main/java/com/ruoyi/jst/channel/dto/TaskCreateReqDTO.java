package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * 主管派任务请求 DTO。
 */
public class TaskCreateReqDTO {

    @NotNull(message = "被分配销售 ID 不能为空")
    private Long assigneeSalesId;

    @NotNull(message = "渠道 ID 不能为空")
    private Long channelId;

    @NotNull(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "截止日期不能为空")
    private Date dueDate;

    public Long getAssigneeSalesId() { return assigneeSalesId; }
    public void setAssigneeSalesId(Long assigneeSalesId) { this.assigneeSalesId = assigneeSalesId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
}
