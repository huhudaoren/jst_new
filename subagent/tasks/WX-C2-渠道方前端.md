# 任务卡 WX-C2 - 小程序端：渠道方页面集（返点中心 + 提现）

## 阶段 / 端
阶段 C 前端 / **jst-uniapp**

## 业务背景

C5a / C5b 后端实现了渠道方返点中心 + 提现申请 + 管理后台审核打款。本任务补齐渠道方小程序端：
- 返点中心首页（4 KPI 卡片 + 明细列表）
- 创建提现单页（勾选返点、填收款账户、发票信息）
- 我的提现单列表 + 详情 + 取消

**不包含** admin 审核页面（admin 用 PC 后台即可，不在小程序端）。

## 必读

1. `subagent/tasks/C5a-渠道提现申请.md`
2. `subagent/tasks/C5b-admin审核与打款.md`（前端需显示审核中/已打款等状态）
3. `test/wx-tests.http` § C5a 段（接口 URL + 入参 + 响应）
4. 原型 PNG（严格对齐）：
   - `小程序原型图/channel-home.png`
   - `小程序原型图/channel-rebate.png` ⭐
   - `小程序原型图/channel-settlement.png` ⭐
   - `小程序原型图/channel-orders.png`
   - `小程序原型图/channel-data.png`
   - `小程序原型图/channel-students.png`
5. 既有前端参考：`jst-uniapp/pages/my/index.vue`（tab 结构）、`jst-uniapp/pages-sub/my/enroll.vue`（列表页模板）

## 后端接口清单（C5a 实现，已就绪 + 实测绿灯）

- GET `/jst/wx/channel/rebate/summary` — 4 KPI
- GET `/jst/wx/channel/rebate/ledger/list?status=&pageNum=&pageSize=`
- POST `/jst/wx/channel/withdraw/apply`
- POST `/jst/wx/channel/withdraw/{id}/cancel`
- GET `/jst/wx/channel/withdraw/list`
- GET `/jst/wx/channel/withdraw/{id}`

## ⚠️ 契约细节（派发前主 Agent 已实测）

- `withdraw apply` 入参：`{ ledgerIds: Long[], expectedAmount: Number, bankAccount: {bankName,accountName,accountNo}, invoiceInfo: {...}? }`
  - 前端勾选 ledger → 前端计算 expectedAmount → 后端重算比对，误差 >0.01 拒绝（错误码 40012）
  - 收款账户前端组装成 JSON，后端写 `bank_account_snapshot` 字段（一个字符串列）
  - 发票信息本期不强制校验
- `withdraw detail` 返回体含 `applyAmount` / `negativeOffsetAmount`（本期 C5a 阶段为 0）/ `actualPayAmount` 三金额，前端三行展示即可
- `rebate summary` 返回：`{ withdrawableAmount, reviewingAmount, paidAmount, rolledBackAmount }` 全是两位小数字符串
- 错误码：40011（台账无效）/ 40012（金额不一致）/ 40013（状态非法）/ 40014（锁超时）

## 交付物

### 1. API 封装
- `jst-uniapp/api/channel.js`（新建）— 封装以上 6 个接口

### 2. 路由与入口
- 在 `jst-uniapp/pages.json` 分包新增 `pages-sub/channel/` 路径
- **角色感知入口**：`pages-sub/my/index.vue` 判断当前用户 `userType === 'channel'` 时显示"返点中心"/"我的学生"/"渠道数据"入口 tile，学生用户隐藏。userType 从 `store/user.js` 的 `profile` 字段读取，后端登录接口已返回

### 3. 页面（`jst-uniapp/pages-sub/channel/` 下）

- **home.vue** — 渠道首页（对照 `channel-home.png`）
  - 顶部 banner + 4 个快捷入口 tile（返点中心/我的学生/数据/订单）
  - 本月新增学生数、本月订单量、本月预计返点三个数字
  - 最近订单 list 预览

- **rebate.vue** — 返点中心（对照 `channel-rebate.png`）⭐
  - 顶部 4 KPI 卡片（可提现/审核中/累计打款/退款回退）
  - status tab：全部/待结算/可提现/审核中/已打款/已驳回/已回退
  - 明细列表卡片（订单号、赛事名、金额、状态、时间）
  - 底部悬浮"申请提现"按钮（仅当 `withdrawableAmount > 0` 时显示）
  - 点击按钮跳 `withdraw-apply.vue` 并传递已选 ledgerIds（可默认全选可提现项）

- **withdraw-apply.vue** — 创建提现（对照 `channel-settlement.png` 新建表单模态）
  - 可提现余额（顶部大字展示）
  - 勾选列表：ledger items with checkbox
  - 收款账户表单：开户行 / 卡号 / 户名（简单 JSON input）
  - 发票信息表单（选填折叠面板）：抬头 / 税号
  - 底部"提交申请"按钮：调 apply 接口，成功后跳 withdraw-list
  - 前端计算 `expectedAmount = Σ(勾选项 rebateAmount)` 传后端校验

- **withdraw-list.vue** — 我的提现单列表
  - tab：全部/待提交/审核中/已打款/已驳回
  - 卡片：单号 / 金额 / 状态 / 提交时间
  - 点卡片跳详情

- **withdraw-detail.vue** — 提现单详情
  - 单号、金额（apply/negative_offset/actual_pay 三行展示）
  - 收款账户、发票信息
  - 状态时间轴：提交 → 审核通过/驳回 → 打款
  - 关联返点明细列表
  - pending 状态底部【取消申请】按钮

### 4. 占位页面（原型有但本任务不深度做，仅骨架 + TODO 标注）
- **orders.vue**（`channel-orders.png`）仅列表骨架 + "功能开发中"提示
- **students.vue**（`channel-students.png`）仅列表骨架
- **data.vue**（`channel-data.png`）仅骨架
- 这 3 个页面等 F-CHANNEL-DASHBOARD 后端接口 ready 再迭代

## 视觉对齐

- KPI 卡片配色按原型（4 种渐变色）
- 金额大号字体 28rpx / 32rpx / 40rpx 分级
- 状态标签色板与 WX-C1 保持一致
- 按钮：主按钮 primary 蓝，次要 outline

## DoD

- [ ] 1 个 api 文件、5 个核心页面、3 个骨架页面
- [ ] pages.json 注册
- [ ] 个人中心首页渠道用户入口切换逻辑验证
- [ ] 对照原型 PNG 无明显偏差
- [ ] 任务报告含 5+ 截图
- [ ] commit: `feat(wx): WX-C2 渠道方返点与提现页面`

## 不许做

- ❌ 不许改后端
- ❌ 不许在 channel 页面复用 student 的权限逻辑（userType 判断必须做）
- ❌ 不许跳过 PNG 对齐
- ❌ 不许在骨架页面里写 fake 数据

## 依赖：C5a 后端合并 ✅
## 优先级：中

---
派发时间：2026-04-09
