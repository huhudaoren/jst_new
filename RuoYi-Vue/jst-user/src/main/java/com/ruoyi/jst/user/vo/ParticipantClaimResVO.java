package com.ruoyi.jst.user.vo;

import java.util.Date;

/**
 * 认领结果-响应 VO
 * <p>
 * 强制：所有出参不允许直接返回 Entity,必须经 VO 过滤+脱敏
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantClaimResVO {

    private Long participantId;
    private Long userId;
    private String claimMethod;
    private Date claimTime;
    private String resultMessage;

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getClaimMethod() { return claimMethod; }
    public void setClaimMethod(String claimMethod) { this.claimMethod = claimMethod; }
    public Date getClaimTime() { return claimTime; }
    public void setClaimTime(Date claimTime) { this.claimTime = claimTime; }
    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
}
