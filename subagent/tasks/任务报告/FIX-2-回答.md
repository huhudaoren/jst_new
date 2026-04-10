# 任务报告 - FIX-2 后端接口补齐

## A. 任务前自检（Step 2 答题）
1. 涉及表：
   `jst_order_main`、`jst_order_item`、`jst_rebate_ledger`、`jst_contest`、`jst_channel`、`jst_user`、`jst_participant`、`jst_enroll_record`、`jst_score_record`、`jst_cert_record`、`jst_course_learn_record`、`jst_points_account`、`jst_level_config`、`jst_user_coupon`、`jst_student_channel_binding`、`jst_notice`
2. 涉及状态机：
   `SM-14`（临时参赛档案认领状态；本卡只读取/限制 `unclaimed` 可改可删，不新增状态跃迁）
3. 涉及权限标识：
   无新增 `jst:xxx:xxx` 细粒度权限；本卡新增接口均使用角色鉴权：`@ss.hasRole('jst_channel')`、`@ss.hasRole('jst_partner')`
4. 涉及锁名：
   无。本卡无并发写锁场景，也未新增 `lock:xxx:{id}` 级别锁
5. 事务边界：
   `ChannelParticipantServiceImpl.updateParticipant()` + `@Transactional(rollbackFor = Exception.class)`  
   `ChannelParticipantServiceImpl.deleteParticipant()` + `@Transactional(rollbackFor = Exception.class)`
6. ResVO 字段（脱敏后）：
   - `ChannelTopContestResVO`：`contestId/contestName/category/enrollCount`
   - `ChannelTopStudentResVO`：`studentName/enrollCount/payAmount`
   - `ChannelOrderDetailResVO`：`orderId/orderNo/contestId/contestName/participantId/participantName/mobileMasked/orderStatus/refundStatus/priceOriginal/couponDiscount/pointsDiscount/pointsUsed/userNetPay/payAmount/platformFee/rebateBase/rebateRate/rebateAmount/channelOwner/timeline/participantSnapshot`
   - `ChannelParticipantResVO`：`participantId/name/school/className/guardianMobileMasked/guardianMobile/claimStatus/claimedUserName/createTime`
   - `UserProfileVO`：保留原 profile 字段，并补 `enrollCount/scoreCount/certCount/courseCount/frozenPoints/levelName/couponCount/unreadMsgCount/channelBinding`
   - `PartnerDashboardSummaryResVO`：`pendingEnrollCount/monthEnrollCount/pendingScoreCount/pendingCertificateCount`
   - `PartnerDashboardTodoResVO`：`pendingEnrollList/pendingScoreList`
   - `PartnerRecentNoticeResVO`：`noticeId/title/summary/publishTime`
   - `OrderDetailVO.ParticipantSnapshotVO`：补 `originalParticipantName`
7. 复用样板：
   `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/ChannelTopRankingReqDTO.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/ChannelOrderDetailResVO.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/ChannelTopContestResVO.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/ChannelTopStudentResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerDashboardController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/PartnerDashboardQueryReqDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/PartnerDashboardMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/PartnerDashboardService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/PartnerDashboardServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PartnerDashboardSummaryResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PartnerDashboardTodoItemResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PartnerDashboardTodoResVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PartnerRecentNoticeResVO.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/PartnerDashboardMapperExt.xml`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/dto/ChannelParticipantQueryReqDTO.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/dto/ChannelParticipantUpdateReqDTO.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/mapper/UserProfileLookupMapper.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/ChannelParticipantService.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/UserProfileService.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ChannelParticipantServiceImpl.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/UserProfileServiceImpl.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/vo/ChannelParticipantResVO.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/vo/UserProfileVO.java`
- `RuoYi-Vue/jst-user/src/main/resources/mapper/user/UserProfileLookupMapper.xml`

修改文件：
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/wx/WxChannelDashboardController.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/OrderMainLookupMapper.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/ChannelSupplementService.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelSupplementServiceImpl.java`
- `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/lookup/OrderMainLookupMapper.xml`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/impl/OrderServiceImpl.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/vo/OrderDetailVO.java`
- `RuoYi-Vue/jst-order/src/main/resources/mapper/order/OrderMainMapperExt.xml`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/controller/wx/WxParticipantController.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/controller/wx/WxUserController.java`
- `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/mapper/ParticipantMapper.java`
- `RuoYi-Vue/jst-user/src/main/resources/mapper/user/ParticipantMapper.xml`
- `test/wx-tests.http`

实现说明：
- Part A：补齐渠道排行榜 2 接口，SQL 最终按“返点归属到当前渠道 + 当前 active 绑定学生”统计，避免把渠道本人订单混入学生排行
- Part B：补齐渠道订单详情接口，完成 channel 归属校验、费用明细、渠道归属信息、时间轴与 `participantSnapshot` 兜底
- Part C：补齐临时档案列表/更新/软删接口，并限制仅 `unclaimed` 可编辑/删除
- Part D：补齐 `getProfile` 的 9 个扩展字段，并对真实表结构做兼容映射：`jst_course_learn_record / issue_status='issued' / coupon status='unused' / unreadMsgCount=0`
- Part E：新建赛事方工作台 3 接口，补 `@PartnerDataScope` 与 `@PreAuthorize("@ss.hasRole('jst_partner')")`
- Part F：在订单详情 VO 预留 `originalParticipantName`，并做读取兜底，不改 auto-claim 合并逻辑
- Part G：在 `test/wx-tests.http` 追加 FIX-2-1 ~ FIX-2-10 测试块

## C. 编译 / 打包结果
- `mvn compile -DskipTests`：`BUILD SUCCESS`，18 模块全绿，`Total time: 41.605 s`
- `mvn clean install -DskipTests`：`BUILD SUCCESS`，18 模块全绿，`Total time: 55.373 s`
- `mvn -pl jst-event,jst-channel -am compile -DskipTests`：`BUILD SUCCESS`，增量校验最终改动，`Total time: 25.363 s`
- `mvn -pl ruoyi-admin -am package -DskipTests`：`BUILD SUCCESS`，最终 fat jar 重打包成功，`Total time: 37.859 s`

## D. 本地 8080 实测结果（真实 HTTP）
测试方式：
- 启动 `RuoYi-Vue/ruoyi-admin/target/ruoyi-admin.jar`
- 渠道登录：`MOCK_9201`
- 学生/家长登录：`MOCK_1001`
- 赛事方登录：`partner_f7_a / admin123`

实测结果：
- `FIX-2-1 GET /jst/wx/channel/dashboard/top-contests?period=last30d&limit=5`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[{"contestId":8201,"contestName":"测试_F7_在线赛事","category":"art","enrollCount":1},{"contestId":8261,"contestName":"测试_C6_三子项预约赛事","category":"art","enrollCount":1}]}`
- `FIX-2-2 GET /jst/wx/channel/dashboard/top-students?limit=5`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[{"studentName":"F***********A","enrollCount":2,"payAmount":498.00},{"studentName":"F***********B","enrollCount":1,"payAmount":159.00}]}`
- `FIX-2-3 GET /jst/wx/channel/orders/94401`
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"orderId":94401,"orderNo":"OD_C5A_001","priceOriginal":123.00,"couponDiscount":0.00,"pointsDiscount":0.00,"userNetPay":123.00,"platformFee":5.00,"rebateBase":118.00,"rebateRate":0.1042,"rebateAmount":12.30,"channelOwner":{"channelId":9201,"channelType":"teacher","name":"测试_C5A渠道"},"timeline":[]}}`
- `FIX-2-4 GET /jst/wx/channel/participant/my?status=unclaimed&pageNum=1&pageSize=10`
  - HTTP 200
  - 初始 body 摘要：`{"code":200,"total":0,"rows":[]}`
- `FIX-2-4a POST /jst/wx/channel/participant/batch-create`（为 FIX-2-5/6 生成新鲜 `unclaimed` 数据）
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"createdCount":2,"participantIds":[3606,3607]}}`
  - 备注：现有 `BatchCreateParticipantReqDTO.gender` 为 `Integer`，实测请求需传 `1/2`，`wx-tests.http` 已按此修正
- `FIX-2-5 PUT /jst/wx/channel/participant/{id}`
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"participantId":3606,"name":"FIX2_HTTP_A_UPDATED","claimStatus":"unclaimed","guardianMobileMasked":"138****9021","school":"测试_FIX2小学_更新","className":"三年级1班"}}`
- `FIX-2-6 DELETE /jst/wx/channel/participant/{id}`
  - HTTP 200
  - body 摘要：`{"code":200}`
- `FIX-2-7 GET /jst/wx/user/profile`
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"enrollCount":15,"scoreCount":0,"certCount":0,"courseCount":1,"frozenPoints":0,"levelName":null,"couponCount":0,"unreadMsgCount":0,"channelBinding":null}}`
  - 备注：本次库中 `userId=1001` 当前无 active binding，所以 `channelBinding=null`；接口字段已按要求返回
- `FIX-2-8 GET /jst/partner/dashboard/summary`
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"pendingEnrollCount":1,"monthEnrollCount":14,"pendingScoreCount":0,"pendingCertificateCount":0}}`
- `FIX-2-9 GET /jst/partner/dashboard/todo`
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"pendingEnrollList":[{"enrollId":8903,"contestName":"测试_F7_在线赛事","studentName":"测试_张小红","submitTime":"2026-04-10 16:00:26"}],"pendingScoreList":[]}}`
- `FIX-2-10 GET /jst/partner/notice/recent`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[{"noticeId":7008},{"noticeId":7007},{"noticeId":7006}]}`

## E. 遗留 TODO
- 无阻塞项
- 说明 1：`partner/notice/recent` 的具体前三条公告内容取决于当前测试库 `jst_notice` 数据，本次实测返回 `7008/7007/7006`
- 说明 2：本卡未改 auto-claim 合并逻辑，仅对订单详情 VO/读取做 `originalParticipantName` 预留与兜底，符合任务卡 Part F 范围

## F. 我做了任务卡之外的什么
- 无越权改动
- 没有新增依赖、没有改 DDL、没有改前端、没有改父 pom / application.yml
- 中途为本地启动验证临时创建过 `tmp/start-fix2-ruoyi-admin.bat`，验证结束后已删除，不计入交付物

## G. 自检清单确认（16-安全文档 §8）
- [x] 本卡新增角色接口已补充 `@PreAuthorize`
- [x] ReqDTO 已补 JSR-303（本卡新增 DTO）
- [x] 出参走 ResVO / `AjaxResult.success(...)`
- [x] 敏感字段已脱敏（`studentName` / `mobileMasked` / `guardianMobileMasked`）
- [x] 详情接口已做归属校验（渠道订单详情、渠道临时档案）
- [x] 写操作已补 `@OperateLog`（临时档案更新/删除）
- [x] 写操作事务已补 `@Transactional`
- [x] 本卡无新增高并发写锁场景，无需 `jstLockTemplate.execute`
- [x] 未使用 `${}` 拼接业务 SQL
- [x] 未打印明文敏感字段
