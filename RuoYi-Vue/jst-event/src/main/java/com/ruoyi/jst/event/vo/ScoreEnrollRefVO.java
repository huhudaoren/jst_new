package com.ruoyi.jst.event.vo;

/**
 * 成绩写入时使用的报名记录引用。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreEnrollRefVO {

    private Long enrollId;
    private String enrollNo;
    private Long contestId;
    private Long userId;
    private Long participantId;
    private String auditStatus;

    public Long getEnrollId() { return enrollId; }
    public void setEnrollId(Long enrollId) { this.enrollId = enrollId; }
    public String getEnrollNo() { return enrollNo; }
    public void setEnrollNo(String enrollNo) { this.enrollNo = enrollNo; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
}
