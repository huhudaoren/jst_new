package com.ruoyi.jst.order.vo;

import java.math.BigDecimal;

/**
 * Round 2A A7: 退款回退券信息快照
 * <p>
 * 在 RefundListVO / RefundDetailVO 中作为嵌套对象返回，供小程序端直接展示
 * 退款已回退的优惠券名称 / 面额 / 当前状态（unused/used/expired/refunded）。
 *
 * @author jst
 * @since 1.0.0
 */
public class CouponRefundInfo {

    /** 优惠券名称 */
    private String couponName;

    /** 优惠券面额 */
    private BigDecimal faceAmount;

    /** 优惠券当前状态（jst_user_coupon.status）：unused/locked/used/expired/refunded */
    private String couponStatus;

    public CouponRefundInfo() {
    }

    public CouponRefundInfo(String couponName, BigDecimal faceAmount, String couponStatus) {
        this.couponName = couponName;
        this.faceAmount = faceAmount;
        this.couponStatus = couponStatus;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public BigDecimal getFaceAmount() {
        return faceAmount;
    }

    public void setFaceAmount(BigDecimal faceAmount) {
        this.faceAmount = faceAmount;
    }

    public String getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(String couponStatus) {
        this.couponStatus = couponStatus;
    }
}
