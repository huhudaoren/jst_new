package com.ruoyi.jst.event.vo;

import java.util.Date;

/**
 * 评审老师返回 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ReviewerVO {

    /** 主键 */
    private Long id;

    /** 赛事ID */
    private Long contestId;

    /** 用户ID */
    private Long userId;

    /** 老师姓名 */
    private String reviewerName;

    /** 角色：chief_reviewer/reviewer */
    private String role;

    /** 状态 */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
