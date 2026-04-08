# 14. 定时任务清单（基于若依 Quartz）

> 决策 Q2：使用若依自带 `ruoyi-quartz` 模块，**禁止引入 XXL-JOB**
> 实现要点：每个 Job 实现 `IJobHandler` 风格的 Spring Bean，通过若依后台「定时任务调度」配置 cron。
> 落点：每个 Job 落在对应业务模块的 `com.ruoyi.jst.{module}.job` 包下。

---

## 总览

| # | Job 名称 | 落点模块 | cron 建议 | 幂等键 | 关联状态机 |
|---|---|---|---|---|---|
| J01 | 订单超时未支付取消 | jst-order | `0 */5 * * * ?` (每5分钟) | order_no | SM-1 pending_pay→cancelled |
| J02 | 售后截止扫描自动完成 | jst-order | `0 30 * * * ?` (每小时30分) | order_id | SM-1 in_service→completed |
| J03 | 团队预约场次结束扫描 | jst-order | `0 0 * * * ?` (每小时整点) | team_appointment_id | SM-13 →partial_writeoff_ended/no_show |
| J04 | 个人预约过期扫描 | jst-order | `0 15 * * * ?` | appointment_id | SM-11 →expired |
| J05 | 返点台账自动计提 | jst-channel | `0 0 1 * * ?` (每天 01:00) | order_item.id | SM-8 pending_accrual→withdrawable |
| J06 | 优惠券到期失效 | jst-marketing | `0 0 2 * * ?` | user_coupon_id | SM-16 →expired |
| J07 | 用户权益到期失效 | jst-marketing | `0 5 2 * * ?` | user_rights_id | SM-17 →expired |
| J08 | 临时档案自动认领扫描 | jst-user | `0 30 3 * * ?` | participant_id | SM-14 unclaimed→auto_claimed |
| J09 | 消息发送失败重试 | jst-message | `0 */10 * * * ?` (每10分钟) | log_id | — |
| J10 | 兑换订单未支付取消 | jst-points | `0 */5 * * * ?` | exchange_no | SM-18 pending_pay→closed |
| J11 | 风险预警自动巡检 | jst-risk | `0 0 */2 * * ?` (每2小时) | rule_id+target_id | — |
| J12 | 凭证审核超时提醒 | jst-order | `0 0 9 * * ?` (每天 09:00) | payment_id | — |

---

## 实现规范（强制）

### 1. 类位置与命名
```
com.ruoyi.jst.{module}.job
  ├── OrderTimeoutCancelJob.java   // J01
  ├── OrderAftersaleEndJob.java    // J02
  ...
```

### 2. 类签名模板
```java
package com.ruoyi.jst.order.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * J01-订单超时未支付自动取消
 * <p>
 * 触发条件：order_status='pending_pay' AND create_time < now() - 30min
 * 状态机：SM-1 pending_pay → cancelled
 * cron：0 *\/5 * * * ?
 * 幂等：基于 order_no 行锁 + 状态校验，重复执行无副作用
 * <p>
 * 在若依「系统监控-定时任务」配置：
 *   bean 调用：orderTimeoutCancelJob.execute()
 */
@Component("orderTimeoutCancelJob")
public class OrderTimeoutCancelJob {
    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutCancelJob.class);

    @Autowired private OrderCancelService orderCancelService;

    public void execute() {
        long start = System.currentTimeMillis();
        int total = 0, success = 0, fail = 0;
        try {
            // 1. 分页扫描，每页 200，避免一次扫表
            // 2. 对每条订单：状态机校验 → 资金回退 → 写 audit_log
            // 3. 异常单条捕获，不影响整体
        } finally {
            log.info("[Job J01] 完成 total={} success={} fail={} cost={}ms",
                     total, success, fail, System.currentTimeMillis()-start);
        }
    }
}
```

### 3. 强制要求
1. **不得直接 SQL 批量 UPDATE 状态**，必须走 Service 层走状态机校验
2. **不得抛出未捕获异常**：单条失败不能影响整批
3. **必须打印开始/结束日志**含 total/success/fail/cost
4. **必须分页处理**，单批次 ≤ 1000
5. **重入保护**：通过 Redisson `lock:job:{jobName}` 防止集群重复触发
6. **入库**：通过若依「定时任务调度」页面配置，bean 名与上文 `@Component("xxxJob")` 一致

### 4. 若依配置 SQL 模板
```sql
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by)
VALUES ('订单超时取消', 'JST_ORDER', 'orderTimeoutCancelJob.execute()', '0 */5 * * * ?', '3', '1', '0', 'admin');
```
（job_group 全部用 `JST_{MODULE}` 前缀以便区分若依原生任务）

---

## 关键 Job 详细规则

### J02 售后截止扫描自动完成 ⭐
- 扫描 `order_status='in_service' AND aftersale_deadline < now() AND 不存在状态∈(pending,approved,refunding) 的售后单`
- 每条：跃迁到 `completed`，触发 J05 候选写入
- 此 Job 是返点计提的前置依赖

### J03 团队预约场次结束扫描 ⭐⭐ 高敏感
- 扫描 `appointment_date < today OR (appointment_date=today AND session 已结束)`
- 必须持锁 `lock:team_appt:{id}` 后再判断当前 status：
  - `booked` → `no_show`
  - `partial_writeoff` → `partial_writeoff_ended`（不可逆）
  - `fully_writeoff` → 不变
- 已在 SM-13 标注：`partial_writeoff_ended` 后**不可再核销**

### J05 返点台账自动计提 ⭐
- 扫描 `rebate_ledger.status='pending_accrual' AND order.order_status='completed' AND order.aftersale_deadline<now() AND list_amount>0`
- 跃迁 `→ withdrawable`
- 同时按 `order_item.rebate_share` 写入金额（在下单时已分摊）
- 已存在退款场景由退款流程同步处理 `rolled_back / negative`，本 Job 不处理

### J08 临时档案自动认领 ⭐
- 扫描 `claim_status='unclaimed' AND guardian_mobile IS NOT NULL`
- 按 `(guardian_mobile, name)` 匹配 jst_user
- 唯一命中 → 写 `participant_user_map` + 跃迁 `auto_claimed`
- 多候选 → 跃迁 `pending_manual`
- 不直接覆盖任何流水表的 participant_id（防孤儿规则）
