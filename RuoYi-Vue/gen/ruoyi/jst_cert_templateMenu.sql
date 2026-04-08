-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板', '2200', '1', 'jst_cert_template', 'jst/event/jst_cert_template/index', 1, 0, 'C', '0', '0', 'jst:event:cert_template:list', '#', 'admin', sysdate(), '', null, '证书模板菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:export',       '#', 'admin', sysdate(), '', null, '');