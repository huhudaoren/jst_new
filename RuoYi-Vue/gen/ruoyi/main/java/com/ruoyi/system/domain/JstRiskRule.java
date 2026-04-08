package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 风控规则对象 jst_risk_rule
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRiskRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long riskRuleId;

    /** 规则名 */
    @Excel(name = "规则名")
    private String ruleName;

    /** 类型：bind_freq/coupon_freq/refund_freq/rebate_anomaly/zero_order_freq/aftersale_anomaly */
    @Excel(name = "类型：bind_freq/coupon_freq/refund_freq/rebate_anomaly/zero_order_freq/aftersale_anomaly")
    private String ruleType;

    /** 维度：user/device/mobile/channel */
    @Excel(name = "维度：user/device/mobile/channel")
    private String dimension;

    /** 阈值配置JSON */
    @Excel(name = "阈值配置JSON")
    private String thresholdJson;

    /** 命中动作：warn告警/intercept拦截/manual人工 */
    @Excel(name = "命中动作：warn告警/intercept拦截/manual人工")
    private String action;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setRiskRuleId(Long riskRuleId) 
    {
        this.riskRuleId = riskRuleId;
    }

    public Long getRiskRuleId() 
    {
        return riskRuleId;
    }

    public void setRuleName(String ruleName) 
    {
        this.ruleName = ruleName;
    }

    public String getRuleName() 
    {
        return ruleName;
    }

    public void setRuleType(String ruleType) 
    {
        this.ruleType = ruleType;
    }

    public String getRuleType() 
    {
        return ruleType;
    }

    public void setDimension(String dimension) 
    {
        this.dimension = dimension;
    }

    public String getDimension() 
    {
        return dimension;
    }

    public void setThresholdJson(String thresholdJson) 
    {
        this.thresholdJson = thresholdJson;
    }

    public String getThresholdJson() 
    {
        return thresholdJson;
    }

    public void setAction(String action) 
    {
        this.action = action;
    }

    public String getAction() 
    {
        return action;
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
            .append("riskRuleId", getRiskRuleId())
            .append("ruleName", getRuleName())
            .append("ruleType", getRuleType())
            .append("dimension", getDimension())
            .append("thresholdJson", getThresholdJson())
            .append("action", getAction())
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
