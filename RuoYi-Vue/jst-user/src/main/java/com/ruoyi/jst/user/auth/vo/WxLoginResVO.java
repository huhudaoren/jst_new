package com.ruoyi.jst.user.auth.vo;

import java.util.List;

/**
 * 微信小程序登录响应 VO
 *
 * @author jst
 * @since 1.0.0
 */
public class WxLoginResVO {

    /** JWT token */
    private String token;

    /** 是否新用户 */
    private Boolean isNewUser;

    /** 当前 token 携带的角色 role_key 列表 */
    private List<String> roles;

    /** 用户摘要信息 */
    private UserBriefVO userInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public UserBriefVO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserBriefVO userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 登录后展示的用户摘要信息
     */
    public static class UserBriefVO {

        private Long userId;
        private String nickname;
        private String avatar;
        /** 脱敏手机号 */
        private String mobileMasked;
        private String userType;
        /** 是否已绑定手机号 */
        private Boolean phoneBound;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getMobileMasked() {
            return mobileMasked;
        }

        public void setMobileMasked(String mobileMasked) {
            this.mobileMasked = mobileMasked;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Boolean getPhoneBound() {
            return phoneBound;
        }

        public void setPhoneBound(Boolean phoneBound) {
            this.phoneBound = phoneBound;
        }
    }
}
