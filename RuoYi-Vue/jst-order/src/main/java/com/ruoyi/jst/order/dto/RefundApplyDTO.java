package com.ruoyi.jst.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 退款申请入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class RefundApplyDTO {

    @NotBlank(message = "退款类型不能为空")
    @Pattern(regexp = "refund_only|return_refund", message = "退款类型仅支持 refund_only/return_refund")
    private String refundType;

    @NotBlank(message = "退款原因不能为空")
    @Size(max = 255, message = "退款原因长度不能超过255个字符")
    private String reason;

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
