package com.ruoyi.jst.points.vo;

/**
 * 积分中心首页汇总。
 */
public class PointsCenterSummaryVO {

    private Long availablePoints;

    private Long frozenPoints;

    private Long totalEarn;

    private Long totalSpend;

    private Long growthValue;

    private LevelVO currentLevel;

    private LevelVO nextLevel;

    private Long pointsToNextLevel;

    public Long getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Long availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Long getFrozenPoints() {
        return frozenPoints;
    }

    public void setFrozenPoints(Long frozenPoints) {
        this.frozenPoints = frozenPoints;
    }

    public Long getTotalEarn() {
        return totalEarn;
    }

    public void setTotalEarn(Long totalEarn) {
        this.totalEarn = totalEarn;
    }

    public Long getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(Long totalSpend) {
        this.totalSpend = totalSpend;
    }

    public Long getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Long growthValue) {
        this.growthValue = growthValue;
    }

    public LevelVO getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(LevelVO currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LevelVO getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(LevelVO nextLevel) {
        this.nextLevel = nextLevel;
    }

    public Long getPointsToNextLevel() {
        return pointsToNextLevel;
    }

    public void setPointsToNextLevel(Long pointsToNextLevel) {
        this.pointsToNextLevel = pointsToNextLevel;
    }
}
