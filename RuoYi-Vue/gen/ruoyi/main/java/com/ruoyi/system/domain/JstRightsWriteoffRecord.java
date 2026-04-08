package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 权益核销记录对象 jst_rights_writeoff_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstRightsWriteoffRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 核销记录ID */
    private Long recordId;

    /** 核销单号 */
    @Excel(name = "核销单号")
    private String writeoffNo;

    /** 用户权益ID */
    @Excel(name = "用户权益ID")
    private Long userRightsId;

    /** 使用额度 */
    @Excel(name = "使用额度")
    private BigDecimal useAmount;

    /** 申请说明 */
    @Excel(name = "申请说明")
    private String applyRemark;

    /** 状态：pending/approved/rejected/confirmed/rolled_back */
    @Excel(name = "状态：pending/approved/rejected/confirmed/rolled_back")
    private String status;

    /** 审核员 */
    @Excel(name = "审核员")
    private Long auditUserId;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 核销时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "核销时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date writeoffTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setWriteoffNo(String writeoffNo) 
    {
        this.writeoffNo = writeoffNo;
    }

    public String getWriteoffNo() 
    {
        return writeoffNo;
    }

    public void setUserRightsId(Long userRightsId) 
    {
        this.userRightsId = userRightsId;
    }

    public Long getUserRightsId() 
    {
        return userRightsId;
    }

    public void setUseAmount(BigDecimal useAmount) 
    {
        this.useAmount = useAmount;
    }

    public BigDecimal getUseAmount() 
    {
        return useAmount;
    }

    public void setApplyRemark(String applyRemark) 
    {
        this.applyRemark = applyRemark;
    }

    public String getApplyRemark() 
    {
        return applyRemark;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setAuditUserId(Long auditUserId) 
    {
        this.auditUserId = auditUserId;
    }

    public Long getAuditUserId() 
    {
        return auditUserId;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setWriteoffTime(Date writeoffTime) 
    {
        this.writeoffTime = writeoffTime;
    }

    public Date getWriteoffTime() 
    {
        return writeoffTime;
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
            .append("recordId", getRecordId())
            .append("writeoffNo", getWriteoffNo())
            .append("userRightsId", getUserRightsId())
            .append("useAmount", getUseAmount())
            .append("applyRemark", getApplyRemark())
            .append("status", getStatus())
            .append("auditUserId", getAuditUserId())
            .append("auditRemark", getAuditRemark())
            .append("writeoffTime", getWriteoffTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
