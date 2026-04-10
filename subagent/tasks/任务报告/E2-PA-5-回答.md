# 任务报告 - E2-PA-5 成绩导入与发布

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_score_record`、`jst_enroll_record`、`jst_contest`、`jst_participant`；配套权限/fixture 涉及 `sys_menu`、`sys_role_menu` 与 `99-test-fixtures.sql`。
2. 涉及状态机：`SM-19` 成绩审核/发布；任务卡补充了赛事方侧草稿态，因此本实现使用 `audit_status=draft/pending/approved/rejected` 与 `publish_status=unpublished/published/withdrawn`。
3. 涉及权限标识：`jst:event:score_record:list`、`jst:event:score_record:add`、`jst:event:score_record:edit`。
4. 涉及锁名：无新增 Redis 锁；`15-Redis-Key与锁规约.md` 未定义成绩导入/提审锁，本实现使用事务 + 期望状态更新做并发保护。
5. 事务边界：`PartnerScoreServiceImpl.importScores`、`saveScore`、`submitReview`、`applyCorrection`。
6. ResVO 字段：`PartnerScoreResVO` 返回成绩、赛事、报名、参赛人、脱敏手机号、审核/发布/展示状态；`ScoreStatsResVO` 返回报名数、已录入数、审核中数、已发布数；`ScoreImportResVO` 返回导入批次、成功/失败数量与错误行。
7. 复用样板：`ParticipantClaimServiceImpl` 的事务、状态校验、`@OperateLog`、脱敏输出风格；列表数据隔离使用 PA-9 的 `@PartnerDataScope`。

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerScoreController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/PartnerScoreService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/PartnerScoreServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/PartnerScoreMapper.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/PartnerScoreMapper.xml`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ScoreQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ScoreSaveReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ScoreSubmitReviewReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ScoreCorrectionApplyReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ScoreImportRowDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/ScoreAuditStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/ScorePublishStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PartnerScoreResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ScoreEnrollRefVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ScoreImportResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ScoreStatsResVO.java`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/score.js`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/score-manage.vue`

修改文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `RuoYi-Vue/ruoyi-ui/src/router/index.js`
- `架构设计/ddl/96-grant-jst-partner-permissions.sql`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/admin-tests.http`
- `test/run_http.py`

## C. 实现说明
- 后端新增 `/jst/partner/score/*`：`POST /import`、`GET /list`、`GET /stats`、`PUT /{id}`、`PUT /submit-review`、`GET /correction/apply`、`POST /correction/apply`。
- 导入 Excel 使用 `ExcelUtil<ScoreImportRowDTO>`，逐行校验报名是否属于赛事、报名是否已审核通过、报名是否已有成绩；成功行写入 `jst_score_record` 草稿。
- 赛事方只能编辑 `draft/rejected + unpublished` 成绩；不能直接发布，不能编辑 `published` 成绩。
- 提审按 `SM-19` 执行 `draft/rejected -> pending`，并通过 Mapper 的 expected status 条件做乐观并发保护。
- 已发布成绩更正只写入 `[CORRECTION_REQUESTED]` 备注，不修改已发布成绩字段，符合 Q-12。
- 前端新增赛事方成绩管理页：赛事筛选、统计卡、导入模板、Excel 导入、单条新增/编辑、批量提审、已发布成绩更正申请。

## D. 验证结果
- `mvn compile -DskipTests`：`BUILD SUCCESS`，总耗时 `25.769 s`。
- `npm run build:prod`：构建成功；仅有既有 bundle/asset size warning。
- `.http`：已在 `test/admin-tests.http` 追加 `PA5-0` 到 `PA5-5`。
- 已将 `test/run_http.py` 的读取改为 `errors='replace'`，可兼容现有 `admin-tests.http` 历史混合编码。
- 执行 `python test/run_http.py test/admin-tests.http "PA5|PA6"` 时，本机 `127.0.0.1:8080` 未监听，所有 PA5 请求均连接拒绝；因此本轮未取得接口运行通过结果。

## E. 遗留 TODO
- 启动 `ruoyi-admin:8080`，并确保已导入 `96-grant-jst-partner-permissions.sql`、`99-test-fixtures.sql` 后，复跑 `PA5-0` 到 `PA5-5`。
- 建议后续统一 `admin-tests.http` 的历史编码；当前 runner 已容错，但文件内容仍混有乱码注释。

## F. 任务卡之外的处理
- 补充 `BizErrorCode` 中成绩相关错误码。
- 补充 partner 角色对 `score_record` 权限点的授权 SQL。
- 补充 PA5 fixture 与 HTTP 用例。
- 让 `test/run_http.py` 对历史混合编码 `.http` 文件容错读取。
- 未执行 commit：当前工作区存在大量并行任务未提交/未跟踪文件，避免误纳入非本任务改动。

## G. 自检清单
- [x] 新增后端方法均有 `@PreAuthorize`。
- [x] ReqDTO 使用 JSR-303。
- [x] 出参使用 ResVO，不直接返回 Entity。
- [x] 手机号字段已脱敏。
- [x] 列表使用 `@PartnerDataScope`，详情/写操作在 Service 校验赛事方归属。
- [x] 写操作有 `@OperateLog`。
- [x] 写操作有 `@Transactional`。
- [x] 状态流转按 `SM-19` 校验。
- [x] Mapper XML 未使用不受控 `${}`，仅消费 `PartnerDataScope` 注入的 `params.dataScope`。
