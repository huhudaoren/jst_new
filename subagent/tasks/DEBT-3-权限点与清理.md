# 任务卡 DEBT-3 - 权限点补齐 + 代码债清理

## 阶段 / 端
阶段 D / **架构设计 + RuoYi-Vue 运维**（无代码逻辑改动）

## 背景
阶段 C 全部合并后累积三类低风险债务：
1. 23-基础数据.sql 缺 C5~C8 / F-CHANNEL-DASHBOARD / F-MR / C9 的权限点（当前靠 `hasAnyRoles` 角色兜底能跑）
2. `test/temp_test_apply_patch.txt` orphan 文件未清理（C4 时期遗留）
3. `jst_points_account.owner_type` 历史混 `user` / `student`（C8 兼容读，长期需清洗）

## 交付物

### Part A — 权限点补齐（迁移脚本）
**新建**：`架构设计/ddl/95-migration-phase-c-permissions.sql`

- **不要改 23-基础数据初始化.sql**（它是设计期基线）
- 迁移脚本要求：幂等（`INSERT IGNORE` 或 `ON DUPLICATE KEY UPDATE`），MySQL 5.7 兼容
- 插入以下权限点到 `sys_menu` 表（参考既有 `jst:xxx:list` 的 menu_id 分配策略，取一个安全的高段号如 `9500-9700`）：

**jst-channel**
- `jst:channel:withdraw:list|query|apply|cancel|audit|execute`
- `jst:channel:dashboard:monthly|students|orders|stats`

**jst-event**
- `jst:event:appointment:apply|cancel|my|query`
- `jst:event:writeoff:scan|records`

**jst-points**
- `jst:points:mall:goods:list|add|edit|remove|publish`
- `jst:points:mall:exchange:list|ship|query`
- `jst:points:mall:exchange:apply|my|cancel`
- `jst:points:mall:browse`（@Anonymous 也可访问）
- `jst:points:mall:aftersale:apply|my|audit`
- `jst:points:center:summary|levels|ledger|tasks`

**jst-marketing**
- `jst:marketing:coupon:list|query|add|edit|remove|publish|offline|issue`
- `jst:marketing:coupon:my|claim|select`
- `jst:marketing:rights:list|query|add|edit|remove|publish|offline|issue`
- `jst:marketing:rights:my|apply`
- `jst:marketing:campaign:list|query`

**同时**：把上述权限点批量挂到 3 个业务角色：
- `jst_student`：wx 学生权限（appointment:apply/my/cancel/query、mall:browse/exchange:apply/my/cancel/aftersale:apply/my、coupon:my/claim/select、rights:my/apply、campaign:list/query、points:center:*）
- `jst_channel`：渠道权限（channel:withdraw:list/query/apply/cancel、channel:dashboard:*、writeoff:scan/records、points:center:*）
- `jst_platform_op`（或 admin 对应角色）：所有 admin 权限（channel:withdraw:audit/execute、mall:goods/exchange:ship、marketing:coupon/rights:* CRUD、mall:aftersale:audit）

权限点/角色绑定写 `sys_role_menu`。

### Part B — orphan 文件清理
- 删除 `test/temp_test_apply_patch.txt`
- `grep` 仓库是否还有其他 `temp_*` / `test_*.tmp` / `*.orig` / `*.bak` orphan，列在报告里（只列不删，需我确认）

### Part C — owner_type 数据清洗脚本（不执行）
**新建**：`架构设计/ddl/95-migration-unify-owner-type.sql`

- 编写幂等 SQL（不真正执行）：`UPDATE jst_points_account SET owner_type='user' WHERE owner_type='student';`
- 影响评估：查一下当前 DB 有多少行 `owner_type='student'`，写进报告 §C
- 告知用户：此脚本**需要后端停服后手动执行**（因为 C8 代码兼容读，切换期可能有写入冲突），本卡不执行

### Part D — 23-基础数据 typo / 完整性审计
`grep` `架构设计/23-基础数据初始化.sql` 是否还有其他 `menu_check_strict` 类 typo 或缺字段；列在报告，**不修**（C7 agent 已经修过一处）。

## DoD
- [ ] `95-migration-phase-c-permissions.sql` 幂等可重跑（测试库跑一次验证）
- [ ] `95-migration-unify-owner-type.sql` 写好不执行
- [ ] `test/temp_test_apply_patch.txt` 已删
- [ ] 任务报告 `DEBT-3-权限点与清理-回答.md` 包含 Part C 影响行数统计 + Part D typo 列表
- [ ] commit: `chore: DEBT-3 权限点补齐 + orphan 清理 + owner_type 清洗脚本`

## 不许做
- ❌ 不许改 23-基础数据.sql（基线文档）
- ❌ 不许执行 owner_type 清洗（停服期才能跑）
- ❌ 不许改任何 Java 代码
- ❌ 不许直接动 sys_menu 插入 API，必须走 SQL 迁移脚本

## 依赖：无（与 POLISH-BATCH2 / F-USER-ADDRESS 并行）
## 优先级：中

---
派发时间：2026-04-09
