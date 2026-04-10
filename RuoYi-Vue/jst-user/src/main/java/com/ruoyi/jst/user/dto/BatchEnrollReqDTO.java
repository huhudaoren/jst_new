package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 批量报名入参
 * <p>
 * 渠道方批量报名：指定赛事 + 参赛人ID列表
 *
 * @author jst
 * @since 1.0.0
 */
public class BatchEnrollReqDTO {

    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    @NotEmpty(message = "参赛人ID列表不能为空")
    private List<Long> participantIds;

    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public List<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Long> participantIds) { this.participantIds = participantIds; }
}
