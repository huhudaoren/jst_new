-- =====================================================================
-- 文件名：08-jst-message.sql
-- 模块  ：jst-message 公告 / 消息模板 / 发送日志
-- 来源  ：竞赛通-V4.1 域 H7-H9，共 3 张表
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- H7. jst_notice —— 公告
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_notice`;
CREATE TABLE `jst_notice` (
  `notice_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title`        varchar(255) NOT NULL                COMMENT '标题',
  `category`     varchar(20)  NOT NULL                COMMENT '分类：contest赛事/platform平台/points积分/mall商城',
  `content`      longtext     NOT NULL                COMMENT '富文本内容',
  `cover_image`  varchar(255) DEFAULT NULL            COMMENT '封面图URL',
  `top_flag`     tinyint(4)   NOT NULL DEFAULT '0'    COMMENT '是否置顶：0否 1是',
  `status`       varchar(10)  NOT NULL DEFAULT 'draft' COMMENT '状态：draft/published/offline',
  `publish_time` datetime     DEFAULT NULL            COMMENT '发布时间',
  `create_by`    varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`  datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`    varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`  datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`       varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`     char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`notice_id`),
  KEY `idx_category_status` (`category`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告';


-- ---------------------------------------------------------------------
-- H8. jst_message_template —— 消息模板
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_message_template`;
CREATE TABLE `jst_message_template` (
  `template_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(64)  NOT NULL                COMMENT '模板编码',
  `template_name` varchar(128) NOT NULL                COMMENT '模板名',
  `channel`       varchar(20)  NOT NULL                COMMENT '通道：inner站内/sms短信/wechat_template微信模板消息',
  `scene`         varchar(32)  NOT NULL                COMMENT '场景：auth_result/withdraw_result/settle_result/points_change/ship/aftersale',
  `content`       text         NOT NULL                COMMENT '模板内容（含 ${var} 变量占位）',
  `status`        tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`     varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`   datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`   datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`        varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`      char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息模板';


-- ---------------------------------------------------------------------
-- H9. jst_message_log —— 消息发送日志
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_message_log`;
CREATE TABLE `jst_message_log` (
  `log_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `template_code`  varchar(64)  NOT NULL                COMMENT '模板编码',
  `channel`        varchar(20)  NOT NULL                COMMENT '通道：inner/sms/wechat',
  `target_user_id` bigint(20)   DEFAULT NULL            COMMENT '接收用户ID',
  `target_mobile`  varchar(20)  DEFAULT NULL            COMMENT '接收手机',
  `trigger_source` varchar(64)  DEFAULT NULL            COMMENT '触发来源（业务场景）',
  `content`        text         DEFAULT NULL            COMMENT '渲染后的实际内容',
  `send_status`    varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '发送状态：pending/success/failed',
  `fail_reason`    varchar(255) DEFAULT NULL            COMMENT '失败原因',
  `send_time`      datetime     DEFAULT NULL            COMMENT '发送时间',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`log_id`),
  KEY `idx_target_user_id` (`target_user_id`),
  KEY `idx_template_code` (`template_code`),
  KEY `idx_send_status` (`send_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息发送日志';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 8 完成：3 张表
