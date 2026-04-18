-- =====================================================================
-- 文件名：99-migration-business-roles.sql
-- 用途  ：补齐 CLAUDE.md §六 宣称的 6 业务角色 + 对应的测试 sys_user 账号
-- 适用  ：MySQL 5.7，本地测试 / 测试服务器
-- 派发  ：TEST-ROUND3 前置准备
-- 关联  ：CLAUDE.md §六「角色体系（8 个）」
--
-- 说明  ：
--   - 本脚本幂等（全部 INSERT...SELECT FROM DUAL WHERE NOT EXISTS）
--   - 只补角色和 sys_user 账号，不动业务表
--   - 菜单绑定部分按 "数据看板 + 各自领域" 最小原则，不给跨域权限
--   - 密码统一为 `admin123`（bcrypt），登录用：用户名 = role_key，密码 = admin123
--
-- 验证  ：
--   SELECT role_id, role_key, role_name FROM sys_role WHERE role_key LIKE 'jst_%' ORDER BY role_id;
--   SELECT user_id, user_name, nick_name FROM sys_user WHERE user_name LIKE 'jst_%' ORDER BY user_id;
-- =====================================================================
SET NAMES utf8mb4;

-- =====================================================================
-- Part 1: 补齐 6 个业务角色
-- =====================================================================

-- 1.1 运营主管
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 110, '运营主管', 'jst_operator', 50, '1', '1', '1', '0', '0',
       'migration', NOW(), 'TEST-ROUND3: 赛事+订单+用户+渠道+公告+消息'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'jst_operator');

-- 1.2 财务专员
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 111, '财务专员', 'jst_finance', 51, '1', '1', '1', '0', '0',
       'migration', NOW(), 'TEST-ROUND3: 看板+订单+财务'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'jst_finance');

-- 1.3 客服专员
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 112, '客服专员', 'jst_support', 52, '1', '1', '1', '0', '0',
       'migration', NOW(), 'TEST-ROUND3: 看板+用户+赛事+订单+消息（只读为主）'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'jst_support');

-- 1.4 营销专员
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 113, '营销专员', 'jst_marketing', 53, '1', '1', '1', '0', '0',
       'migration', NOW(), 'TEST-ROUND3: 看板+优惠券+权益+积分+商城'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'jst_marketing');

-- 1.5 风控专员
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 114, '风控专员', 'jst_risk', 54, '1', '1', '1', '0', '0',
       'migration', NOW(), 'TEST-ROUND3: 看板+风控+用户渠道'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'jst_risk');

-- 1.6 数据分析师
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope,
    menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 115, '数据分析师', 'jst_analyst', 55, '1', '1', '1', '0', '0',
       'migration', NOW(), 'TEST-ROUND3: 看板+全部列表页只读'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'jst_analyst');

-- =====================================================================
-- Part 2: 菜单权限绑定（按 CLAUDE.md §六角色表最小原则）
-- =====================================================================

SET @role_operator  = (SELECT role_id FROM sys_role WHERE role_key = 'jst_operator'  LIMIT 1);
SET @role_finance   = (SELECT role_id FROM sys_role WHERE role_key = 'jst_finance'   LIMIT 1);
SET @role_support   = (SELECT role_id FROM sys_role WHERE role_key = 'jst_support'   LIMIT 1);
SET @role_marketing = (SELECT role_id FROM sys_role WHERE role_key = 'jst_marketing' LIMIT 1);
SET @role_risk      = (SELECT role_id FROM sys_role WHERE role_key = 'jst_risk'      LIMIT 1);
SET @role_analyst   = (SELECT role_id FROM sys_role WHERE role_key = 'jst_analyst'   LIMIT 1);

-- 看板（所有 6 角色均需）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_operator, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:dashboard:%' OR m.menu_name LIKE '%看板%';
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_finance, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:dashboard:%' OR m.menu_name LIKE '%看板%';
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_support, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:dashboard:%' OR m.menu_name LIKE '%看板%';
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_marketing, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:dashboard:%' OR m.menu_name LIKE '%看板%';
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_risk, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:dashboard:%' OR m.menu_name LIKE '%看板%';
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_analyst, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:dashboard:%' OR m.menu_name LIKE '%看板%';

-- 运营主管：赛事 + 订单 + 用户 + 渠道 + 公告 + 消息 + 赛事管理中心(9700段)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_operator, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:contest:%'
   OR m.perms LIKE 'jst:event:%'
   OR m.perms LIKE 'jst:order:%'
   OR m.perms LIKE 'jst:refund:%'
   OR m.perms LIKE 'jst:enroll:%'
   OR m.perms LIKE 'jst:user:%'
   OR m.perms LIKE 'jst:participant:%'
   OR m.perms LIKE 'jst:channel:%'
   OR m.perms LIKE 'jst:notice:%'
   OR m.perms LIKE 'jst:message:%'
   OR m.perms LIKE 'jst:course:%'
   OR m.perms LIKE 'jst:cert:%'
   OR (m.menu_id >= 9700 AND m.menu_id < 9800);

-- 财务专员：订单（只读）+ 财务全量
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_finance, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:order:%:list'
   OR m.perms LIKE 'jst:order:%:query'
   OR m.perms LIKE 'jst:finance:%'
   OR m.perms LIKE 'jst:payout:%'
   OR m.perms LIKE 'jst:settlement:%'
   OR m.perms LIKE 'jst:withdraw:%'
   OR m.perms LIKE 'jst:invoice:%'
   OR m.perms LIKE 'jst:contract:%';

-- 客服专员：用户+赛事+订单+消息（大部分只读）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_support, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:user:%:list'
   OR m.perms LIKE 'jst:user:%:query'
   OR m.perms LIKE 'jst:participant:%:list'
   OR m.perms LIKE 'jst:participant:%:query'
   OR m.perms LIKE 'jst:contest:%:list'
   OR m.perms LIKE 'jst:contest:%:query'
   OR m.perms LIKE 'jst:enroll:%:list'
   OR m.perms LIKE 'jst:enroll:%:query'
   OR m.perms LIKE 'jst:order:%:list'
   OR m.perms LIKE 'jst:order:%:query'
   OR m.perms LIKE 'jst:message:%'
   OR m.perms LIKE 'jst:notice:%:list';

-- 营销专员：优惠券+权益+积分+商城
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_marketing, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:coupon:%'
   OR m.perms LIKE 'jst:benefit:%'
   OR m.perms LIKE 'jst:rights:%'
   OR m.perms LIKE 'jst:points:%'
   OR m.perms LIKE 'jst:mall:%'
   OR m.perms LIKE 'jst:marketing:%';

-- 风控专员：风控 + 用户渠道
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_risk, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:risk:%'
   OR m.perms LIKE 'jst:user:%:list'
   OR m.perms LIKE 'jst:user:%:query'
   OR m.perms LIKE 'jst:channel:%:list'
   OR m.perms LIKE 'jst:channel:%:query';

-- 数据分析师：全部列表页只读（所有 :list / :query / :export）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @role_analyst, m.menu_id FROM sys_menu m
WHERE m.perms LIKE 'jst:%:list'
   OR m.perms LIKE 'jst:%:query'
   OR m.perms LIKE 'jst:%:export';

-- =====================================================================
-- Part 3: 创建 6 个测试用 sys_user（密码均为 admin123）
-- bcrypt hash of 'admin123' = $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
-- =====================================================================

INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
)
SELECT 9201, 'jst_operator', '测试_运营主管', '00', 'operator@test.local', '13900000001', '1', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NULL, NOW(),
       'migration', NOW(), '', NULL, 'TEST-ROUND3 运营主管账号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'jst_operator');

INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
)
SELECT 9202, 'jst_finance', '测试_财务专员', '00', 'finance@test.local', '13900000002', '1', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NULL, NOW(),
       'migration', NOW(), '', NULL, 'TEST-ROUND3 财务专员账号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'jst_finance');

INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
)
SELECT 9203, 'jst_support', '测试_客服专员', '00', 'support@test.local', '13900000003', '1', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NULL, NOW(),
       'migration', NOW(), '', NULL, 'TEST-ROUND3 客服专员账号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'jst_support');

INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
)
SELECT 9204, 'jst_marketing', '测试_营销专员', '00', 'marketing@test.local', '13900000004', '1', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NULL, NOW(),
       'migration', NOW(), '', NULL, 'TEST-ROUND3 营销专员账号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'jst_marketing');

INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
)
SELECT 9205, 'jst_risk', '测试_风控专员', '00', 'risk@test.local', '13900000005', '1', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NULL, NOW(),
       'migration', NOW(), '', NULL, 'TEST-ROUND3 风控专员账号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'jst_risk');

INSERT INTO sys_user (
    user_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar,
    password, status, del_flag, login_ip, login_date, pwd_update_date,
    create_by, create_time, update_by, update_time, remark
)
SELECT 9206, 'jst_analyst', '测试_数据分析师', '00', 'analyst@test.local', '13900000006', '1', '',
       '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NULL, NOW(),
       'migration', NOW(), '', NULL, 'TEST-ROUND3 数据分析师账号'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE user_name = 'jst_analyst');

-- =====================================================================
-- Part 4: 绑定 sys_user_role
-- =====================================================================

INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES
(9201, 110),  -- operator
(9202, 111),  -- finance
(9203, 112),  -- support
(9204, 113),  -- marketing
(9205, 114),  -- risk
(9206, 115);  -- analyst

-- =====================================================================
-- Part 5: 验证
-- =====================================================================
SELECT '✅ 业务角色 6 个' AS metric, COUNT(*) AS value
FROM sys_role
WHERE role_key IN ('jst_operator','jst_finance','jst_support','jst_marketing','jst_risk','jst_analyst');

SELECT '✅ 业务账号 6 个' AS metric, COUNT(*) AS value
FROM sys_user
WHERE user_name IN ('jst_operator','jst_finance','jst_support','jst_marketing','jst_risk','jst_analyst');

SELECT '✅ 角色绑定' AS metric, COUNT(*) AS value
FROM sys_user_role
WHERE user_id IN (9201,9202,9203,9204,9205,9206);

-- =====================================================================
-- 登录信息（给 Test Agent）
--   用户名  jst_operator   密码 admin123
--   用户名  jst_finance    密码 admin123
--   用户名  jst_support    密码 admin123
--   用户名  jst_marketing  密码 admin123
--   用户名  jst_risk       密码 admin123
--   用户名  jst_analyst    密码 admin123
--   （已有）admin          密码 admin123 （若依默认）
--   （已有）partner_f7_a   密码 admin123 （fixture）
-- =====================================================================
