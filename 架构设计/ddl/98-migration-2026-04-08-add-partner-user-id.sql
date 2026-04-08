-- =====================================================================
-- 文件名：98-migration-2026-04-08-add-partner-user-id.sql
-- 用途  ：增量变更 - jst_event_partner 增加 user_id 字段
-- 背景  ：F5 赛事方入驻审核功能需要,审核通过后创建 sys_user 并回填到本字段
-- 适用  ：已经导入过 02-jst-event.sql 的库需执行本脚本(全量重导可跳过)
-- =====================================================================
SET NAMES utf8mb4;

ALTER TABLE `jst_event_partner`
  ADD COLUMN `user_id` BIGINT(20) NULL COMMENT '关联sys_user.user_id(审核通过后自动创建并回填)'
  AFTER `partner_id`;

ALTER TABLE `jst_event_partner`
  ADD INDEX `idx_user_id` (`user_id`);

-- =====================================================================
-- 完成。请在下一次启动 ruoyi-admin 前在 MySQL 中执行本脚本
-- =====================================================================
