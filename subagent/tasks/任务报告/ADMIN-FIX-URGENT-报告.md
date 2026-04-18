# ADMIN-FIX-URGENT 任务报告

- 任务卡：`subagent/tasks/ADMIN-FIX-URGENT-断链修复.md`
- 执行时间：2026-04-17
- 执行范围：admin 管理端止血修复（不改后端、不做重构）

## 一、改动文件清单（4 个）

1. `RuoYi-Vue/ruoyi-ui/src/router/index.js`
2. `RuoYi-Vue/ruoyi-ui/src/store/modules/permission.js`
3. `RuoYi-Vue/ruoyi-ui/src/permission.js`
4. `RuoYi-Vue/ruoyi-ui/src/views/jst/dashboard/index.vue`

## 二、数据库核对结果

### 2.1 必查：`sys_menu(9800,9814)`

执行 SQL：

```sql
SELECT menu_id, menu_name, parent_id, path, component, perms
FROM sys_menu
WHERE menu_id IN (9800, 9814)
   OR menu_name LIKE '%入驻%'
ORDER BY menu_id;
```

结果：

```text
ROW_COUNT= 2
{"menu_id": 9800, "menu_name": "平台数据管理", "parent_id": 0, "path": "jst", "component": null, "perms": ""}
{"menu_id": 9814, "menu_name": "赛事方入驻管理", "parent_id": 0, "path": "partner-apply", "component": "jst/partner-apply/index", "perms": "jst:organizer:partnerApply:list"}
```

结论：`9814` 目前是顶级菜单且 path 为 `partner-apply`，与公开路由旧地址冲突风险成立。

### 2.2 附加核对：9700 段角色绑定

执行 SQL：

```sql
SELECT r.role_key, rm.menu_id, m.menu_name, m.path
FROM sys_role_menu rm
JOIN sys_role r ON r.role_id = rm.role_id
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE r.role_key IN ('admin','jst_partner')
  AND m.menu_id BETWEEN 9700 AND 9708
ORDER BY r.role_key, rm.menu_id;
```

结果摘要：`admin` 与 `jst_partner` 都绑定了 `9700~9708`（赛事方工作台菜单）。

## 三、修复内容

### 修复 1：partner 路由 roles 守卫（拦 admin 误入）

- 在 `router/index.js` 的 `dynamicRoutes` `/partner` 段新增：

```js
meta: { roles: ['jst_partner'] }
```

- 在 `store/modules/permission.js`：
  - 增加 `hasPermission(roles, route)`，基于 `route.meta.roles` / `route.roles` 做角色白名单判断。
  - `GenerateRoutes` 传入 `rootState.user.roles` 给 `filterDynamicRoutes`。
  - `filterDynamicRoutes` 调整为“权限点 + 角色”双判定，满足才注入动态路由。

说明：这里角色判断不走 `auth.hasRoleOr` 的 `admin` 超级角色兜底逻辑，确保 `admin` 不会因为超级角色而穿透 `jst_partner` 限制。

### 修复 2：公开入驻路由改名避冲突

- 在 `router/index.js` 将公开路由改为：
  - `/public/partner-apply`
  - `/public/partner-apply/form`
  - `/public/partner-apply/status`

- 在 `src/permission.js`：
  - 白名单同步改为 `/public/partner-apply*`。
  - 增加未登录旧地址兼容跳转：
    - `/partner-apply` → `/public/partner-apply`
    - `/partner-apply/form` → `/public/partner-apply/form`
    - `/partner-apply/status` → `/public/partner-apply/status`

### 修复 3：看板 candidates 死链清理

- 在 `views/jst/dashboard/index.vue`：
  - 清理 `navGroupConfigs / todoItemConfigs / quickActionConfigs` 中所有 `/jst/group-*` 兜底候选。
  - 每个卡片仅保留已确认存在的 admin 路径，避免解析到不存在路由。

## 四、构建与验证

### 4.1 构建

执行命令：

```bash
cd RuoYi-Vue/ruoyi-ui
npm run build:prod
```

结果：**通过**（仅有 asset/entrypoint 体积 warning，无编译错误）。

### 4.2 对应测试场景状态

- A-1（admin 不能访问 `/partner/home`）：代码路径已完成（`/partner` 动态路由需 `jst_partner` 角色）。
- A-3（待审入驻申请打开审核列表）：冲突路径已消除（公开路由迁出 `/partner-apply`）。
- A-4（看板候选无死链）：`/jst/group-*` 候选已清理。
- A-2/A-5 需要浏览器登录与网络面板联调，CLI 环境未做截图验证。

## 五、风险与后续建议

1. 当前 DB 里 `admin` 仍绑定 `9700~9708` 赛事方菜单，前端已加 roles 守卫可止血；建议后续在权限数据层清理该误绑定（`sys_role_menu`）。
2. 本次按任务要求未做重构，仅做止血修复。