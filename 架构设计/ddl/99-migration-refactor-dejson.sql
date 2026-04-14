SET NAMES utf8mb4;

-- =====================================================================
-- REFACTOR-1-DDL: 数据库去 JSON 化
-- 目标:
-- 1) 新增 7 张结构化子表
-- 2) 为 jst_contest 增补结构化字段（兼容 MySQL 5.7，无 ADD COLUMN IF NOT EXISTS 语法）
-- 3) 将 jst_contest 里的历史 JSON 数据迁移到子表（幂等：已迁移赛事跳过）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 赛事赛程阶段（替代 jst_contest.schedule_json）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_contest_schedule (
  schedule_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  stage_name     VARCHAR(100) NOT NULL COMMENT '阶段名称',
  start_time     DATETIME COMMENT '开始时间',
  end_time       DATETIME COMMENT '结束时间',
  venue          VARCHAR(200) COMMENT '场地',
  description    VARCHAR(500) COMMENT '说明',
  sort_order     INT DEFAULT 0 COMMENT '排序',
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0' COMMENT '0正常 2删除',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事赛程阶段';

-- ---------------------------------------------------------------------
-- 2. 奖项设置（替代 jst_contest.awards_json）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_contest_award (
  award_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  award_name     VARCHAR(100) NOT NULL COMMENT '奖项名称',
  award_level    VARCHAR(50) COMMENT '等级编码（gold/silver/bronze/first/second/third）',
  description    VARCHAR(500) COMMENT '奖项说明',
  quota          INT DEFAULT 0 COMMENT '名额数',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事奖项设置';

-- ---------------------------------------------------------------------
-- 3. 常见问题（替代 jst_contest.faq_json）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_contest_faq (
  faq_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  question       VARCHAR(500) NOT NULL COMMENT '问题',
  answer         TEXT NOT NULL COMMENT '回答',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事常见问题';

-- ---------------------------------------------------------------------
-- 4. 成绩项定义（替代 jst_contest.score_rule_json.scoreItems）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_score_item (
  item_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  item_name      VARCHAR(100) NOT NULL COMMENT '成绩项名称（总分/技巧/表现力）',
  max_score      DECIMAL(8,2) DEFAULT 100.00 COMMENT '满分值',
  weight         DECIMAL(5,2) DEFAULT 1.00 COMMENT '权重（占总分比例）',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事成绩项定义';

-- ---------------------------------------------------------------------
-- 5. 预约时间段（新增）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_appointment_slot (
  slot_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  slot_date      DATE NOT NULL COMMENT '预约日期',
  start_time     TIME NOT NULL COMMENT '开始时间',
  end_time       TIME NOT NULL COMMENT '结束时间',
  venue          VARCHAR(200) COMMENT '场地名称',
  capacity       INT NOT NULL DEFAULT 0 COMMENT '该时段容量',
  booked_count   INT DEFAULT 0 COMMENT '已预约数',
  status         TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest_date (contest_id, slot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事预约时间段';

-- ---------------------------------------------------------------------
-- 6. 课程章节（替代 jst_course.chapters_json）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_course_chapter (
  chapter_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  course_id      BIGINT NOT NULL COMMENT 'FK jst_course',
  chapter_name   VARCHAR(200) NOT NULL COMMENT '章节名称',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节';

-- ---------------------------------------------------------------------
-- 7. 课程课时（chapters_json 内 lessons）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jst_course_lesson (
  lesson_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  chapter_id     BIGINT NOT NULL COMMENT 'FK jst_course_chapter',
  lesson_name    VARCHAR(200) NOT NULL COMMENT '课时名称',
  duration       VARCHAR(50) COMMENT '时长描述',
  is_free        TINYINT DEFAULT 0 COMMENT '是否免费试看',
  video_url      VARCHAR(500) COMMENT '视频URL',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_chapter (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程课时';

-- ---------------------------------------------------------------------
-- jst_contest 增加结构化字段（兼容 MySQL 5.7 的 IF NOT EXISTS 处理）
-- ---------------------------------------------------------------------
SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'banner_image'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN banner_image VARCHAR(500) COMMENT ''Banner大图URL'' AFTER cover_image'
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'total_quota'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN total_quota INT DEFAULT 0 COMMENT ''总报名名额(0=不限)'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'per_user_limit'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN per_user_limit INT DEFAULT 1 COMMENT ''单人限购数'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'score_publish_mode'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN score_publish_mode VARCHAR(20) DEFAULT ''manual'' COMMENT ''成绩发布模式: manual/auto'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'cert_issue_mode'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN cert_issue_mode VARCHAR(20) DEFAULT ''manual'' COMMENT ''证书颁发模式: manual/auto'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'writeoff_mode'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN writeoff_mode VARCHAR(20) DEFAULT ''qr'' COMMENT ''核销方式: qr/manual'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'need_sign_in'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN need_sign_in TINYINT DEFAULT 0 COMMENT ''是否需要签到: 0否 1是'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'team_min_size'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN team_min_size INT DEFAULT 0 COMMENT ''团队最小人数(0=不支持团队)'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'team_max_size'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN team_max_size INT DEFAULT 0 COMMENT ''团队最大人数'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'team_leader_fields'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN team_leader_fields VARCHAR(500) COMMENT ''队长需填字段(逗号分隔)'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'team_member_fields'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN team_member_fields VARCHAR(500) COMMENT ''队员需填字段(逗号分隔)'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'score_publish_time'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN score_publish_time DATETIME COMMENT ''成绩发布时间(auto模式用)'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'aftersale_deadline'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN aftersale_deadline DATETIME COMMENT ''售后截止时间'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'organizer'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN organizer VARCHAR(200) COMMENT ''主办方'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'co_organizer'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN co_organizer VARCHAR(200) COMMENT ''协办方'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
  SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'jst_contest' AND COLUMN_NAME = 'event_address'),
    'SELECT 1',
    'ALTER TABLE jst_contest ADD COLUMN event_address VARCHAR(500) COMMENT ''比赛地址'''
  )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ---------------------------------------------------------------------
-- 历史 JSON 数据迁移（测试库）
-- ---------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_seq_0_99;
CREATE TEMPORARY TABLE tmp_seq_0_99 (
  n INT PRIMARY KEY
);
INSERT INTO tmp_seq_0_99(n) VALUES
 (0),(1),(2),(3),(4),(5),(6),(7),(8),(9),
 (10),(11),(12),(13),(14),(15),(16),(17),(18),(19),
 (20),(21),(22),(23),(24),(25),(26),(27),(28),(29),
 (30),(31),(32),(33),(34),(35),(36),(37),(38),(39),
 (40),(41),(42),(43),(44),(45),(46),(47),(48),(49),
 (50),(51),(52),(53),(54),(55),(56),(57),(58),(59),
 (60),(61),(62),(63),(64),(65),(66),(67),(68),(69),
 (70),(71),(72),(73),(74),(75),(76),(77),(78),(79),
 (80),(81),(82),(83),(84),(85),(86),(87),(88),(89),
 (90),(91),(92),(93),(94),(95),(96),(97),(98),(99);

INSERT INTO jst_contest_schedule (
  contest_id, stage_name, start_time, end_time, venue, description, sort_order,
  create_by, create_time, update_by, update_time, del_flag
)
SELECT
  c.contest_id,
  JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].stageName'))),
  STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].startTime'))), '%Y-%m-%d %H:%i:%s'),
  STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].endTime'))), '%Y-%m-%d %H:%i:%s'),
  JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].venue'))),
  JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].description'))),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].sortOrder'))) AS SIGNED), s.n),
  'migration', NOW(), 'migration', NOW(), '0'
FROM jst_contest c
JOIN tmp_seq_0_99 s ON s.n < IFNULL(JSON_LENGTH(c.schedule_json), 0)
WHERE c.schedule_json IS NOT NULL
  AND c.schedule_json <> ''
  AND JSON_VALID(c.schedule_json) = 1
  AND JSON_UNQUOTE(JSON_EXTRACT(c.schedule_json, CONCAT('$[', s.n, '].stageName'))) IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM jst_contest_schedule x
    WHERE x.contest_id = c.contest_id AND x.del_flag = '0'
  );

INSERT INTO jst_contest_award (
  contest_id, award_name, award_level, description, quota, sort_order,
  create_by, create_time, update_by, update_time, del_flag
)
SELECT
  c.contest_id,
  JSON_UNQUOTE(JSON_EXTRACT(c.awards_json, CONCAT('$[', s.n, '].awardName'))),
  JSON_UNQUOTE(JSON_EXTRACT(c.awards_json, CONCAT('$[', s.n, '].awardLevel'))),
  JSON_UNQUOTE(JSON_EXTRACT(c.awards_json, CONCAT('$[', s.n, '].description'))),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.awards_json, CONCAT('$[', s.n, '].count'))) AS SIGNED), 0),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.awards_json, CONCAT('$[', s.n, '].sortOrder'))) AS SIGNED), s.n),
  'migration', NOW(), 'migration', NOW(), '0'
FROM jst_contest c
JOIN tmp_seq_0_99 s ON s.n < IFNULL(JSON_LENGTH(c.awards_json), 0)
WHERE c.awards_json IS NOT NULL
  AND c.awards_json <> ''
  AND JSON_VALID(c.awards_json) = 1
  AND JSON_UNQUOTE(JSON_EXTRACT(c.awards_json, CONCAT('$[', s.n, '].awardName'))) IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM jst_contest_award x
    WHERE x.contest_id = c.contest_id AND x.del_flag = '0'
  );

INSERT INTO jst_contest_faq (
  contest_id, question, answer, sort_order,
  create_by, create_time, update_by, update_time, del_flag
)
SELECT
  c.contest_id,
  JSON_UNQUOTE(JSON_EXTRACT(c.faq_json, CONCAT('$[', s.n, '].question'))),
  JSON_UNQUOTE(JSON_EXTRACT(c.faq_json, CONCAT('$[', s.n, '].answer'))),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.faq_json, CONCAT('$[', s.n, '].sortOrder'))) AS SIGNED), s.n),
  'migration', NOW(), 'migration', NOW(), '0'
FROM jst_contest c
JOIN tmp_seq_0_99 s ON s.n < IFNULL(JSON_LENGTH(c.faq_json), 0)
WHERE c.faq_json IS NOT NULL
  AND c.faq_json <> ''
  AND JSON_VALID(c.faq_json) = 1
  AND JSON_UNQUOTE(JSON_EXTRACT(c.faq_json, CONCAT('$[', s.n, '].question'))) IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM jst_contest_faq x
    WHERE x.contest_id = c.contest_id AND x.del_flag = '0'
  );

INSERT INTO jst_score_item (
  contest_id, item_name, max_score, weight, sort_order,
  create_by, create_time, update_by, update_time, del_flag
)
SELECT
  c.contest_id,
  COALESCE(
    JSON_UNQUOTE(JSON_EXTRACT(c.score_rule_json, CONCAT('$.scoreItems[', s.n, '].itemName'))),
    JSON_UNQUOTE(JSON_EXTRACT(c.score_rule_json, CONCAT('$.scoreItems[', s.n, '].name'))),
    CONCAT('成绩项', s.n + 1)
  ),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.score_rule_json, CONCAT('$.scoreItems[', s.n, '].maxScore'))) AS DECIMAL(8,2)), 100.00),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.score_rule_json, CONCAT('$.scoreItems[', s.n, '].weight'))) AS DECIMAL(5,2)), 1.00),
  IFNULL(CAST(JSON_UNQUOTE(JSON_EXTRACT(c.score_rule_json, CONCAT('$.scoreItems[', s.n, '].sortOrder'))) AS SIGNED), s.n),
  'migration', NOW(), 'migration', NOW(), '0'
FROM jst_contest c
JOIN tmp_seq_0_99 s ON s.n < IFNULL(JSON_LENGTH(c.score_rule_json, '$.scoreItems'), 0)
WHERE c.score_rule_json IS NOT NULL
  AND c.score_rule_json <> ''
  AND JSON_VALID(c.score_rule_json) = 1
  AND NOT EXISTS (
    SELECT 1 FROM jst_score_item x
    WHERE x.contest_id = c.contest_id AND x.del_flag = '0'
  );

DROP TEMPORARY TABLE IF EXISTS tmp_seq_0_99;
