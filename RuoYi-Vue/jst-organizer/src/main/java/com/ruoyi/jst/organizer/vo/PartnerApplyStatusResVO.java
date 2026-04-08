package com.ruoyi.jst.organizer.vo;

import java.util.Date;

/**
 * 赛事方入驻申请-公开状态查询响应 VO
 *
 * @author jst
 * @since 1.0.0
 */
public class PartnerApplyStatusResVO {

    private String applyStatus;

    private String auditRemark;

    private Date submitTime;

    private Date auditTime;

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}
