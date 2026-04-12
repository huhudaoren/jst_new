package com.ruoyi.jst.event.vo;

import java.math.BigDecimal;

/**
 * 小程序端课程详情出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxCourseDetailVO {

    private Long courseId;
    private String courseName;
    private String courseType;
    private String coverImage;
    private String description;
    private BigDecimal price;
    private Long pointsPrice;
    private Integer lessonCount;
    private Integer learnerCount;
    private String totalDuration;
    private String chaptersJson;
    private String teacherName;
    private String teacherAvatar;
    private String teacherDesc;

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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getPointsPrice() {
        return pointsPrice;
    }

    public void setPointsPrice(Long pointsPrice) {
        this.pointsPrice = pointsPrice;
    }

    public Integer getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(Integer lessonCount) {
        this.lessonCount = lessonCount;
    }

    public Integer getLearnerCount() {
        return learnerCount;
    }

    public void setLearnerCount(Integer learnerCount) {
        this.learnerCount = learnerCount;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getChaptersJson() {
        return chaptersJson;
    }

    public void setChaptersJson(String chaptersJson) {
        this.chaptersJson = chaptersJson;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherAvatar() {
        return teacherAvatar;
    }

    public void setTeacherAvatar(String teacherAvatar) {
        this.teacherAvatar = teacherAvatar;
    }

    public String getTeacherDesc() {
        return teacherDesc;
    }

    public void setTeacherDesc(String teacherDesc) {
        this.teacherDesc = teacherDesc;
    }
}
