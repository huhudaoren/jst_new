# 功能补全任务派发清单

> 按顺序派发，每波内可并行。复制对应区块给 Agent 即可。

---

## 第 1 波（并行 4 卡）

### → Backend Agent 1：FEAT-CONTEST-DETAIL-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-CONTEST-DETAIL-BE.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

### → Backend Agent 2：FEAT-COURSE-DETAIL-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-COURSE-DETAIL-BE.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

### → Backend Agent 3：FEAT-INDEX-ENRICH-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-INDEX-ENRICH-BE.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

### → MiniProgram Agent 1：FEAT-MY-ENROLL-FIX-FE（纯前端，无依赖）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-MY-ENROLL-FIX-FE.md 全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图 和 html 获取精确视觉参数。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 第 2 波（等第 1 波 3 个 BE 完成后派发）

### → MiniProgram Agent 1：FEAT-CONTEST-DETAIL-FE11

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-CONTEST-DETAIL-FE.md 全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图和html获取精确视觉参数。
⭐ 先读 subagent/tasks/任务报告/FEAT-CONTEST-DETAIL-BE-回答.md 确认后端字段和接口。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

### → MiniProgram Agent 2：FEAT-COURSE-DETAIL-FE111

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-COURSE-DETAIL-FE.md 全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图和html获取精确视觉参数。
⭐ 先读 subagent/tasks/任务报告/FEAT-COURSE-DETAIL-BE-回答.md 确认后端字段和接口。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

### → MiniProgram Agent 3：FEAT-INDEX-ENRICH-FE

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-INDEX-ENRICH-FE.md 全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图和html获取精确视觉参数。
⭐ 先读 subagent/tasks/任务报告/FEAT-INDEX-ENRICH-BE-回答.md 确认后端字段和接口。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 第 3 波（等第 2 波完成后派发）

### → Backend Agent：FEAT-FILTER-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-FILTER-BE.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

### → MiniProgram Agent：FEAT-FILTER-FE（等 FEAT-FILTER-BE 完成后）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（ D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（ D:\coding\jst_v1\subagent\tasks\FEAT-FILTER-FE.md 全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图获取精确视觉参数。
⭐ 先读 subagent/tasks/任务报告/FEAT-FILTER-BE-回答.md 确认后端接口。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 派发进度追踪

| 波次 | 任务 | Agent | 状态 |
|------|------|-------|------|
| 1 | FEAT-CONTEST-DETAIL-BE | Backend 1 | ⏳ |
| 1 | FEAT-COURSE-DETAIL-BE | Backend 2 | ⏳ |
| 1 | FEAT-INDEX-ENRICH-BE | Backend 3 | ⏳ |
| 1 | FEAT-MY-ENROLL-FIX-FE | MiniProgram 1 | ⏳ |
| 2 | FEAT-CONTEST-DETAIL-FE | MiniProgram | ⏳ 等 BE |
| 2 | FEAT-COURSE-DETAIL-FE | MiniProgram | ⏳ 等 BE |
| 2 | FEAT-INDEX-ENRICH-FE | MiniProgram | ⏳ 等 BE |
| 3 | FEAT-FILTER-BE | Backend | ⏳ 等第 2 波 |
| 3 | FEAT-FILTER-FE | MiniProgram | ⏳ 等 BE |

> 每个 BE 完成后：review 报告 → 执行 DDL → 重启后端 → 再派对应 FE
