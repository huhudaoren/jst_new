package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;

/**
 * 赛事详情相关推荐课程 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ContestRecommendCourseVO {

    private Long courseId;
    private String courseName;
    private String coverImage;
    private BigDecimal price;
    private Long learnerCount;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getLearnerCount() {
        return learnerCount;
    }

    public void setLearnerCount(Long learnerCount) {
        this.learnerCount = learnerCount;
    }
}
