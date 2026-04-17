# ADMIN-UX-B3 — 赛事上下线回路 + 报名按渠道分层 + 表单/证书预览

> 优先级：**P1** | 预估：**M**（1.5-2 天） | Agent：**Web Admin Agent**（跨栈，含少量后端）
> 派发时间：2026-04-17 | 版本：任务卡 v1
> 依赖：B1（`JstJsonEditor/SchemaPreview` 已就绪）、REFACTOR-3-CONTEST（contest-edit 8-Tab）、REFACTOR-5-CERT（Fabric.js 证书设计器）

---

## 一、业务背景

管理端三处体验欠缺，用户实际使用时反馈：

1. **赛事下线是死胡同**：赛事一旦 offline，状态机没有出口，运营想重新上线必须改库。
2. **报名列表看不到用户所属渠道**：同一赛事下的报名记录无法按「报名用户当前绑定的渠道」聚合查看，运营排查某渠道的获客/质量时只能 Excel 导出。
3. **赛事编辑-报名规则里选完"报名表单"和"证书模板"，看不到长啥样**：下拉只显示模板名，不点进子页面没法确认选对没。

本卡一次性补齐这三处。

---

## 二、必读上下文

1. `CLAUDE.md` § 四（开发阶段）+ § 五编码规范
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`
3. 状态机：`RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/ContestAuditStatus.java`（⚠️ L37 是死胡同的源头）
4. 已有组件：
   - `ruoyi-ui/src/components/JstJsonEditor/SchemaPreview.vue`（B1 产出，表单 schema 预览）
   - `ruoyi-ui/src/views/partner/cert-manage.vue`（REFACTOR-5-CERT 产出，已有 `openDesigner()` 入口）
5. 涉及页面：
   - `ruoyi-ui/src/views/partner/contest-list.vue`（赛事列表，有「下线」按钮）
   - `ruoyi-ui/src/views/partner/contest-edit.vue`（赛事编辑 8-Tab，L204-216 表单选择、L329-342 证书选择）
   - `ruoyi-ui/src/views/jst/enroll/enroll-manage.vue`（管理端报名审核列表，当前按 `enroll_source` 显示「个人/渠道代报名」标签，但无"用户所属渠道"列）
6. 数据表：
   - `jst_user.bound_channel_id`（用户当前绑定渠道，FK→`jst_channel`）
   - `jst_participant.created_by_channel_id`（临时档案归属渠道，FK→`jst_channel`）
   - `jst_enroll_record`（报名记录，含 `user_id` + `participant_id`）
   - `jst_channel.channel_name`（渠道名称）

---

## 三、三条主线

### 主线 A：赛事上下线回路

#### A.1 状态机修正

**文件**：`jst-event/src/main/java/com/ruoyi/jst/event/enums/ContestAuditStatus.java` L37

```java
// Before
ALLOWED.put(OFFLINE, Collections.emptySet());

// After
ALLOWED.put(OFFLINE, EnumSet.of(ONLINE));
```

语义：offline 的赛事可以直接回到 online（不再经过 pending 审核，因为它之前已审核通过过）。

#### A.2 新增后端接口

**文件**：`jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerContestController.java`

仿照现有 `offline` 接口加一个 `online`：

```java
@PutMapping("/{id}/online")
@PreAuthorize("@ss.hasAnyRoles('jst_partner,admin,jst_operator')")
public AjaxResult goOnline(@PathVariable Long id) {
    contestService.transitionAuditStatus(id, ContestAuditStatus.ONLINE, getPartnerIdOrNull());
    return success();
}
```

**注意**：
- partner 调用时走 PartnerScope 校验（只能操作自己的赛事）
- admin/jst_operator 可操作任意赛事
- 如果当前 service 的 transition 方法没有这个签名，可以复用 `updateAuditStatus(id, targetStatus)`，不要擅自新增 service 方法名

#### A.3 前端「重新上线」按钮

**文件**：`ruoyi-ui/src/views/partner/contest-list.vue`

现有「下线」按钮（`canOffline()` 判定 `row.auditStatus === 'online'`）旁边加「重新上线」按钮：

```vue
<el-button
  v-if="row.auditStatus === 'offline'"
  size="mini"
  type="success"
  @click="handleOnline(row)"
>重新上线</el-button>
```

`handleOnline` 走 `$modal.confirm('确认将此赛事重新上线？')` → 调 `PUT /jst/partner/contest/{id}/online` → toast + 刷新列表。

**同时**：如果管理端有独立的赛事管理列表页（不是复用 partner 的），也需要补同样的按钮。Agent 需先审计。

---

### 主线 B：报名按"用户所属渠道"分层

#### B.1 后端 VO 字段扩展

**文件**：`jst-event/src/main/java/com/ruoyi/jst/event/vo/EnrollListVO.java`

追加 2 个字段：

```java
private Long boundChannelId;       // 用户当前绑定渠道 ID（可能来自 user 或 participant）
private String boundChannelName;   // 渠道名称（用于列表直接展示）
```

**不要**改 DO，也不要动 `enroll_source` 相关字段（那是报名来源标记，与"绑定渠道"是两件事）。

#### B.2 Mapper JOIN 改造

**文件**：`jst-event/src/main/resources/mapper/event/JstEnrollRecordMapperExt.xml`

在 `selectAdminList` 的 SQL 里加 3 个 LEFT JOIN：

```sql
LEFT JOIN jst_user u ON u.user_id = er.user_id
LEFT JOIN jst_participant p ON p.participant_id = er.participant_id
LEFT JOIN jst_channel ch ON ch.channel_id = COALESCE(u.bound_channel_id, p.created_by_channel_id)
```

SELECT 列追加：
```sql
ch.channel_id AS boundChannelId,
ch.channel_name AS boundChannelName,
```

**筛选参数**：支持两个新入参
- `boundChannelId`：按渠道精确筛选（`AND COALESCE(u.bound_channel_id, p.created_by_channel_id) = #{boundChannelId}`）
- `hasChannel`：布尔筛选，true=仅有绑定，false=仅无绑定（对应"个人" Tab）

#### B.3 新增"渠道聚合"接口

**文件**：`jst-event/src/main/java/com/ruoyi/jst/event/controller/JstEnrollRecordController.java`（管理端 Controller，非 partner 那个）

新增端点：

```java
@GetMapping("/channel-groups")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_support,jst_analyst')")
public AjaxResult channelGroups(@RequestParam Long contestId) {
    return success(enrollRecordService.groupByBoundChannel(contestId));
}
```

返回结构：

```json
[
  { "channelId": 101, "channelName": "XX教育", "enrollCount": 23 },
  { "channelId": 102, "channelName": "YY机构", "enrollCount": 7 }
]
```

Service 新增 `groupByBoundChannel(Long)` + Mapper 新增 `selectChannelGroupsByContest(Long)`，SQL：

```sql
SELECT
  COALESCE(u.bound_channel_id, p.created_by_channel_id) AS channelId,
  ch.channel_name AS channelName,
  COUNT(*) AS enrollCount
FROM jst_enroll_record er
LEFT JOIN jst_user u ON u.user_id = er.user_id
LEFT JOIN jst_participant p ON p.participant_id = er.participant_id
LEFT JOIN jst_channel ch ON ch.channel_id = COALESCE(u.bound_channel_id, p.created_by_channel_id)
WHERE er.contest_id = #{contestId}
  AND er.del_flag = '0'
  AND COALESCE(u.bound_channel_id, p.created_by_channel_id) IS NOT NULL
GROUP BY channelId, channelName
ORDER BY enrollCount DESC
```

#### B.4 前端三 Tab 改造

**文件**：`ruoyi-ui/src/views/jst/enroll/enroll-manage.vue`

在当前列表上方加 `el-tabs`：

```vue
<el-tabs v-model="activeTab" @tab-click="handleTabChange">
  <el-tab-pane label="全部" name="all" />
  <el-tab-pane label="个人（无绑定渠道）" name="personal" />
  <el-tab-pane label="按渠道查看" name="channel" />
</el-tabs>
```

**Tab 行为**：

- **全部**：现有表格 + 追加「所属渠道」列（`scope.row.boundChannelName || '—'`；点击渠道名跳 `/jst/channel/admin-list?channelId=xxx`）
- **个人**：查询参数 `hasChannel=false`，其余同上（渠道列全是 `—`，可隐藏）
- **按渠道查看**：
  - 先调 `GET /jst/enroll/channel-groups?contestId=xxx` 拿聚合数据
  - 用 `el-table` + `type="expand"` 渲染：一级行 = 渠道（展示 channelName + enrollCount），展开内容 = 该渠道下的报名记录子表（调 `selectAdminList` 带 `boundChannelId` + `contestId` 参数）
  - 子表可复用主表格的列模板（用 slot 或抽成子组件 `<enroll-row-table>`）

**赛事维度**：渠道 Tab 只在"已选中赛事"场景下才有意义（即 URL 带 `contestId`）。如果没选赛事，渠道 Tab 禁用并显示提示「请先在筛选区选择赛事」。

---

### 主线 C：报名表单 + 证书模板预览

#### C.1 报名表单「查看」按钮

**文件**：`ruoyi-ui/src/views/partner/contest-edit.vue` L204-216

现有结构：

```vue
<el-form-item label="报名表单" prop="formTemplateId">
  <div class="inline-btn-group">
    <el-select v-model="form.formTemplateId" ...>...</el-select>
    <el-button @click="showFormTemplateDrawer = true">新建表单</el-button>
  </div>
</el-form-item>
```

加一个「查看」按钮：

```vue
<el-button
  :disabled="!form.formTemplateId"
  icon="el-icon-view"
  @click="previewFormTemplate"
>查看</el-button>
```

**`previewFormTemplate` 方法**：
- 从 `formTemplateOptions` 找到选中项，拿 `schemaJson`（若列表里没有就调 `GET /jst/form-template/{id}` 取完整数据）
- 打开一个新的 `el-dialog`（title="表单预览：{模板名}"，width="680px"，isMobile 时 fullscreen）
- Dialog 内引入 `SchemaPreview` 组件，props 传 `schema`
- 只读，无编辑按钮

#### C.2 证书模板「查看」按钮

**文件**：同上 L329-342

现有结构：多选 el-select + 「新建模板」。

加「查看已选」按钮：

```vue
<el-button
  :disabled="!certTemplateIds || certTemplateIds.length === 0"
  icon="el-icon-view"
  @click="previewCertTemplates"
>查看已选</el-button>
```

**`previewCertTemplates` 方法**：
- 打开一个 `el-dialog`（title="证书模板预览"，width="900px"）
- Dialog 内循环渲染已选模板：
  - 如果模板有缩略图字段（`thumbnailUrl` 之类），优先展示图片 + 模板名
  - 否则展示一个"打开设计器查看"按钮，点击跳转 `/partner/cert-manage?templateId=xxx&readonly=1`（复用现有 cert-manage 页，加 `readonly` query 参数让设计器进入只读模式 —— 如果当前设计器不支持 readonly，本卡可以先做成跳转打开设计器，用户手动看完关闭即可，**不要**为了 readonly 改设计器核心逻辑）

**克制原则**：C.2 预览做到"能看到就行"即可，不要为此改 Fabric.js 设计器。如果缩略图字段不存在，直接做成"跳转到设计器查看"也算达标。

---

## 四、涉及权限 / 菜单

**权限**：
- `PUT /jst/partner/contest/{id}/online`：沿用 `jst:partner:contest:online`（如果不存在，复用 `jst:partner:contest:edit`，不新增权限点避免菜单脏数据）
- `GET /jst/enroll/channel-groups`：沿用 `jst:enroll:list`

**菜单 SQL**：无新增。

**后端 DDL**：无。

---

## 五、测试场景

### B3-A-1 赛事重新上线
1. 找一个 `audit_status='online'` 的赛事 → 点「下线」→ 状态变 `offline`
2. 列表按钮变为「重新上线」
3. 点击「重新上线」→ 确认弹窗 → 调接口 → 刷新后状态回到 `online`
4. 越权场景：非 admin/partner_owner 的用户调接口返回 403

### B3-B-1 报名列表三 Tab
1. 选中一个有报名的赛事，进入报名管理
2. 全部 Tab：看到所属渠道列（部分行有渠道名，部分显 `—`）
3. 个人 Tab：列表里所有行渠道列都是 `—`
4. 渠道 Tab：看到渠道一级行 + 报名数；展开后看到该渠道的报名子表
5. 点击全部 Tab 里的渠道名链接 → 跳到渠道管理页并高亮该渠道

### B3-B-2 渠道绑定数据链路
1. 创建一个用户 A，`bound_channel_id=101`
2. 用户 A 在赛事 X 报名
3. 报名列表赛事 X 的「按渠道查看」Tab 出现渠道 101，展开看到用户 A 的报名
4. 创建一个临时档案 B，`created_by_channel_id=102`，用户为 NULL
5. 档案 B 报名赛事 X
6. 渠道 Tab 出现渠道 102，展开看到档案 B 的报名

### B3-C-1 表单预览
1. 赛事编辑页 - 报名规则 Tab - 选中一个报名表单
2. 点击「查看」→ 弹出 Dialog 显示表单字段列表（字段名/类型/必填）
3. 切换不同表单模板，预览内容随之变化
4. 未选中模板时「查看」按钮置灰

### B3-C-2 证书预览
1. 赛事编辑页 - 证书 Tab - 多选 2 个证书模板
2. 点击「查看已选」→ 弹出 Dialog
3. 如果有缩略图，看到 2 张缩略图 + 模板名
4. 如果没缩略图，看到 2 个「打开设计器」跳转按钮，点击后进入设计器页（可手动关闭返回）

---

## 六、DoD 验收标准

- [ ] 主线 A：状态机改动 + online 接口 + 前端按钮，功能验证通过
- [ ] 主线 B：VO+Mapper+聚合接口+前端三 Tab 全部落地，B3-B-1/B3-B-2 两个测试场景通过
- [ ] 主线 C：表单和证书两个查看按钮均可用，B3-C-1/B3-C-2 测试通过
- [ ] 后端 `mvn clean package -pl jst-event -am -DskipTests` 通过
- [ ] 前端 `npm run build:prod` 通过
- [ ] 无新增 lint 错误、无新增警告
- [ ] 任务报告：`subagent/tasks/任务报告/ADMIN-UX-B3-报告.md`（截图至少 6 张：重新上线前后 / 三 Tab 各一张 / 表单预览 / 证书预览）
- [ ] commit message：`feat(admin): ADMIN-UX-B3 赛事上下线回路 + 报名按渠道分层 + 表单证书预览`

---

## 七、不许做的事

- ❌ **不许**改 `jst_enroll_record` 表结构（不许加 `source_channel_id` 之类新列，渠道信息全部从 user/participant 侧 JOIN 出来）
- ❌ **不许**新增菜单 SQL（权限点复用现有）
- ❌ **不许**改 Fabric.js 证书设计器内部逻辑为 readonly 专门适配（C.2 用跳转兜底即可）
- ❌ **不许**在 SchemaPreview 组件里加任何"编辑"能力（只读预览）
- ❌ **不许**顺手重构 enroll-manage.vue 的其他列或筛选逻辑（保持最小改动）
- ❌ **不许**把 offline→online 接口做成"再审核"（直接 online，因为前置状态已审核过）
- ❌ **不许**在状态机里开其他 transition（比如 offline→draft、approved→draft），只开 offline→online 一条
- ❌ **不许**动 `enroll_source` 字段的现有筛选或展示（那是两件事，别混）

---

## 八、交付物

- 状态机 + 接口 + 前端按钮 diff（主线 A）
- VO + Mapper + Service + Controller + 前端三 Tab diff（主线 B）
- 两处预览按钮 + Dialog + SchemaPreview 接入 diff（主线 C）
- 测试截图（至少 6 张）
- 任务报告：`subagent/tasks/任务报告/ADMIN-UX-B3-报告.md`
