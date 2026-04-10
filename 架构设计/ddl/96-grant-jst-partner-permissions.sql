-- =====================================================================
-- 文件名：96-grant-jst-partner-permissions.sql
-- 用途  : 给 jst_partner 角色批量赋 jst:event:* 等业务权限
-- 适用  : DEV/TEST,允许测试 PartnerScope 切面
-- 背景  : F5 子 Agent 通过审核创建的赛事方账号绑定 jst_partner 角色,
--         但 jst_partner 默认没有任何菜单权限 → 调任何 jst:event:* 接口 403
--         本脚本为开发期补全权限菜单数据
-- =====================================================================
SET NAMES utf8mb4;

-- 1. 找到 jst_partner 角色 ID
SELECT @partner_role_id := role_id FROM sys_role WHERE role_key = 'jst_partner' LIMIT 1;

-- 2. 把所有 jst:event:* 和 jst:user:binding:* 权限对应的菜单 ID 全部赋给 jst_partner
-- 注: gen 出来的菜单 perms 形如 jst:event:contest:list / jst:event:contest:add 等
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @partner_role_id, m.menu_id
FROM sys_menu m
WHERE m.perms IN (
    -- 赛事 CRUD
    'jst:event:contest:list',     'jst:event:contest:query',    'jst:event:contest:add',
    'jst:event:contest:edit',     'jst:event:contest:submit',
    -- 表单模板 (赛事方需要配置自己的表单)
    'jst:event:formTemplate:list','jst:event:formTemplate:edit','jst:event:formTemplate:audit',
    -- 报名/成绩/证书 (查看自己的)
    'jst:event:enrollRecord:list','jst:event:scoreRecord:list', 'jst:event:certRecord:list',
    'jst:event:score_record:list','jst:event:score_record:query','jst:event:score_record:add','jst:event:score_record:edit',
    'jst:event:cert_record:list','jst:event:cert_record:query','jst:event:cert_record:add','jst:event:cert_record:edit',
    'jst:event:cert_template:list','jst:event:cert_template:query','jst:event:cert_template:add','jst:event:cert_template:edit',
    'jst:channel:event_settlement:list', 'jst:channel:event_settlement:query',
    'jst:channel:event_settlement:edit'
)
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @partner_role_id AND rm.menu_id = m.menu_id
);

-- 3. 验证: 应返回 jst_partner 已有的所有菜单
SELECT m.menu_name, m.perms FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE rm.role_id = @partner_role_id
ORDER BY m.perms;

-- =====================================================================
-- 注意事项:
-- - 此脚本可重复执行,有 NOT EXISTS 防重
-- - 生产环境应通过管理后台「角色管理 → 菜单权限」勾选,而不是 SQL
-- - jst_channel / jst_student 角色的权限单独写另外的脚本
-- =====================================================================
