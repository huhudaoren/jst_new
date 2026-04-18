package com.ruoyi.jst.finance.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 小程序端发票记录出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class InvoiceRecordVO {

    private Long invoiceId;
    private String invoiceNo;
    private String refSettlementNo;
    private String invoiceTitle;
    private String taxNo;
    private BigDecimal amount;
    private String status;
    private String fileUrl;
    private String expressStatus;
    private Date issueTime;
    private Date createTime;

    /** 关联结算单 ID（通过 ref_settlement_no LEFT JOIN 查出） */
    private Long settlementId;
    /** 关联结算单号（= refSettlementNo，冗余以保持字段命名一致） */
    private String settlementNo;
    /** 快递公司 */
    private String trackingCompany;
    /** 快递单号 */
    private String trackingNo;
    /** 投递邮箱（已脱敏） */
    private String deliveryEmail;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getRefSettlementNo() {
        return refSettlementNo;
    }

    public void setRefSettlementNo(String refSettlementNo) {
        this.refSettlementNo = refSettlementNo;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getExpressStatus() {
        return expressStatus;
    }

    public void setExpressStatus(String expressStatus) {
        this.expressStatus = expressStatus;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getSettlementId() { return settlementId; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }
    public String getSettlementNo() { return settlementNo; }
    public void setSettlementNo(String settlementNo) { this.settlementNo = settlementNo; }
    public String getTrackingCompany() { return trackingCompany; }
    public void setTrackingCompany(String trackingCompany) { this.trackingCompany = trackingCompany; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
    public String getDeliveryEmail() { return deliveryEmail; }
    public void setDeliveryEmail(String deliveryEmail) { this.deliveryEmail = deliveryEmail; }
}
