# 任务卡 WX-C3 - 小程序端：预约 + 扫码核销 + 积分商城页面集

## 阶段 / 端
阶段 C 前端 / **jst-uniapp**

## 业务背景
对接 C6 / C7 / C8 后端：个人预约、团队预约、扫码核销、积分商城。

## 必读
1. `subagent/tasks/C6-团队预约与扫码核销.md`
2. `subagent/tasks/C7-个人预约.md`
3. `subagent/tasks/C8-积分商城兑换.md`
4. `test/wx-tests.http` § C6 / C7 / C8 段
5. 原型 PNG（严格对齐）：
   - `appointment-select.*`（个人预约选日期场次）
   - `appointment-detail.*`（预约详情含 QR 子项）
   - `my-appointment.*`（我的预约列表）
   - `channel-appointment.*`（团队预约创建）
   - `checkin-scan.*`（扫码核销终端）
   - `points-mall.*`（商城首页）
   - `points-goods-detail.*`（商品详情）
   - `points-order.*`（兑换订单列表）
   - `coupon-center.*`（我的优惠券，展示兑换结果）

## 交付物

### 1. API 封装（`jst-uniapp/api/`）
- `appointment.js` — 个人/团队预约创建、取消、查询；扫码核销
- `mall.js` — 商品列表、详情、兑换、订单列表、取消

### 2. 学生端预约页面（`pages-sub/appointment/`）
- `select.vue` — 赛事详情→预约按钮跳入；选日期+场次；显示剩余名额；"确认预约"
- `my.vue` — 我的预约列表（个人 + 团队归属标记）
- `detail.vue` — 预约详情
  - 主 QR（预约本身标识）
  - N 个子项 QR 卡片（到场/礼品/仪式/自定义），每张卡片独立状态徽章（未核销/已核销/已作废）
  - 支持点击子项放大 QR（便于现场出示）
  - booked 且无 used 子项时底部【取消预约】按钮

### 3. 渠道方团队预约（`pages-sub/channel/appointment-team.vue`）
- 选赛事、场次、日期
- 添加成员：支持已有学生选择 + 手动录入临时档案（姓名/手机/身份证）
- 批量校验 + 提交 → 调 `/team-appointment/apply`
- 成功后跳渠道方首页并 toast

### 4. 扫码核销终端（`pages-sub/writeoff/scan.vue`）
- 使用 uni `uni.scanCode` API
- 扫描成功后调 `/writeoff/scan` 接口
- 显示核销结果卡片：成员姓名 / 子项类型 / 团队进度（如团队）/ 剩余未核销
- "继续扫码"按钮循环操作
- 近期核销记录列表（今日）
- 权限：仅赛事方账号可见此入口，放在 `pages-sub/partner/index.vue`（或复用 channel 首页的角色判断）

### 5. 积分商城页面（`pages-sub/mall/`）
- `index.vue` — 商城首页
  - 顶部 banner + 我的积分余额
  - 分类 tab：优惠券 / 权益 / 实物
  - 商品卡片网格（图 + 名称 + 积分价 + 现金价）
- `goods-detail.vue` — 商品详情
  - 图文介绍、库存、角色限制提示
  - 底部"立即兑换"按钮 → 弹数量 + 确认
- `exchange-apply.vue` — 兑换下单（也可直接在详情弹层完成）
- `my-orders.vue` — 我的兑换订单列表
  - tab：待支付 / 待发货 / 已发货 / 已完成 / 已取消
  - 卡片支持取消（pending_pay）/ 确认收货（shipped）
- `order-detail.vue` — 兑换订单详情
  - 商品快照、数量、积分 + 现金明细、收货地址（实物）、物流信息、状态时间轴

### 6. 入口接入
- 赛事详情页（`pages-sub/contest/detail.vue`）：若 `contest.support_appointment=1`，加"预约"按钮跳 `appointment/select`
- 个人中心：加"我的预约"、"积分商城"、"我的兑换"入口
- 渠道方首页：加"团队预约"入口
- 赛事方首页（若存在）：加"扫码核销"入口

### 7. 路由
- `pages.json` 新增分包 `pages-sub/appointment/`、`pages-sub/mall/`、`pages-sub/writeoff/`

## 视觉对齐
- QR 卡片：白底 + 圆角 + 阴影，每张独立
- 状态徽章色板沿用 WX-C1 定义
- 商城商品卡片：2 列网格，圆角 16rpx
- 扫码页：全屏相机 + 底部半透明结果抽屉

## DoD
- [ ] 2 个 api + 约 12 个页面（核心 10 + 入口修改 2）
- [ ] 个人预约 → 核销 联调通过（自己扫自己的码）
- [ ] 团队预约成员创建成功能看到成员列表
- [ ] 兑换订单 happy path 能跑完
- [ ] 任务报告含至少 10 张截图
- [ ] commit: `feat(wx): WX-C3 预约+核销+商城页面集`

## 不许做
- ❌ 不许引入新的扫码 SDK，仅用 uni 原生 `scanCode`
- ❌ 不许在兑换页伪造积分余额
- ❌ 不许跳过角色权限判断（学生看不到扫码入口）

## 依赖：C6 / C7 / C8 后端全部合并
## 优先级：中

---
派发时间：2026-04-09
