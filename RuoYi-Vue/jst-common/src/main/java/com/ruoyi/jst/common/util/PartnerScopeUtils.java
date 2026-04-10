package com.ruoyi.jst.common.util;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper methods shared by partner data-isolation infrastructure.
 */
public final class PartnerScopeUtils {

    public static final String ROLE_PARTNER = "jst_partner";
    public static final String ROLE_PLATFORM_OP = "jst_platform_op";
    private static final Pattern SQL_IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

    private PartnerScopeUtils() {
    }

    /**
     * Returns the current partner id from login context.
     */
    public static Long getCurrentPartnerId() {
        return JstLoginContext.currentPartnerId();
    }

    /**
     * Returns the current authenticated user, or {@code null} when unavailable.
     */
    public static LoginUser getLoginUserQuietly() {
        try {
            return com.ruoyi.common.utils.SecurityUtils.getLoginUser();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Ensures the current request is running under a partner account with a bound partner id.
     */
    public static Long requireCurrentPartnerId() {
        LoginUser loginUser = getLoginUserQuietly();
        if (!isPartnerRole(loginUser)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Long partnerId = getCurrentPartnerId();
        if (partnerId == null) {
            throw new ServiceException("当前赛事方账号未绑定 partnerId",
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return partnerId;
    }

    /**
     * Returns whether the login user contains the partner role.
     */
    public static boolean isPartnerRole(LoginUser loginUser) {
        return hasRole(loginUser, ROLE_PARTNER);
    }

    /**
     * Returns whether the login user is platform operator or super admin.
     */
    public static boolean isPlatformOp(LoginUser loginUser) {
        if (loginUser == null) {
            return false;
        }
        if (loginUser.getUser() != null && loginUser.getUser().isAdmin()) {
            return true;
        }
        return hasRole(loginUser, ROLE_PLATFORM_OP);
    }

    /**
     * Returns whether the login user has the given role.
     */
    public static boolean hasRole(LoginUser loginUser, String roleKey) {
        if (loginUser == null || loginUser.getUser() == null) {
            return false;
        }
        List<SysRole> roles = loginUser.getUser().getRoles();
        if (roles == null) {
            return false;
        }
        for (SysRole role : roles) {
            if (role != null && StringUtils.equals(roleKey, role.getRoleKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first {@link BaseEntity} argument from the current invocation.
     */
    public static BaseEntity findBaseEntity(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof BaseEntity) {
                return (BaseEntity) arg;
            }
        }
        return null;
    }

    /**
     * Builds the validated SQL snippet consumed by mapper XML.
     */
    public static String buildDataScopeSql(String deptAlias, String partnerIdColumn, Long partnerId) {
        if (partnerId == null) {
            throw new ServiceException("PartnerDataScope 缺少 partnerId",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        String column = requireIdentifier(partnerIdColumn, "partnerIdColumn");
        String qualifiedColumn = column;
        if (StringUtils.isNotBlank(deptAlias)) {
            qualifiedColumn = requireIdentifier(deptAlias, "deptAlias") + "." + column;
        }
        return " AND " + qualifiedColumn + " = " + partnerId + " ";
    }

    private static String requireIdentifier(String identifier, String fieldName) {
        String trimmed = StringUtils.trim(identifier);
        if (StringUtils.isBlank(trimmed) || !SQL_IDENTIFIER.matcher(trimmed).matches()) {
            throw new ServiceException("PartnerDataScope " + fieldName + " 非法: " + identifier,
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        return trimmed;
    }
}
