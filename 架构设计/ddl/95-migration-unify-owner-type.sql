-- =====================================================================
-- 文件名：95-migration-unify-owner-type.sql
-- 用途  ：统一 jst_points_account.owner_type 历史枚举值
-- 适用  ：MySQL 5.7 / 仅限停服维护窗口手动执行
-- 说明  ：
--   1. 本脚本幂等，可重复执行
--   2. 本卡只交付脚本，不在当前开发窗口执行
--   3. 执行前必须停服，避免与旧兼容写入并发冲突
-- =====================================================================
SET NAMES utf8mb4;

-- 维护窗口执行前先确认影响行数。
SELECT COUNT(*) AS owner_type_student_before
FROM jst_points_account
WHERE owner_type = 'student';

-- 幂等清洗：只把历史 student 归一为 user。
UPDATE jst_points_account
SET owner_type = 'user'
WHERE owner_type = 'student';

-- 执行后复核：应返回 0。
SELECT COUNT(*) AS owner_type_student_after
FROM jst_points_account
WHERE owner_type = 'student';
