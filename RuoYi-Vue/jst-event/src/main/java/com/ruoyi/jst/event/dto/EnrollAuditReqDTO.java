package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 报名记录审核请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollAuditReqDTO {

    @NotBlank(message = "result 不能为空")
    @Pattern(regexp = "approved|rejected|supplement", message = "result 仅支持 approved/rejected/supplement")
    private String result;

    @Size(max = 255, message = "auditRemark 长度不能超过 255")
    private String auditRemark;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
