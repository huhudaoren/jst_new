package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 赛事方证书列表响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class PartnerCertResVO {

    private Long certId;
    private String certNo;
    private Long contestId;
    private String contestName;
    private Long scoreId;
    private Long enrollId;
    private String enrollNo;
    private Long userId;
    private Long participantId;
    private String participantName;
    private String guardianMobileMasked;
    private Long templateId;
    private String templateName;
    private String certFileUrl;
    private String issueStatus;
    private String displayStatus;
    private Date issueTime;
    private String verifyCode;
    private String awardLevel;

    public Long getCertId() { return certId; }
    public void setCertId(Long certId) { this.certId = certId; }
    public String getCertNo() { return certNo; }
    public void setCertNo(String certNo) { this.certNo = certNo; }
    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getContestName() { return contestName; }
    public void setContestName(String contestName) { this.contestName = contestName; }
    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }
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
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getCertFileUrl() { return certFileUrl; }
    public void setCertFileUrl(String certFileUrl) { this.certFileUrl = certFileUrl; }
    public String getIssueStatus() { return issueStatus; }
    public void setIssueStatus(String issueStatus) { this.issueStatus = issueStatus; }
    public String getDisplayStatus() { return displayStatus; }
    public void setDisplayStatus(String displayStatus) { this.displayStatus = displayStatus; }
    public Date getIssueTime() { return issueTime; }
    public void setIssueTime(Date issueTime) { this.issueTime = issueTime; }
    public String getVerifyCode() { return verifyCode; }
    public void setVerifyCode(String verifyCode) { this.verifyCode = verifyCode; }
    public String getAwardLevel() { return awardLevel; }
    public void setAwardLevel(String awardLevel) { this.awardLevel = awardLevel; }
}
