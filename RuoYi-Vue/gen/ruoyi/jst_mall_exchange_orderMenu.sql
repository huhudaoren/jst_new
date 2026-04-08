-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城兑换订单', '2500', '1', 'jst_mall_exchange_order', 'jst/points/jst_mall_exchange_order/index', 1, 0, 'C', '0', '0', 'jst:points:mall_exchange_order:list', '#', 'admin', sysdate(), '', null, '积分商城兑换订单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城兑换订单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_exchange_order:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城兑换订单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_exchange_order:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城兑换订单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_exchange_order:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城兑换订单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_exchange_order:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分商城兑换订单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:mall_exchange_order:export',       '#', 'admin', sysdate(), '', null, '');