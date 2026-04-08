-- =====================================================================
-- 文件名：04-jst-channel.sql
-- 模块  ：jst-channel 渠道返点与结算域
-- 适配  ：MySQL 5.7 / RuoYi-Vue
-- 来源  ：竞赛通-V4.1 域 D，共 4 张表
-- 关键并发：
--   1. rebate_ledger 计提/状态推进 → 必须事务 + 行锁
--   2. 提现 approved 时按 channel_id 锁定 → 优先抵扣 negative 台账
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- D1. jst_rebate_rule —— 返点规则
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_rebate_rule`;
CREATE TABLE `jst_rebate_rule` (
  `rule_id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `contest_id`        bigint(20)    NOT NULL                COMMENT '赛事ID',
  `channel_id`        bigint(20)    DEFAULT NULL            COMMENT '渠道方ID（NULL=按赛事默认规则）',
  `rebate_mode`       varchar(10)   NOT NULL                COMMENT '返点模式：rate按比例 / fixed固定金额',
  `rebate_value`      decimal(12,4) NOT NULL DEFAULT '0.0000' COMMENT '返点值（rate=比例如0.1, fixed=金额）',
  `service_fee_mode`  varchar(10)   NOT NULL DEFAULT 'none' COMMENT '服务费模式：fixed/rate/none',
  `service_fee_value` decimal(12,4) NOT NULL DEFAULT '0.0000' COMMENT '服务费值',
  `effective_time`    datetime      NOT NULL                COMMENT '生效时间',
  `expire_time`       datetime      DEFAULT NULL            COMMENT '失效时间',
  `status`            tinyint(4)    NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`         varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`       datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`         varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`       datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`            varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`          char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`rule_id`),
  KEY `idx_contest_channel` (`contest_id`,`channel_id`),
  KEY `idx_effective_window` (`effective_time`,`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道返点规则';


-- ---------------------------------------------------------------------
-- D2. jst_rebate_ledger —— 返点计提台账
--     状态：pending_accrual → withdrawable → in_review → paid / rolled_back / negative
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_rebate_ledger`;
CREATE TABLE `jst_rebate_ledger` (
  `ledger_id`       bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '台账ID',
  `order_id`        bigint(20)    NOT NULL                COMMENT '订单ID',
  `item_id`         bigint(20)    NOT NULL                COMMENT '订单明细ID',
  `channel_id`      bigint(20)    NOT NULL                COMMENT '渠道方ID',
  `contest_id`      bigint(20)    NOT NULL                COMMENT '赛事ID',
  `rule_id`         bigint(20)    DEFAULT NULL            COMMENT '命中规则ID',
  `list_amount`     decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '标价金额',
  `net_pay_amount`  decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '净实付金额',
  `service_fee`    decimal(12,2)  NOT NULL DEFAULT '0.00' COMMENT '服务费',
  `rebate_base`     decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '返点基数 = max(0, 标价 - 服务费)',
  `rebate_amount`   decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '返点金额（可负）',
  `direction`       varchar(10)   NOT NULL DEFAULT 'positive' COMMENT '方向：positive正向 / negative负向',
  `status`          varchar(20)   NOT NULL DEFAULT 'pending_accrual' COMMENT '台账状态：pending_accrual/withdrawable/in_review/paid/rolled_back/negative',
  `accrual_time`    datetime      DEFAULT NULL            COMMENT '计提时间',
  `event_end_time`  datetime      DEFAULT NULL            COMMENT '赛事结束时间快照',
  `settlement_id`   bigint(20)    DEFAULT NULL            COMMENT '关联结算单ID',
  `create_by`       varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`     datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`       varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`     datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`          varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`        char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`ledger_id`),
  KEY `idx_channel_status` (`channel_id`,`status`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_settlement_id` (`settlement_id`),
  KEY `idx_contest_id` (`contest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='返点计提台账';


-- ---------------------------------------------------------------------
-- D3. jst_rebate_settlement —— 渠道提现/结算单
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_rebate_settlement`;
CREATE TABLE `jst_rebate_settlement` (
  `settlement_id`          bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '结算单ID',
  `settlement_no`          varchar(32)   NOT NULL                COMMENT '结算单号',
  `channel_id`             bigint(20)    NOT NULL                COMMENT '渠道方ID',
  `apply_amount`           decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '申请金额',
  `negative_offset_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '负向台账抵扣金额',
  `actual_pay_amount`      decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '实际打款金额',
  `invoice_status`         varchar(20)   NOT NULL DEFAULT 'none' COMMENT '发票状态：none/pending/issued/voided',
  `invoice_id`             bigint(20)    DEFAULT NULL            COMMENT '关联发票ID',
  `bank_account_snapshot`  varchar(255)  DEFAULT NULL            COMMENT '收款账户快照',
  `status`                 varchar(20)   NOT NULL DEFAULT 'pending' COMMENT '状态：pending/reviewing/rejected/approved/paid',
  `audit_remark`           varchar(255)  DEFAULT NULL            COMMENT '审核备注',
  `pay_voucher_url`        varchar(255)  DEFAULT NULL            COMMENT '打款凭证URL',
  `apply_time`             datetime      NOT NULL                COMMENT '申请时间',
  `pay_time`               datetime      DEFAULT NULL            COMMENT '打款时间',
  `create_by`              varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`            datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`              varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`            datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`                 varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`               char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`settlement_id`),
  UNIQUE KEY `uk_settlement_no` (`settlement_no`),
  KEY `idx_channel_status` (`channel_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道提现/结算单';


-- ---------------------------------------------------------------------
-- D4. jst_event_settlement —— 赛事方结算单
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_event_settlement`;
CREATE TABLE `jst_event_settlement` (
  `event_settlement_id`  bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '结算单ID',
  `settlement_no`        varchar(32)   NOT NULL                COMMENT '结算单号',
  `partner_id`           bigint(20)    NOT NULL                COMMENT '赛事方ID',
  `contest_id`           bigint(20)    NOT NULL                COMMENT '赛事ID',
  `total_list_amount`    decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '标价合计',
  `total_coupon_amount`  decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券合计',
  `total_points_amount`  decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣合计',
  `platform_bear_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '平台承担合计',
  `total_net_pay`        decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '用户净实付合计',
  `total_refund`         decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款合计',
  `total_rebate`         decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '渠道返点合计',
  `total_service_fee`    decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '服务费合计',
  `contract_deduction`   decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '合同约定扣项',
  `final_amount`         decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '最终结算金额',
  `status`               varchar(20)   NOT NULL DEFAULT 'pending_confirm' COMMENT '状态：pending_confirm/reviewing/rejected/pending_pay/paid',
  `audit_remark`         varchar(255)  DEFAULT NULL            COMMENT '审核备注',
  `pay_voucher_url`      varchar(255)  DEFAULT NULL            COMMENT '打款凭证URL',
  `pay_time`             datetime      DEFAULT NULL            COMMENT '打款时间',
  `create_by`            varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`          datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`            varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`          datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`               varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`             char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`event_settlement_id`),
  UNIQUE KEY `uk_settlement_no` (`settlement_no`),
  KEY `idx_partner_status` (`partner_id`,`status`),
  KEY `idx_contest_id` (`contest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赛事方结算单';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 4 完成：4 张表
