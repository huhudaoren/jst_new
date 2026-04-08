package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 支付记录对象 jst_payment_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstPaymentRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支付ID */
    private Long paymentId;

    /** 内部支付流水号 */
    @Excel(name = "内部支付流水号")
    private String paymentNo;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 支付方式：wechat/bank_transfer/points/mix */
    @Excel(name = "支付方式：wechat/bank_transfer/points/mix")
    private String payMethod;

    /** 现金金额 */
    @Excel(name = "现金金额")
    private BigDecimal cashAmount;

    /** 积分折现金额 */
    @Excel(name = "积分折现金额")
    private BigDecimal pointsAmount;

    /** 消耗积分数 */
    @Excel(name = "消耗积分数")
    private Long pointsUsed;

    /** 第三方流水号（微信/银行） */
    @Excel(name = "第三方流水号", readConverterExp = "微=信/银行")
    private String thirdPartyNo;

    /** 对公转账凭证URL */
    @Excel(name = "对公转账凭证URL")
    private String voucherUrl;

    /** 凭证审核状态：pending/approved/rejected */
    @Excel(name = "凭证审核状态：pending/approved/rejected")
    private String voucherAuditStatus;

    /** 支付状态：pending/success/failed/refunding/refunded */
    @Excel(name = "支付状态：pending/success/failed/refunding/refunded")
    private String payStatus;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 操作人ID（对公转账录入员） */
    @Excel(name = "操作人ID", readConverterExp = "对=公转账录入员")
    private Long operatorId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setPaymentId(Long paymentId) 
    {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() 
    {
        return paymentId;
    }

    public void setPaymentNo(String paymentNo) 
    {
        this.paymentNo = paymentNo;
    }

    public String getPaymentNo() 
    {
        return paymentNo;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setPayMethod(String payMethod) 
    {
        this.payMethod = payMethod;
    }

    public String getPayMethod() 
    {
        return payMethod;
    }

    public void setCashAmount(BigDecimal cashAmount) 
    {
        this.cashAmount = cashAmount;
    }

    public BigDecimal getCashAmount() 
    {
        return cashAmount;
    }

    public void setPointsAmount(BigDecimal pointsAmount) 
    {
        this.pointsAmount = pointsAmount;
    }

    public BigDecimal getPointsAmount() 
    {
        return pointsAmount;
    }

    public void setPointsUsed(Long pointsUsed) 
    {
        this.pointsUsed = pointsUsed;
    }

    public Long getPointsUsed() 
    {
        return pointsUsed;
    }

    public void setThirdPartyNo(String thirdPartyNo) 
    {
        this.thirdPartyNo = thirdPartyNo;
    }

    public String getThirdPartyNo() 
    {
        return thirdPartyNo;
    }

    public void setVoucherUrl(String voucherUrl) 
    {
        this.voucherUrl = voucherUrl;
    }

    public String getVoucherUrl() 
    {
        return voucherUrl;
    }

    public void setVoucherAuditStatus(String voucherAuditStatus) 
    {
        this.voucherAuditStatus = voucherAuditStatus;
    }

    public String getVoucherAuditStatus() 
    {
        return voucherAuditStatus;
    }

    public void setPayStatus(String payStatus) 
    {
        this.payStatus = payStatus;
    }

    public String getPayStatus() 
    {
        return payStatus;
    }

    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
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
            .append("paymentId", getPaymentId())
            .append("paymentNo", getPaymentNo())
            .append("orderId", getOrderId())
            .append("payMethod", getPayMethod())
            .append("cashAmount", getCashAmount())
            .append("pointsAmount", getPointsAmount())
            .append("pointsUsed", getPointsUsed())
            .append("thirdPartyNo", getThirdPartyNo())
            .append("voucherUrl", getVoucherUrl())
            .append("voucherAuditStatus", getVoucherAuditStatus())
            .append("payStatus", getPayStatus())
            .append("payTime", getPayTime())
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
