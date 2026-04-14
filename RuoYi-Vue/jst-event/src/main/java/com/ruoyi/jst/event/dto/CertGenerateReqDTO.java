package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 证书生成请求（简化版，仅创建记录+编号，不做 PDF 渲染）。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertGenerateReqDTO {

    /** 赛事ID。 */
    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    /** 证书模板ID。 */
    @NotNull(message = "证书模板ID不能为空")
    private Long templateId;

    /** 报名ID列表。 */
    @NotEmpty(message = "报名ID列表不能为空")
    private List<Long> enrollIds;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<Long> getEnrollIds() {
        return enrollIds;
    }

    public void setEnrollIds(List<Long> enrollIds) {
        this.enrollIds = enrollIds;
    }
}
