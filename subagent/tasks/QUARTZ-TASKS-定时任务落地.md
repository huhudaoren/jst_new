# 任务卡 QUARTZ-TASKS - 定时任务落地

## 阶段
阶段 F / **jst-order + jst-channel + jst-event + ruoyi-quartz**

## 背景
`架构设计/14-定时任务清单(Quartz).md` 定义了多个定时任务但零实现。没有这些任务，订单永远不会自动取消，返点永远不会自动计提，退款超时不会自动处理——交易闭环的关键环节缺失。

## 必读文档
1. `架构设计/14-定时任务清单(Quartz).md` — 任务定义
2. `架构设计/11-状态机定义.md` — 状态跃迁规则
3. `RuoYi-Vue/ruoyi-quartz/` — 若依 Quartz 模块（已有定时任务框架）

## 交付物

### Task 1 — 订单超时自动取消（最高优先级）
**规则**：`pending_pay` 订单超过 30 分钟未支付 → 自动 `cancelled`
**实现**：
- 新建 `jst-order/.../task/OrderTimeoutCancelTask.java`
- 继承若依 `AbstractQuartzJob` 或直接用 `@Component` + 若依 sys_job 注册
- SQL：`SELECT order_id FROM jst_order_main WHERE order_status='pending_pay' AND create_time < NOW() - INTERVAL 30 MINUTE AND del_flag='0'`
- 逐条调 `OrderServiceImpl.cancelOrder(orderId, "系统自动取消：超时未支付")`
- 回退积分冻结 + 优惠券锁定
- 频率：每 5 分钟执行一次
- 日志：每次执行记录处理条数

### Task 2 — 退款超时自动关闭
**规则**：`pending` 退款申请超过 7 天未审核 → 自动 `closed`（非拒绝，仅关闭申请）
**实现**：
- 新建 `jst-order/.../task/RefundTimeoutCloseTask.java`
- SQL：`SELECT refund_id FROM jst_refund_record WHERE refund_status='pending' AND create_time < NOW() - INTERVAL 7 DAY AND del_flag='0'`
- 更新状态为 `closed`，remark 标注"系统自动关闭：超时未审核"
- 频率：每天凌晨 2:00

### Task 3 — 返点售后期自动计提
**规则**：订单售后期结束（`aftersale_deadline < NOW()`）且订单 `completed` → 返点从 `pending` → `usable`（可提现）
**实现**：
- 新建 `jst-channel/.../task/RebateAutoSettleTask.java`
- SQL：
  ```sql
  SELECT l.ledger_id FROM jst_rebate_ledger l
  JOIN jst_order_main o ON o.order_id = l.order_id
  WHERE l.status = 'pending'
    AND o.order_status = 'completed'
    AND o.aftersale_deadline < NOW()
    AND l.del_flag = '0'
  ```
- 逐条更新 `status = 'usable'`
- 频率：每天凌晨 3:00

### Task 4 — 预约过期自动取消
**规则**：预约日期已过但状态仍为 `confirmed` → 自动 `expired`
**实现**：
- 新建 `jst-event/.../task/AppointmentExpireTask.java`
- SQL：`SELECT appointment_id FROM jst_appointment_record WHERE main_status='confirmed' AND appointment_date < CURDATE() AND del_flag='0'`
- 更新 `main_status = 'expired'`
- 频率：每天凌晨 1:00

### Task 5 — sys_job 注册 SQL
**新建**：`架构设计/ddl/99-migration-quartz-jobs.sql`

```sql
-- 幂等注册 4 个定时任务到 sys_job
INSERT IGNORE INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES
('订单超时自动取消', 'JST_TASK', 'orderTimeoutCancelTask.execute()', '0 */5 * * * ?', '2', '1', '0', 'migration', NOW(), '每 5 分钟扫描 pending_pay 超 30 分钟的订单'),
('退款超时自动关闭', 'JST_TASK', 'refundTimeoutCloseTask.execute()', '0 0 2 * * ?', '2', '1', '0', 'migration', NOW(), '每天 02:00 扫描 pending 超 7 天的退款'),
('返点售后期自动计提', 'JST_TASK', 'rebateAutoSettleTask.execute()', '0 0 3 * * ?', '2', '1', '0', 'migration', NOW(), '每天 03:00 扫描售后期结束的返点'),
('预约过期自动取消', 'JST_TASK', 'appointmentExpireTask.execute()', '0 0 1 * * ?', '2', '1', '0', 'migration', NOW(), '每天 01:00 扫描已过期预约');
```

### 实现要点
- 每个 Task 类用 `@Component("xxxTask")` 注册为 Spring Bean
- 提供 `public void execute()` 方法（若依 Quartz 通过 `invoke_target` 反射调用）
- 每次执行用 `try-catch` 包裹，单条失败不影响其他条
- 执行前后 `log.info("[TASK] xxx 开始/完成，处理 N 条")`
- **不用 `@Transactional` 包裹整个批次**（避免大事务），单条内部走 Service 的事务

### 测试验证
- 手动调用验证：`POST /monitor/job/run` 若依原生接口可触发单次执行
- 或在 `test/admin-tests.http` 追加 `QT-1~QT-4` 段，调 `/monitor/job/run` 触发 + 检查结果

## DoD
- [ ] 4 个 Task 类全部实现
- [ ] sys_job 注册 SQL 幂等
- [ ] 每个 Task 有日志输出
- [ ] 单条失败不影响批次
- [ ] mvn compile 18 模块 SUCCESS
- [ ] 任务报告 `QUARTZ-TASKS-回答.md`
- [ ] commit: `feat(be): QUARTZ-TASKS 定时任务落地（订单超时/退款关闭/返点计提/预约过期）`

## 不许做
- ❌ 不许改前端
- ❌ 不许用 `@Transactional` 包裹整个批量扫描（单条内部事务即可）
- ❌ 不许硬编码超时时间（从 application.yml 读或常量类定义）
- ❌ 不许改若依 Quartz 框架代码

## 依赖：无
## 优先级：P1（交易闭环关键环节）

---
派发时间：2026-04-10
