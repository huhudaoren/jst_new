-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细', '2300', '1', 'jst_team_appointment_member', 'jst/order/jst_team_appointment_member/index', 1, 0, 'C', '0', '0', 'jst:order:team_appointment_member:list', '#', 'admin', sysdate(), '', null, '团队预约成员明细菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:export',       '#', 'admin', sysdate(), '', null, '');