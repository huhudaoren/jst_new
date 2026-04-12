# 任务卡 FEAT-COURSE-DETAIL-BE - 课程详情后端补全

## 阶段
阶段 F-BUG / **jst-event**（Backend Agent）

## 背景
课程详情页缺节数、学习人数、总时长、课程目录、讲师信息。后端 VO 和表结构均需补。

## 必读
1. `CLAUDE.md`
2. `小程序原型图/course-detail.html` — 原型结构
3. 当前 Course 相关 VO/Mapper/Service

## 交付物

### Part A — DDL

**新建**：`架构设计/ddl/99-migration-course-detail-fields.sql`

```sql
ALTER TABLE jst_course ADD COLUMN lesson_count INT DEFAULT 0 COMMENT '课时数';
ALTER TABLE jst_course ADD COLUMN learner_count INT DEFAULT 0 COMMENT '学习人数';
ALTER TABLE jst_course ADD COLUMN total_duration VARCHAR(50) DEFAULT NULL COMMENT '总时长如12小时30分';
ALTER TABLE jst_course ADD COLUMN chapters_json TEXT COMMENT '课程目录 [{chapterName,lessons:[{lessonName,duration,isFree}]}]';
ALTER TABLE jst_course ADD COLUMN teacher_name VARCHAR(100) DEFAULT NULL COMMENT '讲师姓名';
ALTER TABLE jst_course ADD COLUMN teacher_avatar VARCHAR(255) DEFAULT NULL COMMENT '讲师头像';
ALTER TABLE jst_course ADD COLUMN teacher_desc VARCHAR(500) DEFAULT NULL COMMENT '讲师简介';
```

### Part B — VO + DTO + Mapper

**修改** `CourseDetailVO.java`：补 7 字段
**修改** `CourseListVO.java`：补 `lessonCount`、`learnerCount`（列表页也需要）
**修改** `CourseSaveReqDTO.java`：补 7 字段
**修改** `CourseMapperExt.xml`：SELECT / INSERT / UPDATE 补这些列

### Part C — fixture

```sql
UPDATE jst_course SET
  lesson_count = 24, learner_count = 1580, total_duration = '36小时',
  chapters_json = '[{"chapterName":"第一章 基础入门","lessons":[{"lessonName":"1.1 认识画笔","duration":"45分钟","isFree":true},{"lessonName":"1.2 色彩基础","duration":"50分钟","isFree":false}]},{"chapterName":"第二章 技法提升","lessons":[{"lessonName":"2.1 构图技巧","duration":"55分钟","isFree":false},{"lessonName":"2.2 光影表现","duration":"60分钟","isFree":false}]}]',
  teacher_name = '张艺老师', teacher_avatar = 'https://fixture.example.com/teacher-avatar.jpg',
  teacher_desc = '中央美术学院硕士，10年青少年美术教育经验，全国美术教育优秀指导教师'
WHERE course_id IN (7201, 7202, 7203);
```

### Part D — 编译验证

`mvn compile -DskipTests` SUCCESS

## DoD
- [ ] DDL 7 列
- [ ] VO/DTO/Mapper 补齐
- [ ] fixture 数据
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `FEAT-COURSE-DETAIL-BE-回答.md`

## 不许做
- ❌ 不许改前端
- ❌ 不许改其他模块

## 优先级：P0
---
派发时间：2026-04-11
