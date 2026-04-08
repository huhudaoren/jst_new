-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录', '2300', '1', 'jst_team_appointment', 'jst/order/jst_team_appointment/index', 1, 0, 'C', '0', '0', 'jst:order:team_appointment:list', '#', 'admin', sysdate(), '', null, '团队预约主记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:export',       '#', 'admin', sysdate(), '', null, '');