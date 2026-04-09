package com.ruoyi.jst.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 退款审核入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class RefundAuditDTO {

    @NotBlank(message = "审核备注不能为空")
    @Size(max = 255, message = "审核备注长度不能超过255个字符")
    private String auditRemark;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
