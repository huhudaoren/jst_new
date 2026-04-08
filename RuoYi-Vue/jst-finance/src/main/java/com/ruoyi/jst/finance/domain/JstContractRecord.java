package com.ruoyi.jst.finance.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 合同记录对象 jst_contract_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstContractRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 合同ID */
    private Long contractId;

    /** 合同编号 */
    @Excel(name = "合同编号")
    private String contractNo;

    /** 合同类型：partner_coop赛事方合作/channel_settle渠道结算/supplement补充 */
    @Excel(name = "合同类型：partner_coop赛事方合作/channel_settle渠道结算/supplement补充")
    private String contractType;

    /** 对象类型：partner/channel */
    @Excel(name = "对象类型：partner/channel")
    private String targetType;

    /** 对象ID */
    @Excel(name = "对象ID")
    private Long targetId;

    /** 合同模板ID */
    @Excel(name = "合同模板ID")
    private Long templateId;

    /** 合同文件URL */
    @Excel(name = "合同文件URL")
    private String fileUrl;

    /** 合同状态：draft/pending_sign/signed/expired/archived */
    @Excel(name = "合同状态：draft/pending_sign/signed/expired/archived")
    private String status;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveTime;

    /** 签署时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签署时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date signTime;

    /** 关联结算单ID */
    @Excel(name = "关联结算单ID")
    private Long refSettlementId;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setContractId(Long contractId) 
    {
        this.contractId = contractId;
    }

    public Long getContractId() 
    {
        return contractId;
    }

    public void setContractNo(String contractNo) 
    {
        this.contractNo = contractNo;
    }

    public String getContractNo() 
    {
        return contractNo;
    }

    public void setContractType(String contractType) 
    {
        this.contractType = contractType;
    }

    public String getContractType() 
    {
        return contractType;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetId(Long targetId) 
    {
        this.targetId = targetId;
    }

    public Long getTargetId() 
    {
        return targetId;
    }

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
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

    public void setSignTime(Date signTime) 
    {
        this.signTime = signTime;
    }

    public Date getSignTime() 
    {
        return signTime;
    }

    public void setRefSettlementId(Long refSettlementId) 
    {
        this.refSettlementId = refSettlementId;
    }

    public Long getRefSettlementId() 
    {
        return refSettlementId;
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
            .append("contractId", getContractId())
            .append("contractNo", getContractNo())
            .append("contractType", getContractType())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("templateId", getTemplateId())
            .append("fileUrl", getFileUrl())
            .append("status", getStatus())
            .append("effectiveTime", getEffectiveTime())
            .append("signTime", getSignTime())
            .append("refSettlementId", getRefSettlementId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
