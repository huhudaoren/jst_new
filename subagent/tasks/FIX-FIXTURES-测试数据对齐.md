# 任务卡 FIX-FIXTURES：测试前置数据补齐（直操库模式）

## 任务编号
FIX-FIXTURES

## 阶段
运维收尾 / 测试前置

## 涉及模块
本地 MySQL `127.0.0.1:3306/jst`（用户测试库） + `架构设计/ddl/`

## 业务背景

TEST-ROUND3 全量回归测试的前置障碍：
1. 原 `99-test-fixtures.sql`（1994 行）与当前 schema 字段漂移严重（REFACTOR-1 起多轮 DDL 变动）
2. 原 fixtures 预塞了大量"流程产物"（订单/退款/核销/台账），违背集成测试"链路自造数据"原则
3. 用户本地 `jst` 库含**手工修改的业务数据**，不能 drop 重建

**调整后的方案（B 路线）**：
- **废弃 `99-test-fixtures.sql` 的"reset+全量预塞"模型**
- **直接操作 `jst` 库**：审计现状 → 补齐缺失的前置数据 → 产出一次性幂等补丁 SQL
- **严格只增不删不改**：用户手工改过的数据原样保留
- fixtures 文件保留作历史归档，不再维护，头部加废弃标记

## 范围定义

### 前置数据（本任务负责补齐，只增不删）
| 类别 | 具体 |
|---|---|
| 用户账号 | 学生 10001/10002、渠道 10003、赛事方 10004、admin（若缺） |
| 赛事 | 8201（常规）、8206（团队）、及 `wx-tests.http` / `admin-tests.http` 里引用的任何 contestId |
| 赛事子表 | round、reviewer、appointment_slot、awards、faq 等（按赛事最小配） |
| 课程 | list/detail 测试引用的 courseId |
| 赛事方入驻记录 + 认证渠道 | 对应 10004 / 10003 |
| 学生-渠道绑定 | 对应 10001 ← 10003 |
| 优惠券模板 / 权益 / 积分规则 / 编号规则 / 字典 | `.http` 测试依赖的模板/规则 |
| 公告 / 商城商品 | list 测试引用的 ID |

### 流程产物（本任务**不管**，由 TEST-ROUND3 链路自造）
订单、退款、预约记录、返点台账、提现、核销、报名、积分流水

## 已验证的环境现状（主 Agent 2026-04-17 实测，子 Agent 可直接信赖）

| 维度 | 现状 |
|---|---|
| DB 连接 | `127.0.0.1:3306/jst` 通，`mp_query_db` / `mp_exec_db` 可用 |
| 后端 | `http://127.0.0.1:8080/actuator/health` = 200 |
| MCP 45 flows | 全部就绪（`mp_run_flow_list`） |
| **测试账号** | user_id 10001/10002/10003/10004 **全部缺失**（当前 jst_user 共 12 条，都是用户自建其他 ID） |
| **jst_contest 数据量** | 30 条（用户自建；是否含 contestId 8201/8206 需 Agent 审计） |
| **jst_course 数据量** | 9 条 |
| **contest 子表（真实 schema）** | `jst_contest_schedule`、`jst_contest_award`、`jst_contest_faq`、`jst_contest_reviewer` ✅ 已建表 |
| **先前误述的子表** | ~~jst_contest_round~~ 和 ~~jst_contest_appointment_slot~~ **不存在**（schema 里没这两张表，别再找了） |
| 其他新表 | `jst_biz_no_rule`、`jst_biz_no_seq`、`jst_message`、`jst_user_address` ✅ 已建 |

## 必读文档

1. **当前 fixtures**（作为"应该有什么"的参考清单）：`架构设计/ddl/99-test-fixtures.sql`
2. **DDL 主文件 + migration**（作为"现在 schema 长什么样"的权威）：
   - `架构设计/ddl/01~10-*.sql`
   - `架构设计/ddl/9*-migration-*.sql`（全部，重点下面几个）：
     - `99-migration-refactor-dejson.sql`（jst_contest +16 字段 + 7 张子表）
     - `99-migration-contest-reviewer.sql`
     - `99-migration-biz-no-rule.sql`
     - `99-migration-contest-detail-fields.sql`
     - `99-migration-course-detail-fields.sql`
     - `99-migration-fix-not-null-defaults.sql`
     - `97-migration-add-contest-appointment-fields.sql`
     - `98-migration-phase-e-prep.sql`
     - `98-migration-2026-04-08-add-partner-user-id.sql`
     - `96-migration-user-address.sql`
     - `99-migration-jst-message.sql`
3. **测试用例（权威的前置数据需求方）**：
   - `test/wx-tests.http`
   - `test/admin-tests.http`
   - 逐用例扫 `userId`、`contestId`、`courseId`、`couponId`、`rightId`、`orderNo` 等硬编码 ID

4. **项目记忆**：测试 DB 连接 `127.0.0.1:3306/jst`，root/123456（`C:\Users\X\.claude\projects\D--coding-jst-v1\memory\project_test_db.md`）

## 可用工具

- **DB 读**：`mp_query_db`（只读查询，随便用）
- **DB 写**：`mp_exec_db`（INSERT/UPDATE/DDL；**本任务只允许 INSERT**）
- **文件**：Read / Write / Edit / Grep

## 工作流程

### Step 1：环境探测

1. 用 `mp_query_db "SELECT COUNT(*) FROM jst_user"` 等确认 DB 能连
2. `SHOW TABLES` 列出所有表，对照 DDL 主文件确认 schema 已是最新（已知真实子表：`jst_contest_schedule` / `jst_contest_award` / `jst_contest_faq` / `jst_contest_reviewer`；另有 `jst_biz_no_rule`/`jst_biz_no_seq`/`jst_message`/`jst_user_address`）
3. **若 schema 不是最新**（缺表或缺字段）：停止，在报告中列出缺失项，不要试图 ALTER。这表示用户还没跑全 migration，需用户先处理。

### Step 2：盘点前置数据需求

遍历 `test/wx-tests.http` + `test/admin-tests.http`，产出一份**前置数据需求清单**：

```
[用户] 10001 (学生 zhangsan), 10002 (学生 lisi), 10003 (渠道 agent_a), 10004 (赛事方 partner_a)
[赛事] 8201 (常规-若依少儿编程), 8206 (团队-XX 机器人), 8207 (...)
[赛事子表] 8201.round: 需要 1 条; 8206.appointment_slot: 需要 2 条
[课程] 7101, 7102
[优惠券模板] 100 (满减券), 101 (直减券)
[权益] ...
[积分规则] 报名送积分规则
[公告] ...
[商城商品] ...
[渠道认证] agent_a 已通过
[绑定关系] 10001 ← 10003
...
```

这份清单贴到完工报告。

### Step 3：审计当前 DB 现状

对 Step 2 的每个前置项，用 `mp_query_db` 查 DB 现状：
- 用户是否存在：`SELECT user_id, user_name, phone FROM jst_user WHERE user_id IN (10001, 10002, 10003, 10004)`
- 赛事是否存在且字段齐全：`SELECT contest_id, name, schedule_json, awards_json FROM jst_contest WHERE contest_id IN (8201, 8206, ...)`
- 子表数据：`SELECT ... FROM jst_contest_schedule WHERE contest_id IN (...)`（及 _award / _faq / _reviewer）
- 以此类推

产出**现状差异矩阵**（贴报告）：

| 前置项 | DB 现状 | 缺口 | 处理 |
|---|---|---|---|
| user 10001 | 存在 | 无 | 跳过 |
| user 10002 | 缺失 | 整条 | 补 INSERT |
| contest 8201 | 存在 | `schedule_json` 为 NULL | **跳过**（用户数据不改） |
| contest 8206 | 缺失 | 整条 | 补 INSERT |
| contest_schedule 8206 | 缺失 | 2 条 | 补 INSERT |
| ... | ... | ... | ... |

**铁律**：如果某条记录**已存在**（主键命中），**一律跳过**，**不更新**（即使字段值看起来"过时"）。用户的手工改动优先。

### Step 4：产出补丁 SQL

新建文件 `架构设计/ddl/99-test-data-patch.sql`（**新增文件**，不改原 99-test-fixtures.sql）：

```sql
-- ═════════════════════════════════════════════════
-- 测试前置数据补丁（FIX-FIXTURES 产出）
-- 生成时间: 2026-04-17
-- 策略: 幂等 INSERT IGNORE，只增不删不改
-- 适用: 本地 127.0.0.1:3306/jst（已含用户手工业务数据）
-- ═════════════════════════════════════════════════

SET FOREIGN_KEY_CHECKS = 0;

-- [用户] 补齐 10002
INSERT IGNORE INTO jst_user (user_id, user_name, phone, ...) VALUES (10002, 'lisi', '13800000002', ...);

-- [赛事] 补齐 8206（团队赛）
INSERT IGNORE INTO jst_contest (contest_id, name, schedule_json, awards_json, ...) 
VALUES (8206, 'XX 机器人赛', '[]', '[]', ...);

-- [赛事子表] 补齐 8206 的预约时段
INSERT IGNORE INTO jst_contest_schedule (...) VALUES (...);

-- ... 所有 Step 3 差异矩阵中标记"补 INSERT"的项

SET FOREIGN_KEY_CHECKS = 1;

-- ═════════════════════════════════════════════════
-- 执行完毕。再次执行不会产生副作用（INSERT IGNORE 保底幂等）。
-- ═════════════════════════════════════════════════
```

**写法要求**：
- 一律 `INSERT IGNORE`（主键冲突静默跳过）
- **不用** `ON DUPLICATE KEY UPDATE`（会改用户数据）
- **不用** `REPLACE INTO`（会先删后插）
- 字段列表显式写全，不要 `INSERT INTO xxx VALUES (...)` 无列名
- 所有 NOT NULL 字段给明确值（参考当前 schema + `99-migration-fix-not-null-defaults.sql`）

### Step 5：执行补丁

用 `mp_exec_db` 逐段执行 `99-test-data-patch.sql` 的 INSERT。每段执行完 `SELECT COUNT(*)` 验证行数。

**失败处理**：若某条 INSERT 因外键/NOT NULL 报错，**不要回滚用户数据**，记录错误到报告，继续后续。

### Step 6：废弃标记

在 `架构设计/ddl/99-test-fixtures.sql` 文件顶部追加废弃头（**只改注释，不改 SQL 内容**）：

```sql
-- ═════════════════════════════════════════════════
-- ⚠️ 已废弃（2026-04-17，FIX-FIXTURES 任务）
-- 
-- 本文件因字段漂移 + 包含流程产物，不再作为"一键 reset"源使用。
-- 当前测试数据策略：
--   1. 前置数据由 99-test-data-patch.sql 幂等补丁维护（已与当前 schema 对齐）
--   2. 流程产物（订单/退款/预约/台账等）由测试链路自造
--   3. 如需重置，执行 90-reset-fixtures.sql 清流程产物表后重跑 TEST-ROUND3
-- 
-- 保留本文件作历史参考，请勿执行。
-- ═════════════════════════════════════════════════
-- [原文件内容保持原样，不修改不删除]
```

### Step 7：同步 90-reset-fixtures.sql

调整 `架构设计/ddl/90-reset-fixtures.sql`：
- TRUNCATE 清单**只保留流程产物表**（order/refund/appointment/rebate_ledger/withdraw/writeoff/enroll/points_log/message 等）
- **删掉**前置表的 TRUNCATE（jst_user/jst_contest/jst_course 等），不再清前置
- 加注释说明"本脚本仅清流程产物，前置数据由 99-test-data-patch.sql 维护"

## 交付物

### 新增文件
- `架构设计/ddl/99-test-data-patch.sql`（幂等补丁 SQL）

### 修改文件
- `架构设计/ddl/99-test-fixtures.sql`（仅加废弃头注释）
- `架构设计/ddl/90-reset-fixtures.sql`（精简 TRUNCATE 清单）

### 报告
保存到 `subagent/tasks/任务报告/FIX-FIXTURES-完工报告.md`：
1. Step 1 环境探测结果（schema 是否最新）
2. Step 2 前置数据需求清单
3. Step 3 现状差异矩阵（完整）
4. Step 4 补丁 SQL 统计（N 个 INSERT，覆盖 N 张表）
5. Step 5 执行日志（每表 INSERT 行数 + Rows matched 0 的跳过项）
6. 任何 INSERT 失败的错误摘要（如果有）

## DoD

- [ ] `99-test-data-patch.sql` 在当前 `jst` 库执行成功（或失败项已记录到报告）
- [ ] 用户已有数据未被修改（报告里声明"本次未执行任何 UPDATE/DELETE/REPLACE"）
- [ ] 补丁重复执行第二次 0 新增行（幂等验证）
- [ ] `99-test-fixtures.sql` 顶部有废弃头
- [ ] `90-reset-fixtures.sql` TRUNCATE 清单已精简
- [ ] 完工报告齐全
- [ ] commit message：`chore(fixtures): FIX-FIXTURES 前置数据补丁 + 废弃旧 fixtures`

## 不许做的事

- 🔴 **严禁对 `jst` 库任何表执行** `UPDATE` / `DELETE` / `REPLACE` / `TRUNCATE` / `DROP`
- 🔴 **严禁执行 DDL**（`CREATE TABLE` / `ALTER TABLE` / `DROP TABLE`）—— schema 若不对，停下来报给主 Agent
- ❌ 不许动 01~10 主 DDL
- ❌ 不许动 9x-migration-*.sql
- ❌ 不许修改 `99-test-fixtures.sql` 的 SQL 内容（只允许加顶部废弃注释）
- ❌ 不许改 `test/wx-tests.http` 和 `test/admin-tests.http`
- ❌ 不许改 Java/Vue/前端/后端任何代码
- ❌ 不许创建 sandbox schema（用户只授权动 `jst` 库，但也只能 INSERT）
- ❌ 不许"顺手"修 .http 里发现的 ID 不一致问题（记到报告供主 Agent 决策）

## 预计工作量
- Step 1+2 探测+需求盘点：1~1.5 小时
- Step 3 现状审计：1.5~2 小时
- Step 4 写补丁：2 小时
- Step 5 执行+验证：0.5 小时
- Step 6+7 废弃+reset 调整：0.5 小时
- 报告：0.5 小时
- **合计：约 6~7 小时 / 半人日**

## 优先级
高（阻塞 TEST-ROUND3）

---

主 Agent 签名：Claude (Opus 4.7)
派发时间：2026-04-17
版本：任务卡 v3（直操库模式）
后继任务：TEST-ROUND3 全量回归测试
