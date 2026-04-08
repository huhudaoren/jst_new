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
