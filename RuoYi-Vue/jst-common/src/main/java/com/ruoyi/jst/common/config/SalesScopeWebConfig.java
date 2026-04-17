package com.ruoyi.jst.common.config;

import com.ruoyi.jst.common.interceptor.SalesExportWatermarkInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 销售模块 Web MVC 配置：注册 SalesExportWatermarkInterceptor 到 /sales/** 与 /admin/sales/** 路径。
 * <p>
 * 拦截器顺序 100，让其在大多数业务拦截器之后运行（仅在请求即将进入 Controller 前注入水印 header）。
 *
 * @author jst
 * @since 1.0.0
 */
@Configuration
public class SalesScopeWebConfig implements WebMvcConfigurer {

    @Autowired
    private SalesExportWatermarkInterceptor exportWatermarkInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(exportWatermarkInterceptor)
                .addPathPatterns("/sales/**", "/admin/sales/**")
                .order(100);
    }
}
