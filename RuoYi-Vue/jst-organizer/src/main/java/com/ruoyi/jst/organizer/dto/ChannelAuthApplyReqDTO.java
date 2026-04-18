package com.ruoyi.jst.organizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 渠道认证申请-提交入参
 * <p>
 * 关联表：jst_channel_auth_apply
 * 关联状态机：SM-3 pending 初始态
 * 关联权限：@ss.hasRole('jst_student')
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelAuthApplyReqDTO {

    @NotBlank(message = "渠道类型不能为空")
    @Pattern(regexp = "teacher|organization|individual", message = "渠道类型非法")
    private String channelType;

    @NotBlank(message = "申请名称不能为空")
    @Size(max = 128, message = "申请名称长度不能超过128个字符")
    private String applyName;

    @NotBlank(message = "地区不能为空")
    @Size(max = 64, message = "地区长度不能超过64个字符")
    private String region;

    @NotBlank(message = "认证材料不能为空")
    private String materialsJson;

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMaterialsJson() {
        return materialsJson;
    }

    public void setMaterialsJson(String materialsJson) {
        this.materialsJson = materialsJson;
    }
}
