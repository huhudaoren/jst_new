# 任务卡 UI-REFRESH-1 - 核心页面视觉刷新（批次 1）

## 阶段
阶段 F / **jst-uniapp**（UI Refresh Agent）

## 背景
UI-INFRA 已搭建完成（uView 2.0 + Design Token + 组件升级）。本卡刷新用户最高频接触的 5 个页面，建立视觉标杆供后续批次参照。

## 系统提示词
使用 `subagent/UI_REFRESH_AGENT_PROMPT.md`

## 必读
1. `subagent/UI_REFRESH_AGENT_PROMPT.md` — 设计语言、token 体系、检查清单
2. `jst-uniapp/styles/design-tokens.scss` — 当前 token 值
3. 每个页面对应的原型 PNG（`小程序原型图/` 目录）— **布局参考，不是视觉上限**
4. 每个页面的 `.vue` 源码 — 理解业务逻辑（script 不动）

## 刷新范围

### 刷新页面清单

| # | 页面 | 路径 | 原型参考（如有） |
|---|------|------|-----------------|
| 1 | 首页 | `pages/index/index.vue` | `小程序原型图/index*.png` |
| 2 | 赛事列表 | `pages/contest/list.vue` | `小程序原型图/contest-list*.png` |
| 3 | 我的 | `pages/my/index.vue` | `小程序原型图/my*.png` |
| 4 | 公告列表 | `pages/notice/list.vue` | `小程序原型图/notice*.png` |
| 5 | 课程列表 | `pages/course/list.vue` | `小程序原型图/course*.png` |

每个页面的具体改造方案由你读完源码和原型后自行设计。核心目标：
- 手写 CSS → uView 组件 + jst-* 组件
- 硬编码数值 → token 变量
- 加载态 / 空状态 / 微交互补齐
- **比原型更好**

## 每个页面的刷新步骤（强制）

1. 读原型 PNG → 理解布局骨架（原型是下限不是上限）
2. 读当前 `.vue` → 标记所有手写 CSS 和硬编码值
3. 列出组件替换清单（手写 → uView/jst-*）
4. **设计视觉特效方案**（本页适合哪些微交互？参考 prompt §4.5 特效表）
5. 输出 Diff Plan（含特效方案）→ **等用户确认**
6. 重写 `<template>` + `<style>`，可追加视觉脚本（标记 `// [visual-effect]`）
7. 逐条过检查清单（prompt §七，含特效检查）

## 视觉特效

由你自主设计每个页面的微交互和动效。参考 prompt §4.5 的原则和约束，用你的审美判断。

唯一要求：每个页面**至少 1 个有意义的微交互**，在报告中说明设计意图。

## 质量要求

每个页面必须满足（对照 prompt §七）：
- [ ] 零硬编码颜色（全部 `$jst-*`）
- [ ] 零硬编码字号
- [ ] 零硬编码间距
- [ ] 零硬编码圆角
- [ ] 零硬编码阴影
- [ ] 有骨架屏（`u-skeleton`）
- [ ] 有空状态（`u-empty`）
- [ ] 列表有加载更多（`u-loadmore`）
- [ ] 可点击元素有按压反馈
- [ ] 状态标签颜色符合 §4.8 映射表

## DoD
- [ ] 5 个页面全部刷新
- [ ] 每页组件替换 + token 化完成
- [ ] 每页有骨架屏 + 空状态 + 加载更多
- [ ] 编译 H5 + 微信小程序无报错
- [ ] 现有业务功能无回归（script 未改）
- [ ] 任务报告 `UI-REFRESH-1-回答.md`（含 Before/After + 组件替换统计 + token 化统计）

## 不许做
- ❌ 不许改 `<script>` 中的业务逻辑（可以追加视觉特效代码，必须标记 `// [visual-effect]`）
- ❌ 不许改 api/ 或 store/
- ❌ 不许改后端代码
- ❌ 不许在前端 mock 数据
- ❌ 不许改本批次之外的页面（批次 2-4 另卡）
- ❌ 不许删除已有 class/id/ref 名称
- ❌ 不许使用 !important（除非覆盖 uView 内部样式无其他方案）
- ❌ 不许硬编码任何数值（必须用 token）
- ✅ 可以新增视觉 JS（`// [visual-effect]` 标记，删除后功能正常）
- ✅ 可以使用 `utils/visual-effects.js` 工具函数
- ✅ 可以使用 `styles/animations.scss` 全局动画 class

## 依赖：UI-INFRA ✅
## 优先级：P1

---
派发时间：2026-04-10
