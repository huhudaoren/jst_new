package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 成绩批量提交审核请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreSubmitReviewReqDTO {

    /** 成绩ID列表。 */
    @NotEmpty(message = "成绩ID列表不能为空")
    private List<Long> scoreIds;

    public List<Long> getScoreIds() {
        return scoreIds;
    }

    public void setScoreIds(List<Long> scoreIds) {
        this.scoreIds = scoreIds;
    }
}
