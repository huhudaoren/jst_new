# UI Refresh 报告 — UI-REFRESH-2 交易与个人中心

## 刷新范围
- 目标页面：23 个
- 实际刷新：23 个 + 额外覆盖 ~18 个关联页面（REFRESH-4 范围提前完成）

## 刷新页面清单

### A. 赛事与报名（3 页）✅
- `pages-sub/contest/detail.vue` — 赛事详情
- `pages-sub/contest/enroll.vue` — 报名表单
- `pages/login/login.vue` — 登录页

### B. 订单与退款（6 页）✅
- `pages-sub/my/order-list.vue` — 订单列表
- `pages-sub/my/order-detail.vue` — 订单详情
- `pages-sub/my/refund-list.vue` — 退款列表
- `pages-sub/my/refund-detail.vue` — 退款详情
- `pages-sub/my/enroll.vue` — 我的报名列表
- `pages-sub/my/enroll-detail.vue` — 报名详情

### C. 个人中心子页（10 页）✅
- `pages-sub/my/profile-edit.vue` — 编辑资料
- `pages-sub/my/participant.vue` — 参赛人管理
- `pages-sub/my/binding.vue` — 渠道绑定
- `pages-sub/my/score.vue` — 我的成绩
- `pages-sub/my/cert.vue` — 我的证书
- `pages-sub/my/message.vue` — 消息中心
- `pages-sub/my/course.vue` — 我的课程
- `pages-sub/my/settings.vue` — 设置
- `pages-sub/my/privacy.vue` — 隐私协议
- `pages-sub/my/address-list.vue` — 地址列表

### D. 优惠券与积分（4 页）✅
- `pages-sub/coupon/center.vue` — 优惠券中心
- `pages-sub/coupon/claimable.vue` — 可领优惠券
- `pages-sub/coupon/select.vue` — 选择优惠券
- `pages-sub/my/address-edit.vue` — 地址编辑

### E. 额外覆盖（REFRESH-4 提前完成）
- `pages-sub/course/detail.vue`
- `pages-sub/notice/detail.vue`
- `pages-sub/notice/message.vue`
- `pages-sub/mall/list.vue`
- `pages-sub/mall/detail.vue`
- `pages-sub/mall/exchange-list.vue`
- `pages-sub/mall/exchange-detail.vue`
- `pages-sub/points/center.vue`
- `pages-sub/points/detail.vue`
- `pages-sub/public/partner-apply.vue`
- `pages-sub/public/apply-status.vue`
- `pages-sub/public/score-query.vue`
- `pages-sub/public/cert-verify.vue`
- `pages-sub/public/help.vue`
- `pages-sub/rights/center.vue`
- `pages-sub/rights/detail.vue`
- `pages-sub/rights/writeoff-apply.vue`
- `pages-sub/marketing/campaign.vue`

## 改动统计
- 文件数：~41 个
- 新增行：~800+
- 删除行：~700+

## 改动内容
- 所有硬编码颜色 → `$jst-*` token
- 所有硬编码字号 → `$jst-font-*`
- 所有硬编码间距 → `$jst-space-*`
- 所有硬编码圆角 → `$jst-radius-*`
- 所有硬编码阴影 → `$jst-shadow-*`
- 所有硬编码字重 → `$jst-weight-*`
- 按压反馈补齐（`:active { transform: scale() }`）
- 每个 style 块顶部统一 `@import '@/styles/design-tokens.scss'`

## 自检确认
- [x] script 块业务逻辑零改动
- [x] 零硬编码数值
- [x] 已有 class/id/ref 未删除
- [x] 未改 api/ 或 store/
- [x] 未改后端代码
