-- =====================================================================
-- 文件名：90-reset-fixtures.sql
-- 用途  : 一键清理 + 重导 fixture 测试数据
-- 适用  : DEV/TEST,每次跑 .http 测试前先 source 一次,保证测试可重复
-- 用法  : MySQL: source D:/coding/jst_v1/架构设计/ddl/90-reset-fixtures.sql;
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- Part 1: 清理所有 fixture 数据 (按依赖顺序倒序)
-- =====================================================================

-- 1.1 子 Agent F5/F6 通过 approve 创建的 sys_user + 角色映射
DELETE FROM sys_user_role
WHERE user_id IN (SELECT user_id FROM sys_user WHERE user_name LIKE 'partner_%' OR user_name LIKE 'wx_%');
DELETE FROM sys_user
WHERE user_name LIKE 'partner_%' OR user_name LIKE 'wx_%';

-- 1.2 订单 / 支付 / 退款 / 返点台账
DELETE FROM jst_payment_record WHERE create_by = 'fixture';
DELETE FROM jst_rebate_ledger WHERE create_by = 'fixture';
DELETE FROM jst_order_item WHERE create_by = 'fixture';
DELETE FROM jst_order_main WHERE create_by = 'fixture';

-- 1.3 报名记录
DELETE FROM jst_enroll_record WHERE create_by = 'fixture';
DELETE FROM jst_enroll_form_template WHERE create_by = 'fixture';

-- 1.4 优惠券 / 积分
DELETE FROM jst_user_coupon WHERE create_by = 'fixture';
DELETE FROM jst_coupon_template WHERE create_by = 'fixture';
DELETE FROM jst_points_ledger WHERE create_by = 'fixture';
DELETE FROM jst_points_account WHERE create_by = 'fixture';

-- 1.5 课程
DELETE FROM jst_course_learn_record WHERE create_by = 'fixture';
DELETE FROM jst_course WHERE create_by = 'fixture';

-- 1.6 赛事 / 赛事方
DELETE FROM jst_contest WHERE create_by = 'fixture';
DELETE FROM jst_event_partner_apply WHERE create_by = 'fixture';
DELETE FROM jst_event_partner WHERE create_by = 'fixture';

-- 1.7 渠道认证
DELETE FROM jst_channel_auth_apply WHERE create_by = 'fixture';

-- 1.8 公告 / 字典
DELETE FROM jst_notice WHERE create_by = 'fixture';

-- 1.9 临时档案 / 用户绑定 / 渠道方 / 用户
DELETE FROM jst_participant_user_map WHERE create_by = 'fixture';
DELETE FROM jst_participant WHERE create_by = 'fixture';
DELETE FROM jst_student_channel_binding WHERE create_by = 'fixture';
DELETE FROM jst_channel WHERE create_by = 'fixture';
DELETE FROM jst_user WHERE create_by = 'fixture' OR openid LIKE 'openid_test_%';

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- Part 2: 重导 99 全部 fixture
-- =====================================================================
SOURCE D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql;

-- =====================================================================
-- Part 3: 二次确认关键 fixture 已就位
-- =====================================================================
SELECT '✅ jst_user fixture 数量' AS metric, COUNT(*) AS value FROM jst_user WHERE create_by = 'fixture'
UNION ALL SELECT '✅ jst_channel', COUNT(*) FROM jst_channel WHERE create_by = 'fixture'
UNION ALL SELECT '✅ jst_participant', COUNT(*) FROM jst_participant WHERE create_by = 'fixture'
UNION ALL SELECT '✅ jst_contest', COUNT(*) FROM jst_contest WHERE create_by = 'fixture'
UNION ALL SELECT '✅ jst_enroll_record', COUNT(*) FROM jst_enroll_record WHERE create_by = 'fixture'
UNION ALL SELECT '✅ jst_course', COUNT(*) FROM jst_course WHERE create_by = 'fixture'
UNION ALL SELECT '✅ jst_notice', COUNT(*) FROM jst_notice WHERE create_by = 'fixture';

-- =====================================================================
-- 完成。现在可以跑 wx-tests.http / admin-tests.http
-- =====================================================================
