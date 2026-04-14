package com.ruoyi.jst.event.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程对象 jst_course
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstCourse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程ID */
    private Long courseId;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 类型：normal普通 / ai_maic AI课程 */
    @Excel(name = "类型：normal普通 / ai_maic AI课程")
    private String courseType;

    /** 封面 */
    @Excel(name = "封面")
    private String coverImage;

    /** 简介 */
    @Excel(name = "简介")
    private String description;

    /** 现金价格 */
    @Excel(name = "现金价格")
    private BigDecimal price;

    /** 积分价格 */
    @Excel(name = "积分价格")
    private Long pointsPrice;

    /** 课时数 */
    @Excel(name = "课时数")
    private Integer lessonCount;

    /** 学习人数 */
    @Excel(name = "学习人数")
    private Integer learnerCount;

    /** 总时长如12小时30分 */
    @Excel(name = "总时长如12小时30分")
    private String totalDuration;

    /** 课程目录JSON */
    @Deprecated
    @Excel(name = "课程目录JSON")
    private String chaptersJson;

    /** 讲师姓名 */
    @Excel(name = "讲师姓名")
    private String teacherName;

    /** 讲师头像 */
    @Excel(name = "讲师头像")
    private String teacherAvatar;

    /** 讲师简介 */
    @Excel(name = "讲师简介")
    private String teacherDesc;

    /** 创建者类型：platform/channel */
    @Excel(name = "创建者类型：platform/channel")
    private String creatorType;

    /** 创建者ID */
    @Excel(name = "创建者ID")
    private Long creatorId;

    /** OpenMAIC 第三方课件ID */
    @Excel(name = "OpenMAIC 第三方课件ID")
    private String maicSourceId;

    /** 审核状态：draft/pending/approved/rejected */
    @Excel(name = "审核状态：draft/pending/approved/rejected")
    private String auditStatus;

    /** 上下架：on/off */
    @Excel(name = "上下架：on/off")
    private String status;

    /** 可见范围 */
    @Excel(name = "可见范围")
    private String visibleScope;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setCourseName(String courseName) 
    {
        this.courseName = courseName;
    }

    public String getCourseName() 
    {
        return courseName;
    }

    public void setCourseType(String courseType) 
    {
        this.courseType = courseType;
    }

    public String getCourseType() 
    {
        return courseType;
    }

    public void setCoverImage(String coverImage) 
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage() 
    {
        return coverImage;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setPointsPrice(Long pointsPrice) 
    {
        this.pointsPrice = pointsPrice;
    }

    public Long getPointsPrice() 
    {
        return pointsPrice;
    }

    public void setLessonCount(Integer lessonCount)
    {
        this.lessonCount = lessonCount;
    }

    public Integer getLessonCount()
    {
        return lessonCount;
    }

    public void setLearnerCount(Integer learnerCount)
    {
        this.learnerCount = learnerCount;
    }

    public Integer getLearnerCount()
    {
        return learnerCount;
    }

    public void setTotalDuration(String totalDuration)
    {
        this.totalDuration = totalDuration;
    }

    public String getTotalDuration()
    {
        return totalDuration;
    }

    public void setChaptersJson(String chaptersJson)
    {
        this.chaptersJson = chaptersJson;
    }

    public String getChaptersJson()
    {
        return chaptersJson;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherAvatar(String teacherAvatar)
    {
        this.teacherAvatar = teacherAvatar;
    }

    public String getTeacherAvatar()
    {
        return teacherAvatar;
    }

    public void setTeacherDesc(String teacherDesc)
    {
        this.teacherDesc = teacherDesc;
    }

    public String getTeacherDesc()
    {
        return teacherDesc;
    }

    public void setCreatorType(String creatorType) 
    {
        this.creatorType = creatorType;
    }

    public String getCreatorType() 
    {
        return creatorType;
    }

    public void setCreatorId(Long creatorId) 
    {
        this.creatorId = creatorId;
    }

    public Long getCreatorId() 
    {
        return creatorId;
    }

    public void setMaicSourceId(String maicSourceId) 
    {
        this.maicSourceId = maicSourceId;
    }

    public String getMaicSourceId() 
    {
        return maicSourceId;
    }

    public void setAuditStatus(String auditStatus) 
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() 
    {
        return auditStatus;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setVisibleScope(String visibleScope) 
    {
        this.visibleScope = visibleScope;
    }

    public String getVisibleScope() 
    {
        return visibleScope;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("courseId", getCourseId())
            .append("courseName", getCourseName())
            .append("courseType", getCourseType())
            .append("coverImage", getCoverImage())
            .append("description", getDescription())
            .append("price", getPrice())
            .append("pointsPrice", getPointsPrice())
            .append("lessonCount", getLessonCount())
            .append("learnerCount", getLearnerCount())
            .append("totalDuration", getTotalDuration())
            .append("chaptersJson", getChaptersJson())
            .append("teacherName", getTeacherName())
            .append("teacherAvatar", getTeacherAvatar())
            .append("teacherDesc", getTeacherDesc())
            .append("creatorType", getCreatorType())
            .append("creatorId", getCreatorId())
            .append("maicSourceId", getMaicSourceId())
            .append("auditStatus", getAuditStatus())
            .append("status", getStatus())
            .append("visibleScope", getVisibleScope())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
