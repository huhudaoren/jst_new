-- =====================================================================
-- 文件名：99-test-fixtures.sql
-- 用途  ：开发/测试环境的测试数据 (FOR DEV/TEST ONLY, 禁止生产执行!)
-- 适用  ：MySQL 5.7
-- 强制规约：参见 架构设计/25-从0到1开发流程.md §3.8
--   - 所有测试数据必须从本文件入库,禁止前端硬编码 mock
--   - 测试用户 mobile 前缀 13800000xxx
--   - 测试人员姓名前缀 测试_
--   - 每个 feature 完成后由开发追加自己的 fixture
-- =====================================================================
SET NAMES utf8mb4;

-- =====================================================================
-- jst-user 模块测试数据 (含临时档案认领样板 feature)
-- =====================================================================

-- 1. 测试正式用户(注意:sys_user 由若依默认提供 admin,这里写业务表 jst_user)
INSERT INTO jst_user (user_id, openid, mobile, nickname, real_name, user_type, status, register_time, create_by, create_time)
VALUES
(1001, 'openid_test_1001', '13800000001', '测试_张妈妈', '测试_张妈妈', 'parent', 1, NOW(), 'fixture', NOW()),
(1002, 'openid_test_1002', '13800000002', '测试_李爸爸', '测试_李爸爸', 'parent', 1, NOW(), 'fixture', NOW()),
(1003, 'openid_test_1003', '13800000003', '测试_王老师', '测试_王老师', 'channel',1, NOW(), 'fixture', NOW()),
(1004, 'openid_test_1004', '13800000004', '测试_刘老师', '测试_刘老师', 'channel',1, NOW(), 'fixture', NOW());

-- 2. 测试渠道方
INSERT INTO jst_channel (channel_id, user_id, channel_type, channel_name, contact_mobile, auth_status, status, create_by, create_time)
VALUES
(2001, 1003, 'teacher', '测试_艺术工作室', '13800000003', 'approved', 1, 'fixture', NOW()),
(2002, 1004, 'organization', '测试_星光美育中心', '13800000004', 'approved', 1, 'fixture', NOW());

-- 2.1 学生初始 active 绑定（F4 学生渠道绑定）
INSERT INTO jst_student_channel_binding (binding_id, user_id, channel_id, bind_time, status, operator_id, create_by, create_time)
VALUES
(5001, 1001, 2001, NOW(), 'active', 1001, 'fixture', NOW());

UPDATE jst_user
SET bound_channel_id = 2001,
    update_by = 'fixture',
    update_time = NOW()
WHERE user_id = 1001
  AND create_by = 'fixture';

-- 3. 测试临时参赛档案 (覆盖样板 feature 的 4 种场景)

-- 3.1 场景一:唯一精确命中 (mobile=13800000001 + name=测试_张小明) → 应自动认领到 user 1001
INSERT INTO jst_participant (participant_id, user_id, participant_type, name, gender, age,
                              guardian_name, guardian_mobile, school, created_by_channel_id,
                              claim_status, visible_scope, create_by, create_time)
VALUES
(3001, NULL, 'temporary_participant', '测试_张小明', 1, 8,
       '测试_张妈妈', '13800000001', '测试_第一小学', 2001,
       'unclaimed', 'channel_only', 'fixture', NOW());

-- 3.2 场景二:多候选 (同 mobile 不同姓名) → 应进入 pending_manual
INSERT INTO jst_participant (participant_id, user_id, participant_type, name, gender, age,
                              guardian_name, guardian_mobile, school, created_by_channel_id,
                              claim_status, visible_scope, create_by, create_time)
VALUES
(3002, NULL, 'temporary_participant', '测试_李大宝', 1, 7,
       '测试_李爸爸', '13800000002', '测试_第二小学', 2001,
       'unclaimed', 'channel_only', 'fixture', NOW()),
(3003, NULL, 'temporary_participant', '测试_李二宝', 2, 5,
       '测试_李爸爸', '13800000002', '测试_第二幼儿园', 2001,
       'unclaimed', 'channel_only', 'fixture', NOW());

-- 3.3 场景三:已认领状态(用于测试撤销)
INSERT INTO jst_participant (participant_id, user_id, participant_type, name, gender, age,
                              guardian_name, guardian_mobile, school, created_by_channel_id,
                              claim_status, claimed_user_id, claimed_time, visible_scope, create_by, create_time)
VALUES
(3004, 1001, 'temporary_participant', '测试_张小红', 2, 9,
       '测试_张妈妈', '13800000001', '测试_第一小学', 2001,
       'auto_claimed', 1001, NOW(), 'channel_only', 'fixture', NOW());

INSERT INTO jst_participant_user_map (map_id, participant_id, user_id, claim_method, claim_time, status, create_by, create_time)
VALUES
(4001, 3004, 1001, 'auto_phone_name', NOW(), 'active', 'fixture', NOW());

-- 3.4 场景四:无任何匹配(用于测试 null 返回)
-- (无需插入,只要 mobile=13800009999 不存在即可)

-- =====================================================================
-- 后续模块测试数据 (其他 feature 按本格式追加,标注 feature 名 + 涉及表)
-- =====================================================================

-- =====================================================================
-- F5 jst-organizer 赛事方入驻审核测试数据
-- 涉及表：jst_event_partner_apply
-- =====================================================================

INSERT INTO jst_event_partner_apply (
    apply_id, apply_no, partner_id, partner_name, contact_name, contact_mobile,
    business_license_no, qualification_json, settlement_info_json, invoice_info_json,
    contract_files_json, apply_status, supplement_remark, audit_remark, audit_user_id,
    submit_time, create_by, create_time
) VALUES (
    5001, 'PA_FIXTURE_5001', NULL, '测试_待审核赛事机构', '测试_赛事方联系人', '13800005556',
    '91110000FIXTURE01',
    JSON_ARRAY(JSON_OBJECT('fileName', 'license.pdf', 'url', 'https://fixture.example.com/license.pdf')),
    JSON_OBJECT('accountName', '测试_待审核赛事机构', 'bankAccountNo', '6222020000005001', 'bankName', '测试银行'),
    JSON_OBJECT('invoiceType', 'normal', 'taxNo', '91110000FIXTURE01'),
    JSON_ARRAY(JSON_OBJECT('fileName', 'contract.pdf', 'url', 'https://fixture.example.com/contract.pdf')),
    'pending', NULL, NULL, NULL,
    NOW(), 'fixture', NOW()
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
(7001, '测试_首页置顶公告', 'platform', '<p>这是首页 banner 公告内容，用于测试首页轮播与公告详情展示。</p>',
 'https://fixture.example.com/notice/banner-7001.png', 1, 'published', DATE_SUB(NOW(), INTERVAL 1 DAY),
 'fixture', NOW(), 'fixture', NOW(), 'F-NOTICE fixture', '0'),
(7002, '测试_赛事公告', 'contest', '<p>这是赛事分类公告，用于测试公告列表与摘要截取逻辑。</p>',
 'https://fixture.example.com/notice/banner-7002.png', 0, 'published', DATE_SUB(NOW(), INTERVAL 2 DAY),
 'fixture', NOW(), 'fixture', NOW(), 'F-NOTICE fixture', '0'),
(7003, '测试_积分公告', 'points', '<p>这是积分分类公告，用于测试公告列表排序与详情接口。</p>',
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
    7201, '测试_素描启蒙课', 'normal', 'https://fixture.example.com/course-normal.jpg',
    '测试_normal_课程简介，用于小程序课程列表、详情和我的课程场景联调。', 199.00, 19900,
    'platform', 1, NULL, 'approved', 'on', 'all',
    'fixture', NOW(), 'fixture', NOW(), 'F-COURSE normal fixture', '0'
),
(
    7202, '测试_AI美育体验课', 'ai_maic', 'https://fixture.example.com/course-ai.jpg',
    '测试_ai_maic_课程简介，用于课程列表过滤和详情展示。', 299.00, 29900,
    'platform', 1, 'maic_fixture_7202', 'approved', 'on', 'all',
    'fixture', NOW(), 'fixture', NOW(), 'F-COURSE ai_maic fixture', '0'
),
(
    7203, '测试_待审核课程', 'normal', 'https://fixture.example.com/course-draft.jpg',
    '测试_draft_课程简介，用于提审和未审核禁止上架场景。', 99.00, 9900,
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
-- F7 jst-event 赛事 CRUD 与查询测试数据
-- 涉及表：sys_user / sys_user_role / jst_event_partner / jst_contest
-- =====================================================================

-- F7-0 两个赛事方账号（登录密码均为 admin123）
INSERT INTO sys_user (
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
(8101, 9101, '测试_赛事机构A', '测试_A联系人', '13800007771', 'approved', NOW(), 1, 'active', 'fixture', NOW(), '0'),
(8102, 9102, '测试_赛事机构B', '测试_B联系人', '13800007772', 'approved', NOW(), 1, 'active', 'fixture', NOW(), '0');

-- F7-1 ~ F7-7 预置 5 张赛事（覆盖 draft/pending/approved/online/offline + 多分类）
INSERT INTO jst_contest (
    contest_id, contest_name, source_type, partner_id, category, group_levels, cover_image, description,
    enroll_start_time, enroll_end_time, event_start_time, event_end_time, price,
    support_channel_enroll, support_points_deduct, support_appointment,
    cert_rule_json, score_rule_json, form_template_id, aftersale_days,
    audit_status, status, created_user_id, create_by, create_time, del_flag
) VALUES
(8201, '测试_F7_在线赛事', 'partner', 8101, 'art', '小学组,初中组', 'https://fixture.example.com/f7-online.jpg', '测试_F7_在线赛事说明',
 '2026-04-01 09:00:00', '2026-05-01 18:00:00', '2026-05-10 09:00:00', '2026-05-12 18:00:00', 99.00,
 1, 1, 0, '{}', '{}', NULL, 7, 'online', 'enrolling', 9101, 'fixture', NOW(), '0'),
(8202, '测试_F7_已下线赛事', 'partner', 8101, 'music', '幼儿组', 'https://fixture.example.com/f7-offline.jpg', '测试_F7_已下线赛事说明',
 '2026-03-01 09:00:00', '2026-03-31 18:00:00', '2026-04-10 09:00:00', '2026-04-12 18:00:00', 59.00,
 1, 0, 0, '{}', '{}', NULL, 7, 'offline', 'closed', 9101, 'fixture', NOW(), '0'),
(8203, '测试_F7_待上线赛事', 'partner', 8101, 'dance', '高中组', 'https://fixture.example.com/f7-approved.jpg', '测试_F7_待上线赛事说明',
 '2026-06-01 09:00:00', '2026-06-20 18:00:00', '2026-06-25 09:00:00', '2026-06-26 18:00:00', 129.00,
 1, 1, 1, '{}', '{}', NULL, 7, 'approved', 'not_started', 9101, 'fixture', NOW(), '0'),
(8204, '测试_F7_待审核赛事', 'partner', 8102, 'painting', '小学组', 'https://fixture.example.com/f7-pending.jpg', '测试_F7_待审核赛事说明',
 '2026-06-01 09:00:00', '2026-06-20 18:00:00', '2026-06-25 09:00:00', '2026-06-26 18:00:00', 79.00,
 1, 0, 0, '{}', '{}', NULL, 7, 'pending', 'not_started', 9102, 'fixture', NOW(), '0'),
(8205, '测试_F7_草稿赛事', 'partner', 8102, 'tech', '初中组', 'https://fixture.example.com/f7-draft.jpg', '测试_F7_草稿赛事说明',
 '2026-06-01 09:00:00', '2026-06-20 18:00:00', '2026-06-25 09:00:00', '2026-06-26 18:00:00', 0.00,
 0, 0, 0, '{}', '{}', NULL, 7, 'draft', 'not_started', 9102, 'fixture', NOW(), '0');

-- =====================================================================
-- F8 jst-event 动态表单模板测试数据
-- 涉及表：jst_enroll_form_template / jst_contest
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
    8802, '测试_F8_在线赛事', 'platform', NULL, '艺术', '小学组', NULL, '测试_F8_动态表单模板用在线赛事',
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 11 DAY), 0.00,
    1, 0, 0,
    NULL, NULL, 8801, 7,
    'online', 'enrolling', NULL, 'fixture', NOW(), 'fixture', NOW(), NULL, '0'
);

-- TODO jst-event 报名场景测试数据 (待 jst-event feature 完成后追加)
-- TODO jst-order 订单/退款场景测试数据
-- TODO jst-channel 返点台账正向/负向场景
-- TODO jst-points 积分账户余额/冻结场景
-- TODO jst-order 团队预约部分核销场景

-- =====================================================================
-- 清理脚本 (用于重跑测试前清空)
-- 手动执行: 仅删除 fixture 数据,不影响真实业务
-- =====================================================================
-- DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
-- DELETE FROM jst_participant WHERE create_by = 'fixture';
-- DELETE FROM jst_channel WHERE create_by = 'fixture';
-- DELETE FROM jst_user WHERE create_by = 'fixture';
-- DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
-- DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';
-- DELETE FROM jst_notice WHERE create_by = 'fixture';
-- DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
-- DELETE FROM jst_course WHERE create_by = 'fixture';
-- DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';
-- DELETE FROM jst_contest WHERE create_by = 'fixture';
-- DELETE FROM jst_event_partner WHERE create_by = 'fixture';
-- DELETE FROM sys_user_role WHERE user_id IN (9101, 9102);
-- DELETE FROM sys_user WHERE create_by = 'fixture' AND user_id IN (9101, 9102);
-- -- Step 1.1: 清理旧 fixture (避免 PK 冲突)
-- DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
-- DELETE FROM jst_participant WHERE create_by = 'fixture';
-- DELETE FROM jst_student_channel_binding WHERE create_by = 'fixture';
-- DELETE FROM jst_channel WHERE create_by = 'fixture';
-- DELETE FROM jst_user WHERE create_by = 'fixture';
-- -- 其他可能的 fixture 表(F-NOTICE/F-COURSE/F7/F8 新增的)
-- DELETE FROM jst_notice WHERE create_by = 'fixture';
-- DELETE FROM jst_course WHERE create_by = 'fixture';
-- DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
-- DELETE FROM jst_contest WHERE create_by = 'fixture';
-- DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
-- DELETE FROM jst_event_partner WHERE create_by = 'fixture';
-- DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';
-- DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';
-- -- 1.1 清理所有 fixture 数据(按依赖顺序)
-- DELETE FROM sys_user_role WHERE user_id IN (SELECT user_id FROM sys_user WHERE user_name LIKE
--                                                                                'partner_%' OR user_name LIKE 'wx_%');
-- DELETE FROM sys_user WHERE user_name LIKE 'partner_%' OR user_name LIKE 'wx_%';
-- DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
-- DELETE FROM jst_participant WHERE create_by = 'fixture';
-- DELETE FROM jst_student_channel_binding WHERE create_by = 'fixture';
-- DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
-- DELETE FROM jst_event_partner WHERE create_by = 'fixture';
-- DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';
-- DELETE FROM jst_channel WHERE create_by = 'fixture';
-- DELETE FROM jst_user WHERE create_by = 'fixture' OR openid LIKE 'openid_test_%';
-- DELETE FROM jst_notice WHERE create_by = 'fixture';
-- DELETE FROM jst_course WHERE create_by = 'fixture';
-- DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
-- DELETE FROM jst_contest WHERE create_by = 'fixture';
-- DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';

-- =====================================================================
-- F9 jst-event 报名记录与表单快照测试数据
-- 涉及表：jst_contest / jst_enroll_record
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
        'formData', JSON_OBJECT('name', '测试_张小明', 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
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
        'formData', JSON_OBJECT('name', '测试_张小红', 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
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
        'formData', JSON_OBJECT('name', '测试_张小红', 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8903-old.pdf')
    ),
    NULL, 'supplement', 'supplement',
    '请补充最新作品附件', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'F9 supplement fixture', '0'
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
    9702, '测试_C2_失效券', 'direct_reduce', 10.00, NULL, 0.00,
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
        'formData', JSON_OBJECT('name', '测试_张小明', 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
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
        'formData', JSON_OBJECT('name', '测试_张小红', 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
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
        'formData', JSON_OBJECT('name', '测试_张小明', 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
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
        'formData', JSON_OBJECT('name', '测试_张小红', 'gender', 'female', 'age', 9, 'school', '测试_第一小学'),
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
        'formData', JSON_OBJECT('name', '测试_张小明', 'gender', 'male', 'age', 8, 'school', '测试_第一小学'),
        'attachments', JSON_ARRAY('https://fixture.example.com/enroll/work-8908.pdf')
    ),
    NULL, 'submitted', 'approved',
    '审核通过', NOW(), 'fixture', NOW(), 'fixture', NOW(), 'C2 approved fixture paid', '0'
);

-- C2 cleanup helper
-- DELETE FROM jst_rebate_ledger WHERE create_by = 'fixture';
-- DELETE FROM jst_payment_record WHERE create_by = 'fixture';
-- DELETE FROM jst_order_item WHERE create_by = 'fixture';
-- DELETE FROM jst_order_main WHERE create_by = 'fixture';
