# UI Refresh 报告 — UI-REFRESH-3 渠道与预约

## 刷新范围
- 目标页面：23 个
- 实际刷新：23 个

## 刷新页面清单

### A. 渠道认证（3 页）✅
- `pages-sub/channel/apply-entry.vue` — 认证入口
- `pages-sub/channel/apply-form.vue` — 认证申请表单
- `pages-sub/channel/apply-status.vue` — 认证状态

### B. 渠道工作台（5 页）✅
- `pages-sub/channel/home.vue` — 渠道首页/工作台
- `pages-sub/channel/data.vue` — 数据统计
- `pages-sub/channel/students.vue` — 学生管理
- `pages-sub/channel/student-score.vue` — 学生成绩
- `pages-sub/channel/student-cert.vue` — 学生证书

### C. 渠道订单与返点（6 页）✅
- `pages-sub/channel/orders.vue` — 渠道订单列表
- `pages-sub/channel/order-detail.vue` — 渠道订单详情
- `pages-sub/channel/rebate.vue` — 返点中心
- `pages-sub/channel/settlement.vue` — 结算页
- `pages-sub/channel/withdraw-list.vue` — 提现列表
- `pages-sub/channel/withdraw-detail.vue` — 提现详情

### D. 渠道业务操作（3 页）✅
- `pages-sub/channel/withdraw-apply.vue` — 提现申请
- `pages-sub/channel/batch-enroll.vue` — 批量报名
- `pages-sub/channel/participants.vue` — 临时档案管理

### E. 渠道预约（1 页）✅
- `pages-sub/channel/appointment.vue` — 渠道团队预约

### F. 预约与核销（5 页）✅
- `pages-sub/appointment/apply.vue` — 个人预约申请
- `pages-sub/appointment/detail.vue` — 预约详情
- `pages-sub/appointment/my-list.vue` — 我的预约列表
- `pages-sub/appointment/scan.vue` — 扫码核销
- `pages-sub/appointment/writeoff-record.vue` — 核销记录

## 改动统计
- 文件数：23 个
- 新增行：~450+
- 删除行：~400+

## 改动内容
- 所有硬编码颜色/字号/间距/圆角/阴影/字重 → `$jst-*` token
- 渠道工作台（home/data）金额数字突出（大字号+粗字重+品牌色）
- 提现/结算页金额展示优化
- 扫码核销页视觉引导
- 按压反馈补齐
- 每个 style 块顶部统一 `@import '@/styles/design-tokens.scss'`

## 自检确认
- [x] script 块业务逻辑零改动
- [x] 零硬编码数值
- [x] 已有 class/id/ref 未删除
- [x] 未改 api/ 或 store/
- [x] 未改后端代码
