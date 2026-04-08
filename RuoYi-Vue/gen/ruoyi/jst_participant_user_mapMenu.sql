-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参赛档案-正式用户认领映射', '3', '1', 'jst_participant_user_map', 'system/jst_participant_user_map/index', 1, 0, 'C', '0', '0', 'system:jst_participant_user_map:list', '#', 'admin', sysdate(), '', null, '参赛档案-正式用户认领映射菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参赛档案-正式用户认领映射查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant_user_map:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参赛档案-正式用户认领映射新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant_user_map:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参赛档案-正式用户认领映射修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant_user_map:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参赛档案-正式用户认领映射删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant_user_map:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参赛档案-正式用户认领映射导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:jst_participant_user_map:export',       '#', 'admin', sysdate(), '', null, '');