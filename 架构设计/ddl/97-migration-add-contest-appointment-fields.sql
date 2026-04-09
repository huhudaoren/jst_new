-- =====================================================================
-- 文件名：97-migration-add-contest-appointment-fields.sql
-- 用途  ：给 jst_contest 补充线下预约能力字段
-- 说明  ：MySQL 5.7 不支持 ADD COLUMN IF NOT EXISTS，这里用 information_schema
--         + 动态 SQL 做等价幂等保护，重复执行不会失败。
-- =====================================================================
SET NAMES utf8mb4;

SET @db_name = DATABASE();

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db_name
          AND TABLE_NAME = 'jst_contest'
          AND COLUMN_NAME = 'appointment_capacity'
    ),
    'SELECT 1',
    "ALTER TABLE `jst_contest`
       ADD COLUMN `appointment_capacity` int(11) NOT NULL DEFAULT '0'
       COMMENT '线下预约场次总名额：0表示不限'
       AFTER `support_appointment`"
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db_name
          AND TABLE_NAME = 'jst_contest'
          AND COLUMN_NAME = 'writeoff_config'
    ),
    'SELECT 1',
    "ALTER TABLE `jst_contest`
       ADD COLUMN `writeoff_config` json DEFAULT NULL
       COMMENT '核销子项配置JSON，支持arrival/gift/ceremony/custom'
       AFTER `appointment_capacity`"
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db_name
          AND TABLE_NAME = 'jst_contest'
          AND COLUMN_NAME = 'allow_repeat_appointment'
    ),
    'SELECT 1',
    "ALTER TABLE `jst_contest`
       ADD COLUMN `allow_repeat_appointment` tinyint(1) NOT NULL DEFAULT '0'
       COMMENT '是否允许同一参赛人重复预约：0否 1是'
       AFTER `writeoff_config`"
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
