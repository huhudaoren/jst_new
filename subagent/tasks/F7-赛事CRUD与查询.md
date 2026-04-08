# 任务卡 F7 - 赛事 CRUD + 双状态机 + 小程序查询接口

## ⚠️ 第二波豁免 (主 Agent 临时规则)
本任务卡**豁免**「BACKEND_AGENT_PROMPT.md §.http 测试规约」中的本地启动 + 跑测试要求。
原因:第二波 4 个 Agent 同时跑测试会抢 8080 端口,改为用户在阶段汇总时统一跑。
你需要做的:
- ✅ 仍然必须 mvn compile 18 模块 SUCCESS
- ✅ 仍然必须**追加** ### 测试块到 test/api-tests.http (语法正确,变量提取正确)
- ❌ **不要**尝试 kill 8080 / 启动 ruoyi-admin / 跑 .http 测试
- ✅ 报告 D 段写"测试块已追加,等待用户阶段汇总时统一跑"
- ✅ 报告中如有任何"我担心 X 接口可能跑不通"必须明确写出来

⚠️ **常见陷阱避坑**(从第一波 4 个 Agent 的踩坑总结,必读):
1. Mapper.xml 文件名**必须**含 "Mapper" 字样: `XxxMapper.xml` 或 `XxxMapperExt.xml`,**禁止** `XxxExt.xml`
2. 公开/小程序接口必须加 `@Anonymous` 或 `@PreAuthorize("hasRole(...)")` 注解,否则被若依默认拦截 401
3. Mapper interface 与 xml namespace 必须**完全一致** (全限定类名)
4. ⚠️ **F7/F-COURSE/F8 都在 jst-event 模块**,3 个 Agent 共享 BizErrorCode/99-fixtures/api-tests.http,请在文件末尾**追加**而非覆盖
5. F7 的 form_template_id 软依赖 F8,如 F8 还没合并,请实现"如果 form_template_id 为 null 跳过校验"

## 阶段 / 模块
阶段 B / jst-event

## 业务背景
赛事是平台核心业务对象。本任务实现赛事的:
1. 赛事方/平台**创建+审核**(双状态机:审核 SM-5a + 业务 SM-5b)
2. 平台**审核**(通过/驳回/上下线)
3. 小程序**查询**(列表/详情/分类/搜索)
4. 数据权限:赛事方只能看自己的赛事 (`@PartnerScope`)

## 必读上下文
1. `架构设计/13-技术栈与依赖清单.md`
2. `架构设计/15-Redis-Key与锁规约.md`
3. `架构设计/16-安全与敏感字段.md`
4. `架构设计/11-状态机定义.md` § SM-5 (双状态机)
5. `架构设计/27-用户端API契约.md` § 3.2
6. `架构设计/12-API边界与服务划分.md` § 3
7. `架构设计/29-赛事方Web工作台架构.md` § 2.4 (赛事方可见功能)
8. `架构设计/ddl/02-jst-event.sql` 表 `jst_contest`
9. `RuoYi-Vue/jst-user/.../service/impl/ParticipantClaimServiceImpl.java` ⭐ 样板

## 涉及表
- jst_contest (主)
- jst_event_partner (查赛事方名称)
- jst_enroll_form_template (查表单模板,只校验存在性)

## 涉及状态机
**SM-5a** `jst_contest.audit_status`:
```
draft → pending → (approved | rejected) → online → offline
```

**SM-5b** `jst_contest.status`:
```
not_started → enrolling → closed → scoring → published → ended
```

两个状态机**正交**:audit 控制是否对外可见,status 控制业务阶段。

## 涉及锁
- `lock:contest:audit:{contestId}` (wait 3s, lease 5s) - 审核
- `lock:contest:status:{contestId}` (wait 3s, lease 5s) - 业务状态推进

## 涉及权限
- 赛事方端: `jst:event:contest:add`, `:edit`, `:submit`, `:list` (+ `@PartnerScope`)
- 平台审核: `jst:event:contest:audit`, `:online`, `:offline`
- 小程序: `@Anonymous` (列表/详情公开)

## 接口契约

### A. 赛事方/平台 admin 接口

#### 1. POST /jst/event/contest/add
**用途**: 赛事方创建赛事(默认 draft)
**入参**: ContestSaveReqDTO (含 contest_name/category/group_levels/cover_image/description/enroll_start_time/enroll_end_time/event_start_time/event_end_time/price/support_*/cert_rule_json/score_rule_json/form_template_id/aftersale_days)
**出参**: `AjaxResult<{contestId}>`
**权限**: `jst:event:contest:add` + `@PartnerScope` 自动注入 partnerId
**业务**:
1. 校验 enroll_end_time > enroll_start_time, event_end_time > event_start_time, price >= 0
2. 校验 form_template_id 存在(若提供)
3. INSERT, source_type=partner (从 JstLoginContext.partnerId 取), audit_status=draft, status=not_started
4. 写 audit_log

#### 2. PUT /jst/event/contest/edit
**入参**: ContestSaveReqDTO + contestId
**业务**:
- 校验 contest 归属 (PartnerScope 已注入,但 Service 层第一行 SecurityCheck.assert: contest.partnerId == JstLoginContext.partnerId)
- 校验 audit_status in (draft, rejected) 才允许编辑(其他状态禁止)
- UPDATE

#### 3. POST /jst/event/contest/{id}/submit
**用途**: 赛事方提交审核
**业务**: 状态机 SM-5a draft→pending,UPDATE

#### 4. POST /jst/event/contest/{id}/audit/approve  ⭐
**用途**: 平台审核通过
**入参**: AuditReqDTO {String auditRemark}
**权限**: `jst:event:contest:audit` (不带 PartnerScope,平台运营)
**业务**: lock + 状态机 SM-5a pending→approved,UPDATE

#### 5. POST /jst/event/contest/{id}/audit/reject
**入参**: AuditReqDTO {@NotBlank String auditRemark}
**业务**: 状态机 pending→rejected

#### 6. POST /jst/event/contest/{id}/online
**业务**: 状态机 SM-5a approved→online,同时 SM-5b not_started→enrolling (如果当前在报名期内)
**业务规则**: 上线后小程序可见

#### 7. POST /jst/event/contest/{id}/offline
**业务**: 状态机 online→offline

#### 8. GET /jst/event/contest/list (admin)
**用途**: 赛事方/平台查询列表
**入参**: ContestQueryReqDTO {String contestName, String category, String auditStatus, String status, Long partnerId, ...}
**出参**: TableDataInfo<ContestListVO>
**权限**: `jst:event:contest:list` + `@PartnerScope` (赛事方自动加 partnerId 过滤)

#### 9. GET /jst/event/contest/{id} (admin)
**用途**: 详情(含完整字段)
**业务**: SecurityCheck 归属

### B. 小程序公开接口

#### 10. GET /jst/wx/contest/list ⭐
**用途**: 小程序赛事列表
**入参**: WxContestQueryDTO {String category, String groupLevel, String keyword, Integer pageNum, Integer pageSize}
**出参**: TableDataInfo<WxContestCardVO>
```java
class WxContestCardVO {
    Long contestId;
    String contestName;
    String coverImage;
    String category;
    BigDecimal price;
    Date enrollStartTime;
    Date enrollEndTime;
    String status;        // 业务状态(枚举名)
    Boolean enrollOpen;   // 当前是否在报名期 (前端方便判断)
    String partnerName;   // 赛事方名称(冗余)
}
```
**权限**: `@Anonymous`
**业务**:
- 仅返回 audit_status='online' 的赛事
- 默认按 enroll_start_time DESC
- 不返回敏感字段(form_template_id 等)

#### 11. GET /jst/wx/contest/{id} ⭐
**用途**: 小程序赛事详情
**出参**: WxContestDetailVO (含 description / cert_rule_json / 等)
**权限**: `@Anonymous`
**业务**: 仅 online 可访问,offline/草稿返回 404

#### 12. GET /jst/wx/contest/hot
**用途**: 首页热门赛事(取最近 N 条 online)
**入参**: `@RequestParam(defaultValue="6") Integer limit`
**出参**: AjaxResult<List<WxContestCardVO>>
**权限**: `@Anonymous`

#### 13. GET /jst/wx/contest/categories
**用途**: 赛事分类字典(便于小程序顶部 tab)
**出参**: AjaxResult<List<{code, name, count}>> (从 sys_dict_data jst_contest_category 取 + 计数)
**权限**: `@Anonymous`

## 交付物清单

新增文件 (jst-event):
- `enums/ContestAuditStatus.java`
- `enums/ContestBizStatus.java`
- `dto/ContestSaveReqDTO.java`
- `dto/ContestQueryReqDTO.java`
- `dto/WxContestQueryDTO.java`
- `dto/AuditReqDTO.java`
- `vo/ContestListVO.java`
- `vo/ContestDetailVO.java` (admin)
- `vo/WxContestCardVO.java` (小程序)
- `vo/WxContestDetailVO.java` (小程序)
- `vo/CategoryStatVO.java`
- `mapper/ContestMapperExt.java` + xml (扩展查询: 按 category 计数, 热门, 在线列表)
- `service/ContestService.java`
- `service/impl/ContestServiceImpl.java` ⭐
- `controller/ContestController.java` (admin)
- `controller/wx/WxContestController.java` ⭐

修改文件:
- `jst-common/.../BizErrorCode.java`: 追加 `JST_EVENT_CONTEST_NOT_FOUND(20001)`,`JST_EVENT_CONTEST_NOT_ENROLLING(20002)`,`JST_EVENT_CONTEST_NOT_ONLINE(20003)`,`JST_EVENT_CONTEST_ILLEGAL_TRANSIT(20004)`
- `99-test-fixtures.sql`: 追加 5 张测试赛事 (覆盖 各 audit_status / category)
- `test/api-tests.http`: 追加 ### F7 测试块

## 测试场景

### F7-1 (admin) 创建草稿
### F7-2 (admin) 提交审核
### F7-3 (admin) 审核通过+上线
### F7-4 (wx) 查列表 应能看到 online 的
### F7-5 (wx) 查详情
### F7-6 (wx) 查热门 (限 6 条)
### F7-7 (wx) 查分类 (返回 8 个分类 + 计数)
### F7-8 越权: 赛事方 A 修改赛事方 B 的赛事 应 403

## DoD

- [ ] mvn compile 18 模块 SUCCESS
- [ ] api-tests.http F7-* 全部 ✓
- [ ] 双状态机正交校验通过
- [ ] PartnerScope 切面在 admin 列表/详情/编辑接口生效
- [ ] 小程序接口零敏感字段泄漏

## 不许做的事

- ❌ 不许动 ddl
- ❌ 不许实现报名/订单逻辑(F9 任务做)
- ❌ 不许"顺手"实现 form_template (F8 任务做)
- ❌ 不许跳过双状态机(只做一个)
- ❌ 不许跨域 import jst-user 的内部类(SysUser 等用 LoginContext 取)

## 优先级
高 (前端首页/赛事 tab 阻塞依赖)

## 预计工作量
8-12 小时 (双状态机 + 多接口)
