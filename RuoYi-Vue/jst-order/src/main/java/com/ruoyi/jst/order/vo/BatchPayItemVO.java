package com.ruoyi.jst.order.vo;

import java.math.BigDecimal;

/**
 * 批量支付单笔订单明细（与 jsapi params 聚合）。
 * <p>
 * 对应端点：POST /jst/wx/channel/orders/batch-pay（Round 2B · B1）。
 *
 * @author jst
 * @since 1.0.0
 */
public class BatchPayItemVO {

    /** 订单 ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 本单应支付金额（= jst_order_main.net_pay_amount） */
    private BigDecimal payAmount;

    /** 微信预支付参数（Mock/Real 双实现，前端直接调起 wx.requestPayment） */
    private WechatPrepayVO merchantPayParams;

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

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public WechatPrepayVO getMerchantPayParams() {
        return merchantPayParams;
    }

    public void setMerchantPayParams(WechatPrepayVO merchantPayParams) {
        this.merchantPayParams = merchantPayParams;
    }
}
