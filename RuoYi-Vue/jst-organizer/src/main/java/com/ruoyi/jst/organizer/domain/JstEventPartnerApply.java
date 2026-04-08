package com.ruoyi.jst.organizer.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事方入驻申请对象 jst_event_partner_apply
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstEventPartnerApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    private Long applyId;

    /** 申请单号 */
    @Excel(name = "申请单号")
    private String applyNo;

    /** 通过后生成的赛事方ID（FK→jst_event_partner） */
    @Excel(name = "通过后生成的赛事方ID", readConverterExp = "F=K→jst_event_partner")
    private Long partnerId;

    /** 申请名称 */
    @Excel(name = "申请名称")
    private String partnerName;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactMobile;

    /** 营业执照号 */
    @Excel(name = "营业执照号")
    private String businessLicenseNo;

    /** 资质材料JSON */
    @Excel(name = "资质材料JSON")
    private String qualificationJson;

    /** 结算信息JSON */
    @Excel(name = "结算信息JSON")
    private String settlementInfoJson;

    /** 发票信息JSON */
    @Excel(name = "发票信息JSON")
    private String invoiceInfoJson;

    /** 合同附件JSON */
    @Excel(name = "合同附件JSON")
    private String contractFilesJson;

    /** 状态：draft/pending/approved/rejected/supplement */
    @Excel(name = "状态：draft/pending/approved/rejected/supplement")
    private String applyStatus;

    /** 补充材料说明 */
    @Excel(name = "补充材料说明")
    private String supplementRemark;

    /** 审核意见 */
    @Excel(name = "审核意见")
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

    public void setPartnerId(Long partnerId) 
    {
        this.partnerId = partnerId;
    }

    public Long getPartnerId() 
    {
        return partnerId;
    }

    public void setPartnerName(String partnerName) 
    {
        this.partnerName = partnerName;
    }

    public String getPartnerName() 
    {
        return partnerName;
    }

    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactName() 
    {
        return contactName;
    }

    public void setContactMobile(String contactMobile) 
    {
        this.contactMobile = contactMobile;
    }

    public String getContactMobile() 
    {
        return contactMobile;
    }

    public void setBusinessLicenseNo(String businessLicenseNo) 
    {
        this.businessLicenseNo = businessLicenseNo;
    }

    public String getBusinessLicenseNo() 
    {
        return businessLicenseNo;
    }

    public void setQualificationJson(String qualificationJson) 
    {
        this.qualificationJson = qualificationJson;
    }

    public String getQualificationJson() 
    {
        return qualificationJson;
    }

    public void setSettlementInfoJson(String settlementInfoJson) 
    {
        this.settlementInfoJson = settlementInfoJson;
    }

    public String getSettlementInfoJson() 
    {
        return settlementInfoJson;
    }

    public void setInvoiceInfoJson(String invoiceInfoJson) 
    {
        this.invoiceInfoJson = invoiceInfoJson;
    }

    public String getInvoiceInfoJson() 
    {
        return invoiceInfoJson;
    }

    public void setContractFilesJson(String contractFilesJson) 
    {
        this.contractFilesJson = contractFilesJson;
    }

    public String getContractFilesJson() 
    {
        return contractFilesJson;
    }

    public void setApplyStatus(String applyStatus) 
    {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatus() 
    {
        return applyStatus;
    }

    public void setSupplementRemark(String supplementRemark) 
    {
        this.supplementRemark = supplementRemark;
    }

    public String getSupplementRemark() 
    {
        return supplementRemark;
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
            .append("partnerId", getPartnerId())
            .append("partnerName", getPartnerName())
            .append("contactName", getContactName())
            .append("contactMobile", getContactMobile())
            .append("businessLicenseNo", getBusinessLicenseNo())
            .append("qualificationJson", getQualificationJson())
            .append("settlementInfoJson", getSettlementInfoJson())
            .append("invoiceInfoJson", getInvoiceInfoJson())
            .append("contractFilesJson", getContractFilesJson())
            .append("applyStatus", getApplyStatus())
            .append("supplementRemark", getSupplementRemark())
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
