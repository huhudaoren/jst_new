package com.ruoyi.jst.common.interceptor;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 销售导出水印拦截器（AC3）。
 * <p>
 * 拦截路径：{@code /sales/&#42;&#42;/export}, {@code /sales/&#42;&#42;/download}
 * 等销售可触发的导出接口。
 * <p>
 * 行为：在响应 header 写入水印元信息（X-Export-Watermark），下游具体 export 服务读取后插入
 * Excel/PDF/CSV。同步写 sys_oper_log（business_type='SALES_EXPORT'），由若依 @Log 注解负责，
 * 本拦截器不重复写。
 * <p>
 * 注意：本类仅做 header 注入和工具方法，真正插入水印行的逻辑在 plan-04 的具体 export 接口里
 * 调用静态工具方法（buildWatermarkText / buildCsvWatermark）。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class SalesExportWatermarkInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SalesExportWatermarkInterceptor.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (uri == null) return true;
        if (!(uri.contains("/export") || uri.contains("/download"))) return true;
        if (!uri.startsWith("/sales/")) return true;

        LoginUser u = SalesScopeUtils.getLoginUserQuietly();
        if (u == null || u.getUser() == null) return true;
        if (!SalesScopeUtils.isSalesRole(u)) return true;

        String name = u.getUser().getNickName();
        String userNo = String.valueOf(u.getUser().getUserId());
        String watermark = buildWatermarkText(name, userNo, LocalDateTime.now());
        response.setHeader("X-Export-Watermark", watermark);
        log.info("[SalesExport] user={} uri={} watermarkAttached", name, uri);
        return true;
    }

    /** 通用水印文本（Excel 首行 / PDF 页脚） */
    public static String buildWatermarkText(String name, String userNo, LocalDateTime time) {
        return String.format("本表由销售[%s/%s]于[%s]导出，仅供内部使用，外泄追责",
                name, userNo, time.format(FMT));
    }

    /** CSV 注释行 */
    public static String buildCsvWatermark(String name, String userNo, LocalDateTime time) {
        return String.format("# Exported by %s(%s) at %s", name, userNo, time.format(FMT));
    }
}
