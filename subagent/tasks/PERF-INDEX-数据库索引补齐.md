# PERF-INDEX — 数据库索引补齐与查询优化

> 优先级：P1 | 预估：S | Agent：Backend Agent

---

## 一、背景

系统预计年服务 100 万参赛用户，报名高峰日 3~5 万人，峰值并发 500~2000 QPS。现有 DDL 已有较完善的索引（各表 4-8 个索引），但 Mapper XML 中的复合查询条件与现有索引可能不完全匹配。需要补齐查询热点的组合索引。

## 二、测试库连接信息

```
url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
username: jst_new
password: J8zZpAa4zG8y6a7e
```

**操作步骤**：
1. 先连接测试库，对每张目标表执行 `SHOW INDEX FROM jst_xxx` 获取真实索引列表
2. 对比下方建议索引，已存在的跳过，缺失的才创建
3. 创建后执行 `EXPLAIN` 验证热点查询走新索引

## 三、索引分析与补充

### 3.1 需要补充的组合索引（先查库确认后再建）

以下索引基于 Mapper XML 中的 WHERE 条件分析，现有单列索引无法覆盖的多条件查询：

```sql
-- 1. 订单：用户+状态组合查询（我的订单列表高频）
-- 已有 idx_user_id 和 idx_order_status，但组合查询需覆盖索引
ALTER TABLE jst_order_main ADD INDEX idx_user_order_status (user_id, order_status, create_time DESC);

-- 2. 报名记录：赛事+审核状态（报名管理列表高频）
-- 已有 idx_contest_id 和 idx_audit_status，补组合
ALTER TABLE jst_enroll_record ADD INDEX idx_contest_audit (contest_id, audit_status, create_time DESC);

-- 3. 退款：订单关联+状态（退款管理高频）
ALTER TABLE jst_refund_record ADD INDEX idx_order_status_time (order_id, status, create_time DESC);

-- 4. 积分流水：owner+时间范围（积分中心列表高频）
ALTER TABLE jst_points_ledger ADD INDEX idx_owner_time (owner_type, owner_id, create_time DESC);

-- 5. 用户券：用户+状态（我的优惠券高频）
ALTER TABLE jst_user_coupon ADD INDEX idx_user_status (user_id, status);

-- 6. 用户权益：用户+状态
ALTER TABLE jst_user_rights ADD INDEX idx_owner_status (owner_type, owner_id, status);

-- 7. 消息：用户+已读状态+时间（消息中心高频）
ALTER TABLE jst_message ADD INDEX idx_user_read_time (user_id, read_status, create_time DESC);

-- 8. 渠道认证申请：用户ID（查我的认证）
ALTER TABLE jst_channel_auth_apply ADD INDEX idx_user_id (user_id);
```

### 2.2 连接池参数优化

修改 `application-druid.yml`（或各环境 yml）：

```yaml
spring:
  datasource:
    druid:
      # 开发环境
      initialSize: 5
      minIdle: 5
      maxActive: 20
      
      # 测试环境（application-test.yml 覆盖）
      # maxActive: 30
      
      # 生产环境（application-prod.yml 覆盖）
      # initialSize: 10
      # minIdle: 10
      # maxActive: 50
      # maxWait: 30000
```

### 2.3 慢查询优化

检查以下 Mapper XML 中 LEFT JOIN 数量过多的查询，确认执行计划：

| Mapper | 查询 | JOIN 数 | 建议 |
|---|---|---|---|
| `JstUserCouponMapper.xml` | selectList | 3 个 LEFT JOIN | 确认 explain 是否走索引 |
| `JstAppointmentRecordMapper.xml` | selectList | 3 个 LEFT JOIN | 同上 |
| `JstRebateLedgerMapper.xml` | selectList | 2 个 LEFT JOIN | 同上 |

如果 explain 显示全表扫描，在 JOIN 条件列补充索引。

## 四、产出物

1. SQL 迁移文件：`架构设计/ddl/99-migration-perf-indexes.sql`
2. 连接池参数调整（如已有多环境 yml 则改在对应文件中）
3. 慢查询 explain 报告（在 test 环境执行 EXPLAIN 并记录）

## 五、DoD

- [ ] 索引 SQL 生成（~8 条 ALTER TABLE）
- [ ] 在测试库执行索引 SQL
- [ ] 对 3 个高 JOIN 查询执行 EXPLAIN 确认走索引
- [ ] 连接池参数按环境优化
- [ ] `mvn compile -DskipTests` 通过
- [ ] 报告交付
