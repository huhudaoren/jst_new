package com.ruoyi.jst.common.util;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.List;

/**
 * 销售身份与数据隔离工具类（复刻 PartnerScopeUtils 模式）。
 * <p>
 * 用法：
 * <ul>
 *   <li>{@link #currentSalesId()} — 取当前销售 ID（非销售返 null）</li>
 *   <li>{@link #currentSalesIdRequired()} — 强制要求是销售身份</li>
 *   <li>{@link #isPlainSalesOnly(LoginUser)} — 判断是否仅有 jst_sales 角色（用于金额脱敏）</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
public final class SalesScopeUtils {

    public static final String ROLE_SALES = "jst_sales";
    public static final String ROLE_SALES_MANAGER = "jst_sales_manager";
    public static final String ROLE_PLATFORM_OP = "jst_platform_op";
    public static final String ROLE_OPERATOR = "jst_operator";

    private SalesScopeUtils() {}

    /** 当前销售 ID（非销售返 null） */
    public static Long currentSalesId() {
        return JstLoginContext.currentSalesId();
    }

    /** 强制要求是销售身份 + 已绑定 sales_id */
    public static Long currentSalesIdRequired() {
        LoginUser loginUser = getLoginUserQuietly();
        if (!isSalesRole(loginUser)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Long salesId = currentSalesId();
        if (salesId == null) {
            throw new ServiceException("当前销售账号未绑定 salesId",
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return salesId;
    }

    /** 销售或销售主管 */
    public static boolean isSalesRole(LoginUser loginUser) {
        return hasRole(loginUser, ROLE_SALES) || hasRole(loginUser, ROLE_SALES_MANAGER);
    }

    /** 仅销售主管 */
    public static boolean isManagerRole(LoginUser loginUser) {
        return hasRole(loginUser, ROLE_SALES_MANAGER);
    }

    /** admin / 平台运营 / 销售主管：可看全量金额 */
    public static boolean isPlatformOpOrAdminOrManager(LoginUser loginUser) {
        if (loginUser == null) return false;
        if (loginUser.getUser() != null && loginUser.getUser().isAdmin()) return true;
        return hasRole(loginUser, ROLE_PLATFORM_OP)
                || hasRole(loginUser, ROLE_OPERATOR)
                || hasRole(loginUser, ROLE_SALES_MANAGER);
    }

    /** 当前用户是否仅有 jst_sales 角色（用于金额脱敏判断，主管不脱敏） */
    public static boolean isPlainSalesOnly(LoginUser loginUser) {
        return isSalesRole(loginUser)
                && !isPlatformOpOrAdminOrManager(loginUser);
    }

    /**
     * 销售身份读视图：
     * <ul>
     *   <li>销售 / 销售主管：返自己的 salesId</li>
     *   <li>admin / jst_operator / jst_platform_op：返 null（Service 层据此查全量）</li>
     *   <li>其他角色：抛 99902</li>
     * </ul>
     * 用于销售工作台的"列表/查询"类接口，让 admin 能无缝看到全量数据。
     */
    public static Long currentSalesIdOrAllowAdminView() {
        LoginUser u = getLoginUserQuietly();
        if (u != null && u.getUser() != null && u.getUser().isAdmin()) return null;
        if (hasRole(u, ROLE_PLATFORM_OP) || hasRole(u, ROLE_OPERATOR)) return null;
        return currentSalesIdRequired();
    }

    public static LoginUser getLoginUserQuietly() {
        try {
            return com.ruoyi.common.utils.SecurityUtils.getLoginUser();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean hasRole(LoginUser loginUser, String roleKey) {
        if (loginUser == null || loginUser.getUser() == null) return false;
        List<SysRole> roles = loginUser.getUser().getRoles();
        if (roles == null) return false;
        for (SysRole role : roles) {
            if (role != null && StringUtils.equals(roleKey, role.getRoleKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first {@link BaseEntity} argument from method invocation args.
     * Used by {@code SalesDataScopeAspect} (plan-02) to inject SQL filter snippets.
     */
    public static BaseEntity findBaseEntity(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof BaseEntity) {
                return (BaseEntity) arg;
            }
        }
        return null;
    }
}
