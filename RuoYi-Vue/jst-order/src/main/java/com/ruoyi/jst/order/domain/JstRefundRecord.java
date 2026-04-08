package com.ruoyi.jst.order.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 退款/售后单对象 jst_refund_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRefundRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 售后ID */
    private Long refundId;

    /** 售后单号 */
    @Excel(name = "售后单号")
    private String refundNo;

    /** 关联订单 */
    @Excel(name = "关联订单")
    private Long orderId;

    /** 关联明细（部分退款时必填） */
    @Excel(name = "关联明细", readConverterExp = "部=分退款时必填")
    private Long itemId;

    /** 类型：refund_only仅退款/return_refund退货退款/special_refund特批 */
    @Excel(name = "类型：refund_only仅退款/return_refund退货退款/special_refund特批")
    private String refundType;

    /** 申请来源：user/admin/system */
    @Excel(name = "申请来源：user/admin/system")
    private String applySource;

    /** 申请原因 */
    @Excel(name = "申请原因")
    private String reason;

    /** 申请退现金 */
    @Excel(name = "申请退现金")
    private BigDecimal applyCash;

    /** 申请退积分 */
    @Excel(name = "申请退积分")
    private Long applyPoints;

    /** 实退现金 */
    @Excel(name = "实退现金")
    private BigDecimal actualCash;

    /** 实退积分 */
    @Excel(name = "实退积分")
    private Long actualPoints;

    /** 优惠券是否回退：0否 1是（仅整单全退且原券有效期内） */
    @Excel(name = "优惠券是否回退：0否 1是", readConverterExp = "仅=整单全退且原券有效期内")
    private Integer couponReturned;

    /** 状态：pending/approved/rejected/refunding/completed/closed */
    @Excel(name = "状态：pending/approved/rejected/refunding/completed/closed")
    private String status;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 操作人 */
    @Excel(name = "操作人")
    private Long operatorId;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date completeTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setRefundId(Long refundId) 
    {
        this.refundId = refundId;
    }

    public Long getRefundId() 
    {
        return refundId;
    }

    public void setRefundNo(String refundNo) 
    {
        this.refundNo = refundNo;
    }

    public String getRefundNo() 
    {
        return refundNo;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }

    public void setRefundType(String refundType) 
    {
        this.refundType = refundType;
    }

    public String getRefundType() 
    {
        return refundType;
    }

    public void setApplySource(String applySource) 
    {
        this.applySource = applySource;
    }

    public String getApplySource() 
    {
        return applySource;
    }

    public void setReason(String reason) 
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
    }

    public void setApplyCash(BigDecimal applyCash) 
    {
        this.applyCash = applyCash;
    }

    public BigDecimal getApplyCash() 
    {
        return applyCash;
    }

    public void setApplyPoints(Long applyPoints) 
    {
        this.applyPoints = applyPoints;
    }

    public Long getApplyPoints() 
    {
        return applyPoints;
    }

    public void setActualCash(BigDecimal actualCash) 
    {
        this.actualCash = actualCash;
    }

    public BigDecimal getActualCash() 
    {
        return actualCash;
    }

    public void setActualPoints(Long actualPoints) 
    {
        this.actualPoints = actualPoints;
    }

    public Long getActualPoints() 
    {
        return actualPoints;
    }

    public void setCouponReturned(Integer couponReturned) 
    {
        this.couponReturned = couponReturned;
    }

    public Integer getCouponReturned() 
    {
        return couponReturned;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setOperatorId(Long operatorId) 
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() 
    {
        return operatorId;
    }

    public void setCompleteTime(Date completeTime) 
    {
        this.completeTime = completeTime;
    }

    public Date getCompleteTime() 
    {
        return completeTime;
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
            .append("refundId", getRefundId())
            .append("refundNo", getRefundNo())
            .append("orderId", getOrderId())
            .append("itemId", getItemId())
            .append("refundType", getRefundType())
            .append("applySource", getApplySource())
            .append("reason", getReason())
            .append("applyCash", getApplyCash())
            .append("applyPoints", getApplyPoints())
            .append("actualCash", getActualCash())
            .append("actualPoints", getActualPoints())
            .append("couponReturned", getCouponReturned())
            .append("status", getStatus())
            .append("auditRemark", getAuditRemark())
            .append("operatorId", getOperatorId())
            .append("completeTime", getCompleteTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
