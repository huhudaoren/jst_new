package com.ruoyi.jst.common.context;

import java.util.Collections;
import java.util.List;

/**
 * 登录上下文 (ThreadLocal)
 * <p>
 * 决策 Q1=A:用 ThreadLocal 扩展若依 LoginUser,不侵入 ruoyi-common 源码。
 * 由 {@link JstLoginContextInterceptor} 在每个请求 preHandle 阶段填充,
 * afterCompletion 阶段 clear。
 * <p>
 * 强约束：
 * <ul>
 *   <li>禁止在异步线程中直接访问,如需传递必须在父线程 snapshot 后传给子线程</li>
 *   <li>禁止在 Job/Quartz 中使用,定时任务无 HTTP 请求上下文</li>
 *   <li>读取 partnerId/channelId 时若为 null 表示当前用户没有该身份</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
public class JstLoginContext {

    private static final ThreadLocal<JstLoginContext> CTX = new ThreadLocal<>();

    /** 用户ID (业务 jst_user.user_id 或若依 sys_user.user_id) */
    private Long userId;

    /** 关联赛事方ID (若当前用户为赛事方账号) */
    private Long partnerId;

    /** 关联渠道方ID (若当前用户为渠道方账号) */
    private Long channelId;

    /** 关联销售档案ID (若当前用户为销售/销售主管) */
    private Long salesId;

    /** 业务用户类型: student/parent/channel/partner/admin */
    private String userType;

    /** 角色 key 列表 (jst_student/jst_channel/jst_partner/jst_platform_op) */
    private List<String> roleKeys = Collections.emptyList();

    /** 当前请求来源:web=PC后台 / wx=小程序 / api=外部 */
    private String terminal;

    // ========== 静态访问 ==========

    public static JstLoginContext current() {
        JstLoginContext ctx = CTX.get();
        return ctx == null ? new JstLoginContext() : ctx;
    }

    public static void set(JstLoginContext ctx) {
        CTX.set(ctx);
    }

    public static void clear() {
        CTX.remove();
    }

    /** 是否拥有指定角色 */
    public static boolean hasRole(String roleKey) {
        return current().getRoleKeys().contains(roleKey);
    }

    /** 当前赛事方ID(平台运营/学生时为 null) */
    public static Long currentPartnerId() {
        return current().partnerId;
    }

    /** 当前渠道方ID */
    public static Long currentChannelId() {
        return current().channelId;
    }

    /** 当前销售ID(平台运营/学生/渠道时为 null) */
    public static Long currentSalesId() {
        return current().salesId;
    }

    /** 当前用户ID(可能为 null) */
    public static Long currentUserId() {
        return current().userId;
    }

    // ========== getter/setter ==========
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public List<String> getRoleKeys() { return roleKeys; }
    public void setRoleKeys(List<String> roleKeys) { this.roleKeys = roleKeys == null ? Collections.emptyList() : roleKeys; }
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }
}
