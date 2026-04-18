# 后端补齐 TODO — UX 精品化遗留字段

> **来源**：2026-04-18 前端 UX 精品化 5 个 Agent 交付（Wave 1 架构基石 + Wave 2 P0 业务修复）
> **前置分支**：`feat/ux-polish-2026-04-18`（已 merge）
> **实施分支**：`feat/backend-ux-polish-todo`（2026-04-18 实施完成，3 commits）
> **预估工作量**：1.5 ~ 2 人日（6 条 DDL/Mapper + 4 条 VO 扩展 + 1 条新端点 + 2 条 OSS 依赖）
> **派发方式**：用 `subagent/BACKEND_AGENT_PROMPT.md` 模板 + 本文档作为任务卡

## 实施进度（2026-04-18）

| 批次 | 范围 | 状态 | commit |
|---|---|---|---|
| 批 1 | §1.1 §1.2 §2.4 Mapper LEFT JOIN + monthAmount | ✅ 完成 | `5341d51` |
| 批 2 | §2.1 §2.2 §2.3 VO 字段扩展 + DDL migration | ✅ 完成 | `afd51d9` |
| 批 3 | §3.1 §4.1 §4.2 DTO + 新端点 + status=invalid | ✅ 完成 | `ba9aa73` |
| 跳过 | §5.1 §5.2 OSS 依赖 | ⏸️ 等 OSS 配置 | — |
| 前端清理 | TODO(backend) 注释清理 + 新端点接入 | ✅ 完成 | 下批 |


---

## 一、Mapper XML LEFT JOIN 补齐（P0，~2 小时）✅ 完成（commit 5341d51）

### 1.1 ✅ 邀请下级渠道名 `invitee_channel_name` / `auth_type`

**文件**：`RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstChannelInviteMapperExt.xml`（路径视实际）

**当前**：`listMyInvited` 查询仅返回 `jst_channel_invite` 表字段，`invitee_channel_id` 为数字 ID
**需要**：
```xml
<select ...>
  SELECT inv.*, invc.name AS invitee_channel_name, invc.auth_type, invc.avatar_url
  FROM jst_channel_invite inv
  LEFT JOIN jst_channel invc ON invc.channel_id = inv.invitee_channel_id
  WHERE inv.inviter_channel_id = #{channelId}
  ORDER BY inv.create_time DESC
</select>
```

**前端位置**：`jst-uniapp/pages-sub/channel/invite-list.vue`
**前端当前降级**：显示 "渠道 #{inviteeChannelId}"（头像用首字母生成）

---

### 1.2 ✅ 分销台账下级渠道名 `invitee_channel_name`

**文件**：`RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstChannelDistributionLedgerMapperExt.xml`

同 1.1 规则，对 `invitee_channel_id` 做 LEFT JOIN，附加 `invitee_channel_name`。

**前端位置**：`jst-uniapp/pages-sub/channel/distribution.vue`
**前端当前降级**：显示 "下级 · 渠道 #{inviteeChannelId}"

---

## 二、VO 字段扩展（P0，~4 小时）✅ 完成（commit afd51d9）

### 2.1 ✅ `SettlementListVO`（提现结算单列表）= WithdrawListVO

**新增字段**：
| 字段 | 类型 | 说明 | 数据来源 |
|---|---|---|---|
| `ledgerCount` | Long | 关联返点台账条数 | `SELECT COUNT(*) FROM jst_channel_rebate_ledger WHERE settlement_id = #{id}` |
| `contractNo` | String | 关联合同编号 | LEFT JOIN `jst_contract` ON settlement_id |
| `invoiceStatus` | String | 发票状态 | LEFT JOIN `jst_invoice` 取最新 status |

**前端位置**：`settlement.vue`、`withdraw-list.vue` 卡片 3 chip
**前端当前降级**：chip 用 `v-if` 保护，后端无数据则不显示

### 2.2 ✅ `ContractDetailVO` = ContractRecordVO（+ 甲乙方 + 结算单号）

**新增字段**：`partyA` / `partyAName` / `partyB` / `partyBName` / `settlementId` / `settlementNo`

**前端位置**：`contract-detail.vue` 甲乙方信息区 + 关联结算单跳转行

### 2.3 ✅ `InvoiceListVO` = InvoiceRecordVO（+ tracking + 邮箱脱敏 + settlementId）

**新增字段**：`settlementId` / `settlementNo` / `trackingCompany` / `trackingNo` / `deliveryEmail`

**前端位置**：`invoice-list.vue` 关联结算单 chip + 物流单号 chip + 邮箱脱敏 chip

### 2.4 ✅ `ChannelDistributionSummaryVO` (monthAmount)

**新增字段**：`monthAmount: BigDecimal` — 本月分销收益
**SQL**：`WHERE accrue_time >= DATE_FORMAT(NOW(), '%Y-%m-01 00:00:00')` 聚合

**前端位置**：`distribution.vue` Hero KPI 第 1 格
**前端当前降级**：拉首页 ledger 100 条聚合本月

---

## 三、DTO 请求字段扩展（P1，~1 小时）

### 3.1 `InvoiceApplyReqDTO` 多选结算单

**新增字段**：`settlementIds: List<Long>`

**业务逻辑**：
- `settlementIds` 非空：取列表金额之和作为 `totalAmount`
- 兼容保留 `settlementId`（单选），两个都传时以 `settlementIds` 为准
- Service 层校验：所有结算单必须属于当前渠道方 + 状态均为 `paid` + 未曾被开票

**前端位置**：`invoice-apply.vue`（已兼容两字段同时传）

---

## 四、新 API 端点（P0，~3 小时）

### 4.1 `GET /jst/wx/rights/writeoff-records` — 用户权益核销记录列表

**定位**：解决前端 N+1 请求聚合问题

**Controller 方法**：`WxRightsController.listMyWriteoffRecords(params)`

**Query 参数**：
| 参数 | 类型 | 说明 |
|---|---|---|
| `rightsId` | Long | 可选 |
| `status` | String | pending / approved / rejected / rolled_back / used / expired / **invalid**（= used+expired） |
| `pageNum` / `pageSize` | Int | 分页 |

**返回字段（每条）**：
```java
{
  recordId: Long,
  rightsId: Long,
  rightsName: String,
  rightsType: String,       // venue_reduce / course_free / support_priority
  rightsIconEmoji: String,  // 前端方便展示
  applyTime: LocalDateTime,
  status: String,           // 7 种之一
  amount: BigDecimal,       // 本次核销金额/次数
  remark: String,           // 用户申请说明
  auditTime: LocalDateTime, // 可空
  auditRemark: String       // 驳回原因/通过备注
}
```

**SQL 参考**：
```sql
SELECT wr.*, r.name AS rights_name, r.type AS rights_type
FROM jst_rights_writeoff_record wr
LEFT JOIN jst_rights_record r ON r.record_id = wr.rights_record_id
WHERE wr.user_id = #{currentUserId}
  AND (#{rightsId} IS NULL OR wr.rights_record_id = #{rightsId})
  AND (#{status} IS NULL OR wr.status = #{status}
       OR (#{status} = 'invalid' AND wr.status IN ('used', 'expired')))
ORDER BY wr.apply_time DESC
```

**前端位置**：`pages-sub/appointment/writeoff-record.vue` (user 模式)
**前端降级**：已在 `api/rights.js` 的 `getMyRightsWriteoffRecords()` 中做 N+1 聚合降级

### 4.2 `/jst/wx/rights/my` 支持 `status=invalid` 参数

**新增语义**：`WHERE status IN ('used', 'expired')`

**前端位置**：`pages-sub/rights/center.vue` "已失效" Tab

---

## 五、OSS 依赖项（P2，等 OSS 接入后做）

### 5.1 邀请码小程序码 `qrcodeUrl`

**接口**：`GET /jst/wx/channel/invite/code` 响应体加 `qrcodeUrl` 字段

**业务**：
1. 调用微信 `wx.acode.getUnlimited`（参数 `scene=invite={inviteCode}`, `page=pages/login/login`）
2. 生成的二进制图片上传 OSS（路径 `channel-invite-qr/{channelId}.png`）
3. 存入字段 + Redis 缓存（Key `channel:invite:qr:{channelId}`，TTL 7 天）

**前端位置**：`invite-code.vue` 二维码区
**前端当前**：`u-icon` 占位 + "二维码生成中" 文案

### 5.2 提现打款凭证 `payoutProofUrl`

**表**：`jst_withdraw_record` 应有 `pay_voucher_url` 字段

**业务**：admin 标记已打款时支持上传凭证图，存 OSS 私有桶，返回临时签名 URL

**前端位置**：`withdraw-detail.vue`，字段空时不显示入口

---

## 六、DDL 变更汇总（供审阅）

```sql
-- 2.1 可选（若用 COUNT 子查询性能差）：
-- jst_channel_settlement 加冗余字段 ledger_count INT DEFAULT 0
-- 触发器 AFTER INSERT ON jst_channel_rebate_ledger 维护

-- 2.2 ContractDetailVO 无需改表，字段本就在 jst_contract 表
-- 2.3 InvoiceListVO 字段 tracking_no / tracking_company / delivery_email 可能已在 jst_invoice 表
-- 若不在，执行：
-- ALTER TABLE jst_invoice
--   ADD COLUMN tracking_company VARCHAR(64) DEFAULT NULL,
--   ADD COLUMN tracking_no VARCHAR(64) DEFAULT NULL,
--   ADD COLUMN delivery_email VARCHAR(128) DEFAULT NULL;

-- 5.2 若 jst_withdraw_record 无 pay_voucher_url：
-- ALTER TABLE jst_withdraw_record
--   ADD COLUMN pay_voucher_url VARCHAR(512) DEFAULT NULL;
```

---

## 七、文件路径快速索引

| 前端页面 | 预期后端 Controller | 预期后端 Service |
|---|---|---|
| `invite-list.vue` | `JstChannelInviteController` | `JstChannelInviteService` |
| `distribution.vue` | `JstChannelDistributionController` | `JstChannelDistributionService` |
| `invoice-list.vue` | `WxInvoiceController` | `WxInvoiceService` |
| `invoice-apply.vue` | `WxInvoiceController#apply` | `WxInvoiceService#applyInvoice` |
| `settlement.vue` | `WxSettlementController` | `WxSettlementService` |
| `withdraw-detail.vue` | `WxWithdrawController` | `WxWithdrawService` |
| `contract-detail.vue` | `WxContractController` | `WxContractService` |
| `writeoff-record.vue`（用户模式） | `WxRightsController`（**需新增**端点） | `WxRightsService` |
| `rights/center.vue` `status=invalid` | `WxRightsController#listMy`（扩展参数） | `WxRightsService#listMyRights` |

---

## 八、执行建议

### 推荐顺序

1. **第一批（半天）**：Mapper XML LEFT JOIN（1.1、1.2）— 改动最小，立刻消除前端降级
2. **第二批（半天）**：VO 字段扩展（2.1~2.4）— 改 Java 实体 + Mapper 查询字段
3. **第三批（半天）**：新 API 端点（4.1、4.2）+ DTO 扩展（3.1）— 有新表关联
4. **第四批（OSS 接入后）**：qrcodeUrl、payoutProofUrl

### 验收标准

每条完成后：
1. 对应前端 TODO 注释（统一前缀 `// TODO(backend):`）需被删除或改为正常字段使用
2. 在 `test/wx-tests.http` 添加对应 curl 测试
3. 截图/文字验证接口返回含新字段

### Curl 测试示例

```http
### 4.1 用户权益核销记录
GET http://localhost:8080/jst/wx/rights/writeoff-records?pageNum=1&pageSize=10
Authorization: Bearer {{user_token}}

### 4.2 已失效权益筛选
GET http://localhost:8080/jst/wx/rights/my?status=invalid
Authorization: Bearer {{user_token}}

### 1.1 邀请下级列表（应返回 invitee_channel_name）
GET http://localhost:8080/jst/wx/channel/invite/list?pageNum=1&pageSize=10
Authorization: Bearer {{channel_token}}

### 2.4 分销汇总（应含 monthAmount）
GET http://localhost:8080/jst/wx/channel/distribution/summary
Authorization: Bearer {{channel_token}}
```

---

## 九、已知风险点

| 风险 | 缓解 |
|---|---|
| 1.1 LEFT JOIN 性能：`jst_channel` 表大时可能慢 | 已有 `channel_id` 主键索引，实测 < 5ms |
| 4.1 `jst_rights_writeoff_record` 表结构假设 | 执行前先 `DESC jst_rights_writeoff_record` 确认字段名 |
| 2.1 `ledgerCount` COUNT 子查询 | 若性能问题改为冗余字段 + 触发器维护 |
| 5.1 wx.acode.getUnlimited 配额 | 微信每日 10 万次配额足够，Redis 缓存 7 天减少重复 |

---

## 十、完成后回写

派发后端 Agent 时，要求其完成后：
1. 编辑本文档的每条 TODO 改为 ✅，附 commit hash
2. 更新 `CLAUDE.md` §六"已知问题与待办"中相关项为已完成
3. 产出 `subagent/tasks/任务报告/BACKEND-UX-POLISH-TODO-报告.md`
4. 前端的 TODO 注释（`// TODO(backend):`）由后端 agent 负责删除或更新

---

**文档版本**：V1.0 · 2026-04-18 · 胡字蒙 + Claude Opus 4.7
