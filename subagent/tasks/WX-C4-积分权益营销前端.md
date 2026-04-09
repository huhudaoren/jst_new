# 任务卡 WX-C4 - 小程序端：积分中心 + 优惠券 + 权益核销 + 营销活动

## 阶段 / 端
阶段 C 前端 / **jst-uniapp**

## 业务背景

批 4 并行：后端 `C9+F-POINTS-CENTER-BE` + `F-MARKETING-RIGHTS-BE` 提供积分中心、券、权益、营销活动全套接口；本任务补齐对应小程序端 7 个核心页面。

**注意**：本任务与两张后端卡**并行派发**，子 Agent 需按任务卡中的接口契约开发，允许先写页面骨架 + API 封装，后端合并后联调。若契约与最终后端偏差，在报告 §C 契约偏差说明里如实列出。

## 必读

1. `subagent/tasks/C9+F-POINTS-CENTER-BE-商城售后与积分中心.md` Part B 段（积分中心接口契约）
2. `subagent/tasks/F-MARKETING-RIGHTS-BE-优惠券与权益后端.md`（券/权益/营销接口契约）
3. `小程序原型图/界面位置说明-V3.0.md` § 12~14（积分/营销/权益模块的区域描述 + 状态变量）
4. 原型 PNG：`points-center.png` ⭐ / `points-detail.png` / `points-mall.png`（C8 已做）/ `coupon-center.html` / `coupon-select.html` / `campaign-topic.html` / `rights-center.html` / `rights-detail.html` / `writeoff-apply.html`
5. 既有样板：`pages-sub/channel/rebate.vue`（Hero + tab + list + 悬浮按钮）/ `pages-sub/mall/list.vue`（grid + tab）/ `pages-sub/my/order-detail.vue`（时间轴）

## 后端接口清单（并行卡契约）

### 积分中心（F-POINTS-CENTER-BE）
- GET `/jst/wx/points/center/summary` → `{availablePoints,frozenPoints,totalEarn,totalSpend,growthValue,currentLevel,nextLevel,pointsToNextLevel}`
- GET `/jst/wx/points/center/levels?role=student`
- GET `/jst/wx/points/ledger?changeType=&pageNum=`
- GET `/jst/wx/points/growth/ledger?pageNum=`
- GET `/jst/wx/points/tasks`（mock 5 条）

### 商城售后（C9）
- POST `/jst/wx/mall/aftersale/apply` `{exchangeId, reason, refundType}`
- GET `/jst/wx/mall/aftersale/my?status=&pageNum=`
- GET `/jst/wx/mall/aftersale/{refundId}`

### 优惠券（F-MARKETING-RIGHTS-BE）
- GET `/jst/wx/coupon/template/claimable`
- POST `/jst/wx/coupon/claim?templateId=`
- GET `/jst/wx/coupon/my?status=&pageNum=`
- POST `/jst/wx/coupon/select` `{orderAmount, contestId?, goodsId?, candidateCouponIds?}`

### 权益
- GET `/jst/wx/rights/my?status=&pageNum=`
- GET `/jst/wx/rights/{userRightsId}`
- POST `/jst/wx/rights/{userRightsId}/apply-writeoff` `{writeoffAmount?, remark?}`

### 营销活动
- GET `/jst/wx/campaign/list?pageNum=`
- GET `/jst/wx/campaign/{id}`

## 交付物

### 1. API 封装（4 个新文件）
- `jst-uniapp/api/points.js`（center/levels/ledger/growth/tasks）
- `jst-uniapp/api/coupon.js`（claimable/claim/my/select）
- `jst-uniapp/api/rights.js`（my/detail/apply-writeoff）
- `jst-uniapp/api/marketing.js`（campaign list/detail）
- `jst-uniapp/api/mall.js` **扩展** 追加 aftersale 3 个接口

### 2. 路由注册
- `pages.json` 分包新增 `pages-sub/points/`、`pages-sub/coupon/`、`pages-sub/rights/`、`pages-sub/marketing/`

### 3. 页面

#### 积分模块（`pages-sub/points/`）

- **center.vue** ⭐（对照 `points-center.png`）
  - **等级 Hero**：紫金渐变 `#4A0072 → #7B1FA2 → #F5A623`，徽章 + 等级名 + 成长值进度条 + 距下一级差值
  - **积分统计 3 卡**：可用积分（金色大号）/ 冻结积分 / 累计获取
  - **快捷入口 grid 4 格**：积分商城 / 积分明细 / 兑换订单 / 优惠券
  - **等级权益列表**：按后端 levels 渲染，当前等级高亮，未达锁定态灰显 + "还差 xxx 成长值"
  - **赚积分任务**：后端 tasks mock，每条带图标+文案+"去完成"

- **detail.vue**（对照 `points-detail.png`）
  - 两 tab：积分流水 / 成长值流水
  - 列表卡：来源行为 / 时间 / +N 绿色 / -N 红色 / balance_after

#### 优惠券模块（`pages-sub/coupon/`）

- **center.vue**（对照 `coupon-center.html`）
  - tab：未使用（含数量角标）/ 已使用 / 已过期
  - 券卡 3 色：满减橙红 / 折扣紫 / 直减金
  - 信息：面额 / 门槛 / 名称 / 有效期 / 适用范围 / 去使用按钮
  - 顶部"去领券"跳 claimable.vue

- **claimable.vue** — 领券中心
  - 列表所有可领模板，卡片带"立即领取"按钮
  - 领取成功 toast + 刷新列表

- **select.vue**（对照 `coupon-select.html`）
  - 入参 query：`orderAmount` + `contestId?` + `goodsId?`
  - onLoad 调 `/coupon/select` 自动拿到推荐券
  - 列表可切换券，切换时重新计算 netPayAmount
  - 底部返回按钮 → 通过 uni 事件或 storage 把 selectedCouponId 回传给调用页
  - **注意**：本页当前没有真实调用方（enroll.vue 还没接入），先把页面做完，enroll.vue 接入留 P-POLISH

#### 权益模块（`pages-sub/rights/`）

- **center.vue**（对照 `rights-center.html`）
  - 顶部 3 KPI：可用权益数 / 申请中 / 已使用
  - 按状态 tab：可用 / 申请中 / 已使用 / 已过期
  - 卡片：权益名 + 剩余额度 / 次数 + 有效期 + 状态 badge + "立即使用"按钮

- **detail.vue**（对照 `rights-detail.html`）
  - 权益基本信息
  - 核销规则说明
  - 核销历史时间轴
  - 底部 CTA："申请核销" → `writeoff-apply.vue`

- **writeoff-apply.vue**（对照 `writeoff-apply.html`）
  - 根据 rights_type 显示不同模板（课程学习 / 线下活动 / 客服特权）
  - 实时金额/次数预估
  - 备注文本框
  - 提交 → `/rights/{id}/apply-writeoff`
  - 成功返回 center 并 toast

#### 营销活动（`pages-sub/marketing/`）

- **campaign.vue**（对照 `campaign-topic.html`）
  - 活动 hero：banner + 倒计时（结束时间）
  - 可领券列表（卡片 + 立即领取，复用 coupon/claimable 样式）
  - 参与赛事列表占位（本期不阻塞，可 TODO）

### 4. 入口改造

- `pages/my/index.vue` "我的服务" grid 追加：
  - **积分中心** → `pages-sub/points/center`
  - **我的优惠券** → `pages-sub/coupon/center`
  - **我的权益** → `pages-sub/rights/center`
- `pages/index/index.vue` 首页如果有"活动横幅"位，链接到 `marketing/campaign`（若无则跳过）

### 5. 视觉规范
- 积分紫金主色：`#4A0072 / #7B1FA2 / #F5A623`
- 积分标签背景：`#F3E8FF`
- 券卡色：满减 `#F4511E` / 折扣 `#7C3AED` / 直减 `#F5A623`
- 权益主色：`#1B5E20`（核销绿）

## DoD
- [ ] 4 个 api 文件 + mall.js 扩展
- [ ] 7 个核心页面 + 2 个附属页（claimable / campaign）
- [ ] pages.json 注册 9 个页面 + 4 个新分包
- [ ] my/index 3 个入口 tile
- [ ] 对照原型 PNG 视觉对齐
- [ ] 任务报告 `WX-C4-积分权益营销前端-回答.md`
- [ ] commit: `feat(wx): WX-C4 积分中心 + 券 + 权益 + 营销前端`

## 不许做
- ❌ 不许改后端
- ❌ 不许在任何页面写 fake 数据（tasks mock 由后端返，不是前端）
- ❌ 不许跳过 PNG 对齐
- ❌ 不许做真实二维码渲染（权益核销本期不涉及 QR）
- ❌ 不许动 pages-sub/mall 已有页面

## 依赖：两张后端卡（并行，派发时未合并也可开工，允许契约偏差）
## 优先级：高

---
派发时间：2026-04-09
