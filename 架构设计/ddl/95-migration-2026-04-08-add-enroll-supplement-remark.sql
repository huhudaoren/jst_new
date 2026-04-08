-- =====================================================================
-- 文件名：95-migration-2026-04-08-add-enroll-supplement-remark.sql
-- 用途  : 增量变更 - jst_enroll_record 增加 supplement_remark 列
-- 背景  : F9 子 Agent 因为没有专用列,把 supplementRemark 暂时落到 remark 字段
--         本迁移补回独立列,避免 remark 被业务覆盖
-- 适用  : 已经导入 02-jst-event.sql 的库需执行 (全量重导可跳过,但 02-jst-event.sql 也已同步更新)
-- =====================================================================
SET NAMES utf8mb4;

ALTER TABLE `jst_enroll_record`
  ADD COLUMN `supplement_remark` VARCHAR(255) NULL COMMENT '补充材料说明(用户视角)'
  AFTER `audit_remark`;

-- 把已落到 remark 的 supplement 数据迁过去 (仅 supplement 状态的)
-- (开发期 fixture 数据通常较少,不强制必须跑)
UPDATE jst_enroll_record
SET supplement_remark = remark, remark = NULL
WHERE audit_status = 'supplement' AND supplement_remark IS NULL AND remark IS NOT NULL;
