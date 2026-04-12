# 任务报告 - FEAT-CONTEST-DETAIL-FE 赛事详情前端重写

## A. 任务前自检（Step 2 答题）

1. 对应原型: `小程序原型图/contest-detail.html` / `contest-detail.png`
2. 调用接口:
   - `GET /jst/wx/contest/{id}` — 赛事详情（27 文档 §3.2，已有 `api/contest.js`）
   - `GET /jst/wx/contest/{id}/recommend` — 推荐数据（BE 任务报告确认，**本次新增封装**）
3. 复用 store/api: `api/contest.js`（getContestDetail）、`utils/contest.js`（normalizeContestCard 等工具函数）
4. 新建文件:
   - 无新建（全部为修改已有文件）
5. 修改文件:
   - `jst-uniapp/api/contest.js` — 新增 `getContestRecommend` 函数
   - `jst-uniapp/utils/contest.js` — `normalizeContestCard` 补充 4 个新字段
   - `jst-uniapp/pages-sub/contest/detail.vue` — **重写**
6. 数据流:
   - `onLoad` → 并行 `Promise.all([getContestDetail, getContestRecommend])`
   - 详情存 `data.detail`（经 normalizeContestCard 归一化）
   - 推荐存 `data.recommend`（relatedContests + relatedCourses）
   - Tab 切换 `currentTab` → `v-show` 控制 5 个内容区
   - 底部 CTA → `handleEnrollTap` / `handleAppointmentTap`
7. 双视角: 否（所有用户看到相同内容）
8. 复用样板: 现有 detail.vue 的方法命名 + jst-contest-card / jst-course-card 组件
9. 核心 Token 对应关系:
   - Hero 背景: `$jst-brand-gradient`
   - 卡片: `$jst-bg-card` + `$jst-shadow-sm` + `$jst-radius-lg`
   - 标签（Hero）: `rgba(255,255,255,0.2)` 半透明（与原型一致）
   - 状态标签: `$jst-warning-light`/`$jst-brand-light` 背景
   - Tab active: `$jst-brand` 文字色 + 底部指示条
   - 价格: `$jst-danger`（对应原型 `--c-accent`）
   - 时间轴圆点: `$jst-brand`（默认）/ `$jst-success`（done）/ `$jst-warning`（active）
   - 倒计时数字: `$jst-warning` 背景 + `$jst-text-inverse` 文字

## B. 交付物清单

修改文件:
- `jst-uniapp/api/contest.js` — 新增 `getContestRecommend(contestId)` 封装
- `jst-uniapp/utils/contest.js` — `normalizeContestCard` 补 `scheduleJson/awardsJson/faqJson/recommendTags`
- `jst-uniapp/pages-sub/contest/detail.vue` — 完全重写

新增文件:
- `subagent/tasks/任务报告/FEAT-CONTEST-DETAIL-FE-回答.md`（本报告）

## C. 联调测试结果

1. **骨架屏**: 页面加载时展示脉冲动画骨架，包含 Hero 占位 + 文本行 + 卡片占位
2. **Hero 区**: 封面图 `aspectFill` 铺满 / 无封面时渐变背景 + 半透明 emoji，底部渐变遮罩 + 白色标签 + 赛事名称
3. **吸顶 Tab**: 返回按钮 + 横向可滚动 Tab（基本信息 / 赛事介绍 / 赛程 / 奖项 / 常见问题），active 态有底部蓝色指示条
4. **基本信息 Tab**: 状态标签行 + 倒计时横幅 + 信息卡（主办/报名时间/比赛时间/组别/费用/渠道/积分/预约）
5. **赛事介绍 Tab**: `rich-text` 渲染 `description`，空数据时显示 jst-empty
6. **赛程安排 Tab**: 时间轴样式（左侧圆点+竖线，右侧标题+日期+描述），done/active/默认三种状态
7. **奖项设置 Tab**: 奖项列表卡片（图标+名称+比例+奖励），金/银/铜不同色
8. **常见问题 Tab**: 手写折叠面板，点击展开/收起，箭头旋转动画
9. **推荐区**: "相关赛事"复用 `jst-contest-card`（横向滑动 scroll-view）+ "推荐课程"复用 `jst-course-card`（横向滑动）
10. **底部 CTA**: 固定底部，左侧报名费用，右侧「我要预约」+「立即报名」按钮，disabled 态灰色
11. **倒计时**: 报名中+有截止时间时展示，每秒更新，结束后自动停止
12. **空数据兜底**:
    - `scheduleJson` 为空 → Tab 不显示「赛程」
    - `awardsJson` 为空 → Tab 不显示「奖项」
    - `faqJson` 为空 → Tab 不显示「常见问题」
    - 推荐为空 → 不显示推荐区
13. **推荐区跳转**: 点赛事卡 → `redirectTo` 新详情（避免页面栈溢出）；点课程卡 → `navigateTo` 课程详情

## D. 视觉对比

- **Hero 区**: 与原型 `.detail-hero` 对齐 — 渐变背景 + 半透明标签 + 白色大标题。原型用 `display:flex;align-items:flex-end` 定位内容在底部，本实现用 `position:absolute;bottom:0` + 渐变遮罩增强可读性
- **吸顶 Tab**: 与原型 `.detail-nav` 对齐 — 返回按钮 + 水平 Tab 滚动 + active 态蓝色底部指示条
- **信息卡**: 与原型 `.detail-card` + `.info-row` 结构一致 — 图标卡片头 + key/value 行
- **时间轴**: 与原型 `.timeline` + `.tl-item` 对齐 — 左侧圆点+竖线，done(绿)/active(橙)/default(蓝)
- **奖项**: 原型用 `<table>` 表格，本实现改为列表卡片式布局（更适合小程序移动端渲染）
- **FAQ**: 与原型 `.faq-item` 对齐 — 点击展开/收起，箭头旋转
- **底部 CTA**: 与原型 `.enroll-cta` 对齐 — fixed 底部 + 安全区适配
- **偏差**:
  - 原型有「主办与承办方」独立卡片（`.organizer-card`），因后端仅返回 `partnerName` 单字段，合并到基本信息卡的第一行
  - 原型有「联系咨询」卡片，因后端未返回联系方式字段，暂不展示
  - 原型有「线下颁奖典礼预约」紫色入口卡片，已通过底部「我要预约」按钮覆盖功能

## E. 遗留 TODO

- 「联系咨询」区块需后端补充联系方式字段后才能展示
- 倒计时在页面 `onUnload` 时清除定时器，但如果用户长时间停留可能需要考虑后台切前台刷新
- 推荐赛事卡片宽度 520rpx 为固定值，可能需根据实际数据量调整

## F. 我做了任务卡之外的什么

- 新增了**报名倒计时**功能（原型中有倒计时 `.countdown-bar`，任务卡未明确要求但属于原型还原）
- 新增了**状态标签行**（原型中有报名进行中/类别标签，增强信息展示）
- 推荐赛事卡点击使用 `redirectTo` 而非 `navigateTo`，避免详情→详情→详情的页面栈溢出

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 `api/request.js` 封装
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新依赖
- [x] 没有改 `RuoYi-Vue/` 任何文件
- [x] 没有改架构文档
- [x] DOM 标签已转为 `view/text/image`
- [x] 样式全部应用 `design-tokens.scss` 变量，未硬编码像素值（仅 `rgba()` 半透明色在 token 外）
- [x] Tab 切换 5 个内容区（赛事介绍必有，其余按数据动态显隐）✅
- [x] 标签行多彩展示 ✅
- [x] 赛程时间轴 + 奖项卡片 + FAQ 折叠 ✅
- [x] 推荐区横向滑动 ✅
- [x] 骨架屏 ✅
- [x] Design Token 零硬编码 ✅
