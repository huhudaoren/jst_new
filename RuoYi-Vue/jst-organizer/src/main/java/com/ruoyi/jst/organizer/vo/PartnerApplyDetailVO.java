package com.ruoyi.jst.organizer.vo;

import java.util.Date;

/**
 * 赛事方入驻申请-后台详情响应 VO
 *
 * @author jst
 * @since 1.0.0
 */
public class PartnerApplyDetailVO {

    private Long applyId;

    private String applyNo;

    private Long partnerId;

    private String partnerName;

    private String contactName;

    private String contactMobile;

    private String businessLicenseNo;

    private Object qualificationJson;

    private Object settlementInfoJson;

    private Object invoiceInfoJson;

    private Object contractFilesJson;

    private String applyStatus;

    private String supplementRemark;

    private String auditRemark;

    private Long auditUserId;

    private Date submitTime;

    private Date auditTime;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getBusinessLicenseNo() {
        return businessLicenseNo;
    }

    public void setBusinessLicenseNo(String businessLicenseNo) {
        this.businessLicenseNo = businessLicenseNo;
    }

    public Object getQualificationJson() {
        return qualificationJson;
    }

    public void setQualificationJson(Object qualificationJson) {
        this.qualificationJson = qualificationJson;
    }

    public Object getSettlementInfoJson() {
        return settlementInfoJson;
    }

    public void setSettlementInfoJson(Object settlementInfoJson) {
        this.settlementInfoJson = settlementInfoJson;
    }

    public Object getInvoiceInfoJson() {
        return invoiceInfoJson;
    }

    public void setInvoiceInfoJson(Object invoiceInfoJson) {
        this.invoiceInfoJson = invoiceInfoJson;
    }

    public Object getContractFilesJson() {
        return contractFilesJson;
    }

    public void setContractFilesJson(Object contractFilesJson) {
        this.contractFilesJson = contractFilesJson;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getSupplementRemark() {
        return supplementRemark;
    }

    public void setSupplementRemark(String supplementRemark) {
        this.supplementRemark = supplementRemark;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}
