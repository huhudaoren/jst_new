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
}
