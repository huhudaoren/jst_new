-- =====================================================================
-- 文件名：06-jst-organizer.sql
-- 模块  ：jst-organizer 赛事方/渠道方 入驻申请域
-- 来源  ：竞赛通-V4.1 域 F，共 2 张表
-- 说明  ：赛事方主档 jst_event_partner 已在 02-jst-event.sql 定义
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- F1. jst_event_partner_apply —— 赛事方入驻申请
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_event_partner_apply`;
CREATE TABLE `jst_event_partner_apply` (
  `apply_id`             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no`             varchar(32)  NOT NULL                COMMENT '申请单号',
  `partner_id`           bigint(20)   DEFAULT NULL            COMMENT '通过后生成的赛事方ID（FK→jst_event_partner）',
  `partner_name`         varchar(128) NOT NULL                COMMENT '申请名称',
  `contact_name`         varchar(64)  NOT NULL                COMMENT '联系人',
  `contact_mobile`       varchar(20)  NOT NULL                COMMENT '联系电话',
  `business_license_no`  varchar(64)  DEFAULT NULL            COMMENT '营业执照号',
  `qualification_json`   json         DEFAULT NULL            COMMENT '资质材料JSON',
  `settlement_info_json` json         DEFAULT NULL            COMMENT '结算信息JSON',
  `invoice_info_json`    json         DEFAULT NULL            COMMENT '发票信息JSON',
  `contract_files_json`  json         DEFAULT NULL            COMMENT '合同附件JSON',
  `apply_status`         varchar(20)  NOT NULL DEFAULT 'draft' COMMENT '状态：draft/pending/approved/rejected/supplement',
  `supplement_remark`    varchar(255) DEFAULT NULL            COMMENT '补充材料说明',
  `audit_remark`         varchar(255) DEFAULT NULL            COMMENT '审核意见',
  `audit_user_id`        bigint(20)   DEFAULT NULL            COMMENT '审核员ID',
  `submit_time`          datetime     DEFAULT NULL            COMMENT '提交时间',
  `audit_time`           datetime     DEFAULT NULL            COMMENT '审核时间',
  `create_by`            varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`          datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`            varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`          datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`             char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_apply_status` (`apply_status`),
  KEY `idx_partner_id` (`partner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赛事方入驻申请';


-- ---------------------------------------------------------------------
-- F2. jst_channel_auth_apply —— 渠道认证申请
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_channel_auth_apply`;
CREATE TABLE `jst_channel_auth_apply` (
  `apply_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no`        varchar(32)  NOT NULL                COMMENT '申请单号',
  `user_id`         bigint(20)   NOT NULL                COMMENT '申请人用户ID',
  `channel_id`      bigint(20)   DEFAULT NULL            COMMENT '通过后生成的渠道方ID',
  `channel_type`    varchar(20)  NOT NULL                COMMENT '渠道类型：teacher/organization/individual',
  `apply_name`      varchar(128) NOT NULL                COMMENT '申请名称',
  `materials_json`  json         DEFAULT NULL            COMMENT '认证材料JSON',
  `apply_status`    varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected/suspended',
  `audit_remark`    varchar(255) DEFAULT NULL            COMMENT '审核备注',
  `audit_user_id`   bigint(20)   DEFAULT NULL            COMMENT '审核员ID',
  `submit_time`     datetime     DEFAULT NULL            COMMENT '提交时间',
  `audit_time`      datetime     DEFAULT NULL            COMMENT '审核时间',
  `create_by`       varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`     datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`       varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`     datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`          varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`        char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_apply_status` (`apply_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道认证申请';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 6 完成：2 张表
