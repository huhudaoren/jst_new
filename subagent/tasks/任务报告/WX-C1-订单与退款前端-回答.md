# WX-C1 订单与退款前端 - 交付报告

## 1. 任务概述

本次完成了小程序学生侧订单 / 支付 / 退款页面集的前端实现，覆盖：

- 我的订单列表
- 订单详情
- 我的退款列表
- 退款详情
- 报名详情页“去支付”链路接入
- 个人中心新增“我的订单 / 我的退款”入口
- `pages.json` 路由注册

实现目录：`D:\coding\jst_v1\jst-uniapp`

---

## 2. 本次新增与修改

### 2.1 新增文件

- `D:\coding\jst_v1\jst-uniapp\api\order.js`
- `D:\coding\jst_v1\jst-uniapp\api\refund.js`
- `D:\coding\jst_v1\jst-uniapp\pages-sub\my\order-list.vue`
- `D:\coding\jst_v1\jst-uniapp\pages-sub\my\order-detail.vue`
- `D:\coding\jst_v1\jst-uniapp\pages-sub\my\refund-list.vue`
- `D:\coding\jst_v1\jst-uniapp\pages-sub\my\refund-detail.vue`

### 2.2 修改文件

- `D:\coding\jst_v1\jst-uniapp\pages-sub\my\enroll-detail.vue`
- `D:\coding\jst_v1\jst-uniapp\pages\my\index.vue`
- `D:\coding\jst_v1\jst-uniapp\pages.json`

---

## 3. 接口接入情况

### 3.1 订单相关

- `POST /jst/wx/order/create`
- `GET /jst/wx/order/my`
- `GET /jst/wx/order/{orderId}`
- `POST /jst/wx/order/{orderId}/cancel`
- `POST /jst/wx/pay/mock-success?orderId=`

### 3.2 退款相关

- `POST /jst/wx/order/{orderId}/refund`
- `GET /jst/wx/refund/my`
- `GET /jst/wx/refund/{refundId}`
- `POST /jst/wx/refund/{refundId}/cancel`

### 3.3 本期支付链路

- 报名详情页审核通过后，点击“去支付”会先调 `createOrder`
- 创建成功后跳转 `order-detail?orderId=xxx&enrollId=xxx`
- 订单详情页点击“去支付”会先调 `mock-success`
- 代码中已保留 `TODO: 接入真实微信 JSAPI 支付`

---

## 4. 页面实现说明

### 4.1 我的订单 `order-list.vue`

- 顶部横向状态 tab
- 列表卡片展示赛事名、订单号、参赛人、创建时间、支付方式、实付金额
- 支持下拉刷新、上拉加载
- `pending_pay` 支持取消订单 / 去支付
- 已支付订单支持进入详情发起退款
- 退款中的订单支持跳转退款列表

### 4.2 订单详情 `order-detail.vue`

- 顶部状态卡 + 主金额展示
- 金额明细区：标价、优惠券减免、积分抵扣、实付金额
- 订单信息区：赛事、参赛人、支付方式、支付单号、第三方流水、售后状态
- 时间轴区：创建、支付、售后截止
- 底部动态按钮：
  - `pending_pay`：取消订单 / 去支付
  - `paid` 且可售后：申请退款
- 申请退款使用页面内弹窗，固定 `refund_only`

### 4.3 我的退款 `refund-list.vue`

- 顶部横向状态 tab
- 列表卡片展示退款单号、关联订单号、申请金额、申请时间、状态
- 有审核备注时展示提示区
- `pending` 支持撤销申请

### 4.4 退款详情 `refund-detail.vue`

- 顶部状态卡 + 申请金额
- 退款信息区：退款类型、申请来源、原因
- 原订单快照区
- 金额拆分区：现金 / 积分 / 优惠券返还
- 审核记录区
- 状态时间轴
- `pending` 支持撤销申请

---

## 5. 与任务卡的差异说明

### 5.1 已完成

- [x] API 2 个文件新增
- [x] 页面 4 个文件新增
- [x] `pages.json` 已注册
- [x] 个人中心已加“我的订单 / 我的退款”入口
- [x] `enroll-detail` 的“去支付”链路已接

### 5.2 未完全闭合项

- [ ] 未在 HBuilderX / 微信开发者工具中实际运行
- [ ] 未生成 5-8 张真实自测截图

原因：

- 当前仓库没有可直接执行的 `build:mp-weixin` 脚本
- 本轮运行环境不包含 HBuilderX / 微信开发者工具可视化调试上下文

### 5.3 任务卡外的真实约束

- 订单详情接口当前未返回 `enrollId`
  - 所以“查看报名详情”只能在“从报名详情页创建订单后跳转进来”的场景下成立
  - 订单列表直接进入详情时，无法凭现有契约反查报名详情
- 本期未实现优惠券选择页 / 积分抵扣选择页
  - 任务交付物中未要求新增 coupon 页
  - 本次 `createOrder` 走默认参数：`pointsToUse=0`、无 `couponId`、`payMethod='wechat'`

---

## 6. 自查结果

已完成的静态自查：

- `pages.json` 已通过 JSON 解析检查
- 本次目标文件已通过 `git diff --check`
- 新增页面内未保留 mock 数据
- 支付按钮已接 `mock-success`
- 退款弹窗已使用真实接口提交

未完成的联调验证：

- 小程序编译打开验证
- 真机或开发者工具视觉对齐截图
- 后端返回边界值联调

---

## 7. 风险与后续建议

建议主 Agent 或联调同学下一步重点验证：

1. 审核通过报名记录点击“去支付”后，重复创建订单时的提示是否符合预期
2. `mock-success` 在当前本地后端配置下是否稳定可用
3. 退款列表按状态筛选是否需要后端补充 `status` 查询参数，以避免纯前端分页过滤带来的边界误差
4. 是否需要在后端 `OrderDetailVO` 中补充 `enrollId`
5. 后续是否追加“优惠券 / 积分选择”页面，补齐 C2 完整支付方案

---

## 8. 本次交付结论

本轮已经把学生侧订单、支付、退款页面链路从“无入口 / 无页面”补到了“有入口、有列表、有详情、有支付 mock、有退款申请/撤销”的可联调状态。

如果后续需要继续推进，我建议优先做两件事：

1. 在微信开发者工具里跑一轮视觉与交互联调
2. 由后端补充 `enrollId` 或订单查询能力，彻底打通“订单详情 -> 报名详情”
