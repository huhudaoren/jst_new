package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 管理端参赛档案列表查询请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class ParticipantAdminQueryReqDTO {

    /** 参赛人姓名（模糊查询）。 */
    @Size(max = 64, message = "name 长度不能超过 64")
    private String name;

    /** 监护人手机号（模糊查询，兼容前端 mobile 参数）。 */
    @Size(max = 20, message = "mobile 长度不能超过 20")
    private String mobile;

    /** 监护人手机号（模糊查询）。 */
    @Size(max = 20, message = "guardianMobile 长度不能超过 20")
    private String guardianMobile;

    /** 认领状态过滤。 */
    @Pattern(
            regexp = "unclaimed|auto_claimed|manual_claimed|pending_manual",
            message = "claimStatus 仅支持 unclaimed/auto_claimed/manual_claimed/pending_manual"
    )
    private String claimStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }
}
