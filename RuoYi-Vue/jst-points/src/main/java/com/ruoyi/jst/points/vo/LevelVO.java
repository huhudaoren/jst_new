package com.ruoyi.jst.points.vo;

/**
 * 等级配置出参。
 */
public class LevelVO {

    private Long levelId;

    private String levelCode;

    private String levelName;

    private Integer levelNo;

    private Long growthThreshold;

    private String icon;

    private String applicableRole;

    private Boolean unlocked;

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Integer getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }

    public Long getGrowthThreshold() {
        return growthThreshold;
    }

    public void setGrowthThreshold(Long growthThreshold) {
        this.growthThreshold = growthThreshold;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getApplicableRole() {
        return applicableRole;
    }

    public void setApplicableRole(String applicableRole) {
        this.applicableRole = applicableRole;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }
}
