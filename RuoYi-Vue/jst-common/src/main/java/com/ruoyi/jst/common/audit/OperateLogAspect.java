package com.ruoyi.jst.common.audit;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @OperateLog 切面
 * <p>
 * 拦截后异步写入 jst_audit_log。本类只负责采集，写库通过 AuditLogWriter（由 jst-risk 实现）完成。
 * 这里采用 ApplicationEventPublisher 解耦：jst-common 不能直接依赖 jst-risk 的 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
@Aspect
@Component
public class OperateLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperateLogAspect.class);
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint pjp, OperateLog operateLog) throws Throwable {
        long start = System.currentTimeMillis();
        String result = "success";
        Object retVal = null;
        Throwable error = null;

        try {
            retVal = pjp.proceed();
            return retVal;
        } catch (Throwable t) {
            result = "fail";
            error = t;
            throw t;
        } finally {
            try {
                MethodSignature sig = (MethodSignature) pjp.getSignature();
                Method method = sig.getMethod();

                String targetId = parseTarget(operateLog.target(), sig.getParameterNames(), pjp.getArgs());
                String paramsJson = operateLog.recordParams() ? safeJson(pjp.getArgs()) : null;
                String resultJson = operateLog.recordResult() && retVal != null ? safeJson(retVal) : null;

                Long operatorId = tryGetUserId();

                // TODO 由 jst-risk 模块订阅事件并落库 jst_audit_log
                log.info("[AuditLog] module={} action={} target={} operator={} result={} cost={}ms params={} result={}",
                        operateLog.module(), operateLog.action(), targetId,
                        operatorId, result, (System.currentTimeMillis() - start),
                        paramsJson, resultJson);

                if (error != null) {
                    log.warn("[AuditLog] error={}", error.getMessage());
                }
            } catch (Exception e) {
                log.error("[AuditLog] 审计切面自身异常", e);
            }
        }
    }

    private String parseTarget(String expr, String[] paramNames, Object[] args) {
        if (expr == null || expr.isEmpty() || paramNames == null) return null;
        try {
            // 简化：仅支持 #{paramName} 形式
            if (expr.startsWith("#{") && expr.endsWith("}")) {
                String name = expr.substring(2, expr.length() - 1);
                EvaluationContext ctx = new StandardEvaluationContext();
                for (int i = 0; i < paramNames.length; i++) {
                    ctx.setVariable(paramNames[i], args[i]);
                }
                Expression e = PARSER.parseExpression("#" + name);
                Object v = e.getValue(ctx);
                return v == null ? null : v.toString();
            }
            return expr;
        } catch (Exception ex) {
            return null;
        }
    }

    private String safeJson(Object o) {
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            return "[serialize_fail]";
        }
    }

    private Long tryGetUserId() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
