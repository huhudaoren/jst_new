# REFACTOR-1-DDL — 数据库去 JSON 化

> 优先级：P0 | 预估：L | Agent：Backend Agent

---

## 一、背景

系统中大量业务配置以 JSON 字段存储（schedule_json、awards_json、score_rule_json 等），前端用户无法直观编辑。需要将用户可配置的 JSON 字段迁移为结构化的数据库子表，为前端傻瓜式操作打基础。

## 二、测试库连接信息

```
url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
username: jst_new
password: J8zZpAa4zG8y6a7e
```

## 三、任务清单

### 3.1 新增 7 张子表（DDL）

产出文件：`架构设计/ddl/99-migration-refactor-dejson.sql`

在测试库执行以下建表语句：

```sql
-- 1. 赛程阶段（替代 jst_contest.schedule_json）
CREATE TABLE IF NOT EXISTS jst_contest_schedule (
  schedule_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  stage_name     VARCHAR(100) NOT NULL COMMENT '阶段名称',
  start_time     DATETIME COMMENT '开始时间',
  end_time       DATETIME COMMENT '结束时间',
  venue          VARCHAR(200) COMMENT '场地',
  description    VARCHAR(500) COMMENT '说明',
  sort_order     INT DEFAULT 0 COMMENT '排序',
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0' COMMENT '0正常 2删除',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事赛程阶段';

-- 2. 奖项设置（替代 jst_contest.awards_json）
CREATE TABLE IF NOT EXISTS jst_contest_award (
  award_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  award_name     VARCHAR(100) NOT NULL COMMENT '奖项名称',
  award_level    VARCHAR(50) COMMENT '等级编码（gold/silver/bronze/first/second/third）',
  description    VARCHAR(500) COMMENT '奖项说明',
  quota          INT DEFAULT 0 COMMENT '名额数',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事奖项设置';

-- 3. 常见问题（替代 jst_contest.faq_json）
CREATE TABLE IF NOT EXISTS jst_contest_faq (
  faq_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  question       VARCHAR(500) NOT NULL COMMENT '问题',
  answer         TEXT NOT NULL COMMENT '回答',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事常见问题';

-- 4. 成绩项定义（替代 jst_contest.score_rule_json 中的 scoreItems）
CREATE TABLE IF NOT EXISTS jst_score_item (
  item_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  item_name      VARCHAR(100) NOT NULL COMMENT '成绩项名称（总分/技巧/表现力）',
  max_score      DECIMAL(8,2) DEFAULT 100.00 COMMENT '满分值',
  weight         DECIMAL(5,2) DEFAULT 1.00 COMMENT '权重（占总分比例）',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest (contest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事成绩项定义';

-- 5. 预约时间段（新增，当前无对应 JSON）
CREATE TABLE IF NOT EXISTS jst_appointment_slot (
  slot_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  contest_id     BIGINT NOT NULL COMMENT 'FK jst_contest',
  slot_date      DATE NOT NULL COMMENT '预约日期',
  start_time     TIME NOT NULL COMMENT '开始时间',
  end_time       TIME NOT NULL COMMENT '结束时间',
  venue          VARCHAR(200) COMMENT '场地名称',
  capacity       INT NOT NULL DEFAULT 0 COMMENT '该时段容量',
  booked_count   INT DEFAULT 0 COMMENT '已预约数',
  status         TINYINT DEFAULT 1 COMMENT '1启用 0停用',
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_contest_date (contest_id, slot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事预约时间段';

-- 6. 课程章节（替代 jst_course.chapters_json）
CREATE TABLE IF NOT EXISTS jst_course_chapter (
  chapter_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  course_id      BIGINT NOT NULL COMMENT 'FK jst_course',
  chapter_name   VARCHAR(200) NOT NULL COMMENT '章节名称',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节';

-- 7. 课程课时（chapters_json 中的 lessons）
CREATE TABLE IF NOT EXISTS jst_course_lesson (
  lesson_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  chapter_id     BIGINT NOT NULL COMMENT 'FK jst_course_chapter',
  lesson_name    VARCHAR(200) NOT NULL COMMENT '课时名称',
  duration       VARCHAR(50) COMMENT '时长描述',
  is_free        TINYINT DEFAULT 0 COMMENT '是否免费试看',
  video_url      VARCHAR(500) COMMENT '视频URL',
  sort_order     INT DEFAULT 0,
  create_by      VARCHAR(64) DEFAULT '',
  create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_by      VARCHAR(64) DEFAULT '',
  update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag       CHAR(1) DEFAULT '0',
  INDEX idx_chapter (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程课时';
```

### 3.2 jst_contest 表新增结构化字段

```sql
-- 新增 Banner、名额、团队、结构化核销等字段
ALTER TABLE jst_contest
  ADD COLUMN IF NOT EXISTS banner_image      VARCHAR(500) COMMENT 'Banner大图URL' AFTER cover_image,
  ADD COLUMN IF NOT EXISTS total_quota        INT DEFAULT 0 COMMENT '总报名名额(0=不限)',
  ADD COLUMN IF NOT EXISTS per_user_limit     INT DEFAULT 1 COMMENT '单人限购数',
  ADD COLUMN IF NOT EXISTS score_publish_mode VARCHAR(20) DEFAULT 'manual' COMMENT '成绩发布模式: manual/auto',
  ADD COLUMN IF NOT EXISTS cert_issue_mode    VARCHAR(20) DEFAULT 'manual' COMMENT '证书颁发模式: manual/auto',
  ADD COLUMN IF NOT EXISTS writeoff_mode      VARCHAR(20) DEFAULT 'qr' COMMENT '核销方式: qr/manual',
  ADD COLUMN IF NOT EXISTS need_sign_in       TINYINT DEFAULT 0 COMMENT '是否需要签到: 0否 1是',
  ADD COLUMN IF NOT EXISTS team_min_size      INT DEFAULT 0 COMMENT '团队最小人数(0=不支持团队)',
  ADD COLUMN IF NOT EXISTS team_max_size      INT DEFAULT 0 COMMENT '团队最大人数',
  ADD COLUMN IF NOT EXISTS team_leader_fields VARCHAR(500) COMMENT '队长需填字段(逗号分隔)',
  ADD COLUMN IF NOT EXISTS team_member_fields VARCHAR(500) COMMENT '队员需填字段(逗号分隔)',
  ADD COLUMN IF NOT EXISTS score_publish_time DATETIME COMMENT '成绩发布时间(auto模式用)',
  ADD COLUMN IF NOT EXISTS aftersale_deadline DATETIME COMMENT '售后截止时间',
  ADD COLUMN IF NOT EXISTS organizer          VARCHAR(200) COMMENT '主办方',
  ADD COLUMN IF NOT EXISTS co_organizer       VARCHAR(200) COMMENT '协办方',
  ADD COLUMN IF NOT EXISTS event_address      VARCHAR(500) COMMENT '比赛地址';
```

### 3.3 后端代码（jst-event 模块）

为每张新表生成标准若依 CRUD：

1. **Domain 类**：JstContestSchedule, JstContestAward, JstContestFaq, JstScoreItem, JstAppointmentSlot, JstCourseChapter, JstCourseLesson
2. **Mapper + XML**：标准 CRUD + `selectByContestId` / `deleteByContestId` 批量操作
3. **Service + Impl**：标准 CRUD
4. **Controller**：仅内部使用，不需要独立 Controller（通过赛事保存接口级联）

**关键改动**：
- `ContestServiceImpl` 的保存方法：赛事保存时级联保存 schedule/award/faq/scoreItem/appointmentSlot 子表（先 deleteByContestId 再 batchInsert）
- `ContestServiceImpl` 的详情方法：查赛事详情时同时查出所有子表数据并塞入 VO
- JstContest Domain 新增 15 个结构化字段的 getter/setter
- DTO 新增对应字段
- VO 新增子表列表字段：`List<JstContestSchedule> scheduleList`、`List<JstContestAward> awardList` 等

### 3.4 数据迁移（在测试库执行）

对现有 JSON 数据进行解析迁移。由于测试库数据量极小（fixture 数据），可用简单的 INSERT...SELECT + JSON_EXTRACT：

```sql
-- 迁移 score_rule_json 中的 scoreItems
-- 示例逻辑（实际需根据数据格式调整）
-- INSERT INTO jst_score_item (contest_id, item_name, sort_order)
-- SELECT contest_id, jt.item_name, jt.ord
-- FROM jst_contest, JSON_TABLE(score_rule_json, '$.scoreItems[*]' COLUMNS(...)) AS jt
-- WHERE score_rule_json IS NOT NULL;
```

如果 JSON 格式不适合 JSON_TABLE，可以写一个简单的 Java main 方法或 SQL 存储过程来迁移。

### 3.5 赛事复制接口

新增 `POST /jst/partner/contest/copy/{contestId}` 接口：
- 深拷贝赛事基本信息（清空 contestId，名称加"(副本)"）
- 深拷贝 schedule/award/faq/scoreItem 子表数据
- 不拷贝报名记录和订单数据
- 返回新赛事 ID

## 四、DoD

- [ ] 7 张新表在测试库建表成功
- [ ] jst_contest 新增 15 个字段成功
- [ ] 7 个 Domain/Mapper/Service 代码生成完成
- [ ] ContestServiceImpl 级联保存/查询改造完成
- [ ] DTO/VO 新增字段完成
- [ ] 赛事复制接口完成
- [ ] 数据迁移脚本执行（测试数据）
- [ ] `mvn compile -DskipTests` 通过
- [ ] 报告交付

## 五、注意事项

- **不删除旧 JSON 字段**（schedule_json 等），仅标记 @Deprecated，保证向后兼容
- 新表统一加 `create_by/create_time/update_by/update_time/del_flag` 基线字段
- 子表批量保存用 `先删后插` 模式（简单可靠）
- 赛事详情 VO 返回子表时统一按 `sort_order ASC` 排序
