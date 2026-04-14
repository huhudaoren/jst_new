package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程章节实体。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstCourseChapter extends BaseEntity {

    private Long chapterId;
    private Long courseId;
    private String chapterName;
    private Integer sortOrder;
    private String delFlag;

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
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
