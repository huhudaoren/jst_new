# 任务报告 - F-COURSE 课程基础接口

## A. 任务前自检
1. 涉及表：`jst_course`、`jst_course_learn_record`
2. 涉及状态机：`SM-21(jst_course.audit_status)`；另实现了任务卡要求的 `jst_course.status on/off` 上下架约束
3. 涉及权限标识：`jst:event:course:list`、`jst:event:course:query`、`jst:event:course:add`、`jst:event:course:edit`、`jst:event:course:audit`、`jst:event:course:on`、`jst:event:course:off`；wx 侧为 `@Anonymous`、`@ss.hasRole('jst_student')`
4. 涉及锁名：无
5. 事务边界：`CourseServiceImpl.addCourse`、`editCourse`、`submitCourse`、`approveCourse`、`rejectCourse`、`onCourse`、`offCourse`
6. ResVO 字段：`CourseListVO`、`CourseDetailVO`、`WxCourseCardVO`、`WxCourseDetailVO`、`MyCourseVO`，均不含敏感字段，无脱敏需求
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/CourseAuditStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CourseSaveReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/CourseQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/WxCourseQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CourseListVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CourseDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxCourseCardVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxCourseDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/MyCourseVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/CourseMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/CourseMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/CourseService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/CourseServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/CourseController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxCourseController.java`

修改文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/api-tests.http`

## C. mvn compile 结果
```text
[INFO] BUILD SUCCESS
[INFO] Total time:  01:07 min
```

## D. .http 测试结果
测试块已追加，等待用户阶段汇总时统一跑。
已追加场景覆盖：`创建→提审→通过→上架`、wx 公开列表、wx 详情、wx my、`draft` 课程禁止上架。

## E. 遗留 TODO / 风险
- `BizErrorCode` 现有仓库里已存在 `JST_EVENT_PARTNER_NOT_AUTHED(20030)`，任务卡又要求 `JST_EVENT_COURSE_NOT_FOUND(20030)`；已按任务卡追加，但两个常量当前共享同一业务码 `20030`，存在码段冲突风险。
- `.http` 未本地实际执行；如果权限初始化数据里没有 `jst:event:course:list/query`，则 admin 列表/详情接口可能在联调时出现 `403`。

## F. 我做了任务卡之外的什么
- 为满足“18 模块 compile SUCCESS”，补齐了 `jst-event` 现有源码已引用但缺失的错误码：`JST_EVENT_CONTEST_NOT_ONLINE`、`JST_EVENT_CONTEST_ILLEGAL_TRANSIT`、`JST_EVENT_FORM_TEMPLATE_NOT_FOUND`、`JST_EVENT_FORM_TEMPLATE_ILLEGAL_TRANSIT`。
- 恢复了兼容常量 `JST_EVENT_FORM_VALIDATION_FAIL`，避免旧引用后续再出编译问题。

## G. 自检清单确认
- [x] 后台/登录接口已显式 `@PreAuthorize`，公开接口已显式 `@Anonymous`
- [x] ReqDTO 有 JSR-303 注解
- [x] 出参使用 VO + `AjaxResult` / `TableDataInfo`
- [x] 无敏感字段明文返回
- [x] `/my` 接口不接受前端 `userId`，直接取 token
- [x] 写操作已加 `@OperateLog`
- [x] 写操作已加 `@Transactional(rollbackFor = Exception.class)`
- [x] 本任务无高并发写场景，未新增锁
- [x] Mapper XML 未使用 `${}`
- [x] 未打印明文敏感字段
