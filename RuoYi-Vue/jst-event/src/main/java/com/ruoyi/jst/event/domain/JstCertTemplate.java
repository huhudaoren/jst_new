package com.ruoyi.jst.event.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 证书模板对象 jst_cert_template
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstCertTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String templateName;

    /** 所属：platform/partner */
    @Excel(name = "所属：platform/partner")
    private String ownerType;

    /** 所属赛事方ID */
    @Excel(name = "所属赛事方ID")
    private Long ownerId;

    /** 底图URL */
    @Excel(name = "底图URL")
    private String bgImage;

    /** 布局/字段配置JSON */
    @Excel(name = "布局/字段配置JSON")
    private String layoutJson;

    /** 审核状态：pending/approved/rejected */
    @Excel(name = "审核状态：pending/approved/rejected")
    private String auditStatus;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

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

    public void setBgImage(String bgImage) 
    {
        this.bgImage = bgImage;
    }

    public String getBgImage() 
    {
        return bgImage;
    }

    public void setLayoutJson(String layoutJson) 
    {
        this.layoutJson = layoutJson;
    }

    public String getLayoutJson() 
    {
        return layoutJson;
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
            .append("ownerType", getOwnerType())
            .append("ownerId", getOwnerId())
            .append("bgImage", getBgImage())
            .append("layoutJson", getLayoutJson())
            .append("auditStatus", getAuditStatus())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
