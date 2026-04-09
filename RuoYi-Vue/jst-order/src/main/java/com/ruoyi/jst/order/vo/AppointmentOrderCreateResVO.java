package com.ruoyi.jst.order.vo;

import java.math.BigDecimal;

/**
 * 预约子订单创建结果。
 *
 * @author jst
 * @since 1.0.0
 */
public class AppointmentOrderCreateResVO {

    private Long orderId;
    private String orderNo;
    private String orderType;
    private String orderStatus;
    private BigDecimal netPayAmount;

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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getNetPayAmount() {
        return netPayAmount;
    }

    public void setNetPayAmount(BigDecimal netPayAmount) {
        this.netPayAmount = netPayAmount;
    }
}
