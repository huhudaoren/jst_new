# 任务卡 P3 - 赛事 tab + 列表筛选 + 详情页

## ⚠️ 第二波豁免 (主 Agent 临时规则)

**本任务与后端 F7 同时进行 (并行模式)**,你必须接受以下事实:
- 你调用的 `/jst/wx/contest/list`、`/jst/wx/contest/{id}`、`/jst/wx/contest/categories` 等接口**可能 404 / 500**(F7 尚未完成)
- 这是**预期的**,你**不要**因此改成 mock 数据,也不要等后端
- 你的代码必须按 27 文档的接口契约写真实调用,**优雅降级**:
  - 接口失败 → 显示空状态 (jst-empty) 或 loading
  - 不要弹错误 toast (避免吓到测试人员)
  - 用 try-catch 包裹接口调用
- 后端完成后,**你的代码无需改动**,页面自动有数据
- ❌ **绝对禁止**在 .vue 内 mock 数据 (违反 25 §3.8)
- ✅ 报告中说明"已按 27 文档契约实现,等后端 F7 完成后联调"

**前置已就位**:
- ✅ P1 (登录 + store)
- 🟡 F7 (并行进行中)
- 🟡 F-NOTICE (字典接口,并行进行中,如未完成则分类 tab 显示 loading)

## 阶段 / 模块
阶段 B / jst-uniapp

## 业务背景
小程序赛事 tab 主页 + 详情页。这是核心入口,后续 P-ENROLL 任务做报名流程。
- 列表: 顶部分类 tab + 卡片列表 + 分页
- 详情: 完整赛事信息 + 报名按钮 (本任务报名按钮跳占位 toast,等 F9 完成后再绑接口)

对应原型: contest-list.html / contest-detail.html

## 必读上下文
1. `26-Uniapp用户端架构.md`
2. `27-用户端API契约.md` § 3.2
3. `25-从0到1开发流程.md` § 3.8
4. P1 的页面样板
5. `小程序原型图/contest-list.html`
6. `小程序原型图/contest-detail.html`
7. F7 任务卡 (确认接口契约)

## 涉及接口 (F7 完成后就绪)
- GET /jst/wx/contest/list → 列表查询
- GET /jst/wx/contest/{id} → 详情
- GET /jst/wx/contest/hot → 热门 (P2 已用)
- GET /jst/wx/contest/categories → 分类列表

## 涉及页面

### 1. pages/contest/list.vue (新建,tab 主页)
**视觉对齐**: contest-list.html
**结构**:
- 顶部搜索框 (占位)
- 顶部横滚分类 tab (从 /jst/wx/contest/categories 取)
- 卡片列表 (uni-list, 分页加载)
- 卡片字段: cover/name/category/price/enroll period/status badge

**数据**:
- onLoad → 并行调 categories + list
- 切换分类 → 重新调 list (传 category 参数)
- onReachBottom → 加载下一页

### 2. pages-sub/contest/detail.vue (新建)
**视觉对齐**: contest-detail.html
**结构**:
- 顶部 cover 大图
- 名称 / 分类 / 标价
- 报名时间 / 比赛时间
- 富文本 description
- 报名按钮 (固定底部)
  - 当前在报名期 → 显示「立即报名」(点击 toast "F9 完成后开放,跳 enroll.html 占位")
  - 报名未开始 → 「未开始」灰色
  - 报名结束 → 「已结束」灰色

**数据**: onLoad 取 query.id → /jst/wx/contest/{id}

### 3. (复用) pages-sub/contest/search.vue
**本任务不实现**,toast 占位

## 涉及组件
- 新建 components/jst-contest-card/jst-contest-card.vue (赛事卡片,P2 首页热门赛事区也用)
- 新建 components/jst-status-badge/jst-status-badge.vue (状态标签,通用,enrolling/closed/scoring/published)

## api 文件
- 新建 jst-uniapp/api/contest.js
```javascript
import request from './request'
export const getContestList = (params) => request({ url: '/jst/wx/contest/list', method: 'GET', data: params })
export const getContestDetail = (id) => request({ url: `/jst/wx/contest/${id}`, method: 'GET' })
export const getContestHot = (limit=6) => request({ url: '/jst/wx/contest/hot', method: 'GET', data: { limit } })
export const getContestCategories = () => request({ url: '/jst/wx/contest/categories', method: 'GET' })
```

## pages.json 改动
- pages: contest/list 加入 tabBar
- subPackages: pages-sub/contest 子包包含 detail

## 交付物清单

新增:
- `jst-uniapp/pages/contest/list.vue` ⭐
- `jst-uniapp/pages-sub/contest/detail.vue` ⭐
- `jst-uniapp/api/contest.js`
- `jst-uniapp/components/jst-contest-card/jst-contest-card.vue`
- `jst-uniapp/components/jst-status-badge/jst-status-badge.vue`
- `jst-uniapp/static/tab/contest.png`, `contest-on.png`

修改:
- `jst-uniapp/pages.json` (追加路由 + tabBar)
- `jst-uniapp/pages/index/index.vue` (P2 已建) ⚠️ 改其热门赛事区使用 jst-contest-card 组件并对接接口

## 测试场景

### P3-1 列表加载 应看到 fixture 中的 5 张赛事
### P3-2 切换分类 → 列表筛选生效
### P3-3 下拉加载下一页
### P3-4 点击卡片 → 跳详情
### P3-5 详情完整渲染
### P3-6 报名按钮根据状态显示不同文字/灰禁用
### P3-7 点报名按钮 toast 占位

## DoD

- [ ] 7 个场景全跑通
- [ ] 没有页面内 mock
- [ ] 状态徽章颜色与原型一致
- [ ] 富文本渲染无 XSS

## 不许做的事

- ❌ 不许实现报名提交 (F9 + P-ENROLL 任务)
- ❌ 不许实现搜索 (后续)
- ❌ 不许动后端
- ❌ 不许引入新依赖

## 优先级
高

## 预计工作量
4-5 小时
