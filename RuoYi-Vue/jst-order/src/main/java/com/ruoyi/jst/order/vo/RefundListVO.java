package com.ruoyi.jst.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款列表出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class RefundListVO {

    private Long refundId;

    private String refundNo;

    private Long orderId;

    private String orderNo;

    private Long contestId;

    private String contestName;

    private Long participantId;

    private String participantName;

    private String refundType;

    private String applySource;

    private BigDecimal applyCash;

    private Long applyPoints;

    private BigDecimal actualCash;

    private Long actualPoints;

    private Integer couponReturned;

    /** Round 2A A7: 回退的积分数（别名，提高前端语义一致性，等价 actualPoints） */
    private Long pointsRefund;

    /** Round 2A A7: 回退优惠券快照（含券名、面额、当前状态），无券则 null */
    private CouponRefundInfo couponRefund;

    /** Round 2A A7: 预计到账时间（approved_time + 7 天；approved 状态才有值） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expectedArrivalTime;

    private String status;

    private String auditRemark;

    private String orderStatus;

    private String refundStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    // ---- Round 2A A7: MyBatis shadow fields (不直接返给前端，Service 合成 couponRefund / expectedArrivalTime) ----
    @JsonIgnore
    private String couponName;
    @JsonIgnore
    private BigDecimal couponFaceAmount;
    @JsonIgnore
    private String couponStatus;
    @JsonIgnore
    private Date updateTime;

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

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

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public BigDecimal getApplyCash() {
        return applyCash;
    }

    public void setApplyCash(BigDecimal applyCash) {
        this.applyCash = applyCash;
    }

    public Long getApplyPoints() {
        return applyPoints;
    }

    public void setApplyPoints(Long applyPoints) {
        this.applyPoints = applyPoints;
    }

    public BigDecimal getActualCash() {
        return actualCash;
    }

    public void setActualCash(BigDecimal actualCash) {
        this.actualCash = actualCash;
    }

    public Long getActualPoints() {
        return actualPoints;
    }

    public void setActualPoints(Long actualPoints) {
        this.actualPoints = actualPoints;
    }

    public Integer getCouponReturned() {
        return couponReturned;
    }

    public void setCouponReturned(Integer couponReturned) {
        this.couponReturned = couponReturned;
    }

    public Long getPointsRefund() {
        return pointsRefund;
    }

    public void setPointsRefund(Long pointsRefund) {
        this.pointsRefund = pointsRefund;
    }

    public CouponRefundInfo getCouponRefund() {
        return couponRefund;
    }

    public void setCouponRefund(CouponRefundInfo couponRefund) {
        this.couponRefund = couponRefund;
    }

    public Date getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(Date expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    // ---- Round 2A A7 shadow getters/setters ----
    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public BigDecimal getCouponFaceAmount() {
        return couponFaceAmount;
    }

    public void setCouponFaceAmount(BigDecimal couponFaceAmount) {
        this.couponFaceAmount = couponFaceAmount;
    }

    public String getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(String couponStatus) {
        this.couponStatus = couponStatus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
