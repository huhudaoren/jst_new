# 任务报告 - PARTNER-WRAPPER 赛事方接口包装层

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_contest`、`jst_enroll_record`、`jst_enroll_form_template`、`jst_event_partner`（列表/详情联表），以及报名详情关联读取 `jst_participant`。
2. 涉及状态机：`SM-5a/SM-5b`（赛事审核/业务状态）、`SM-6`（报名审核状态）、`SM-25`（表单模板可用性筛选语义）。
3. 涉及权限标识：本卡对外接口统一使用 `@PreAuthorize("@ss.hasRole('jst_partner')")`；未新增 `jst:xxx:xxx` 权限点。
4. 涉及锁名：`lock:contest:audit:{contestId}`、`lock:enroll:audit:{enrollId}`（批量审核逐条复用单条审核锁）。
5. 事务边界：`ContestServiceImpl.addContest/editContest/submitContest/offlineContest/deleteContest`，`EnrollRecordServiceImpl.audit/batchAudit`（均 `@Transactional(rollbackFor = Exception.class)`）。
6. ResVO 字段（脱敏后）：
   - 赛事列表 `ContestListVO`：`contestId/contestName/category/groupLevels/coverImage/price/enrollStartTime/enrollEndTime/eventStartTime/eventEndTime/enrollCount/auditStatus/auditRemark/status/partnerId/partnerName`
   - 赛事详情 `ContestDetailVO`：含 `allowAppointmentRefund/auditRemark` 等完整详情字段
   - 报名列表/详情：沿用 `EnrollListVO/EnrollDetailVO`，手机号字段为 `guardianMobileMasked`
   - 表单模板列表：`FormTemplateListVO`（公共模板 + 当前赛事方模板）
7. 复用样板：`D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`。

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerContestController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerEnrollController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerFormTemplateController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/EnrollBatchAuditReqDTO.java`

修改文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ContestQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ContestSaveReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/EnrollQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/ContestAuditStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/ContestMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/FormTemplateMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/ContestService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/EnrollRecordService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/FormTemplateService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/ContestServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/EnrollRecordServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/FormTemplateServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestListVO.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/ContestMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/EnrollRecordMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/FormTemplateMapperExt.xml`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/contest.js`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/enroll.js`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/contest-list.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/contest-edit.vue`
- `test/admin-tests.http`（追加 `PW-1~PW-7`，并将 `PW-4` 的 `writeoffConfig` 从空串改为 `'{}'` 以满足 JSON 列约束）

## C. 编译/构建结果
1. `mvn compile -DskipTests`（`D:/coding/jst_v1/RuoYi-Vue`）
- 18 模块全部 SUCCESS
- `[INFO] BUILD SUCCESS`
- Total time: `02:18`

2. `mvn clean install -DskipTests`（按接口测试规约补跑）
- 18 模块全部 SUCCESS
- `[INFO] BUILD SUCCESS`
- Total time: `01:45`

3. 前端构建
- `npm run build`：项目无该 script（`package.json` 未定义）
- 按项目实际脚本执行 `npm run build:prod`：成功（有体积告警，无构建失败）

## D. 接口测试结果（PW-1~PW-7）
说明：在 `8080` 启动最新 `ruoyi-admin.jar` 后，按 `test/admin-tests.http` 的 PW 场景等价执行。

- PW-1 partner A 登录：HTTP 200，`code=200`
- PW-1B partner B 登录：HTTP 200，`code=200`
- PW-2 `GET /jst/partner/contest/list`：HTTP 200，`code=200`，返回行 `partnerId` 全为 `8101`
- PW-3 `GET /jst/partner/contest/8201`：HTTP 200，`code=200`，`contestId=8201, partnerId=8101`
- PW-4A `POST /jst/partner/contest`：HTTP 200，`code=200`，成功创建 `contestId=8807`
- PW-4B `PUT /jst/partner/contest/8807/submit`：HTTP 200，`code=200`
- PW-5 `GET /jst/partner/enroll/list?contestId=8201`：HTTP 200，`code=200`，结果均归属目标赛事
- PW-6 `PUT /jst/partner/enroll/batch-audit`：HTTP 200，一次实测成功 `code=200` 且 `successCount=1`（重复执行会受测试数据状态影响）
- PW-7 partner B 访问 partner A 赛事详情：HTTP 200，业务拒绝 `code=99902`（无权限）

## E. 遗留 TODO
- `PW-6` 为状态机写操作，重复执行会消耗/改变 `pending` 数据；回归时建议先重置对应 fixture 再跑。

## F. 我做了任务卡之外的什么
- 仅做了测试稳定性修正：`test/admin-tests.http` 中 `PW-4` 的 `writeoffConfig` 空串会触发 MySQL JSON 列报错（`Invalid JSON text: The document is empty`），已改为 `'{}'`。

## G. 自检清单确认（16-安全文档 §8）
- [x] wrapper 接口均加 `@PreAuthorize("@ss.hasRole('jst_partner')")`
- [x] 新增 ReqDTO 使用 JSR-303 约束
- [x] Controller 出参统一 `AjaxResult`/分页 VO
- [x] 详情与写操作通过 Service 侧归属校验防越权
- [x] 写操作保持 `@Transactional`
- [x] 并发状态写通过既有锁模板与状态机断言
- [x] Mapper 无新增不受控 `${}`（仅复用 dataScope 预留位）
- [x] 无明文敏感日志输出
