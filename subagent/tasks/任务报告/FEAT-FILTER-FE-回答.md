# 任务报告 - FEAT-FILTER-FE 多级筛选前端

## A. 任务前自检（Step 2 答题）

1. **对应原型**: `小程序原型图/contest-list.png` + `contest-list.html` / `course-list.png` + `course-list.html`
2. **调用接口**:
   - `GET /jst/wx/dict/contest-categories` — 返回 `[{label, value}]`
   - `GET /jst/wx/dict/course-categories` — 返回 `[{label, value}]`
   - `GET /jst/wx/contest/list` — 支持 `category/sortBy/eventType/keyword` (api/contest.js)
   - `GET /jst/wx/course/list` — 支持 `category/sortBy/courseType` (api/course.js)
3. **复用 store/api**: `api/contest.js`、`api/course.js`、`utils/contest.js`、`utils/visual-effects.js`
4. **新建文件**:
   - `jst-uniapp/components/jst-filter-bar/jst-filter-bar.vue` — 通用筛选组件
   - `jst-uniapp/api/dict.js` — 字典分类接口
5. **数据流**:
   - 赛事列表: onLoad → fetchCategories() + fetchContestList() 并行 → 渲染分类 Tab + 列表
   - 用户点分类 Tab → onCategoryTap → 更新 filterValue.category → resetAndLoadList
   - 用户点排序/筛选 → jst-filter-bar @change → onFilterChange → resetAndLoadList
   - 课程列表: 同理，胶囊横滑 + 排序筛选栏
6. **双视角**: 否（公开列表页面）
7. **复用样板**: 原有 contest/list.vue 骨架 + u-skeleton + u-loadmore
8. **核心 Token 对应**:
   - 分类 Tab 激活: `$jst-brand` fg + 下划线
   - 筛选胶囊激活: `$jst-brand` fg + `$jst-brand-light` bg
   - 搜索框背景: `$jst-bg-grey`（赛事）/ `rgba(255,255,255,0.15)`（课程 hero 内）
   - 筛选面板遮罩: `rgba(26,31,46,0.45)`
   - 多彩标签: primary/success/warning/info 四色循环

## B. 交付物清单

**新增文件**:
- `jst-uniapp/components/jst-filter-bar/jst-filter-bar.vue` — 通用筛选栏组件
- `jst-uniapp/api/dict.js` — 字典分类接口封装

**修改文件**:
- `jst-uniapp/pages/contest/list.vue` — 加分类 Tab + 筛选栏 + 搜索美化
- `jst-uniapp/pages/course/list.vue` — 加分类胶囊 + 排序筛选 + 搜索栏
- `jst-uniapp/components/jst-contest-card/jst-contest-card.vue` — 加多彩推荐标签行

## C. 各 Part 对照 DoD

| Part | 要求 | 实现 | 状态 |
|------|------|------|------|
| A | jst-filter-bar 通用组件 | 支持 grid/radio/multi 3 种类型，遮罩+滑下面板 | ✅ |
| B | 赛事列表 3 维筛选 | 分类 Tab + 排序 radio + 筛选 multi(赛事形式) | ✅ |
| B | 多彩标签 | recommendTags 逗号分隔 → 4 色循环渲染 | ✅ |
| B | 搜索美化 | 自定义搜索框，圆角+图标+清除按钮 | ✅ |
| C | 课程列表 2 维筛选 | 分类胶囊横滑 + 排序 radio | ✅ |
| D | API 封装 | api/dict.js: getContestCategories + getCourseCategories | ✅ |

## D. jst-filter-bar 组件说明

**Props**:
- `filters: Array` — 筛选配置 `[{key, label, type, options?, groups?}]`
  - `type: 'grid'` — 4 列网格单选，选中即关闭
  - `type: 'radio'` — 单选列表带勾，选中即关闭
  - `type: 'multi'` — 分组多选，底部重置/确认
- `value: Object` — 当前筛选值 `{key: value}`

**Events**:
- `@change(newValue)` — 筛选变化时触发
- `@input(newValue)` — 支持 v-model

**UI 行为**:
- 顶部按钮行：文字 + 箭头，有活动值时高亮品牌色
- 下拉面板：半透明遮罩 + 内容区从顶部滑下动画
- 点击遮罩关闭

## E. 视觉对比

- ✅ 赛事列表分类 Tab 横滑：对齐原型 `.cat-tabs` 样式
- ✅ 筛选胶囊：对齐原型 `.filter-pill` 样式
- ✅ 搜索栏：圆角 + 搜索图标 + 清除按钮
- ✅ 课程列表搜索：在 hero 渐变背景内，半透明搜索框
- ✅ 多彩标签行：primary/success/warning/info 四色
- ✅ 全部样式使用 design-tokens.scss 变量

## F. 遗留 TODO

- 赛事列表原型中有"状态筛选胶囊行"（报名中/即将开始/线上赛事等），当前合并到 filter-bar 的 multi 类型中。如需独立胶囊行可后续拆分
- 课程列表原型有"线上课程/线下课程" Tab，当前保留为"普通课程/AI 课程"（与现有 courseType 字段对齐）
- 分类接口返回空时胶囊/Tab 仅显示"全部"

## G. 我做了任务卡之外的什么

- 赛事列表搜索栏从 `u-search` 改为自定义实现，以对齐原型搜索框样式（圆角 + 图标 + 清除按钮）
- 课程列表增加了搜索栏（原 list.vue 无搜索功能）

## H. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js 封装
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码
- [x] 搜索 input font-size ≥ 16px (32rpx)
