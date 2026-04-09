package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Admin withdrawal audit request.
 */
public class WithdrawAuditDTO {

    @NotBlank(message = "auditRemark cannot be blank")
    @Size(max = 255, message = "auditRemark length must be <= 255")
    private String auditRemark;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
