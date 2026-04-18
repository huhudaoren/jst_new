package com.ruoyi.jst.channel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Withdrawal list item.
 */
public class WithdrawListVO {

    private Long settlementId;
    private String settlementNo;
    private BigDecimal applyAmount;
    private BigDecimal negativeOffsetAmount;
    private BigDecimal actualPayAmount;
    private String status;
    private String invoiceStatus;

    /** 关联返点台账条数（子查询） */
    private Long ledgerCount;
    /** 关联合同编号（LEFT JOIN jst_contract_record.contract_no） */
    private String contractNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

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

    public Long getLedgerCount() {
        return ledgerCount;
    }

    public void setLedgerCount(Long ledgerCount) {
        this.ledgerCount = ledgerCount;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
