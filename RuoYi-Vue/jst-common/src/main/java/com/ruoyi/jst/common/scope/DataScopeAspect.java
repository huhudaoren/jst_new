package com.ruoyi.jst.common.scope;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.utils.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * 数据权限切面：处理 @PartnerScope / @ChannelScope
 * <p>
 * 实现思路：
 * 1. 拦截带这两个注解的方法
 * 2. 检查当前登录用户角色 key
 * 3. 如果是 jst_partner / jst_channel，将 ReqDTO 中对应字段强制赋值为当前用户的 partnerId / channelId
 * 4. 平台运营 jst_platform_op 不做任何处理
 * <p>
 * 注意：当前用户的 partnerId / channelId 来源由 jst-user 模块的登录上下文提供，
 * 本切面通过 SecurityUtils.getLoginUser() 中扩展字段读取（首次实现 jst-user 时需补充扩展）。
 *
 * @author jst
 * @since 1.0.0
 */
@Aspect
@Component("jstDataScopeAspect")
public class DataScopeAspect {

    private static final Logger log = LoggerFactory.getLogger(DataScopeAspect.class);

    private static final String ROLE_PLATFORM_OP = "jst_platform_op";
    private static final String ROLE_PARTNER = "jst_partner";
    private static final String ROLE_CHANNEL = "jst_channel";

    @Around("@annotation(scope)")
    public Object aroundPartner(ProceedingJoinPoint pjp, PartnerScope scope) throws Throwable {
        if (!hasRole(ROLE_PARTNER) || hasRole(ROLE_PLATFORM_OP)) {
            return pjp.proceed();
        }
        Long partnerId = currentPartnerId();
        if (partnerId == null) {
            log.warn("[PartnerScope] 当前用户无 partnerId 上下文，跳过过滤");
            return pjp.proceed();
        }
        injectField(pjp.getArgs(), scope.field(), partnerId);
        return pjp.proceed();
    }

    @Around("@annotation(scope)")
    public Object aroundChannel(ProceedingJoinPoint pjp, ChannelScope scope) throws Throwable {
        if (!hasRole(ROLE_CHANNEL) || hasRole(ROLE_PLATFORM_OP)) {
            return pjp.proceed();
        }
        Long channelId = currentChannelId();
        if (channelId == null) {
            log.warn("[ChannelScope] 当前用户无 channelId 上下文，跳过过滤");
            return pjp.proceed();
        }
        injectField(pjp.getArgs(), scope.field(), channelId);
        return pjp.proceed();
    }

    /** 反射注入 ReqDTO 字段 */
    private void injectField(Object[] args, String fieldName, Object value) {
        if (args == null) return;
        for (Object arg : args) {
            if (arg == null) continue;
            try {
                Field f = findField(arg.getClass(), fieldName);
                if (f != null) {
                    f.setAccessible(true);
                    f.set(arg, value);
                    return;
                }
            } catch (Exception e) {
                log.error("[DataScope] 注入字段失败 field={} value={}", fieldName, value, e);
            }
        }
    }

    private Field findField(Class<?> clazz, String name) {
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignore) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    private boolean hasRole(String roleKey) {
        try {
            List<SysRole> roles = SecurityUtils.getLoginUser().getUser().getRoles();
            if (roles == null) return false;
            for (SysRole r : roles) {
                if (roleKey.equals(r.getRoleKey())) return true;
            }
        } catch (Exception ignore) {}
        return false;
    }

    /**
     * 获取当前登录赛事方 partnerId(从 JstLoginContext 读取)
     */
    private Long currentPartnerId() {
        return com.ruoyi.jst.common.context.JstLoginContext.currentPartnerId();
    }

    /**
     * 获取当前登录渠道方 channelId(从 JstLoginContext 读取)
     */
    private Long currentChannelId() {
        return com.ruoyi.jst.common.context.JstLoginContext.currentChannelId();
    }
}
