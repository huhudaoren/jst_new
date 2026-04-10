# 任务卡 E2-PA-2 - 赛事方工作台首页

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui**

## 背景
赛事方登录后的默认落地页，4 格 KPI + 菜单 + 待办 + 最近通知。

## PRD 依据
- §7.6 赛事方工作台
- 原型：`event-home.html`

## 交付物

### 1. 新建 `ruoyi-ui/src/views/partner/home.vue`

**布局**（响应式栅格）：

#### A. Hero 4 格 KPI
- 待审报名数
- 本月报名总数
- 待发成绩数
- 待领证书数
- 数据来源：`GET /jst/partner/dashboard/summary`

#### B. 快捷菜单宫格（9 格）
- 赛事管理 → `/partner/contest-list`
- 报名审核 → `/partner/enroll-manage`
- 成绩管理 → `/partner/score-manage`
- 证书管理 → `/partner/cert-manage`
- 现场核销 → `/partner/writeoff`
- 结算中心 → `/partner/settlement`
- 合同发票 → `/partner/contract-invoice`（灰标 F-2）
- 数据统计 → `/partner/stats`
- 账号设置 → `/partner/settings`

#### C. 待办清单
- 待审报名 Top 5
- 待发成绩 Top 5
- 每条点击跳对应页面

#### D. 平台通知
- 最近 3 条平台公告
- 数据来源：`GET /jst/partner/notice/recent`

### 2. 路由与菜单配置

**修改**：`ruoyi-ui/src/router/index.js`

```js
{
  path: '/partner',
  component: Layout,
  meta: { roles: ['jst_partner'] },
  children: [
    { path: 'home', component: () => import('@/views/partner/home.vue'), meta: { title: '工作台' } }
  ]
}
```

### 3. 赛事方登录后默认跳转

**修改**：`ruoyi-ui/src/permission.js`

登录后根据角色判断：若 `roles` 包含 `jst_partner` → 默认跳 `/partner/home`，否则走原逻辑。

### 4. API

**新建**：`ruoyi-ui/src/api/partner/dashboard.js`

```js
export function getPartnerSummary() { return request({ url: '/jst/partner/dashboard/summary', method: 'get' }) }
export function getPartnerTodo() { return request({ url: '/jst/partner/dashboard/todo', method: 'get' }) }
export function getPartnerRecentNotice() { return request({ url: '/jst/partner/notice/recent', method: 'get' }) }
```

**⚠️ 后端接口缺失**：E0-1 未覆盖 `jst/partner/dashboard/*`。若后端没有，写字段缺口反馈文档 `subagent/ui-feedback/2026-04-10-partner-dashboard-字段需求.md`，列出需补 3 个接口。

### 5. 响应式
- `<el-row :gutter="20">` + `<el-col :xs="24" :sm="12" :md="6">`
- 宫格手机下变 2 列
- KPI 手机下变 2 列

## DoD
- [ ] 工作台 5 区块完整
- [ ] 路由 + 菜单配置
- [ ] 登录后默认跳转逻辑
- [ ] 响应式手机下可用
- [ ] 字段缺口反馈（如有）
- [ ] 任务报告 `E2-PA-2-回答.md`

## 不许做
- ❌ 不许自己补后端接口
- ❌ 不许动其他角色的菜单配置

## 依赖：E2-PA-1 + E0-1 + PA-9（PartnerScope 切面）
## 优先级：P1

---
派发时间：2026-04-10
