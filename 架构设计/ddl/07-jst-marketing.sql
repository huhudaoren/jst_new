-- =====================================================================
-- 文件名：07-jst-marketing.sql
-- 模块  ：jst-marketing 优惠券与权益域
-- 来源  ：竞赛通-V4.1 域 H1-H6，共 6 张表
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- H1. jst_coupon_template —— 优惠券模板
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_coupon_template`;
CREATE TABLE `jst_coupon_template` (
  `coupon_template_id`     bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `coupon_name`            varchar(128)  NOT NULL                COMMENT '券名',
  `coupon_type`            varchar(20)   NOT NULL                COMMENT '类型：full_reduce满减/direct_reduce直减/discount折扣/contest_specific赛事专享',
  `face_value`             decimal(12,2) DEFAULT NULL            COMMENT '面额',
  `discount_rate`          decimal(5,2)  DEFAULT NULL            COMMENT '折扣率',
  `threshold_amount`       decimal(12,2) DEFAULT NULL            COMMENT '门槛金额',
  `applicable_role`        varchar(20)   DEFAULT NULL            COMMENT '适用角色：student/channel/all',
  `applicable_contest_ids` varchar(1024) DEFAULT NULL            COMMENT '适用赛事ID列表（逗号分隔）',
  `valid_days`             int(11)       DEFAULT NULL            COMMENT '相对有效天数',
  `valid_start`            datetime      DEFAULT NULL            COMMENT '绝对有效开始',
  `valid_end`              datetime      DEFAULT NULL            COMMENT '绝对有效结束',
  `stack_rule`             varchar(20)   DEFAULT NULL            COMMENT '叠加规则',
  `status`                 tinyint(4)    NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`              varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`            datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`              varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`            datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`                 varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`               char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`coupon_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='优惠券模板';


-- ---------------------------------------------------------------------
-- H2. jst_user_coupon —— 用户券
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_user_coupon`;
CREATE TABLE `jst_user_coupon` (
  `user_coupon_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '用户券ID',
  `coupon_template_id` bigint(20)  NOT NULL                COMMENT '模板ID',
  `user_id`            bigint(20)  NOT NULL                COMMENT '用户ID',
  `issue_batch_no`     varchar(32) DEFAULT NULL            COMMENT '发券批次号',
  `issue_source`       varchar(20) NOT NULL                COMMENT '发券来源：platform/campaign/manual',
  `status`             varchar(20) NOT NULL DEFAULT 'unused' COMMENT '状态：unused/locked/used/expired/refunded',
  `valid_start`        datetime    NOT NULL                COMMENT '生效时间',
  `valid_end`          datetime    NOT NULL                COMMENT '失效时间',
  `order_id`           bigint(20)  DEFAULT NULL            COMMENT '使用订单ID',
  `use_time`           datetime    DEFAULT NULL            COMMENT '使用时间',
  `create_by`          varchar(64) DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime    DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64) DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime    DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`           char(1)     DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`user_coupon_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_template_id` (`coupon_template_id`),
  KEY `idx_valid_end` (`valid_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户持有优惠券';


-- ---------------------------------------------------------------------
-- H3. jst_coupon_issue_batch —— 发券批次
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_coupon_issue_batch`;
CREATE TABLE `jst_coupon_issue_batch` (
  `batch_id`           bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_no`           varchar(32) NOT NULL                COMMENT '批次号',
  `coupon_template_id` bigint(20)  NOT NULL                COMMENT '模板ID',
  `target_type`        varchar(20) NOT NULL                COMMENT '目标类型：user/channel/campaign/tag',
  `target_json`        json        DEFAULT NULL            COMMENT '目标对象JSON',
  `total_count`        int(11)     NOT NULL DEFAULT '0'    COMMENT '计划数量',
  `success_count`      int(11)     NOT NULL DEFAULT '0'    COMMENT '成功数',
  `fail_count`         int(11)     NOT NULL DEFAULT '0'    COMMENT '失败数',
  `status`             varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/running/completed/failed',
  `operator_id`        bigint(20)  DEFAULT NULL            COMMENT '操作人',
  `create_by`          varchar(64) DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime    DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64) DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime    DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`           char(1)     DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='发券批次';


-- ---------------------------------------------------------------------
-- H4. jst_rights_template —— 权益模板
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_rights_template`;
CREATE TABLE `jst_rights_template` (
  `rights_template_id` bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '权益模板ID',
  `rights_name`        varchar(128)  NOT NULL                COMMENT '权益名称',
  `rights_type`        varchar(20)   NOT NULL                COMMENT '类型：enroll_deduct报名抵扣/venue_reduce场地减免/exclusive_course专属课程/cs_priority优先客服/custom自定义',
  `quota_mode`         varchar(10)   NOT NULL                COMMENT '口径：times次数/amount金额/period周期',
  `quota_value`        decimal(12,2) DEFAULT NULL            COMMENT '数值',
  `valid_days`         int(11)       DEFAULT NULL            COMMENT '有效期天数',
  `writeoff_mode`      varchar(20)   NOT NULL                COMMENT '核销方式：online_auto/manual_review/offline_confirm',
  `applicable_role`    varchar(20)   NOT NULL DEFAULT 'student' COMMENT '角色：student/channel/both',
  `status`             tinyint(4)    NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`          varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`           char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`rights_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权益模板';


-- ---------------------------------------------------------------------
-- H5. jst_user_rights —— 用户权益
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_user_rights`;
CREATE TABLE `jst_user_rights` (
  `user_rights_id`     bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '用户权益ID',
  `rights_template_id` bigint(20)    NOT NULL                COMMENT '权益模板ID',
  `owner_type`         varchar(10)   NOT NULL                COMMENT '持有方：student/channel',
  `owner_id`           bigint(20)    NOT NULL                COMMENT '持有方ID',
  `source_type`        varchar(20)   NOT NULL                COMMENT '来源：level等级/campaign活动/manual手工',
  `source_ref_id`      bigint(20)    DEFAULT NULL            COMMENT '来源单据ID',
  `remain_quota`       decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '剩余额度',
  `valid_start`        datetime      NOT NULL                COMMENT '生效时间',
  `valid_end`          datetime      NOT NULL                COMMENT '失效时间',
  `status`             varchar(20)   NOT NULL DEFAULT 'available' COMMENT '状态：available/locked/used/expired/revoked',
  `create_by`          varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`           char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`user_rights_id`),
  KEY `idx_owner_status` (`owner_type`,`owner_id`,`status`),
  KEY `idx_template_id` (`rights_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户权益持有';


-- ---------------------------------------------------------------------
-- H6. jst_rights_writeoff_record —— 权益核销记录
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_rights_writeoff_record`;
CREATE TABLE `jst_rights_writeoff_record` (
  `record_id`      bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '核销记录ID',
  `writeoff_no`    varchar(32)   NOT NULL                COMMENT '核销单号',
  `user_rights_id` bigint(20)    NOT NULL                COMMENT '用户权益ID',
  `use_amount`     decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '使用额度',
  `apply_remark`   varchar(255)  DEFAULT NULL            COMMENT '申请说明',
  `status`         varchar(20)   NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected/confirmed/rolled_back',
  `audit_user_id`  bigint(20)    DEFAULT NULL            COMMENT '审核员',
  `audit_remark`   varchar(255)  DEFAULT NULL            COMMENT '审核备注',
  `writeoff_time`  datetime      DEFAULT NULL            COMMENT '核销时间',
  `create_by`      varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_writeoff_no` (`writeoff_no`),
  KEY `idx_user_rights_id` (`user_rights_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权益核销记录';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 7 完成：6 张表
