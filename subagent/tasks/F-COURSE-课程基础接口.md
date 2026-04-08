# 任务卡 F-COURSE - 课程基础列表 + 详情 (不含 VOD)

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
2. 公开接口(@Anonymous)适用于无需登录的接口
3. Mapper interface 与 xml namespace 必须**完全一致** (全限定类名)
4. ⚠️ **F-COURSE 与 F7/F8 都在 jst-event 模块**,3 个 Agent 共享 BizErrorCode/99-fixtures/api-tests.http,请在文件末尾**追加**而非覆盖

## 阶段 / 模块
阶段 B / jst-event

## 业务背景
小程序课程 tab 需要的最小接口集。
**本任务范围**: 课程 CRUD + 列表/详情查询 + 简单 my 课程接口
**不在本任务范围**: VOD 视频上传/播放凭证/学习上报 (那是 F12 任务,本期可推迟)

## 必读上下文
1. `13` `15` `16`
2. `27-用户端API契约.md` § 3.3
3. `ddl/02-jst-event.sql` 表 `jst_course` 和 `jst_course_learn_record`
4. gen 出来的 `RuoYi-Vue/jst-event/.../JstCourseController.java` (基础 CRUD 已生成)
5. `ParticipantClaimServiceImpl.java` 样板

## 涉及表
- jst_course
- jst_course_learn_record (本任务仅查询,不写)

## 涉及状态机
**SM-21** `jst_course.audit_status`:
```
draft → pending → (approved | rejected)
```

## 涉及权限
- admin: `jst:event:course:add`,`:edit`,`:audit`,`:on`,`:off`
- wx: `@Anonymous` (课程列表/详情公开浏览)

## 接口契约

### 1. (admin) 5 个基础 CRUD 接口
对照 F7 赛事 CRUD 的样式实现:
- POST /jst/event/course/add (默认 draft)
- PUT /jst/event/course/edit (仅 draft/rejected 可改)
- POST /jst/event/course/{id}/submit (draft→pending)
- POST /jst/event/course/{id}/audit/approve
- POST /jst/event/course/{id}/audit/reject

### 2. POST /jst/event/course/{id}/on
**业务**: status='on' (上架,需 audit_status=approved 才能上)

### 3. POST /jst/event/course/{id}/off
**业务**: status='off'

### 4. (admin) GET /jst/event/course/list
**入参**: CourseQueryReqDTO {String courseName, String courseType, String auditStatus, String status}

### 5. (wx) GET /jst/wx/course/list ⭐
**入参**: WxCourseQueryDTO {String courseType, Integer pageNum, Integer pageSize}
**出参**: TableDataInfo<WxCourseCardVO>
```java
{
  Long courseId;
  String courseName;
  String courseType;       // normal/ai_maic
  String coverImage;
  String description;      // 截取
  BigDecimal price;
  Long pointsPrice;
}
```
**业务**: 仅 audit_status=approved AND status=on
**权限**: `@Anonymous`

### 6. (wx) GET /jst/wx/course/{id}
**用途**: 课程详情
**出参**: WxCourseDetailVO (含完整 description)
**业务**: 仅 approved+on 可访问
**权限**: `@Anonymous`

### 7. (wx) GET /jst/wx/course/my
**用途**: 我学习过的课程列表
**业务**: SELECT FROM jst_course_learn_record WHERE user_id=current JOIN jst_course
**权限**: `hasRole('jst_student')`

### 注意:不实现以下接口 (F12 任务)
- POST /jst/wx/course/learn-record (学习进度上报)
- GET /jst/wx/course/play-auth (VOD 播放凭证)

## 交付物清单

新增文件:
- `jst-event/.../enums/CourseAuditStatus.java`
- `jst-event/.../dto/CourseSaveReqDTO.java`
- `jst-event/.../dto/CourseQueryReqDTO.java`
- `jst-event/.../dto/WxCourseQueryDTO.java`
- `jst-event/.../vo/CourseListVO.java`
- `jst-event/.../vo/CourseDetailVO.java`
- `jst-event/.../vo/WxCourseCardVO.java`
- `jst-event/.../vo/WxCourseDetailVO.java`
- `jst-event/.../vo/MyCourseVO.java`
- `jst-event/.../mapper/CourseMapperExt.java` + xml
- `jst-event/.../service/CourseService.java`
- `jst-event/.../service/impl/CourseServiceImpl.java`
- `jst-event/.../controller/CourseController.java` (改造 gen 出的 JstCourseController 或新建)
- `jst-event/.../controller/wx/WxCourseController.java`

修改文件:
- `BizErrorCode.java`: 追加 `JST_EVENT_COURSE_NOT_FOUND(20030)`,`JST_EVENT_COURSE_NOT_ON(20031)`
- `99-test-fixtures.sql`: 追加 3 个测试课程 (1 个 normal, 1 个 ai_maic, 1 个 draft 用于审核测试)
- `test/api-tests.http`: 追加 ### F-COURSE 测试块

## 测试场景

### C-1 (admin) 创建 → 提审 → 通过 → 上架
### C-2 (wx) 列表 应能看到 fixture 中已上架的 2 个
### C-3 (wx) 详情
### C-4 (wx) my (用 1001 token, fixture 中应有 1 条学习记录)
### C-5 上下架联动: 未审核通过不许 on

## DoD
- [ ] mvn SUCCESS
- [ ] api-tests.http C-* 全部 ✓
- [ ] 双状态机 (audit + on/off)

## 不许做的事
- ❌ 不许实现 VOD 接口 (F12)
- ❌ 不许实现 maic 创建接口 (后期 E2)
- ❌ 不许动 ddl

## 优先级
中高 (前端课程 tab 阻塞依赖)

## 预计工作量
4-6 小时
