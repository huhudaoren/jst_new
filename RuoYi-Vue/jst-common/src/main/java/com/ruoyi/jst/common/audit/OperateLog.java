package com.ruoyi.jst.common.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务操作审计注解
 * <p>
 * 标注在 Controller 或 Service 方法上，由 OperateLogAspect 切面拦截后写入 jst_audit_log。
 * 注意：before/after JSON 在序列化前会经过 MaskUtil 脱敏（见 16-安全与敏感字段.md）。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;OperateLog(module = "订单", action = "REFUND_APPROVE", target = "#{refundId}")
 * &#64;PreAuthorize("@ss.hasPermi('jst:order:refund')")
 * public AjaxResult approveRefund(@RequestBody RefundApproveReqDTO req) { ... }
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {

    /** 模块名（如 "订单" "用户"） */
    String module();

    /** 动作名（如 REFUND_APPROVE / CLAIM_AUTO） */
    String action();

    /** 目标对象 ID 表达式（SpEL，可引用入参字段） */
    String target() default "";

    /** 是否记录入参 */
    boolean recordParams() default true;

    /** 是否记录返回值 */
    boolean recordResult() default false;
}
