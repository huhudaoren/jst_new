package com.ruoyi.jst.common.security;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.Objects;

/**
 * 越权防御工具
 * <p>
 * 用于"按 ID 取详情/编辑/删除"接口在 Service 层的归属校验。
 * 强制：所有此类接口必须在 Service 方法第一行调用本类相应方法。
 * 参见 16-安全与敏感字段.md §4.1
 *
 * @author jst
 * @since 1.0.0
 */
public final class SecurityCheck {

    private SecurityCheck() {}

    /** 当前登录用户必须等于 targetUserId，否则抛 403 */
    public static void assertSameUser(Long targetUserId) {
        Long currentId = SecurityUtils.getUserId();
        if (!Objects.equals(currentId, targetUserId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    /** 当前登录用户必须是 admin，否则抛 403 */
    public static void assertAdmin() {
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }
}
