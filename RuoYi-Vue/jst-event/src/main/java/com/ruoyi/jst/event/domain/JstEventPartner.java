package com.ruoyi.jst.event.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事方档案对象 jst_event_partner
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstEventPartner extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 赛事方ID */
    private Long partnerId;

    /** 关联sys_user.user_id */
    @Excel(name = "关联用户ID")
    private Long userId;

    /** 赛事方名称 */
    @Excel(name = "赛事方名称")
    private String partnerName;

    /** 联系人姓名 */
    @Excel(name = "联系人姓名")
    private String contactName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactMobile;

    /** 营业执照号 */
    @Excel(name = "营业执照号")
    private String businessLicenseNo;

    /** 资质材料附件JSON */
    @Excel(name = "资质材料附件JSON")
    private String qualificationJson;

    /** 结算信息JSON（户名/账号/开户行） */
    @Excel(name = "结算信息JSON", readConverterExp = "户=名/账号/开户行")
    private String settlementInfoJson;

    /** 发票信息JSON */
    @Excel(name = "发票信息JSON")
    private String invoiceInfoJson;

    /** 合同附件JSON */
    @Excel(name = "合同附件JSON")
    private String contractFilesJson;

    /** 入驻审核状态：draft/pending/approved/rejected/supplement */
    @Excel(name = "入驻审核状态：draft/pending/approved/rejected/supplement")
    private String auditStatus;

    /** 审核通过时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核通过时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 账号启停：0停用 1启用 */
    @Excel(name = "账号启停：0停用 1启用")
    private Integer accountStatus;

    /** 合作状态：active/expired/terminated */
    @Excel(name = "合作状态：active/expired/terminated")
    private String coopStatus;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setPartnerId(Long partnerId) 
    {
        this.partnerId = partnerId;
    }

    public Long getPartnerId() 
    {
        return partnerId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
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

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setAuditTime(Date auditTime) 
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() 
    {
        return auditTime;
    }

    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }

    public void setAccountStatus(Integer accountStatus) 
    {
        this.accountStatus = accountStatus;
    }

    public Integer getAccountStatus() 
    {
        return accountStatus;
    }

    public void setCoopStatus(String coopStatus) 
    {
        this.coopStatus = coopStatus;
    }

    public String getCoopStatus() 
    {
        return coopStatus;
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
            .append("partnerId", getPartnerId())
            .append("userId", getUserId())
            .append("partnerName", getPartnerName())
            .append("contactName", getContactName())
            .append("contactMobile", getContactMobile())
            .append("businessLicenseNo", getBusinessLicenseNo())
            .append("qualificationJson", getQualificationJson())
            .append("settlementInfoJson", getSettlementInfoJson())
            .append("invoiceInfoJson", getInvoiceInfoJson())
            .append("contractFilesJson", getContractFilesJson())
            .append("auditStatus", getAuditStatus())
            .append("auditTime", getAuditTime())
            .append("auditRemark", getAuditRemark())
            .append("accountStatus", getAccountStatus())
            .append("coopStatus", getCoopStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
