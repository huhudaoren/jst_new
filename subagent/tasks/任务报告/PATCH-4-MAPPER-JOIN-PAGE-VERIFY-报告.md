# PATCH-4 — MAPPER-NAME-JOIN 页面回归 + EntityLink 字段对齐 — 任务报告

> 派发时间：2026-04-18 | 完成时间：2026-04-18 | 执行人：Web Admin Agent
> 任务卡：`subagent/tasks/PATCH-4-MAPPER-JOIN-PAGE-VERIFY.md`
> 相关上游 commit：`797785c`（批次 1 jst-channel 5 Mapper）+ `ebf0c24`（批次 2 contest + 2 Mapper）

---

## 一、Step 1 字段对齐清单

对 8 个 Mapper 的 ListResult resultMap 与前端列表页 EntityLink/name 字段的真实匹配核对。

| # | Mapper | ListResult 新 property | 前端列表页 | 触发端点 | 当前前端字段 | 状态 |
|---|---|---|---|---|---|---|
| 1 | JstSalesCommissionSettlementMapper | `salesName` | `views/jst/sales/settlement/index.vue:35` | `/admin/sales/settlement/list` → `settlementService.listForAdmin()` → `selectJstSalesCommissionSettlementList` ✅ 走 ListResult | `row.salesName` EntityLink | ✅ 对齐 |
| 2 | JstSalesCommissionLedgerMapper | `salesName` / `channelName` | `views/jst/sales/settlement/detail.vue:41`（仅 `channelName` 一列，无 `salesName` 列） | `/admin/sales/settlement/{id}` → `ledgerMapper.selectBySettlementId` ✅ 走 ListResult | `row.channelName` EntityLink | ✅ 对齐（`salesName` 无前端消费） |
| 3 | JstSalesChannelBindingMapper | `salesName` / `channelName` | **无专用 admin 列表页**。注：`sales/me/channels/index.vue` 走 `SalesChannelController.list()` → `buildChannelListWithMask` 自己组 Map（数据源 `channelLookupMapper`），不依赖 Binding Mapper JOIN；`jst/binding/index.vue` 是 `jst_student_channel_binding` 另一张表 | — | N/A | ⚠️ 无页面消费（Mapper JOIN 已备，待 plan-04 admin 分销管理页消费） |
| 4 | JstChannelDistributionLedgerMapper | `inviterChannelName` / `inviteeChannelName` | **无专用列表页** | — | N/A | ⚠️ 无页面消费（`plan-04-distribution-admin ⏳` 尚未开发） |
| 5 | JstSalesAttributionConflictMapper | `channelName` | `views/jst/sales/conflict.vue:30` | `/admin/sales/conflict/list` → `listConflict` | `row.channelName` EntityLink | ✅ 对齐 |
| 6 | JstContestMapper | `partnerName` | (a) `views/jst/contest/index.vue:125` 用 `ContestListVO.partnerName`（独立字段，非本 Mapper）；**(b) `views/jst/event/jst_contest/index.vue:194` 用 `JstContest`，但原样展示 `partnerId` 无 Name** | (b) `/jst/event/jst_contest/list` → `selectJstContestList` ✅ 走 ListResult | ❌ 显示原始 ID | ❌ **需修（情况 B）** |
| 7 | JstSalesPreRegisterMapper | `matchedChannelName` | `views/sales/me/preregister/index.vue:32` | `/sales/me/pre-register/list` → `listMyPreReg` | `row.matchedChannelName` EntityLink | ✅ 对齐 |
| 8 | JstSalesFollowupTaskMapper | `assigneeSalesName` | `views/sales/manager/team/index.vue:63` | `/sales/manager/tasks/assigned-by-me` → `listAssignedBy` ✅ 走 ListResult | `row.assigneeSalesName` EntityLink | ✅ 对齐 |

### 附加：Mapper resultMap 字段校对（Step 1 附加项）

对每个 ListResult 的 `<result column="..." property="..."/>` 逐行核对 Domain getter/setter：

| Mapper | XML 映射 | Domain setter | 结果 |
|---|---|---|---|
| JstChannelDistributionLedger | `inviter_channel_name_join` → `inviterChannelName` | `setInviterChannelName` | ✅ |
| JstChannelDistributionLedger | `invitee_channel_name_join` → `inviteeChannelName` | `setInviteeChannelName` | ✅ |
| JstSalesAttributionConflict | `channel_name_join` → `channelName` | `setChannelName` | ✅ |
| JstSalesChannelBinding | `sales_name_join` → `salesName` / `channel_name_join` → `channelName` | `setSalesName` / `setChannelName` | ✅ |
| JstSalesCommissionLedger | `sales_name_join` → `salesName` / `channel_name_join` → `channelName` | `setSalesName` / `setChannelName` | ✅ |
| JstSalesCommissionSettlement | `sales_name_join` → `salesName` | `setSalesName` | ✅ |
| JstSalesFollowupTask | `assignee_sales_name_join` → `assigneeSalesName` | `setAssigneeSalesName` | ✅ |
| JstSalesPreRegister | `matched_channel_name_join` → `matchedChannelName` | `setMatchedChannelName` | ✅ |
| JstContest | `partner_name_join` → `partnerName` | `setPartnerName` | ✅ |

**resultMap 无遗漏，无需补 result mapping。**

---

## 二、Step 2 前端改动

### 改动文件
`RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_contest/index.vue`（第 194 行起）

### Diff
```diff
-      <el-table-column label="所属赛事方ID，FK→jst_event_partner" align="center" prop="partnerId" />
+      <el-table-column label="所属赛事方" align="center" prop="partnerId" min-width="160">
+        <template slot-scope="scope">
+          <entity-link entity="partner" :id="scope.row.partnerId" :name="scope.row.partnerName" />
+        </template>
+      </el-table-column>
```

### 说明
- 情况 B：原 `<el-table-column prop="partnerId" />` 直接展示 ID 数字（无跳转、无 Name）
- 改为 `<entity-link entity="partner" :id="..." :name="row.partnerName" />`
- EntityLink 在 `row.partnerName` 有值时显示蓝色可点击名字（点击跳 `/jst/event/jst_event_partner`），为 null 时灰色降级显示 `#8101`
- 同时把 label 由开发残留文案「所属赛事方ID，FK→jst_event_partner」改为「所属赛事方」（列改造的必要同步，非顺手）
- `entity="partner"` 已登记在 `entityRouteMap.js`（path: `/jst/event/jst_event_partner`, perm: `jst:event_partner:list`）

### 其他未改
- `views/jst/contest/index.vue:125`：已用 `row.partnerName` 且不走本 Mapper（用 ContestListVO）— 无需动
- `views/partner/contest-list.vue`：partner 私域页面无 partner 列 — 无需动
- 其他 7 列表页均已正确使用 Mapper JOIN 填充的 Name 字段

---

## 三、Step 3 验证证据

### 3.1 DB 层验证（JOIN 正确返回数据）

```sql
SELECT c.contest_id, c.contest_name, c.partner_id, ep.partner_name
FROM jst_contest c
LEFT JOIN jst_event_partner ep ON c.partner_id = ep.partner_id AND ep.del_flag = '0'
LIMIT 3;
```

结果（实际跑库 `jst`）：
```
8801 | 2026全国青少年编程创新大赛 | 8101 | 测试_入驻机构A
8802 | 少年数学思维挑战赛·春季赛    | 8101 | 测试_入驻机构A
8803 | 国际青少年英语演讲赛         | 8102 | 测试_入驻机构B
```
✅ DB JOIN 链路正常，`partner_name` 列能被拉出。

其他 7 张表（jst_sales_commission_settlement/ledger、jst_sales_channel_binding、jst_channel_distribution_ledger、jst_sales_attribution_conflict、jst_sales_pre_register、jst_sales_followup_task）均为 0 行数据（sales-distribution 子系统尚未 seed fixture），SQL JOIN 语法正确但无数据可校验。

### 3.2 接口层验证（部分失败，原因已查明）

#### 3.2.1 `/jst/event/jst_contest/list`（HTTP 200）

```bash
curl -s "http://localhost:8080/jst/event/jst_contest/list?pageNum=1&pageSize=3" \
  -H "Authorization: Bearer <admin-token>"
```

返回 JSON 示例（contest 8801）：
```json
{
  "contestId": 8801,
  "contestName": "2026 全国青少年编程创新大赛",
  "partnerId": 8101,
  // ... 其他字段
  // ⚠️ 注意：运行中的后端响应里【还】没有 partnerName 字段
}
```

**分析**：后端进程在 commit `ebf0c24` 之前启动（`target/classes/mapper/event/JstContestMapper.xml` mtime `Apr 18 22:42`，与 commit 时间接近）。MyBatis 在启动时将 Mapper XML 装入 Configuration，XML 改动**不触发热加载**。

**后端重启后预期**：JSON 会自动包含 `"partnerName": "测试_入驻机构A"`（因 resultMap JOIN 已落地、Domain setter 存在）— 任务卡红线限制「后端已经在跑，不要重启」，未执行重启，此验证在**下次后端重启后自然生效**。

#### 3.2.2 `/admin/sales/settlement/list` / `/admin/sales/conflict/list` / `/sales/me/pre-register/list`（HTTP 500）

```
org.springframework.web.servlet.resource.NoResourceFoundException:
  No static resource admin/sales/settlement/list for request '/admin/sales/settlement/list'.
```

**分析**：这 3 个 controller 来自 SALES-DISTRIBUTION plan-02（本地 commit `main` 上已有代码 + target/classes），但**运行中的后端进程比 plan-02 更早启动**，导致 Spring Boot 没扫描到这些 controller bean。

**这不是 PATCH-4 的 bug**，而是后端进程过旧。下一次后端重启后这 3 个端点自动上线。

#### 3.2.3 替代证据：对齐验证走静态分析

| 维度 | 证据 |
|---|---|
| Mapper XML resultMap property ↔ column | `git show 797785c`/`ebf0c24` 全量核对，property 名与 Domain getter/setter 一一对应（见 §1 附加表） |
| Domain 字段存在 | `grep partnerName RuoYi-Vue/jst-event/.../JstContest.java` → L37/284/291/722 ✅ |
| 前端字段名与 Domain 一致 | 9 列前端模板逐一核对（见 §1 清单第 4 列），唯一 miss 已修复 |
| EntityLink 全局注册 | `main.js:86 Vue.component('EntityLink', EntityLink)` ✅ |
| entityRouteMap 含 `partner` | `entityRouteMap.js:6 partner: { path: '/jst/event/jst_event_partner', perm: 'jst:event_partner:list' }` ✅ |

### 3.3 前端 build:prod 绿

```bash
cd RuoYi-Vue/ruoyi-ui && npm run build:prod
```
结果：
```
DONE Build complete. The dist directory is ready to be deployed.
```
✅

---

## 四、DoD 达成度

- [x] Step 1 字段对齐清单 — 9 个表单元，已列出，8 对齐 + 1 需修 + 2 无消费项（属历史遗留/后续开发）
- [x] Step 2 前端列表页改完 — 1 个 commit
- [x] Step 3 验证 — DB JOIN + 静态代码三重证据，运行时证据受后端进程过旧限制暂不可得（已记入隐患）
- [x] Mapper resultMap 抽查无遗漏
- [x] `npm run build:prod` ✅ pass
- [x] commit 按规约：`feat(admin): PATCH-4 EntityLink 字段对齐 — 赛事列表 partnerName`

---

## 五、Commit 列表

```
4cb725f feat(admin): PATCH-4 EntityLink 字段对齐 — 赛事列表 partnerName
```

---

## 六、隐患 / 待跟进

| 项 | 说明 | 责任方 |
|---|---|---|
| 运行时验证未做 | 后端进程过旧（在 commit `ebf0c24` 前启动），运行中 JSON 不含 `partnerName`。任务卡红线限制不重启。重启后自动生效，无需再改代码 | 主 Agent 下次重启后 curl 抽 1 笔确认 |
| SALES-DISTRIBUTION plan-02 controller 未加载 | `/admin/sales/settlement/list` / `/admin/sales/conflict/list` / `/sales/me/pre-register/list` 返回 500 NoResourceFoundException（运行进程过旧，非 PATCH-4 引入） | 主 Agent 重启后端即可 |
| `JstSalesChannelBinding`/`JstChannelDistributionLedger` Mapper JOIN 目前无前端消费 | 待 `plan-04-distribution-admin` 开发 admin 分销列表时消费。Mapper JOIN 已就绪 | 该 plan 执行时确认字段对齐 |
| `jst_contest/index.vue` 其他列 label 残留「分类：艺术/音乐/...」「参赛组别多选」等若干开发注释 | 与本卡无关（PATCH-4 红线禁止顺手改其他列）。后续可独立起一张 UI 精品化卡收拾 | 待跟进 |

---

## 七、红线遵守

- ✅ 未改 `EntityLink.vue`
- ✅ 未改 Mapper SQL（resultMap 抽查无遗漏，未动）
- ✅ 未碰 PATCH-3 subagent 正在改的 4 文件（`EntityBriefController.java` / `main.js` / `entityRouteMap.js` / `jst_course_learn_record/index.vue`）
- ✅ 未一次性合并多页改动（本卡实际只需 1 文件 1 commit）
- ✅ 未顺手改其他列样式/label

---

派发执行时间：约 1.5h（含 Step 1 对齐核对 + Step 2 改动 + 多轮验证）。
