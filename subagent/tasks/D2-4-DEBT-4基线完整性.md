# 任务卡 D2-4 - DEBT-4 23 基线完整性修复

## 阶段 / 模块
阶段 D 批 2 / **架构设计**（SQL 基线 + 迁移脚本）

## 背景
DEBT-3 §E 审计发现 `架构设计/23-基础数据初始化.sql` 有 3 条完整性问题（DEBT-3 按约定只列不修）。本卡做修复 + 配套迁移脚本，确保"全新库"能直接具备当前生产所需的角色基线。

## 问题回顾（来自 DEBT-3 §E）

1. **问题 1**：`23-基础数据初始化.sql:177-178` 把 `role_id=100` 固定写成 `jst_platform_op`，但测试库实际 `role_id=100` 是 `jst_student`，且不存在 `jst_platform_op`
2. **问题 2**：23 基线仅初始化 `jst_platform_op / jst_partner / jst_channel`，缺少当前已广泛使用的 `jst_student`
3. **问题 3**：23 基线 line 173 的注释仍假设"下级菜单由代码生成器自动写入 sys_menu"，阶段 C 大量手写 Controller 使其失效

## CCB 决策

| # | 决策 | 选择 |
|---|---|---|
| 1 | 是否改 23 基线 | 改。本卡是专门的基线修复卡，**允许修改 23-基础数据初始化.sql** |
| 2 | role_id 分配策略 | `100=jst_student` / `101=jst_partner` / `102=jst_channel` / `103=jst_platform_op`（对齐测试库现状） |
| 3 | 已部署环境怎么办 | 写 migration `97-migration-baseline-roles.sql`，幂等 INSERT IGNORE 补缺失角色，不改已存在的 |
| 4 | 注释修正 | 保留注释但改措辞，写明"手写 Controller 权限点走 95/96/... migration 补齐" |

## 交付物

### 1. 修复 23 基线
**修改**：`架构设计/23-基础数据初始化.sql`

- `sys_role` 初始化段：
  - 把 `role_id=100` 改为 `jst_student`（学生角色）
  - `role_id=101` 改为 `jst_partner`（赛事方）
  - `role_id=102` 改为 `jst_channel`（渠道）
  - `role_id=103` 新增 `jst_platform_op`（平台运营）
- `sys_role_menu` 如有硬编码 role_id 的关联，同步调整
- line 173 附近的注释改为：
  ```sql
  -- 基础数据仅初始化若依代码生成器自动产出的菜单；
  -- 手写 Controller 的权限点由 ddl/95-migration-phase-*.sql 系列迁移脚本补齐。
  ```
- **只动角色初始化与相关注释，不动其他基础数据**

### 2. 配套 migration（兼容已部署环境）
**新建**：`架构设计/ddl/97-migration-baseline-roles.sql`

- 幂等：`INSERT ... ON DUPLICATE KEY UPDATE role_name=VALUES(role_name)`（或 `INSERT IGNORE`）
- 补齐 4 个角色：`jst_student / jst_partner / jst_channel / jst_platform_op`
- **不覆盖已存在的 role_name**，避免破坏生产自定义
- 头部注释说明：
  - 此脚本用于已部署且早于本基线的环境补齐角色
  - 新建库直接跑 23-基础数据初始化.sql 即可，无需跑此脚本
  - 执行后需手动检查 `sys_role_menu` 中 role_id 映射是否仍有意义
- **测试库执行验证**（与 DEBT-3 一样跑两次验证幂等）

### 3. 回归校验
- 跑 `SELECT role_id, role_key, role_name FROM sys_role WHERE role_key LIKE 'jst_%' ORDER BY role_id;`
- 报告中贴出执行前 / 执行后快照

### 4. 文档同步
**修改**：`CLAUDE.md` §五 关键决策记录或 §七 DDL 文件清单
- 追加 `97-migration-baseline-roles.sql` 说明

## DoD
- [ ] 23-基础数据初始化.sql 修复问题 1/2/3
- [ ] 97-migration-baseline-roles.sql 幂等可重跑
- [ ] 测试库验证：执行前 / 执行后 `sys_role` 快照
- [ ] DEBT-3 中的 admin / jst_platform_op 回落逻辑可删除（本卡完成后平台角色已统一；但为安全起见**本卡不删**，留到后续观察）
- [ ] CLAUDE.md 同步
- [ ] 任务报告 `D2-4-DEBT-4-回答.md`
- [ ] commit: `chore: D2-4 DEBT-4 基线角色完整性修复`

## 不许做
- ❌ 不许动其他基础数据（字典、参数、用户等）
- ❌ 不许改任何 Java 代码
- ❌ 不许改 95/96-migration 已有脚本
- ❌ 不许执行 migration 到生产，仅测试库验证
- ❌ 不许删除 `sys_role` 中任何现有行（只补不删）

## 依赖：无（与 D2-1/D2-2/D2-3 并行）
## 优先级：中

---
派发时间：2026-04-09
