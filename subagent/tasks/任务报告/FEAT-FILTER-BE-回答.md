# 任务报告 - FEAT-FILTER-BE 多级筛选后端

## A. 任务前自检（Step 2 答题）
1. 涉及表：`sys_dict_type`、`sys_dict_data`、`jst_contest`、`jst_enroll_record`、`jst_course`
2. 涉及状态机：`SM-5`（赛事审核状态，列表仅查询 `audit_status='online'`）、`SM-21`（课程审核状态，列表仅查询 `audit_status='approved'` 且 `status='on'`）
3. 涉及权限标识：无（本任务新增/改造接口均为公开接口，`@Anonymous`）
4. 涉及锁名：无（纯查询改造，无并发写）
5. 事务边界：无新增写方法，无新增 `@Transactional` 边界
6. ResVO 字段：
   - 分类接口：`label` / `value`
   - 赛事列表：`contestId/contestName/coverImage/category/price/enrollStartTime/enrollEndTime/status/enrollOpen/partnerName`（`TableDataInfo.rows`）
   - 课程列表：`courseId/courseName/courseType/coverImage/description/price/pointsPrice`（`TableDataInfo.rows`）
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `架构设计/ddl/99-migration-category-dict.sql`

修改文件：
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/wx/WxDictController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/WxContestQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/WxCourseQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/ContestMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/CourseMapperExt.xml`
- `test/wx-tests.http`

实现要点：
- 新增分类字典迁移：`jst_contest_category`、`jst_course_category`（含 15+ 赛事分类与课程扩展分类，幂等插入）
- 新增公开分类接口：
  - `GET /jst/wx/dict/contest-categories`
  - `GET /jst/wx/dict/course-categories`
- 赛事列表增强：支持 `category/sortBy/eventType/tag`
  - `sortBy`: `hot/newest/deadline/default`
  - `eventType` 映射：基于现有字段 `support_appointment` + `support_channel_enroll` 推导
  - `tag`：`recommend_tags LIKE`
- 课程列表增强：支持 `category/sortBy`（保留原 `courseType` 兼容）

## C. mvn compile 结果
- 执行：`mvn compile -DskipTests`
- 结果：`BUILD SUCCESS`
- 摘要：18/18 模块成功，`Total time: 45.278 s`

补充（按接口实测流程刷新本地仓库）：
- 执行：`mvn clean install -DskipTests`
- 结果：`BUILD SUCCESS`
- 摘要：18/18 模块成功，`Total time: 01:47 min`

## D. .http / 接口实测结果
本地启动 `ruoyi-admin`（8080）后，使用实际 HTTP 请求验证 FILTER-1~4：

1. FILTER-1 `GET /jst/wx/dict/contest-categories`
   - HTTP 200
   - body: `code=200`，返回 `data` 数组（当前环境有数据）
2. FILTER-2 `GET /jst/wx/dict/course-categories`
   - HTTP 200
   - body: `code=200`，返回 `data` 数组（当前环境为空数组）
3. FILTER-3 `GET /jst/wx/contest/list?pageNum=1&pageSize=10&category=art&sortBy=hot&eventType=online&tag=%E8%89%BA%E6%9C%AF`
   - HTTP 200
   - body: `code=200`，`rows` 数组（当前环境为空数组）
4. FILTER-4 `GET /jst/wx/course/list?pageNum=1&pageSize=10&category=ai_maic&sortBy=hot`
   - HTTP 200
   - body: `code=200`，`rows` 数组（当前环境为空数组）

## E. 遗留 TODO
- `jst_course` 当前无独立 `category` 字段，课程 `category` 先按现有字段做兼容筛选（`course_type` 精确 + 名称/简介/备注模糊）。
- `jst_contest` 当前无独立 `event_type` 字段，`eventType` 采用现有字段映射（`support_appointment/support_channel_enroll`）实现筛选；若后续新增正式字段，建议直接切换为列过滤。
- 当前运行库字典数据存在重复（`contest-categories` 返回重复项），建议后续清理历史重复字典值。

## F. 我做了任务卡之外的什么
- 除任务卡要求的 `mvn compile` 外，额外执行了 `mvn clean install -DskipTests` 与 4 个接口的真实 HTTP 调用验证。
- 在分类 wrapper 接口中将返回结构收敛为 `label/value`，避免透出不必要字段。

## G. 自检清单确认（16-安全文档 §8）
- [x] 新增公开接口显式 `@Anonymous`
- [x] DTO 入参增加 JSR-303 约束（`sortBy/eventType/category/tag`）
- [x] Controller 未直接调用 Mapper（通过 Service）
- [x] 无 `${}` 动态拼接 SQL
- [x] 无写操作新增，故无事务/锁遗漏风险
- [x] 无敏感字段明文日志输出
