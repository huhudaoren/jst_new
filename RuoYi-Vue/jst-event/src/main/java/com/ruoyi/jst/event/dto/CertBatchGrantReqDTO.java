package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 批量生成证书请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertBatchGrantReqDTO {

    /** 赛事ID。 */
    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    /** 证书模板ID。 */
    @NotNull(message = "证书模板ID不能为空")
    private Long templateId;

    /** 成绩ID列表，空表示当前赛事全部已发布成绩。 */
    private List<Long> scoreIds;

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

    public List<Long> getScoreIds() {
        return scoreIds;
    }

    public void setScoreIds(List<Long> scoreIds) {
        this.scoreIds = scoreIds;
    }
}
