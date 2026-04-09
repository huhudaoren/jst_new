package com.ruoyi.jst.channel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Withdrawal apply request.
 */
public class WithdrawApplyDTO {

    @NotEmpty(message = "ledgerIds cannot be empty")
    private List<Long> ledgerIds;

    @NotNull(message = "expectedAmount cannot be null")
    @DecimalMin(value = "0.01", message = "expectedAmount must be at least 0.01")
    @Digits(integer = 10, fraction = 2, message = "expectedAmount scale must be 2")
    private BigDecimal expectedAmount;

    @Valid
    @NotNull(message = "payeeAccount cannot be null")
    private PayeeAccount payeeAccount;

    private Map<String, Object> invoiceInfo;

    public List<Long> getLedgerIds() {
        return ledgerIds;
    }

    public void setLedgerIds(List<Long> ledgerIds) {
        this.ledgerIds = ledgerIds;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(BigDecimal expectedAmount) {
        this.expectedAmount = expectedAmount;
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

    public static class PayeeAccount {

        @NotBlank(message = "bankName cannot be blank")
        @Size(max = 64, message = "bankName length must be <= 64")
        private String bankName;

        @NotBlank(message = "accountNo cannot be blank")
        @Size(max = 64, message = "accountNo length must be <= 64")
        private String accountNo;

        @NotBlank(message = "accountName cannot be blank")
        @Size(max = 64, message = "accountName length must be <= 64")
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
