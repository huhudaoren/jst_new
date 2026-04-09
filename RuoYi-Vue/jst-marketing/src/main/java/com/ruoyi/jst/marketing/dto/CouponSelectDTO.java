package com.ruoyi.jst.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Coupon select pre-calc DTO.
 */
public class CouponSelectDTO {

    @NotNull(message = "orderAmount不能为空")
    @DecimalMin(value = "0.00", message = "orderAmount不能为负数")
    private BigDecimal orderAmount;

    private Long contestId;

    private Long goodsId;

    private List<Long> candidateCouponIds;

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public List<Long> getCandidateCouponIds() {
        return candidateCouponIds;
    }

    public void setCandidateCouponIds(List<Long> candidateCouponIds) {
        this.candidateCouponIds = candidateCouponIds;
    }
}
