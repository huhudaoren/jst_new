# 任务卡 FEAT-INDEX-ENRICH-FE - 首页前端丰富化

## 阶段
阶段 F-BUG / **jst-uniapp**（MiniProgram Agent）

## 背景
FEAT-INDEX-ENRICH-BE 已补齐 4 个首页接口。本卡对照原型重写首页，增加推荐赛事、推荐课程、热门标签、专题活动。

## 必读
1. `小程序原型图/index.html` + `index.png` — **必须先 Read PNG**
2. `jst-uniapp/pages/index/index.vue` — 当前实现
3. `subagent/tasks/任务报告/FEAT-INDEX-ENRICH-BE-回答.md` — 确认接口

## 交付物

### Part A — API 封装

**修改** `api/home.js`（如不存在则新建）：
```js
export function getRecommendContests() { return request({ url: '/jst/wx/home/recommend-contests', method: 'GET' }) }
export function getRecommendCourses() { return request({ url: '/jst/wx/home/recommend-courses', method: 'GET' }) }
export function getHotTags() { return request({ url: '/jst/wx/home/hot-tags', method: 'GET' }) }
export function getTopics() { return request({ url: '/jst/wx/home/topics', method: 'GET' }) }
```

### Part B — 首页重写

**修改** `pages/index/index.vue`，对照原型实现以下区块（从上到下）：

1. **搜索栏**（已有，美化：加搜索图标，背景色对齐 token）
2. **Banner 轮播**（已有 `jst-banner-swiper`，保留）
3. **金刚区**（已有，保留）
4. **热门标签行**（新增）：
   - 横向 `scroll-view`，每个标签用 `u-tag` 不同颜色
   - 点击跳转 `pages/contest/list?tag=xxx`
5. **专题活动**（新增）：
   - 1~3 张大卡片（封面图 + 标题 + 描述）
   - 点击跳转 `linkUrl`（从 topic 数据获取）
   - 无数据时不显示此区块
6. **推荐赛事**（新增/增强）：
   - 标题行："热门赛事" + "查看更多 ›"（带箭头图标）
   - 横向 `scroll-view`，复用 `jst-contest-card`（紧凑版）
   - 点击"查看更多"跳转赛事 Tab
7. **推荐课程**（新增）：
   - 标题行："精选课程" + "查看更多 ›"
   - 横向 `scroll-view`，复用 `jst-course-card`（紧凑版）
   - 点击"查看更多"跳转课程 Tab
8. **平台数据**（已有，保留在底部）

### Part C — "查看更多"统一样式

所有"查看更多"改为统一组件/样式：
```vue
<view class="jst-section-header">
  <text class="jst-section-title">热门赛事</text>
  <view class="jst-more-link" @tap="goContestTab">
    <text>查看更多</text>
    <u-icon name="arrow-right" size="24rpx" />
  </view>
</view>
```

### Part D — 数据加载

`onLoad` 中并行请求所有接口（`Promise.allSettled`），单个失败不影响其他区块。每个区块有独立骨架屏。

## DoD
- [ ] 8 个区块对照原型完整
- [ ] 热门标签可点击筛选
- [ ] 专题活动卡片可跳转
- [ ] 推荐赛事/课程横向滑动
- [ ] "查看更多"统一箭头图标
- [ ] 骨架屏
- [ ] Token 化
- [ ] 任务报告 `FEAT-INDEX-ENRICH-FE-回答.md`

## 依赖：FEAT-INDEX-ENRICH-BE ✅
## 优先级：P0
---
派发时间：2026-04-11
