-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程', '2200', '1', 'jst_course', 'jst/event/jst_course/index', 1, 0, 'C', '1', '0', 'jst:event:course:list', '#', 'admin', sysdate(), '', null, '课程菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:export',       '#', 'admin', sysdate(), '', null, '');
