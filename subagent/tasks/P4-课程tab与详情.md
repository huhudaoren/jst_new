# 任务卡 P4 - 课程 tab + 列表 + 详情页

## ⚠️ 第二波豁免 (主 Agent 临时规则)

**本任务与后端 F-COURSE 同时进行 (并行模式)**,你必须接受以下事实:
- 你调用的 `/jst/wx/course/list`、`/jst/wx/course/{id}`、`/jst/wx/course/my` 等接口**可能 404 / 500**(F-COURSE 尚未完成)
- 这是**预期的**,你**不要**因此改成 mock 数据,也不要等后端
- 你的代码必须按 27 文档的接口契约写真实调用,**优雅降级**:
  - 接口失败 → 显示空状态 (jst-empty) 或 loading
  - 不要弹错误 toast
  - 用 try-catch 包裹接口调用
- 后端完成后,**你的代码无需改动**,页面自动有数据
- ❌ **绝对禁止**在 .vue 内 mock 数据
- ✅ 报告中说明"已按 27 文档契约实现,等后端 F-COURSE 完成后联调"

**前置已就位**:
- ✅ P1
- 🟡 F-COURSE (并行进行中)

## 阶段 / 模块
阶段 B / jst-uniapp

## 业务背景
小程序课程 tab 主页 + 详情页。
- 列表: 类型 tab (普通课程 / AI 课程) + 卡片列表
- 详情: 课程信息 + 购买按钮 (本任务购买按钮 toast 占位,等订单 F9-资金主线完成)
- **本任务不做视频播放器** (依赖 VOD F12,后续 P-COURSE-PLAY 单独任务)

对应原型: course-list.html / course-detail.html

## 必读上下文
1. `26-Uniapp用户端架构.md`
2. `27-用户端API契约.md` § 3.3
3. P1/P2/P3 已建的页面 (作为样板)
4. `小程序原型图/course-list.html`
5. `小程序原型图/course-detail.html`
6. F-COURSE 任务卡

## 涉及接口
- GET /jst/wx/course/list
- GET /jst/wx/course/{id}
- GET /jst/wx/course/my (我的课程,登录态需要)

## 涉及页面

### 1. pages/course/list.vue (tab 主页)
**视觉对齐**: course-list.html
**结构**:
- 顶部 segmented control: 普通课程 / AI 课程 (对应 courseType=normal/ai_maic)
- 卡片列表 (cover/name/price 现金 + 积分价 / 简介)
- 分页

**数据**:
- onLoad/onShow → 调 list (默认 normal)
- 切换类型 → 重调 list

### 2. pages-sub/course/detail.vue
**视觉对齐**: course-detail.html
**结构**:
- 顶部 cover
- 名称 / 类型徽章
- 价格 (现金 + 积分)
- 富文本简介
- 「立即购买」固定底部按钮 (本任务 toast "购买功能 F9 完成后开放")

**数据**: query.id → /jst/wx/course/{id}

## 涉及组件
- 复用 jst-status-badge (P3 已建)
- 新建 components/jst-course-card/jst-course-card.vue

## api 文件
- 新建 jst-uniapp/api/course.js
```javascript
import request from './request'
export const getCourseList = (params) => request({ url: '/jst/wx/course/list', method: 'GET', data: params })
export const getCourseDetail = (id) => request({ url: `/jst/wx/course/${id}`, method: 'GET' })
export const getMyCourses = () => request({ url: '/jst/wx/course/my', method: 'GET' })
```

## pages.json 改动
- pages: course/list 加入 tabBar
- subPackages: pages-sub/course 子包

## 交付物清单

新增:
- `jst-uniapp/pages/course/list.vue` ⭐
- `jst-uniapp/pages-sub/course/detail.vue`
- `jst-uniapp/api/course.js`
- `jst-uniapp/components/jst-course-card/jst-course-card.vue`
- `jst-uniapp/static/tab/course.png`, `course-on.png`

修改:
- `jst-uniapp/pages.json`

## 测试场景

### P4-1 列表 应看到 fixture 中已上架的 2 门课
### P4-2 切换类型 (normal/ai_maic) → 列表过滤
### P4-3 点击卡片 → 详情
### P4-4 「立即购买」 toast 占位
### P4-5 (P1 我的页) 我的课程入口 toast 占位 → 改为跳本 tab 或 my-course 子页

## DoD
- [ ] 5 个场景跑通
- [ ] 无页面 mock
- [ ] 富文本无 XSS

## 不许做的事
- ❌ 不许实现视频播放 (P-COURSE-PLAY 任务)
- ❌ 不许实现购买 (依赖订单)
- ❌ 不许动后端

## 优先级
中高

## 预计工作量
3-4 小时
