# 派发清单 — QUALITY-FIX 质量批量修复（2 卡并行）

> 2 个 Agent 并行，文件完全不重叠

---

## Agent 1: QUALITY-FIX-ADMIN（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\QUALITY-FIX-ADMIN.md 全部内容）

⭐ 这是质量审计修复卡，不是新功能开发。
⭐ 重点是删除开发残留、补 catch 提示、补权限检查。
⭐ 先全量 grep "联调|包装路由|ui-feedback|测试账号" 确认要改的行。
⭐ 改完后再 grep 确认零残留。
⭐ npm run build:prod 必须通过。
```

---

## Agent 2: QUALITY-FIX-MP（MiniProgram Agent）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\QUALITY-FIX-MP.md 全部内容）

⭐ 这是质量审计修复卡，不是新功能开发。
⭐ script 业务逻辑一行不动，只改文案 + 加 catch toast + 加 v-if 条件。
⭐ 先全量 grep "联调|F7|F8|F9|测试账号|测试数据|mock.*支付" 确认要改的行。
⭐ 改完后再 grep 确认零残留。
⭐ Mock 登录按钮用 v-if="isDev" 控制，不要删除（开发时还需要）。
```

---

## 文件冲突检查

| 卡 | 改动范围 | 冲突风险 |
|---|---|---|
| QUALITY-FIX-ADMIN | `ruoyi-ui/src/views/` + `ruoyi-ui/src/utils/format.js` + `ruoyi-ui/src/assets/styles/` | 无冲突 |
| QUALITY-FIX-MP | `jst-uniapp/pages/` + `jst-uniapp/pages-sub/` | 无冲突 |

✅ 2 卡可安全并行。
