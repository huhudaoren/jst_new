package com.ruoyi.jst.points.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 成长值流水出参。
 */
public class GrowthLedgerVO {

    private Long ledgerId;

    private String changeType;

    private String sourceType;

    private Long sourceRefId;

    private Long growthChange;

    private Long balanceAfter;

    private Long levelBefore;

    private Long levelAfter;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceRefId() {
        return sourceRefId;
    }

    public void setSourceRefId(Long sourceRefId) {
        this.sourceRefId = sourceRefId;
    }

    public Long getGrowthChange() {
        return growthChange;
    }

    public void setGrowthChange(Long growthChange) {
        this.growthChange = growthChange;
    }

    public Long getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Long getLevelBefore() {
        return levelBefore;
    }

    public void setLevelBefore(Long levelBefore) {
        this.levelBefore = levelBefore;
    }

    public Long getLevelAfter() {
        return levelAfter;
    }

    public void setLevelAfter(Long levelAfter) {
        this.levelAfter = levelAfter;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
