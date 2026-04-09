# 任务卡 C5a - 渠道返点提现申请（渠道方发起）

## 阶段 / 模块
阶段 C / **jst-channel**

## 业务背景

C2 订单支付时已把每笔订单的分成（`rebate_amount`）预计算写入 `jst_rebate_ledger`，初始状态 `pending_accrual`。
- 售后期结束 + 订单 completed 后，定时任务（下一期做）或手动脚本会把 `pending_accrual → withdrawable`。**本任务先假设已有 withdrawable 台账可测**（通过 fixture 预置）。
- C4 退款时已把未提现 ledger 置 `rolled_back`、已提现 ledger 写 `negative` 行。

C5a 要做的是：**渠道方登录小程序/H5 看返点中心 → 勾选可提现台账 → 填写收款账户 → 提交提现申请**。
提现单创建后 ledger 状态推进 `withdrawable → in_review`，提现单 status=`pending`。

C5b（独立任务卡）负责 admin 审核 + negative 自动抵扣 + 打款落账。

## CCB 决策点

| # | 决策 | 选择 |
|---|---|---|
| 1 | 提现发起方 | 仅渠道方本人（角色 `jst_channel`） |
| 2 | 最小提现金额 | 本期不限（≥ 0.01 即可），后期走运营配置 |
| 3 | 一次提现能选多少台账 | 不限，由渠道方勾选；服务端重新校验 status=withdrawable 且 channel_id 一致 |
| 4 | 收款账户 | 本期单字段 JSON（bank_name/account_no/account_name），不走独立表 |
| 5 | 发票信息 | 本期选填 JSON 字段，不做校验不出发票 |
| 6 | 金额一致性 | 前端传 `expectedAmount`，后端重算并比对，误差 0.01 以上直接拒绝 |
| 7 | 幂等 | `settlement_no` 唯一；同一渠道 30s 内重复提交相同勾选拒绝（Redisson 锁 + 查重） |
| 8 | 取消 | 渠道方在 `pending` 状态可取消，ledger 状态回退 `in_review → withdrawable` |
| 9 | 4KPI 聚合 | 返点中心页 4 卡片（可提现/审核中/累计打款/退款回退）一个接口返回 |

## 必读上下文

1. `架构设计/11-状态机定义.md` § SM-8（rebate_ledger）+ SM-9（rebate_settlement）
2. `架构设计/13-技术栈与依赖清单.md`、`15-Redis-Key与锁规约.md`、`16-安全与敏感字段.md`
3. `架构设计/ddl/04-jst-channel.sql` 表 `jst_rebate_ledger` + `jst_rebate_settlement`
4. `架构设计/ddl/10-jst-finance.sql` 表 `jst_payment_pay_record`（C5b 才写，本任务只看结构）
5. **C2 实现**：`RuoYi-Vue/jst-order/.../service/impl/OrderServiceImpl.java` 看 rebate ledger 是如何预写的（字段含义）
6. **C4 实现**：`RuoYi-Vue/jst-order/.../mapper/lookup/RebateLedgerLookupMapper.java/xml` 看 lookup 模式（C5 要在 jst-channel 里做**反方向**的同类跨模块查询？否——**C5a 是 jst-channel 自己模块内写自己的表**，不需跨模块 lookup）
7. 原型图：`小程序原型图/channel-rebate.*` + `channel-settlement.*`

## 涉及表

**写表**
- `jst_rebate_settlement`（主，INSERT）
- `jst_rebate_ledger`（UPDATE status + settlement_id）

**只读**
- `jst_channel`（权限校验、渠道基本信息）
- `sys_user`（登录态）

## 涉及状态机

**SM-8 rebate_ledger 相关跃迁**
```
withdrawable → in_review   (创建提现单)
in_review    → withdrawable (渠道方取消 pending 提现单)
```

**SM-9 rebate_settlement**
```
(new) → pending   创建
pending → closed  渠道方主动取消
```
（reviewing/approved/rejected/paid 由 C5b 负责）

## 涉及锁

- `lock:channel:withdraw:apply:{channelId}` — 防同一渠道并发创建提现单（等待 3s，持有 10s）
- `lock:channel:withdraw:cancel:{settlementId}` — 防并发取消（等待 3s，持有 5s）

**必须先在** `架构设计/15-Redis-Key与锁规约.md` **登记**。

## 涉及权限

- `jst:channel:withdraw:apply` （渠道方创建提现单 + 渠道方取消）
- `jst:channel:withdraw:my` （渠道方查自己的提现单 + 返点中心）

渠道方控制器使用 `@PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:apply')")`。渠道 role_id = 102。

## 接口契约

### 1. GET /jst/wx/channel/rebate/summary ⭐ 返点中心 4KPI
- 入参：无（channelId 从 SecurityUtils 取）
- 出参：`RebateSummaryVO`
  ```
  {
    withdrawableAmount: 1234.56,   // ledger status=withdrawable 且 direction=positive 之和
    reviewingAmount:    200.00,    // ledger status=in_review
    paidAmount:         5000.00,   // ledger status=paid
    rolledBackAmount:   300.00     // ledger status=rolled_back + negative 行合计（绝对值）
  }
  ```
- 权限：`jst_channel` 角色
- 业务逻辑：
  1. `channelId = getCurrentChannelId()`（封装：根据 SecurityUtils.getUserId 查 jst_channel）
  2. 一条聚合 SQL：`SELECT status, SUM(rebate_amount * IF(direction='negative',-1,1)) FROM jst_rebate_ledger WHERE channel_id=? AND del_flag='0' GROUP BY status`
  3. 装 VO

### 2. GET /jst/wx/channel/rebate/ledger/list ⭐ 返点明细列表
- 入参：`RebateLedgerQueryReqDTO { status?, pageNum, pageSize }`
- 出参：`TableDataInfo<RebateLedgerListVO>`（含 order_no/contest_name/amount/status/create_time）
- 权限：同上
- 业务逻辑：`channelId` 强制注入，不允许前端传；contest_name 走 lookup mapper 读 jst-event（新建 `ContestLookupMapper`）

### 3. POST /jst/wx/channel/withdraw/apply ⭐ 创建提现单
- 入参：`WithdrawApplyDTO`
  ```
  {
    ledgerIds:  [1001, 1002, ...],   // 勾选的 ledger id，JSR-303 @NotEmpty
    expectedAmount: 1234.56,          // 前端预计金额，校验用
    payeeAccount: {                   // JSON
      bankName, accountNo, accountName
    },
    invoiceInfo: {...} | null
  }
  ```
- 出参：`WithdrawApplyResVO { settlementId, settlementNo, actualAmount, status }`
- 权限：`jst_channel`
- 业务逻辑（**必须全 @Transactional(rollbackFor=Exception.class)**）：
  1. `SecurityUtils.getUserId()` → `channelId`
  2. `jstLockTemplate.execute("lock:channel:withdraw:apply:" + channelId, 3, 10, () -> {`
  3. `SELECT ... FROM jst_rebate_ledger WHERE ledger_id IN (?) FOR UPDATE`
  4. 逐条校验：`channel_id == currentChannelId` + `status == 'withdrawable'` + `direction == 'positive'`，任一不通过 → `BizErrorCode.CHANNEL_WITHDRAW_LEDGER_INVALID`
  5. `actualAmount = SUM(rebate_amount)`
  6. `abs(actualAmount - expectedAmount) > 0.01` → `CHANNEL_WITHDRAW_AMOUNT_MISMATCH`
  7. 生成 `settlement_no`（格式 `WD{channelId}{yyyyMMddHHmmss}{随机4位}`）
  8. `INSERT jst_rebate_settlement (status='pending', apply_amount=actualAmount, negative_offset_amount=0, actual_pay_amount=NULL, payee_account_json, invoice_json, ...)`
  9. `UPDATE jst_rebate_ledger SET status='in_review', settlement_id=? WHERE ledger_id IN (?)`
  10. 返回 VO
  11. `})`

### 4. POST /jst/wx/channel/withdraw/{settlementId}/cancel ⭐ 取消 pending 提现
- 入参：path param
- 出参：`AjaxResult`
- 权限：`jst_channel`
- 业务逻辑：
  1. 锁 `lock:channel:withdraw:cancel:{settlementId}`
  2. 查 settlement，校验 `channel_id == current` + `status == 'pending'` → `WITHDRAW_STATUS_INVALID`
  3. `UPDATE jst_rebate_settlement SET status='closed', cancel_time=NOW()`
  4. `UPDATE jst_rebate_ledger SET status='withdrawable', settlement_id=NULL WHERE settlement_id=?`

### 5. GET /jst/wx/channel/withdraw/list ⭐ 我的提现单列表
- 入参：`WithdrawQueryReqDTO { status?, pageNum, pageSize }`
- 出参：`TableDataInfo<WithdrawListVO>`
- 权限：`jst_channel`
- 业务逻辑：强制注入 channelId

### 6. GET /jst/wx/channel/withdraw/{settlementId} ⭐ 提现单详情
- 出参：`WithdrawDetailVO`（含 payee_account + invoice_info + ledgerItems 列表）
- 权限：`jst_channel`
- 业务逻辑：校验 ownership

## 交付物清单

新增：
- `jst-channel/.../enums/RebateSettlementStatus.java`（pending/reviewing/rejected/approved/paid/closed + `assertCanTransitTo`）
- `jst-channel/.../dto/WithdrawApplyDTO.java`
- `jst-channel/.../dto/WithdrawQueryReqDTO.java`
- `jst-channel/.../dto/RebateLedgerQueryReqDTO.java`
- `jst-channel/.../vo/RebateSummaryVO.java`
- `jst-channel/.../vo/RebateLedgerListVO.java`
- `jst-channel/.../vo/WithdrawApplyResVO.java`
- `jst-channel/.../vo/WithdrawListVO.java`
- `jst-channel/.../vo/WithdrawDetailVO.java`
- `jst-channel/.../mapper/RebateLedgerMapperExt.java` + xml（渠道自己模块读自己的 ledger，不是 lookup）
- `jst-channel/.../mapper/RebateSettlementMapperExt.java` + xml
- `jst-channel/.../mapper/lookup/ContestLookupMapper.java` + xml（跨模块只读 jst_contest.contest_name）
- `jst-channel/.../service/ChannelWithdrawService.java`
- `jst-channel/.../service/impl/ChannelWithdrawServiceImpl.java`
- `jst-channel/.../controller/wx/WxChannelWithdrawController.java`

修改：
- `jst-common/.../BizErrorCode.java` 追加：
  - `CHANNEL_WITHDRAW_LEDGER_INVALID(40011, "所选返点台账状态非法或不属于当前渠道")`
  - `CHANNEL_WITHDRAW_AMOUNT_MISMATCH(40012, "提现金额与后端重算不一致")`
  - `CHANNEL_WITHDRAW_STATUS_INVALID(40013, "提现单状态非法跃迁")`
  - `CHANNEL_WITHDRAW_LOCK_TIMEOUT(40014, "提现申请处理中，请稍后重试")`
- `架构设计/15-Redis-Key与锁规约.md` 登记 2 把锁
- `架构设计/ddl/99-test-fixtures.sql` 追加：
  - 渠道账号 `channel_c5_a`（user_id=9201，绑定 role_id=102）+ jst_channel 主表
  - 5 条 withdrawable ledger（channel_id 指向上述渠道）
  - 1 条 rolled_back、1 条 paid、1 条 negative（用于 summary 聚合验证）
- `test/wx-tests.http` 追加 C5a 测试段

## 测试场景（.http 必须覆盖）

- **C5a-W1** 渠道登录
- **C5a-W2** 返点 summary 4KPI 断言
- **C5a-W3** ledger list 按 withdrawable 过滤
- **C5a-W4** happy path 创建提现单（勾选 3 条 ledger）
- **C5a-W5** 金额不一致应失败（40012）
- **C5a-W6** 勾选含其他渠道的 ledger 应失败（40011）
- **C5a-W7** 勾选含已 in_review 的 ledger 应失败（40011）
- **C5a-W8** 取消 pending 提现单（ledger 回到 withdrawable）
- **C5a-W9** 取消 already-closed 的应失败（40013）
- **C5a-W10** 学生角色调此接口应 403

## DoD

- [ ] mvn compile 18 模块 SUCCESS
- [ ] .http 测试块追加
- [ ] 锁文档已登记
- [ ] 自检答题（SYSTEM PROMPT Step 2）
- [ ] 任务报告写到 `subagent/tasks/任务报告/C5a-渠道提现申请-回答.md`
- [ ] commit message: `feat(jst-channel): C5a 渠道返点提现申请 (SM-8/SM-9)`

## 不许做

- ❌ 不许改 ddl 主文件（只能改 99-test-fixtures.sql）
- ❌ 不许碰 approve/reject/execute（C5b 的活）
- ❌ 不许写定时任务把 pending_accrual → withdrawable（下一期）
- ❌ 不许在 jst-channel 里 import jst-order entity，跨模块只读走 lookup
- ❌ 不许引入新依赖

## 优先级
高（阻塞 C5b）

---

派发时间：2026-04-09
版本：任务卡 v1
