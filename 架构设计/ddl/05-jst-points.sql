-- =====================================================================
-- 文件名：05-jst-points.sql
-- 模块  ：jst-points 积分/等级/商城域
-- 来源  ：竞赛通-V4.1 域 E，共 7 张表
-- 关键并发：
--   1. points_account 扣减/冻结 → 乐观锁(version) 或 行锁
--   2. mall_goods.stock 扣减 → Redisson 锁防超卖
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- E1. jst_points_account —— 积分账户（学生 / 渠道）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_points_account`;
CREATE TABLE `jst_points_account` (
  `account_id`        bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `owner_type`        varchar(10) NOT NULL                COMMENT '持有者类型：student/channel',
  `owner_id`          bigint(20)  NOT NULL                COMMENT '持有者业务ID',
  `available_points`  bigint(20)  NOT NULL DEFAULT '0'    COMMENT '可用积分',
  `frozen_points`     bigint(20)  NOT NULL DEFAULT '0'    COMMENT '冻结积分（兑换中/退款中）',
  `total_earn`        bigint(20)  NOT NULL DEFAULT '0'    COMMENT '累计获取',
  `total_spend`       bigint(20)  NOT NULL DEFAULT '0'    COMMENT '累计消耗',
  `growth_value`      bigint(20)  NOT NULL DEFAULT '0'    COMMENT '成长值（升级用）',
  `current_level_id`  bigint(20)  DEFAULT NULL            COMMENT '当前等级ID',
  `version`           int(11)     NOT NULL DEFAULT '0'    COMMENT '乐观锁版本号',
  `create_by`         varchar(64) DEFAULT ''              COMMENT '创建人',
  `create_time`       datetime    DEFAULT NULL            COMMENT '创建时间',
  `update_by`         varchar(64) DEFAULT ''              COMMENT '更新人',
  `update_time`       datetime    DEFAULT NULL            COMMENT '更新时间',
  `remark`            varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`          char(1)     DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_owner` (`owner_type`,`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分账户';


-- ---------------------------------------------------------------------
-- E2. jst_points_ledger —— 积分流水
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_points_ledger`;
CREATE TABLE `jst_points_ledger` (
  `ledger_id`      bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `account_id`     bigint(20)  NOT NULL                COMMENT '账户ID',
  `owner_type`     varchar(10) NOT NULL                COMMENT '持有者类型：student/channel',
  `owner_id`       bigint(20)  NOT NULL                COMMENT '持有者ID',
  `change_type`    varchar(20) NOT NULL                COMMENT '变更类型：earn/spend/freeze/unfreeze/adjust/rollback',
  `source_type`    varchar(20) NOT NULL                COMMENT '来源：enroll/course/sign/invite/learn/award/exchange/manual/refund',
  `source_ref_id`  bigint(20)  DEFAULT NULL            COMMENT '来源单据ID',
  `points_change`  bigint(20)  NOT NULL                COMMENT '积分变化（正负）',
  `balance_after`  bigint(20)  NOT NULL                COMMENT '变更后余额',
  `operator_id`    bigint(20)  DEFAULT NULL            COMMENT '操作人',
  `create_by`      varchar(64) DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime    DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64) DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime    DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`       char(1)     DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`ledger_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_owner` (`owner_type`,`owner_id`),
  KEY `idx_source` (`source_type`,`source_ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分流水';


-- ---------------------------------------------------------------------
-- E3. jst_growth_ledger —— 成长值流水
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_growth_ledger`;
CREATE TABLE `jst_growth_ledger` (
  `ledger_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `account_id`    bigint(20)  NOT NULL                COMMENT '账户ID',
  `change_type`   varchar(20) NOT NULL                COMMENT '类型：earn/adjust/level_up',
  `source_type`   varchar(20) NOT NULL                COMMENT '来源：enroll/course/sign/invite/learn/award/manual',
  `source_ref_id` bigint(20)  DEFAULT NULL            COMMENT '来源ID',
  `growth_change` bigint(20)  NOT NULL                COMMENT '变化值',
  `balance_after` bigint(20)  NOT NULL                COMMENT '变更后余额',
  `level_before`  bigint(20)  DEFAULT NULL            COMMENT '升级前等级',
  `level_after`   bigint(20)  DEFAULT NULL            COMMENT '升级后等级',
  `create_by`     varchar(64) DEFAULT ''              COMMENT '创建人',
  `create_time`   datetime    DEFAULT NULL            COMMENT '创建时间',
  `update_by`     varchar(64) DEFAULT ''              COMMENT '更新人',
  `update_time`   datetime    DEFAULT NULL            COMMENT '更新时间',
  `remark`        varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`      char(1)     DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`ledger_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成长值流水';


-- ---------------------------------------------------------------------
-- E4. jst_level_config —— 等级配置
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_level_config`;
CREATE TABLE `jst_level_config` (
  `level_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `level_code`       varchar(32)  NOT NULL                COMMENT '等级编码',
  `level_name`       varchar(64)  NOT NULL                COMMENT '等级名称',
  `level_no`         int(11)      NOT NULL                COMMENT '等级序号（用于排序与升降）',
  `growth_threshold` bigint(20)   NOT NULL DEFAULT '0'    COMMENT '成长值门槛',
  `icon`             varchar(255) DEFAULT NULL            COMMENT '等级图标URL',
  `applicable_role`  varchar(20)  NOT NULL DEFAULT 'student' COMMENT '适用角色：student/channel/both',
  `status`           tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`        varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`      datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`        varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`      datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`           varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`         char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`level_id`),
  UNIQUE KEY `uk_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='等级配置';


-- ---------------------------------------------------------------------
-- E5. jst_points_rule —— 积分/成长值规则
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_points_rule`;
CREATE TABLE `jst_points_rule` (
  `rule_id`         bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_type`       varchar(20)   NOT NULL                COMMENT '类型：points积分 / growth成长值',
  `source_type`    varchar(20)    NOT NULL                COMMENT '来源行为：enroll/course/sign/invite/learn/award',
  `reward_mode`     varchar(10)   NOT NULL                COMMENT '模式：fixed/rate',
  `reward_value`    decimal(12,4) NOT NULL                COMMENT '数值',
  `applicable_role` varchar(20)   NOT NULL DEFAULT 'student' COMMENT '适用角色：student/channel/both',
  `effective_time`  datetime      DEFAULT NULL            COMMENT '生效时间',
  `expire_time`     datetime      DEFAULT NULL            COMMENT '失效时间',
  `mutex_group`     varchar(32)   DEFAULT NULL            COMMENT '互斥组（同组规则同时只命中一个）',
  `status`          tinyint(4)    NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`       varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`     datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`       varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`     datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`          varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`        char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`rule_id`),
  KEY `idx_type_source` (`rule_type`,`source_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分/成长值规则';


-- ---------------------------------------------------------------------
-- E6. jst_mall_goods —— 积分商城商品
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_mall_goods`;
CREATE TABLE `jst_mall_goods` (
  `goods_id`        bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name`      varchar(255)  NOT NULL                COMMENT '商品名称',
  `goods_type`      varchar(10)   NOT NULL                COMMENT '类型：virtual虚拟 / physical实物',
  `cover_image`     varchar(255)  DEFAULT NULL            COMMENT '主图',
  `description`     text          DEFAULT NULL            COMMENT '描述',
  `points_price`    bigint(20)    NOT NULL DEFAULT '0'    COMMENT '所需积分',
  `cash_price`      decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '现金补差金额',
  `stock`           int(11)       NOT NULL DEFAULT '0'    COMMENT '库存',
  `stock_warning`   int(11)       DEFAULT NULL            COMMENT '预警阈值',
  `role_limit`      varchar(20)   DEFAULT NULL            COMMENT '角色限制：student/channel/both',
  `status`          varchar(10)   NOT NULL DEFAULT 'draft' COMMENT '状态：on/off/draft',
  `create_by`       varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`     datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`       varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`     datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`          varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`        char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`goods_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分商城商品';


-- ---------------------------------------------------------------------
-- E7. jst_mall_exchange_order —— 兑换订单
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_mall_exchange_order`;
CREATE TABLE `jst_mall_exchange_order` (
  `exchange_id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '兑换订单ID',
  `exchange_no`           varchar(32)   NOT NULL                COMMENT '单号',
  `user_id`               bigint(20)    NOT NULL                COMMENT '用户ID',
  `goods_id`              bigint(20)    NOT NULL                COMMENT '商品ID',
  `quantity`              int(11)       NOT NULL DEFAULT '1'    COMMENT '数量',
  `points_used`           bigint(20)    NOT NULL DEFAULT '0'    COMMENT '消耗积分',
  `cash_amount`           decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '现金补差金额',
  `order_id`              bigint(20)    DEFAULT NULL            COMMENT '关联现金支付订单ID',
  `address_snapshot_json` json          DEFAULT NULL            COMMENT '收货地址快照JSON',
  `status`                varchar(20)   NOT NULL DEFAULT 'pending_pay' COMMENT '订单状态：pending_pay/paid/pending_ship/shipped/completed/aftersale/closed',
  `logistics_company`     varchar(64)   DEFAULT NULL            COMMENT '物流公司',
  `logistics_no`          varchar(64)   DEFAULT NULL            COMMENT '物流单号',
  `ship_time`             datetime      DEFAULT NULL            COMMENT '发货时间',
  `complete_time`         datetime      DEFAULT NULL            COMMENT '完成时间',
  `aftersale_status`      varchar(20)   DEFAULT NULL            COMMENT '售后状态：none/applying/refunding/refunded',
  `create_by`             varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`           datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`             varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`           datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`                varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`              char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`exchange_id`),
  UNIQUE KEY `uk_exchange_no` (`exchange_no`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分商城兑换订单';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 5 完成：7 张表
