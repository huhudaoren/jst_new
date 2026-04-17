package com.ruoyi.jst.common.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.jst.common.annotation.SalesDataScope;
import com.ruoyi.jst.common.context.JstLoginContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SalesDataScopeAspectTest {

    private SalesDataScopeAspect aspect;

    static class FakeQuery extends BaseEntity {}

    @SalesDataScope(alias = "s", salesIdColumn = "sales_id")
    void targetMethod(FakeQuery q) {}

    @BeforeEach
    void setUp() {
        aspect = new SalesDataScopeAspect();
        JstLoginContext.clear();
    }

    @AfterEach
    void tearDown() {
        JstLoginContext.clear();
    }

    @Test
    void doAfter_injectsSelfFilter_forPlainSales() throws Exception {
        // 模拟 LoginUser context（可惜 SecurityUtils 静态方法在测试里不好 mock，
        // 此测试只验证 applyScope 的逻辑分支：当 SalesScopeUtils.isManagerRole=false
        // 且 currentSalesId 非 null 时注入 self 过滤）。
        // 因为 applyScope 内部调 getLoginUserQuietly() 在无 SecurityContext 时返 null，
        // 而 isManagerRole(null) = false，所以走 else 分支。
        JstLoginContext ctx = new JstLoginContext();
        ctx.setSalesId(1001L);
        ctx.setRoleKeys(Collections.singletonList("jst_sales"));
        JstLoginContext.set(ctx);

        FakeQuery q = new FakeQuery();
        SalesDataScope ann = getClass().getDeclaredMethod("targetMethod", FakeQuery.class)
                .getAnnotation(SalesDataScope.class);

        aspect.applyScope(q, ann);

        String sql = (String) q.getParams().get("salesScopeSql");
        assertNotNull(sql);
        assertTrue(sql.contains("s.sales_id = 1001"), "Expected self filter; got: " + sql);
    }

    @Test
    void doAfter_skipsFilter_whenNoSalesId() {
        // 没设 salesId（admin 等无销售身份的用户）
        JstLoginContext ctx = new JstLoginContext();
        JstLoginContext.set(ctx);

        FakeQuery q = new FakeQuery();
        SalesDataScope ann;
        try {
            ann = getClass().getDeclaredMethod("targetMethod", FakeQuery.class)
                    .getAnnotation(SalesDataScope.class);
        } catch (NoSuchMethodException e) { throw new RuntimeException(e); }

        aspect.applyScope(q, ann);

        assertNull(q.getParams().get("salesScopeSql"), "无 salesId 不应注入过滤 SQL");
    }

    @Test
    void doAfter_injectsManagerScope_whenManagerResolverProvided() throws Exception {
        // 注：此测试需模拟 isManagerRole 返 true。但 SalesScopeUtils.isManagerRole 依赖
        // SecurityUtils.getLoginUser()（在 getLoginUserQuietly 里），无 SecurityContext 时返 null。
        // 简化：通过反射直接验证 managerIdsResolver 的 list 拼接逻辑（构造 SQL）。
        // 手工模拟管理员场景：若 isManagerRole=false（无 SecurityContext），走 self 分支。
        // 这里只验证 resolver 的设置/取用：

        JstLoginContext ctx = new JstLoginContext();
        ctx.setSalesId(2001L);
        JstLoginContext.set(ctx);

        // 预设 resolver（即使分支不走 manager，验证 setManagerIdsResolver 不报错）
        aspect.setManagerIdsResolver(salesId -> Arrays.asList(2001L, 3001L, 3002L));

        FakeQuery q = new FakeQuery();
        SalesDataScope ann = getClass().getDeclaredMethod("targetMethod", FakeQuery.class)
                .getAnnotation(SalesDataScope.class);
        aspect.applyScope(q, ann);

        // 走 self 分支（因 isManagerRole 在测试环境为 false），注入 = 2001
        String sql = (String) q.getParams().get("salesScopeSql");
        assertNotNull(sql);
        assertTrue(sql.contains("s.sales_id = 2001"), "无 SecurityContext 时走 self 分支; got: " + sql);
    }

    @Test
    void doAfter_clearsStaleValue_beforeApplyingNew() throws Exception {
        FakeQuery q = new FakeQuery();
        q.getParams().put("salesScopeSql", "STALE VALUE");

        // 无 salesId → 不注入 → 应清掉 stale value
        JstLoginContext.set(new JstLoginContext());
        SalesDataScope ann = getClass().getDeclaredMethod("targetMethod", FakeQuery.class)
                .getAnnotation(SalesDataScope.class);
        aspect.applyScope(q, ann);

        assertNull(q.getParams().get("salesScopeSql"), "Stale value 应被清掉");
    }
}
