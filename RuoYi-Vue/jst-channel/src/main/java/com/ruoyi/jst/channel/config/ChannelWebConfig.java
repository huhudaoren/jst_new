package com.ruoyi.jst.channel.config;

import com.ruoyi.jst.channel.interceptor.RestrictedSalesActionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * jst-channel 模块 Web MVC 配置：注册 RestrictedSalesActionInterceptor。
 * <p>
 * 顺序 50（比 plan-01 SalesExportWatermarkInterceptor 的 100 更早），
 * 这样限权拦截先生效（返 403），再到水印拦截器；不会两个都命中。
 *
 * @author jst
 * @since 1.0.0
 */
@Configuration
public class ChannelWebConfig implements WebMvcConfigurer {

    @Autowired
    private RestrictedSalesActionInterceptor restrictedSalesActionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restrictedSalesActionInterceptor)
                .addPathPatterns("/sales/me/**")
                .order(50);
    }
}
