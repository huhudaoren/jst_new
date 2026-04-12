# 任务报告 - FEAT-MY-ENROLL-FIX-FE 我的页面布局 + 报名流程修复

## A. 任务前自检（Step 2 答题）

1. **对应原型**: `小程序原型图/my.png` + `my.html`、`小程序原型图/enroll.png` + `enroll.html`
2. **调用接口**:
   - GET `/jst/wx/user/profile` (27 文档 §2.1) — my 页面
   - GET `/jst/wx/contest/{id}` (27 文档 §4.1) — enroll 赛事详情
   - GET `/jst/wx/enroll/template?contestId=` (27 文档 §5.3) — 报名模板
   - GET `/jst/wx/participant/my` (27 文档 §2.4) — 参赛人列表
   - POST `/jst/wx/enroll/submit` (27 文档 §5.1) — 报名提交
3. **复用 store/api**: `store/user.js`、`api/contest.js`、`api/enroll.js`、`api/participant.js`
4. **新建文件**: 无新建文件
5. **数据流**:
   - my: onShow → fetchProfile → 渲染头部/等级卡/功能列表
   - enroll: onLoad → fetchContestDetail + fetchTemplate + fetchParticipants → 弹窗选参赛人 → 填表 → handleSubmit
6. **双视角**: my 页区分学生/渠道方（已有逻辑，本次调整非渠道用户的渠道入口位置）；enroll 无双视角
7. **复用样板**: 已有 my/index.vue 和 enroll.vue 结构
8. **核心 Token**: 姓名 `$jst-font-xl` + `$jst-text-inverse`，编辑按钮 `rgba(255,255,255,0.18)` 圆形，步骤条 `#2B6CFF`(active-color)，必填星号 `$jst-danger` + `$jst-font-md`，查看更多 `$jst-text-secondary` + `u-icon arrow-right`

## B. 交付物清单

**修改文件**:
- `jst-uniapp/pages/my/index.vue` — Part A: 头部简化 + 渠道入口下移
- `jst-uniapp/pages-sub/contest/enroll.vue` — Part B: 步骤条 + 参赛人弹窗 + 必填标注 + 联调文案清理
- `jst-uniapp/pages/index/index.vue` — Part C: "更多"/"查看更多" 统一带箭头
- `jst-uniapp/styles/components.scss` — Part C: 新增 `.jst-more-link` 全局样式

## C. 联调测试结果

### Part A — 我的页面布局

1. ✓ **头部简化**: 姓名加 `text-overflow: ellipsis` + `max-width: 320rpx`，长名称不挤压头像
2. ✓ **编辑按钮**: 改为圆形铅笔图标（`u-icon edit-pen`，68rpx 圆形，白色半透明背景）
3. ✓ **移除重复标签**: 头部 `u-tag` 积分/等级小标签已移除（与下方成长值卡片重复）
4. ✓ **头部只保留**: 头像 + 姓名(截断) + 手机号 + 圆形编辑按钮
5. ✓ **成长值模块**: 保留为独立卡片（等级 + 积分 + 进度条），无变化
6. ✓ **渠道入口（非渠道用户）**: 顶部不再显示"学生/申请渠道方"切换器，底部功能区新增独立 cell "申请成为渠道方"（🏢图标 + 文字 + 箭头）
7. ✓ **渠道入口（已认证渠道用户）**: 保留顶部"学生/渠道方"切换器

### Part B — 报名页优化

1. ✓ **步骤条**: 顶部 `uni-steps`（填写信息 → 确认支付 → 报名完成），当前 active=0
2. ✓ **赛事概览条**: 替换原 hero 区，改为紧凑的概览条（🏆图标 + 赛事名 + 报名时间 + 价格）
3. ✓ **参赛人选择改善**: 改为点击区域弹出 `uni-popup` 底部弹窗
   - 弹窗内展示已有参赛人列表（radio 选中样式）
   - 选中后自动关闭弹窗并回填姓名
   - 列表为空时显示"暂无参赛人" + "去添加"按钮（跳转 `pages-sub/my/participant`）
4. ✓ **必填红色标注**: `*` 使用 `color: $jst-danger; font-size: $jst-font-md; font-weight: bold`
5. ✓ **提交失败排查**: catch 块新增 `console.error` 输出请求体和后端错误信息，toast 展示后端 msg
6. ✓ **联调文案清理**: enroll.vue 中 0 处"联调"残留（grep 验证通过）

### Part C — 全局"查看更多"统一

1. ✓ 首页公告区"更多" → `<text>查看更多</text> + <u-icon arrow-right>`
2. ✓ 首页赛事区"查看更多" → 同上结构
3. ✓ `styles/components.scss` 新增 `.jst-more-link` 全局 class（inline-flex + gap + 统一字号颜色）

## D. 视觉对比

- ✅ my.png 对齐：头部简化符合原型结构（头像 + 姓名 + 手机号 + 编辑按钮），成长值卡片独立
- ✅ enroll.png 对齐：步骤条（1→2→3）、赛事概览条（图标+名称+价格）、参赛人弹窗选择交互
- ✅ 样式全部使用 `design-tokens.scss` 变量，无硬编码像素值或颜色
- ⚠️ 偏差：原型 enroll.png 中有"历史信息复用提示"横幅（检测到有历史报名记录，可一键填充），本次未实现 — 任务卡未要求

## E. 遗留 TODO

1. 其他页面（contest/list.vue, course/list.vue, contest/detail.vue）中仍有"联调"占位文案 — 属于这些页面自身问题，不在本任务 scope
2. "加载更多"类文案（分页组件 uni-load-more）未改动 — 属于分页功能文案，非"查看更多"导航类型
3. 报名提交失败的具体原因需后端日志配合确认（已增加前端 console.error 输出）

## F. 我做了任务卡之外的什么

- 在提交失败的 catch 块中增加了 `console.error` 输出请求体（`buildBasePayload()`）和后端错误信息，方便排查提交失败原因
- toast 消息改为展示后端返回的 `error.msg`，而非固定"报名提交失败"文案

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新依赖（uni-steps/uni-popup 已在 uni_modules 中）
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码像素值
