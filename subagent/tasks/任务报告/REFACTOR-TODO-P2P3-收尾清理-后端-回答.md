# 任务报告 - REFACTOR-TODO-P2P3 收尾清理（Backend）

## A. 任务前自检（Step 2）
1. 涉及表：`jst_enroll_record`、`jst_participant`、`jst_contest_reviewer`、`jst_contest`
2. 涉及状态机：`SM-6`（报名审核状态机，当前改动未新增状态迁移）
3. 涉及权限标识：沿用既有 `hasRole('jst_partner')`（`/jst/partner/enroll/list`、`/jst/wx/enroll/team/submit`）
4. 涉及锁名：`lock:enroll:team:{userId}:{contestId}`（wait 5s / lease 10s）
5. 事务边界：`EnrollRecordServiceImpl.submitTeam()`（`@Transactional`）及既有报名事务不变
6. ResVO 字段：
   - 评审列表接口：`TableDataInfo.rows(EnrollListVO)`
   - 团队报名接口：`TeamEnrollResVO.leaderEnrollId/leaderEnrollNo/enrollIds/teamSize`
7. 复用模板：`jst-user/.../ParticipantClaimServiceImpl.java` 的临时档案字段策略（`temporary_participant / unclaimed / channel_only`）

## B. 交付物清单

### 新增/修改文件
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/EnrollQueryReqDTO.java`
  - 新增 `contestIds` 字段，用于 reviewer 自动注入赛事过滤范围。
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/EnrollRecordMapperExt.java`
  - 新增 `selectParticipantIdByPhone(phone, name)`
  - 新增 `insertTempParticipant(Map<String, Object>)`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/EnrollRecordMapperExt.xml`
  - 新增手机号复用 participant 查询 SQL
  - 新增临时 participant 插入 SQL（回填 `participantId`）
  - `selectAdminList` 新增 `contest_id in (query.contestIds)` 过滤
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/EnrollRecordServiceImpl.java`
  - `selectAdminList` 中接入 `ContestReviewerService#getReviewerContestIds`，自动注入 reviewer 赛事范围过滤
  - 团队报名锁时长改为 `wait=5s/lease=10s`
  - 团队报名新增 participant 关联：先按手机号复用，缺失时创建临时档案
  - 队长/队员创建报名记录时都写入 `participantId`
- `架构设计/15-Redis-Key与锁规约.md`
  - 补录：`lock:enroll:team:{userId}:{contestId}`
- `test/admin-tests.http`
  - 新增 `RTP23-BE-*` 回归块（reviewer 自动过滤）
- `test/wx-tests.http`
  - 新增 `RTP23-WX-*` 回归块（团队报名 participant 关联校验）

## C. mvn compile 结果
执行目录：`D:\coding\jst_v1\RuoYi-Vue`

执行命令：
```bash
mvn compile -DskipTests
```

结果：
- `BUILD SUCCESS`
- Reactor 18 模块全部 SUCCESS

## D. .http 测试结果
执行命令：
```bash
python test/run_http.py test/admin-tests.http 'RTP23-BE-'
python test/run_http.py test/wx-tests.http 'RTP23-WX-'
```

结果：
- RTP23-BE-0：200 / code=200（登录成功）
- RTP23-BE-1：200 / code=200（自动过滤生效）
- RTP23-BE-2：200 / code=200（查询非负责赛事返回空）
- RTP23-WX-0：200 / code=200（登录成功）
- RTP23-WX-1：200 / code=20082（当前赛事不支持团队报名）
- RTP23-WX-2：200 / code=500（因上一步未产出 enrollId，模板变量未展开）
- RTP23-WX-3：200 / code=500（同上）

## E. 遗漏问题 / 阻塞说明（必须说明）
1. **团队报名 happy path 在当前测试库不可达**
   - 当前测试库在线赛事 `team_min_size` 全为 `0`，团队报名接口会返回 `20082`（赛事不支持团队报名）。
2. **根因（代码侧）**
   - `jst-event` 的 `JstContestMapper.xml` 未映射 `team_min_size` / `team_max_size` 字段，`requireContest()` 读取到的 `JstContest.teamMinSize` 仍为 `null/0`。
   - 因此即便数据库临时改值，也无法在本实现中读到有效 team 配置。
3. **影响**
   - 本次只能完成：代码实现 + 编译通过 + reviewer 过滤实测通过。
   - 无法在现有 fixture 上闭环验证“团队报名写入 participantId”的 happy path。
4. **建议下一步（由主 Agent 决策）**
   - 补一张支持团队报名的可用赛事 fixture，并且在 `JstContestMapper` 补齐 `team_min_size/team_max_size` 映射后再跑 RTP23-WX 全链路验证。

## F. 任务卡外改动说明
- 未处理 §三 MiniProgram Agent 范围内容。
- 未新增依赖、未改动 DDL、未扩展任务卡之外的业务接口。

## G. 安全与规范自检
- [x] Service 层执行业务，未在 Controller 写业务
- [x] 写操作保持事务边界
- [x] 使用统一锁模板 `jstLockTemplate.execute`
- [x] Redis Key 已在文档登记
- [x] Mapper.xml namespace 与接口匹配
- [x] 未引入任务外依赖