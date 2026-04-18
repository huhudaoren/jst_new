# MAPPER-NAME-JOIN — Mapper LEFT JOIN 补齐列表页关联名称

> 优先级：**P2** | 预估：**M**（1 天） | Agent：**Backend Agent**
> 派发时间：2026-04-18 | 版本：任务卡 v1
> **独立任务**，不依赖其他 4 卡（但做完对前端 `EntityLink` 体验更好）

---

## 一、业务背景

Plan-06 加了 `EntityLink` 组件，让列表页里的 `userId / channelId / contestId` 等字段能点击跳详情、并且展示"名称而非数字"。但很多列表接口目前只返回 ID，不返回名称，`EntityLink` 为此做了 fallback：**如果 row 里没有 `xxxName` 字段，就发请求查 `/admin/entity/brief` hydrate**。

这 fallback 是"能用但浪费"的方案——一个列表 20 行 × 每行 3 个 ID → 60 次 brief 请求。本卡把这些都通过 Mapper `LEFT JOIN` 一次性补齐，前端 `EntityLink` 直接用 row 里的 `xxxName`，请求降到 1 次。

---

## 二、必读上下文

1. `CLAUDE.md` § 四 / § 五（禁止直接返回 Entity，必须 VO）
2. `RuoYi-Vue/ruoyi-ui/src/components/EntityLink.vue`（看它读哪些 fallback 字段 → 决定 Mapper 要补哪些 `xxxName`）
3. **项目已有 JOIN 示范**：
   - `RuoYi-Vue/jst-event/src/main/resources/mapper/event/JstEventPartnerMapper.xml`（plan-06 已经 JOIN 过，看最近 commit）
   - `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/JstSalesMapper.xml`
   - 近期类似改造：`git log --oneline | grep -iE "ID2NAME|POLISH" | head -5` 找 ID2NAME-BE 那一批 commit 的 diff
4. `CLAUDE.md` § 六 "ID→名称 ID2NAME-BE 16 Mapper LEFT JOIN" —— **本卡是对那轮的补漏**，不是重来

---

## 三、扫描 → 分类 → 改造

### Step 1：扫描待改 Mapper

#### 1.1 扫描目标

在以下范围内找出"Service 返给列表接口的 VO 里有 `xxxId` 但没有对应 `xxxName`"的 Mapper：

```bash
cd RuoYi-Vue

# 找所有 list 类 Mapper XML（非 ext）
find jst-*/src/main/resources/mapper -name '*.xml' | xargs grep -lE '<select id="select[A-Z][a-zA-Z]*List"' | head -40

# 对每个候选文件，看 columns / resultMap 是否有 *_id 但无对应 *_name
# 以 JstSalesCommissionSettlementMapper.xml 为例
grep -A 30 '<resultMap' jst-channel/src/main/resources/mapper/channel/JstSalesCommissionSettlementMapper.xml
```

#### 1.2 重点嫌疑清单（**必须全部检查**，可能还有漏网）

| Mapper（路径前缀省略 `RuoYi-Vue/`） | 可能缺的 name |
|---|---|
| `jst-channel/.../mapper/channel/JstSalesCommissionSettlementMapper.xml` | salesName（关联 jst_sales） |
| `jst-channel/.../mapper/channel/JstSalesCommissionLedgerMapper*.xml` | salesName, channelName, orderNo |
| `jst-channel/.../mapper/channel/JstSalesChannelBindingMapper.xml` | salesName, channelName |
| `jst-channel/.../mapper/channel/JstChannelDistributionMapper.xml` | inviterChannelName, inviteeChannelName |
| `jst-channel/.../mapper/channel/JstChannelMapper.xml` | boundSalesName（如有 bound_sales_id） |
| `jst-event/.../mapper/event/JstCertTemplateMapper.xml` | partnerName |
| `jst-event/.../mapper/event/JstContestMapper.xml` | partnerName, formTemplateName, certTemplateName |
| `jst-event/.../mapper/event/JstEventPartnerMapper.xml` | （plan-06 已改，复查） |
| `jst-event/.../mapper/event/PartnerCertMapper.xml` | templateName |
| `jst-event/.../mapper/event/FormTemplateMapperExt.xml` | partnerName |
| `jst-marketing/.../mapper/marketing/JstCouponIssueBatchMapper.xml` | couponTemplateName |
| `jst-order/.../mapper/order/JstTeamAppointmentMapper.xml` | contestName, participantName |
| `jst-organizer/.../mapper/organizer/JstChannelAuthApplyMapper.xml` | channelName, userName |
| `jst-organizer/.../mapper/organizer/JstEventPartnerApplyMapper.xml` | contactUserName |
| `jst-points/.../mapper/points/JstMallExchangeOrderMapper.xml` | userName, goodsName |
| `jst-risk/.../mapper/risk/JstAuditLogMapper.xml` | operatorUserName |
| `jst-risk/.../mapper/risk/JstRiskAlertMapper.xml` | userName（或 triggerUserName） |

> 这张表是**起点而非终点**。subagent 要自己扫一遍 `git status` 中 `M` 标记的 Mapper XML（截至 2026-04-18 的未提交改动），以及 `jst-*/src/main/resources/mapper/**/*.xml` 全量，发现漏的加进清单。

#### 1.3 清单提交主 Agent review

产出一张表：

| 文件 | VO 类路径 | 缺字段 | 关联表 | 关联主键 |
|---|---|---|---|---|
| JstSalesCommissionSettlementMapper.xml | .../vo/SettlementListVO.java（或直接 domain） | salesName | jst_sales | sales_id |
| ... | ... | ... | ... | ... |

**等主 Agent 确认后再进 Step 2**，避免做无用功。

---

### Step 2：按 Mapper 批量改造

#### 2.1 改造套路（每个 Mapper 3 步）

**Mapper XML**：
1. **`cols` sql 片段保持原样**（其他 by-id select 还要用）
2. **只改 `selectXxxList`**：在 SELECT 后追加 JOIN 表的字段 + LEFT JOIN 子句

```xml
<!-- 原 -->
<select id="selectJstSalesCommissionSettlementList" ...>
    SELECT <include refid="cols"/> FROM jst_sales_commission_settlement
    <where>...</where>
</select>

<!-- 改 -->
<select id="selectJstSalesCommissionSettlementList" ...>
    SELECT s.*, sa.sales_name AS sales_name_join
      FROM jst_sales_commission_settlement s
      LEFT JOIN jst_sales sa ON s.sales_id = sa.sales_id
    <where>
        <if test="salesId != null">AND s.sales_id = #{salesId}</if>
        <!-- 原有 where 条件的列名都加 s. 前缀 -->
    </where>
    ORDER BY s.period DESC
</select>
```

3. **更新 resultMap**（如果 List 和 byId 共用 resultMap，就新建一个 ListResult 专给 list 用，**不要动原 resultMap 防连坐 bug**）：

```xml
<resultMap id="JstSalesCommissionSettlementListResult"
           type="com.ruoyi.jst.channel.domain.JstSalesCommissionSettlement"
           extends="JstSalesCommissionSettlementResult">
    <result property="salesName" column="sales_name_join"/>
</resultMap>

<!-- 然后 list select 改成 resultMap="JstSalesCommissionSettlementListResult" -->
```

**Domain / VO**：
4. 给 Domain 类（或 ListVO 类，哪种风格跟该模块现状走）加一个 `salesName` 字段 + getter/setter（用 Lombok 就 `@Data` 自动）。**放在底部**，不要插到现有字段中间。

#### 2.2 注意 where 条件加前缀

原来 `<where>` 里的 `AND sales_id = #{salesId}` 在加了 JOIN 后可能歧义（jst_sales 和 jst_sales_commission_settlement 都有 sales_id），**必须加表别名前缀**：`AND s.sales_id = #{salesId}`。

漏了这步会报 `Column 'sales_id' in where clause is ambiguous`。跑一次启动测试接口就能发现，但更快的办法是 subagent 自己写完后用 IDE grep 每个 `<if>` 的 column 都有前缀。

#### 2.3 params.scopeSalesIds 等嵌套 params 的 where 条件也要加前缀

plan-06 中 Task 21 加了 `<if test="params.scopeSalesIds != null">AND sales_id IN (...)</if>`，改造后要改成 `AND s.sales_id IN (...)`。

---

### Step 3：验证

#### 3.1 编译

```bash
cd RuoYi-Vue && mvn clean compile -DskipTests
```

期望全模块 BUILD SUCCESS。

#### 3.2 启动 + 接口抽测

启动 `ruoyi-admin`，用项目已有的 `.http` 文件（`test/admin-tests.http`）挑 3 个改过的 list 接口跑：
- 期望返回 200
- 响应 JSON 里新 `xxxName` 字段**有值**（不是 null，除非关联表确实被删/FK 为空）
- 不改对照：在 `git stash` 原样跑一次，对比两次响应的 `total` / `rows.length` 一致（**JOIN 不应改变行数**——如果行数变了就是 JOIN 条件写错成了 INNER JOIN）

#### 3.3 前端无感

不改前端。`EntityLink` 本来就优先读 row 里的 `xxxName`，Mapper 一补齐，fallback 请求自动不再发。浏览器 DevTools Network 里看，进改过的列表页，`/admin/entity/brief` 请求数应该明显减少。

---

## 四、DoD 验收标准

- [ ] Step 1 清单提交并获批
- [ ] Step 2 每改 3-5 个 Mapper 独立 commit 一次
- [ ] 所有改过的 Mapper 都新建 `XxxListResult` resultMap，**不动原 byId resultMap**
- [ ] 所有 `<where>` 条件列名都加了表别名前缀
- [ ] `mvn clean compile` 全模块 ✅
- [ ] ≥ 3 个接口 .http 手测 ✅（新字段有值 + 行数无变化）
- [ ] 报告列表：改过的 Mapper 数量、新增字段数量、前端 Network 请求数量变化对比（改前/改后各截图一次，看 brief 请求数）
- [ ] commit 系列：`feat(backend): MAPPER-NAME-JOIN 批次 N — jst-xxx 补齐关联 name`

---

## 五、红线（不许做的事）

- ❌ **不许用 INNER JOIN**，只能 LEFT JOIN（FK 为空时行不能丢）
- ❌ **不许改原 resultMap**，加新 resultMap 继承原有
- ❌ 不许改接口响应字段名（比如改成 `sales_name_display` 这种）——字段名就叫 `salesName`（Java 驼峰）对应列 `sales_name_join`
- ❌ 不许改 `cols` sql 片段（其他 by-id 还要用）
- ❌ 不许改 VO 类里原有字段的顺序
- ❌ 不许改 where 原有业务逻辑（只给 column 加前缀）
- ❌ 不许"顺手"给 byId select 也 JOIN（byId 现在走 EntityBriefController，重复 JOIN 浪费）
- ❌ **不许改数据库 schema / 索引**（纯 Mapper 改造）
- ❌ 不许把此改造扩展到若依系统 Mapper（`sys_*`）

---

## 六、派发附言

本卡工作量大但技术门槛低，适合"细心的 SQL 工"。关键是：
1. **先 grep 找齐所有候选**，不要改一半漏一半
2. **每个 Mapper 3 步**，别发挥创意
3. **列名加前缀**，避免 ambiguous 错误
4. **LEFT JOIN 不是 INNER JOIN**，避免丢行

如果发现某 Mapper 的 list SQL 有自定义动态 JOIN（已经 JOIN 过别的表），就在现有 JOIN 链后**追加** LEFT JOIN，不要推倒重写。

派发时间：2026-04-18 | 主 Agent 签名：竞赛通架构师
