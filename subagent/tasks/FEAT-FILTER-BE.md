# 任务卡 FEAT-FILTER-BE - 多级筛选后端

## 阶段
阶段 F-BUG / **jst-event**（Backend Agent）

## 背景
赛事和课程列表需要多级筛选（20+ 分类），后端需要提供分类树接口 + 列表接口支持多条件查询。

## 交付物

### Part A — 分类数据方案

使用若依字典 `sys_dict_data` 存储分类（最小改动），分两个 dict_type：
- `jst_contest_category` — 赛事分类（如：艺术/体育/科技/文学/数学/英语...）
- `jst_course_category` — 课程分类（如：线上/线下/AI课程/绘画/音乐/编程...）

用 `dict_sort` 控制显示顺序。如果需要二级分类，用 `remark` 字段存 parentValue。

**新建 DDL**：`架构设计/ddl/99-migration-category-dict.sql`

```sql
-- 赛事分类字典
INSERT IGNORE INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time)
VALUES (200, '赛事分类', 'jst_contest_category', '0', 'migration', NOW());

INSERT IGNORE INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time)
VALUES
(1, '艺术', 'art', 'jst_contest_category', '0', 'migration', NOW()),
(2, '体育', 'sports', 'jst_contest_category', '0', 'migration', NOW()),
(3, '科技', 'tech', 'jst_contest_category', '0', 'migration', NOW()),
(4, '文学', 'literature', 'jst_contest_category', '0', 'migration', NOW()),
(5, '数学', 'math', 'jst_contest_category', '0', 'migration', NOW()),
(6, '英语', 'english', 'jst_contest_category', '0', 'migration', NOW()),
(7, '音乐', 'music', 'jst_contest_category', '0', 'migration', NOW()),
(8, '舞蹈', 'dance', 'jst_contest_category', '0', 'migration', NOW()),
(9, '书法', 'calligraphy', 'jst_contest_category', '0', 'migration', NOW()),
(10, '编程', 'programming', 'jst_contest_category', '0', 'migration', NOW()),
(11, '机器人', 'robotics', 'jst_contest_category', '0', 'migration', NOW()),
(12, '演讲', 'speech', 'jst_contest_category', '0', 'migration', NOW()),
(13, '摄影', 'photography', 'jst_contest_category', '0', 'migration', NOW()),
(14, '棋类', 'chess', 'jst_contest_category', '0', 'migration', NOW()),
(15, '综合', 'general', 'jst_contest_category', '0', 'migration', NOW());

-- 课程分类字典（类似，Agent 自行补充）
```

### Part B — 分类查询接口

```
GET /jst/wx/dict/contest-categories
权限：公开
返回：[{label: "艺术", value: "art"}, ...]
实现：复用若依 /system/dict/data/type/{dictType}，或新建 wrapper 简化返回格式

GET /jst/wx/dict/course-categories
同上
```

### Part C — 赛事列表接口增强

**修改** 现有赛事列表接口 `GET /jst/wx/contest/list`，`ContestQueryReqDTO` 补参数：

| 参数 | 类型 | 说明 |
|------|------|------|
| `category` | String | 分类筛选（如 "art"） |
| `sortBy` | String | 排序：`hot`（报名人数）/ `newest`（上架时间）/ `deadline`（截止临近）/ 默认综合 |
| `eventType` | String | 赛事形式：`online` / `offline` / `mixed` |
| `tag` | String | 标签筛选（匹配 recommend_tags LIKE） |

**修改** `ContestMapperExt.xml`：`selectWxContestList` 补 WHERE 条件 + ORDER BY 动态排序

### Part D — 课程列表接口增强

**修改** 课程列表接口，补 `category` + `sortBy` 参数，逻辑类似赛事。

### Part E — 编译验证 + .http 测试

追加 FILTER-1~4 测试段。

## DoD
- [ ] 分类字典数据（15+ 赛事分类）
- [ ] 2 个分类查询接口
- [ ] 赛事列表支持 category/sortBy/eventType/tag
- [ ] 课程列表支持 category/sortBy
- [ ] mvn compile SUCCESS
- [ ] 任务报告 `FEAT-FILTER-BE-回答.md`

## 优先级：P1
---
派发时间：2026-04-11
