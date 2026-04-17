package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class JstSalesAttributionConflict extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long conflictId;
    private Long channelId;
    private String candidateSalesIds;
    private String reason;
    private String status;
    private Long resolvedBy;
    private Date resolvedAt;
    private String resolutionRemark;

    public Long getConflictId() { return conflictId; }
    public void setConflictId(Long conflictId) { this.conflictId = conflictId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getCandidateSalesIds() { return candidateSalesIds; }
    public void setCandidateSalesIds(String candidateSalesIds) { this.candidateSalesIds = candidateSalesIds; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(Long resolvedBy) { this.resolvedBy = resolvedBy; }
    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getResolutionRemark() { return resolutionRemark; }
    public void setResolutionRemark(String resolutionRemark) { this.resolutionRemark = resolutionRemark; }
}
