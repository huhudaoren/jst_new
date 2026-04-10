# 任务卡 TEST-ROUND2 - 第二轮全量测试（验证 FIX-1~5 修复效果）

## 背景
第一轮 Test Agent 发现 10 个失败点（admin-tests 8 失败 + wx-tests 1 失败 + 链路 E 🔴）。FIX-1~5 已完成修复。本轮验证修复效果并更新健康度基线。

## 系统提示词
使用 `subagent/TEST_AGENT_PROMPT.md`（含反幻觉准则 + Layer 1/2/3）

## 本轮重点验证项

### 必须验证的修复点（对照第一轮失败）

| # | 第一轮失败 | 修复卡 | 本轮预期 |
|---|---|---|---|
| 1 | H4 赛事方提交审核 403 | FIX-1 权限 | → 200 |
| 2 | PA7-A2~A5 结算全失败 | FIX-1 fixture | → A1/A2 通过 |
| 3 | 团队预约 API 404 | FIX-1 URL | → 200 |
| 4 | 临时档案 PUT/DELETE 缺 | FIX-2 补接口 | → 200 |
| 5 | DEBT-1-W1 roles 缺 jst_channel | FIX-1 fixture | → roles 含 jst_channel |
| 6 | B2 撤销认领 400 | FIX-4 URL 编码 | → 200 |
| 7 | H7 越权 500 | FIX-4 补字段 | → 403 |
| 8 | 链路 E 赛事方闭环 🔴 | FIX-1 | → ✅ |
| 9 | 链路 C 渠道代报名 ⏸️ | FIX-1+2 | → ✅ 或至少部分通过 |

### 新增接口验证（FIX-2 + FIX-5）

| # | 接口 | 来源 |
|---|---|---|
| 1 | GET /jst/wx/user/profile → 9 新字段 | FIX-2 Part D |
| 2 | GET /jst/partner/dashboard/summary | FIX-2 Part E |
| 3 | GET /jst/partner/dashboard/todo | FIX-2 Part E |
| 4 | GET /jst/partner/notice/recent | FIX-2 Part E |
| 5 | GET /jst/wx/score/my | FIX-5 Part A |
| 6 | GET /jst/wx/cert/my | FIX-5 Part B |
| 7 | GET /jst/wx/message/my | FIX-5 Part C |
| 8 | POST /jst/wx/message/read-all | FIX-5 Part C |
| 9 | GET /jst/public/score/query | FIX-5 Part D |
| 10 | GET /jst/public/cert/verify | FIX-5 Part E |

### 新增页面验证（FIX-3）

Layer 1 静态验证 8 个新页面的路由 + 入口 + API 匹配：
- pages-sub/my/score.vue
- pages-sub/my/cert.vue
- pages-sub/my/message.vue
- pages-sub/my/settings.vue
- pages-sub/my/privacy.vue
- pages-sub/notice/message.vue
- pages-sub/public/score-query.vue
- pages-sub/public/cert-verify.vue

### Toast 占位验证（FIX-4）

```bash
grep -rn "后续开放" jst-uniapp/ --include="*.vue" --include="*.js" | grep -v unpackage
```
预期：**0 结果**（仅剩"即将上线"灰标）

## 运行环境
- 后端：`http://127.0.0.1:8080`（用户需确认已用最新代码重启）
- 前端：`http://localhost:8081/#/`
- 测试文件：`test/wx-tests.http` + `test/admin-tests.http`

## 输出
报告路径：`subagent/tasks/任务报告/TEST-ROUND2-回答.md`

格式同第一轮（总览 + 逐段结果 + 链路验证 + 健康度评分），额外增加：
- §修复验证矩阵：对照上表 9 个修复点逐条标 ✅/❌
- §新增接口矩阵：对照上表 10 个接口逐条标 ✅/❌
- §与第一轮对比：admin-tests 通过率从 81% → ?%，wx-tests 从 95% → ?%

## 不许做
- ❌ 不许改任何代码
- ❌ 不许编造测试结果（反幻觉准则）
- ❌ 不许跳过任何修复验证点

---
派发时间：2026-04-10
