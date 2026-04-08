package com.ruoyi.jst.marketing.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户权益持有对象 jst_user_rights
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstUserRights extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户权益ID */
    private Long userRightsId;

    /** 权益模板ID */
    @Excel(name = "权益模板ID")
    private Long rightsTemplateId;

    /** 持有方：student/channel */
    @Excel(name = "持有方：student/channel")
    private String ownerType;

    /** 持有方ID */
    @Excel(name = "持有方ID")
    private Long ownerId;

    /** 来源：level等级/campaign活动/manual手工 */
    @Excel(name = "来源：level等级/campaign活动/manual手工")
    private String sourceType;

    /** 来源单据ID */
    @Excel(name = "来源单据ID")
    private Long sourceRefId;

    /** 剩余额度 */
    @Excel(name = "剩余额度")
    private BigDecimal remainQuota;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validStart;

    /** 失效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date validEnd;

    /** 状态：available/locked/used/expired/revoked */
    @Excel(name = "状态：available/locked/used/expired/revoked")
    private String status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setUserRightsId(Long userRightsId) 
    {
        this.userRightsId = userRightsId;
    }

    public Long getUserRightsId() 
    {
        return userRightsId;
    }

    public void setRightsTemplateId(Long rightsTemplateId) 
    {
        this.rightsTemplateId = rightsTemplateId;
    }

    public Long getRightsTemplateId() 
    {
        return rightsTemplateId;
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

    public void setSourceType(String sourceType) 
    {
        this.sourceType = sourceType;
    }

    public String getSourceType() 
    {
        return sourceType;
    }

    public void setSourceRefId(Long sourceRefId) 
    {
        this.sourceRefId = sourceRefId;
    }

    public Long getSourceRefId() 
    {
        return sourceRefId;
    }

    public void setRemainQuota(BigDecimal remainQuota) 
    {
        this.remainQuota = remainQuota;
    }

    public BigDecimal getRemainQuota() 
    {
        return remainQuota;
    }

    public void setValidStart(Date validStart) 
    {
        this.validStart = validStart;
    }

    public Date getValidStart() 
    {
        return validStart;
    }

    public void setValidEnd(Date validEnd) 
    {
        this.validEnd = validEnd;
    }

    public Date getValidEnd() 
    {
        return validEnd;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
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
            .append("userRightsId", getUserRightsId())
            .append("rightsTemplateId", getRightsTemplateId())
            .append("ownerType", getOwnerType())
            .append("ownerId", getOwnerId())
            .append("sourceType", getSourceType())
            .append("sourceRefId", getSourceRefId())
            .append("remainQuota", getRemainQuota())
            .append("validStart", getValidStart())
            .append("validEnd", getValidEnd())
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
