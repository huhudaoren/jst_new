# WEB-ADMIN-6 — 订单明细与预约管理

> 优先级：P1 | 预估：M | Agent：Web Admin Agent

---

## 一、目标

增强 6 个订单/预约相关代码生成页面，补齐搜索、响应式、详情抽屉。

## 二、增强页面清单（6 页）

路径前缀：`ruoyi-ui/src/views/jst/order/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 1 | `jst_order_item/index.vue` | 订单明细行 | +订单号/商品名搜索 +金额格式化（分→元） +关联订单跳转 |
| 2 | `jst_payment_record/index.vue` | 支付记录 | +支付单号/订单号搜索 +支付方式/状态筛选 +金额高亮 +时间范围 |
| 3 | `jst_appointment_record/index.vue` | 个人预约记录 | +预约人/赛事搜索 +预约状态标签 +核销状态 +详情抽屉 |
| 4 | `jst_appointment_writeoff_item/index.vue` | 核销明细 | +核销单号/预约单号搜索 +核销人/时间 +关联预约跳转 |
| 5 | `jst_team_appointment/index.vue` | 团队预约 | +团队名/赛事搜索 +成员数展示 +预约状态 +详情抽屉（含成员列表） |
| 6 | `jst_team_appointment_member/index.vue` | 团队成员 | +团队/成员名搜索 +签到状态 +详情 |

## 三、清理重复页（2 页）

| 文件 | 被替代者 |
|---|---|
| `jst_order_main/index.vue` | `admin-order/index.vue` |
| `jst_refund_record/index.vue` | `admin-refund/index.vue` |

在 `<template>` 顶部加 `<!-- DEPRECATED -->` 注释。

## 四、增强规范

同 WEB-ADMIN-5 §五，额外注意：

### 金额展示
- 数据库存分（Long），展示时 `(value / 100).toFixed(2)` 并加 ¥ 前缀
- 金额列右对齐、加粗
- 退款金额用红色 `color: #F56C6C`

### 关联跳转
- 订单明细行 → 点击订单号跳转到 `admin-order` 详情
- 核销明细 → 点击预约单号跳转到 `jst_appointment_record` 详情
- 使用 `<el-link>` 或 `<router-link>` 实现

### 团队预约详情
- 抽屉内展示：团队基本信息 + 成员表格（嵌套 `el-table`）
- 成员表格列：姓名、手机、签到状态、签到时间

## 五、参考
- 精品页面：`views/jst/order/admin-order/index.vue`（搜索+响应式+详情模式）
- 预约业务：后端 `jst-order` 模块的 `AppointmentRecord` / `TeamAppointment` 实体

## 六、DoD
- [ ] 6 个增强页面搜索/响应式/详情三项齐全
- [ ] 金额展示统一（分→元）
- [ ] 2 个重复页标记 deprecated
- [ ] `npm run build:prod` 无编译错误
- [ ] 报告交付
