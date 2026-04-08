-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案', '2100', '1', 'jst_channel', 'jst/user/jst_channel/index', 1, 0, 'C', '0', '0', 'jst:user:channel:list', '#', 'admin', sysdate(), '', null, '渠道方档案菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:export',       '#', 'admin', sysdate(), '', null, '');