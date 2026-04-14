package com.ruoyi.jst.event.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 证书记录对象 jst_cert_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstCertRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 证书ID */
    private Long certId;

    /** 证书编号，格式 JST-YYYY-ART-XXXXX */
    @Excel(name = "证书编号，格式 JST-YYYY-ART-XXXXX")
    private String certNo;

    /** 赛事ID */
    @Excel(name = "赛事ID")
    private Long contestId;

    /** 关联成绩ID */
    @Excel(name = "关联成绩ID")
    private Long scoreId;

    /** 报名ID */
    @Excel(name = "报名ID")
    private Long enrollId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 参赛人ID */
    @Excel(name = "参赛人ID")
    private Long participantId;

    /** 证书模板ID */
    @Excel(name = "证书模板ID")
    private Long templateId;

    /** 证书文件URL */
    @Excel(name = "证书文件URL")
    private String certFileUrl;

    /** 发放状态：pending/issued/voided */
    @Excel(name = "发放状态：pending/issued/voided")
    private String issueStatus;

    /** 发放时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发放时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date issueTime;

    /** 作废原因 */
    @Excel(name = "作废原因")
    private String voidReason;

    /** 公开校验码 */
    @Excel(name = "公开校验码")
    private String verifyCode;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 赛事名称（JOIN查出，非持久化） */
    private String contestName;

    /** 参赛人姓名（JOIN查出，非持久化） */
    private String participantName;

    /** 模板名称（JOIN查出，非持久化） */
    private String templateName;

    public void setCertId(Long certId) 
    {
        this.certId = certId;
    }

    public Long getCertId() 
    {
        return certId;
    }

    public void setCertNo(String certNo) 
    {
        this.certNo = certNo;
    }

    public String getCertNo() 
    {
        return certNo;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
    }

    public void setScoreId(Long scoreId) 
    {
        this.scoreId = scoreId;
    }

    public Long getScoreId() 
    {
        return scoreId;
    }

    public void setEnrollId(Long enrollId) 
    {
        this.enrollId = enrollId;
    }

    public Long getEnrollId() 
    {
        return enrollId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParticipantId(Long participantId) 
    {
        this.participantId = participantId;
    }

    public Long getParticipantId() 
    {
        return participantId;
    }

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setCertFileUrl(String certFileUrl) 
    {
        this.certFileUrl = certFileUrl;
    }

    public String getCertFileUrl() 
    {
        return certFileUrl;
    }

    public void setIssueStatus(String issueStatus) 
    {
        this.issueStatus = issueStatus;
    }

    public String getIssueStatus() 
    {
        return issueStatus;
    }

    public void setIssueTime(Date issueTime) 
    {
        this.issueTime = issueTime;
    }

    public Date getIssueTime() 
    {
        return issueTime;
    }

    public void setVoidReason(String voidReason) 
    {
        this.voidReason = voidReason;
    }

    public String getVoidReason() 
    {
        return voidReason;
    }

    public void setVerifyCode(String verifyCode) 
    {
        this.verifyCode = verifyCode;
    }

    public String getVerifyCode() 
    {
        return verifyCode;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setContestName(String contestName)
    {
        this.contestName = contestName;
    }

    public String getContestName()
    {
        return contestName;
    }

    public void setParticipantName(String participantName)
    {
        this.participantName = participantName;
    }

    public String getParticipantName()
    {
        return participantName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("certId", getCertId())
            .append("certNo", getCertNo())
            .append("contestId", getContestId())
            .append("scoreId", getScoreId())
            .append("enrollId", getEnrollId())
            .append("userId", getUserId())
            .append("participantId", getParticipantId())
            .append("templateId", getTemplateId())
            .append("certFileUrl", getCertFileUrl())
            .append("issueStatus", getIssueStatus())
            .append("issueTime", getIssueTime())
            .append("voidReason", getVoidReason())
            .append("verifyCode", getVerifyCode())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
