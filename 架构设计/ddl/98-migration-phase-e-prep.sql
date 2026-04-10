-- =====================================================================
-- 文件名：98-migration-phase-e-prep.sql
-- 用途  ：阶段 E 后端预备 DDL 迁移（幂等脚本）
-- 适用  ：MySQL 5.7
-- 关联  ：任务卡 E0-1，CCB 决策 Q-02/Q-07/Q-11
-- 执行  ：首跑 + 复跑均幂等
-- =====================================================================
SET NAMES utf8mb4;

-- =====================================================================
-- 1. Q-07: jst_contest 增加预约退款配置开关
-- =====================================================================
-- MySQL 5.7 不支持 ADD COLUMN IF NOT EXISTS，用存储过程实现幂等
DELIMITER $$
DROP PROCEDURE IF EXISTS _e0_add_col_contest_allow_refund$$
CREATE PROCEDURE _e0_add_col_contest_allow_refund()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'jst_contest'
          AND COLUMN_NAME = 'allow_appointment_refund'
    ) THEN
        ALTER TABLE jst_contest
            ADD COLUMN allow_appointment_refund TINYINT(1) NOT NULL DEFAULT 0
                COMMENT '是否允许预约退款（Q-07 配置化）：0=不允许（默认），1=允许';
    END IF;
END$$
DELIMITER ;
CALL _e0_add_col_contest_allow_refund();
DROP PROCEDURE IF EXISTS _e0_add_col_contest_allow_refund;

-- =====================================================================
-- 2. Q-02: jst_channel_auth_apply 增加驳回计数 + 锁定标记
-- =====================================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS _e0_add_col_auth_reject_count$$
CREATE PROCEDURE _e0_add_col_auth_reject_count()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'jst_channel_auth_apply'
          AND COLUMN_NAME = 'reject_count'
    ) THEN
        ALTER TABLE jst_channel_auth_apply
            ADD COLUMN reject_count INT NOT NULL DEFAULT 0
                COMMENT '累计驳回次数（Q-02 限制 3 次）';
    END IF;
END$$
DELIMITER ;
CALL _e0_add_col_auth_reject_count();
DROP PROCEDURE IF EXISTS _e0_add_col_auth_reject_count;

DELIMITER $$
DROP PROCEDURE IF EXISTS _e0_add_col_auth_locked$$
CREATE PROCEDURE _e0_add_col_auth_locked()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'jst_channel_auth_apply'
          AND COLUMN_NAME = 'locked_for_manual'
    ) THEN
        ALTER TABLE jst_channel_auth_apply
            ADD COLUMN locked_for_manual TINYINT(1) NOT NULL DEFAULT 0
                COMMENT '>=3 次驳回后锁定，需联系客服人工审核';
    END IF;
END$$
DELIMITER ;
CALL _e0_add_col_auth_locked();
DROP PROCEDURE IF EXISTS _e0_add_col_auth_locked;

-- =====================================================================
-- 3. Q-11: 新增平台审核员 / 平台财务员角色
-- =====================================================================
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '平台审核员', 'jst_platform_auditor', 20, '1', '0', '0', 'system', NOW(), 'E0-1: Q-11 平台审核员角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'jst_platform_auditor' AND del_flag = '0'
);

INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time, remark)
SELECT '平台财务员', 'jst_platform_finance', 21, '1', '0', '0', 'system', NOW(), 'E0-1: Q-11 平台财务员角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'jst_platform_finance' AND del_flag = '0'
);

-- =====================================================================
-- 验证
-- =====================================================================
SELECT 'E0-VERIFY: jst_contest.allow_appointment_refund' AS chk,
       COUNT(1) AS col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'jst_contest'
  AND COLUMN_NAME = 'allow_appointment_refund';

SELECT 'E0-VERIFY: jst_channel_auth_apply.reject_count' AS chk,
       COUNT(1) AS col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'jst_channel_auth_apply'
  AND COLUMN_NAME = 'reject_count';

SELECT 'E0-VERIFY: jst_channel_auth_apply.locked_for_manual' AS chk,
       COUNT(1) AS col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'jst_channel_auth_apply'
  AND COLUMN_NAME = 'locked_for_manual';

SELECT 'E0-VERIFY: sys_role jst_platform_auditor' AS chk,
       COUNT(1) AS role_exists
FROM sys_role WHERE role_key = 'jst_platform_auditor' AND del_flag = '0';

SELECT 'E0-VERIFY: sys_role jst_platform_finance' AS chk,
       COUNT(1) AS role_exists
FROM sys_role WHERE role_key = 'jst_platform_finance' AND del_flag = '0';

-- =====================================================================
-- 4. FIX-1: appointment defaults for partner contest create/edit
-- =====================================================================
ALTER TABLE jst_contest
    MODIFY COLUMN appointment_capacity INT NOT NULL DEFAULT 0
        COMMENT '预约容量';

ALTER TABLE jst_contest
    MODIFY COLUMN allow_repeat_appointment TINYINT(1) NOT NULL DEFAULT 0
        COMMENT '是否允许重复预约：0否 1是';
