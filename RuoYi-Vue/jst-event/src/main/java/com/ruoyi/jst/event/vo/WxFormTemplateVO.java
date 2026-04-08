package com.ruoyi.jst.event.vo;

/**
 * 小程序端报名表单模板 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxFormTemplateVO {

    private Long templateId;

    private Integer templateVersion;

    private Object schemaJson;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(Integer templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Object getSchemaJson() {
        return schemaJson;
    }

    public void setSchemaJson(Object schemaJson) {
        this.schemaJson = schemaJson;
    }
}
