package com.ruoyi.jst.channel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Withdrawal detail view.
 */
public class WithdrawDetailVO {

    private Long settlementId;
    private String settlementNo;
    private BigDecimal applyAmount;
    private BigDecimal negativeOffsetAmount;
    private BigDecimal actualPayAmount;
    private String status;
    private String invoiceStatus;
    private String auditRemark;
    private String payVoucherUrl;
    private PayeeAccount payeeAccount;
    private Map<String, Object> invoiceInfo;
    private List<RebateLedgerListVO> ledgerItems;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }

    public String getSettlementNo() {
        return settlementNo;
    }

    public void setSettlementNo(String settlementNo) {
        this.settlementNo = settlementNo;
    }

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getNegativeOffsetAmount() {
        return negativeOffsetAmount;
    }

    public void setNegativeOffsetAmount(BigDecimal negativeOffsetAmount) {
        this.negativeOffsetAmount = negativeOffsetAmount;
    }

    public BigDecimal getActualPayAmount() {
        return actualPayAmount;
    }

    public void setActualPayAmount(BigDecimal actualPayAmount) {
        this.actualPayAmount = actualPayAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getPayVoucherUrl() {
        return payVoucherUrl;
    }

    public void setPayVoucherUrl(String payVoucherUrl) {
        this.payVoucherUrl = payVoucherUrl;
    }

    public PayeeAccount getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(PayeeAccount payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public Map<String, Object> getInvoiceInfo() {
        return invoiceInfo;
    }

    public void setInvoiceInfo(Map<String, Object> invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }

    public List<RebateLedgerListVO> getLedgerItems() {
        return ledgerItems;
    }

    public void setLedgerItems(List<RebateLedgerListVO> ledgerItems) {
        this.ledgerItems = ledgerItems;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static class PayeeAccount {
        private String bankName;
        private String accountNo;
        private String accountName;

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }
    }
}
