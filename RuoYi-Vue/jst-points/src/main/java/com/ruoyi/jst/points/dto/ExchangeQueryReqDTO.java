package com.ruoyi.jst.points.dto;

/**
 * 兑换订单查询条件。
 *
 * @author jst
 * @since 1.0.0
 */
public class ExchangeQueryReqDTO {

    private String exchangeNo;

    private Long userId;

    private Long goodsId;

    private String status;

    public String getExchangeNo() {
        return exchangeNo;
    }

    public void setExchangeNo(String exchangeNo) {
        this.exchangeNo = exchangeNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
