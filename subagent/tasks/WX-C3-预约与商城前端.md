# 任务卡 WX-C3 - 小程序端：个人预约 + 扫码核销 + 积分商城

## 阶段 / 端
阶段 C 前端 / **jst-uniapp**

## 业务背景

C6（团队预约 + 核销引擎）、C7（个人预约）、C8（积分商城）后端已全部合并绿灯。本任务补齐对应的小程序端：
1. **个人预约**：赛事详情页"我要预约"入口 → 选场次/日期 → 下单（付费走 C2）→ 生成预约记录 + 二维码
2. **我的预约**列表 + 详情 + 二维码展示 + 取消
3. **扫码核销**（渠道/教师侧，复用 C6 接口）
4. **积分商城**：商品列表 + 详情 + 兑换下单 + 我的兑换订单 + 详情

## 必读

1. `subagent/tasks/C6-团队预约与扫码核销.md` + `C7-个人预约.md` + `C8-积分商城兑换.md`
2. `test/wx-tests.http` § C6-W / C7-W / C8-W 段（接口清单 + 实测响应，子 Agent 必须照实测 JSON 结构来映射字段，不要臆测）
3. 原型 PNG（严格对齐）：
   - **预约**：`reserve.png` / `my-reserve.png` / `writeoff-apply.png` / `writeoff-record.png` / `checkin-scan.png`
   - **商城**：`points-mall.png` ⭐ / `points-goods-detail.png` ⭐ / `points-order.png` / `points-center.png` / `points-detail.png`
4. 既有参考：`jst-uniapp/pages-sub/my/enroll.vue`、`pages-sub/my/order-detail.vue`、`pages-sub/channel/rebate.vue`（列表+tab+悬浮按钮模板）

## 后端接口清单（已实测绿灯）

### 预约（C6 + C7）
- POST `/jst/wx/appointment/apply` — 入参 `{contestId, sessionCode, appointmentDate, participantId?}`，付费赛事额外返回 `orderId/orderNo/orderStatus`
- POST `/jst/wx/appointment/{id}/cancel`
- GET `/jst/wx/appointment/my?status=&pageNum=&pageSize=`
- GET `/jst/wx/appointment/{id}` — 含 `qrCodes` 数组 + `individualAppointment`/`teamAppointment` 标识
- GET `/jst/wx/appointment/contest/{contestId}/remaining?sessionCode=&appointmentDate=`
- POST `/jst/wx/writeoff/scan` — 入参 `{qrPayload}`（C6 实现）
- GET `/jst/wx/writeoff/records?pageNum=`

### 商城（C8）
- GET `/jst/wx/mall/goods/list?goodsType=&roleLimit=&pageNum=&pageSize=`
- GET `/jst/wx/mall/goods/{id}`
- POST `/jst/wx/mall/exchange/apply` — 入参 `{goodsId, quantity, addressSnapshot?, payMethod}`
- POST `/jst/wx/mall/exchange/{id}/pay/mock-success`
- POST `/jst/wx/mall/exchange/{id}/cancel`
- GET `/jst/wx/mall/exchange/my?status=&pageNum=`
- GET `/jst/wx/mall/exchange/{id}` — 含 `addressSnapshot`

## ⚠️ 契约要点

- `appointment apply` 付费单返回 orderId 后，前端跳"去支付"路径或 toast 提示
- `appointment.qrCodes` 是数组，一个 appointment 多个核销子项各自一码，详情页要 swiper 横滑展示
- `appointment cancel` 已支付单本期不自动退款，仅提示"请联系客服"
- `mall/exchange/apply` 三条分支：
  - 纯积分：返回 status=`paid`（虚拟商品直接 completed）
  - 积分+现金：返回 status=`pending_pay` + orderId，继续走 C2 mock 支付
  - 实物：入参必须带 `addressSnapshot` 对象（**不是 addressId**）
- `mall/exchange/cancel` 仅 `pending_pay` 可取消
- `writeoff/scan` 仅 `jst_channel` / `jst_teacher` 可用，学生访问应 403

## 交付物

### 1. API 封装
- `jst-uniapp/api/appointment.js`（新建）
- `jst-uniapp/api/mall.js`（新建）

### 2. 路由注册
- `jst-uniapp/pages.json` 分包新增 `pages-sub/appointment/` 和 `pages-sub/mall/`

### 3. 预约模块页面（`pages-sub/appointment/`）

- **apply.vue** — 创建个人预约（对照 `reserve.png`）
  - query: `contestId` + `contestName`
  - 场次下拉 + 日期选择器 + 参赛者选择（复用 participant 逻辑）
  - 底部实时剩余名额（调 `/remaining`）
  - 提交：付费单跳 C2 mock 支付，免费单跳 detail

- **my-list.vue** — 我的预约（对照 `my-reserve.png`）
  - tab：全部 / 待使用 / 已使用 / 已取消
  - 卡片：赛事名 / 日期 / 场次 / 核销进度 x/y / 状态
  - 点卡片跳 detail

- **detail.vue** — 预约详情（对照 `writeoff-apply.png` 学生视角）
  - 赛事信息、预约人、场次、日期、状态
  - **二维码 swiper**：多张横滑，每张标注子项名称
  - 关联订单入口（若有 orderId）
  - pending 可取消

- **scan.vue** — 扫码核销（对照 `checkin-scan.png`）
  - **权限门**：仅 `jst_channel` / `jst_teacher`，学生访问路由即跳回 my 并 toast
  - `uni.scanCode({scanType:['qrCode']})` → POST `/writeoff/scan`
  - 成功后展示核销结果卡片 + "继续扫码"

- **writeoff-record.vue** — 核销记录（对照 `writeoff-record.png`）

### 4. 商城模块页面（`pages-sub/mall/`）

- **list.vue** ⭐（对照 `points-mall.png`）
  - 顶部我的积分余额 banner
  - 分类 tab：全部 / 优惠券 / 权益 / 实物
  - 商品 grid：图 / 名 / 积分价 / 现金补差 / 库存
  - 右下悬浮"我的兑换"按钮

- **detail.vue** ⭐（对照 `points-goods-detail.png`）
  - 图 swiper / 名称 / 价格 / 描述
  - 数量步进器
  - 实物商品选地址入口（从 `/jst/wx/user/address/list`；无则提示去完善地址）
  - 底部 CTA"立即兑换"

- **exchange-list.vue** — 我的兑换订单
  - tab：全部/待支付/待发货/已发货/已完成/已取消
  - 卡片跳 detail

- **exchange-detail.vue**（对照 `points-order.png`）
  - 商品快照 / 积分 / 现金 / 数量
  - 收货地址（实物）
  - 状态时间轴
  - pending_pay 显示取消 + 去支付
  - 虚拟商品已发放显示"前往查看"占位入口

### 5. 入口改造

- **赛事详情页**（`pages-sub/event/detail.vue` 或等效文件）：为支持预约的赛事加"我要预约"按钮
- **pages/my/index.vue** "我的服务" grid 追加：
  - 我的预约 → `appointment/my-list`
  - 积分商城 → `mall/list`
- **扫码入口**：仅渠道/教师视角，`pages/my/index.vue` 的渠道 grid 新增"扫码核销" tile

> ⚠️ 若 DEBT-2 已合并则直接加真实项；若未合并，加在已有 `showComingSoon` 占位旁，不冲突。子 Agent 开工前先 `git log` 确认 DEBT-2 状态。

### 6. 视觉规范
- 积分余额主色 `#F5A623`
- 商品价：积分金色 + 现金灰小字
- 二维码容器 500rpx × 500rpx 白底

## DoD

- [ ] 2 个 api 文件 + 9 个核心页面 + 入口改造
- [ ] pages.json 注册
- [ ] 扫码权限门生效（学生不可见）
- [ ] 对照原型 PNG 无明显偏差
- [ ] 任务报告 `WX-C3-预约与商城前端-回答.md` 含 5+ 截图
- [ ] commit: `feat(wx): WX-C3 预约核销与积分商城前端`

## 不许做

- ❌ 不许改后端
- ❌ 不许在任何页面写 fake 数据
- ❌ 不许跳过 PNG 对齐
- ❌ 不许在学生视角显示扫码核销入口
- ❌ 不许自己实现二维码生成（后端已返回 qrCodes payload 字符串，前端用 `uqrcode` 或复用项目已有二维码组件渲染；若无组件则走 base64 image）

## 依赖：C6 ✅ / C7 ✅ / C8 ✅
## 优先级：中

---
派发时间：2026-04-09
