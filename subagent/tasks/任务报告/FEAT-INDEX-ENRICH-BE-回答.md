# 任务报告 - FEAT-INDEX-ENRICH-BE 首页聚合接口

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_contest`、`jst_enroll_record`、`jst_course`、`jst_course_learn_record`、`jst_notice`
2. 涉及状态机：`SM-5`（赛事审核状态用于在线筛选）、`SM-21`（课程审核状态用于上架筛选）；本任务仅查询，不做跃迁
3. 涉及权限标识：无（4 个接口均为公开接口，`@Anonymous`）
4. 涉及锁名：无（纯读接口，无并发写入）
5. 事务边界：无新增写操作方法，无 `@Transactional` 新增点
6. ResVO 字段：
   - 推荐赛事 `ContestListVO`：`contestId/contestName/category/groupLevels/coverImage/price/enrollStartTime/enrollEndTime/eventStartTime/eventEndTime/enrollCount/auditStatus/status/partnerId/partnerName/recommendTags`
   - 推荐课程 `CourseListVO`：`courseId/courseName/courseType/coverImage/price/pointsPrice/lessonCount/learnerCount/creatorType/creatorId/auditStatus/status/createTime/updateTime`
   - 热门标签 `HomeHotTagVO`：`tag/count`
   - 专题活动 `HomeTopicVO`：`noticeId/title/coverImage/description/linkUrl/publishTime`
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxHomeController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/HomeService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/HomeServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/HomeMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/HomeHotTagVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/HomeTagSourceVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/HomeTopicVO.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/HomeMapperExt.xml`

修改文件：
- `架构设计/ddl/99-test-fixtures.sql`（追加 7004~7006 三条 `category='topic'` 专题活动 fixture）
- `test/wx-tests.http`（追加 HOME-1 ~ HOME-4 测试块）

方案选择说明（Part A）：
- 采用**方案 1：复用 `jst_notice`**，以 `category='topic'` 作为专题活动
- 映射规则：`cover_image` 作为封面，`content` 作为描述，`remark` 作为跳转链接
- 选择理由：最小改动、无新表、与现有公告模型兼容

## C. mvn compile 结果
- 执行：`mvn compile -DskipTests`
- 结果：`BUILD SUCCESS`
- 摘要：18/18 模块成功，`Total time: 01:41 min`

补充（为联调刷新本地依赖）：
- 执行：`mvn clean install -DskipTests`
- 结果：`BUILD SUCCESS`
- 摘要：18/18 模块成功，`Total time: 01:54 min`

## D. .http / 接口实测结果
说明：本地启动 `ruoyi-admin` 后，按 HOME-1~4 路由实际调用校验。

1. HOME-1 `GET /jst/wx/home/recommend-contests`：`HTTP 200`，`code=200`，返回数组（当前环境 1 条）
2. HOME-2 `GET /jst/wx/home/recommend-courses`：`HTTP 200`，`code=200`，返回数组（当前环境 6 条）
3. HOME-3 `GET /jst/wx/home/hot-tags`：`HTTP 200`，`code=200`，返回数组（当前环境 1 条）
4. HOME-4 `GET /jst/wx/home/topics`：`HTTP 200`，`code=200`，返回数组（当前环境 0 条）

## E. 遗留 TODO
- 当前运行库未执行 `recommend_tags/lesson_count/learner_count` 相关迁移字段，已在实现中做兼容查询（不依赖缺失列）。
- HOME-4 返回空数组是因为本地库未重新导入 `99-test-fixtures.sql` 新增 topic 数据；导入后应返回最多 3 条专题。

## F. 我做了任务卡之外的什么
- 为保证当前库结构可运行，对推荐课程人数改为从 `jst_course_learn_record` 聚合；热门标签在缺失 `recommend_tags` 列时自动走 `category` 聚合 fallback。
- 为避免匿名访问扫描差异，在 `WxHomeController` 的每个方法上显式增加了 `@Anonymous`。

## G. 自检清单确认（16-安全文档 §8）
- [x] 公开接口显式 `@Anonymous`
- [x] Controller 未直接调用 Mapper（通过 `HomeService`）
- [x] 未新增写接口，无事务/锁遗漏风险
- [x] 无 `${}` 动态拼接 SQL
- [x] 无敏感信息打印
