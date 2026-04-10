# 任务卡 FIX-4 - 前端收尾清理（toast/文案/搜索/分享）

## 阶段
阶段 E 收尾 / **jst-uniapp**

## 背景
Test Agent 发现 14 处 toast 占位，其中 7 处指向已存在的页面或可实现的功能。本卡做最后一轮清理。

## 交付物

### Part A — 首页积分商城断链（2 处）
**文件**：`pages/index/index.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~171 | 积分商城 desc `后续开放` | `积分兑好礼` |
| ~321 | toast `积分商城后续开放` | `uni.navigateTo({ url: '/pages-sub/mall/list' })` |

### Part B — 搜索功能实现（3 处）
**文件**：`pages/index/index.vue` + `pages/contest/list.vue` + `pages/notice/list.vue`

**首页搜索**（index.vue:~281）：
- 搜索框 `@confirm` → `uni.navigateTo({ url: '/pages/contest/list?keyword=' + value })`
- 跳到赛事列表并带 keyword 参数
- 赛事列表 onLoad 读 query.keyword 自动筛选

**赛事列表搜索**（contest/list.vue:~191）：
- 搜索框 `@confirm="onSearch"` + `@clear="onClearSearch"`
- `onSearch(e)` → `this.keyword = e.detail.value; this.page = 1; this.loadList()`
- `loadList` 传 `{ keyword: this.keyword, pageNum, pageSize }`
- 后端 `GET /jst/wx/contest/list` 已支持 keyword 参数

**公告列表搜索**（notice/list.vue:~115）：
- 同上模式。若后端不支持 keyword，做前端本地 filter 兜底

### Part C — 分享功能
**文件**：`pages-sub/notice/detail.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~154 | toast `分享功能后续开放` | 接入 `onShareAppMessage` |

```js
onShareAppMessage() {
  return {
    title: this.detail.title || '竞赛通公告',
    path: `/pages-sub/notice/detail?id=${this.noticeId}`
  }
}
```
分享按钮改为 `<button open-type="share" class="...">分享</button>`

### Part D — 过时文案清理
**文件**：`pages/login/login.vue`

| 行号 | 当前 | 改为 |
|---|---|---|
| ~41 | "当前页面仅做P1登录闭环，渠道绑定与老师视角后续开放" | 删除此行，或改为"微信一键登录，开启竞赛之旅" |

### Part E — 废弃方法清理
**文件**：`pages/my/index.vue`

- 删除 `showTeacherComingSoon` 方法定义（line ~226）— 无调用方

### Part F — 测试文件修正（P3 小修）
**文件**：`test/admin-tests.http`

- B2 撤销认领：URL 中文参数 `reason=测试撤销` → 改为 URL 编码或 JSON body
- H7 越权测试：补全请求体必填字段，预期 403 而非 500

## DoD
- [ ] 积分商城 2 处 toast → 真实跳转
- [ ] 3 处搜索 toast → 真实搜索功能
- [ ] 分享 toast → onShareAppMessage
- [ ] 登录页过时文案删除
- [ ] 废弃方法清理
- [ ] 测试文件 2 处小修
- [ ] grep "后续开放" 确认仅剩合理灰标（AI/分析/合同/发票/播放器）
- [ ] 任务报告 `FIX-4-回答.md`

## 不许做
- ❌ 不许改后端
- ❌ 不许删灰标占位（AI 课程/经营分析/合同/发票保留"即将上线"）
- ❌ 不许改 channel/* 业务逻辑

## 依赖：无（与 FIX-1/2/3 并行）
## 优先级：P3

---
派发时间：2026-04-10
