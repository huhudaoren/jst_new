package com.ruoyi.jst.channel.payout;

import java.math.BigDecimal;

/**
 * Payout gateway abstraction for channel withdraw execute.
 */
public interface PayoutService {

    String mode();

    PayoutResult payout(PayoutRequest request);

    class PayoutRequest {
        private Long settlementId;
        private String payNo;
        private BigDecimal amount;
        private String payeeAccount;

        public Long getSettlementId() {
            return settlementId;
        }

        public void setSettlementId(Long settlementId) {
            this.settlementId = settlementId;
        }

        public String getPayNo() {
            return payNo;
        }

        public void setPayNo(String payNo) {
            this.payNo = payNo;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getPayeeAccount() {
            return payeeAccount;
        }

        public void setPayeeAccount(String payeeAccount) {
            this.payeeAccount = payeeAccount;
        }
    }

    class PayoutResult {
        private boolean success;
        private String voucherUrl;
        private String providerNo;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getVoucherUrl() {
            return voucherUrl;
        }

        public void setVoucherUrl(String voucherUrl) {
            this.voucherUrl = voucherUrl;
        }

        public String getProviderNo() {
            return providerNo;
        }

        public void setProviderNo(String providerNo) {
            this.providerNo = providerNo;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
