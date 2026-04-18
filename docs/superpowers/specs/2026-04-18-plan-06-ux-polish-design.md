# Plan-06 精品化方案：管理端 UX 防御性审查 + 业务零门槛化

> **作者**：Claude（与用户协作审查）
> **日期**：2026-04-18
> **状态**：方案阶段，等待用户 review 决定实施范围
> **关联**：继 plan-01~plan-05 之后的精品化轮

---

## 0. 核心理念（用户定调）

- **系统架构师视角**：防御性思维——任何可被用户误操作的地方都要在设计层面堵死
- **交互设计师视角**：极端注重体验——业务人员是**零技术背景**，进系统要立刻知道要干什么
- **铁律一**：凡是 `_id` 结尾的外键字段，**严禁暴露文本框手填**，必须是下拉 / 穿梭框 / 关联弹窗
- **铁律二**：每个页面都要回答业务人员三问——"这页做什么 / 我要怎么开始 / 数据怎么看"
- **铁律三**：admin / 销售 / 销售主管 各自进系统看到的是**适合自己的数据仪表盘**，不是让他们翻列表自己汇总

---

## 1. 现状审计汇总

全仓 `views/jst/` + `views/sales/` + `views/channel/` + `views/event/` + `views/order/` + `views/marketing/` 等扫描，累计 **~140 处问题**（Explore agent 扫描结果）。

| 维度 | 数量 | 典型症状 |
|---|---|---|
| **A. 外键 ID 被暴露为手填文本框** | 56 处跨 26 页 | `<el-input v-model="form.channelId" />` — 业务人员要去哪里查 channelId? |
| **B. TODO/占位/Deprecated 遗留** | 3 处 DEPRECATED + 80+ 空态无 CTA | el-empty 只说"暂无"不给用户下一步 |
| **C. 字典值裸写 / 技术术语** | 7-10 处 | 列名写"参赛人ID快照"、"支付时绑定的渠道方ID"——业务人员看不懂 |
| **D. 无面包屑/无引导/按钮拥挤** | 多处 | 销售管理页 7 个操作按钮挤在一列 |
| **E. 三角色看板缺失** | 仅 admin 有 dashboard，销售个人+主管都没独立看板 | 销售登录首页看到什么？主管看团队业绩要去哪个页？ |

### 最严重 Top 10（按影响面）

1. **`jst/order/jst_order_main/index.vue`** — 8 个 ID 输入框 + 27 个搜索筛选 + 列显示裸 ID
2. **`jst/event/jst_enroll_record/index.vue`** — 6 个 ID 输入框（contestId/userId/participantId/channelId/templateId/orderId）
3. **`jst/event/jst_cert_record/index.vue`** — 5 个 ID 输入框
4. **`jst/order/jst_refund_record/index.vue`** — 3 个 ID 输入框 + 无数据引导
5. **`jst/sales/index.vue`** — 新建销售弹框让 admin 手填 `sysUserId`
6. **`jst/event/jst_score_record/index.vue`** — contestId/enrollId/userId/participantId 全是 input
7. **`jst/channel/jst_rebate_rule/index.vue`** — contestId/channelId 手填
8. **`jst/contest/edit.vue`** — formTemplateId 手填
9. **`jst/event/jst_contest/index.vue`** — partnerId/formTemplateId/createdUserId 手填
10. **`jst/event/jst_course/index.vue`** — creatorId 手填

### Deprecated 遗留（非 SALES-DISTRIBUTION 产生，但严重影响 UX）
- `jst/order/jst_order_main/index.vue` — 含 `DEPRECATED` 标记，redirect 到 `/jst/order/admin-order`
- `jst/order/jst_enroll_record/index.vue` — redirect 到 `/jst/enroll`
- `jst/order/jst_refund_record/index.vue` — redirect 到 `/jst/order/admin-refund`

这些页面长期保留 redirect 占位，但**其中的字段 bug** 被扫描工具误报——实际上它们已废弃，只是没在菜单里删。plan-06 应该顺手彻底删掉。

---

## 2. 方案 A — 外键字段控件改造（解决 56 处 ID 暴露）

### 2.1 核心原则

任何 `form.xxxId` 字段在 UI 层**严禁 `<el-input>`**，必须替换为以下之一：

| 场景 | 控件 | 适用 |
|---|---|---|
| 候选 < 50 条且变化少 | 普通 `<el-select>` | 字典/角色/静态分类 |
| 候选 50~5000 条 | `<el-select filterable remote :remote-method>` | 销售列表、渠道列表、赛事列表 |
| 候选 > 5000 条或要多条件筛选 | **关联弹窗选择器**（自建组件 `<RelationPicker>`） | 学生、订单、历史参赛档案 |
| 树状组织结构 | `<el-tree> / <el-cascader>` | 菜单、部门、分组 |
| 需要多选 + 模糊批量 | `<el-transfer>` 穿梭框 | 角色授权、批量分配 |

### 2.2 需要新建的基础组件 — `<RelationPicker>` 族

放在 `ruoyi-ui/src/components/RelationPicker/`，按**实体类型**拆 7 个组件（SRP 原则）：

| 组件 | 文件 | 查询 API | 用途 |
|---|---|---|---|
| `<ChannelPicker>` | ChannelPicker.vue | `GET /admin/channel/search?kw=` | 渠道选择（56 处中 14 处用它） |
| `<UserPicker>` | UserPicker.vue | `GET /admin/user/search?kw=` | sys_user / jst_user 选择（17 处用它） |
| `<SalesPicker>` | SalesPicker.vue | `GET /admin/sales/search?kw=` | 销售选择（5 处） |
| `<ContestPicker>` | ContestPicker.vue | `GET /admin/contest/search?kw=` | 赛事选择（7 处） |
| `<ParticipantPicker>` | ParticipantPicker.vue | `GET /admin/participant/search?kw=` | 参赛档案选择（6 处） |
| `<PartnerPicker>` | PartnerPicker.vue | `GET /admin/partner/search?kw=` | 赛事方选择（3 处） |
| `<OrderPicker>` | OrderPicker.vue | `GET /admin/order/search?kw=` | 订单选择（4 处） |

### 2.3 组件规范（通用 API）

```vue
<ChannelPicker
  v-model="form.channelId"
  placeholder="请选择渠道（输入名称/编号搜索）"
  :clearable="true"
  :disabled="false"
  :show-detail-link="true"      <!-- 选中后显示"查看详情 →"链接跳转 -->
  @change="onChannelChange"     <!-- 暴露完整对象给父组件 -->
/>
```

**组件内部**：
- 弹层显示候选项：**名称 / 编号 / 状态徽章**，不显示 id 数字（id 仅作为 v-model 的值绑定）
- 支持 `filterable remote` + debounce 400ms 触发 search API
- 首次加载若 v-model 有值，自动 GET `/admin/xxx/{id}/brief` 补出展示文本
- 空结果时给"新建 xxx"跳转按钮（对 channelId / contestId 这类 UGC 实体）
- 禁用态显示纯文本（只读），非禁用态可编辑

### 2.4 后端需要补的 search API（预估）

每个实体加一个**统一格式**的 search 端点：

```java
@GetMapping("/search")
public AjaxResult search(@RequestParam(required = false) String kw,
                         @RequestParam(defaultValue = "20") int limit,
                         @RequestParam(required = false) Long excludeId) {
    // 返回 { id, name, subtitle, statusTag }
    // name: 主展示文本（渠道名/销售名）
    // subtitle: 辅助信息（编号/手机号末 4 位）
    // statusTag: 可选状态徽章
}
```

需新建约 7 个 search Controller / 或改造现有 list Controller 加查询参数。

### 2.5 改造工作量预估

- 新建 7 个 RelationPicker 组件 + 统一 SCSS 样式：~2 人日
- 改造 56 处 el-input 替换为对应 Picker：~3 人日（机械 but 细）
- 后端补 7 个 search API：~1 人日（简单模糊查询）
- **合计 ~6 人日**

### 2.6 渐进替换策略

不必一次全改。按**影响面排序**分 3 轮：

**Round 1（必改，~20 处）**：销售工作台 + admin 销售管理 + 订单主表
**Round 2（应改，~20 处）**：赛事报名 / 证书记录 / 成绩 / 退款
**Round 3（可改，~16 处）**：运营后台查询筛选项

---

## 3. 方案 B — 空态 CTA + Deprecated 清理

### 3.1 空态组件标准化

新建 `<EmptyStateCTA>` 组件（或扩展 `<el-empty>` 样式），每个列表页**必须**传 CTA 按钮：

```vue
<EmptyStateCTA
  v-if="!list.length"
  image="no-data"
  title="还没有销售记录"
  description="创建第一个销售，才能记录业绩"
  primary-text="新建销售"
  @primary-click="openCreateDialog"
  secondary-text="从 sys_user 批量导入"
  @secondary-click="openImportDialog"
/>
```

标准引导文案库（由产品统一给）：
- 销售：*"创建第一个销售，才能记录业绩"*
- 预录入：*"预录入渠道手机号，新渠道注册时自动归属"*
- 跟进：*"记录第一次沟通，让客户关系可追溯"*
- 渠道：*"还没有渠道方入驻，引导运营发链接邀请"*

### 3.2 Deprecated 页面彻底清理

3 个 DEPRECATED 页面（jst_order_main / jst_enroll_record / jst_refund_record）**物理删除**，并：
- sys_menu 中对应菜单 `visible=1` 标记隐藏 或直接 DELETE
- router/permission.js 清理残留路由
- 保证 "不会再有人从任何入口误进入"

---

## 4. 方案 C — 术语友好化 + 技术词过滤

### 4.1 字段别名字典

建立一个 **字段展示名对照表** `ruoyi-ui/src/utils/fieldLabelMap.js`：

```javascript
export const FIELD_LABEL_MAP = {
  channelId: '渠道',
  channelName: '渠道名',
  salesId: '销售',
  salesNo: '销售编号',
  sysUserId: '系统用户',
  partnerId: '赛事方',
  contestId: '赛事',
  participantId: '参赛档案',
  orderId: '订单',
  orderNo: '订单编号',
  // 彻底不允许出现的技术词汇
  'businessType': '业务类型',
  'bindingType': '归属类型',
  'bindSource': '归属来源',
  // 列名从 domain 模型转过来时用这个做翻译
}
```

所有 `<el-table-column label="xxxId">` / `<el-form-item label="xxxId">` 都改用 `:label="$fieldLabel('xxxId')"`。

### 4.2 裸枚举值全面替换字典

Dimension 3 中 7 处硬编码 label/value，改为走 `jst_sales_*` 字典。需要 DDL 补几个字典类型（如 `jst_channel_rebate_strategy`）。

### 4.3 列表列显示 ID 全换 Name

`order/jst_order_main` 等列表列 `prop="channelId"` 不显示数字，统一改：
- Mapper XML LEFT JOIN 名称字段（如 `channel_name`）
- 前端列改 `prop="channelName"` 加跳转链接 `<router-link :to="/jst/channel/detail/{id}">...</router-link>`

影响面：admin 端约 15 个列表页需要补 LEFT JOIN + 列重写。

---

## 5. 方案 D — 导航与操作区规范化

### 5.1 面包屑 + 页头组件

每个页面顶部统一用 `<PageHeader>` 组件：

```vue
<PageHeader
  title="销售管理"
  :breadcrumb="[
    { text: '用户渠道', to: '/group-user' },
    { text: '销售管理' }
  ]"
  help-text="管理全部销售档案、费率、主管层级，申请离职也在此页操作"
  :primary-action="{ text: '新建销售', icon: 'el-icon-plus', handler: openCreate }"
/>
```

- `title`: 大字号显著位置
- `breadcrumb`: 路径（顶部小字）
- `help-text`: 一句话说明这页能做什么（业务人员扫一眼就懂）
- `primary-action`: 主操作按钮（最多 2 个，其他放右上三点菜单）

### 5.2 操作列按钮去拥挤

硬规则：**操作列直接可见按钮 ≤ 3 个**，其余放 `<el-dropdown>` 的"更多"下拉。

例：销售管理页的 7 个操作：
- **直接可见**：改费率 / 设主管 / 查看明细
- **更多**：申请离职 / 立即执行 / 终结过渡 / 删除

### 5.3 表单分区 + 进度条

超过 10 个字段的表单（如销售新建）必须用 `el-steps` 分步，或者 `form-section` 分区：

```
[1. 基本信息] ──→ [2. 归属与权限] ──→ [3. 费率配置] ──→ 提交
```

每步只关心 3-5 个字段。业务人员不会看到"27 个字段一次性展开"。

---

## 6. 方案 E — 三角色仪表看板（核心）

**当前现状**：
- admin：登录直接到"运营数据看板"，OK
- **销售**：登录无仪表盘，直接看"我的渠道"列表 → 要翻列表自己汇总
- **销售主管**：登录只能看"团队管理"列表 → 要点进每个下属单独看

**目标**：每个角色进系统，**第一眼就看到该看的数字** + **今日要做的事**。

### 6.1 销售个人仪表盘（`/sales/me/dashboard` — 已有但弱）

**上方 4 stat-card**（已有，强化）：
- 本月成交订单 / 本月覆盖渠道 / 本月待跟进 / 预录入命中数

**中间左右两栏**：
- 左：**今日待跟进卡片列表**（基于 next_followup_at = today + 任务 due_date = today）
- 右：**本月成交业务类型分布**（饼图，用 echarts）

**下方**：
- 🚨 "提醒事项": 列出需要关注的"超过 14 天未跟进的 VIP 渠道" / "快到期的预录入（剩 7 天内）"

这样销售登录**第一屏**就看到了：业绩状态 / 今天该做什么 / 被忽略的重要渠道。

### 6.2 销售主管仪表盘（新，`/sales/manager/dashboard`）

**上方 4 stat-card**：
- 团队人数（含自己）/ 本月团队订单 / 本月团队 GMV / 本月团队跟进活跃度

**中间**：
- 左：**下属销售业绩排行**（表格 + 条形图，高亮前 3 + 最后 1 名）
- 右：**团队客户类型分布**（VIP / 重点维护 / 新客 比例）

**下方**：
- ⚠️ "团队健康度": 列出"连续 3 周业绩为 0 的销售" / "名下渠道超过 30 天无跟进的"

### 6.3 admin 经营仪表盘（扩展已有 `/jst/sales/dashboard`）

**增加维度**：
- **销售成本趋势**（近 6 月 ledger 总支出折线图）
- **压缩触发比例**（有多少订单触发了单笔分润上限压缩——发现费率配错的信号）
- **渠道分布热力图**（销售名下渠道的业务类型 × 数量）
- **实时业绩排行**（top 10 销售 by 本月提成）

### 6.4 菜单改造

新增菜单项：
- **978500 销售工作台 → 工作台首页**（component=`sales/me/dashboard`）— 默认路由
- **978507 销售主管 → 团队看板**（component=`sales/manager/dashboard`） — 新建

销售/主管登录后默认跳转到自己的 dashboard。

---

## 7. 方案 F — 销售主管 Scope 精细化（plan-05 遗留）

### 问题
当前"业绩明细"页（`sales/manager/settlement/index.vue`）调 `/admin/sales/settlement/list`，返回**全部销售**的月结单。主管应该只看下属的。

### 修复
后端 `AdminSettlementController.list` 加一个可选参数 `scopeSalesIds[]`，默认 admin 为 null 查全量，主管登录时前端自动传入 `SalesService.resolveScopeSalesIds(currentSalesId)` 的结果（含自己+下属）。

后端：
```java
@GetMapping("/list")
public TableDataInfo list(SettlementQueryReqDTO query) {
    // query 可选含 scopeSalesIds: List<Long>
    if (SalesScopeUtils.isManagerRole() && query.getScopeSalesIds() == null) {
        query.setScopeSalesIds(salesService.resolveScopeSalesIds(currentSalesId));
    }
    // 如果 admin，scopeSalesIds 为 null，不过滤
    return getDataTable(settlementService.listForAdmin(query));
}
```

Mapper 增加 `<if test="scopeSalesIds != null">AND sales_id IN (...)</if>`。

前端主管登录时不传 scopeSalesIds，完全走后端判定。

---

## 8. 通用基础设施提案

| 组件/工具 | 路径 | 职责 |
|---|---|---|
| `<RelationPicker>` 族（7 个） | `components/RelationPicker/` | 外键选择器，消除所有 _id 手填 |
| `<PageHeader>` | `components/PageHeader.vue` | 统一页头（面包屑 + 帮助 + 主操作） |
| `<EmptyStateCTA>` | `components/EmptyStateCTA.vue` | 空态 + 引导按钮 |
| `fieldLabelMap.js` | `utils/fieldLabelMap.js` | 技术字段 → 业务名称映射 |
| `<StatCardGroup>` | `components/StatCardGroup.vue` | 仪表盘 4 卡片标准 |
| `Vue.prototype.$fieldLabel()` | `plugins/field-label.js` | 全局别名翻译函数 |

这 6 件基础设施先做好，后续改造才能快。

---

## 9. 分期实施建议

**不建议一次全做**。分 3 期，每期 2-3 人日：

### Phase 1：防御性底线（~3 人日）
必须先做，否则每新建销售都要让 admin 手填 sysUserId，形同找 bug。

- 新建 `<UserPicker>` + `<SalesPicker>` + `<ChannelPicker>`
- 改造销售工作台 + admin 销售管理的 8 处 ID 输入框
- 补 3 个 search API（user/sales/channel）
- 建 `<PageHeader>` + `<EmptyStateCTA>` 基础组件

### Phase 2：三角色看板（~3 人日）
让业务人员登录即有数据读，不翻列表。

- 销售个人 dashboard.vue 强化（今日待跟进 + 业务分布 + 提醒）
- 新建销售主管 dashboard.vue（团队排行 + 健康度）
- admin dashboard 扩展（成本趋势 + 压缩触发 + 热力图）
- 菜单新增 978500 工作台首页 + 978507 团队看板
- 销售/主管登录默认跳自己 dashboard

### Phase 3：全量 UX 扫尾（~5 人日）
把剩下 48 处 ID 暴露、裸字段、表单拥挤、Deprecated 页面清理到位。

- 补 4 个剩余 RelationPicker（Contest/Participant/Partner/Order）
- 改造其余 48 处 _id 输入框
- 术语友好化（fieldLabelMap 全面覆盖）
- Mapper XML LEFT JOIN 补 15 个列表页的名称字段
- 删除 3 个 DEPRECATED 页面 + 清菜单
- 表单分步改造（≥ 10 字段的都分步）

### 里程碑
- 完成 Phase 1 → 关键销售流程业务员可操作
- 完成 Phase 2 → admin/销售/主管 三角色进系统立刻有数据可读
- 完成 Phase 3 → 整个管理端达到"业务人员零技术门槛"可用

---

## 10. 验收标准（业务人员视角）

Phase 3 完成后，用如下场景验证：

| 场景 | 验收 |
|---|---|
| 新来的 admin（不懂技术）要新建一个销售 | 不用问任何人，能在 10 分钟内完成 |
| 销售主管上班第一件事 | 登录 → dashboard → 立刻看到下属排行 → 点进"业绩为 0 的销售" → 派任务 |
| 销售离职流程 | admin 点"申请离职" → 弹窗让选日期 + 接收主管（下拉，不用输 ID）→ 提交 |
| 全系统任意 `_id` 字段 | 不存在一个需要手填数字的场景 |
| 业务人员找不到某功能 | 面包屑 + 帮助文字都能回答，不用找 IT |

---

## 11. 风险与取舍

| 风险 | 缓解 |
|---|---|
| 7 个 search API + 7 个 Picker 组件改造量大 | 分 Phase 1/3 渐进，先做 3 个核心的 |
| 删 Deprecated 页面可能有人书签 | 保留 redirect 半年再物理删 |
| 主管看板数据量大性能问题 | 夜间预计算到 jst_sales_daily_stat 表缓存 |
| 三角色 dashboard 改动涉及菜单 + 默认路由 + 权限 | 改完要重新 admin/销售/主管 各登录跑一次 |

---

## 12. 用户决策（2026-04-18）

1. **✅ Phase 1 + 2 + 3 一次性合并做** —— 不分期，避免反复 review 开销
2. **✅ 三角色看板按推荐方案，加：**
   - **日期范围筛选器**（近 7 日 / 近 30 日 / 本月 / 上月 / 自定义区间）
   - **地区筛选器**（渠道所在省/市）
   - **业务类型筛选**（赛事/课程/商城）
3. **✅ RelationPicker 带"查看详情"跳转链接**
4. **🔴 新增铁律四（关键）**：**所有业务关联字段在展示时**都要显示**业务名称**，且是**可点击跳转详情页**的链接。

---

## 12.5 铁律四详述 — 关联字段展示层改造

### 问题
当前列表表格列 `prop="channelId"` 显示 `#10023` 数字，业务人员完全看不懂。现有 `ID2NAME` 改造覆盖了部分页面，但不全面且不统一。

### 解决方案：`<EntityLink>` 组件

新建 `ruoyi-ui/src/components/EntityLink.vue`（与 `<RelationPicker>` 族互补——**Picker 负责选择，Link 负责展示**）。

```vue
<EntityLink
  entity="channel"
  :id="row.channelId"
  :name="row.channelName"      <!-- 优先用行内 JOIN 出的名称，避免二次查询 -->
  :target="'_blank'"            <!-- 新标签跳转，不打断当前任务 -->
  mode="text"                   <!-- text: 纯链接 / tag: 彩色 tag 链接 / card: 悬浮卡 -->
  :disabled="false"
/>
```

**组件内部**：
- 如果 `name` 已传：直接渲染 `<a class="entity-link">{{ name }}</a>` + 点击触发 `$router.push('/jst/' + entity + '/' + id)`
- 如果 `name` 缺失（旧数据）：先调 `GET /admin/entity-brief?type=channel&id=xxx` 拿名称，再渲染
- hover 显示小 popover 卡片（实体概要：名称 + 状态 + 1-2 条关键属性），点击才跳转详情页
- 权限检查：若当前用户没有该实体详情页的 perms，链接显示为纯文本（不可点击）

### 实体跳转路由映射表（router/permission 里维护）

| entity | 详情路由 | 需要权限点 |
|---|---|---|
| `channel` | `/jst/channel/detail/:id` | `jst:channel:detail` |
| `sales` | `/jst/sales/:id` | `jst:sales:list` |
| `user` | `/jst/user/detail/:id` | `system:user:query` |
| `partner` | `/jst/event/partner/detail/:id` | `jst:partner:detail` |
| `contest` | `/jst/event/contest/detail/:id` | `jst:contest:detail` |
| `participant` | `/jst/participant/detail/:id` | `jst:participant:detail` |
| `order` | `/jst/order/detail/:id` | `jst:order:detail` |
| `settlement` | `/jst/sales/settlement/detail/:id` | `jst:sales:settlement:review` |

### 与 `<RelationPicker>` 的互补关系

| 场景 | 用什么 |
|---|---|
| **编辑态**（form 表单要选一个外键） | `<RelationPicker>`（选中后 v-model=id） |
| **查看态**（列表/详情页展示外键数据） | `<EntityLink>`（展示 name，点击跳详情） |

两者共用同一套后端 API（search / brief）。

### 统一工作量

- `<EntityLink>` 组件：0.5 人日
- 后端补 `/admin/entity-brief` 统一端点：0.5 人日（支持按 type 路由到不同 service）
- 列表页/详情页全量替换 `<span>{{ row.channelId }}</span>` 为 `<EntityLink>`：~2 人日（扫描 ~30+ 处）

**合计新增：3 人日**（并入 Phase 3，不额外分期）

---

## 13. 更新后的总工作量

| Phase | 原估 | 新增 | 合计 |
|---|---|---|---|
| Phase 1 防御性底线 | 3 人日 | — | 3 人日 |
| Phase 2 三角色看板 | 3 人日 | 0.5 人日（看板加日期/地区/业务类型筛选） | 3.5 人日 |
| Phase 3 全量扫尾 + EntityLink | 5 人日 | 3 人日（EntityLink + 跳转 + 30 处替换） | 8 人日 |
| **总计（3 phase 合并一起做）** | 11 人日 | 3.5 人日 | **~14.5 人日** |

---

## 14. 下一步

**方案已 review 通过**，用户确认 3 phase 合并 + 4 个铁律全部实施。

下一步：invoke superpowers:writing-plans 生成完整实施计划，包含：
- 7 个 RelationPicker + 1 个 EntityLink + 1 个 PageHeader + 1 个 EmptyStateCTA 组件创建
- 后端 search API + entity-brief API 补齐
- 销售/主管/admin 3 个 dashboard 含日期/地区/业务类型筛选
- 56 处 `_id` 输入改造 + 30 处列表展示改 EntityLink
- 删 3 个 DEPRECATED 页面 + 清菜单
- 销售主管 settlement scope 精细化（plan-05 遗留）
- 字段别名字典 fieldLabelMap 全覆盖
- 每页加 PageHeader（面包屑 + help-text + primary action）
- 空态 CTA 标准化

---

**End of Design（用户决策版 v2）**
