-- =====================================================================
-- 文件名：99-migration-admin-menus.sql
-- 用途  : 管理后台菜单注册（渠道管理 + 数据看板）+ admin 角色绑定
-- 背景  : WEB-ADMIN-4 任务卡交付物，注册 5 个管理模块 + 1 个数据看板
-- 幂等  : INSERT IGNORE，可重复执行
-- 兼容  : MySQL 5.7+
-- menu_id 段: 9900-9999（避开 9700-9799 赛事方菜单段）
-- =====================================================================

SET NAMES utf8mb4;

-- =====================
-- 1. 一级目录
-- =====================

INSERT IGNORE INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 渠道管理
(9900, '渠道管理', 0, 20, 'channel-manage', NULL, NULL, 1, 0, 'M', '0', '0', '', 'peoples', 'migration', NOW(), '', NULL, '渠道管理一级目录'),
-- 数据看板
(9910, '数据看板', 0, 1, 'dashboard-admin', NULL, NULL, 1, 0, 'M', '0', '0', '', 'chart', 'migration', NOW(), '', NULL, '平台数据看板一级目录');

-- =====================
-- 2. 二级菜单
-- =====================

INSERT IGNORE INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 渠道管理子菜单
(9901, '渠道认证管理', 9900, 1, 'channel-auth',  'jst/channel-auth/index',  NULL, 1, 0, 'C', '0', '0', 'jst:organizer:channelApply:list', 'form',     'migration', NOW(), '', NULL, '渠道认证审核列表'),
(9902, '渠道列表',     9900, 2, 'channel-list',  'jst/channel/index',       NULL, 1, 0, 'C', '0', '0', 'jst:user:channel:list',          'list',     'migration', NOW(), '', NULL, '已认证渠道列表'),
(9903, '绑定管理',     9900, 3, 'binding',       'jst/binding/index',       NULL, 1, 0, 'C', '0', '0', 'jst:user:binding:list',          'link',     'migration', NOW(), '', NULL, '学生-渠道绑定管理'),
(9904, '预约管理',     9900, 4, 'appointment',   'jst/appointment/index',   NULL, 1, 0, 'C', '0', '0', 'jst:order:appointment_record:list', 'date', 'migration', NOW(), '', NULL, '预约记录管理'),
-- 数据看板子菜单
(9911, '运营数据',     9910, 1, 'overview',      'jst/dashboard/index',     NULL, 1, 0, 'C', '0', '0', 'jst:admin:dashboard',            'dashboard', 'migration', NOW(), '', NULL, '平台运营数据看板');

-- =====================
-- 3. 三级按钮权限
-- =====================

INSERT IGNORE INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 渠道认证管理按钮（parent 9901）
(9920, '认证列表',   9901, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:organizer:channelApply:list',    '#', 'migration', NOW(), '', NULL, ''),
(9921, '认证详情',   9901, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:organizer:channelApply:detail',  '#', 'migration', NOW(), '', NULL, ''),
(9922, '审核通过',   9901, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:organizer:channelApply:approve', '#', 'migration', NOW(), '', NULL, ''),
(9923, '审核驳回',   9901, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:organizer:channelApply:reject',  '#', 'migration', NOW(), '', NULL, ''),
(9924, '认证暂停',   9901, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:organizer:channelApply:suspend', '#', 'migration', NOW(), '', NULL, ''),

-- 渠道列表按钮（parent 9902）
(9930, '渠道查询',   9902, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:user:channel:list',    '#', 'migration', NOW(), '', NULL, ''),
(9931, '渠道详情',   9902, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:user:channel:query',   '#', 'migration', NOW(), '', NULL, ''),
(9932, '渠道编辑',   9902, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:user:channel:edit',    '#', 'migration', NOW(), '', NULL, ''),
(9933, '渠道导出',   9902, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:user:channel:export',  '#', 'migration', NOW(), '', NULL, ''),

-- 绑定管理按钮（parent 9903）
(9940, '绑定查询',   9903, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:user:binding:list',        '#', 'migration', NOW(), '', NULL, ''),
(9941, '强制解绑',   9903, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:user:binding:forceUnbind', '#', 'migration', NOW(), '', NULL, ''),

-- 预约管理按钮（parent 9904）
(9950, '预约查询',   9904, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:order:appointment_record:list',   '#', 'migration', NOW(), '', NULL, ''),
(9951, '预约详情',   9904, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:order:appointment_record:query',  '#', 'migration', NOW(), '', NULL, ''),
(9952, '预约取消',   9904, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:order:appointment_record:remove', '#', 'migration', NOW(), '', NULL, ''),
(9953, '预约导出',   9904, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:order:appointment_record:export', '#', 'migration', NOW(), '', NULL, '');

-- =====================
-- 4. 绑定 admin 角色到所有管理菜单
-- =====================

SET @admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @admin_role_id, menu_id
FROM sys_menu
WHERE menu_id BETWEEN 9900 AND 9999
  AND @admin_role_id IS NOT NULL;

-- =====================
-- 5. 验证
-- =====================

SELECT m.menu_id, m.menu_name, m.menu_type, m.perms
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE rm.role_id = @admin_role_id
  AND m.menu_id BETWEEN 9900 AND 9999
ORDER BY m.menu_id;

-- =====================================================================
-- 后续事项：
-- 1. 如需增加更多管理菜单，在 9960-9999 段分配 menu_id
-- 2. 本脚本仅注册渠道管理 + 看板，其他管理模块（赛事/订单/内容等）
--    已有代码生成器产出的菜单，不在本卡范围
-- =====================================================================
