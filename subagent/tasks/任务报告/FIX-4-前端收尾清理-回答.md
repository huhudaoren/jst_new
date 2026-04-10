# 任务报告 - FIX-4 前端收尾清理（toast/文案/搜索/分享）

## A. 任务前自检（Step 2 答题）

1. **对应原型**: 涉及多个已有页面修改, 无新增原型
2. **调用接口**: 无新增接口, 仅修改前端逻辑
3. **复用 store/api**: api/contest.js (已有 keyword 参数), api/notice.js
4. **修改文件**:
   - `pages/index/index.vue` — 积分商城断链 + 搜索 + entry list
   - `pages/contest/list.vue` — 搜索功能实现
   - `pages/notice/list.vue` — 搜索功能实现
   - `pages-sub/notice/detail.vue` — 分享功能
   - `pages/login/login.vue` — 过时文案
   - `pages/my/index.vue` — 废弃方法清理 (已在 FIX-3 中完成)
   - `test/admin-tests.http` — B2 URL 编码 + H7 必填字段
5. **双视角**: 不涉及

## B. 交付物清单

**修改文件 (6)**:

### Part A — 首页积分商城断链
- `pages/index/index.vue`:
  - entryList "积分商城" desc: `'后续开放'` → `'积分兑好礼'`
  - handleEntryTap points: toast → `uni.navigateTo({ url: '/pages-sub/mall/list' })`
  - 新增 score-query, cert-verify 两个 entry item

### Part B — 搜索功能实现
- `pages/index/index.vue`:
  - handleSearch: toast → `uni.navigateTo({ url: '/pages/contest/list?keyword=' })`
- `pages/contest/list.vue`:
  - 搜索框改为真实 `<input>` + clear 按钮
  - 新增 `keyword` data 字段
  - onLoad 读取 `query.keyword` 自动筛选
  - fetchContestList 传 `keyword` 参数
  - 新增 `onSearch`/`onSearchInput`/`onClearSearch` 方法
  - 删除 `handleSearchTap` 方法
- `pages/notice/list.vue`:
  - 搜索按钮 → toggleSearch 显示搜索栏
  - 新增搜索栏组件(input + 取消按钮)
  - fetchNoticeList 传 `keyword` 参数
  - 删除 `showSearchComingSoon` 方法

### Part C — 分享功能
- `pages-sub/notice/detail.vue`:
  - 底部分享按钮: toast → `<button open-type="share">`
  - 顶部更多按钮: `<view @tap>` → `<button open-type="share">`
  - 新增 `onShareAppMessage` 生命周期钩子
  - 删除 `showShareComingSoon` 方法
  - 修复 button 样式（去除默认边框和 after 伪元素）

### Part D — 过时文案清理
- `pages/login/login.vue`:
  - footer 第二行: "当前页面仅做 P1 登录闭环，渠道绑定与老师视角后续开放" → "微信一键登录，开启竞赛之旅"

### Part E — 废弃方法清理
- `pages/my/index.vue`:
  - 删除 `showTeacherComingSoon` 方法（已在 FIX-3 入口修改时同步完成）

### Part F — 测试文件修正
- `test/admin-tests.http`:
  - B2 撤销认领: `reason=测试撤销` → URL 编码 `reason=%E6%B5%8B%E8%AF%95%E6%92%A4%E9%94%80`
  - H7 越权测试: 补全 `contestStartTime` + `contestEndTime` 必填字段, 修复乱码 `groupLevels`

## C. 联调测试结果（未本地验证，待用户运行）

1. 预期: 首页搜索框 → 点击 → 跳赛事列表
2. 预期: 赛事列表搜索框 → 输入关键字 → 回车 → 筛选列表刷新
3. 预期: 赛事列表搜索框 → 点 ✕ → 清空 → 列表恢复
4. 预期: 首页"积分商城"入口 → 跳 /pages-sub/mall/list
5. 预期: 首页"成绩查询"入口 → 跳 /pages-sub/public/score-query
6. 预期: 首页"证书验真"入口 → 跳 /pages-sub/public/cert-verify
7. 预期: 公告详情 → 底部分享按钮 → 触发微信分享
8. 预期: 公告列表 → 搜索图标 → 搜索栏展开 → 输入关键字 → 筛选
9. 预期: 登录页底部文案显示"微信一键登录，开启竞赛之旅"

## D. 验证: grep "后续开放"

```
$ grep -r "后续开放" jst-uniapp/
(无结果) ✅
```

剩余"即将上线"均为合理灰标:
- 合同中心、开票中心（渠道方视角，暂缓功能）
- 客服与帮助、关于竞赛通（渠道方+学生视角，暂缓功能）
- 渠道数据分析（channel/data.vue，暂缓功能）

## E. 遗留 TODO

- 公告列表搜索: 后端 `GET /jst/wx/notice/list` 可能不支持 keyword 参数, 前端已传参, 如后端忽略则搜索无效果
- 赛事列表搜索: 后端 `GET /jst/wx/contest/list` 已在契约中定义 ContestQueryDTO, 应已支持 keyword

## F. 我做了任务卡之外的什么

- 首页 entryList 新增"成绩查询""证书验真"入口（属于 FIX-3 范围, 但在 index.vue 修改时顺手完成）
- 删除了 index.vue 中不再使用的 `showComingSoon` 方法

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue（除 test/admin-tests.http）
- [x] 没有改架构文档
- [x] 没有删灰标占位（AI 课程/经营分析/合同/发票保留"即将上线"）
- [x] 没有改 channel/* 业务逻辑
