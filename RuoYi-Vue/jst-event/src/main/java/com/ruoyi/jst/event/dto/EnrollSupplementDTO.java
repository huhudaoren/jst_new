package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * 小程序报名补件请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollSupplementDTO {

    private Map<String, Object> formData;

    private List<String> attachments;

    @Size(max = 255, message = "supplementRemark 长度不能超过 255")
    private String supplementRemark;

    public Map<String, Object> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getSupplementRemark() {
        return supplementRemark;
    }

    public void setSupplementRemark(String supplementRemark) {
        this.supplementRemark = supplementRemark;
    }
}
