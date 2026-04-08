-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单', '2300', '1', 'jst_refund_record', 'jst/order/jst_refund_record/index', 1, 0, 'C', '0', '0', 'jst:order:refund_record:list', '#', 'admin', sysdate(), '', null, '退款/售后单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:export',       '#', 'admin', sysdate(), '', null, '');