package com.ruoyi.jst.user.dto;

/**
 * 管理端参赛档案认领映射查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantUserMapQueryReqDTO {

    /** 参赛档案ID。 */
    private Long participantId;

    /** 认领用户ID。 */
    private Long userId;

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
