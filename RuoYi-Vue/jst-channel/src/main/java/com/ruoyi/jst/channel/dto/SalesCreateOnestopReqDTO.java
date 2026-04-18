package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 销售管理一站式新建：同事务里 INSERT sys_user + sys_user_role + jst_sales。
 * <p>
 * 用于 PATCH-7：把"先去系统用户管理建账号→记 ID→来销售页新建"的割裂流程合并成一步。
 * 保留原 {@link SalesCreateReqDTO}（要求已有 sys_user_id）不动以兼容旧调用。
 *
 * @author jst
 * @since 1.0.0
 */
public class SalesCreateOnestopReqDTO {

    /** 登录用户名（sys_user.user_name 唯一） */
    @NotBlank(message = "登录用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度 4-30 位")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    private String userName;

    /** 初始密码（明文传入，落库前 BCrypt 加密） */
    @NotBlank(message = "初始密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度 6-20 位")
    private String initPassword;

    /** 销售姓名 —— 同时写 sys_user.nick_name 和 jst_sales.sales_name */
    @NotBlank(message = "销售姓名不能为空")
    @Size(max = 64)
    private String salesName;

    /** 手机号 —— 同时写 sys_user.phonenumber 和 jst_sales.phone，后者须在 jst_sales 中唯一 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 直属主管的 sales_id（可空） */
    private Long managerId;

    /** 默认提成费率（0.0000-1.0000，如 0.05 = 5%） */
    @NotNull(message = "默认费率不能为空")
    @DecimalMin(value = "0.0000", message = "费率不能小于 0")
    @DecimalMax(value = "1.0000", message = "费率不能大于 1")
    private BigDecimal commissionRateDefault;

    /** 是否直接设为主管：true 绑 jst_sales_manager，false 绑 jst_sales */
    private Boolean asManager = Boolean.FALSE;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getInitPassword() { return initPassword; }
    public void setInitPassword(String initPassword) { this.initPassword = initPassword; }

    public String getSalesName() { return salesName; }
    public void setSalesName(String salesName) { this.salesName = salesName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public BigDecimal getCommissionRateDefault() { return commissionRateDefault; }
    public void setCommissionRateDefault(BigDecimal v) { this.commissionRateDefault = v; }

    public Boolean getAsManager() { return asManager; }
    public void setAsManager(Boolean asManager) { this.asManager = asManager; }
}
