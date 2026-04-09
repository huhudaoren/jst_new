package com.ruoyi.jst.points.vo;

import com.ruoyi.jst.order.vo.WechatPrepayVO;

import java.math.BigDecimal;

/**
 * 兑换下单结果出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class ExchangeApplyResVO {

    private Long exchangeId;

    private String exchangeNo;

    private Long orderId;

    private String status;

    private Long pointsUsed;

    private BigDecimal cashAmount;

    private WechatPrepayVO wechatPrepay;

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getExchangeNo() {
        return exchangeNo;
    }

    public void setExchangeNo(String exchangeNo) {
        this.exchangeNo = exchangeNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(Long pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public WechatPrepayVO getWechatPrepay() {
        return wechatPrepay;
    }

    public void setWechatPrepay(WechatPrepayVO wechatPrepay) {
        this.wechatPrepay = wechatPrepay;
    }
}
