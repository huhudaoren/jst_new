# E2-PA-2 赛事方工作台首页 - 交付报告

## 1. 任务结论

- 已完成赛事方工作台首页前端实现，新增页面：[home.vue](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/partner/home.vue)
- 已补齐任务卡要求的前端 API 占位：[dashboard.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/api/partner/dashboard.js)
- 已补齐赛事方登录后的默认落地页跳转逻辑：[permission.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/permission.js)
- 已补齐仅对 `jst_partner` 生效的本地首页路由注入，避免影响其他角色菜单：[index.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/router/index.js)、[permission.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/store/modules/permission.js)
- 已按任务卡要求输出后端字段缺口反馈文档：[2026-04-10-partner-dashboard-字段需求.md](D:/coding/jst_v1/subagent/ui-feedback/2026-04-10-partner-dashboard-字段需求.md)

## 2. 交付物清单

### 2.1 新增文件

- `RuoYi-Vue/ruoyi-ui/src/views/partner/home.vue`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/dashboard.js`
- `subagent/ui-feedback/2026-04-10-partner-dashboard-字段需求.md`

### 2.2 修改文件

- `RuoYi-Vue/ruoyi-ui/src/router/index.js`
- `RuoYi-Vue/ruoyi-ui/src/store/modules/permission.js`
- `RuoYi-Vue/ruoyi-ui/src/permission.js`

## 3. 实现说明

### 3.1 工作台首页内容

页面已按任务卡落成 4 个主区块：

- Hero KPI 区：待审报名数、本月报名总数、待发成绩数、待领证书数
- 快捷菜单宫格：9 个入口，移动端自动两列
- 待办区：待审报名 Top 5、待发成绩 Top 5
- 平台通知区：最近 3 条公告

同时补了以下交互细节：

- 首页支持手动“刷新数据”
- `合同发票` 入口灰态展示并标记 `F-2`
- 待办项点击后按任务卡要求跳转到对应页面
- 后端接口未落地时，页面展示 warning 空态提示，不直接把页面做成白屏

### 3.2 路由与菜单注入策略

任务卡只要求在 [index.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/router/index.js) 增加 `/partner/home` 路由，但当前 `ruoyi-ui` 的侧边菜单主要依赖后端菜单接口 `getRouters()` 生成。

如果只在本地路由文件增加一条静态路由：

- 路由本身可以访问
- 但不会自动出现在侧边栏菜单

因此本次在 [permission.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/store/modules/permission.js) 额外补了“仅对 `jst_partner` 注入本地首页路由”的逻辑：

- 只给 `jst_partner` 注入 `partnerHomeRoute`
- 注入到 `rewriteRoutes` 和 `sidebarRoutes`
- 不改动其他角色菜单树

这个改动是为了满足任务卡“路由 + 菜单配置”这部分 DoD，并且遵守“不动其他角色菜单配置”。

### 3.3 登录默认跳转

在 [permission.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/permission.js) 增加了伙伴角色默认落地页逻辑：

- `roles` 包含 `jst_partner`
- 且当前落点为 `/` 或 `/index`
- 自动跳转到 `/partner/home`

保留行为：

- 如果登录前已经带了明确 `redirect` 目标，不会强行覆盖成 `/partner/home`
- 非 `jst_partner` 角色仍保持原逻辑

### 3.4 后端接口缺口

按任务卡要求已检查后端仓库，当前未发现以下接口的已实现版本：

- `GET /jst/partner/dashboard/summary`
- `GET /jst/partner/dashboard/todo`
- `GET /jst/partner/notice/recent`

因此已输出字段需求反馈文档：

- [2026-04-10-partner-dashboard-字段需求.md](D:/coding/jst_v1/subagent/ui-feedback/2026-04-10-partner-dashboard-字段需求.md)

文档中已明确给出 3 个接口的建议返回结构和前端当前接线字段名。

## 4. 验证结果

### 4.1 构建验证

执行命令：

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
npm run build:prod
```

过程结论：

- 第一次构建失败：我新增页面里的深度选择器使用了旧写法 `/deep/`，已修正为 `::v-deep`
- 第二次构建继续执行后，阻塞在仓库现有基线问题，不是本任务新增问题

当前构建失败点：

- `@/views/partner-apply/form`
- `@/views/partner-apply/status`

对应路由位于 [index.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/router/index.js)，但当前仓库仅存在：

- [index.vue](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/partner-apply/index.vue)

所以本次不能给出“全量前端 build success”的结果。现阶段能确认的是：

- 本任务新增的 `/deep/` 样式问题已修复
- 全量 build 继续被既有 `partner-apply` 缺失页面阻塞

## 5. 风险与待联调事项

- 后端首页接口尚未落地，当前首页只能展示空态和前端壳子
- 快捷菜单目标页大多依赖后续赛事方工作台子任务，当前点击后若目标页未开发，会进入 404 或空白页
- 若后端最终返回字段名与反馈文档不一致，前端仍需补一轮字段映射兼容

## 6. 本任务之外的最小必要处理

- 因原始路由文件存在编码混杂，新增中文标题时容易出现字符串损坏；本次顺手将 [index.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/router/index.js) 整理为规范 UTF-8 文本，但没有改变既有路由行为
- 为满足“菜单可见”这一 DoD，额外在权限路由生成阶段注入了伙伴首页本地路由；这是实现任务目标所需的最小补充，不属于越界开发
