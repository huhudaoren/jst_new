package com.ruoyi.jst.marketing.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户持有优惠券对象 jst_user_coupon
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstUserCoupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户券ID */
    private Long userCouponId;

    /** 模板ID */
    @Excel(name = "模板ID")
    private Long couponTemplateId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 发券批次号 */
    @Excel(name = "发券批次号")
    private String issueBatchNo;

    /** 发券来源：platform/campaign/manual */
    @Excel(name = "发券来源：platform/campaign/manual")
    private String issueSource;

    /** 状态：unused/locked/used/expired/refunded */
    @Excel(name = "状态：unused/locked/used/expired/refunded")
    private String status;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validStart;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validEnd;

    /** 使用订单ID */
    @Excel(name = "使用订单ID")
    private Long orderId;

    /** 使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "使用时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date useTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 用户昵称（JOIN查出，非持久化） */
    private String userName;

    /** 优惠券模板名称（JOIN查出，非持久化） */
    private String couponTemplateName;

    /** 订单编号（JOIN查出，非持久化） */
    private String orderNo;

    public void setUserCouponId(Long userCouponId) 
    {
        this.userCouponId = userCouponId;
    }

    public Long getUserCouponId() 
    {
        return userCouponId;
    }

    public void setCouponTemplateId(Long couponTemplateId) 
    {
        this.couponTemplateId = couponTemplateId;
    }

    public Long getCouponTemplateId() 
    {
        return couponTemplateId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setIssueBatchNo(String issueBatchNo) 
    {
        this.issueBatchNo = issueBatchNo;
    }

    public String getIssueBatchNo() 
    {
        return issueBatchNo;
    }

    public void setIssueSource(String issueSource) 
    {
        this.issueSource = issueSource;
    }

    public String getIssueSource() 
    {
        return issueSource;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setUseTime(Date useTime) 
    {
        this.useTime = useTime;
    }

    public Date getUseTime() 
    {
        return useTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setCouponTemplateName(String couponTemplateName)
    {
        this.couponTemplateName = couponTemplateName;
    }

    public String getCouponTemplateName()
    {
        return couponTemplateName;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userCouponId", getUserCouponId())
            .append("couponTemplateId", getCouponTemplateId())
            .append("userId", getUserId())
            .append("issueBatchNo", getIssueBatchNo())
            .append("issueSource", getIssueSource())
            .append("status", getStatus())
            .append("validStart", getValidStart())
            .append("validEnd", getValidEnd())
            .append("orderId", getOrderId())
            .append("useTime", getUseTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
