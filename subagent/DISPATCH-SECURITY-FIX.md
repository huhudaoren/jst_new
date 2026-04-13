# 派发清单 — SECURITY-FIX 多环境配置 + 安全加固

> 1 个 Backend Agent

---

## Agent: SECURITY-FIX

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\SECURITY-FIX-多环境配置与安全加固.md 全部内容）

⭐ 这是 P0 安全任务，审计发现 5 个禁止上线级问题。
⭐ 核心工作：拆分 application.yml 为 dev/test/prod 三环境 + 修复安全问题。
⭐ 先读现有 application.yml 和 application-druid.yml 了解当前配置。
⭐ 敏感值（密码/密钥）在 prod.yml 中必须用 ${ENV_VAR} 无默认值（不配置则启动失败）。
⭐ dev.yml 可以保留弱密钥和本地连接信息（方便开发）。
⭐ 改完后用 dev profile 启动验证功能正常。
⭐ 报告末尾附"生产环境变量清单"，列出所有需要配置的环境变量。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 改动范围

| 文件 | 操作 |
|---|---|
| `application.yml` | 移除敏感值，改为环境变量占位 |
| `application-druid.yml` | 移除数据源凭证 |
| `application-dev.yml` | **新建** — 开发环境配置 |
| `application-test.yml` | **新建** — 测试环境配置 |
| `application-prod.yml` | **新建** — 生产环境配置（全部环境变量） |
| `ResourcesConfig.java` | CORS 白名单改造 |
| `SwaggerConfig.java` | 加 @ConditionalOnProperty |
| `SecurityConfig.java` | prod 禁用代码生成器路由 |
| `JobInvokeUtil.java` | 定时任务 Bean 白名单 |
| `GlobalExceptionHandler.java` | 补 SQLException 处理 |
| 多个 Controller | 补 @Log 注解 |
