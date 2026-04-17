# ADMIN-UX-B3 任务报告

> 交付日期：2026-04-17
> 承接：前一轮 Agent 在 Step 4 执行过程中超时中断。本次仅补齐缺失代码，不重做 Plan。
> 范围：主线 A（赛事离线→重新上线） + 主线 B（报名管理「按渠道查看」Tab + 所属渠道列） + 主线 C（赛事编辑「查看」已选表单模板 / 证书模板）

## 一、执行摘要

### 主线 A · 赛事重新上线
后端 `ContestAuditStatus.OFFLINE → ONLINE` 状态机 + `PartnerContestController.goOnline` 端点已由前一轮预置；本次补齐前端 API（`onlinePartnerContest`）与 `views/partner/contest-list.vue` 中的「重新上线」按钮（table + mobile 卡片两处），仅对 `auditStatus === 'offline'` 行显示，confirm → PUT → 成功刷新列表。

### 主线 B · 报名管理按渠道查看
后端 `EnrollQueryReqDTO.boundChannelId/hasChannel`、`EnrollListVO.boundChannelId/boundChannelName`、`EnrollChannelGroupVO`、`EnrollRecordMapperExt.xml` 的 JOIN 与 `selectChannelGroupsByContest` SQL 已由前一轮预置。本次补齐：
- Mapper 接口 `selectChannelGroupsByContest(contestId)`
- Service 接口 + 实现 `groupByBoundChannel(contestId)`
- Controller `GET /jst/event/enroll/channel-groups?contestId=` 复用 `jst:event:enrollRecord:list` 权限
- 前端 API `listEnrollChannelGroups(contestId)`
- `views/jst/enroll/index.vue`：
  - el-tabs 全部 / 个人 / 按渠道 三 Tab
  - 搜索区新增「赛事ID」input（el-input-number）
  - 主表新增「所属渠道」列（有渠道蓝链跳 `/channel?channelId=`，无则 —）
  - 按渠道 Tab 独立 el-table：`type="expand"` 一级行（channelId / channelName / enrollCount），展开懒加载本渠道报名明细（复用 `listAdminEnrolls` + `boundChannelId` + `pageSize=100`）
  - mobile 卡片同步增加所属渠道行

### 主线 C · 赛事编辑查看已选
`views/partner/contest-edit.vue` 的报名规则 Tab 与证书 Tab 分别追加「查看」/「查看已选」按钮：
- 表单模板：优先从 `formTemplateOptions` 读 `schemaJson`，若缺则调 `getJst_enroll_form_template(id)`；JSON parse 后挂入复用的 `SchemaPreview` 组件（drawer，prop `visible` + `schema`，emit `close`）
- 证书模板：从 `certTemplateOptions` 按 id 取对象列表，el-dialog 网格展示底图 + 名称；未配置底图时兜底「打开设计器查看」按钮跳 `/partner/cert-manage`（严守不做可视化编辑的红线）

---

## 二、DoD 清单

| # | DoD 条目 | 完成情况 | 证据 |
|---|---|---|---|
| 1 | 赛事列表 offline 行显示「重新上线」按钮 | ✅ | `contest-list.vue` mobile 卡片 L78-82 + table L117-121 新增 `v-if="canOnline(row)"` |
| 2 | 点击「重新上线」调 PUT `/jst/partner/contest/{id}/online`，成功刷新列表 | ✅ | `handleOnline` async/await + confirm + msgSuccess + getList |
| 3 | 报名管理新增全部/个人/按渠道三 Tab 可切换 | ✅ | `enroll/index.vue` `<el-tabs v-model="activeTab">` + `handleTabChange` |
| 4 | 全部 Tab 清空 channel 过滤，个人 Tab 设 `hasChannel=false`，按渠道 Tab 要求 contestId | ✅ | `handleTabChange` 三分支 |
| 5 | 主表显示「所属渠道」列，有值时可点击跳转 | ✅ | `el-link type="primary" @click="goChannel"` |
| 6 | 按渠道 Tab 一级行展开显示该渠道报名明细 | ✅ | `el-table type="expand"` + `@expand-change="onChannelExpand"` 懒加载 |
| 7 | 搜索区加赛事 ID 筛选 | ✅ | `el-input-number v-model="queryParams.contestId"` |
| 8 | 赛事编辑表单模板行「查看」按钮 | ✅ | contest-edit.vue 报名规则 Tab `previewFormTemplate` |
| 9 | 赛事编辑证书模板行「查看已选」按钮 | ✅ | contest-edit.vue 证书 Tab `previewCertTemplates` |
| 10 | 不改 cert-manage 为 readonly，只跳转兜底 | ✅ | 对话框内「打开设计器查看」按钮跳 `/partner/cert-manage` |
| 11 | 不在 SchemaPreview 里加编辑 | ✅ | 原 drawer 未改动，只读复用 |
| 12 | mvn compile 绿 | ✅ | `jst-event` SUCCESS，247 源文件编译通过 |
| 13 | npm run build:prod 绿 | ✅ | `DONE Build complete. The dist directory is ready to be deployed.` |

---

## 三、Diff 文件清单

| # | 文件 | 关键改动 |
|---|---|---|
| 1 | `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/EnrollRecordMapperExt.java` | import `EnrollChannelGroupVO`；新增 `selectChannelGroupsByContest(@Param("contestId") Long)` |
| 2 | `.../service/EnrollRecordService.java` | import `EnrollChannelGroupVO`；新增 `groupByBoundChannel(Long)` 接口 |
| 3 | `.../service/impl/EnrollRecordServiceImpl.java` | import `EnrollChannelGroupVO`；新增 `groupByBoundChannel` 方法（直转 Mapper，未加 partner 权限过滤，因 endpoint 仅开给 admin/运营） |
| 4 | `.../controller/EnrollRecordController.java` | import `EnrollChannelGroupVO` + `RequestParam`；新增 `GET /channel-groups` 端点，复用 `jst:event:enrollRecord:list` 权限 |
| 5 | `ruoyi-ui/src/api/partner/contest.js` | 新增 `onlinePartnerContest(id)` → PUT `/jst/partner/contest/{id}/online` |
| 6 | `ruoyi-ui/src/api/jst/event/admin-enroll.js` | 新增 `listEnrollChannelGroups(contestId)` → GET `/jst/event/enroll/channel-groups` |
| 7 | `ruoyi-ui/src/views/partner/contest-list.vue` | import `onlinePartnerContest`；mobile + table 增「重新上线」按钮；新增 `canOnline(row)` + `handleOnline(row)` |
| 8 | `ruoyi-ui/src/views/jst/enroll/index.vue` | import `listEnrollChannelGroups`；`<el-tabs>` 三 Tab；搜索区赛事 ID；主表「所属渠道」列；mobile 卡片同字段；按渠道 `el-table type="expand"` + 懒加载；script 追加 `activeTab/handleTabChange/loadChannelGroups/onChannelExpand/goChannel/channelKey` 与对应 data 字段；style 追加 `.enroll-tabs/.channel-group-table/.channel-sub-wrap/.contest-id-input` |
| 9 | `ruoyi-ui/src/views/partner/contest-edit.vue` | import `getJst_enroll_form_template` + `SchemaPreview`；组件注册；报名规则 Tab + 证书 Tab 各追加「查看」按钮；挂载 `<SchemaPreview>` drawer + `<el-dialog>` 证书预览；data 新增四个 preview 字段；methods 追加 `previewFormTemplate/parseSchema/previewCertTemplates`；style 追加 cert 预览网格/卡片/占位样式 |

**文件总数：9（Backend 4 + Frontend 5）**，与 Plan 一致。

---

## 四、偏离说明

### 偏离 1：channel-groups 的 partner 维度过滤
Plan 文字说 Controller 复用 `jst:event:enrollRecord:list`；本次未对 partnerId 做额外过滤，因为：
- `jst:event:enrollRecord:list` 在当前角色体系下仅 `admin` / `jst_operator` / `jst_analyst` / `jst_support` 有
- `jst_partner` 不在此权限点范围内（赛事方的报名审核走 `/jst/partner/enroll/*` 链路）
- 因此渠道聚合不会被赛事方意外访问到，`selectChannelGroupsByContest` 只按 `contestId` 过滤即可

如后续要开放给 partner，需要在 service 中按 `isPartnerUser()` 做 partner_id 断言（与 `getAdminDetail` 同模式）。

### 偏离 2：SchemaPreview 组件实际是 Drawer 而非 Dialog
Plan 中示例挂载代码写的是 `<SchemaPreview :visible.sync=...>`；实际组件 `ruoyi-ui/src/components/JstJsonEditor/SchemaPreview.vue` 使用 `prop visible + emit close`（模拟 sync），且组件内部是 `el-drawer`（标题「模拟学生填表预览」）。本次按组件实际接口调用：

```html
<SchemaPreview
  v-if="schemaPreviewVisible"
  :visible="schemaPreviewVisible"
  :schema="schemaPreviewData"
  @close="schemaPreviewVisible = false"
/>
```

视觉上是右侧滑出的 drawer 预览（与 B1 报告一致），满足「可视化展示表单结构」诉求。

### 偏离 3：goChannel 跳转路径
Plan 指定 `/jst/channel/admin-list?channelId=xxx`，但在菜单 SQL 中 `jst/channel/index` 组件实际挂载路径有两个：
- `/channel`（父菜单 9800，menu 9845）
- `/channel-list`（父菜单 9900，menu 9902，已隐藏）

两者都不是 `/jst/channel/admin-list`。本次采用 `/channel?channelId=xxx`，并在 `catch` 兜底尝试 `/channel-list?channelId=xxx`，避免路由 404。目标页当前未必支持 `query.channelId` 预筛选，但至少不会报错，用户可在目标页手动筛选渠道名。

**风险**：未来若菜单 SQL 再次调整使 `/channel` 被隐藏，跳转会失败。建议在后续迭代为 `jst/channel/index` 显式支持 `$route.query.channelId` 自动填充 queryParams 并 getList（非本次 B3 范围）。

---

## 五、5 个测试场景验证路径

### 场景 1：离线赛事重新上线
1. admin 登录 → 「平台数据管理 > 赛事管理」或 partner 登录 → 「赛事管理中心 > 赛事管理」
2. 找到一行 `auditStatus = offline` 的赛事（SQL: `UPDATE jst_contest SET audit_status='offline' WHERE contest_id=8201 LIMIT 1`）
3. 点「重新上线」→ confirm 弹窗 → 确认
4. 预期：toast「已重新上线」，列表自动刷新，该行 status → `online`

### 场景 2：按渠道查看 Tab 展开
1. 进入「报名管理」页面
2. 切到「按渠道查看」Tab → 若未填赛事 ID 会提示
3. 在搜索区输入赛事 ID（例如 8201）→ 自动加载渠道分组列表
4. 点击一级行前的展开箭头 → 懒加载该渠道下的报名明细
5. 预期：一级行显示 channelId/channelName/enrollCount，展开后是子表（报名 ID/参赛人/审核状态/时间/详情按钮）

### 场景 3：按渠道查看 无渠道（自然流量）分组
SQL 准备：`SELECT bound_channel_id, count(*) FROM jst_user u JOIN jst_enroll_record e ON e.user_id=u.user_id WHERE e.contest_id=8201 GROUP BY bound_channel_id;`
- 若存在 bound_channel_id IS NULL 的行，按渠道 Tab 会出现 channelId=null 的一级行显示「无渠道（自然流量）」，展开正常

### 场景 4：主表所属渠道列 跳转
1. 全部 Tab 下，表格新增「所属渠道」列
2. 对有渠道的报名点击蓝色链接 → 跳转 `/channel?channelId=xxx`
3. 预期：落地到渠道列表页（目标页是否预筛选取决于目标页实现）

### 场景 5：赛事编辑 查看已选模板
1. 进入「赛事管理 → 编辑」某个赛事（已选报名表单 + 证书模板）
2. 报名规则 Tab → 报名表单行点「查看」→ 右侧滑出 drawer 显示表单字段（模拟学生填表）
3. 证书 Tab → 证书模板行点「查看已选」→ 弹出对话框展示每个已选模板的底图 + 名称
4. 若模板无底图 → 占位框 + 「打开设计器查看」按钮（点击跳 `/partner/cert-manage`）

---

## 六、Build 证据（最后 15 行）

### Backend mvn clean compile
```
[INFO] --- compiler:3.13.0:compile (default-compile) @ jst-event ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 247 source files with javac [debug parameters target 17] to target\classes
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for ruoyi 3.9.2:
[INFO]
[INFO] ruoyi .............................................. SUCCESS [  0.277 s]
[INFO] ruoyi-common ....................................... SUCCESS [  7.484 s]
[INFO] jst-common ......................................... SUCCESS [  3.497 s]
[INFO] jst-order .......................................... SUCCESS [  3.243 s]
[INFO] jst-event .......................................... SUCCESS [  3.824 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  19.081 s
```

### Frontend npm run build:prod
```
  dist\static\css\chunk-06c73ce6.9e8148b    0.23 KiB         0.14 KiB
  dist\static\css\chunk-352ef607.061f393    0.23 KiB         0.14 KiB
  dist\static\css\chunk-df3cdf0e.d4a0311    0.23 KiB         0.14 KiB
  dist\static\css\chunk-18d31268.352d08b    0.04 KiB         0.06 KiB
  dist\static\css\chunk-1e833158.352d08b    0.04 KiB         0.06 KiB

  Images and other types of assets omitted.

 DONE  Build complete. The dist directory is ready to be deployed.
 INFO  Check out deployment instructions at https://cli.vuejs.org/guide/deployment.html
```

---

## 七、已知风险

| # | 风险 | 缓解 |
|---|---|---|
| 1 | `goChannel` 跳转路径是兜底拼装（`/channel` + `catch /channel-list`），目标页未必支持 `query.channelId` 预筛选 | 后续可在 `jst/channel/index.vue` created hook 读 `$route.query.channelId` 自动填入搜索并 getList |
| 2 | 按渠道 Tab 一级行展开后的子表没有分页（pageSize=100 硬上限） | 实际单赛事单渠道报名数超过 100 概率极低；如发生，用户可到「全部 Tab + boundChannelId 筛选」查看完整列表（需手工在 URL 追加查询，或后续开放渠道筛选器） |
| 3 | `channel-groups` 端点当前未对 partner 做数据隔离 | 权限点 `jst:event:enrollRecord:list` 在当前角色配置下未绑定到 `jst_partner`，因此不会被误用；如权限点后续被开放，需在 service 加 `assertEnrollPartnerOwnership(contestId)` 拦截 |
| 4 | `SchemaPreview` 只能解析 `{ fields: [...] }` 或 `[...]` 结构，若后端返回 `{ sections: [...] }` 嵌套结构会空展示 | 与 B1 现有预览逻辑一致，非本次新增风险 |
| 5 | 「重新上线」未加二次权限判断（当前仅依赖后端 `hasRole('jst_partner')` 或 admin） | 如果想让 jst_operator 也能帮 partner 重新上线，需要在 `PartnerContestController.goOnline` 类级 @PreAuthorize 加 admin/jst_operator（类比 FIX-PARTNER-SCOPE-READONLY） |
| 6 | 证书预览占位跳转 `/partner/cert-manage` 无 templateId 参数 | 目标页是 Fabric.js 拖拽设计器，进入后用户需手动选择模板；若想直达需目标页支持 `?templateId=` 自动 loadLayout（不在本次 B3 范围） |

---

## 八、未动清单（红线严守）

- ❌ `jst_enroll_record` 表结构未改
- ❌ 未新增菜单 SQL
- ❌ 未改 `cert-manage.vue` 为 readonly
- ❌ 未在 `SchemaPreview` 里加编辑功能
- ❌ 未顺手重构 `enroll/index.vue` 其他列（审核/驳回/详情按钮一字未动）
- ❌ 未顺手改其他文件（严格限定在 9 个 Plan 文件）
