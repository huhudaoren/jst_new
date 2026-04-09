# 任务报告 - P2 首页与公告 tab

## A. 任务前自检（Step 2 答题）
1. 对应原型：
   `小程序原型图/index.html`
   `小程序原型图/notice.html`
   `小程序原型图/notice-detail.html`

2. 调用接口：
   `GET /jst/wx/index/banner`，文档 `架构设计/27-用户端API契约.md` §3.2
   `GET /jst/wx/notice/list`，文档 `架构设计/27-用户端API契约.md` §3.9
   `GET /jst/wx/notice/{id}`，文档 `架构设计/27-用户端API契约.md` §3.9
   `GET /jst/wx/dict/jst_notice_category`，任务卡 `subagent/tasks/F-NOTICE-公告与首页接口.md` 接口契约 9
   `GET /jst/wx/contest/hot`，文档 `架构设计/27-用户端API契约.md` §3.2，首页热门赛事区可选增强

3. 复用 store/api：
   本任务页面不直接依赖 store
   复用 `jst-uniapp/api/request.js` 的统一请求、401 跳登录、静默失败能力
   复用现有 `jst-uniapp/pages/login/login.vue` / `jst-uniapp/pages/my/index.vue` 的页面写法与当前工程路由约定

4. 新建文件：
   `jst-uniapp/api/notice.js`
   `jst-uniapp/api/contest.js`
   `jst-uniapp/components/jst-banner-swiper/jst-banner-swiper.vue`
   `jst-uniapp/components/jst-notice-card/jst-notice-card.vue`
   `jst-uniapp/pages/notice/list.vue`
   `jst-uniapp/pages-sub/notice/detail.vue`
   `jst-uniapp/static/tab/index.png`
   `jst-uniapp/static/tab/index-on.png`
   `jst-uniapp/static/tab/notice.png`
   `jst-uniapp/static/tab/notice-on.png`

5. 数据流：
   首页 `onShow` 并行拉 banner、公告前 3 条、热门赛事，分别渲染，单区块失败不阻塞其他区块
   公告页 `onLoad/onShow` 拉分类字典和分页列表，顶部置顶公告单独展示，分类切换后重拉列表
   详情页 `onLoad(id)` 拉详情，富文本经简单清洗后交给 `rich-text`
   用户点击 banner/公告卡片/快讯条后跳转详情；点击分类 tab 后带 category 重查；点击首页入口卡片按当前阶段跳 tab 或 toast 占位

6. 双视角：
   本批页面是公开内容页，不区分学生/老师视角；老师/学生双视角逻辑仍留在“我的”页体系

7. 复用样板：
   交互和 methods 写法主要复用 `jst-uniapp/pages/login/login.vue`
   卡片、渐变、分组区块风格主要沿用 `jst-uniapp/pages/my/index.vue`
   兼容了当前工程已存在的 `jst-uniapp/pages/course/list.vue` tab 结构

## B. 交付物清单
新增文件：
- `jst-uniapp/api/notice.js`
- `jst-uniapp/api/contest.js`
- `jst-uniapp/components/jst-banner-swiper/jst-banner-swiper.vue`
- `jst-uniapp/components/jst-notice-card/jst-notice-card.vue`
- `jst-uniapp/pages/notice/list.vue`
- `jst-uniapp/pages-sub/notice/detail.vue`
- `jst-uniapp/static/tab/index.png`
- `jst-uniapp/static/tab/index-on.png`
- `jst-uniapp/static/tab/notice.png`
- `jst-uniapp/static/tab/notice-on.png`

修改文件：
- `jst-uniapp/api/request.js`
- `jst-uniapp/pages/index/index.vue`
- `jst-uniapp/pages.json`

## C. 联调测试结果
未在 HBuilderX / 微信开发者工具中实际运行，以下是按当前代码路径的预期结果：

1. 启动进入首页；banner 接口成功则显示轮播，失败则整块隐藏，不弹错误 toast。
2. 首页公告快讯条取公告列表前 3 条；点击后跳 `pages-sub/notice/detail.vue`。
3. 公告 tab 可切分类；字典接口失败时回退到文档里的 5 个固定分类值。
4. 公告详情页用 `rich-text` 渲染正文；`script/style/on*` 已做基础清洗。
5. 热门赛事区会真实请求 `/jst/wx/contest/hot`；F7 未完成时显示空状态，不造假数据。
6. `request.js` 已兼容分页接口 `rows/total`，并支持 `silent` 时连网络异常也不弹 toast。

## D. 视觉对比
整体对齐了原型的几个核心块：首页蓝色品牌头图、banner 卡片、公告快讯条、公告页分类胶囊、置顶公告、详情页文章卡片。

存在的偏差：
- 首页没有把原型里的课程推荐、平台介绍、客服悬浮一起带进来，只做了任务卡要求范围
- 公告详情保留了分享占位，但未做原型里的相关公告推荐和分享 Sheet
- 首页快捷入口结合当前工程现状做了调整：课程可直达已存在 tab，赛事仍为占位 toast

## E. 遗留 TODO
- 赛事 tab 目前工程里还不存在，所以首页“赛事入口”和“查看更多热门赛事”仍是占位 toast，待 P3 接入
- 未做真机/HBuilderX 可视验证，需用户本地跑一轮首页/公告/详情交互
- 如果后端 `notice/list` 最终返回结构和 27/F-NOTICE 有偏差，还需要按真实返回字段再微调一次映射

## F. 任务卡外补充
我额外补了 `jst-uniapp/api/contest.js`，只为让首页热门赛事区在 F7 完成后自动有数据，不需要再返工首页。

另外顺手把 `jst-uniapp/api/request.js` 做了分页返回兼容和静默失败兼容，这对公告页、课程页这类并行联调页面都更稳。

## G. 自检确认
- [x] 没有页面内 mock 数据
- [x] 新增接口都经过 `api/*` 封装
- [x] 公告/首页失败时走空态或隐藏，不主动弹业务错误 toast
- [x] 401 仍由统一请求层处理
- [x] 未修改 `RuoYi-Vue/`、`架构设计/`、需求文档
- [x] 未引入新依赖
- [x] 新页面样式单位均为 `rpx`
- [x] 新增 tab 图标已落地为本地 PNG
