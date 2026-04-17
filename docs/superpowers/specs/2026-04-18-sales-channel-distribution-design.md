# 销售管理 + 渠道分销 设计文档

> **日期**：2026-04-18
> **作者**：Claude（与用户协作 brainstorm）
> **状态**：已 brainstorm 完成，待 review
> **关联模块**：jst-channel（主），jst-common（角色/切面/工具），jst-order（订单事件）
> **后续动作**：用户 review 通过后，调用 writing-plans 生成实施计划

---

## 0. 摘要

新增两个独立但关联的子系统：

- **A. 销售管理子系统**：新增"销售/销售主管"角色，销售可在 Web 管理端预录入渠道、维护跟进、看自己业绩；订单产生时按"销售-渠道归属关系"计提销售提成
- **B. 渠道分销子系统**：渠道方在小程序通过分享邀请新渠道；新渠道注册即与上级建立永久邀请关系；下级渠道每笔订单按比例分润给上级

两子系统共享一个核心概念——**"渠道归属关系"**——但归属对象不同（A 归属销售，B 归属上级渠道）。两条提成链路独立计算（销售提成 + 渠道分销提成 + 既有的渠道返点）但通过**单笔订单总分润上限**做兜底保护。

### 8 大核心决策（Q1~Q8 + CRM）

| 编号 | 主题 | 决策 |
|---|---|---|
| Q1 | 销售角色定位 | 新增 `jst_sales` + `jst_sales_manager` 两个角色，配 SalesScope 数据隔离切面 |
| Q2 | 自动绑定规则 | 手机号优先 + 业务编号回退；业务编号选填+文案引导；预录入赢；admin 可改、仅影响新订单 |
| Q3 | 提成模型 | 实付金额为基数 × 销售个性化费率 × 业务类型差异化系数；全业务类型计提；7 天售后期过后入账 |
| Q4 | 结算时点 | 7 天售后期 + 完整 4 状态机 + 月结 + 销售看明细但金额脱敏 |
| Q5 | 渠道分销 | 1 级（反传销）+ 永久绑定 + 从平台净收入切（有上限）+ 默认 1.5%可单独覆盖 + 注册一次性绑定 + admin 后门可改 + 销售优先互斥 + 销售穿透 1 级 |
| Q6 | 销售离职 | 转给销售主管 + 截止离职日结清 + 财务过渡期可配置 |
| Q7 | 销售层级 | 2 层（主管/销售）+ 主管看下属全明细 + 团队管理提成预留接口 + 接收的渠道主管不拿提成（平台留存） |
| Q8 | 兜底条款 | T1 永久绑定不加自动失效 + 单笔订单总分润上限保护启用（默认 15%） |
| CRM | 销售 CRM | 标准版：跟进记录 + 标签 + 任务 + 渠道画像，微信推送留接口 |

### 派生强化决策

- **金额全脱敏**：销售本人看不到任何金额（包括费率、GMV、提成金额、月结总额），所有钱相关数字归 HR/主管/admin
- **离职 3 阶段拆分**：申请离职（限权但保留登录） → 实际离职日（账号即时停权 + 名下渠道转主管） → 财务过渡期结束（彻底归档），阶段 1 最大上限 7 天可配
- **反带客户加固**：渠道方手机号常态脱敏（所有销售平时看渠道详情都默认脱敏，点"查看完整"写审计日志）+ 所有销售导出加销售姓名/工号/时间戳水印

---

## 1. 数据模型

新增 9 张表 + 老表加 3 个字段，全部归属 `jst-channel` 模块。

### 1.1 销售身份与组织

#### `jst_sales` 销售档案表
| 字段 | 类型 | 说明 |
|---|---|---|
| sales_id (PK) | bigint | 雪花 ID |
| sys_user_id | bigint | 关联 sys_user |
| sales_no | varchar(32) | 销售业务编号（唯一，渠道注册时填的"信物"，如 S202604001） |
| sales_name | varchar(64) | 冗余便于查询 |
| phone | varchar(20) | 销售本人手机号 |
| manager_id | bigint | 直属销售主管 sales_id（H1 二层结构，主管此字段为 null） |
| is_manager | tinyint | 是否主管 |
| status | varchar(32) | `active`(在职) / `resign_apply`(申请离职) / `resigned_pending_settle`(已离职待结算) / `resigned_final`(彻底离职) |
| resign_apply_date | datetime | 提交离职申请时间 |
| expected_resign_date | datetime | 预期离职日（与申请日间隔 ≤ sysParam 上限） |
| actual_resign_date | datetime | 实际离职执行日 |
| transition_end_date | datetime | 财务过渡期结束日（actual_resign_date + N 月） |
| commission_rate_default | decimal(5,4) | 默认费率（B2，0.0~1.0） |
| manager_commission_rate | decimal(5,4) | 团队管理提成率（G3 预留，本期固定 0） |
| created_by/created_at/update_by/update_time | | 标准字段 |

**索引**：`uk_sales_no(sales_no)`, `uk_sys_user(sys_user_id)`, `idx_manager(manager_id)`, `idx_status(status)`

#### `jst_sales_commission_rate` 销售费率明细表（C2 业务类型差异化）
| 字段 | 类型 | 说明 |
|---|---|---|
| id (PK) | bigint | |
| sales_id | bigint | |
| business_type | varchar(32) | 字典：enroll / course / mall / appointment_team / appointment_personal |
| rate | decimal(5,4) | 该销售在该业务类型下的费率（覆盖 commission_rate_default） |
| effective_from | datetime | 生效时间（仅影响该时间之后的订单，F4） |
| created_by/created_at | | |

**索引**：`idx_sales_biz_eff(sales_id, business_type, effective_from)`

> **查询逻辑**：订单提成计算时按"订单付款时刻 ≥ effective_from"取最新一行；无明细行则回退 commission_rate_default。

### 1.2 销售-渠道归属

#### `jst_sales_pre_register` 销售预录入表
| 字段 | 类型 | 说明 |
|---|---|---|
| pre_id (PK) | bigint | |
| sales_id | bigint | 哪个销售预录入的 |
| phone | varchar(20) | 预录入的渠道方手机号（A2 主匹配键） |
| target_name | varchar(128) | 联系人/单位名（仅展示用） |
| status | varchar(32) | `pending`(待匹配) / `matched`(已匹配) / `expired`(已失效) |
| matched_channel_id | bigint | 匹配成功后回填 |
| matched_at | datetime | |
| expire_at | datetime | 90 天过期时间，可续期 |
| renew_count | tinyint | 已续期次数（最多 2 次） |
| created_by/created_at | | |

**索引**：`uk_phone_pending(phone, status)`（仅 status='pending' 的唯一）, `idx_sales(sales_id)`, `idx_expire(expire_at, status)`

> 唯一索引保证 A2 同一手机号同时只能被一个销售预录入。

#### `jst_sales_channel_binding` 销售-渠道绑定关系表（D2 版本化）
| 字段 | 类型 | 说明 |
|---|---|---|
| binding_id (PK) | bigint | |
| channel_id | bigint | |
| sales_id | bigint | 当前归属销售 |
| effective_from | datetime | 本次绑定生效时间 |
| effective_to | datetime | null=当前生效；非 null=历史归属 |
| bind_source | varchar(32) | `pre_register` / `business_no` / `manual` / `transfer_resign` / `manual_transfer` |
| bind_remark | varchar(255) | admin 转移时填写的原因 |
| operator_id | bigint | 谁触发的 |
| created_at | datetime | |

**索引**：`uk_channel_current(channel_id, effective_to_is_null)`（条件唯一索引，保证一渠道同时只有一行 current）, `idx_sales_eff(sales_id, effective_from)`

> 实现条件唯一索引：MySQL 5.7 不直接支持 partial index，用生成列 + 唯一索引模拟：`is_current TINYINT GENERATED ALWAYS AS (IF(effective_to IS NULL, 1, NULL)) VIRTUAL` + `UNIQUE KEY uk_channel_current(channel_id, is_current)`。

### 1.3 渠道分销邀请

#### `jst_channel_invite` 渠道邀请关系表
| 字段 | 类型 | 说明 |
|---|---|---|
| invite_id (PK) | bigint | |
| inviter_channel_id | bigint | 上级渠道 |
| invitee_channel_id | bigint | 下级渠道 |
| invite_code | varchar(32) | 上级渠道生成的邀请码 |
| share_scene | varchar(32) | `mp_share` / `qrcode` / `manual_admin` |
| commission_rate | decimal(5,4) | null=用系统默认 1.5%；非 null=单独配置 |
| invited_at | datetime | 邀请关系建立时间（T1 永久，无 expires_at） |
| status | varchar(32) | `active` / `unbound`（admin 手动解绑） |
| created_at | datetime | |

**索引**：`uk_invitee(invitee_channel_id, status='active')`（条件唯一），`idx_inviter(inviter_channel_id)`, `idx_code(invite_code)`

#### `jst_channel` 老表加字段
| 字段 | 类型 | 说明 |
|---|---|---|
| business_no | varchar(32) | 渠道注册时填的销售业务编号（B3 选填，对应 jst_sales.sales_no） |
| invite_code | varchar(32) | 本渠道作为邀请人时分享出去的邀请码（注册自动生成，唯一） |
| parent_invite_id | bigint | 作为被邀请方时关联的邀请关系 ID（冗余便于查询） |
| parent_invite_attempted | varchar(32) | M1 互斥时记录"原本邀请人"用于追溯（admin 可见） |
| is_official | tinyint | 是否官方/平台自营渠道（不参与分销提成） |

**索引**：`uk_invite_code(invite_code)`

### 1.4 提成账本与结算

#### `jst_sales_commission_ledger` 销售提成台账
| 字段 | 类型 | 说明 |
|---|---|---|
| ledger_id (PK) | bigint | |
| sales_id | bigint | 提成归属销售 |
| order_id | bigint | |
| order_no | varchar(64) | 冗余便查询 |
| channel_id | bigint | |
| binding_type | varchar(32) | `direct`(直属) / `level1`(N2 穿透 1 级) |
| business_type | varchar(32) | enroll / course / mall / ... |
| base_amount | decimal(12,2) | 计算基数（甲：实付金额） |
| applied_rate | decimal(5,4) | 实际适用费率 |
| raw_amount | decimal(12,2) | 压缩前 = base × rate |
| compress_ratio | decimal(5,4) | 单笔上限保护压缩系数（默认 1） |
| amount | decimal(12,2) | 最终入账 = raw × ratio |
| status | varchar(32) | `pending`(待计提) / `accrued`(已计提) / `settled`(已结算) / `paid`(已打款) / `rejected` / `cancelled` |
| accrue_at | datetime | 售后期满入账时间 = order.pay_time + 7 天 |
| accrued_at | datetime | 实际入账时间 |
| settlement_id | bigint | 关联月结单 |
| created_at/update_time | | |

**索引**：`uk_order_sales_type(order_id, sales_id, binding_type)`（防重复）, `idx_status_accrue(status, accrue_at)`, `idx_sales_status(sales_id, status)`, `idx_settlement(settlement_id)`

#### `jst_sales_commission_settlement` 销售月结单
| 字段 | 类型 | 说明 |
|---|---|---|
| settlement_id (PK) | bigint | |
| sales_id | bigint | |
| period | varchar(7) | 结算周期（如 `2026-04`） |
| total_count | int | 包含的提成笔数 |
| total_amount | decimal(12,2) | 月结金额 |
| status | varchar(32) | `pending_review` / `approved` / `paid` / `rejected` |
| approved_by | bigint | admin 审核人 |
| approved_at | datetime | |
| paid_at | datetime | |
| pay_voucher | varchar(255) | 打款流水号 |
| reject_reason | varchar(255) | |
| created_at | datetime | |

**索引**：`uk_sales_period(sales_id, period)`, `idx_status(status)`

#### `jst_channel_distribution_ledger` 渠道分销提成台账
| 字段 | 类型 | 说明 |
|---|---|---|
| ledger_id (PK) | bigint | |
| inviter_channel_id | bigint | 上级渠道（受益方） |
| invitee_channel_id | bigint | 下级渠道 |
| order_id | bigint | |
| order_no | varchar(64) | |
| base_amount | decimal(12,2) | 实付金额 |
| applied_rate | decimal(5,4) | invite 表上的费率，回退默认 1.5% |
| raw_amount | decimal(12,2) | |
| compress_ratio | decimal(5,4) | |
| amount | decimal(12,2) | |
| status | varchar(32) | 同销售台账状态机 |
| accrue_at | datetime | |
| accrued_at | datetime | |
| created_at/update_time | | |

**索引**：`uk_order_inviter(order_id, inviter_channel_id)`, `idx_status_accrue(status, accrue_at)`, `idx_inviter_status(inviter_channel_id, status)`

> **结算路径**：分销提成已计提后，金额并入渠道账户余额，复用现有 `jst_channel_withdraw` 提现流程（C5b 已实现），不进销售月结单。

### 1.5 销售 CRM（标准版）

#### `jst_sales_followup_record` 跟进记录
| 字段 | 类型 | 说明 |
|---|---|---|
| record_id (PK) | bigint | |
| sales_id | bigint | |
| channel_id | bigint | |
| followup_type | varchar(32) | phone / wechat / visit / email / other |
| followup_at | datetime | 跟进时间（默认 now） |
| content | varchar(1000) | 文本内容 |
| attachment_urls | json | OSS URL 数组（最多 5 个） |
| mood | varchar(32) | high / mid / low / none |
| next_followup_at | datetime | 下次跟进时间（可空） |
| can_edit_until | datetime | 创建时间 + 24h |
| created_by/created_at/update_time | | |

**索引**：`idx_channel_time(channel_id, followup_at)`, `idx_sales_time(sales_id, followup_at)`, `idx_next(next_followup_at)`

#### `jst_sales_channel_tag` 渠道标签
| 字段 | 类型 | 说明 |
|---|---|---|
| id (PK) | bigint | |
| channel_id | bigint | |
| tag_code | varchar(64) | 字典 + 自定义（自定义存 `custom:xxx` 前缀） |
| tag_color | varchar(16) | 显示色（可空） |
| created_by | bigint | |
| created_at | datetime | |

**索引**：`uk_channel_tag(channel_id, tag_code)`, `idx_channel(channel_id)`

#### `jst_sales_followup_task` 跟进任务
| 字段 | 类型 | 说明 |
|---|---|---|
| task_id (PK) | bigint | |
| assignee_sales_id | bigint | 任务执行人 |
| assigner_id | bigint | 派任务的人（主管/admin/销售自己） |
| channel_id | bigint | |
| title | varchar(128) | |
| description | varchar(500) | 可空 |
| due_date | date | 截止日期 |
| status | varchar(32) | `pending` / `in_progress` / `completed` / `overdue` |
| completed_at | datetime | |
| linked_record_id | bigint | 完成时关联到 followup_record |
| created_at/update_time | | |

**索引**：`idx_assignee_status(assignee_sales_id, status)`, `idx_due(due_date, status)`

### 1.6 冲突与审计

#### `jst_sales_attribution_conflict` 归属冲突队列
| 字段 | 类型 | 说明 |
|---|---|---|
| conflict_id (PK) | bigint | |
| channel_id | bigint | |
| candidate_sales_ids | json | 候选销售 ID 列表 |
| reason | varchar(255) | |
| status | varchar(32) | `pending` / `resolved` |
| resolved_by | bigint | admin |
| resolved_at | datetime | |
| resolution_remark | varchar(500) | |
| created_at | datetime | |

> C2 决策"预录入赢"下，绝大多数自动绑定不会产生冲突，此表主要用于**异常场景兜底**（如多个销售预录入了同一手机号——理论上唯一索引会拦截，但保留兜底）。

---

## 2. 自动绑定算法

### 2.1 销售自动绑定（A2 + B3 + C2）

**触发时机**：渠道认证申请提交成功 / 渠道方账号自助注册成功（接现有 `PartnerApplyServiceImpl` / `JstChannelAuthApplyController` 链路）

**算法**：

```
输入: channel_id, registered_phone, filled_business_no(可空)

Step 1 (A2 手机号优先):
  query jst_sales_pre_register pr
    JOIN jst_sales s ON pr.sales_id = s.sales_id
   WHERE pr.phone = registered_phone
     AND pr.status = 'pending'
     AND s.status IN ('active', 'resign_apply')  -- 在职或申请离职均可
  if 命中 1 条:
    → bind to that sales (bind_source='pre_register')
    → 该 pre_register 行置 'matched'，回填 matched_channel_id
    → 写 jst_sales_channel_binding (effective_from=now, sales_id=命中销售)
    → 结束
  if 命中 多条 (异常情况，唯一索引正常情况下不会命中):
    → 写 jst_sales_attribution_conflict
    → 不绑定，触发 admin 工单通知
    → 结束

Step 2 (业务编号回退):
  if filled_business_no 不为空:
    query jst_sales s
     WHERE s.sales_no = filled_business_no
       AND s.status IN ('active', 'resign_apply')
    if 命中 1 个销售:
      → bind to that sales (bind_source='business_no')
      → 写 jst_sales_channel_binding
      → 结束
    if 未命中:
      → 写一条 sys_oper_log 记录"曾尝试绑定 business_no=X 失败"

Step 3:
  → 不写 binding 行（无销售归属）
  → admin 后续可在管理端手动绑定 (bind_source='manual')
```

**自绑防御**：所有 Step 入口检查 `registered_phone != 命中销售.phone`，避免销售自己注册渠道时触发自绑。

### 2.2 渠道邀请关系建立（F1 + admin 后门 + M1 互斥）

**触发时机**：新渠道方注册时填写 invite_code（小程序分享链接带 `?invite_code=xxx` 自动回填）

**算法**：

```
输入: new_channel_id, invite_code(可空)

if invite_code 为空: → 不建立邀请关系，结束

query jst_channel WHERE invite_code = ? AND status='active'
if 未命中: → 静默忽略（不报错避免渠道方困惑）

校验：
  - 防自邀: inviter_channel_id != new_channel_id
  - 反环路: new_channel_id 不在 inviter 的祖先链（实际不可能，新渠道刚注册）
  - inviter 不是 official: inviter.is_official = 0

M1 互斥（关键）:
  if jst_sales_channel_binding 当前已存在 channel_id=new_channel_id 的绑定:
    → 不写 jst_channel_invite
    → UPDATE jst_channel SET parent_invite_attempted = invite_code (供 admin 追溯)
    → 结束
  else:
    INSERT jst_channel_invite (
      inviter_channel_id, invitee_channel_id=new_channel_id, 
      invite_code, share_scene='mp_share', commission_rate=null
    )
    UPDATE jst_channel SET parent_invite_id = new_invite_id
```

**F1 admin 后门**：admin 在 Web 管理端"渠道详情"页有隐藏菜单"调整邀请关系"（仅 admin 可见，运营/客服都看不到），可解绑/重指定上级/修改 commission_rate/强制绕过 M1 互斥。每次操作写 sys_oper_log。调整不影响历史已计提分销提成，仅影响调整时刻之后产生的订单。

### 2.3 销售归属转移（D2 + O2）

**路径 A：admin 主动调整**
```
当前 binding effective_to = now
新 binding (sales_id=新, effective_from=now, bind_source='manual_transfer', bind_remark=理由)
```

**路径 B：销售离职**（详见 §4 离职 3 阶段）

### 2.4 边界与异常清单（56 条）

#### A. 预录入阶段
| # | 场景 | 行为 |
|---|---|---|
| A1 | 空手机号/非法格式 | 表单校验拦截 |
| A2 | 销售 A 已录入手机号 X，B 也想录入 | 拒绝 B（唯一索引） |
| A3 | A2 场景下 B 急需 | B 在 Web 工单页发起"预录入归属调整"申请 |
| A4 | 预录入了已是渠道但无销售归属的手机号 | 立即"补录绑定"（不进 pending） |
| A5 | 预录入了已是渠道且已有归属的手机号 | 拒绝，提示"已属销售 X" |
| A6 | 90 天未匹配 | Quartz 自动失效 |
| A7 | 销售对将到期的预录入续期 | +90 天，最多 2 次 |
| A8 | 销售离职 | 名下所有 pending 预录入自动失效 |
| A9 | 销售自己删除 pending 预录入 | 允许 |
| A10 | 预录入了销售本人手机号 | 拒绝（自绑防御） |

#### B. 渠道注册自动绑定
| # | 场景 | 行为 |
|---|---|---|
| B1 | 手机号命中预录入（销售在职） | bind_source='pre_register' |
| B2 | 手机号命中预录入但销售已离职（A8 已失效，理论不会发生） | 跳过，继续 Step 2 |
| B3 | 销售在过渡期 | 仍按预录入绑（status 仍是 active 或 resign_apply） |
| B4 | 业务编号命中销售 | bind_source='business_no' |
| B5 | 业务编号填的是已离职销售 | 跳过 Step 2，进 Step 3 |
| B6 | 业务编号填错 | 跳过 + 写审计日志 |
| B7 | 都未命中 | 进 Step 3 无归属 |
| B8 | 注册手机号 = 销售本人手机号 | 自绑防御不绑 |
| B9 | 同一渠道多次走绑定流程 | 仅当前无绑定时走算法 |
| B10 | 同一手机号 2 个渠道并发注册 | 数据库唯一索引保护 |
| B11 | invite_code + business_no 都填 | M1 销售优先：销售绑成功则跳过邀请 |
| B12 | M1 互斥未建邀请的追溯 | parent_invite_attempted 字段记录 |

#### C. 销售归属转移
| # | 场景 | 行为 |
|---|---|---|
| C1 | admin 调整销售 A → B | 当前 binding effective_to=now，新 binding |
| C2 | 目标销售已离职 | 拒绝 |
| C3 | 调整后又调回 A | 允许，3 行历史 |
| C4 | 离职转给主管 | bind_source='transfer_resign'，按 E3 主管不计提 |
| C5 | 离职销售是顶级主管 | 名下渠道+下属进"无主队列"，admin 人工分配 |
| C6 | 主管离职 | 下属销售 manager_id=NULL，admin 后续重分配 |
| C7 | 过渡期内 admin 又把渠道转给在职销售 C | 允许，C 按自己费率正常计提 |
| C8 | 边界订单 | 按 pay_time vs effective_to 严格比较 |

#### D. 渠道分销邀请
| # | 场景 | 行为 |
|---|---|---|
| D1 | 无 invite_code | 不建关系 |
| D2 | invite_code 错（不存在） | 静默忽略 |
| D3 | invite_code 是已封禁渠道 | 静默忽略 |
| D4 | 自邀 | 算法防御 |
| D5 | 上级渠道被封禁 | 关系保留，提成计算时跳过 |
| D6 | 下级被封禁 | 上级不再从下级新订单分销 |
| D7 | 上级法人变更 | 关系不变 |
| D8 | invite_code 被千万人用 | 允许，每人一行 invite |
| D9 | admin 后门改邀请关系 | 类似版本化，旧 unbound 新 active |
| D10 | admin 强制绕过 M1 互斥建邀请 | F1 后门允许 |

#### E. 提成计算并发与一致性
| # | 场景 | 行为 |
|---|---|---|
| E1 | 重复触发提成计算 | 唯一索引拦截 |
| E2 | binding 关系并发修改 | 按 pay_time 锁定 binding |
| E3 | 单笔上限压缩 | 4 类提成项必须同事务批量计算 |
| E4 | raw_amount ≠ amount | 月结/看板用 amount，raw 仅审计 |
| E5 | 售后期内退款 | UPDATE pending → cancelled |
| E6 | 计提定时任务崩溃 | 幂等 UPDATE，下次自动续 |
| E7 | 单订单 4 类提成同时算 | 单事务批量 |

#### F. 销售/渠道身份变化
| # | 场景 | 行为 |
|---|---|---|
| F1 | 销售改名/改手机号 | sales_no 不变 |
| F2 | sales_no 离职后能否复用 | 不能，唯一索引覆盖全状态 |
| F3 | 同一 sys_user 同时是销售 + 渠道方 | 业务禁止，创建时校验 |
| F4 | 销售费率被修改 | 仅影响修改时刻之后的订单（commission_rate 表 effective_from） |
| F5 | 主管降级 | is_manager=0，下属 manager_id=NULL |
| F6 | 销售升级为主管 | is_manager=1 |

#### G. 数据完整性
| # | 场景 | 行为 |
|---|---|---|
| G1 | 渠道物理删除 | 业务禁止，仅停用 |
| G2 | 销售物理删除 | 业务禁止，仅离职 |
| G3 | 订单删除 | 业务禁止 |

---

## 2.5 销售 CRM（标准版）

### 功能边界

销售在 Web 工作台对名下渠道进行**主动维护**——记录沟通过程、打标签、设置回访提醒、看渠道画像。

### 数据模型

见 §1.5（3 张表）。

### 5 个页面

1. **我的渠道列表**（已在主架构里）→ 每行新增"跟进 / 打标签"按钮 + 列显示"最近联系：3 天前" + "标签 chip"
2. **渠道详情 → 跟进时间线**：垂直时间线展示该渠道所有跟进记录（按时间倒序），顶部"新建跟进"按钮
3. **今日待跟进**（销售工作台首页卡片）：基于 `next_followup_at <= today + followup_task.due_date <= today` 聚合
4. **跟进任务列表**：销售看自己被派的任务；主管看自己派出去的任务进度
5. **渠道画像页**：标签 / 最近联系 / 跟进次数 / 累计成交笔数 / 活跃度趋势小折线图

### admin 业绩看板新增维度

- **跟进活跃度**：每销售本月跟进笔数 / 覆盖渠道数
- **覆盖率**：销售名下渠道中 30 天内有跟进的占比
- **任务完成率**：主管派的任务按时完成比例
- **意向漏斗**：跟进记录的 mood 分布（high/mid/low/none 渠道数）

### 关键边界

| # | 场景 | 行为 |
|---|---|---|
| H1 | 跟进记录可改/可删？ | 24h 内可编辑/删除，之后只读 |
| H2 | 销售离职后跟进记录 | 全部保留，新接手销售可看历史，记录显示原销售名 |
| H3 | 渠道方能否看到跟进记录 | 永远不可见（内部 CRM 数据） |
| H4 | 渠道转移后旧跟进记录归属 | 归渠道（不归销售），新销售自动继承 |
| H5 | 销售之间能否互看跟进 | 不能（仅本人 + 直属主管 + admin） |
| H6 | 自定义标签是否进字典 | 不进，以 `custom:` 前缀存；admin 可"提升为字典" |
| H7 | 跟进附件 OSS | 复用现有 OSS；销售离职后归档不删 |

### 微信推送（留接口后期做）

定义 `SalesNotificationService` 接口，本期实现"站内消息"版本，预留"WX 模板消息"实现。

---

## 3. 提成计算管线

### 3.1 总览：4 阶段 1 兜底

```
订单付款即时记账 (status=pending)
    ↓
售后期等待 7 天
    ↓
入账计提 (status=accrued)（Quartz 每日凌晨）
    ↓
月结打款 (status=settled → paid)（每月 1 日）

兜底分支：售后期内退款 → status=cancelled
```

### 3.2 阶段 1：订单付款时即时记账

监听 `OrderPaidEvent`，**同一事务**内执行 4 类提成项的计算 + 上限压缩 + 批量入库。详见原 brainstorm §3.2 算法伪代码。

**关键约束**：
- 4 类提成项（销售直属、销售穿透、分销提成、渠道返点）必须同事务
- N2 防双计提：上级渠道的销售 = 直属销售时跳过穿透
- 官方渠道豁免：is_official=1 不参与分销
- E3 离职接收：bind_source='transfer_resign' 的销售不计提，平台留存

### 3.3 阶段 2：售后期等待

- ledger 行 status='pending'，accrue_at 字段记录"何时可入账"
- 监听 `OrderRefundedEvent`：UPDATE pending ledger SET status='cancelled'
- 因 C4 决策"本期仅全额退"，不存在部分倒扣

### 3.4 阶段 3：入账计提

Quartz `JstSalesCommissionAccrueJob`，每天 02:00：
```sql
UPDATE jst_sales_commission_ledger
   SET status='accrued', accrued_at=NOW()
 WHERE status='pending' AND accrue_at <= NOW()
```
幂等 UPDATE 保证重跑安全。

同样的逻辑跑 `jst_channel_distribution_ledger`。

### 3.5 阶段 4：月结与打款

**Quartz `JstSalesMonthlySettlementJob`**，每月 1 日 03:00：

```sql
-- 对每个 status IN ('active','resign_apply','resigned_pending_settle') 的销售
INSERT INTO jst_sales_commission_settlement
SELECT sales_id, '上月 YYYY-MM', SUM(amount), 'pending_review'
  FROM jst_sales_commission_ledger
 WHERE sales_id=? AND status='accrued' AND accrued_at IN (上月 1 号, 上月末)
 GROUP BY sales_id;

UPDATE ledger SET status='settled', settlement_id=新结算 WHERE 上述条件;
```

**Web admin 流程**：审核 → 通过 → 录入打款流水 → status='paid'。驳回则退回 'accrued' 下月重汇总。

**渠道分销提成的结算**：不进销售月结，金额并入渠道账户余额，复用现有 `jst_channel_withdraw` 提现流程。

### 3.6 销售端可见性（脱敏方案）

| 角色 | 业绩数据（笔数/渠道数） | GMV / 费率 / 金额 / 月结总额 |
|---|---|---|
| 销售本人 | ✅ 完整可见 | ❌ 全脱敏（显示"由 HR 发放"） |
| 直属主管 | ✅ 完整 | ✅ 完整 |
| admin / 财务 | ✅ 完整 | ✅ 完整 |

**实施**：API 层身份判断 + `@Sensitive(money=true)` 字段注解 + AOP 切面统一脱敏；前端 `<MaskedAmount />` 占位组件。

### 3.7 异常与监控

| 场景 | 处理 |
|---|---|
| 阶段 1 写入失败 | 整事务回滚，订单付款仍成功，定时任务每小时扫描"已付款无 ledger"补偿 |
| 阶段 3 漏跑 | accrue_at 是绝对时间，下次启动续上 |
| 月结跨月延迟 | 不影响数据一致性 |
| 销售月结金额为 0 | 不生成结算单 |
| 单条 ledger 金额特别大 | 触发 admin 站内消息"高额提成单待审核"（阈值默认 5000 元） |
| 上限压缩比过高（< 0.3） | 监控告警，提示运营 review 费率 |

---

## 4. 生命周期与转移

### 4.1 渠道-销售归属生命周期

```
未注册 → 自动绑定决策 → 已绑定
                              ├─→ admin 主动换销售
                              └─→ 销售离职（详见 4.5）
```

### 4.2 销售提成生命周期（单条 ledger）

```
pending → accrued → settled → paid
   ↓
cancelled（售后期内退款）

settled 也可 → rejected（admin 驳回）→ 退回 accrued
```

### 4.3 渠道分销邀请生命周期

```
邀请码生成（注册即生成）
    ↓
新渠道注册时填 invite_code → 邀请关系建立（M1 互斥检查）
    ↓
下级订单付款 → 分销提成入账（同 §3 管线）
    ↓
渠道方提现（复用 jst_channel_withdraw）
```

### 4.4 Quartz 任务清单（8 个）

| 任务 | 频率 | 职责 |
|---|---|---|
| `JstSalesPreRegisterExpireJob` | 每天 01:00 | 90 天未匹配的预录入置失效 |
| `JstSalesCommissionAccrueJob` | 每天 02:00 | 售后期满的销售提成 ledger → accrued |
| `JstChannelDistributionAccrueJob` | 每天 02:30 | 同上，分销提成台账 |
| `JstSalesMonthlySettlementJob` | 每月 1 日 03:00 | 月结销售提成 |
| `JstSalesResignExecuteJob` | 每天 04:00 | 到期的离职申请自动执行（账号停权） |
| `JstSalesTransitionEndJob` | 每天 04:30 | 财务过渡期到期 → status='resigned_final' |
| `JstSalesFollowupReminderJob` | 每天 08:00 | 跟进提醒站内消息推送 |
| `JstOrderUnpaidCommissionRepairJob` | 每小时 | 补偿"已付款无 ledger"的订单 |

### 4.5 销售离职 3 阶段（防带客户）

```
Day 0 (提交申请)              Day N (实际离职日)            Day N+M月 (财务过渡期结束)
  status=resign_apply  →  status=resigned_pending_settle  →  status=resigned_final
  账号可登录但限权          账号即时停权 + 渠道转主管             jst_sales 归档保留
```

#### 阶段 1：申请离职到实际离职日（≤ sysParam `jst.sales.resign_apply_max_days`，默认 7 天）

**限权清单**：

| 功能 | active | resign_apply | resigned_pending_settle |
|---|---|---|---|
| 登录系统 | ✅ | ✅ | ❌ |
| 看自己业绩 | ✅ | ✅ | ❌ |
| 看渠道列表 | ✅ | ✅（手机号脱敏，AC1 常态） | ❌ |
| 看渠道详情 | ✅ | ✅（敏感字段脱敏 + 审计） | ❌ |
| 看跟进记录 | ✅ | ✅（只读） | ❌ |
| 写跟进记录 | ✅ | ✅ | ❌ |
| 新建预录入 | ✅ | ❌ | ❌ |
| 续期预录入 | ✅ | ❌ | ❌ |
| 导出渠道列表 | ✅ | ❌ | ❌ |
| 导出跟进记录 | ✅ | ❌ | ❌ |
| 修改跟进任务负责人 | ✅ | ❌ | ❌ |
| 接受被分配的新渠道 | ✅ | ❌ | ❌ |

#### 阶段 2：实际离职日（账号即时停权）

`expected_resign_date` 到达时（或 admin 手动"立即停权"），Quartz `JstSalesResignExecuteJob` 自动执行：
- sys_user 禁用
- jst_sales.status = 'resigned_pending_settle'，actual_resign_date=now，transition_end_date = now + N 月
- 名下所有当前 binding 转给 manager_id（bind_source='transfer_resign'）
- 顶级主管离职：渠道+下属进"无主队列"，admin 人工分配
- pre_register 全部失效

之后销售本人无法访问系统。离职日之前付款的订单提成正常按 W1 走月结打款；离职日之后产生的订单按 E3 主管接收但不计提，平台留存。

#### 阶段 3：财务过渡期结束

Quartz `JstSalesTransitionEndJob` 扫描 `transition_end_date <= now` 的销售，status='resigned_final'，jst_sales 行保留不删（用于历史业绩可追溯），sys_user 归档不删除。

### 4.6 反带客户加固（已选 AC1 + AC3）

#### AC1 手机号常态脱敏

不仅离职阶段，**所有销售平时看渠道详情都默认脱敏**为 `139****1234`。点"查看完整"按钮 → 调用 `GET /sales/channel/{id}/phone-full`，返回完整号码 + 写一条 `sys_oper_log`（business_type='SALES_VIEW_PHONE'）。admin 可在审计日志页检索这类记录。

#### AC3 导出水印

`SalesExportWatermarkInterceptor` 拦截所有销售可触发的导出接口：
- Excel：第一行插入水印行
- PDF：每页右下角灰色斜体水印
- CSV：首行 `# Exported by [name]([no]) at [time]`
- 同步写 `sys_oper_log`（business_type='SALES_EXPORT'）

### 4.7 Spring 事件清单

| 事件 | 发布者 | 监听者 | 职责 |
|---|---|---|---|
| `OrderPaidEvent` | OrderService.pay | `SalesCommissionListener` | 即时记账 §3.2 |
| | | `ChannelDistributionListener` | 分销记账 |
| `OrderRefundedEvent` | OrderService.refund | `SalesCommissionRefundListener` | 倒扣 pending ledger |
| `ChannelRegisteredEvent` | ChannelService.register | `SalesAutoBindingListener` | 自动绑定 §2.1 |
| | | `ChannelInviteBindingListener` | 邀请关系建立 §2.2 |
| `SalesResignedEvent` | SalesService.resignExecute | `SalesChannelTransferListener` | 转移名下渠道 |
| `FollowupRecordCreatedEvent` | CRM | `SalesActivityMetricListener` | 更新活跃度看板 |

复用现有 F-MSG-TRIGGER 总线。

---

## 5. 权限切面 + UI 复用 + API 大纲

### 5.1 角色与菜单

新增 2 个角色：

| role_key | 名称 | 说明 |
|---|---|---|
| `jst_sales` | 销售 | 个人销售，SalesScope 数据隔离 + 金额脱敏 |
| `jst_sales_manager` | 销售主管 | 看全部下属销售 + 完整金额，可派任务、调整下属归属 |

**菜单段位**：9785（销售工作台）+ 9762 用户渠道下追加 admin 端 3 项

```
平台数据管理 (9800)
└── 用户渠道 (9762)
    ├── 渠道管理 [已有]
    ├── 销售管理 [新]
    ├── 销售业绩看板 [新]
    └── 归属冲突队列 [新]

销售工作台 (9785) [仅 jst_sales/jst_sales_manager 可见]
├── 我的渠道
├── 我的业绩
├── 我的预录入
├── 跟进任务
├── 团队管理 [仅 jst_sales_manager]
└── 业绩明细 [仅 jst_sales_manager / admin]
```

### 5.2 SalesScope 数据隔离切面

复刻 PartnerScope 模式：

**`SalesScopeUtils`**（jst-common）
- `currentSalesId()` 当前销售 ID，非销售返 null
- `currentSalesIdRequired()` 强制要求是销售
- `currentManagerVisibleSalesIds()` 主管可见的下属 ID 列表（含自己）
- `isPlatformOpOrManager()` 是否平台运营/admin/主管（看全量）

**`BaseSalesController`** 基类
- 类级 `@PreAuthorize("hasAnyRoles('jst_sales,jst_sales_manager,admin,jst_operator')")`
- 方法级根据具体接口加细化权限
- 提供 `currentScopeSalesIds()` 给 Mapper 注入

**Mapper XML 写法**：统一 `<sql id="salesScope">` 片段。

### 5.3 金额脱敏切面

`MaskSalaryAspect` AOP：
- 拦截 `SalesController+` 子类返回值
- 当前用户仅有 `jst_sales` 角色 → 反射置空 `@Sensitive(money=true)` 字段
- 同时拦截 `@Sensitive(phone=true)` 做手机号脱敏

`@Sensitive` 字段注解用于 ledger / settlement / channel rate VO。

### 5.4 导出水印拦截器

`SalesExportWatermarkInterceptor`：
- 拦截路径：`/sales/**/export`、`/sales/**/download`、其他销售可触发的导出接口
- 在 Excel/PDF/CSV 中插入水印 + 写审计日志

### 5.5 API 大纲

#### 销售工作台 API（前缀 `/sales/`）

| Method | Path | 说明 |
|---|---|---|
| GET | `/sales/me/dashboard` | 工作台首页聚合 |
| GET | `/sales/me/channels` | 我的渠道列表（脱敏 phone） |
| GET | `/sales/me/channels/{id}` | 渠道详情（脱敏） |
| GET | `/sales/me/channels/{id}/phone-full` | 查看完整手机号（写审计） |
| GET | `/sales/me/performance` | 我的业绩（笔数/状态，金额脱敏） |
| GET | `/sales/me/pre-register/list` | 预录入列表 |
| POST | `/sales/me/pre-register` | 新建预录入 |
| POST | `/sales/me/pre-register/{id}/renew` | 续期 |
| DELETE | `/sales/me/pre-register/{id}` | 删除（仅 pending） |
| GET | `/sales/me/followup/list?channel_id=` | 跟进记录列表 |
| POST | `/sales/me/followup` | 新建跟进 |
| PUT | `/sales/me/followup/{id}` | 编辑（24h 内） |
| GET | `/sales/me/tasks?status=` | 跟进任务列表 |
| POST | `/sales/me/tasks/{id}/complete` | 完成任务 |
| GET | `/sales/me/tags?channel_id=` | 渠道标签 |
| POST | `/sales/me/tags` | 打标签 |
| DELETE | `/sales/me/tags/{id}` | 删标签 |

#### 主管 API（前缀 `/sales/manager/`）

| Method | Path | 说明 |
|---|---|---|
| GET | `/sales/manager/team` | 下属销售列表 + 业绩聚合（含金额） |
| GET | `/sales/manager/team/{salesId}/detail` | 单下属业绩明细 |
| POST | `/sales/manager/tasks` | 派任务给下属 |
| POST | `/sales/manager/transfer` | 调整下属名下渠道归属 |
| GET | `/sales/manager/settlement/list` | 下属月结单列表（含金额） |

#### admin API（前缀 `/admin/sales/`）

| Method | Path | 说明 |
|---|---|---|
| GET | `/admin/sales/list` | 全员销售档案 |
| POST | `/admin/sales` | 新建销售（关联 sys_user） |
| PUT | `/admin/sales/{id}/rate` | 修改个性化费率 |
| PUT | `/admin/sales/{id}/manager` | 设置/修改直属主管 |
| POST | `/admin/sales/{id}/resign-apply` | 提交离职申请 |
| POST | `/admin/sales/{id}/resign-execute` | 立即执行离职 |
| POST | `/admin/sales/{id}/transition-end` | 手动终结过渡期 |
| GET | `/admin/sales/dashboard` | 全员业绩看板 |
| GET | `/admin/sales/binding/conflict` | 归属冲突队列 |
| POST | `/admin/sales/binding/conflict/{id}/resolve` | 裁决冲突 |
| POST | `/admin/sales/binding/manual` | 手动绑定渠道-销售 |
| GET | `/admin/sales/settlement/pending` | 待审核月结单 |
| POST | `/admin/sales/settlement/{id}/approve` | 审核通过 |
| POST | `/admin/sales/settlement/{id}/reject` | 驳回 |
| POST | `/admin/sales/settlement/{id}/pay-record` | 录入打款流水 |
| GET | `/admin/sales/audit/phone-views` | 销售查看手机号审计 |
| GET | `/admin/sales/audit/exports` | 销售导出操作审计 |

#### 渠道分销 API（小程序 `/jst/wx/channel/`）

| Method | Path | 说明 |
|---|---|---|
| GET | `/jst/wx/channel/invite/code` | 我的邀请码 + 二维码 |
| GET | `/jst/wx/channel/invite/list` | 我邀请的渠道列表 |
| GET | `/jst/wx/channel/distribution/ledger` | 分销提成明细 |
| GET | `/jst/wx/channel/distribution/summary` | 按月汇总 |
| POST | `/jst/wx/channel/register`（已有，body 加 inviteCode 字段） | |

### 5.6 前端 UI 复用策略

| 页面 | 复用策略 |
|---|---|
| 销售工作台首页 / 我的业绩 / 我的预录入 / 我的跟进 / 我的任务 / 我的渠道列表 | 新建（`views/sales/`） |
| 渠道详情页 | 复用 `views/channel/detail.vue`，加销售身份判断 + 脱敏渲染 |
| 跟进记录组件 `<FollowupTimeline />` | 新建组件，销售/admin 共用 |
| admin 销售管理 / 业绩看板 / 冲突队列 / 月结单审核 | 新建（`views/jst/sales/`） |
| 跟进时间线、标签 chip、任务卡片 | 新建组件，全平台复用 |

**销售主管的"团队管理"视图**：复用 `views/sales/me/*` + 顶部"切换查看"下拉。

**admin/运营的"全员视图"**：复用同一套 + 顶部"销售筛选"下拉。

### 5.7 系统参数清单

| Key | 默认值 | 说明 |
|---|---|---|
| `jst.sales.aftersales_days` | 7 | 售后期天数 |
| `jst.sales.transition_months` | 3 | 财务过渡期月数 |
| `jst.sales.resign_apply_max_days` | 7 | 离职申请到实际离职最大间隔 |
| `jst.sales.pre_register_valid_days` | 90 | 预录入有效期 |
| `jst.sales.pre_register_renew_max` | 2 | 预录入最多续期次数 |
| `jst.sales.distribution.default_rate` | 0.015 | 渠道分销默认费率 |
| `jst.sales.order.max_total_share_rate` | 0.15 | 单笔订单总分润上限 |
| `jst.sales.high_amount_alert_threshold` | 5000 | 高额提成告警阈值 |

---

## 6. 实施次序建议（供 writing-plans 参考）

由于范围大，建议拆为 4 个独立可上线的子任务：

1. **DDL + 基础架构**：9 张新表 + 老表加字段 + 角色 + 菜单 + SalesScope 切面 + 金额脱敏 AOP + 字典数据
2. **销售管理核心**：自动绑定算法（§2）+ 销售档案 CRUD + 预录入 + 提成计算管线（§3）+ Quartz 任务
3. **CRM 子模块**：跟进记录 + 标签 + 任务 + 渠道画像 + 销售工作台前端
4. **渠道分销 + admin 后台**：邀请码生成 + 注册回填 + 分销提成台账 + 离职 3 阶段流程 + admin 月结审核 + 业绩看板 + 反带客户加固（AC1+AC3）

---

## 7. 待确认 / 后期扩展

| 项 | 状态 | 说明 |
|---|---|---|
| 微信模板消息推送（CRM 提醒） | 留接口后期做 | 需要小程序 OA 模板审批 |
| 团队管理提成（G2/G3） | 留接口（manager_commission_rate 字段已留） | 团队规模 30+ 时再启用 |
| AC2 异常操作监控 | 不做 | 后期视情况 |
| AC4 离职面谈流程 | 不做 | admin 自己内部流程 |
| AC6 附件水印 | 不做 | 工程化成本高 |
| 微信企业转账 API（销售月结打款） | 留接口 | 本期 admin 线下打款 + 录流水 |
| 销售提成阶梯费率（B3/B4） | 不做 | 个性化费率已能满足 |
| 跨业务类型团队 KPI 看板 | 不做 | 后期 admin 看板扩展 |

---

**End of Document**
