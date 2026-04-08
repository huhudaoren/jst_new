-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案', '2200', '1', 'jst_event_partner', 'jst/event/jst_event_partner/index', 1, 0, 'C', '0', '0', 'jst:event:event_partner:list', '#', 'admin', sysdate(), '', null, '赛事方档案菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:export',       '#', 'admin', sysdate(), '', null, '');