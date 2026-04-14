-- =====================================================================
-- 99-migration-biz-no-rule.sql
-- REFACTOR-6-BIZ-NO：统一编号规则引擎
-- 说明：
--   1) 新增业务编号规则表 jst_biz_no_rule
--   2) 新增按日序列进度表 jst_biz_no_seq
--   3) 预置 3 条规则：cert_no / channel_auth_no / enroll_cert_no
-- 幂等：可重复执行
-- =====================================================================

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS jst_biz_no_rule (
  rule_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  rule_code     VARCHAR(50) NOT NULL UNIQUE COMMENT '规则编码: cert_no/channel_auth_no/enroll_cert_no',
  rule_name     VARCHAR(100) NOT NULL COMMENT '规则名称',
  prefix        VARCHAR(20) DEFAULT '' COMMENT '前缀如 JST-CERT-',
  date_format   VARCHAR(20) DEFAULT 'yyyyMMdd' COMMENT '日期部分格式',
  seq_length    INT DEFAULT 4 COMMENT '序号位数',
  seq_start     BIGINT DEFAULT 1 COMMENT '起始序号',
  description   VARCHAR(500) DEFAULT NULL COMMENT '规则描述',
  status        TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  create_by     VARCHAR(64) DEFAULT '',
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by     VARCHAR(64) DEFAULT '',
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务编号规则';

CREATE TABLE IF NOT EXISTS jst_biz_no_seq (
  rule_code     VARCHAR(50) NOT NULL COMMENT '规则编码',
  date_key      VARCHAR(20) NOT NULL COMMENT '日期键如 20260413',
  current_seq   BIGINT DEFAULT 0 COMMENT '当前序号',
  PRIMARY KEY (rule_code, date_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务编号序列';

INSERT INTO jst_biz_no_rule (rule_code, rule_name, prefix, date_format, seq_length, seq_start, description, status, create_by, create_time, update_by, update_time)
SELECT 'cert_no', '证书编号', 'JST-CERT-', 'yyyyMMdd', 4, 1, '证书唯一编号，如 JST-CERT-20260413-0001', 1, 'migration', NOW(), 'migration', NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM jst_biz_no_rule
    WHERE rule_code = 'cert_no'
);

INSERT INTO jst_biz_no_rule (rule_code, rule_name, prefix, date_format, seq_length, seq_start, description, status, create_by, create_time, update_by, update_time)
SELECT 'channel_auth_no', '渠道授权编号', 'JST-AUTH-', 'yyyyMMdd', 4, 1, '渠道授权证书编号', 1, 'migration', NOW(), 'migration', NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM jst_biz_no_rule
    WHERE rule_code = 'channel_auth_no'
);

INSERT INTO jst_biz_no_rule (rule_code, rule_name, prefix, date_format, seq_length, seq_start, description, status, create_by, create_time, update_by, update_time)
SELECT 'enroll_cert_no', '报名证书编号', 'JST-EC-', 'yyyyMMdd', 5, 1, '报名通过后的参赛证编号', 1, 'migration', NOW(), 'migration', NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM jst_biz_no_rule
    WHERE rule_code = 'enroll_cert_no'
);
