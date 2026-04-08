package com.ruoyi.jst.common.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 赛事方数据权限注解
 * <p>
 * 标注在 Controller 方法上，由 DataScopeAspect 切面在查询前向 ReqDTO 注入 partnerId 过滤条件。
 * 当前登录用户角色为 jst_partner 时生效；平台运营 jst_platform_op 不受影响。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;PartnerScope(field = "partnerId")
 * &#64;GetMapping("/contest/list")
 * public TableDataInfo list(ContestQueryReqDTO query) { ... }
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartnerScope {
    /** ReqDTO 中的赛事方ID字段名 */
    String field() default "partnerId";
}
