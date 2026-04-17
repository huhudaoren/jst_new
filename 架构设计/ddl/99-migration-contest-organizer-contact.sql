-- =====================================================================
-- 赛事详情重塑: 主办方详细信息 + 联系咨询字段
-- 产出: 批次 B - 赛事详情页重塑
-- 影响表: jst_contest
-- =====================================================================

-- 主办方 LOGO
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'organizer_logo');
SET @sql := IF(@exists = 0,
  'ALTER TABLE jst_contest ADD COLUMN organizer_logo VARCHAR(500) DEFAULT NULL COMMENT ''主办方 LOGO URL''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 主办方简介
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'organizer_desc');
SET @sql := IF(@exists = 0,
  'ALTER TABLE jst_contest ADD COLUMN organizer_desc VARCHAR(500) DEFAULT NULL COMMENT ''主办方简介''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 咨询电话
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'contact_phone');
SET @sql := IF(@exists = 0,
  'ALTER TABLE jst_contest ADD COLUMN contact_phone VARCHAR(32) DEFAULT NULL COMMENT ''咨询电话''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 咨询微信
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'contact_wechat');
SET @sql := IF(@exists = 0,
  'ALTER TABLE jst_contest ADD COLUMN contact_wechat VARCHAR(64) DEFAULT NULL COMMENT ''咨询微信号''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 咨询邮箱
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'contact_email');
SET @sql := IF(@exists = 0,
  'ALTER TABLE jst_contest ADD COLUMN contact_email VARCHAR(128) DEFAULT NULL COMMENT ''咨询邮箱''',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =====================================================================
-- 测试数据: 为 8801 / 8802 赛事补 mock 主办方 + 联系方式
-- (跳过已有数据, 允许多次执行不覆盖真实生产数据)
-- =====================================================================
UPDATE jst_contest
SET organizer_logo = 'https://cdn.jst.example/organizer-logo/mock.png',
    organizer_desc = '国内领先的青少年编程创新赛事机构，专注于 Scratch / Python / C++ 等方向的赛事组织与评审。',
    contact_phone = '400-123-5678',
    contact_wechat = 'jst_service',
    contact_email = 'service@jst.example.com'
WHERE contest_id = 8801 AND COALESCE(contact_phone, '') = '';

UPDATE jst_contest
SET organizer_logo = 'https://cdn.jst.example/organizer-logo/art.png',
    organizer_desc = '资深艺术类赛事机构，历届累计评审作品 10 万+，覆盖全国 28 省市。',
    contact_phone = '400-123-5679',
    contact_wechat = 'jst_art',
    contact_email = 'art@jst.example.com'
WHERE contest_id = 8802 AND COALESCE(contact_phone, '') = '';
