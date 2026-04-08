package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程学习记录对象 jst_course_learn_record
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstCourseLearnRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学习ID */
    private Long learnId;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 学习进度百分比 */
    @Excel(name = "学习进度百分比")
    private BigDecimal progress;

    /** 累计学习时长（秒） */
    @Excel(name = "累计学习时长", readConverterExp = "秒=")
    private Long durationSeconds;

    /** 测验分 */
    @Excel(name = "测验分")
    private BigDecimal quizScore;

    /** 完课状态：learning/completed */
    @Excel(name = "完课状态：learning/completed")
    private String completeStatus;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date completeTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setLearnId(Long learnId) 
    {
        this.learnId = learnId;
    }

    public Long getLearnId() 
    {
        return learnId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setProgress(BigDecimal progress) 
    {
        this.progress = progress;
    }

    public BigDecimal getProgress() 
    {
        return progress;
    }

    public void setDurationSeconds(Long durationSeconds) 
    {
        this.durationSeconds = durationSeconds;
    }

    public Long getDurationSeconds() 
    {
        return durationSeconds;
    }

    public void setQuizScore(BigDecimal quizScore) 
    {
        this.quizScore = quizScore;
    }

    public BigDecimal getQuizScore() 
    {
        return quizScore;
    }

    public void setCompleteStatus(String completeStatus) 
    {
        this.completeStatus = completeStatus;
    }

    public String getCompleteStatus() 
    {
        return completeStatus;
    }

    public void setCompleteTime(Date completeTime) 
    {
        this.completeTime = completeTime;
    }

    public Date getCompleteTime() 
    {
        return completeTime;
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
            .append("learnId", getLearnId())
            .append("courseId", getCourseId())
            .append("userId", getUserId())
            .append("progress", getProgress())
            .append("durationSeconds", getDurationSeconds())
            .append("quizScore", getQuizScore())
            .append("completeStatus", getCompleteStatus())
            .append("completeTime", getCompleteTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
