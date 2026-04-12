# 任务卡 FEAT-CONTEST-DETAIL-BE - 赛事详情后端补全

## 阶段
阶段 F-BUG / **jst-event**（Backend Agent）

## 背景
赛事详情页原型有 5 个 Tab（基本信息/赛事介绍/赛程/奖项/常见问题）+ 标签 + 推荐，当前后端 VO 缺字段、缺推荐接口。

## 必读
1. `CLAUDE.md`
2. `架构设计/13-技术栈与依赖清单.md`
3. `小程序原型图/contest-detail.html` — 原型结构参考
4. `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/vo/ContestDetailVO.java` — 当前 VO
5. `RuoYi-Vue/jst-event/src/main/resources/mapper/event/ContestMapperExt.xml` — 当前查询

## 交付物

### Part A — DDL

**新建**：`架构设计/ddl/99-migration-contest-detail-fields.sql`

```sql
ALTER TABLE jst_contest ADD COLUMN schedule_json TEXT COMMENT '赛程JSON [{stageName,startTime,endTime,description}]';
ALTER TABLE jst_contest ADD COLUMN awards_json TEXT COMMENT '奖项JSON [{awardName,awardLevel,description,count}]';
ALTER TABLE jst_contest ADD COLUMN faq_json TEXT COMMENT '常见问题JSON [{question,answer}]';
ALTER TABLE jst_contest ADD COLUMN recommend_tags VARCHAR(500) DEFAULT NULL COMMENT '标签（逗号分隔）如"艺术,全学段,国际赛事"';
```

### Part B — VO + DTO + Mapper

**修改** `ContestDetailVO.java`：补 `scheduleJson`(String)、`awardsJson`(String)、`faqJson`(String)、`recommendTags`(String)

**修改** `ContestSaveReqDTO.java`：补同名 4 字段，允许管理端/赛事方编辑

**修改** `ContestMapperExt.xml`：`selectContestDetail` 的 SELECT 补查这 4 列；`insertContest` / `updateContest` 补写这 4 列

**修改** `ContestListVO.java`：补 `recommendTags`（列表页也要展示标签）

### Part C — 推荐接口

**新增方法**（在现有 `WxContestController.java` 或新建 `WxContestRecommendController.java`）：

```
GET /jst/wx/contest/{id}/recommend
权限：公开或 @ss.hasRole('jst_student')
返回：{ relatedContests: [...], relatedCourses: [...] }
```

- `relatedContests`：同 category 的其他在线赛事，排除自身，最多 6 条。字段复用 `ContestListVO`
- `relatedCourses`：如有 tag 交集则匹配，否则返回热门课程，最多 4 条。字段：`courseId/courseName/coverImage/price/learnerCount`

需要在 `ContestMapperExt` 或新 Mapper 中补对应 SQL。

### Part D — fixture

在 `99-test-fixtures.sql` 中给赛事 8201/8202 补：
```sql
UPDATE jst_contest SET 
  schedule_json = '[{"stageName":"初赛","startTime":"2026-05-01","endTime":"2026-05-15","description":"线上提交作品"},{"stageName":"复赛","startTime":"2026-06-01","endTime":"2026-06-10","description":"现场展演"},{"stageName":"决赛","startTime":"2026-07-01","endTime":"2026-07-05","description":"全国总决赛"}]',
  awards_json = '[{"awardName":"金奖","awardLevel":"一等奖","description":"奖杯+证书+奖金","count":10},{"awardName":"银奖","awardLevel":"二等奖","description":"证书+奖金","count":20},{"awardName":"铜奖","awardLevel":"三等奖","description":"证书","count":50}]',
  faq_json = '[{"question":"报名后可以退款吗？","answer":"报名后7天内可申请全额退款"},{"question":"比赛形式是什么？","answer":"初赛线上提交，复赛及决赛线下参加"},{"question":"有年龄限制吗？","answer":"面向6-18岁青少年"}]',
  recommend_tags = '艺术,全学段,线上+线下,国际赛事,报名中'
WHERE contest_id IN (8201, 8202);
```

### Part E — 编译验证

`mvn compile -DskipTests` 18 模块 SUCCESS

## DoD
- [ ] DDL 4 列
- [ ] ContestDetailVO + ContestListVO 补字段
- [ ] ContestSaveReqDTO 补字段
- [ ] Mapper XML 补查+补写
- [ ] 推荐接口 1 个（返回 relatedContests + relatedCourses）
- [ ] fixture 数据
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `FEAT-CONTEST-DETAIL-BE-回答.md`

## 不许做
- ❌ 不许改前端
- ❌ 不许改报名/支付逻辑
- ❌ 不许改其他模块

## 优先级：P0
---
派发时间：2026-04-11
