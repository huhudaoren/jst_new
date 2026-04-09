-- =====================================================================
-- 文件名：23-基础数据初始化.sql
-- 用途  ：竞赛通业务字典 + 菜单 + 角色 + 权限 初始化
-- 适用  ：MySQL 5.7 / RuoYi-Vue 4.x
-- 执行顺序：在 ddl/01~10 业务表导入完成之后执行
-- 说明  ：本脚本只插入业务相关的字典/菜单/权限，不动若依原生数据
-- =====================================================================
SET NAMES utf8mb4;

-- =====================================================================
-- Part 1. 业务字典（sys_dict_type + sys_dict_data）
-- 命名约定：dict_type 全部以 `jst_` 开头
-- =====================================================================

-- ----- 字典类型 -----
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES
('用户类型',           'jst_user_type',           '0', 'admin', NOW(), '学生/家长/渠道方'),
('用户状态',           'jst_user_status',         '0', 'admin', NOW(), '账号启停'),
('风控标记',           'jst_risk_flag',           '0', 'admin', NOW(), '正常/黑名单/待复核'),
('档案认领状态',       'jst_claim_status',        '0', 'admin', NOW(), 'SM-14'),
('渠道类型',           'jst_channel_type',        '0', 'admin', NOW(), '教师/机构/个人'),
('渠道认证状态',       'jst_channel_auth_status', '0', 'admin', NOW(), 'SM-3'),
('绑定状态',           'jst_binding_status',      '0', 'admin', NOW(), 'SM-15'),
('赛事来源',           'jst_contest_source',      '0', 'admin', NOW(), '平台/赛事方'),
('赛事审核状态',       'jst_contest_audit',       '0', 'admin', NOW(), 'SM-5'),
('赛事业务状态',       'jst_contest_status',      '0', 'admin', NOW(), 'SM-5'),
('报名来源',           'jst_enroll_source',       '0', 'admin', NOW(), '自助/渠道单条/批量'),
('报名审核状态',       'jst_enroll_audit',        '0', 'admin', NOW(), 'SM-6'),
('订单类型',           'jst_order_type',          '0', 'admin', NOW(), '普通/零元/全抵/兑换/预约'),
('订单业务类型',       'jst_order_business',      '0', 'admin', NOW(), 'enroll/course/mall/appointment'),
('订单状态',           'jst_order_status',        '0', 'admin', NOW(), 'SM-1'),
('退款状态',           'jst_refund_status',       '0', 'admin', NOW(), 'SM-2'),
('退款类型',           'jst_refund_type',         '0', 'admin', NOW(), 'refund_only/return_refund/special'),
('支付方式',           'jst_pay_method',          '0', 'admin', NOW(), 'wechat/bank/points/mix'),
('凭证审核',           'jst_voucher_audit',       '0', 'admin', NOW(), 'SM-7'),
('团队预约状态',       'jst_team_appt_status',    '0', 'admin', NOW(), 'SM-13'),
('个人预约主状态',     'jst_appt_main_status',    '0', 'admin', NOW(), 'SM-11'),
('核销子项类型',       'jst_writeoff_item_type',  '0', 'admin', NOW(), 'arrival/gift/ceremony/custom'),
('核销子项状态',       'jst_writeoff_status',     '0', 'admin', NOW(), 'SM-12'),
('返点台账状态',       'jst_rebate_status',       '0', 'admin', NOW(), 'SM-8'),
('渠道提现状态',       'jst_settle_status',       '0', 'admin', NOW(), 'SM-9'),
('赛事方结算状态',     'jst_event_settle_status', '0', 'admin', NOW(), 'SM-10'),
('返点模式',           'jst_rebate_mode',         '0', 'admin', NOW(), 'rate/fixed'),
('积分变更类型',       'jst_points_change_type',  '0', 'admin', NOW(), 'earn/spend/freeze/...'),
('积分来源',           'jst_points_source',       '0', 'admin', NOW(), 'enroll/course/sign/...'),
('适用角色',           'jst_applicable_role',     '0', 'admin', NOW(), 'student/channel/both'),
('优惠券类型',         'jst_coupon_type',         '0', 'admin', NOW(), '满减/直减/折扣/赛事专享'),
('优惠券状态',         'jst_coupon_status',       '0', 'admin', NOW(), 'SM-16'),
('权益类型',           'jst_rights_type',         '0', 'admin', NOW(), '报名抵扣/场地减免/...'),
('权益状态',           'jst_rights_status',       '0', 'admin', NOW(), 'SM-17'),
('成绩发布状态',       'jst_score_publish',       '0', 'admin', NOW(), 'SM-19'),
('证书发放状态',       'jst_cert_issue',          '0', 'admin', NOW(), 'SM-20'),
('课程审核状态',       'jst_course_audit',        '0', 'admin', NOW(), 'SM-21'),
('合同状态',           'jst_contract_status',     '0', 'admin', NOW(), 'SM-22'),
('发票状态',           'jst_invoice_status',      '0', 'admin', NOW(), 'SM-23'),
('风险等级',           'jst_risk_level',          '0', 'admin', NOW(), 'low/mid/high'),
('风险工单状态',       'jst_risk_case_status',    '0', 'admin', NOW(), 'SM-24'),
('赛事方入驻状态',     'jst_partner_apply',       '0', 'admin', NOW(), 'SM-4'),
('赛事分类',           'jst_contest_category',    '0', 'admin', NOW(), '艺术/音乐/...'),
('参赛组别',           'jst_group_level',         '0', 'admin', NOW(), '幼儿园/小学低/...');

-- ----- 字典数据（精选关键状态机；其余可在管理后台后续维护） -----
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time) VALUES
-- 用户类型
(1,'学生','student','jst_user_type','','primary','N','0','admin',NOW()),
(2,'家长','parent','jst_user_type','','info','N','0','admin',NOW()),
(3,'渠道方','channel','jst_user_type','','warning','N','0','admin',NOW()),
-- 用户状态
(1,'禁用','0','jst_user_status','','danger','N','0','admin',NOW()),
(2,'正常','1','jst_user_status','','success','Y','0','admin',NOW()),
(3,'封禁','2','jst_user_status','','danger','N','0','admin',NOW()),
-- 订单状态 SM-1
(1,'已创建','created','jst_order_status','','info','Y','0','admin',NOW()),
(2,'待支付','pending_pay','jst_order_status','','warning','N','0','admin',NOW()),
(3,'已支付','paid','jst_order_status','','primary','N','0','admin',NOW()),
(4,'审核中','reviewing','jst_order_status','','warning','N','0','admin',NOW()),
(5,'服务中','in_service','jst_order_status','','primary','N','0','admin',NOW()),
(6,'售后中','aftersale','jst_order_status','','warning','N','0','admin',NOW()),
(7,'已完成','completed','jst_order_status','','success','N','0','admin',NOW()),
(8,'已取消','cancelled','jst_order_status','','danger','N','0','admin',NOW()),
(9,'已归档','closed','jst_order_status','','info','N','0','admin',NOW()),
-- 退款状态 SM-2
(1,'待审核','pending','jst_refund_status','','warning','Y','0','admin',NOW()),
(2,'已通过','approved','jst_refund_status','','primary','N','0','admin',NOW()),
(3,'已驳回','rejected','jst_refund_status','','danger','N','0','admin',NOW()),
(4,'退款中','refunding','jst_refund_status','','warning','N','0','admin',NOW()),
(5,'已完成','completed','jst_refund_status','','success','N','0','admin',NOW()),
(6,'已关闭','closed','jst_refund_status','','info','N','0','admin',NOW()),
-- 团队预约 SM-13
(1,'已预约','booked','jst_team_appt_status','','primary','Y','0','admin',NOW()),
(2,'部分核销','partial_writeoff','jst_team_appt_status','','warning','N','0','admin',NOW()),
(3,'全部核销','fully_writeoff','jst_team_appt_status','','success','N','0','admin',NOW()),
(4,'部分核销已结束','partial_writeoff_ended','jst_team_appt_status','','danger','N','0','admin',NOW()),
(5,'未到场','no_show','jst_team_appt_status','','danger','N','0','admin',NOW()),
(6,'已取消','cancelled','jst_team_appt_status','','info','N','0','admin',NOW()),
(7,'已过期','expired','jst_team_appt_status','','info','N','0','admin',NOW()),
-- 返点台账 SM-8
(1,'待计提','pending_accrual','jst_rebate_status','','info','Y','0','admin',NOW()),
(2,'可提现','withdrawable','jst_rebate_status','','primary','N','0','admin',NOW()),
(3,'审核中','in_review','jst_rebate_status','','warning','N','0','admin',NOW()),
(4,'已打款','paid','jst_rebate_status','','success','N','0','admin',NOW()),
(5,'已回退','rolled_back','jst_rebate_status','','danger','N','0','admin',NOW()),
(6,'负向台账','negative','jst_rebate_status','','danger','N','0','admin',NOW()),
-- 渠道认证 SM-3
(1,'待审核','pending','jst_channel_auth_status','','warning','Y','0','admin',NOW()),
(2,'已通过','approved','jst_channel_auth_status','','success','N','0','admin',NOW()),
(3,'已驳回','rejected','jst_channel_auth_status','','danger','N','0','admin',NOW()),
(4,'已暂停','suspended','jst_channel_auth_status','','info','N','0','admin',NOW()),
-- 档案认领 SM-14
(1,'未认领','unclaimed','jst_claim_status','','info','Y','0','admin',NOW()),
(2,'自动认领','auto_claimed','jst_claim_status','','success','N','0','admin',NOW()),
(3,'人工认领','manual_claimed','jst_claim_status','','primary','N','0','admin',NOW()),
(4,'待人工','pending_manual','jst_claim_status','','warning','N','0','admin',NOW()),
-- 赛事分类
(1,'艺术综合','art','jst_contest_category','','default','N','0','admin',NOW()),
(2,'音乐','music','jst_contest_category','','default','N','0','admin',NOW()),
(3,'舞蹈','dance','jst_contest_category','','default','N','0','admin',NOW()),
(4,'美术','painting','jst_contest_category','','default','N','0','admin',NOW()),
(5,'朗诵戏剧','drama','jst_contest_category','','default','N','0','admin',NOW()),
(6,'文化','culture','jst_contest_category','','default','N','0','admin',NOW()),
(7,'科技','tech','jst_contest_category','','default','N','0','admin',NOW()),
(8,'体育','sport','jst_contest_category','','default','N','0','admin',NOW()),
-- 参赛组别
(1,'幼儿园组','kindergarten','jst_group_level','','default','N','0','admin',NOW()),
(2,'小学低年级组','primary_low','jst_group_level','','default','N','0','admin',NOW()),
(3,'小学高年级组','primary_high','jst_group_level','','default','N','0','admin',NOW()),
(4,'初中组','junior','jst_group_level','','default','N','0','admin',NOW()),
(5,'高中组','senior','jst_group_level','','default','N','0','admin',NOW());

-- 其余字典数据可在管理后台「字典管理」补充，DDL 中已注明枚举

-- =====================================================================
-- Part 2. 业务初始数据：等级配置 + 积分规则
-- =====================================================================
INSERT INTO jst_level_config (level_code, level_name, level_no, growth_threshold, applicable_role, status, create_by, create_time) VALUES
('STU_LV1','学徒',     1,      0, 'student', 1, 'admin', NOW()),
('STU_LV2','学子',     2,    500, 'student', 1, 'admin', NOW()),
('STU_LV3','才俊',     3,   2000, 'student', 1, 'admin', NOW()),
('STU_LV4','英才',     4,   5000, 'student', 1, 'admin', NOW()),
('STU_LV5','大师',     5,  20000, 'student', 1, 'admin', NOW()),
('CHN_LV1','合作伙伴', 1,      0, 'channel', 1, 'admin', NOW()),
('CHN_LV2','金牌伙伴', 2,  10000, 'channel', 1, 'admin', NOW()),
('CHN_LV3','钻石伙伴', 3,  50000, 'channel', 1, 'admin', NOW()),
('CHN_LV4','战略伙伴', 4, 200000, 'channel', 1, 'admin', NOW());

INSERT INTO jst_points_rule (rule_type, source_type, reward_mode, reward_value, applicable_role, status, create_by, create_time, remark) VALUES
('points','enroll','rate', 0.05,'student',1,'admin',NOW(),'报名按净实付5%返积分'),
('points','sign',  'fixed',  10,'student',1,'admin',NOW(),'每日签到+10积分'),
('points','invite','fixed', 200,'student',1,'admin',NOW(),'成功邀请新用户+200积分'),
('growth','enroll','rate',  0.1,'student',1,'admin',NOW(),'报名净实付10%成长值'),
('growth','award', 'fixed',1000,'student',1,'admin',NOW(),'获奖+1000成长值'),
('points','enroll','rate', 0.02,'channel',1,'admin',NOW(),'渠道带单按净实付2%返积分'),
('growth','enroll','rate', 0.05,'channel',1,'admin',NOW(),'渠道带单5%成长值');

-- =====================================================================
-- Part 3. 菜单 + 权限（sys_menu）
-- 父菜单 ID 从 2000 开始（避开若依原生 1-1063）
-- =====================================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
-- 顶级目录
(2000,'竞赛通业务',          0, 5, 'jst', NULL, 1, 0, 'M','0','0','',          'star',     'admin', NOW(), '竞赛通业务总目录'),
-- 一级目录
(2100,'用户与身份',         2000, 1, 'user',     NULL, 1, 0, 'M','0','0','', 'user',     'admin', NOW(), 'jst-user 模块'),
(2200,'赛事与报名',         2000, 2, 'event',    NULL, 1, 0, 'M','0','0','', 'tree',     'admin', NOW(), 'jst-event 模块'),
(2300,'订单与资金',         2000, 3, 'order',    NULL, 1, 0, 'M','0','0','', 'money',    'admin', NOW(), 'jst-order 模块'),
(2400,'渠道与返点',         2000, 4, 'channel',  NULL, 1, 0, 'M','0','0','', 'tree-table','admin',NOW(), 'jst-channel 模块'),
(2500,'积分与商城',         2000, 5, 'points',   NULL, 1, 0, 'M','0','0','', 'chart',    'admin', NOW(), 'jst-points 模块'),
(2600,'营销中心',           2000, 6, 'marketing',NULL, 1, 0, 'M','0','0','', 'tab',      'admin', NOW(), 'jst-marketing 模块'),
(2700,'消息中心',           2000, 7, 'message',  NULL, 1, 0, 'M','0','0','', 'message',  'admin', NOW(), 'jst-message 模块'),
(2800,'风控中心',           2000, 8, 'risk',     NULL, 1, 0, 'M','0','0','', 'lock',     'admin', NOW(), 'jst-risk 模块'),
(2900,'财务中心',           2000, 9, 'finance',  NULL, 1, 0, 'M','0','0','', 'money',    'admin', NOW(), 'jst-finance 模块');

-- 注：下级菜单（user/list、user/edit 等）由若依代码生成器在生成时自动写入 sys_menu，
-- 这里只准备一级目录，避免重复维护。

-- 角色（赛事方/渠道方/平台运营）
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) VALUES
(100, '平台运营',   'jst_platform_op', 10, '1', '1', '1', '0', '0', 'admin', NOW(), '看全部业务数据'),
(101, '赛事方账号', 'jst_partner',     20, '1', '1', '1', '0', '0', 'admin', NOW(), '通过 @PartnerScope 限制只能看自己的赛事'),
(102, '渠道方账号', 'jst_channel',     30, '1', '1', '1', '0', '0', 'admin', NOW(), '通过 @ChannelScope 限制只能看自己拉新的数据');

-- 角色-菜单关联（平台运营拥有 2000 一级目录全部）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(100, 2000),(100,2100),(100,2200),(100,2300),(100,2400),(100,2500),(100,2600),(100,2700),(100,2800),(100,2900),
(101, 2000),(101,2200),(101,2300),(101,2900),
(102, 2000),(102,2100),(102,2300),(102,2400),(102,2500);

-- =====================================================================
-- 完成。导入完成后请：
-- 1. 在管理后台为各角色补全二级菜单（生成器生成后自动加入）
-- 2. 创建第一个平台运营管理员用户并赋角色 jst_platform_op
-- =====================================================================
