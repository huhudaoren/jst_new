# 任务卡 FIX-LINK-FE - 前端断链修复

## 阶段
阶段 E 收尾 / **jst-uniapp**

## 背景
小程序端 61 页结构完整（零孤儿零空壳），但存在 7 处 toast 占位断链 + 1 处过时文案 + 1 处废弃方法 + 2 处 API URL 路径不一致。用户实际使用时遇到"点了没反应"或"提示后续开放"但功能页面已存在。

## 交付物

### Part A — 首页 3 处断链修复
**文件**：`jst-uniapp/pages/index/index.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~171 | 积分商城 desc `后续开放` | 改文案为 `积分兑好礼` |
| ~281 | `handleSearch` toast "搜索功能后续开放" | 实现简单搜索：跳到 `/pages/contest/list?keyword=xxx`（赛事搜索），或弹出搜索面板，按关键词跳对应 Tab 页 |
| ~321 | `showComingSoon('积分商城后续开放')` | 改为 `uni.navigateTo({ url: '/pages-sub/mall/list' })` |

### Part B — 赛事列表搜索
**文件**：`jst-uniapp/pages/contest/list.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~191 | toast "搜索功能后续开放" | 实现真实搜索：搜索框输入 → 设 `keyword` 变量 → 调 `getContestList({ keyword, page: 1 })` → 刷新列表。后端 `GET /jst/wx/contest/list` 已支持 `keyword` query 参数 |

搜索交互：
- 搜索框 `@confirm="onSearch"` + `@clear="onClearSearch"`
- `onSearch(e)` → `this.keyword = e.detail.value; this.page = 1; this.loadList()`
- `onClearSearch` → `this.keyword = ''; this.page = 1; this.loadList()`
- `loadList` 里传 `{ keyword: this.keyword, pageNum: this.page, pageSize: 20 }`

### Part C — 公告列表搜索
**文件**：`jst-uniapp/pages/notice/list.vue`

同 Part B，改为真实搜索。后端 `GET /jst/wx/notice/list` 应支持 `keyword` 或 `title` 参数。若后端不支持，**写字段缺口反馈文档**，前端先做本地过滤（filter loaded list）兜底。

### Part D — 分享功能
**文件**：`jst-uniapp/pages-sub/notice/detail.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~154 | toast "分享功能后续开放" | 接入 `onShareAppMessage` 微信分享 |

实现：
```js
onShareAppMessage() {
  return {
    title: this.detail.title || '竞赛通公告',
    path: `/pages-sub/notice/detail?id=${this.noticeId}`
  }
}
```
同时把分享按钮改为 `<button open-type="share">` 或直接触发系统分享。

### Part E — 登录页过时文案
**文件**：`jst-uniapp/pages/login/login.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~41 | "当前页面仅做P1登录闭环，渠道绑定与老师视角后续开放" | 删除此行或改为"微信一键登录，开启竞赛之旅" |

### Part F — 废弃方法清理
**文件**：`jst-uniapp/pages/my/index.vue`

- 删除 `showTeacherComingSoon` 方法（line ~226）— 该方法已无调用方（"老师" tab 已改为 `navigateChannelApply`）

### Part G — 团队预约 API URL 修正
**文件**：`jst-uniapp/api/appointment.js`

| 方法 | 当前 URL | 后端真实 URL | 改为 |
|---|---|---|---|
| `createTeamAppointment` | `/jst/wx/appointment/team/create` | `/jst/wx/team-appointment/apply` | `/jst/wx/team-appointment/apply` |
| `getTeamAppointmentDetail` | `/jst/wx/appointment/team/{id}` | `/jst/wx/team-appointment/{id}` | `/jst/wx/team-appointment/${id}` |

## DoD
- [ ] 7 处 toast → 真实跳转/搜索/分享
- [ ] 1 处过时文案删除
- [ ] 1 处废弃方法清理
- [ ] 2 处 API URL 修正
- [ ] grep "后续开放" 确认仅剩 LOW 灰标（AI 课程/分析/合同/发票）
- [ ] 任务报告 `FIX-LINK-FE-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许删灰标占位（AI 课程/经营分析/合同/发票保留"即将上线"）
- ❌ 不许改 channel/* 页面的业务逻辑
- ❌ 不许引入 npm 依赖

## 依赖：无
## 优先级：P0（链路闭环阻塞）

---
派发时间：2026-04-10
