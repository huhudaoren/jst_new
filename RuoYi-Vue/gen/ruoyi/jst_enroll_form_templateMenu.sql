-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）', '2200', '1', 'jst_enroll_form_template', 'jst/event/jst_enroll_form_template/index', 1, 0, 'C', '0', '0', 'jst:event:enroll_form_template:list', '#', 'admin', sysdate(), '', null, '报名动态单模板（schema_json定义字段）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:export',       '#', 'admin', sysdate(), '', null, '');