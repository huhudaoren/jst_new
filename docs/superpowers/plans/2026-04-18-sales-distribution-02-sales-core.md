# 销售管理 + 渠道分销 - 计划 #2: 销售核心 + 提成管线 + Quartz

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 plan-01 基础设施之上，落地销售档案/费率/预录入的 Service+Controller，实现自动绑定算法（Step 1/2/3 + B1-B12 边界），落地提成计算管线（4 类提成项 + 单笔上限压缩 + 售后期等待 + 月结），落地 6 个 Quartz 定时任务，落地销售离职 3 阶段后端流程。

**Architecture:** Service 层放 jst-channel 模块；@SalesDataScope 注解 + AOP 复刻 PartnerDataScope 模式；提成计算监听 OrderPaidEvent 单事务批量计算；Quartz 任务复用 ruoyi-quartz 模块注册 SQL 模式。

**Tech Stack:** Spring + MyBatis + Quartz；JUnit 5 + Mockito；事件机制复用现有 `com.ruoyi.jst.common.event.OrderPaidEvent`。

**前置:** plan-01 已合并到当前分支基（DDL/角色/SalesScopeUtils/Sensitive 注解/MaskAspect/ExportWatermark/12 个 Domain+Mapper 全部就位）。

**关联 spec:** `docs/superpowers/specs/2026-04-18-sales-channel-distribution-design.md` §2 / §3 / §4.4-4.5

---

## 文件结构

### Java（jst-channel/src/main/java/com/ruoyi/jst/channel/）
**service/**
- `SalesService.java`（销售档案 CRUD + 状态机 + 主管设置）
- `SalesService.java` 实现 `impl/SalesServiceImpl.java`
- `SalesCommissionRateService.java` + impl
- `SalesPreRegisterService.java` + impl
- `SalesAutoBindingService.java` + impl（自动绑定算法核心）
- `SalesChannelBindingService.java` + impl（version-tracked binding 操作）
- `SalesCommissionService.java` + impl（提成计算核心）
- `SalesCommissionSettlementService.java` + impl（月结）
- `SalesResignationService.java` + impl（离职 3 阶段编排）

**aspect/**
- `SalesDataScopeAspect.java`（@SalesDataScope AOP，注入 SQL 过滤）

**annotation/** (jst-common)
- `SalesDataScope.java`（注解定义，复刻 PartnerDataScope）

**listener/**
- `SalesCommissionListener.java`（监听 OrderPaidEvent + OrderRefundedEvent）
- `SalesAutoBindingListener.java`（监听 ChannelRegisteredEvent）

**event/** (jst-common)
- `ChannelRegisteredEvent.java`（新事件）

**controller/admin/**
- `AdminSalesController.java`（销售档案 CRUD + 离职流程）

**controller/sales/**
- `SalesPreRegisterController.java`（销售本人预录入 CRUD）
- `SalesPerformanceController.java`（销售本人看业绩聚合）

**interceptor/** (jst-common)
- `RestrictedSalesActionInterceptor.java`（resign_apply 阶段限权）

**job/** (ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/)
- `SalesCommissionAccrueJob.java`
- `SalesCommissionRepairJob.java`
- `SalesPreRegisterExpireJob.java`
- `SalesMonthlySettlementJob.java`
- `SalesResignExecuteJob.java`
- `SalesTransitionEndJob.java`

### SQL
- 新建 `架构设计/ddl/99-migration-sales-distribution-quartz.sql`（6 个 Quartz 任务注册）

### Mapper XML 增量
- 修改 `JstSalesMapper.xml`、`JstSalesPreRegisterMapper.xml`、`JstSalesChannelBindingMapper.xml`、`JstSalesCommissionLedgerMapper.xml`、`JstSalesCommissionSettlementMapper.xml`、`JstSalesCommissionRateMapper.xml` — 追加业务查询语句

### 测试
- 14 个新测试类于 `RuoYi-Vue/jst-channel/src/test/java/com/ruoyi/jst/channel/`

### CLAUDE.md
- 修改顶部"上次更新"行 + §六 SALES-DISTRIBUTION 行（标记 plan-02 完成）

---

## Task 1: 准备分支 + 检查 plan-01 已就位

- [ ] **Step 1: 拉最新 main 并开新分支**

```bash
cd D:/coding/jst_v1
git checkout main
git pull
git checkout -b feature/sales-distribution-02-sales-core
```

- [ ] **Step 2: 验证 plan-01 产物存在（DDL/角色/Java 类）**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SHOW TABLES LIKE 'jst_sales%';
SELECT role_id FROM sys_role WHERE role_id IN (501, 502);
" jst
ls D:/coding/jst_v1/RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesScopeUtils.java
ls D:/coding/jst_v1/RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/MaskSalaryAspect.java
ls D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSales.java
```
Expected: 12 张表存在 / 2 角色存在 / 3 个 java 文件存在。如果不存在则 plan-01 未合并，必须先合并。

---

## Task 2: @SalesDataScope 注解 + SalesDataScopeAspect 切面

> 复刻 PartnerDataScope 模式，给 Mapper SQL 注入 `AND sales_id = ?` 过滤。销售本人查询时自动过滤；admin/主管不过滤但传入下属 ID 列表。

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/annotation/SalesDataScope.java`
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspectj/SalesDataScopeAspect.java`
- Create: `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/aspectj/SalesDataScopeAspectTest.java`

- [ ] **Step 1: 读现有 PartnerDataScope.java 和 Aspect 作为参考**

```bash
cat RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/annotation/PartnerDataScope.java
cat RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspectj/PartnerDataScopeAspect.java
```
Expected: 输出参考实现，理解 @PartnerDataScope 注入 `AND xxx = ?` 的模式。

- [ ] **Step 2: 创建 SalesDataScope 注解**

```java
package com.ruoyi.jst.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 销售数据隔离注解。
 * <p>
 * 用法：标在 Controller 方法上，参数对象（继承 BaseEntity）会被注入 params.dataScope SQL 片段：
 * <ul>
 *   <li>当前用户是 jst_sales（非主管）→ AND {alias}.{column} = currentSalesId()</li>
 *   <li>当前用户是 jst_sales_manager → AND {alias}.{column} IN (manager.ids + 下属销售 ids)</li>
 *   <li>当前用户是 admin / jst_operator → 不过滤（看全量）</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SalesDataScope {
    /** SQL 表别名，如 "s" 对应 jst_sales s */
    String alias() default "";
    /** 销售 ID 列名 */
    String salesIdColumn() default "sales_id";
}
```

- [ ] **Step 3: 写 SalesDataScopeAspectTest（参考 PartnerDataScopeAspectTest 结构）**

```java
package com.ruoyi.jst.common.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.annotation.SalesDataScope;
import com.ruoyi.jst.common.context.JstLoginContext;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertTrue(sql.contains("s.sales_id = 1001"));
    }

    @Test
    void doAfter_skipsFilter_forAdmin() throws Exception {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setRoleKeys(Collections.singletonList("admin"));
        JstLoginContext.set(ctx);

        FakeQuery q = new FakeQuery();
        SalesDataScope ann = getClass().getDeclaredMethod("targetMethod", FakeQuery.class)
                .getAnnotation(SalesDataScope.class);

        aspect.applyScope(q, ann);

        assertNull(q.getParams().get("salesScopeSql"),
                "admin 不应注入过滤 SQL");
    }

    @Test
    void doAfter_injectsManagerScope_forManager() throws Exception {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setSalesId(2001L);
        ctx.setRoleKeys(Collections.singletonList("jst_sales_manager"));
        JstLoginContext.set(ctx);

        FakeQuery q = new FakeQuery();
        SalesDataScope ann = getClass().getDeclaredMethod("targetMethod", FakeQuery.class)
                .getAnnotation(SalesDataScope.class);

        // 模拟 manager 下属 ids = [2001(自己), 3001, 3002]
        aspect.setManagerIdsResolver(salesId -> Arrays.asList(2001L, 3001L, 3002L));
        aspect.applyScope(q, ann);

        String sql = (String) q.getParams().get("salesScopeSql");
        assertNotNull(sql);
        assertTrue(sql.contains("s.sales_id IN (2001, 3001, 3002)"));
    }
}
```

- [ ] **Step 4: 实现 SalesDataScopeAspect**

```java
package com.ruoyi.jst.common.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.annotation.SalesDataScope;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Aspect
@Component
public class SalesDataScopeAspect {

    /** 主管 → 下属销售 ID 列表（含主管自己）的解析器，由业务 Service 注入 */
    private Function<Long, List<Long>> managerIdsResolver = id -> java.util.Collections.singletonList(id);

    public void setManagerIdsResolver(Function<Long, List<Long>> resolver) {
        if (resolver != null) this.managerIdsResolver = resolver;
    }

    @Before("@annotation(scope)")
    public void doBefore(JoinPoint point, SalesDataScope scope) {
        BaseEntity be = SalesScopeUtils.findBaseEntity(point.getArgs());
        if (be == null) return;
        applyScope(be, scope);
    }

    /** 暴露给单测调用 */
    public void applyScope(BaseEntity entity, SalesDataScope scope) {
        LoginUser u = SalesScopeUtils.getLoginUserQuietly();
        if (u != null && u.getUser() != null && u.getUser().isAdmin()) return;
        if (SalesScopeUtils.hasRole(u, "jst_operator")
                || SalesScopeUtils.hasRole(u, SalesScopeUtils.ROLE_PLATFORM_OP)) return;

        Long salesId = JstLoginContext.currentSalesId();
        if (salesId == null) return;  // 没绑 sales_id 的角色不进入此过滤（业务层应另行拒绝）

        String alias = scope.alias();
        String column = scope.salesIdColumn();
        String qualified = (alias == null || alias.isEmpty()) ? column : alias + "." + column;

        String sql;
        if (SalesScopeUtils.isManagerRole(u)) {
            List<Long> ids = managerIdsResolver.apply(salesId);
            sql = " AND " + qualified + " IN (" 
                  + ids.stream().map(String::valueOf).collect(Collectors.joining(", ")) 
                  + ") ";
        } else {
            sql = " AND " + qualified + " = " + salesId + " ";
        }
        entity.getParams().put("salesScopeSql", sql);
    }
}
```

> 注：`SalesScopeUtils.findBaseEntity()` 复用 `PartnerScopeUtils.findBaseEntity()`，需要在 SalesScopeUtils 加一个 `findBaseEntity(Object[] args)` static 方法。如果 plan-01 没加，本任务追加：

补到 plan-01 的 `SalesScopeUtils`：
```java
public static com.ruoyi.common.core.domain.BaseEntity findBaseEntity(Object[] args) {
    if (args == null) return null;
    for (Object arg : args) {
        if (arg instanceof com.ruoyi.common.core.domain.BaseEntity) {
            return (com.ruoyi.common.core.domain.BaseEntity) arg;
        }
    }
    return null;
}
```

- [ ] **Step 5: 运行测试**

```bash
mvn -pl RuoYi-Vue/jst-common test -Dtest=SalesDataScopeAspectTest -q
```
Expected: BUILD SUCCESS, Tests: 3。

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/annotation/SalesDataScope.java \
        RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspectj/SalesDataScopeAspect.java \
        RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/aspectj/SalesDataScopeAspectTest.java \
        RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesScopeUtils.java
git commit -m "feat(common): @SalesDataScope 注解 + Aspect (复刻 PartnerDataScope，支持 self/manager 双模式)"
```

---

## Task 3: SalesService（销售档案 CRUD + 状态机基础操作）+ 单测

**Files:**
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/SalesService.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/SalesServiceImpl.java`
- Create: `RuoYi-Vue/jst-channel/src/test/java/com/ruoyi/jst/channel/service/SalesServiceImplTest.java`
- Modify: `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSalesMapper.xml`（追加 selectBySysUserId / selectBySalesNo / selectByManagerId / countByStatus）

- [ ] **Step 1: 写 Mapper XML 追加查询**

在 `JstSalesMapper.xml` 现有 `</mapper>` 前追加：

```xml
    <select id="selectBySysUserId" parameterType="Long" resultMap="JstSalesResult">
        SELECT <include refid="cols"/> FROM jst_sales WHERE sys_user_id = #{sysUserId}
    </select>

    <select id="selectBySalesNo" parameterType="String" resultMap="JstSalesResult">
        SELECT <include refid="cols"/> FROM jst_sales WHERE sales_no = #{salesNo}
    </select>

    <select id="selectByManagerId" parameterType="Long" resultMap="JstSalesResult">
        SELECT <include refid="cols"/> FROM jst_sales 
        WHERE manager_id = #{managerId} AND status IN ('active','resign_apply','resigned_pending_settle')
        ORDER BY create_time DESC
    </select>

    <select id="selectActiveTeamIds" parameterType="Long" resultType="Long">
        SELECT sales_id FROM jst_sales 
        WHERE (sales_id = #{managerId} OR manager_id = #{managerId})
          AND status IN ('active','resign_apply','resigned_pending_settle')
    </select>
```

并在 `JstSalesMapper.java` 接口加：
```java
JstSales selectBySysUserId(@org.apache.ibatis.annotations.Param("sysUserId") Long sysUserId);
JstSales selectBySalesNo(@org.apache.ibatis.annotations.Param("salesNo") String salesNo);
List<JstSales> selectByManagerId(@org.apache.ibatis.annotations.Param("managerId") Long managerId);
List<Long> selectActiveTeamIds(@org.apache.ibatis.annotations.Param("managerId") Long managerId);
```

- [ ] **Step 2: 写 SalesService 接口**

```java
package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.domain.JstSales;
import java.util.List;

public interface SalesService {

    JstSales getById(Long salesId);
    JstSales getBySysUserId(Long sysUserId);
    JstSales getBySalesNo(String salesNo);
    List<JstSales> listSubordinates(Long managerId);
    /** 主管的可见销售 ID 列表（含自己）；普通销售返自己；admin 返 null */
    List<Long> resolveScopeSalesIds(Long currentSalesId);

    /** 创建销售（必须先有 sys_user，自动生成 sales_no） */
    JstSales create(JstSales row);
    /** 修改默认费率（仅 admin 调） */
    void updateDefaultRate(Long salesId, java.math.BigDecimal rate);
    /** 设置主管 */
    void updateManager(Long salesId, Long managerId);

    /** 状态推进 */
    void applyResign(Long salesId, java.util.Date expectedResignDate);
    void executeResign(Long salesId);
    void endTransition(Long salesId);
}
```

- [ ] **Step 3: 写 SalesServiceImplTest（先红）**

```java
package com.ruoyi.jst.channel.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.impl.SalesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesServiceImplTest {

    @Mock JstSalesMapper salesMapper;
    @InjectMocks SalesServiceImpl service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void create_assignsSalesNo_andDefaultStatus() {
        JstSales in = new JstSales();
        in.setSysUserId(100L);
        in.setSalesName("张三");
        in.setPhone("13900000001");
        in.setCommissionRateDefault(new BigDecimal("0.05"));
        when(salesMapper.selectBySysUserId(100L)).thenReturn(null);
        when(salesMapper.insertJstSales(any())).thenReturn(1);

        JstSales out = service.create(in);

        assertNotNull(out.getSalesId());
        assertNotNull(out.getSalesNo());
        assertTrue(out.getSalesNo().startsWith("S"));
        assertEquals("active", out.getStatus());
        assertEquals(0, out.getIsManager());
    }

    @Test
    void create_throws_whenSysUserAlreadyMapped() {
        JstSales existing = new JstSales();
        existing.setSysUserId(100L);
        when(salesMapper.selectBySysUserId(100L)).thenReturn(existing);

        JstSales in = new JstSales();
        in.setSysUserId(100L);
        assertThrows(ServiceException.class, () -> service.create(in));
    }

    @Test
    void resolveScopeSalesIds_returnsSelf_forPlainSales() {
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(salesWith(1001L, false));

        var ids = service.resolveScopeSalesIds(1001L);
        assertEquals(Arrays.asList(1001L), ids);
    }

    @Test
    void resolveScopeSalesIds_returnsTeam_forManager() {
        when(salesMapper.selectJstSalesBySalesId(2001L)).thenReturn(salesWith(2001L, true));
        when(salesMapper.selectActiveTeamIds(2001L)).thenReturn(Arrays.asList(2001L, 3001L, 3002L));

        var ids = service.resolveScopeSalesIds(2001L);
        assertEquals(Arrays.asList(2001L, 3001L, 3002L), ids);
    }

    @Test
    void applyResign_setsStatusAndDates() {
        JstSales s = salesWith(1001L, false);
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);
        Date expected = new Date(System.currentTimeMillis() + 86400_000L * 5);

        service.applyResign(1001L, expected);

        verify(salesMapper).updateJstSales(argThat(row ->
            "resign_apply".equals(row.getStatus())
            && row.getResignApplyDate() != null
            && row.getExpectedResignDate().equals(expected)
        ));
    }

    @Test
    void applyResign_throws_whenIntervalExceedsMax() {
        JstSales s = salesWith(1001L, false);
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);
        // sysParam max_days 默认 7，传一个 30 天后的日期
        Date tooFar = new Date(System.currentTimeMillis() + 86400_000L * 30);
        // 假设 service 注入了 sysParam，本测试通过 setMaxDays 覆盖
        service.setResignMaxDays(7);

        assertThrows(ServiceException.class, () -> service.applyResign(1001L, tooFar));
    }

    @Test
    void executeResign_setsStatusToPendingSettle_andCalculatesTransitionEnd() {
        JstSales s = salesWith(1001L, false);
        s.setStatus("resign_apply");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);
        service.setTransitionMonths(3);

        service.executeResign(1001L);

        verify(salesMapper).updateJstSales(argThat(row ->
            "resigned_pending_settle".equals(row.getStatus())
            && row.getActualResignDate() != null
            && row.getTransitionEndDate() != null
        ));
    }

    @Test
    void endTransition_setsFinalStatus_onlyForPendingSettle() {
        JstSales s = salesWith(1001L, false);
        s.setStatus("resigned_pending_settle");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);

        service.endTransition(1001L);

        verify(salesMapper).updateJstSales(argThat(row -> "resigned_final".equals(row.getStatus())));
    }

    @Test
    void endTransition_throws_whenStatusNotPendingSettle() {
        JstSales s = salesWith(1001L, false);
        s.setStatus("active");
        when(salesMapper.selectJstSalesBySalesId(1001L)).thenReturn(s);

        assertThrows(ServiceException.class, () -> service.endTransition(1001L));
    }

    private JstSales salesWith(Long id, boolean isManager) {
        JstSales s = new JstSales();
        s.setSalesId(id);
        s.setIsManager(isManager ? 1 : 0);
        s.setStatus("active");
        return s;
    }
}
```

- [ ] **Step 4: 跑测试，验证失败**

```bash
mvn -pl RuoYi-Vue/jst-channel test -Dtest=SalesServiceImplTest -q
```
Expected: 编译失败 — SalesServiceImpl 不存在。

- [ ] **Step 5: 实现 SalesServiceImpl**

```java
package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.mapper.JstSalesMapper;
import com.ruoyi.jst.channel.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class SalesServiceImpl implements SalesService {

    @Autowired
    private JstSalesMapper salesMapper;

    @Value("${jst.sales.resign_apply_max_days:7}")
    private int resignMaxDays;

    @Value("${jst.sales.transition_months:3}")
    private int transitionMonths;

    public void setResignMaxDays(int v) { this.resignMaxDays = v; }
    public void setTransitionMonths(int v) { this.transitionMonths = v; }

    @Override
    public JstSales getById(Long salesId) {
        return salesMapper.selectJstSalesBySalesId(salesId);
    }

    @Override
    public JstSales getBySysUserId(Long sysUserId) {
        return salesMapper.selectBySysUserId(sysUserId);
    }

    @Override
    public JstSales getBySalesNo(String salesNo) {
        return salesMapper.selectBySalesNo(salesNo);
    }

    @Override
    public List<JstSales> listSubordinates(Long managerId) {
        return salesMapper.selectByManagerId(managerId);
    }

    @Override
    public List<Long> resolveScopeSalesIds(Long currentSalesId) {
        if (currentSalesId == null) return null;
        JstSales me = salesMapper.selectJstSalesBySalesId(currentSalesId);
        if (me == null) return Collections.singletonList(currentSalesId);
        if (me.getIsManager() != null && me.getIsManager() == 1) {
            return salesMapper.selectActiveTeamIds(currentSalesId);
        }
        return Collections.singletonList(currentSalesId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JstSales create(JstSales row) {
        if (row.getSysUserId() == null) {
            throw new ServiceException("缺少 sys_user_id");
        }
        if (salesMapper.selectBySysUserId(row.getSysUserId()) != null) {
            throw new ServiceException("该 sys_user 已是销售");
        }
        if (row.getSalesId() == null) row.setSalesId(snowId());
        if (row.getSalesNo() == null) row.setSalesNo(generateSalesNo());
        if (row.getStatus() == null) row.setStatus("active");
        if (row.getIsManager() == null) row.setIsManager(0);
        if (row.getCommissionRateDefault() == null) row.setCommissionRateDefault(new BigDecimal("0.0500"));
        if (row.getManagerCommissionRate() == null) row.setManagerCommissionRate(BigDecimal.ZERO);
        salesMapper.insertJstSales(row);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultRate(Long salesId, BigDecimal rate) {
        JstSales s = requireSales(salesId);
        s.setCommissionRateDefault(rate);
        salesMapper.updateJstSales(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateManager(Long salesId, Long managerId) {
        JstSales s = requireSales(salesId);
        if (managerId != null) {
            JstSales mgr = requireSales(managerId);
            if (mgr.getIsManager() == null || mgr.getIsManager() != 1) {
                throw new ServiceException("目标不是主管");
            }
        }
        s.setManagerId(managerId);
        salesMapper.updateJstSales(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyResign(Long salesId, Date expectedResignDate) {
        JstSales s = requireSales(salesId);
        if (!"active".equals(s.getStatus())) {
            throw new ServiceException("仅在职销售可申请离职");
        }
        if (expectedResignDate == null) throw new ServiceException("缺少预期离职日");
        Date now = new Date();
        long diffDays = (expectedResignDate.getTime() - now.getTime()) / 86400_000L;
        if (diffDays > resignMaxDays) {
            throw new ServiceException("预期离职日不能超过 " + resignMaxDays + " 天后");
        }
        if (diffDays < 0) throw new ServiceException("预期离职日不能早于今天");
        s.setStatus("resign_apply");
        s.setResignApplyDate(now);
        s.setExpectedResignDate(expectedResignDate);
        salesMapper.updateJstSales(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeResign(Long salesId) {
        JstSales s = requireSales(salesId);
        if (!"active".equals(s.getStatus()) && !"resign_apply".equals(s.getStatus())) {
            throw new ServiceException("当前状态不能执行离职");
        }
        Date now = new Date();
        s.setStatus("resigned_pending_settle");
        s.setActualResignDate(now);
        LocalDateTime endTime = now.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMonths(transitionMonths);
        s.setTransitionEndDate(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        salesMapper.updateJstSales(s);
        // 后续：触发 SalesResignedEvent 让 Listener 处理 binding 转移、pre_register 失效、sys_user 禁用
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endTransition(Long salesId) {
        JstSales s = requireSales(salesId);
        if (!"resigned_pending_settle".equals(s.getStatus())) {
            throw new ServiceException("仅过渡期内销售可结束过渡");
        }
        s.setStatus("resigned_final");
        salesMapper.updateJstSales(s);
    }

    private JstSales requireSales(Long salesId) {
        JstSales s = salesMapper.selectJstSalesBySalesId(salesId);
        if (s == null) throw new ServiceException("销售不存在: " + salesId);
        return s;
    }

    private Long snowId() {
        return Long.parseLong(IdUtils.fastSimpleUUID().substring(0, 16), 16) & Long.MAX_VALUE;
    }

    private String generateSalesNo() {
        // 复用既有 BizNoRule 引擎（计划 #1 之外，若引擎不可用则使用简单 fallback）
        return "S" + new java.text.SimpleDateFormat("yyMM").format(new Date())
                + String.format("%04d", new Random().nextInt(10000));
    }
}
```

- [ ] **Step 6: 跑测试**

```bash
mvn -pl RuoYi-Vue/jst-channel test -Dtest=SalesServiceImplTest -q
```
Expected: BUILD SUCCESS, Tests: 9。

- [ ] **Step 7: 提交**

```bash
git add RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/SalesService.java \
        RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/SalesServiceImpl.java \
        RuoYi-Vue/jst-channel/src/test/java/com/ruoyi/jst/channel/service/SalesServiceImplTest.java \
        RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/JstSalesMapper.java \
        RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSalesMapper.xml
git commit -m "feat(channel): SalesService CRUD + 状态机（applyResign/executeResign/endTransition）+ 9 单测"
```

---

## Task 4: SalesCommissionRateService（按业务类型查 effective_from 最新费率）+ 单测

**Files:**
- Create: `service/SalesCommissionRateService.java` + `impl/SalesCommissionRateServiceImpl.java`
- Modify: `JstSalesCommissionRateMapper.xml` 加 `selectLatestRate`
- Create: 单测 `SalesCommissionRateServiceImplTest.java`

- [ ] **Step 1: Mapper XML 追加**

```xml
    <select id="selectLatestRate" resultType="java.math.BigDecimal">
        SELECT rate FROM jst_sales_commission_rate
         WHERE sales_id = #{salesId} 
           AND business_type = #{businessType}
           AND effective_from &lt;= #{atTime}
         ORDER BY effective_from DESC LIMIT 1
    </select>
```

加 Mapper 接口方法：
```java
java.math.BigDecimal selectLatestRate(@Param("salesId") Long salesId,
                                       @Param("businessType") String businessType,
                                       @Param("atTime") java.util.Date atTime);
```

- [ ] **Step 2: 写 SalesCommissionRateService 接口 + 测试**

```java
public interface SalesCommissionRateService {
    /** 查销售在某业务类型在某时刻的费率，回退默认 commission_rate_default */
    BigDecimal resolveRate(Long salesId, String businessType, Date atTime);
    /** 设置某业务类型的费率（写入 effective_from 行） */
    void upsertRate(Long salesId, String businessType, BigDecimal rate);
}
```

测试（5 case）：
- resolveRate 命中明细 → 返明细
- resolveRate 未命中明细 → 回退 SalesService.getById().commissionRateDefault
- resolveRate 命中多条历史 → 取 effective_from <= atTime 的最新
- resolveRate sales 不存在 → 抛 ServiceException
- upsertRate 写入新行

- [ ] **Step 3-5: 实现 + 跑测试 + 提交**

实现按测试驱动写。提交：
```bash
git commit -m "feat(channel): SalesCommissionRateService 按业务类型 + effective_from 解析费率 + 5 单测"
```

---

## Task 5: SalesPreRegisterService + Quartz 过期任务 + 单测

**Files:**
- Create: `service/SalesPreRegisterService.java` + impl
- Modify: `JstSalesPreRegisterMapper.xml` 加 `selectExpiringForJob`、`selectByPhonePending`、`countActiveBySales`
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesPreRegisterExpireJob.java`
- Create: 单测 `SalesPreRegisterServiceImplTest.java`

- [ ] **Step 1: Mapper XML 追加查询**

```xml
    <select id="selectByPhonePending" resultMap="JstSalesPreRegisterResult">
        SELECT <include refid="cols"/> FROM jst_sales_pre_register
         WHERE phone = #{phone} AND status = 'pending' LIMIT 1
    </select>

    <select id="selectExpiredPending" resultType="Long">
        SELECT pre_id FROM jst_sales_pre_register
         WHERE status = 'pending' AND expire_at &lt;= NOW()
         LIMIT 1000
    </select>

    <update id="markExpiredBatch">
        UPDATE jst_sales_pre_register SET status = 'expired' 
        WHERE pre_id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
        AND status = 'pending'
    </update>

    <select id="countActiveBySales" resultType="int">
        SELECT COUNT(*) FROM jst_sales_pre_register
         WHERE sales_id = #{salesId} AND status = 'pending'
    </select>
```

加对应 Mapper 接口方法。

- [ ] **Step 2: SalesPreRegisterService 接口**

```java
public interface SalesPreRegisterService {
    /** 销售新建预录入（A1/A2/A4/A5/A10 校验） */
    JstSalesPreRegister create(Long salesId, String phone, String targetName);
    /** 续期 +90 天（A7，最多 2 次） */
    void renew(Long preId, Long currentSalesId);
    /** 销售自己删除（A9，仅 pending） */
    void remove(Long preId, Long currentSalesId);
    /** 列表（销售视角，按 SalesScope 过滤） */
    List<JstSalesPreRegister> listMine(Long salesId);
    /** 取一行 pending（自动绑定 Step 1 用） */
    JstSalesPreRegister findActiveByPhone(String phone);
    /** 标记已匹配 */
    void markMatched(Long preId, Long channelId);
    /** Quartz 任务用：批量失效已过期 */
    int expirePendingByCron();
}
```

- [ ] **Step 3: 写测试 8 个 case**

涵盖：
- create 成功（写入 expire_at = now + 90 天）
- create 重复手机号（A2，`selectByPhonePending` 命中已存在 → 抛 ServiceException）
- create 手机号是已绑定渠道（A4 补录）—— 这一支 service 内调用 SalesAutoBindingService，本测试用 mock 隔离
- create 手机号是销售本人手机号（A10）→ 抛
- renew 成功 expire_at +90 天 + renew_count + 1
- renew 已达 2 次（A7）→ 抛
- remove 成功（仅 pending）
- remove 别人的预录入（鉴权 currentSalesId != row.salesId）→ 抛
- expirePendingByCron 批量更新 N 行

实现 + 跑通测试。

- [ ] **Step 4: SalesPreRegisterExpireJob**

```java
package com.ruoyi.quartz.task;

import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("salesPreRegisterExpireJob")
public class SalesPreRegisterExpireJob {

    @Autowired
    private SalesPreRegisterService preRegisterService;

    /** Quartz 调用入口（每天 01:00） */
    public void execute() {
        int n = preRegisterService.expirePendingByCron();
        org.slf4j.LoggerFactory.getLogger(getClass())
                .info("[SalesPreRegisterExpireJob] 失效 {} 条预录入", n);
    }
}
```

- [ ] **Step 5: 提交**

```bash
git commit -m "feat(channel): SalesPreRegisterService (A1-A10 边界) + 90 天过期 Quartz Job + 8 单测"
```

---

## Task 6: SalesChannelBindingService（版本化绑定 + 转移）+ 单测

**Files:**
- Create: `service/SalesChannelBindingService.java` + impl
- Modify: `JstSalesChannelBindingMapper.xml` 加 `selectCurrentByChannelId`、`selectAtTime`、`closeBinding`、`selectCurrentByOwnerSales`

- [ ] **Step 1: Mapper XML 追加**

```xml
    <select id="selectCurrentByChannelId" parameterType="Long" resultMap="JstSalesChannelBindingResult">
        SELECT <include refid="cols"/> FROM jst_sales_channel_binding
         WHERE channel_id = #{channelId} AND effective_to IS NULL LIMIT 1
    </select>

    <select id="selectAtTime" resultMap="JstSalesChannelBindingResult">
        SELECT <include refid="cols"/> FROM jst_sales_channel_binding
         WHERE channel_id = #{channelId}
           AND effective_from &lt;= #{atTime}
           AND (effective_to IS NULL OR effective_to &gt; #{atTime})
         LIMIT 1
    </select>

    <update id="closeBinding">
        UPDATE jst_sales_channel_binding SET effective_to = #{closeAt}
         WHERE binding_id = #{bindingId} AND effective_to IS NULL
    </update>

    <select id="selectCurrentByOwnerSales" parameterType="Long" resultMap="JstSalesChannelBindingResult">
        SELECT <include refid="cols"/> FROM jst_sales_channel_binding
         WHERE sales_id = #{salesId} AND effective_to IS NULL
    </select>
```

加 Mapper 接口方法（`atTime` 用 `@Param("channelId") Long channelId, @Param("atTime") Date atTime` 等）。

- [ ] **Step 2: SalesChannelBindingService 接口**

```java
public interface SalesChannelBindingService {
    /** 绑定（C1: 当前 binding effective_to=now，新插一行） */
    void bindOrTransfer(Long channelId, Long newSalesId, String bindSource, String remark, Long operatorId);
    /** 查渠道当前归属销售 */
    JstSalesChannelBinding getCurrentByChannelId(Long channelId);
    /** 查某时刻该渠道归属（提成计算用） */
    JstSalesChannelBinding getBindingAtTime(Long channelId, Date atTime);
    /** 销售名下当前所有渠道（离职转移用） */
    List<JstSalesChannelBinding> listCurrentByOwnerSales(Long salesId);
    /** 离职批量转移给主管 */
    int transferAllToManager(Long fromSalesId, Long managerId, Long operatorId);
}
```

- [ ] **Step 3: 写测试 7 case**

- bindOrTransfer 渠道无现有 binding → 直接 insert 新 binding，不 close
- bindOrTransfer 渠道有现有 binding → close 旧 + insert 新（effective_from = now）
- getCurrentByChannelId 命中 → 返当前；未命中 → 返 null
- getBindingAtTime 多个历史 → 按 effective_from/to 区间正确返
- listCurrentByOwnerSales 返多行
- transferAllToManager 销售无名下渠道 → 0
- transferAllToManager 销售名下 N 个渠道 → 全部 close + 插 N 个新行（bind_source='transfer_resign'）

- [ ] **Step 4: 实现 + 跑通测试 + 提交**

```bash
git commit -m "feat(channel): SalesChannelBindingService 版本化绑定 + 离职批量转移 + 7 单测"
```

---

## Task 7: SalesAutoBindingService（核心算法 Step 1/2/3）+ 单测【B1-B12 边界全覆盖】

**Files:**
- Create: `service/SalesAutoBindingService.java` + impl
- Create: 单测 `SalesAutoBindingServiceImplTest.java`（覆盖 B1-B12）

- [ ] **Step 1: SalesAutoBindingService 接口**

```java
public interface SalesAutoBindingService {
    /**
     * 渠道注册时触发自动绑定。
     * @param channelId           新注册的渠道 ID
     * @param registeredPhone     渠道方手机号
     * @param filledBusinessNo    渠道注册时填的业务编号（可空）
     * @return 命中并绑定的 sales_id；未命中返 null
     */
    Long autoBind(Long channelId, String registeredPhone, String filledBusinessNo);
}
```

- [ ] **Step 2: 写 12 个测试 case，覆盖 B1-B12**

每个 case 用 Mockito mock SalesPreRegisterService、SalesService、SalesChannelBindingService、JstSalesAttributionConflictMapper：

- B1: phone 命中 pending pre_register（销售 active）→ 调 markMatched + bindOrTransfer，返 salesId
- B2: phone 命中 pending pre_register 但 sales status='resigned_final' → A8 已失效，pre_register 应已被定时任务清理；测试假设 service 入口 Step 1 查询 JOIN status 条件正确，跳过到 Step 2
- B3: 销售在 'resign_apply' 状态 → 仍按 Step 1 绑（status IN ('active','resign_apply') 通过）
- B4: phone 不命中 pre，business_no 命中销售 → 调 bindOrTransfer，bindSource='business_no'
- B5: business_no 命中已离职销售 → 跳过 Step 2，进 Step 3 无归属
- B6: business_no 填错 → Step 3 + 写 sys_oper_log（mock 一个 OperLogService 校验）
- B7: 都未命中 → 返 null
- B8: registeredPhone == 销售本人 phone → 自绑防御不绑
- B9: 渠道当前已有 binding → 不重新走算法（service 入口校验）
- B10: 数据库唯一索引（不需要单测，DDL 保证）
- B11: M1 互斥情况下，invite_code + business_no 都命中 → 销售优先
- B12: M1 互斥写 channel.parent_invite_attempted 字段（这个由计划 #4 渠道分销监听器处理，本任务只验证 SalesAutoBindingService 自身正确返 sales_id）

- [ ] **Step 3: 实现 SalesAutoBindingService**

实现要点：
- Step 1 调 `preRegisterService.findActiveByPhone(phone)` → 命中且 sales 在职 → 调 bindingService.bindOrTransfer(channelId, salesId, 'pre_register', ...) + preRegisterService.markMatched(preId, channelId)
- Step 2 调 `salesService.getBySalesNo(filledBusinessNo)` → 命中且 sales 在职 → bindOrTransfer
- Step 3 不绑，写日志
- 自绑防御：从 `salesService.getById(candidateSalesId).phone` 反查
- 唯一异常：B6 需要写 sys_oper_log（用 ruoyi 的 OperLogService 或简单 LOG.info）

- [ ] **Step 4: 跑测试 + 提交**

```bash
mvn -pl RuoYi-Vue/jst-channel test -Dtest=SalesAutoBindingServiceImplTest -q
```
Expected: 12 case 全过。

```bash
git commit -m "feat(channel): SalesAutoBindingService 自动绑定算法 (Step 1/2/3 + B1-B12 边界) + 12 单测"
```

---

## Task 8: ChannelRegisteredEvent + 监听器

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/ChannelRegisteredEvent.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/listener/SalesAutoBindingListener.java`
- Modify: `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/PartnerApplyServiceImpl.java` 或 `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/JstChannelServiceImpl.java`（在渠道注册成功后 publishEvent）

- [ ] **Step 1: ChannelRegisteredEvent**

```java
package com.ruoyi.jst.common.event;

import org.springframework.context.ApplicationEvent;

public class ChannelRegisteredEvent extends ApplicationEvent {
    private final Long channelId;
    private final String registeredPhone;
    private final String filledBusinessNo;
    private final String filledInviteCode;

    public ChannelRegisteredEvent(Object source, Long channelId, String phone, String businessNo, String inviteCode) {
        super(source);
        this.channelId = channelId;
        this.registeredPhone = phone;
        this.filledBusinessNo = businessNo;
        this.filledInviteCode = inviteCode;
    }
    public Long getChannelId() { return channelId; }
    public String getRegisteredPhone() { return registeredPhone; }
    public String getFilledBusinessNo() { return filledBusinessNo; }
    public String getFilledInviteCode() { return filledInviteCode; }
}
```

- [ ] **Step 2: SalesAutoBindingListener**

```java
package com.ruoyi.jst.channel.listener;

import com.ruoyi.jst.channel.service.SalesAutoBindingService;
import com.ruoyi.jst.common.event.ChannelRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SalesAutoBindingListener {
    private static final Logger log = LoggerFactory.getLogger(SalesAutoBindingListener.class);

    @Autowired
    private SalesAutoBindingService autoBindingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelRegisteredEvent event) {
        try {
            Long salesId = autoBindingService.autoBind(
                event.getChannelId(),
                event.getRegisteredPhone(),
                event.getFilledBusinessNo()
            );
            log.info("[SalesAutoBinding] channel={} bind={} (null=未匹配)", event.getChannelId(), salesId);
        } catch (Exception ex) {
            log.error("[SalesAutoBinding] channel={} 失败", event.getChannelId(), ex);
        }
    }
}
```

- [ ] **Step 3: 在渠道注册成功路径里 publishEvent**

定位到现有渠道注册接口（如 `JstChannelServiceImpl` 的 register 方法或 `PartnerApplyServiceImpl.approveAndCreateChannel`），在事务提交前后注入：

```java
@Autowired
private org.springframework.context.ApplicationEventPublisher publisher;

// 注册流程末尾
publisher.publishEvent(new ChannelRegisteredEvent(this, 
    newChannel.getChannelId(),
    newChannel.getPhone(),
    requestDto.getBusinessNo(),   // 新增字段
    requestDto.getInviteCode()    // 新增字段
));
```

> ⚠️ 渠道注册请求 DTO 需要加 `businessNo` 和 `inviteCode` 两个可空字段。如果现有 DTO 是不可改的（如生成代码），就在 Controller 层加两个 RequestParam 显式传入。

- [ ] **Step 4: 编译 + 提交**

```bash
mvn -pl RuoYi-Vue/jst-common,RuoYi-Vue/jst-channel,RuoYi-Vue/jst-user -am clean compile -DskipTests -q
git commit -m "feat(channel): ChannelRegisteredEvent + SalesAutoBindingListener (AFTER_COMMIT 触发)"
```

---

## Task 9: AdminSalesController（销售档案 CRUD + 状态机）

**Files:**
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesController.java`
- Create: 对应的 ReqDTO / ResVO 类

- [ ] **Step 1: 创建 ReqDTO/ResVO**

```java
// SalesCreateReqDTO.java
package com.ruoyi.jst.channel.dto;
import jakarta.validation.constraints.*;
public class SalesCreateReqDTO {
    @NotNull private Long sysUserId;
    @NotBlank @Size(max = 64) private String salesName;
    @NotBlank @Pattern(regexp = "^1[3-9]\\d{9}$") private String phone;
    private Long managerId;
    @NotNull @DecimalMin("0") @DecimalMax("1") private java.math.BigDecimal commissionRateDefault;
    private Boolean asManager = false;
    // getter/setter
}
```

类似创建：`SalesUpdateRateReqDTO`、`SalesSetManagerReqDTO`、`SalesResignApplyReqDTO`、`SalesPageQueryReqDTO`、`SalesListVO`、`SalesDetailVO`。

- [ ] **Step 2: AdminSalesController**

```java
package com.ruoyi.jst.channel.controller.admin;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.dto.*;
import com.ruoyi.jst.channel.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/sales")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
public class AdminSalesController extends BaseController {

    @Autowired
    private SalesService salesService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('jst:sales:list')")
    public TableDataInfo list(SalesPageQueryReqDTO query) {
        startPage();
        List<JstSales> rows = salesService.listForAdmin(query);  // 加这个 service 方法
        return getDataTable(rows);
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('jst:sales:add')")
    @Log(title = "销售档案", businessType = BusinessType.INSERT)
    public AjaxResult create(@Valid @RequestBody SalesCreateReqDTO req) {
        JstSales row = new JstSales();
        row.setSysUserId(req.getSysUserId());
        row.setSalesName(req.getSalesName());
        row.setPhone(req.getPhone());
        row.setManagerId(req.getManagerId());
        row.setCommissionRateDefault(req.getCommissionRateDefault());
        row.setIsManager(Boolean.TRUE.equals(req.getAsManager()) ? 1 : 0);
        return AjaxResult.success(salesService.create(row));
    }

    @PutMapping("/{salesId}/rate")
    @PreAuthorize("@ss.hasPermi('jst:sales:edit:rate')")
    @Log(title = "销售费率", businessType = BusinessType.UPDATE)
    public AjaxResult updateRate(@PathVariable Long salesId, @Valid @RequestBody SalesUpdateRateReqDTO req) {
        salesService.updateDefaultRate(salesId, req.getRate());
        return AjaxResult.success();
    }

    @PutMapping("/{salesId}/manager")
    @PreAuthorize("@ss.hasPermi('jst:sales:edit:manager')")
    @Log(title = "销售主管", businessType = BusinessType.UPDATE)
    public AjaxResult updateManager(@PathVariable Long salesId, @RequestBody SalesSetManagerReqDTO req) {
        salesService.updateManager(salesId, req.getManagerId());
        return AjaxResult.success();
    }

    @PostMapping("/{salesId}/resign-apply")
    @PreAuthorize("@ss.hasPermi('jst:sales:resign-apply')")
    @Log(title = "申请离职", businessType = BusinessType.UPDATE)
    public AjaxResult applyResign(@PathVariable Long salesId, @RequestBody SalesResignApplyReqDTO req) {
        salesService.applyResign(salesId, req.getExpectedResignDate());
        return AjaxResult.success();
    }

    @PostMapping("/{salesId}/resign-execute")
    @PreAuthorize("@ss.hasPermi('jst:sales:resign-execute')")
    @Log(title = "立即执行离职", businessType = BusinessType.UPDATE)
    public AjaxResult executeResign(@PathVariable Long salesId) {
        salesService.executeResign(salesId);  // 触发 SalesResignedEvent，由 ResignationService 处理转移
        return AjaxResult.success();
    }

    @PostMapping("/{salesId}/transition-end")
    @PreAuthorize("@ss.hasPermi('jst:sales:transition-end')")
    public AjaxResult endTransition(@PathVariable Long salesId) {
        salesService.endTransition(salesId);
        return AjaxResult.success();
    }
}
```

加 SalesService 缺失方法：
```java
List<JstSales> listForAdmin(SalesPageQueryReqDTO query);
```

实现：调 `salesMapper.selectJstSalesList(filter)`。

- [ ] **Step 3: 编译 + 启动 sanity check（已有 Service 测试覆盖 service 层逻辑，本 Controller 不写单测）**

```bash
mvn -pl RuoYi-Vue/jst-channel,RuoYi-Vue/ruoyi-admin -am clean compile -DskipTests -q
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel): AdminSalesController 销售档案 CRUD + 离职状态机端点"
```

---

## Task 10: SalesPreRegisterController（销售本人 CRUD）

**Files:**
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/sales/SalesPreRegisterController.java`
- Create: ReqDTO 类

- [ ] **Step 1: Controller**

```java
package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.channel.dto.SalesPreRegisterCreateReqDTO;
import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales/me/pre-register")
public class SalesPreRegisterController extends BaseSalesController {

    @Autowired
    private SalesPreRegisterService preRegisterService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:list')")
    public TableDataInfo list() {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        return getDataTable(preRegisterService.listMine(salesId));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:add')")
    public AjaxResult create(@Valid @RequestBody SalesPreRegisterCreateReqDTO req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        return AjaxResult.success(preRegisterService.create(salesId, req.getPhone(), req.getTargetName()));
    }

    @PostMapping("/{preId}/renew")
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:renew')")
    public AjaxResult renew(@PathVariable Long preId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        preRegisterService.renew(preId, salesId);
        return AjaxResult.success();
    }

    @DeleteMapping("/{preId}")
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:remove')")
    public AjaxResult remove(@PathVariable Long preId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        preRegisterService.remove(preId, salesId);
        return AjaxResult.success();
    }
}
```

- [ ] **Step 2: 编译 + 提交**

```bash
git commit -m "feat(channel): SalesPreRegisterController 销售本人 CRUD（list/create/renew/remove）"
```

---

## Task 11: SalesCommissionService.calculateOnOrderPaid（核心提成计算）+ 单笔上限压缩 + 单测

**Files:**
- Create: `service/SalesCommissionService.java` + impl
- Modify: `JstSalesCommissionLedgerMapper.xml` 加 `batchInsert`、`updateStatusBatch`、`selectByOrderId`、`selectPendingForAccrue`、`cancelPendingByOrder`
- Create: 单测 `SalesCommissionServiceImplTest.java`（重点覆盖 §3.2 算法）

- [ ] **Step 1: Mapper XML 追加**

```xml
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO jst_sales_commission_ledger (
            ledger_id, sales_id, order_id, order_no, channel_id, binding_type, business_type,
            base_amount, applied_rate, raw_amount, compress_ratio, amount,
            status, accrue_at, create_time
        ) VALUES
        <foreach collection="list" item="r" separator=",">
            (#{r.ledgerId},#{r.salesId},#{r.orderId},#{r.orderNo},#{r.channelId},#{r.bindingType},
             #{r.businessType},#{r.baseAmount},#{r.appliedRate},#{r.rawAmount},#{r.compressRatio},
             #{r.amount},#{r.status},#{r.accrueAt},NOW())
        </foreach>
    </insert>

    <select id="selectByOrderId" parameterType="Long" resultMap="JstSalesCommissionLedgerResult">
        SELECT <include refid="cols"/> FROM jst_sales_commission_ledger WHERE order_id = #{orderId}
    </select>

    <select id="selectPendingForAccrue" resultType="Long">
        SELECT ledger_id FROM jst_sales_commission_ledger
         WHERE status = 'pending' AND accrue_at &lt;= NOW()
         LIMIT 1000
    </select>

    <update id="markAccruedBatch">
        UPDATE jst_sales_commission_ledger SET status = 'accrued', accrued_at = NOW(), update_time = NOW()
         WHERE ledger_id IN <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
           AND status = 'pending'
    </update>

    <update id="cancelPendingByOrder">
        UPDATE jst_sales_commission_ledger SET status = 'cancelled', update_time = NOW()
         WHERE order_id = #{orderId} AND status = 'pending'
    </update>
```

加 Mapper 接口方法。

- [ ] **Step 2: SalesCommissionService 接口**

```java
public interface SalesCommissionService {
    /**
     * 订单付款时计算并写入销售提成 ledger（直属 + 穿透）。
     * 返回写入的 ledger 列表（含压缩前后的金额）。
     * 不写分销提成（那由 ChannelDistributionService 处理，本计划之外）。
     */
    List<JstSalesCommissionLedger> calculateOnOrderPaid(OrderPaidContext ctx);

    /** 单笔上限压缩（J）：返回压缩比例（0~1） */
    BigDecimal computeCompressRatio(BigDecimal sumAllShares, BigDecimal paidAmount, BigDecimal maxRate);

    /** 退款撤销 pending */
    int cancelOnOrderRefunded(Long orderId);

    /** Quartz 任务用：批量推进 pending → accrued */
    int accruePendingByCron();
}
```

OrderPaidContext POJO：
```java
public class OrderPaidContext {
    public Long orderId;
    public String orderNo;
    public Long channelId;
    public String businessType;  // enroll / course / mall / appointment_*
    public BigDecimal paidAmount;
    public Date payTime;
    public BigDecimal channelRebateAmount;       // 由调用方传入，参与上限计算（不影响销售 ledger 写入）
    public BigDecimal distributionAmount;        // 同上
}
```

- [ ] **Step 3: 写测试覆盖 §3.2 多场景**

至少 12 个测试 case：

1. `calculateOnOrderPaid_directOnly_writesOneLedger` — 渠道有直属销售，无上级渠道，无分销 → 1 行 direct ledger
2. `calculateOnOrderPaid_directAndLevel1_writesTwoLedgers` — 直属 + 上级渠道有销售（不同人）→ 2 行（direct + level1）
3. `calculateOnOrderPaid_skipsLevel1_whenSameSales` — N2 防双计提：上级渠道销售 = 直属销售 → 只 1 行
4. `calculateOnOrderPaid_skipsDirect_whenBindSourceIsTransferResign` — E3 离职接收：跳过该 ledger
5. `calculateOnOrderPaid_skipsAll_whenNoBinding` — 渠道无销售归属 → 0 行
6. `calculateOnOrderPaid_appliesBusinessTypeRate` — C2 按业务类型：Enroll 5%、Course 3%
7. `calculateOnOrderPaid_baseIsActualPaidAmount` — 甲：基数 = 实付（不是订单原价）
8. `calculateOnOrderPaid_zeroAmount_skipsAll` — 实付 0 → 跳过
9. `calculateOnOrderPaid_appliesCompression_whenOverCap` — 上限触发：销售提成+分销+返点 > 实付 × 15% → compress_ratio < 1
10. `calculateOnOrderPaid_skipsResignedSales` — 销售 status='resigned_pending_settle' 且 binding bind_source='transfer_resign' → 跳
11. `calculateOnOrderPaid_setsAccrueAt_to_payTime_plus_7days`
12. `cancelOnOrderRefunded_updatesPendingToCancelled`
13. `accruePendingByCron_idempotent` — 重复跑只更新 pending 行

- [ ] **Step 4: 实现 SalesCommissionServiceImpl**

```java
@Service
public class SalesCommissionServiceImpl implements SalesCommissionService {

    @Autowired private JstSalesCommissionLedgerMapper ledgerMapper;
    @Autowired private SalesChannelBindingService bindingService;
    @Autowired private SalesService salesService;
    @Autowired private SalesCommissionRateService rateService;
    @Autowired private JstChannelMapper channelMapper;       // 取上级渠道
    @Autowired private JstChannelInviteMapper inviteMapper;  // 查 invite 关系（计划 #4 也用这个 mapper）

    @Value("${jst.sales.aftersales_days:7}")
    private int aftersalesDays;

    @Value("${jst.sales.order.max_total_share_rate:0.15}")
    private BigDecimal maxTotalShareRate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<JstSalesCommissionLedger> calculateOnOrderPaid(OrderPaidContext ctx) {
        if (ctx.paidAmount == null || ctx.paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return Collections.emptyList();
        }

        List<JstSalesCommissionLedger> items = new ArrayList<>();

        // 1. 直属销售
        JstSalesChannelBinding direct = bindingService.getBindingAtTime(ctx.channelId, ctx.payTime);
        Long directSalesId = null;
        if (direct != null && !"transfer_resign".equals(direct.getBindSource())) {
            JstSales s = salesService.getById(direct.getSalesId());
            if (s != null && !"resigned_pending_settle".equals(s.getStatus())
                          && !"resigned_final".equals(s.getStatus())) {
                directSalesId = s.getSalesId();
                BigDecimal rate = rateService.resolveRate(directSalesId, ctx.businessType, ctx.payTime);
                items.add(buildLedger(ctx, directSalesId, "direct", rate));
            }
        }

        // 2. 穿透 1 级（N2）
        if (directSalesId != null) {
            JstChannelInvite invite = inviteMapper.selectActiveByInviteeAtTime(ctx.channelId, ctx.payTime);
            if (invite != null) {
                JstSalesChannelBinding parentBinding = bindingService.getBindingAtTime(
                    invite.getInviterChannelId(), ctx.payTime);
                if (parentBinding != null
                    && !"transfer_resign".equals(parentBinding.getBindSource())
                    && !parentBinding.getSalesId().equals(directSalesId)) {  // 防双计
                    JstSales parentSales = salesService.getById(parentBinding.getSalesId());
                    if (parentSales != null
                        && !"resigned_pending_settle".equals(parentSales.getStatus())
                        && !"resigned_final".equals(parentSales.getStatus())) {
                        BigDecimal rate = rateService.resolveRate(
                            parentSales.getSalesId(), ctx.businessType, ctx.payTime);
                        items.add(buildLedger(ctx, parentSales.getSalesId(), "level1", rate));
                    }
                }
            }
        }

        if (items.isEmpty()) return items;

        // 3. 单笔上限压缩
        BigDecimal sumSales = items.stream()
            .map(JstSalesCommissionLedger::getRawAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAllShares = sumSales
            .add(ctx.channelRebateAmount == null ? BigDecimal.ZERO : ctx.channelRebateAmount)
            .add(ctx.distributionAmount == null ? BigDecimal.ZERO : ctx.distributionAmount);
        BigDecimal compressRatio = computeCompressRatio(sumAllShares, ctx.paidAmount, maxTotalShareRate);
        for (JstSalesCommissionLedger row : items) {
            row.setCompressRatio(compressRatio);
            row.setAmount(row.getRawAmount().multiply(compressRatio).setScale(2, RoundingMode.HALF_UP));
        }

        ledgerMapper.batchInsert(items);
        return items;
    }

    private JstSalesCommissionLedger buildLedger(OrderPaidContext ctx, Long salesId, String type, BigDecimal rate) {
        JstSalesCommissionLedger row = new JstSalesCommissionLedger();
        row.setLedgerId(snowId());
        row.setSalesId(salesId);
        row.setOrderId(ctx.orderId);
        row.setOrderNo(ctx.orderNo);
        row.setChannelId(ctx.channelId);
        row.setBindingType(type);
        row.setBusinessType(ctx.businessType);
        row.setBaseAmount(ctx.paidAmount);
        row.setAppliedRate(rate);
        row.setRawAmount(ctx.paidAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP));
        row.setCompressRatio(BigDecimal.ONE);
        row.setAmount(row.getRawAmount());
        row.setStatus("pending");
        row.setAccrueAt(addDays(ctx.payTime, aftersalesDays));
        return row;
    }

    @Override
    public BigDecimal computeCompressRatio(BigDecimal sumAll, BigDecimal paidAmount, BigDecimal maxRate) {
        if (sumAll == null || paidAmount == null) return BigDecimal.ONE;
        BigDecimal cap = paidAmount.multiply(maxRate);
        if (sumAll.compareTo(cap) <= 0) return BigDecimal.ONE;
        return cap.divide(sumAll, 4, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelOnOrderRefunded(Long orderId) {
        return ledgerMapper.cancelPendingByOrder(orderId);
    }

    @Override
    public int accruePendingByCron() {
        List<Long> ids = ledgerMapper.selectPendingForAccrue();
        if (ids.isEmpty()) return 0;
        return ledgerMapper.markAccruedBatch(ids);
    }

    // 工具方法
    private Date addDays(Date d, int days) { return new Date(d.getTime() + 86400_000L * days); }
    private Long snowId() { /* 同 SalesServiceImpl */ return 0L; }
}
```

> 还要在 `JstChannelInviteMapper.xml` 加 `selectActiveByInviteeAtTime`：
```xml
    <select id="selectActiveByInviteeAtTime" resultMap="JstChannelInviteResult">
        SELECT <include refid="cols"/> FROM jst_channel_invite
         WHERE invitee_channel_id = #{inviteeChannelId}
           AND status = 'active'
           AND invited_at &lt;= #{atTime}
         LIMIT 1
    </select>
```

- [ ] **Step 5: 跑测试**

```bash
mvn -pl RuoYi-Vue/jst-channel test -Dtest=SalesCommissionServiceImplTest -q
```
Expected: 13 case 全过。

- [ ] **Step 6: 提交**

```bash
git commit -m "feat(channel): SalesCommissionService 提成计算管线 (4 类项+N2 穿透+J 上限压缩+E3 离职跳过) + 13 单测"
```

---

## Task 12: OrderPaid / OrderRefunded 监听器

**Files:**
- Create: `listener/SalesCommissionListener.java`
- Modify: `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/...` — 在 OrderPaidEvent / OrderRefundedEvent 发布点确认（已有 OrderPaidEvent，仅检查现有发布逻辑），如果没有 OrderRefundedEvent 则新建一个并在退款服务里 publish

- [ ] **Step 1: 检查 OrderRefundedEvent 是否存在**

```bash
find RuoYi-Vue -name "OrderRefundedEvent.java" -o -name "OrderRefundEvent.java" 2>/dev/null
```
若不存在，参照 OrderPaidEvent 创建：

```java
package com.ruoyi.jst.common.event;
import org.springframework.context.ApplicationEvent;
public class OrderRefundedEvent extends ApplicationEvent {
    private final Long orderId;
    private final Long refundId;
    private final String refundType;  // full / partial（本期仅 full）
    public OrderRefundedEvent(Object source, Long orderId, Long refundId, String refundType) {
        super(source);
        this.orderId = orderId;
        this.refundId = refundId;
        this.refundType = refundType;
    }
    public Long getOrderId() { return orderId; }
    public Long getRefundId() { return refundId; }
    public String getRefundType() { return refundType; }
}
```

并在退款 Service 的 markRefundCompleted 方法里 publishEvent。

- [ ] **Step 2: SalesCommissionListener**

```java
package com.ruoyi.jst.channel.listener;

import com.ruoyi.jst.channel.service.SalesCommissionService;
import com.ruoyi.jst.channel.service.SalesCommissionService.OrderPaidContext;
import com.ruoyi.jst.common.event.OrderPaidEvent;
import com.ruoyi.jst.common.event.OrderRefundedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Component
public class SalesCommissionListener {
    private static final Logger log = LoggerFactory.getLogger(SalesCommissionListener.class);

    @Autowired
    private SalesCommissionService commissionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaidEvent event) {
        try {
            Map<String, Object> extra = event.getExtraData();
            OrderPaidContext ctx = new OrderPaidContext();
            ctx.orderId = event.getBizId();
            ctx.orderNo = (String) extra.getOrDefault("orderNo", "");
            ctx.channelId = (Long) extra.get("channelId");
            ctx.businessType = (String) extra.getOrDefault("businessType", "enroll");
            ctx.paidAmount = (BigDecimal) extra.get("paidAmount");
            ctx.payTime = extra.get("payTime") != null ? (Date) extra.get("payTime") : new Date();
            ctx.channelRebateAmount = (BigDecimal) extra.getOrDefault("channelRebateAmount", BigDecimal.ZERO);
            ctx.distributionAmount = (BigDecimal) extra.getOrDefault("distributionAmount", BigDecimal.ZERO);

            if (ctx.channelId == null) {
                log.debug("[SalesCommission] order={} 无渠道，跳过", ctx.orderId);
                return;
            }
            commissionService.calculateOnOrderPaid(ctx);
        } catch (Exception ex) {
            log.error("[SalesCommission] OrderPaidEvent 处理失败 order={}", event.getBizId(), ex);
            // 不抛出，由 SalesCommissionRepairJob 兜底
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefund(OrderRefundedEvent event) {
        try {
            int n = commissionService.cancelOnOrderRefunded(event.getOrderId());
            log.info("[SalesCommission] order={} 退款撤销 {} 行 pending ledger", event.getOrderId(), n);
        } catch (Exception ex) {
            log.error("[SalesCommission] OrderRefundedEvent 失败 order={}", event.getOrderId(), ex);
        }
    }
}
```

> ⚠️ OrderPaidEvent 的 extraData 必须包含 `channelId / paidAmount / orderNo / businessType / payTime / channelRebateAmount / distributionAmount`。如果当前事件发布点未携带这些字段，需要修改订单付款 Service 增补 extraData 内容。本任务包含此修改：定位 `JstOrderServiceImpl.markPaid()`（或类似方法）确认 extraData 完整。

- [ ] **Step 3: 编译 + 提交**

```bash
mvn -pl RuoYi-Vue/jst-channel,RuoYi-Vue/jst-order,RuoYi-Vue/ruoyi-admin -am clean compile -DskipTests -q
git commit -m "feat(channel): SalesCommissionListener 订阅 OrderPaid/Refunded 事件 + 完善 extraData"
```

---

## Task 13: SalesCommissionAccrueJob + SalesCommissionRepairJob

**Files:**
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesCommissionAccrueJob.java`
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesCommissionRepairJob.java`

- [ ] **Step 1: AccrueJob**

```java
package com.ruoyi.quartz.task;

import com.ruoyi.jst.channel.service.SalesCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("salesCommissionAccrueJob")
public class SalesCommissionAccrueJob {

    @Autowired
    private SalesCommissionService commissionService;

    public void execute() {
        int n = commissionService.accruePendingByCron();
        org.slf4j.LoggerFactory.getLogger(getClass())
                .info("[SalesCommissionAccrueJob] 推进 {} 行 pending → accrued", n);
    }
}
```

- [ ] **Step 2: RepairJob**

补偿 OrderPaidEvent 监听失败的订单。需在 `JstOrderMapper.xml` 加 `selectPaidWithoutLedger`：

```xml
    <select id="selectPaidWithoutLedger" resultType="Long">
        SELECT o.order_id FROM jst_order o
         WHERE o.status = 'paid'
           AND o.pay_time &gt;= DATE_SUB(NOW(), INTERVAL 7 DAY)
           AND o.channel_id IS NOT NULL
           AND NOT EXISTS (
               SELECT 1 FROM jst_sales_commission_ledger l
                WHERE l.order_id = o.order_id
           )
         LIMIT 200
    </select>
```

```java
@Component("salesCommissionRepairJob")
public class SalesCommissionRepairJob {

    @Autowired private JstOrderMapper orderMapper;
    @Autowired private SalesCommissionService commissionService;
    @Autowired private SalesService salesService;
    @Autowired private SalesChannelBindingService bindingService;

    public void execute() {
        List<Long> orderIds = orderMapper.selectPaidWithoutLedger();
        for (Long oid : orderIds) {
            try {
                JstOrder o = orderMapper.selectJstOrderByOrderId(oid);
                if (o == null || o.getChannelId() == null) continue;
                OrderPaidContext ctx = new OrderPaidContext();
                ctx.orderId = oid;
                ctx.orderNo = o.getOrderNo();
                ctx.channelId = o.getChannelId();
                ctx.businessType = o.getBusinessType();
                ctx.paidAmount = o.getPaidAmount();
                ctx.payTime = o.getPayTime();
                ctx.channelRebateAmount = o.getChannelRebateAmount();
                commissionService.calculateOnOrderPaid(ctx);
            } catch (Exception ex) {
                log.error("[Repair] order={} 失败", oid, ex);
            }
        }
    }
}
```

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(quartz): SalesCommissionAccrueJob + SalesCommissionRepairJob (兜底机制)"
```

---

## Task 14: SalesMonthlySettlementJob

**Files:**
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesMonthlySettlementJob.java`
- Create: `service/SalesCommissionSettlementService.java` + impl
- Modify: `JstSalesCommissionLedgerMapper.xml` 加 `selectAccruedByPeriod`、`bindToSettlement`
- Modify: `JstSalesCommissionSettlementMapper.xml` 加 `insertOrUpdate`

- [ ] **Step 1: Mapper 追加查询**

```xml
<!-- ledger -->
    <select id="selectAccruedByPeriod" resultMap="JstSalesCommissionLedgerResult">
        SELECT <include refid="cols"/> FROM jst_sales_commission_ledger
         WHERE sales_id = #{salesId}
           AND status = 'accrued'
           AND accrued_at &gt;= #{periodStart} AND accrued_at &lt; #{periodEnd}
    </select>

    <update id="bindToSettlement">
        UPDATE jst_sales_commission_ledger 
           SET status = 'settled', settlement_id = #{settlementId}, update_time = NOW()
         WHERE sales_id = #{salesId} AND status = 'accrued'
           AND accrued_at &gt;= #{periodStart} AND accrued_at &lt; #{periodEnd}
    </update>

    <select id="selectActiveSalesIdsForSettlement" resultType="Long">
        SELECT sales_id FROM jst_sales 
         WHERE status IN ('active','resign_apply','resigned_pending_settle')
    </select>
```

- [ ] **Step 2: SettlementService 接口与实现**

```java
public interface SalesCommissionSettlementService {
    /** Quartz 调用：为所有 active/过渡期销售生成上月结算单 */
    int generateLastMonthSettlements();
    /** admin 审核 */
    void approve(Long settlementId, Long approverUserId);
    void reject(Long settlementId, String reason);
    void recordPayment(Long settlementId, String voucher);
}
```

实现要点：
- `generateLastMonthSettlements()` 计算上月 1 号到本月 1 号区间，遍历 active 销售：
  - SUM accrued ledger amount → 写 settlement 行 (status='pending_review')
  - bindToSettlement update ledger → status='settled' + settlement_id

- `approve` / `reject` / `recordPayment` 各自是状态推进 + ledger 状态推进（approved → ledger 'settled' 不变；recordPayment → settlement 'paid'，对应 ledger 推进到 'paid'）。

- [ ] **Step 3: SalesMonthlySettlementJob**

```java
@Component("salesMonthlySettlementJob")
public class SalesMonthlySettlementJob {
    @Autowired
    private SalesCommissionSettlementService settlementService;

    public void execute() {
        int n = settlementService.generateLastMonthSettlements();
        org.slf4j.LoggerFactory.getLogger(getClass())
                .info("[SalesMonthlySettlementJob] 生成 {} 张月结单", n);
    }
}
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(quartz): SalesMonthlySettlementJob + SalesCommissionSettlementService 月结流程"
```

---

## Task 15: SalesResignationService（离职 3 阶段编排）+ SalesResignExecuteJob + SalesTransitionEndJob

**Files:**
- Create: `service/SalesResignationService.java` + impl
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesResignExecuteJob.java`
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesTransitionEndJob.java`
- Modify: `JstSalesMapper.xml` 加 `selectExpectedToResign` / `selectTransitionExpired`

- [ ] **Step 1: Mapper 追加**

```xml
    <select id="selectExpectedToResign" resultType="Long">
        SELECT sales_id FROM jst_sales
         WHERE status = 'resign_apply' AND expected_resign_date &lt;= NOW()
    </select>

    <select id="selectTransitionExpired" resultType="Long">
        SELECT sales_id FROM jst_sales
         WHERE status = 'resigned_pending_settle' AND transition_end_date &lt;= NOW()
    </select>
```

- [ ] **Step 2: SalesResignationService**

```java
public interface SalesResignationService {
    /** 离职执行 = SalesService.executeResign + 转移渠道 + 失效预录入 + sys_user 禁用 */
    void executeResign(Long salesId);
    /** 过渡期结束 = SalesService.endTransition + 触发后续清算（本期不做） */
    void endTransition(Long salesId);
    /** Quartz 用：批量执行到期的 resign_apply */
    int batchExecuteExpectedResigns();
    /** Quartz 用：批量结束到期的 transition */
    int batchEndExpiredTransitions();
}
```

实现：
- `executeResign(salesId)`：
  1. salesService.executeResign(salesId)
  2. JstSales me = salesService.getById(salesId)
  3. bindingService.transferAllToManager(salesId, me.getManagerId() == null ? me.getSalesId() : me.getManagerId(), operatorId=系统)
     - 如果 managerId == null（顶级主管）→ 进"无主队列"：bindingService 写一个 transfer_orphan binding 类型 或 不动 binding 让 admin 后续手动处理（按 spec C5）
  4. preRegisterService.expireBySales(salesId) - mapper 直接 UPDATE
  5. sysUserService.disable(me.getSysUserId()) - 调若依 SysUserService 禁用

- `endTransition` 简单调 salesService.endTransition

- batchExecuteExpectedResigns 跑 Quartz 入口

- [ ] **Step 3: 两个 Quartz Job**

```java
@Component("salesResignExecuteJob")
public class SalesResignExecuteJob {
    @Autowired private SalesResignationService resignationService;
    public void execute() {
        int n = resignationService.batchExecuteExpectedResigns();
        log.info("[SalesResignExecuteJob] 执行 {} 个销售离职", n);
    }
}

@Component("salesTransitionEndJob")
public class SalesTransitionEndJob {
    @Autowired private SalesResignationService resignationService;
    public void execute() {
        int n = resignationService.batchEndExpiredTransitions();
        log.info("[SalesTransitionEndJob] 终结 {} 个过渡期", n);
    }
}
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel): SalesResignationService 离职 3 阶段编排 + 2 Quartz Job"
```

---

## Task 16: 阶段 1 限权拦截器（resign_apply 期间禁用导出/新建预录入等）

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/interceptor/RestrictedSalesActionInterceptor.java`
- Modify: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/config/SalesScopeWebConfig.java` 注册新拦截器

- [ ] **Step 1: 拦截器**

```java
package com.ruoyi.jst.common.interceptor;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.service.SalesService;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class RestrictedSalesActionInterceptor implements HandlerInterceptor {

    /** 申请离职阶段禁用的 URI 模式 */
    private static final Set<String> BLOCKED = Set.of(
        "POST /sales/me/pre-register",
        "POST /sales/me/pre-register/.*/renew",
        "GET /sales/me/.*/export",
        "GET /sales/me/followup/.*/export"
    );

    @Autowired
    private SalesService salesService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        LoginUser u = SalesScopeUtils.getLoginUserQuietly();
        if (u == null) return true;
        if (!SalesScopeUtils.isSalesRole(u)) return true;

        Long salesId = SalesScopeUtils.currentSalesId();
        if (salesId == null) return true;
        JstSales s = salesService.getById(salesId);
        if (s == null || !"resign_apply".equals(s.getStatus())) return true;

        String key = req.getMethod() + " " + req.getRequestURI();
        for (String pattern : BLOCKED) {
            if (key.matches(pattern.replace(".*", ".*"))) {
                resp.setStatus(403);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(
                    "{\"code\":403,\"msg\":\"申请离职阶段禁用此操作\"}"
                );
                return false;
            }
        }
        return true;
    }
}
```

- [ ] **Step 2: 注册到 SalesScopeWebConfig**

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(exportWatermarkInterceptor)
            .addPathPatterns("/sales/**", "/admin/sales/**")
            .order(100);
    registry.addInterceptor(restrictedSalesActionInterceptor)
            .addPathPatterns("/sales/me/**")
            .order(50);
}
```

并 @Autowired 注入。

- [ ] **Step 3: 编译 + 提交**

```bash
git commit -m "feat(common): RestrictedSalesActionInterceptor 申请离职阶段限权（导出/预录入禁用）"
```

---

## Task 17: SalesPerformanceController（销售本人看业绩聚合，金额脱敏）

**Files:**
- Create: `controller/sales/SalesPerformanceController.java`
- Create: `dto/SalesPerformanceVO.java`（含 `@Sensitive(money=true)` 字段）
- Modify: `JstSalesCommissionLedgerMapper.xml` 加聚合查询

- [ ] **Step 1: VO**

```java
package com.ruoyi.jst.channel.dto;
import com.ruoyi.jst.common.annotation.Sensitive;
import java.math.BigDecimal;

public class SalesPerformanceVO {
    private int orderCount;        // 笔数（销售可见）
    private int channelCount;      // 渠道数（销售可见）
    @Sensitive(money = true)
    private BigDecimal totalGmv;
    @Sensitive(money = true)
    private BigDecimal totalCommission;
    @Sensitive(money = true)
    private BigDecimal pendingCommission;  // 待计提+已计提
    @Sensitive(money = true)
    private BigDecimal settledCommission;  // 已结算+已打款
    // 业务类型分布（笔数和金额）
    private java.util.Map<String, Integer> countByBusinessType;
    @Sensitive(money = true)
    private java.util.Map<String, BigDecimal> gmvByBusinessType;
    // getter/setter
}
```

- [ ] **Step 2: Mapper 聚合**

```xml
    <select id="aggregatePerformance" resultMap="..." resultType="java.util.Map">
        SELECT 
            COUNT(DISTINCT order_id) AS orderCount,
            COUNT(DISTINCT channel_id) AS channelCount,
            SUM(base_amount) AS totalGmv,
            SUM(amount) AS totalCommission,
            SUM(CASE WHEN status IN ('pending','accrued') THEN amount ELSE 0 END) AS pendingCommission,
            SUM(CASE WHEN status IN ('settled','paid') THEN amount ELSE 0 END) AS settledCommission
        FROM jst_sales_commission_ledger
        WHERE sales_id = #{salesId}
          AND create_time &gt;= #{periodStart} AND create_time &lt; #{periodEnd}
    </select>
```

- [ ] **Step 3: Controller**

```java
@RestController
@RequestMapping("/sales/me/performance")
public class SalesPerformanceController extends BaseSalesController {

    @Autowired private SalesPerformanceService performanceService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('sales:me:performance:view')")
    public AjaxResult getMine(@RequestParam(required = false) String month) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        SalesPerformanceVO vo = performanceService.aggregate(salesId, month);
        return AjaxResult.success(vo);
        // MaskSalaryAspect 自动把 @Sensitive(money=true) 字段置 null
    }
}
```

> SalesPerformanceService 抽出来或者直接在 Controller 里调 mapper。本计划放 service 层。

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel): SalesPerformanceController 销售本人业绩聚合 (金额字段 @Sensitive 脱敏)"
```

---

## Task 18: 注册 6 个 Quartz 任务到 sys_job 表

**Files:**
- Create: `架构设计/ddl/99-migration-sales-distribution-quartz.sql`

- [ ] **Step 1: SQL**

```sql
SET NAMES utf8mb4;

-- 注册 6 个 Quartz 任务（参考 ruoyi 现有 sys_job 字段结构）
-- 任务组：DEFAULT；状态：1=暂停，0=运行（按需 admin 在 Web 端启动）

INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark) VALUES
(2001, '销售-预录入过期清理',     'SALES', 'salesPreRegisterExpireJob.execute()',  '0 0 1 * * ?',  '1', '1', '1', 'admin', NOW(), '90 天未匹配自动失效'),
(2002, '销售-提成入账推进',        'SALES', 'salesCommissionAccrueJob.execute()',   '0 0 2 * * ?',  '1', '1', '1', 'admin', NOW(), '售后期满 pending → accrued'),
(2003, '销售-提成兜底补偿',        'SALES', 'salesCommissionRepairJob.execute()',   '0 0 * * * ?',  '1', '1', '1', 'admin', NOW(), '已付款无 ledger 的订单补提成（每小时）'),
(2004, '销售-月结单生成',          'SALES', 'salesMonthlySettlementJob.execute()',  '0 0 3 1 * ?',  '1', '1', '1', 'admin', NOW(), '每月 1 日 03:00 月结上月'),
(2005, '销售-离职执行',            'SALES', 'salesResignExecuteJob.execute()',      '0 0 4 * * ?',  '1', '1', '1', 'admin', NOW(), '到期的 resign_apply 自动 → resigned_pending_settle + 转移'),
(2006, '销售-过渡期终结',          'SALES', 'salesTransitionEndJob.execute()',      '0 30 4 * * ?', '1', '1', '1', 'admin', NOW(), '到期的 resigned_pending_settle 自动 → resigned_final');
```

> ⚠️ status=1 表示 PAUSED，admin 在 sys_job 列表手动启用。生产部署时由运维确认。

- [ ] **Step 2: 应用 + 验证**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-quartz.sql"
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT job_id, job_name, invoke_target, cron_expression, status FROM sys_job 
WHERE job_id BETWEEN 2001 AND 2010;" jst
```
Expected: 6 行。

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(quartz): 注册 6 个销售 Quartz 任务到 sys_job (paused 状态待 admin 启用)"
```

---

## Task 19: 全量编译 + spring-boot 启动 + 接口手工 smoke

- [ ] **Step 1: 全量编译**

```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

- [ ] **Step 2: 跑全部新增测试**

```bash
mvn -pl jst-common,jst-channel test -q
```
Expected: 所有计划 #1 + #2 的单测全过（约 60+ 测试）。

- [ ] **Step 3: 启动 ruoyi-admin**

```bash
mvn -pl ruoyi-admin spring-boot:run -DskipTests
```
Expected: 启动成功，能看到所有新 Bean 注入：
- SalesService / SalesAutoBindingService / SalesCommissionService / ...
- SalesAutoBindingListener / SalesCommissionListener
- 6 个 Quartz Job Bean（@Component "salesXxxJob"）

无 NoSuchBeanDefinitionException。

- [ ] **Step 4: 用 admin token 手工 smoke 几个端点（可选）**

新建 `test/sales-smoke.http`（参考现有 admin-tests.http 格式），覆盖：
- POST /admin/sales — 创建一个销售
- GET /admin/sales/list — 列表
- POST /admin/sales/{id}/resign-apply — 申请离职
- GET /sales/me/performance — 用销售 token 查业绩（金额应为 null）

---

## Task 20: 更新 CLAUDE.md

- [ ] **Step 1: 修改顶部 + §六 SALES-DISTRIBUTION 行**

将 SALES-DISTRIBUTION 行替换为：

`**P0 🟡** | SALES-DISTRIBUTION | 🟡 计划 #1 + #2 完成（DDL+基础架构 + 销售核心+提成管线+Quartz），plan-03 / plan-04 待启动 | 计划 #2 已交付：SalesScopeAspect / 9 个 Service (SalesService/SalesPreRegister/Binding/AutoBinding/Commission/CommissionRate/Settlement/Resignation) / SalesCommissionListener (订阅 OrderPaid+Refunded) / SalesAutoBindingListener (订阅 ChannelRegistered) / 6 Quartz Job (Accrue/Repair/PreRegisterExpire/MonthlySettlement/ResignExecute/TransitionEnd) / RestrictedSalesActionInterceptor (申请离职限权) / AdminSalesController + SalesPreRegisterController + SalesPerformanceController / 60+ TDD 单测全绿。下一步 plan-03：CRM 子模块 (跟进/标签/任务) + 销售工作台前端 5 页。`

- [ ] **Step 2: 提交 + merge**

```bash
git add CLAUDE.md
git commit -m "docs: CLAUDE.md 计划 #2 完成（销售核心+提成管线+Quartz）"
git checkout main
git merge --no-ff feature/sales-distribution-02-sales-core
```

---

## 自检清单

- [ ] `@SalesDataScope` AOP 通过 3 单测
- [ ] SalesService 状态机 9 单测全过
- [ ] SalesPreRegisterService 8 单测覆盖 A1/A2/A4/A5/A7/A9/A10
- [ ] SalesAutoBindingService 12 单测覆盖 B1-B12
- [ ] SalesCommissionService 13 单测覆盖直属/穿透/E3/上限压缩/退款撤销
- [ ] 6 Quartz 任务全部注册到 sys_job
- [ ] OrderPaidEvent / OrderRefundedEvent 监听器 AFTER_COMMIT 触发
- [ ] 9 个 Service 全部实现
- [ ] 3 个销售 Controller（Admin/PreReg/Performance）
- [ ] RestrictedSalesActionInterceptor 注册到 /sales/me/**
- [ ] mvn clean compile 全模块绿
- [ ] spring-boot:run 启动正常
- [ ] CLAUDE.md 已更新

---

**End of Plan #2**
