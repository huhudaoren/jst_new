package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 动态表单模板列表查询 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class FormTemplateQueryReqDTO {

    @Size(max = 128, message = "templateName 长度不能超过 128")
    private String templateName;

    @Pattern(regexp = "platform|partner", message = "ownerType 仅支持 platform/partner")
    private String ownerType;

    private Long ownerId;

    @Pattern(regexp = "draft|pending|approved|rejected", message = "auditStatus 非法")
    private String auditStatus;

    @Min(value = 0, message = "status 仅支持 0/1")
    @Max(value = 1, message = "status 仅支持 0/1")
    private Integer status;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
