-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）', '2100', '1', 'jst_student_channel_binding', 'jst/user/jst_student_channel_binding/index', 1, 0, 'C', '0', '0', 'jst:user:student_channel_binding:list', '#', 'admin', sysdate(), '', null, '学生-渠道方绑定关系（同一user同时仅1条active）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:export',       '#', 'admin', sysdate(), '', null, '');