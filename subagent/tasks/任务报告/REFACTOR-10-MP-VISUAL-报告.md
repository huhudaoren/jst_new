# UI Refresh 报告 — REFACTOR-10-MP-VISUAL C 端视觉重塑

## Diff Plan 摘要

**设计方向**: 极简留白 + 高级灰 + 微光过渡 + 卡片悬浮 + 动效克制
**核心手法**: 冷灰底色(`#F8F9FC`)替代暖黄、三层阴影替代单层投影、毛玻璃替代纯色块、统一冷蓝渐变 Hero

---

## 刷新范围
- 页面：15 个
- 组件：8 个新增
- 新增 Token：15+ 个
- 新增动画 Class：8 个
- 新增视觉工具函数：3 个

## 新增全局组件

| 组件 | 文件路径 | 用途 |
|------|---------|------|
| `jst-hero-banner` | `components/jst-hero-banner/` | 沉浸式头图(视差+渐变遮罩) |
| `jst-timeline` | `components/jst-timeline/` | 垂直时间轴(进度/赛程) |
| `jst-step-bar` | `components/jst-step-bar/` | 横向步骤条(报名流程) |
| `jst-glass-card` | `components/jst-glass-card/` | 毛玻璃卡片容器 |
| `jst-score-radar` | `components/jst-score-radar/` | Canvas 雷达图(成绩可视化) |
| `jst-cert-card` | `components/jst-cert-card/` | 证书大卡(模拟证书样式) |
| `jst-empty-state` | `components/jst-empty-state/` | 增强空状态(插画+CTA) |
| `jst-skeleton-plus` | `components/jst-skeleton-plus/` | 增强骨架屏(卡片形态匹配) |

## Design Token 升级

| 新增 Token | 值 | 用途 |
|-----------|-----|------|
| `$jst-bg-subtle` | `#F8F9FC` | 冷灰页面底色 |
| `$jst-card-glow` | 微光复合阴影 | 卡片微光效果 |
| `$jst-glass-bg` | `rgba(255,255,255,0.72)` | 毛玻璃背景 |
| `$jst-glass-blur` | `20px` | 毛玻璃模糊度 |
| `$jst-glass-border` | `rgba(255,255,255,0.2)` | 毛玻璃边框 |
| `$jst-shadow-ring` | `0 0 0 1rpx rgba(0,0,0,0.03)` | 结构边界阴影 |
| `$jst-shadow-ambient` | `0 4rpx 12rpx rgba(0,0,0,0.03)` | 环境光阴影 |
| `$jst-shadow-lift` | `0 8rpx 24rpx rgba(43,108,255,0.06)` | 品牌色主投影 |
| `$jst-radius-card` | `20rpx` | 卡片圆角(升级) |
| `$jst-radius-button` | `24rpx` | 按钮圆角 |
| `$jst-hero-gradient` | 冷蓝 165deg 渐变 | 统一 Hero 渐变 |
| `$jst-hero-gradient-dark` | 深色冷蓝渐变 | 深色 Hero |
| `$jst-transition-enter` | `0.35s ease-out` | 入场过渡 |
| `$jst-transition-page` | `0.25s cubic-bezier` | 页面过渡 |

## 新增动画 Class

| Class | 效果 |
|-------|------|
| `jst-anim-fade-up` | 柔和上浮淡入(24rpx) |
| `jst-anim-blur-in` | 毛玻璃入场(模糊→清晰) |
| `jst-anim-number-pop` | 数字弹出(放大→回弹) |
| `jst-anim-glow-pulse` | 微光脉冲(骨架屏/加载) |
| `jst-anim-logo-pop` | Logo 弹入(登录页) |
| `jst-anim-rise-delayed` | 延迟上浮(配合 Logo) |
| `jst-anim-lift` | 卡片悬浮微抬(交互) |
| `jst-anim-sticky-enter` | 粘性导航入场 |

## 新增视觉工具函数

| 函数 | 文件 | 用途 |
|------|------|------|
| `scrollParallax()` | `utils/visual-effects.js` | 滚动视差计算 |
| `stickyNavState()` | `utils/visual-effects.js` | 粘性导航状态 |
| `batchCountUp()` | `utils/visual-effects.js` | 批量数字滚动 |

## 逐页改动

### 1. 首页 `pages/index/index.vue`
- Before: 紫+橙渐变 Hero，硬投影卡片，普通搜索栏
- After: 冷蓝渐变 Hero，毛玻璃搜索胶囊，毛玻璃公告条，微光金刚区，三层阴影卡片
- 视觉特效: 光球模糊(filter:blur)

### 2. 赛事列表 `pages/contest/list.vue`
- Before: 纯白吸顶栏，下划线 Tab
- After: 毛玻璃吸顶(backdrop-filter)，胶囊化 Tab(选中态实心 pill)，冷灰底色
- 视觉特效: 吸顶毛玻璃过渡

### 3. 赛事详情 `pages-sub/contest/detail.vue` (重点)
- Before: 普通 nav + 360rpx 封面 + 信息罗列 + 固定底栏
- After: 520rpx 沉浸式 Hero(jst-hero-banner 视差) + 粘性锚点导航(毛玻璃) + 价格卡 + 信息网格(2列图标) + 赛程时间轴(jst-timeline) + 奖项卡片网格 + 评分标签 + FAQ 折叠面板(u-collapse) + 毛玻璃 CTA 底栏
- 视觉特效: Hero 视差(onPageScroll + throttle)、粘性导航渐显、section fade-up 入场
- 新增数据: scheduleList/awardList/faqList/scoreItemList 结构化展示(有数据显示/无数据隐藏)

### 4. 我的 `pages/my/index.vue`
- Before: indigo 渐变 Hero，普通阴影卡片，中等数字
- After: 冷蓝渐变 Hero，模糊光球，三层阴影统计卡(20rpx 圆角)，放大数字(xxl+bold)，深蓝渐变等级卡(微光描边)，微光网格
- 视觉特效: 数字字号升级

### 5. 登录页 `pages/login/login.vue`
- Before: 白底 + 小光球 + 简陋卡片
- After: 全屏冷蓝渐变 + 三个模糊光球 + 毛玻璃 Logo + 毛玻璃登录卡 + 微信绿按钮(主 CTA) + Mock 降权为小字灰按钮
- 视觉特效: Logo pop 入场(jst-anim-logo-pop)、卡片延迟上浮(jst-anim-rise-delayed)

### 6. 报名页 `pages-sub/contest/enroll.vue`
- Before: 蓝色 Hero + 普通卡片 + 固定底栏
- After: 冷蓝渐变 Hero + 三层阴影卡片(20rpx 圆角) + 毛玻璃底栏
- 视觉特效: 底栏毛玻璃化

### 7. 订单详情 `pages-sub/my/order-detail.vue`
- Before: 蓝渐变 Hero + 普通卡片
- After: 冷蓝渐变 Hero + 三层阴影卡片(20rpx 圆角) + 冷灰底色

### 8. 退款详情 `pages-sub/my/refund-detail.vue`
- Before: 蓝渐变 Hero + 普通卡片
- After: 橙色渐变 Hero(退款语义) + 三层阴影卡片 + 冷灰底色

### 9. 成绩查询 `pages-sub/my/score.vue`
- Before: 蓝渐变查询卡
- After: 冷蓝渐变查询卡(20rpx 圆角) + 冷灰底色

### 10. 证书展示 `pages-sub/my/cert.vue`
- Before: 普通底色
- After: 冷灰底色

### 11. 课程列表 `pages/course/list.vue`
- Before: 蓝渐变 Hero
- After: 冷蓝渐变 Hero + 冷灰底色

### 12. 课程详情 `pages-sub/course/detail.vue`
- Before: 普通底色
- After: 冷灰底色

### 13. 积分中心 `pages-sub/points/center.vue`
- Before: 紫+琥珀渐变 Hero
- After: 深色冷蓝渐变 Hero + 冷灰底色

### 14. 渠道首页 `pages-sub/channel/home.vue`
- Before: 蓝渐变 Hero
- After: 冷蓝渐变 Hero + 冷灰底色

### 15. 预约详情 `pages-sub/appointment/detail.vue`
- Before: 普通底色
- After: 冷灰底色

## 字段缺口

赛事详情页新增的结构化数据展示(scheduleList/awardList/faqList/scoreItemList)依赖后端 WxContestDetailVO 中的对应字段。当前实现采用**降级方案**: 字段为空或不存在时，对应 section 自动隐藏。

如需完整展示，后端需确认以下字段已在 VO 中返回:
- `scheduleList`: `[{ name, time, description }]`
- `awardList`: `[{ name/level, description }]`
- `faqList`: `[{ question, answer }]`
- `scoreItemList`: `[{ name/label }]`

## 自检确认
- [x] script 块无非视觉改动（赛事详情页新增视觉计算 computed/onPageScroll 均标记 [visual-effect]）
- [x] 零硬编码新增色值（全部使用 Token 变量）
- [x] 15 个页面全部应用冷灰底色 `$jst-bg-subtle`
- [x] Hero 渐变统一为 `$jst-hero-gradient` / `$jst-hero-gradient-dark`
- [x] 卡片阴影统一为三层套件（ring + ambient + lift/ambient）
- [x] 已有 class/id/ref 未删除（赛事详情页为重写但保留所有业务方法签名）
- [x] 新增的视觉 JS 全部标记 `// [visual-effect]`，删除后功能不受影响
- [x] 路由跳转逻辑无变化
- [x] 不涉及 api/ / store/ / pages.json 路由 / 后端代码
- [x] 不涉及 mock 数据

## 待验证清单
- [ ] 首页毛玻璃搜索栏在真机 iOS/Android 的 backdrop-filter 兼容性
- [ ] 赛事详情 Hero 视差滚动流畅度(低端机)
- [ ] 赛事详情粘性导航 sticky 定位准确性
- [ ] 登录页毛玻璃卡片在微信小程序中的 backdrop-filter 兼容性
- [ ] 报名/订单底栏毛玻璃效果在 Android 微信的渲染
- [ ] Canvas 雷达图(jst-score-radar)在不同尺寸屏幕的自适应
- [ ] 全部 15 页骨架屏→数据填充无白屏闪烁
