package com.ruoyi.jst.event.dto;

import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import com.ruoyi.jst.event.domain.JstContestAward;
import com.ruoyi.jst.event.domain.JstContestFaq;
import com.ruoyi.jst.event.domain.JstContestSchedule;
import com.ruoyi.jst.event.domain.JstScoreItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 赛事新增/编辑请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ContestSaveReqDTO {

    private Long contestId;

    private Long partnerId;

    @NotBlank(message = "赛事名称不能为空")
    @Size(max = 255, message = "赛事名称长度不能超过255")
    private String contestName;

    @NotBlank(message = "赛事分类不能为空")
    @Size(max = 32, message = "赛事分类长度不能超过32")
    private String category;

    @Size(max = 255, message = "参赛组别长度不能超过255")
    private String groupLevels;

    @Size(max = 255, message = "封面图地址长度不能超过255")
    private String coverImage;

    @Size(max = 500, message = "Banner地址长度不能超过500")
    private String bannerImage;

    private String description;

    @NotNull(message = "报名开始时间不能为空")
    private Date enrollStartTime;

    @NotNull(message = "报名结束时间不能为空")
    private Date enrollEndTime;

    @NotNull(message = "比赛开始时间不能为空")
    private Date eventStartTime;

    @NotNull(message = "比赛结束时间不能为空")
    private Date eventEndTime;

    @NotNull(message = "报名价格不能为空")
    @DecimalMin(value = "0.00", message = "报名价格不能小于0")
    private BigDecimal price;

    @NotNull(message = "是否支持渠道代报名不能为空")
    @Min(value = 0, message = "是否支持渠道代报名取值非法")
    @Max(value = 1, message = "是否支持渠道代报名取值非法")
    private Integer supportChannelEnroll;

    @NotNull(message = "是否支持积分抵扣不能为空")
    @Min(value = 0, message = "是否支持积分抵扣取值非法")
    @Max(value = 1, message = "是否支持积分抵扣取值非法")
    private Integer supportPointsDeduct;

    @NotNull(message = "是否支持线下预约不能为空")
    @Min(value = 0, message = "是否支持线下预约取值非法")
    @Max(value = 1, message = "是否支持线下预约取值非法")
    private Integer supportAppointment;

    @Min(value = 0, message = "总名额不能小于0")
    private Integer totalQuota = 0;

    @Min(value = 1, message = "单人限购数不能小于1")
    private Integer perUserLimit = 1;

    @Min(value = 0, message = "预约容量不能小于0")
    private Integer appointmentCapacity = 0;

    @Size(max = 20, message = "核销模式长度不能超过20")
    private String writeoffMode = "qr";

    private String writeoffConfig;

    @Min(value = 0, message = "是否需要签到取值非法")
    @Max(value = 1, message = "是否需要签到取值非法")
    private Integer needSignIn = 0;

    @Min(value = 0, message = "团队最小人数不能小于0")
    private Integer teamMinSize = 0;

    @Min(value = 0, message = "团队最大人数不能小于0")
    private Integer teamMaxSize = 0;

    @Size(max = 500, message = "队长字段配置长度不能超过500")
    private String teamLeaderFields;

    @Size(max = 500, message = "队员字段配置长度不能超过500")
    private String teamMemberFields;

    @Min(value = 0, message = "是否允许重复预约取值非法")
    @Max(value = 1, message = "是否允许重复预约取值非法")
    private Integer allowRepeatAppointment = 0;

    @Min(value = 0, message = "是否允许预约退款取值非法")
    @Max(value = 1, message = "是否允许预约退款取值非法")
    private Integer allowAppointmentRefund = 0;

    private String certRuleJson;

    @Size(max = 20, message = "证书颁发模式长度不能超过20")
    private String certIssueMode = "manual";

    private Date scorePublishTime;

    @Size(max = 20, message = "成绩发布模式长度不能超过20")
    private String scorePublishMode = "manual";

    @Deprecated
    private String scoreRuleJson;

    private Date aftersaleDeadline;

    @Size(max = 200, message = "主办方长度不能超过200")
    private String organizer;

    @Size(max = 200, message = "协办方长度不能超过200")
    private String coOrganizer;

    @Size(max = 500, message = "比赛地址长度不能超过500")
    private String eventAddress;

    @Deprecated
    private String scheduleJson;

    @Deprecated
    private String awardsJson;

    @Deprecated
    private String faqJson;

    @Size(max = 500, message = "推荐标签长度不能超过500")
    private String recommendTags;

    private List<JstContestSchedule> scheduleList;
    private List<JstContestAward> awardList;
    private List<JstContestFaq> faqList;
    private List<JstScoreItem> scoreItemList;
    private List<JstAppointmentSlot> appointmentSlotList;

    private Long formTemplateId;

    @NotNull(message = "售后宽限天数不能为空")
    @Min(value = 0, message = "售后宽限天数不能小于0")
    private Long aftersaleDays;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
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

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public Integer getAllowAppointmentRefund() {
        return allowAppointmentRefund;
    }

    public void setAllowAppointmentRefund(Integer allowAppointmentRefund) {
        this.allowAppointmentRefund = allowAppointmentRefund;
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

    public Long getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(Long formTemplateId) {
        this.formTemplateId = formTemplateId;
    }

    public Long getAftersaleDays() {
        return aftersaleDays;
    }

    public void setAftersaleDays(Long aftersaleDays) {
        this.aftersaleDays = aftersaleDays;
    }
}
