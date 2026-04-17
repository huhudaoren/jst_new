-- =====================================================
-- 销售管理 + 渠道分销 + CRM 子系统 DDL 迁移
-- 关联 spec: docs/superpowers/specs/2026-04-18-sales-channel-distribution-design.md
-- =====================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========== 1.1 jst_sales 销售档案表 ==========
DROP TABLE IF EXISTS jst_sales;
CREATE TABLE jst_sales (
  sales_id              BIGINT          NOT NULL                COMMENT '销售 ID（雪花）',
  sys_user_id           BIGINT          NOT NULL                COMMENT '关联 sys_user.user_id',
  sales_no              VARCHAR(32)     NOT NULL                COMMENT '销售业务编号（信物，如 S202604001）',
  sales_name            VARCHAR(64)     NOT NULL                COMMENT '销售姓名（冗余）',
  phone                 VARCHAR(20)     NOT NULL                COMMENT '销售本人手机号',
  manager_id            BIGINT          NULL                    COMMENT '直属主管 sales_id（主管为 NULL）',
  is_manager            TINYINT         NOT NULL DEFAULT 0      COMMENT '是否主管 1/0',
  status                VARCHAR(32)     NOT NULL DEFAULT 'active' COMMENT 'active / resign_apply / resigned_pending_settle / resigned_final',
  resign_apply_date     DATETIME        NULL                    COMMENT '提交离职申请时间',
  expected_resign_date  DATETIME        NULL                    COMMENT '预期离职日',
  actual_resign_date    DATETIME        NULL                    COMMENT '实际离职执行日',
  transition_end_date   DATETIME        NULL                    COMMENT '财务过渡期结束日',
  commission_rate_default DECIMAL(5,4)  NOT NULL DEFAULT 0.0500 COMMENT '默认费率',
  manager_commission_rate DECIMAL(5,4)  NOT NULL DEFAULT 0.0000 COMMENT '团队管理提成率（预留）',
  create_by             VARCHAR(64)     NULL,
  create_time           DATETIME        NULL,
  update_by             VARCHAR(64)     NULL,
  update_time           DATETIME        NULL,
  PRIMARY KEY (sales_id),
  UNIQUE KEY uk_sales_no (sales_no),
  UNIQUE KEY uk_sys_user (sys_user_id),
  KEY idx_manager (manager_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售档案';

-- ========== 1.2 jst_sales_commission_rate 销售费率明细表 ==========
DROP TABLE IF EXISTS jst_sales_commission_rate;
CREATE TABLE jst_sales_commission_rate (
  id              BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  sales_id        BIGINT          NOT NULL                COMMENT '销售 ID',
  business_type   VARCHAR(32)     NOT NULL                COMMENT 'enroll/course/mall/appointment_team/appointment_personal',
  rate            DECIMAL(5,4)    NOT NULL                COMMENT '费率',
  effective_from  DATETIME        NOT NULL                COMMENT '生效时间（仅影响该时间之后订单）',
  create_by       VARCHAR(64)     NULL,
  create_time     DATETIME        NULL,
  PRIMARY KEY (id),
  KEY idx_sales_biz_eff (sales_id, business_type, effective_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售业务类型费率明细';

-- ========== 1.3 jst_sales_pre_register 预录入表 ==========
DROP TABLE IF EXISTS jst_sales_pre_register;
CREATE TABLE jst_sales_pre_register (
  pre_id              BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  sales_id            BIGINT          NOT NULL                COMMENT '哪个销售录入的',
  phone               VARCHAR(20)     NOT NULL                COMMENT '渠道方手机号（A2 主匹配键）',
  target_name         VARCHAR(128)    NULL                    COMMENT '联系人/单位名',
  status              VARCHAR(32)     NOT NULL DEFAULT 'pending' COMMENT 'pending / matched / expired',
  matched_channel_id  BIGINT          NULL                    COMMENT '匹配后回填',
  matched_at          DATETIME        NULL,
  expire_at           DATETIME        NOT NULL                COMMENT '过期时间（默认创建+90 天）',
  renew_count         TINYINT         NOT NULL DEFAULT 0      COMMENT '已续期次数（最多 2）',
  create_by           VARCHAR(64)     NULL,
  create_time         DATETIME        NULL,
  PRIMARY KEY (pre_id),
  -- 仅 status='pending' 的同一手机号唯一（用生成列模拟 partial index）
  pending_flag TINYINT GENERATED ALWAYS AS (IF(status='pending', 1, NULL)) VIRTUAL,
  UNIQUE KEY uk_phone_pending (phone, pending_flag),
  KEY idx_sales (sales_id),
  KEY idx_expire (expire_at, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售预录入';

-- ========== 1.4 jst_sales_channel_binding 销售-渠道绑定（版本化）==========
DROP TABLE IF EXISTS jst_sales_channel_binding;
CREATE TABLE jst_sales_channel_binding (
  binding_id      BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  channel_id      BIGINT          NOT NULL                COMMENT '渠道 ID',
  sales_id        BIGINT          NOT NULL                COMMENT '当前归属销售',
  effective_from  DATETIME        NOT NULL                COMMENT '生效时间',
  effective_to    DATETIME        NULL                    COMMENT 'NULL=当前 / 非NULL=历史',
  bind_source     VARCHAR(32)     NOT NULL                COMMENT 'pre_register / business_no / manual / transfer_resign / manual_transfer',
  bind_remark     VARCHAR(255)    NULL                    COMMENT 'admin 转移时填写的原因',
  operator_id     BIGINT          NULL                    COMMENT '触发人 sys_user_id',
  create_time     DATETIME        NULL,
  PRIMARY KEY (binding_id),
  -- 同渠道当前只有一行 current（条件唯一索引模拟）
  is_current TINYINT GENERATED ALWAYS AS (IF(effective_to IS NULL, 1, NULL)) VIRTUAL,
  UNIQUE KEY uk_channel_current (channel_id, is_current),
  KEY idx_sales_eff (sales_id, effective_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售-渠道绑定关系（版本化）';

-- ========== 1.5 jst_channel_invite 渠道邀请关系 ==========
DROP TABLE IF EXISTS jst_channel_invite;
CREATE TABLE jst_channel_invite (
  invite_id           BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  inviter_channel_id  BIGINT          NOT NULL                COMMENT '上级渠道',
  invitee_channel_id  BIGINT          NOT NULL                COMMENT '下级渠道',
  invite_code         VARCHAR(32)     NOT NULL                COMMENT '上级生成的邀请码',
  share_scene         VARCHAR(32)     NOT NULL DEFAULT 'mp_share' COMMENT 'mp_share / qrcode / manual_admin',
  commission_rate     DECIMAL(5,4)    NULL                    COMMENT 'NULL=用系统默认 1.5%',
  invited_at          DATETIME        NOT NULL                COMMENT '邀请关系建立时间',
  status              VARCHAR(32)     NOT NULL DEFAULT 'active' COMMENT 'active / unbound',
  create_time         DATETIME        NULL,
  PRIMARY KEY (invite_id),
  -- 同一下级在同一时刻只能 active 一次
  active_flag TINYINT GENERATED ALWAYS AS (IF(status='active', 1, NULL)) VIRTUAL,
  UNIQUE KEY uk_invitee_active (invitee_channel_id, active_flag),
  KEY idx_inviter (inviter_channel_id),
  KEY idx_code (invite_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道邀请关系';

-- ========== 1.6 jst_channel 老表加字段 ==========
ALTER TABLE jst_channel
  ADD COLUMN business_no              VARCHAR(32)  NULL COMMENT '渠道注册时填的销售业务编号' AFTER status,
  ADD COLUMN invite_code              VARCHAR(32)  NULL COMMENT '本渠道分享出去的邀请码（注册自动生成）' AFTER business_no,
  ADD COLUMN parent_invite_id         BIGINT       NULL COMMENT '作为被邀请方关联的邀请关系 ID' AFTER invite_code,
  ADD COLUMN parent_invite_attempted  VARCHAR(32)  NULL COMMENT 'M1 互斥时记录原本邀请人 invite_code' AFTER parent_invite_id,
  ADD COLUMN is_official              TINYINT      NOT NULL DEFAULT 0 COMMENT '是否官方/自营渠道（不参与分销）' AFTER parent_invite_attempted,
  ADD UNIQUE KEY uk_invite_code (invite_code);

-- ========== 1.7 jst_sales_commission_ledger 销售提成台账 ==========
DROP TABLE IF EXISTS jst_sales_commission_ledger;
CREATE TABLE jst_sales_commission_ledger (
  ledger_id       BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  sales_id        BIGINT          NOT NULL                COMMENT '提成归属销售',
  order_id        BIGINT          NOT NULL                COMMENT '订单 ID',
  order_no        VARCHAR(64)     NOT NULL                COMMENT '订单编号（冗余）',
  channel_id      BIGINT          NOT NULL                COMMENT '触发的渠道',
  binding_type    VARCHAR(32)     NOT NULL                COMMENT 'direct(直属) / level1(穿透 1 级)',
  business_type   VARCHAR(32)     NOT NULL                COMMENT 'enroll/course/...',
  base_amount     DECIMAL(12,2)   NOT NULL                COMMENT '计算基数（实付金额）',
  applied_rate    DECIMAL(5,4)    NOT NULL                COMMENT '实际适用费率',
  raw_amount      DECIMAL(12,2)   NOT NULL                COMMENT '压缩前 = base × rate',
  compress_ratio  DECIMAL(5,4)    NOT NULL DEFAULT 1.0000 COMMENT '上限压缩系数',
  amount          DECIMAL(12,2)   NOT NULL                COMMENT '最终入账 = raw × ratio',
  status          VARCHAR(32)     NOT NULL DEFAULT 'pending' COMMENT 'pending / accrued / settled / paid / rejected / cancelled',
  accrue_at       DATETIME        NOT NULL                COMMENT '售后期满入账时间',
  accrued_at      DATETIME        NULL                    COMMENT '实际入账时间',
  settlement_id   BIGINT          NULL                    COMMENT '关联月结单',
  create_time     DATETIME        NULL,
  update_time     DATETIME        NULL,
  PRIMARY KEY (ledger_id),
  UNIQUE KEY uk_order_sales_type (order_id, sales_id, binding_type),
  KEY idx_status_accrue (status, accrue_at),
  KEY idx_sales_status (sales_id, status),
  KEY idx_settlement (settlement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售提成台账';

-- ========== 1.8 jst_sales_commission_settlement 销售月结单 ==========
DROP TABLE IF EXISTS jst_sales_commission_settlement;
CREATE TABLE jst_sales_commission_settlement (
  settlement_id   BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  sales_id        BIGINT          NOT NULL                COMMENT '销售 ID',
  period          VARCHAR(7)      NOT NULL                COMMENT '结算周期 YYYY-MM',
  total_count     INT             NOT NULL DEFAULT 0      COMMENT '提成笔数',
  total_amount    DECIMAL(12,2)   NOT NULL DEFAULT 0      COMMENT '月结金额',
  status          VARCHAR(32)     NOT NULL DEFAULT 'pending_review' COMMENT 'pending_review / approved / paid / rejected',
  approved_by     BIGINT          NULL,
  approved_at     DATETIME        NULL,
  paid_at         DATETIME        NULL,
  pay_voucher     VARCHAR(255)    NULL                    COMMENT '打款流水号',
  reject_reason   VARCHAR(255)    NULL,
  create_time     DATETIME        NULL,
  PRIMARY KEY (settlement_id),
  UNIQUE KEY uk_sales_period (sales_id, period),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售月结单';

-- ========== 1.9 jst_channel_distribution_ledger 渠道分销提成台账 ==========
DROP TABLE IF EXISTS jst_channel_distribution_ledger;
CREATE TABLE jst_channel_distribution_ledger (
  ledger_id            BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  inviter_channel_id   BIGINT          NOT NULL                COMMENT '上级渠道（受益方）',
  invitee_channel_id   BIGINT          NOT NULL                COMMENT '下级渠道',
  order_id             BIGINT          NOT NULL                COMMENT '订单 ID',
  order_no             VARCHAR(64)     NOT NULL                COMMENT '订单号',
  base_amount          DECIMAL(12,2)   NOT NULL                COMMENT '实付金额',
  applied_rate         DECIMAL(5,4)    NOT NULL                COMMENT 'invite 表上的费率（回退默认）',
  raw_amount           DECIMAL(12,2)   NOT NULL,
  compress_ratio       DECIMAL(5,4)    NOT NULL DEFAULT 1.0000,
  amount               DECIMAL(12,2)   NOT NULL,
  status               VARCHAR(32)     NOT NULL DEFAULT 'pending',
  accrue_at            DATETIME        NOT NULL,
  accrued_at           DATETIME        NULL,
  create_time          DATETIME        NULL,
  update_time          DATETIME        NULL,
  PRIMARY KEY (ledger_id),
  UNIQUE KEY uk_order_inviter (order_id, inviter_channel_id),
  KEY idx_status_accrue (status, accrue_at),
  KEY idx_inviter_status (inviter_channel_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道分销提成台账';

-- ========== 1.10 jst_sales_followup_record 跟进记录 ==========
DROP TABLE IF EXISTS jst_sales_followup_record;
CREATE TABLE jst_sales_followup_record (
  record_id          BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  sales_id           BIGINT          NOT NULL,
  channel_id         BIGINT          NOT NULL,
  followup_type      VARCHAR(32)     NOT NULL                COMMENT 'phone / wechat / visit / email / other',
  followup_at        DATETIME        NOT NULL                COMMENT '跟进时间',
  content            VARCHAR(1000)   NOT NULL,
  attachment_urls    JSON            NULL                    COMMENT 'OSS URL 数组（最多 5）',
  mood               VARCHAR(32)     NULL                    COMMENT 'high / mid / low / none',
  next_followup_at   DATETIME        NULL                    COMMENT '下次跟进时间',
  can_edit_until     DATETIME        NOT NULL                COMMENT '创建时间+24h',
  create_by          VARCHAR(64)     NULL,
  create_time        DATETIME        NULL,
  update_by          VARCHAR(64)     NULL,
  update_time        DATETIME        NULL,
  PRIMARY KEY (record_id),
  KEY idx_channel_time (channel_id, followup_at),
  KEY idx_sales_time (sales_id, followup_at),
  KEY idx_next (next_followup_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售跟进记录';

-- ========== 1.11 jst_sales_channel_tag 渠道标签 ==========
DROP TABLE IF EXISTS jst_sales_channel_tag;
CREATE TABLE jst_sales_channel_tag (
  id           BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  channel_id   BIGINT          NOT NULL,
  tag_code     VARCHAR(64)     NOT NULL                COMMENT '字典 + 自定义（custom: 前缀）',
  tag_color    VARCHAR(16)     NULL,
  create_by    BIGINT          NULL,
  create_time  DATETIME        NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_channel_tag (channel_id, tag_code),
  KEY idx_channel (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道标签';

-- ========== 1.12 jst_sales_followup_task 跟进任务 ==========
DROP TABLE IF EXISTS jst_sales_followup_task;
CREATE TABLE jst_sales_followup_task (
  task_id            BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  assignee_sales_id  BIGINT          NOT NULL                COMMENT '任务执行人',
  assigner_id        BIGINT          NOT NULL                COMMENT '派任务的人 sys_user_id',
  channel_id         BIGINT          NOT NULL,
  title              VARCHAR(128)    NOT NULL,
  description        VARCHAR(500)    NULL,
  due_date           DATE            NOT NULL,
  status             VARCHAR(32)     NOT NULL DEFAULT 'pending' COMMENT 'pending / in_progress / completed / overdue',
  completed_at       DATETIME        NULL,
  linked_record_id   BIGINT          NULL                    COMMENT '完成时关联的跟进记录',
  create_time        DATETIME        NULL,
  update_time        DATETIME        NULL,
  PRIMARY KEY (task_id),
  KEY idx_assignee_status (assignee_sales_id, status),
  KEY idx_due (due_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售跟进任务';

-- ========== 1.13 jst_sales_attribution_conflict 归属冲突队列 ==========
DROP TABLE IF EXISTS jst_sales_attribution_conflict;
CREATE TABLE jst_sales_attribution_conflict (
  conflict_id          BIGINT          NOT NULL                COMMENT 'PK（雪花）',
  channel_id           BIGINT          NOT NULL,
  candidate_sales_ids  JSON            NOT NULL                COMMENT '候选销售 ID 列表',
  reason               VARCHAR(255)    NOT NULL,
  status               VARCHAR(32)     NOT NULL DEFAULT 'pending' COMMENT 'pending / resolved',
  resolved_by          BIGINT          NULL,
  resolved_at          DATETIME        NULL,
  resolution_remark    VARCHAR(500)    NULL,
  create_time          DATETIME        NULL,
  PRIMARY KEY (conflict_id),
  KEY idx_status (status),
  KEY idx_channel (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归属冲突队列';

SET FOREIGN_KEY_CHECKS = 1;
