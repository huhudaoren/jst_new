package com.ruoyi.jst.finance.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 赛事方结算单列表视图。
 */
public class PartnerSettlementListResVO {

    private Long eventSettlementId;
    private String settlementNo;
    private Long partnerId;
    private String partnerName;
    private Long contestId;
    private String contestName;
    private Long enrollCount;
    private BigDecimal totalListAmount;
    private BigDecimal totalCouponAmount;
    private BigDecimal totalPointsAmount;
    private BigDecimal platformBearAmount;
    private BigDecimal totalNetPay;
    private BigDecimal totalRefund;
    private BigDecimal totalRebate;
    private BigDecimal totalServiceFee;
    private BigDecimal contractDeduction;
    private BigDecimal finalAmount;
    private String status;
    private String auditRemark;
    private String payVoucherUrl;
    private Date payTime;
    private Date createTime;

    public Long getEventSettlementId() {
        return eventSettlementId;
    }

    public void setEventSettlementId(Long eventSettlementId) {
        this.eventSettlementId = eventSettlementId;
    }

    public String getSettlementNo() {
        return settlementNo;
    }

    public void setSettlementNo(String settlementNo) {
        this.settlementNo = settlementNo;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public Long getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Long enrollCount) {
        this.enrollCount = enrollCount;
    }

    public BigDecimal getTotalListAmount() {
        return totalListAmount;
    }

    public void setTotalListAmount(BigDecimal totalListAmount) {
        this.totalListAmount = totalListAmount;
    }

    public BigDecimal getTotalCouponAmount() {
        return totalCouponAmount;
    }

    public void setTotalCouponAmount(BigDecimal totalCouponAmount) {
        this.totalCouponAmount = totalCouponAmount;
    }

    public BigDecimal getTotalPointsAmount() {
        return totalPointsAmount;
    }

    public void setTotalPointsAmount(BigDecimal totalPointsAmount) {
        this.totalPointsAmount = totalPointsAmount;
    }

    public BigDecimal getPlatformBearAmount() {
        return platformBearAmount;
    }

    public void setPlatformBearAmount(BigDecimal platformBearAmount) {
        this.platformBearAmount = platformBearAmount;
    }

    public BigDecimal getTotalNetPay() {
        return totalNetPay;
    }

    public void setTotalNetPay(BigDecimal totalNetPay) {
        this.totalNetPay = totalNetPay;
    }

    public BigDecimal getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }

    public BigDecimal getTotalRebate() {
        return totalRebate;
    }

    public void setTotalRebate(BigDecimal totalRebate) {
        this.totalRebate = totalRebate;
    }

    public BigDecimal getTotalServiceFee() {
        return totalServiceFee;
    }

    public void setTotalServiceFee(BigDecimal totalServiceFee) {
        this.totalServiceFee = totalServiceFee;
    }

    public BigDecimal getContractDeduction() {
        return contractDeduction;
    }

    public void setContractDeduction(BigDecimal contractDeduction) {
        this.contractDeduction = contractDeduction;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
