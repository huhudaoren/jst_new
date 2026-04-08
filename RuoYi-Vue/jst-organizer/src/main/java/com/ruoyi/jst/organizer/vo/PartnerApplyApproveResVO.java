package com.ruoyi.jst.organizer.vo;

/**
 * 赛事方入驻申请-审核通过响应 VO
 *
 * @author jst
 * @since 1.0.0
 */
public class PartnerApplyApproveResVO {

    private Long partnerId;

    private Long userId;

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
