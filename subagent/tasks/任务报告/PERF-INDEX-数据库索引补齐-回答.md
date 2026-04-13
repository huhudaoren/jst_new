# 任务报告 - PERF-INDEX 数据库索引补齐与查询优化

## A. 任务前自检（Step 2 答题）

1. **涉及表**：jst_order_main, jst_enroll_record, jst_refund_record, jst_points_ledger, jst_user_coupon, jst_user_rights, jst_message, jst_channel_auth_apply
2. **涉及状态机**：无（纯索引/配置任务）
3. **涉及权限标识**：无
4. **涉及锁名**：无
5. **事务边界**：无（DDL + 配置变更）
6. **ResVO 字段**：无
7. **复用样板**：不适用（非 CRUD 任务）

## B. 交付物清单

**新增文件：**
- `架构设计/ddl/99-migration-perf-indexes.sql` — 5 组索引迁移（7 条 ALTER TABLE）

**修改文件：**
- `RuoYi-Vue/ruoyi-admin/src/main/resources/application-test.yml` — 追加 druid 连接池参数（maxActive=30）
- `RuoYi-Vue/ruoyi-admin/src/main/resources/application-prod.yml` — 追加 druid 连接池参数（initialSize=10, minIdle=10, maxActive=50, maxWait=30000）

## C. mvn compile 结果

```
[INFO] BUILD SUCCESS
[INFO] Total time:  17.133 s
```

18/18 模块全部 SUCCESS。

## D. 索引执行与 EXPLAIN 验证

### D1. 索引对比结果（SHOW INDEX FROM 确认）

| # | 表 | 建议索引 | 现有 | 操作 |
|---|---|---|---|---|
| 1 | jst_order_main | idx_user_order_status(user_id, order_status, create_time) | 仅单列 idx_user_id + idx_order_status | ✅ 新建 |
| 2 | jst_enroll_record | idx_contest_audit(contest_id, audit_status, create_time) | 仅单列 idx_contest_id + idx_audit_status | ✅ 新建 |
| 3 | jst_refund_record | idx_order_status_time(order_id, status, create_time) | 仅单列 idx_order_id + idx_status | ✅ 新建 |
| 4 | jst_points_ledger | idx_owner_time(owner_type, owner_id, create_time) | idx_owner(owner_type,owner_id) 缺 create_time | ✅ 删旧建新（超集替换） |
| 5 | jst_user_coupon | idx_user_status(user_id, status) | ✅ 已存在 idx_user_status | ⏭️ 跳过 |
| 6 | jst_user_rights | idx_owner_status(owner_type, owner_id, status) | ✅ 已存在 idx_owner_status | ⏭️ 跳过 |
| 7 | jst_message | idx_user_read_time(user_id, read_status, create_time) | idx_user_read(user_id,read_status) 缺 create_time | ✅ 删旧建新（超集替换） |
| 8 | jst_channel_auth_apply | idx_user_id(user_id) | ✅ 已存在 idx_user_id | ⏭️ 跳过 |

**实际执行：新建 3 + 替换扩展 2 = 共 7 条 ALTER TABLE，跳过 3 个已存在索引。**

### D2. EXPLAIN 热点查询验证（5 个新索引）

```
-- 1. 订单: WHERE user_id=1 AND order_status='paid' ORDER BY create_time DESC LIMIT 10
key: idx_user_order_status | type: ref | rows: 1 | Extra: Using where

-- 2. 报名: WHERE contest_id=1 AND audit_status='passed' ORDER BY create_time DESC LIMIT 20
key: idx_contest_audit | type: ref | rows: 1 | Extra: Using where

-- 3. 退款: WHERE order_id=1 AND status='approved' ORDER BY create_time DESC
key: idx_order_status_time | type: ref | rows: 1 | filtered: 100% | Extra: Using where

-- 4. 积分: WHERE owner_type='student' AND owner_id=1 ORDER BY create_time DESC LIMIT 20
key: idx_owner_time | type: ref | rows: 1 | filtered: 100% | Extra: Using where

-- 5. 消息: WHERE user_id=1 AND read_status=0 ORDER BY create_time DESC LIMIT 20
key: idx_user_read_time | type: ref | rows: 1 | filtered: 100% | Extra: Using where
```

**全部走新建组合索引，type=ref（索引查找），无全表扫描。**

### D3. 高 JOIN 查询验证（3 个 Mapper）

| Mapper | JOIN 数 | 主表 key (type) | JOIN 表 type | filesort | 结论 |
|---|---|---|---|---|---|
| JstUserCouponMapper.selectList | 3 LEFT JOIN | idx_user_status (ref) | const / eq_ref (PK) | 有，rows=1 | ✅ 无需额外优化 |
| JstAppointmentRecordMapper.selectList | 3 LEFT JOIN | idx_user_id (ref) | const / eq_ref (PK) | 有，rows=1 | ✅ 无需额外优化 |
| JstRebateLedgerMapper.selectList | 2 LEFT JOIN | idx_channel_status (ref) | const / eq_ref (PK) | 有，rows=1 | ✅ 无需额外优化 |

3 个高 JOIN 查询主表走索引过滤后 rows 极低，JOIN 全走 PK，filesort 在小结果集上无性能问题。

### D4. 连接池参数调整

| 环境 | initialSize | minIdle | maxActive | maxWait | 来源 |
|---|---|---|---|---|---|
| dev | 5 | 10 | 20 | 60000 | application-druid.yml（基础配置，不改） |
| test | 5 | 10 | **30** | 60000 | application-test.yml 覆盖 |
| prod | **10** | **10** | **50** | **30000** | application-prod.yml 覆盖 |

## E. 遗留 TODO

- 无

## F. 任务卡之外的操作

- 无。3 个已有索引（jst_user_coupon / jst_user_rights / jst_channel_auth_apply）确认后直接跳过，未做多余操作。
- jst_points_ledger 和 jst_message 采用"先删旧索引 → 再建扩展超集"策略，避免冗余索引占用空间。

## G. 自检清单

- [x] 索引 SQL 生成（5 组 7 条 ALTER TABLE）
- [x] 在测试库执行索引 SQL — 全部成功
- [x] 对 5 个热点查询执行 EXPLAIN 确认走索引 — 全部 type=ref
- [x] 对 3 个高 JOIN 查询执行 EXPLAIN — 全部走索引，无全表扫描
- [x] 连接池参数按环境优化 — test(30) / prod(50)
- [x] `mvn compile -DskipTests` 通过 — 18/18 SUCCESS
