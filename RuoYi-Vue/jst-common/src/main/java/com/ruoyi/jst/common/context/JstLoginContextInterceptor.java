package com.ruoyi.jst.common.context;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录上下文拦截器
 * <p>
 * 在每个请求 preHandle 阶段:
 * 1. 从若依 SecurityContext 取出 LoginUser
 * 2. 调用 JstLoginEnricher 查询业务关联(partnerId/channelId)
 * 3. 填充 JstLoginContext ThreadLocal
 * 在 afterCompletion 阶段 clear ThreadLocal,防止线程池泄漏。
 * <p>
 * 注册位置:{@link JstLoginContextWebConfig}
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class JstLoginContextInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JstLoginContextInterceptor.class);

    /** 由 jst-user 实现注入,启动期可能为 null(单元测试场景) */
    @Autowired(required = false)
    private JstLoginEnricher enricher;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoginUser)) {
                return true;
            }
            LoginUser loginUser = (LoginUser) auth.getPrincipal();
            JstLoginContext ctx = new JstLoginContext();
            ctx.setUserId(loginUser.getUserId());

            // 从 LoginUser 取角色 keys
            if (loginUser.getUser() != null && loginUser.getUser().getRoles() != null) {
                List<String> keys = loginUser.getUser().getRoles().stream()
                        .map(SysRole::getRoleKey)
                        .collect(Collectors.toList());
                ctx.setRoleKeys(keys);
            }

            // 终端判定:URL 前缀 /jst/wx → wx,/jst/public → public,其他 → web
            String uri = request.getRequestURI();
            if (uri.startsWith("/jst/wx/")) ctx.setTerminal("wx");
            else if (uri.startsWith("/jst/public/")) ctx.setTerminal("public");
            else ctx.setTerminal("web");

            // 调用 enricher 充实业务字段
            if (enricher != null) {
                try {
                    enricher.enrich(loginUser.getUserId(), ctx);
                } catch (Exception e) {
                    log.error("[JstLoginContext] enricher 异常 userId={}", loginUser.getUserId(), e);
                }
            }

            JstLoginContext.set(ctx);
        } catch (Exception e) {
            log.error("[JstLoginContext] preHandle 异常,跳过填充", e);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        JstLoginContext.clear();
    }
}
