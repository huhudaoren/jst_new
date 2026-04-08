-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账', '2400', '1', 'jst_rebate_ledger', 'jst/channel/jst_rebate_ledger/index', 1, 0, 'C', '0', '0', 'jst:channel:rebate_ledger:list', '#', 'admin', sysdate(), '', null, '返点计提台账菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:export',       '#', 'admin', sysdate(), '', null, '');