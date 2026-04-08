-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次', '2600', '1', 'jst_coupon_issue_batch', 'jst/marketing/jst_coupon_issue_batch/index', 1, 0, 'C', '0', '0', 'jst:marketing:coupon_issue_batch:list', '#', 'admin', sysdate(), '', null, '发券批次菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:export',       '#', 'admin', sysdate(), '', null, '');