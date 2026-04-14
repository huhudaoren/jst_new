package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 赛事成绩项定义实体。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstScoreItem extends BaseEntity {

    private Long itemId;
    private Long contestId;
    private String itemName;
    private BigDecimal maxScore;
    private BigDecimal weight;
    private Integer sortOrder;
    private String delFlag;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
