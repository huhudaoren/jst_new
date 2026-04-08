-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）', '2200', '1', 'jst_enroll_record', 'jst/event/jst_enroll_record/index', 1, 0, 'C', '0', '0', 'jst:event:enroll_record:list', '#', 'admin', sysdate(), '', null, '报名记录（含动态单快照）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:export',       '#', 'admin', sysdate(), '', null, '');