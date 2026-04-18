-- =====================================================
-- 渠道认证 / 渠道档案补地区字段（省级维度）
-- 说明：
-- 1. jst_channel_auth_apply.region 用于申请阶段必填采集
-- 2. jst_channel.region 用于正式档案与 admin 渠道热力图
-- 3. 历史数据允许为空，运行态由地址表 province 兜底
-- =====================================================
SET NAMES utf8mb4;

SET @ddl := IF (
    (SELECT COUNT(*)
       FROM INFORMATION_SCHEMA.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'jst_channel_auth_apply'
        AND COLUMN_NAME = 'region') = 0,
    'ALTER TABLE jst_channel_auth_apply ADD COLUMN region VARCHAR(64) NULL COMMENT ''申请地区（省级维度，热力图用）'' AFTER apply_name',
    'SELECT ''jst_channel_auth_apply.region exists'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := IF (
    (SELECT COUNT(*)
       FROM INFORMATION_SCHEMA.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'jst_channel'
        AND COLUMN_NAME = 'region') = 0,
    'ALTER TABLE jst_channel ADD COLUMN region VARCHAR(64) NULL COMMENT ''渠道地区（省级维度，热力图用）'' AFTER channel_name',
    'SELECT ''jst_channel.region exists'''
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
