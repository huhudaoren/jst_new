-- =====================================================================
-- 文件名：97-migration-baseline-roles.sql
-- 用途  ：为早于修正版 23-基础数据初始化.sql 的环境补齐基线角色
-- 说明  ：
--   1. 全新数据库直接执行 23-基础数据初始化.sql 即可，无需再跑本脚本
--   2. 本脚本只补缺失角色，不覆盖现有 role_name / 自定义配置
--   3. 若目标环境已存在同 role_id 但 role_key 不一致的历史数据，请人工评估
--   4. 执行后建议手工检查 sys_role_menu 中各 role_id 的映射是否仍符合当前业务
-- 验证  ：SELECT role_id, role_key, role_name FROM sys_role WHERE role_key LIKE 'jst_%' ORDER BY role_id;
-- 适用  ：MySQL 5.7
-- =====================================================================
SET NAMES utf8mb4;

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
    status, del_flag, create_by, create_time, remark
)
SELECT
    100, '学生账号', 'jst_student', 10, '1', '1', '1',
    '0', '0', 'admin', NOW(), '基线补齐：小程序学生/家长默认业务角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'jst_student'
)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_id = 100
);

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
    status, del_flag, create_by, create_time, remark
)
SELECT
    101, '赛事方账号', 'jst_partner', 20, '1', '1', '1',
    '0', '0', 'admin', NOW(), '基线补齐：赛事方业务角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'jst_partner'
)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_id = 101
);

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
    status, del_flag, create_by, create_time, remark
)
SELECT
    102, '渠道方账号', 'jst_channel', 30, '1', '1', '1',
    '0', '0', 'admin', NOW(), '基线补齐：渠道方业务角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'jst_channel'
)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_id = 102
);

INSERT INTO sys_role (
    role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
    status, del_flag, create_by, create_time, remark
)
SELECT
    103, '平台运营', 'jst_platform_op', 40, '1', '1', '1',
    '0', '0', 'admin', NOW(), '基线补齐：平台运营角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'jst_platform_op'
)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_id = 103
);
