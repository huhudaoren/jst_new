package com.ruoyi.jst.order.vo;

import java.math.BigDecimal;

/**
 * 创建订单响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class CreateOrderResVO {

    private Long orderId;

    private String orderNo;

    private BigDecimal listAmount;

    private BigDecimal couponAmount;

    private BigDecimal pointsDeductAmount;

    private Long pointsUsed;

    private BigDecimal netPayAmount;

    private String orderType;

    private WechatPrepayVO wechatPrepay;

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

    public WechatPrepayVO getWechatPrepay() {
        return wechatPrepay;
    }

    public void setWechatPrepay(WechatPrepayVO wechatPrepay) {
        this.wechatPrepay = wechatPrepay;
    }
}
