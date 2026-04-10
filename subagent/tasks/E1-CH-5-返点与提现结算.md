# 任务卡 E1-CH-5 - 返点中心 + 提现结算合并

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
当前：
- `pages-sub/channel/rebate.vue` 骨架 Tab 不全
- `pages-sub/channel/withdraw-apply.vue` / `withdraw-list.vue` / `withdraw-detail.vue` 三页分散

原型 V4.0：
- `channel-rebate.html`：4 格 Hero + **6 状态 Tab** + 明细卡片 + 底部固定提现按钮
- `channel-settlement.html`：**统一的提现与结算页**，7 状态 Tab + 结算单卡片 + 申请表单 + 合同/发票子入口

## PRD 依据
- §7.3 返点中心 + 提现结算
- §8.5 返点状态机（pending → usable → applying → paid/rejected/rolledback）
- 原型：`channel-rebate.html` + `channel-settlement.html`

## 交付物

### 1. 重构 `pages-sub/channel/rebate.vue`

#### A. Hero 4 格（金色主题）
- 可提现余额（突出，大字号金色）
- 审核中金额
- 累计已打款
- 历史退款回退
- 数据来源：`GET /channel/rebate/summary`

#### B. 6 状态 Tab
- 全部
- 待结算（pending）
- 可提现（usable）
- 审核中（applying）
- 已打款（paid）
- 已驳回（rejected）
- 已回退（rolledback）

#### C. 明细卡片
每张明细包含：
- 赛事名称 + 学生名
- 订单号 + 订单状态
- 净实付金额
- 平台服务费
- 返点规则说明
- 返点金额（突出）
- 状态徽章
- 赛事结束时间（售后结束才计提）

数据来源：`GET /channel/rebate/ledger?status=&page=`

#### D. 底部固定栏
- 左：可提现金额
- 右：**申请提现**按钮 → 跳 `channel/settlement`

### 2. 新建 `pages-sub/channel/settlement.vue`（替代 withdraw-list）

合并现有 withdraw-list/withdraw-apply/withdraw-detail 3 页为**单页多视图**：

#### 顶视图 A：状态 Tab（7 个）
- 全部 / 待提交 / 审核中 / 已撤回 / 已驳回 / 待打款 / 已打款

#### 中间：结算单卡片列表
每张包含：
- 结算单编号
- 申请时间
- 审核状态 + 打款状态
- 收款账户（脱敏）
- 发票状态（若有）
- 关联返点明细数
- 金额
- 点击展开：查看详情 / 查看打款凭证 / 查看驳回原因

#### 顶部"新建提现"按钮 → 展开申请表单
- 勾选待提现明细项（多选）
- 填写/选择收款账户
- 填写发票抬头 + 税号（可选，走合同发票子入口）
- 合同/发票子入口（灰标 "F-2 批次"）
- 提交按钮 → `POST /channel/rebate/withdraw`

### 3. API 封装
**修改**：`api/channel.js`

```js
export function getRebateSummary() { return request({ url: '/jst/wx/channel/rebate/summary', method: 'GET' }) }
export function getRebateLedger(params) { return request({ url: '/jst/wx/channel/rebate/ledger', method: 'GET', params }) }
export function getWithdrawList(params) { return request({ url: '/jst/wx/channel/withdraw/list', method: 'GET', params }) }
export function getWithdrawDetail(id) { return request({ url: `/jst/wx/channel/withdraw/${id}`, method: 'GET' }) }
export function applyWithdraw(body) { return request({ url: '/jst/wx/channel/withdraw/apply', method: 'POST', data: body }) }
export function cancelWithdraw(id) { return request({ url: `/jst/wx/channel/withdraw/${id}/cancel`, method: 'POST' }) }
```

这些接口在 C5a/C5b 已全部实现，直接调用即可。

### 4. 页面清理
- **重构**：`rebate.vue`
- **新建**：`settlement.vue`
- **删除**（或保留作为 deprecated 跳转）：`withdraw-list.vue` / `withdraw-apply.vue`
  - 保守起见先**不删**，但从 home 的功能宫格跳转改为 settlement
  - `withdraw-detail.vue` 保留，作为 settlement 展开的详情子页
- 修改 `pages.json` 注册 settlement 页

## DoD
- [ ] rebate.vue 4 Hero + 6 Tab + 底部提现按钮
- [ ] settlement.vue 7 Tab + 卡片列表 + 申请表单
- [ ] 合同/发票 disable 灰标
- [ ] 状态徽章 6 种颜色区分
- [ ] 任务报告 `E1-CH-5-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许真实打通合同发票（F-2 批次的活）
- ❌ 不许删 withdraw-detail.vue（还要用）

## 依赖：E0-1 + E1-CH-2
## 优先级：P1

---
派发时间：2026-04-10
