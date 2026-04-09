package com.ruoyi.jst.order.dto;

import jakarta.validation.constraints.Size;

/**
 * 退款列表查询条件。
 *
 * @author jst
 * @since 1.0.0
 */
public class RefundQueryReqDTO {

    @Size(max = 32, message = "退款单号长度不能超过32位")
    private String refundNo;

    @Size(max = 32, message = "订单号长度不能超过32位")
    private String orderNo;

    @Size(max = 20, message = "退款状态长度不能超过20位")
    private String status;

    @Size(max = 20, message = "退款类型长度不能超过20位")
    private String refundType;

    @Size(max = 20, message = "申请来源长度不能超过20位")
    private String applySource;

    private Long orderId;

    private Long contestId;

    private Long userId;

    private Long partnerId;

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }
}
