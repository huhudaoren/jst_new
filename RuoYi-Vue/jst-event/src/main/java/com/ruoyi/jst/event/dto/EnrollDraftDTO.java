package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * 小程序报名草稿保存请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollDraftDTO {

    private Long enrollId;

    @NotNull(message = "contestId 不能为空")
    private Long contestId;

    @NotNull(message = "participantId 不能为空")
    private Long participantId;

    private Map<String, Object> formData;

    private List<String> attachments;

    public Long getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(Long enrollId) {
        this.enrollId = enrollId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

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
}
