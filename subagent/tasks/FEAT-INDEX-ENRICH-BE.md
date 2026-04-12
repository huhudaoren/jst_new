# 任务卡 FEAT-INDEX-ENRICH-BE - 首页聚合接口

## 阶段
阶段 F-BUG / **jst-event + jst-common**（Backend Agent）

## 背景
首页内容太少，需要推荐赛事、推荐课程、热门标签、专题活动 4 个接口。

## 必读
1. `CLAUDE.md`
2. `小程序原型图/index.html` — 原型结构
3. 现有 `WxContestController`、`WxCourseController`、`WxNoticeController` — 确认是否可扩展

## 交付物

### Part A — 专题活动表（如果复用公告不合适则新建）

优先尝试复用公告表 `jst_notice`，`category='topic'`，附加字段用 `remark` 或新列：

```sql
-- 方案 1：复用公告（推荐，最小改动）
-- 在 jst_notice 中 category='topic' 的记录作为专题活动
-- cover_image 作为封面，content 作为描述，remark 存跳转链接

-- 方案 2：新表（如果公告语义不合适）
CREATE TABLE IF NOT EXISTS jst_activity_topic (
  topic_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  cover_image VARCHAR(255),
  description VARCHAR(500),
  link_url VARCHAR(255),
  sort_order INT DEFAULT 0,
  status VARCHAR(10) DEFAULT 'published',
  create_by VARCHAR(64), create_time DATETIME,
  update_by VARCHAR(64), update_time DATETIME,
  del_flag CHAR(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专题活动';
```

Agent 自行判断用哪个方案，在报告中说明选择理由。

### Part B — 4 个首页接口

在 `WxContestController` 或新建 `WxHomeController` 中实现：

```
GET /jst/wx/home/recommend-contests
权限：公开（@Anonymous 或不加注解）
返回：ContestListVO 列表，最多 8 条
逻辑：status='online' AND enroll_deadline > NOW()，按 enroll_count DESC

GET /jst/wx/home/recommend-courses
权限：公开
返回：CourseListVO 列表，最多 6 条
逻辑：status='published'，按 learner_count DESC

GET /jst/wx/home/hot-tags
权限：公开
返回：[{tag: "艺术", count: 15}, {tag: "全学段", count: 8}, ...]
逻辑：聚合 jst_contest.recommend_tags（拆分逗号后 GROUP BY 计数），最多 12 个
如果 recommend_tags 还没数据，也支持从 category 字段聚合

GET /jst/wx/home/topics
权限：公开
返回：专题活动列表，最多 3 条
逻辑：按方案 1 或 2 查询
```

### Part C — fixture

插入 2~3 条专题活动测试数据。
确认至少 2 条赛事有 `recommend_tags`（FEAT-CONTEST-DETAIL-BE 已补）。

### Part D — .http 测试

在 `test/wx-tests.http` 追加：
```
### HOME-1 推荐赛事
GET {{baseUrl}}/jst/wx/home/recommend-contests

### HOME-2 推荐课程
GET {{baseUrl}}/jst/wx/home/recommend-courses

### HOME-3 热门标签
GET {{baseUrl}}/jst/wx/home/hot-tags

### HOME-4 专题活动
GET {{baseUrl}}/jst/wx/home/topics
```

### Part E — 编译验证

`mvn compile -DskipTests` SUCCESS

## DoD
- [ ] 4 个首页接口全部可用
- [ ] 专题活动方案落地（复用公告或新表）
- [ ] fixture 数据
- [ ] .http 测试通过
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `FEAT-INDEX-ENRICH-BE-回答.md`

## 不许做
- ❌ 不许改前端
- ❌ 不许改已有接口的签名

## 优先级：P0
---
派发时间：2026-04-11
