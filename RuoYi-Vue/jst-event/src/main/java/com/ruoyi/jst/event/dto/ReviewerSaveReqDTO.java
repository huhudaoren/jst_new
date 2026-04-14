package com.ruoyi.jst.event.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 评审老师配置请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ReviewerSaveReqDTO {

    /** 评审老师列表 */
    @NotEmpty(message = "reviewers 不能为空")
    private List<ReviewerItem> reviewers;

    public List<ReviewerItem> getReviewers() {
        return reviewers;
    }

    public void setReviewers(List<ReviewerItem> reviewers) {
        this.reviewers = reviewers;
    }

    /**
     * 单个评审老师条目。
     */
    public static class ReviewerItem {

        /** 用户ID（FK sys_user） */
        @NotNull(message = "userId 不能为空")
        private Long userId;

        /** 老师姓名 */
        @Size(max = 50, message = "reviewerName 长度不能超过 50")
        private String reviewerName;

        /** 角色：chief_reviewer/reviewer */
        @Pattern(regexp = "chief_reviewer|reviewer", message = "role 仅支持 chief_reviewer/reviewer")
        private String role;

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
    }
}
