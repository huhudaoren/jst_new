package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分账户对象 jst_points_account
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstPointsAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账户ID */
    private Long accountId;

    /** 持有者类型：student/channel */
    @Excel(name = "持有者类型：student/channel")
    private String ownerType;

    /** 持有者业务ID */
    @Excel(name = "持有者业务ID")
    private Long ownerId;

    /** 可用积分 */
    @Excel(name = "可用积分")
    private Long availablePoints;

    /** 冻结积分（兑换中/退款中） */
    @Excel(name = "冻结积分", readConverterExp = "兑=换中/退款中")
    private Long frozenPoints;

    /** 累计获取 */
    @Excel(name = "累计获取")
    private Long totalEarn;

    /** 累计消耗 */
    @Excel(name = "累计消耗")
    private Long totalSpend;

    /** 成长值（升级用） */
    @Excel(name = "成长值", readConverterExp = "升=级用")
    private Long growthValue;

    /** 当前等级ID */
    @Excel(name = "当前等级ID")
    private Long currentLevelId;

    /** 乐观锁版本号 */
    @Excel(name = "乐观锁版本号")
    private Long version;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

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

    public void setAvailablePoints(Long availablePoints) 
    {
        this.availablePoints = availablePoints;
    }

    public Long getAvailablePoints() 
    {
        return availablePoints;
    }

    public void setFrozenPoints(Long frozenPoints) 
    {
        this.frozenPoints = frozenPoints;
    }

    public Long getFrozenPoints() 
    {
        return frozenPoints;
    }

    public void setTotalEarn(Long totalEarn) 
    {
        this.totalEarn = totalEarn;
    }

    public Long getTotalEarn() 
    {
        return totalEarn;
    }

    public void setTotalSpend(Long totalSpend) 
    {
        this.totalSpend = totalSpend;
    }

    public Long getTotalSpend() 
    {
        return totalSpend;
    }

    public void setGrowthValue(Long growthValue) 
    {
        this.growthValue = growthValue;
    }

    public Long getGrowthValue() 
    {
        return growthValue;
    }

    public void setCurrentLevelId(Long currentLevelId) 
    {
        this.currentLevelId = currentLevelId;
    }

    public Long getCurrentLevelId() 
    {
        return currentLevelId;
    }

    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
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
            .append("accountId", getAccountId())
            .append("ownerType", getOwnerType())
            .append("ownerId", getOwnerId())
            .append("availablePoints", getAvailablePoints())
            .append("frozenPoints", getFrozenPoints())
            .append("totalEarn", getTotalEarn())
            .append("totalSpend", getTotalSpend())
            .append("growthValue", getGrowthValue())
            .append("currentLevelId", getCurrentLevelId())
            .append("version", getVersion())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
