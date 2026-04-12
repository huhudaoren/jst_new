# 任务卡 UI-INFRA - 视觉基础设施搭建

## 阶段
阶段 F / **jst-uniapp**（UI Refresh Agent）

## 背景
项目已有 61 个页面，全部手写 CSS（534 个 border-radius / 177 个 box-shadow / 143 个 gradient 散落各处）。`uni_modules/` 下安装了完整 uni-ui 但几乎未使用（仅 1 处引用）。uView 2.0 尚未安装。

本卡搭建视觉刷新的基础设施：安装 uView 2.0 + 建立 Design Token + 升级已有业务组件。**不改任何页面**，只建基座。

## 交付物

### Part A — 安装 uView 2.0

1. **安装依赖**：
```bash
cd jst-uniapp
npm install uview-ui@2.0.39
```
> 注意：项目已有 `package.json`（dependencies 仅 pinia）。安装后确认 `node_modules/uview-ui` 存在。如果 HBuilderX 编译报找不到模块，检查 `vue.config.js` 或 `vite.config.js` 的 transpileDependencies 是否需要加 `uview-ui`。

2. **配置 main.js**：
```js
// main.js 顶部追加
import uView from 'uview-ui'
Vue.use(uView)
```

3. **配置 uni.scss**（追加到现有内容之后）：
```scss
@import 'uview-ui/theme.scss';
// 品牌色覆盖（对齐 design-tokens.scss）
$u-primary: #2B6CFF;
$u-success: #18B566;
$u-warning: #FF9500;
$u-error: #FF4D4F;
$u-info: #909399;
$u-main-color: #1A1A1A;
$u-content-color: #4A4A4A;
$u-tips-color: #8A8A8A;
```

4. **配置 pages.json**（追加 easycom 规则）：
```json
{
  "easycom": {
    "autoscan": true,
    "custom": {
      "^u-(.*)": "uview-ui/components/u-$1/u-$1.vue"
    }
  }
}
```
注意：pages.json 已有内容，只追加 easycom 块，不改路由。

5. **配置 App.vue**（style 块追加）：
```scss
@import 'uview-ui/index.scss';
```

### Part B — Design Token 文件

**新建** `jst-uniapp/styles/design-tokens.scss`

内容按 `subagent/UI_REFRESH_AGENT_PROMPT.md` §三 的 Token 结构创建。所有变量名、数值严格按 prompt 定义。

**新建** `jst-uniapp/styles/mixins.scss`

常用 mixin 提取：
```scss
@import './design-tokens.scss';

// 文字截断
@mixin text-ellipsis($lines: 1) {
  @if $lines == 1 {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  } @else {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: $lines;
    overflow: hidden;
  }
}

// 安全区域底部
@mixin safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

// 卡片基础样式
@mixin card-base {
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-md;
  padding: $jst-space-lg;
}

// 按压反馈
@mixin press-effect {
  transition: transform $jst-duration-fast $jst-easing, box-shadow $jst-duration-fast $jst-easing;
  &:active {
    transform: scale(0.97);
    box-shadow: $jst-shadow-sm;
  }
}

// flex 居中
@mixin flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

// flex 两端对齐
@mixin flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

// 页面容器
@mixin page-container {
  min-height: 100vh;
  background: $jst-bg-page;
  padding: $jst-page-padding;
}

// 分割线
@mixin divider {
  border-bottom: 1rpx solid $jst-border;
}
```

### Part C — 业务组件升级（10 个现有 jst-* 组件）

升级以下组件，内部改用 uView 组件 + token 变量：

| 组件 | 改动方向 |
|------|---------|
| `jst-contest-card` | 按 prompt §八的示例重写，u-tag 替代手写 status |
| `jst-course-card` | 同上模式，u-tag + token |
| `jst-notice-card` | u-cell 模式，token 化间距/颜色 |
| `jst-status-badge` | 内部改用 `u-tag`，props 不变（向后兼容） |
| `jst-empty` | 内部改用 `u-empty`，保留 slot 接口 |
| `jst-loading` | 内部改用 `u-loading-icon`，保留 props |
| `jst-banner-swiper` | token 化颜色/圆角/阴影，swiper 保留 |
| `jst-form-render` | token 化表单样式，u-input 替代手写 input |
| `jst-countdown-confirm` | token 化，u-button 替代手写按钮 |
| `jst-invite-modal` | token 化，u-popup 替代手写弹窗 |

**升级原则**：
- 改每个组件前，先 `grep -rn "jst-xxx" pages/ pages-sub/` 找出所有使用方，确认 props/events 用法
- Props 接口**不变**（父页面无需修改）
- Events 接口**不变**
- 只改 `<template>` 结构和 `<style>` 块
- `<script>` 中的业务逻辑**不动**
- 如果组件原本接受 `type` / `status` 等 prop，映射到 uView 的 `type` 属性

### Part D — 视觉特效基础设施

**新建** `jst-uniapp/utils/visual-effects.js` — 可复用的动画工具函数（节流、缓动等，由你设计内容）
**新建** `jst-uniapp/styles/animations.scss` — 可复用的 CSS 动画 class（入场、淡入、弹跳等，由你设计内容）

在 App.vue 中引入 animations.scss。

### Part E — 全局样式基础

**修改** `App.vue` 的 `<style>` 块，追加全局基础样式：

```scss
@import '@/styles/design-tokens.scss';

// 全局页面背景
page {
  background-color: $jst-bg-page;
  font-size: $jst-font-base;
  color: $jst-text-primary;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

// 全局滚动条隐藏（微信端无效但 H5 有效）
::-webkit-scrollbar { display: none; }
```

### Part F — 编译验证

1. HBuilderX 编译到 H5 模式 → 无报错
2. HBuilderX 编译到微信小程序 → 无报错
3. 随机打开 3 个页面 → 现有页面**无视觉回归**（因为本卡没改页面，只建基座）
4. 在任意页面手动加一个 `<u-button type="primary">测试</u-button>` → 确认 uView 组件可用 → 验证后删除

## DoD
- [ ] uView 2.0 安装 + main.js/uni.scss/pages.json/App.vue 配置完成
- [ ] design-tokens.scss 包含全量 token（色彩/字号/间距/圆角/阴影/动效）
- [ ] mixins.scss 包含常用 mixin
- [ ] animations.scss 全局动画 class 就位
- [ ] visual-effects.js 动画工具函数就位
- [ ] 10 个 jst-* 组件全部升级（内部 uView + token，props/events 不变）
- [ ] uni.scss 中 uView 主题变量对齐品牌色
- [ ] App.vue 全局基础样式 + 动画引入就位
- [ ] 编译 H5 + 微信小程序无报错
- [ ] 现有页面无视觉回归
- [ ] 任务报告 `UI-INFRA-回答.md`

## 不许做
- ❌ 不许改任何 `pages/` 或 `pages-sub/` 下的页面文件（本卡只建基座）
- ❌ 不许改 `api/` 或 `store/` 目录
- ❌ 不许改后端代码
- ❌ 不许改 pages.json 的路由结构（只加 easycom）
- ❌ 不许删除任何现有文件
- ❌ 不许改组件的 props/events 接口（向后兼容）

## 依赖：无
## 优先级：P0（后续所有 UI-REFRESH 任务依赖本卡）

---
派发时间：2026-04-10
