package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

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

    @Min(value = 0, message = "预约容量不能小于0")
    private Integer appointmentCapacity;

    private String writeoffConfig;

    @Min(value = 0, message = "是否允许重复预约取值非法")
    @Max(value = 1, message = "是否允许重复预约取值非法")
    private Integer allowRepeatAppointment;

    private String certRuleJson;

    private String scoreRuleJson;

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

    public Integer getAppointmentCapacity() {
        return appointmentCapacity;
    }

    public void setAppointmentCapacity(Integer appointmentCapacity) {
        this.appointmentCapacity = appointmentCapacity;
    }

    public String getWriteoffConfig() {
        return writeoffConfig;
    }

    public void setWriteoffConfig(String writeoffConfig) {
        this.writeoffConfig = writeoffConfig;
    }

    public Integer getAllowRepeatAppointment() {
        return allowRepeatAppointment;
    }

    public void setAllowRepeatAppointment(Integer allowRepeatAppointment) {
        this.allowRepeatAppointment = allowRepeatAppointment;
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
