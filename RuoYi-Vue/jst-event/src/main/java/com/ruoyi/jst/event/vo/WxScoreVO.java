package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Mini-program score list item.
 */
public class WxScoreVO {

    private Long scoreId;
    private Long enrollId;
    private Long contestId;
    private String contestName;
    private String category;
    private BigDecimal scoreValue;
    private BigDecimal score;
    private Long rankNo;
    private Long rank;
    private String awardLevel;
    private String remark;
    private Date publishTime;
    private Boolean hasCert;
    private String scoreLabel;

    public Long getScoreId() {
        return scoreId;
    }

    public void setScoreId(Long scoreId) {
        this.scoreId = scoreId;
    }

    public Long getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(Long enrollId) {
        this.enrollId = enrollId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Long getRankNo() {
        return rankNo;
    }

    public void setRankNo(Long rankNo) {
        this.rankNo = rankNo;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getHasCert() {
        return hasCert;
    }

    public void setHasCert(Boolean hasCert) {
        this.hasCert = hasCert;
    }

    public String getScoreLabel() {
        return scoreLabel;
    }

    public void setScoreLabel(String scoreLabel) {
        this.scoreLabel = scoreLabel;
    }
}
