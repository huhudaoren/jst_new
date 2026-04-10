-- =====================================================================
-- 文件名：99-migration-partner-menus.sql
-- 用途  : 赛事方工作台菜单注册 + jst_partner 角色绑定
-- 背景  : E2-PA-1~PA-8 完成后，赛事方前端页面已开发，但 sys_menu 中
--         没有对应的菜单目录条目，侧边栏无法显示。本脚本注册完整菜单树
--         并绑定 jst_partner 角色，替代 permission.js 的客户端 hack。
-- 幂等  : INSERT IGNORE，可重复执行
-- 兼容  : MySQL 5.7+
-- menu_id 段: 9700-9799（避开 95-migration 的 9500-9611）
-- =====================================================================

SET NAMES utf8mb4;

-- =====================
-- 1. 赛事方一级目录
-- =====================

INSERT IGNORE INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 一级目录：赛事方工作台
(9700, '赛事方工作台', 0, 10, 'partner', NULL, NULL, 1, 0, 'M', '0', '0', '', 'peoples', 'migration', NOW(), '', NULL, '赛事方专属菜单目录');

-- =====================
-- 2. 二级菜单（8 个页面）
-- =====================

INSERT IGNORE INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 工作台首页
(9701, '工作台首页',  9700, 1, 'home',          'partner/home',          NULL, 1, 0, 'C', '0', '0', 'jst:partner:home',          'dashboard', 'migration', NOW(), '', NULL, '赛事方工作台首页'),
-- 赛事管理
(9702, '赛事管理',    9700, 2, 'contest-list',   'partner/contest-list',   NULL, 1, 0, 'C', '0', '0', 'jst:partner:contest:list',   'form',      'migration', NOW(), '', NULL, '赛事方赛事列表'),
-- 报名审核
(9703, '报名审核',    9700, 3, 'enroll-manage',  'partner/enroll-manage',  NULL, 1, 0, 'C', '0', '0', 'jst:partner:enroll:list',    'peoples',   'migration', NOW(), '', NULL, '赛事方报名审核'),
-- 成绩管理
(9704, '成绩管理',    9700, 4, 'score-manage',   'partner/score-manage',   NULL, 1, 0, 'C', '0', '0', 'jst:partner:score:list',     'education', 'migration', NOW(), '', NULL, '赛事方成绩管理'),
-- 证书管理
(9705, '证书管理',    9700, 5, 'cert-manage',    'partner/cert-manage',    NULL, 1, 0, 'C', '0', '0', 'jst:partner:cert:list',      'validCode', 'migration', NOW(), '', NULL, '赛事方证书管理'),
-- 结算中心
(9706, '结算中心',    9700, 6, 'settlement',     'partner/settlement',     NULL, 1, 0, 'C', '0', '0', 'jst:partner:settlement:list','money',     'migration', NOW(), '', NULL, '赛事方结算中心'),
-- 现场核销
(9707, '现场核销',    9700, 7, 'writeoff',       'partner/writeoff',       NULL, 1, 0, 'C', '0', '0', 'jst:partner:writeoff:scan',  'code',      'migration', NOW(), '', NULL, '赛事方现场核销'),
-- 账号设置（预留）
(9708, '账号设置',    9700, 8, 'settings',       'partner/settings',       NULL, 1, 0, 'C', '0', '0', 'jst:partner:settings',       'user',      'migration', NOW(), '', NULL, '赛事方账号设置');

-- =====================
-- 3. 三级按钮权限（赛事管理下的操作）
-- =====================

INSERT IGNORE INTO sys_menu
(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
-- 赛事管理按钮
(9710, '赛事新增',    9702, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:contest:add',     '#', 'migration', NOW(), '', NULL, ''),
(9711, '赛事编辑',    9702, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:contest:edit',    '#', 'migration', NOW(), '', NULL, ''),
(9712, '赛事提审',    9702, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:contest:submit',  '#', 'migration', NOW(), '', NULL, ''),
(9713, '赛事下线',    9702, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:contest:offline', '#', 'migration', NOW(), '', NULL, ''),
(9714, '赛事删除',    9702, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:contest:remove',  '#', 'migration', NOW(), '', NULL, ''),
(9715, '赛事提交审核',9702, 6, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:event:contest:submit',    '#', 'migration', NOW(), '', NULL, '兼容后端 submit 接口权限'),
-- 报名审核按钮
(9720, '报名查询',    9703, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:enroll:query',    '#', 'migration', NOW(), '', NULL, ''),
(9721, '报名审核',    9703, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:enroll:audit',    '#', 'migration', NOW(), '', NULL, ''),
(9722, '批量审核',    9703, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:enroll:batchAudit','#', 'migration', NOW(), '', NULL, ''),
-- 成绩管理按钮
(9730, '成绩查询',    9704, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:score:query',     '#', 'migration', NOW(), '', NULL, ''),
(9731, '成绩导入',    9704, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:score:import',    '#', 'migration', NOW(), '', NULL, ''),
(9732, '成绩编辑',    9704, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:score:edit',      '#', 'migration', NOW(), '', NULL, ''),
(9733, '成绩提审',    9704, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:score:submit',    '#', 'migration', NOW(), '', NULL, ''),
(9734, '成绩更正',    9704, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:score:correction','#', 'migration', NOW(), '', NULL, ''),
-- 证书管理按钮
(9740, '证书查询',    9705, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:cert:query',      '#', 'migration', NOW(), '', NULL, ''),
(9741, '模板管理',    9705, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:cert:template',   '#', 'migration', NOW(), '', NULL, ''),
(9742, '批量发证',    9705, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:cert:batchGrant', '#', 'migration', NOW(), '', NULL, ''),
(9743, '证书提审',    9705, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:cert:submit',     '#', 'migration', NOW(), '', NULL, ''),
(9744, '证书预览',    9705, 5, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:cert:preview',    '#', 'migration', NOW(), '', NULL, ''),
-- 结算中心按钮
(9750, '结算查询',    9706, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:settlement:query','#', 'migration', NOW(), '', NULL, ''),
(9751, '结算确认',    9706, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:settlement:confirm','#','migration', NOW(), '', NULL, ''),
(9752, '结算争议',    9706, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'jst:partner:settlement:dispute','#','migration', NOW(), '', NULL, '');

-- =====================
-- 4. 绑定 jst_partner 角色到所有赛事方菜单
-- =====================

-- 获取 jst_partner role_id
SET @partner_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'jst_partner' LIMIT 1);

-- 绑定：一级目录 + 二级菜单 + 三级按钮
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT @partner_role_id, menu_id
FROM sys_menu
WHERE menu_id BETWEEN 9700 AND 9799
  AND @partner_role_id IS NOT NULL;

-- =====================
-- 5. 验证
-- =====================

-- 应返回 9700-9799 段所有菜单
SELECT m.menu_id, m.menu_name, m.menu_type, m.perms
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE rm.role_id = @partner_role_id
  AND m.menu_id BETWEEN 9700 AND 9799
ORDER BY m.menu_id;

-- =====================================================================
-- 后续事项：
-- 1. 后端 Controller 的 @PreAuthorize 需改为 jst:partner:xxx 权限点
--    （当前部分 Controller 用 hasRole('jst_partner') 兜底，正式版改精确权限）
-- 2. permission.js 中的客户端 hack 注入可以移除
--    （菜单通过 getRouters() 从后端拉取即可）
-- 3. 若后续增加赛事方子菜单，在 9760-9799 段分配 menu_id
-- =====================================================================
