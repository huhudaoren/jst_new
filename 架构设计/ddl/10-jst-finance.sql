-- =====================================================================
-- 文件名：10-jst-finance.sql
-- 模块  ：jst-finance 合同 / 发票 / 统一打款
-- 来源  ：竞赛通-V4.1 域 H15-H17，共 3 张表
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- H15. jst_contract_record —— 合同
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_contract_record`;
CREATE TABLE `jst_contract_record` (
  `contract_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `contract_no`        varchar(32)  NOT NULL                COMMENT '合同编号',
  `contract_type`      varchar(20)  NOT NULL                COMMENT '合同类型：partner_coop赛事方合作/channel_settle渠道结算/supplement补充',
  `target_type`        varchar(20)  NOT NULL                COMMENT '对象类型：partner/channel',
  `target_id`          bigint(20)   NOT NULL                COMMENT '对象ID',
  `template_id`        bigint(20)   DEFAULT NULL            COMMENT '合同模板ID',
  `file_url`           varchar(255) DEFAULT NULL            COMMENT '合同文件URL',
  `status`             varchar(20)  NOT NULL DEFAULT 'draft' COMMENT '合同状态：draft/pending_sign/signed/expired/archived',
  `effective_time`     datetime     DEFAULT NULL            COMMENT '生效时间',
  `sign_time`          datetime     DEFAULT NULL            COMMENT '签署时间',
  `ref_settlement_id`  bigint(20)   DEFAULT NULL            COMMENT '关联结算单ID',
  `create_by`          varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`           char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`contract_id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同记录';


-- ---------------------------------------------------------------------
-- H16. jst_invoice_record —— 发票
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_invoice_record`;
CREATE TABLE `jst_invoice_record` (
  `invoice_id`         bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '发票ID',
  `invoice_no`         varchar(64)   DEFAULT NULL            COMMENT '发票号码',
  `target_type`        varchar(20)   NOT NULL                COMMENT '对象类型：channel/partner',
  `target_id`          bigint(20)    NOT NULL                COMMENT '对象ID',
  `ref_settlement_no`  varchar(32)   DEFAULT NULL            COMMENT '关联结算/提现单号',
  `invoice_title`      varchar(255)  NOT NULL                COMMENT '发票抬头',
  `tax_no`             varchar(64)   DEFAULT NULL            COMMENT '税号',
  `amount`             decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `status`             varchar(20)   NOT NULL DEFAULT 'pending_apply' COMMENT '状态：pending_apply/reviewing/issuing/issued/voided/red_offset',
  `file_url`           varchar(255)  DEFAULT NULL            COMMENT '票据附件URL',
  `express_status`     varchar(20)   DEFAULT NULL            COMMENT '快递状态',
  `issue_time`         datetime      DEFAULT NULL            COMMENT '开票时间',
  `create_by`          varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`           char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`invoice_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  KEY `idx_ref_settlement_no` (`ref_settlement_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='发票记录';


-- ---------------------------------------------------------------------
-- H17. jst_payment_pay_record —— 统一打款记录（渠道提现/赛事方结算共用）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_payment_pay_record`;
CREATE TABLE `jst_payment_pay_record` (
  `pay_record_id` bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '打款记录ID',
  `pay_no`        varchar(32)   NOT NULL                COMMENT '打款单号',
  `business_type` varchar(20)   NOT NULL                COMMENT '业务类型：rebate_withdraw渠道提现/event_settlement赛事方结算',
  `business_id`   bigint(20)    NOT NULL                COMMENT '业务单据ID',
  `target_type`   varchar(20)   NOT NULL                COMMENT '收款方类型：channel/partner',
  `target_id`     bigint(20)    NOT NULL                COMMENT '收款方ID',
  `amount`        decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '打款金额',
  `pay_account`   varchar(255)  DEFAULT NULL            COMMENT '打款账户快照',
  `pay_time`      datetime      DEFAULT NULL            COMMENT '打款时间',
  `voucher_url`   varchar(255)  DEFAULT NULL            COMMENT '打款凭证URL',
  `operator_id`   bigint(20)    DEFAULT NULL            COMMENT '操作人',
  `create_by`     varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`   datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`     varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`   datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`        varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`      char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`pay_record_id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_business` (`business_type`,`business_id`),
  KEY `idx_target` (`target_type`,`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='统一打款记录';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 10 完成：3 张表
-- =====================================================================
-- 全部 DDL 完成：10 个 sql 文件 / 51 张业务表
--   01-jst-user.sql       5  用户/参赛档案/认领映射/渠道/绑定
--   02-jst-event.sql      9  赛事方/赛事/动态表单/报名/成绩/证书*2/课程*2
--   03-jst-order.sql      8  订单主/明细/支付/退款/团队预约*2/个人预约/核销子项
--   04-jst-channel.sql    4  返点规则/计提台账/渠道结算/赛事方结算
--   05-jst-points.sql     7  积分账户/积分流水/成长流水/等级/规则/商品/兑换订单
--   06-jst-organizer.sql  2  赛事方入驻申请/渠道认证申请
--   07-jst-marketing.sql  6  优惠券模板/用户券/批次/权益模板/用户权益/核销
--   08-jst-message.sql    3  公告/消息模板/发送日志
--   09-jst-risk.sql       5  审计日志/风控规则/预警/工单/黑白名单
--   10-jst-finance.sql    3  合同/发票/统一打款
-- =====================================================================
