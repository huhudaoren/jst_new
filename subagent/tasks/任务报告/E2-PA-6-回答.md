# 任务报告 - E2-PA-6 证书管理

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_cert_template`、`jst_cert_record`、`jst_score_record`、`jst_contest`、`jst_enroll_record`、`jst_participant`。
2. 涉及状态机：`SM-20` 证书发放；批量生成前同时校验 `SM-19` 中成绩必须为 `audit_status=approved` 且 `publish_status=published`。
3. 涉及权限标识：`jst:event:cert_template:list`、`jst:event:cert_template:add`、`jst:event:cert_record:list`、`jst:event:cert_record:add`、`jst:event:cert_record:edit`、`jst:event:cert_record:query`。
4. 涉及锁名：无新增 Redis 锁；证书批量生成通过事务 + 非作废证书去重 + 状态 expected update 控制。
5. 事务边界：`PartnerCertServiceImpl.saveTemplate`、`batchGrant`、`submitReview`。
6. ResVO 字段：`CertTemplateResVO` 返回模板；`PartnerCertResVO` 返回证书、赛事、报名、参赛人、奖项、脱敏手机号、状态；`CertBatchGrantResVO` 返回生成/跳过数量与证书ID；`CertPreviewResVO` 返回预览图 Data URI。
7. 复用样板：`ParticipantClaimServiceImpl` 的事务/状态校验/审计风格；PA-9 `@PartnerDataScope` 的列表隔离风格。

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerCertController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/PartnerCertService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/PartnerCertServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/PartnerCertMapper.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/PartnerCertMapper.xml`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CertTemplateReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CertQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CertBatchGrantReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CertSubmitReviewReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/CertIssueStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CertTemplateResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PartnerCertResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CertBatchGrantResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CertPreviewResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CertScoreRefVO.java`
- `RuoYi-Vue/ruoyi-ui/src/api/partner/cert.js`
- `RuoYi-Vue/ruoyi-ui/src/views/partner/cert-manage.vue`

修改文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `RuoYi-Vue/ruoyi-ui/src/router/index.js`
- `架构设计/ddl/96-grant-jst-partner-permissions.sql`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/admin-tests.http`
- `test/run_http.py`

## C. 实现说明
- 后端新增 `/jst/partner/cert/*`：`POST /template`、`GET /template/list`、`GET /list`、`POST /batch-grant`、`POST /submit-review`、`GET /{certId}/preview`。
- 模板保存绑定当前赛事方，默认 `audit_status=pending`，不直接视为平台审核通过。
- 批量生成证书只允许使用已启用且 `audit_status=approved` 的模板；成绩必须已审核通过并已发布，防止赛事方绕过平台审核链路。
- 生成的证书为 `issue_status=draft`，赛事方只能提交审核，不能直接发放；提审按 `SM-20` 执行 `draft -> pending`。
- 证书预览使用后端 `BufferedImage` 生成 PNG Data URI 占位图，满足任务卡“可占位渲染”要求。
- 前端新增证书管理页：模板新增/上传、模板列表、证书列表、批量生成、批量提审、证书预览。

## D. 验证结果
- `mvn compile -DskipTests`：`BUILD SUCCESS`，总耗时 `25.769 s`。
- `npm run build:prod`：构建成功；仅有既有 bundle/asset size warning。
- `.http`：已在 `test/admin-tests.http` 追加 `PA6-1` 到 `PA6-6`。
- 已将 `test/run_http.py` 的读取改为 `errors='replace'`，可兼容现有 `admin-tests.http` 历史混合编码。
- 执行 `python test/run_http.py test/admin-tests.http "PA5|PA6"` 时，本机 `127.0.0.1:8080` 未监听，所有 PA6 请求均连接拒绝；因此本轮未取得接口运行通过结果。

## E. 遗留 TODO
- 启动 `ruoyi-admin:8080`，并确保已导入 `96-grant-jst-partner-permissions.sql`、`99-test-fixtures.sql` 后，复跑 `PA6-1` 到 `PA6-6`。
- 后续平台端证书审核通过/作废接口不属于本卡，需由平台管理端任务补齐后形成完整发放闭环。
- 建议后续统一 `admin-tests.http` 的历史编码；当前 runner 已容错，但文件内容仍混有乱码注释。

## F. 任务卡之外的处理
- 补充 `BizErrorCode` 中证书模板/证书记录错误码。
- 补充 partner 角色对 `cert_record`、`cert_template` 权限点的授权 SQL。
- 补充 PA6 fixture 与 HTTP 用例。
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
- [x] 状态流转按 `SM-20` 校验，并校验成绩发布状态。
- [x] Mapper XML 未使用不受控 `${}`，仅消费 `PartnerDataScope` 注入的 `params.dataScope`。
