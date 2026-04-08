package com.ruoyi.jst.event.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报名动态单模板（schema_json定义字段）对象 jst_enroll_form_template
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstEnrollFormTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String templateName;

    /** 版本号（修改即递增） */
    @Excel(name = "版本号", readConverterExp = "修=改即递增")
    private Long templateVersion;

    /** 所属方：platform/partner */
    @Excel(name = "所属方：platform/partner")
    private String ownerType;

    /** 所属赛事方ID */
    @Excel(name = "所属赛事方ID")
    private Long ownerId;

    /** 字段定义JSON：[{key,type,label,required,validators,visibleIf,sort}] */
    @Excel(name = "字段定义JSON：[{key,type,label,required,validators,visibleIf,sort}]")
    private String schemaJson;

    /** 审核状态：draft/pending/approved/rejected */
    @Excel(name = "审核状态：draft/pending/approved/rejected")
    private String auditStatus;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
    }

    public void setTemplateVersion(Long templateVersion) 
    {
        this.templateVersion = templateVersion;
    }

    public Long getTemplateVersion() 
    {
        return templateVersion;
    }

    public void setOwnerType(String ownerType) 
    {
        this.ownerType = ownerType;
    }

    public String getOwnerType() 
    {
        return ownerType;
    }

    public void setOwnerId(Long ownerId) 
    {
        this.ownerId = ownerId;
    }

    public Long getOwnerId() 
    {
        return ownerId;
    }

    public void setSchemaJson(String schemaJson) 
    {
        this.schemaJson = schemaJson;
    }

    public String getSchemaJson() 
    {
        return schemaJson;
    }

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setEffectiveTime(Date effectiveTime) 
    {
        this.effectiveTime = effectiveTime;
    }

    public Date getEffectiveTime() 
    {
        return effectiveTime;
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
            .append("templateId", getTemplateId())
            .append("templateName", getTemplateName())
            .append("templateVersion", getTemplateVersion())
            .append("ownerType", getOwnerType())
            .append("ownerId", getOwnerId())
            .append("schemaJson", getSchemaJson())
            .append("auditStatus", getAuditStatus())
            .append("status", getStatus())
            .append("effectiveTime", getEffectiveTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
