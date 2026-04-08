package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 渠道返点规则对象 jst_rebate_rule
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRebateRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 赛事ID */
    @Excel(name = "赛事ID")
    private Long contestId;

    /** 渠道方ID（NULL=按赛事默认规则） */
    @Excel(name = "渠道方ID", readConverterExp = "N=ULL=按赛事默认规则")
    private Long channelId;

    /** 返点模式：rate按比例 / fixed固定金额 */
    @Excel(name = "返点模式：rate按比例 / fixed固定金额")
    private String rebateMode;

    /** 返点值（rate=比例如0.1, fixed=金额） */
    @Excel(name = "返点值", readConverterExp = "r=ate=比例如0.1,,f=ixed=金额")
    private BigDecimal rebateValue;

    /** 服务费模式：fixed/rate/none */
    @Excel(name = "服务费模式：fixed/rate/none")
    private String serviceFeeMode;

    /** 服务费值 */
    @Excel(name = "服务费值")
    private BigDecimal serviceFeeValue;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireTime;

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

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setRebateMode(String rebateMode) 
    {
        this.rebateMode = rebateMode;
    }

    public String getRebateMode() 
    {
        return rebateMode;
    }

    public void setRebateValue(BigDecimal rebateValue) 
    {
        this.rebateValue = rebateValue;
    }

    public BigDecimal getRebateValue() 
    {
        return rebateValue;
    }

    public void setServiceFeeMode(String serviceFeeMode) 
    {
        this.serviceFeeMode = serviceFeeMode;
    }

    public String getServiceFeeMode() 
    {
        return serviceFeeMode;
    }

    public void setServiceFeeValue(BigDecimal serviceFeeValue) 
    {
        this.serviceFeeValue = serviceFeeValue;
    }

    public BigDecimal getServiceFeeValue() 
    {
        return serviceFeeValue;
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
            .append("contestId", getContestId())
            .append("channelId", getChannelId())
            .append("rebateMode", getRebateMode())
            .append("rebateValue", getRebateValue())
            .append("serviceFeeMode", getServiceFeeMode())
            .append("serviceFeeValue", getServiceFeeValue())
            .append("effectiveTime", getEffectiveTime())
            .append("expireTime", getExpireTime())
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
