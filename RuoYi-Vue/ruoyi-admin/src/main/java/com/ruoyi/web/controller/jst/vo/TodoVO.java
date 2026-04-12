package com.ruoyi.web.controller.jst.vo;

/**
 * 平台运营待办计数出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class TodoVO {

    private Integer pendingContestAudit;
    private Integer pendingEnrollAudit;
    private Integer pendingRefund;
    private Integer pendingWithdraw;
    private Integer pendingPartnerApply;
    private Integer pendingChannelAuth;

    public Integer getPendingContestAudit() {
        return pendingContestAudit;
    }

    public void setPendingContestAudit(Integer pendingContestAudit) {
        this.pendingContestAudit = pendingContestAudit;
    }

    public Integer getPendingEnrollAudit() {
        return pendingEnrollAudit;
    }

    public void setPendingEnrollAudit(Integer pendingEnrollAudit) {
        this.pendingEnrollAudit = pendingEnrollAudit;
    }

    public Integer getPendingRefund() {
        return pendingRefund;
    }

    public void setPendingRefund(Integer pendingRefund) {
        this.pendingRefund = pendingRefund;
    }

    public Integer getPendingWithdraw() {
        return pendingWithdraw;
    }

    public void setPendingWithdraw(Integer pendingWithdraw) {
        this.pendingWithdraw = pendingWithdraw;
    }

    public Integer getPendingPartnerApply() {
        return pendingPartnerApply;
    }

    public void setPendingPartnerApply(Integer pendingPartnerApply) {
        this.pendingPartnerApply = pendingPartnerApply;
    }

    public Integer getPendingChannelAuth() {
        return pendingChannelAuth;
    }

    public void setPendingChannelAuth(Integer pendingChannelAuth) {
        this.pendingChannelAuth = pendingChannelAuth;
    }
}
