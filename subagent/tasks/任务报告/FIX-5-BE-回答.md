# 任务报告 - FIX-5-BE 缺失页面后端接口

## A. 任务前自检（Step 2 答题）
1. 涉及表：
   `jst_score_record`、`jst_cert_record`、`jst_enroll_record`、`jst_contest`、`jst_participant`、`jst_cert_template`、`jst_message`
2. 涉及状态机：
   `SM-19`（成绩发布态，读取 `publish_status='published'` 的成绩）
   `SM-20`（证书发放态，读取 `issue_status in ('issued','granted')` 的证书；验真时 `issued/granted` 视为有效）
3. 涉及权限标识：
   本卡没有新增 `jst:xxx:xxx` 细粒度权限点；
   已落地的权限控制为 `@ss.hasRole('jst_student')`、`@ss.hasRole('jst_channel')`，公开接口使用 `@Anonymous`
4. 涉及锁名：
   无。本卡没有并发写锁场景，未新增 `lock:xxx:{id}` 级别锁
5. 事务边界：
   `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/service/impl/WxMessageServiceImpl.java`
   `markMessageRead()` + `@Transactional(rollbackFor = Exception.class)`
   `markAllRead()` + `@Transactional(rollbackFor = Exception.class)`
6. ResVO 字段（脱敏后）：
   - `WxScoreVO`：`scoreId/enrollId/contestId/contestName/category/scoreValue/score/rankNo/rank/awardLevel/remark/publishTime/hasCert/scoreLabel`
   - `WxCertVO`：`certId/contestId/contestName/category/certName/certNo/awardLevel/certImageUrl/certFileUrl/holderName/issueTime/issueDate`
   - `WxMessageVO`：`messageId/type/msgType/title/content/readStatus/readFlag/bizType/bizId/linkUrl/linkText/createTime`
   - `PublicScoreVO`：`contestId/contestName/category/scoreValue/score/rankNo/rank/awardLevel/remark/publishTime`
   - `PublicCertVerifyVO`：`certNo/contestName/holderName(脱敏)/awardLevel/groupLevel/issueOrg/issueTime/issueDate/valid`
7. 复用模板：
   `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/support/SchemaInspector.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/public_/PublicCertController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/public_/PublicScoreController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxCertController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxScoreController.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/PublicCertVerifyQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/PublicScoreQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/WxScoreQueryDTO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/WxCertMapper.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/WxScoreMapper.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/WxCertService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/WxScoreService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/WxCertServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/WxScoreServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PublicCertVerifyVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/PublicScoreVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxCertVO.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/WxScoreVO.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/WxCertMapper.xml`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/WxScoreMapper.xml`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/controller/wx/WxMessageController.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/dto/WxMessageQueryDTO.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/mapper/WxMessageMapper.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/service/WxMessageService.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/service/impl/WxMessageServiceImpl.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/vo/WxMessageVO.java`
- `RuoYi-Vue/jst-message/src/main/resources/mapper/message/WxMessageMapper.xml`

修改文件：
- `test/wx-tests.http`

实现说明：
- Part A：补齐 `GET /jst/wx/score/my`，优先查 `jst_score_record`，若缺表则尝试 `jst_enroll_record(score_value/award/publish_time)` 兜底；并兼容前端字段 `score/rank/hasCert`
- Part B：补齐 `GET /jst/wx/cert/my`，读取已发放证书；若证书表不存在则返回空数组
- Part C：补齐消息中心 3 个接口；若 `jst_message` 缺表或缺关键列则空返回 / 成功幂等
- Part D：补齐 `GET /jst/public/score/query`，加 `@Anonymous + @RateLimiter(limitType=IP)`；同时兼容当前 uniapp 实际传参，允许 `name` 作为手机号/报名号关键字，允许 `contestId` 非数字时按赛事名模糊匹配
- Part E：补齐 `GET /jst/public/cert/verify`，加 `@Anonymous + @RateLimiter(limitType=IP)`；证书有效时返回 `valid=true`，未命中时返回 `null` 以兼容当前前端“null=验真失败”的页面逻辑
- Part F：未直接修改 `SecurityConfig.java`。当前项目已有 `@Anonymous -> PermitAllUrlProperties -> permitAll` 自动白名单链路，保持既有方式实现公开免登录
- Part G：在 `test/wx-tests.http` 追加 FIX5-1 ~ FIX5-7 测试块

## C. 编译 / 打包结果
- `mvn -pl jst-event,jst-message,jst-common -am compile -DskipTests`
  - `BUILD SUCCESS`
- `mvn compile -DskipTests`
  - `BUILD SUCCESS`
  - 18 模块全绿
- `mvn -pl ruoyi-admin -am package -DskipTests`
  - `BUILD SUCCESS`
  - 成功产出 `ruoyi-admin/target/ruoyi-admin.jar`

## D. 本地 HTTP 实测结果（真实请求）
说明：
- 现有 8080 上已有一个旧的 `ruoyi-admin` 进程在监听，但它不是本次最新构建版本；我对 `GET /jst/public/cert/verify?certNo=CERT_TEST_001` 的探测结果是 HTTP 401，说明公开白名单未反映到该旧进程
- 为避免强制杀掉你当前 8080 进程影响本地环境，我临时启动了最新构建的 `ruoyi-admin.jar` 到 `18080` 做烟测，验证后已记录结果；这属于“验证端口偏离”，不是交付接口路径偏离

实测结果：
- `POST /jst/wx/auth/login` with `MOCK_1001`
  - HTTP 200
  - body 摘要：`{"code":200,"data":{"token":"<omitted>","userInfo":{"userId":1001},"roles":["jst_student"]}}`
- `GET /jst/wx/score/my`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[]}`
- `GET /jst/wx/cert/my`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[]}`
- `GET /jst/wx/message/my`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[]}`
- `POST /jst/wx/message/read-all`
  - HTTP 200
  - body 摘要：`{"code":200}`
- `GET /jst/public/score/query?name=测试&mobile=13800001001`
  - HTTP 200
  - body 摘要：`{"code":200,"data":[]}`
- `GET /jst/public/cert/verify?certNo=CERT_TEST_001`
  - HTTP 200
  - body 摘要：`{"code":200}`
  - 说明：当前测试库未命中证书，接口按前端兼容策略返回 `null/无data`

## E. 遗留 TODO
- 当前测试库里 `MOCK_1001` 对应的成绩、证书、消息数据均为空，所以本次只能验证“接口存在 + 空态不报错”，未验证非空数据渲染
- `jst_message` 在任务卡背景里是“可能缺表”的兼容场景；当前实现按关键列存在性探测后再访问，若后续正式落库字段命名与任务卡不一致，需要同步 mapper SQL
- 公开成绩查询的 `idNo/idCard4` 查询依赖 `jst_participant.id_card_no` 可检索；如果线上真实存储为不可直接检索的密文，需后续再补专用索引字段或脱敏尾号字段

## F. 我做了任务卡之外的什么
- 为避免在多个模块里重复写 `DatabaseMetaData` 判断，额外新增了共享组件 `SchemaInspector`
- 为兼容当前 uniapp 页面实际消费方式，做了两处前端兼容型偏离：
  - 公开证书验真未命中时返回 `null`，而不是 `{valid:false}`，否则当前页面会把“失败”误渲染成“成功”
  - 公开成绩查询兼容 `name` 单字段关键字和非数字 `contestId`（实际页面传的是赛事名输入框）
- 没有改前端、没有改 DDL、没有加依赖、没有改 `SecurityConfig` / `application.yml`

## G. 自检清单确认（16-安全文档 §8）
- [x] 所有新增接口均有 `@PreAuthorize` 或 `@Anonymous`
- [x] 未新增明文敏感字段输出；公开证书持有人姓名已脱敏
- [x] Controller 未直调 Mapper，均经 Service
- [x] Service 未手写硬编码 SQL
- [x] Mapper.xml 未使用 `${}` 拼接业务 SQL
- [x] 写操作 `markMessageRead/markAllRead` 已加 `@Transactional(rollbackFor = Exception.class)`
- [x] 本卡无高并发写锁场景，未误加分布式锁
- [x] 缺表/缺列场景已做兼容空返回，避免页面直接 500
- [x] `mvn compile -DskipTests` 已通过
- [x] `test/wx-tests.http` 已补 FIX5-1 ~ FIX5-7
