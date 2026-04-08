package com.ruoyi.jst.marketing.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 权益模板对象 jst_rights_template
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRightsTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 权益模板ID */
    private Long rightsTemplateId;

    /** 权益名称 */
    @Excel(name = "权益名称")
    private String rightsName;

    /** 类型：enroll_deduct报名抵扣/venue_reduce场地减免/exclusive_course专属课程/cs_priority优先客服/custom自定义 */
    @Excel(name = "类型：enroll_deduct报名抵扣/venue_reduce场地减免/exclusive_course专属课程/cs_priority优先客服/custom自定义")
    private String rightsType;

    /** 口径：times次数/amount金额/period周期 */
    @Excel(name = "口径：times次数/amount金额/period周期")
    private String quotaMode;

    /** 数值 */
    @Excel(name = "数值")
    private BigDecimal quotaValue;

    /** 有效期天数 */
    @Excel(name = "有效期天数")
    private Long validDays;

    /** 核销方式：online_auto/manual_review/offline_confirm */
    @Excel(name = "核销方式：online_auto/manual_review/offline_confirm")
    private String writeoffMode;

    /** 角色：student/channel/both */
    @Excel(name = "角色：student/channel/both")
    private String applicableRole;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setRightsTemplateId(Long rightsTemplateId) 
    {
        this.rightsTemplateId = rightsTemplateId;
    }

    public Long getRightsTemplateId() 
    {
        return rightsTemplateId;
    }

    public void setRightsName(String rightsName) 
    {
        this.rightsName = rightsName;
    }

    public String getRightsName() 
    {
        return rightsName;
    }

    public void setRightsType(String rightsType) 
    {
        this.rightsType = rightsType;
    }

    public String getRightsType() 
    {
        return rightsType;
    }

    public void setQuotaMode(String quotaMode) 
    {
        this.quotaMode = quotaMode;
    }

    public String getQuotaMode() 
    {
        return quotaMode;
    }

    public void setQuotaValue(BigDecimal quotaValue) 
    {
        this.quotaValue = quotaValue;
    }

    public BigDecimal getQuotaValue() 
    {
        return quotaValue;
    }

    public void setValidDays(Long validDays) 
    {
        this.validDays = validDays;
    }

    public Long getValidDays() 
    {
        return validDays;
    }

    public void setWriteoffMode(String writeoffMode) 
    {
        this.writeoffMode = writeoffMode;
    }

    public String getWriteoffMode() 
    {
        return writeoffMode;
    }

    public void setApplicableRole(String applicableRole) 
    {
        this.applicableRole = applicableRole;
    }

    public String getApplicableRole() 
    {
        return applicableRole;
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
            .append("rightsTemplateId", getRightsTemplateId())
            .append("rightsName", getRightsName())
            .append("rightsType", getRightsType())
            .append("quotaMode", getQuotaMode())
            .append("quotaValue", getQuotaValue())
            .append("validDays", getValidDays())
            .append("writeoffMode", getWriteoffMode())
            .append("applicableRole", getApplicableRole())
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
