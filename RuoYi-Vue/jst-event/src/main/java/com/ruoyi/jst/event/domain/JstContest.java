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

    /** 预约容量 */
    @Excel(name = "预约容量")
    private Integer appointmentCapacity = 0;

    /** 核销子项配置JSON */
    @Excel(name = "核销子项配置JSON")
    private String writeoffConfig;

    /** 是否允许重复预约：0否 1是 */
    @Excel(name = "是否允许重复预约：0否 1是")
    private Integer allowRepeatAppointment = 0;

    /** 是否允许预约退款（Q-07 配置化）：0=不允许（默认），1=允许 */
    @Excel(name = "是否允许预约退款：0否 1是")
    private Integer allowAppointmentRefund;

    /** 证书发放规则JSON */
    @Excel(name = "证书发放规则JSON")
    private String certRuleJson;

    /** 成绩规则JSON */
    @Excel(name = "成绩规则JSON")
    private String scoreRuleJson;

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

    public void setAppointmentCapacity(Integer appointmentCapacity)
    {
        this.appointmentCapacity = appointmentCapacity;
    }

    public Integer getAppointmentCapacity()
    {
        return appointmentCapacity;
    }

    public void setWriteoffConfig(String writeoffConfig)
    {
        this.writeoffConfig = writeoffConfig;
    }

    public String getWriteoffConfig()
    {
        return writeoffConfig;
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

    public void setScoreRuleJson(String scoreRuleJson) 
    {
        this.scoreRuleJson = scoreRuleJson;
    }

    public String getScoreRuleJson() 
    {
        return scoreRuleJson;
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
            .append("description", getDescription())
            .append("enrollStartTime", getEnrollStartTime())
            .append("enrollEndTime", getEnrollEndTime())
            .append("eventStartTime", getEventStartTime())
            .append("eventEndTime", getEventEndTime())
            .append("price", getPrice())
            .append("supportChannelEnroll", getSupportChannelEnroll())
            .append("supportPointsDeduct", getSupportPointsDeduct())
            .append("supportAppointment", getSupportAppointment())
            .append("appointmentCapacity", getAppointmentCapacity())
            .append("writeoffConfig", getWriteoffConfig())
            .append("allowRepeatAppointment", getAllowRepeatAppointment())
            .append("allowAppointmentRefund", getAllowAppointmentRefund())
            .append("certRuleJson", getCertRuleJson())
            .append("scoreRuleJson", getScoreRuleJson())
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
