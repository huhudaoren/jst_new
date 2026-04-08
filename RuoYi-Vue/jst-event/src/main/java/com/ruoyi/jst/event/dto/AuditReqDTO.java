package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 审核请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class AuditReqDTO {

    /**
     * 驳回校验分组。
     */
    public interface Reject {
    }

    @NotBlank(message = "auditRemark 不能为空", groups = Reject.class)
    @Size(max = 255, message = "auditRemark 长度不能超过 255")
    private String auditRemark;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
