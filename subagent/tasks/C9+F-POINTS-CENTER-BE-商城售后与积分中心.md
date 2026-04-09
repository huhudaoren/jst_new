# 任务卡 C9 + F-POINTS-CENTER-BE - 商城售后退款 + 积分中心后端

## 阶段 / 模块
阶段 C 补齐 / **jst-points**（单模块打包，避免 BizErrorCode / service 冲突）

## 业务背景

两个独立 feature 合并派发（同模块，一个 agent 写完，避免 merge 冲突）：

### Part A — C9 商城售后退款
C8 交付了积分商城兑换的正向链路（下单/发货/完成），但**未做售后**。本任务补齐：
- 虚拟商品已 used 不可退；未 used 可退（券 voided / 权益回扣）
- 实物商品 pending_ship 可整单退；shipped 后需发起退货退款
- 资金回退顺序：**现金 → 积分**（券本期不涉及，商城兑换不消耗券）
- 参考 **C4 RefundServiceImpl** 的拆分（apply → approve → execute）

### Part B — F-POINTS-CENTER-BE 积分中心后端
对应 `points-center.html` 原型，PRD §12。需要给前端提供：
- 当前等级 / 下一级差值 / 等级权益列表
- 积分统计（可用 / 冻结 / 累计获取 / 累计消耗）
- 积分流水 + 成长值流水（两套，分开查）
- 等级配置查询（按角色）

## ⚠️ 实测 Schema（主 Agent 已 probe 活库）

### Part A 复用表
- `jst_mall_exchange_order`：主键 `exchange_id`（不是 order_id），已有 `aftersale_status VARCHAR(20)` 列
- `jst_refund_record`：全字段复用（refund_type / apply_cash / apply_points / actual_cash / actual_points / status / audit_remark / complete_time）— **跨 jst-order 模块走 lookup**
- `jst_points_account`：`owner_type='user'` + `owner_id=userId`，含 `available_points/frozen_points/total_spend/version`
- `jst_points_ledger`：`change_type` / `source_type` 字符串
- `jst_user_coupon`：status=`voided` 回退
- `jst_user_rights`：`remain_quota` 回扣 + status 变更

### Part B 读表
- `jst_level_config`：`level_id / level_code / level_name / level_no / growth_threshold / applicable_role / status`
- `jst_growth_ledger`：`ledger_id / account_id / change_type / source_type / source_ref_id / growth_change / balance_after / level_before / level_after`
- `jst_points_account`：读 `growth_value / current_level_id / total_earn / total_spend`
- `jst_points_ledger`：读流水

## CCB 决策（已定）

| # | 决策 | 选择 |
|---|---|---|
| 1 | 售后发起方 | 学生发起 + admin 特批（参考 C4） |
| 2 | 退款金额 | **仅全额退**（本期，参考 C4） |
| 3 | 虚拟券已 used | 不可退（`MALL_AFTERSALE_COUPON_USED`） |
| 4 | 虚拟权益已核销 | 不可退 |
| 5 | 实物 shipped 后 | 走退货退款（需 admin 确认收货再退款） |
| 6 | 退款审核 | 单级 admin 审核（不走赛事方，商城不涉及赛事方） |
| 7 | 现金部分退款 | 复用 `WxPayService` mock/real |
| 8 | 积分回退 | 直接 `available_points += points_used`，写 ledger 反向行 `change_type='aftersale_refund'` |
| 9 | 等级计算 | 由 `jst_level_config` 查当前/下一级；`growth_value >= threshold` 即当前等级 |
| 10 | 积分任务 | **本期不实现任务领取**（`point_tasks` 表不存在，占位返回空列表给前端，任务卡 §G 留 TODO） |

## 必读

1. PRD §7.5 / §7.6 / §8.7（商城售后）
2. PRD §12 积分中心（等级 Hero + 成长值进度 + 权益格 + 任务）
3. `RuoYi-Vue/jst-order/.../service/impl/RefundServiceImpl.java`（C4 模板）
4. `RuoYi-Vue/jst-points/.../service/impl/MallExchangeServiceImpl.java`（C8 基座）
5. 原型 `小程序原型图/points-center.png` / `points-detail.png` / `points-order.html`（售后流程）

## Part A 接口 - 商城售后

### POST /jst/wx/mall/aftersale/apply ⭐
- 权限：`jst:points:mall:aftersale:apply`
- 入参：`{exchangeId, reason, refundType}` 其中 refundType='refund_only'(虚拟/未发货) | 'return_refund'(实物已发货)
- 逻辑（@Transactional）：
  1. 查 exchange FOR UPDATE，校验 owner
  2. 虚拟商品：若 user_coupon.status='used' 或 user_rights.status='used' → 拒绝
  3. 实物 + status in (pending_pay) → 直接走 cancel 逻辑而不是 aftersale
  4. 锁 `lock:mall:aftersale:{exchangeId}`
  5. INSERT `jst_refund_record (order_id=exchangeId 映射现金 order_id, apply_cash, apply_points, status='pending_audit', refund_type='aftersale')` — 注意 order_id 要走 MallOrderLookupMapper 查到现金部分订单
  6. UPDATE `jst_mall_exchange_order.aftersale_status='applying'`
  7. 返回 `AftersaleApplyResVO {refundId, refundNo}`

### GET /jst/wx/mall/aftersale/my — 我的售后列表分页
- `my-list` + tab（applying/approved/rejected/completed）

### GET /jst/wx/mall/aftersale/{refundId} — 详情

### POST /jst/points/mall/aftersale/{refundId}/approve ⭐ admin
- 权限：`jst:points:mall:aftersale:audit`
- 逻辑：
  1. 查 refund FOR UPDATE，校验 status='pending_audit'
  2. 乐观锁回退 `jst_points_account`：`available_points += points_used`，写 ledger `change_type='aftersale_refund'` `balance_after=新值`
  3. 若 cash > 0：调 `WxPayService.refund(payRecordId, amount)` mock/real
  4. 若虚拟商品 user_coupon.status in (new/issued) → status='voided'；已 used → 已在 apply 阶段拒
  5. 若虚拟商品 user_rights：`remain_quota -= 0`（已是 0 保持）或 status='voided'
  6. 实物退货退款：`exchange.status='refunded'`；pending_ship 的直接回退库存
  7. `refund.status='completed'`, `complete_time=NOW()`
  8. `exchange.aftersale_status='completed'`

### POST /jst/points/mall/aftersale/{refundId}/reject — admin 驳回
- `refund.status='rejected'`, `exchange.aftersale_status='rejected'`

## Part B 接口 - 积分中心

### GET /jst/wx/points/center/summary ⭐
- 返回：
  ```json
  {
    "availablePoints": 1250,
    "frozenPoints": 100,
    "totalEarn": 3500,
    "totalSpend": 2150,
    "growthValue": 850,
    "currentLevel": {"levelId":2,"levelCode":"L2","levelName":"银卡","levelNo":2,"growthThreshold":500,"icon":"..."},
    "nextLevel": {"levelId":3,"levelCode":"L3","levelName":"金卡","levelNo":3,"growthThreshold":2000},
    "pointsToNextLevel": 1150
  }
  ```

### GET /jst/wx/points/center/levels?role=student — 等级权益列表
- 返回全部 level_config，按 `level_no` 升序，带 `unlocked` 标志（当前用户 growth_value >= threshold）

### GET /jst/wx/points/ledger?changeType=&pageNum=&pageSize= — 积分流水分页

### GET /jst/wx/points/growth/ledger?pageNum=&pageSize= — 成长值流水分页

### GET /jst/wx/points/tasks — 积分任务列表
- 本期返回**固定 mock 列表**（5 条静态数据：每日签到 / 报名参赛 / 购买课程 / AI课堂 / 获奖奖励），前端展示用
- 不接入真实任务表（不存在）
- TODO: 后期接入 `jst_point_task` 表

## 交付物

### Part A 新增
- `jst-points/.../dto/AftersaleApplyDTO.java`
- `jst-points/.../vo/AftersaleApplyResVO.java / AftersaleListVO.java / AftersaleDetailVO.java`
- `jst-points/.../enums/AftersaleStatus.java`（applying/approved/rejected/completed/cancelled）
- `jst-points/.../service/MallAftersaleService.java` + Impl
- `jst-points/.../controller/wx/WxMallAftersaleController.java`
- `jst-points/.../controller/MallAftersaleAdminController.java`
- `jst-points/.../mapper/lookup/RefundRecordLookupMapper.java` + xml（写 jst-order.jst_refund_record）

### Part B 新增
- `jst-points/.../vo/PointsCenterSummaryVO.java / LevelVO.java / PointsTaskVO.java`
- `jst-points/.../service/PointsCenterService.java` + Impl
- `jst-points/.../controller/wx/WxPointsCenterController.java`
- `jst-points/.../mapper/lookup/LevelConfigLookupMapper.java` + xml（读 jst_level_config）
- `jst-points/.../mapper/lookup/GrowthLedgerLookupMapper.java` + xml

### 共用修改
- `jst-common/.../BizErrorCode.java` 追加：
  - `MALL_AFTERSALE_NOT_ALLOWED(60030)`
  - `MALL_AFTERSALE_COUPON_USED(60031)`
  - `MALL_AFTERSALE_STATUS_INVALID(60032)`
- `架构设计/15-Redis-Key与锁规约.md` 登记 `lock:mall:aftersale:{exchangeId}`
- `架构设计/ddl/99-test-fixtures.sql` 追加：
  - level_config 4 级（L1 普通/L2 银卡/L3 金卡/L4 钻石）
  - 用户 9001 growth_value=850 位于 L2
  - 售后场景：1 个已支付实物订单可退 / 1 个虚拟券已 used 不可退
- `test/wx-tests.http` + `test/admin-tests.http` 追加 C9 / F-PC 段

## 锁
- `lock:mall:aftersale:{exchangeId}` — 等 3s，持 10s
- 积分回退复用 C8 的 `lock:points:freeze:{userId}`

## 权限
- `jst:points:mall:aftersale:apply`（wx 学生）
- `jst:points:mall:aftersale:my`（wx 学生）
- `jst:points:mall:aftersale:audit`（admin）

## 测试

**C9 售后**
- C9-W1 虚拟券未使用，申请退款 → approved → 积分回退 + voided
- C9-W2 虚拟券已 used → 拒绝 60031
- C9-W3 实物 pending_ship 申请退款 → approved → 积分回退 + 恢复库存
- C9-W4 实物 shipped 申请退货退款 → approved → 积分回退
- C9-W5 重复申请应幂等拒绝
- C9-A1 admin approve 走 mock 退款
- C9-A2 admin reject

**F-POINTS-CENTER**
- PC-W1 summary 当前等级 + 下级差值正确
- PC-W2 levels 列表按角色过滤
- PC-W3 积分流水分页
- PC-W4 成长值流水分页
- PC-W5 tasks 返回 5 条 mock

## DoD
- [ ] mvn compile SUCCESS
- [ ] .http 全绿
- [ ] 任务报告 `C9+F-POINTS-CENTER-BE-回答.md`
- [ ] commit: `feat(jst-points): C9 商城售后 + F-POINTS-CENTER-BE 积分中心`

## 不许做
- ❌ 不许跨 import jst-marketing / jst-order entity，一律 lookup mapper
- ❌ 不许改 C8 已有 apply/cancel 路径
- ❌ 不许触碰 jst_user_coupon 的积分余额字段（不存在）
- ❌ 不许建新表（level/growth 已有）
- ❌ 不许实现真实任务系统

## 依赖：C8 ✅ / C4 参考
## 优先级：高

---
派发时间：2026-04-09
