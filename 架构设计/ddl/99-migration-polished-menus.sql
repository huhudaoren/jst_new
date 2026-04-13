-- =====================================================================
-- 文件名：99-migration-polished-menus.sql
-- 用途  ：P0 路由与菜单修复（精品页菜单注册 + 首页/看板跳转兜底）
-- 背景  ：WEB-ADMIN-FIX 任务卡（修复首页和看板 404）
-- 说明  ：
--   1) 使用 9800-9899 菜单号段，避免与 9700/9900 冲突
--   2) 所有精品页（含 hidden edit/detail 子页）均注册到 sys_menu
--   3) 脚本幂等：INSERT ... ON DUPLICATE KEY UPDATE
-- =====================================================================

SET NAMES utf8mb4;

-- =====================
-- 1) 根目录（/jst）
-- =====================
INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
(9800, '竞赛通管理', 0, 1, 'jst', NULL, NULL, 1, 0, 'M', '0', '0', '', 'el-icon-data-analysis', 'migration', NOW(), 'migration', NOW(), '竞赛通管理端精品页根目录')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  update_by = 'migration',
  update_time = NOW(),
  remark = VALUES(remark);

-- =====================
-- 2) 精品页菜单（含 hidden 子页面）
-- =====================
INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 运营看板（优先级 1）
(9801, '运营数据看板', 9800, 10, 'dashboard', 'jst/dashboard/index', NULL, 1, 0, 'C', '0', '0', 'jst:admin:dashboard', 'el-icon-data-analysis', 'migration', NOW(), 'migration', NOW(), '管理端运营看板'),

-- 赛事管理（优先级 2, 报名合并到赛事）
(9810, '赛事管理', 9800, 20, 'contest', 'jst/contest/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:contest:list', 'el-icon-trophy', 'migration', NOW(), 'migration', NOW(), '赛事列表精品页'),
(9811, '赛事编辑', 9800, 21, 'contest-edit', 'jst/contest/edit', NULL, 1, 0, 'C', '1', '0', 'jst:event:contest:edit', '#', 'migration', NOW(), 'migration', NOW(), '隐藏：赛事编辑页'),
(9812, '赛事详情', 9800, 22, 'contest-detail', 'jst/contest/detail', NULL, 1, 0, 'C', '1', '0', 'jst:event:contest:query', '#', 'migration', NOW(), 'migration', NOW(), '隐藏：赛事详情页'),
(9813, '报名管理', 9800, 23, 'enroll', 'jst/enroll/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:enrollRecord:list', '#', 'migration', NOW(), 'migration', NOW(), '赛事报名管理'),
(9814, '赛事方入驻管理', 9800, 24, 'partner-apply', 'jst/partner-apply/index', NULL, 1, 0, 'C', '0', '0', 'jst:organizer:partnerApply:list', '#', 'migration', NOW(), 'migration', NOW(), '赛事方入驻审核'),

-- 课程管理（优先级 4）
(9820, '课程管理', 9800, 30, 'course', 'jst/course/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:course:list', 'el-icon-reading', 'migration', NOW(), 'migration', NOW(), '课程列表精品页'),
(9821, '课程编辑', 9800, 31, 'course-edit', 'jst/course/edit', NULL, 1, 0, 'C', '1', '0', 'jst:event:course:edit', '#', 'migration', NOW(), 'migration', NOW(), '隐藏：课程编辑页'),

-- 订单管理（优先级 5）
(9830, '订单管理', 9800, 40, 'order/admin-order', 'jst/order/admin-order/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:adminOrder:list', 'el-icon-s-order', 'migration', NOW(), 'migration', NOW(), '订单管理精品页'),
(9831, '退款管理', 9800, 41, 'order/admin-refund', 'jst/order/admin-refund/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:refund:list', '#', 'migration', NOW(), 'migration', NOW(), '退款管理精品页'),
(9832, '订单明细行', 9800, 42, 'order/order-item', 'jst/order/jst_order_item/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:order_item:list', '#', 'migration', NOW(), 'migration', NOW(), '订单明细行'),
(9833, '支付记录', 9800, 43, 'order/payment-record', 'jst/order/jst_payment_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:payment_record:list', '#', 'migration', NOW(), 'migration', NOW(), '支付记录'),
(9834, '预约记录', 9800, 44, 'order/appointment-record', 'jst/order/jst_appointment_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:appointment_record:list', '#', 'migration', NOW(), 'migration', NOW(), '预约记录'),
(9835, '团队预约', 9800, 45, 'order/team-appointment', 'jst/order/jst_team_appointment/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:team_appointment:list', '#', 'migration', NOW(), 'migration', NOW(), '团队预约'),
(9836, '核销明细', 9800, 46, 'order/writeoff-item', 'jst/order/jst_appointment_writeoff_item/index', NULL, 1, 0, 'C', '0', '0', 'jst:order:appointment_writeoff_item:list', '#', 'migration', NOW(), 'migration', NOW(), '核销明细'),

-- 用户管理（优先级 6）
(9840, '用户管理', 9800, 50, 'user', 'jst/user/index', NULL, 1, 0, 'C', '0', '0', 'jst:user:user:list', 'el-icon-user', 'migration', NOW(), 'migration', NOW(), '用户列表精品页'),
(9841, '参赛档案管理', 9800, 51, 'participant', 'jst/participant/index', NULL, 1, 0, 'C', '0', '0', 'jst:user:participant:list', '#', 'migration', NOW(), 'migration', NOW(), '参赛档案管理'),
(9842, '表单模板管理', 9800, 52, 'form-template', 'jst/form-template/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:formTemplate:list', '#', 'migration', NOW(), 'migration', NOW(), '报名表单模板管理'),

-- 渠道管理（优先级 7）
(9845, '渠道管理', 9800, 60, 'channel', 'jst/channel/index', NULL, 1, 0, 'C', '0', '0', 'jst:user:channel:list', 'el-icon-user-solid', 'migration', NOW(), 'migration', NOW(), '渠道列表'),
(9846, '渠道认证审核', 9800, 61, 'channel-auth', 'jst/channel-auth/index', NULL, 1, 0, 'C', '0', '0', 'jst:organizer:channelApply:list', '#', 'migration', NOW(), 'migration', NOW(), '渠道认证审核'),
(9847, '渠道提现审核', 9800, 62, 'channel/admin-withdraw', 'jst/channel/admin-withdraw/index', NULL, 1, 0, 'C', '0', '0', 'jst:channel:withdraw:list', '#', 'migration', NOW(), 'migration', NOW(), '渠道提现审核'),

-- 营销管理（优先级 8）
(9850, '优惠券模板', 9800, 70, 'coupon/template', 'jst/coupon/template', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:coupon_template:list', 'el-icon-present', 'migration', NOW(), 'migration', NOW(), '优惠券模板精品页'),
(9851, '已发优惠券', 9800, 71, 'coupon/issued', 'jst/coupon/issued', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:user_coupon:list', '#', 'migration', NOW(), 'migration', NOW(), '已发优惠券'),
(9852, '权益模板', 9800, 72, 'rights/template', 'jst/rights/template', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:rights_template:list', '#', 'migration', NOW(), 'migration', NOW(), '权益模板精品页'),
(9853, '已发权益', 9800, 73, 'rights/issued', 'jst/rights/issued', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:user_rights:list', '#', 'migration', NOW(), 'migration', NOW(), '已发权益'),
(9854, '优惠券批次', 9800, 74, 'marketing/coupon-batch', 'jst/marketing/jst_coupon_issue_batch/index', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:coupon_issue_batch:list', '#', 'migration', NOW(), 'migration', NOW(), '优惠券批次'),
(9855, '用户优惠券', 9800, 75, 'marketing/user-coupon', 'jst/marketing/jst_user_coupon/index', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:user_coupon:list', '#', 'migration', NOW(), 'migration', NOW(), '用户优惠券'),
(9856, '用户权益', 9800, 76, 'marketing/user-rights', 'jst/marketing/jst_user_rights/index', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:user_rights:list', '#', 'migration', NOW(), 'migration', NOW(), '用户权益'),
(9857, '权益核销记录', 9800, 77, 'marketing/rights-writeoff', 'jst/marketing/jst_rights_writeoff_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:marketing:rights_writeoff_record:list', '#', 'migration', NOW(), 'migration', NOW(), '权益核销记录'),

-- 积分管理（优先级 9）
(9860, '积分账户', 9800, 80, 'points/points-account', 'jst/points/jst_points_account/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:points_account:list', 'el-icon-coin', 'migration', NOW(), 'migration', NOW(), '积分账户'),
(9861, '积分流水', 9800, 81, 'points/points-ledger', 'jst/points/jst_points_ledger/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:points_ledger:list', '#', 'migration', NOW(), 'migration', NOW(), '积分流水'),
(9862, '积分规则', 9800, 82, 'points/points-rule', 'jst/points/jst_points_rule/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:points_rule:list', '#', 'migration', NOW(), 'migration', NOW(), '积分规则'),
(9863, '等级配置', 9800, 83, 'points/level-config', 'jst/points/jst_level_config/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:level_config:list', '#', 'migration', NOW(), 'migration', NOW(), '等级配置'),
(9864, '成长值流水', 9800, 84, 'points/growth-ledger', 'jst/points/jst_growth_ledger/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:growth_ledger:list', '#', 'migration', NOW(), 'migration', NOW(), '成长值流水'),
(9865, '积分商城', 9800, 85, 'mall', 'jst/mall/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:mall_goods:list', '#', 'migration', NOW(), 'migration', NOW(), '积分商城精品页'),
(9866, '兑换订单', 9800, 86, 'mall/exchange', 'jst/mall/exchange', NULL, 1, 0, 'C', '0', '0', 'jst:points:mall_exchange_order:list', '#', 'migration', NOW(), 'migration', NOW(), '兑换订单精品页'),
(9867, '商城商品', 9800, 87, 'points/mall-goods', 'jst/points/jst_mall_goods/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:mall_goods:list', '#', 'migration', NOW(), 'migration', NOW(), '商城商品'),
(9868, '兑换订单记录', 9800, 88, 'points/exchange-order', 'jst/points/jst_mall_exchange_order/index', NULL, 1, 0, 'C', '0', '0', 'jst:points:mall_exchange_order:list', '#', 'migration', NOW(), 'migration', NOW(), '兑换订单记录'),

-- 公告管理（优先级 10）
(9870, '公告管理', 9800, 90, 'notice', 'jst/notice/index', NULL, 1, 0, 'C', '0', '0', 'jst:message:notice:list', 'el-icon-bell', 'migration', NOW(), 'migration', NOW(), '公告管理精品页'),

-- 消息管理（优先级 11）
(9871, '消息模板', 9800, 100, 'message/msg-template', 'jst/message/jst_message_template/index', NULL, 1, 0, 'C', '0', '0', 'jst:message:message_template:list', 'el-icon-message', 'migration', NOW(), 'migration', NOW(), '消息模板'),
(9872, '消息发送记录', 9800, 101, 'message/msg-log', 'jst/message/jst_message_log/index', NULL, 1, 0, 'C', '0', '0', 'jst:message:message_log:list', '#', 'migration', NOW(), 'migration', NOW(), '消息发送记录'),

-- 赛事数据（优先级 12）
(9880, '成绩记录', 9800, 110, 'event/score-record', 'jst/event/jst_score_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:score_record:list', 'el-icon-data-analysis', 'migration', NOW(), 'migration', NOW(), '成绩记录'),
(9881, '证书记录', 9800, 111, 'event/cert-record', 'jst/event/jst_cert_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:cert_record:list', '#', 'migration', NOW(), 'migration', NOW(), '证书记录'),
(9882, '证书模板', 9800, 112, 'event/cert-template', 'jst/event/jst_cert_template/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:cert_template:list', '#', 'migration', NOW(), 'migration', NOW(), '证书模板'),
(9883, '报名表单模板', 9800, 113, 'event/enroll-form', 'jst/event/jst_enroll_form_template/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:enroll_form_template:list', '#', 'migration', NOW(), 'migration', NOW(), '报名表单模板'),
(9884, '赛事方关联', 9800, 114, 'event/event-partner', 'jst/event/jst_event_partner/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:event_partner:list', '#', 'migration', NOW(), 'migration', NOW(), '赛事方关联'),
(9885, '学习记录', 9800, 115, 'event/learn-record', 'jst/event/jst_course_learn_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:event:course_learn_record:list', '#', 'migration', NOW(), 'migration', NOW(), '学习记录'),

-- 财务管理（优先级 13）
(9890, '打款记录', 9800, 120, 'finance/payout', 'jst/finance/jst_payment_pay_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:finance:payment_pay_record:list', 'el-icon-wallet', 'migration', NOW(), 'migration', NOW(), '打款记录'),
(9891, '合同管理', 9800, 121, 'finance/contract', 'jst/finance/jst_contract_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:finance:contract_record:list', '#', 'migration', NOW(), 'migration', NOW(), '合同管理'),
(9892, '发票管理', 9800, 122, 'finance/invoice', 'jst/finance/jst_invoice_record/index', NULL, 1, 0, 'C', '0', '0', 'jst:finance:invoice_record:list', '#', 'migration', NOW(), 'migration', NOW(), '发票管理'),

-- 风控管理（优先级 14）
(9893, '风控规则', 9800, 130, 'risk/risk-rule', 'jst/risk/jst_risk_rule/index', NULL, 1, 0, 'C', '0', '0', 'jst:risk:rule:list', 'el-icon-warning', 'migration', NOW(), 'migration', NOW(), '风控规则'),
(9894, '风控告警', 9800, 131, 'risk/risk-alert', 'jst/risk/jst_risk_alert/index', NULL, 1, 0, 'C', '0', '0', 'jst:risk:alert:list', '#', 'migration', NOW(), 'migration', NOW(), '风控告警'),
(9895, '黑名单', 9800, 132, 'risk/blacklist', 'jst/risk/jst_risk_blacklist/index', NULL, 1, 0, 'C', '0', '0', 'jst:risk:blacklist:list', '#', 'migration', NOW(), 'migration', NOW(), '黑名单'),
(9896, '风控案例', 9800, 133, 'risk/risk-case', 'jst/risk/jst_risk_case/index', NULL, 1, 0, 'C', '0', '0', 'jst:risk:case:list', '#', 'migration', NOW(), 'migration', NOW(), '风控案例'),
(9897, '审计日志', 9800, 134, 'risk/audit-log', 'jst/risk/jst_audit_log/index', NULL, 1, 0, 'C', '0', '0', 'jst:risk:audit:list', '#', 'migration', NOW(), 'migration', NOW(), '审计日志')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  query = VALUES(query),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  update_by = 'migration',
  update_time = NOW(),
  remark = VALUES(remark);

-- =====================
-- 3) 角色绑定（admin / jst_admin）
-- =====================
SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);
SET @jst_admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'jst_admin' LIMIT 1);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, menu_id
FROM sys_menu
WHERE menu_id BETWEEN 9800 AND 9899
  AND @admin_role_id IS NOT NULL;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @jst_admin_role_id, menu_id
FROM sys_menu
WHERE menu_id BETWEEN 9800 AND 9899
  AND @jst_admin_role_id IS NOT NULL;

-- =====================
-- 4) 菜单排序微调（已有菜单）
-- =====================
UPDATE sys_menu SET order_num = 7 WHERE menu_id = 9900; -- 渠道管理（已存在）
UPDATE sys_menu SET order_num = 15 WHERE menu_id = 9700; -- 赛事方工作台（已存在）

-- =====================
-- 5) 验证：首页/看板跳转路径是否可命中
-- =====================
SELECT menu_id, menu_name, path, component, visible
FROM sys_menu
WHERE menu_id BETWEEN 9800 AND 9899
   OR (parent_id = 9800 AND path IN (
      'dashboard',
      'contest',
      'contest-edit',
      'enroll',
      'partner-apply',
      'course',
      'form-template',
      'coupon/template',
      'rights/template',
      'mall',
      'mall/exchange',
      'notice',
      'order/admin-refund',
      'channel',
      'channel-auth',
      'channel/admin-withdraw'
   ))
ORDER BY menu_id;

