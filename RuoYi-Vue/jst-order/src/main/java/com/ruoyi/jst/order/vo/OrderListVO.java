package com.ruoyi.jst.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单列表出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class OrderListVO {

    private Long orderId;

    private String orderNo;

    private Long contestId;

    private String contestName;

    private Long participantId;

    private String participantName;

    private BigDecimal listAmount;

    private BigDecimal couponAmount;

    private BigDecimal pointsDeductAmount;

    private Long pointsUsed;

    private BigDecimal netPayAmount;

    /** Round 2A A6: 是否允许学生自助退款（status=paid AND 在售后期 AND netPayAmount>0） */
    private Boolean refundEnabled;

    private String orderType;

    private String payMethod;

    private String orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** Round 2A A6: 售后截止时间（用于前端倒计时 + refundEnabled 计算） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date aftersaleDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public BigDecimal getListAmount() {
        return listAmount;
    }

    public void setListAmount(BigDecimal listAmount) {
        this.listAmount = listAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getPointsDeductAmount() {
        return pointsDeductAmount;
    }

    public void setPointsDeductAmount(BigDecimal pointsDeductAmount) {
        this.pointsDeductAmount = pointsDeductAmount;
    }

    public Long getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(Long pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public BigDecimal getNetPayAmount() {
        return netPayAmount;
    }

    public void setNetPayAmount(BigDecimal netPayAmount) {
        this.netPayAmount = netPayAmount;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getAftersaleDeadline() {
        return aftersaleDeadline;
    }

    public void setAftersaleDeadline(Date aftersaleDeadline) {
        this.aftersaleDeadline = aftersaleDeadline;
    }

    public Boolean getRefundEnabled() {
        return refundEnabled;
    }

    public void setRefundEnabled(Boolean refundEnabled) {
        this.refundEnabled = refundEnabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
