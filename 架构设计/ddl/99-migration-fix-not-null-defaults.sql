-- ============================================================
-- 修复 NOT NULL 字段缺少默认值导致 INSERT 失败的隐患
-- 背景：代码生成的 Mapper 使用动态 INSERT（只含非空字段），
--       如果字段 NOT NULL 且无 DEFAULT，未传值时数据库报错。
-- 策略：给所有"用户表单创建"场景下可能漏传的 NOT NULL 字段加 DEFAULT。
-- ============================================================

-- 1. jst_enroll_form_template
ALTER TABLE jst_enroll_form_template
  MODIFY COLUMN owner_type VARCHAR(20) NOT NULL DEFAULT 'platform' COMMENT '所属方：platform/partner',
  MODIFY COLUMN schema_json JSON DEFAULT NULL COMMENT '字段定义JSON';

-- 2. jst_cert_template
ALTER TABLE jst_cert_template
  MODIFY COLUMN owner_type VARCHAR(10) NOT NULL DEFAULT 'platform' COMMENT '所属：platform/partner';

-- 3. jst_course
ALTER TABLE jst_course
  MODIFY COLUMN course_type VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '类型：normal普通/ai_maic AI课程',
  MODIFY COLUMN creator_type VARCHAR(20) NOT NULL DEFAULT 'platform' COMMENT '创建者类型：platform/channel';

-- 4. jst_risk_rule
ALTER TABLE jst_risk_rule
  MODIFY COLUMN threshold_json JSON DEFAULT NULL COMMENT '阈值配置JSON';

-- 5. jst_notice
ALTER TABLE jst_notice
  MODIFY COLUMN content LONGTEXT DEFAULT NULL COMMENT '富文本内容';

-- 6. jst_message_template
ALTER TABLE jst_message_template
  MODIFY COLUMN content TEXT DEFAULT NULL COMMENT '模板内容';

-- 验证
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('jst_enroll_form_template','jst_cert_template','jst_course','jst_risk_rule','jst_notice','jst_message_template')
  AND COLUMN_NAME IN ('owner_type','schema_json','course_type','creator_type','threshold_json','content')
ORDER BY TABLE_NAME;
