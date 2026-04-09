# 任务卡 C5b - 渠道返点提现审核与打款（admin）⭐⭐ 高风险

## 🔴 阶段 C 第三高风险 feature（仅次于 C2/C4）
涉及**资金出账**、**负向台账自动抵扣**、**多表事务**。任何 bug 会导致渠道方钱打多/打少/漏打。

## CCB 决策点（派发前 user 过目）

| # | 决策 | 选择 |
|---|---|---|
| 1 | 审核流程 | 单级 admin 审核（本期无赛事方参与，与 C4 不同） |
| 2 | 负向抵扣时机 | **approve 时**自动抵扣，不等到 execute |
| 3 | 负向抵扣顺序 | 按 `create_time` 正序（先产生的先抵扣） |
| 4 | 负向抵扣跨渠道？ | 否，仅抵扣同 channel_id 的 negative 行 |
| 5 | 负向不足抵扣怎么办 | `actual_pay_amount = apply_amount - negative_offset`；actual < 0 时拒绝审核通过（`CHANNEL_WITHDRAW_NEGATIVE_OVERFLOW`） |
| 6 | 驳回 | `reviewing → rejected`，ledger 状态回 `withdrawable`（不动 settlement_id 便于审计，只推 status） |
| 7 | 打款 | 本期 **mock 模式**：approve 后一个独立接口 `/execute` 标记 paid 并写 `jst_payment_pay_record`；真实打款留 `RealPayoutService.payout()` 占位 |
| 8 | 状态流转 | `pending → reviewing`（admin 接单，可选）或 **直接 `pending → approved`**（本期省略 reviewing 中间态，避免复杂度） |
| 9 | 幂等 | 同一 settlement_id 重复 execute 只生效一次；Mock 打款 tx_no 可生成也可空 |
| 10 | 权限 | `jst:channel:withdraw:audit` / `jst:channel:withdraw:execute` 拆分 |

## 阶段 / 模块
阶段 C / **jst-channel** + **jst-finance**（写 `jst_payment_pay_record`）

## ⚠️ 实测 Schema 校正（派发前主 Agent 补录）

`jst_rebate_settlement` 实际字段与原任务卡描述有出入，以下为 **live DB 真实列**（C5a 已适配）：
- 没有 `payee_account_json`，用 `bank_account_snapshot VARCHAR(255)`（C5a 存 JSON 字符串到这里）
- 没有 `invoice_json`，用 `invoice_status VARCHAR(20)` + `invoice_id BIGINT`
- 没有 `audit_time`/`execute_time`/`pay_tx_no`，用 `apply_time` / `pay_time` / `pay_voucher_url`
- 驳回/审核备注写 `audit_remark VARCHAR(255)`
- C5b `approve` 时更新 `pay_time` 留空、status=approved、audit_remark 写入
- C5b `execute` 时写 `pay_time=NOW()`、`pay_voucher_url=mockPayoutResult`、status=paid

`jst_payment_pay_record` 实际列：`pay_record_id` / `pay_no` / `business_type` / `business_id` / `target_type` / `target_id` / `amount` / `pay_account` / `pay_time` / `voucher_url` / `operator_id` + 审计列。**没有** `status` / `tx_no`，成功即写入（`pay_no` 充当流水号，`voucher_url` 存 mock 凭证 URL 或占位字符串）。

`jst_rebate_ledger` 的 `settlement_id` 字段确认存在可写。

## ⚠️ 前置准备（派发前主 Agent 已完成）
- C5a 已合并，service 层 `requireCurrentChannelId()` + `PageUtils.startPage()` 模式已建立，C5b admin 侧控制器可直接照搬 `startPage()` + controller 模式（admin 端不依赖 current channel，无此坑）

## 必读上下文

1. **C5a 任务卡 + C5a 实现**（本任务基于 C5a 的 settlement/ledger 数据）
2. `架构设计/11-状态机定义.md` § SM-8 / SM-9
3. `架构设计/ddl/04-jst-channel.sql` 表 `jst_rebate_settlement` + `jst_rebate_ledger`
4. `架构设计/ddl/10-jst-finance.sql` 表 `jst_payment_pay_record`
5. `RuoYi-Vue/jst-order/.../service/impl/RefundServiceImpl.java` 的 execute 逻辑（退款打款和提现打款同构，参照抽象 Mock/Real）
6. `RuoYi-Vue/jst-channel/.../service/impl/ChannelWithdrawServiceImpl.java`（C5a 产出，本任务在此扩展或新建 admin service）

## 涉及表

**写**
- `jst_rebate_settlement`（status + negative_offset_amount + actual_pay_amount + audit_time/execute_time）
- `jst_rebate_ledger`（status 推进：in_review→paid 或回 withdrawable；negative 行被抵扣后加 `settlement_id`）
- `jst_payment_pay_record`（execute 时新增一行，business_type='rebate_withdraw'，business_id=settlementId）

## 涉及状态机

**SM-9 rebate_settlement**（C5b 负责的跃迁）
```
pending  → approved         (审核通过，同时自动负向抵扣)
pending  → rejected         (驳回)
approved → paid             (execute 打款完成)
```

**SM-8 rebate_ledger 联动**
```
in_review → paid          (settlement approved + execute 完成)
in_review → withdrawable  (settlement rejected 回退)
negative  → (无状态变化，仅写回 settlement_id)
```

## 涉及锁

- `lock:channel:withdraw:audit:{settlementId}` — 审核互斥（等 3s，持 10s）
- `lock:channel:withdraw:execute:{settlementId}` — 执行打款互斥（等 3s，持 15s）
- `lock:channel:withdraw:offset:{channelId}` — 跨提现单串行化负向抵扣，防两张提现单同时抢同一批 negative（等 5s，持 10s）

登记到 `架构设计/15-Redis-Key与锁规约.md`。

## 涉及权限

- `jst:channel:withdraw:list`（admin 列表查询所有渠道的提现单）
- `jst:channel:withdraw:audit`（approve/reject）
- `jst:channel:withdraw:execute`（execute 打款）
- `jst:channel:withdraw:query`（详情）

## 接口契约

### 1. GET /jst/channel/withdraw/list
- admin 侧分页列表，支持按 channelId、status 过滤
- 权限：`jst:channel:withdraw:list`
- 不需要 PartnerScope，admin 看全平台

### 2. GET /jst/channel/withdraw/{settlementId}
- 详情（含 payee_account + ledger 明细 + negative 预抵扣预览）
- 权限：`jst:channel:withdraw:query`
- **关键**：返回体里含 `previewNegativeOffset`（如果现在 approve，将抵扣多少 negative）供前端展示

### 3. POST /jst/channel/withdraw/{settlementId}/approve ⭐⭐
- 入参：`WithdrawAuditDTO { auditRemark }`
- 权限：`jst:channel:withdraw:audit`
- 业务逻辑（**@Transactional(rollbackFor=Exception.class)**）：
  1. `lock:channel:withdraw:audit:{settlementId}`
  2. 查 settlement FOR UPDATE，校验 `status == 'pending'` → `CHANNEL_WITHDRAW_STATUS_INVALID`
  3. `lock:channel:withdraw:offset:{channelId}` 嵌套锁
  4. 查该渠道所有 `direction='negative' AND settlement_id IS NULL` 的 ledger，按 create_time ASC，`FOR UPDATE`
  5. 从前往后累加 `offsetAmount`，直到 `offsetAmount >= apply_amount` 或耗尽所有 negative
  6. `actual_pay_amount = apply_amount - offsetAmount`
  7. `actual_pay_amount < 0` → `CHANNEL_WITHDRAW_NEGATIVE_OVERFLOW`（本期直接拒绝；后期可允许部分抵扣并挂账）
  8. `UPDATE jst_rebate_settlement SET status='approved', negative_offset_amount=offsetAmount, actual_pay_amount=actual_pay_amount, audit_remark=?, audit_time=NOW()`
  9. `UPDATE jst_rebate_ledger SET settlement_id=? WHERE ledger_id IN (被抵扣的 negative ids)`（status 保持 negative，仅挂 settlement_id）
  10. 返回 approve result VO

### 4. POST /jst/channel/withdraw/{settlementId}/reject ⭐
- 入参：`WithdrawAuditDTO { auditRemark 必填 }`
- 权限：`jst:channel:withdraw:audit`
- 业务逻辑：
  1. 锁 audit
  2. 校验 `status == 'pending'`
  3. `UPDATE settlement SET status='rejected', audit_remark=?, audit_time=NOW()`
  4. `UPDATE jst_rebate_ledger SET status='withdrawable', settlement_id=NULL WHERE settlement_id=? AND direction='positive'`
  5. （negative 行如果有已 offset 的，这里不会发生因为本期只在 approve 时才 offset，pending 时 negative 未动）

### 5. POST /jst/channel/withdraw/{settlementId}/execute ⭐⭐ 打款
- 入参：无
- 权限：`jst:channel:withdraw:execute`
- 业务逻辑：
  1. `lock:channel:withdraw:execute:{settlementId}`
  2. 查 settlement FOR UPDATE
  3. `status == 'paid'` → **幂等成功**返回（不抛错）
  4. `status != 'approved'` → `CHANNEL_WITHDRAW_STATUS_INVALID`
  5. `payoutService.payout(settlementId, actualPayAmount, payeeAccount)`
     - `MockPayoutServiceImpl.payout`：直接返回 `txNo = "MOCK_PAYOUT_" + settlementId`, `success=true`
     - `RealPayoutServiceImpl.payout`：占位抛 `UnsupportedOperationException`
     - 切换开关：`jst.payout.enabled=mock|real`（复用 C2 的 `WxPayService` 同构模式）
  6. `INSERT jst_payment_pay_record (business_type='rebate_withdraw', business_id=settlementId, target_type='channel', target_id=channelId, amount=actualPayAmount, tx_no, status='success', ...)`
  7. `UPDATE jst_rebate_settlement SET status='paid', execute_time=NOW(), pay_tx_no=?`
  8. `UPDATE jst_rebate_ledger SET status='paid' WHERE settlement_id=? AND direction='positive'`
     （negative 行不动，始终 negative）

## 交付物清单

新增：
- `jst-channel/.../dto/WithdrawAuditDTO.java`
- `jst-channel/.../vo/WithdrawAdminListVO.java`
- `jst-channel/.../vo/WithdrawAdminDetailVO.java`（含 previewNegativeOffset）
- `jst-channel/.../service/ChannelWithdrawAdminService.java`
- `jst-channel/.../service/impl/ChannelWithdrawAdminServiceImpl.java`
- `jst-channel/.../controller/ChannelWithdrawAdminController.java`
- `jst-channel/.../payout/PayoutService.java` 接口
- `jst-channel/.../payout/impl/MockPayoutServiceImpl.java`
- `jst-channel/.../payout/impl/RealPayoutServiceImpl.java`
- `jst-channel/.../mapper/lookup/PaymentPayRecordLookupMapper.java` + xml（写 jst-finance 表，跨模块走 lookup）

修改：
- `jst-common/.../BizErrorCode.java`：
  - `CHANNEL_WITHDRAW_NEGATIVE_OVERFLOW(40015, "负向台账抵扣后金额小于 0")`
  - `CHANNEL_PAYOUT_FAILED(40016, "打款执行失败")`
- `架构设计/15-Redis-Key与锁规约.md` 登记 3 把锁
- `架构设计/ddl/99-test-fixtures.sql`：
  - 在 C5a fixture 基础上，追加 1 个待审核 approved 场景 + 1 个 negative 抵扣场景（渠道预置 2 条 negative 行 total -30，settlement apply=100 → offset=30 → actual_pay=70）
  - 追加 1 个 negative overflow 场景（apply=50, negative total=-80 → overflow 拒绝）
- `test/admin-tests.http` 追加 C5b 测试段

## 测试场景

- **C5b-A1** admin 登录
- **C5b-A2** 列表分页（不过滤）
- **C5b-A3** 详情 preview negative offset 字段正确
- **C5b-A4** approve happy path（无 negative，actual_pay = apply）
- **C5b-A5** approve with negative offset（actual_pay = apply - 30）
- **C5b-A6** approve negative overflow 应失败（40015）
- **C5b-A7** reject happy path（ledger 回到 withdrawable）
- **C5b-A8** reject already-approved 应失败（40013）
- **C5b-A9** execute approved settlement（写 payment_pay_record，ledger → paid）
- **C5b-A10** execute 重复应幂等成功
- **C5b-A11** execute pending settlement 应失败（40013）
- **C5b-A12** 渠道角色调 admin 接口应 403

## DoD

- [ ] mvn compile SUCCESS
- [ ] 所有 .http 测试通过
- [ ] 锁已登记
- [ ] 任务报告写到 `subagent/tasks/任务报告/C5b-admin审核与打款-回答.md`
- [ ] commit message: `feat(jst-channel): C5b 渠道提现审核与打款 (SM-9/SM-8)`

## 不许做

- ❌ 不许改 C5a 已有逻辑（有问题反馈主 Agent）
- ❌ 不许在 jst-channel 里直接写 jst_payment_pay_record insert SQL 而不走 lookup mapper
- ❌ 不许在 Real payout 里乱写真实 API 调用
- ❌ 不许把 approve + execute 合成一个接口（必须拆）
- ❌ 不许删 SM-9 的中间态

## 依赖
前置：C5a 必须已合并

## 优先级
高

---
派发时间：2026-04-09
