# 任务卡 WEB-ADMIN-2 - 订单与财务管理页面

## 阶段
E-4-WEB / **ruoyi-ui**（Web Admin Agent）

## 背景
平台运营需要管理订单、退款审核、渠道提现审核、结算。后端 API 已就绪。

## 系统提示词
使用 `subagent/WEB_ADMIN_AGENT_PROMPT.md`

## 交付物

### 页面清单

#### 1. 订单管理 `views/jst/order/`
- **index.vue** — 订单列表
  - 搜索：orderNo / userName / orderStatus / payMethod / 下单时间范围
  - 表格：orderId / orderNo / userName / contestName / totalAmount / payAmount / orderStatus / refundStatus / createTime
  - 操作：查看详情
  - API：`api/jst/order/` — `listOrderMains`

- **detail.vue** — 订单详情（弹窗或抽屉）
  - 订单基本信息 + 支付记录 + 退款记录 + 订单项明细
  - 操作：admin 特批退款（如 orderStatus=paid 且无退款）

#### 2. 退款管理 `views/jst/refund/`
- **index.vue** — 退款列表
  - 搜索：refundNo / userName / refundStatus / 申请时间
  - 表格：refundId / refundNo / orderId / userName / refundAmount / refundStatus / applyTime / auditTime
  - 操作：审核通过 / 驳回 / 执行退款 / 查看详情
  - API：`api/jst/order/` — 退款相关接口

#### 3. 渠道提现管理 `views/jst/withdraw/`
- **index.vue** — 提现申请列表
  - 搜索：channelName / status / 申请时间
  - 表格：settlementId / channelName / withdrawAmount / bankInfo / status / applyTime
  - 操作：审核通过 / 驳回 / 执行打款
  - API：`api/jst/channel/` — `ChannelWithdrawAdminController` 相关

#### 4. 返点台账 `views/jst/rebate/`
- **index.vue** — 返点台账列表
  - 搜索：channelName / orderId / status
  - 表格：ledgerId / channelName / orderId / rebateAmount / status / createTime
  - 只读列表，不可编辑
  - API：`api/jst/channel/` — `listJstRebateLedgers`

#### 5. 赛事结算 `views/jst/settlement/`
- **index.vue** — 赛事结算列表
  - 搜索：contestName / partnerName / status
  - 表格：settlementId / contestName / partnerName / totalAmount / settledAmount / status
  - 操作：确认结算 / 查看明细
  - API：`api/jst/channel/` — 结算相关

## 开发规范
同 WEB-ADMIN-1。金额字段右对齐、保留 2 位小数、红色高亮负数。

## DoD
- [ ] 5 个管理模块页面完成
- [ ] 金额展示格式统一
- [ ] 退款/提现审核操作有二次确认弹窗
- [ ] `npm run build:prod` SUCCESS
- [ ] 任务报告 `WEB-ADMIN-2-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许改小程序

## 优先级：P1
---
派发时间：2026-04-12
