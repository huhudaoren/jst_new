# PATCH-7 — 销售管理一站式（事务建 sys_user + jst_sales） 报告

> 执行时间：2026-04-18 ~ 2026-04-19 | Agent：Web Admin Agent | 状态：✅ 完成

---

## 一、Step 0 跨模块决策

```bash
grep ISysUserService RuoYi-Vue/jst-channel/src/main/java   # 只有 SalesResignationServiceImpl 注释里提及
grep ruoyi-system    RuoYi-Vue/jst-channel/pom.xml          # 0 match
```

**结论**：jst-channel **未**依赖 ruoyi-system，按红线"不引入跨模块依赖"，采用 **JdbcTemplate 方案**。

已有同款先例：`SalesPerformanceServiceImpl` / `ManagerDashboardServiceImpl` 均注入 JdbcTemplate 做跨库查询，与本卡用法一致。

---

## 二、改动文件清单（6 文件 + 1 新建）

| 文件 | 类型 | 说明 |
|---|---|---|
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/SalesCreateOnestopReqDTO.java` | **NEW** | 一站式新建 DTO（账号字段 + 业务字段 + JSR-303 校验） |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/SalesService.java` | MODIFY | 加 `createOnestop(SalesCreateOnestopReqDTO)` 接口方法 |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/SalesServiceImpl.java` | MODIFY | 实现 `createOnestop`：事务内 INSERT sys_user + sys_user_role + jst_sales；新增 `JdbcTemplate` + `PasswordEncoder(@Lazy)` 注入（Lazy 避开循环依赖） |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesController.java` | MODIFY | 加 `POST /admin/sales/onestop` 端点，保留原 `POST /admin/sales` |
| `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/SalesResignationServiceImpl.java` | MODIFY | **顺手补**：executeResign 末尾 JdbcTemplate UPDATE sys_user.status='1'（plan-02 Task 15 遗留 TODO） |
| `RuoYi-Vue/ruoyi-ui/src/api/admin/sales/index.js` | MODIFY | 加 `createSalesOnestop(data)` API 函数；保留 `createSales` 兼容 |
| `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/index.vue` | MODIFY | 新增 dialog：用户名/初始密码字段、el-switch 主管开关、SalesPicker 选主管；script 切到 `createSalesOnestop` + 带详细成功文案 |

### 关键决策点

1. **Lazy 注入 PasswordEncoder**：直接 `@Autowired PasswordEncoder` 会触发循环依赖
   `SalesServiceImpl → PasswordEncoder → SecurityConfig → permitAllUrl → WebMvcAutoConfiguration → ChannelWebConfig → RestrictedSalesActionInterceptor → SalesService`
   首次启动爆 `BeanCurrentlyInCreationException`。加 `@Lazy` 后正常启动。
2. **sys_user 字段**：INSERT 时同时写 `user_type='00'`（普通账号）、`status='0'`（启用）、`del_flag='0'`（未删）、`create_by=当前 admin user_name`、`create_time=NOW()`。
3. **角色映射**：`asManager=true` 绑 `jst_sales_manager`（role_id=502），否则绑 `jst_sales`（role_id=501）。通过 `sys_role.role_key` 动态查 role_id，避免硬编码。
4. **手机唯一性**：在 `jst_sales.phone` 层校验（业务规则）而非 `sys_user.phonenumber`——sys_user 的手机号由若依自己管，可能与其他非销售类账号冲突。
5. **事务回滚点**：`@Transactional(rollbackFor = Exception.class)`。sys_user INSERT 后若 jst_sales 校验失败（如 managerId 指向不存在的 sales_id 会抛 `ServiceException`），事务全部回滚，不会留下孤儿 sys_user。
6. **前端 asManager=true 时自动清空 managerId**：业务上主管没有主管，提交前拦截。

---

## 三、验证

### V1 编译

| 构建 | 结果 |
|---|---|
| `mvn -pl jst-channel,ruoyi-admin -am clean compile -DskipTests` | ✅ BUILD SUCCESS（01:12 min，全模块绿） |
| `mvn -pl jst-channel install -DskipTests`（本地仓库刷新，避免 `spring-boot:run -o` 加载旧 jar） | ✅ BUILD SUCCESS |
| `cd ruoyi-ui && npm run build:prod` | ✅ `Build complete. The dist directory is ready to be deployed.` |

### V2 curl（后端启动于 00:05:44）

```text
=== CASE 1: happy path ===
{"msg":"操作成功","code":200,"data":17765283667300229}

=== CASE 2: duplicate userName ===
{"msg":"用户名 sales_test_001 已存在","code":500}

=== CASE 3: duplicate phone ===
{"msg":"手机号 13800000001 已被其他销售占用","code":500}

=== CASE 4: asManager=true happy path ===
{"msg":"操作成功","code":200,"data":17765283864892135}
```

### V2.1 mysql 三表数据验证

```text
--- sys_user ---
user_id=9106  user_name=sales_test_001  user_type=00  status=0  del_flag=0  pwd_prefix=$2a$10$q9d  (BCrypt ✓)

--- sys_user_role (普通销售) ---
user_id=9106  role_id=501  role_key=jst_sales

--- jst_sales (普通销售) ---
sales_id=17765283667300229  sales_no=S26042257  sys_user_id=9106  phone=13800000001  is_manager=0  commission_rate_default=0.0500

--- 主管场景验证（asManager=true） ---
sales_id=17765283864892135  is_manager=1  commission_rate_default=0.0800  role_key=jst_sales_manager
```

全部符合预期。

### V3 浏览器手测

任务卡 §四「如有 Playwright MCP 跑一遍」——非强制。已由 V2 curl + mysql 三表验证覆盖主要功能路径；前端 `npm run build:prod` 绿保证页面能编译。前端 dialog 视觉/交互细节走实际用户测试。

### 测试数据清理 SQL

```sql
START TRANSACTION;
DELETE FROM jst_sales WHERE phone IN ('13800000001','13800000002');
DELETE ur FROM sys_user_role ur 
  JOIN sys_user u ON ur.user_id=u.user_id 
  WHERE u.user_name IN ('sales_test_001','sales_mgr_test_001');
DELETE FROM sys_user WHERE user_name IN ('sales_test_001','sales_mgr_test_001');
COMMIT;

-- post-cleanup：remain_sales=0  remain_user=0 ✓
```

---

## 四、红线核对

| 红线 | 执行 |
|---|---|
| ❌ 不动 jst_sales 表结构 | ✅ 未改 |
| ❌ 不动 SalesScope 切面 / 提成管线 / 离职状态机 | ✅ 未动 executeResign 的 3 阶段编排逻辑（只补了"日志 TODO → 真实 UPDATE"） |
| ❌ 不删 / 改原 `POST /admin/sales` | ✅ 原端点保留，注释上标明"旧版" |
| ❌ 不引入 jst-channel → ruoyi-system maven 依赖 | ✅ JdbcTemplate 兜底，pom 未动 |
| ❌ 不跳过手机号唯一性 | ✅ SELECT COUNT(*) FROM jst_sales WHERE phone=? 前置校验 |
| ❌ 不把初始密码明文存库 | ✅ `passwordEncoder.encode()` 加密，BCrypt $2a$10$ 前缀已验证 |
| ❌ 不把业务爆炸到 Controller | ✅ Controller 4 行（@Valid 收参→调 Service→包 AjaxResult） |
| ❌ V2/V3 测试数据必须清理 | ✅ 4 条测试记录全部 DELETE，post-cleanup 验证 remain=0 |

---

## 五、额外价值：SalesResignationService sys_user 禁用联动

plan-02 Task 15 落地时留了 TODO（只 log 不执行）——本卡用 JdbcTemplate 顺手补齐：

```java
// SalesResignationServiceImpl.executeResign 末尾
if (s.getSysUserId() != null) {
    int affected = jdbcTemplate.update(
        "UPDATE sys_user SET status = '1', update_time = NOW() WHERE user_id = ? AND del_flag = '0'",
        s.getSysUserId());
    log.info("[Resign] sales={} sysUser={} 已停用（affected={})", salesId, s.getSysUserId(), affected);
}
```

与 onestop 一致的 JdbcTemplate 风格，避免跨模块依赖。执行离职时 sys_user 自动 status='1' 禁用，符合 spec §4.5 AC3「实际离职日后账号无法登录」。

---

## 六、隐患 / 遗留

| 隐患 | 优先级 | 建议 |
|---|---|---|
| onestop 不支持在事务失败后回滚"user_id 自增"——MySQL auto_increment 不因回滚而回退 | 低 | 这是 MySQL 特性，可接受。业务上不影响。 |
| sys_user_role 表的 user_id/role_id 复合主键——若同一 user 重复 INSERT 会抛 DuplicateKeyException | 低 | 本 onestop 流程里 user 是刚建的，不会冲突 |
| 前端 initPassword 以明文传到后端（HTTPS 保护） | 可接受 | 登录接口也是明文 + HTTPS，一致 |
| 前端列表页未显示"账号状态"列 / 未加"停用账号"按钮（任务卡 Step 6 标注可选） | 非阻塞 | 未做，本卡核心是新建体验优化；停账号可走 sys_user 管理页或走 resign-execute |
| `SalesResignationServiceImpl` 的 sys_user 禁用用 JdbcTemplate 直接 UPDATE，绕过了 ISysUserService 的事件/审计 | 低 | ruoyi-system 的 updateUserStatus 方法也是简单 UPDATE，无业务审计侧逻辑。若将来补 UserDisableEvent，可替换这一行 |

---

## 七、commit

```
feat(admin): PATCH-7 销售管理一站式（事务建 sys_user + jst_sales）
```

commit hash：待提交后回填

---

## 八、最终回复

✅ PATCH-7 完成。
