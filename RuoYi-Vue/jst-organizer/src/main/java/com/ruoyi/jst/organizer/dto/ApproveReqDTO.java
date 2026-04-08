package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 赛事方入驻申请-审核通过请求 DTO
 *
 * @author jst
 * @since 1.0.0
 */
public class ApproveReqDTO {

    @Size(max = 255, message = "auditRemark 长度不能超过 255")
    private String auditRemark;

    @NotBlank(message = "username 不能为空")
    @Size(max = 30, message = "username 长度不能超过 30")
    private String username;

    @Email(message = "email 格式不正确")
    @Size(max = 50, message = "email 长度不能超过 50")
    private String email;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
