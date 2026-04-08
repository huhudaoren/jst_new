-- =====================================================================
-- 文件名：03-jst-order.sql
-- 模块  ：jst-order 资金流转域（订单/混合支付/退款/团队预约/部分核销）
-- 适配  ：MySQL 5.7 / RuoYi-Vue
-- 来源  ：竞赛通-V4.1 域 C，共 8 张表
-- 关键并发：
--   1. team_appointment.writeoff_persons 扣减 → 必须 Redisson 分布式锁
--   2. order_status / refund 资金流转 → 服务层 @Transactional(rollbackFor)
--   3. order_item 是最小核算单元，所有分摊/回滚以 item 为粒度
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- C1. jst_order_main —— 订单主表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_order_main`;
CREATE TABLE `jst_order_main` (
  `order_id`             bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`             varchar(32)   NOT NULL                COMMENT '订单号（业务可读）',
  `order_type`           varchar(20)   NOT NULL                COMMENT '订单类型：normal/zero_price零元/full_deduct全额抵扣/exchange兑换/appointment预约',
  `business_type`        varchar(20)   NOT NULL                COMMENT '业务类型：enroll报名/course课程/mall商城/appointment预约',
  `user_id`              bigint(20)    DEFAULT NULL            COMMENT '学生用户ID',
  `participant_id`       bigint(20)    NOT NULL                COMMENT '参赛人ID快照（含临时档案）',
  `channel_id`           bigint(20)    DEFAULT NULL            COMMENT '支付时绑定的渠道方ID（锁定不变）',
  `contest_id`           bigint(20)    DEFAULT NULL            COMMENT '关联赛事ID',
  `partner_id`           bigint(20)    DEFAULT NULL            COMMENT '关联赛事方ID',
  `team_appointment_id`  bigint(20)    DEFAULT NULL            COMMENT '关联团队预约主记录ID',
  `list_amount`          decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单标价金额',
  `coupon_amount`        decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券抵扣金额',
  `points_deduct_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣折现金额',
  `points_used`          bigint(20)    NOT NULL DEFAULT '0'    COMMENT '使用积分数（原始积分）',
  `platform_bear_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '平台承担的优惠金额',
  `net_pay_amount`       decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '用户净实付金额',
  `service_fee`          decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '平台服务费',
  `pay_method`           varchar(20)   DEFAULT NULL            COMMENT '支付方式：wechat/bank_transfer/points/points_cash_mix',
  `pay_initiator`        varchar(20)   DEFAULT NULL            COMMENT '支付发起方：self本人/channel渠道代付',
  `pay_initiator_id`     bigint(20)    DEFAULT NULL            COMMENT '发起方ID',
  `pay_time`             datetime      DEFAULT NULL            COMMENT '支付完成时间',
  `order_status`         varchar(20)   NOT NULL DEFAULT 'created' COMMENT '订单状态：created/pending_pay/paid/reviewing/in_service/aftersale/completed/cancelled/closed',
  `refund_status`        varchar(20)   NOT NULL DEFAULT 'none' COMMENT '退款状态：none/partial/full',
  `aftersale_deadline`   datetime      DEFAULT NULL            COMMENT '售后截止时间 = max(赛事结束,成绩发布) + N天',
  `coupon_id`            bigint(20)    DEFAULT NULL            COMMENT '使用的用户券ID',
  `allow_self_refund`    tinyint(4)    NOT NULL DEFAULT '1'    COMMENT '是否允许用户自助退款：0否 1是',
  `create_by`            varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`          datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`            varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`          datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`               varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`             char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id`             (`user_id`),
  KEY `idx_participant_id`      (`participant_id`),
  KEY `idx_channel_id`          (`channel_id`),
  KEY `idx_contest_id`          (`contest_id`),
  KEY `idx_team_appointment_id` (`team_appointment_id`),
  KEY `idx_order_status`        (`order_status`),
  KEY `idx_pay_time`            (`pay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单主表';


-- ---------------------------------------------------------------------
-- C2. jst_order_item —— 订单明细（最小核算单元）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_order_item`;
CREATE TABLE `jst_order_item` (
  `item_id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`          bigint(20)    NOT NULL                COMMENT '订单ID',
  `sku_type`          varchar(20)   NOT NULL                COMMENT '类型：enroll/appointment_member/goods/course',
  `ref_id`            bigint(20)    DEFAULT NULL            COMMENT '引用业务ID（报名/成员/商品/课程）',
  `item_name`         varchar(255)  NOT NULL                COMMENT '商品/项目名称',
  `quantity`          int(11)       NOT NULL DEFAULT '1'    COMMENT '数量',
  `list_amount`       decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '标价金额',
  `coupon_share`      decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券分摊金额',
  `points_share`      decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣分摊金额',
  `net_pay_share`     decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '净实付分摊金额',
  `service_fee_share` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '服务费分摊',
  `rebate_share`      decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '渠道返点分摊（计提阶段写入）',
  `refund_amount`     decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '已退现金累计',
  `refund_points`     bigint(20)    NOT NULL DEFAULT '0'    COMMENT '已回退积分累计',
  `create_by`         varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`       datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`         varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`       datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`            varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`          char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_ref` (`sku_type`,`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细（最小核算单元，承载分摊与回滚）';


-- ---------------------------------------------------------------------
-- C3. jst_payment_record —— 支付记录
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_payment_record`;
CREATE TABLE `jst_payment_record` (
  `payment_id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `payment_no`           varchar(64)   NOT NULL                COMMENT '内部支付流水号',
  `order_id`             bigint(20)    NOT NULL                COMMENT '订单ID',
  `pay_method`           varchar(20)   NOT NULL                COMMENT '支付方式：wechat/bank_transfer/points/mix',
  `cash_amount`          decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '现金金额',
  `points_amount`        decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '积分折现金额',
  `points_used`          bigint(20)    NOT NULL DEFAULT '0'    COMMENT '消耗积分数',
  `third_party_no`       varchar(128)  DEFAULT NULL            COMMENT '第三方流水号（微信/银行）',
  `voucher_url`          varchar(255)  DEFAULT NULL            COMMENT '对公转账凭证URL',
  `voucher_audit_status` varchar(20)   DEFAULT NULL            COMMENT '凭证审核状态：pending/approved/rejected',
  `pay_status`           varchar(20)   NOT NULL DEFAULT 'pending' COMMENT '支付状态：pending/success/failed/refunding/refunded',
  `pay_time`             datetime      DEFAULT NULL            COMMENT '完成时间',
  `operator_id`          bigint(20)    DEFAULT NULL            COMMENT '操作人ID（对公转账录入员）',
  `create_by`            varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`          datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`            varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`          datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`               varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`             char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付记录';


-- ---------------------------------------------------------------------
-- C4. jst_refund_record —— 退款/售后单
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_refund_record`;
CREATE TABLE `jst_refund_record` (
  `refund_id`        bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '售后ID',
  `refund_no`        varchar(32)   NOT NULL                COMMENT '售后单号',
  `order_id`         bigint(20)    NOT NULL                COMMENT '关联订单',
  `item_id`          bigint(20)    DEFAULT NULL            COMMENT '关联明细（部分退款时必填）',
  `refund_type`      varchar(20)   NOT NULL                COMMENT '类型：refund_only仅退款/return_refund退货退款/special_refund特批',
  `apply_source`     varchar(20)   NOT NULL                COMMENT '申请来源：user/admin/system',
  `reason`           varchar(255)  DEFAULT NULL            COMMENT '申请原因',
  `apply_cash`       decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '申请退现金',
  `apply_points`     bigint(20)    NOT NULL DEFAULT '0'    COMMENT '申请退积分',
  `actual_cash`      decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '实退现金',
  `actual_points`    bigint(20)    NOT NULL DEFAULT '0'    COMMENT '实退积分',
  `coupon_returned`  tinyint(4)    NOT NULL DEFAULT '0'    COMMENT '优惠券是否回退：0否 1是（仅整单全退且原券有效期内）',
  `status`           varchar(20)   NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected/refunding/completed/closed',
  `audit_remark`     varchar(255)  DEFAULT NULL            COMMENT '审核备注',
  `operator_id`      bigint(20)    DEFAULT NULL            COMMENT '操作人',
  `complete_time`    datetime      DEFAULT NULL            COMMENT '完成时间',
  `create_by`        varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`      datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`        varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`      datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`           varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`         char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`refund_id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退款/售后单';


-- ---------------------------------------------------------------------
-- C5. jst_team_appointment —— 团队预约主记录
--     业务约束：扣减 writeoff_persons / 状态推进必须使用 Redisson 锁
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_team_appointment`;
CREATE TABLE `jst_team_appointment` (
  `team_appointment_id` bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '团队预约ID',
  `team_no`             varchar(32)  NOT NULL                COMMENT '团队预约号',
  `contest_id`          bigint(20)   NOT NULL                COMMENT '关联活动ID',
  `channel_id`          bigint(20)   NOT NULL                COMMENT '渠道方ID',
  `team_name`           varchar(128) NOT NULL                COMMENT '团队/批次名称',
  `appointment_date`    date         NOT NULL                COMMENT '预约日期',
  `session_code`        varchar(32)  DEFAULT NULL            COMMENT '场次/时段编码',
  `total_persons`       int(11)      NOT NULL DEFAULT '0'    COMMENT '预约总人数',
  `member_persons`      int(11)      NOT NULL DEFAULT '0'    COMMENT '正式成员人数',
  `extra_persons`       int(11)      NOT NULL DEFAULT '0'    COMMENT '额外人数（家长/工作人员）',
  `extra_list_json`     json         DEFAULT NULL            COMMENT '额外名单JSON',
  `writeoff_persons`    int(11)      NOT NULL DEFAULT '0'    COMMENT '已核销人数',
  `status`              varchar(30)  NOT NULL DEFAULT 'booked' COMMENT '团队预约状态：booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/no_show/cancelled/expired',
  `create_by`           varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`         datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`           varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`         datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`              varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`            char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`team_appointment_id`),
  UNIQUE KEY `uk_team_no` (`team_no`),
  KEY `idx_contest_id` (`contest_id`),
  KEY `idx_channel_id` (`channel_id`),
  KEY `idx_date_session` (`appointment_date`,`session_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='团队预约主记录';


-- ---------------------------------------------------------------------
-- C6. jst_team_appointment_member —— 团队预约成员明细
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_team_appointment_member`;
CREATE TABLE `jst_team_appointment_member` (
  `member_id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '成员明细ID',
  `team_appointment_id` bigint(20)   NOT NULL                COMMENT '团队预约主ID',
  `user_id`             bigint(20)   DEFAULT NULL            COMMENT '正式用户ID',
  `participant_id`      bigint(20)   NOT NULL                COMMENT '参赛人ID（含临时档案）',
  `member_no`           varchar(32)  NOT NULL                COMMENT '成员编号',
  `name_snapshot`       varchar(64)  NOT NULL                COMMENT '姓名快照',
  `mobile_snapshot`     varchar(20)  DEFAULT NULL            COMMENT '手机快照',
  `sub_order_id`        bigint(20)   DEFAULT NULL            COMMENT '个人子订单ID',
  `writeoff_status`     varchar(20)  NOT NULL DEFAULT 'unused' COMMENT '核销状态：unused/used/expired/voided',
  `create_by`           varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`         datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`           varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`         datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`              varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`            char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`member_id`),
  KEY `idx_team_id` (`team_appointment_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_participant_id` (`participant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='团队预约成员明细';


-- ---------------------------------------------------------------------
-- C7. jst_appointment_record —— 个人预约记录
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_appointment_record`;
CREATE TABLE `jst_appointment_record` (
  `appointment_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `appointment_no`      varchar(32)  NOT NULL                COMMENT '预约号',
  `contest_id`          bigint(20)   NOT NULL                COMMENT '关联活动',
  `user_id`             bigint(20)   DEFAULT NULL            COMMENT '用户ID',
  `participant_id`      bigint(20)   NOT NULL                COMMENT '参赛人ID',
  `channel_id`          bigint(20)   DEFAULT NULL            COMMENT '锁定渠道方',
  `appointment_type`    varchar(20)  NOT NULL                COMMENT '类型：individual/team',
  `team_appointment_id` bigint(20)   DEFAULT NULL            COMMENT '关联团队主记录',
  `appointment_date`    date         NOT NULL                COMMENT '预约日期',
  `session_code`        varchar(32)  DEFAULT NULL            COMMENT '场次/时段',
  `order_id`            bigint(20)   DEFAULT NULL            COMMENT '关联订单',
  `main_status`         varchar(30)  NOT NULL DEFAULT 'unbooked' COMMENT '主状态：unbooked/booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/cancelled/expired/no_show',
  `qr_code`             varchar(255) DEFAULT NULL            COMMENT '主二维码URL',
  `create_by`           varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`         datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`           varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`         datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`              varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`            char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `uk_appointment_no` (`appointment_no`),
  KEY `idx_contest_id` (`contest_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_team_id` (`team_appointment_id`),
  KEY `idx_main_status` (`main_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='个人预约记录';


-- ---------------------------------------------------------------------
-- C8. jst_appointment_writeoff_item —— 核销子项
--     业务规则：每个子项独立流转，不联动其他子项
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_appointment_writeoff_item`;
CREATE TABLE `jst_appointment_writeoff_item` (
  `writeoff_item_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '子项ID',
  `appointment_id`      bigint(20)   DEFAULT NULL            COMMENT '个人预约ID',
  `team_appointment_id` bigint(20)   DEFAULT NULL            COMMENT '团队预约ID',
  `item_type`           varchar(20)  NOT NULL                COMMENT '子项类型：arrival到场/gift礼品/ceremony仪式/custom自定义',
  `item_name`           varchar(64)  DEFAULT NULL            COMMENT '自定义子项名称',
  `qr_code`             varchar(255) NOT NULL                COMMENT '独立二维码URL',
  `status`              varchar(20)  NOT NULL DEFAULT 'unused' COMMENT '状态：unused/used/expired/voided',
  `writeoff_time`       datetime     DEFAULT NULL            COMMENT '核销时间',
  `writeoff_user_id`    bigint(20)   DEFAULT NULL            COMMENT '核销操作人',
  `writeoff_terminal`   varchar(32)  DEFAULT NULL            COMMENT '核销终端',
  `writeoff_qty`        int(11)      NOT NULL DEFAULT '0'    COMMENT '已核销人数（团队场景累加）',
  `total_qty`           int(11)      NOT NULL DEFAULT '0'    COMMENT '总人数（团队场景）',
  `create_by`           varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`         datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`           varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`         datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`              varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`            char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`writeoff_item_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_team_id` (`team_appointment_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='核销子项（团队部分核销/到场/礼品/仪式独立流转）';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 3 完成：8 张表
