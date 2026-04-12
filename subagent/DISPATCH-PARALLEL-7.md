# 7 Agent 并行派发清单

> 全部可同时派发，无依赖关系

---

## 前端 Agent 1 → FIX-COMPILE（MiniProgram Agent）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\FIX-COMPILE-小程序编译修复.md）

⭐ 这是编译修复卡，不涉及视觉和业务逻辑，只修编译错误和运行时兼容问题。
⭐ 每个问题类型先 grep 全量扫描，确保不遗漏。
⭐ 修完后必须零 Error。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 前端 Agent 2 → UI-REFRESH-2（UI Refresh Agent）

```
你是竞赛通项目的 UI Refresh Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\UI_REFRESH_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\UI-REFRESH-2-交易与个人中心.md）

⭐ 原型是下限不是上限，你的目标是做得比原型更好。
⭐ 必须用 uView 组件 + Design Token，禁止手写硬编码 CSS。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ script 块的业务逻辑一行不动。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 前端 Agent 3 → UI-REFRESH-3（UI Refresh Agent）

```
你是竞赛通项目的 UI Refresh Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\UI_REFRESH_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\UI-REFRESH-3-渠道与预约.md）

⭐ 原型是下限不是上限，你的目标是做得比原型更好。
⭐ 必须用 uView 组件 + Design Token，禁止手写硬编码 CSS。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ script 块的业务逻辑一行不动。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 后端→Web Agent 1 → WEB-ADMIN-1 用户赛事（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-1-用户赛事管理.md）

⭐ 先读已有 views/partner/ 和 views/system/user/ 了解项目风格。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 后端→Web Agent 2 → WEB-ADMIN-2 订单财务（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-2-订单财务管理.md）

⭐ 先读已有 views/partner/ 了解项目风格。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 金额字段右对齐、保留2位小数、负数红色。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 后端→Web Agent 3 → WEB-ADMIN-3 内容营销（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-3-内容营销管理.md）

⭐ 先读已有 views/partner/ 和 views/system/notice/ 了解项目风格。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 富文本编辑器复用若依已有的编辑器组件。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 后端→Web Agent 4 → WEB-ADMIN-4 渠道数据（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md）

【文档 2: 任务卡】
（D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-4-渠道数据系统.md）

⭐ 先读已有 views/partner/ 了解项目风格。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 数据看板不引入新依赖，用纯 CSS + 数字展示（若已有 echarts 可复用）。
⭐ 菜单注册 SQL 必须幂等。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
完成任务以后，交付报告写在 D:\coding\jst_v1\subagent\tasks\1111
```

---

## 进度追踪

| # | Agent | 任务 | 状态 |
|---|-------|------|------|
| 1 | 前端 1 | FIX-COMPILE 编译修复 | ⏳ |
| 2 | 前端 2 | UI-REFRESH-2 交易+个人 23页 | ⏳ |
| 3 | 前端 3 | UI-REFRESH-3 渠道+预约 23页 | ⏳ |
| 4 | Web 1 | WEB-ADMIN-1 用户+赛事管理 | ⏳ |
| 5 | Web 2 | WEB-ADMIN-2 订单+财务管理 | ⏳ |
| 6 | Web 3 | WEB-ADMIN-3 内容+营销管理 | ⏳ |
| 7 | Web 4 | WEB-ADMIN-4 渠道+数据看板 | ⏳ |
