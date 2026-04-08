# 任务卡 P-POLISH（备忘） - 视觉对齐 sprint

> **状态**：未派发，等阶段 C 资金主线完成后启动
> **触发时机**：阶段 C 全部 feature 跑通 + P5/P6 报名/订单页面完成后
> **派发模式**：单 MiniProgram Agent，工作量约 2-3 天

## 背景

阶段 B 4 个前端 Agent 完成基础页面后，用户首轮联调发现页面视觉与 `小程序原型图/*.html` 有差距。此差距是预期的:
- Agent 第一次写 vue 时优先正确性 + 数据流，视觉细节 trade-off
- 原型 HTML 是 PC 浏览器 px 单位，Uniapp 是 rpx 响应式单位，需要适配转换
- 部分原型用了 emoji/SVG/伪元素，组件体系需要统一封装

## 必读上下文
1. `架构设计/26-Uniapp用户端架构.md`
2. `小程序原型图/AGENT_GUIDE.md`
3. `小程序原型图/design-system.css` ← **核心 token 来源**
4. `小程序原型图/design-system.html` ← 浏览器打开看组件实物
5. ⭐ **`小程序原型图/{xxx}.png`** 91 张高保真截图 ← **像素级对齐的唯一真理来源**
6. `小程序原型图/{xxx}.html` ← DOM 结构 + class 命名参考
7. `subagent/MINIPROGRAM_AGENT_PROMPT.md` § 对齐工作流 (强制 5 步)

## 待对齐页面清单

| 页面 | Vue 文件 | 原型 HTML | 优先级 |
|---|---|---|---|
| 登录页 | `pages/login/login.vue` | `小程序原型图/login.html` | 高 |
| 首页 | `pages/index/index.vue` | `小程序原型图/index.html` | 高 |
| 赛事列表 | `pages/contest/list.vue` | `小程序原型图/contest-list.html` | 高 |
| 赛事详情 | `pages-sub/contest/detail.vue` | `小程序原型图/contest-detail.html` | 高 |
| 课程列表 | `pages/course/list.vue` | `小程序原型图/course-list.html` | 中 |
| 课程详情 | `pages-sub/course/detail.vue` | `小程序原型图/course-detail.html` | 中 |
| 公告列表 | `pages/notice/list.vue` | `小程序原型图/notice.html` | 中 |
| 公告详情 | `pages-sub/notice/detail.vue` | `小程序原型图/notice-detail.html` | 中 |
| 我的(双视角) | `pages/my/index.vue` | `小程序原型图/my.html` | 高 |
| 资料编辑 | `pages-sub/my/profile-edit.vue` | `小程序原型图/profile-edit.html` | 低 |
| 我的档案 | `pages-sub/my/participant.vue` | (无对应原型) | 低 |

## 对齐原则

1. **像素级对齐**：以原型为准，忽略 vue 文件已写的样式
2. **复用 design-system.css 的所有 token**：
   - 颜色 (主色 / 文字 / 背景 / 边框)
   - 字号 (title / body / caption)
   - 间距 (8/16/24/32rpx)
   - 圆角 (8/16/24rpx)
   - 阴影 (card / dialog / button)
3. **px → rpx 转换**：原型中的 px 数值 × 2 = rpx (375 设计稿基准)
4. **组件抽取**：相同的 UI 元素（卡片/按钮/标签）抽到 `components/jst-*` 公共组件
5. **emoji → 图标**：原型用 emoji 占位的图标（🏆 📚 📢 等），保留 emoji 兼容方案 + 推荐替换为 SVG/PNG

## 严禁

- ❌ 修改 .vue 的 `<script>` 业务逻辑（仅改 `<template>` 和 `<style>`）
- ❌ 修改 api/store/utils（仅改视觉层）
- ❌ 引入新的 npm UI 库
- ❌ 删除现有功能为了"简化视觉"

## 微调清单（用户首轮联调发现的问题，需 polish sprint 时确认/修复）

### 已知偏差（来自子 Agent 报告 + 用户反馈）

1. **首页 (`pages/index/index.vue`)**：
   - 没有把原型里的"课程推荐 / 平台介绍 / 客服悬浮"一起带进来
   - 入口卡片样式不对齐
2. **公告详情**：
   - 缺少原型里的"相关公告推荐"和"分享 Sheet"
3. **赛事 tab**：
   - tab 图标用了 index 图标占位（需要专属赛事图标）
4. **课程详情**：
   - 缺少 design-system 里的卡片阴影/圆角细节
5. **我的页**：
   - 双视角切换 UI 仅做了占位，未对齐原型
6. **登录页**：
   - 字体大小 / 渐变方向可能与原型有差异

## DoD

- [ ] 11 个页面与原型像素级对齐（误差 < 4rpx）
- [ ] 颜色/字号/间距全部使用 design-system token
- [ ] tab 图标全部用专属图标
- [ ] HBuilderX 浏览器跑通无 console 红色
- [ ] 6 个验证场景（见 P1-P4 任务卡 DoD）全部通过

## 工时
2-3 天（一个 MiniProgram Agent）

## 优先级
**中高**（影响用户体验，但不阻塞业务）

## 派发时机
**阶段 C 资金主线全部跑通 + P5/P6 报名/订单页面完成后**统一做。
