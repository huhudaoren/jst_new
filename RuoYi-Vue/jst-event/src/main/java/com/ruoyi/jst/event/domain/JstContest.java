package com.ruoyi.jst.event.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事主对象 jst_contest
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstContest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 赛事ID */
    private Long contestId;

    /** 赛事名称 */
    @Excel(name = "赛事名称")
    private String contestName;

    /** 赛事来源：platform平台自营/partner赛事方 */
    @Excel(name = "赛事来源：platform平台自营/partner赛事方")
    private String sourceType;

    /** 所属赛事方ID，FK→jst_event_partner */
    @Excel(name = "所属赛事方ID，FK→jst_event_partner")
    private Long partnerId;

    /** 分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育 */
    @Excel(name = "分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育")
    private String category;

    /** 参赛组别多选（逗号分隔） */
    @Excel(name = "参赛组别多选", readConverterExp = "逗=号分隔")
    private String groupLevels;

    /** 封面图URL */
    @Excel(name = "封面图URL")
    private String coverImage;

    /** Banner大图URL */
    @Excel(name = "Banner大图URL")
    private String bannerImage;

    /** 赛事介绍HTML/富文本 */
    @Excel(name = "赛事介绍HTML/富文本")
    private String description;

    /** 报名开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报名开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date enrollStartTime;

    /** 报名结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报名结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date enrollEndTime;

    /** 比赛开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "比赛开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date eventStartTime;

    /** 比赛结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "比赛结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date eventEndTime;

    /** 报名标价（0=零元赛事） */
    @Excel(name = "报名标价", readConverterExp = "0==零元赛事")
    private BigDecimal price;

    /** 是否支持渠道代报名：0否 1是 */
    @Excel(name = "是否支持渠道代报名：0否 1是")
    private Integer supportChannelEnroll;

    /** 是否支持积分抵扣：0否 1是 */
    @Excel(name = "是否支持积分抵扣：0否 1是")
    private Integer supportPointsDeduct;

    /** 是否支持线下预约：0否 1是 */
    @Excel(name = "是否支持线下预约：0否 1是")
    private Integer supportAppointment;

    /** 总报名名额(0=不限) */
    @Excel(name = "总报名名额", readConverterExp = "0=不限")
    private Integer totalQuota;

    /** 单人限购数 */
    @Excel(name = "单人限购数")
    private Integer perUserLimit;

    /** 预约容量 */
    @Excel(name = "预约容量")
    private Integer appointmentCapacity = 0;

    /** 核销模式：qr/manual */
    @Excel(name = "核销模式：qr/manual")
    private String writeoffMode;

    /** 核销子项配置JSON */
    @Excel(name = "核销子项配置JSON")
    private String writeoffConfig;

    /** 是否需要签到：0否 1是 */
    @Excel(name = "是否需要签到：0否 1是")
    private Integer needSignIn;

    /** 团队最小人数 */
    @Excel(name = "团队最小人数")
    private Integer teamMinSize;

    /** 团队最大人数 */
    @Excel(name = "团队最大人数")
    private Integer teamMaxSize;

    /** 队长需填字段 */
    @Excel(name = "队长需填字段")
    private String teamLeaderFields;

    /** 队员需填字段 */
    @Excel(name = "队员需填字段")
    private String teamMemberFields;

    /** 是否允许重复预约：0否 1是 */
    @Excel(name = "是否允许重复预约：0否 1是")
    private Integer allowRepeatAppointment = 0;

    /** 是否允许预约退款（Q-07 配置化）：0=不允许（默认），1=允许 */
    @Excel(name = "是否允许预约退款：0否 1是")
    private Integer allowAppointmentRefund;

    /** 证书发放规则JSON */
    @Excel(name = "证书发放规则JSON")
    private String certRuleJson;

    /** 证书颁发模式：manual/auto */
    @Excel(name = "证书颁发模式：manual/auto")
    private String certIssueMode;

    /** 成绩发布时间(auto模式用) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "成绩发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date scorePublishTime;

    /** 成绩发布模式：manual/auto */
    @Excel(name = "成绩发布模式：manual/auto")
    private String scorePublishMode;

    /** 成绩规则JSON */
    @Deprecated
    @Excel(name = "成绩规则JSON")
    private String scoreRuleJson;

    /** 售后截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "售后截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date aftersaleDeadline;

    /** 主办方 */
    @Excel(name = "主办方")
    private String organizer;

    /** 协办方 */
    @Excel(name = "协办方")
    private String coOrganizer;

    /** 比赛地址 */
    @Excel(name = "比赛地址")
    private String eventAddress;

    /** 主办方 LOGO */
    @Excel(name = "主办方LOGO")
    private String organizerLogo;

    /** 主办方简介 */
    @Excel(name = "主办方简介")
    private String organizerDesc;

    /** 咨询电话 */
    @Excel(name = "咨询电话")
    private String contactPhone;

    /** 咨询微信 */
    @Excel(name = "咨询微信")
    private String contactWechat;

    /** 咨询邮箱 */
    @Excel(name = "咨询邮箱")
    private String contactEmail;

    /** 赛程JSON */
    @Deprecated
    @Excel(name = "赛程JSON")
    private String scheduleJson;

    /** 奖项JSON */
    @Deprecated
    @Excel(name = "奖项JSON")
    private String awardsJson;

    /** 常见问题JSON */
    @Deprecated
    @Excel(name = "常见问题JSON")
    private String faqJson;

    /** 推荐标签 */
    @Excel(name = "推荐标签")
    private String recommendTags;

    /** 默认报名表单模板ID，FK→jst_enroll_form_template */
    @Excel(name = "默认报名表单模板ID，FK→jst_enroll_form_template")
    private Long formTemplateId;

    /** 售后宽限天数 */
    @Excel(name = "售后宽限天数")
    private Long aftersaleDays;

    /** 审核状态：draft/pending/approved/rejected/online/offline */
    @Excel(name = "审核状态：draft/pending/approved/rejected/online/offline")
    private String auditStatus;

    /** 业务状态：not_started/enrolling/closed/scoring/published/ended */
    @Excel(name = "业务状态：not_started/enrolling/closed/scoring/published/ended")
    private String status;

    /** 创建人用户ID */
    @Excel(name = "创建人用户ID")
    private Long createdUserId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setContestName(String contestName) 
    {
        this.contestName = contestName;
    }

    public String getContestName() 
    {
        return contestName;
    }

    public void setSourceType(String sourceType) 
    {
        this.sourceType = sourceType;
    }

    public String getSourceType() 
    {
        return sourceType;
    }

    public void setPartnerId(Long partnerId) 
    {
        this.partnerId = partnerId;
    }

    public Long getPartnerId() 
    {
        return partnerId;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setGroupLevels(String groupLevels) 
    {
        this.groupLevels = groupLevels;
    }

    public String getGroupLevels() 
    {
        return groupLevels;
    }

    public void setCoverImage(String coverImage) 
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage() 
    {
        return coverImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setEnrollStartTime(Date enrollStartTime) 
    {
        this.enrollStartTime = enrollStartTime;
    }

    public Date getEnrollStartTime() 
    {
        return enrollStartTime;
    }

    public void setEnrollEndTime(Date enrollEndTime) 
    {
        this.enrollEndTime = enrollEndTime;
    }

    public Date getEnrollEndTime() 
    {
        return enrollEndTime;
    }

    public void setEventStartTime(Date eventStartTime) 
    {
        this.eventStartTime = eventStartTime;
    }

    public Date getEventStartTime() 
    {
        return eventStartTime;
    }

    public void setEventEndTime(Date eventEndTime) 
    {
        this.eventEndTime = eventEndTime;
    }

    public Date getEventEndTime() 
    {
        return eventEndTime;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setSupportChannelEnroll(Integer supportChannelEnroll) 
    {
        this.supportChannelEnroll = supportChannelEnroll;
    }

    public Integer getSupportChannelEnroll() 
    {
        return supportChannelEnroll;
    }

    public void setSupportPointsDeduct(Integer supportPointsDeduct) 
    {
        this.supportPointsDeduct = supportPointsDeduct;
    }

    public Integer getSupportPointsDeduct() 
    {
        return supportPointsDeduct;
    }

    public void setSupportAppointment(Integer supportAppointment) 
    {
        this.supportAppointment = supportAppointment;
    }

    public Integer getSupportAppointment() 
    {
        return supportAppointment;
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

    public void setAppointmentCapacity(Integer appointmentCapacity)
    {
        this.appointmentCapacity = appointmentCapacity;
    }

    public Integer getAppointmentCapacity()
    {
        return appointmentCapacity;
    }

    public String getWriteoffMode() {
        return writeoffMode;
    }

    public void setWriteoffMode(String writeoffMode) {
        this.writeoffMode = writeoffMode;
    }

    public void setWriteoffConfig(String writeoffConfig)
    {
        this.writeoffConfig = writeoffConfig;
    }

    public String getWriteoffConfig()
    {
        return writeoffConfig;
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

    public void setAllowRepeatAppointment(Integer allowRepeatAppointment)
    {
        this.allowRepeatAppointment = allowRepeatAppointment;
    }

    public Integer getAllowRepeatAppointment()
    {
        return allowRepeatAppointment;
    }

    public Integer getAllowAppointmentRefund() {
        return allowAppointmentRefund;
    }

    public void setAllowAppointmentRefund(Integer allowAppointmentRefund) {
        this.allowAppointmentRefund = allowAppointmentRefund;
    }

    public void setCertRuleJson(String certRuleJson)
    {
        this.certRuleJson = certRuleJson;
    }

    public String getCertRuleJson() 
    {
        return certRuleJson;
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

    public void setScoreRuleJson(String scoreRuleJson) 
    {
        this.scoreRuleJson = scoreRuleJson;
    }

    public String getScoreRuleJson() 
    {
        return scoreRuleJson;
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

    public void setScheduleJson(String scheduleJson)
    {
        this.scheduleJson = scheduleJson;
    }

    public String getScheduleJson()
    {
        return scheduleJson;
    }

    public void setAwardsJson(String awardsJson)
    {
        this.awardsJson = awardsJson;
    }

    public String getAwardsJson()
    {
        return awardsJson;
    }

    public void setFaqJson(String faqJson)
    {
        this.faqJson = faqJson;
    }

    public String getFaqJson()
    {
        return faqJson;
    }

    public void setRecommendTags(String recommendTags)
    {
        this.recommendTags = recommendTags;
    }

    public String getRecommendTags()
    {
        return recommendTags;
    }

    public void setFormTemplateId(Long formTemplateId) 
    {
        this.formTemplateId = formTemplateId;
    }

    public Long getFormTemplateId() 
    {
        return formTemplateId;
    }

    public void setAftersaleDays(Long aftersaleDays) 
    {
        this.aftersaleDays = aftersaleDays;
    }

    public Long getAftersaleDays() 
    {
        return aftersaleDays;
    }

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setCreatedUserId(Long createdUserId) 
    {
        this.createdUserId = createdUserId;
    }

    public Long getCreatedUserId() 
    {
        return createdUserId;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("contestId", getContestId())
            .append("contestName", getContestName())
            .append("sourceType", getSourceType())
            .append("partnerId", getPartnerId())
            .append("category", getCategory())
            .append("groupLevels", getGroupLevels())
            .append("coverImage", getCoverImage())
            .append("bannerImage", getBannerImage())
            .append("description", getDescription())
            .append("enrollStartTime", getEnrollStartTime())
            .append("enrollEndTime", getEnrollEndTime())
            .append("eventStartTime", getEventStartTime())
            .append("eventEndTime", getEventEndTime())
            .append("price", getPrice())
            .append("supportChannelEnroll", getSupportChannelEnroll())
            .append("supportPointsDeduct", getSupportPointsDeduct())
            .append("supportAppointment", getSupportAppointment())
            .append("totalQuota", getTotalQuota())
            .append("perUserLimit", getPerUserLimit())
            .append("appointmentCapacity", getAppointmentCapacity())
            .append("writeoffMode", getWriteoffMode())
            .append("writeoffConfig", getWriteoffConfig())
            .append("needSignIn", getNeedSignIn())
            .append("teamMinSize", getTeamMinSize())
            .append("teamMaxSize", getTeamMaxSize())
            .append("teamLeaderFields", getTeamLeaderFields())
            .append("teamMemberFields", getTeamMemberFields())
            .append("allowRepeatAppointment", getAllowRepeatAppointment())
            .append("allowAppointmentRefund", getAllowAppointmentRefund())
            .append("certRuleJson", getCertRuleJson())
            .append("certIssueMode", getCertIssueMode())
            .append("scorePublishTime", getScorePublishTime())
            .append("scorePublishMode", getScorePublishMode())
            .append("scoreRuleJson", getScoreRuleJson())
            .append("aftersaleDeadline", getAftersaleDeadline())
            .append("organizer", getOrganizer())
            .append("coOrganizer", getCoOrganizer())
            .append("eventAddress", getEventAddress())
            .append("organizerLogo", getOrganizerLogo())
            .append("organizerDesc", getOrganizerDesc())
            .append("contactPhone", getContactPhone())
            .append("contactWechat", getContactWechat())
            .append("contactEmail", getContactEmail())
            .append("scheduleJson", getScheduleJson())
            .append("awardsJson", getAwardsJson())
            .append("faqJson", getFaqJson())
            .append("recommendTags", getRecommendTags())
            .append("formTemplateId", getFormTemplateId())
            .append("aftersaleDays", getAftersaleDays())
            .append("auditStatus", getAuditStatus())
            .append("status", getStatus())
            .append("createdUserId", getCreatedUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
