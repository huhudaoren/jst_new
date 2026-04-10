# 任务报告 - E1-CH-7 批量报名 + 临时参赛档案 + 团队预约前端

## A. 任务前自检（Step 2 答题）

1. **对应原型 PNG 和 HTML**:
   - `channel-batch-enroll.png` / `channel-batch-enroll.html` → **不存在**，无独立原型
   - `channel-appointment.png` / `channel-appointment.html` → **不存在**，无独立原型
   - 替代参考：`channel-students.png`（学生管理视觉风格）、`channel-home.png`（渠道工作台整体风格）、`channel-orders.png`（订单卡片风格）
   - 已读取上述 3 张 PNG 获取视觉参数

2. **调用接口**:
   - `GET /jst/wx/channel/students` (27 §3.10) — 获取已绑定学生列表
   - `POST /jst/wx/channel/participant/batch-create` — 批量创建临时档案
   - `POST /jst/wx/channel/participant/batch-enroll` — 批量报名
   - `GET /jst/wx/channel/participant/my` — 渠道临时档案列表
   - `PUT /jst/wx/channel/participant/{id}` — 更新临时档案
   - `DELETE /jst/wx/channel/participant/{id}` — 删除临时档案
   - `POST /jst/wx/appointment/team/create` (27 §3.5) — 创建团队预约
   - `GET /jst/wx/appointment/team/{id}` (27 §3.5) — 团队预约详情
   - `GET /jst/wx/appointment/contest/{id}/remaining` (27 §3.5) — 查剩余名额
   - `GET /jst/wx/contest/list` (27 §3.2) — 赛事列表
   - `GET /jst/wx/channel/orders/{id}` — 渠道订单详情

3. **复用 store/api**: `api/channel.js`, `api/appointment.js`, `api/contest.js`, `api/request.js`, `store/user.js`, `utils/qrcode-wrapper.js`

4. **新建文件**:
   - `jst-uniapp/pages-sub/channel/batch-enroll.vue` — 批量报名页
   - `jst-uniapp/pages-sub/channel/participants.vue` — 临时档案管理页
   - `jst-uniapp/pages-sub/channel/appointment.vue` — 团队预约（创建+详情双模式）
   - `subagent/ui-feedback/2026-04-10-participant-merge-字段需求.md` — Q-03 字段反馈

5. **数据流**:
   - 批量报名: onLoad → loadContests + loadBoundStudents → 用户选赛事/勾选学生/手动填写/Excel导入 → onSubmit → batchCreateParticipants (手动) → batchEnroll → navigateBack
   - 临时档案: onShow → getMyParticipants → 渲染3状态列表 → 编辑(updateParticipant) / 删除(deleteParticipant)
   - 团队预约: onLoad → 判断 teamId → 创建模式(选赛事+日期+学生) 或 详情模式(loadDetail + makeQr)
   - 订单标注: loadDetail → 检查 participantSnapshot.originalParticipantName → 条件渲染标签

6. **双视角**: 本批所有页面仅渠道方视角（`jst_channel` 角色），订单详情标注两端都有（渠道+学生）

7. **复用样板**: `pages-sub/channel/students.vue` (列表/Tab/卡片)、`pages-sub/channel/orders.vue` (导航/样式)、`pages-sub/appointment/apply.vue` (预约表单)

8. **PNG 视觉参数**:
   - 主色: #3F51B5 (渠道系统统一的 Indigo)
   - 卡片圆角: `var(--jst-radius-lg)` = 32rpx
   - 关键间距: 页面边距 32rpx, 卡片内边距 28rpx, 列表项间距 20rpx
   - 头像: 80-88rpx 圆形 + 渐变背景
   - Tab: 圆角胶囊 `var(--jst-radius-full)`, 选中蓝边框+蓝背景
   - 操作按钮: 底部固定栏 + 白色高模糊背景 + 顶部阴影

## B. 交付物清单

**新增文件**:
- `jst-uniapp/pages-sub/channel/batch-enroll.vue` — 批量报名页 (3 Tab + 赛事选择 + 底部提交)
- `jst-uniapp/pages-sub/channel/participants.vue` — 临时档案管理 (列表 + 3 状态 + 编辑/删除)
- `jst-uniapp/pages-sub/channel/appointment.vue` — 团队预约 (创建 + 详情 + 成员QR)
- `subagent/ui-feedback/2026-04-10-participant-merge-字段需求.md` — Q-03 后端字段需求反馈

**修改文件**:
- `jst-uniapp/api/channel.js` — 新增 6 个 API 函数 (batchCreateParticipants, batchEnroll, batchImportParticipants, getMyParticipants, updateParticipant, deleteParticipant)
- `jst-uniapp/api/appointment.js` — 新增 2 个 API 函数 (createTeamAppointment, getTeamAppointmentDetail)
- `jst-uniapp/pages.json` — 新增 3 条路由 (batch-enroll, participants, appointment)
- `jst-uniapp/pages-sub/channel/order-detail.vue` — Q-03 临时档案合并标注
- `jst-uniapp/pages-sub/my/order-detail.vue` — Q-03 临时档案合并标注
- `jst-uniapp/pages-sub/channel/students.vue` — goEnroll 跳转到 batch-enroll 页

## C. 联调测试结果 (未本地验证，待用户运行)

预期交互:
1. 渠道工作台 → 点「批量报名」→ 进入 batch-enroll 页
2. 选择赛事 → 弹窗选择 → 显示赛事名+单价
3. Tab A: 勾选已绑定学生 → 底部显示已选人数+金额
4. Tab B: 点「+新增一行」→ 填写表单 → 底部显示
5. Tab C: 上传 Excel → 后端解析 → 显示导入结果
6. 点「提交报名」→ 先创建临时档案 → 再批量报名 → 成功 toast → 返回
7. 学生管理页 → 点「代报名」→ 跳到 batch-enroll 并预选该学生
8. 临时档案页 → 列表 + Tab 筛选 → unclaimed 可编辑/删除
9. 团队预约 → 选赛事+日期+场次+学生 → 创建 → 跳详情页 → 渲染成员 QR
10. 订单详情 → 若有 originalParticipantName → 顶部显示合并标注

## D. 视觉对比

- 参考 `channel-students.png` / `channel-home.png` / `channel-orders.png` 的视觉风格
- 主色 #3F51B5 (Indigo)、卡片圆角 32rpx、列表项间距 20rpx、操作区底部固定
- 无独立原型 PNG，已根据现有渠道页面风格统一设计

## E. 遗留 TODO

1. **Excel 模板下载**: 任务卡提到 `static/templates/batch-enroll-template.xlsx` 暂用占位，待 OSS 配置后接入
2. **临时档案详情页**: participants.vue 的「查看详情」和「查看冲突」暂用 toast 占位
3. **后端 VO 字段**: Q-03 `participantSnapshot.originalParticipantName` 字段需后端合并逻辑中回写 (已写反馈文档)
4. **批量报名后端接口**: `batch-create` 和 `batch-enroll` 两个接口需确认后端已实现
5. **团队预约后端**: `POST /jst/wx/appointment/team/create` 已在 C6 实现，但 VO 结构需确认（members 数组 + qrCode 字段）

## F. 我做了任务卡之外的什么

- 修改 `students.vue` 的 `goEnroll` 方法，从 toast 占位改为跳转到 `batch-enroll` 页并传 `singleStudentId`

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] 单位全部 rpx
- [x] 颜色用 design-system token (CSS 变量)
- [x] pages.json 已添加 3 条新路由
- [x] Q-03 字段需求反馈文档已写
