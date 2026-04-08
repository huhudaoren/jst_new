package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 小程序赛事详情 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxContestDetailVO {

    private Long contestId;
    private String contestName;
    private String coverImage;
    private String category;
    private String groupLevels;
    private String description;
    private BigDecimal price;
    private Date enrollStartTime;
    private Date enrollEndTime;
    private Date eventStartTime;
    private Date eventEndTime;
    private String status;
    private Boolean enrollOpen;
    private String partnerName;
    private Integer supportChannelEnroll;
    private Integer supportPointsDeduct;
    private Integer supportAppointment;
    private String certRuleJson;
    private String scoreRuleJson;
    private Long aftersaleDays;

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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getEnrollOpen() {
        return enrollOpen;
    }

    public void setEnrollOpen(Boolean enrollOpen) {
        this.enrollOpen = enrollOpen;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Integer getSupportChannelEnroll() {
        return supportChannelEnroll;
    }

    public void setSupportChannelEnroll(Integer supportChannelEnroll) {
        this.supportChannelEnroll = supportChannelEnroll;
    }

    public Integer getSupportPointsDeduct() {
        return supportPointsDeduct;
    }

    public void setSupportPointsDeduct(Integer supportPointsDeduct) {
        this.supportPointsDeduct = supportPointsDeduct;
    }

    public Integer getSupportAppointment() {
        return supportAppointment;
    }

    public void setSupportAppointment(Integer supportAppointment) {
        this.supportAppointment = supportAppointment;
    }

    public String getCertRuleJson() {
        return certRuleJson;
    }

    public void setCertRuleJson(String certRuleJson) {
        this.certRuleJson = certRuleJson;
    }

    public String getScoreRuleJson() {
        return scoreRuleJson;
    }

    public void setScoreRuleJson(String scoreRuleJson) {
        this.scoreRuleJson = scoreRuleJson;
    }

    public Long getAftersaleDays() {
        return aftersaleDays;
    }

    public void setAftersaleDays(Long aftersaleDays) {
        this.aftersaleDays = aftersaleDays;
    }
}
