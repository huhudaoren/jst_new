package com.ruoyi.jst.event.vo;

/**
 * 首页热门标签聚合源 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class HomeTagSourceVO {

    private String recommendTags;
    private String category;

    public String getRecommendTags() {
        return recommendTags;
    }

    public void setRecommendTags(String recommendTags) {
        this.recommendTags = recommendTags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
