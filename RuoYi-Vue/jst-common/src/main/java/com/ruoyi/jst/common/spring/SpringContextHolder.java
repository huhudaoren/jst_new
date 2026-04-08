package com.ruoyi.jst.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationContext 静态持有器
 * <p>
 * 用途：让非 Spring 管理的对象(如 MyBatis TypeHandler)能拿到 Spring Bean
 * <p>
 * 启动时由 Spring 自动注入 ApplicationContext,后续静态访问。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHolder.applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring ApplicationContext 尚未初始化");
        }
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring ApplicationContext 尚未初始化");
        }
        return applicationContext.getBean(name, clazz);
    }
}
