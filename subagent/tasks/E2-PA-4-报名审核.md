# 任务卡 E2-PA-4 - 报名审核管理

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui**

## 背景
赛事方审核学生提交的报名记录（含动态表单数据快照 + 作品材料）。后端 F9 已完成。

## PRD 依据
- §7.1 报名审核流
- §7.6 赛事方报名管理
- 原型：`event-enroll-manage.html`

## 交付物

### 1. 报名列表 `ruoyi-ui/src/views/partner/enroll-manage.vue`

- 筛选：按赛事 / 按状态（pending/approved/rejected）/ 按时间 / 按学生姓名
- 表格：
  - 报名编号
  - 学生姓名 + 脱敏手机号
  - 赛事名称
  - 来源（个人/渠道代报名）
  - 提交时间
  - 状态
  - 操作（查看 / 审核）
- 批量操作：批量通过 / 批量驳回
- 支持 Tab：[待审核] [已通过] [已驳回] [全部]
- **Q-03 标注**：临时档案来源的报名需显示 `📎 临时档案` 标签

### 2. 报名详情抽屉 `components/EnrollDetailDrawer.vue`

点击列表项右侧抽屉弹出：
- 学生基本信息
- 赛事信息
- 动态表单数据（按 F8 表单模板渲染，只读）
- 作品材料（图片/PDF/视频预览）
- 历史审核记录（若重新提交）
- 底部操作区：通过 / 驳回（驳回需填原因）/ 关闭

### 3. API

**新建**：`ruoyi-ui/src/api/partner/enroll.js`

```js
export function listPartnerEnrolls(params) { return request({ url: '/jst/partner/enroll/list', method: 'get', params }) }
export function getPartnerEnrollDetail(id) { return request({ url: `/jst/partner/enroll/${id}`, method: 'get' }) }
export function auditPartnerEnroll(id, body) { return request({ url: `/jst/partner/enroll/${id}/audit`, method: 'put', data: body }) }
export function batchAuditEnrolls(body) { return request({ url: '/jst/partner/enroll/batch-audit', method: 'put', data: body }) }
```

若后端缺 partner 前缀路由，写字段缺口反馈文档。

### 4. 响应式
- 抽屉手机下改为全屏 drawer
- 表格手机下改为卡片列表模式（或横向滚动）

## DoD
- [ ] 列表 + 详情抽屉 + 批量操作
- [ ] 临时档案标签显示
- [ ] API 封装
- [ ] 响应式可用
- [ ] 任务报告 `E2-PA-4-回答.md`

## 不许做
- ❌ 不许改 F9 后端
- ❌ 不许做成绩/证书管理（分别是 PA-5 / PA-6）

## 依赖：E2-PA-1~3 + PA-9
## 优先级：P1

---
派发时间：2026-04-10
