# 任务卡 E1-CH-7 - 批量报名 + 临时参赛档案 + 团队预约前端

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
PRD §7.2.1 要求渠道支持批量报名（手动/Excel 导入/附件批量上传），对未注册参赛人支持**临时档案**，后续学生注册后**自动/人工合并认领**。PRD §7.7 要求 `channel-appointment.html` 团队预约页（复用 C6 后端）。本卡前端一次落地。

## CCB 决策
- **Q-03**：临时档案合并后**完全合并**，订单卡顶部标注 `📎 此订单原以"XXX（临时档案）"名义报名`

## PRD 依据
- §7.2.1 批量报名 + 临时参赛档案
- §7.7 团队预约 + §8.12 team_appointment 数据模型
- 原型：`channel-batch-enroll.html` + `channel-appointment.html`

## 交付物

### Part A — 批量报名页 `pages-sub/channel/batch-enroll.vue`

#### 顶部赛事选择
- 下拉或弹窗选择赛事
- 显示赛事基本信息 + 报名费单价
- 若从学生管理单学生跳入（`?singleStudentId=xxx`），预填该学生为唯一参赛人

#### 参赛人添加方式 3 种 Tab
**Tab A - 从已绑定学生添加**：
- 勾选学生列表（多选）
- 显示已选 N 人

**Tab B - 手动新增临时参赛档案**：
- 逐条新增表单：
  - 姓名 `@NotBlank`
  - 性别
  - 出生日期
  - 监护人姓名
  - 监护人手机号 `@NotBlank`
  - 学校/机构/班级
  - 证件类型 + 证件号
- 支持一次录入多条（+ 按钮新增一行）

**Tab C - Excel 模板导入**：
- 下载模板按钮 → 提供 `.xlsx` 模板下载（模板放 `static/templates/batch-enroll-template.xlsx`，本卡暂可用占位）
- 上传文件 → 调 `POST /channel/participant/batch-create`（Excel 解析由后端完成，前端只传 file）
- 回显解析结果（N 条成功 / M 条失败）

#### 底部
- 已选参赛人列表（可编辑/移除）
- 总金额
- 提交按钮 → `POST /channel/participant/batch-enroll`
- 成功后跳到"批量报名记录"子页或回工作台

### Part B — 临时档案管理子页 `pages-sub/channel/participants.vue`

- 列表：显示所有该渠道创建的临时档案
- 状态标签：
  - 🟡 `unclaimed` 未认领
  - 🟢 `claimed` 已认领（显示关联的正式用户）
  - 🔴 `conflict` 存在多候选待人工处理
- 操作：
  - 编辑（仅 unclaimed）
  - 删除（仅 unclaimed）
  - 查看认领详情（claimed）

### Part C — 团队预约 `pages-sub/channel/appointment.vue`

#### 顶部
- 选择活动（筛选支持团队预约的赛事场次）
- 选择日期与场次（复用 C6 现有 appointment slot 接口）

#### 成员名单
- 从已绑定学生选择（多选）
- 额外添加临时参赛人数（数字输入 + 备注名单 textarea）
- 显示总人数

#### 生成团队预约
- 确认 → `POST /wx/event/team-appointment/create`
- 成功后跳"团队预约详情"页展示：
  - 团队名称
  - 预约总人数
  - 成员人数 / 额外人数
  - 预约日期
  - 已核销人数 / 剩余可核销人数
  - 每位成员的 qrCode（复用 utils/qrcode-wrapper.js 懒渲染）

### Part D — 订单详情临时档案标注（Q-03）

**修改**：`pages-sub/channel/order-detail.vue`（CH-4 已新建）和 `pages-sub/my/order-detail.vue`（学生视角）

- 若订单的 `participantSnapshot` 有 `originalParticipantName` 字段（后端合并时回写）
- 在顶部显示标签：`📎 此订单原以"张小明（临时档案）"名义报名`

若后端 VO 无该字段，写反馈文档 `subagent/ui-feedback/2026-04-10-participant-merge-字段需求.md`。

### Part E — API 封装
**修改**：`api/channel.js` + `api/participant.js`

```js
// channel.js
export function batchCreateParticipants(body) { return request({ url: '/jst/wx/channel/participant/batch-create', method: 'POST', data: body }) }
export function batchEnroll(body) { return request({ url: '/jst/wx/channel/participant/batch-enroll', method: 'POST', data: body }) }
export function getMyParticipants(params) { return request({ url: '/jst/wx/channel/participant/my', method: 'GET', params }) }
export function deleteParticipant(id) { return request({ url: `/jst/wx/channel/participant/${id}`, method: 'DELETE' }) }

// appointment.js (可能需要新建或扩展)
export function createTeamAppointment(body) { return request({ url: '/jst/wx/event/team-appointment/create', method: 'POST', data: body }) }
export function getTeamAppointmentDetail(id) { return request({ url: `/jst/wx/event/team-appointment/${id}`, method: 'GET' }) }
```

## DoD
- [ ] batch-enroll.vue 3 Tab + 底部提交
- [ ] participants.vue 临时档案列表 + 3 状态
- [ ] appointment.vue 团队预约创建 + 详情页
- [ ] order-detail 临时档案标注（Q-03）
- [ ] 字段缺口反馈文档（如有）
- [ ] 任务报告 `E1-CH-7-回答.md`

## 不许做
- ❌ 不许自己做 Excel 解析（后端 E0-1 已处理）
- ❌ 不许改 C6 团队预约后端
- ❌ 不许动临时档案的 `participant_user_map` 合并逻辑（纯展示）

## 依赖：E0-1 + E1-CH-1 + E1-CH-3 + E1-CH-4
## 优先级：P2（可与 CH-5/CH-6 并行）

---
派发时间：2026-04-10
