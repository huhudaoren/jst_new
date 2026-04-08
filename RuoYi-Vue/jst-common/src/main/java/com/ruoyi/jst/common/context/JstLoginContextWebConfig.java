package com.ruoyi.jst.common.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 JstLoginContextInterceptor 到所有 /jst/** 路径
 *
 * @author jst
 * @since 1.0.0
 */
@Configuration
public class JstLoginContextWebConfig implements WebMvcConfigurer {

    @Autowired
    private JstLoginContextInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/jst/**")
                .order(100);  // 在若依的 RepeatSubmitInterceptor 之后
    }
}
