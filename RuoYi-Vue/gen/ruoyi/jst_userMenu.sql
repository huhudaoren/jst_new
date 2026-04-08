-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号', '2100', '1', 'jst_user', 'jst/user/jst_user/index', 1, 0, 'C', '0', '0', 'jst:user:user:list', '#', 'admin', sysdate(), '', null, '用户主-学生/家长/渠道方业务账号菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:export',       '#', 'admin', sysdate(), '', null, '');