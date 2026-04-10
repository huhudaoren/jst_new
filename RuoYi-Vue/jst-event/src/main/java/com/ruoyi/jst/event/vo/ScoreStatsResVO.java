package com.ruoyi.jst.event.vo;

/**
 * 成绩管理统计响应。
 *
 * @author jst
 * @since 1.0.0
 */
public class ScoreStatsResVO {

    private Long enrollCount;
    private Long scoredCount;
    private Long pendingReviewCount;
    private Long publishedCount;

    public Long getEnrollCount() { return enrollCount; }
    public void setEnrollCount(Long enrollCount) { this.enrollCount = enrollCount; }
    public Long getScoredCount() { return scoredCount; }
    public void setScoredCount(Long scoredCount) { this.scoredCount = scoredCount; }
    public Long getPendingReviewCount() { return pendingReviewCount; }
    public void setPendingReviewCount(Long pendingReviewCount) { this.pendingReviewCount = pendingReviewCount; }
    public Long getPublishedCount() { return publishedCount; }
    public void setPublishedCount(Long publishedCount) { this.publishedCount = publishedCount; }
}
