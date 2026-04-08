package com.ruoyi.jst.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 小程序创建混合支付订单入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class CreateOrderReqDTO {

    @NotNull(message = "报名ID不能为空")
    private Long enrollId;

    private Long couponId;

    @Min(value = 0, message = "积分使用数不能小于0")
    private Long pointsToUse;

    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "wechat|bank_transfer|points", message = "支付方式仅支持 wechat/bank_transfer/points")
    private String payMethod;

    public Long getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(Long enrollId) {
        this.enrollId = enrollId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getPointsToUse() {
        return pointsToUse;
    }

    public void setPointsToUse(Long pointsToUse) {
        this.pointsToUse = pointsToUse;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
}
