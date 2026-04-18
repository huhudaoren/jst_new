# PATCH-4 — MAPPER-NAME-JOIN 页面回归 + EntityLink 字段对齐

> 优先级：**🔴 P0** | 预估：**S（4-6h）** | Agent：**Web Admin Agent**（前端为主）
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 起因：MAPPER-NAME-JOIN §G.1 隐患——8 个 Mapper 已 LEFT JOIN 补齐 `xxxName`，但**没人在浏览器里验证**前端 `EntityLink` 字段名是否对齐。**编译过 ≠ 用户看到名字**

---

## 一、问题本质

`EntityLink.vue` 现状（**没有自动 fallback**，只显示 `props.name`）：
```vue
<a v-if="canClick && name">{{ name }}</a>
<span v-else-if="name">{{ name }}</span>
<span v-else>{{ id ? '#' + id : '-' }}</span>  <!-- 灰色 loading 态 -->
```

如果列表页代码是：
```vue
<EntityLink entity="channel" :id="row.channelId" :name="row.channelName" />
```
而 Mapper 返回的字段叫 `salesChannelName` 或 `bound_channel_name_join` → **`row.channelName === undefined` → 用户看到 `#8806`**。

本卡是把 8 个 Mapper 的字段名 **真实落到前端列表页**，并通过浏览器验证。

---

## 二、必读上下文

1. **MAPPER-NAME-JOIN 报告**：`subagent/tasks/任务报告/MAPPER-NAME-JOIN-报告.md` 全文（看 8 个 Mapper 的字段名）
2. **EntityLink 组件**：`RuoYi-Vue/ruoyi-ui/src/components/EntityLink.vue`（明确无 fallback）
3. **8 个 Mapper 改过的文件**（开工前 `git show 797785c` 和 `git show ebf0c24` 看 diff）

---

## 三、三步走

### Step 1：清单核对（**先做，不要直接改前端**）

逐个 Mapper 看其新增的 `xxxName` 别名是什么，对应前端列表页是哪个，列出三列表：

| Mapper | Domain 新字段（驼峰） | 前端列表页 | 当前 EntityLink 取的字段 | 是否对齐 |
|---|---|---|---|---|
| JstSalesCommissionSettlementMapper | `salesName` | `views/jst/sales/settlement/index.vue`（实际路径以 grep 为准） | `row.salesName`？ | ✅/❌ |
| JstSalesCommissionLedgerMapper | `salesName, channelName` | `views/jst/sales/ledger/index.vue` | ? | ? |
| JstSalesChannelBindingMapper | `salesName, channelName` | `views/jst/sales/binding/index.vue` 或类似 | ? | ? |
| JstChannelDistributionLedgerMapper | `inviterChannelName, inviteeChannelName` | distribution 列表 | ? | ? |
| JstSalesAttributionConflictMapper | `channelName` | conflict 列表 | ? | ? |
| JstContestMapper | `partnerName` | `views/jst/event/jst_contest/index.vue` 或 `views/partner/contest-list.vue` | ? | ? |
| JstSalesPreRegisterMapper | `matchedChannelName` | preregister 列表 | ? | ? |
| JstSalesFollowupTaskMapper | `assigneeSalesName` | followup-task 列表 | ? | ? |

**找前端列表页的方法**：
```bash
# 以 settlement 为例
grep -rn "settlement\|Settlement" RuoYi-Vue/ruoyi-ui/src/views | grep -v ".test\|.bak" | head -10
```

清单完成后**先把这张表提交主 Agent review**（输出到报告里），等回复"OK 改"再进 Step 2。

### Step 2：改前端列表页

对清单里"未对齐"的项，按以下两种情况处理：

**情况 A**：前端用了 EntityLink 但 prop name 字段错（如 `:name="row.partner_name"` 而 Mapper 返 `partnerName`）
- 改 `:name="row.partnerName"` 与 Domain 字段对齐

**情况 B**：前端还在显示原始 ID（没用 EntityLink）
- 替换为 `<EntityLink entity="partner" :id="row.partnerId" :name="row.partnerName" />`
- 同时保留 `<el-table-column prop="partnerId">` 改成 `<el-table-column label="赛事方">` 用 `<template #default="{ row }"><EntityLink ...></template>`

**情况 C**（mapper 字段大小写差异）：MyBatis 默认驼峰映射，但如果 resultMap 写错可能没生效。验证方法：浏览器 DevTools Network 看接口 raw response，确认 JSON 里字段名是 `partnerName` 还是 `partner_name_join` 还是别的。

每改 2-3 个文件 commit 一次。

### Step 3：浏览器手测（**强制**）

后端启动 + admin 登录 + 逐页打开（依赖 PATCH-1 CORS 修复）：

| 页面 | 验证 |
|---|---|
| settlement 列表 | 销售列显示真名（不是 `#xxx`） |
| ledger 列表 | 销售列 + 渠道列都显示名 |
| binding 列表 | 销售 + 渠道 |
| distribution 列表 | 邀请方 + 被邀请方都显示名 |
| conflict 列表 | 渠道列显示名 |
| 赛事列表（管理端） | 赛事方列显示名 |
| preregister 列表 | matched 渠道列显示名 |
| followup-task 列表 | assignee 销售列显示名 |

**DevTools Network 验证**：8 个页面里**不应该再有** `/admin/entity/brief?type=xxx&id=yyy` 请求（因为 row 里已有 name，EntityLink 不再走 brief fallback）。

把 8 页的截图（或文字记录"✅ settlement 销售列显示'王小明'"）+ Network 抽查截图（"无 brief 请求"）贴报告。

---

## 四、附加：Mapper 字段名校对

Step 1 做的同时，**对 Mapper XML 抽查**：
```bash
cd D:/coding/jst_v1/RuoYi-Vue
grep -nE "AS sales_name_join|AS channel_name_join|AS partner_name_join" jst-channel/src/main/resources/mapper/channel/*.xml jst-event/src/main/resources/mapper/event/JstContestMapper.xml
```

**确认每个 alias 都映射到 Domain 的对应驼峰字段**（看 `ListResult` resultMap 的 `<result column="sales_name_join" property="salesName"/>` 这行是否齐全）。

如果发现某 Mapper 的 alias→property 对应缺失或写错（subagent 该 commit 后没人验证），**直接补 resultMap**（这是配套修复，不算越界），但要在 commit message 里明确"修 MAPPER-NAME-JOIN 遗漏的 result mapping"。

---

## 五、DoD

- [ ] Step 1 字段对齐清单提交并获 review
- [ ] Step 2 前端列表页改完，分批 commit
- [ ] Step 3 8 个页面浏览器逐个验证，DevTools Network 抽 3 个确认无 brief 请求
- [ ] Mapper resultMap 抽查无遗漏
- [ ] `npm run build:prod` ✅
- [ ] 报告附 8 页验证证据 + 3 张 Network 截图
- [ ] commit 系列：`feat(admin): PATCH-4 EntityLink 字段对齐 — xxx 列表`

---

## 六、红线

- ❌ 不改 EntityLink.vue（它的设计就是不 fallback，rely on row.name）
- ❌ 不改 Mapper 的 SQL（MAPPER-NAME-JOIN 已落地，本卡是配套前端 + resultMap）
- ❌ Step 3 必须**真实浏览器**验证，不许"看代码觉得对"就交付
- ❌ 不许"顺手"改其他列样式/对齐
- ❌ 不许把 8 个页面一次性 1 个 commit 改完

---

## 七、派发附言

如果某个列表页没找到（比如 conflict 列表前端尚未做），在报告里明确"❌ 该列表页不存在前端，待后续做"——属于已知缺口，不是本卡 blocker。

派发时间：2026-04-18 | 主 Agent：竞赛通架构师
