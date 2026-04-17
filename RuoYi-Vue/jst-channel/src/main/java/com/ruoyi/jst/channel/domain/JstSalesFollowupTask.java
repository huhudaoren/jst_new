package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class JstSalesFollowupTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long assigneeSalesId;
    private Long assignerId;
    private Long channelId;
    private String title;
    private String description;
    private Date dueDate;
    private String status;
    private Date completedAt;
    private Long linkedRecordId;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getAssigneeSalesId() { return assigneeSalesId; }
    public void setAssigneeSalesId(Long assigneeSalesId) { this.assigneeSalesId = assigneeSalesId; }
    public Long getAssignerId() { return assignerId; }
    public void setAssignerId(Long assignerId) { this.assignerId = assignerId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
    public Long getLinkedRecordId() { return linkedRecordId; }
    public void setLinkedRecordId(Long linkedRecordId) { this.linkedRecordId = linkedRecordId; }
}
