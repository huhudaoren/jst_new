-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水', '2500', '1', 'jst_points_ledger', 'jst/points/jst_points_ledger/index', 1, 0, 'C', '0', '0', 'jst:points:points_ledger:list', '#', 'admin', sysdate(), '', null, '积分流水菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:export',       '#', 'admin', sysdate(), '', null, '');