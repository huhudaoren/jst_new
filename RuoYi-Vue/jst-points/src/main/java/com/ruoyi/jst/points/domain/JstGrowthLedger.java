package com.ruoyi.jst.points.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成长值流水对象 jst_growth_ledger
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstGrowthLedger extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流水ID */
    private Long ledgerId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 类型：earn/adjust/level_up */
    @Excel(name = "类型：earn/adjust/level_up")
    private String changeType;

    /** 来源：enroll/course/sign/invite/learn/award/manual */
    @Excel(name = "来源：enroll/course/sign/invite/learn/award/manual")
    private String sourceType;

    /** 来源ID */
    @Excel(name = "来源ID")
    private Long sourceRefId;

    /** 变化值 */
    @Excel(name = "变化值")
    private Long growthChange;

    /** 变更后余额 */
    @Excel(name = "变更后余额")
    private Long balanceAfter;

    /** 升级前等级 */
    @Excel(name = "升级前等级")
    private Long levelBefore;

    /** 升级后等级 */
    @Excel(name = "升级后等级")
    private Long levelAfter;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setLedgerId(Long ledgerId) 
    {
        this.ledgerId = ledgerId;
    }

    public Long getLedgerId() 
    {
        return ledgerId;
    }

    public void setAccountId(Long accountId) 
    {
        this.accountId = accountId;
    }

    public Long getAccountId() 
    {
        return accountId;
    }

    public void setChangeType(String changeType) 
    {
        this.changeType = changeType;
    }

    public String getChangeType() 
    {
        return changeType;
    }

    public void setSourceType(String sourceType) 
    {
        this.sourceType = sourceType;
    }

    public String getSourceType() 
    {
        return sourceType;
    }

    public void setSourceRefId(Long sourceRefId) 
    {
        this.sourceRefId = sourceRefId;
    }

    public Long getSourceRefId() 
    {
        return sourceRefId;
    }

    public void setGrowthChange(Long growthChange) 
    {
        this.growthChange = growthChange;
    }

    public Long getGrowthChange() 
    {
        return growthChange;
    }

    public void setBalanceAfter(Long balanceAfter) 
    {
        this.balanceAfter = balanceAfter;
    }

    public Long getBalanceAfter() 
    {
        return balanceAfter;
    }

    public void setLevelBefore(Long levelBefore) 
    {
        this.levelBefore = levelBefore;
    }

    public Long getLevelBefore() 
    {
        return levelBefore;
    }

    public void setLevelAfter(Long levelAfter) 
    {
        this.levelAfter = levelAfter;
    }

    public Long getLevelAfter() 
    {
        return levelAfter;
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
            .append("ledgerId", getLedgerId())
            .append("accountId", getAccountId())
            .append("changeType", getChangeType())
            .append("sourceType", getSourceType())
            .append("sourceRefId", getSourceRefId())
            .append("growthChange", getGrowthChange())
            .append("balanceAfter", getBalanceAfter())
            .append("levelBefore", getLevelBefore())
            .append("levelAfter", getLevelAfter())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
