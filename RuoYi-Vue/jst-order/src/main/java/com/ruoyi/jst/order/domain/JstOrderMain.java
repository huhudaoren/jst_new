package com.ruoyi.jst.order.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单主对象 jst_order_main
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstOrderMain extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单号（业务可读） */
    @Excel(name = "订单号", readConverterExp = "业=务可读")
    private String orderNo;

    /** 订单类型：normal/zero_price零元/full_deduct全额抵扣/exchange兑换/appointment预约 */
    @Excel(name = "订单类型：normal/zero_price零元/full_deduct全额抵扣/exchange兑换/appointment预约")
    private String orderType;

    /** 业务类型：enroll报名/course课程/mall商城/appointment预约 */
    @Excel(name = "业务类型：enroll报名/course课程/mall商城/appointment预约")
    private String businessType;

    /** 学生用户ID */
    @Excel(name = "学生用户ID")
    private Long userId;

    /** 参赛人ID快照（含临时档案） */
    @Excel(name = "参赛人ID快照", readConverterExp = "含=临时档案")
    private Long participantId;

    /** 支付时绑定的渠道方ID（锁定不变） */
    @Excel(name = "支付时绑定的渠道方ID", readConverterExp = "锁=定不变")
    private Long channelId;

    /** 关联赛事ID */
    @Excel(name = "关联赛事ID")
    private Long contestId;

    /** 关联赛事方ID */
    @Excel(name = "关联赛事方ID")
    private Long partnerId;

    /** 关联团队预约主记录ID */
    @Excel(name = "关联团队预约主记录ID")
    private Long teamAppointmentId;

    /** 订单标价金额 */
    @Excel(name = "订单标价金额")
    private BigDecimal listAmount;

    /** 优惠券抵扣金额 */
    @Excel(name = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    /** 积分抵扣折现金额 */
    @Excel(name = "积分抵扣折现金额")
    private BigDecimal pointsDeductAmount;

    /** 使用积分数（原始积分） */
    @Excel(name = "使用积分数", readConverterExp = "原=始积分")
    private Long pointsUsed;

    /** 平台承担的优惠金额 */
    @Excel(name = "平台承担的优惠金额")
    private BigDecimal platformBearAmount;

    /** 用户净实付金额 */
    @Excel(name = "用户净实付金额")
    private BigDecimal netPayAmount;

    /** 平台服务费 */
    @Excel(name = "平台服务费")
    private BigDecimal serviceFee;

    /** 支付方式：wechat/bank_transfer/points/points_cash_mix */
    @Excel(name = "支付方式：wechat/bank_transfer/points/points_cash_mix")
    private String payMethod;

    /** 支付发起方：self本人/channel渠道代付 */
    @Excel(name = "支付发起方：self本人/channel渠道代付")
    private String payInitiator;

    /** 发起方ID */
    @Excel(name = "发起方ID")
    private Long payInitiatorId;

    /** 支付完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 订单状态：created/pending_pay/paid/reviewing/in_service/aftersale/completed/cancelled/closed */
    @Excel(name = "订单状态：created/pending_pay/paid/reviewing/in_service/aftersale/completed/cancelled/closed")
    private String orderStatus;

    /** 退款状态：none/partial/full */
    @Excel(name = "退款状态：none/partial/full")
    private String refundStatus;

    /** 售后截止时间 = max(赛事结束,成绩发布) + N天 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "售后截止时间 = max(赛事结束,成绩发布) + N天", width = 30, dateFormat = "yyyy-MM-dd")
    private Date aftersaleDeadline;

    /** 使用的用户券ID */
    @Excel(name = "使用的用户券ID")
    private Long couponId;

    /** 是否允许用户自助退款：0否 1是 */
    @Excel(name = "是否允许用户自助退款：0否 1是")
    private Integer allowSelfRefund;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }

    public void setOrderType(String orderType) 
    {
        this.orderType = orderType;
    }

    public String getOrderType() 
    {
        return orderType;
    }

    public void setBusinessType(String businessType) 
    {
        this.businessType = businessType;
    }

    public String getBusinessType() 
    {
        return businessType;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParticipantId(Long participantId) 
    {
        this.participantId = participantId;
    }

    public Long getParticipantId() 
    {
        return participantId;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setPartnerId(Long partnerId) 
    {
        this.partnerId = partnerId;
    }

    public Long getPartnerId() 
    {
        return partnerId;
    }

    public void setTeamAppointmentId(Long teamAppointmentId) 
    {
        this.teamAppointmentId = teamAppointmentId;
    }

    public Long getTeamAppointmentId() 
    {
        return teamAppointmentId;
    }

    public void setListAmount(BigDecimal listAmount) 
    {
        this.listAmount = listAmount;
    }

    public BigDecimal getListAmount() 
    {
        return listAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) 
    {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getCouponAmount() 
    {
        return couponAmount;
    }

    public void setPointsDeductAmount(BigDecimal pointsDeductAmount) 
    {
        this.pointsDeductAmount = pointsDeductAmount;
    }

    public BigDecimal getPointsDeductAmount() 
    {
        return pointsDeductAmount;
    }

    public void setPointsUsed(Long pointsUsed) 
    {
        this.pointsUsed = pointsUsed;
    }

    public Long getPointsUsed() 
    {
        return pointsUsed;
    }

    public void setPlatformBearAmount(BigDecimal platformBearAmount) 
    {
        this.platformBearAmount = platformBearAmount;
    }

    public BigDecimal getPlatformBearAmount() 
    {
        return platformBearAmount;
    }

    public void setNetPayAmount(BigDecimal netPayAmount) 
    {
        this.netPayAmount = netPayAmount;
    }

    public BigDecimal getNetPayAmount() 
    {
        return netPayAmount;
    }

    public void setServiceFee(BigDecimal serviceFee) 
    {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getServiceFee() 
    {
        return serviceFee;
    }

    public void setPayMethod(String payMethod) 
    {
        this.payMethod = payMethod;
    }

    public String getPayMethod() 
    {
        return payMethod;
    }

    public void setPayInitiator(String payInitiator) 
    {
        this.payInitiator = payInitiator;
    }

    public String getPayInitiator() 
    {
        return payInitiator;
    }

    public void setPayInitiatorId(Long payInitiatorId) 
    {
        this.payInitiatorId = payInitiatorId;
    }

    public Long getPayInitiatorId() 
    {
        return payInitiatorId;
    }

    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }

    public void setOrderStatus(String orderStatus) 
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() 
    {
        return orderStatus;
    }

    public void setRefundStatus(String refundStatus) 
    {
        this.refundStatus = refundStatus;
    }

    public String getRefundStatus() 
    {
        return refundStatus;
    }

    public void setAftersaleDeadline(Date aftersaleDeadline) 
    {
        this.aftersaleDeadline = aftersaleDeadline;
    }

    public Date getAftersaleDeadline() 
    {
        return aftersaleDeadline;
    }

    public void setCouponId(Long couponId) 
    {
        this.couponId = couponId;
    }

    public Long getCouponId() 
    {
        return couponId;
    }

    public void setAllowSelfRefund(Integer allowSelfRefund) 
    {
        this.allowSelfRefund = allowSelfRefund;
    }

    public Integer getAllowSelfRefund() 
    {
        return allowSelfRefund;
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
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("orderType", getOrderType())
            .append("businessType", getBusinessType())
            .append("userId", getUserId())
            .append("participantId", getParticipantId())
            .append("channelId", getChannelId())
            .append("contestId", getContestId())
            .append("partnerId", getPartnerId())
            .append("teamAppointmentId", getTeamAppointmentId())
            .append("listAmount", getListAmount())
            .append("couponAmount", getCouponAmount())
            .append("pointsDeductAmount", getPointsDeductAmount())
            .append("pointsUsed", getPointsUsed())
            .append("platformBearAmount", getPlatformBearAmount())
            .append("netPayAmount", getNetPayAmount())
            .append("serviceFee", getServiceFee())
            .append("payMethod", getPayMethod())
            .append("payInitiator", getPayInitiator())
            .append("payInitiatorId", getPayInitiatorId())
            .append("payTime", getPayTime())
            .append("orderStatus", getOrderStatus())
            .append("refundStatus", getRefundStatus())
            .append("aftersaleDeadline", getAftersaleDeadline())
            .append("couponId", getCouponId())
            .append("allowSelfRefund", getAllowSelfRefund())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
