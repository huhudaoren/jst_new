# E2-PA-3 赛事 CRUD 与提审 - 交付报告

## 1. 任务结论

已完成赛事方视角的赛事 CRUD 与提审前端页面，实现范围覆盖任务卡要求的列表页、编辑页、API 封装、6 个配置 Tab、Q-07 `allow_appointment_refund` 开关、字段缺口反馈文档与构建验证。

本次未修改后端 partner wrapper，也未修改 F7 admin Controller，符合任务卡“不允许做”的约束。

## 2. 交付文件

- `RuoYi-Vue/ruoyi-ui/src/views/partner/contest-list.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/contest-edit.vue`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/contest.js`
- `RuoYi-Vue/ruoyi-ui/src/router/index.js`
- `subagent/ui-feedback/2026-04-10-partner-contest-字段需求.md`

## 3. 实现内容

### 3.1 赛事列表

- 实现赛事方赛事列表页，支持赛事名称、审核状态、分类、创建时间筛选。
- 表格字段覆盖：赛事编号、赛事名称、类别、开始时间、状态、报名数、操作。
- 操作覆盖：编辑、提交审核、下线、查看报名、删除。
- 删除当前仅展示安全提示，不调用通用删除接口，避免后端缺少赛事方隔离删除接口时产生越权风险。
- 增加移动端卡片布局，适配 RuoYi 移动设备状态。

### 3.2 赛事编辑

- 实现赛事新建/编辑页，底部支持“保存草稿”“保存并提交审核”。
- Tab A 基本信息：赛事名称、分类、组别、封面图、富文本详情。
- Tab B 时间与场次：报名开始/结束、比赛开始/结束、售后宽限天数。
- Tab C 报名规则：报名价格、渠道报名、积分抵扣、报名表模板、Q-07 预约可退开关。
- Tab D 成绩与证书：成绩规则 JSON、证书规则 JSON，并提供 JSON 格式化与合法性校验。
- Tab E 线下预约：预约开关、预约容量、重复预约、核销配置 JSON。
- Tab F 团队预约配置：保留入口并说明当前后端缺少团队预约结构化字段，后续可无破坏扩展。

### 3.3 API 封装

任务卡要求 `/jst/partner/contest/*`，当前后端仅发现 `/jst/event/contest/*` 可用接口。本次前端先桥接 F7 现有能力，封装方法保持任务卡要求的 6 个核心语义：

- `listPartnerContests`
- `getPartnerContest`
- `createPartnerContest`
- `updatePartnerContest`
- `submitPartnerContest`
- `offlinePartnerContest`

额外增加 `listPartnerFormTemplates`，用于编辑页报名表模板下拉。

### 3.4 路由接入

已在 `partnerHomeRoute` 下接入：

- `/partner/contest-list`
- `/partner/contest-edit`
- `/partner/contest-edit/:contestId`
- `/partner/enroll-manage`

这样赛事方首页快捷入口、列表新建/编辑、查看报名跳转可以闭环打开。

## 4. 后端缺口反馈

已新增反馈文档：`subagent/ui-feedback/2026-04-10-partner-contest-字段需求.md`。

重点反馈：

- 缺少赛事方独立 wrapper：`/jst/partner/contest/list`、详情、新建、编辑、提交审核、下线、删除/作废草稿。
- 列表缺少 `eventStartTime`、`eventEndTime`、`enrollCount`、`auditRemark`、`updatedTime` 等字段。
- 编辑/详情缺少主办方、协办方、举办地点、详情图集、成绩发布时间、售后截止时间、总名额、单人限制、优惠券、预约时段、团队预约等结构化字段。
- Q-07 `allowAppointmentRefund`：领域对象已有字段，但 `ContestSaveReqDTO`、`ContestDetailVO`、`ContestMapperExt.xml`、`JstContestMapper.xml` 尚未完整映射，当前前端已发送该字段，但后端可能暂不持久化/回显。

## 5. 验证记录

### 5.1 SFC 语法校验

命令：使用 `vue-template-compiler` + `@babel/parser` 校验 `contest-list.vue`、`contest-edit.vue`。

结果：通过。

### 5.2 前端生产构建

命令：`npm run build:prod`

结果：通过。

说明：首次构建在清理旧 `dist/favicon.ico` 时出现 `EPERM: operation not permitted, unlink`，使用提升权限重跑后构建成功。最终仅保留既有 asset size / entrypoint size warning，不影响本次页面编译交付。

## 6. DoD 对照

- [x] 列表 + 编辑页完整
- [x] 6 个 Tab 全部覆盖
- [x] `allow_appointment_refund` 开关实现（Q-07）
- [x] API 封装 6 方法
- [x] 字段缺口反馈文档
- [x] 任务报告 `E2-PA-3-回答.md`
