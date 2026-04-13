# 派发清单 — WEB-ADMIN-FIX 路由与菜单修复

> 1 个 Web Admin Agent（P0 优先级）

---

## Agent: WEB-ADMIN-FIX

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-FIX-路由与菜单修复.md 全部内容）

⭐ 这是 P0 修复任务，当前看板和首页的所有跳转链接都 404。
⭐ 核心工作是生成菜单注册 SQL — 若依的动态路由完全依赖 sys_menu 表。
⭐ 先读 架构设计/ddl/99-migration-admin-menus.sql 了解已有菜单 SQL 格式。
⭐ 先读 架构设计/ddl/99-migration-partner-menus.sql 了解 partner 菜单格式。
⭐ SQL 中 menu_id 使用 9800-9899 段。
⭐ 所有精品页都需要注册，包括 hidden 子页面（edit/detail），否则路由不生效。
⭐ 完成后确认：dashboard 和 index 的每个跳转路径都能在 SQL 中找到对应菜单。
⭐ npm run build:prod 必须通过。
```

---

## 改动范围

| 文件 | 改动 |
|---|---|
| `架构设计/ddl/99-migration-polished-menus.sql` | **新建** — ~50 条精品页菜单注册 |
| `views/jst/dashboard/index.vue` | 修正跳转路径 + 移除 deprecated 引用 |
| `views/index.vue` | 修正跳转路径 |
| `views/error/404.vue`（如存在） | 优化 404 页面体验 |
| 9 个 deprecated 页面 | 修正 alert 中的跳转链接 |

## 执行后注意

**SQL 必须执行**：Agent 完成后会产出一个 SQL 文件，你需要在数据库中执行：
```sql
source 架构设计/ddl/99-migration-polished-menus.sql;
```
执行后**重启后端**，菜单才会生效。
