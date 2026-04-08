-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请', '2200', '1', 'jst_event_partner_apply', 'jst/organizer/jst_event_partner_apply/index', 1, 0, 'C', '0', '0', 'jst:organizer:event_partner_apply:list', '#', 'admin', sysdate(), '', null, '赛事方入驻申请菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:export',       '#', 'admin', sysdate(), '', null, '');