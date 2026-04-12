package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 赛事后台列表 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ContestListVO {

    private Long contestId;
    private String contestName;
    private String category;
    private String groupLevels;
    private String coverImage;
    private BigDecimal price;
    private Date enrollStartTime;
    private Date enrollEndTime;
    private Date eventStartTime;
    private Date eventEndTime;
    private Long enrollCount;
    private String auditStatus;
    private String auditRemark;
    private String status;
    private Long partnerId;
    private String partnerName;
    private String recommendTags;

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

    public String getGroupLevels() {
        return groupLevels;
    }

    public void setGroupLevels(String groupLevels) {
        this.groupLevels = groupLevels;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getEnrollStartTime() {
        return enrollStartTime;
    }

    public void setEnrollStartTime(Date enrollStartTime) {
        this.enrollStartTime = enrollStartTime;
    }

    public Date getEnrollEndTime() {
        return enrollEndTime;
    }

    public void setEnrollEndTime(Date enrollEndTime) {
        this.enrollEndTime = enrollEndTime;
    }

    public Date getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Date eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Date getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Date eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public Long getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Long enrollCount) {
        this.enrollCount = enrollCount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getRecommendTags() {
        return recommendTags;
    }

    public void setRecommendTags(String recommendTags) {
        this.recommendTags = recommendTags;
    }
}
