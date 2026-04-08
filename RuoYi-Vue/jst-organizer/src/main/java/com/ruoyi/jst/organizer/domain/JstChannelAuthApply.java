package com.ruoyi.jst.organizer.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 渠道认证申请对象 jst_channel_auth_apply
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstChannelAuthApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    private Long applyId;

    /** 申请单号 */
    @Excel(name = "申请单号")
    private String applyNo;

    /** 申请人用户ID */
    @Excel(name = "申请人用户ID")
    private Long userId;

    /** 通过后生成的渠道方ID */
    @Excel(name = "通过后生成的渠道方ID")
    private Long channelId;

    /** 渠道类型：teacher/organization/individual */
    @Excel(name = "渠道类型：teacher/organization/individual")
    private String channelType;

    /** 申请名称 */
    @Excel(name = "申请名称")
    private String applyName;

    /** 认证材料JSON */
    @Excel(name = "认证材料JSON")
    private String materialsJson;

    /** 状态：pending/approved/rejected/suspended */
    @Excel(name = "状态：pending/approved/rejected/suspended")
    private String applyStatus;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 审核员ID */
    @Excel(name = "审核员ID")
    private Long auditUserId;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setApplyId(Long applyId) 
    {
        this.applyId = applyId;
    }

    public Long getApplyId() 
    {
        return applyId;
    }

    public void setApplyNo(String applyNo) 
    {
        this.applyNo = applyNo;
    }

    public String getApplyNo() 
    {
        return applyNo;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setChannelType(String channelType) 
    {
        this.channelType = channelType;
    }

    public String getChannelType() 
    {
        return channelType;
    }

    public void setApplyName(String applyName) 
    {
        this.applyName = applyName;
    }

    public String getApplyName() 
    {
        return applyName;
    }

    public void setMaterialsJson(String materialsJson) 
    {
        this.materialsJson = materialsJson;
    }

    public String getMaterialsJson() 
    {
        return materialsJson;
    }

    public void setApplyStatus(String applyStatus) 
    {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatus() 
    {
        return applyStatus;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setAuditUserId(Long auditUserId) 
    {
        this.auditUserId = auditUserId;
    }

    public Long getAuditUserId() 
    {
        return auditUserId;
    }

    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }

    public void setAuditTime(Date auditTime) 
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() 
    {
        return auditTime;
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
            .append("applyId", getApplyId())
            .append("applyNo", getApplyNo())
            .append("userId", getUserId())
            .append("channelId", getChannelId())
            .append("channelType", getChannelType())
            .append("applyName", getApplyName())
            .append("materialsJson", getMaterialsJson())
            .append("applyStatus", getApplyStatus())
            .append("auditRemark", getAuditRemark())
            .append("auditUserId", getAuditUserId())
            .append("submitTime", getSubmitTime())
            .append("auditTime", getAuditTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
