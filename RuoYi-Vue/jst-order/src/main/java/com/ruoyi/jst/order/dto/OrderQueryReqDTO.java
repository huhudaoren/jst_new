package com.ruoyi.jst.order.dto;

import jakarta.validation.constraints.Size;

/**
 * 订单查询条件。
 *
 * @author jst
 * @since 1.0.0
 */
public class OrderQueryReqDTO {

    @Size(max = 32, message = "订单号长度不能超过32位")
    private String orderNo;

    @Size(max = 20, message = "订单状态长度不能超过20位")
    private String orderStatus;

    @Size(max = 20, message = "支付方式长度不能超过20位")
    private String payMethod;

    private Long contestId;

    private Long userId;

    private Long channelId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
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

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
