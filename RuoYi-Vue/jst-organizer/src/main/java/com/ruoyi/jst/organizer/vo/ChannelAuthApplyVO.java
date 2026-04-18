package com.ruoyi.jst.organizer.vo;

import java.util.Date;

/**
 * 渠道认证申请-展示 VO
 * <p>
 * 关联表：jst_channel_auth_apply
 * 关联状态机：SM-3
 * 输出约束：不返回敏感手机号/证件号/银行卡明文
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelAuthApplyVO {

    private Long applyId;
    private String applyNo;
    private Long userId;
    private Long channelId;
    private String channelType;
    private String applyName;
    private String region;
    private String materialsJson;
    private String applyStatus;
    private String auditRemark;
    /** Round 2A A2: 驳回原因（业务语义，区别于 audit_remark） */
    private String rejectReason;
    private Integer rejectCount;
    private Integer lockedForManual;
    private Date submitTime;
    private Date auditTime;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMaterialsJson() {
        return materialsJson;
    }

    public void setMaterialsJson(String materialsJson) {
        this.materialsJson = materialsJson;
    }

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

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Integer getRejectCount() {
        return rejectCount;
    }

    public void setRejectCount(Integer rejectCount) {
        this.rejectCount = rejectCount;
    }

    public Integer getLockedForManual() {
        return lockedForManual;
    }

    public void setLockedForManual(Integer lockedForManual) {
        this.lockedForManual = lockedForManual;
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
