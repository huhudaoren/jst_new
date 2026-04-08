-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险工单', '2800', '1', 'jst_risk_case', 'jst/risk/jst_risk_case/index', 1, 0, 'C', '0', '0', 'jst:risk:risk_case:list', '#', 'admin', sysdate(), '', null, '风险工单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险工单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_case:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险工单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_case:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险工单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_case:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险工单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_case:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险工单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_case:export',       '#', 'admin', sysdate(), '', null, '');