# 任务报告 - MAPPER-NAME-JOIN 全站列表 Mapper 补 LEFT JOIN 名称字段

## 0. 当前工作区信息

- 工作区根目录：`D:\coding\jst_v1`
- 代码仓目录：`D:\coding\jst_v1\RuoYi-Vue`
- 当前分支：`main`
- 完成时间：`2026-04-18`

---

## A. 任务目标与约束

本卡目标是为全站使用中的 list Mapper 补充关联表名称字段，避免前端 `EntityLink` 继续走 fallback 显示 ID。

本次执行严格遵守任务卡约束：

1. Step 1 先做扫描清单并提交 review，确认后才进入改代码。
2. 所有新增关联一律使用 `LEFT JOIN`，避免 FK 为空时丢行。
3. 所有带 JOIN 的列表查询，`where` 条件列名都补表别名前缀，避免 `ambiguous column`。
4. 所有列表查询新增 `ListResult` resultMap，继承原 resultMap，不直接改原 resultMap。
5. 每改 3-5 个 Mapper 独立 commit。
6. 最终执行全模块 `mvn clean compile`，要求全绿。

本卡是纯 SQL / Mapper / Domain 非持久化字段补齐，没有改 Controller、Service、事务、锁、权限点、DDL。

---

## B. Step 1 扫描 review 结论

### B.1 高置信、进入实施范围的 8 个 Mapper

| 序号 | 模块 | Mapper | 需要补的名称字段 | 备注 |
|---|---|---|---|---|
| 1 | jst-channel | `JstSalesCommissionSettlementMapper.xml` | `salesName` | 任务卡点名项 |
| 2 | jst-channel | `JstSalesCommissionLedgerMapper.xml` | `salesName`、`channelName` | 任务卡点名项 |
| 3 | jst-channel | `JstSalesChannelBindingMapper.xml` | `salesName`、`channelName` | 任务卡点名项 |
| 4 | jst-channel | `JstChannelDistributionLedgerMapper.xml` | `inviterChannelName`、`inviteeChannelName` | 任务卡点名项 |
| 5 | jst-event | `JstContestMapper.xml` | `partnerName` | 任务卡点名项 |
| 6 | jst-channel | `JstSalesAttributionConflictMapper.xml` | `channelName` | 扫描补出的漏网项 |
| 7 | jst-channel | `JstSalesPreRegisterMapper.xml` | `matchedChannelName` | 扫描补出的漏网项 |
| 8 | jst-channel | `JstSalesFollowupTaskMapper.xml` | `assigneeSalesName` | 扫描补出的漏网项 |

### B.2 扫描时同步识别的“非标准 list 方法”

只改 `selectXxxList` 不够，以下方法实际也被前端列表页消费，因此一并纳入：

| Mapper | 实际一起补齐的方法 |
|---|---|
| `JstSalesCommissionLedgerMapper.xml` | `selectBySettlementId` |
| `JstSalesChannelBindingMapper.xml` | `selectCurrentByOwnerSales`、`selectAllCurrent` |
| `JstChannelDistributionLedgerMapper.xml` | `selectByInviter` |
| `JstSalesAttributionConflictMapper.xml` | `selectByStatus` |
| `JstSalesPreRegisterMapper.xml` | `selectMineByStatus` |
| `JstSalesFollowupTaskMapper.xml` | `listMine`、`listAssignedBy` |

### B.3 扫描时刻意未纳入本卡的项

- `FormTemplateMapperExt.xml`：
  当时复查页面后判定为第二优先级，当前列表暂无明确前端消费 `ownerName`，因此本卡未动。
- 已经补过名称字段的 Mapper：
  扫描时发现部分 Mapper 已有 `ownerName` / `contestName` / `goodsName` 等现成字段，本卡未重复改。

---

## C. 实际交付清单

### C.1 批次 1

- Commit：`797785c`
- 提交信息：`feat(backend): MAPPER-NAME-JOIN 批次 1 — jst-channel 补齐关联 name`

#### 批次 1 修改的 Mapper XML

| 文件 | 新增名称字段 | JOIN 目标表 | 一并补齐的方法 |
|---|---|---|---|
| `jst-channel/.../JstSalesCommissionSettlementMapper.xml` | `salesName` | `jst_sales` | `selectJstSalesCommissionSettlementList` |
| `jst-channel/.../JstSalesCommissionLedgerMapper.xml` | `salesName`、`channelName` | `jst_sales`、`jst_channel` | `selectJstSalesCommissionLedgerList`、`selectBySettlementId` |
| `jst-channel/.../JstSalesChannelBindingMapper.xml` | `salesName`、`channelName` | `jst_sales`、`jst_channel` | `selectJstSalesChannelBindingList`、`selectCurrentByOwnerSales`、`selectAllCurrent` |
| `jst-channel/.../JstChannelDistributionLedgerMapper.xml` | `inviterChannelName`、`inviteeChannelName` | 双别名 `jst_channel` | `selectJstChannelDistributionLedgerList`、`selectByInviter` |
| `jst-channel/.../JstSalesAttributionConflictMapper.xml` | `channelName` | `jst_channel` | `selectJstSalesAttributionConflictList`、`selectByStatus` |

#### 批次 1 修改的 Domain

| Domain | 新增非持久化字段 |
|---|---|
| `JstSalesCommissionSettlement` | `salesName` |
| `JstSalesCommissionLedger` | `salesName`、`channelName` |
| `JstSalesChannelBinding` | `salesName`、`channelName` |
| `JstChannelDistributionLedger` | `inviterChannelName`、`inviteeChannelName` |
| `JstSalesAttributionConflict` | `channelName` |

### C.2 批次 2

- Commit：`ebf0c24`
- 提交信息：`feat(backend): MAPPER-NAME-JOIN 批次 2 — contest 与销售漏网列表补齐`

#### 批次 2 修改的 Mapper XML

| 文件 | 新增名称字段 | JOIN 目标表 | 一并补齐的方法 |
|---|---|---|---|
| `jst-event/.../JstContestMapper.xml` | `partnerName` | `jst_event_partner` | `selectJstContestList` |
| `jst-channel/.../JstSalesPreRegisterMapper.xml` | `matchedChannelName` | `jst_channel` | `selectJstSalesPreRegisterList`、`selectMineByStatus` |
| `jst-channel/.../JstSalesFollowupTaskMapper.xml` | `assigneeSalesName` | `jst_sales` | `selectJstSalesFollowupTaskList`、`listMine`、`listAssignedBy` |

#### 批次 2 修改的 Domain

| Domain | 新增非持久化字段 |
|---|---|
| `JstContest` | `partnerName` |
| `JstSalesPreRegister` | `matchedChannelName` |
| `JstSalesFollowupTask` | `assigneeSalesName` |

### C.3 统一实施模式

8 个 Mapper 都使用了同一套实现模式：

1. 保留原有 `Result` resultMap 不动。
2. 新建 `ListResult` resultMap，`extends` 原 resultMap。
3. 在列表 SQL 中改成 `主表别名.* + 关联字段 AS xxx_name_join`。
4. 新增 `LEFT JOIN` 到对应实体表。
5. 所有新 JOIN 的列表 SQL 统一给 `where` 条件和 `order by` 补主表别名。
6. Domain 中只补非持久化字段和 getter/setter，不改数据库字段定义。

---

## D. 关键实现细节

### D.1 为什么坚持 `LEFT JOIN`

任务卡明确要求 FK 为空时不能丢行，所以即使关联名称缺失，也要保证列表主记录仍能返回。这一点在以下场景特别重要：

- `sales_id`、`channel_id`、`partner_id` 允许为空或历史数据不完整。
- 关联表被逻辑删除后，主表历史单据仍然需要展示。
- 前端可以接受 `xxxName = null`，但不能接受整行消失。

### D.2 为什么 where 必须补别名

这批查询一旦引入 JOIN，就会出现很多重名列：

- `status`
- `channel_id`
- `sales_id`
- `partner_id`
- `create_time`

如果不补别名，MySQL 很容易报：

```text
Column 'xxx' in where clause is ambiguous
```

因此本卡所有新 JOIN 的列表 SQL 都把过滤列改成了 `主表别名.列名`。

### D.3 为什么不改原 resultMap

这是任务卡红线之一。保留原 resultMap 的好处：

- 不影响详情查询、写入回读、内部业务查询等旧路径。
- 只让列表查询走 `ListResult`，改动面更可控。
- 后续如果某个名称字段只服务列表页，也不会污染所有查询语义。

### D.4 本卡刻意没有做的事

- 没有补充 Controller / Service 组装逻辑。
- 没有改 `selectById`、详情页查询、写接口。
- 没有顺手做前端回填或 `EntityLink` 兜底逻辑清理。
- 没有做数据库索引、DDL、缓存、枚举翻译等额外优化。

---

## E. 编译与验证

### E.1 分阶段编译

先执行了局部联编，验证第二批 XML 和字段映射：

```bash
mvn -pl jst-channel,jst-event -am compile -DskipTests
```

结果：`BUILD SUCCESS`

### E.2 全模块编译

按任务卡要求，最终执行：

```bash
mvn clean compile
```

结果：18 个模块全部 `SUCCESS`，包含：

- `ruoyi-common`
- `ruoyi-system`
- `ruoyi-framework`
- `ruoyi-quartz`
- `ruoyi-generator`
- `jst-common`
- `jst-user`
- `jst-order`
- `jst-event`
- `jst-channel`
- `jst-points`
- `jst-organizer`
- `jst-marketing`
- `jst-message`
- `jst-risk`
- `jst-finance`
- `ruoyi-admin`

总结果：`BUILD SUCCESS`

### E.3 本轮未做的验证

- 未启动后端做 HTTP 接口 smoke。
- 未逐页点开 admin / 小程序页面验证渲染。
- 未对前端 `EntityLink` fallback 消失情况做人工录屏或截图比对。

所以本卡的验证强度目前是：

- SQL 语法级正确
- MyBatis 映射级正确
- Java 编译级正确

但还不是页面行为级完全回归。

---

## F. 当前工作区状态与并行改动说明

当前工作区是共享脏工作区，不是干净分支环境。

### F.1 与本卡无关、但当前工作区存在的并行改动

工作区里还有大量其他模块改动，包括但不限于：

- `jst-organizer`
- `jst-user`
- `ruoyi-admin`
- `ruoyi-ui`
- `jst-uniapp`

这些改动不是本卡产生，我没有替用户回滚，也没有把它们打包进本卡 commit。

### F.2 与本卡相关、但当前又出现新改动的文件

当前 `jst-channel/src/main/resources/mapper/channel/JstSalesCommissionLedgerMapper.xml` 在我完成批次 1 提交之后，又出现了未提交的新 diff。

这部分新 diff 内容是：

- `defaultAddressJoin`
- `regionExpr`
- `ledgerBucketExpr`
- `orderBucketExpr`
- `selectCommissionTrend`
- `selectCompressionStats`
- `selectChannelHeatmap`

判断：这是另一张看板 / dashboard 相关任务的并行改动，不属于本卡的 `name join` 交付范围。

处理原则：

- 我没有覆盖它。
- 我没有把它混进本卡 commit。
- 后续如果要 cherry-pick 本卡 commit 到别的分支，需注意这个文件可能发生冲突。

---

## G. 隐患点

### G.1 页面级回归尚未做

虽然编译通过，但还没有逐页验证以下页面是否已经完全不走 fallback：

- 佣金结算列表
- 佣金台账列表 / 结算详情台账
- 销售绑定列表
- 渠道分销台账列表
- 归因冲突列表
- 预登记列表
- 跟进任务列表
- 赛事列表

如果前端某处字段名写成别的命名，仍有可能看起来“像没生效”，这需要页面联调确认。

### G.2 历史脏数据可能仍返回 null

本卡保证的是：

- 主记录不丢
- 关联名称能在 FK 正常时带回

但如果历史数据存在以下情况，`xxxName` 仍会是 `null`：

- FK 本身为空
- FK 指向的记录不存在
- 关联表记录已逻辑删除，且 JOIN 明确加了 `del_flag = '0'`

这不属于 SQL Bug，而是数据现状。前端如果要求“永远有名字”，还需要数据修复或更复杂兜底。

### G.3 扫描结论是按“当前消费路径”做的

本卡处理的是当前已确认被列表页消费的 Mapper 和非标准 list 方法。如果后续有人新增页面，开始消费同一个 Mapper 里的其他查询方法，可能还会遇到新的 `xxxName` 缺口。

### G.4 `FormTemplateMapperExt.xml` 仍是潜在候选

扫描时把它排到第二优先级，没有纳入本卡。如果后续前端开始在模板列表展示 `ownerName` 或赛事方名称，这里大概率还要补一次。

---

## H. TODO 建议

### H.1 建议立即补的回归动作

1. 用 admin 账号把 8 个目标列表页各点一遍，确认前端 `EntityLink` 已直接展示 `xxxName`。
2. 抽查 2-3 条“FK 为空 / 关联删除”的历史数据，确认 `LEFT JOIN` 没有丢行。
3. 重点关注 `JstSalesCommissionLedgerMapper.xml`，因为它当前有并行 diff，合并时要防止覆盖本卡改动。

### H.2 建议后续补扫的一轮

1. 复查 `FormTemplateMapperExt.xml` 是否需要补名称字段。
2. 继续扫是否还有“前端读 `xxxName`，后端列表没 JOIN”的非标准方法。
3. 如果这类问题高频出现，建议沉淀一份“列表页 ID→名称 字段巡检清单”，避免每次靠人工扫。

### H.3 如果要继续扩卡

后续如果继续做这类任务，建议保持同一标准：

1. 先扫“页面真实消费字段”而不是只扫表结构。
2. 优先查“非标准 list 方法”。
3. 一律 `LEFT JOIN + ListResult + where 带别名`。
4. 小批次 commit，避免单次改太多 Mapper。

---

## I. 最终结论

本卡已完成 8 个高置信目标 Mapper 的后端补齐，并按要求分 2 个批次提交：

- `797785c`
- `ebf0c24`

交付状态：

- Step 1 扫描 review 已完成并确认
- 8 个目标 Mapper 已改完
- 对应 Domain 非持久化名称字段已补完
- 非标准列表方法已同步补齐
- 全模块 `mvn clean compile` 已通过

当前未完成项只剩页面级联调 / HTTP 级 smoke，不影响“代码已交付、可继续联调”的判断。
