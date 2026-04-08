-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控规则', '2800', '1', 'jst_risk_rule', 'jst/risk/jst_risk_rule/index', 1, 0, 'C', '0', '0', 'jst:risk:risk_rule:list', '#', 'admin', sysdate(), '', null, '风控规则菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控规则查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_rule:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控规则新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_rule:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控规则修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_rule:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控规则删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_rule:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控规则导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_rule:export',       '#', 'admin', sysdate(), '', null, '');