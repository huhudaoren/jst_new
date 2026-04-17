package com.ruoyi.jst.common.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.annotation.SalesDataScope;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 注入销售级 SQL 过滤片段到 {@code BaseEntity.params.salesScopeSql}。
 * <p>
 * 主管/下属解析器由业务层注入 {@link #setManagerIdsResolver}，默认仅返回主管自己（用于测试隔离）。
 *
 * @author jst
 * @since 1.0.0
 */
@Aspect
@Component
public class SalesDataScopeAspect {

    public static final String SCOPE_KEY = "salesScopeSql";

    /** 主管 → 下属销售 ID 列表（含主管自己）的解析器，由业务 Service 注入。 */
    private Function<Long, List<Long>> managerIdsResolver = id -> Collections.singletonList(id);

    public void setManagerIdsResolver(Function<Long, List<Long>> resolver) {
        if (resolver != null) this.managerIdsResolver = resolver;
    }

    @Before("@annotation(scope)")
    public void doBefore(JoinPoint point, SalesDataScope scope) {
        BaseEntity entity = SalesScopeUtils.findBaseEntity(point.getArgs());
        if (entity != null) {
            applyScope(entity, scope);
        }
    }

    /** 暴露给单测调用 */
    public void applyScope(BaseEntity entity, SalesDataScope scope) {
        entity.getParams().remove(SCOPE_KEY);  // clear stale value

        LoginUser u = SalesScopeUtils.getLoginUserQuietly();
        // admin / jst_operator / jst_platform_op：不过滤（看全量）
        if (u != null && u.getUser() != null && u.getUser().isAdmin()) return;
        if (SalesScopeUtils.hasRole(u, SalesScopeUtils.ROLE_OPERATOR)
                || SalesScopeUtils.hasRole(u, SalesScopeUtils.ROLE_PLATFORM_OP)) return;

        Long salesId = JstLoginContext.currentSalesId();
        if (salesId == null) return;  // 没绑 sales_id 的角色不进入此过滤

        String alias = scope.alias();
        String column = scope.salesIdColumn();
        String qualified = (alias == null || alias.isEmpty()) ? column : alias + "." + column;

        String sql;
        if (SalesScopeUtils.isManagerRole(u)) {
            List<Long> ids = managerIdsResolver.apply(salesId);
            if (ids == null || ids.isEmpty()) ids = Collections.singletonList(salesId);
            sql = " AND " + qualified + " IN ("
                  + ids.stream().map(String::valueOf).collect(Collectors.joining(", "))
                  + ") ";
        } else {
            sql = " AND " + qualified + " = " + salesId + " ";
        }
        entity.getParams().put(SCOPE_KEY, sql);
    }
}
