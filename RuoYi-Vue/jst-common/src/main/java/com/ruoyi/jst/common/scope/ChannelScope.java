package com.ruoyi.jst.common.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 渠道方数据权限注解
 * <p>
 * 标注在 Controller 方法上，由 DataScopeAspect 切面在查询前向 ReqDTO 注入 channelId 过滤条件。
 * 当前登录用户角色为 jst_channel 时生效；平台运营 jst_platform_op 不受影响。
 *
 * @author jst
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelScope {
    /** ReqDTO 中的渠道方ID字段名 */
    String field() default "channelId";
}
