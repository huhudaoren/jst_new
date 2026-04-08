package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 报名记录详情 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollDetailVO {

    private Long enrollId;

    private String enrollNo;

    private Long contestId;

    private String contestName;

    private Long partnerId;

    private Long participantId;

    private String participantName;

    private String guardianName;

    private String guardianMobileMasked;

    private String school;

    private Long templateId;

    private Integer templateVersion;

    private Object formSnapshotJson;

    private String materialStatus;

    private String auditStatus;

    private String auditRemark;

    private String supplementRemark;

    private Date submitTime;

    private Date updateTime;

    private String remark;

    public Long getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(Long enrollId) {
        this.enrollId = enrollId;
    }

    public String getEnrollNo() {
        return enrollNo;
    }

    public void setEnrollNo(String enrollNo) {
        this.enrollNo = enrollNo;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianMobileMasked() {
        return guardianMobileMasked;
    }

    public void setGuardianMobileMasked(String guardianMobileMasked) {
        this.guardianMobileMasked = guardianMobileMasked;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(Integer templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Object getFormSnapshotJson() {
        return formSnapshotJson;
    }

    public void setFormSnapshotJson(Object formSnapshotJson) {
        this.formSnapshotJson = formSnapshotJson;
    }

    public String getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(String materialStatus) {
        this.materialStatus = materialStatus;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getSupplementRemark() {
        return supplementRemark;
    }

    public void setSupplementRemark(String supplementRemark) {
        this.supplementRemark = supplementRemark;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
