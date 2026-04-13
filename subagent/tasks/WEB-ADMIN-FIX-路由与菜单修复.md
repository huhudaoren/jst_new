# WEB-ADMIN-FIX — 路由与菜单修复 + 管理端体验优化

> 优先级：P0 | 预估：M | Agent：Web Admin Agent

---

## 一、背景

当前管理后台存在严重的导航 404 问题：精品页（WEB-ADMIN-1~4 手工开发）没有在 `sys_menu` 中注册菜单，若依动态路由无法加载，导致看板和首页的所有跳转链接失效。

## 二、任务内容

### A. 菜单注册 SQL（核心，修复 404）

在 `架构设计/ddl/` 下新建 `99-migration-polished-menus.sql`，为所有精品页注册 `sys_menu` 条目。

**需要注册的精品页菜单**（按业务分组）：

```
一级菜单：赛事管理 (menu_id 自选 9800 段)
├── 赛事列表         component: jst/contest/index       path: contest
├── 赛事编辑         component: jst/contest/edit        path: contest-edit (hidden)
├── 赛事详情         component: jst/contest/detail      path: contest-detail (hidden)
├── 报名管理         component: jst/enroll/index        path: enroll
└── 赛事方入驻管理   component: jst/partner-apply/index  path: partner-apply

一级菜单：课程管理
├── 课程列表         component: jst/course/index         path: course
└── 课程编辑         component: jst/course/edit          path: course-edit (hidden)

一级菜单：订单管理
├── 订单管理         component: jst/order/admin-order/index   path: admin-order
├── 退款管理         component: jst/order/admin-refund/index  path: admin-refund
├── 订单明细行       component: jst/order/jst_order_item/index  path: order-item
├── 支付记录         component: jst/order/jst_payment_record/index  path: payment-record
├── 预约记录         component: jst/order/jst_appointment_record/index  path: appointment-record
├── 团队预约         component: jst/order/jst_team_appointment/index  path: team-appointment
└── 核销明细         component: jst/order/jst_appointment_writeoff_item/index  path: writeoff-item

一级菜单：用户管理
├── 用户列表         component: jst/user/index            path: user
├── 参赛档案管理     component: jst/participant/index     path: participant
└── 表单模板管理     component: jst/form-template/index   path: form-template

一级菜单：营销管理
├── 优惠券模板       component: jst/coupon/template       path: coupon-template
├── 已发优惠券       component: jst/coupon/issued         path: coupon-issued
├── 权益模板         component: jst/rights/template       path: rights-template
├── 已发权益         component: jst/rights/issued         path: rights-issued
├── 优惠券批次       component: jst/marketing/jst_coupon_issue_batch/index  path: coupon-batch
├── 用户优惠券       component: jst/marketing/jst_user_coupon/index  path: user-coupon
├── 用户权益         component: jst/marketing/jst_user_rights/index  path: user-rights
└── 权益核销记录     component: jst/marketing/jst_rights_writeoff_record/index  path: rights-writeoff

一级菜单：积分管理
├── 积分账户         component: jst/points/jst_points_account/index  path: points-account
├── 积分流水         component: jst/points/jst_points_ledger/index  path: points-ledger
├── 积分规则         component: jst/points/jst_points_rule/index  path: points-rule
├── 等级配置         component: jst/points/jst_level_config/index  path: level-config
├── 成长值流水       component: jst/points/jst_growth_ledger/index  path: growth-ledger
├── 积分商城         component: jst/mall/index             path: mall
├── 兑换订单         component: jst/mall/exchange           path: mall-exchange
├── 商城商品         component: jst/points/jst_mall_goods/index  path: mall-goods
└── 兑换订单记录     component: jst/points/jst_mall_exchange_order/index  path: exchange-order

一级菜单：公告管理
└── 公告列表         component: jst/notice/index           path: notice

一级菜单：消息管理
├── 消息模板         component: jst/message/jst_message_template/index  path: msg-template
└── 消息发送记录     component: jst/message/jst_message_log/index  path: msg-log

一级菜单：赛事数据（赛事相关明细）
├── 成绩记录         component: jst/event/jst_score_record/index  path: score-record
├── 证书记录         component: jst/event/jst_cert_record/index  path: cert-record
├── 证书模板         component: jst/event/jst_cert_template/index  path: cert-template
├── 报名表单模板     component: jst/event/jst_enroll_form_template/index  path: enroll-form
├── 赛事方关联       component: jst/event/jst_event_partner/index  path: event-partner
└── 学习记录         component: jst/event/jst_course_learn_record/index  path: learn-record

一级菜单：财务管理
├── 打款记录         component: jst/finance/jst_payment_pay_record/index  path: payout
├── 合同管理         component: jst/finance/jst_contract_record/index  path: contract
└── 发票管理         component: jst/finance/jst_invoice_record/index  path: invoice

一级菜单：风控管理
├── 风控规则         component: jst/risk/jst_risk_rule/index  path: risk-rule
├── 风控告警         component: jst/risk/jst_risk_alert/index  path: risk-alert
├── 黑名单           component: jst/risk/jst_risk_blacklist/index  path: blacklist
├── 风控案例         component: jst/risk/jst_risk_case/index  path: risk-case
└── 审计日志         component: jst/risk/jst_audit_log/index  path: audit-log
```

**SQL 格式**：参考已有的 `99-migration-admin-menus.sql` 和 `99-migration-partner-menus.sql` 格式。

**注意事项**：
- `menu_id` 使用 9800-9899 段（避免与已有 9700 partner / 9900 admin 冲突）
- 编辑/详情等子页面设置 `visible = '1'`（隐藏）但仍需注册（否则路由不生效）
- 每个菜单的 `perms` 字段参考已有 Controller 的 `@PreAuthorize` 注解
- 按钮权限（menu_type='F'）本卡不做，只做目录和页面级菜单

### B. 前端路径修正

**B1. Dashboard（views/jst/dashboard/index.vue）**

修正 `todoItems` 和 `quickActions` 的 `path` 值，使用 SQL 注册后的真实菜单路径。

例如（具体 path 以 SQL 注册为准）：
- `/jst/contest` → 确保菜单 path 能解析
- `/jst/order/admin-refund` → 确保菜单 path 能解析
- 移除所有对 deprecated 路径的引用（如 `/jst/event/jst_contest`）

如果使用了 `route-access.js` 候选数组机制，确保候选数组中第一项是精品页路径。

**B2. Index 首页（views/index.vue）**

同样修正所有入口链接的 path 值，对齐菜单注册路径。

**B3. 删除 deprecated 页面的废弃提示中的误导**

在 9 个 deprecated 页面顶部，已有 `<!-- DEPRECATED -->` 注释和 `el-alert`。确保 alert 中的跳转链接指向正确的精品页路径（而非另一个 404）。

### C. 管理端体验优化

**C1. 左侧菜单图标**

为所有新注册的一级菜单配置 Element UI 图标：
- 赛事管理 → `el-icon-trophy`
- 课程管理 → `el-icon-reading`
- 订单管理 → `el-icon-s-order`
- 用户管理 → `el-icon-user`
- 营销管理 → `el-icon-present`
- 积分管理 → `el-icon-coin`
- 公告管理 → `el-icon-bell`
- 消息管理 → `el-icon-message`
- 赛事数据 → `el-icon-data-analysis`
- 财务管理 → `el-icon-wallet`
- 风控管理 → `el-icon-warning`

**C2. 菜单排序**

在 SQL 中设置 `order_num` 使菜单按业务优先级排列：
1. 运营数据看板（最常用）
2. 赛事管理
3. 报名管理（合并到赛事下）
4. 课程管理
5. 订单管理
6. 用户管理
7. 渠道管理
8. 营销管理
9. 积分管理
10. 公告管理
11. 消息管理
12. 赛事数据
13. 财务管理
14. 风控管理
15. 赛事方工作台（赛事方角色专属）

**C3. 404 页面优化**

修改 `views/error/404.vue`（如存在），增加：
- 返回首页按钮
- 返回上一页按钮
- 友好的提示文案（"页面不存在或您没有访问权限"）

**C4. 页面标题统一**

确保 `document.title` 在每个页面正确显示为 `页面名称 - 竞赛通`。检查 `src/permission.js` 中的 title 拼接逻辑。

## 三、规范

- SQL 使用 `INSERT INTO sys_menu` + `ON DUPLICATE KEY UPDATE` 或 `REPLACE INTO`，确保重复执行不报错
- 菜单 `menu_id` 用 9800-9899 段
- 不改后端 Java 代码
- `npm run build:prod` 必须通过

## 四、DoD

- [ ] 菜单注册 SQL 覆盖所有精品页（~50 条菜单）
- [ ] Dashboard 所有跳转链接不再 404
- [ ] Index 首页所有跳转链接不再 404
- [ ] Deprecated 页面的跳转链接指向正确精品页
- [ ] 菜单图标和排序配置
- [ ] `npm run build:prod` 通过
- [ ] 报告交付（含"需要执行的 SQL 文件名"提醒）
