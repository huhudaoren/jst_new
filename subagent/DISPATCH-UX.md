# 派发清单 — WEB-ADMIN-UX + POLISH（2 卡并行）

> 2 个 Web Admin Agent 并行，文件无冲突

---

## Agent: WEB-ADMIN-UX

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-UX-友好化.md 全部内容）

⭐ 先读已有 views/jst/dashboard/index.vue 了解看板现状。
⭐ 先读 views/partner/contest-edit.vue 了解 JSON textarea 的现状。
⭐ JSON 可视化是重点：用户是小白，不懂 JSON 语法，必须全部表单化。
⭐ 帮助文字要简洁实用，每段不超过 5 行。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

---

## 改动范围

| 文件 | 改动 |
|---|---|
| `views/jst/dashboard/index.vue` | +快速操作卡片 +帮助 popover |
| `views/jst/contest/index.vue` | +帮助 popover |
| `views/jst/contest/edit.vue` | 3 个 JSON textarea → 表单 |
| `views/jst/enroll/index.vue` | +帮助 popover |
| `views/jst/order/admin-order/index.vue` | +帮助 popover |
| `views/jst/order/admin-refund/index.vue` | +帮助 popover |
| `views/jst/channel/admin-withdraw/index.vue` | +帮助 popover |
| `views/partner/contest-edit.vue` | 3 个 JSON textarea → 表单 |
| `架构设计/ddl/99-migration-hide-deprecated-menus.sql` | 新建 |

---

## Agent 2: WEB-ADMIN-POLISH

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\WEB-ADMIN-POLISH-页面精品化.md 全部内容）

⭐ 先读 views/partner/contest-edit.vue 了解精品页的 Hero + Tab 标准。
⭐ 先读 views/jst/user/index.vue 了解列表页的 Hero + 搜索面板 + 详情抽屉标准。
⭐ course/edit.vue 是重点：必须 Tab 化（5 Tab），对齐 partner/contest-edit 体验。
⭐ script 业务逻辑一行不动，只改 template 结构和 style。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

---

## Agent 2 改动范围

| 文件 | 改动 |
|---|---|
| `views/jst/course/edit.vue` | Hero + Tab 化（5 Tab）+ 帮助 |
| `views/jst/course/index.vue` | +Hero +搜索面板 +帮助 |
| `views/jst/coupon/template.vue` | +Hero +dialog→drawer +帮助 |
| `views/jst/coupon/issued.vue` | +Hero +drawer +帮助 |
| `views/jst/rights/template.vue` | +Hero +dialog→drawer +帮助 |
| `views/jst/rights/issued.vue` | +Hero +drawer +帮助 |
| `views/jst/mall/index.vue` | +Hero +drawer +库存预警 +帮助 |
| `views/jst/mall/exchange.vue` | +Hero +drawer +帮助 |
| `views/jst/form-template/index.vue` | +Hero +JSON预览drawer +帮助 |
| `views/jst/notice/index.vue` | +Hero +帮助 |

---

## 文件冲突检查

| 卡 | 改动目录 | 冲突风险 |
|---|---|---|
| UX | dashboard + contest + enroll + order + channel + partner | 无冲突 |
| POLISH | course + coupon + rights + mall + form-template + notice | 无冲突 |

✅ 2 卡可安全并行（dashboard 已被 UX 占有，POLISH 不碰它）。

**注意**：UX 卡改 `jst/contest/edit.vue`（JSON 可视化），POLISH 卡改 `jst/course/edit.vue`（Tab 化），是不同文件，无冲突。
