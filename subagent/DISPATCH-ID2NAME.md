# 派发清单 — ID→名称 后端+前端（2 卡顺序执行）

> BE 先行，FE 跟随（FE 依赖 BE 返回的 xxxName 字段）

---

## 🟢 Wave 1: Backend Agent

### Agent 1: WEB-ADMIN-ID2NAME-BE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-ID2NAME-BE.md 全部内容）

⭐ 只改 Mapper XML 的 SELECT/FROM 部分 + Domain 类追加属性，不改 Controller/Service。
⭐ LEFT JOIN 必须加 del_flag='0' 条件。
⭐ 编译通过后启动，抽查 3 个列表接口确认返回 xxxName。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 🟡 Wave 2: Web Admin Agent（等 BE 完成+重启后派发）

### Agent 2: WEB-ADMIN-ID2NAME-FE

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-ID2NAME-FE.md 全部内容）

⭐ 后端已返回 xxxName 字段，你只需改前端展示。
⭐ 名称为 null 时降级显示原 ID，不报错。
⭐ 有跳转目标的名称用 el-link 包裹。
⭐ 手机端卡片和详情抽屉同步更新。
⭐ npm run build:prod 必须通过。
```

---

## 依赖关系

```
WEB-ADMIN-ID2NAME-BE（后端 JOIN 补名称）
        ↓ 完成后重启后端
WEB-ADMIN-ID2NAME-FE（前端展示名称+跳转）
```

## 文件冲突检查

| 卡 | 改动范围 | 冲突风险 |
|---|---|---|
| BE | Mapper XML + Domain 类（Java） | 无冲突 |
| FE | views/jst/ 下 15 个 .vue 文件 | 无冲突 |
