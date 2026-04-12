# UI Refresh 报告 — UI-INFRA 视觉基础设施

## 刷新范围
- 页面：0 个（本卡只建基座）
- 组件：10 个（全部 jst-* 组件升级）
- 新增 token：65 个
- 新建文件：4 个

## 组件替换统计

| 手写 → 组件 | 替换次数 |
|-------------|---------|
| 手写 status badge 多色分支 → `u-tag` | 1 (jst-status-badge) |
| 手写 empty → `u-empty` | 1 (jst-empty) |
| 手写 spinner → `u-loading-icon` | 1 (jst-loading) |
| 手写 mask/sheet → `u-popup` | 2 (jst-countdown-confirm, jst-invite-modal) |
| 手写 btn → `u-button` | 2 (jst-countdown-confirm) |
| 手写 tag → `u-tag` | 2 (jst-notice-card 置顶+分类) |
| jst-contest-card category 手写 → `u-tag` | 1 |

## Token 化统计

| 类型 | 硬编码消除数 |
|------|------------|
| 颜色 | 28 |
| 字号 | 22 |
| 间距 | 35 |
| 圆角 | 12 |
| 阴影 | 8 |
| 字重 | 10 |
| 动效 | 6 |

## 配置改动

| 文件 | 改动 |
|------|------|
| `package.json` | 添加 `uview-ui@^2.0.38` |
| `main.js` | Vue 2 块追加 `import uView; Vue.use(uView)` |
| `uni.scss` | 追加 `@import 'uview-ui/theme.scss'` + 8 个品牌色覆盖变量 |
| `pages.json` | 追加 `easycom` 块（仅此，路由结构未动） |
| `App.vue` | style 追加 `uview-ui/index.scss` + `animations.scss` + scrollbar 隐藏 |
| `styles/index.scss` | 首行追加 `@import './design-tokens.scss'` |

## 新建文件

| 文件 | 内容 |
|------|------|
| `styles/design-tokens.scss` | 65 个 SCSS token 变量（色彩/字号/间距/圆角/阴影/动效） |
| `styles/animations.scss` | 6 个全局动画 class + 8 个 stagger 延迟 + press/pulse |
| `utils/visual-effects.js` | 4 个工具函数（throttle/countUp/createObserver/staggerDelay） |
| `styles/mixins.scss` | 重写：保留原有 mixin 名 + 追加 7 个新 mixin + 内部切换到新 token |

## 逐组件改动

### jst-status-badge
- Before: 手写 6 种 tone 的背景/文字色（`.jst-status-badge--primary/accent/success/warning/danger/gray`）
- After: 内部用 `u-tag` 组件，computed `uTagType` 做 tone→type 映射
- Props 不变: `text` / `tone`

### jst-empty
- Before: 手写 emoji + 文字的空状态
- After: 内部用 `u-empty` 组件，`icon` prop 映射到 `mode`
- Props 不变: `text` / `icon`

### jst-loading
- Before: 手写 CSS spinner 动画
- After: 内部用 `u-loading-icon` 组件（mode=circle, color=品牌蓝）
- Props 不变: `loading` / `text`

### jst-contest-card
- Before: `var(--jst-*)` CSS 变量 + 硬编码 `#1A1A1A`, `#EAF4FC`, `#4A90D9`, `box-shadow rgba(...)`
- After: 全部 SCSS `$jst-*` token + category 用 `u-tag` 替代手写 class
- Script: 零改动

### jst-course-card
- Before: `var(--jst-*)` CSS 变量 + 硬编码字号 `34rpx/30rpx/24rpx/22rpx`
- After: 全部 `$jst-font-*` / `$jst-space-*` token + 按压态动画
- Script: 零改动

### jst-notice-card
- Before: 手写 `.notice-card__tag` 样式 + `var(--jst-*)` 变量
- After: tag 用 `u-tag` 组件 + 全部 `$jst-*` token
- Script: 零改动

### jst-banner-swiper
- Before: `var(--jst-*)` CSS 变量
- After: `$jst-*` SCSS token + dot 切换加过渡动画
- Script: 零改动

### jst-form-render
- Before: 11 处硬编码色值（`#dce6f7/#2f7de1/#eef4ff/#eb5757/#a2afc6/#9aa8bf/#bcd2f5/#f8fbff/#95a4bf/#f5f8ff/#ff6a3d`）
- After: 全部替换为 `$jst-*` token（border/brand/brand-light/danger/placeholder/bg-grey 等）
- Script: 零改动（template 也零改动，仅 style 块重写）

### jst-countdown-confirm
- Before: 手写 `.cdm-mask` + `.cdm-sheet` + `.cdm-btn` + CSS 动画
- After: `u-popup` (mode=bottom, round=32) + `u-button` (type=info/error)
- Script: 零改动

### jst-invite-modal
- Before: 手写 `.inv-mask` + `.inv-sheet` + CSS 动画
- After: `u-popup` (mode=bottom, round=32) + token 化
- QR 卡片渐变色保留为设计特例
- Script: 零改动

## 自检确认
- [x] script 块无非视觉改动
- [x] 零硬编码数值（组件内全部 token 化）
- [x] 已有 class/id/ref 未删除（template 结构保持一致或用 uView 组件替代）
- [x] Props/Events 接口向后兼容（10/10 组件零变化）
- [x] 未改任何 pages/ 或 pages-sub/ 下的页面文件
- [x] 未改 api/ 或 store/ 目录
- [x] 未改后端代码
- [x] pages.json 仅增 easycom 块，路由结构不变
- [x] uView 2.0 安装 + 配置完成（main.js/uni.scss/pages.json/App.vue）
- [x] design-tokens.scss 包含全量 token
- [x] mixins.scss 包含新旧全部 mixin
- [x] animations.scss 全局动画 class 就位
- [x] visual-effects.js 动画工具函数就位

## 待验证清单
- [ ] HBuilderX 编译到微信小程序 → 无报错
- [ ] HBuilderX 编译到 H5 → 无报错
- [ ] 打开首页 → 现有页面无视觉回归（banner/赛事卡/课程卡/公告卡正常渲染）
- [ ] 任意页面加 `<u-button type="primary">测试</u-button>` → 品牌蓝色按钮出现 → 验证后删除
- [ ] 打开渠道方学生管理页 → 解绑弹窗正常弹出（u-popup 替代验证）
- [ ] 打开渠道方首页 → 邀请弹窗正常弹出（u-popup 替代验证）

## 遗留
- 旧 `variables.scss` 的 `:root` CSS 自定义属性保留（页面仍在引用），后续 UI-REFRESH-1~4 逐页切换到 SCSS token 后可清理
- 封面渐变色（contest-card 分类色、invite-modal QR 卡片色）保留为设计特例，未强制 token 化
