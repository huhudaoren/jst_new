-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道返点规则', '2400', '1', 'jst_rebate_rule', 'jst/channel/jst_rebate_rule/index', 1, 0, 'C', '0', '0', 'jst:channel:rebate_rule:list', '#', 'admin', sysdate(), '', null, '渠道返点规则菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道返点规则查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_rule:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道返点规则新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_rule:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道返点规则修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_rule:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道返点规则删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_rule:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道返点规则导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_rule:export',       '#', 'admin', sysdate(), '', null, '');