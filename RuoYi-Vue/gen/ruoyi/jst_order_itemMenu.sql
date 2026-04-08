-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单明细（最小核算单元，承载分摊与回滚）', '2300', '1', 'jst_order_item', 'jst/order/jst_order_item/index', 1, 0, 'C', '0', '0', 'jst:order:order_item:list', '#', 'admin', sysdate(), '', null, '订单明细（最小核算单元，承载分摊与回滚）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单明细（最小核算单元，承载分摊与回滚）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_item:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单明细（最小核算单元，承载分摊与回滚）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_item:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单明细（最小核算单元，承载分摊与回滚）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_item:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单明细（最小核算单元，承载分摊与回滚）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_item:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单明细（最小核算单元，承载分摊与回滚）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_item:export',       '#', 'admin', sysdate(), '', null, '');