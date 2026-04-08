-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险预警', '2800', '1', 'jst_risk_alert', 'jst/risk/jst_risk_alert/index', 1, 0, 'C', '0', '0', 'jst:risk:risk_alert:list', '#', 'admin', sysdate(), '', null, '风险预警菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险预警查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_alert:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险预警新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_alert:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险预警修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_alert:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险预警删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_alert:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风险预警导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_alert:export',       '#', 'admin', sysdate(), '', null, '');