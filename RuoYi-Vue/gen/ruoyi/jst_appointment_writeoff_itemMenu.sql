-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('核销子项（团队部分核销/到场/礼品/仪式独立流转）', '2300', '1', 'jst_appointment_writeoff_item', 'jst/order/jst_appointment_writeoff_item/index', 1, 0, 'C', '0', '0', 'jst:order:appointment_writeoff_item:list', '#', 'admin', sysdate(), '', null, '核销子项（团队部分核销/到场/礼品/仪式独立流转）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('核销子项（团队部分核销/到场/礼品/仪式独立流转）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_writeoff_item:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('核销子项（团队部分核销/到场/礼品/仪式独立流转）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_writeoff_item:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('核销子项（团队部分核销/到场/礼品/仪式独立流转）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_writeoff_item:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('核销子项（团队部分核销/到场/礼品/仪式独立流转）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_writeoff_item:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('核销子项（团队部分核销/到场/礼品/仪式独立流转）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_writeoff_item:export',       '#', 'admin', sysdate(), '', null, '');