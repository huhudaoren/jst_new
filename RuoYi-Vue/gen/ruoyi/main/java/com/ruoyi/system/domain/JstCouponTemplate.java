package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 优惠券模板对象 jst_coupon_template
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstCouponTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long couponTemplateId;

    /** 券名 */
    @Excel(name = "券名")
    private String couponName;

    /** 类型：full_reduce满减/direct_reduce直减/discount折扣/contest_specific赛事专享 */
    @Excel(name = "类型：full_reduce满减/direct_reduce直减/discount折扣/contest_specific赛事专享")
    private String couponType;

    /** 面额 */
    @Excel(name = "面额")
    private BigDecimal faceValue;

    /** 折扣率 */
    @Excel(name = "折扣率")
    private BigDecimal discountRate;

    /** 门槛金额 */
    @Excel(name = "门槛金额")
    private BigDecimal thresholdAmount;

    /** 适用角色：student/channel/all */
    @Excel(name = "适用角色：student/channel/all")
    private String applicableRole;

    /** 适用赛事ID列表（逗号分隔） */
    @Excel(name = "适用赛事ID列表", readConverterExp = "逗=号分隔")
    private String applicableContestIds;

    /** 相对有效天数 */
    @Excel(name = "相对有效天数")
    private Long validDays;

    /** 绝对有效开始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "绝对有效开始", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validStart;

    /** 绝对有效结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "绝对有效结束", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validEnd;

    /** 叠加规则 */
    @Excel(name = "叠加规则")
    private String stackRule;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setCouponTemplateId(Long couponTemplateId) 
    {
        this.couponTemplateId = couponTemplateId;
    }

    public Long getCouponTemplateId() 
    {
        return couponTemplateId;
    }

    public void setCouponName(String couponName) 
    {
        this.couponName = couponName;
    }

    public String getCouponName() 
    {
        return couponName;
    }

    public void setCouponType(String couponType) 
    {
        this.couponType = couponType;
    }

    public String getCouponType() 
    {
        return couponType;
    }

    public void setFaceValue(BigDecimal faceValue) 
    {
        this.faceValue = faceValue;
    }

    public BigDecimal getFaceValue() 
    {
        return faceValue;
    }

    public void setDiscountRate(BigDecimal discountRate) 
    {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountRate() 
    {
        return discountRate;
    }

    public void setThresholdAmount(BigDecimal thresholdAmount) 
    {
        this.thresholdAmount = thresholdAmount;
    }

    public BigDecimal getThresholdAmount() 
    {
        return thresholdAmount;
    }

    public void setApplicableRole(String applicableRole) 
    {
        this.applicableRole = applicableRole;
    }

    public String getApplicableRole() 
    {
        return applicableRole;
    }

    public void setApplicableContestIds(String applicableContestIds) 
    {
        this.applicableContestIds = applicableContestIds;
    }

    public String getApplicableContestIds() 
    {
        return applicableContestIds;
    }

    public void setValidDays(Long validDays) 
    {
        this.validDays = validDays;
    }

    public Long getValidDays() 
    {
        return validDays;
    }

    public void setValidStart(Date validStart) 
    {
        this.validStart = validStart;
    }

    public Date getValidStart() 
    {
        return validStart;
    }

    public void setValidEnd(Date validEnd) 
    {
        this.validEnd = validEnd;
    }

    public Date getValidEnd() 
    {
        return validEnd;
    }

    public void setStackRule(String stackRule) 
    {
        this.stackRule = stackRule;
    }

    public String getStackRule() 
    {
        return stackRule;
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
            .append("couponTemplateId", getCouponTemplateId())
            .append("couponName", getCouponName())
            .append("couponType", getCouponType())
            .append("faceValue", getFaceValue())
            .append("discountRate", getDiscountRate())
            .append("thresholdAmount", getThresholdAmount())
            .append("applicableRole", getApplicableRole())
            .append("applicableContestIds", getApplicableContestIds())
            .append("validDays", getValidDays())
            .append("validStart", getValidStart())
            .append("validEnd", getValidEnd())
            .append("stackRule", getStackRule())
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
