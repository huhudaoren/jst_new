# 派发清单 — 性能优化 + 数据迁移（3 卡）

> PERF-INDEX + PERF-CACHE 可并行，DATA-MIGRATION 独立

---

## 🟢 Wave 1：并行（2 个 Backend Agent）

### Agent 1: PERF-INDEX

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\PERF-INDEX-数据库索引补齐.md 全部内容）

⭐ 直接连测试库查现有索引（SHOW INDEX FROM），避免重复建索引。
⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ 在测试库执行 SHOW INDEX FROM jst_xxx 确认已有索引后再决定补哪些。
⭐ 新建索引后用 EXPLAIN 验证热点查询走索引。
⭐ 连接池参数按环境区分（如已有多环境 yml）。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### Agent 2: PERF-CACHE

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\PERF-CACHE-热点数据缓存层.md 全部内容）

⭐ JstCacheService 放在 jst-common 模块。
⭐ 缓存接入只改 Service 层，不改 Controller。
⭐ 空值防穿透 + TTL 抖动防雪崩必须实现。
⭐ 写操作后的缓存清除不能漏（更新/上线/下线/发布都要清）。
⭐ 更新 架构设计/15-Redis-Key与锁规约.md 补充 cache: 前缀规范。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 🟡 Wave 2：独立（等旧库信息确认后）

### Agent 3: DATA-MIGRATION

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\DATA-MIGRATION-旧数据迁移方案.md 全部内容）

⭐ 本卡只做脚本框架 + 核心 3 表脚本 + 对账工具。
⭐ 旧库连接信息和字段映射目前未知，用占位符。
⭐ 脚本必须幂等（INSERT IGNORE），可重复执行。
⭐ 分批写入（1000 条/批 + sleep），避免锁表。
⭐ 产出 README.md 迁移执行手册。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 依赖关系

```
PERF-INDEX  ──┐
              ├── 可并行（改 DDL vs 改 Service，无冲突）
PERF-CACHE  ──┘

DATA-MIGRATION ── 独立（等旧库信息后才能实际执行）
```

## 文件冲突检查

| 卡 | 改动范围 | 冲突风险 |
|---|---|---|
| PERF-INDEX | DDL SQL + application-druid.yml 连接池参数 | 无冲突 |
| PERF-CACHE | jst-common/cache/ + 各模块 Service + Redis Key 文档 | 无冲突 |
| DATA-MIGRATION | scripts/migration/ 新目录 | 无冲突 |
