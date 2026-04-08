# 子 Agent 协作工作流（你的操作手册）

> **角色**：你是协调员，传话筒 + 测试员
> **不需要**理解代码细节，只需要按本流程操作

---

## ⚠️ 子 Agent 分两类，不能混用

| Agent 类型 | 系统提示词 | 干什么 | 工作目录 |
|---|---|---|---|
| **Backend Agent** | `BACKEND_AGENT_PROMPT.md` | Java 接口实现、admin Vue 改造、ddl test fixture | `RuoYi-Vue/jst-{module}/` + `test/api-tests.http` |
| **MiniProgram Agent** | `MINIPROGRAM_AGENT_PROMPT.md` | Uniapp 页面/组件/对接接口 | `jst-uniapp/` |

**派发原则**：
- F1-Fxx 后端 feature → Backend Agent
- P1-Pxx 前端 feature → MiniProgram Agent
- 任何混合任务（后端要给前端 mock 数据）→ 拆成两个 feature 派两次
- 一个 feature 同时改前后端 → 严禁，必须先后端通了 .http 测试再派前端

---

## 整体流程图

```
┌─────────────────┐
│  你 (用户/PM)    │
└────────┬────────┘
         │ ① "继续做 F4"
         ▼
┌─────────────────┐
│  我 (主 Agent)   │  本对话窗口
│  - 写任务卡 F4   │
│  - 回你 1 个 .md│
└────────┬────────┘
         │ ② 复制 SUBAGENT_PROMPT.md + 任务卡-F4.md
         ▼
┌─────────────────┐
│ 子 Agent (新窗口)│  另开 Claude/Cursor/任意 LLM
│ - 读文档         │
│ - 写代码         │
│ - 跑编译         │
│ - 写测试         │
│ - 输出报告       │
└────────┬────────┘
         │ ③ 复制子 Agent 的报告
         ▼
┌─────────────────┐
│  你 (用户/PM)    │
└────────┬────────┘
         │ ④ 把报告发给我
         ▼
┌─────────────────┐
│  我 (主 Agent)   │
│  - 审查报告      │
│  - 抽查文件      │
│  - 给 OK/返工    │
└────────┬────────┘
         │ ⑤ 如返工 → 把意见发给子 Agent (回到 ③)
         │   如 OK  → 通知你测试
         ▼
┌─────────────────┐
│  你跑 .http 测试  │
└────────┬────────┘
         │ ⑥ 测试结果发给我
         ▼
        Gate ✅ → 下一个 feature
        Gate ❌ → 返回 ④ 让我或子 Agent 修
```

---

## 详细步骤

### Step 1: 我（主 Agent）派任务

你跟我说："**派 F4 任务**"或"**做 F4+F5+F6**"。

我会**生成 N 个任务卡**（每个 feature 一个 .md 文件，存到 `D:\coding\jst_v1\subagent\tasks\` 下）。

例如：
- `tasks/F4-学生渠道绑定.md`
- `tasks/F5-赛事方入驻审核.md`
- `tasks/F6-渠道认证申请.md`

我会告诉你：
> "F4/F5/F6 任务卡已生成，请按以下方式派发到 3 个独立窗口"

---

### Step 2: 你（PM）派发到子 Agent

打开**新窗口**（推荐 Cursor / Claude.ai 网页版 / Windsurf 的另一个会话），按以下方式提问：

**Backend Agent 提问模板**：
```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\F4-学生渠道绑定.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

**MiniProgram Agent 提问模板**：
```
你是竞赛通项目的 Frontend MiniProgram Agent。请按以下两份文档的要求开始工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\P1-登录与个人中心页.md 全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

**重要提示**：
- 子 Agent 需要**能访问你本地文件系统**（Cursor / Claude Code 都可以）
- 如果是没有文件系统的窗口（如 Claude.ai 网页），子 Agent 写不了代码，只能输出代码内容让你手动建文件——**不推荐**
- 推荐：Cursor / Windsurf / Claude Code CLI 开新会话

**多个子 Agent 并行**：
- 同时开 3 个窗口（F4 / F5 / F6 各一个）
- 每个窗口独立对话
- 它们写的代码在同一个 jst-user 模块下，但**不会互相覆盖**因为路径不重叠（F4 写 binding/, F5 写 organizer/, F6 写 channel/）

---

### Step 3: 收集子 Agent 报告

每个子 Agent 完成后会输出**结构化报告**（格式见 SUBAGENT_PROMPT.md §"你的报告格式"）。

**重要**：
- ✅ 复制**完整**报告，包括 mvn 输出、.http 测试输出
- ❌ 不要省略 / 不要"我觉得没问题"地汇报
- ❌ 不要让子 Agent 自己说"我审查通过"——主 Agent 才有审查权

---

### Step 4: 把报告发给我（主 Agent）

回到本对话窗口（我），说：

```
F4 子 Agent 报告:
（粘贴报告全文）
```

或同时发多个：
```
F4 子 Agent 报告: ...
---
F5 子 Agent 报告: ...
---
F6 子 Agent 报告: ...
```

---

### Step 5: 我审查 → 给意见

我会做：
1. 看报告 A 段（自检答题）是否合理
2. 抽查关键文件（用 Read 工具看 ServiceImpl 的核心方法）
3. 看是否违反 25 文档强约束
4. 给以下结论之一：

**结论 1：通过**
> "F4 通过，请测试 .http 中的 F4-1 ~ F4-5 用例"

**结论 2：返工**
> "F4 需返工，把以下意见复制给子 Agent：
> 1. BindingServiceImpl 缺 @Transactional
> 2. SwitchChannelReqDTO 的 channelId 没 @NotNull
> 3. .http 缺并发场景测试
> 请改后重新报告"

你把意见复制回那个子 Agent 窗口让它修。

**结论 3：紧急回退**
> "F4 严重违规（用了 Lombok / 改了 ddl），请让子 Agent 停止，我会重新派一个新窗口"

---

### Step 6: 你跑测试

我说"通过"后，你**回到 IDEA**：
1. 重启 ruoyi-admin（让新代码生效）
2. 打开 `test/api-tests.http`
3. 找到 F4 测试块，从顶部 Run All

测试结果：
- **全绿** → 在本窗口告诉我"F4 测试通过"，我标记 F4 完成
- **有红** → 把红色的 ✗ 行复制给我，我判断是代码问题还是测试问题，决定下一步

---

## 派多个 feature 时的额外注意

### 并行 vs 串行

| 情况 | 模式 |
|---|---|
| F4/F5/F6 互不依赖 | ✅ 并行（同时 3 个窗口） |
| F7 依赖 F5 (赛事方) | ❌ 必须 F5 通过后再派 F7 |
| F9 依赖 F8 (动态表单) | ❌ 串行 |

我在派任务卡时会**明确告诉你哪些可并行**。

### 命名冲突预防

- 不同 feature 的代码路径互不重叠（不同的子包/不同的类名）
- BizErrorCode.java 是共享文件，可能多个子 Agent 同时改 → **追加错误码冲突 → 我合并**
- 99-test-fixtures.sql 是共享文件 → 同样我合并
- pom.xml 任何修改都禁止子 Agent 做 → 由我做

---

## 紧急情况

### 子 Agent 输出乱码 / 不符合格式
→ 重发 SUBAGENT_PROMPT.md 给它，让它重新对齐

### 子 Agent 说"我没法访问 D:\coding 路径"
→ 那个 LLM 工具不支持文件系统，换 Cursor / Claude Code

### 子 Agent 私自改了不该改的文件
→ 把那个文件路径告诉我，我用 git 或备份恢复

### 多个子 Agent 同时改 BizErrorCode 冲突
→ 把它们的 BizErrorCode diff 都发我，我做合并

---

## 你的工作量预估

每个 feature 的协作流程，**你需要做的事**：
1. 跟我说"派 F4"（10 秒）
2. 复制任务卡 + 提示词到子 Agent 窗口（30 秒）
3. 等子 Agent 完成，复制报告回我（1-3 秒，子 Agent 工作时间不算你的）
4. 等我审查（10-30 秒）
5. 跑 .http 测试（30 秒）
6. 反馈结果（10 秒）

**单 feature 你纯操作时间约 2 分钟**，子 Agent 后台工作约 5-15 分钟。

---

## 子 Agent 用什么工具？

推荐排序（**取决于你能用什么**）：

| 工具 | 文件系统 | 多窗口并行 | 推荐度 |
|---|---|---|---|
| **Cursor (本地)** | ✅ | ✅ 多 tab | ⭐⭐⭐⭐⭐ |
| **Claude Code CLI (本地)** | ✅ | ✅ 多终端 | ⭐⭐⭐⭐⭐ |
| **Windsurf** | ✅ | ✅ | ⭐⭐⭐⭐ |
| **GitHub Copilot Workspace** | ✅ | ⚠️ | ⭐⭐⭐ |
| **Claude.ai 网页版** | ❌ 写不了文件 | ✅ | ⭐⭐ 仅设计/审查用 |
| **ChatGPT** | ❌ | ✅ | ⭐⭐ 同上 |

如果你只有网页版，子 Agent 只能输出**整个文件内容**让你手动复制粘贴到 IDEA，那样反而比我直接写慢。

---

## 总结：你需要做的事就 3 件

1. 跟我说"派 F4"
2. 把我给的任务卡 + SUBAGENT_PROMPT 复制到另一个 Cursor 窗口
3. 跑 .http 测试反馈结果

其他都是我和子 Agent 之间的事。
