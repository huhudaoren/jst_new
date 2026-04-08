package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 公开提交赛事方入驻申请-请求 DTO
 *
 * @author jst
 * @since 1.0.0
 */
public class PartnerApplyReqDTO {

    @NotBlank(message = "partnerName 不能为空")
    @Size(max = 128, message = "partnerName 长度不能超过 128")
    private String partnerName;

    @NotBlank(message = "contactName 不能为空")
    @Size(max = 64, message = "contactName 长度不能超过 64")
    private String contactName;

    @NotBlank(message = "contactMobile 不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "contactMobile 格式不正确")
    private String contactMobile;

    @Size(max = 64, message = "businessLicenseNo 长度不能超过 64")
    private String businessLicenseNo;

    private String qualificationJson;

    private String settlementInfoJson;

    private String invoiceInfoJson;

    private String contractFilesJson;

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

    public String getQualificationJson() {
        return qualificationJson;
    }

    public void setQualificationJson(String qualificationJson) {
        this.qualificationJson = qualificationJson;
    }

    public String getSettlementInfoJson() {
        return settlementInfoJson;
    }

    public void setSettlementInfoJson(String settlementInfoJson) {
        this.settlementInfoJson = settlementInfoJson;
    }

    public String getInvoiceInfoJson() {
        return invoiceInfoJson;
    }

    public void setInvoiceInfoJson(String invoiceInfoJson) {
        this.invoiceInfoJson = invoiceInfoJson;
    }

    public String getContractFilesJson() {
        return contractFilesJson;
    }

    public void setContractFilesJson(String contractFilesJson) {
        this.contractFilesJson = contractFilesJson;
    }
}
