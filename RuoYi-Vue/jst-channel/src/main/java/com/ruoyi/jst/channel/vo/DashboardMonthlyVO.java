package com.ruoyi.jst.channel.vo;

import java.math.BigDecimal;

/**
 * Monthly dashboard summary.
 */
public class DashboardMonthlyVO {

    private String yearMonth;
    private Integer newStudentCount;
    private Integer orderCount;
    private BigDecimal orderPaidAmount;
    private BigDecimal rebateEstimatedAmount;

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public Integer getNewStudentCount() {
        return newStudentCount;
    }

    public void setNewStudentCount(Integer newStudentCount) {
        this.newStudentCount = newStudentCount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getOrderPaidAmount() {
        return orderPaidAmount;
    }

    public void setOrderPaidAmount(BigDecimal orderPaidAmount) {
        this.orderPaidAmount = orderPaidAmount;
    }

    public BigDecimal getRebateEstimatedAmount() {
        return rebateEstimatedAmount;
    }

    public void setRebateEstimatedAmount(BigDecimal rebateEstimatedAmount) {
        this.rebateEstimatedAmount = rebateEstimatedAmount;
    }
}
