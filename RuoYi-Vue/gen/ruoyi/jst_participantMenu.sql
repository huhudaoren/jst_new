-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('临时参赛档案-允许无正式账号存在', '3', '1', 'jst_participant', 'system/jst_participant/index', 1, 0, 'C', '0', '0', 'system:jst_participant:list', '#', 'admin', sysdate(), '', null, '临时参赛档案-允许无正式账号存在菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('临时参赛档案-允许无正式账号存在查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('临时参赛档案-允许无正式账号存在新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('临时参赛档案-允许无正式账号存在修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('临时参赛档案-允许无正式账号存在删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('临时参赛档案-允许无正式账号存在导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant:export',       '#', 'admin', sysdate(), '', null, '');