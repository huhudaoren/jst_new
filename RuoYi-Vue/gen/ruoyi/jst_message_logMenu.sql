-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志', '2700', '1', 'jst_message_log', 'jst/message/jst_message_log/index', 1, 0, 'C', '0', '0', 'jst:message:message_log:list', '#', 'admin', sysdate(), '', null, '消息发送日志菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:export',       '#', 'admin', sysdate(), '', null, '');