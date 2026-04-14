package com.ruoyi.jst.event.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赛事评审老师实体。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstContestReviewer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 赛事ID */
    private Long contestId;

    /** 评审老师用户ID（FK sys_user） */
    private Long userId;

    /** 老师姓名 */
    private String reviewerName;

    /** 角色：chief_reviewer/reviewer */
    private String role;

    /** 状态：1启用 0停用 */
    private Integer status;

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
}
