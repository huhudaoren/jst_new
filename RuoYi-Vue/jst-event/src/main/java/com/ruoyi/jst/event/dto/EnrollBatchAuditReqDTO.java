package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 赛事方批量审核报名请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollBatchAuditReqDTO {

    @NotEmpty(message = "enrollIds 不能为空")
    private List<Long> enrollIds;

    @NotBlank(message = "result 不能为空")
    @Pattern(regexp = "approved|rejected|supplement", message = "result 仅支持 approved/rejected/supplement")
    private String result;

    @Size(max = 255, message = "auditRemark 长度不能超过 255")
    private String auditRemark;

    public List<Long> getEnrollIds() {
        return enrollIds;
    }

    public void setEnrollIds(List<Long> enrollIds) {
        this.enrollIds = enrollIds;
    }

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
