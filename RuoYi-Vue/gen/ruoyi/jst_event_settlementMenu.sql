-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单', '2400', '1', 'jst_event_settlement', 'jst/channel/jst_event_settlement/index', 1, 0, 'C', '0', '0', 'jst:channel:event_settlement:list', '#', 'admin', sysdate(), '', null, '赛事方结算单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:export',       '#', 'admin', sysdate(), '', null, '');