# 任务卡 P2 - 首页 + 公告 tab + 详情页

## ⚠️ 第二波豁免 (主 Agent 临时规则)

**本任务与后端 F-NOTICE 同时进行 (并行模式)**,你必须接受以下事实:
- 你调用的 `/jst/wx/notice/list`、`/jst/wx/index/banner`、`/jst/wx/dict/{type}` 等接口**可能 404 / 500**(后端尚未实现)
- 这是**预期的**,你**不要**因此改成 mock 数据,也不要等后端
- 你的代码必须按 27 文档的接口契约写真实调用,**优雅降级**:
  - 接口失败 → 显示空状态 (jst-empty 组件) 或 loading 兜底
  - 不要弹错误 toast (避免吓到测试人员)
  - 用 try-catch 包裹接口调用
- 后端完成后,**你的代码无需改动**,页面自动有数据
- ❌ **绝对禁止**在 .vue 内 mock 数据,哪怕"先占位也不行" (违反 25 §3.8)
- ✅ 报告中说明"已按 27 文档契约实现,等后端 F-NOTICE 完成后联调"

**前置已就位**:
- ✅ P1 (登录闭环 / store / 公共组件)
- 🟡 F-NOTICE (并行进行中,后端接口可能尚未完成)

## 业务背景
小程序首页 + 公告 tab 的闭环。
- 首页: banner 轮播 + 热门赛事区 + 公告快讯区 + 入口卡片
- 公告: tab 列表 + 详情查看
对应原型: index.html / notice.html / notice-detail.html

## 必读上下文
1. `26-Uniapp用户端架构.md` § 2 § 3 § 7
2. `27-用户端API契约.md` § 3.2 § 3.9
3. `25-从0到1开发流程.md` § 3.8 (禁页面 mock)
4. `30-接口测试指南.md` (知道接口长啥样)
5. P1 的 my/index.vue 已实现 (作为 tab 主页样板)
6. `小程序原型图/index.html` 视觉
7. `小程序原型图/notice.html` 视觉
8. `小程序原型图/notice-detail.html` 视觉
9. `jst-uniapp/api/request.js` 请求封装
10. F-NOTICE 任务卡 (确认接口路径)

## 涉及接口 (F-NOTICE 完成后就绪)
- GET /jst/wx/index/banner → 首页 banner
- GET /jst/wx/notice/list → 公告列表
- GET /jst/wx/notice/{id} → 公告详情
- GET /jst/wx/dict/{dictType} → 取分类字典 (jst_notice 公告分类)
- (可选) GET /jst/wx/contest/hot → 首页热门赛事 (依赖 F7,若 F7 未完成本任务可先用空状态占位)

## 涉及页面

### 1. pages/index/index.vue (新建,tab 主页)
**视觉对齐**: index.html
**结构**:
- 顶部搜索框 (占位,跳 search 占位 toast)
- banner 轮播 (uni-swiper, 数据来自 /jst/wx/index/banner)
- 公告快讯条 (单行滚动,取最新 3 条)
- 入口卡片 4 个: 赛事/课程/公告/积分商城 (前两个 switchTab,后两个 toast)
- 热门赛事区 (取 /jst/wx/contest/hot,无数据则隐藏区块或显示空状态)

**数据**:
- onShow → 并行调 banner + 公告列表前 3 条
- 失败时单独区块显示 jst-empty,不阻塞其他区块

### 2. pages/notice/list.vue (新建,tab 主页)
**视觉对齐**: notice.html
**结构**:
- 顶部分类 tab (从 /jst/wx/dict/jst_notice_category 取,或硬编码 4 个: 全部/赛事/平台/积分/商城)
- 列表卡片 (cover_image + title + 摘要 + publish_time)
- 分页 (uni 下拉加载)
**数据**: onLoad + onShow → /jst/wx/notice/list

### 3. pages-sub/notice/detail.vue (新建)
**视觉对齐**: notice-detail.html
**结构**:
- 标题 + 发布时间 + 富文本 content
- 点赞/分享 (本任务可省略)
**数据**: onLoad 取 query.id → /jst/wx/notice/{id}
**注意**: content 是富文本,用 mp-html 或 rich-text 组件渲染(uniapp 支持的)

## 涉及组件
- 复用 P1 已建的 jst-empty / jst-loading
- 新建 components/jst-banner-swiper/jst-banner-swiper.vue (轮播组件,接受 list prop)
- 新建 components/jst-notice-card/jst-notice-card.vue (公告卡片)

## 涉及 api 文件
- 新建 jst-uniapp/api/notice.js
```javascript
import request from './request'
export const getNoticeList = (params) => request({ url: '/jst/wx/notice/list', method: 'GET', data: params })
export const getNoticeDetail = (id) => request({ url: `/jst/wx/notice/${id}`, method: 'GET' })
export const getBanner = () => request({ url: '/jst/wx/index/banner', method: 'GET' })
export const getDict = (dictType) => request({ url: `/jst/wx/dict/${dictType}`, method: 'GET' })
```

## pages.json 改动
追加:
- pages 数组: index/index, notice/list 作为 tabBar 主页
- subPackages: pages-sub/notice/detail
- tabBar list: 加首页 + 公告 (icons 占位)

## 交付物清单

新增:
- `jst-uniapp/pages/index/index.vue` ⭐
- `jst-uniapp/pages/notice/list.vue` ⭐
- `jst-uniapp/pages-sub/notice/detail.vue`
- `jst-uniapp/api/notice.js`
- `jst-uniapp/components/jst-banner-swiper/jst-banner-swiper.vue`
- `jst-uniapp/components/jst-notice-card/jst-notice-card.vue`
- `jst-uniapp/static/tab/index.png`, `index-on.png`, `notice.png`, `notice-on.png`

修改:
- `jst-uniapp/pages.json` (追加路由 + tabBar)
- `jst-uniapp/styles/variables.scss` (按需补 token)

## 测试场景

### P2-1 启动 → 自动登录 → 首页
应看到:
- banner 至少 1 张 (fixture 中的 top_flag=1 公告)
- 公告快讯条 3 条
- 入口卡片 4 个
- 若 F7 完成则有热门赛事区,否则空

### P2-2 切换到公告 tab
应看到 fixture 中的 3 条公告

### P2-3 点击公告 → 详情
应看到完整富文本 content

### P2-4 公告分类 tab 切换
- 点"赛事"分类 → 仅显示 category=contest 的

### P2-5 异常处理
- banner 接口失败 → 区块隐藏,其他区块正常
- 详情 id 不存在 → 显示空状态/返回上一页

## DoD

- [ ] 4 个核心交互全跑通
- [ ] 没有页面内 mock 数据
- [ ] 所有 API 通过 api/notice.js
- [ ] 富文本渲染无 XSS 风险 (用 rich-text 不允许 script)
- [ ] tab 切换流畅无白屏

## 不许做的事

- ❌ 不许实现搜索功能 (后续 P 任务)
- ❌ 不许写消息中心 (后续)
- ❌ 不许动后端
- ❌ 不许引入新依赖

## 优先级
高 (前端基础闭环关键)

## 预计工作量
4-5 小时
