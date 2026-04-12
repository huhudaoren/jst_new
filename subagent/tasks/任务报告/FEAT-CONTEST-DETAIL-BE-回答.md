# 任务报告 - FEAT-CONTEST-DETAIL-BE 赛事详情后端补全

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_contest`（主表，新增详情字段），`jst_course`（推荐课程来源），`jst_course_learn_record`（课程学习人数统计），`jst_event_partner`（赛事方名称关联）。
2. 涉及状态机：`SM-5`（赛事审核/上架态，推荐接口仅允许 online），`SM-21`（课程审核/上下架态，推荐课程仅取 approved+on）。
3. 涉及权限标识：本任务未新增 `jst:*` 管理端权限点；小程序推荐接口沿用公开访问（`@Anonymous`）。
4. 涉及锁名：无（本任务为读取接口与字段补齐，无并发写入场景）。
5. 事务边界：写方法仍在 `ContestServiceImpl.addContest/editContest` 且保留 `@Transactional(rollbackFor = Exception.class)`；新接口 `getWxRecommend` 为纯查询，无事务。
6. ResVO 字段：
   - `relatedContests`: 复用 `ContestListVO`（含 `recommendTags`）
   - `relatedCourses`: `courseId/courseName/coverImage/price/learnerCount`
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`。

## B. 交付物清单
新增文件：
- `架构设计/ddl/99-migration-contest-detail-fields.sql`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestRecommendVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestRecommendCourseVO.java`
- `subagent/tasks/任务报告/FEAT-CONTEST-DETAIL-BE-回答.md`

修改文件：
- `架构设计/ddl/99-test-fixtures.sql`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ContestSaveReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestListVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxContestDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/domain/JstContest.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/ContestMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/ContestMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/ContestService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/ContestServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxContestController.java`
- `test/wx-tests.http`

## C. mvn compile 结果
执行命令：`mvn compile -DskipTests`（`RuoYi-Vue` 根目录）
- Reactor 18/18 模块全部 SUCCESS
- 最终结果：`BUILD SUCCESS`
- Total time：`38.489 s`

补充验证：按系统提示完成过一次 `mvn clean install -DskipTests`，结果同样 18/18 SUCCESS（`02:35 min`），确保本地依赖 jar 已刷新。

## D. .http / 接口联调结果
联调前置：
- 已执行 `99-migration-contest-detail-fields.sql`（4 条 DDL 全成功）
- 已启动 `ruoyi-admin`（8080）

1. `GET /jst/wx/contest/8809/recommend`
- HTTP 状态：`200`
- 响应：`{"code":200,"data":{"relatedContests":[],"relatedCourses":[...<=4]}}`
- 结果：通过（返回结构符合 `relatedContests + relatedCourses` 约定）

2. `GET /jst/wx/contest/8808/recommend`
- HTTP 状态：`200`
- 响应：`{"code":20003,"msg":"赛事未上架"}`
- 结果：通过（非 online 赛事按预期失败）

另外已在 `test/wx-tests.http` 追加两条用例：
- `CONTEST-DETAIL-BE-1` happy path
- `CONTEST-DETAIL-BE-2` non-happy path

## E. 遗留 TODO
- 当前测试库中 `jst_contest` 可用 fixture 与任务卡示例 ID（8201/8202）不一致，联调使用了现有 online 赛事 `8809`。后续如需完全按任务卡 ID 复现，需补齐/回灌对应 fixture 赛事数据。

## F. 我做了任务卡之外的什么？
- 额外补了 `WxContestDetailVO` 的 4 个详情字段透传（`scheduleJson/awardsJson/faqJson/recommendTags`），保证详情接口和保存链路一致。
- 额外补了 `test/wx-tests.http` 的接口回归用例（任务卡仅强制编译，但系统提示要求补接口测试块）。

## G. 自检清单确认（16-安全文档 §8）
- [x] 未新增越权权限点；公开接口使用 `@Anonymous`
- [x] `ReqDTO` 新字段保持 JSR-303 约束（`recommendTags` 长度限制）
- [x] 出参使用 VO，未直接返回 Entity
- [x] 本任务新增字段不涉及敏感字段明文泄露
- [x] 无 `${}` 拼接 SQL
- [x] 写接口事务边界保持不变（`@Transactional`）
- [x] 无新增高并发写场景，无需新增分布式锁
