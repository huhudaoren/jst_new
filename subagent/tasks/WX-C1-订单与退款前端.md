# 任务卡 WX-C1 - 小程序端：订单 + 支付 + 退款页面集

## 阶段 / 端
阶段 C 前端 / **jst-uniapp**

## 业务背景

阶段 C 后端 C2（订单混合支付）和 C4（退款全流程）已完成并测试通过，但小程序前端没有对应页面：
- 学生做完 F9 报名后看不到订单入口
- 看不到"我的订单"列表
- 看不到订单详情（支付/取消/退款按钮）
- 没有退款申请页和"我的退款"列表

本任务**只做前端**，后端接口已经全部 ready（见契约文档）。

## 必读上下文

1. **PRD**：`需求文档/竞赛通-产品需求文档-统一版-V4.1.md` § 5.1/5.2/7.1/8.4（订单/支付/退款）
2. **后端契约**：`架构设计/27-用户端API契约.md`
3. **C2 任务卡**：`subagent/tasks/C2-订单混合支付.md`（了解金额口径、orderType、payMethod 枚举）
4. **C4 任务卡**：`subagent/tasks/C4-退款全流程.md`（了解 refund_only / refund_type、售后期逻辑）
5. **测试参考**：`test/wx-tests.http` § C2 / C4-W 段，所有请求 URL 和响应字段都在里面
6. **前端既有代码规范**：
   - `jst-uniapp/api/request.js`（统一 request）
   - `jst-uniapp/api/enroll.js`（已有接口模块的写法）
   - `jst-uniapp/pages-sub/my/enroll.vue`（P6 已有的列表页写法）
   - `jst-uniapp/pages-sub/my/enroll-detail.vue`（已有详情页写法）
7. **原型 PNG（必须像素级对齐）**：
   - `小程序原型图/order-detail.png` + `order-detail.html`
   - `小程序原型图/my-order.png` + `my-order.html`
   - `小程序原型图/coupon-select.png`（下单选券）
   - 退款相关原型：检查 `小程序原型图/` 下 `refund-*` 或 `my-refund-*`；如无则参照 `my-enroll` 的布局自制
8. **pages.json 路由**：新增路由需更新 `jst-uniapp/pages.json`

## 后端接口清单（全部已 ready，URL/入参/响应见 wx-tests.http）

### 订单（C2）
- `POST /jst/wx/order/create` — 创建订单（入参 enrollId、couponId、pointsToUse、payMethod）
- `GET /jst/wx/order/my?pageNum=&pageSize=&status=` — 我的订单列表
- `GET /jst/wx/order/{orderId}` — 订单详情
- `POST /jst/wx/order/{orderId}/cancel` — 取消待支付订单
- `POST /jst/wx/pay/mock-success?orderId=` — mock 支付成功（本期前端按钮直接调它，后期替换为微信 JSAPI）

### 退款（C4）
- `POST /jst/wx/order/{orderId}/refund` — 发起退款申请（入参 refundType、reason）
- `GET /jst/wx/refund/{refundId}` — 退款详情
- `GET /jst/wx/refund/my?pageNum=&pageSize=` — 我的退款列表
- `POST /jst/wx/refund/{refundId}/cancel` — 撤销 pending 退款

## 交付物清单

### 1. API 封装（新建）
- `jst-uniapp/api/order.js` — 封装所有 `/jst/wx/order/*` 和 `/jst/wx/pay/*`
- `jst-uniapp/api/refund.js` — 封装所有 `/jst/wx/refund/*` 和 `/jst/wx/order/{id}/refund`

### 2. 订单页面（`jst-uniapp/pages-sub/my/` 下）
- `order-list.vue` — 我的订单列表
  - tab 过滤（全部/待支付/已支付/进行中/售后中/已完成/已取消/退款中）
  - 卡片显示：赛事名、订单号、金额、状态、创建时间、操作按钮
  - 下拉刷新 + 上拉加载（参考 enroll.vue 的 mescroll 或原生）
  - 空态插画（若无原型，用 `static/empty.png` 或 tn-icon）
- `order-detail.vue` — 订单详情
  - 订单信息区：订单号、创建时间、状态标签
  - 金额明细区：赛事费 / 报名费 / 优惠券减免 / 积分抵扣 / 实付金额
  - 关联报名区：赛事名 + 参赛人 + 场次（点击跳 enroll-detail）
  - 按钮区根据 order_status + refund_status 动态：
    - `pending_pay`: 【取消订单】【去支付】
    - `paid` 且在售后期: 【申请退款】
    - `in_service`: 只看不能操作
    - 其他：无按钮
  - 申请退款按钮打开弹窗：选 refundType（本期固定 `refund_only`）+ 填 reason → 调 /refund 接口

### 3. 退款页面（`jst-uniapp/pages-sub/my/` 下）
- `refund-list.vue` — 我的退款列表
  - 卡片显示：退款单号、关联订单号、金额、状态（pending/approved/rejected/refunding/completed/closed/cancelled）、申请时间
- `refund-detail.vue` — 退款详情
  - 退款单信息、原订单快照、审核记录、退款金额拆分（现金/积分/优惠券）、状态流转时间轴
  - pending 状态显示【撤销申请】按钮

### 4. 支付交互
- `order-detail.vue` 内嵌支付调用：本期点【去支付】直接调 `/jst/wx/pay/mock-success`，弹 toast "支付成功" 后刷新
- 保留 `TODO: 接入真实微信 JSAPI`，用注释标记替换位置

### 5. 路由与入口
- 更新 `jst-uniapp/pages.json`：
  - 分包 `my` 加上 4 个新页面
- 更新 `jst-uniapp/pages-sub/my/index.vue`（个人中心首页）：
  - 在"我的报名"入口旁加"我的订单"入口 icon + 跳转
  - 加"我的退款"入口（折叠在"更多"里也可）
- `jst-uniapp/pages-sub/my/enroll-detail.vue`：报名审核通过后显示【去支付】按钮，跳 `order-detail?enrollId=xxx`（或先调 /create 拿 orderId 再跳）
  - 决策：在 enroll-detail 新增一个"待支付"按钮，点了先调 `/order/create`，成功后跳 `order-detail?orderId={返回的id}`

## 视觉对齐要求（硬性）

1. **必须先看 PNG 截图**（`小程序原型图/order-detail.png` 等）再写代码
2. 颜色、圆角、padding、字号严格按原型
3. 状态标签颜色规约（参考 enroll.vue 已有色板）：
   - 待支付：橙色 `#FF8A00`
   - 已支付/进行中：蓝色 `#2E7DFF`
   - 已完成：绿色 `#52C41A`
   - 已取消/已关闭：灰色 `#999`
   - 退款中：紫色 `#8B5CF6`
4. **按钮布局**：主操作按钮 primary（蓝底白字），次要操作 outline（白底蓝字边框）
5. 金额显示：`¥` + 两位小数，主金额字号加大加粗

## 测试方式

前端任务**不跑后端 .http**，由主 Agent 测。子 Agent 只需：
1. 代码可编译（`npm run build:mp-weixin` 或 `cli.js build` 视项目配置）
2. HBuilderX / 微信开发者工具能打开首页不报红
3. 关键页面能 mock 数据渲染出来（可以临时把 api 调用 mock 掉走 devtools 截图对齐原型）
4. 提交前移除 mock，留真实 api 调用

## DoD

- [ ] 6 个文件新建完成（api 2 + pages 4）
- [ ] pages.json 已注册
- [ ] 个人中心首页入口已加
- [ ] enroll-detail 的【去支付】跳转已接
- [ ] 4 个页面对照原型 PNG 无明显视觉偏差
- [ ] 无 console.error / uniapp 编译警告
- [ ] 任务报告写到 `subagent/tasks/任务报告/WX-C1-订单与退款前端-回答.md`（含 5-8 张自测截图 base64 或路径引用，覆盖 4 个页面 + 2 个弹窗）
- [ ] commit message: `feat(wx): WX-C1 订单/支付/退款页面集`

## 不许做

- ❌ 不许改后端 Java 代码
- ❌ 不许改 request.js 的拦截器逻辑
- ❌ 不许引入新 npm 依赖（仅用项目已有的 uView / tn-icon / mescroll）
- ❌ 不许在页面里写测试假数据（加载慢宁可转圈圈）
- ❌ 不许自作主张加后端没有的字段（发现字段不够反馈主 Agent）
- ❌ 不许跳过 PNG 对齐

## 优先级
高（阶段 C 闭环）

## 依赖
前置：C2 + C4 后端已合并 ✅（当前状态）

---
派发时间：2026-04-09
