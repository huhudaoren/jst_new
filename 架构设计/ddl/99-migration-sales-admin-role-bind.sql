-- =====================================================
-- 修复：admin (role_id=1) 未绑销售工作台 + admin 销售管理菜单
-- 关联 plan-01 Task 5 SQL 遗漏 + 用户反馈 "admin 无法访问销售工作台"
--
-- 补绑：
--   - 9785 段：销售工作台（6 C 菜单 + 11 F 按钮 + 1 顶级 M）→ admin 也有权看
--   - 976210-976299 段：admin 销售管理菜单（3 C + 11 F）→ admin 本就该看
--   - 顺带给 jst_operator (业务运营) 绑 admin 销售管理，让运营也能代看
-- =====================================================
SET NAMES utf8mb4;

-- admin (1) 绑销售工作台 9785 段全部 (19 行)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 9785),
(1, 978501), (1, 978502), (1, 978503), (1, 978504), (1, 978505), (1, 978506),
(1, 978511), (1, 978512), (1, 978521), (1, 978522), (1, 978523),
(1, 978531), (1, 978532), (1, 978533),
(1, 978541),
(1, 978551), (1, 978552);

-- admin (1) 绑 admin 销售管理 976210-976299 段全部 (14 行)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 976210), (1, 976211), (1, 976212),
(1, 976220), (1, 976221), (1, 976222), (1, 976223), (1, 976224), (1, 976225),
(1, 976230), (1, 976231),
(1, 976240), (1, 976241),
(1, 976250);

-- jst_operator (业务运营) 找出 role_id，绑 admin 销售管理（业务层面运营可代看代操作）
-- 运营不绑 9785 销售工作台（那是销售个人用的 SalesScope 视角）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
  FROM sys_role r
 CROSS JOIN (
   SELECT menu_id FROM sys_menu WHERE menu_id BETWEEN 976210 AND 976299
 ) m
 WHERE r.role_key = 'jst_operator';

-- 验证
SELECT 'admin 绑定销售工作台条数' AS label, COUNT(*) AS cnt FROM sys_role_menu
 WHERE role_id=1 AND menu_id BETWEEN 9785 AND 978999
UNION ALL
SELECT 'admin 绑定 admin 销售管理条数', COUNT(*) FROM sys_role_menu
 WHERE role_id=1 AND menu_id BETWEEN 976210 AND 976299
UNION ALL
SELECT 'jst_operator 绑定 admin 销售管理条数', COUNT(*)
  FROM sys_role_menu rm JOIN sys_role r ON rm.role_id=r.role_id
 WHERE r.role_key='jst_operator' AND rm.menu_id BETWEEN 976210 AND 976299;
