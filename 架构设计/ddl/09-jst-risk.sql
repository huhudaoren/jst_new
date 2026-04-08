-- =====================================================================
-- 文件名：09-jst-risk.sql
-- 模块  ：jst-risk 操作审计 + 风控
-- 来源  ：竞赛通-V4.1 域 H10-H14，共 5 张表
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- H10. jst_audit_log —— 操作审计日志
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_audit_log`;
CREATE TABLE `jst_audit_log` (
  `audit_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator_id`   bigint(20)   NOT NULL                COMMENT '操作人ID',
  `operator_name` varchar(64)  DEFAULT NULL            COMMENT '操作人姓名',
  `operator_type` varchar(20)  NOT NULL                COMMENT '操作人类型：admin/partner/channel/system',
  `module`        varchar(64)  NOT NULL                COMMENT '模块名',
  `action`        varchar(64)  NOT NULL                COMMENT '动作：review/pay/adjust/refund/grant/revoke 等',
  `target_type`   varchar(64)  DEFAULT NULL            COMMENT '目标对象类型',
  `target_id`     varchar(64)  DEFAULT NULL            COMMENT '目标对象ID',
  `before_json`   json         DEFAULT NULL            COMMENT '变更前快照',
  `after_json`    json         DEFAULT NULL            COMMENT '变更后快照',
  `ip`            varchar(64)  DEFAULT NULL            COMMENT '操作IP',
  `terminal`      varchar(32)  DEFAULT NULL            COMMENT '终端：web/h5/api',
  `result`        varchar(10)  NOT NULL                COMMENT '结果：success/fail',
  `op_time`       datetime     NOT NULL                COMMENT '操作时间',
  `create_by`     varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`   datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`   datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`        varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`      char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`audit_id`),
  KEY `idx_operator` (`operator_type`,`operator_id`),
  KEY `idx_module_action` (`module`,`action`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_op_time` (`op_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作审计日志';


-- ---------------------------------------------------------------------
-- H11. jst_risk_rule —— 风控规则
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_risk_rule`;
CREATE TABLE `jst_risk_rule` (
  `risk_rule_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name`      varchar(128) NOT NULL                COMMENT '规则名',
  `rule_type`      varchar(32)  NOT NULL                COMMENT '类型：bind_freq/coupon_freq/refund_freq/rebate_anomaly/zero_order_freq/aftersale_anomaly',
  `dimension`      varchar(32)  NOT NULL                COMMENT '维度：user/device/mobile/channel',
  `threshold_json` json         NOT NULL                COMMENT '阈值配置JSON',
  `action`         varchar(20)  NOT NULL                COMMENT '命中动作：warn告警/intercept拦截/manual人工',
  `status`         tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`risk_rule_id`),
  KEY `idx_type_status` (`rule_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='风控规则';


-- ---------------------------------------------------------------------
-- H12. jst_risk_alert —— 风险预警
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_risk_alert`;
CREATE TABLE `jst_risk_alert` (
  `alert_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `risk_rule_id`     bigint(20)   NOT NULL                COMMENT '命中规则ID',
  `risk_level`       varchar(10)  NOT NULL                COMMENT '风险等级：low/mid/high',
  `target_type`      varchar(20)  NOT NULL                COMMENT '对象类型：user/channel/order',
  `target_id`        bigint(20)   NOT NULL                COMMENT '对象ID',
  `hit_detail_json`  json         DEFAULT NULL            COMMENT '命中详情JSON',
  `status`           varchar(20)  NOT NULL DEFAULT 'open' COMMENT '处理状态：open/processing/closed/false_alarm',
  `case_id`          bigint(20)   DEFAULT NULL            COMMENT '关联工单ID',
  `alert_time`       datetime     NOT NULL                COMMENT '预警时间',
  `create_by`        varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`      datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`        varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`      datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`           varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`         char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`alert_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_status` (`status`),
  KEY `idx_case_id` (`case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='风险预警';


-- ---------------------------------------------------------------------
-- H13. jst_risk_case —— 风险工单
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_risk_case`;
CREATE TABLE `jst_risk_case` (
  `case_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `case_no`     varchar(32)  NOT NULL                COMMENT '工单号',
  `alert_id`    bigint(20)   DEFAULT NULL            COMMENT '关联预警',
  `assignee_id` bigint(20)   DEFAULT NULL            COMMENT '处理人',
  `status`      varchar(20)  NOT NULL DEFAULT 'open' COMMENT '状态：open/assigned/processing/reviewing/closed',
  `conclusion`  varchar(255) DEFAULT NULL            COMMENT '处理结论',
  `reviewer_id` bigint(20)   DEFAULT NULL            COMMENT '复核人',
  `close_time`  datetime     DEFAULT NULL            COMMENT '关闭时间',
  `create_by`   varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time` datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time` datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`    char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`case_id`),
  UNIQUE KEY `uk_case_no` (`case_no`),
  KEY `idx_status` (`status`),
  KEY `idx_assignee` (`assignee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='风险工单';


-- ---------------------------------------------------------------------
-- H14. jst_risk_blacklist —— 黑白名单
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_risk_blacklist`;
CREATE TABLE `jst_risk_blacklist` (
  `list_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `list_type`      varchar(10)  NOT NULL                COMMENT '名单类型：black/white',
  `target_type`    varchar(20)  NOT NULL                COMMENT '对象类型：user/channel/device/mobile/address',
  `target_value`   varchar(128) NOT NULL                COMMENT '目标值',
  `reason`         varchar(255) DEFAULT NULL            COMMENT '入名单原因',
  `effective_time` datetime     DEFAULT NULL            COMMENT '生效时间',
  `expire_time`    datetime     DEFAULT NULL            COMMENT '失效时间',
  `status`         tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`list_id`),
  KEY `idx_target` (`target_type`,`target_value`),
  KEY `idx_list_type` (`list_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='风控黑白名单';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 9 完成：5 张表
