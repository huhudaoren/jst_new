package com.ruoyi.jst.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

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

    /** 各成绩项打分（仅审核通过时可选传入） */
    @Valid
    private List<ScoreItemReqDTO> scores;

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

    public List<ScoreItemReqDTO> getScores() {
        return scores;
    }

    public void setScores(List<ScoreItemReqDTO> scores) {
        this.scores = scores;
    }
}
