# ADMIN-DASHBOARD-METRICS — 销售 admin 看板 3 指标（成本趋势 / 压缩触发率 / 渠道热力）

> 优先级：**P2** | 预估：**M**（1-1.5 天） | Agent：**Web Admin Agent**（跨栈，后端 SQL + 前端 echarts）
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 依赖：plan-06 已产出的 `AdminSalesDashboardController` + `jst/sales/dashboard.vue`
> **独立任务**，不依赖其他 4 卡

---

## 一、业务背景

Plan-06 已把 admin 销售看板做成有筛选（日期范围 / 业务类型 / 地区）的基础版，但用户反馈"老板最关心的 3 个经营指标还没做"：

1. **提成成本趋势（按日/周/月）**：每天发出去多少销售提成、对比收入占比是不是健康
2. **J 上限压缩触发率**：方案里"单笔分润 ≤ 15% of 实付"这个上限今天触发了多少次、压了多少钱——用于判断费率设计是否偏激进
3. **渠道业绩热力图**：按地区 / 按业务类型双维度看哪些是 TOP 渠道、哪些已经睡死

这 3 个指标都是已有数据源能算的（`jst_sales_commission_ledger` + `jst_channel` + `jst_order_main`），本卡把它们加到现有 admin 看板上。

---

## 二、必读上下文

1. `CLAUDE.md` § 四 / § 五
2. **销售分销系统 spec**：`docs/superpowers/specs/2026-04-18-sales-channel-distribution-design.md`（重点看 §3.2 提成模型 + § "J 上限" 章节）
3. **已有后端代码**：
   - `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesDashboardController.java`（**在此文件追加 3 个端点，不新建 Controller**）
   - `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/AdminSalesDashboardServiceImpl.java`（SQL 实现层，照现有 `selectSalesRanking` / `selectOverview` 风格追加）
4. **已有前端代码**：
   - `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue`（**在此文件追加 3 个区块**）
   - `RuoYi-Vue/ruoyi-ui/src/api/jst/salesDashboard.js`（前端 API 文件，对齐加 3 个方法）
5. **数据表关键字段**（**必须 DESCRIBE 核对**，脑补字段名会翻车）：
   - `jst_sales_commission_ledger`：`ledger_id, sales_id, order_id, channel_id, business_type, commission_amount, compression_amount, original_amount, accrue_time, status, period`
     （⚠️ 注意：`compression_amount` 和 `original_amount` 是 plan-02 产出的；如果实际字段名不同，以 DESCRIBE 为准）
   - `jst_channel`：`channel_id, channel_name, province, city, business_type, status` （province/city 可能叫 `region` 或 `area`，DESCRIBE）
   - `jst_order_main`：`order_id, net_pay_amount, order_status, pay_time`

---

## 三、3 个指标详细设计

### 指标 1：提成成本趋势（按日/周/月）

#### 1.1 后端端点

**方法签名**：
```java
/**
 * 提成成本趋势。
 *
 * @param bucket  聚合粒度：day / week / month
 * @param startDate yyyy-MM-dd（含）
 * @param endDate   yyyy-MM-dd（含）
 * @return List of {bucket: '2026-04-01', commissionTotal: 12345.67, orderNetTotal: 98765.43, costRate: 0.125}
 */
@GetMapping("/admin/sales/dashboard/commission-trend")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_finance')")
public AjaxResult commissionTrend(
    @RequestParam(defaultValue = "day") String bucket,
    @RequestParam String startDate,
    @RequestParam String endDate) { ... }
```

#### 1.2 SQL 核心（放 ServiceImpl）

```sql
-- day bucket 示例
SELECT
  DATE(l.accrue_time)                         AS bucket,
  SUM(l.commission_amount)                    AS commissionTotal,
  (SELECT IFNULL(SUM(o.net_pay_amount),0)
     FROM jst_order_main o
    WHERE DATE(o.pay_time) = DATE(l.accrue_time)
      AND o.order_status = 'paid')            AS orderNetTotal
FROM jst_sales_commission_ledger l
WHERE l.accrue_time >= ? AND l.accrue_time < DATE_ADD(?, INTERVAL 1 DAY)
  AND l.status IN ('accrued','settled')
GROUP BY DATE(l.accrue_time)
ORDER BY bucket ASC
```

- `week` bucket：`DATE_FORMAT(l.accrue_time, '%x-W%v')` 或 `YEARWEEK(l.accrue_time, 1)`
- `month` bucket：`DATE_FORMAT(l.accrue_time, '%Y-%m')`
- `costRate` 在 Service 层算：`commissionTotal / orderNetTotal`（防除零，null → 0）

#### 1.3 前端图表

`dashboard.vue` 加一个区块：
- 顶部：`<el-radio-group>` bucket 切换（日/周/月）+ 复用页顶已有的日期范围
- 主体：`echarts` 双 Y 轴折线图
  - 左轴：提成总额（柱）
  - 右轴：成本率 %（线）
- 下方：小卡片显示"区间平均成本率 X%"、"环比上周期 +/- Y pp"

---

### 指标 2：J 上限压缩触发率

#### 2.1 业务定义

在 spec §3.2 里，单笔提成 ≤ 15% * 实付金额是硬顶。plan-02 落地时把 `commission_amount`（实际发）和 `original_amount`（未压缩前计算值）都存了 ledger，`compression_amount = original_amount - commission_amount`（> 0 即触发了压缩）。

**两个指标**：
- **触发次数占比** = `COUNT(compression_amount > 0) / COUNT(*)`
- **压缩总金额** = `SUM(compression_amount)`

#### 2.2 后端端点

```java
@GetMapping("/admin/sales/dashboard/compression-stats")
public AjaxResult compressionStats(
    @RequestParam String startDate,
    @RequestParam String endDate,
    @RequestParam(required = false) String businessType) { ... }
```

#### 2.3 SQL

```sql
SELECT
  COUNT(*)                                                    AS totalCount,
  SUM(CASE WHEN compression_amount > 0 THEN 1 ELSE 0 END)     AS triggeredCount,
  IFNULL(SUM(compression_amount), 0)                          AS compressedAmount,
  IFNULL(SUM(original_amount), 0)                             AS originalTotal
FROM jst_sales_commission_ledger
WHERE accrue_time >= ? AND accrue_time < DATE_ADD(?, INTERVAL 1 DAY)
  AND status IN ('accrued','settled')
  <if test="businessType != null"> AND business_type = #{businessType}</if>
```

返回：`{ totalCount, triggeredCount, triggerRate: triggeredCount/totalCount, compressedAmount, compressedRate: compressedAmount/originalTotal }`

#### 2.4 前端展示

看板加一个 2x2 卡片区：
- 卡 1：触发次数 / 总次数（大号 vs 小号）
- 卡 2：触发率 % （带预警色：> 10% 红、5-10% 黄、<5% 绿）
- 卡 3：压缩金额（大号 ¥）
- 卡 4：压缩率 % （压缩金额 / 原计算值）

下方加提示文案：
> ⚠️ 压缩率持续 > 10% 意味着费率设计偏激进，建议检查 `jst_sales_commission_rate` 中自定义费率是否需要下调。

---

### 指标 3：渠道业绩热力图

#### 3.1 双维度

- 行：地区（`jst_channel.province` 聚合，如字段是 `region` 以 DESCRIBE 为准）
- 列：业务类型（`business_type`：报名 / 课程 / 团购 / 商城 / 合同等，**从字典 `jst_business_type` 取枚举**，前端 `this.getDicts('jst_business_type')`）

格子值 = 该地区 + 业务类型下的 `SUM(net_pay_amount)`（不脱敏，admin 有权看金额，参考 § spec 铁律）。

#### 3.2 后端端点

```java
@GetMapping("/admin/sales/dashboard/channel-heatmap")
public AjaxResult channelHeatmap(
    @RequestParam String startDate,
    @RequestParam String endDate) { ... }
```

**SQL**：
```sql
SELECT
  c.province    AS region,
  o.business_type,
  COUNT(DISTINCT c.channel_id) AS channelCount,
  SUM(o.net_pay_amount)         AS gmv
FROM jst_order_main o
JOIN jst_channel c ON o.channel_id = c.channel_id
WHERE o.pay_time BETWEEN ? AND DATE_ADD(?, INTERVAL 1 DAY)
  AND o.order_status = 'paid'
GROUP BY c.province, o.business_type
ORDER BY gmv DESC
```

> ⚠️ `jst_order_main` 里不一定直接有 `business_type`。如果没有，按以下备选顺序：
> 1. `jst_order_main` 有 `order_type` → 映射到 business_type
> 2. 关联 `jst_order_item` 或 `jst_order_detail` → 聚合
> 3. 实在都没有 → 退化为单维度（只按 region），并在报告里明确标注"因订单主表无 business_type 字段，本指标降级为单维度"

#### 3.3 前端

`echarts` heatmap：
- `xAxis`：业务类型（动态从 dict 取）
- `yAxis`：地区（从返回数据 distinct province）
- `visualMap`：渐变色（0 → 最大 gmv）
- 格子 tooltip：`{region} × {businessType}: ¥{gmv} / {channelCount} 个渠道`

下方附 TOP 10 表格（region + business_type + gmv + channelCount，按 gmv DESC）。

---

## 四、交付物清单

### 后端

**修改**：
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesDashboardController.java`（追加 3 个 `@GetMapping`）
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/AdminSalesDashboardService.java`（追加 3 个接口方法）
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/AdminSalesDashboardServiceImpl.java`（追加 3 个实现方法，用 `@Autowired JdbcTemplate` 或已有 Mapper 风格二选一，**和现有方法保持风格一致**，不要混）
- （如走 Mapper 风格）`RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/AdminSalesDashboardMapperExt.xml` 追加 3 个 select

**禁止修改**：
- ❌ 不许改 `AdminSettlementController` / `SalesCommissionService` 等其他类
- ❌ 不许新建 Controller
- ❌ 不许改数据库 schema（3 指标都是只读聚合）

### 前端

**修改**：
- `RuoYi-Vue/ruoyi-ui/src/api/jst/salesDashboard.js`（追加 3 个 export 函数）
- `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue`（追加 3 个区块，按 "趋势 → 压缩 → 热力" 顺序排列，全部在现有日期筛选下方）

**echarts 引入方式**：沿用项目现有方式。先 grep `import echarts`：
```bash
grep -rn "echarts" RuoYi-Vue/ruoyi-ui/src/views/jst/sales/
```
照最近一个页面的 import 风格抄。**不要新增 `vue-echarts` 等包**。

---

## 五、DoD 验收标准

- [ ] 3 个后端端点加到 `AdminSalesDashboardController`，`mvn -pl jst-channel -am clean compile -DskipTests` ✅
- [ ] SQL 字段名和 `jst_sales_commission_ledger` / `jst_channel` / `jst_order_main` DESCRIBE 结果一致（subagent 必须先 DESCRIBE 贴进报告）
- [ ] 三个端点用 `.http` 或 `curl` 手测各 ≥ 3 个场景（happy / 无数据 / 跨月）
- [ ] `dashboard.vue` 3 个区块渲染，echarts 无 Console Error
- [ ] `npm run build:prod` ✅
- [ ] 有新数据时肉眼验证：趋势折线有数据、压缩卡片数字合理、热力图有色块
- [ ] 没有数据时优雅展示（`EmptyStateCTA` 或 echarts 空态），**不要让图表空白**
- [ ] 报告列出"3 个端点路径 + 3 个 SQL + 3 张截图"
- [ ] commit：`feat(admin): ADMIN-DASHBOARD-METRICS 3 指标（成本趋势/压缩/热力）`

---

## 六、红线（不许做的事）

- ❌ **不许改 `jst_sales_commission_ledger` 表结构**——哪怕缺字段也不动，降级处理
- ❌ 不许用 `SELECT *`，显式列字段
- ❌ 不许在 ServiceImpl 里做"客户端聚合"（把 1000 行读出来 Java 里 groupBy），必须 SQL 聚合
- ❌ 不许脱敏金额（admin 看板按 spec 可见实际金额，脱敏是销售端的事）
- ❌ 不许引入新的 echarts 主题 / 新组件库
- ❌ 不许把指标放进 `/sales/me/dashboard.vue`（那是销售自己看的，有脱敏）
- ❌ 不许跳过 DESCRIBE，凭 spec 脑补字段名

---

## 七、派发附言

如果发现 `compression_amount` / `original_amount` 字段 plan-02 实际没落（被裁剪掉了），**2 种应对**：

1. **优先**：查 `jst_sales_commission_ledger` 的 DDL（`架构设计/ddl/99-migration-sales-distribution-ddl.sql`），找类似含义的字段名（`cap_applied_amount`、`raw_amount` 等）
2. **兜底**：如果数据确实没存压缩量，本指标降级为"只显示触发次数（按 `commission_amount = 实付 * 15%` 近似判断）"，在报告里明确标注"因 ledger 无原始/压缩额字段，指标 2 为近似估算"

**不要自己加字段改表**——数据模型动起来是大事，得另外开卡。

派发时间：2026-04-18 | 主 Agent 签名：竞赛通架构师
