# 任务报告 - F-CONTRACT-INVOICE-BE 合同/发票小程序端接口

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_contract_record`、`jst_invoice_record`、`jst_rebate_settlement`、`jst_event_settlement`
2. 涉及状态机：`SM-22`（合同，读取）、`SM-23`（发票，申请写入 `pending_apply`）
3. 涉及权限标识：无新增 `jst:xxx:xxx`，使用角色鉴权 `@PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")`
4. 涉及锁名：无（本任务不新增并发写锁）
5. 事务边界：`WxFinanceServiceImpl.applyInvoice(...)`，`@Transactional(rollbackFor = Exception.class)`（含 `// TX:` 注释）
6. ResVO 字段：
   - 合同：`contractId/contractNo/contractType/status/fileUrl/effectiveTime/signTime/remark/createTime`
   - 发票：`invoiceId/invoiceNo/refSettlementNo/invoiceTitle/taxNo/amount/status/fileUrl/expressStatus/issueTime/createTime`
7. 复用样板：
   - `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`
   - `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxTeamAppointmentController.java`
   - `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/service/impl/PartnerSettlementServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/controller/wx/WxContractController.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/controller/wx/WxInvoiceController.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/service/WxFinanceService.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/service/impl/WxFinanceServiceImpl.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/dto/InvoiceApplyForm.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/vo/ContractRecordVO.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/vo/InvoiceRecordVO.java`

修改文件：
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/mapper/JstContractRecordMapper.java`
- `RuoYi-Vue/jst-finance/src/main/java/com/ruoyi/jst/finance/mapper/JstInvoiceRecordMapper.java`
- `RuoYi-Vue/jst-finance/src/main/resources/mapper/finance/JstContractRecordMapper.xml`
- `RuoYi-Vue/jst-finance/src/main/resources/mapper/finance/JstInvoiceRecordMapper.xml`
- `test/wx-tests.http`（追加 `F-CONTRACT-INVOICE-BE` 测试块）

关键实现点：
- `targetType/targetId` 仅从登录上下文推导，前端不可透传；双身份时按 channel 优先。
- 列表/详情 SQL 全部加 `del_flag='0'`，详情全部做 `id + target_type + target_id` 归属过滤。
- 开票金额上限按实付口径：
  - channel：`jst_rebate_settlement.actual_pay_amount`
  - partner：`jst_event_settlement.final_amount`
- 申请开票落库状态固定 `pending_apply`，返回新建 `invoiceId`。
- 列表接口统一分页结构 `rows/total`。

## C. mvn compile 结果
- 执行命令：`mvn compile -DskipTests`（`RuoYi-Vue` 根目录）
- 结果：`BUILD SUCCESS`（18 模块全部成功）
- 结束时间：`2026-04-12 19:51:56 +08:00`

## D. .http/API 联调结果
执行前按规约完成：
1. 启动 `ruoyi-admin`（`ruoyi-admin` 目录下 `mvn spring-boot:run -DskipTests`）
2. 因单模块启动默认读取本地仓库依赖，先执行 `mvn -pl jst-finance -am install -DskipTests` 确保新接口生效
3. 服务日志确认 `Started RuoYiApplication in 130.877 seconds`

`F-CI` 用例结果（共 13 条）：
- `F-CI-0` channel 登录：HTTP `200`，业务码 `200`，通过
- `F-CI-1` channel 合同列表：HTTP `200`，业务码 `200`，`rows=0,total=0`，通过
- `F-CI-2` channel 发票列表：HTTP `200`，业务码 `200`，`rows=0,total=0`，通过
- `F-CI-3` channel 开票申请成功：HTTP `200`，业务码 `200`，返回 `invoiceId=94772`，通过
- `F-CI-4` channel 超限开票失败：HTTP `200`，业务码 `99901`，通过（符合预期失败）
- `F-CI-5` partner 登录：HTTP `200`，业务码 `200`，通过
- `F-CI-6` partner 合同列表：HTTP `200`，业务码 `200`，命中 `CT_PA7_94704`，通过
- `F-CI-7` partner 合同详情：HTTP `200`，业务码 `200`，`contractNo=CT_PA7_94704`，通过
- `F-CI-8` partner 发票列表：HTTP `200`，业务码 `200`，命中 `INV_PA7_94704`，通过
- `F-CI-9` partner 发票详情：HTTP `200`，业务码 `200`，`invoiceNo=INV_PA7_94704`，通过
- `F-CI-10` channel 越权查 partner 合同：HTTP `200`，业务码 `99902`，通过（符合预期失败）
- `F-CI-11` channel 越权查 partner 发票：HTTP `200`，业务码 `99902`，通过（符合预期失败）
- `F-CI-12` student 调用合同列表：HTTP `200`，业务码 `403`，通过（符合预期拒绝）

汇总：`PASS 13/13`

## E. 遗留 TODO
- 无

## F. 我做了任务卡之外的什么
- 为保证运行时加载到最新 `jst-finance` 代码，额外执行了 `mvn -pl jst-finance -am install -DskipTests`（不改业务逻辑、仅解决联调类路径问题）。

## G. 自检清单确认（安全与规范）
- [x] 接口鉴权已加（角色鉴权）
- [x] 入参使用 DTO + JSR-303
- [x] 出参使用 VO / 分页结构
- [x] 详情接口做了归属校验
- [x] 写操作有 `@OperateLog`
- [x] 写操作有 `@Transactional`
- [x] 无 `${}` 拼接 SQL
- [x] 无新增 DDL / 无改 `pom.xml` / 无新增权限菜单
