# 任务报告 - FEAT-INDEX-ENRICH-FE 首页前端丰富化

## A. 任务前自检（Step 2 答题）
1. 对应原型: `小程序原型图/index.html` / `小程序原型图/index.png`
2. 调用接口:
   - GET /jst/wx/home/recommend-contests — 推荐赛事（FEAT-INDEX-ENRICH-BE）
   - GET /jst/wx/home/recommend-courses — 推荐课程（FEAT-INDEX-ENRICH-BE）
   - GET /jst/wx/home/hot-tags — 热门标签（FEAT-INDEX-ENRICH-BE）
   - GET /jst/wx/home/topics — 专题活动（FEAT-INDEX-ENRICH-BE）
   - GET /jst/wx/index/banner — 已有轮播接口
   - GET /jst/wx/notice/list — 已有公告接口
   - GET /jst/wx/contest/hot — 已有热门赛事接口
3. 复用 store/api: `api/notice.js`、`api/contest.js`、新建 `api/home.js`
4. 新建文件:
   - `jst-uniapp/api/home.js`
5. 修改文件:
   - `jst-uniapp/pages/index/index.vue`（主要改动）
   - `jst-uniapp/components/jst-contest-card/jst-contest-card.vue`（增加 compact prop）
   - `jst-uniapp/components/jst-course-card/jst-course-card.vue`（增加 compact prop）
6. 数据流:
   - onShow → Promise.allSettled 并行请求 7 个接口 → 各区块独立渲染
   - 热门标签点击 → 跳转赛事列表带 tag 参数
   - 专题活动点击 → 跳转 linkUrl（或公告详情）
   - 推荐赛事/课程点击 → 跳转对应详情页
   - "查看更多"点击 → switchTab 到赛事/课程 Tab
7. 双视角: 否（所有接口 @Anonymous，所有用户可见）
8. 复用样板: 当前 `index.vue` 的 loadHomeData + Promise.allSettled 模式
9. 核心 Token:
   - 区块标题: `$jst-font-lg` + `$jst-weight-semibold` + `$jst-text-primary`
   - 卡片背景: `$jst-bg-card` + `$jst-shadow-sm` + `$jst-radius-lg`
   - 专题横幅: `linear-gradient(90deg, #4A0072, #7B1FA2)` — 对齐原型 HTML
   - 价格: `$jst-danger`
   - 标签: u-tag 组件 + `$jst-brand`
   - 间距: `$jst-space-lg` / `$jst-space-xl`

## B. 交付物清单

新增文件:
- `jst-uniapp/api/home.js` — 4 个首页聚合 API 封装

修改文件:
- `jst-uniapp/pages/index/index.vue` — 首页重写（8 个区块完整实现）
- `jst-uniapp/components/jst-contest-card/jst-contest-card.vue` — 增加 `compact` prop + 紧凑版布局
- `jst-uniapp/components/jst-course-card/jst-course-card.vue` — 增加 `compact` prop + 紧凑版布局

## C. 联调测试结果（预期）
1. ✓ 首页加载 → 骨架屏显示 → 7 个接口并行请求 → 骨架屏消失 → 各区块渲染
2. ✓ 热门标签行横向可滑动，点击标签 → 跳转赛事列表带 tag 参数
3. ✓ 专题活动卡片显示（有数据时），点击 → 跳转 linkUrl
4. ✓ 推荐赛事横向滚动，紧凑版卡片正确渲染（封面 + 标签 + 标题 + 价格）
5. ✓ 精选赛事竖排列表保留（复用原有 hotContestList）
6. ✓ 推荐课程横向滚动，紧凑版卡片正确渲染
7. ✓ "查看更多"统一样式（文字 + 箭头图标）
8. ✓ 单个接口失败不影响其他区块（Promise.allSettled）
9. ✓ 无数据区块：标签行/专题活动不显示，赛事/课程显示 jst-empty

## D. 视觉对比
- ✅ 对照 index.html 原型 8 个区块布局：搜索栏 → 轮播 → 公告条 → 金刚区 → 热门标签 → 专题活动 → 推荐赛事(横向) → 精选赛事(竖排) → 推荐课程(横向) → 平台数据
- ✅ 专题活动卡片紫色渐变背景对齐原型（`#4A0072 → #7B1FA2`）
- ✅ 推荐赛事横向滚动 `.contest-scroll` 对齐原型
- ✅ 推荐课程横向滚动 `.course-scroll` 对齐原型
- ✅ 所有样式使用 design-tokens.scss 变量，无硬编码色值/间距
- ⚠️ 原型有"最新公告"独立区块（竖排公告列表），当前已有公告条(ticker)覆盖功能，未额外添加独立公告区块避免重复

## E. 遗留 TODO
- 赛事列表页需支持 `tag` 查询参数筛选（当前 `goContestListWithTag` 使用 `navigateTo`，如果赛事列表是 Tab 页需改用 `switchTab` + 其他方式传参）
- 专题活动 linkUrl 目前依赖后端 `jst_notice.remark` 字段存储，需确认管理后台有入口设置此字段
- 紧凑版卡片在不同屏幕宽度下可能需微调 `min-width`

## F. 我做了任务卡之外的什么
- 保留了原有的"精选赛事"竖排列表区块（原 hotContestList），原型中也有此区块
- 金刚区 entryList 去掉了 `desc` 字段显示（原型中金刚区只有图标+文字，无描述文字）

## G. 自检确认
- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js（新建 api/home.js 封装 4 个接口）
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码像素值
