# 任务卡 E1-CH-4 - 渠道订单 + 订单详情 V4.0 改造

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
`pages-sub/channel/orders.vue` 当前是骨架分页列表。原型 V4.0 要求 6 状态 Tab（含**已退款**新增）+ 多维筛选 + 订单详情"费用明细"拆分 + 归属渠道方展示 + 时间轴扩展。

## PRD 依据
- §7.1 订单状态
- §7.3 渠道订单视角
- §8.4 订单数据模型
- 原型：`channel-orders.html` + `order-detail.html`

## 交付物

### 1. 重构 `pages-sub/channel/orders.vue`

#### 顶部
- 搜索栏（按学生姓名 / 订单号）
- 筛选按钮 → 弹窗选：按赛事 / 按时间范围
- 状态 Tab（6 个）：全部 / 待支付 / 已支付 / 对公转账 / 已退款 / 已关闭

#### 订单卡片
每项显示：
- 订单号 + 创建时间
- 学生姓名（脱敏手机号）
- 赛事名称
- 金额：标价 → 实付（若有折扣显示删除线）
- 状态徽章（不同颜色）
- 退款状态标签（若有）
- 点击卡片跳 `channel/order-detail/{orderId}`

#### 分页
使用 `uni-list` + 触底加载，每页 20 条

### 2. 重构或新建 `pages-sub/channel/order-detail.vue`

按原型 V4.0 要求完整重做：

#### A. 基础信息区
- 订单号（复制按钮）
- 创建时间 / 支付时间 / 完成时间
- 学生姓名（脱敏）
- 赛事名 + 点击跳赛事详情
- 订单状态 + 退款状态

#### B. 费用明细区（V4.0 新增，核心）
```
报名费标价        ¥200.00
  优惠券抵扣       -¥20.00 (满 100 减 20)
  积分抵扣         -¥10.00 (100 积分)
─────────────
  用户净实付       ¥170.00

平台服务费        ¥17.00  (净实付 × 10%)
─────────────
  渠道返点基数     ¥153.00
  返点比例         × 20%
  返点金额         ¥30.60
```

每行清晰对齐，使用 SCSS 单独样式类 `.order-fee-breakdown`

#### C. 归属渠道方（V4.0 新增）
- 小卡片：`👨‍🏫 王建国（老师渠道方）`
- 底部 caption：`返点锁定依据：订单创建时的有效绑定关系`

#### D. 时间轴（扩展版）
- 订单创建
- 积分冻结
- 优惠券锁定
- 支付成功
- 积分消耗
- 优惠券使用
- 审核通过
- 返点计提（售后期结束后）
- （若退款）退款发起 → 积分回退 → 优惠券回退 → 返点回退

#### E. 操作区
- 复制订单号
- 查看赛事详情
- （若有发票）查看/下载发票

### 3. API 补充
**修改**：`jst-uniapp/api/channel.js`

```js
export function getChannelOrders(params) { return request({ url: '/jst/wx/channel/orders', method: 'GET', params }) }
export function getOrderDetailForChannel(orderId) { return request({ url: `/jst/wx/channel/orders/${orderId}`, method: 'GET' }) }
```

**⚠️ 注意**：如果后端 `GET /channel/orders/{id}` 返回的 VO 缺费用明细字段，**不要自己补后端**，而是写字段缺口反馈文档 `subagent/ui-feedback/2026-04-10-channel-order-detail-字段需求.md`，列出需补的字段：
- `priceOriginal`（标价）
- `couponDiscount`
- `pointsDiscount`
- `userNetPay`
- `platformFee`
- `rebateBase`
- `rebateRate`
- `rebateAmount`
- `channelOwner: { name, channelType }`
- `timeline: [{ step, time, description }]`

反馈文档走 §8 流程，主 Agent 会派后端卡补。

### 4. 页面文件
- 重构：`pages-sub/channel/orders.vue`
- 重构/新建：`pages-sub/channel/order-detail.vue`
- 修改：`api/channel.js` / `pages.json`

## DoD
- [ ] orders.vue 6 状态 Tab + 筛选 + 分页
- [ ] order-detail 5 区块完整
- [ ] 费用明细清晰拆分
- [ ] 时间轴完整
- [ ] 字段缺口反馈文档（如有）已写
- [ ] 任务报告 `E1-CH-4-回答.md`

## 不许做
- ❌ 不许改后端 VO（字段不足走反馈文档）
- ❌ 不许编造假字段填充视觉
- ❌ 不许动学生视角的 `pages-sub/my/order-*.vue`

## 依赖：E0-1 + E1-CH-1 + E1-CH-2
## 优先级：P1

---
派发时间：2026-04-10
