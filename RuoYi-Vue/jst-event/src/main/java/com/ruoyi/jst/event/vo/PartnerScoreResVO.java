package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 赛事方成绩列表响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class PartnerScoreResVO {

    private Long scoreId;
    private Long contestId;
    private String contestName;
    private Long enrollId;
    private String enrollNo;
    private Long userId;
    private Long participantId;
    private String participantName;
    private String guardianMobileMasked;
    private BigDecimal scoreValue;
    private String awardLevel;
    private Long rankNo;
    private String importBatchNo;
    private String auditStatus;
    private String publishStatus;
    private String displayStatus;
    private Date publishTime;
    private String remark;
    private Date updateTime;

    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
    public Long getEnrollId() { return enrollId; }
    public void setEnrollId(Long enrollId) { this.enrollId = enrollId; }
    public String getEnrollNo() { return enrollNo; }
    public void setEnrollNo(String enrollNo) { this.enrollNo = enrollNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }
    public String getGuardianMobileMasked() { return guardianMobileMasked; }
    public void setGuardianMobileMasked(String guardianMobileMasked) { this.guardianMobileMasked = guardianMobileMasked; }
    public BigDecimal getScoreValue() { return scoreValue; }
    public void setScoreValue(BigDecimal scoreValue) { this.scoreValue = scoreValue; }
    public String getAwardLevel() { return awardLevel; }
    public void setAwardLevel(String awardLevel) { this.awardLevel = awardLevel; }
    public Long getRankNo() { return rankNo; }
    public void setRankNo(Long rankNo) { this.rankNo = rankNo; }
    public String getImportBatchNo() { return importBatchNo; }
    public void setImportBatchNo(String importBatchNo) { this.importBatchNo = importBatchNo; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public String getDisplayStatus() { return displayStatus; }
    public void setDisplayStatus(String displayStatus) { this.displayStatus = displayStatus; }
    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
