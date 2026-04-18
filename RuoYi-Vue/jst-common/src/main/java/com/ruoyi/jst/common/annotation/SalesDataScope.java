package com.ruoyi.jst.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 销售数据隔离注解。
 * <p>
 * 用法：标在 Controller / Service 方法上，参数对象（继承 BaseEntity）会被注入
 * params.salesScopeSql SQL 片段：
 * <ul>
 *   <li>当前用户是 jst_sales（非主管）→ {@code AND {alias}.{column} = currentSalesId()}</li>
 *   <li>当前用户是 jst_sales_manager → {@code AND {alias}.{column} IN (主管 + 下属 ids)}</li>
 *   <li>当前用户是 admin / jst_operator → 不过滤（看全量）</li>
 * </ul>
 * Mapper XML 须消费 {@code ${params.salesScopeSql}}（注意是 ${}，因为是 SQL 片段而非参数）。
 *
 * @author jst
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SalesDataScope {
    /** SQL 表别名，如 "s" 对应 jst_sales s */
    String alias() default "";
    /** 销售 ID 列名 */
    String salesIdColumn() default "sales_id";
}
