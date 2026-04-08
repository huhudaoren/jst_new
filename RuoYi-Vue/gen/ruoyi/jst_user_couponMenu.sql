-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户持有优惠券', '2600', '1', 'jst_user_coupon', 'jst/marketing/jst_user_coupon/index', 1, 0, 'C', '0', '0', 'jst:marketing:user_coupon:list', '#', 'admin', sysdate(), '', null, '用户持有优惠券菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户持有优惠券查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_coupon:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户持有优惠券新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_coupon:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户持有优惠券修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_coupon:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户持有优惠券删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_coupon:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户持有优惠券导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_coupon:export',       '#', 'admin', sysdate(), '', null, '');