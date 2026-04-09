package com.ruoyi.jst.channel.vo;

import java.math.BigDecimal;

/**
 * Rebate center summary.
 */
public class RebateSummaryVO {

    private BigDecimal withdrawableAmount;
    private BigDecimal reviewingAmount;
    private BigDecimal paidAmount;
    private BigDecimal rolledBackAmount;

    public BigDecimal getWithdrawableAmount() {
        return withdrawableAmount;
    }

    public void setWithdrawableAmount(BigDecimal withdrawableAmount) {
        this.withdrawableAmount = withdrawableAmount;
    }

    public BigDecimal getReviewingAmount() {
        return reviewingAmount;
    }

    public void setReviewingAmount(BigDecimal reviewingAmount) {
        this.reviewingAmount = reviewingAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getRolledBackAmount() {
        return rolledBackAmount;
    }

    public void setRolledBackAmount(BigDecimal rolledBackAmount) {
        this.rolledBackAmount = rolledBackAmount;
    }
}
