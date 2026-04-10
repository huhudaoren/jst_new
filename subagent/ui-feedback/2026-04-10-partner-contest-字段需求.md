# 2026-04-10 赛事方赛事 CRUD 与提审字段/接口需求

## 背景

- 任务卡：[E2-PA-3-赛事CRUD与提审.md](D:/coding/jst_v1/subagent/tasks/E2-PA-3-赛事CRUD与提审.md)
- 前端页面：[contest-list.vue](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/partner/contest-list.vue)
- 前端页面：[contest-edit.vue](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/partner/contest-edit.vue)
- 前端 API：[contest.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/api/partner/contest.js)

## 检查结论

2026-04-10 在当前后端中未发现任务卡要求的赛事方包装路由 `/jst/partner/contest/*`。前端为保证页面可联调，暂桥接到现有 F7 管理侧赛事接口 `/jst/event/contest/*`，但仍建议后端补齐赛事方独立 wrapper，统一 URL、HTTP method、权限点与字段语义。

## 1. 建议补齐赛事方接口

### 1.1 列表

- 建议接口：`GET /jst/partner/contest/list`
- 当前桥接：`GET /jst/event/contest/list`
- 用途：赛事方查看自己名下赛事，支持名称、分类、审核状态、创建时间筛选。

### 1.2 详情

- 建议接口：`GET /jst/partner/contest/{contestId}`
- 当前桥接：`GET /jst/event/contest/{contestId}`
- 用途：编辑页回显 6 个配置模块。

### 1.3 新建

- 建议接口：`POST /jst/partner/contest`
- 当前桥接：`POST /jst/event/contest/add`
- 用途：赛事方保存赛事草稿。

### 1.4 编辑

- 建议接口：`PUT /jst/partner/contest/{contestId}`
- 当前桥接：`PUT /jst/event/contest/edit`
- 用途：赛事方更新草稿或驳回后重新编辑。

### 1.5 提交审核

- 建议接口：`PUT /jst/partner/contest/{contestId}/submit`
- 当前桥接：`POST /jst/event/contest/{contestId}/submit`
- 用途：赛事方将草稿/驳回赛事提交平台审核。

### 1.6 下线

- 建议接口：`PUT /jst/partner/contest/{contestId}/offline`
- 当前桥接：`POST /jst/event/contest/{contestId}/offline`
- 用途：赛事方下线已上线赛事。

### 1.7 删除/作废草稿

- 建议接口：`DELETE /jst/partner/contest/{contestId}` 或明确“不支持删除，仅支持作废/下线”
- 当前状态：未发现带赛事方隔离校验的删除接口。
- 前端处理：列表页仅在草稿态展示删除入口，但点击后提示“当前后端未提供赛事方隔离删除接口”，不调用通用删除接口，避免越权风险。

## 2. 列表字段缺口

当前 `ContestListVO` 已有 `contestId`、`contestName`、`category`、`groupLevels`、`coverImage`、`price`、`enrollStartTime`、`enrollEndTime`、`auditStatus`、`status`、`partnerId`、`partnerName`。

建议补齐：

- `eventStartTime`：赛事开始时间，列表“开始时间”列优先展示。
- `eventEndTime`：赛事结束时间，便于列表和卡片展示完整周期。
- `enrollCount`：报名人数，列表 DoD 要求展示。
- `auditRemark`：审核驳回原因，驳回后列表/编辑页提示赛事方修正。
- `updatedTime`：最后修改时间，便于赛事方识别草稿更新状态。

## 3. 编辑/详情字段缺口

当前 `ContestSaveReqDTO` 与 `ContestDetailVO` 已覆盖核心字段：名称、分类、组别、封面、详情、报名/比赛时间、价格、渠道报名、积分抵扣、预约开关、预约容量、核销配置、重复预约、成绩规则 JSON、证书规则 JSON、表单模板、售后宽限天数。

建议补齐：

- `organizerName`：主办方名称。
- `coOrganizerName`：协办方名称。
- `venueAddress`：线下地址。
- `detailImages`：详情图集。
- `scorePublishTime`：成绩发布时间。
- `aftersaleDeadline`：售后截止时间，如以后端计算为准也建议详情回显。
- `totalQuota`：总名额。
- `perUserLimit`：单用户/单家庭限购或限报次数。
- `supportCoupon`：是否支持优惠券。
- `couponRuleIds`：可用优惠券/权益规则范围。
- `appointmentSlots`：预约日期、时间段、场地、每段容量等结构化配置。
- `teamAppointmentConfig`：团队预约最小人数、最大人数、队伍成员字段、联系人字段、批量预约规则。
- `certTemplateId` / `certTemplateRules`：证书模板与发证规则结构化字段，避免仅依赖 JSON 文本。
- `scoreRuleConfig`：成绩项、奖项等级、发布方式等结构化字段，避免仅依赖 JSON 文本。

## 4. Q-07 预约退款开关缺口

前端已在报名规则标签中实现 `allowAppointmentRefund` 开关，并在保存 payload 中附带该字段。

当前后端检查结果：

- `JstContest` 领域对象中存在 `allowAppointmentRefund`。
- `ContestSaveReqDTO` 未包含 `allowAppointmentRefund`，保存接口无法稳定接收。
- `ContestDetailVO` 未包含 `allowAppointmentRefund`，详情接口无法稳定回显。
- `ContestMapperExt.xml` 的插入/更新 SQL 未映射 `allow_appointment_refund`。
- `JstContestMapper.xml` 的通用插入/更新 SQL 也未映射 `allow_appointment_refund`。

建议补齐：

- `ContestSaveReqDTO.allowAppointmentRefund`
- `ContestDetailVO.allowAppointmentRefund`
- `ContestMapperExt.xml` insert/update 字段映射
- `JstContestMapper.xml` insert/update 字段映射
- 如有导出/列表需要，也同步补充 `ContestListVO`

## 5. 联调说明

- 前端当前 API wrapper 保持任务卡要求的 6 个核心方法：列表、详情、新建、编辑、提交审核、下线。
- 待赛事方 wrapper 落地后，只需调整 `src/api/partner/contest.js` 的 URL 与 method，页面层无需大改。
- 如果后端字段命名与本反馈不同，请优先给出稳定接口契约，前端再做一次映射兼容。
