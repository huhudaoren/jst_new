# 任务报告 - REFACTOR-TODO-CERT-RENDER（Backend）

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_cert_record`、`jst_enroll_record`、`jst_contest`、`jst_score_record`、`jst_cert_template`（详情接口主链路），以及已有能力中的 `jst_participant`（原列表/验真逻辑）。
2. 涉及状态机：`SM-20`（`jst_cert_record.issue_status`，本次为只读查询，不做状态跃迁）。
3. 涉及权限标识：小程序端按角色控制，本接口使用 `@PreAuthorize("@ss.hasRole('jst_student')")`。
4. 涉及锁名：无（只读查询）。
5. 事务边界：无新增事务（只读接口，不加 `@Transactional`）。
6. ResVO 字段：新增详情返回 `templateName/layoutJson/bgImage/variables`，并保留详情展示字段 `certNo/contestName/awardLevel/groupLevel/issueStatus/createTime/issueTime/issueDate/certImageUrl...`。
7. 复用样板：`jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java` 的 Service 分层与安全校验风格；并对齐现有 `WxScore/WxCert` 的 Controller-Service-Mapper 结构。

## B. 交付物清单
新增文件：
1. `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxCertDetailVO.java`

修改文件：
1. `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxCertController.java`
2. `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/WxCertService.java`
3. `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/WxCertMapper.java`
4. `RuoYi-Vue/jst-event/src/main/resources/mapper/event/WxCertMapper.xml`
5. `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/WxCertServiceImpl.java`
6. `test/wx-tests.http`

## C. 关键实现说明（仅 Backend §三）
1. 新增 `GET /jst/wx/cert/{certId}`，返回 `layoutJson + bgImage + variables`。
2. 后端不做证书图片渲染，仅返回渲染所需数据。
3. `variables` 组装链路按任务要求：`cert_record -> enroll_record(form_snapshot_json) -> contest -> score_record`。
4. 增加本人归属校验（SQL 条件 `cr.user_id = #{userId}`），非本人返回业务错误 `20061`。

## D. 编译与接口测试结果
1. `mvn compile -DskipTests`（`D:\coding\jst_v1\RuoYi-Vue`）通过，18 模块 `BUILD SUCCESS`。
2. `wx-tests.http` 新增并执行：
- `FIX5-3a`：`GET /jst/wx/cert/97081`（user 1001）HTTP `200`，业务 `code=200`，返回含 `layoutJson/bgImage/variables`。
- `FIX5-3b`：`MOCK_1002` 登录 HTTP `200`，业务 `code=200`。
- `FIX5-3c`：`GET /jst/wx/cert/97081`（user 1002）HTTP `200`，业务 `code=20061`，`msg=证书记录不存在`。

补充响应摘要（实测）：
- 成功详情：`data.variables` 含 `name/contestName/awardName/score/certNo/date/school/groupLevel`。
- 非本人详情：`{"code":20061,"msg":"证书记录不存在"}`。

## E. 遗留 TODO
1. 无（Backend §三范围内已完成）。

## F. 任务外改动说明
1. 仅补充了对应回归用例块到 `wx-tests.http`（`FIX5-3a/3b/3c`），未扩展业务实现范围。