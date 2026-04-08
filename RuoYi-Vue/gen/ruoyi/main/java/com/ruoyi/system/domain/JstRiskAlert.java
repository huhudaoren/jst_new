package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 风险预警对象 jst_risk_alert
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRiskAlert extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预警ID */
    private Long alertId;

    /** 命中规则ID */
    @Excel(name = "命中规则ID")
    private Long riskRuleId;

    /** 风险等级：low/mid/high */
    @Excel(name = "风险等级：low/mid/high")
    private String riskLevel;

    /** 对象类型：user/channel/order */
    @Excel(name = "对象类型：user/channel/order")
    private String targetType;

    /** 对象ID */
    @Excel(name = "对象ID")
    private Long targetId;

    /** 命中详情JSON */
    @Excel(name = "命中详情JSON")
    private String hitDetailJson;

    /** 处理状态：open/processing/closed/false_alarm */
    @Excel(name = "处理状态：open/processing/closed/false_alarm")
    private String status;

    /** 关联工单ID */
    @Excel(name = "关联工单ID")
    private Long caseId;

    /** 预警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预警时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date alertTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setAlertId(Long alertId) 
    {
        this.alertId = alertId;
    }

    public Long getAlertId() 
    {
        return alertId;
    }

    public void setRiskRuleId(Long riskRuleId) 
    {
        this.riskRuleId = riskRuleId;
    }

    public Long getRiskRuleId() 
    {
        return riskRuleId;
    }

    public void setRiskLevel(String riskLevel) 
    {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() 
    {
        return riskLevel;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetId(Long targetId) 
    {
        this.targetId = targetId;
    }

    public Long getTargetId() 
    {
        return targetId;
    }

    public void setHitDetailJson(String hitDetailJson) 
    {
        this.hitDetailJson = hitDetailJson;
    }

    public String getHitDetailJson() 
    {
        return hitDetailJson;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setCaseId(Long caseId) 
    {
        this.caseId = caseId;
    }

    public Long getCaseId() 
    {
        return caseId;
    }

    public void setAlertTime(Date alertTime) 
    {
        this.alertTime = alertTime;
    }

    public Date getAlertTime() 
    {
        return alertTime;
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
            .append("alertId", getAlertId())
            .append("riskRuleId", getRiskRuleId())
            .append("riskLevel", getRiskLevel())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("hitDetailJson", getHitDetailJson())
            .append("status", getStatus())
            .append("caseId", getCaseId())
            .append("alertTime", getAlertTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
