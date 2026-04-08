# 任务报告 - F7 赛事CRUD与查询

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_contest`、`jst_event_partner`、`jst_enroll_form_template`、`sys_dict_data`
2. 涉及状态机：`SM-5a`、`SM-5b`
3. 涉及权限标识：`jst:event:contest:add`、`jst:event:contest:edit`、`jst:event:contest:submit`、`jst:event:contest:list`、`jst:event:contest:audit`、`jst:event:contest:online`、`jst:event:contest:offline`
4. 涉及锁名：`lock:contest:audit:{contestId}`、`lock:contest:status:{contestId}`
5. 事务边界：`ContestServiceImpl.addContest`、`ContestServiceImpl.editContest`、`ContestServiceImpl.submitContest`、`ContestServiceImpl.approveContest`、`ContestServiceImpl.rejectContest`、`ContestServiceImpl.onlineContest`、`ContestServiceImpl.offlineContest`，均为 `@Transactional(rollbackFor = Exception.class)`
6. ResVO 字段：
   `ContestListVO`：`contestId/contestName/category/groupLevels/coverImage/price/enrollStartTime/enrollEndTime/auditStatus/status/partnerId/partnerName`
   `ContestDetailVO`：`contestId/contestName/sourceType/partnerId/partnerName/category/groupLevels/coverImage/description/enrollStartTime/enrollEndTime/eventStartTime/eventEndTime/price/supportChannelEnroll/supportPointsDeduct/supportAppointment/certRuleJson/scoreRuleJson/formTemplateId/aftersaleDays/auditStatus/status/createdUserId`
   `WxContestCardVO`：`contestId/contestName/coverImage/category/price/enrollStartTime/enrollEndTime/status/enrollOpen/partnerName`
   `WxContestDetailVO`：`contestId/contestName/coverImage/category/groupLevels/description/price/enrollStartTime/enrollEndTime/eventStartTime/eventEndTime/status/enrollOpen/partnerName/supportChannelEnroll/supportPointsDeduct/supportAppointment/certRuleJson/scoreRuleJson/aftersaleDays`
   `CategoryStatVO`：`code/name/count`
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/ContestAuditStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/enums/ContestBizStatus.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ContestSaveReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/ContestQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/WxContestQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/AuditReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestListVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxContestCardVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxContestDetailVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/CategoryStatVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/ContestMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/ContestService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/ContestServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/ContestController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxContestController.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/ContestMapperExt.xml`

修改文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/api-tests.http`

## C. mvn compile 结果
`D:\coding\jst_v1\RuoYi-Vue` 执行：
`mvn compile -DskipTests`

结果：
```text
[INFO] BUILD SUCCESS
[INFO] Total time:  01:01 min
```

## D. .http 测试结果
测试块已追加，等待用户阶段汇总时统一跑。

已追加 F7 用例：
- `F7-0` / `F7-0B` 赛事方 A/B 登录
- `F7-1` 创建草稿
- `F7-2` 提交审核
- `F7-3A` 审核通过
- `F7-3B` 上线
- `F7-4` 小程序列表
- `F7-5` 小程序详情
- `F7-6` 热门赛事
- `F7-7` 分类统计
- `F7-8` 赛事方越权编辑失败

我担心的点：
- 任务卡要求“小程序详情对 offline/draft 返回 404”，当前实现沿用项目现有 `AjaxResult + BizErrorCode` 风格，返回业务码 `20003(JST_EVENT_CONTEST_NOT_ONLINE)`，不是原生 HTTP 404。如果阶段汇总验收严格校验 HTTP 状态码，这一项可能需要主 Agent 决策是否补统一异常映射。

## E. 遗留 TODO
- 无代码阻塞项。
- 如需严格满足“未上线赛事详情返回 HTTP 404”，需补全局异常映射策略或单接口特殊处理。

## F. 我做了任务卡之外的什么
- 在 `BizErrorCode` 中补充了任务卡要求但原文件尚未存在的两个错误码：`JST_EVENT_CONTEST_NOT_ONLINE(20003)`、`JST_EVENT_CONTEST_ILLEGAL_TRANSIT(20004)`。
- F7 fixture 中补充了两条赛事方 `sys_user` + `sys_user_role` 测试账号，便于覆盖 `@PartnerScope` 越权场景。

## G. 自检清单确认 (16-安全文档 §8)
- [x] 所有方法都有权限控制或 `@Anonymous`
- [x] ReqDTO 有 JSR-303
- [x] 出参使用 VO / `AjaxResult`
- [x] 小程序接口零敏感字段泄漏
- [x] 详情接口在 Service 层做了归属校验
- [x] 写操作加了 `@OperateLog`
- [x] 状态机写操作使用 `@Transactional`
- [x] 审核/状态推进使用 `jstLockTemplate.execute`
- [x] 没有 `${}` 拼 SQL
- [x] 没有打印明文敏感字段
