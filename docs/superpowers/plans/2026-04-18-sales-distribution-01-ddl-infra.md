# 销售管理 + 渠道分销 - 计划 #1: DDL + 基础架构

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 落地销售管理 + 渠道分销 + CRM 子系统的所有数据库结构、角色/菜单/字典/系统参数初始化、以及 jst-common 模块的基础切面（SalesScope 数据隔离 + 金额脱敏 AOP + 导出水印拦截器）。本计划交付后，后续 3 个计划（销售管理核心 / CRM / 分销+admin）可独立并行开发。

**Architecture:** 复刻现有 PartnerScope 模式：jst-common 提供 `SalesScopeUtils` + `BaseSalesController` + `@Sensitive` 注解 + `MaskSalaryAspect` AOP + `SalesExportWatermarkInterceptor`；jst-channel 模块新增 9 张表（销售档案/费率/预录入/绑定/邀请/提成台账×2/月结/CRM×3/冲突）+ Domain/Mapper；SQL 文件全部放在 `架构设计/ddl/` 走 `99-migration-sales-distribution-*.sql` 命名。

**Tech Stack:** Spring Boot 4.x + JDK 17 + MyBatis + MySQL 5.7 (InnoDB / utf8mb4) + Spring AOP + JUnit 5 + Mockito。复用若依 RuoYi-Vue 3.9.2 框架。

**关联 spec:** `docs/superpowers/specs/2026-04-18-sales-channel-distribution-design.md`

---

## 文件结构（本计划新增/修改的文件）

### SQL（架构设计/ddl/）
- 新建 `99-migration-sales-distribution-ddl.sql` — 9 张新表 + jst_channel 加 5 字段
- 新建 `99-migration-sales-distribution-dict.sql` — 字典类型与字典数据（business_type, followup_type, mood, bind_source, sales_status, ledger_status, settlement_status, share_scene, channel_tag）
- 新建 `99-migration-sales-distribution-sysparam.sql` — 8 个系统参数
- 新建 `99-migration-sales-distribution-role-menu.sql` — 2 个角色 + 9785 段菜单 + 9762 下追加 3 项 admin 菜单 + 角色权限绑定

### Java（jst-common 模块）
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesScopeUtils.java`
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/controller/BaseSalesController.java`
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/annotation/Sensitive.java`
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/MaskSalaryAspect.java`
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/SensitiveMasker.java`（反射工具）
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/interceptor/SalesExportWatermarkInterceptor.java`
- 新建 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/config/SalesScopeWebConfig.java`（注册拦截器）
- 修改 `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/login/JstLoginEnricherImpl.java`（充实 salesId 到 LoginContext）
- 修改 `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/context/JstLoginContext.java`（加 salesId / managerVisibleSalesIds 字段）

### Java（jst-channel 模块 — 9 张表的 Domain + Mapper）
- 新建 9 个 Domain 类于 `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/`
- 新建 9 个 Mapper 接口于 `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/`
- 新建 9 个 Mapper XML 于 `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/`（仅 BaseResultMap + insert / selectById / updateById 等基础 CRUD，业务查询留给计划 #2~#4）

### Java 测试
- 新建 `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/util/SalesScopeUtilsTest.java`
- 新建 `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/aspect/SensitiveMaskerTest.java`
- 新建 `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/interceptor/SalesExportWatermarkInterceptorTest.java`

### CLAUDE.md
- 修改顶部"上次更新"行 + §六 待处理表 SALES-DISTRIBUTION 行（标记计划 #1 完成）

---

## Task 1: 准备工作 — 检查环境、本地数据库就位

**Files:** （只读校验，不改文件）

- [ ] **Step 1: 确认本地 MySQL 库可连接**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT DATABASE();" jst
```
Expected: 返回 `jst` 数据库名，无错误。

- [ ] **Step 2: 确认 jst_channel / jst_sales / jst_sales_pre_register 等本计划要新建的表均不存在**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SHOW TABLES LIKE 'jst_sales%';
SHOW TABLES LIKE 'jst_channel_invite';
SHOW TABLES LIKE 'jst_channel_distribution_ledger';
" jst
```
Expected: 返回空结果（这些表本计划才创建）。如果已存在，说明环境不干净，需要先 drop 再继续。

- [ ] **Step 3: 检查 jst_channel 表的字段，确认要加的 5 个字段（business_no / invite_code / parent_invite_id / parent_invite_attempted / is_official）尚未存在**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
DESCRIBE jst_channel;
" jst
```
Expected: 不应包含上述 5 个字段。

- [ ] **Step 4: 拉取最新代码，开新工作分支**

Run:
```bash
cd D:/coding/jst_v1
git checkout main
git pull
git checkout -b feature/sales-distribution-01-ddl-infra
```
Expected: 成功创建分支。

---

## Task 2: 创建 DDL 迁移文件 — 9 张新表 + jst_channel 加字段

**Files:**
- Create: `架构设计/ddl/99-migration-sales-distribution-ddl.sql`

- [ ] **Step 1: 创建 DDL 文件，写入完整 SQL**

写入文件内容如下（**全部 SQL 一次性写入**，不要分段）：

```sql
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
```

- [ ] **Step 2: 应用 DDL 到本地库**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-ddl.sql"
```
Expected: 无错误输出。

- [ ] **Step 3: 验证 9 张新表已创建**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT table_name, table_comment FROM information_schema.tables 
WHERE table_schema='jst' 
  AND table_name IN (
    'jst_sales','jst_sales_commission_rate','jst_sales_pre_register',
    'jst_sales_channel_binding','jst_channel_invite',
    'jst_sales_commission_ledger','jst_sales_commission_settlement',
    'jst_channel_distribution_ledger',
    'jst_sales_followup_record','jst_sales_channel_tag','jst_sales_followup_task',
    'jst_sales_attribution_conflict'
  )
ORDER BY table_name;
" jst
```
Expected: 返回 12 行（9 张新表 + 3 张 CRM 表）。

- [ ] **Step 4: 验证 jst_channel 加字段成功**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT column_name FROM information_schema.columns 
WHERE table_schema='jst' AND table_name='jst_channel' 
  AND column_name IN ('business_no','invite_code','parent_invite_id','parent_invite_attempted','is_official');
" jst
```
Expected: 返回 5 行。

- [ ] **Step 5: 验证 partial unique index 模拟生效**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
INSERT INTO jst_sales_pre_register (pre_id,sales_id,phone,status,expire_at,renew_count) 
VALUES (1,100,'13900000001','pending',NOW()+INTERVAL 90 DAY,0);
INSERT INTO jst_sales_pre_register (pre_id,sales_id,phone,status,expire_at,renew_count) 
VALUES (2,101,'13900000001','pending',NOW()+INTERVAL 90 DAY,0);
" jst 2>&1 | tee /tmp/test-uk.txt
```
Expected: 第一条 INSERT 成功，第二条返回 `Duplicate entry` 错误。验证唯一索引拦截生效。

清理：
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "DELETE FROM jst_sales_pre_register WHERE pre_id IN (1,2);" jst
```

- [ ] **Step 6: 提交**

```bash
git add "架构设计/ddl/99-migration-sales-distribution-ddl.sql"
git commit -m "feat(ddl): 销售管理+渠道分销+CRM 9 张新表 + jst_channel 加 5 字段

- jst_sales / jst_sales_commission_rate / jst_sales_pre_register
- jst_sales_channel_binding（版本化，条件唯一索引）
- jst_channel_invite + jst_channel 加 business_no/invite_code/parent_invite_*/is_official
- jst_sales_commission_ledger / settlement
- jst_channel_distribution_ledger
- jst_sales_followup_record / channel_tag / followup_task
- jst_sales_attribution_conflict
- 用 generated column 模拟 MySQL 5.7 partial unique index"
```

---

## Task 3: 字典数据 SQL

**Files:**
- Create: `架构设计/ddl/99-migration-sales-distribution-dict.sql`

- [ ] **Step 1: 创建字典 SQL 文件**

写入完整内容：

```sql
-- =====================================================
-- 销售管理字典数据
-- 关联 spec §1
-- 字典段：jst_sales_*（避免与既有字典冲突）
-- =====================================================
SET NAMES utf8mb4;

-- ========== 字典类型定义 ==========
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES
('销售业务类型',          'jst_sales_business_type',  '0', 'admin', NOW(), '销售提成按业务类型差异化'),
('跟进类型',              'jst_sales_followup_type',  '0', 'admin', NOW(), 'CRM 跟进沟通类型'),
('跟进意向',              'jst_sales_mood',           '0', 'admin', NOW(), '跟进时记录的客户意向'),
('销售-渠道绑定来源',     'jst_sales_bind_source',    '0', 'admin', NOW(), '销售归属绑定的来源'),
('销售状态',              'jst_sales_status',         '0', 'admin', NOW(), '销售在职/申请离职/已离职'),
('销售提成台账状态',      'jst_sales_ledger_status',  '0', 'admin', NOW(), '销售提成 ledger 状态机'),
('销售月结单状态',        'jst_sales_settlement_status', '0', 'admin', NOW(), '月结单状态机'),
('渠道邀请分享场景',      'jst_channel_share_scene',  '0', 'admin', NOW(), '渠道邀请关系建立的场景'),
('渠道标签',              'jst_sales_channel_tag',    '0', 'admin', NOW(), '销售给渠道打的标签');

-- ========== jst_sales_business_type ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(1, '赛事报名',     'enroll',                'jst_sales_business_type', NULL, 'primary', 'N', '0', 'admin', NOW(), NULL),
(2, '课程购买',     'course',                'jst_sales_business_type', NULL, 'success', 'N', '0', 'admin', NOW(), NULL),
(3, '商城兑换',     'mall',                  'jst_sales_business_type', NULL, 'info',    'N', '0', 'admin', NOW(), NULL),
(4, '团队预约',     'appointment_team',      'jst_sales_business_type', NULL, 'warning', 'N', '0', 'admin', NOW(), NULL),
(5, '个人预约',     'appointment_personal',  'jst_sales_business_type', NULL, 'warning', 'N', '0', 'admin', NOW(), NULL);

-- ========== jst_sales_followup_type ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '电话',  'phone',  'jst_sales_followup_type', 'primary', '0', 'admin', NOW()),
(2, '微信',  'wechat', 'jst_sales_followup_type', 'success', '0', 'admin', NOW()),
(3, '拜访',  'visit',  'jst_sales_followup_type', 'warning', '0', 'admin', NOW()),
(4, '邮件',  'email',  'jst_sales_followup_type', 'info',    '0', 'admin', NOW()),
(5, '其他',  'other',  'jst_sales_followup_type', 'default', '0', 'admin', NOW());

-- ========== jst_sales_mood ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '意向高',   'high', 'jst_sales_mood', 'success', '0', 'admin', NOW()),
(2, '意向中',   'mid',  'jst_sales_mood', 'primary', '0', 'admin', NOW()),
(3, '意向低',   'low',  'jst_sales_mood', 'warning', '0', 'admin', NOW()),
(4, '无意向',   'none', 'jst_sales_mood', 'danger',  '0', 'admin', NOW());

-- ========== jst_sales_bind_source ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '预录入命中',     'pre_register',     'jst_sales_bind_source', 'success', '0', 'admin', NOW()),
(2, '业务编号命中',   'business_no',      'jst_sales_bind_source', 'primary', '0', 'admin', NOW()),
(3, 'admin 手动',     'manual',           'jst_sales_bind_source', 'info',    '0', 'admin', NOW()),
(4, '离职转移',       'transfer_resign',  'jst_sales_bind_source', 'warning', '0', 'admin', NOW()),
(5, '手动调整',       'manual_transfer',  'jst_sales_bind_source', 'info',    '0', 'admin', NOW());

-- ========== jst_sales_status ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '在职',           'active',                   'jst_sales_status', 'success', '0', 'admin', NOW()),
(2, '申请离职',       'resign_apply',             'jst_sales_status', 'warning', '0', 'admin', NOW()),
(3, '已离职待结算',   'resigned_pending_settle',  'jst_sales_status', 'info',    '0', 'admin', NOW()),
(4, '彻底离职',       'resigned_final',           'jst_sales_status', 'danger',  '0', 'admin', NOW());

-- ========== jst_sales_ledger_status ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '待计提',  'pending',   'jst_sales_ledger_status', 'info',    '0', 'admin', NOW()),
(2, '已计提',  'accrued',   'jst_sales_ledger_status', 'primary', '0', 'admin', NOW()),
(3, '已结算',  'settled',   'jst_sales_ledger_status', 'warning', '0', 'admin', NOW()),
(4, '已打款',  'paid',      'jst_sales_ledger_status', 'success', '0', 'admin', NOW()),
(5, '已驳回',  'rejected',  'jst_sales_ledger_status', 'danger',  '0', 'admin', NOW()),
(6, '已撤销',  'cancelled', 'jst_sales_ledger_status', 'default', '0', 'admin', NOW());

-- ========== jst_sales_settlement_status ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '待审核',  'pending_review', 'jst_sales_settlement_status', 'warning', '0', 'admin', NOW()),
(2, '已审核',  'approved',       'jst_sales_settlement_status', 'primary', '0', 'admin', NOW()),
(3, '已打款',  'paid',           'jst_sales_settlement_status', 'success', '0', 'admin', NOW()),
(4, '已驳回',  'rejected',       'jst_sales_settlement_status', 'danger',  '0', 'admin', NOW());

-- ========== jst_channel_share_scene ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, '小程序分享', 'mp_share',     'jst_channel_share_scene', 'primary', '0', 'admin', NOW()),
(2, '二维码',     'qrcode',       'jst_channel_share_scene', 'info',    '0', 'admin', NOW()),
(3, 'admin 录入', 'manual_admin', 'jst_channel_share_scene', 'warning', '0', 'admin', NOW());

-- ========== jst_sales_channel_tag (默认 7 个，自定义以 custom: 前缀) ==========
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, status, create_by, create_time) VALUES
(1, 'VIP',          'vip',           'jst_sales_channel_tag', 'success', '0', 'admin', NOW()),
(2, '重点维护',     'key_account',   'jst_sales_channel_tag', 'primary', '0', 'admin', NOW()),
(3, '流失风险',     'at_risk',       'jst_sales_channel_tag', 'danger',  '0', 'admin', NOW()),
(4, '新客',         'new',           'jst_sales_channel_tag', 'info',    '0', 'admin', NOW()),
(5, '沉默客户',     'silent',        'jst_sales_channel_tag', 'warning', '0', 'admin', NOW()),
(6, '高价值',       'high_value',    'jst_sales_channel_tag', 'success', '0', 'admin', NOW()),
(7, '试合作',       'trial',         'jst_sales_channel_tag', 'info',    '0', 'admin', NOW());
```

- [ ] **Step 2: 应用 SQL**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-dict.sql"
```
Expected: 无错误。

- [ ] **Step 3: 验证字典插入**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT dict_type, COUNT(*) AS cnt FROM sys_dict_data 
WHERE dict_type LIKE 'jst_sales_%' OR dict_type='jst_channel_share_scene'
GROUP BY dict_type;
" jst
```
Expected: 9 行，counts 分别为 5/5/4/5/4/6/4/3/7。

- [ ] **Step 4: 提交**

```bash
git add "架构设计/ddl/99-migration-sales-distribution-dict.sql"
git commit -m "feat(ddl): 销售管理 9 字典类型 43 条字典数据"
```

---

## Task 4: 系统参数 SQL

**Files:**
- Create: `架构设计/ddl/99-migration-sales-distribution-sysparam.sql`

- [ ] **Step 1: 创建系统参数 SQL**

写入完整内容：

```sql
-- =====================================================
-- 销售管理系统参数（admin 在系统参数页可调）
-- =====================================================
SET NAMES utf8mb4;

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark) VALUES
('销售提成-售后期天数',           'jst.sales.aftersales_days',              '7',     'Y', 'admin', NOW(), '订单付款后多少天无退款，提成才入账'),
('销售-财务过渡期月数',           'jst.sales.transition_months',            '3',     'Y', 'admin', NOW(), '离职后已计提提成结算的财务过渡期'),
('销售-离职申请到执行的最大间隔天数', 'jst.sales.resign_apply_max_days',      '7',     'Y', 'admin', NOW(), '提交离职申请到实际离职日的最大间隔'),
('销售-预录入有效期天数',         'jst.sales.pre_register_valid_days',      '90',    'Y', 'admin', NOW(), '预录入未匹配自动失效'),
('销售-预录入最多续期次数',       'jst.sales.pre_register_renew_max',       '2',     'Y', 'admin', NOW(), '销售可手动续期次数上限'),
('渠道分销-默认费率',             'jst.sales.distribution.default_rate',    '0.0150','Y', 'admin', NOW(), '渠道邀请关系未单独配费率时使用'),
('单笔订单总分润上限',            'jst.sales.order.max_total_share_rate',   '0.1500','Y', 'admin', NOW(), '销售提成+分销+返点 总和不超过订单实付的此比例'),
('销售-高额提成告警阈值',         'jst.sales.high_amount_alert_threshold',  '5000',  'Y', 'admin', NOW(), '单条 ledger 金额超此值触发 admin 告警');
```

- [ ] **Step 2: 应用 SQL**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-sysparam.sql"
```
Expected: 无错误。

- [ ] **Step 3: 验证 8 个系统参数已写入**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT config_key, config_value FROM sys_config WHERE config_key LIKE 'jst.sales.%' ORDER BY config_key;
" jst
```
Expected: 返回 8 行。

- [ ] **Step 4: 提交**

```bash
git add "架构设计/ddl/99-migration-sales-distribution-sysparam.sql"
git commit -m "feat(ddl): 销售管理 8 个系统参数（aftersales/transition/distribution/cap）"
```

---

## Task 5: 角色 + 菜单 + 权限点 SQL

**Files:**
- Create: `架构设计/ddl/99-migration-sales-distribution-role-menu.sql`

- [ ] **Step 1: 查询既有最大菜单 ID 与角色 ID（避免冲突）**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT MAX(menu_id) FROM sys_menu;
SELECT MAX(role_id) FROM sys_role;
SELECT role_id, role_key, role_name FROM sys_role ORDER BY role_id;
" jst
```
Expected: 记下 MAX(menu_id) 和 MAX(role_id)，确认 role_key='jst_sales' 和 'jst_sales_manager' 不存在。新角色 ID 用 MAX+1 / MAX+2，菜单 ID 用 9785 段（销售工作台）+ 9762xx（admin 子菜单）。

- [ ] **Step 2: 创建角色+菜单 SQL 文件**

写入完整内容（**注意：menu_id 和 role_id 用确定的固定值，不要依赖 MAX**，便于幂等回滚）：

```sql
-- =====================================================
-- 销售管理：角色 / 菜单 / 权限点
-- 角色：jst_sales (501) / jst_sales_manager (502)
-- 菜单段：9785 (销售工作台) / 9762xx (admin 销售管理)
-- =====================================================
SET NAMES utf8mb4;

-- ========== 1. 角色 ==========
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) VALUES
(501, '销售',     'jst_sales',         50, '5', 1, 1, '0', '0', 'admin', NOW(), '销售个人，SalesScope 数据隔离 + 金额脱敏'),
(502, '销售主管', 'jst_sales_manager', 51, '5', 1, 1, '0', '0', 'admin', NOW(), '看下属全明细 + 完整金额，可派任务、调整下属归属');

-- ========== 2. 销售工作台菜单 (9785 段) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
-- 顶级菜单（M）
(9785,    '销售工作台', 0,    50, 'sales-workbench', NULL,                          NULL, 1, 0, 'M', '0', '0', NULL,                       'people',     'admin', NOW(), '销售/销售主管专用'),
-- 二级菜单（C）
(978501,  '我的渠道',   9785, 1,  'channels',        'sales/me/channels/index',     NULL, 1, 0, 'C', '0', '0', 'sales:me:channels:list',   'tree',       'admin', NOW(), NULL),
(978502,  '我的业绩',   9785, 2,  'performance',     'sales/me/performance/index',  NULL, 1, 0, 'C', '0', '0', 'sales:me:performance:view','chart',      'admin', NOW(), NULL),
(978503,  '我的预录入', 9785, 3,  'pre-register',    'sales/me/preregister/index',  NULL, 1, 0, 'C', '0', '0', 'sales:me:prereg:list',     'edit',       'admin', NOW(), NULL),
(978504,  '跟进任务',   9785, 4,  'tasks',           'sales/me/tasks/index',        NULL, 1, 0, 'C', '0', '0', 'sales:me:tasks:list',      'checkbox',   'admin', NOW(), NULL),
(978505,  '团队管理',   9785, 5,  'team',            'sales/manager/team/index',    NULL, 1, 0, 'C', '0', '0', 'sales:manager:team:view',  'team',       'admin', NOW(), '仅 jst_sales_manager 可见'),
(978506,  '业绩明细',   9785, 6,  'settlement',      'sales/manager/settlement/index', NULL, 1, 0, 'C', '0', '0', 'sales:manager:settlement:view', 'money', 'admin', NOW(), '仅 jst_sales_manager / admin 可见，含金额');

-- ========== 3. admin 端菜单 (9762 用户渠道下追加 3 项) ==========
-- 注意：父 ID 9762 已存在（用户渠道分组）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(976210, '销售管理',         9762, 10, 'sales',         'jst/sales/index',     NULL, 1, 0, 'C', '0', '0', 'jst:sales:list',     'people',  'admin', NOW(), 'admin 看全员销售档案'),
(976211, '销售业绩看板',     9762, 11, 'sales-board',   'jst/sales/dashboard', NULL, 1, 0, 'C', '0', '0', 'jst:sales:dashboard','chart',   'admin', NOW(), '每销售业绩/提成成本/跟进活跃度'),
(976212, '归属冲突队列',     9762, 12, 'sales-conflict','jst/sales/conflict',  NULL, 1, 0, 'C', '0', '0', 'jst:sales:conflict', 'shield',  'admin', NOW(), 'admin 处理预录入冲突');

-- ========== 4. 销售工作台 — 按钮权限点 ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, create_by, create_time) VALUES
-- 我的渠道
(978511, '渠道详情', 978501, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:channels:detail',  'admin', NOW()),
(978512, '查看完整手机号', 978501, 2, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:channels:phone-full', 'admin', NOW()),
-- 预录入
(978531, '新建预录入', 978503, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:prereg:add',     'admin', NOW()),
(978532, '续期预录入', 978503, 2, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:prereg:renew',   'admin', NOW()),
(978533, '删除预录入', 978503, 3, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:prereg:remove',  'admin', NOW()),
-- 跟进
(978521, '新建跟进',  978501, 11, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:followup:add',  'admin', NOW()),
(978522, '编辑跟进',  978501, 12, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:followup:edit', 'admin', NOW()),
(978523, '打标签',    978501, 13, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:tags:edit',     'admin', NOW()),
-- 任务
(978541, '完成任务',  978504, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:me:tasks:complete', 'admin', NOW()),
-- 主管派任务
(978551, '派任务',     978505, 1, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:manager:tasks:assign', 'admin', NOW()),
(978552, '调整下属归属',978505, 2, NULL, NULL, 1, 0, 'F', '0', '0', 'sales:manager:transfer',     'admin', NOW());

-- ========== 5. admin 销售管理 — 按钮权限点 ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, create_by, create_time) VALUES
(976220, '新建销售',           976210, 1,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:add',            'admin', NOW()),
(976221, '修改费率',           976210, 2,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:edit:rate',      'admin', NOW()),
(976222, '设置主管',           976210, 3,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:edit:manager',   'admin', NOW()),
(976223, '提交离职申请',       976210, 4,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:resign-apply',   'admin', NOW()),
(976224, '立即执行离职',       976210, 5,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:resign-execute', 'admin', NOW()),
(976225, '终结过渡期',         976210, 6,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:transition-end', 'admin', NOW()),
(976230, '裁决冲突',           976212, 1,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:conflict:resolve', 'admin', NOW()),
(976231, '手动绑定渠道-销售',  976212, 2,  NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:binding:manual',   'admin', NOW()),
(976240, '审核月结单',         976210, 10, NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:settlement:review','admin', NOW()),
(976241, '录入打款流水',       976210, 11, NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:settlement:pay',   'admin', NOW()),
(976250, '查看脱敏审计',       976210, 20, NULL, NULL, 1, 0, 'F', '0', '0', 'jst:sales:audit:view',       'admin', NOW());

-- ========== 6. 角色-菜单绑定 ==========
-- jst_sales (501) 看 销售工作台 + 我的渠道/我的业绩/我的预录入/跟进任务（不含团队管理 / 业绩明细）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(501, 9785),
(501, 978501), (501, 978511), (501, 978512), (501, 978521), (501, 978522), (501, 978523),
(501, 978502),
(501, 978503), (501, 978531), (501, 978532), (501, 978533),
(501, 978504), (501, 978541);

-- jst_sales_manager (502) 看销售工作台全部 + 业绩明细
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(502, 9785),
(502, 978501), (502, 978511), (502, 978512), (502, 978521), (502, 978522), (502, 978523),
(502, 978502),
(502, 978503), (502, 978531), (502, 978532), (502, 978533),
(502, 978504), (502, 978541),
(502, 978505), (502, 978551), (502, 978552),
(502, 978506);

-- admin (1) 已经默认全部权限，无需额外绑定。但 jst_operator (业务运营) 需要看 admin 销售管理菜单
-- 假设 jst_operator 角色 role_id 是 100（请按实际查询调整）
-- 注：本计划不绑 jst_operator，留给计划 #2 / #4 视需求决定
```

- [ ] **Step 3: 应用 SQL**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-role-menu.sql"
```
Expected: 无错误。

- [ ] **Step 4: 验证角色与菜单写入**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT role_id, role_key, role_name FROM sys_role WHERE role_id IN (501,502);
SELECT COUNT(*) AS sales_menu_count FROM sys_menu WHERE menu_id BETWEEN 9785 AND 978999;
SELECT COUNT(*) AS admin_menu_count FROM sys_menu WHERE menu_id BETWEEN 976210 AND 976299;
SELECT role_id, COUNT(*) AS bound_menu_count FROM sys_role_menu WHERE role_id IN (501,502) GROUP BY role_id;
" jst
```
Expected:
- 销售工作台菜单 ~19 行（6 二级 + 12 按钮 + 1 顶级）
- admin 销售菜单 ~14 行（3 C + 11 F）
- jst_sales 绑定 ~14 行 / jst_sales_manager 绑定 ~21 行

- [ ] **Step 5: 提交**

```bash
git add "架构设计/ddl/99-migration-sales-distribution-role-menu.sql"
git commit -m "feat(ddl): 新增 jst_sales/jst_sales_manager 角色 + 9785 销售工作台菜单段 + 9762 下加 admin 销售管理 3 项"
```

---

## Task 6: JstLoginContext + Enricher 加 salesId 支持

**Files:**
- Modify: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/context/JstLoginContext.java`
- Modify: `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/login/JstLoginEnricherImpl.java`
- Create: `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/mapper/SalesLookupMapper.java`
- Create: `RuoYi-Vue/jst-user/src/main/resources/mapper/user/SalesLookupMapper.xml`

- [ ] **Step 1: 修改 JstLoginContext.java，加 salesId 字段和访问方法**

读取现有文件内容确认结构后，在文件中追加 salesId 字段。

具体改动：
- 在 channelId 字段下方加：`private Long salesId;` 字段，附 javadoc "销售档案 ID（销售或销售主管）"
- 在 currentChannelId() 方法下方加 `public static Long currentSalesId() { return current().salesId; }`
- 加 getter/setter

完整代码块（追加到现有文件 `private Long channelId;` 后）：

```java
    /** 关联销售档案ID (若当前用户为销售/销售主管) */
    private Long salesId;
```

在 `public static Long currentChannelId()` 方法后添加：
```java
    /** 当前销售ID(平台运营/学生/渠道时为 null) */
    public static Long currentSalesId() {
        return current().salesId;
    }
```

在最后 getter/setter 区域添加：
```java
    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
```

- [ ] **Step 2: 创建 SalesLookupMapper.java**

```java
package com.ruoyi.jst.user.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 跨域查询：根据 sys_user_id 反查 sales_id（避免 jst-user 直接依赖 jst-channel）
 */
public interface SalesLookupMapper {
    Long selectSalesIdByUserId(@Param("userId") Long userId);
}
```

- [ ] **Step 3: 创建 SalesLookupMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.jst.user.mapper.SalesLookupMapper">
    <select id="selectSalesIdByUserId" resultType="java.lang.Long">
        SELECT sales_id FROM jst_sales 
         WHERE sys_user_id = #{userId}
           AND status IN ('active','resign_apply','resigned_pending_settle')
         LIMIT 1
    </select>
</mapper>
```

- [ ] **Step 4: 修改 JstLoginEnricherImpl.java，注入 SalesLookupMapper 并填充 salesId**

在现有文件追加：
- 在 PartnerLookupMapper 字段下方加 `@Autowired private SalesLookupMapper salesLookupMapper;`
- 在 `enrich()` 方法末尾加：
```java
        // 4. 查 jst_sales 拿 salesId
        Long salesId = salesLookupMapper.selectSalesIdByUserId(userId);
        ctx.setSalesId(salesId);
```

- [ ] **Step 5: 编译**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl jst-common,jst-user -am clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/context/JstLoginContext.java \
        RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/login/JstLoginEnricherImpl.java \
        RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/mapper/SalesLookupMapper.java \
        RuoYi-Vue/jst-user/src/main/resources/mapper/user/SalesLookupMapper.xml
git commit -m "feat(common): JstLoginContext 加 salesId + Enricher 通过 SalesLookupMapper 填充"
```

---

## Task 7: SalesScopeUtils 单元测试 + 实现

**Files:**
- Create: `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/util/SalesScopeUtilsTest.java`
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesScopeUtils.java`

- [ ] **Step 1: 写失败测试**

写入 `SalesScopeUtilsTest.java`：

```java
package com.ruoyi.jst.common.util;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.context.JstLoginContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SalesScopeUtilsTest {

    @BeforeEach
    void setUp() {
        JstLoginContext.clear();
    }

    @AfterEach
    void tearDown() {
        JstLoginContext.clear();
    }

    @Test
    void currentSalesId_returnsNull_whenNoContext() {
        assertNull(SalesScopeUtils.currentSalesId());
    }

    @Test
    void currentSalesId_returnsValue_fromContext() {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setSalesId(1001L);
        JstLoginContext.set(ctx);
        assertEquals(1001L, SalesScopeUtils.currentSalesId());
    }

    @Test
    void currentSalesIdRequired_throws_whenNotSalesRole() {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setRoleKeys(Collections.singletonList("jst_partner"));
        JstLoginContext.set(ctx);
        assertThrows(RuntimeException.class, SalesScopeUtils::currentSalesIdRequired);
    }

    @Test
    void currentSalesIdRequired_returnsId_whenSalesRoleAndIdPresent() {
        JstLoginContext ctx = new JstLoginContext();
        ctx.setRoleKeys(Collections.singletonList("jst_sales"));
        ctx.setSalesId(2002L);
        JstLoginContext.set(ctx);
        assertEquals(2002L, SalesScopeUtils.currentSalesIdRequired());
    }

    @Test
    void isSalesRole_returnsTrue_forSalesOrManagerRole() {
        LoginUser u = mockLoginUser("jst_sales");
        assertTrue(SalesScopeUtils.isSalesRole(u));

        LoginUser m = mockLoginUser("jst_sales_manager");
        assertTrue(SalesScopeUtils.isSalesRole(m));

        LoginUser p = mockLoginUser("jst_partner");
        assertFalse(SalesScopeUtils.isSalesRole(p));
    }

    @Test
    void isManagerRole_returnsTrue_onlyForManagerRole() {
        assertTrue(SalesScopeUtils.isManagerRole(mockLoginUser("jst_sales_manager")));
        assertFalse(SalesScopeUtils.isManagerRole(mockLoginUser("jst_sales")));
    }

    @Test
    void isPlatformOpOrAdminOrManager_returnsTrue_forAdmin() {
        SysUser admin = new SysUser();
        admin.setUserId(1L);  // userId=1 在若依里是 admin
        LoginUser u = new LoginUser();
        u.setUser(admin);
        u.setRoles(Collections.emptyList());
        assertTrue(SalesScopeUtils.isPlatformOpOrAdminOrManager(u));
    }

    @Test
    void isPlatformOpOrAdminOrManager_returnsTrue_forSalesManager() {
        assertTrue(SalesScopeUtils.isPlatformOpOrAdminOrManager(mockLoginUser("jst_sales_manager")));
    }

    @Test
    void isPlatformOpOrAdminOrManager_returnsFalse_forPlainSales() {
        assertFalse(SalesScopeUtils.isPlatformOpOrAdminOrManager(mockLoginUser("jst_sales")));
    }

    private LoginUser mockLoginUser(String roleKey) {
        SysUser user = new SysUser();
        user.setUserId(100L);
        SysRole role = new SysRole();
        role.setRoleKey(roleKey);
        user.setRoles(Arrays.asList(role));
        LoginUser lu = new LoginUser();
        lu.setUser(user);
        lu.setRoles(Arrays.asList(role));
        return lu;
    }
}
```

- [ ] **Step 2: 运行测试，验证失败**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl jst-common test -Dtest=SalesScopeUtilsTest -q
```
Expected: 编译失败 — `SalesScopeUtils` 类不存在。

- [ ] **Step 3: 实现 SalesScopeUtils**

写入 `SalesScopeUtils.java`：

```java
package com.ruoyi.jst.common.util;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;

import java.util.List;

/**
 * 销售身份与数据隔离工具类（复刻 PartnerScopeUtils 模式）
 */
public final class SalesScopeUtils {

    public static final String ROLE_SALES = "jst_sales";
    public static final String ROLE_SALES_MANAGER = "jst_sales_manager";
    public static final String ROLE_PLATFORM_OP = "jst_platform_op";
    public static final String ROLE_OPERATOR = "jst_operator";

    private SalesScopeUtils() {}

    /** 当前销售 ID（非销售返 null） */
    public static Long currentSalesId() {
        return JstLoginContext.currentSalesId();
    }

    /** 强制要求是销售身份 + 已绑定 sales_id */
    public static Long currentSalesIdRequired() {
        LoginUser loginUser = getLoginUserQuietly();
        if (!isSalesRole(loginUser)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Long salesId = currentSalesId();
        if (salesId == null) {
            throw new ServiceException("当前销售账号未绑定 salesId",
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return salesId;
    }

    /** 销售或销售主管 */
    public static boolean isSalesRole(LoginUser loginUser) {
        return hasRole(loginUser, ROLE_SALES) || hasRole(loginUser, ROLE_SALES_MANAGER);
    }

    /** 仅销售主管 */
    public static boolean isManagerRole(LoginUser loginUser) {
        return hasRole(loginUser, ROLE_SALES_MANAGER);
    }

    /** admin / 平台运营 / 销售主管：可看全量金额 */
    public static boolean isPlatformOpOrAdminOrManager(LoginUser loginUser) {
        if (loginUser == null) return false;
        if (loginUser.getUser() != null && loginUser.getUser().isAdmin()) return true;
        return hasRole(loginUser, ROLE_PLATFORM_OP)
                || hasRole(loginUser, ROLE_OPERATOR)
                || hasRole(loginUser, ROLE_SALES_MANAGER);
    }

    /** 当前用户是否仅有 jst_sales 角色（用于金额脱敏判断，主管不脱敏） */
    public static boolean isPlainSalesOnly(LoginUser loginUser) {
        return isSalesRole(loginUser)
                && !isPlatformOpOrAdminOrManager(loginUser);
    }

    public static LoginUser getLoginUserQuietly() {
        try {
            return SecurityUtils.getLoginUser();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean hasRole(LoginUser loginUser, String roleKey) {
        if (loginUser == null || loginUser.getUser() == null) return false;
        List<SysRole> roles = loginUser.getUser().getRoles();
        if (roles == null) return false;
        for (SysRole role : roles) {
            if (role != null && StringUtils.equals(roleKey, role.getRoleKey())) {
                return true;
            }
        }
        return false;
    }
}
```

- [ ] **Step 4: 运行测试，验证通过**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl jst-common test -Dtest=SalesScopeUtilsTest -q
```
Expected: BUILD SUCCESS, Tests run: 9, Failures: 0。

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesScopeUtils.java \
        RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/util/SalesScopeUtilsTest.java
git commit -m "feat(common): SalesScopeUtils 工具类（复刻 PartnerScope 模式）+ 9 单测"
```

---

## Task 8: BaseSalesController 基类

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/controller/BaseSalesController.java`

- [ ] **Step 1: 创建 BaseSalesController**

```java
package com.ruoyi.jst.common.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 销售工作台 / admin 销售管理通用基类。
 * <p>
 * 类级权限：jst_sales / jst_sales_manager / admin / jst_operator。
 * 子类按需在方法上加细化权限点 @PreAuthorize("@ss.hasPermi('jst:sales:xxx')")。
 */
@PreAuthorize("@ss.hasAnyRoles('jst_sales,jst_sales_manager,admin,jst_operator')")
public abstract class BaseSalesController extends BaseController {

    /**
     * 当前销售 ID。
     * - 销售/销售主管：返回自己的 sales_id
     * - admin / jst_operator：返 null（业务层据此判断是否过滤）
     */
    protected Long currentSalesId() {
        return SalesScopeUtils.currentSalesId();
    }
}
```

- [ ] **Step 2: 编译**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl jst-common -am clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/controller/BaseSalesController.java
git commit -m "feat(common): BaseSalesController 基类（角色门面 + currentSalesId）"
```

---

## Task 9: @Sensitive 注解 + SensitiveMasker 反射工具 + 单测

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/annotation/Sensitive.java`
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/SensitiveMasker.java`
- Create: `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/aspect/SensitiveMaskerTest.java`

- [ ] **Step 1: 创建 @Sensitive 注解**

```java
package com.ruoyi.jst.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段标记。配合 SensitiveMasker / MaskSalaryAspect 在响应序列化前置 null 或脱敏值。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
    /** 金额（销售本人不可见，主管/admin 可见） */
    boolean money() default false;
    /** 手机号（所有销售看渠道详情时需点"查看完整"才显示，AC1） */
    boolean phone() default false;
    /** 身份证号（仅 admin 可见） */
    boolean idcard() default false;
    /** 销售费率（销售本人不可见） */
    boolean rate() default false;
}
```

- [ ] **Step 2: 写 SensitiveMaskerTest**

```java
package com.ruoyi.jst.common.aspect;

import com.ruoyi.jst.common.annotation.Sensitive;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveMaskerTest {

    static class Vo {
        @Sensitive(money = true)
        BigDecimal amount = new BigDecimal("123.45");

        @Sensitive(phone = true)
        String phone = "13912345678";

        @Sensitive(rate = true)
        BigDecimal rate = new BigDecimal("0.05");

        @Sensitive(idcard = true)
        String idcard = "110101199001011234";

        String name = "alice";
    }

    @Test
    void maskMoney_setsAmountAndRateToNull_keepsOthersUnchanged() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, true, false, false);
        assertNull(vo.amount);
        assertNull(vo.rate);
        assertEquals("13912345678", vo.phone);
        assertEquals("110101199001011234", vo.idcard);
        assertEquals("alice", vo.name);
    }

    @Test
    void maskPhone_replacesMiddleFourDigitsWithStars() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, false, true, false);
        assertEquals("139****5678", vo.phone);
        assertNotNull(vo.amount);
    }

    @Test
    void maskIdcard_replacesMiddleEightDigitsWithStars() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, false, false, true);
        assertEquals("110101********1234", vo.idcard);
    }

    @Test
    void maskAll_appliesAllRulesAtOnce() {
        Vo vo = new Vo();
        SensitiveMasker.maskFields(vo, true, true, true);
        assertNull(vo.amount);
        assertNull(vo.rate);
        assertEquals("139****5678", vo.phone);
        assertEquals("110101********1234", vo.idcard);
    }

    @Test
    void maskList_iteratesAndApplies() {
        List<Vo> list = Arrays.asList(new Vo(), new Vo());
        SensitiveMasker.maskFields(list, true, false, false);
        assertNull(list.get(0).amount);
        assertNull(list.get(1).amount);
    }

    @Test
    void mask_handlesNullObjectGracefully() {
        assertDoesNotThrow(() -> SensitiveMasker.maskFields(null, true, true, true));
    }

    @Test
    void mask_handlesShortPhoneGracefully() {
        Vo vo = new Vo();
        vo.phone = "139";
        SensitiveMasker.maskFields(vo, false, true, false);
        // 太短直接保留原值，不报错
        assertEquals("139", vo.phone);
    }
}
```

- [ ] **Step 3: 运行测试，验证失败**

Run:
```bash
mvn -pl RuoYi-Vue/jst-common test -Dtest=SensitiveMaskerTest -q
```
Expected: 编译失败 — `SensitiveMasker` 类不存在。

- [ ] **Step 4: 实现 SensitiveMasker**

```java
package com.ruoyi.jst.common.aspect;

import com.ruoyi.jst.common.annotation.Sensitive;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 反射工具：根据 @Sensitive 注解掩码字段值。
 * 支持：单对象 / Collection / Map.values / 嵌套对象（仅一层，避免循环引用）
 */
public final class SensitiveMasker {

    private SensitiveMasker() {}

    public static void maskFields(Object target, boolean maskMoney, boolean maskPhone, boolean maskIdcard) {
        if (target == null) return;
        if (target instanceof Collection) {
            for (Object item : (Collection<?>) target) {
                maskOne(item, maskMoney, maskPhone, maskIdcard);
            }
            return;
        }
        if (target instanceof Map) {
            for (Object v : ((Map<?, ?>) target).values()) {
                maskOne(v, maskMoney, maskPhone, maskIdcard);
            }
            return;
        }
        maskOne(target, maskMoney, maskPhone, maskIdcard);
    }

    private static void maskOne(Object target, boolean maskMoney, boolean maskPhone, boolean maskIdcard) {
        if (target == null) return;
        Class<?> clazz = target.getClass();
        if (clazz.getName().startsWith("java.")) return;  // 不处理 jdk 类型
        for (Field field : clazz.getDeclaredFields()) {
            Sensitive ann = field.getAnnotation(Sensitive.class);
            if (ann == null) continue;
            try {
                field.setAccessible(true);
                Object value = field.get(target);
                if (value == null) continue;
                if (maskMoney && (ann.money() || ann.rate())) {
                    field.set(target, null);
                }
                if (maskPhone && ann.phone() && value instanceof String) {
                    field.set(target, maskMobile((String) value));
                }
                if (maskIdcard && ann.idcard() && value instanceof String) {
                    field.set(target, maskIdcard((String) value));
                }
            } catch (IllegalAccessException ignored) {
                // skip
            }
        }
    }

    private static String maskMobile(String s) {
        if (s == null || s.length() < 11) return s;
        return s.substring(0, 3) + "****" + s.substring(7);
    }

    private static String maskIdcard(String s) {
        if (s == null || s.length() < 18) return s;
        return s.substring(0, 6) + "********" + s.substring(14);
    }
}
```

- [ ] **Step 5: 运行测试，验证通过**

Run:
```bash
mvn -pl RuoYi-Vue/jst-common test -Dtest=SensitiveMaskerTest -q
```
Expected: BUILD SUCCESS, Tests: 7。

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/annotation/Sensitive.java \
        RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/SensitiveMasker.java \
        RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/aspect/SensitiveMaskerTest.java
git commit -m "feat(common): @Sensitive 注解 + SensitiveMasker 反射工具 + 7 单测"
```

---

## Task 10: MaskSalaryAspect AOP 切面

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/MaskSalaryAspect.java`

- [ ] **Step 1: 实现 AOP 切面**

```java
package com.ruoyi.jst.common.aspect;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 金额脱敏切面。
 * <p>
 * 触发条件：当前用户仅有 jst_sales 角色（非主管/非 admin/非平台运营）。
 * 范围：所有继承 BaseSalesController 的接口的返回值。
 * 行为：反射置 null 所有 @Sensitive(money=true 或 rate=true) 字段；@Sensitive(phone=true) 走脱敏字符串。
 */
@Aspect
@Component
public class MaskSalaryAspect {

    @Pointcut("execution(* com.ruoyi.jst..controller..*Controller.*(..)) " +
              "&& target(com.ruoyi.jst.common.controller.BaseSalesController)")
    public void salesEndpoints() {}

    @AfterReturning(pointcut = "salesEndpoints()", returning = "result")
    public void mask(JoinPoint pjp, Object result) {
        LoginUser loginUser = SalesScopeUtils.getLoginUserQuietly();
        boolean maskMoney = SalesScopeUtils.isPlainSalesOnly(loginUser);
        boolean maskPhone = SalesScopeUtils.isSalesRole(loginUser);  // AC1: 所有销售常态脱敏
        if (!maskMoney && !maskPhone) {
            return;
        }

        Object payload = unwrap(result);
        if (payload != null) {
            SensitiveMasker.maskFields(payload, maskMoney, maskPhone, false);
        }
    }

    /** 拆 AjaxResult / TableDataInfo 拿到真实业务对象 */
    private Object unwrap(Object result) {
        if (result instanceof AjaxResult) {
            return ((AjaxResult) result).get(AjaxResult.DATA_TAG);
        }
        if (result instanceof TableDataInfo) {
            return ((TableDataInfo) result).getRows();
        }
        return result;
    }
}
```

- [ ] **Step 2: 编译**

Run:
```bash
mvn -pl RuoYi-Vue/jst-common -am clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

> 注：此切面的端到端测试需要 Web 上下文，本计划留到计划 #2 / #3 真实接口落地后通过手工 curl + 多角色 token 验证。

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/aspect/MaskSalaryAspect.java
git commit -m "feat(common): MaskSalaryAspect AOP 切面（销售本人金额/费率脱敏 + 全销售手机号常态脱敏）"
```

---

## Task 11: SalesExportWatermarkInterceptor 导出水印拦截器 + 单测

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/interceptor/SalesExportWatermarkInterceptor.java`
- Create: `RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/interceptor/SalesExportWatermarkInterceptorTest.java`

- [ ] **Step 1: 写测试（仅测水印字符串拼接 + audit log 调用，不测 Servlet 包装）**

```java
package com.ruoyi.jst.common.interceptor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesExportWatermarkInterceptorTest {

    @Test
    void buildWatermarkText_containsNameAndTime() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 18, 14, 30, 0);
        String text = SalesExportWatermarkInterceptor.buildWatermarkText("张三", "S202604001", now);
        assertTrue(text.contains("张三"));
        assertTrue(text.contains("S202604001"));
        assertTrue(text.contains("2026-04-18 14:30:00"));
        assertTrue(text.contains("仅供内部使用") || text.contains("外泄追责"));
    }

    @Test
    void buildCsvWatermark_startsWithHash() {
        String csv = SalesExportWatermarkInterceptor.buildCsvWatermark("alice", "S001",
                LocalDateTime.of(2026, 4, 18, 9, 0, 0));
        assertTrue(csv.startsWith("# Exported"));
        assertTrue(csv.contains("alice"));
    }
}
```

- [ ] **Step 2: 运行测试，验证失败**

Run:
```bash
mvn -pl RuoYi-Vue/jst-common test -Dtest=SalesExportWatermarkInterceptorTest -q
```
Expected: 编译失败 — 类不存在。

- [ ] **Step 3: 实现拦截器（仅写工具方法 + Spring HandlerInterceptor 钩子，实际包装 Excel/PDF 留给计划 #4 集成时处理）**

```java
package com.ruoyi.jst.common.interceptor;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 销售导出水印拦截器（AC3）。
 * <p>
 * 拦截路径：/sales/**\/export, /sales/**\/download 等销售可触发的导出接口。
 * 行为：在响应 header 写入水印元信息（X-Export-Watermark），下游具体 export 服务读取后插入 Excel/PDF/CSV。
 *      同步写 sys_oper_log（business_type='SALES_EXPORT'，由若依 @Log 注解负责，本拦截器不重复写）。
 * <p>
 * 注意：本类仅做 header 注入和工具方法，真正插入水印行的逻辑在计划 #4 / #3 的具体 export 接口里调用工具方法。
 */
@Component
public class SalesExportWatermarkInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SalesExportWatermarkInterceptor.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (uri == null) return true;
        if (!(uri.contains("/export") || uri.contains("/download"))) return true;
        if (!uri.startsWith("/sales/")) return true;

        LoginUser u = SalesScopeUtils.getLoginUserQuietly();
        if (u == null || u.getUser() == null) return true;
        if (!SalesScopeUtils.isSalesRole(u)) return true;

        String name = u.getUser().getNickName();
        String userNo = String.valueOf(u.getUser().getUserId());
        String watermark = buildWatermarkText(name, userNo, LocalDateTime.now());
        response.setHeader("X-Export-Watermark", watermark);
        log.info("[SalesExport] user={} uri={} watermarkAttached", name, uri);
        return true;
    }

    /** 通用水印文本（Excel 首行 / PDF 页脚） */
    public static String buildWatermarkText(String name, String userNo, LocalDateTime time) {
        return String.format("本表由销售[%s/%s]于[%s]导出，仅供内部使用，外泄追责",
                name, userNo, time.format(FMT));
    }

    /** CSV 注释行 */
    public static String buildCsvWatermark(String name, String userNo, LocalDateTime time) {
        return String.format("# Exported by %s(%s) at %s", name, userNo, time.format(FMT));
    }
}
```

- [ ] **Step 4: 运行测试，验证通过**

Run:
```bash
mvn -pl RuoYi-Vue/jst-common test -Dtest=SalesExportWatermarkInterceptorTest -q
```
Expected: BUILD SUCCESS, Tests: 2。

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/interceptor/SalesExportWatermarkInterceptor.java \
        RuoYi-Vue/jst-common/src/test/java/com/ruoyi/jst/common/interceptor/SalesExportWatermarkInterceptorTest.java
git commit -m "feat(common): SalesExportWatermarkInterceptor 拦截器 + 水印文本工具 + 2 单测"
```

---

## Task 12: 注册拦截器到 Web 配置

**Files:**
- Create: `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/config/SalesScopeWebConfig.java`

- [ ] **Step 1: 创建 Web 配置类**

```java
package com.ruoyi.jst.common.config;

import com.ruoyi.jst.common.interceptor.SalesExportWatermarkInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SalesScopeWebConfig implements WebMvcConfigurer {

    @Autowired
    private SalesExportWatermarkInterceptor exportWatermarkInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(exportWatermarkInterceptor)
                .addPathPatterns("/sales/**", "/admin/sales/**")
                .order(100);
    }
}
```

- [ ] **Step 2: 编译并启动 sanity check**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl jst-common,ruoyi-admin -am clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/config/SalesScopeWebConfig.java
git commit -m "feat(common): SalesScopeWebConfig 注册导出水印拦截器到 /sales/** 与 /admin/sales/** 路径"
```

---

## Task 13: 9 张表的 Domain 实体 + Mapper 接口

> 本任务批量创建 Domain 类（用 RuoYi 标准 getter/setter，不引 Lombok）和 Mapper 接口。Mapper XML 仅留 BaseResultMap + 基础 CRUD（业务查询全部留给计划 #2~#4 按需补充）。

**Files:**
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSales.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesCommissionRate.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesPreRegister.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesChannelBinding.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstChannelInvite.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesCommissionLedger.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesCommissionSettlement.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstChannelDistributionLedger.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesFollowupRecord.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesChannelTag.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesFollowupTask.java`
- Create: `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSalesAttributionConflict.java`
- Create: 12 个对应的 `*Mapper.java`
- Create: 12 个对应的 `*Mapper.xml`

- [ ] **Step 1: 创建 JstSales.java（销售档案）**

```java
package com.ruoyi.jst.channel.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class JstSales extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long salesId;
    private Long sysUserId;
    private String salesNo;
    private String salesName;
    private String phone;
    private Long managerId;
    private Integer isManager;
    private String status;
    private Date resignApplyDate;
    private Date expectedResignDate;
    private Date actualResignDate;
    private Date transitionEndDate;
    private BigDecimal commissionRateDefault;
    private BigDecimal managerCommissionRate;

    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }
    public Long getSysUserId() { return sysUserId; }
    public void setSysUserId(Long sysUserId) { this.sysUserId = sysUserId; }
    public String getSalesNo() { return salesNo; }
    public void setSalesNo(String salesNo) { this.salesNo = salesNo; }
    public String getSalesName() { return salesName; }
    public void setSalesName(String salesName) { this.salesName = salesName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public Integer getIsManager() { return isManager; }
    public void setIsManager(Integer isManager) { this.isManager = isManager; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getResignApplyDate() { return resignApplyDate; }
    public void setResignApplyDate(Date resignApplyDate) { this.resignApplyDate = resignApplyDate; }
    public Date getExpectedResignDate() { return expectedResignDate; }
    public void setExpectedResignDate(Date expectedResignDate) { this.expectedResignDate = expectedResignDate; }
    public Date getActualResignDate() { return actualResignDate; }
    public void setActualResignDate(Date actualResignDate) { this.actualResignDate = actualResignDate; }
    public Date getTransitionEndDate() { return transitionEndDate; }
    public void setTransitionEndDate(Date transitionEndDate) { this.transitionEndDate = transitionEndDate; }
    public BigDecimal getCommissionRateDefault() { return commissionRateDefault; }
    public void setCommissionRateDefault(BigDecimal v) { this.commissionRateDefault = v; }
    public BigDecimal getManagerCommissionRate() { return managerCommissionRate; }
    public void setManagerCommissionRate(BigDecimal v) { this.managerCommissionRate = v; }
}
```

- [ ] **Step 2: 创建 JstSalesMapper.java**

```java
package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSales;
import java.util.List;

public interface JstSalesMapper {
    JstSales selectJstSalesBySalesId(Long salesId);
    List<JstSales> selectJstSalesList(JstSales query);
    int insertJstSales(JstSales row);
    int updateJstSales(JstSales row);
    int deleteJstSalesBySalesId(Long salesId);
}
```

- [ ] **Step 3: 创建 JstSalesMapper.xml**

文件路径：`RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSalesMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.jst.channel.mapper.JstSalesMapper">

    <resultMap type="com.ruoyi.jst.channel.domain.JstSales" id="JstSalesResult">
        <id     property="salesId"               column="sales_id"/>
        <result property="sysUserId"             column="sys_user_id"/>
        <result property="salesNo"               column="sales_no"/>
        <result property="salesName"             column="sales_name"/>
        <result property="phone"                 column="phone"/>
        <result property="managerId"             column="manager_id"/>
        <result property="isManager"             column="is_manager"/>
        <result property="status"                column="status"/>
        <result property="resignApplyDate"       column="resign_apply_date"/>
        <result property="expectedResignDate"    column="expected_resign_date"/>
        <result property="actualResignDate"      column="actual_resign_date"/>
        <result property="transitionEndDate"     column="transition_end_date"/>
        <result property="commissionRateDefault" column="commission_rate_default"/>
        <result property="managerCommissionRate" column="manager_commission_rate"/>
        <result property="createBy"              column="create_by"/>
        <result property="createTime"            column="create_time"/>
        <result property="updateBy"              column="update_by"/>
        <result property="updateTime"            column="update_time"/>
    </resultMap>

    <sql id="cols">
        sales_id, sys_user_id, sales_no, sales_name, phone, manager_id, is_manager, status,
        resign_apply_date, expected_resign_date, actual_resign_date, transition_end_date,
        commission_rate_default, manager_commission_rate,
        create_by, create_time, update_by, update_time
    </sql>

    <select id="selectJstSalesBySalesId" parameterType="Long" resultMap="JstSalesResult">
        SELECT <include refid="cols"/> FROM jst_sales WHERE sales_id = #{salesId}
    </select>

    <select id="selectJstSalesList" parameterType="com.ruoyi.jst.channel.domain.JstSales" resultMap="JstSalesResult">
        SELECT <include refid="cols"/> FROM jst_sales
        <where>
            <if test="status != null and status != ''">AND status = #{status}</if>
            <if test="managerId != null">AND manager_id = #{managerId}</if>
            <if test="isManager != null">AND is_manager = #{isManager}</if>
            <if test="salesName != null and salesName != ''">AND sales_name LIKE CONCAT('%', #{salesName}, '%')</if>
            <if test="phone != null and phone != ''">AND phone = #{phone}</if>
        </where>
        ORDER BY create_time DESC
    </select>

    <insert id="insertJstSales" parameterType="com.ruoyi.jst.channel.domain.JstSales">
        INSERT INTO jst_sales (
            sales_id, sys_user_id, sales_no, sales_name, phone, manager_id, is_manager, status,
            resign_apply_date, expected_resign_date, actual_resign_date, transition_end_date,
            commission_rate_default, manager_commission_rate,
            create_by, create_time
        ) VALUES (
            #{salesId}, #{sysUserId}, #{salesNo}, #{salesName}, #{phone}, #{managerId},
            #{isManager}, #{status},
            #{resignApplyDate}, #{expectedResignDate}, #{actualResignDate}, #{transitionEndDate},
            #{commissionRateDefault}, #{managerCommissionRate},
            #{createBy}, NOW()
        )
    </insert>

    <update id="updateJstSales" parameterType="com.ruoyi.jst.channel.domain.JstSales">
        UPDATE jst_sales
        <set>
            <if test="sysUserId != null">sys_user_id = #{sysUserId},</if>
            <if test="salesNo != null">sales_no = #{salesNo},</if>
            <if test="salesName != null">sales_name = #{salesName},</if>
            <if test="phone != null">phone = #{phone},</if>
            <if test="managerId != null">manager_id = #{managerId},</if>
            <if test="isManager != null">is_manager = #{isManager},</if>
            <if test="status != null">status = #{status},</if>
            <if test="resignApplyDate != null">resign_apply_date = #{resignApplyDate},</if>
            <if test="expectedResignDate != null">expected_resign_date = #{expectedResignDate},</if>
            <if test="actualResignDate != null">actual_resign_date = #{actualResignDate},</if>
            <if test="transitionEndDate != null">transition_end_date = #{transitionEndDate},</if>
            <if test="commissionRateDefault != null">commission_rate_default = #{commissionRateDefault},</if>
            <if test="managerCommissionRate != null">manager_commission_rate = #{managerCommissionRate},</if>
            <if test="updateBy != null">update_by = #{updateBy},</if>
            update_time = NOW()
        </set>
        WHERE sales_id = #{salesId}
    </update>

    <delete id="deleteJstSalesBySalesId" parameterType="Long">
        DELETE FROM jst_sales WHERE sales_id = #{salesId}
    </delete>
</mapper>
```

- [ ] **Step 4: 重复 Step 1~3 的 Domain + Mapper + XML 三件套，对剩余 11 张表逐个生成**

按下表逐个创建（每张表的字段都来自 Task 2 的 DDL）：

| Domain 类名 | 主键字段 | 关键业务字段 |
|---|---|---|
| `JstSalesCommissionRate` | id | salesId, businessType, rate, effectiveFrom |
| `JstSalesPreRegister` | preId | salesId, phone, targetName, status, matchedChannelId, matchedAt, expireAt, renewCount |
| `JstSalesChannelBinding` | bindingId | channelId, salesId, effectiveFrom, effectiveTo, bindSource, bindRemark, operatorId |
| `JstChannelInvite` | inviteId | inviterChannelId, inviteeChannelId, inviteCode, shareScene, commissionRate, invitedAt, status |
| `JstSalesCommissionLedger` | ledgerId | salesId, orderId, orderNo, channelId, bindingType, businessType, baseAmount, appliedRate, rawAmount, compressRatio, amount, status, accrueAt, accruedAt, settlementId |
| `JstSalesCommissionSettlement` | settlementId | salesId, period, totalCount, totalAmount, status, approvedBy, approvedAt, paidAt, payVoucher, rejectReason |
| `JstChannelDistributionLedger` | ledgerId | inviterChannelId, inviteeChannelId, orderId, orderNo, baseAmount, appliedRate, rawAmount, compressRatio, amount, status, accrueAt, accruedAt |
| `JstSalesFollowupRecord` | recordId | salesId, channelId, followupType, followupAt, content, attachmentUrls, mood, nextFollowupAt, canEditUntil |
| `JstSalesChannelTag` | id | channelId, tagCode, tagColor, createBy |
| `JstSalesFollowupTask` | taskId | assigneeSalesId, assignerId, channelId, title, description, dueDate, status, completedAt, linkedRecordId |
| `JstSalesAttributionConflict` | conflictId | channelId, candidateSalesIds (String), reason, status, resolvedBy, resolvedAt, resolutionRemark |

每个文件按 Task 13 Step 1~3 的模式生成（Java POJO 用基础 getter/setter；Mapper 接口包含 selectById/selectList/insert/update/deleteById 五个方法；XML 包含 resultMap + cols sql 片段 + 5 个语句）。

> `attachmentUrls` 字段是 JSON，Java 类型用 `String`（业务层负责 parse）。
> `candidateSalesIds` 字段同上。

- [ ] **Step 5: 编译全部 jst-channel 模块**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl jst-channel -am clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstSales*.java \
        RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/domain/JstChannel*.java \
        RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/JstSales*.java \
        RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/JstChannel*.java \
        RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSales*.xml \
        RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstChannel*.xml
git commit -m "feat(channel): 12 个 Domain + Mapper + XML（销售/邀请/账本/月结/CRM/冲突）"
```

---

## Task 14: 全量编译 + 启动 sanity check + 验证迁移幂等

**Files:** （只读校验）

- [ ] **Step 1: 全量编译**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn clean compile -DskipTests -q
```
Expected: BUILD SUCCESS。

- [ ] **Step 2: 启动 ruoyi-admin（不含端口冲突时）**

Run:
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl ruoyi-admin spring-boot:run -DskipTests
```
Expected: 成功启动并监听 8080，日志中能看到 `Started RuoYiApplication`。无 NoSuchBeanDefinitionException / Mapper 注册失败 / 拦截器注册异常。

启动后用 Ctrl+C 停止。

- [ ] **Step 3: 验证 SQL 迁移可重复跑（drop + recreate）**

Run:
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-ddl.sql"
```
Expected: 因为 DDL 文件用了 `DROP TABLE IF EXISTS`，第二次跑也成功，但 ALTER TABLE jst_channel 部分会因字段已存在报错。

> ⚠️ ALTER 部分需要手工幂等性确认：
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
DESCRIBE jst_channel;
" jst | grep -E 'business_no|invite_code|parent_invite|is_official'
```
Expected: 5 个字段都还在（首次 ALTER 已加，第二次失败不影响数据）。

> 修复策略：DDL 文件的 ALTER 段后续要改写为"动态 IF NOT EXISTS"或单独抽到一个版本控制的 idempotent 迁移文件。本计划不修，留待生产部署前由 DBA 处理。在 spec § 7 "待确认" 表里追加一项。

- [ ] **Step 4: 验证字典/sysparam/角色 SQL 也是幂等（除 INSERT 自增冲突外）**

仅做读取校验：
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT 'dict' AS t, COUNT(*) FROM sys_dict_data WHERE dict_type LIKE 'jst_sales_%'
UNION ALL
SELECT 'config', COUNT(*) FROM sys_config WHERE config_key LIKE 'jst.sales.%'
UNION ALL
SELECT 'role', COUNT(*) FROM sys_role WHERE role_id IN (501,502)
UNION ALL
SELECT 'menu', COUNT(*) FROM sys_menu WHERE menu_id BETWEEN 9785 AND 978999;
" jst
```
Expected: 数值与 Task 3/4/5 验证步骤一致。

---

## Task 15: 更新 CLAUDE.md 记录计划 #1 完成

**Files:**
- Modify: `CLAUDE.md`

- [ ] **Step 1: 修改 CLAUDE.md 顶部"上次更新"行**

将 `> 会话恢复文件。上次更新：2026-04-18（**销售管理 + 渠道分销** 设计稿完成...）` 替换为：

`> 会话恢复文件。上次更新：2026-04-XX（**销售管理 + 渠道分销** 计划 #1 DDL+基础架构完成；plan-02-sales-core 待启动）`

（XX 用执行当天的日期）

- [ ] **Step 2: 修改 §六 待处理表 SALES-DISTRIBUTION 行**

将 `**P0 🟡** | SALES-DISTRIBUTION | 🟡 设计完成（2026-04-18），spec 已 commit ebe17a7 | ...` 一整行改为：

`**P0 🟡** | SALES-DISTRIBUTION | 🟡 计划 #1 完成（DDL+基础架构），plan-02 待启动 | spec：docs/superpowers/specs/2026-04-18-sales-channel-distribution-design.md。计划 #1 已交付：9 张新表 + jst_channel 加 5 字段 + 9 字典类型 43 条数据 + 8 系统参数 + 2 角色 + 9785 销售工作台菜单段 + 9762 下加 admin 销售管理 3 项 + jst-common 模块 SalesScopeUtils/BaseSalesController/@Sensitive/SensitiveMasker/MaskSalaryAspect/SalesExportWatermarkInterceptor + JstLoginContext/Enricher 加 salesId + jst-channel 12 Domain+Mapper+XML 三件套。下一步 plan-02-sales-core：自动绑定算法+预录入+提成计算管线+8 Quartz 任务。`

- [ ] **Step 3: 提交**

```bash
git add CLAUDE.md
git commit -m "docs: CLAUDE.md 登记 SALES-DISTRIBUTION 计划 #1 完成（DDL+基础架构）"
```

---

## Task 16: 合并到 main

- [ ] **Step 1: 确保所有改动已提交，工作目录干净**

Run:
```bash
git status
```
Expected: `working tree clean`，仅 `feature/sales-distribution-01-ddl-infra` 分支领先 main 数个 commit。

- [ ] **Step 2: 切回 main 并合并**

Run:
```bash
git checkout main
git merge --no-ff feature/sales-distribution-01-ddl-infra
```
Expected: 自动 merge 成功，生成一个 merge commit。

- [ ] **Step 3: （可选）push 到远端 — 由用户决定**

> ⚠️ 本步骤需用户确认。默认不 push（本仓库未配置 remote 自动推送）。

---

## 自检清单（计划执行人在最终交付前自验）

- [ ] DDL 12 张表（9 新表 + 3 CRM）+ jst_channel 加 5 字段 全部存在
- [ ] 9 字典类型 + 43 字典条目 全部存在
- [ ] 8 个系统参数 全部存在
- [ ] 2 个角色（501/502）+ 9785 段销售工作台菜单（≥19 行）+ 9762 段 admin 菜单（≥14 行）
- [ ] 角色绑定：jst_sales 14 行 / jst_sales_manager 21 行
- [ ] jst-common 新增 7 个文件（util/controller/annotation/aspect ×2/interceptor/config）
- [ ] jst-common 新增 3 个测试类，全部通过
- [ ] jst-channel 新增 12 套 Domain+Mapper+XML
- [ ] JstLoginContext + Enricher + SalesLookupMapper 修改完成
- [ ] mvn clean compile -DskipTests 全模块绿
- [ ] ruoyi-admin spring-boot:run 启动正常
- [ ] CLAUDE.md 已更新进度

---

**End of Plan #1**
