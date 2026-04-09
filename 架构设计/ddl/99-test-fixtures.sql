-- =====================================================================
-- 文件名：99-test-fixtures.sql
-- 用�? ：开�?测试环境的测试数�?(FOR DEV/TEST ONLY, 禁止生产执行!)
-- 适用  ：MySQL 5.7
-- 强制规约：参�?架构设计/25-�?�?开发流�?md §3.8
--   - 所有测试数据必须从本文件入�?禁止前端硬编�?mock
--   - 测试用户 mobile 前缀 13800000xxx
--   - 测试人员姓名前缀 测试_
--   - 每个 feature 完成后由开发追加自己的 fixture
-- =====================================================================
SET NAMES utf8mb4;



-- TODO jst-event 报名场景测试数据 (�?jst-event feature 完成后追�?
-- TODO jst-order 订单/退款场景测试数�?-- TODO jst-channel 返点台账正向/负向场景
-- TODO jst-points 积分账户余额/冻结场景
-- TODO jst-order 团队预约部分核销场景

-- =====================================================================
-- 清理脚本 (用于重跑测试前清�?
-- 手动执行: 仅删�?fixture 数据,不影响真实业�?-- =====================================================================
DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
DELETE FROM jst_participant WHERE create_by = 'fixture';
DELETE FROM jst_channel WHERE create_by = 'fixture';
DELETE FROM jst_user WHERE create_by = 'fixture';
DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';
DELETE FROM jst_notice WHERE create_by = 'fixture';
DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
DELETE FROM jst_course WHERE create_by = 'fixture';
DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';
DELETE FROM jst_contest WHERE create_by = 'fixture';
DELETE FROM jst_event_partner WHERE create_by = 'fixture';
DELETE FROM sys_user_role WHERE user_id IN (9101, 9102, 9201, 9203);
DELETE FROM sys_user WHERE create_by = 'fixture' AND user_id IN (9101, 9102);
-- Step 1.1: 清理�?fixture (避免 PK 冲突)
DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
DELETE FROM jst_participant WHERE create_by = 'fixture';
DELETE FROM jst_student_channel_binding WHERE create_by = 'fixture';
DELETE FROM jst_channel WHERE create_by = 'fixture';
DELETE FROM jst_user WHERE create_by = 'fixture';
-- 其他可能�?fixture �?F-NOTICE/F-COURSE/F7/F8 新增�?
DELETE FROM jst_notice WHERE create_by = 'fixture';
DELETE FROM jst_course WHERE create_by = 'fixture';
DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
DELETE FROM jst_contest WHERE create_by = 'fixture';
DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
DELETE FROM jst_event_partner WHERE create_by = 'fixture';
DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';
DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';
-- 1.1 清理所�?fixture 数据(按依赖顺�?
DELETE FROM sys_user_role WHERE user_id IN (SELECT user_id FROM sys_user WHERE user_name LIKE
                                                                               'partner_%' OR user_name LIKE 'wx_%');
DELETE FROM sys_user WHERE user_name LIKE 'partner_%' OR user_name LIKE 'wx_%';
DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
DELETE FROM jst_participant WHERE create_by = 'fixture';
DELETE FROM jst_student_channel_binding WHERE create_by = 'fixture';
DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
DELETE FROM jst_event_partner WHERE create_by = 'fixture';
DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';
DELETE FROM jst_channel WHERE create_by = 'fixture';
DELETE FROM jst_user WHERE create_by = 'fixture' OR openid LIKE 'openid_test_%';
DELETE FROM jst_notice WHERE create_by = 'fixture';
DELETE FROM jst_course WHERE create_by = 'fixture';
DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
DELETE FROM jst_contest WHERE create_by = 'fixture';
DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';
-- C2 cleanup helper
DELETE FROM jst_rebate_ledger WHERE create_by = 'fixture';
DELETE FROM jst_rebate_settlement WHERE create_by = 'fixture';
DELETE FROM jst_payment_pay_record WHERE business_type = 'rebate_withdraw'
  AND business_id IN (9100001, 9105101, 9105102, 9105103, 9105104);
DELETE FROM jst_payment_record WHERE create_by = 'fixture';
DELETE FROM jst_order_item WHERE create_by = 'fixture';
DELETE FROM jst_order_main WHERE create_by = 'fixture';
DELETE FROM jst_order_item WHERE create_by = 'fixture';
DELETE FROM jst_payment_record WHERE create_by = 'fixture';
DELETE FROM jst_rebate_ledger WHERE create_by = 'fixture';
DELETE FROM jst_enroll_record WHERE create_by = 'fixture';
DELETE FROM jst_user_rights WHERE create_by = 'fixture';
DELETE FROM jst_user_coupon WHERE create_by = 'fixture';
DELETE FROM jst_rights_template WHERE create_by = 'fixture';
DELETE FROM jst_coupon_template WHERE create_by = 'fixture';
DELETE FROM jst_mall_exchange_order WHERE create_by = 'fixture';
DELETE FROM jst_mall_goods WHERE create_by = 'fixture';
DELETE FROM jst_points_account WHERE create_by = 'fixture';
DELETE FROM jst_points_ledger WHERE create_by = 'fixture';
DELETE FROM jst_growth_ledger WHERE ledger_id BETWEEN 99281 AND 99289;
DELETE FROM jst_level_config WHERE level_id BETWEEN 9811 AND 9814;
DELETE FROM jst_user_coupon WHERE user_coupon_id BETWEEN 99231 AND 99239;
DELETE FROM jst_refund_record WHERE create_by = 'fixture' AND item_id IN (99211, 99212, 99213, 99214);
DELETE FROM jst_payment_record WHERE order_id IN (94212, 94213);
DELETE FROM jst_order_main WHERE order_id IN (94212, 94213);
DELETE FROM jst_refund_record WHERE order_id IN (9301, 9302, 9303, 9304, 9305, 9306);
DELETE FROM jst_rebate_ledger WHERE order_id IN (9302, 9305);
DELETE FROM jst_payment_record WHERE order_id IN (9301, 9302, 9304, 9305, 9306);
DELETE FROM jst_order_item WHERE order_id IN (9301, 9302, 9303, 9304, 9305, 9306);
DELETE FROM jst_order_main WHERE order_id IN (9301, 9302, 9303, 9304, 9305, 9306);
-- C5b/FCD fixture id range collides with auto-increment zone; force clean
DELETE FROM jst_rebate_ledger WHERE ledger_id BETWEEN 94500 AND 94599;
DELETE FROM jst_rebate_settlement WHERE settlement_id BETWEEN 94600 AND 94699;
DELETE FROM jst_rebate_settlement WHERE settlement_id IN (9100001, 9105101, 9105102, 9105103, 9105104);
DELETE FROM jst_order_item WHERE order_id BETWEEN 94410 AND 94499;
DELETE FROM jst_order_main WHERE order_id BETWEEN 94410 AND 94499;
DELETE FROM jst_rebate_rule WHERE rule_id BETWEEN 9601 AND 9699;
DELETE FROM jst_enroll_record WHERE enroll_id IN (8911, 8912, 8913, 8914, 8915, 8916);
DELETE FROM jst_user_coupon WHERE user_coupon_id IN (9805, 9807);
DELETE FROM jst_appointment_writeoff_item WHERE writeoff_item_id BETWEEN 96800 AND 96999;
DELETE FROM jst_appointment_record WHERE appointment_id BETWEEN 96700 AND 96899;
DELETE FROM jst_team_appointment_member WHERE member_id BETWEEN 96600 AND 96799;
DELETE FROM jst_team_appointment WHERE team_appointment_id BETWEEN 96600 AND 96699;
-- =====================================================================
-- jst-user 模块测试数据 (含临时档案认领样�?feature)
-- =====================================================================

-- 1. 测试正式用户(注意:sys_user 由若依默认提�?admin,这里写业务表 jst_user)
INSERT INTO jst_user (user_id, openid, mobile, nickname, real_name, user_type, status, register_time, create_by, create_time)
VALUES
(1001, 'openid_test_1001', '13800000001', '测试_张妈�?, '测试_张妈�?, 'parent', 1, NOW(), 'fixture', NOW()),
(1002, 'openid_test_1002', '13800000002', '测试_李爸�?, '测试_李爸�?, 'parent', 1, NOW(), 'fixture', NOW()),
(1003, 'openid_test_1003', '13800000003', '测试_王老师', '测试_王老师', 'channel',1, NOW(), 'fixture', NOW()),
(1004, 'openid_test_1004', '13800000004', '测试_刘老师', '测试_刘老师', 'channel',1, NOW(), 'fixture', NOW());

-- 2. 测试渠道�?INSERT INTO jst_channel (channel_id, user_id, channel_type, channel_name, contact_mobile, auth_status, status, create_by, create_time)
VALUES
(2001, 1003, 'teacher', '测试_艺术工作�?, '13800000003', 'approved', 1, 'fixture', NOW()),
(2002, 1004, 'organization', '测试_星光美育中心', '13800000004', 'approved', 1, 'fixture', NOW());

-- 2.1 学生初始 active 绑定（F4 学生渠道绑定�?INSERT INTO jst_student_channel_binding (binding_id, user_id, channel_id, bind_time, status, operator_id, create_by, create_time)
VALUES
(5001, 1001, 2001, NOW(), 'active', 1001, 'fixture', NOW());

UPDATE jst_user
SET bound_channel_id = 2001,
    update_by = 'fixture',
    update_time = NOW()
WHERE user_id = 1001
  AND create_by = 'fixture';

-- 3. 测试临时参赛档案 (覆盖样板 feature �?4 种场�?

-- 3.1 场景一:唯一精确命中 (mobile=13800000001 + name=测试_张小�? �?应自动认领到 user 1001
INSERT INTO jst_participant (participant_id, user_id, participant_type, name, gender, age,
                              guardian_name, guardian_mobile, school, created_by_channel_id,
                              claim_status, visible_scope, create_by, create_time)
VALUES
(3001, NULL, 'temporary_participant', '测试_张小�?, 1, 8,
       '测试_张妈�?, '13800000001', '测试_第一小学', 2001,
       'unclaimed', 'channel_only', 'fixture', NOW());

-- 3.2 场景�?多候�?(�?mobile 不同姓名) �?应进�?pending_manual
INSERT INTO jst_participant (participant_id, user_id, participant_type, name, gender, age,
                              guardian_name, guardian_mobile, school, created_by_channel_id,
                              claim_status, visible_scope, create_by, create_time)
VALUES
(3002, NULL, 'temporary_participant', '测试_李大�?, 1, 7,
       '测试_李爸�?, '13800000002', '测试_第二小学', 2001,
       'unclaimed', 'channel_only', 'fixture', NOW()),
(3003, NULL, 'temporary_participant', '测试_李二�?, 2, 5,
       '测试_李爸�?, '13800000002', '测试_第二幼儿�?, 2001,
       'unclaimed', 'channel_only', 'fixture', NOW());

-- 3.3 场景�?已认领状�?用于测试撤销)
INSERT INTO jst_participant (participant_id, user_id, participant_type, name, gender, age,
                              guardian_name, guardian_mobile, school, created_by_channel_id,
                              claim_status, claimed_user_id, claimed_time, visible_scope, create_by, create_time)
VALUES
(3004, 1001, 'temporary_participant', '测试_张小�?, 2, 9,
       '测试_张妈�?, '13800000001', '测试_第一小学', 2001,
       'auto_claimed', 1001, NOW(), 'channel_only', 'fixture', NOW());

INSERT INTO jst_participant_user_map (map_id, participant_id, user_id, claim_method, claim_time, status, create_by, create_time)
VALUES
(4001, 3004, 1001, 'auto_phone_name', NOW(), 'active', 'fixture', NOW());

-- 3.4 场景�?无任何匹�?用于测试 null 返回)
-- (无需插入,只要 mobile=13800009999 不存在即�?

-- =====================================================================
-- 后续模块测试数据 (其他 feature 按本格式追加,标注 feature �?+ 涉及�?
-- =====================================================================

-- =====================================================================
-- F5 jst-organizer 赛事方入驻审核测试数�?-- 涉及表：jst_event_partner_apply
-- =====================================================================

INSERT INTO jst_event_partner_apply (
    apply_id, apply_no, partner_id, partner_name, contact_name, contact_mobile,
    business_license_no, qualification_json, settlement_info_json, invoice_info_json,
    contract_files_json, apply_status, supplement_remark, audit_remark, audit_user_id,
    submit_time, create_by, create_time
) VALUES (
    5001, 'PA_FIXTURE_5001', NULL, '测试_待审核赛事机�?, '测试_赛事方联系人', '13800005556',
    '91110000FIXTURE01',
    JSON_ARRAY(JSON_OBJECT('fileName', 'license.pdf', 'url', 'https://fixture.example.com/license.pdf')),
    JSON_OBJECT('accountName', '测试_待审核赛事机�?, 'bankAccountNo', '6222020000005001', 'bankName', '测试银行'),
    JSON_OBJECT('invoiceType', 'normal', 'taxNo', '91110000FIXTURE01'),
    JSON_ARRAY(JSON_OBJECT('fileName', 'contract.pdf', 'url', 'https://fixture.example.com/contract.pdf')),
    'pending', NULL, NULL, NULL,
    NOW(), 'fixture', NOW()
);
-- =====================================================================
-- C9 mall aftersale + F-POINTS-CENTER fixtures
-- =====================================================================

INSERT INTO jst_user (
    user_id, openid, mobile, nickname, real_name, user_type, status, register_time, create_by, create_time
) VALUES (
    9001, 'openid_test_9001', '13800009001', '测试_积分中心用户', '测试_积分中心用户',
    'parent', 1, NOW(), 'fixture', NOW()
);

UPDATE jst_user
SET available_points = 1250,
    frozen_points = 100,
    update_by = 'fixture',
    update_time = NOW()
WHERE user_id = 9001;

INSERT INTO jst_level_config (
    level_id, level_code, level_name, level_no, growth_threshold, icon, applicable_role, status,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9811, 'L1', '普通学�?, 1, 0, 'https://fixture.example.com/levels/l1.png', 'student', 1,
    'fixture', NOW(), 'fixture', NOW(), 'F-PC level 1', '0'
),
(
    9812, 'L2', '银卡学员', 2, 500, 'https://fixture.example.com/levels/l2.png', 'student', 1,
    'fixture', NOW(), 'fixture', NOW(), 'F-PC level 2', '0'
),
(
    9813, 'L3', '金卡学员', 3, 2000, 'https://fixture.example.com/levels/l3.png', 'student', 1,
    'fixture', NOW(), 'fixture', NOW(), 'F-PC level 3', '0'
),
(
    9814, 'L4', '钻石学员', 4, 5000, 'https://fixture.example.com/levels/l4.png', 'student', 1,
    'fixture', NOW(), 'fixture', NOW(), 'F-PC level 4', '0'
);

INSERT INTO jst_points_account (
    account_id, owner_type, owner_id, available_points, frozen_points, total_earn, total_spend,
    growth_value, current_level_id, version, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES (
    99290, 'user', 9001, 1250, 100, 3500, 2150,
    850, 9812, 0, 'fixture', NOW(), 'fixture', NOW(), 'F-PC summary account', '0'
);

INSERT INTO jst_points_ledger (
    ledger_id, account_id, owner_type, owner_id, change_type, source_type, source_ref_id,
    points_change, balance_after, operator_id, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    99271, 99290, 'user', 9001, 'earn', 'contest', 8201,
    500, 1500, 9001, 'fixture', DATE_SUB(NOW(), INTERVAL 5 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 5 DAY),
    'F-PC contest earn', '0'
),
(
    99272, 99290, 'user', 9001, 'spend', 'mall_exchange', 99212,
    -200, 1300, 9001, 'fixture', DATE_SUB(NOW(), INTERVAL 2 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 2 DAY),
    'F-PC mall spend', '0'
),
(
    99273, 99290, 'user', 9001, 'freeze', 'mall_exchange', 99213,
    -50, 1250, 9001, 'fixture', DATE_SUB(NOW(), INTERVAL 1 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 1 DAY),
    'F-PC mall freeze', '0'
);

INSERT INTO jst_growth_ledger (
    ledger_id, account_id, change_type, source_type, source_ref_id, growth_change, balance_after,
    level_before, level_after, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    99281, 99290, 'earn', 'contest', 8201, 300, 300,
    9811, 9811, 'fixture', DATE_SUB(NOW(), INTERVAL 10 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 10 DAY),
    'F-PC first growth', '0'
),
(
    99282, 99290, 'earn', 'course', 8801, 550, 850,
    9811, 9812, 'fixture', DATE_SUB(NOW(), INTERVAL 3 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 3 DAY),
    'F-PC level up to L2', '0'
);

INSERT INTO jst_mall_goods (
    goods_id, goods_name, goods_type, cover_image, description, points_price, cash_price,
    stock, stock_warning, role_limit, status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    99201, '测试_C9_未使用优惠券商品', 'virtual', 'https://fixture.example.com/mall/c9-coupon-unused.jpg',
    'C9 aftersale virtual coupon unused', 1000, 0.00,
    -1, 0, 'both', 'on', 'fixture', NOW(), 'fixture', NOW(),
    JSON_OBJECT('virtualTargetType', 'coupon', 'couponTemplateId', 9701), '0'
),
(
    99202, '测试_C9_已使用优惠券商品', 'virtual', 'https://fixture.example.com/mall/c9-coupon-used.jpg',
    'C9 aftersale virtual coupon used', 1000, 0.00,
    -1, 0, 'both', 'on', 'fixture', NOW(), 'fixture', NOW(),
    JSON_OBJECT('virtualTargetType', 'coupon', 'couponTemplateId', 9701), '0'
),
(
    99203, '测试_C9_待发货实物商�?, 'physical', 'https://fixture.example.com/mall/c9-pending-ship.jpg',
    'C9 aftersale physical pending ship', 2000, 19.90,
    6, 1, 'student', 'on', 'fixture', NOW(), 'fixture', NOW(),
    NULL, '0'
),
(
    99204, '测试_C9_已发货实物商�?, 'physical', 'https://fixture.example.com/mall/c9-shipped.jpg',
    'C9 aftersale physical shipped', 800, 0.00,
    8, 1, 'student', 'on', 'fixture', NOW(), 'fixture', NOW(),
    NULL, '0'
);

INSERT INTO jst_order_main (
    order_id, order_no, order_type, business_type, user_id, participant_id,
    list_amount, coupon_amount, points_deduct_amount, points_used, platform_bear_amount,
    net_pay_amount, service_fee, pay_method, pay_initiator, pay_initiator_id, pay_time,
    order_status, refund_status, allow_self_refund, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94212, 'OD_C9_94212', 'exchange', 'mall', 1001, 0,
    19.90, 0.00, 0.00, 0, 0.00,
    19.90, 0.00, 'wechat', 'self', 1001, DATE_SUB(NOW(), INTERVAL 2 DAY),
    'paid', 'none', 0, 'fixture', NOW(), 'fixture', NOW(), 'C9 pending ship order', '0'
),
(
    94213, 'OD_C9_94213', 'exchange', 'mall', 1001, 0,
    0.00, 0.00, 0.00, 0, 0.00,
    0.00, 0.00, 'points', 'self', 1001, DATE_SUB(NOW(), INTERVAL 4 DAY),
    'paid', 'none', 0, 'fixture', NOW(), 'fixture', NOW(), 'C9 shipped order', '0'
);

INSERT INTO jst_payment_record (
    payment_id, payment_no, order_id, pay_method, cash_amount, points_amount, points_used,
    third_party_no, pay_status, pay_time, operator_id, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94221, 'PAY_C9_94221', 94212, 'wechat', 19.90, 0.00, 0,
    'WX_TX_C9_94212', 'success', DATE_SUB(NOW(), INTERVAL 2 DAY), 1001, 'fixture', NOW(), 'fixture', NOW(),
    'C9 payment pending ship', '0'
),
(
    94222, 'PAY_C9_94222', 94213, 'points', 0.00, 0.00, 0,
    'POINTS_TX_C9_94213', 'success', DATE_SUB(NOW(), INTERVAL 4 DAY), 1001, 'fixture', NOW(), 'fixture', NOW(),
    'C9 payment shipped', '0'
);

INSERT INTO jst_mall_exchange_order (
    exchange_id, exchange_no, user_id, goods_id, quantity, points_used, cash_amount, order_id,
    address_snapshot_json, status, logistics_company, logistics_no, ship_time, complete_time, aftersale_status,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    99211, 'EX_C9_99211', 1001, 99201, 1, 1000, 0.00, NULL,
    NULL, 'completed', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), 'none',
    'fixture', NOW(), 'fixture', NOW(), 'C9 virtual unused', '0'
),
(
    99214, 'EX_C9_99214', 1001, 99202, 1, 1000, 0.00, NULL,
    NULL, 'completed', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), 'none',
    'fixture', NOW(), 'fixture', NOW(), 'C9 virtual used', '0'
),
(
    99212, 'EX_C9_99212', 1001, 99203, 1, 2000, 19.90, 94212,
    JSON_OBJECT('receiverName', '测试_C9_待发�?, 'mobile', '13800000001', 'province', '广东�?, 'city', '深圳�?, 'district', '南山�?, 'detailAddress', 'C9 pending ship road 1'),
    'pending_ship', NULL, NULL, NULL, NULL, 'none',
    'fixture', NOW(), 'fixture', NOW(), 'C9 pending ship exchange', '0'
),
(
    99213, 'EX_C9_99213', 1001, 99204, 1, 800, 0.00, 94213,
    JSON_OBJECT('receiverName', '测试_C9_已发�?, 'mobile', '13800000001', 'province', '广东�?, 'city', '广州�?, 'district', '天河�?, 'detailAddress', 'C9 shipped road 2'),
    'shipped', '顺丰速运', 'SF-C9-0001', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'none',
    'fixture', NOW(), 'fixture', NOW(), 'C9 shipped exchange', '0'
);

INSERT INTO jst_user_coupon (
    user_coupon_id, coupon_template_id, user_id, issue_batch_no, issue_source, status,
    valid_start, valid_end, order_id, use_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    99231, 9701, 1001, 'C9BATCH001', 'mall_exchange', 'unused',
    DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 99211, NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C9 unused coupon for exchange 99211', '0'
),
(
    99232, 9701, 1001, 'C9BATCH002', 'mall_exchange', 'used',
    DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 99214, DATE_SUB(NOW(), INTERVAL 1 DAY),
    'fixture', NOW(), 'fixture', NOW(), 'C9 used coupon for exchange 99214', '0'
);

-- =====================================================================
-- F6 jst-organizer 渠道认证申请测试数据
-- 涉及表：jst_channel_auth_apply
-- =====================================================================

INSERT INTO jst_channel_auth_apply (
    apply_id, apply_no, user_id, channel_id, channel_type, apply_name, materials_json,
    apply_status, audit_remark, audit_user_id, submit_time, audit_time, create_by, create_time
) VALUES (
    6001, 'CA_FIXTURE_6001', 1002, NULL, 'teacher', '测试_李老师渠道认证',
    JSON_ARRAY(JSON_OBJECT('type', 'id_card', 'url', 'https://fixture.example.com/channel-id-card.jpg')),
    'pending', NULL, NULL, NOW(), NULL, 'fixture', NOW()
);

-- =====================================================================
-- F-NOTICE jst-message 公告 / banner / 字典测试数据
-- 涉及表：jst_notice
-- =====================================================================

INSERT INTO jst_notice (
    notice_id, title, category, content, cover_image, top_flag, status, publish_time,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(7001, '测试_首页置顶公告', 'platform', '<p>这是首页 banner 公告内容，用于测试首页轮播与公告详情展示�?/p>',
 'https://fixture.example.com/notice/banner-7001.png', 1, 'published', DATE_SUB(NOW(), INTERVAL 1 DAY),
 'fixture', NOW(), 'fixture', NOW(), 'F-NOTICE fixture', '0'),
(7002, '测试_赛事公告', 'contest', '<p>这是赛事分类公告，用于测试公告列表与摘要截取逻辑�?/p>',
 'https://fixture.example.com/notice/banner-7002.png', 0, 'published', DATE_SUB(NOW(), INTERVAL 2 DAY),
 'fixture', NOW(), 'fixture', NOW(), 'F-NOTICE fixture', '0'),
(7003, '测试_积分公告', 'points', '<p>这是积分分类公告，用于测试公告列表排序与详情接口�?/p>',
 'https://fixture.example.com/notice/banner-7003.png', 0, 'published', DATE_SUB(NOW(), INTERVAL 3 DAY),
 'fixture', NOW(), 'fixture', NOW(), 'F-NOTICE fixture', '0');

-- =====================================================================
-- F-COURSE jst-event 课程基础接口测试数据
-- 涉及表：jst_course / jst_course_learn_record
-- =====================================================================

INSERT INTO jst_course (
    course_id, course_name, course_type, cover_image, description, price, points_price,
    creator_type, creator_id, maic_source_id, audit_status, status, visible_scope,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    7201, '测试_素描启蒙�?, 'normal', 'https://fixture.example.com/course-normal.jpg',
    '测试_normal_课程简介，用于小程序课程列表、详情和我的课程场景联调�?, 199.00, 19900,
    'platform', 1, NULL, 'approved', 'on', 'all',
    'fixture', NOW(), 'fixture', NOW(), 'F-COURSE normal fixture', '0'
),
(
    7202, '测试_AI美育体验�?, 'ai_maic', 'https://fixture.example.com/course-ai.jpg',
    '测试_ai_maic_课程简介，用于课程列表过滤和详情展示�?, 299.00, 29900,
    'platform', 1, 'maic_fixture_7202', 'approved', 'on', 'all',
    'fixture', NOW(), 'fixture', NOW(), 'F-COURSE ai_maic fixture', '0'
),
(
    7203, '测试_待审核课�?, 'normal', 'https://fixture.example.com/course-draft.jpg',
    '测试_draft_课程简介，用于提审和未审核禁止上架场景�?, 99.00, 9900,
    'platform', 1, NULL, 'draft', 'off', 'all',
    'fixture', NOW(), 'fixture', NOW(), 'F-COURSE draft fixture', '0'
);

INSERT INTO jst_course_learn_record (
    learn_id, course_id, user_id, progress, duration_seconds, quiz_score, complete_status,
    complete_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES (
    7301, 7201, 1001, 68.50, 1800, 92.00, 'learning',
    NULL, 'fixture', NOW(), 'fixture', NOW(), 'F-COURSE learn fixture', '0'
);

-- =====================================================================
-- F7 jst-event 赛事 CRUD 与查询测试数�?-- 涉及表：sys_user / sys_user_role / jst_event_partner / jst_contest
-- =====================================================================

-- F7-0 两个赛事方账号（登录密码均为 admin123�?INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
) VALUES
(9101, 'partner_f7_a', '测试_赛事方A', '00', 'partner_f7_a@example.com', '13800007771', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), NOW(),
 'fixture', NOW(), '', NULL, 'F7 赛事方A账号'),
(9102, 'partner_f7_b', '测试_赛事方B', '00', 'partner_f7_b@example.com', '13800007772', '1', '',
 '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), NOW(),
 'fixture', NOW(), '', NULL, 'F7 赛事方B账号');

INSERT INTO sys_user_role (user_id, role_id)
VALUES
(9101, 101),
(9102, 101);

INSERT INTO jst_event_partner (
    partner_id, user_id, partner_name, contact_name, contact_mobile,
    audit_status, audit_time, account_status, coop_status, create_by, create_time, del_flag
) VALUES
(8101, 9101, '测试_赛事机构A', '测试_A联系�?, '13800007771', 'approved', NOW(), 1, 'active', 'fixture', NOW(), '0'),
(8102, 9102, '测试_赛事机构B', '测试_B联系�?, '13800007772', 'approved', NOW(), 1, 'active', 'fixture', NOW(), '0');

-- F7-1 ~ F7-7 预置 5 张赛事（覆盖 draft/pending/approved/online/offline + 多分类）
INSERT INTO jst_contest (
    contest_id, contest_name, source_type, partner_id, category, group_levels, cover_image, description,
    enroll_start_time, enroll_end_time, event_start_time, event_end_time, price,
    support_channel_enroll, support_points_deduct, support_appointment,
    cert_rule_json, score_rule_json, form_template_id, aftersale_days,
    audit_status, status, created_user_id, create_by, create_time, del_flag
) VALUES
(8201, '测试_F7_在线赛事', 'partner', 8101, 'art', '小学�?初中�?, 'https://fixture.example.com/f7-online.jpg', '测试_F7_在线赛事说明',
 '2026-04-01 09:00:00', '2026-05-01 18:00:00', '2026-05-10 09:00:00', '2026-05-12 18:00:00', 99.00,
 1, 1, 0, '{}', '{}', NULL, 7, 'online', 'enrolling', 9101, 'fixture', NOW(), '0'),
(8202, '测试_F7_已下线赛�?, 'partner', 8101, 'music', '幼儿�?, 'https://fixture.example.com/f7-offline.jpg', '测试_F7_已下线赛事说�?,
 '2026-03-01 09:00:00', '2026-03-31 18:00:00', '2026-04-10 09:00:00', '2026-04-12 18:00:00', 59.00,
 1, 0, 0, '{}', '{}', NULL, 7, 'offline', 'closed', 9101, 'fixture', NOW(), '0'),
(8203, '测试_F7_待上线赛�?, 'partner', 8101, 'dance', '高中�?, 'https://fixture.example.com/f7-approved.jpg', '测试_F7_待上线赛事说�?,
 '2026-06-01 09:00:00', '2026-06-20 18:00:00', '2026-06-25 09:00:00', '2026-06-26 18:00:00', 129.00,
 1, 1, 1, '{}', '{}', NULL, 7, 'approved', 'not_started', 9101, 'fixture', NOW(), '0'),
(8204, '测试_F7_待审核赛�?, 'partner', 8102, 'painting', '小学�?, 'https://fixture.example.com/f7-pending.jpg', '测试_F7_待审核赛事说�?,
 '2026-06-01 09:00:00', '2026-06-20 18:00:00', '2026-06-25 09:00:00', '2026-06-26 18:00:00', 79.00,
 1, 0, 0, '{}', '{}', NULL, 7, 'pending', 'not_started', 9102, 'fixture', NOW(), '0'),
(8205, '测试_F7_草稿赛事', 'partner', 8102, 'tech', '初中�?, 'https://fixture.example.com/f7-draft.jpg', '测试_F7_草稿赛事说明',
 '2026-06-01 09:00:00', '2026-06-20 18:00:00', '2026-06-25 09:00:00', '2026-06-26 18:00:00', 0.00,
 0, 0, 0, '{}', '{}', NULL, 7, 'draft', 'not_started', 9102, 'fixture', NOW(), '0');

-- =====================================================================
-- F8 jst-event 动态表单模板测试数�?-- 涉及表：jst_enroll_form_template / jst_contest
-- =====================================================================

INSERT INTO jst_enroll_form_template (
    template_id, template_name, template_version, owner_type, owner_id,
    schema_json, audit_status, status, effective_time, create_by, create_time, update_by, update_time
) VALUES (
    8801, '测试_F8_标准报名模板', 1, 'platform', NULL,
    JSON_OBJECT(
        'fields',
        JSON_ARRAY(
            JSON_OBJECT('key', 'name', 'type', 'text', 'label', '姓名', 'required', true),
            JSON_OBJECT('key', 'gender', 'type', 'radio', 'label', '性别', 'required', true),
            JSON_OBJECT('key', 'age', 'type', 'age', 'label', '年龄', 'required', true),
            JSON_OBJECT('key', 'school', 'type', 'text', 'label', '学校', 'required', true),
            JSON_OBJECT('key', 'workFile', 'type', 'file', 'label', '作品文件', 'required', true)
        )
    ),
    'approved', 1, NOW(), 'fixture', NOW(), 'fixture', NOW()
);

INSERT INTO jst_contest (
    contest_id, contest_name, source_type, partner_id, category, group_levels, cover_image, description,
    enroll_start_time, enroll_end_time, event_start_time, event_end_time, price,
    support_channel_enroll, support_points_deduct, support_appointment,
    cert_rule_json, score_rule_json, form_template_id, aftersale_days,
    audit_status, status, created_user_id, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES (
    8802, '测试_F8_在线赛事', 'platform', NULL, '艺术', '小学�?, NULL, '测试_F8_动态表单模板用在线赛事',
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 11 DAY), 0.00,
    1, 0, 0,
    NULL, NULL, 8801, 7,
    'online', 'enrolling', NULL, 'fixture', NOW(), 'fixture', NOW(), NULL, '0'
);

-- =====================================================================
-- F9 jst-event 报名记录与表单快照测试数�?-- 涉及表：jst_contest / jst_enroll_record
-- =====================================================================

UPDATE jst_contest
SET form_template_id = 8801,
    update_by = 'fixture',
    update_time = NOW()
WHERE contest_id = 8201
  AND create_by = 'fixture';

INSERT INTO jst_enroll_record (
    enroll_id, enroll_no, contest_id, user_id, participant_id, channel_id, enroll_source,
    template_id, template_version, form_snapshot_json, order_id, material_status, audit_status,
    audit_remark, submit_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    8901, 'EN_FIXTURE_8901', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8901.pdf')
    ),
    NULL, 'submitted', 'pending',
    NULL, NOW(), 'fixture', NOW(), 'fixture', NOW(), 'F9 pending fixture', '0'
),
(
    8902, 'EN_FIXTURE_8902', 8201, 1001, 3004, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8902.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'F9 approved fixture', '0'
),
(
    8903, 'EN_FIXTURE_8903', 8201, 1001, 3004, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8903-old.pdf')
    ),
    NULL, 'supplement', 'supplement',
    '请补充最新作品附�?, NOW(), 'fixture', NOW(), 'fixture', NOW(), 'F9 supplement fixture', '0'
);

-- F9 cleanup helper
-- DELETE FROM jst_enroll_record WHERE create_by = 'fixture';

-- =====================================================================
-- C2 jst-order 订单混合支付测试数据
-- 涉及表：jst_user / jst_points_account / jst_coupon_template / jst_user_coupon /
--        jst_rebate_rule / jst_enroll_record
-- =====================================================================

UPDATE jst_user
SET available_points = 20000,
    frozen_points = 0,
    update_by = 'fixture',
    update_time = NOW()
WHERE user_id = 1001
  AND create_by = 'fixture';

INSERT INTO jst_points_account (
    account_id, owner_type, owner_id, available_points, frozen_points, total_earn, total_spend,
    growth_value, current_level_id, version, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES (
    9901, 'student', 1001, 20000, 0, 0, 0,
    0, NULL, 0, 'fixture', NOW(), 'fixture', NOW(), 'C2 points fixture', '0'
);

INSERT INTO jst_coupon_template (
    coupon_template_id, coupon_name, coupon_type, face_value, discount_rate, threshold_amount,
    applicable_role, applicable_contest_ids, valid_days, valid_start, valid_end, stack_rule,
    status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9701, '测试_C2_20元直减券', 'direct_reduce', 20.00, NULL, 0.00,
    'student', '8201', 30, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C2 direct reduce coupon', '0'
),
(
    9702, '测试_C2_失效�?, 'direct_reduce', 10.00, NULL, 0.00,
    'student', '8201', 30, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C2 invalid coupon', '0'
);

INSERT INTO jst_user_coupon (
    user_coupon_id, coupon_template_id, user_id, issue_batch_no, issue_source, status,
    valid_start, valid_end, order_id, use_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9801, 9701, 1001, 'C2BATCH001', 'manual', 'unused',
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C2 unused coupon', '0'
),
(
    9803, 9701, 1001, 'C2BATCH003', 'manual', 'unused',
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C2 second unused coupon', '0'
),
(
    9802, 9702, 1001, 'C2BATCH002', 'manual', 'used',
    DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 999999, DATE_SUB(NOW(), INTERVAL 1 DAY),
    'fixture', NOW(), 'fixture', NOW(), 'C2 used coupon', '0'
);

INSERT INTO jst_rebate_rule (
    rule_id, contest_id, channel_id, rebate_mode, rebate_value, service_fee_mode, service_fee_value,
    effective_time, expire_time, status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES (
    9601, 8201, 2001, 'rate', 0.1000, 'fixed', 5.0000,
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), 1,
    'fixture', NOW(), 'fixture', NOW(), 'C2 rebate rule', '0'
);

INSERT INTO jst_enroll_record (
    enroll_id, enroll_no, contest_id, user_id, participant_id, channel_id, enroll_source,
    template_id, template_version, form_snapshot_json, order_id, material_status, audit_status,
    audit_remark, submit_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    8904, 'EN_FIXTURE_8904', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8904.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C2 approved fixture coupon', '0'
),
(
    8905, 'EN_FIXTURE_8905', 8201, 1001, 3004, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8905.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C2 approved fixture zero', '0'
),
(
    8906, 'EN_FIXTURE_8906', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8906.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C2 approved fixture mixed', '0'
),
(
    8907, 'EN_FIXTURE_8907', 8201, 1001, 3004, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8907.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C2 approved fixture cancel', '0'
),
(
    8908, 'EN_FIXTURE_8908', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT(
        'template_id', 8801,
        'template_version', 1,
        'formData', JSON_OBJECT('name', '测试_张小�?, 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8908.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C2 approved fixture paid', '0'
);


-- =====================================================================
-- C4 refund flow fixtures
-- Covers: user apply/cancel, expired aftersale denial, zero-price denial,
--         partner approve/reject, admin execute, special refund, rebate rollback.
-- =====================================================================



INSERT INTO jst_user_coupon (
    user_coupon_id, coupon_template_id, user_id, issue_batch_no, issue_source, status,
    valid_start, valid_end, order_id, use_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9805, 9701, 1001, 'C4BATCH005', 'manual', 'used',
    DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 9302, DATE_SUB(NOW(), INTERVAL 5 DAY),
    'fixture', NOW(), 'fixture', NOW(), 'C4 expired used coupon', '0'
),
(
    9807, 9701, 1001, 'C4BATCH007', 'manual', 'used',
    DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 9305, DATE_SUB(NOW(), INTERVAL 1 DAY),
    'fixture', NOW(), 'fixture', NOW(), 'C4 valid used coupon', '0'
);

INSERT INTO jst_order_main (
    order_id, order_no, order_type, business_type, user_id, participant_id, channel_id, contest_id, partner_id,
    list_amount, coupon_amount, points_deduct_amount, points_used, platform_bear_amount, net_pay_amount, service_fee,
    pay_method, pay_initiator, pay_initiator_id, pay_time, order_status, refund_status, aftersale_deadline, coupon_id,
    allow_self_refund, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9301, 'OD_FIXTURE_9301', 'normal', 'enroll', 1001, 3001, 2001, 8201, 8101,
    90.00, 0.00, 20.00, 2000, 0.00, 70.00, 5.00,
    'points_cash_mix', 'self', 1001, DATE_SUB(NOW(), INTERVAL 2 DAY), 'paid', 'none', DATE_ADD(NOW(), INTERVAL 10 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C4 cancel fixture', '0'
),
(
    9302, 'OD_FIXTURE_9302', 'normal', 'enroll', 1001, 3004, 2001, 8201, 8101,
    90.00, 20.00, 0.00, 0, 20.00, 70.00, 5.00,
    'bank_transfer', 'self', 1001, DATE_SUB(NOW(), INTERVAL 5 DAY), 'in_service', 'none', DATE_SUB(NOW(), INTERVAL 1 DAY), 9805,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C4 expired special fixture', '0'
),
(
    9303, 'OD_FIXTURE_9303', 'zero_price', 'enroll', 1001, 3001, NULL, 8201, 8101,
    0.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00,
    'points', 'self', 1001, DATE_SUB(NOW(), INTERVAL 1 DAY), 'paid', 'none', DATE_ADD(NOW(), INTERVAL 10 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C4 zero price fixture', '0'
),
(
    9304, 'OD_FIXTURE_9304', 'normal', 'enroll', 1002, 3001, NULL, 8201, 8101,
    60.00, 0.00, 0.00, 0, 0.00, 60.00, 5.00,
    'wechat', 'self', 1002, DATE_SUB(NOW(), INTERVAL 1 DAY), 'paid', 'none', DATE_ADD(NOW(), INTERVAL 10 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C4 ownership fixture', '0'
),
(
    9305, 'OD_FIXTURE_9305', 'normal', 'enroll', 1001, 3001, 2001, 8201, 8101,
    120.00, 20.00, 20.00, 2000, 20.00, 80.00, 5.00,
    'points_cash_mix', 'self', 1001, DATE_SUB(NOW(), INTERVAL 1 DAY), 'in_service', 'none', DATE_ADD(NOW(), INTERVAL 10 DAY), 9807,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C4 approve execute fixture', '0'
),
(
    9306, 'OD_FIXTURE_9306', 'normal', 'enroll', 1001, 3004, 2001, 8201, 8101,
    75.00, 0.00, 0.00, 0, 0.00, 75.00, 5.00,
    'wechat', 'self', 1001, DATE_SUB(NOW(), INTERVAL 1 DAY), 'paid', 'none', DATE_ADD(NOW(), INTERVAL 10 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C4 reject fixture', '0'
);

INSERT INTO jst_order_item (
    item_id, order_id, sku_type, ref_id, item_name, quantity, list_amount, coupon_share, points_share,
    net_pay_share, service_fee_share, rebate_share, refund_amount, refund_points,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(9311, 9301, 'enroll', 8911, 'C4 item 9301', 1, 90.00, 0.00, 20.00, 70.00, 5.00, 0.00, 0.00, 0, 'fixture', NOW(), 'fixture', NOW(), 'C4 item cancel', '0'),
(9312, 9302, 'enroll', 8912, 'C4 item 9302', 1, 90.00, 20.00, 0.00, 70.00, 5.00, 8.50, 0.00, 0, 'fixture', NOW(), 'fixture', NOW(), 'C4 item expired special', '0'),
(9313, 9303, 'enroll', 8913, 'C4 item 9303', 1, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 'fixture', NOW(), 'fixture', NOW(), 'C4 item zero price', '0'),
(9314, 9304, 'enroll', 8914, 'C4 item 9304', 1, 60.00, 0.00, 0.00, 60.00, 5.00, 0.00, 0.00, 0, 'fixture', NOW(), 'fixture', NOW(), 'C4 item ownership', '0'),
(9315, 9305, 'enroll', 8915, 'C4 item 9305', 1, 120.00, 20.00, 20.00, 80.00, 5.00, 11.50, 0.00, 0, 'fixture', NOW(), 'fixture', NOW(), 'C4 item approve execute', '0'),
(9316, 9306, 'enroll', 8916, 'C4 item 9306', 1, 75.00, 0.00, 0.00, 75.00, 5.00, 0.00, 0.00, 0, 'fixture', NOW(), 'fixture', NOW(), 'C4 item reject', '0');

INSERT INTO jst_payment_record (
    payment_id, payment_no, order_id, pay_method, cash_amount, points_amount, points_used,
    third_party_no, pay_status, pay_time, operator_id,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(9321, 'PAY_FIXTURE_9321', 9301, 'points_cash_mix', 70.00, 20.00, 2000, 'WX_TX_9301', 'success', DATE_SUB(NOW(), INTERVAL 2 DAY), 1001, 'fixture', NOW(), 'fixture', NOW(), 'C4 payment cancel', '0'),
(9322, 'PAY_FIXTURE_9322', 9302, 'bank_transfer', 70.00, 0.00, 0, 'BANK_TX_9302', 'success', DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 'fixture', NOW(), 'fixture', NOW(), 'C4 payment expired special', '0'),
(9324, 'PAY_FIXTURE_9324', 9304, 'wechat', 60.00, 0.00, 0, 'WX_TX_9304', 'success', DATE_SUB(NOW(), INTERVAL 1 DAY), 1002, 'fixture', NOW(), 'fixture', NOW(), 'C4 payment ownership', '0'),
(9325, 'PAY_FIXTURE_9325', 9305, 'points_cash_mix', 80.00, 20.00, 2000, 'WX_TX_9305', 'success', DATE_SUB(NOW(), INTERVAL 1 DAY), 1001, 'fixture', NOW(), 'fixture', NOW(), 'C4 payment approve execute', '0'),
(9326, 'PAY_FIXTURE_9326', 9306, 'wechat', 75.00, 0.00, 0, 'WX_TX_9306', 'success', DATE_SUB(NOW(), INTERVAL 1 DAY), 1001, 'fixture', NOW(), 'fixture', NOW(), 'C4 payment reject', '0');

INSERT INTO jst_rebate_ledger (
    ledger_id, order_id, item_id, channel_id, contest_id, rule_id,
    list_amount, net_pay_amount, service_fee, rebate_base, rebate_amount,
    direction, status, accrual_time, event_end_time,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(9332, 9302, 9312, 2001, 8201, 9601, 90.00, 70.00, 5.00, 85.00, 8.50, 'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 'fixture', NOW(), 'fixture', NOW(), 'C4 rebate rollback', '0'),
(9335, 9305, 9315, 2001, 8201, 9601, 120.00, 80.00, 5.00, 115.00, 11.50, 'positive', 'paid', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 'fixture', NOW(), 'fixture', NOW(), 'C4 rebate negative', '0');

INSERT INTO jst_enroll_record (
    enroll_id, enroll_no, contest_id, user_id, participant_id, channel_id, enroll_source,
    template_id, template_version, form_snapshot_json, order_id, material_status, audit_status,
    audit_remark, submit_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    8911, 'EN_FIXTURE_8911', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT('template_id', 8801, 'template_version', 1, 'formData', JSON_OBJECT('name', 'fixture_c4_9301', 'gender', 'male', 'age', 8, 'school', 'fixture_school'), 'attachments', JSON_ARRAY('https://fixture.example.com/c4/9301.pdf')),
    9301, 'submitted', 'approved', 'fixture approved', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C4 enroll cancel', '0'
),
(
    8912, 'EN_FIXTURE_8912', 8201, 1001, 3004, NULL, 'self',
    8801, 1,
    JSON_OBJECT('template_id', 8801, 'template_version', 1, 'formData', JSON_OBJECT('name', 'fixture_c4_9302', 'gender', 'female', 'age', 9, 'school', 'fixture_school'), 'attachments', JSON_ARRAY('https://fixture.example.com/c4/9302.pdf')),
    9302, 'submitted', 'approved', 'fixture approved', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C4 enroll special', '0'
),
(
    8913, 'EN_FIXTURE_8913', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT('template_id', 8801, 'template_version', 1, 'formData', JSON_OBJECT('name', 'fixture_c4_9303', 'gender', 'male', 'age', 8, 'school', 'fixture_school'), 'attachments', JSON_ARRAY('https://fixture.example.com/c4/9303.pdf')),
    9303, 'submitted', 'approved', 'fixture approved', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C4 enroll zero', '0'
),
(
    8914, 'EN_FIXTURE_8914', 8201, 1002, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT('template_id', 8801, 'template_version', 1, 'formData', JSON_OBJECT('name', 'fixture_c4_9304', 'gender', 'male', 'age', 8, 'school', 'fixture_school'), 'attachments', JSON_ARRAY('https://fixture.example.com/c4/9304.pdf')),
    9304, 'submitted', 'approved', 'fixture approved', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C4 enroll ownership', '0'
),
(
    8915, 'EN_FIXTURE_8915', 8201, 1001, 3001, NULL, 'self',
    8801, 1,
    JSON_OBJECT('template_id', 8801, 'template_version', 1, 'formData', JSON_OBJECT('name', 'fixture_c4_9305', 'gender', 'male', 'age', 8, 'school', 'fixture_school'), 'attachments', JSON_ARRAY('https://fixture.example.com/c4/9305.pdf')),
    9305, 'submitted', 'approved', 'fixture approved', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C4 enroll approve execute', '0'
),
(
    8916, 'EN_FIXTURE_8916', 8201, 1001, 3004, NULL, 'self',
    8801, 1,
    JSON_OBJECT('template_id', 8801, 'template_version', 1, 'formData', JSON_OBJECT('name', 'fixture_c4_9306', 'gender', 'female', 'age', 9, 'school', 'fixture_school'), 'attachments', JSON_ARRAY('https://fixture.example.com/c4/9306.pdf')),
    9306, 'submitted', 'approved', 'fixture approved', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C4 enroll reject', '0'
);

-- =====================================================================
-- C5a channel withdraw apply fixtures
-- Covers: channel-side summary, ledger list, apply, amount mismatch,
--         cross-channel invalid ledger and cancel pending settlement.
-- =====================================================================

INSERT INTO jst_user (
    user_id, openid, mobile, nickname, real_name, user_type, status, register_time, create_by, create_time
) VALUES
(
    9201, 'openid_test_9201', '13800009201', '测试_渠道C5A', '测试_渠道C5A',
    'channel', 1, NOW(), 'fixture', NOW()
),
(
    9202, 'openid_test_9202', '13800009202', '测试_渠道C5B', '测试_渠道C5B',
    'channel', 1, NOW(), 'fixture', NOW()
),
(
    9203, 'openid_test_9203', '13800009203', 'FCD_EMPTY_CHANNEL', 'FCD_EMPTY_CHANNEL',
    'channel', 1, NOW(), 'fixture', NOW()
);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
(9201, 102),
(9203, 102);

INSERT INTO jst_channel (
    channel_id, user_id, channel_type, channel_name, contact_mobile, auth_status, status,
    bank_account_name, bank_account_no, bank_name, create_by, create_time, update_by, update_time, del_flag
) VALUES
(
    9201, 9201, 'teacher', '测试_C5A渠道', '13800009201', 'approved', 1,
    '测试_C5A收款�?, '622202009201', '测试银行',
    'fixture', NOW(), 'fixture', NOW(), '0'
),
(
    9202, 9202, 'teacher', '测试_C5B渠道', '13800009202', 'approved', 1,
    '测试_C5B收款�?, '622202009202', '测试银行',
    'fixture', NOW(), 'fixture', NOW(), '0'
),
(
    9203, 9203, 'teacher', 'FCD Empty Channel', '13800009203', 'approved', 1,
    'FCD Empty Bank', '622202009203', 'FCD Bank',
    'fixture', NOW(), 'fixture', NOW(), '0'
);

-- F-CHANNEL-DASHBOARD fixtures: active students bound to channel 9201
INSERT INTO jst_user (
    user_id, openid, mobile, nickname, real_name, avatar, user_type, bound_channel_id, status, register_time, create_by, create_time
) VALUES
(
    9211, 'openid_test_9211', '13800009211', 'FCD_STUDENT_A', 'FCD_STUDENT_A',
    'https://fixture.example.com/avatar/9211.png', 'student', 9201, 1, NOW(), 'fixture', NOW()
),
(
    9212, 'openid_test_9212', '13800009212', 'FCD_STUDENT_B', 'FCD_STUDENT_B',
    'https://fixture.example.com/avatar/9212.png', 'student', 9201, 1, NOW(), 'fixture', NOW()
);

INSERT INTO jst_student_channel_binding (
    binding_id, user_id, channel_id, bind_time, status, operator_id, create_by, create_time, update_by, update_time
) VALUES
(
    5211, 9211, 9201, DATE_SUB(NOW(), INTERVAL 3 DAY), 'active', 9201, 'fixture', NOW(), 'fixture', NOW()
),
(
    5212, 9212, 9201, DATE_SUB(NOW(), INTERVAL 35 DAY), 'active', 9201, 'fixture', NOW(), 'fixture', NOW()
);

INSERT INTO jst_order_main (
    order_id, order_no, order_type, business_type, user_id, participant_id, channel_id, contest_id, partner_id,
    list_amount, coupon_amount, points_deduct_amount, points_used, platform_bear_amount, net_pay_amount, service_fee,
    pay_method, pay_initiator, pay_initiator_id, pay_time, order_status, refund_status, aftersale_deadline, coupon_id,
    allow_self_refund, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94401, 'OD_C5A_001', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    123.00, 0.00, 0.00, 0, 0.00, 123.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 8 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 1', '0'
),
(
    94402, 'OD_C5A_002', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    200.00, 0.00, 0.00, 0, 0.00, 200.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 8 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 2', '0'
),
(
    94403, 'OD_C5A_003', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    307.00, 0.00, 0.00, 0, 0.00, 307.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 8 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 3', '0'
),
(
    94404, 'OD_C5A_004', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    150.00, 0.00, 0.00, 0, 0.00, 150.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 7 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 4', '0'
),
(
    94405, 'OD_C5A_005', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    88.00, 0.00, 0.00, 0, 0.00, 88.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 7 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 5', '0'
),
(
    94406, 'OD_C5A_006', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    660.00, 0.00, 0.00, 0, 0.00, 660.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 10 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 6', '0'
),
(
    94407, 'OD_C5A_007', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    180.00, 0.00, 0.00, 0, 0.00, 180.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 10 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 7', '0'
),
(
    94408, 'OD_C5A_008', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    95.00, 0.00, 0.00, 0, 0.00, 95.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 10 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a order 8', '0'
),
(
    94409, 'OD_C5B_001', 'normal', 'enroll', 9202, 3001, 9202, 8201, 8101,
    220.00, 0.00, 0.00, 0, 0.00, 220.00, 5.00,
    'wechat', 'self', 9202, DATE_SUB(NOW(), INTERVAL 6 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5a other channel order', '0'
);

INSERT INTO jst_rebate_ledger (
    ledger_id, order_id, item_id, channel_id, contest_id, rule_id,
    list_amount, net_pay_amount, service_fee, rebate_base, rebate_amount,
    direction, status, accrual_time, event_end_time, settlement_id,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94501, 94401, 94601, 9201, 8201, 9601,
    123.00, 123.00, 5.00, 118.00, 12.30,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a withdrawable 1', '0'
),
(
    94502, 94402, 94602, 9201, 8201, 9601,
    200.00, 200.00, 5.00, 195.00, 20.00,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a withdrawable 2', '0'
),
(
    94503, 94403, 94603, 9201, 8201, 9601,
    307.00, 307.00, 5.00, 302.00, 30.70,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a withdrawable 3', '0'
),
(
    94504, 94404, 94604, 9201, 8201, 9601,
    150.00, 150.00, 5.00, 145.00, 15.00,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a withdrawable 4', '0'
),
(
    94505, 94405, 94605, 9201, 8201, 9601,
    88.00, 88.00, 5.00, 83.00, 8.80,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a withdrawable 5', '0'
),
(
    94506, 94406, 94606, 9201, 8201, 9601,
    660.00, 660.00, 5.00, 655.00, 66.00,
    'positive', 'paid', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 9100001,
    'fixture', NOW(), 'fixture', NOW(), 'C5a paid ledger', '0'
),
(
    94507, 94407, 94607, 9201, 8201, 9601,
    180.00, 180.00, 5.00, 175.00, 18.00,
    'positive', 'rolled_back', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a rolled back ledger', '0'
),
(
    94508, 94408, 94608, 9201, 8201, 9601,
    95.00, 95.00, 5.00, 90.00, -9.50,
    'negative', 'negative', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a negative ledger', '0'
),
(
    94509, 94409, 94609, 9202, 8201, 9601,
    220.00, 220.00, 5.00, 215.00, 22.00,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'C5a other channel ledger', '0'
);

INSERT INTO jst_order_main (
    order_id, order_no, order_type, business_type, user_id, participant_id, channel_id, contest_id, partner_id,
    list_amount, coupon_amount, points_deduct_amount, points_used, platform_bear_amount, net_pay_amount, service_fee,
    pay_method, pay_initiator, pay_initiator_id, pay_time, order_status, refund_status, aftersale_deadline, coupon_id,
    allow_self_refund, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94421, 'OD_FCD_001', 'normal', 'enroll', 9211, 39211, NULL, 8201, 8101,
    199.00, 0.00, 0.00, 0, 0.00, 199.00, 5.00,
    'wechat', 'self', 9211, DATE_SUB(NOW(), INTERVAL 2 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 5 DAY), NULL,
    1, 'fixture', DATE_SUB(NOW(), INTERVAL 2 DAY), 'fixture', NOW(), 'FCD current month paid order A', '0'
),
(
    94422, 'OD_FCD_002', 'normal', 'enroll', 9211, 39212, 9202, 8261, 8101,
    299.00, 0.00, 0.00, 0, 0.00, 299.00, 5.00,
    'wechat', 'self', 9211, DATE_SUB(NOW(), INTERVAL 1 DAY), 'paid', 'none', DATE_ADD(NOW(), INTERVAL 5 DAY), NULL,
    1, 'fixture', DATE_SUB(NOW(), INTERVAL 1 DAY), 'fixture', NOW(), 'FCD bound-user order with non-9201 channel_id', '0'
),
(
    94423, 'OD_FCD_003', 'normal', 'enroll', 9212, 39213, NULL, 8201, 8101,
    159.00, 0.00, 0.00, 0, 0.00, 159.00, 5.00,
    'wechat', 'self', 9212, DATE_SUB(NOW(), INTERVAL 34 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 5 DAY), NULL,
    1, 'fixture', DATE_SUB(NOW(), INTERVAL 34 DAY), 'fixture', NOW(), 'FCD previous month paid order', '0'
),
(
    94424, 'OD_FCD_004', 'normal', 'enroll', 9212, 39214, 9202, 8201, 8101,
    88.00, 0.00, 0.00, 0, 0.00, 88.00, 5.00,
    'wechat', 'self', 9212, NULL, 'pending_pay', 'none', DATE_ADD(NOW(), INTERVAL 5 DAY), NULL,
    1, 'fixture', DATE_SUB(NOW(), INTERVAL 1 DAY), 'fixture', NOW(), 'FCD current month pending order', '0'
);

INSERT INTO jst_rebate_ledger (
    ledger_id, order_id, item_id, channel_id, contest_id, rule_id,
    list_amount, net_pay_amount, service_fee, rebate_base, rebate_amount,
    direction, status, accrual_time, event_end_time, settlement_id,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94521, 94421, 94621, 9201, 8201, 9601,
    199.00, 199.00, 5.00, 194.00, 19.90,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'FCD current month rebate 1', '0'
),
(
    94522, 94422, 94622, 9201, 8261, 9601,
    299.00, 299.00, 5.00, 294.00, 29.90,
    'positive', 'paid', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'FCD current month rebate 2', '0'
),
(
    94523, 94423, 94623, 9201, 8201, 9601,
    159.00, 159.00, 5.00, 154.00, 15.90,
    'positive', 'withdrawable', DATE_SUB(NOW(), INTERVAL 33 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), NULL,
    'fixture', NOW(), 'fixture', NOW(), 'FCD previous month rebate', '0'
);

-- =====================================================================
-- C5b admin withdraw audit + payout fixtures
-- Covers:
--   1. admin pending list/detail
--   2. approve without negative offset
--   3. approve with negative offset
--   4. negative overflow reject
--   5. reject pending settlement
-- =====================================================================

INSERT INTO jst_order_main (
    order_id, order_no, order_type, business_type, user_id, participant_id, channel_id, contest_id, partner_id,
    list_amount, coupon_amount, points_deduct_amount, points_used, platform_bear_amount, net_pay_amount, service_fee,
    pay_method, pay_initiator, pay_initiator_id, pay_time, order_status, refund_status, aftersale_deadline, coupon_id,
    allow_self_refund, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94410, 'OD_C5B_ADMIN_010', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    500.00, 0.00, 0.00, 0, 0.00, 500.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 5 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b pending no negative', '0'
),
(
    94411, 'OD_C5B_ADMIN_011', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    1000.00, 0.00, 0.00, 0, 0.00, 1000.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 5 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b pending with negative', '0'
),
(
    94412, 'OD_C5B_ADMIN_012', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    100.00, 0.00, 0.00, 0, 0.00, 100.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 9 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b negative offset 10', '0'
),
(
    94413, 'OD_C5B_ADMIN_013', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    200.00, 0.00, 0.00, 0, 0.00, 200.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 8 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b negative offset 20', '0'
),
(
    94414, 'OD_C5B_ADMIN_014', 'normal', 'enroll', 9202, 3001, 9202, 8201, 8101,
    500.00, 0.00, 0.00, 0, 0.00, 500.00, 5.00,
    'wechat', 'self', 9202, DATE_SUB(NOW(), INTERVAL 5 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b overflow positive', '0'
),
(
    94415, 'OD_C5B_ADMIN_015', 'normal', 'enroll', 9202, 3001, 9202, 8201, 8101,
    300.00, 0.00, 0.00, 0, 0.00, 300.00, 5.00,
    'wechat', 'self', 9202, DATE_SUB(NOW(), INTERVAL 10 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b overflow negative 30', '0'
),
(
    94416, 'OD_C5B_ADMIN_016', 'normal', 'enroll', 9202, 3001, 9202, 8201, 8101,
    500.00, 0.00, 0.00, 0, 0.00, 500.00, 5.00,
    'wechat', 'self', 9202, DATE_SUB(NOW(), INTERVAL 9 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b overflow negative 50', '0'
),
(
    94417, 'OD_C5B_ADMIN_017', 'normal', 'enroll', 9201, 3001, 9201, 8201, 8101,
    400.00, 0.00, 0.00, 0, 0.00, 400.00, 5.00,
    'wechat', 'self', 9201, DATE_SUB(NOW(), INTERVAL 4 DAY), 'completed', 'none', DATE_ADD(NOW(), INTERVAL 3 DAY), NULL,
    1, 'fixture', NOW(), 'fixture', NOW(), 'C5b reject pending', '0'
);

INSERT INTO jst_rebate_settlement (
    settlement_id, settlement_no, channel_id, apply_amount, negative_offset_amount, actual_pay_amount,
    invoice_status, invoice_id, bank_account_snapshot, status, audit_remark, pay_voucher_url,
    apply_time, pay_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9100001, 'WD_FIXTURE_C5A_PAID', 9201, 660.00, 0.00, 660.00,
    'none', NULL, '{"bankName":"测试银行","accountNo":"622202009201","accountName":"测试_C5A收款�?}', 'paid', 'fixture paid',
    'https://fixture.example.com/payout/paid-9100001.png',
    DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY),
    'fixture', NOW(), 'fixture', NOW(), NULL, '0'
),
(
    9105101, 'WD_FIXTURE_C5B_001', 9201, 50.00, 0.00, 50.00,
    'none', NULL, '{"bankName":"测试银行","accountNo":"622202009201","accountName":"测试_C5A收款�?}', 'pending', NULL, NULL,
    DATE_SUB(NOW(), INTERVAL 4 HOUR), NULL,
    'fixture', NOW(), 'fixture', NOW(), NULL, '0'
),
(
    9105102, 'WD_FIXTURE_C5B_002', 9201, 100.00, 0.00, 100.00,
    'none', NULL, '{"bankName":"测试银行","accountNo":"622202009201","accountName":"测试_C5A收款�?}', 'pending', NULL, NULL,
    DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL,
    'fixture', NOW(), 'fixture', NOW(), NULL, '0'
),
(
    9105103, 'WD_FIXTURE_C5B_003', 9202, 50.00, 0.00, 50.00,
    'none', NULL, '{"bankName":"测试银行","accountNo":"622202009202","accountName":"测试_C5B收款�?}', 'pending', NULL, NULL,
    DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL,
    'fixture', NOW(), 'fixture', NOW(), NULL, '0'
),
(
    9105104, 'WD_FIXTURE_C5B_004', 9201, 40.00, 0.00, 40.00,
    'none', NULL, '{"bankName":"测试银行","accountNo":"622202009201","accountName":"测试_C5A收款�?}', 'pending', NULL, NULL,
    DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL,
    'fixture', NOW(), 'fixture', NOW(), NULL, '0'
);

INSERT INTO jst_rebate_ledger (
    ledger_id, order_id, item_id, channel_id, contest_id, rule_id,
    list_amount, net_pay_amount, service_fee, rebate_base, rebate_amount,
    direction, status, accrual_time, event_end_time, settlement_id,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    94510, 94410, 94610, 9201, 8201, 9601,
    500.00, 500.00, 5.00, 495.00, 50.00,
    'positive', 'in_review', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 9105101,
    'fixture', NOW(), 'fixture', NOW(), 'C5b pending no negative positive', '0'
),
(
    94511, 94411, 94611, 9201, 8201, 9601,
    1000.00, 1000.00, 5.00, 995.00, 100.00,
    'positive', 'in_review', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 9105102,
    'fixture', NOW(), 'fixture', NOW(), 'C5b pending with negative positive', '0'
),
(
    94512, 94412, 94612, 9201, 8201, 9601,
    100.00, 100.00, 5.00, 95.00, -10.00,
    'negative', 'negative', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', DATE_SUB(NOW(), INTERVAL 7 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 7 DAY), 'C5b offset oldest -10', '0'
),
(
    94513, 94413, 94613, 9201, 8201, 9601,
    200.00, 200.00, 5.00, 195.00, -20.00,
    'negative', 'negative', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', DATE_SUB(NOW(), INTERVAL 6 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 6 DAY), 'C5b offset next -20', '0'
),
(
    94514, 94414, 94614, 9202, 8201, 9601,
    500.00, 500.00, 5.00, 495.00, 50.00,
    'positive', 'in_review', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 9105103,
    'fixture', NOW(), 'fixture', NOW(), 'C5b overflow positive', '0'
),
(
    94515, 94415, 94615, 9202, 8201, 9601,
    300.00, 300.00, 5.00, 295.00, -30.00,
    'negative', 'negative', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', DATE_SUB(NOW(), INTERVAL 9 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 9 DAY), 'C5b overflow -30', '0'
),
(
    94516, 94416, 94616, 9202, 8201, 9601,
    500.00, 500.00, 5.00, 495.00, -50.00,
    'negative', 'negative', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), NULL,
    'fixture', DATE_SUB(NOW(), INTERVAL 8 DAY), 'fixture', DATE_SUB(NOW(), INTERVAL 8 DAY), 'C5b overflow -50', '0'
),
(
    94517, 94417, 94617, 9201, 8201, 9601,
    400.00, 400.00, 5.00, 395.00, 40.00,
    'positive', 'in_review', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 9105104,
    'fixture', NOW(), 'fixture', NOW(), 'C5b reject pending positive', '0'
);

-- =====================================================================
-- C6 team appointment + writeoff fixtures
-- Covers:
--   1. 渠道方团队预�?happy path / 容量不足
--   2. 学生查看我的预约与二维码明细
--   3. 赛事方扫码核销 / 重复扫码 / 越权扫码 / 管理员扫�?-- =====================================================================

INSERT INTO jst_user (
    user_id, openid, mobile, nickname, real_name, user_type, status, register_time, create_by, create_time
) VALUES
(
    1005, 'openid_test_1005', '13800000005', '测试_赵妈�?, '测试_赵妈�?, 'parent', 1, NOW(), 'fixture', NOW()
);

INSERT INTO jst_contest (
    contest_id, contest_name, source_type, partner_id, category, group_levels, cover_image, description,
    enroll_start_time, enroll_end_time, event_start_time, event_end_time, price,
    support_channel_enroll, support_points_deduct, support_appointment,
    appointment_capacity, writeoff_config, allow_repeat_appointment,
    cert_rule_json, score_rule_json, form_template_id, aftersale_days, audit_status, status,
    created_user_id, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    8261, '测试_C6_三子项预约赛�?, 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c6-8261.jpg', '测试_C6_团队预约与三码核销',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-25 18:00:00', 39.90,
    1, 0, 1,
    10,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销'),
        JSON_OBJECT('itemType', 'gift', 'itemName', '礼品核销'),
        JSON_OBJECT('itemType', 'ceremony', 'itemName', '仪式核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C6 happy path contest', '0'
),
(
    8262, '测试_C6_容量限制赛事', 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c6-8262.jpg', '测试_C6_容量不足',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-25 18:00:00', 39.90,
    1, 0, 1,
    2,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C6 capacity full contest', '0'
),
(
    8263, '测试_C6_越权扫码赛事', 'partner', 8102, 'music', '小学�?, 'https://fixture.example.com/c6-8263.jpg', '测试_C6_跨赛事方扫码拦截',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-25 18:00:00', 19.90,
    1, 0, 1,
    5,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9102, 'fixture', NOW(), 'fixture', NOW(), 'C6 partner scope contest', '0'
),
(
    8264, '测试_C6_扫码核销团队赛事', 'partner', 8101, 'dance', '小学�?, 'https://fixture.example.com/c6-8264.jpg', '测试_C6_赛事方扫码核销',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-25 18:00:00', 0.00,
    1, 0, 1,
    10,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C6 admin scan contest', '0'
);

INSERT INTO jst_participant (
    participant_id, user_id, participant_type, name, gender, age,
    guardian_name, guardian_mobile, school, created_by_channel_id,
    claim_status, claimed_user_id, claimed_time, visible_scope, create_by, create_time
) VALUES
(
    3601, 1001, 'registered_participant', '测试_C6_成员一', 1, 8,
    '测试_张妈�?, '13800000001', '测试_第一小学', 9201,
    'manual_claimed', 1001, NOW(), 'channel_only', 'fixture', NOW()
),
(
    3602, 1002, 'registered_participant', '测试_C6_成员�?, 2, 9,
    '测试_李爸�?, '13800000002', '测试_第二小学', 9201,
    'manual_claimed', 1002, NOW(), 'channel_only', 'fixture', NOW()
),
(
    3603, 1005, 'registered_participant', '测试_C6_成员�?, 1, 10,
    '测试_赵妈�?, '13800000005', '测试_第三小学', 9201,
    'manual_claimed', 1005, NOW(), 'channel_only', 'fixture', NOW()
);

INSERT INTO jst_participant (
    participant_id, user_id, participant_type, name, gender, age,
    guardian_name, guardian_mobile, school, created_by_channel_id,
    claim_status, visible_scope, create_by, create_time
) VALUES
(
    3604, NULL, 'temporary_participant', '测试_C6_成员�?, 2, 7,
    '测试_C6_监护人四', '13800003604', '测试_第四小学', 9201,
    'unclaimed', 'channel_only', 'fixture', NOW()
),
(
    3605, NULL, 'temporary_participant', '测试_C6_成员�?, 1, 6,
    '测试_C6_监护人五', '13800003605', '测试_第五小学', 9201,
    'unclaimed', 'channel_only', 'fixture', NOW()
);

INSERT INTO jst_team_appointment (
    team_appointment_id, team_no, contest_id, channel_id, team_name, appointment_date, session_code,
    total_persons, member_persons, extra_persons, extra_list_json, writeoff_persons, status,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    96601, 'TA_FIXTURE_C6_001', 8264, 9201, '测试_C6_核销团队', '2026-04-22', 'AM',
    5, 5, 0, NULL, 0, 'booked',
    'fixture', NOW(), 'fixture', NOW(), 'C6 scan fixture team', '0'
);

INSERT INTO jst_team_appointment_member (
    member_id, team_appointment_id, user_id, participant_id, member_no, name_snapshot, mobile_snapshot,
    sub_order_id, writeoff_status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    96611, 96601, 1001, 3601, 'TM_FIXTURE_C6_001', '测试_C6_成员一', '13800000001',
    NULL, 'unused', 'fixture', NOW(), 'fixture', NOW(), 'C6 member 1', '0'
),
(
    96612, 96601, 1002, 3602, 'TM_FIXTURE_C6_002', '测试_C6_成员�?, '13800000002',
    NULL, 'unused', 'fixture', NOW(), 'fixture', NOW(), 'C6 member 2', '0'
),
(
    96613, 96601, 1005, 3603, 'TM_FIXTURE_C6_003', '测试_C6_成员�?, '13800000005',
    NULL, 'unused', 'fixture', NOW(), 'fixture', NOW(), 'C6 member 3', '0'
),
(
    96614, 96601, NULL, 3604, 'TM_FIXTURE_C6_004', '测试_C6_成员�?, '13800003604',
    NULL, 'unused', 'fixture', NOW(), 'fixture', NOW(), 'C6 member 4', '0'
),
(
    96615, 96601, NULL, 3605, 'TM_FIXTURE_C6_005', '测试_C6_成员�?, '13800003605',
    NULL, 'unused', 'fixture', NOW(), 'fixture', NOW(), 'C6 member 5', '0'
);

INSERT INTO jst_appointment_record (
    appointment_id, appointment_no, contest_id, user_id, participant_id, channel_id, appointment_type,
    team_appointment_id, appointment_date, session_code, order_id, main_status, qr_code,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    96711, 'AP_FIXTURE_C6_001', 8264, 1001, 3601, 9201, 'team',
    96601, '2026-04-22', 'AM', NULL, 'booked', 'team-record-c6-001',
    'fixture', NOW(), 'fixture', NOW(), 'C6 appointment 1', '0'
),
(
    96712, 'AP_FIXTURE_C6_002', 8264, 1002, 3602, 9201, 'team',
    96601, '2026-04-22', 'AM', NULL, 'booked', 'team-record-c6-002',
    'fixture', NOW(), 'fixture', NOW(), 'C6 appointment 2', '0'
),
(
    96713, 'AP_FIXTURE_C6_003', 8264, 1005, 3603, 9201, 'team',
    96601, '2026-04-22', 'AM', NULL, 'booked', 'team-record-c6-003',
    'fixture', NOW(), 'fixture', NOW(), 'C6 appointment 3', '0'
),
(
    96714, 'AP_FIXTURE_C6_004', 8264, NULL, 3604, 9201, 'team',
    96601, '2026-04-22', 'AM', NULL, 'booked', 'team-record-c6-004',
    'fixture', NOW(), 'fixture', NOW(), 'C6 appointment 4', '0'
),
(
    96715, 'AP_FIXTURE_C6_005', 8264, NULL, 3605, 9201, 'team',
    96601, '2026-04-22', 'AM', NULL, 'booked', 'team-record-c6-005',
    'fixture', NOW(), 'fixture', NOW(), 'C6 appointment 5', '0'
),
(
    96721, 'AP_FIXTURE_C6_021', 8263, 1002, 3602, 9201, 'individual',
    NULL, '2026-04-23', 'PM', NULL, 'booked', 'ind-record-c6-021',
    'fixture', NOW(), 'fixture', NOW(), 'C6 unauthorized partner scope', '0'
);

INSERT INTO jst_appointment_writeoff_item (
    writeoff_item_id, appointment_id, team_appointment_id, item_type, item_name, qr_code, status,
    writeoff_time, writeoff_user_id, writeoff_terminal, writeoff_qty, total_qty,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    96801, 96711, 96601, 'arrival', '到场核销',
    'c6scanqr000000000000000000000001.plKyFdbghzOOuKbWw3rk7T2NPaciNDTYvebj7vSKuBY',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C6 scan code 1', '0'
),
(
    96802, 96712, 96601, 'arrival', '到场核销',
    'c6scanqr000000000000000000000002.zTXd3knuzMrCVqnxlo0XOltp5S182w3D6zytAsOnmko',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C6 scan code 2', '0'
),
(
    96803, 96713, 96601, 'arrival', '到场核销',
    'c6scanqr000000000000000000000003.IOxd8Qx-95ZmlOtMwK6KI0bbV4Gi5HIyhP8xvjP0hf0',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C6 scan code 3', '0'
),
(
    96804, 96714, 96601, 'arrival', '到场核销',
    'c6scanqr000000000000000000000004.NGG5KxKelpKb80GJg-Jhs8AfRvVB4i5auj3Rl7uehOA',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C6 scan code 4', '0'
),
(
    96805, 96715, 96601, 'arrival', '到场核销',
    'c6scanqr000000000000000000000005.po-T26f0H-AGPIVt74Poa5XxgsogBZ8PwzXkHFHdibw',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C6 scan code 5', '0'
),
(
    96821, 96721, NULL, 'arrival', '到场核销',
    'c6unauthqr0000000000000000000001.v---NwkwMPnJHC_aHJzn6lmBScxQ-EVDwHpMlT32UQE',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C6 unauthorized scan code', '0'
);

-- =====================================================================
-- C7 individual appointment fixtures
-- Covers:
--   1. free / paid personal appointment
--   2. allow_repeat=0 / allow_repeat=1
--   3. capacity full / last seat race
--   4. cancel blocked after used writeoff item
-- =====================================================================

INSERT INTO jst_contest (
    contest_id, contest_name, source_type, partner_id, category, group_levels, cover_image, description,
    enroll_start_time, enroll_end_time, event_start_time, event_end_time, price,
    support_channel_enroll, support_points_deduct, support_appointment,
    appointment_capacity, writeoff_config, allow_repeat_appointment,
    cert_rule_json, score_rule_json, form_template_id, aftersale_days, audit_status, status,
    created_user_id, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    8271, '测试_C7_免费个人预约赛事', 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c7-8271.jpg', 'C7 免费个人预约',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-30 18:00:00', 0.00,
    1, 0, 1,
    3,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销'),
        JSON_OBJECT('itemType', 'gift', 'itemName', '礼品核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C7 free appointment contest', '0'
),
(
    8272, '测试_C7_付费个人预约赛事', 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c7-8272.jpg', 'C7 付费个人预约',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-30 18:00:00', 59.90,
    1, 0, 1,
    5,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C7 paid appointment contest', '0'
),
(
    8273, '测试_C7_允许重复预约赛事', 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c7-8273.jpg', 'C7 allow repeat',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-30 18:00:00', 0.00,
    1, 0, 1,
    5,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    1,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C7 repeat appointment contest', '0'
),
(
    8274, '测试_C7_容量已满赛事', 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c7-8274.jpg', 'C7 capacity full',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-30 18:00:00', 0.00,
    1, 0, 1,
    1,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C7 capacity full contest', '0'
),
(
    8275, '测试_C7_已核销不可取消赛事', 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c7-8275.jpg', 'C7 used item cancel fail',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-30 18:00:00', 0.00,
    1, 0, 1,
    5,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C7 used item contest', '0'
),
(
    8276, '测试_C7_最后一个名额赛�?, 'partner', 8101, 'art', '小学�?, 'https://fixture.example.com/c7-8276.jpg', 'C7 last seat',
    '2026-04-01 09:00:00', '2026-05-31 18:00:00', '2026-04-20 09:00:00', '2026-04-30 18:00:00', 0.00,
    1, 0, 1,
    1,
    JSON_ARRAY(
        JSON_OBJECT('itemType', 'arrival', 'itemName', '到场核销')
    ),
    0,
    '{}', '{}', NULL, 7, 'online', 'enrolling',
    9101, 'fixture', NOW(), 'fixture', NOW(), 'C7 last seat contest', '0'
);

INSERT INTO jst_appointment_record (
    appointment_id, appointment_no, contest_id, user_id, participant_id, channel_id, appointment_type,
    team_appointment_id, appointment_date, session_code, order_id, main_status, qr_code,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    96741, 'AP_FIXTURE_C7_041', 8274, 1002, 3602, NULL, 'individual',
    NULL, '2026-04-24', 'AM', NULL, 'booked', 'c7-full-record-001',
    'fixture', NOW(), 'fixture', NOW(), 'C7 capacity full existing record', '0'
),
(
    96751, 'AP_FIXTURE_C7_051', 8275, 1001, 3601, NULL, 'individual',
    NULL, '2026-04-25', 'PM', NULL, 'partial_writeoff', 'c7-used-record-001',
    'fixture', NOW(), 'fixture', NOW(), 'C7 used item cancel fail', '0'
);

INSERT INTO jst_appointment_writeoff_item (
    writeoff_item_id, appointment_id, team_appointment_id, item_type, item_name, qr_code, status,
    writeoff_time, writeoff_user_id, writeoff_terminal, writeoff_qty, total_qty,
    create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    96841, 96741, NULL, 'arrival', '到场核销',
    'c7capfullqr0000000000000000000001.U0f9nV1eS_2mvC8sZ2W5jV7gXo0trr7JQm5ih4bC0x8',
    'unused', NULL, NULL, NULL, 0, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C7 capacity full code', '0'
),
(
    96851, 96751, NULL, 'arrival', '到场核销',
    'c7usedqr000000000000000000000001.1_9UQ3oL5wlnyCYM6cV4LBVvEAV9PRG9Q-NKq4N8g4Y',
    'used', NOW(), 9101, 'fixture_scan', 1, 1,
    'fixture', NOW(), 'fixture', NOW(), 'C7 used item code', '0'
);

-- =====================================================================
-- C8 mall exchange fixtures
-- Covers:
--   1. virtual coupon / rights goods
--   2. physical goods with cash supplement and address snapshot
--   3. stock insufficient / role mismatch / points insufficient scenarios
-- =====================================================================

INSERT INTO jst_rights_template (
    rights_template_id, rights_name, rights_type, quota_mode, quota_value, valid_days,
    writeoff_mode, applicable_role, status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES (
    9751, '测试_C8_报名抵扣权益', 'enroll_deduct', 'times', 3.00, 180,
    'online_auto', 'student', 1, 'fixture', NOW(), 'fixture', NOW(), 'C8 rights template', '0'
);

INSERT INTO jst_mall_goods (
    goods_id, goods_name, goods_type, cover_image, description, points_price, cash_price,
    stock, stock_warning, role_limit, status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    99101, '测试_C8_20元优惠券', 'virtual', 'https://fixture.example.com/mall/c8-coupon.jpg',
    'C8 虚拟优惠券商�?, 3000, 0.00,
    -1, 0, 'both', 'on', 'fixture', NOW(), 'fixture', NOW(),
    JSON_OBJECT('virtualTargetType', 'coupon', 'couponTemplateId', 9701), '0'
),
(
    99102, '测试_C8_报名权益�?, 'virtual', 'https://fixture.example.com/mall/c8-rights.jpg',
    'C8 虚拟权益商品', 4000, 0.00,
    -1, 0, 'both', 'on', 'fixture', NOW(), 'fixture', NOW(),
    JSON_OBJECT('virtualTargetType', 'rights', 'rightsTemplateId', 9751), '0'
),
(
    99103, '测试_C8_实体礼品', 'physical', 'https://fixture.example.com/mall/c8-physical.jpg',
    'C8 实体礼品，学生专属，支持现金补差', 2000, 19.90,
    5, 1, 'student', 'on', 'fixture', NOW(), 'fixture', NOW(),
    NULL, '0'
),
(
    99104, '测试_C8_最�?件礼�?, 'physical', 'https://fixture.example.com/mall/c8-last-stock.jpg',
    'C8 最后一件库存场�?, 500, 0.00,
    1, 1, 'both', 'on', 'fixture', NOW(), 'fixture', NOW(),
    NULL, '0'
);

-- =====================================================================
-- F-MARKETING-RIGHTS-BE fixtures
-- Tables: jst_coupon_template / jst_user_coupon / jst_coupon_issue_batch /
--         jst_rights_template / jst_user_rights
-- User: 9001 (from points-center fixtures)
-- =====================================================================
DELETE FROM jst_rights_writeoff_record WHERE user_rights_id IN (9871, 9872);
DELETE FROM jst_user_rights WHERE user_rights_id IN (9871, 9872) OR rights_template_id IN (9771, 9772);
DELETE FROM jst_user_coupon WHERE user_coupon_id IN (9861, 9862) OR coupon_template_id IN (9761, 9762, 9763);
DELETE FROM jst_coupon_issue_batch WHERE batch_id BETWEEN 98650 AND 98699;
DELETE FROM jst_rights_template WHERE rights_template_id IN (9771, 9772);
DELETE FROM jst_coupon_template WHERE coupon_template_id IN (9761, 9762, 9763);

INSERT INTO jst_coupon_template (
    coupon_template_id, coupon_name, coupon_type, face_value, discount_rate, threshold_amount,
    applicable_role, applicable_contest_ids, valid_days, valid_start, valid_end, stack_rule,
    status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9761, '����_FMR_��50��10', 'full_reduce', 10.00, NULL, 50.00,
    'student', '8201', 30, NULL, NULL, 'once',
    1, 'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ����ȯ', '0'
),
(
    9762, '����_FMR_9��ȯ', 'discount', NULL, 0.90, 0.00,
    'student', '8201', 30, NULL, NULL, 'once',
    1, 'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING �ۿ�ȯ', '0'
),
(
    9763, '����_FMR_ֱ��20', 'direct_reduce', 20.00, NULL, 0.00,
    'student', '8201', 30, NULL, NULL, 'once',
    1, 'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ������ȡȯ', '0'
);

INSERT INTO jst_user_coupon (
    user_coupon_id, coupon_template_id, user_id, issue_batch_no, issue_source, status,
    valid_start, valid_end, order_id, use_time, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9861, 9761, 9001, 'FMR_FIXTURE_CP_1', 'platform', 'unused',
    NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL,
    'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ��������ȯ', '0'
),
(
    9862, 9762, 9001, 'FMR_FIXTURE_CP_2', 'platform', 'unused',
    NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, NULL,
    'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING �����ۿ�ȯ', '0'
);

INSERT INTO jst_rights_template (
    rights_template_id, rights_name, rights_type, quota_mode, quota_value, valid_days,
    writeoff_mode, applicable_role, status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9771, '����_FMR_�γ�ѧϰȨ��', 'course', 'count', 1.00, 30,
    'self_apply', 'student', 1, 'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ��������Ȩ��', '0'
),
(
    9772, '����_FMR_���»Ȩ��', 'offline_activity', 'count', 1.00, 30,
    'scan', 'student', 1, 'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ɨ�����Ȩ��', '0'
);

INSERT INTO jst_user_rights (
    user_rights_id, rights_template_id, owner_type, owner_id, source_type, source_ref_id,
    remain_quota, valid_start, valid_end, status, create_by, create_time, update_by, update_time, remark, del_flag
) VALUES
(
    9871, 9771, 'student', 9001, 'manual', 9771,
    1.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'available',
    'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ����������Ȩ��', '0'
),
(
    9872, 9772, 'student', 9001, 'manual', 9772,
    1.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'available',
    'fixture', NOW(), 'fixture', NOW(), 'F-MARKETING ɨ�����Ȩ��', '0'
);

