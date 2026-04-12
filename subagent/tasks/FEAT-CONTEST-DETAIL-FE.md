# 任务卡 FEAT-CONTEST-DETAIL-FE - 赛事详情前端重写

## 阶段
阶段 F-BUG / **jst-uniapp**（MiniProgram Agent）

## 背景
FEAT-CONTEST-DETAIL-BE 已完成后端补字段+推荐接口。本卡对照原型重写赛事详情页前端。

## 必读
1. `CLAUDE.md`
2. `小程序原型图/contest-detail.html` + `contest-detail.png` — **必须先 Read PNG**
3. `jst-uniapp/pages-sub/contest/detail.vue` — 当前实现
4. `jst-uniapp/api/contest.js` — 已有接口封装
5. `subagent/tasks/任务报告/FEAT-CONTEST-DETAIL-BE-回答.md` — 确认后端字段和接口

## 交付物

### Part A — API 封装

**修改** `api/contest.js`，补：
```js
export function getContestRecommend(contestId) {
  return request({ url: '/jst/wx/contest/' + contestId + '/recommend', method: 'GET' })
}
```

### Part B — 赛事详情页重写

**修改** `pages-sub/contest/detail.vue`，对照原型 `contest-detail.html` 实现：

#### 页面结构（从上到下）

1. **封面区**：赛事封面图 + 赛事名称 + 标签行（`recommendTags` 拆分，每个标签用 `u-tag` 不同颜色）
2. **信息卡**：报名时间 / 比赛时间 / 价格 / 报名人数
3. **Tab 切换区**：用 `u-tabs` 或自定义 tab
   - **赛事介绍**：`rich-text` 渲染 `contestDesc`
   - **赛程安排**：解析 `scheduleJson`，时间轴样式（左侧圆点+竖线，右侧内容）
   - **奖项设置**：解析 `awardsJson`，卡片列表（奖项名+等级+描述+名额）
   - **常见问题**：解析 `faqJson`，折叠面板（`u-collapse` / 手写 accordion）
4. **推荐区**：
   - "相关赛事" + 横向滑动 `scroll-view`，复用 `jst-contest-card`
   - "推荐课程" + 横向滑动 `scroll-view`，复用 `jst-course-card`
5. **底部固定栏**：收藏（可选）+ 立即报名按钮

#### Tab 切换逻辑
- data 中加 `currentTab: 0`
- tab 切换只改 `currentTab`，用 `v-if` 或 `v-show` 控制内容区
- 赛程/奖项/FAQ 数据从详情接口的 JSON 字段解析（`JSON.parse`），加 try-catch 防异常

#### 推荐数据
- `onLoad` 时并行请求详情 + 推荐
- 推荐数据存 `data.relatedContests` / `data.relatedCourses`

### Part C — 样式要求

- 所有样式用 Design Token（`$jst-*` 变量）
- 标签颜色映射：预定义 5~6 种标签背景色循环使用
- Tab 切换有过渡动画
- 骨架屏（`u-skeleton`）首屏加载
- 时间轴、奖项卡片、FAQ 折叠面板的样式对照原型做到**等于或超越**

### Part D — 空数据兜底

- `scheduleJson` 为空 → 不显示赛程 Tab
- `awardsJson` 为空 → 不显示奖项 Tab
- `faqJson` 为空 → 不显示常见问题 Tab
- 推荐为空 → 不显示推荐区

## DoD
- [ ] Tab 切换 5 个内容区（赛事介绍必有，其余按数据动态显隐）
- [ ] 标签行多彩展示
- [ ] 赛程时间轴 + 奖项卡片 + FAQ 折叠
- [ ] 推荐区横向滑动
- [ ] 骨架屏
- [ ] Design Token 零硬编码
- [ ] 任务报告 `FEAT-CONTEST-DETAIL-FE-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许改 api/contest.js 已有函数的签名
- ❌ 不许 mock 数据
- ❌ 不许改报名/支付流程

## 依赖：FEAT-CONTEST-DETAIL-BE ✅
## 优先级：P0
---
派发时间：2026-04-11
