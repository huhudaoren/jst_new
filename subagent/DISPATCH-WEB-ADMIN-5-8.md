# 派发清单 — WEB-ADMIN-5~8（4 卡并行）

> 4 个 Web Admin Agent 并行执行，互不冲突

---

## Agent 1: WEB-ADMIN-5 赛事周边与 Partner 补齐

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-5-赛事周边与Partner补齐.md 全部内容）

⭐ 先读已有 views/jst/user/index.vue 和 views/jst/contest/index.vue 了解精品页风格。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

---

## Agent 2: WEB-ADMIN-6 订单与预约

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-6-订单与预约.md 全部内容）

⭐ 先读已有 views/jst/order/admin-order/index.vue 了解精品页风格。
⭐ 金额字段：数据库存分(Long)，展示时 (value/100).toFixed(2) 加 ¥ 前缀。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

---

## Agent 3: WEB-ADMIN-7 积分营销消息

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-7-积分营销消息.md 全部内容）

⭐ 先读已有 views/jst/coupon/template.vue 了解精品页风格。
⭐ 积分正数绿色、负数红色。金额分→元。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

---

## Agent 4: WEB-ADMIN-8 渠道财务风控

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-8-渠道财务风控.md 全部内容）

⭐ 先读已有 views/jst/channel/admin-rebate/index.vue 了解精品页风格。
⭐ 风控告警级别颜色必须区分。审计日志只读。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

---

## 文件冲突检查

| 卡 | 改动目录 | 冲突风险 |
|---|---|---|
| 5 | `views/jst/event/*` + `views/partner/*` | 无冲突 |
| 6 | `views/jst/order/*` | 无冲突 |
| 7 | `views/jst/points/*` + `marketing/*` + `message/*` | 无冲突 |
| 8 | `views/jst/channel/*` + `finance/*` + `risk/*` + `user/*` | 无冲突 |

✅ 4 卡可安全并行。
