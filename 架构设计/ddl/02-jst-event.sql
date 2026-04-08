-- =====================================================================
-- 文件名：02-jst-event.sql
-- 模块  ：jst-event 赛事报名 + 成绩证书课程域
-- 适配  ：MySQL 5.7 / RuoYi-Vue
-- 来源  ：竞赛通-V4.1 域 B（4 张）+ 域 G（5 张）= 9 张表
-- 关键  ：动态表单模板 schema_json + 报名快照 form_snapshot_json
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- B1. jst_event_partner —— 赛事方档案
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_event_partner`;
CREATE TABLE `jst_event_partner` (
  `partner_id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '赛事方ID',
  `user_id`              bigint(20)   DEFAULT NULL            COMMENT '关联sys_user.user_id(审核通过后自动创建并回填)',
  `partner_name`         varchar(128) NOT NULL                COMMENT '赛事方名称',
  `contact_name`         varchar(64)  NOT NULL                COMMENT '联系人姓名',
  `contact_mobile`       varchar(20)  NOT NULL                COMMENT '联系电话',
  `business_license_no`  varchar(64)  DEFAULT NULL            COMMENT '营业执照号',
  `qualification_json`   json         DEFAULT NULL            COMMENT '资质材料附件JSON',
  `settlement_info_json` json         DEFAULT NULL            COMMENT '结算信息JSON（户名/账号/开户行）',
  `invoice_info_json`    json         DEFAULT NULL            COMMENT '发票信息JSON',
  `contract_files_json`  json         DEFAULT NULL            COMMENT '合同附件JSON',
  `audit_status`         varchar(20)  NOT NULL DEFAULT 'draft' COMMENT '入驻审核状态：draft/pending/approved/rejected/supplement',
  `audit_time`           datetime     DEFAULT NULL            COMMENT '审核通过时间',
  `audit_remark`         varchar(255) DEFAULT NULL            COMMENT '审核备注',
  `account_status`       tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '账号启停：0停用 1启用',
  `coop_status`          varchar(20)  NOT NULL DEFAULT 'active' COMMENT '合作状态：active/expired/terminated',
  `create_by`            varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`          datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`            varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`          datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`               varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`             char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`partner_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_coop_status`  (`coop_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赛事方档案';


-- ---------------------------------------------------------------------
-- B2. jst_contest —— 赛事
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_contest`;
CREATE TABLE `jst_contest` (
  `contest_id`             bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '赛事ID',
  `contest_name`           varchar(255)  NOT NULL                COMMENT '赛事名称',
  `source_type`            varchar(20)   NOT NULL                COMMENT '赛事来源：platform平台自营/partner赛事方',
  `partner_id`             bigint(20)    DEFAULT NULL            COMMENT '所属赛事方ID，FK→jst_event_partner',
  `category`               varchar(32)   NOT NULL                COMMENT '分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育',
  `group_levels`           varchar(255)  DEFAULT NULL            COMMENT '参赛组别多选（逗号分隔）',
  `cover_image`            varchar(255)  DEFAULT NULL            COMMENT '封面图URL',
  `description`            text          DEFAULT NULL            COMMENT '赛事介绍HTML/富文本',
  `enroll_start_time`      datetime      NOT NULL                COMMENT '报名开始时间',
  `enroll_end_time`        datetime      NOT NULL                COMMENT '报名结束时间',
  `event_start_time`       datetime      DEFAULT NULL            COMMENT '比赛开始时间',
  `event_end_time`         datetime      DEFAULT NULL            COMMENT '比赛结束时间',
  `price`                  decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '报名标价（0=零元赛事）',
  `support_channel_enroll` tinyint(4)    NOT NULL DEFAULT '1'    COMMENT '是否支持渠道代报名：0否 1是',
  `support_points_deduct`  tinyint(4)    NOT NULL DEFAULT '0'    COMMENT '是否支持积分抵扣：0否 1是',
  `support_appointment`    tinyint(4)    NOT NULL DEFAULT '0'    COMMENT '是否支持线下预约：0否 1是',
  `cert_rule_json`         json          DEFAULT NULL            COMMENT '证书发放规则JSON',
  `score_rule_json`        json          DEFAULT NULL            COMMENT '成绩规则JSON',
  `form_template_id`       bigint(20)    DEFAULT NULL            COMMENT '默认报名表单模板ID，FK→jst_enroll_form_template',
  `aftersale_days`         int(11)       NOT NULL DEFAULT '7'    COMMENT '售后宽限天数',
  `audit_status`           varchar(20)   NOT NULL DEFAULT 'draft' COMMENT '审核状态：draft/pending/approved/rejected/online/offline',
  `status`                 varchar(20)   NOT NULL DEFAULT 'not_started' COMMENT '业务状态：not_started/enrolling/closed/scoring/published/ended',
  `created_user_id`        bigint(20)    DEFAULT NULL            COMMENT '创建人用户ID',
  `create_by`              varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`            datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`              varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`            datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`                 varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`               char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`contest_id`),
  KEY `idx_partner_id`       (`partner_id`),
  KEY `idx_category_status`  (`category`,`status`),
  KEY `idx_audit_status`     (`audit_status`),
  KEY `idx_enroll_window`    (`enroll_start_time`,`enroll_end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='赛事主表';


-- ---------------------------------------------------------------------
-- B3. jst_enroll_form_template —— 报名动态表单模板
--     字段类型枚举：text/textarea/radio/checkbox/select/date/age/number/image/audio/video/file/group/conditional
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_enroll_form_template`;
CREATE TABLE `jst_enroll_form_template` (
  `template_id`      bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name`    varchar(128) NOT NULL                COMMENT '模板名称',
  `template_version` int(11)      NOT NULL DEFAULT '1'    COMMENT '版本号（修改即递增）',
  `owner_type`       varchar(20)  NOT NULL                COMMENT '所属方：platform/partner',
  `owner_id`         bigint(20)   DEFAULT NULL            COMMENT '所属赛事方ID',
  `schema_json`      json         NOT NULL                COMMENT '字段定义JSON：[{key,type,label,required,validators,visibleIf,sort}]',
  `audit_status`     varchar(20)  NOT NULL DEFAULT 'draft' COMMENT '审核状态：draft/pending/approved/rejected',
  `status`           tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `effective_time`   datetime     DEFAULT NULL            COMMENT '生效时间',
  `create_by`        varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`      datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`        varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`      datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`           varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`         char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`template_id`),
  KEY `idx_owner` (`owner_type`,`owner_id`),
  KEY `idx_audit` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报名动态表单模板（schema_json定义字段）';


-- ---------------------------------------------------------------------
-- B4. jst_enroll_record —— 报名记录（form_snapshot_json 锁定快照）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_enroll_record`;
CREATE TABLE `jst_enroll_record` (
  `enroll_id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `enroll_no`          varchar(32)  NOT NULL                COMMENT '报名编号（业务可读）',
  `contest_id`         bigint(20)   NOT NULL                COMMENT '赛事ID，FK→jst_contest',
  `user_id`            bigint(20)   DEFAULT NULL            COMMENT '学生用户ID（临时档案则为空）',
  `participant_id`     bigint(20)   NOT NULL                COMMENT '参赛人ID（含临时档案），FK→jst_participant',
  `channel_id`         bigint(20)   DEFAULT NULL            COMMENT '关联渠道方ID（支付时锁定）',
  `enroll_source`      varchar(20)  NOT NULL                COMMENT '报名来源：self自助/channel_single渠道单条/channel_batch渠道批量',
  `template_id`        bigint(20)   NOT NULL                COMMENT '使用的表单模板ID',
  `template_version`   int(11)      NOT NULL                COMMENT '表单模板版本号（与快照一致）',
  `form_snapshot_json` json         NOT NULL                COMMENT '报名字段全量快照JSON：动态字段值+附件URL',
  `order_id`           bigint(20)   DEFAULT NULL            COMMENT '关联订单ID',
  `material_status`    varchar(20)  NOT NULL DEFAULT 'draft' COMMENT '资料状态：draft/submitted/supplement',
  `audit_status`       varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending/approved/rejected/supplement',
  `audit_remark`       varchar(255) DEFAULT NULL            COMMENT '审核备注',
  `supplement_remark`  varchar(255) DEFAULT NULL            COMMENT '补充材料说明(用户视角)',
  `submit_time`        datetime     DEFAULT NULL            COMMENT '提交时间',
  `create_by`          varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`        datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`          varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`        datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`             varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`           char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`enroll_id`),
  UNIQUE KEY `uk_enroll_no` (`enroll_no`),
  KEY `idx_contest_id`     (`contest_id`),
  KEY `idx_user_id`        (`user_id`),
  KEY `idx_participant_id` (`participant_id`),
  KEY `idx_channel_id`     (`channel_id`),
  KEY `idx_order_id`       (`order_id`),
  KEY `idx_audit_status`   (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报名记录（含动态表单快照）';


-- ---------------------------------------------------------------------
-- G1. jst_score_record —— 成绩记录
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_score_record`;
CREATE TABLE `jst_score_record` (
  `score_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `contest_id`       bigint(20)   NOT NULL                COMMENT '赛事ID',
  `enroll_id`        bigint(20)   NOT NULL                COMMENT '报名ID',
  `user_id`          bigint(20)   DEFAULT NULL            COMMENT '用户ID（临时档案为空）',
  `participant_id`   bigint(20)   NOT NULL                COMMENT '参赛人ID',
  `score_value`      decimal(8,2) DEFAULT NULL            COMMENT '分数',
  `award_level`      varchar(32)  DEFAULT NULL            COMMENT '奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与',
  `rank_no`          int(11)      DEFAULT NULL            COMMENT '名次',
  `import_batch_no`  varchar(32)  DEFAULT NULL            COMMENT '导入批次号',
  `audit_status`     varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending/approved/rejected',
  `publish_status`   varchar(20)  NOT NULL DEFAULT 'unpublished' COMMENT '发布状态：unpublished/published/withdrawn',
  `publish_time`     datetime     DEFAULT NULL            COMMENT '发布时间',
  `create_by`        varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`      datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`        varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`      datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`           varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`         char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`score_id`),
  KEY `idx_contest_id`    (`contest_id`),
  KEY `idx_enroll_id`     (`enroll_id`),
  KEY `idx_user_id`       (`user_id`),
  KEY `idx_publish_status`(`publish_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成绩记录';


-- ---------------------------------------------------------------------
-- G2. jst_cert_record —— 证书记录
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_cert_record`;
CREATE TABLE `jst_cert_record` (
  `cert_id`        bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '证书ID',
  `cert_no`        varchar(64)  NOT NULL                COMMENT '证书编号，格式 JST-YYYY-ART-XXXXX',
  `contest_id`     bigint(20)   NOT NULL                COMMENT '赛事ID',
  `score_id`       bigint(20)   DEFAULT NULL            COMMENT '关联成绩ID',
  `enroll_id`      bigint(20)   NOT NULL                COMMENT '报名ID',
  `user_id`        bigint(20)   DEFAULT NULL            COMMENT '用户ID',
  `participant_id` bigint(20)   NOT NULL                COMMENT '参赛人ID',
  `template_id`    bigint(20)   DEFAULT NULL            COMMENT '证书模板ID',
  `cert_file_url`  varchar(255) DEFAULT NULL            COMMENT '证书文件URL',
  `issue_status`   varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '发放状态：pending/issued/voided',
  `issue_time`     datetime     DEFAULT NULL            COMMENT '发放时间',
  `void_reason`    varchar(255) DEFAULT NULL            COMMENT '作废原因',
  `verify_code`    varchar(64)  DEFAULT NULL            COMMENT '公开校验码',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`cert_id`),
  UNIQUE KEY `uk_cert_no` (`cert_no`),
  KEY `idx_contest_id`    (`contest_id`),
  KEY `idx_user_id`       (`user_id`),
  KEY `idx_participant_id`(`participant_id`),
  KEY `idx_issue_status`  (`issue_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='证书记录';


-- ---------------------------------------------------------------------
-- G3. jst_cert_template —— 证书模板
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_cert_template`;
CREATE TABLE `jst_cert_template` (
  `template_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name`  varchar(128) NOT NULL                COMMENT '模板名称',
  `owner_type`     varchar(10)  NOT NULL                COMMENT '所属：platform/partner',
  `owner_id`       bigint(20)   DEFAULT NULL            COMMENT '所属赛事方ID',
  `bg_image`       varchar(255) DEFAULT NULL            COMMENT '底图URL',
  `layout_json`    json         DEFAULT NULL            COMMENT '布局/字段配置JSON',
  `audit_status`   varchar(20)  NOT NULL DEFAULT 'pending' COMMENT '审核状态：pending/approved/rejected',
  `status`         tinyint(4)   NOT NULL DEFAULT '1'    COMMENT '启停：0停 1启',
  `create_by`      varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`template_id`),
  KEY `idx_owner` (`owner_type`,`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='证书模板';


-- ---------------------------------------------------------------------
-- G4. jst_course —— 课程（含 AI 课程 OpenMAIC）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_course`;
CREATE TABLE `jst_course` (
  `course_id`      bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name`    varchar(255)  NOT NULL                COMMENT '课程名称',
  `course_type`    varchar(20)   NOT NULL                COMMENT '类型：normal普通 / ai_maic AI课程',
  `cover_image`    varchar(255)  DEFAULT NULL            COMMENT '封面',
  `description`    text          DEFAULT NULL            COMMENT '简介',
  `price`          decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '现金价格',
  `points_price`   bigint(20)    DEFAULT NULL            COMMENT '积分价格',
  `creator_type`   varchar(20)   NOT NULL                COMMENT '创建者类型：platform/channel',
  `creator_id`     bigint(20)    DEFAULT NULL            COMMENT '创建者ID',
  `maic_source_id` varchar(64)   DEFAULT NULL            COMMENT 'OpenMAIC 第三方课件ID',
  `audit_status`   varchar(20)   NOT NULL DEFAULT 'draft' COMMENT '审核状态：draft/pending/approved/rejected',
  `status`         varchar(10)   NOT NULL DEFAULT 'off'  COMMENT '上下架：on/off',
  `visible_scope`  varchar(20)   DEFAULT NULL            COMMENT '可见范围',
  `create_by`      varchar(64)   DEFAULT ''              COMMENT '创建人',
  `create_time`    datetime      DEFAULT NULL            COMMENT '创建时间',
  `update_by`      varchar(64)   DEFAULT ''              COMMENT '更新人',
  `update_time`    datetime      DEFAULT NULL            COMMENT '更新时间',
  `remark`         varchar(500)  DEFAULT NULL            COMMENT '备注',
  `del_flag`       char(1)       DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`course_id`),
  KEY `idx_type_status` (`course_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程';


-- ---------------------------------------------------------------------
-- G5. jst_course_learn_record —— 学习记录
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `jst_course_learn_record`;
CREATE TABLE `jst_course_learn_record` (
  `learn_id`         bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '学习ID',
  `course_id`        bigint(20)   NOT NULL                COMMENT '课程ID',
  `user_id`          bigint(20)   NOT NULL                COMMENT '用户ID',
  `progress`         decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '学习进度百分比',
  `duration_seconds` int(11)      NOT NULL DEFAULT '0'    COMMENT '累计学习时长（秒）',
  `quiz_score`       decimal(5,2) DEFAULT NULL            COMMENT '测验分',
  `complete_status`  varchar(10)  NOT NULL DEFAULT 'learning' COMMENT '完课状态：learning/completed',
  `complete_time`    datetime     DEFAULT NULL            COMMENT '完成时间',
  `create_by`        varchar(64)  DEFAULT ''              COMMENT '创建人',
  `create_time`      datetime     DEFAULT NULL            COMMENT '创建时间',
  `update_by`        varchar(64)  DEFAULT ''              COMMENT '更新人',
  `update_time`      datetime     DEFAULT NULL            COMMENT '更新时间',
  `remark`           varchar(500) DEFAULT NULL            COMMENT '备注',
  `del_flag`         char(1)      DEFAULT '0'             COMMENT '逻辑删除：0存在 2删除',
  PRIMARY KEY (`learn_id`),
  KEY `idx_course_user` (`course_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程学习记录';

SET FOREIGN_KEY_CHECKS = 1;
-- 批 2 完成：9 张表
