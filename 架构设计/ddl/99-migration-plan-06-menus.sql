SET NAMES utf8mb4;

-- 销售工作台首页（默认页，销售/主管登录后的 landing page）
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (978500, '工作台首页', 9785, 0, 'index', 'sales/me/dashboard', 1, 0, 'C', '0', '0', 'sales:me:dashboard:view', 'dashboard', 'admin', NOW());

-- 销售主管团队看板
INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (978507, '团队看板', 9785, 7, 'manager-dashboard', 'sales/manager/dashboard', 1, 0, 'C', '0', '0', 'sales:manager:dashboard:view', 'chart', 'admin', NOW());

-- 角色绑定（501=jst_sales, 502=jst_sales_manager, 1=admin）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(501, 978500),
(502, 978500),
(1,   978500),
(502, 978507),
(1,   978507);

-- 验证
SELECT menu_id, menu_name, component FROM sys_menu WHERE menu_id IN (978500, 978507);
