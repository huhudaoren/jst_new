-- =====================================================================
-- 文件名：01-jst-user.sql
-- 模块  ：jst-user 用户与身份域
-- 适配  ：MySQL 5.7 / RuoYi-Vue 代码生成器
-- 来源  ：竞赛通-产品需求文档-统一版-V4.1.md（域 A 用户与身份）
-- 说明  ：
--   1. 不包含 sys_user / sys_role / sys_dict 等若依基础表
--   2. 所有表统一 InnoDB + utf8mb4 + utf8mb4_general_ci
--   3. 全表自带若依标准审计字段：create_by / create_time / update_by / update_time / remark / del_flag
--   4. 主键采用 BIGINT 自增（业务无关 ID，可由雪花算法替换写入）
--   5. 临时档案 participant.user_id 允许 NULL（防孤儿数据规则）
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- A1. jst_user —— 用户主表（学生 / 家长 / 渠道方 业务账号）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_user`;
CREATE TABLE `jst_user` (
  `user_id`            bigint(20)    NOT NULL AUTO_INCREMENT  COMMENT '用户ID（业务正式账号主键）',
  `openid`             varchar(64)   DEFAULT NULL             COMMENT '微信小程序 openid',
  `unionid`            varchar(64)   DEFAULT NULL             COMMENT '微信开放平台 unionid',
  `mobile`             varchar(20)   DEFAULT NULL             COMMENT '手机号（学生本人或主账号）',
  `nickname`           varchar(64)   DEFAULT NULL             COMMENT '昵称',
  `avatar`             varchar(255)  DEFAULT NULL             COMMENT '头像URL',
  `real_name`          varchar(64)   DEFAULT NULL             COMMENT '真实姓名',
  `gender`             tinyint(4)    DEFAULT '0'              COMMENT '性别：0未知 1男 2女',
  `birthday`           date          DEFAULT NULL             COMMENT '出生日期',
  `id_card_no`         varchar(128)  DEFAULT NULL             COMMENT '证件号（密文存储，AES）',
  `guardian_name`      varchar(64)   DEFAULT NULL             COMMENT '监护人姓名',
  `guardian_mobile`    varchar(20)   DEFAULT NULL             COMMENT '监护人手机号',
  `user_type`          varchar(20)   NOT NULL DEFAULT 'student' COMMENT '用户类型：student学生 / parent家长 / channel渠道方',
  `current_level_id`   bigint(20)    DEFAULT NULL             COMMENT '当前等级ID，FK→jst_level_config.level_id',
  `total_points`       bigint(20)    NOT NULL DEFAULT '0'     COMMENT '累计获得积分（只增不减，用于成长统计）',
  `available_points`   bigint(20)    NOT NULL DEFAULT '0'     COMMENT '当前可用积分余额（不含冻结）',
  `frozen_points`      bigint(20)    NOT NULL DEFAULT '0'     COMMENT '冻结积分（兑换中/退款中）',
  `growth_value`       bigint(20)    NOT NULL DEFAULT '0'     COMMENT '成长值（升级用，默认只增不减）',
  `bound_channel_id`   bigint(20)    DEFAULT NULL             COMMENT '当前有效绑定渠道方ID，FK→jst_channel.channel_id',
  `status`             tinyint(4)    NOT NULL DEFAULT '1'     COMMENT '账号状态：0禁用 1正常 2封禁',
  `risk_flag`          tinyint(4)    NOT NULL DEFAULT '0'     COMMENT '风控标记：0正常 1黑名单 2待复核',
  `register_time`      datetime      NOT NULL                 COMMENT '注册时间',
  `last_login_time`    datetime      DEFAULT NULL             COMMENT '最后登录时间',
  `create_by`          varchar(64)   DEFAULT ''               COMMENT '创建人',
  `create_time`        datetime      DEFAULT NULL             COMMENT '创建时间',
  `update_by`          varchar(64)   DEFAULT ''               COMMENT '更新人',
  `update_time`        datetime      DEFAULT NULL             COMMENT '更新时间',
  `remark`             varchar(500)  DEFAULT NULL             COMMENT '备注',
  `del_flag`           char(1)       DEFAULT '0'              COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_guardian_mobile` (`guardian_mobile`),
  KEY `idx_bound_channel` (`bound_channel_id`),
  KEY `idx_user_type_status` (`user_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户主表-学生/家长/渠道方业务账号';


-- ---------------------------------------------------------------------
-- A2. jst_participant —— 临时参赛档案（核心，user_id 允许 NULL 防孤儿）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_participant`;
CREATE TABLE `jst_participant` (
  `participant_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '参赛人ID',
  `user_id`               bigint(20)   DEFAULT NULL            COMMENT '关联正式账号ID，允许NULL（防孤儿）；认领后回填',
  `participant_type`      varchar(20)  NOT NULL                COMMENT '档案类型：registered_participant正式 / temporary_participant临时',
  `name`                  varchar(64)  NOT NULL                COMMENT '参赛人姓名',
  `gender`                tinyint(4)   DEFAULT '0'             COMMENT '性别：0未知 1男 2女',
  `birthday`              date         DEFAULT NULL            COMMENT '出生日期',
  `age`                   int(11)      DEFAULT NULL            COMMENT '年龄（冗余便于检索）',
  `guardian_name`         varchar(64)  DEFAULT NULL            COMMENT '监护人姓名',
  `guardian_mobile`       varchar(20)  DEFAULT NULL            COMMENT '监护人手机号（自动认领的关键匹配项）',
  `school`                varchar(128) DEFAULT NULL            COMMENT '学校',
  `organization`          varchar(128) DEFAULT NULL            COMMENT '所属机构',
  `class_name`            varchar(64)  DEFAULT NULL            COMMENT '班级',
  `id_card_type`          varchar(20)  DEFAULT NULL            COMMENT '证件类型：id_card身份证/passport护照/...',
  `id_card_no`            varchar(128) DEFAULT NULL            COMMENT '证件号（密文）',
  `created_by_channel_id` bigint(20)   DEFAULT NULL            COMMENT '创建该档案的渠道方ID，FK→jst_channel',
  `claim_status`          varchar(20)  NOT NULL DEFAULT 'unclaimed' COMMENT '认领状态：unclaimed未认领/auto_claimed自动认领/manual_claimed人工认领/pending_manual待人工',
  `claimed_user_id`       bigint(20)   DEFAULT NULL            COMMENT '已认领的正式用户ID（冗余便于检索）',
  `claimed_time`          datetime     DEFAULT NULL            COMMENT '认领生效时间',
  `visible_scope`         varchar(20)  NOT NULL DEFAULT 'channel_only' COMMENT '可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方',
  `create_by`             varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`           datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`             varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`           datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`                varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`              char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`participant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_guardian_mobile_name` (`guardian_mobile`,`name`),
  KEY `idx_created_channel` (`created_by_channel_id`),
  KEY `idx_claim_status` (`claim_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='临时参赛档案-允许无正式账号存在';


-- ---------------------------------------------------------------------
-- A3. jst_participant_user_map —— 参赛档案↔正式用户 认领映射
--     说明：底层订单/成绩/证书继续保留 participant_id 快照，
--           通过本表归集到 user_id，不直接覆盖原始流水主键。
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_participant_user_map`;
CREATE TABLE `jst_participant_user_map` (
  `map_id`         bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '映射ID',
  `participant_id` bigint(20)  NOT NULL                COMMENT '临时档案ID，FK→jst_participant',
  `user_id`        bigint(20)  NOT NULL                COMMENT '正式用户ID，FK→jst_user',
  `claim_method`   varchar(20) NOT NULL                COMMENT '认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动',
  `claim_time`     datetime    NOT NULL                COMMENT '认领生效时间',
  `operator_id`    bigint(20)  DEFAULT NULL            COMMENT '操作管理员ID（manual_admin 时必填）',
  `status`         varchar(20) NOT NULL DEFAULT 'active' COMMENT '映射状态：active生效/revoked已撤销',
  `revoke_reason`  varchar(255) DEFAULT NULL           COMMENT '撤销原因',
  `create_by`      varchar(64)  DEFAULT ''             COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL           COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''             COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL           COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'            COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`map_id`),
  KEY `idx_participant_id` (`participant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参赛档案-正式用户认领映射表';


-- ---------------------------------------------------------------------
-- A4. jst_channel —— 渠道方档案（教师 / 机构 / 个人）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_channel`;
CREATE TABLE `jst_channel` (
  `channel_id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '渠道方ID',
  `user_id`              bigint(20)   NOT NULL                COMMENT '关联用户账号ID，FK→jst_user',
  `channel_type`         varchar(20)  NOT NULL                COMMENT '渠道类型：teacher教师/organization机构/individual个人',
  `channel_name`         varchar(128) NOT NULL                COMMENT '渠道方名称',
  `contact_mobile`       varchar(20)  NOT NULL                COMMENT '联系手机',
  `id_card_no`           varchar(128) DEFAULT NULL            COMMENT '证件号（密文）',
  `business_license_no`  varchar(64)  DEFAULT NULL            COMMENT '营业执照号（机构必填）',
  `cert_materials_json`  json         DEFAULT NULL            COMMENT '认证材料附件JSON：[{type,name,url}]',
  `auth_status`          varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '认证状态：pending待审/approved通过/rejected驳回/suspended暂停',
  `auth_time`            datetime     DEFAULT NULL            COMMENT '认证通过时间',
  `auth_remark`          varchar(255) DEFAULT NULL            COMMENT '审核备注',
  `current_level_id`     bigint(20)   DEFAULT NULL            COMMENT '当前等级ID，FK→jst_level_config',
  `status`               tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停用 1启用',
  `risk_flag`            tinyint(4)   NOT NULL DEFAULT '0'    COMMENT '风控标记：0正常 1黑名单 2待复核',
  `tags`                 varchar(255) DEFAULT NULL            COMMENT '渠道标签（逗号分隔）',
  `bank_account_name`    varchar(64)  DEFAULT NULL            COMMENT '收款户名',
  `bank_account_no`      varchar(64)  DEFAULT NULL            COMMENT '收款账号（密文）',
  `bank_name`            varchar(128) DEFAULT NULL            COMMENT '开户行',
  `create_by`            varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`          datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`            varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`          datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`             char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`channel_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_channel_type_status` (`channel_type`,`status`),
  KEY `idx_auth_status` (`auth_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='渠道方档案表';


-- ---------------------------------------------------------------------
-- A5. jst_student_channel_binding —— 学生-渠道方绑定关系
--     业务约束：同一 user_id 同一时刻仅允许 1 条 status='active'
--     由 service 层 + Redisson 分布式锁保证（DB 仅做检索）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_student_channel_binding`;
CREATE TABLE `jst_student_channel_binding` (
  `binding_id`     bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  `user_id`        bigint(20)  NOT NULL                COMMENT '学生用户ID，FK→jst_user',
  `channel_id`     bigint(20)  NOT NULL                COMMENT '渠道方ID，FK→jst_channel',
  `bind_time`      datetime    NOT NULL                COMMENT '绑定生效时间',
  `unbind_time`    datetime    DEFAULT NULL            COMMENT '失效时间',
  `status`         varchar(20) NOT NULL DEFAULT 'active' COMMENT '状态：active生效/expired过期/replaced被覆盖/manual_unbound人工解绑',
  `unbind_reason`  varchar(255) DEFAULT NULL           COMMENT '解绑原因',
  `operator_id`    bigint(20)   DEFAULT NULL           COMMENT '解绑操作人ID',
  `create_by`      varchar(64)  DEFAULT ''             COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL           COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''             COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL           COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL           COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'            COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`binding_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_channel_status` (`channel_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生-渠道方绑定关系表（同一user同时仅1条active）';

SET FOREIGN_KEY_CHECKS = 1;
-- =====================================================================
-- 批 1 完成：5 张表 / jst-user 模块
-- 下一批 02-jst-event.sql：B域(4) + G域(5) = 9 张表
-- =====================================================================
