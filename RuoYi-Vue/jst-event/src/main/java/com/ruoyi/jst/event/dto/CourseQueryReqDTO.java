package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 管理端课程列表查询入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class CourseQueryReqDTO {

    @Size(max = 255, message = "课程名称长度不能超过255个字符")
    private String courseName;

    @Pattern(regexp = "normal|ai_maic", message = "课程类型非法")
    private String courseType;

    @Pattern(regexp = "draft|pending|approved|rejected", message = "审核状态非法")
    private String auditStatus;

    @Pattern(regexp = "on|off", message = "上下架状态非法")
    private String status;

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

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
