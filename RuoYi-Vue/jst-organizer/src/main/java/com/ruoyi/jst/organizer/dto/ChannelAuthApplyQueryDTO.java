package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.Size;

/**
 * 渠道认证申请-管理端查询入参
 * <p>
 * 关联表：jst_channel_auth_apply
 * 关联状态机：SM-3
 * 关联权限：jst:organizer:channelApply:list
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelAuthApplyQueryDTO {

    @Size(max = 32, message = "申请单号长度不能超过32个字符")
    private String applyNo;

    @Size(max = 128, message = "申请名称长度不能超过128个字符")
    private String applyName;

    private Long userId;

    private String applyStatus;

    private String channelType;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
}
