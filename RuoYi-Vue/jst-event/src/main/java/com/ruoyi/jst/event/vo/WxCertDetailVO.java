package com.ruoyi.jst.event.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Mini-program certificate detail response.
 */
public class WxCertDetailVO {

    private Long certId;
    private String certNo;
    private String templateName;
    private String layoutJson;
    private String bgImage;
    private Map<String, String> variables;
    private String issueStatus;
    private Date createTime;
    private Date issueTime;
    private Date issueDate;
    private String certImageUrl;
    private String certFileUrl;
    private String holderName;
    private String contestName;
    private String awardLevel;
    private String groupLevel;
    private String category;
    private String issueOrg;
    private BigDecimal scoreValue;

    @JsonIgnore
    private String formSnapshotJson;

    public Long getCertId() {
        return certId;
    }

    public void setCertId(Long certId) {
        this.certId = certId;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getCertImageUrl() {
        return certImageUrl;
    }

    public void setCertImageUrl(String certImageUrl) {
        this.certImageUrl = certImageUrl;
    }

    public String getCertFileUrl() {
        return certFileUrl;
    }

    public void setCertFileUrl(String certFileUrl) {
        this.certFileUrl = certFileUrl;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public String getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(String groupLevel) {
        this.groupLevel = groupLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIssueOrg() {
        return issueOrg;
    }

    public void setIssueOrg(String issueOrg) {
        this.issueOrg = issueOrg;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getFormSnapshotJson() {
        return formSnapshotJson;
    }

    public void setFormSnapshotJson(String formSnapshotJson) {
        this.formSnapshotJson = formSnapshotJson;
    }
}
