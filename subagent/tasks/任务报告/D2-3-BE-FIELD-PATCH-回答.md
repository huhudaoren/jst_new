# 任务报告 - D2-3 BE FIELD PATCH 字段补齐

## A. 任务前自检
1. 涉及表：`jst_appointment_record`、`jst_appointment_writeoff_item`、`jst_user_rights`、`jst_rights_template`、`jst_rights_writeoff_record`
2. 涉及状态机：`SM-11(jst_appointment_record.main_status)`、`SM-12(jst_appointment_writeoff_item.status)`、`SM-17(jst_user_rights.status)`
3. 涉及权限标识：`jst:marketing:campaign:list`、`jst:marketing:campaign:query`
4. 涉及锁名：沿用 `lock:rights:writeoff:{userRightsId}`，未新增锁
5. 事务边界：`RightsUserServiceImpl.applyWriteoff`
6. ResVO / DTO：`AppointmentListVO`、`RightsWriteoffApplyDTO`
7. 复用样板：沿用 `RightsUserServiceImpl` 现有自助核销链路与 `AppointmentRecordMapperExt.xml` 现有 VO 自动映射方式

## B. 实现结果
### Part A - AppointmentListVO 核销进度字段
- 在 [AppointmentListVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/AppointmentListVO.java) 新增 `writeoffDoneCount`、`writeoffTotalCount`
- 在 [AppointmentRecordMapperExt.xml](D:/coding/jst_v1/RuoYi-Vue/jst-event/src/main/resources/mapper/event/AppointmentRecordMapperExt.xml) 的 `selectMyList` 增加两个子查询聚合
- 按当前真实基线实现：
  - 核销表使用 `jst_appointment_writeoff_item`
  - 已核销状态使用 `status='used'`
  - 未按任务卡里的旧名 `jst_appointment_writeoff / done` 误改

### Part B - RightsWriteoffApplyDTO 次数字段
- 在 [RightsWriteoffApplyDTO.java](D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/dto/RightsWriteoffApplyDTO.java) 新增 `Integer writeoffCount`
- 在 [RightsUserServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/service/impl/RightsUserServiceImpl.java) 调整 `quotaMode` 分流：
  - `count/times`：优先使用 `writeoffCount`，缺省按 `1`
  - `amount`：强制要求 `writeoffAmount`
  - `unlimited/period`：保持原有单次核销处理
- `jst_rights_writeoff_record.use_amount` 继续复用单列：
  - amount 模式存金额
  - count 模式存次数，按 `BigDecimal` 落库

### Part B - 为 count=2 场景补齐 fixture
- 在 [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql) 追加：
  - `jst_rights_template.rights_template_id=9773`
  - `jst_user_rights.user_rights_id=9873`
- 这样不影响原有 `9871` 单次自助核销用例，同时能让 `writeoffCount=2` 的 `.http` 场景真实可跑

### Part C - WxCampaignController 权限注解修正
- 在 [WxCampaignController.java](D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/controller/wx/WxCampaignController.java) 改为：
  - 列表：`jst:marketing:campaign:list`
  - 详情：`jst:marketing:campaign:query`
- 保留 `@ss.hasRole('jst_student')` 兜底

### Part D - wx-tests.http 补充
- 在 [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http) 补充：
  - appointment my 列表断言 `writeoffDoneCount/writeoffTotalCount`
  - count 模式自助核销 `writeoffCount=2`，断言 `remainQuota === 3`
  - campaign 既有 list/detail 用例保留，仍断言 `200`

## C. 交付物清单
修改文件：
- [AppointmentListVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/AppointmentListVO.java)
- [AppointmentRecordMapperExt.xml](D:/coding/jst_v1/RuoYi-Vue/jst-event/src/main/resources/mapper/event/AppointmentRecordMapperExt.xml)
- [RightsWriteoffApplyDTO.java](D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/dto/RightsWriteoffApplyDTO.java)
- [RightsUserServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/service/impl/RightsUserServiceImpl.java)
- [WxCampaignController.java](D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/controller/wx/WxCampaignController.java)
- [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql)
- [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http)

## D. 编译结果
- 已执行：`mvn -pl jst-event,jst-marketing -am compile -DskipTests`
- 结果：`BUILD SUCCESS`
- 已额外执行：`mvn compile -DskipTests`
- 结果：18 模块 `BUILD SUCCESS`

## E. .http 结果
- `wx-tests.http` 测试块已补齐
- 本次未在本地逐条点跑
- 未执行原因：当前任务以后端补丁与基线修正为主，已完成编译和 fixture/SQL 自检，但未额外拉起接口服务做端到端点击验证

## F. 风险与说明
- `RightsWriteoffApplyDTO` 仍保持“模式相关字段不做 `@NotNull`”的设计，必填约束放在 service 层按 `quotaMode` 分流校验，避免 amount/count 两种模式互相误伤
- D2-3 任务卡里的 appointment 核销表名与状态值已过时；本次实现明确按仓库现状落在 `jst_appointment_writeoff_item + used`

## G. 自检清单
- [x] Part A AppointmentListVO 补字段 + SQL 聚合
- [x] Part B RightsWriteoffApplyDTO 补 `writeoffCount` + 服务层分流
- [x] Part C WxCampaignController 权限注解修正
- [x] 模块编译通过
- [x] 全仓编译通过
- [x] wx-tests.http 已补三段验证
- [x] 任务报告已输出
