# 我的页面「即将上线」占位项排查反馈

> 来源：MiniProgram Agent 排查 `pages/my/index.vue`
> 日期：2026-04-12

---

## 排查结果汇总

| 项目 | 视角 | 页面是否存在 | 处理方式 |
|---|---|---|---|
| 设置 | 渠道方 | ✅ `pages-sub/my/settings.vue` | **已对接** `navigateSettings()` |
| 客服与帮助 | 渠道方 + 学生 | ✅ `pages-sub/public/help.vue` | **已对接** `navigateHelp()` |
| 合同中心 | 渠道方 | ❌ 无页面、无路由 | **需主 Agent 决策** |
| 开票中心 | 渠道方 | ❌ 无页面、无路由 | **需主 Agent 决策** |
| 关于竞赛通 | 学生 | ❌ 无独立页面（版本信息嵌在 settings.vue） | **需主 Agent 决策** |

---

## 已完成对接（本次修改）

### 1. 渠道视角 — 设置
- 原状态：`my-page__cell--disabled` + "即将上线"
- 修改：改为 `@tap="navigateSettings"` 跳转到 `pages-sub/my/settings`

### 2. 渠道视角 + 学生视角 — 客服与帮助
- 原状态：`my-page__cell--disabled` + "即将上线"
- 修改：新增 `navigateHelp()` 方法，跳转到 `pages-sub/public/help`
- 两个视角均已对接

---

## 需主 Agent 决策的 3 项

### 1. 合同中心（渠道方 > 财务与结算）
- **当前状态**：CLAUDE.md 中标记为 `F-CONTRACT-INVOICE ⏸️ F-2 批次`
- **建议**：
  - A) 保留"即将上线"占位，等 F-2 批次开发
  - B) 先移除该入口，开发完再加回来
  - C) 做一个简单占位页提示"功能开发中"

### 2. 开票中心（渠道方 > 财务与结算）
- **当前状态**：同属 `F-CONTRACT-INVOICE`，与合同中心同批次
- **建议**：同上，与合同中心保持一致处理

### 3. 关于竞赛通（学生 > 帮助与设置）
- **当前状态**：无独立页面。`settings.vue` 底部有版本号展示（v1.0.0 · 构建 20260318）
- **建议**：
  - A) 保留"即将上线"占位
  - B) 移除入口（信息已在设置页展示）
  - C) 新建一个简单的 About 页（版本号 + Logo + 服务协议链接）

---

## 改动文件清单

| 文件 | 改动 |
|---|---|
| `jst-uniapp/pages/my/index.vue` | 渠道视角"设置"启用 + 两视角"客服与帮助"对接 help.vue + 新增 `navigateHelp()` |
