-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城商品', '2500', '1', 'jst_mall_goods', 'jst/points/jst_mall_goods/index', 1, 0, 'C', '0', '0', 'jst:points:mall_goods:list', '#', 'admin', sysdate(), '', null, '积分商城商品菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城商品查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_goods:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城商品新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_goods:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城商品修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_goods:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城商品删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_goods:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城商品导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_goods:export',       '#', 'admin', sysdate(), '', null, '');