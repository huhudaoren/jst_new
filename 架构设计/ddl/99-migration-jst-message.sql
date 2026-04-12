-- =====================================================================
-- 99-migration-jst-message.sql
-- 补建 jst_message 站内消息表（MSG-TRIGGER 任务产出）
-- =====================================================================

CREATE TABLE IF NOT EXISTS `jst_message` (
  `message_id`  bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id`     bigint(20)   NOT NULL                COMMENT '接收用户ID',
  `type`        varchar(20)  NOT NULL DEFAULT 'business' COMMENT '消息类型：system/business',
  `title`       varchar(255) NOT NULL                COMMENT '标题',
  `content`     text                                 COMMENT '内容',
  `read_status` tinyint(4)   NOT NULL DEFAULT '0'    COMMENT '已读状态：0未读 1已读',
  `biz_type`    varchar(50)  DEFAULT NULL            COMMENT '业务类型：order_paid/refund_success/enroll_audit/channel_auth/score_published/withdraw_paid',
  `biz_id`      bigint(20)   DEFAULT NULL            COMMENT '业务ID',
  `del_flag`    char(1)      NOT NULL DEFAULT '0'    COMMENT '逻辑删除：0存在 2删除',
  `create_by`   varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time` datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time` datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`      varchar(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`message_id`),
  KEY `idx_user_read` (`user_id`, `read_status`),
  KEY `idx_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='站内消息';
