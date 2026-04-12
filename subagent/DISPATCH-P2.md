# 派发清单 — P2 合同发票 + 运营聚合（3 卡）

> BE 2 卡可并行，FE 1 卡等 F-CONTRACT-INVOICE-BE 完成后派发

---

## 🟢 Wave 1：并行派发（2 个 Backend Agent）

### Agent 1: F-CONTRACT-INVOICE-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\F-CONTRACT-INVOICE-BE.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### Agent 2: F-ANALYSIS-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\F-ANALYSIS-BE.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 🟡 Wave 2：等 BE 完成后派发（1 个 MiniProgram Agent）

### Agent 3: F-CONTRACT-INVOICE-FE

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\F-CONTRACT-INVOICE-FE.md 全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图获取精确视觉参数。
⭐ 必须用 uView 组件 + Design Token，禁止手写硬编码 CSS。
⭐ script 块的业务逻辑参考已有的 channel/ 页面风格。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 依赖关系

```
F-CONTRACT-INVOICE-BE ──┐
                        ├──→ F-CONTRACT-INVOICE-FE
F-ANALYSIS-BE ──────────┘ (无依赖，独立)
```

- BE 两卡并行，互不冲突（finance 模块 vs admin 模块）
- FE 卡依赖 BE 接口就绪（需要后端编译通过 + 重启后才能联调）
- 如果不想等，FE 也可以先做页面结构，mock 数据开发

## 文件冲突检查

| 卡 | 改动范围 | 冲突风险 |
|---|---|---|
| F-CONTRACT-INVOICE-BE | jst-finance/controller/wx/* + service/* | 无冲突 |
| F-ANALYSIS-BE | ruoyi-admin/controller/jst/* + mapper/* | 无冲突 |
| F-CONTRACT-INVOICE-FE | jst-uniapp/pages-sub/channel/* + api/* + pages.json | 无冲突 |
