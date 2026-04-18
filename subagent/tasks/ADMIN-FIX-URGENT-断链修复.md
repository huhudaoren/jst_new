# ADMIN-FIX-URGENT — 管理端 P0 断链止血

> 优先级：**P0**（阻塞 admin 日常操作） | 预估：**S**（半天~1 天） | Agent：**Web Admin Agent**
> 派发时间：2026-04-17 | 版本：任务卡 v1

---

## 一、业务背景

用户实际使用管理端时，发现以下 3 个阻塞性问题，影响 admin 日常操作。本卡聚焦"止血"——修复具体断链，**不做重构**，重构留给 B 卡 `ADMIN-UX-B1/B2`。

### 问题 1：Admin 点开某些入口报 403 "无权访问该资源"

**现场**：admin 登录管理端，点 `赛事管理中心 → 证书管理` 或 `快捷操作` 等入口，后端响应 `/dev-api/jst/partner/cert/template/list` 提示 "无权访问该资源"。

**根因**：`RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/controller/BasePartnerController.java` 在类级加了
```java
@PreAuthorize("@ss.hasRole('jst_partner')")
```
导致 `PartnerCertController` / `PartnerContestController` / `PartnerEnrollController` 等所有 partner 工作台接口强制要求 `jst_partner` 角色，admin 没有该角色直接 403。

**涉及 partner 页面**（目前只被 partner 工作台使用，菜单 9700 段）：
- `views/partner/home.vue`
- `views/partner/cert-manage.vue`
- `views/partner/contest-edit.vue`
- `views/partner/enroll-manage.vue`
- `views/partner/score-manage.vue`
- `views/partner/settlement.vue`
- `views/partner/writeoff.vue`
- `views/partner/account-settings.vue`

**处理原则**：admin 有自己独立的管理端（9800 段菜单 + `views/jst/` 下精品页），**不让 admin 误入 partner 页面**。具体做法见 §三-修复 1。

---

### 问题 2：`运营数据看板` → `待审入驻申请` 卡片 / `平台数据管理` → `赛事方入驻管理` 打开后是"入驻申请表单"而非"审核列表"

**现场**：admin 从看板或侧边菜单点进 `待审入驻申请` / `赛事方入驻管理`，看到的是**公开入驻申请表单**（让用户填资料那个），而不是 admin 审核列表。

**根因假设**：
1. 菜单 9814 配置为 `path='partner-apply'` + `component='jst/partner-apply/index'`，正确组件是 `@/views/jst/partner-apply/index.vue`（审核列表，18KB）。
2. `src/router/index.js:33-49` 的公开路由 `/partner-apply` 也存在，它指向 `@/views/partner-apply/index.vue`（入驻表单，17KB）。
3. 两个 URL 分别是 `/jst/partner-apply`（admin）和 `/partner-apply`（public），本不冲突。
4. **但某些入口**（看板卡片 `candidates` / 面包屑跳转 / 错误菜单配置）可能把用户导到了 `/partner-apply` 公开表单。

**排查顺序**：
1. 登录 admin → 侧边栏点 `平台数据管理 → 赛事方入驻管理` → 观察 URL。
2. 看板点 `待审入驻申请` 卡片 → 观察 URL。
3. 查线上 DB `sys_menu` 中 `menu_id=9814` 的 `path/component` 是否与 SQL 文件一致。
4. 查 `views/jst/dashboard/index.vue:225` 的 `candidates` 数组是否命中 `availablePathSet`。

---

### 问题 3：其他已知断链（附带修复）

排查过程中附带修复：
- `src/views/partner/home.vue:156` 的 `证书管理` 跳 `/partner/cert-manage` 正常（partner 角色），但若 admin 误入 partner home 页会 403。
- `views/jst/dashboard/index.vue:217/267` 等 `candidates` 第二项 `/jst/group-user/...` 均不存在，兜底路径应改为具体存在的 admin 路由或删除。

---

## 二、必读上下文

**子 Agent 开工前必读**：
1. `CLAUDE.md` § 三目录结构 + § 五编码规范
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`（完整读）
3. `subagent/tasks/WEB-ADMIN-FIX-路由与菜单修复.md`（历史路由修复卡，了解已有做法）
4. `架构设计/ddl/99-migration-polished-menus.sql`（9800 段菜单注册 SQL）
5. `架构设计/ddl/96-grant-jst-partner-permissions.sql`（partner 角色权限）
6. `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/controller/BasePartnerController.java`（问题 1 根因所在）

**测试数据库（本地）**：
- URL: `jdbc:mysql://127.0.0.1:3306/jst?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8`
- user: `root` / pwd: `123456`
- 库名: `jst`（本地已迁移，不再使用远程 `jst_new`）

---

## 三、修复清单（精确到文件）

### 修复 1 — Admin 不误入 partner 页面（P0）

**策略**：保持 `BasePartnerController` 的角色限制**不变**（partner 数据隔离需要），只处理入口。

**子任务 1.1**：为 partner 页面加路由级 roles 守卫
- 文件：`RuoYi-Vue/ruoyi-ui/src/router/index.js`
- 找到 `dynamicRoutes` 中 `path: '/partner'` 一段（约第 97-100 行及其 children）
- 在 `meta` 中加 `roles: ['jst_partner']`
- 参考 RuoYi 已有 `permission.js` 中的 `hasPermission(roles, route)` 逻辑——如果当前 route meta.roles 不为空且用户角色不命中，则不渲染路由。
- 验收：admin 登录后浏览器直接访 `/partner/home` 会被 permission.js 拦截，重定向到 `/401` 或 home。

**子任务 1.2**：看板候选路径清理
- 文件：`RuoYi-Vue/ruoyi-ui/src/views/jst/dashboard/index.vue`
- `navGroupConfigs` / `todoItemConfigs` / `quickActionConfigs` 中所有 `candidates` 数组：
  - 删除不存在的 `/jst/group-*/...` 占位兜底项（全部是死路径）
  - 保留第一个确认可达的 admin 路径
- 验收：看板卡片 hover 时 targetPath 显示正确；点击后跳转不 404。

**子任务 1.3**：partner home.vue 里的链接排查
- 文件：`RuoYi-Vue/ruoyi-ui/src/views/partner/home.vue`
- 无需改，但需验证：该页只在 `/partner/*` 路径下出现，admin 登录后侧边栏不应显示。
- 若 admin 侧边栏仍看到 partner 工作台入口，查 `sys_menu` 是否误把 9700 段菜单分给了 admin 角色。必要时出一条 SQL 清理 `sys_role_menu`。

---

### 修复 2 — "待审入驻申请" 菜单指向修正（P0）

**子任务 2.1**：线上 DB 核对
- 在测试库执行：
```sql
SELECT menu_id, menu_name, parent_id, path, component, perms
FROM sys_menu
WHERE menu_id IN (9800, 9814)
   OR menu_name LIKE '%入驻%';
```
- 输出结果贴到任务报告，**确认 9814 的 path='partner-apply' + component='jst/partner-apply/index'**。
- 若 DB 中不一致（如 component 为空或指向 `partner-apply/index`），执行 `99-migration-polished-menus.sql` 补齐。

**子任务 2.2**：验证路由解析
- 启动 admin 前端 `npm run dev`，登录 admin
- 浏览器 devtools console：
  ```js
  $router.options.routes.find(r => r.path === '/jst')?.children?.find(c => c.path === 'partner-apply')
  ```
- 确认 component 函数对应 `views/jst/partner-apply/index.vue`。
- 若仍指向公开表单 `views/partner-apply/index.vue`，排查 `store/modules/permission.js` 的 `filterAsyncRouter` 如何根据 component 字符串 resolve 文件，修掉 map 冲突。

**子任务 2.3**：（若确实冲突）改公开路由路径
- 文件：`RuoYi-Vue/ruoyi-ui/src/router/index.js`
- 把 `path: '/partner-apply'` 改成 `path: '/public/partner-apply'`（及其子页 `/public/partner-apply/form`、`/public/partner-apply/status`）
- 小程序端不受影响（不调用这些 URL）
- 同步改 `views/public/*.vue` 或 QR 入口（若有外链）
- 验收：admin 点"待审入驻申请"看到审核列表；public `/public/partner-apply` 仍可匿名访问。

---

### 修复 3 — 看板候选路径死链清理（P1 顺手修）

同子任务 1.2，合并到本卡。全量审计 `views/jst/dashboard/index.vue` 所有 `candidates`、所有 `navGroupConfigs`，验证 `availablePathSet` 命中，输出清理后的 diff。

---

## 四、涉及权限标识

无新增，仅清理 `sys_role_menu` 中误绑定项（若有）。

---

## 五、测试场景

### A-1 admin 登录不能访问 /partner/home
- 步骤：admin 登录 → 手动 URL 输 `/partner/home`
- 期望：跳转 `/401` 或 `/jst/dashboard`，无 401/403 弹框

### A-2 admin 不调用 /jst/partner/* 接口
- 步骤：admin 登录 → 走一遍赛事管理、证书管理、报名管理、看板、快捷操作
- 期望：Network 面板所有请求都是 `/jst/event/*` 或 `/jst/order/*` 等，**不出现** `/jst/partner/*`

### A-3 "待审入驻申请" 打开审核列表
- 步骤：admin 登录 → 看板点"待审入驻申请"卡片；侧边栏点"平台数据管理 → 赛事方入驻管理"
- 期望：两条入口都打开 `views/jst/partner-apply/index.vue`（标题"赛事方入驻审核"，带审核表格+批量通过/驳回按钮）

### A-4 看板候选路径无死链
- 步骤：admin 登录看板 → hover 每个卡片、快捷操作
- 期望：`enabled=true` 的卡片全部可跳转，跳转后无 404

### A-5 Partner 账号不受影响
- 步骤：用 `jst_partner` 角色账号登录
- 期望：partner 工作台菜单（9700 段）全部正常打开，API 调用返回 200

---

## 六、DoD 验收标准

- [ ] 子任务 1.1 / 1.2 / 2.1 / 2.2 / 2.3 / 3 全部完成
- [ ] A-1 ~ A-5 测试场景全部手工走通，截图贴报告
- [ ] `npm run build:prod` 通过
- [ ] 任务报告列出：改动文件清单（含 diff）、DB 查询结果、测试截图
- [ ] commit message：`fix(admin): ADMIN-FIX-URGENT 修复 admin 误入 partner 页面 + 入驻审核菜单路由`

---

## 七、不许做的事

- ❌ **不许**修改 `BasePartnerController` 的 `@PreAuthorize` 限制（partner 数据隔离依赖它）
- ❌ **不许**给 admin 加 `jst_partner` 角色（破坏数据隔离语义）
- ❌ **不许**删除 `views/partner/` 下任何文件（partner 工作台需要）
- ❌ **不许**顺手做 JSON 可视化编辑器（留给 B 卡）
- ❌ **不许**改 partner controller 的 `@PartnerDataScope` 切面
- ❌ **不许**动 `.mcp.json` / `application-*.yml`

---

## 八、交付物

- 改动文件 diff（最多 4-5 个文件）
- DB 查询截图
- 测试截图 5 张（A-1 ~ A-5 各一）
- 任务报告：`subagent/tasks/任务报告/ADMIN-FIX-URGENT-报告.md`
