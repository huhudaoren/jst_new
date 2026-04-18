package com.ruoyi.jst.event.vo;

import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import com.ruoyi.jst.event.domain.JstContestAward;
import com.ruoyi.jst.event.domain.JstContestFaq;
import com.ruoyi.jst.event.domain.JstContestSchedule;
import com.ruoyi.jst.event.domain.JstScoreItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    private String bannerImage;
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
    private Integer totalQuota;
    private Integer perUserLimit;
    private Integer appointmentCapacity;
    private String writeoffMode;
    private String writeoffConfig;
    private Integer needSignIn;
    private Integer teamMinSize;
    private Integer teamMaxSize;
    private String teamLeaderFields;
    private String teamMemberFields;
    private Integer allowRepeatAppointment;
    /** Round 2A A4: 线下预约剩余名额（null=不限量） */
    private Integer offlineReserveRemaining;
    /** Round 2A A4: 线下预约截止时间 */
    private Date offlineReserveDeadline;
    @Deprecated
    private String scheduleJson;
    @Deprecated
    private String awardsJson;
    @Deprecated
    private String faqJson;
    private String recommendTags;
    private String certRuleJson;
    private String certIssueMode;
    private Date scorePublishTime;
    private String scorePublishMode;
    @Deprecated
    private String scoreRuleJson;
    private Long aftersaleDays;
    private Date aftersaleDeadline;
    private String organizer;
    private String coOrganizer;
    private String eventAddress;
    /** 主办方 LOGO URL */
    private String organizerLogo;
    /** 主办方简介 */
    private String organizerDesc;
    /** 咨询电话 */
    private String contactPhone;
    /** 咨询微信 */
    private String contactWechat;
    /** 咨询邮箱 */
    private String contactEmail;
    private List<JstContestSchedule> scheduleList;
    private List<JstContestAward> awardList;
    private List<JstContestFaq> faqList;
    private List<JstScoreItem> scoreItemList;
    private List<JstAppointmentSlot> appointmentSlotList;

    /** 该赛事是否已发布成绩（至少有 1 条 published 的 score_record）。 */
    private Boolean scorePublished;
    /** 当前用户是否有该赛事的证书。 */
    private Boolean hasCert;
    /** 当前用户的证书图片 URL（如有）。 */
    private String certImageUrl;
    /** 当前用户在该赛事的总分（如有）。 */
    private java.math.BigDecimal totalScore;

    public Boolean getScorePublished() {
        return scorePublished;
    }

    public void setScorePublished(Boolean scorePublished) {
        this.scorePublished = scorePublished;
    }

    public Boolean getHasCert() {
        return hasCert;
    }

    public void setHasCert(Boolean hasCert) {
        this.hasCert = hasCert;
    }

    public String getCertImageUrl() {
        return certImageUrl;
    }

    public void setCertImageUrl(String certImageUrl) {
        this.certImageUrl = certImageUrl;
    }

    public java.math.BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(java.math.BigDecimal totalScore) {
        this.totalScore = totalScore;
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
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

    public Integer getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(Integer totalQuota) {
        this.totalQuota = totalQuota;
    }

    public Integer getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(Integer perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public Integer getAppointmentCapacity() {
        return appointmentCapacity;
    }

    public void setAppointmentCapacity(Integer appointmentCapacity) {
        this.appointmentCapacity = appointmentCapacity;
    }

    public String getWriteoffMode() {
        return writeoffMode;
    }

    public void setWriteoffMode(String writeoffMode) {
        this.writeoffMode = writeoffMode;
    }

    public String getWriteoffConfig() {
        return writeoffConfig;
    }

    public void setWriteoffConfig(String writeoffConfig) {
        this.writeoffConfig = writeoffConfig;
    }

    public Integer getNeedSignIn() {
        return needSignIn;
    }

    public void setNeedSignIn(Integer needSignIn) {
        this.needSignIn = needSignIn;
    }

    public Integer getTeamMinSize() {
        return teamMinSize;
    }

    public void setTeamMinSize(Integer teamMinSize) {
        this.teamMinSize = teamMinSize;
    }

    public Integer getTeamMaxSize() {
        return teamMaxSize;
    }

    public void setTeamMaxSize(Integer teamMaxSize) {
        this.teamMaxSize = teamMaxSize;
    }

    public String getTeamLeaderFields() {
        return teamLeaderFields;
    }

    public void setTeamLeaderFields(String teamLeaderFields) {
        this.teamLeaderFields = teamLeaderFields;
    }

    public String getTeamMemberFields() {
        return teamMemberFields;
    }

    public void setTeamMemberFields(String teamMemberFields) {
        this.teamMemberFields = teamMemberFields;
    }

    public Integer getAllowRepeatAppointment() {
        return allowRepeatAppointment;
    }

    public void setAllowRepeatAppointment(Integer allowRepeatAppointment) {
        this.allowRepeatAppointment = allowRepeatAppointment;
    }

    public Integer getOfflineReserveRemaining() {
        return offlineReserveRemaining;
    }

    public void setOfflineReserveRemaining(Integer offlineReserveRemaining) {
        this.offlineReserveRemaining = offlineReserveRemaining;
    }

    public Date getOfflineReserveDeadline() {
        return offlineReserveDeadline;
    }

    public void setOfflineReserveDeadline(Date offlineReserveDeadline) {
        this.offlineReserveDeadline = offlineReserveDeadline;
    }

    public String getScheduleJson() {
        return scheduleJson;
    }

    public void setScheduleJson(String scheduleJson) {
        this.scheduleJson = scheduleJson;
    }

    public String getAwardsJson() {
        return awardsJson;
    }

    public void setAwardsJson(String awardsJson) {
        this.awardsJson = awardsJson;
    }

    public String getFaqJson() {
        return faqJson;
    }

    public void setFaqJson(String faqJson) {
        this.faqJson = faqJson;
    }

    public String getRecommendTags() {
        return recommendTags;
    }

    public void setRecommendTags(String recommendTags) {
        this.recommendTags = recommendTags;
    }

    public String getCertRuleJson() {
        return certRuleJson;
    }

    public void setCertRuleJson(String certRuleJson) {
        this.certRuleJson = certRuleJson;
    }

    public String getCertIssueMode() {
        return certIssueMode;
    }

    public void setCertIssueMode(String certIssueMode) {
        this.certIssueMode = certIssueMode;
    }

    public Date getScorePublishTime() {
        return scorePublishTime;
    }

    public void setScorePublishTime(Date scorePublishTime) {
        this.scorePublishTime = scorePublishTime;
    }

    public String getScorePublishMode() {
        return scorePublishMode;
    }

    public void setScorePublishMode(String scorePublishMode) {
        this.scorePublishMode = scorePublishMode;
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

    public Date getAftersaleDeadline() {
        return aftersaleDeadline;
    }

    public void setAftersaleDeadline(Date aftersaleDeadline) {
        this.aftersaleDeadline = aftersaleDeadline;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getCoOrganizer() {
        return coOrganizer;
    }

    public void setCoOrganizer(String coOrganizer) {
        this.coOrganizer = coOrganizer;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getOrganizerLogo() { return organizerLogo; }
    public void setOrganizerLogo(String organizerLogo) { this.organizerLogo = organizerLogo; }

    public String getOrganizerDesc() { return organizerDesc; }
    public void setOrganizerDesc(String organizerDesc) { this.organizerDesc = organizerDesc; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactWechat() { return contactWechat; }
    public void setContactWechat(String contactWechat) { this.contactWechat = contactWechat; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public List<JstContestSchedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<JstContestSchedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<JstContestAward> getAwardList() {
        return awardList;
    }

    public void setAwardList(List<JstContestAward> awardList) {
        this.awardList = awardList;
    }

    public List<JstContestFaq> getFaqList() {
        return faqList;
    }

    public void setFaqList(List<JstContestFaq> faqList) {
        this.faqList = faqList;
    }

    public List<JstScoreItem> getScoreItemList() {
        return scoreItemList;
    }

    public void setScoreItemList(List<JstScoreItem> scoreItemList) {
        this.scoreItemList = scoreItemList;
    }

    public List<JstAppointmentSlot> getAppointmentSlotList() {
        return appointmentSlotList;
    }

    public void setAppointmentSlotList(List<JstAppointmentSlot> appointmentSlotList) {
        this.appointmentSlotList = appointmentSlotList;
    }
}
