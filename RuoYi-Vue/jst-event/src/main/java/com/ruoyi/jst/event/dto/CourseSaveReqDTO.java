package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 课程新增/编辑入参。
 * <p>
 * 关联表：jst_course
 * 关联状态机：SM-21（新增默认 draft，编辑仅 draft/rejected）
 *
 * @author jst
 * @since 1.0.0
 */
public class CourseSaveReqDTO {

    private Long courseId;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 255, message = "课程名称长度不能超过255个字符")
    private String courseName;

    @NotBlank(message = "课程类型不能为空")
    @Pattern(regexp = "normal|ai_maic", message = "课程类型非法")
    private String courseType;

    @NotBlank(message = "课程封面不能为空")
    @Size(max = 255, message = "课程封面长度不能超过255个字符")
    private String coverImage;

    @NotBlank(message = "课程简介不能为空")
    private String description;

    @NotNull(message = "现金价格不能为空")
    @DecimalMin(value = "0.00", message = "现金价格不能小于0")
    private BigDecimal price;

    @Min(value = 0, message = "积分价格不能小于0")
    private Long pointsPrice;

    @NotBlank(message = "创建者类型不能为空")
    @Pattern(regexp = "platform|channel", message = "创建者类型非法")
    private String creatorType;

    private Long creatorId;

    @Size(max = 64, message = "第三方课件ID长度不能超过64个字符")
    private String maicSourceId;

    @Size(max = 20, message = "可见范围长度不能超过20个字符")
    private String visibleScope;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

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

    public String getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(String creatorType) {
        this.creatorType = creatorType;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getMaicSourceId() {
        return maicSourceId;
    }

    public void setMaicSourceId(String maicSourceId) {
        this.maicSourceId = maicSourceId;
    }

    public String getVisibleScope() {
        return visibleScope;
    }

    public void setVisibleScope(String visibleScope) {
        this.visibleScope = visibleScope;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
