-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有', '2600', '1', 'jst_user_rights', 'jst/marketing/jst_user_rights/index', 1, 0, 'C', '0', '0', 'jst:marketing:user_rights:list', '#', 'admin', sysdate(), '', null, '用户权益持有菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:export',       '#', 'admin', sysdate(), '', null, '');