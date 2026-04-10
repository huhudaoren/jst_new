package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * Mini-program certificate list item.
 */
public class WxCertVO {

    private Long certId;
    private Long contestId;
    private String contestName;
    private String category;
    private String certName;
    private String certNo;
    private String awardLevel;
    private String certImageUrl;
    private String certFileUrl;
    private String holderName;
    private Date issueTime;
    private Date issueDate;

    public Long getCertId() {
        return certId;
    }

    public void setCertId(Long certId) {
        this.certId = certId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
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
}
