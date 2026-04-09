# 任务报告 - P4 课程 tab 与详情

## A. 任务前自检（Step 2 答题）
1. 对应原型：
   - `小程序原型图/course-list.html`
   - `小程序原型图/course-detail.html`
2. 调用接口：
   - `GET /jst/wx/course/list`（`27-用户端API契约.md` §3.3）
   - `GET /jst/wx/course/{id}`（`27-用户端API契约.md` §3.3）
   - `GET /jst/wx/course/my`（`27-用户端API契约.md` §3.3，本次仅在 `api/course.js` 预留，当前“我的课程”入口改为跳课程 tab）
3. 复用 store/api：
   - `jst-uniapp/api/request.js`
   - `jst-uniapp/store/user.js`（沿用登录态体系）
   - `jst-uniapp/components/jst-empty/jst-empty.vue`
   - `jst-uniapp/components/jst-loading/jst-loading.vue`
4. 新建文件：
   - `jst-uniapp/pages/course/list.vue`
   - `jst-uniapp/pages-sub/course/detail.vue`
   - `jst-uniapp/api/course.js`
   - `jst-uniapp/components/jst-course-card/jst-course-card.vue`
   - `jst-uniapp/components/jst-status-badge/jst-status-badge.vue`
   - `jst-uniapp/static/tab/course.png`
   - `jst-uniapp/static/tab/course-on.png`
5. 数据流：
   - 课程 tab：`onLoad` 首次拉 `getCourseList`，后续 `onShow` 刷新当前类型列表
   - 切换类型：更新 `courseType=normal/ai_maic` 后重新拉列表
   - 点击卡片：携带 `id` 跳 `pages-sub/course/detail`
   - 详情页：`query.id -> getCourseDetail -> rich-text 渲染`
   - 我的页入口：点击“我的课程”直接 `switchTab('/pages/course/list')`
6. 双视角：
   - 否。课程列表/详情为公开浏览页，不区分学生/老师视角；“我的课程”入口仅是从现有学生视角我的页跳到课程 tab
7. 复用样板：
   - `pages/login/login.vue` 的 methods / 注释风格
   - `pages/my/index.vue` 的 token 变量、渐变头图与卡片布局写法

## B. 交付物清单
新增文件：
- `jst-uniapp/pages/course/list.vue`
- `jst-uniapp/pages-sub/course/detail.vue`
- `jst-uniapp/api/course.js`
- `jst-uniapp/components/jst-course-card/jst-course-card.vue`
- `jst-uniapp/components/jst-status-badge/jst-status-badge.vue`
- `jst-uniapp/static/tab/course.png`
- `jst-uniapp/static/tab/course-on.png`

修改文件：
- `jst-uniapp/pages.json`
- `jst-uniapp/pages/my/index.vue`

## C. 联调/预期结果
未在 HBuilderX / 微信开发者工具本地实跑；按任务卡豁免要求，当前给出代码路径对应的预期结果：
1. 首次进入课程 tab：默认按 `courseType=normal` 调 `/jst/wx/course/list`，后端已就绪时显示列表；未就绪时显示空状态，不弹错误 toast。
2. 点击“AI 课程”：重新按 `courseType=ai_maic` 拉列表，成功后只显示 AI 课程。
3. 列表下拉到底：若 `rows < total`，继续按下一页拉取；全部加载完后显示“没有更多课程了”。
4. 点击任一课程卡片：跳详情页并按 `id` 调 `/jst/wx/course/{id}`。
5. 详情页点击“立即购买”：仅弹 `购买功能 F9 完成后开放` toast 占位。
6. 我的页点击“我的课程”：不再弹占位 toast，改为直接切到课程 tab。

## D. 视觉对比
- 课程列表页保留了原型的蓝色顶部氛围、类型切换和大卡片信息层级。
- 课程详情页保留了顶部封面、标题+类型徽章、价格区和固定底部 CTA。
- 当前仓库并非 5 tab 完整基线，只存在 `登录 / 我的`；本次按现仓最小增量接入“课程”tab，没有额外补空白首页/赛事/公告页。

## E. 遗留 TODO
- `GET /jst/wx/course/my` 已在 `api/course.js` 预留，但本次没有新建 `pages-sub/my/course.vue`；如果后续需要“我的课程”独立页，再接该接口。
- `rich-text` 已做最小 XSS 清洗，但最终样式细节仍建议在 HBuilderX 真机/浏览器里复查一次。
- 任务卡写明“复用 `jst-status-badge`（P3 已建）”，但当前仓库缺失该组件；本次已补齐一个最小通用版本供课程页使用。

## F. 我做了任务卡之外的什么
- 由于当前仓库缺少任务卡声称已存在的 `jst-status-badge`，本次顺手补了该组件，否则课程类型徽章无法按“复用组件”的方式落地。

## G. 自检确认
- [x] 没有页面内 mock 数据
- [x] 所有课程接口都经 `api/course.js -> api/request.js`
- [x] 列表/详情接口失败时走静默降级空状态
- [x] 购买能力仍为 toast 占位，未越界实现订单能力
- [x] 富文本做了最小 XSS 清洗
- [x] 没有修改 `RuoYi-Vue`
- [x] 没有修改 `架构设计/`
- [x] 新页面样式单位全部使用 `rpx`
- [x] 页面样式颜色全部使用现有 token / 变量
- [x] 已按 27 文档契约实现，等待后端 F-COURSE 完成后联调
