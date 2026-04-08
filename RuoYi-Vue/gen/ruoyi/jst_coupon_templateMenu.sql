-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('优惠券模板', '2600', '1', 'jst_coupon_template', 'jst/marketing/jst_coupon_template/index', 1, 0, 'C', '0', '0', 'jst:marketing:coupon_template:list', '#', 'admin', sysdate(), '', null, '优惠券模板菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('优惠券模板查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('优惠券模板新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('优惠券模板修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('优惠券模板删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('优惠券模板导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_template:export',       '#', 'admin', sysdate(), '', null, '');