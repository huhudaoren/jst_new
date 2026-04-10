# 任务卡 E2-PA-9 - PartnerScope 数据隔离切面（后端）

## 阶段 / 模块
阶段 E 批 2 / **jst-common** + 所有 jst-* 业务模块

## 背景
Q-08 决策：赛事方数据**完全隔离**，赛事方 A 仅能查询 `organizer_id = A.organizerId` 的数据。本卡实现统一的 `@PartnerDataScope` 切面，强制注入 WHERE 过滤条件。这是 E-2 所有 PA-* 前端卡的**后端基石**。

## CCB 决策
- **Q-08**：完全隔离，强制 WHERE 过滤

## 交付物

### 1. 注解定义
**新建**：`jst-common/.../annotation/PartnerDataScope.java`

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartnerDataScope {
    /** 数据表别名，用于 WHERE 拼接 */
    String deptAlias() default "";
    /** organizer_id 字段名 */
    String organizerIdColumn() default "organizer_id";
}
```

### 2. 切面实现
**新建**：`jst-common/.../aspectj/PartnerDataScopeAspect.java`

```java
@Aspect
@Component
public class PartnerDataScopeAspect {
    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, PartnerDataScope controllerDataScope) {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(JoinPoint joinPoint, PartnerDataScope dataScope) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) return;

        // 仅赛事方角色才注入过滤条件
        if (!hasRole(loginUser, "jst_partner")) return;

        Long organizerId = getOrganizerIdByUserId(loginUser.getUserId());
        if (organizerId == null) {
            throw new ServiceException("赛事方用户未绑定 organizer 档案");
        }

        String sqlCondition = String.format(" AND %s.%s = %d ",
            dataScope.deptAlias(),
            dataScope.organizerIdColumn(),
            organizerId);

        BaseEntity baseEntity = (BaseEntity) joinPoint.getArgs()[0];
        baseEntity.getParams().put(DATA_SCOPE, sqlCondition);
    }
}
```

### 3. Mapper XML 配合
所有受 PartnerScope 保护的 SELECT 必须在 XML 末尾加：
```xml
<if test="params.dataScope != null and params.dataScope != ''">
    ${params.dataScope}
</if>
```

本卡不修改已有 XML，仅在基础设施就位 + 文档说明；实际业务 Mapper 由 PA-3/4/5/6/7 各自卡负责接入。

### 4. 新建辅助方法
**新建**：`jst-common/.../utils/PartnerScopeUtils.java`

```java
public class PartnerScopeUtils {
    public static Long getCurrentOrganizerId() {
        // 从 LoginUser 的 extra 或查库获取当前赛事方 organizer_id
    }

    public static boolean isPartnerRole(LoginUser user) {
        return user.getUser().getRoles().stream()
            .anyMatch(r -> "jst_partner".equals(r.getRoleKey()));
    }
}
```

### 5. partner 前缀 Controller 统一基类
**新建**：`jst-common/.../controller/BasePartnerController.java`

继承 `BaseController`，额外：
- 所有 @RequestMapping 方法默认加 `@PreAuthorize("hasRole('jst_partner')")`
- 获取 `currentOrganizerId` 的快捷方法
- 统一的 `@PartnerDataScope` 标注位置

PA-3/4/5/6/7 的 Controller 统一继承此基类。

### 6. 单元测试
**新建**：`jst-common/src/test/java/.../PartnerDataScopeAspectTest.java`

测试场景：
- 赛事方 A 登录 → list 查询自动注入 `AND organizer_id = A`
- 赛事方 B 登录 → 同样接口查不到 A 的数据
- 超级管理员登录 → 不注入过滤（查全部）
- 学生登录 → 抛异常（非赛事方不允许访问 partner 接口）

### 7. 文档
**新建**：`架构设计/35-PartnerScope切面使用说明.md`

说明：
- 何时需要注解
- deptAlias 如何填
- XML 如何配合
- 已知限制（如 join 多表时的处理）

## DoD
- [ ] 注解 + 切面 + 工具类 + 基类 + 测试 + 文档
- [ ] mvn compile + test SUCCESS
- [ ] 任务报告 `E2-PA-9-回答.md` 含切面工作流程图
- [ ] commit: `feat(jst-common): E2-PA-9 PartnerScope 数据隔离切面`

## 不许做
- ❌ 不许修改业务 Mapper XML（交给 PA-3~7 各自接入）
- ❌ 不许让切面对 admin 接口生效（只对 jst_partner 角色）
- ❌ 不许用硬编码 organizer_id

## 依赖：无（与 PA-1/2 可并行，PA-3~7 依赖本卡）
## 优先级：P0（阻塞 PA-3~7）

---
派发时间：2026-04-10
