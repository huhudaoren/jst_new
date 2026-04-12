-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主', '2200', '1', 'jst_contest', 'jst/event/jst_contest/index', 1, 0, 'C', '1', '0', 'jst:event:contest:list', '#', 'admin', sysdate(), '', null, '赛事主菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:export',       '#', 'admin', sysdate(), '', null, '');
