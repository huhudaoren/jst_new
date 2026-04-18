package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 动态表单模板保存请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class FormTemplateSaveReqDTO {

    private Long templateId;

    @NotBlank(message = "templateName 不能为空")
    @Size(max = 128, message = "templateName 长度不能超过 128")
    private String templateName;

    @NotBlank(message = "ownerType 不能为空")
    @Pattern(regexp = "platform|partner", message = "ownerType 仅支持 platform/partner")
    private String ownerType;

    /**
     * 目标赛事方 ID（仅 ownerType=partner 且当前登录为 admin/运营 时需要）。
     * - partner 登录：忽略此字段，强制取 currentPartnerId
     * - admin / 运营 登录 + ownerType=partner：必填
     * - admin / 运营 登录 + ownerType=platform：忽略
     * 不加 @NotNull，业务语义校验放在 Service 层（FormTemplateServiceImpl.resolveOwnerId）。
     */
    private Long ownerId;

    @NotNull(message = "schemaJson 不能为空")
    private Object schemaJson;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

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

    public Object getSchemaJson() {
        return schemaJson;
    }

    public void setSchemaJson(Object schemaJson) {
        this.schemaJson = schemaJson;
    }
}
