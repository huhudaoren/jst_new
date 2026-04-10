# 任务卡 E2-PA-3 - 赛事 CRUD 与提审（赛事方视角）

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui**

## 背景
赛事方可自主创建赛事、配置报名规则、提交平台审核。后端 F7 赛事 CRUD 已完成，本卡做赛事方视角前端页面。

## PRD 依据
- §7.1 赛事模型
- §7.6 赛事方创建赛事
- 原型：`event-contest-list.html` + `event-contest-edit.html`

## 交付物

### 1. 赛事列表 `ruoyi-ui/src/views/partner/contest-list.vue`

- 表格：赛事编号 / 赛事名称 / 类别 / 开始时间 / 状态（草稿/审核中/已上线/已下线）/ 报名数 / 操作
- 筛选：按状态 / 按时间 / 按类别
- 操作：编辑 / 提交审核 / 下线 / 查看报名 / 删除（仅草稿）
- 顶部"新建赛事"按钮

### 2. 赛事编辑页 `ruoyi-ui/src/views/partner/contest-edit.vue`

**tab 分区**：

#### Tab A - 基本信息
- 赛事名称
- 赛事分类（级联选择）
- 赛事简介（富文本）
- 封面图 + 详情图（OSS 上传）
- 主办单位
- 协办单位
- 举办地点

#### Tab B - 时间与场次
- 报名开始/结束时间
- 赛事开始/结束时间
- 成绩发布时间
- 售后截止时间

#### Tab C - 报名规则
- 报名费（标价）
- 限额（总名额 / 每人最多报名次数）
- 是否支持渠道代报名 ✅/❌
- 是否支持积分抵扣 ✅/❌
- 是否支持优惠券
- 关联报名表单模板（下拉选 F8 模板）
- **Q-07 配置**：`allow_appointment_refund` 开关（是否允许预约退款）

#### Tab D - 成绩与证书
- 成绩规则（百分制/等级制/自定义）
- 证书规则（是否发证 / 证书模板 / 编号规则）

#### Tab E - 线下预约
- 是否需要预约 ✅/❌
- 预约日期配置（日期范围 + 每日场次）
- 核销子项配置（含 qrCode 生成规则）

#### Tab F - 团队预约配置
- 是否允许团队预约 ✅/❌
- 最小/最大人数
- 团购折扣规则（若有）

底部：
- 保存草稿 → `PUT /partner/contest/{id}?status=draft`
- 提交审核 → `PUT /partner/contest/{id}/submit`

### 3. API

**新建**：`ruoyi-ui/src/api/partner/contest.js`

```js
export function listPartnerContests(params) { return request({ url: '/jst/partner/contest/list', method: 'get', params }) }
export function getPartnerContest(id) { return request({ url: `/jst/partner/contest/${id}`, method: 'get' }) }
export function createPartnerContest(body) { return request({ url: '/jst/partner/contest', method: 'post', data: body }) }
export function updatePartnerContest(id, body) { return request({ url: `/jst/partner/contest/${id}`, method: 'put', data: body }) }
export function submitPartnerContest(id) { return request({ url: `/jst/partner/contest/${id}/submit`, method: 'put' }) }
export function offlinePartnerContest(id) { return request({ url: `/jst/partner/contest/${id}/offline`, method: 'put' }) }
```

**⚠️ 后端可能未提供 partner 前缀路由**（F7 是 admin 前缀）。写字段缺口反馈 `subagent/ui-feedback/2026-04-10-partner-contest-字段需求.md`，建议后端加一层 partner wrapper controller（走 PartnerScope 切面，PA-9 会实现）。

### 4. 富文本编辑器
- 复用 ruoyi-ui 自带的 `vue-quill-editor`（若已集成），否则用 `<el-input type="textarea">` 占位并标注"富文本 F-1 升级"

## DoD
- [ ] 列表 + 编辑页完整
- [ ] 6 个 Tab 全部覆盖
- [ ] allow_appointment_refund 开关（Q-07）
- [ ] API 封装 6 方法
- [ ] 字段缺口反馈文档（如有）
- [ ] 任务报告 `E2-PA-3-回答.md`

## 不许做
- ❌ 不许自己补后端 partner 前缀 Controller
- ❌ 不许改 F7 的 admin Controller
- ❌ 不许编造假字段

## 依赖：E2-PA-1 + E2-PA-2 + E0-1 + PA-9
## 优先级：P1

---
派发时间：2026-04-10
