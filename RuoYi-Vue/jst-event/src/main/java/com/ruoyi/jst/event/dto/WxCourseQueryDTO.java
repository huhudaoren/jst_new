package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * 小程序端课程列表查询入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class WxCourseQueryDTO {

    @Pattern(regexp = "normal|ai_maic", message = "课程类型非法")
    private String courseType;

    @Pattern(regexp = "[a-z0-9_]*", message = "category 格式非法")
    private String category;

    @Pattern(regexp = "(default|newest|hot)?", message = "sortBy 仅支持 default/newest/hot")
    private String sortBy;

    @Min(value = 1, message = "pageNum 不能小于1")
    private Integer pageNum;

    @Min(value = 1, message = "pageSize 不能小于1")
    @Max(value = 50, message = "pageSize 不能大于50")
    private Integer pageSize;

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
