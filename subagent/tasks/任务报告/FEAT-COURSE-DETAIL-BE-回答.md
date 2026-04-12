# 任务报告 - FEAT-COURSE-DETAIL-BE 课程详情后端补全

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_course`（主），`jst_course_learn_record`（fixture 既有学习记录联动场景）
2. 涉及状态机：`SM-21`（课程审核状态机，字段补全不改变跃迁规则）
3. 涉及权限标识：`jst:event:course:list`、`jst:event:course:query`、`jst:event:course:add`、`jst:event:course:edit`（以及小程序详情接口 `@Anonymous`）
4. 涉及锁名：无（本任务不新增并发写冲突场景）
5. 事务边界：`CourseServiceImpl.addCourse`、`CourseServiceImpl.editCourse`（均 `@Transactional(rollbackFor = Exception.class)`，本次仅扩展字段透传）
6. ResVO 字段：
   - 管理端 `CourseDetailVO` 新增：`lessonCount`、`learnerCount`、`totalDuration`、`chaptersJson`、`teacherName`、`teacherAvatar`、`teacherDesc`
   - 管理端 `CourseListVO` 新增：`lessonCount`、`learnerCount`
   - 小程序 `WxCourseDetailVO` 新增同课程详情 7 字段（用于详情页统计/讲师/目录）
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `架构设计/ddl/99-migration-course-detail-fields.sql`
- `subagent/tasks/任务报告/FEAT-COURSE-DETAIL-BE-回答.md`

修改文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/domain/JstCourse.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CourseSaveReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CourseDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CourseListVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxCourseDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/CourseServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/CourseMapperExt.xml`
- `架构设计/ddl/99-test-fixtures.sql`

## C. mvn compile 结果
`mvn compile -DskipTests`：
- Reactor 18/18 模块全部 SUCCESS
- 最终结果：`BUILD SUCCESS`
- Total time: `02:26 min`

## D. 接口联调结果
本任务卡 DoD 未要求新增 `.http` 用例；本次按任务卡要求完成 DDL/VO/DTO/Mapper/fixture 与编译校验。

## E. 遗留 TODO
- 无

## F. 我做了任务卡之外的什么
- 额外补充了 `JstCourse`、`CourseServiceImpl.buildCourse`、`WxCourseDetailVO` 三处字段链路。
- 原因：仅改 `CourseSaveReqDTO/CourseDetailVO/CourseMapperExt.xml` 无法保证新增字段可被保存并在小程序课程详情接口返回，需补齐实体与服务透传链路，避免“字段已加但前端仍拿不到”。

## G. 自检清单确认（16-安全文档 §8）
- [x] Controller 权限维持原有约束（管理端 `@PreAuthorize`、小程序公开 `@Anonymous`）
- [x] `ReqDTO` 字段补充了 JSR-303（数值下限与字符串长度）
- [x] 出参使用 `VO`，未直返 `Entity`
- [x] 本任务新增字段不含 mobile/idCard/bank 等敏感字段
- [x] 未引入 `${}` 拼 SQL
- [x] 写方法事务边界保持 `@Transactional(rollbackFor = Exception.class)`
- [x] 无新增高并发写场景，无需新增分布式锁
