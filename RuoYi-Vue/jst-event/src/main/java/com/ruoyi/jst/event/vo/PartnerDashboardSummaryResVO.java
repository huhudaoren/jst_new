package com.ruoyi.jst.event.vo;

public class PartnerDashboardSummaryResVO {

    private Integer pendingEnrollCount;
    private Integer monthEnrollCount;
    private Integer pendingScoreCount;
    private Integer pendingCertificateCount;

    public Integer getPendingEnrollCount() {
        return pendingEnrollCount;
    }

    public void setPendingEnrollCount(Integer pendingEnrollCount) {
        this.pendingEnrollCount = pendingEnrollCount;
    }

    public Integer getMonthEnrollCount() {
        return monthEnrollCount;
    }

    public void setMonthEnrollCount(Integer monthEnrollCount) {
        this.monthEnrollCount = monthEnrollCount;
    }

    public Integer getPendingScoreCount() {
        return pendingScoreCount;
    }

    public void setPendingScoreCount(Integer pendingScoreCount) {
        this.pendingScoreCount = pendingScoreCount;
    }

    public Integer getPendingCertificateCount() {
        return pendingCertificateCount;
    }

    public void setPendingCertificateCount(Integer pendingCertificateCount) {
        this.pendingCertificateCount = pendingCertificateCount;
    }
}
