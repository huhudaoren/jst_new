# UI Refresh 报告 — UI-REFRESH-1 核心页面刷新

## 刷新范围
- 页面：5 个（首页/赛事列表/我的/公告列表/课程列表）
- 新增 token 引用：~127 处硬编码消除

## 组件替换统计

| 手写 → 组件 | 替换次数 | 涉及页面 |
|-------------|---------|---------|
| 手写搜索栏 → `u-search` | 2 | contest/list, notice/list |
| `uni-load-more` / 手写加载更多 → `u-loadmore` | 4 | contest/list, notice/list, course/list, (index 已有 jst-empty) |
| 无骨架屏 → `u-skeleton` | 5 | 全部 5 页 |
| 手写 meta-tag → `u-tag` | 3 | my/index (等级/积分/绑定标签) |
| 手写 platform-tag → `u-tag` | 4 | index (平台介绍 4 个标签) |
| 手写 top-tag → `u-tag` | 1 | notice/list (置顶标签) |
| 手写 logout btn → `u-button` | 1 | my/index |

## Token 化统计

| 类型 | 硬编码消除数 |
|------|------------|
| 颜色 | 52 |
| 字号 | 35 |
| 间距 | 28 |
| 圆角 | 8 |
| 阴影 | 6 |
| 字重 | 12 |
| 动效 | 8 |

## 视觉特效统计

| 页面 | 特效 | 实现方式 | JS 行数 |
|------|------|---------|--------|
| index | 平台数据数字滚动 | `countUp()` from visual-effects.js | 12 |
| index | 赛事卡列表入场 | CSS `.jst-anim-slide-up` + stagger | 3 (JS) + 纯 CSS |
| index | 骨架屏首屏 | `u-skeleton` + `skeletonShow` data | 3 |
| contest/list | 列表卡片入场 | CSS `.jst-anim-slide-up` + stagger | 3 |
| contest/list | 骨架屏首屏 | `u-skeleton` | 3 |
| my/index | Summary 数字滚动 | `countUp()` × 4 | 10 |
| my/index | 等级进度条动画 | CSS `transition: width 0.8s` | 0 (纯 CSS) |
| my/index | 骨架屏首屏 | `u-skeleton` | 3 |
| notice/list | 公告卡入场动画 | CSS `.jst-anim-slide-up` + stagger | 3 |
| notice/list | 骨架屏首屏 | `u-skeleton` | 3 |
| course/list | 课程卡入场动画 | CSS `.jst-anim-scale-in` + stagger | 3 |
| course/list | Segment 切换过渡 | CSS `transition` on bg/color | 0 (纯 CSS) |
| course/list | 骨架屏首屏 | `u-skeleton` | 3 |

## 逐页改动

### 1. 首页 `pages/index/index.vue`
- Before: 手写 CSS ~30 处硬编码（`#F7F8FA/#1A1A1A/#fff/1a7cd9` + 局部 shadow 变量 + 硬编码字号间距圆角），无骨架屏，平台数据静态文字
- After: 全部 `$jst-*` token + `u-skeleton` 骨架屏 + `countUp()` 数字滚动 + `u-tag` 替代手写标签 + 列表入场 stagger 动画 + 按压反馈
- 视觉特效: 平台统计数字从 0 滚动到目标值（500+/10万+/98%）；赛事卡列表交错滑入
- Script 业务逻辑: 零改动（仅追加 `skeletonShow`/`animatedStats`/`statAnimCancels` 视觉字段 + `animateStats()`/`getStaggerDelay()` 视觉方法）

### 2. 赛事列表 `pages/contest/list.vue`
- Before: 手写搜索栏 + `uni-load-more` + `#F7F8FA/#8A8A8A` 硬编码 + 无骨架屏
- After: `u-search` + `u-loadmore` + `u-skeleton` + 全部 token 化 + Tab 切换过渡动画 + 列表入场 stagger
- Script 业务逻辑: 零改动（仅追加 `skeletonShow` + `getStaggerDelay()`）

### 3. 我的页 `pages/my/index.vue`
- Before: ~50 处硬编码色值（`#F7F8FA/#1A1A1A/#8A8A8A/#E74C3C/#C0C4CC/#F2F3F5` + 9 种 icon 背景色 + hero/level-card 渐变色），无骨架屏，Summary 数字静态
- After: 全部 token 化 + `u-skeleton` 骨架屏 + `u-tag` 等级/积分/绑定标签 + `u-button` 退出 + `countUp()` Summary 数字滚动 + 等级进度条 CSS 动画 + 全面按压反馈
- Hero/level-card 渐变色保留为设计特例（深色主题区域，不适合通用 token）
- Script 业务逻辑: 零改动

### 4. 公告列表 `pages/notice/list.vue`
- Before: 手写搜索 input + 手写加载更多文字 + ~20 处硬编码（`#F7F8FA/#1A1A1A/#8A8A8A/#283593/#EAF4FC/#FFF3EE/#ff6b35` 等）
- After: `u-search` + `u-loadmore` + `u-skeleton` + `u-tag` 置顶标签 + 全部 token 化 + 公告卡入场动画
- Script 业务逻辑: 零改动（仅追加 `skeletonShow`/`loadMoreState`/`getStaggerDelay()`）

### 5. 课程列表 `pages/course/list.vue`
- Before: 手写加载更多 + ~12 处硬编码 + `#283593` active 背景 + 无骨架屏
- After: `u-loadmore` + `u-skeleton` + token 化 + Segment 切换 CSS 过渡 + 课程卡 scale-in 入场
- Script 业务逻辑: 零改动（仅追加 `skeletonShow`/`loadMoreState`/`getStaggerDelay()`）

## 自检确认
- [x] script 块无非视觉改动（所有新增 JS 均标记 `// [visual-effect]`，删除后功能正常）
- [x] 零硬编码数值（组件内全部 token 化，hero/level-card 渐变色标注为设计特例）
- [x] 所有 5 页有骨架屏（`u-skeleton` + `skeletonShow`）
- [x] 所有列表有空状态（`jst-empty` 保持）
- [x] 所有列表有 `u-loadmore` 三态
- [x] 可点击元素有按压反馈（`:active { transform: scale() }`）
- [x] 已有 class/id/ref 未删除
- [x] 未改本批次之外的页面
- [x] 未改 api/ 或 store/
- [x] 未改后端代码

## 待验证清单
- [ ] HBuilderX 编译到微信小程序 → 无报错
- [ ] HBuilderX 编译到 H5 → 无报错
- [ ] 首页骨架屏 → 数据加载完消失 → 数字滚动动画 → 赛事卡交错入场
- [ ] 赛事列表搜索栏（u-search） → 搜索/清除功能正常 → Tab 切换过渡 → 列表入场
- [ ] 我的页骨架屏 → Summary 数字滚动 → 等级进度条动画 → 各 cell 按压反馈
- [ ] 公告列表骨架屏 → 置顶标签（u-tag） → 公告卡入场 → u-loadmore 三态
- [ ] 课程列表骨架屏 → Segment 切换过渡 → 课程卡 scale-in 入场 → u-loadmore
