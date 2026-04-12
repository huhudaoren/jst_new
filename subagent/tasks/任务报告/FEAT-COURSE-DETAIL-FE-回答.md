# 任务报告 - FEAT-COURSE-DETAIL-FE 课程详情前端重写

## A. 任务前自检（Step 2 答题）

1. **对应原型**: `小程序原型图/course-detail.png` + `小程序原型图/course-detail.html`
2. **调用接口**:
   - `GET /jst/wx/course/{id}` (api/course.js `getCourseDetail`，27 文档 §3.3)
3. **复用 store/api**: `api/course.js`（已有 `getCourseDetail`），无需新增 store
4. **修改文件**:
   - `jst-uniapp/pages-sub/course/detail.vue`（完全重写）
5. **数据流**:
   - `onLoad(options)` → `getCourseDetail(id)` → 赋值 `detail`
   - `detail.chaptersJson` → `JSON.parse` → `chapters[]` → 折叠面板渲染
   - `detail.description` → `buildRichDescription()` → rich-text 渲染
   - 点击购买 → `handleBuy()` → toast "课程购买即将开放"
   - 点击收藏 → `handleFavorite()` → 本地切换状态 + toast
6. **双视角**: 否（公开页面，后端 @Anonymous）
7. **复用样板**: 原有 detail.vue 的 loadDetail/handleBack 逻辑 + mixins.scss 的 card-base/skeleton-bar
8. **核心 Token 对应关系**:
   - 页面背景 → `$jst-bg-page`
   - 卡片背景 → `$jst-bg-card` + `$jst-shadow-sm`
   - 标题文字 → `$jst-text-primary` + 38rpx / font-weight 800
   - 价格强调 → `$jst-danger`（红色）
   - 辅助文字 → `$jst-text-secondary`
   - Tab 激活态 → `$jst-brand`（蓝色下划线 + 文字）
   - 章节序号圆 → `$jst-color-brand-soft` bg + `$jst-brand` fg
   - 购买按钮 → `$jst-gradient-primary`（橙色渐变）
   - 卡片圆角 → `$jst-radius-lg`
   - 讲师头像 fallback → `linear-gradient(135deg, #FFD54F, #FF8A65)`（与原型一致）
   - 免费试看标签 → `$jst-success-light` bg + `$jst-success` fg

## B. 交付物清单

**修改文件**:
- `jst-uniapp/pages-sub/course/detail.vue` — 完全重写

**无新增文件**（接口、路由均已存在）

## C. 页面结构对照原型 5 大区块

| 区块 | 原型要求 | 实现 | 状态 |
|------|----------|------|------|
| 1. 封面区 Hero | 渐变背景 + emoji/封面 + 返回/分享按钮 + 底部圆角遮罩 | ✅ `cd-hero` 完整实现 | ✅ |
| 2. 标题卡 + 统计行 | 课名 + 标签 + 3 stat cell（课时/学习人数/总时长） | ✅ `cd-title-card` + `cd-stats` | ✅ |
| 3. 讲师卡片 | 头像 + 姓名 + 简介 + 箭头 | ✅ `cd-teacher`，teacherName 为空则不显示 | ✅ |
| 4. Tab 切换 | 课程介绍(rich-text) / 课程目录(折叠面板) | ✅ `cd-tabs-card`，chaptersJson 为空则隐藏目录 Tab | ✅ |
| 5. 底部操作栏 | 价格 + 收藏 + 立即购买 | ✅ `cd-bottom`，购买 toast "课程购买即将开放" | ✅ |

## D. 视觉对比

- ✅ 与 course-detail.html 结构一致，所有 div 转为 view/text/image
- ✅ 骨架屏：自定义 shimmer 动画（hero + 6 个 bar），无需 uView u-skeleton
- ✅ Hero 渐变色与原型一致 `linear-gradient(150deg, #1565C0, #1976D2, #42A5F5)`
- ✅ 底部圆角遮罩 `.cd-hero__curve`
- ✅ 讲师头像 fallback 使用原型金橙渐变
- ✅ Tab 下划线激活态动画
- ✅ 课程目录折叠展开有 max-height transition 动画
- ✅ 样式全部使用 design-tokens.scss / variables.scss 的 SCSS 变量，零硬编码

## E. 空数据兜底

| 场景 | 处理 |
|------|------|
| `chaptersJson` 为空/null | 不显示"课程目录" Tab | ✅ |
| `teacherName` 为空 | 不显示讲师卡片 | ✅ |
| 统计数字为 0 | 显示 "0讲" / "0人已学" | ✅ |
| `description` 为空 | rich-text 显示"课程介绍整理中" | ✅ |
| 加载失败 | 显示 jst-empty 组件 | ✅ |

## F. 遗留 TODO

- 收藏功能目前仅本地切换状态（后端无收藏接口），待后续迭代
- 学员评价 Tab 原型中有，但任务卡未要求（后端也无评价接口），暂不实现

## G. 我做了任务卡之外的什么

- 修改了购买 toast 文案：从 `购买功能 F9 完成后开放` → `课程购买即将开放`（任务卡要求）
- 添加了 `onShareAppMessage` 微信分享支持
- 添加了 `formatNumber` 数字格式化（1000+ 显示为 1.0k，10000+ 显示为 1.0万）

## H. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/course.js → api/request.js
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image，无 div/span 残留
- [x] 样式全部应用 design-tokens.scss + variables.scss 变量，未硬编码像素值
- [x] 底部操作栏已处理 safe-area-inset-bottom
- [x] rich-text 内容已做 XSS 清洗（去 script/事件属性/javascript:）
