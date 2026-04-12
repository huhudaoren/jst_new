-- =====================================================================
-- 99-migration-category-dict.sql
-- FEAT-FILTER-BE: 多级筛选分类字典
-- 说明：
--   1) 使用 sys_dict_type / sys_dict_data 维护赛事与课程分类
--   2) dict_sort 控制展示顺序
--   3) 二级分类通过 remark 存 parentValue
-- 幂等：可重复执行（按 dict_type + dict_value 去重）
-- =====================================================================

SET NAMES utf8mb4;

-- =========================
-- 1. 字典类型
-- =========================

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '赛事分类', 'jst_contest_category', '0', 'migration', NOW(), 'FEAT-FILTER-BE'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_dict_type
    WHERE dict_type = 'jst_contest_category'
);

INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '课程分类', 'jst_course_category', '0', 'migration', NOW(), 'FEAT-FILTER-BE'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_dict_type
    WHERE dict_type = 'jst_course_category'
);

-- =========================
-- 2. 赛事分类（15+）
-- =========================

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark)
SELECT t.dict_sort, t.dict_label, t.dict_value, t.dict_type, '0', 'migration', NOW(), t.remark
FROM (
         SELECT 1 AS dict_sort, '艺术' AS dict_label, 'art' AS dict_value, 'jst_contest_category' AS dict_type, NULL AS remark
         UNION ALL SELECT 2, '体育', 'sports', 'jst_contest_category', NULL
         UNION ALL SELECT 3, '科技', 'tech', 'jst_contest_category', NULL
         UNION ALL SELECT 4, '文学', 'literature', 'jst_contest_category', NULL
         UNION ALL SELECT 5, '数学', 'math', 'jst_contest_category', NULL
         UNION ALL SELECT 6, '英语', 'english', 'jst_contest_category', NULL
         UNION ALL SELECT 7, '音乐', 'music', 'jst_contest_category', NULL
         UNION ALL SELECT 8, '舞蹈', 'dance', 'jst_contest_category', NULL
         UNION ALL SELECT 9, '书法', 'calligraphy', 'jst_contest_category', NULL
         UNION ALL SELECT 10, '编程', 'programming', 'jst_contest_category', NULL
         UNION ALL SELECT 11, '机器人', 'robotics', 'jst_contest_category', NULL
         UNION ALL SELECT 12, '演讲', 'speech', 'jst_contest_category', NULL
         UNION ALL SELECT 13, '摄影', 'photography', 'jst_contest_category', NULL
         UNION ALL SELECT 14, '棋类', 'chess', 'jst_contest_category', NULL
         UNION ALL SELECT 15, '综合', 'general', 'jst_contest_category', NULL
     ) t
         LEFT JOIN sys_dict_data d
                   ON d.dict_type = t.dict_type
                       AND d.dict_value = t.dict_value
WHERE d.dict_code IS NULL;

-- =========================
-- 3. 课程分类（主类 + 二级）
-- remark = parentValue
-- =========================

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark)
SELECT t.dict_sort, t.dict_label, t.dict_value, t.dict_type, '0', 'migration', NOW(), t.remark
FROM (
         -- 一级分类
         SELECT 1 AS dict_sort, '线上课程' AS dict_label, 'online' AS dict_value, 'jst_course_category' AS dict_type, NULL AS remark
         UNION ALL SELECT 2, '线下课程', 'offline', 'jst_course_category', NULL
         UNION ALL SELECT 3, 'AI课程', 'ai_maic', 'jst_course_category', NULL
         UNION ALL SELECT 4, '绘画', 'painting', 'jst_course_category', NULL
         UNION ALL SELECT 5, '音乐', 'music', 'jst_course_category', NULL
         UNION ALL SELECT 6, '舞蹈', 'dance', 'jst_course_category', NULL
         UNION ALL SELECT 7, '书法', 'calligraphy', 'jst_course_category', NULL
         UNION ALL SELECT 8, '编程', 'programming', 'jst_course_category', NULL
         UNION ALL SELECT 9, '机器人', 'robotics', 'jst_course_category', NULL
         UNION ALL SELECT 10, '语言', 'language', 'jst_course_category', NULL
         UNION ALL SELECT 11, '数学', 'math', 'jst_course_category', NULL
         UNION ALL SELECT 12, '科学', 'science', 'jst_course_category', NULL
         UNION ALL SELECT 13, '综合素养', 'general', 'jst_course_category', NULL

         -- 二级分类（remark = parentValue）
         UNION ALL SELECT 101, '素描', 'sketch', 'jst_course_category', 'painting'
         UNION ALL SELECT 102, '水彩', 'watercolor', 'jst_course_category', 'painting'
         UNION ALL SELECT 103, '钢琴', 'piano', 'jst_course_category', 'music'
         UNION ALL SELECT 104, '声乐', 'vocal', 'jst_course_category', 'music'
         UNION ALL SELECT 105, 'Scratch', 'scratch', 'jst_course_category', 'programming'
         UNION ALL SELECT 106, 'Python', 'python', 'jst_course_category', 'programming'
     ) t
         LEFT JOIN sys_dict_data d
                   ON d.dict_type = t.dict_type
                       AND d.dict_value = t.dict_value
WHERE d.dict_code IS NULL;
