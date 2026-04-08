-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录', '2600', '1', 'jst_rights_writeoff_record', 'jst/marketing/jst_rights_writeoff_record/index', 1, 0, 'C', '0', '0', 'jst:marketing:rights_writeoff_record:list', '#', 'admin', sysdate(), '', null, '权益核销记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:export',       '#', 'admin', sysdate(), '', null, '');