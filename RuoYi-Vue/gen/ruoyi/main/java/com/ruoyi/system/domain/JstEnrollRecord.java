package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报名记录（含动态单快照）对象 jst_enroll_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstEnrollRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报名ID */
    private Long enrollId;

    /** 报名编号（业务可读） */
    @Excel(name = "报名编号", readConverterExp = "业=务可读")
    private String enrollNo;

    /** 赛事ID，FK→jst_contest */
    @Excel(name = "赛事ID，FK→jst_contest")
    private Long contestId;

    /** 学生用户ID（临时档案则为空） */
    @Excel(name = "学生用户ID", readConverterExp = "临=时档案则为空")
    private Long userId;

    /** 参赛人ID（含临时档案），FK→jst_participant */
    @Excel(name = "参赛人ID", readConverterExp = "含=临时档案")
    private Long participantId;

    /** 关联渠道方ID（支付时锁定） */
    @Excel(name = "关联渠道方ID", readConverterExp = "支=付时锁定")
    private Long channelId;

    /** 报名来源：self自助/channel_single渠道单条/channel_batch渠道批量 */
    @Excel(name = "报名来源：self自助/channel_single渠道单条/channel_batch渠道批量")
    private String enrollSource;

    /** 使用的表单模板ID */
    @Excel(name = "使用的表单模板ID")
    private Long templateId;

    /** 表单模板版本号（与快照一致） */
    @Excel(name = "表单模板版本号", readConverterExp = "与=快照一致")
    private Long templateVersion;

    /** 报名字段全量快照JSON：动态字段值+附件URL */
    @Excel(name = "报名字段全量快照JSON：动态字段值+附件URL")
    private String formSnapshotJson;

    /** 关联订单ID */
    @Excel(name = "关联订单ID")
    private Long orderId;

    /** 资料状态：draft/submitted/supplement */
    @Excel(name = "资料状态：draft/submitted/supplement")
    private String materialStatus;

    /** 审核状态：pending/approved/rejected/supplement */
    @Excel(name = "审核状态：pending/approved/rejected/supplement")
    private String auditStatus;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setEnrollId(Long enrollId) 
    {
        this.enrollId = enrollId;
    }

    public Long getEnrollId() 
    {
        return enrollId;
    }

    public void setEnrollNo(String enrollNo) 
    {
        this.enrollNo = enrollNo;
    }

    public String getEnrollNo() 
    {
        return enrollNo;
    }

    public void setContestId(Long contestId) 
    {
        this.contestId = contestId;
    }

    public Long getContestId() 
    {
        return contestId;
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

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setEnrollSource(String enrollSource) 
    {
        this.enrollSource = enrollSource;
    }

    public String getEnrollSource() 
    {
        return enrollSource;
    }

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setTemplateVersion(Long templateVersion) 
    {
        this.templateVersion = templateVersion;
    }

    public Long getTemplateVersion() 
    {
        return templateVersion;
    }

    public void setFormSnapshotJson(String formSnapshotJson) 
    {
        this.formSnapshotJson = formSnapshotJson;
    }

    public String getFormSnapshotJson() 
    {
        return formSnapshotJson;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setMaterialStatus(String materialStatus) 
    {
        this.materialStatus = materialStatus;
    }

    public String getMaterialStatus() 
    {
        return materialStatus;
    }

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
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
            .append("enrollId", getEnrollId())
            .append("enrollNo", getEnrollNo())
            .append("contestId", getContestId())
            .append("userId", getUserId())
            .append("participantId", getParticipantId())
            .append("channelId", getChannelId())
            .append("enrollSource", getEnrollSource())
            .append("templateId", getTemplateId())
            .append("templateVersion", getTemplateVersion())
            .append("formSnapshotJson", getFormSnapshotJson())
            .append("orderId", getOrderId())
            .append("materialStatus", getMaterialStatus())
            .append("auditStatus", getAuditStatus())
            .append("auditRemark", getAuditRemark())
            .append("submitTime", getSubmitTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
