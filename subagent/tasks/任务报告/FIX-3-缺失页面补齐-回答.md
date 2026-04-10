# 任务报告 - FIX-3 缺失页面补齐（8 页）

## A. 任务前自检（Step 2 答题）

1. **对应原型 PNG/HTML**:
   - my-score.png / my-score.html
   - my-cert.png / my-cert.html
   - my-message.png / my-message.html
   - notice-msg.png / notice-msg.html
   - settings.png / settings.html
   - privacy.png / privacy.html
   - score-query.png / score-query.html
   - cert-verify.png / cert-verify.html

2. **调用接口**:
   - `GET /jst/wx/score/my` (27 文档 §3.8) — 我的成绩列表
   - `GET /jst/wx/score/{id}` (27 文档 §3.8) — 成绩详情
   - `GET /jst/wx/cert/my` (27 文档 §3.8) — 我的证书列表
   - `GET /jst/wx/cert/{id}` (27 文档 §3.8) — 证书详情
   - `POST /jst/wx/cert/{id}/share` (27 文档 §3.8) — 证书分享
   - `GET /jst/wx/message/my` (27 文档 §3.9) — 我的消息
   - `POST /jst/wx/message/{id}/read` (27 文档 §3.9) — 标记已读
   - `POST /jst/wx/message/read-all` (27 文档 §3.9) — 全部已读
   - `GET /jst/wx/notice/list` (27 文档 §3.9) — 公告列表（复用）
   - `GET /jst/public/score/query` (27 文档 §3.11) — 公开成绩查询
   - `GET /jst/public/cert/verify` (27 文档 §3.11) — 证书验真

3. **复用 store/api**: api/notice.js（复用）, api/request.js, store/user.js

4. **新建文件**:
   - `jst-uniapp/api/score.js`
   - `jst-uniapp/api/cert.js`
   - `jst-uniapp/api/message.js`
   - `jst-uniapp/pages-sub/my/score.vue`
   - `jst-uniapp/pages-sub/my/cert.vue`
   - `jst-uniapp/pages-sub/my/message.vue`
   - `jst-uniapp/pages-sub/my/settings.vue`
   - `jst-uniapp/pages-sub/my/privacy.vue`
   - `jst-uniapp/pages-sub/notice/message.vue`
   - `jst-uniapp/pages-sub/public/score-query.vue`
   - `jst-uniapp/pages-sub/public/cert-verify.vue`

5. **数据流**:
   - score: onLoad → api.getMyScoreList → 渲染列表 + 统计汇总
   - cert: onLoad → api.getMyCertList → 渲染证书卡片
   - message: onLoad → api.getMyMessageList → Tab 筛选 → 按日期分组渲染
   - notice/message: onLoad → api.getNoticeList → Tab 筛选 → 分页加载
   - settings: 纯展示 + 缓存清理 + 退出登录
   - privacy: 纯展示（占位文本）
   - score-query: 输入查询条件 → api.queryPublicScore → 渲染结果
   - cert-verify: 输入证书编号 → api.verifyPublicCert → 渲染结果

6. **双视角**: 所有 8 个页面均不区分学生/老师视角

7. **复用样板**: pages/notice/list.vue 的分页写法 + pages/my/index.vue 的导航模式

8. **PNG 视觉参数**:
   - 主色: #1058A0 (brand blue), #FF6B35 (accent orange)
   - 卡片圆角: 24rpx ~ 32rpx (var(--jst-radius-lg))
   - 间距: 24rpx 页面边距, 16rpx 卡片间距
   - 证书预览区: 金色渐变 #7C4F00→#F5C842, 银色渐变 #3A3A3A→#B0B0B0

## B. 交付物清单

**新增文件 (11)**:
- `jst-uniapp/api/score.js` — 成绩 API 封装 (3 接口)
- `jst-uniapp/api/cert.js` — 证书 API 封装 (4 接口)
- `jst-uniapp/api/message.js` — 消息 API 封装 (3 接口)
- `jst-uniapp/pages-sub/my/score.vue` — 我的成绩 + 公开查询入口
- `jst-uniapp/pages-sub/my/cert.vue` — 我的证书 + 统计 + 验真入口
- `jst-uniapp/pages-sub/my/message.vue` — 个人消息 (Tab 筛选 + 按日期分组)
- `jst-uniapp/pages-sub/my/settings.vue` — 设置页 (隐私/账号/通用/版本/退出)
- `jst-uniapp/pages-sub/my/privacy.vue` — 隐私政策 + 用户协议 (占位文本)
- `jst-uniapp/pages-sub/notice/message.vue` — 公告消息中心 (Tab + 分页)
- `jst-uniapp/pages-sub/public/score-query.vue` — 成绩公开查询 (三种查询方式)
- `jst-uniapp/pages-sub/public/cert-verify.vue` — 证书验真 (输入编号查验)

**修改文件 (2)**:
- `jst-uniapp/pages.json` — 注册 8 条新路由 (5 个 pages-sub/my, 1 个 pages-sub/notice, 2 个 pages-sub/public)
- `jst-uniapp/pages/my/index.vue` — 学生视角新增 4 个入口 (我的成绩/我的证书/我的消息/设置), summary 4 格添加点击, 删除 showTeacherComingSoon 废弃方法

## C. 联调测试结果（未本地验证，待用户运行）

1. 预期: 点"我的"→ "我的成绩" → 跳 score.vue → 调 API → 空状态
2. 预期: 点"我的证书" → 跳 cert.vue → 调 API → 空状态
3. 预期: 点"我的消息" → 跳 message.vue → Tab 切换 → 空状态
4. 预期: 点"设置" → 跳 settings.vue → 隐私/清缓存/退出
5. 预期: settings → 隐私保护政策 → privacy.vue 渲染占位文本
6. 预期: score.vue 公开查询区 → 输入手机号 → 跳 score-query.vue
7. 预期: cert.vue 公开验真区 → 输入编号 → 跳 cert-verify.vue
8. 预期: 首页 entry grid 新增"成绩查询""证书验真"入口

## D. 视觉对比

- ✅ score.vue: 对照 my-score.png 实现查询卡 + 成绩卡片 + 奖项徽章 + 统计汇总
- ✅ cert.vue: 对照 my-cert.png 实现证书预览区(金/银/蓝渐变) + 操作按钮
- ✅ message.vue: 对照 my-message.png 实现 Tab + 日期分组 + 消息卡片
- ✅ settings.vue: 对照 settings.png 实现分组列表 + Toggle 样式
- ✅ privacy.vue: 对照 privacy.png 实现分组内容展示
- ✅ score-query.vue: 对照 score-query.png 实现三种查询方式 Tab + 表单
- ✅ cert-verify.vue: 对照 cert-verify.png 实现验证结果卡 + 手动查询区
- ⚠️ settings.vue: PNG 中有消息提醒 Toggle 组件，本期简化为纯导航列表（无本地 Toggle 开关，因后端无偏好设置接口）
- ⚠️ notice/message.vue: PNG 为 my-message 风格，但 notice-msg.png 是公告 Tab 风格，已按 notice-msg.png 实现

## E. 遗留 TODO

1. **后端缺口**: 以下接口可能未实现，需 Backend Agent 补齐:
   - `GET /jst/wx/score/my` — 成绩列表
   - `GET /jst/wx/cert/my` — 证书列表
   - `GET /jst/wx/message/my` — 消息列表
   - `POST /jst/wx/message/{id}/read` — 标记已读
   - `POST /jst/wx/message/read-all` — 全部已读
   - `GET /jst/public/score/query` — 公开成绩查询
   - `GET /jst/public/cert/verify` — 证书验真
2. settings.vue 的 Toggle 开关（消息提醒偏好）需后端提供 preference 接口
3. cert.vue 的证书查看大图和下载功能需后端提供证书图片 URL

## F. 我做了任务卡之外的什么

- 首页 entry grid 追加了"成绩查询""证书验真"2 个入口（任务卡要求"首页或帮助页追加"）
- my/index.vue summary 4 格（我的成绩/我的证书）添加了 @tap 跳转
- 删除了 `showTeacherComingSoon` 废弃方法（FIX-4 Part E 范围，顺手完成）

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] 单位全部 rpx
- [x] 颜色用 design-system token / variables.scss
