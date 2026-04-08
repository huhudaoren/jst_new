package com.ruoyi.jst.risk.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 风险工单对象 jst_risk_case
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRiskCase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工单ID */
    private Long caseId;

    /** 工单号 */
    @Excel(name = "工单号")
    private String caseNo;

    /** 关联预警 */
    @Excel(name = "关联预警")
    private Long alertId;

    /** 处理人 */
    @Excel(name = "处理人")
    private Long assigneeId;

    /** 状态：open/assigned/processing/reviewing/closed */
    @Excel(name = "状态：open/assigned/processing/reviewing/closed")
    private String status;

    /** 处理结论 */
    @Excel(name = "处理结论")
    private String conclusion;

    /** 复核人 */
    @Excel(name = "复核人")
    private Long reviewerId;

    /** 关闭时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "关闭时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date closeTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setCaseId(Long caseId) 
    {
        this.caseId = caseId;
    }

    public Long getCaseId() 
    {
        return caseId;
    }

    public void setCaseNo(String caseNo) 
    {
        this.caseNo = caseNo;
    }

    public String getCaseNo() 
    {
        return caseNo;
    }

    public void setAlertId(Long alertId) 
    {
        this.alertId = alertId;
    }

    public Long getAlertId() 
    {
        return alertId;
    }

    public void setAssigneeId(Long assigneeId) 
    {
        this.assigneeId = assigneeId;
    }

    public Long getAssigneeId() 
    {
        return assigneeId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setConclusion(String conclusion) 
    {
        this.conclusion = conclusion;
    }

    public String getConclusion() 
    {
        return conclusion;
    }

    public void setReviewerId(Long reviewerId) 
    {
        this.reviewerId = reviewerId;
    }

    public Long getReviewerId() 
    {
        return reviewerId;
    }

    public void setCloseTime(Date closeTime) 
    {
        this.closeTime = closeTime;
    }

    public Date getCloseTime() 
    {
        return closeTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("caseId", getCaseId())
            .append("caseNo", getCaseNo())
            .append("alertId", getAlertId())
            .append("assigneeId", getAssigneeId())
            .append("status", getStatus())
            .append("conclusion", getConclusion())
            .append("reviewerId", getReviewerId())
            .append("closeTime", getCloseTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
