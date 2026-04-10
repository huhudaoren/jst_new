package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;

/**
 * 证书生成时使用的已发布成绩引用。
 *
 * @author jst
 * @since 1.0.0
 */
public class CertScoreRefVO {

    private Long scoreId;
    private Long contestId;
    private Long enrollId;
    private Long userId;
    private Long participantId;
    private BigDecimal scoreValue;
    private String awardLevel;
    private String participantName;
    private String contestName;

    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public Long getEnrollId() { return enrollId; }
    public void setEnrollId(Long enrollId) { this.enrollId = enrollId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public BigDecimal getScoreValue() { return scoreValue; }
    public void setScoreValue(BigDecimal scoreValue) { this.scoreValue = scoreValue; }
    public String getAwardLevel() { return awardLevel; }
    public void setAwardLevel(String awardLevel) { this.awardLevel = awardLevel; }
    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
}
