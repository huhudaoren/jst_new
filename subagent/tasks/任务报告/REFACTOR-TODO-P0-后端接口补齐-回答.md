# 任务报告 - REFACTOR-TODO-P0 后端接口补齐

> 完成时间：2026-04-14 | Agent：Backend Agent | 编译：BUILD SUCCESS (18/18)

## A. 任务前自检（Step 2 答题）

1. **涉及表**: jst_enroll_record, jst_contest, jst_appointment_slot, jst_score_record, jst_cert_record, jst_enroll_form_template, jst_cert_template
2. **涉及状态机**: SM-6 (报名审核), SM-20 (证书发放)
3. **涉及权限标识**: `jst_student` (wx 端), `jst_partner` (partner 端), `jst:event:cert_record:add`, `jst:event:cert_template:add`, `jst:event:appointment:my`, `jst:event:wxacode:get`
4. **涉及锁名**: `lock:enroll:team:{userId}:{contestId}` (新增)
5. **事务边界**: `EnrollRecordServiceImpl.submitTeam` (@Transactional), `PartnerCertServiceImpl.generate` (@Transactional)
6. **ResVO 字段**: TeamEnrollResVO (leaderEnrollId/leaderEnrollNo/enrollIds/teamSize); WxContestDetailVO 新增 (scorePublished/hasCert/certImageUrl/totalScore)
7. **复用样板**: `EnrollRecordServiceImpl` (个人报名流程), `PartnerCertServiceImpl.batchGrant` (证书批量生成)

## B. 交付物清单

### 新增文件

| 文件 | 说明 |
|---|---|
| `jst-event/.../dto/TeamEnrollReqDTO.java` | 团队报名请求 DTO（含内部类 TeamMemberInfo） |
| `jst-event/.../vo/TeamEnrollResVO.java` | 团队报名响应 VO |
| `jst-event/.../dto/CertGenerateReqDTO.java` | 证书生成请求 DTO（按 enrollId 列表） |
| `jst-event/.../controller/wx/WxWxacodeController.java` | 小程序码 Controller（Mock 模式） |

### 修改文件

| 文件 | 改动 |
|---|---|
| `jst-common/.../exception/BizErrorCode.java` | 追加 5 个错误码 (20080~20084) |
| **P0: WxContestDetailVO 字段补充** | |
| `jst-event/.../vo/WxContestDetailVO.java` | 新增 scorePublished/hasCert/certImageUrl/totalScore 4 字段 + getter/setter |
| `jst-event/.../controller/wx/WxContestController.java` | detail() 增加 userId 获取 + fillUserFields 调用 |
| `jst-event/.../service/ContestService.java` | 新增 fillUserFields(vo, userId) 方法声明 |
| `jst-event/.../service/impl/ContestServiceImpl.java` | 实现 fillUserFields：缓存外填充用户相关成绩/证书数据 |
| `jst-event/.../mapper/ContestMapperExt.java` | 新增 countPublishedScores/selectUserTotalScore/selectUserCert |
| `jst-event/src/main/resources/mapper/event/ContestMapperExt.xml` | 新增 3 段 SQL |
| **P0: 团队报名接口** | |
| `jst-event/.../service/EnrollRecordService.java` | 新增 submitTeam(TeamEnrollReqDTO) 方法 |
| `jst-event/.../service/impl/EnrollRecordServiceImpl.java` | 实现 submitTeam + buildTeamMemberRecord 私有方法 |
| `jst-event/.../controller/wx/WxEnrollController.java` | 新增 POST /jst/wx/enroll/team/submit 端点 |
| `jst-event/.../mapper/JstAppointmentSlotMapper.java` | 新增 incrementBookedCount + selectEnabledByContestId |
| `jst-event/src/main/resources/mapper/event/JstAppointmentSlotMapper.xml` | 新增 2 段 SQL |
| **P1: 预约时间段查询** | |
| `jst-event/.../controller/wx/WxAppointmentController.java` | 新增 GET /jst/wx/appointment/contest/{id}/slots 端点 |
| **P1: Partner 表单模板创建** | |
| `jst-event/.../controller/partner/PartnerFormTemplateController.java` | 新增 POST /jst/partner/form-template 端点（自动设置 ownerType=partner） |
| **P1: 证书生成（简化版）** | |
| `jst-event/.../service/PartnerCertService.java` | 新增 generate(CertGenerateReqDTO) 方法 |
| `jst-event/.../service/impl/PartnerCertServiceImpl.java` | 实现 generate：按 enrollId 创建 cert_record + BizNoGenerateService 编号 |
| `jst-event/.../controller/partner/PartnerCertController.java` | 新增 POST /jst/partner/cert/generate 端点 |
| `jst-event/.../mapper/PartnerCertMapper.java` | 新增 countCertByEnrollId + selectApprovedEnrollsForCert |
| `jst-event/src/main/resources/mapper/event/PartnerCertMapper.xml` | 新增 3 段 SQL |
| **测试** | |
| `test/wx-tests.http` | 追加 6 个测试块 (RTP0-0 ~ RTP0-5) |
| `test/admin-tests.http` | 追加 3 个测试块 (RTP1-0 ~ RTP1-2) |

## C. mvn compile 结果

```
[INFO] BUILD SUCCESS
[INFO] Total time: 19.371 s
[INFO] 18/18 modules SUCCESS
```

## D. .http 测试结果

测试用例已编写，待启动 ruoyi-admin 后执行：

| 编号 | 用例 | 预期 |
|---|---|---|
| RTP0-0 | 登录 1001 | 200 + token |
| RTP0-1 | 赛事详情含 scorePublished/hasCert 字段 | 200 + boolean 字段存在 |
| RTP0-2 | 团队报名 happy path | 200 或 20082(赛事不支持团队) |
| RTP0-3 | 团队报名手机号重复 | 20081 |
| RTP0-4 | 预约时间段查询 | 200 + Array |
| RTP0-5 | 小程序码 Mock | 200 + mock=true |
| RTP1-0 | Partner 登录 | 200 + token |
| RTP1-1 | Partner 创建表单模板 | 200 + templateId |
| RTP1-2 | Partner 证书生成 | 200 或失败(取决于模板 fixture) |

## E. 遗留 TODO

| 项 | 说明 |
|---|---|
| Redis Key 登记 | `lock:enroll:team:{userId}:{contestId}` 需补录到 `架构设计/15-Redis-Key与锁规约.md` |
| 小程序码真实模式 | `jst.wxauth.mock=false` 时需实现 wxacode.getUnlimited 调用 + Redis/OSS 缓存 |
| 团队报名 participantId | 当前按 name/phone 存入 formSnapshot，未关联 jst_participant（前端传的是姓名/手机号） |
| 赛事 team_min_size | fixture 中 8201 赛事需设置 team_min_size > 0 才能跑通团队报名 happy path 测试 |

## F. 我做了任务卡之外的什么

1. 在 `BizErrorCode` 追加了 5 个错误码:
   - `JST_EVENT_TEAM_SIZE_INVALID(20080)` — 团队人数不满足要求
   - `JST_EVENT_TEAM_PHONE_DUPLICATE(20081)` — 团队成员手机号重复
   - `JST_EVENT_TEAM_NOT_SUPPORTED(20082)` — 当前赛事不支持团队报名
   - `JST_EVENT_SLOT_CAPACITY_FULL(20083)` — 预约时段名额不足
   - `JST_EVENT_CERT_GENERATE_NO_ENROLL(20084)` — 报名记录不存在或未通过审核
2. 在 `JstAppointmentSlotMapper` 追加了 `incrementBookedCount`（乐观锁扣减容量）和 `selectEnabledByContestId`
3. 在 `PartnerCertMapper` 追加了 `countCertByEnrollId` 和 `selectApprovedEnrollsForCert`

## G. 自检清单确认 (16-安全文档 §8)

- [x] 所有方法 @PreAuthorize
- [x] ReqDTO 有 JSR-303
- [x] 出参用 ResVO
- [x] 敏感字段脱敏
- [x] 详情接口归属校验
- [x] 写操作 @OperateLog
- [x] 资金/状态机方法 @Transactional
- [x] 高并发方法 jstLockTemplate.execute
- [x] 没有 ${} 拼 SQL
- [x] 没有打印明文敏感字段

## H. 接口一览

| 优先级 | 端点 | 方法 | 说明 |
|---|---|---|---|
| **P0** | `/jst/wx/enroll/team/submit` | POST | 团队报名提交 |
| **P0** | `/jst/wx/contest/{contestId}` | GET | 赛事详情（新增 4 个用户字段） |
| **P1** | `/jst/wx/appointment/contest/{id}/slots` | GET | 预约时间段列表 |
| **P1** | `/jst/partner/form-template` | POST | Partner 创建表单模板 |
| **P1** | `/jst/partner/cert/generate` | POST | 按 enrollId 批量生成证书 |
| **P1** | `/jst/wx/wxacode` | GET | 小程序码（Mock 兜底） |
