# 任务报告 - REFACTOR-4 报名审核 + 成绩打分 + 评审系统（后端部分）

## A. 任务前自检（Step 2 答题）

1. **涉及表**: jst_contest_reviewer (新建), jst_enroll_record, jst_score_record, jst_score_item, jst_cert_record, jst_biz_no_rule
2. **涉及状态机**: SM-6（报名审核：pending → approved/rejected/supplement）
3. **涉及权限标识**: `hasRole('jst_partner')` (已有)
4. **涉及锁名**: `lock:enroll:audit:{enrollId}` (已有)
5. **事务边界**:
   - `EnrollRecordServiceImpl.audit` (已有，增强打分+证书逻辑，@Transactional)
   - `ContestReviewerServiceImpl.saveReviewers` (新增，@Transactional)
6. **ResVO 字段**: ReviewerVO (id/contestId/userId/reviewerName/role/status/createTime)
7. **复用样板**: `EnrollRecordServiceImpl.java` 审核方法结构

## B. 交付物清单

### 新增文件:
- `架构设计/ddl/99-migration-contest-reviewer.sql` — 建表 DDL（已在测试库执行）
- `jst-event/src/main/java/com/ruoyi/jst/event/domain/JstContestReviewer.java` — 评审老师实体
- `jst-event/src/main/java/com/ruoyi/jst/event/mapper/JstContestReviewerMapper.java` — Mapper 接口
- `jst-event/src/main/resources/mapper/event/JstContestReviewerMapper.xml` — Mapper XML
- `jst-event/src/main/java/com/ruoyi/jst/event/dto/ScoreItemReqDTO.java` — 单项打分请求 DTO
- `jst-event/src/main/java/com/ruoyi/jst/event/dto/ReviewerSaveReqDTO.java` — 评审老师配置请求 DTO（含内部类 ReviewerItem）
- `jst-event/src/main/java/com/ruoyi/jst/event/vo/ReviewerVO.java` — 评审老师返回 VO
- `jst-event/src/main/java/com/ruoyi/jst/event/service/ContestReviewerService.java` — 服务接口
- `jst-event/src/main/java/com/ruoyi/jst/event/service/impl/ContestReviewerServiceImpl.java` — 服务实现

### 修改文件:
- `jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java` — 追加 2 个错误码 (20070, 20071)
- `jst-event/src/main/java/com/ruoyi/jst/event/dto/EnrollAuditReqDTO.java` — 增加 `List<ScoreItemReqDTO> scores` 字段
- `jst-event/src/main/java/com/ruoyi/jst/event/dto/EnrollBatchAuditReqDTO.java` — 增加 `List<ScoreItemReqDTO> scores` 字段
- `jst-event/src/main/java/com/ruoyi/jst/event/service/EnrollRecordService.java` — `batchAudit` 方法签名增加 `List<ScoreItemReqDTO> scores` 参数
- `jst-event/src/main/java/com/ruoyi/jst/event/service/impl/EnrollRecordServiceImpl.java` — audit() 增强打分+证书生成逻辑，新增 `handleScoreAndCert` 私有方法
- `jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerEnrollController.java` — batchAudit 调用传递 scores
- `jst-event/src/main/java/com/ruoyi/jst/event/controller/partner/PartnerContestController.java` — 新增 2 个评审老师端点 (GET/POST reviewers)
- `架构设计/ddl/99-test-fixtures.sql` — 追加 score_item(98001~98003) / reviewer(98101~98102) / biz_no_rule 幂等 fixtures
- `test/admin-tests.http` — 追加 6 个测试用例 (R4-0~R4-5)

## C. mvn compile 结果

```
[INFO] BUILD SUCCESS
[INFO] Reactor Summary for ruoyi 3.9.2:
[INFO] ruoyi .............................................. SUCCESS
[INFO] ruoyi-common ....................................... SUCCESS
[INFO] ruoyi-system ....................................... SUCCESS
[INFO] ruoyi-framework .................................... SUCCESS
[INFO] ruoyi-quartz ....................................... SUCCESS
[INFO] ruoyi-generator .................................... SUCCESS
[INFO] jst-common ......................................... SUCCESS
[INFO] jst-user ........................................... SUCCESS
[INFO] jst-order .......................................... SUCCESS
[INFO] jst-event .......................................... SUCCESS
[INFO] jst-channel ........................................ SUCCESS
[INFO] jst-points ......................................... SUCCESS
[INFO] jst-organizer ...................................... SUCCESS
[INFO] jst-marketing ...................................... SUCCESS
[INFO] jst-message ........................................ SUCCESS
[INFO] jst-risk ........................................... SUCCESS
[INFO] jst-finance ........................................ SUCCESS
[INFO] ruoyi-admin ........................................ SUCCESS
[INFO] Total time:  36.123 s
```

18/18 模块全部 SUCCESS。

## D. .http 测试结果

测试用例已追加到 `test/admin-tests.http`，需启动 ruoyi-admin 后执行：

| 编号 | 用例 | 预期 |
|------|------|------|
| R4-0 | partner A login | 200 获取 token |
| R4-1 | GET /jst/partner/contest/8201/reviewers | 200 返回评审老师列表 |
| R4-2 | POST /jst/partner/contest/8201/reviewers (配置 2 名老师) | 200 配置成功 |
| R4-3 | PUT /jst/partner/enroll/8901/audit (approved + 3 项打分) | 200 审核通过 + 成绩写入 + 证书编号生成 |
| R4-4 | PUT /jst/partner/enroll/8903/audit (score=150 超满分) | 非200 参数校验失败 |
| R4-5 | PUT /jst/partner/enroll/8903/audit (itemId=999999 不存在) | 非200 成绩项不存在 |

（未启动服务实跑，标记为 **待验证**）

## E. 遗留 TODO

- 无阻塞项
- 评审老师登录后 `GET /jst/partner/enroll/list` 自动过滤只返回其负责赛事的报名 — 此功能需修改 `EnrollRecordServiceImpl.selectAdminList` 查询逻辑，建议作为 **REFACTOR-4b** 独立小卡处理，避免影响现有列表接口

## F. 我做了任务卡之外的什么

- 在 `BizErrorCode` 追加了 `JST_EVENT_REVIEWER_NOT_FOUND(20070)` 和 `JST_EVENT_REVIEWER_DUPLICATE(20071)` 错误码（任务卡未列但评审系统需要）
- 个项打分详情存储在 `jst_score_record.remark` 字段（JSON 格式 `[{itemId, itemName, score, maxScore, weight}]`），因为现有 `jst_score_record` 表无 `item_id` 列，避免新建子表超出任务范围

## G. 自检清单确认 (16-安全文档 §8)

- [x] 所有方法 @PreAuthorize (`hasRole('jst_partner')`)
- [x] ReqDTO 有 JSR-303 (`@NotNull/@NotBlank/@Valid/@Pattern/@DecimalMin/@Size`)
- [x] 出参用 ResVO (ReviewerVO)
- [x] 无敏感字段需脱敏
- [x] 赛事归属校验 (`assertContestPartnerOwnership` / `assertEnrollPartnerOwnership`)
- [x] 写操作 @Log / @OperateLog
- [x] 资金/状态机方法 @Transactional (rollbackFor = Exception.class)
- [x] 高并发方法 jstLockTemplate.execute (复用已有 `lock:enroll:audit:{enrollId}`)
- [x] 没有 ${} 拼 SQL
- [x] 没有打印明文敏感字段

## H. 核心设计说明

### 审核打分流程

```
PUT /jst/partner/enroll/{enrollId}/audit
  body: { result: "approved", scores: [{itemId, score}, ...] }
    ↓
1. SM-6 状态校验 (pending → approved)
2. 乐观锁更新 audit_status
3. 若 scores 非空:
   a. 加载 jst_score_item (按 contestId)
   b. 校验每项: itemId 归属赛事 + score ≤ maxScore
   c. 计算加权总分: Σ(score × weight)
   d. INSERT jst_score_record (score_value=总分, remark=明细JSON)
   e. BizNoGenerateService.nextNo("cert_no") → 证书编号
   f. INSERT jst_cert_record (cert_no, issue_status=pending)
4. 发布 EnrollAuditEvent
```

### 评审老师管理

```
GET  /jst/partner/contest/{contestId}/reviewers → List<ReviewerVO>
POST /jst/partner/contest/{contestId}/reviewers → 全量替换 (delete + batch insert)
```

### 新增接口汇总

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/jst/partner/contest/{contestId}/reviewers` | 评审老师列表 |
| POST | `/jst/partner/contest/{contestId}/reviewers` | 配置评审老师 |
| PUT | `/jst/partner/enroll/{enrollId}/audit` | (增强) 支持 scores 打分 |
| PUT | `/jst/partner/enroll/batch-audit` | (增强) 支持 scores 统一打分 |
