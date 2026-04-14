-- =====================================================================
-- 99-migration-contest-reviewer.sql
-- REFACTOR-4-ENROLL: 赛事评审老师表
-- 幂等：可重复执行
-- =====================================================================

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS jst_contest_reviewer (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  user_id        BIGINT NOT NULL COMMENT 'FK sys_user（评审老师账号）',
  reviewer_name  VARCHAR(50) COMMENT '老师姓名',
  role           VARCHAR(20) DEFAULT 'reviewer' COMMENT 'chief_reviewer/reviewer',
  status         TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_contest_user (contest_id, user_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事评审老师';
