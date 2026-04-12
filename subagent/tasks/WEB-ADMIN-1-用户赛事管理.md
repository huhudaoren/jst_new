# 任务卡 WEB-ADMIN-1 - 用户与赛事管理页面

## 阶段
E-4-WEB / **ruoyi-ui**（Web Admin Agent）

## 背景
管理后台需要平台运营人员管理用户和赛事。后端 API 已全部就绪（`api/jst/user/` + `api/jst/event/`），只需写前端页面。

## 系统提示词
使用 `subagent/WEB_ADMIN_AGENT_PROMPT.md`

## 必读
1. `CLAUDE.md`
2. `RuoYi-Vue/ruoyi-ui/src/views/partner/` — 已有赛事方页面风格参考
3. `RuoYi-Vue/ruoyi-ui/src/api/jst/` — 已有 API 封装
4. 若依原生 `views/system/user/` — CRUD 页面样板

## 交付物

### 页面清单（新建 `views/jst/` 目录）

#### 1. 用户管理 `views/jst/user/`
- **index.vue** — 用户列表页
  - 搜索：nickname / mobile / userType / 注册时间范围
  - 表格：userId / nickname / mobileMasked / userType / boundChannelId / createTime / 操作
  - 操作：查看详情 / 禁用启用
  - API：`api/jst/user/` 下已有 `listJstUsers` / `getJstUser` / `updateJstUser`

- **detail.vue** — 用户详情页（弹窗或抽屉）
  - 基本信息 + 参赛档案列表 + 绑定关系 + 积分记录
  - 只读展示，不可编辑敏感字段

#### 2. 参赛档案管理 `views/jst/participant/`
- **index.vue** — 档案列表
  - 搜索：name / mobile / claimStatus
  - 表格：participantId / name / age / school / claimStatus / claimedByUser / channelName
  - 操作：手动认领 / 撤销认领 / 查看详情
  - API：`api/jst/user/` 下已有

#### 3. 赛事管理 `views/jst/contest/`
- **index.vue** — 赛事列表
  - 搜索：contestName / category / auditStatus / status / partnerName
  - 表格：contestId / contestName / category / partnerName / price / auditStatus / status / enrollCount / createTime
  - 操作：审核（通过/驳回）/ 上线 / 下线 / 查看详情
  - API：`api/jst/event/` 下已有 admin CRUD

- **edit.vue** — 赛事编辑（新页面或大弹窗，admin 审核+编辑用）
  - 基本信息 Tab：赛事名称 / 分类 / 组别 / 封面 / 价格 / 报名时间 / 比赛时间 / 说明（富文本）
  - **赛程安排 Tab**：可视化编辑 `schedule_json`（表格形式：阶段名+开始时间+结束时间+描述，支持增删行）
  - **奖项设置 Tab**：可视化编辑 `awards_json`（表格形式：奖项名+等级+描述+名额，支持增删行）
  - **常见问题 Tab**：可视化编辑 `faq_json`（表格形式：问题+答案，支持增删行）
  - **标签 Tab**：编辑 `recommend_tags`（逗号分隔输入或 Tag 组件，可增删标签）
  - 底部保存按钮调 `updateContest` API
  - ⚠️ 这 4 个 JSON 字段是小程序赛事详情页的核心内容，必须支持可视化编辑，不能只做 textarea JSON

- **detail.vue** — 赛事只读详情（弹窗或抽屉，快速查看用）
  - 基本信息 + 报名统计 + 赛程/奖项/FAQ（JSON 渲染展示）

#### 4. 报名管理 `views/jst/enroll/`
- **index.vue** — 报名记录列表
  - 搜索：contestName / participantName / auditStatus / enrollTime
  - 表格：enrollId / contestName / participantName / auditStatus / createTime
  - 操作：审核（通过/驳回）/ 查看详情 / 查看表单快照
  - API：`api/jst/event/` 下已有

#### 5. 赛事方入驻管理 `views/jst/partner-apply/`
- **index.vue** — 入驻申请列表
  - 搜索：applyName / status
  - 表格：applyId / orgName / contactName / contactMobile / status / submitTime
  - 操作：审核通过 / 驳回
  - API：`api/jst/organizer/` 下已有

### 路由注册

在 `99-migration-partner-menus.sql` 之外，补充管理端菜单 SQL（menu_id 从 9800 开始），或使用若依管理后台动态添加菜单。

**建议**：先把页面写好，菜单 SQL 单独出（或在报告中给出 SQL）。路由用若依 `getRouters()` 动态加载。

### 开发规范
- 表格用 `el-table` + `<pagination>`
- 搜索用 `el-form :inline="true"`
- 弹窗用 `el-dialog` 或 `el-drawer`
- 权限控制用 `v-hasPermi`
- 响应式：手机端表格横向滚动 + 弹窗全屏（已有 responsive.scss）
- 复用 `JstStatusBadge` 组件展示状态

## DoD
- [ ] 5 个管理模块（用户/档案/赛事/报名/入驻）页面完成
- [ ] 每个列表有搜索+分页+操作
- [ ] 状态展示使用 `JstStatusBadge`
- [ ] `npm run build:prod` SUCCESS
- [ ] 任务报告 `WEB-ADMIN-1-回答.md`

## 不许做
- ❌ 不许改后端 Java 代码
- ❌ 不许改小程序（jst-uniapp）
- ❌ 不许引入新依赖

## 优先级：P1
---
派发时间：2026-04-12
