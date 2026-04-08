# 任务报告 - P3 赛事 tab 与详情

## A. 任务前自检（Step 2 答题）
1. 对应原型：
   - `小程序原型图/contest-list.html`
   - `小程序原型图/contest-detail.html`
2. 调用接口：
   - `GET /jst/wx/contest/list`（27 文档 §3.2）
   - `GET /jst/wx/contest/{id}`（27 文档 §3.2）
   - `GET /jst/wx/contest/hot`（27 文档 §3.2，首页热门赛事区复用）
   - `GET /jst/wx/contest/categories`（27 文档 §3.2）
3. 复用 store/api：
   - `jst-uniapp/api/request.js`
   - `jst-uniapp/pages/index/index.vue` 现有首页结构
   - 本任务未新增跨页共享状态，不涉及新增 store
4. 新建文件：
   - `jst-uniapp/pages/contest/list.vue`
   - `jst-uniapp/pages-sub/contest/detail.vue`
   - `jst-uniapp/api/contest.js`
   - `jst-uniapp/components/jst-contest-card/jst-contest-card.vue`
   - `jst-uniapp/components/jst-status-badge/jst-status-badge.vue`
   - `jst-uniapp/utils/contest.js`
   - `jst-uniapp/static/tab/contest.png`
   - `jst-uniapp/static/tab/contest-on.png`
5. 数据流：
   - `pages/contest/list.vue onLoad` 并行请求 `categories + list`，分类切换后重查列表，触底继续请求下一页
   - 点击赛事卡片后跳转 `pages-sub/contest/detail.vue?id=...`
   - 详情页 `onLoad` 调 `getContestDetail(id)` 渲染封面、时间、描述与按钮状态
   - 首页 `pages/index/index.vue` 的热门赛事区调 `getContestHot()`，也复用赛事卡片并跳详情
6. 双视角：
   - 否。本任务页面不区分学生/老师视角，所有用户都可见
7. 复用样板：
   - `pages/login/login.vue` 的 Options API + async methods 写法
   - `pages/index/index.vue` 现有首页 section 结构

## B. 交付物清单
新增文件：
- `jst-uniapp/pages/contest/list.vue`
- `jst-uniapp/pages-sub/contest/detail.vue`
- `jst-uniapp/api/contest.js`
- `jst-uniapp/components/jst-contest-card/jst-contest-card.vue`
- `jst-uniapp/components/jst-status-badge/jst-status-badge.vue`
- `jst-uniapp/utils/contest.js`
- `jst-uniapp/static/tab/contest.png`
- `jst-uniapp/static/tab/contest-on.png`

修改文件：
- `jst-uniapp/pages.json`
- `jst-uniapp/pages/index/index.vue`
- `jst-uniapp/api/request.js`

## C. 联调测试结果
1. `P3-1 列表加载`：已按 `/jst/wx/contest/list` 真接口接入；未本地运行，待 HBuilderX/Chrome 联调验证
2. `P3-2 切换分类`：已按 `/jst/wx/contest/categories` + 切换后重查列表实现；未本地运行
3. `P3-3 下拉加载下一页`：已用 `onReachBottom` + `pageNum` 递增实现；未本地运行
4. `P3-4 点击卡片跳详情`：列表页和首页热门区都已跳到 `/pages-sub/contest/detail?id=...`
5. `P3-5 详情完整渲染`：已按详情接口字段渲染封面、时间、分类、价格、描述；描述经过前端安全清洗
6. `P3-6 报名按钮状态`：已按 `enrollOpen/status` 显示“立即报名 / 未开始 / 已结束”
7. `P3-7 点报名按钮`：已实现 toast 占位“F9 完成后开放”

说明：
- 本次未在本机 HBuilderX / 内置浏览器完成真实页面跑通，当前结论是“代码已完成、待联调验证”
- 已按任务卡第二波豁免要求，对 contest 相关接口使用 `silent: true` + try/catch，避免 F7 未完成时误弹错误 toast

## D. 视觉对比
- 列表页按 `contest-list.html` 保留了顶部搜索占位、横向分类 tab、卡片列表与分页加载
- 详情页按 `contest-detail.html` 保留了大图、基础信息、赛事介绍和底部固定 CTA
- 首页热门赛事区已替换为公共 `jst-contest-card`
- 偏差：`contest.png` / `contest-on.png` 当前由现有 `index` tab 图标复制生成，仅为资源占位，待后续替换为专属赛事图标

## E. 遗留 TODO
- 需要在 HBuilderX 或 Chrome 中实际跑一次 7 个场景，确认样式和跳转无误
- 赛事 tab 图标目前是占位资源，建议后续补正式图标
- `pages/index/index.vue` 内仍保留了旧首页模块逻辑，建议后续做一次统一清理，但本次未扩范围

## F. 我做了任务卡之外的什么
- 新增了 `utils/contest.js` 统一做赛事字段归一、时间格式化、状态文案和富文本安全清洗，减少列表/详情重复逻辑

## G. 自检确认
- [x] 没有页面内 mock 数据
- [x] 所有赛事 API 通过 `api/request.js`
- [x] contest 相关失败场景使用 `silent: true` 做优雅降级
- [x] 没有引入新依赖
- [x] 没有改 `RuoYi-Vue`
- [x] 没有改 `架构设计/`
- [x] 页面样式单位使用 `rpx`
- [x] 页面颜色使用现有 design token / 变量
