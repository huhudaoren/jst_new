package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分流水对象 jst_points_ledger
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstPointsLedger extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流水ID */
    private Long ledgerId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 持有者类型：student/channel */
    @Excel(name = "持有者类型：student/channel")
    private String ownerType;

    /** 持有者ID */
    @Excel(name = "持有者ID")
    private Long ownerId;

    /** 变更类型：earn/spend/freeze/unfreeze/adjust/rollback */
    @Excel(name = "变更类型：earn/spend/freeze/unfreeze/adjust/rollback")
    private String changeType;

    /** 来源：enroll/course/sign/invite/learn/award/exchange/manual/refund */
    @Excel(name = "来源：enroll/course/sign/invite/learn/award/exchange/manual/refund")
    private String sourceType;

    /** 来源单据ID */
    @Excel(name = "来源单据ID")
    private Long sourceRefId;

    /** 积分变化（正负） */
    @Excel(name = "积分变化", readConverterExp = "正=负")
    private Long pointsChange;

    /** 变更后余额 */
    @Excel(name = "变更后余额")
    private Long balanceAfter;

    /** 操作人 */
    @Excel(name = "操作人")
    private Long operatorId;

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

    public void setOwnerType(String ownerType) 
    {
        this.ownerType = ownerType;
    }

    public String getOwnerType() 
    {
        return ownerType;
    }

    public void setOwnerId(Long ownerId) 
    {
        this.ownerId = ownerId;
    }

    public Long getOwnerId() 
    {
        return ownerId;
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

    public void setPointsChange(Long pointsChange) 
    {
        this.pointsChange = pointsChange;
    }

    public Long getPointsChange() 
    {
        return pointsChange;
    }

    public void setBalanceAfter(Long balanceAfter) 
    {
        this.balanceAfter = balanceAfter;
    }

    public Long getBalanceAfter() 
    {
        return balanceAfter;
    }

    public void setOperatorId(Long operatorId) 
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() 
    {
        return operatorId;
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
            .append("ownerType", getOwnerType())
            .append("ownerId", getOwnerId())
            .append("changeType", getChangeType())
            .append("sourceType", getSourceType())
            .append("sourceRefId", getSourceRefId())
            .append("pointsChange", getPointsChange())
            .append("balanceAfter", getBalanceAfter())
            .append("operatorId", getOperatorId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
