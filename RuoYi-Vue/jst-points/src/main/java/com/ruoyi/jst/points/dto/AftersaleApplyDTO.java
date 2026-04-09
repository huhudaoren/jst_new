package com.ruoyi.jst.points.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 商城售后申请入参。
 */
public class AftersaleApplyDTO {

    @NotNull(message = "兑换单ID不能为空")
    private Long exchangeId;

    @NotBlank(message = "售后原因不能为空")
    @Size(max = 255, message = "售后原因长度不能超过255个字符")
    private String reason;

    @NotBlank(message = "退款类型不能为空")
    private String refundType;

    public Long getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }
}
