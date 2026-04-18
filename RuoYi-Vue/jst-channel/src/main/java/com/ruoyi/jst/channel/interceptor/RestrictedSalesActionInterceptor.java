package com.ruoyi.jst.channel.interceptor;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.service.SalesService;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 申请离职阶段 1 限权拦截器（spec §4.5 反带客户加固）。
 * <p>
 * 当销售 status = 'resign_apply' 时：
 * <ul>
 *   <li>✅ 保留：登录、看自己业绩、看渠道列表、写跟进记录、看跟进记录</li>
 *   <li>❌ 禁用：新建/续期预录入、导出渠道、导出跟进、改跟进任务负责人、接新渠道</li>
 * </ul>
 * 阶段 2 (resigned_pending_settle) 账号本身已被 sys_user 禁用，到不了拦截器。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class RestrictedSalesActionInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RestrictedSalesActionInterceptor.class);

    /** 阶段 1 禁用的 "METHOD URI_PATTERN" 组合。URI_PATTERN 用简单 contains 匹配。 */
    private static final List<String> BLOCKED = Arrays.asList(
        "POST /sales/me/pre-register",
        "POST /sales/me/pre-register/*/renew",
        "GET /sales/me/*/export",
        "GET /sales/me/followup/*/export"
    );

    @Autowired
    private SalesService salesService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/sales/me/")) return true;

        LoginUser u = SalesScopeUtils.getLoginUserQuietly();
        if (u == null) return true;
        if (!SalesScopeUtils.isSalesRole(u)) return true;

        Long salesId = SalesScopeUtils.currentSalesId();
        if (salesId == null) return true;
        JstSales s = salesService.getById(salesId);
        if (s == null || !"resign_apply".equals(s.getStatus())) return true;

        String method = request.getMethod();
        String key = method + " " + uri;
        for (String pattern : BLOCKED) {
            if (matches(pattern, key)) {
                log.warn("[Restricted] 申请离职阶段禁用操作 sales={} key={}", salesId, key);
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"msg\":\"申请离职阶段禁用此操作，请联系主管\"}");
                return false;
            }
        }
        return true;
    }

    /**
     * 简单模式匹配。示例 pattern "POST /sales/me/pre-register/(star)/renew"
     * 能匹配 "POST /sales/me/pre-register/123/renew"。
     * 仅支持 星号 通配任意非斜杠段。
     */
    private boolean matches(String pattern, String actual) {
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", "[^/\\s]+");
        return actual.matches(regex);
    }
}
