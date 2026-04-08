package com.ruoyi.jst.points.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分/成长值规则对象 jst_points_rule
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstPointsRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 类型：points积分 / growth成长值 */
    @Excel(name = "类型：points积分 / growth成长值")
    private String ruleType;

    /** 来源行为：enroll/course/sign/invite/learn/award */
    @Excel(name = "来源行为：enroll/course/sign/invite/learn/award")
    private String sourceType;

    /** 模式：fixed/rate */
    @Excel(name = "模式：fixed/rate")
    private String rewardMode;

    /** 数值 */
    @Excel(name = "数值")
    private BigDecimal rewardValue;

    /** 适用角色：student/channel/both */
    @Excel(name = "适用角色：student/channel/both")
    private String applicableRole;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireTime;

    /** 互斥组（同组规则同时只命中一个） */
    @Excel(name = "互斥组", readConverterExp = "同=组规则同时只命中一个")
    private String mutexGroup;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setRuleId(Long ruleId) 
    {
        this.ruleId = ruleId;
    }

    public Long getRuleId() 
    {
        return ruleId;
    }

    public void setRuleType(String ruleType) 
    {
        this.ruleType = ruleType;
    }

    public String getRuleType() 
    {
        return ruleType;
    }

    public void setSourceType(String sourceType) 
    {
        this.sourceType = sourceType;
    }

    public String getSourceType() 
    {
        return sourceType;
    }

    public void setRewardMode(String rewardMode) 
    {
        this.rewardMode = rewardMode;
    }

    public String getRewardMode() 
    {
        return rewardMode;
    }

    public void setRewardValue(BigDecimal rewardValue) 
    {
        this.rewardValue = rewardValue;
    }

    public BigDecimal getRewardValue() 
    {
        return rewardValue;
    }

    public void setApplicableRole(String applicableRole) 
    {
        this.applicableRole = applicableRole;
    }

    public String getApplicableRole() 
    {
        return applicableRole;
    }

    public void setEffectiveTime(Date effectiveTime) 
    {
        this.effectiveTime = effectiveTime;
    }

    public Date getEffectiveTime() 
    {
        return effectiveTime;
    }

    public void setExpireTime(Date expireTime) 
    {
        this.expireTime = expireTime;
    }

    public Date getExpireTime() 
    {
        return expireTime;
    }

    public void setMutexGroup(String mutexGroup) 
    {
        this.mutexGroup = mutexGroup;
    }

    public String getMutexGroup() 
    {
        return mutexGroup;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
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
            .append("ruleId", getRuleId())
            .append("ruleType", getRuleType())
            .append("sourceType", getSourceType())
            .append("rewardMode", getRewardMode())
            .append("rewardValue", getRewardValue())
            .append("applicableRole", getApplicableRole())
            .append("effectiveTime", getEffectiveTime())
            .append("expireTime", getExpireTime())
            .append("mutexGroup", getMutexGroup())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
