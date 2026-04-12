-- 合并自 50 个 *Menu.sql,执行前确保 23-基础数据初始化.sql 已执行
-- 父菜单 2000-2900 必须已存在(由 23 文档创建)

-- ===== jst_appointment_writeoff_item =====
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
-- ===== jst_event_partner =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案', '2200', '1', 'jst_event_partner', 'jst/event/jst_event_partner/index', 1, 0, 'C', '0', '0', 'jst:event:event_partner:list', '#', 'admin', sysdate(), '', null, '赛事方档案菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方档案导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:event_partner:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_rebate_settlement =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道提现/结算单', '2400', '1', 'jst_rebate_settlement', 'jst/channel/jst_rebate_settlement/index', 1, 0, 'C', '0', '0', 'jst:channel:rebate_settlement:list', '#', 'admin', sysdate(), '', null, '渠道提现/结算单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道提现/结算单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_settlement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道提现/结算单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_settlement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道提现/结算单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_settlement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道提现/结算单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_settlement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道提现/结算单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_settlement:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_risk_blacklist =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控黑白名单', '2800', '1', 'jst_risk_blacklist', 'jst/risk/jst_risk_blacklist/index', 1, 0, 'C', '0', '0', 'jst:risk:risk_blacklist:list', '#', 'admin', sysdate(), '', null, '风控黑白名单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控黑白名单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_blacklist:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控黑白名单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_blacklist:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控黑白名单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_blacklist:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控黑白名单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_blacklist:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('风控黑白名单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:risk_blacklist:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_payment_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('支付记录', '2300', '1', 'jst_payment_record', 'jst/order/jst_payment_record/index', 1, 0, 'C', '0', '0', 'jst:order:payment_record:list', '#', 'admin', sysdate(), '', null, '支付记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('支付记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:payment_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('支付记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:payment_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('支付记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:payment_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('支付记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:payment_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('支付记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:payment_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_event_partner_apply =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请', '2200', '1', 'jst_event_partner_apply', 'jst/organizer/jst_event_partner_apply/index', 1, 0, 'C', '0', '0', 'jst:organizer:event_partner_apply:list', '#', 'admin', sysdate(), '', null, '赛事方入驻申请菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方入驻申请导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:event_partner_apply:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_audit_log =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('操作审计日志', '2800', '1', 'jst_audit_log', 'jst/risk/jst_audit_log/index', 1, 0, 'C', '0', '0', 'jst:risk:audit_log:list', '#', 'admin', sysdate(), '', null, '操作审计日志菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('操作审计日志查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:audit_log:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('操作审计日志新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:audit_log:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('操作审计日志修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:audit_log:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('操作审计日志删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:audit_log:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('操作审计日志导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:risk:audit_log:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_cert_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书记录', '2200', '1', 'jst_cert_record', 'jst/event/jst_cert_record/index', 1, 0, 'C', '0', '0', 'jst:event:cert_record:list', '#', 'admin', sysdate(), '', null, '证书记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_mall_goods =====
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
-- ===== jst_notice =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('公告', '2700', '1', 'jst_notice', 'jst/message/jst_notice/index', 1, 0, 'C', '0', '0', 'jst:message:notice:list', '#', 'admin', sysdate(), '', null, '公告菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('公告查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:notice:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('公告新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:notice:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('公告修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:notice:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('公告删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:notice:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('公告导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:notice:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_risk_case =====
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
-- ===== jst_score_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩记录', '2200', '1', 'jst_score_record', 'jst/event/jst_score_record/index', 1, 0, 'C', '0', '0', 'jst:event:score_record:list', '#', 'admin', sysdate(), '', null, '成绩记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:score_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:score_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:score_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:score_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:score_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_contract_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合同记录', '2900', '1', 'jst_contract_record', 'jst/finance/jst_contract_record/index', 1, 0, 'C', '0', '0', 'jst:finance:contract_record:list', '#', 'admin', sysdate(), '', null, '合同记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合同记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:contract_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合同记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:contract_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合同记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:contract_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合同记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:contract_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合同记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:contract_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_points_account =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分账户', '2500', '1', 'jst_points_account', 'jst/points/jst_points_account/index', 1, 0, 'C', '0', '0', 'jst:points:points_account:list', '#', 'admin', sysdate(), '', null, '积分账户菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分账户查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_account:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分账户新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_account:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分账户修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_account:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分账户删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_account:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分账户导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_account:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_message_template =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息模板', '2700', '1', 'jst_message_template', 'jst/message/jst_message_template/index', 1, 0, 'C', '0', '0', 'jst:message:message_template:list', '#', 'admin', sysdate(), '', null, '消息模板菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息模板查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息模板新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息模板修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息模板删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息模板导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_template:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_mall_exchange_order =====
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
-- ===== jst_risk_alert =====
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
-- ===== jst_coupon_template =====
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
-- ===== jst_risk_rule =====
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
-- ===== jst_team_appointment_member =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细', '2300', '1', 'jst_team_appointment_member', 'jst/order/jst_team_appointment_member/index', 1, 0, 'C', '0', '0', 'jst:order:team_appointment_member:list', '#', 'admin', sysdate(), '', null, '团队预约成员明细菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约成员明细导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment_member:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_coupon_issue_batch =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次', '2600', '1', 'jst_coupon_issue_batch', 'jst/marketing/jst_coupon_issue_batch/index', 1, 0, 'C', '0', '0', 'jst:marketing:coupon_issue_batch:list', '#', 'admin', sysdate(), '', null, '发券批次菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发券批次导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:coupon_issue_batch:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_payment_pay_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('统一打款记录', '2900', '1', 'jst_payment_pay_record', 'jst/finance/jst_payment_pay_record/index', 1, 0, 'C', '0', '0', 'jst:finance:payment_pay_record:list', '#', 'admin', sysdate(), '', null, '统一打款记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('统一打款记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:payment_pay_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('统一打款记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:payment_pay_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('统一打款记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:payment_pay_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('统一打款记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:payment_pay_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('统一打款记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:payment_pay_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_user =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号', '2100', '1', 'jst_user', 'jst/user/jst_user/index', 1, 0, 'C', '0', '0', 'jst:user:user:list', '#', 'admin', sysdate(), '', null, '用户主-学生/家长/渠道方业务账号菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户主-学生/家长/渠道方业务账号导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:user:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_enroll_form_template =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）', '2200', '1', 'jst_enroll_form_template', 'jst/event/jst_enroll_form_template/index', 1, 0, 'C', '0', '0', 'jst:event:enroll_form_template:list', '#', 'admin', sysdate(), '', null, '报名动态单模板（schema_json定义字段）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名动态单模板（schema_json定义字段）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_form_template:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_rights_writeoff_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录', '2600', '1', 'jst_rights_writeoff_record', 'jst/marketing/jst_rights_writeoff_record/index', 1, 0, 'C', '0', '0', 'jst:marketing:rights_writeoff_record:list', '#', 'admin', sysdate(), '', null, '权益核销记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益核销记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_writeoff_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_event_settlement =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单', '2400', '1', 'jst_event_settlement', 'jst/channel/jst_event_settlement/index', 1, 0, 'C', '0', '0', 'jst:channel:event_settlement:list', '#', 'admin', sysdate(), '', null, '赛事方结算单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事方结算单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:event_settlement:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_rebate_ledger =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账', '2400', '1', 'jst_rebate_ledger', 'jst/channel/jst_rebate_ledger/index', 1, 0, 'C', '0', '0', 'jst:channel:rebate_ledger:list', '#', 'admin', sysdate(), '', null, '返点计提台账菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('返点计提台账导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:channel:rebate_ledger:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_rights_template =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益模板', '2600', '1', 'jst_rights_template', 'jst/marketing/jst_rights_template/index', 1, 0, 'C', '0', '0', 'jst:marketing:rights_template:list', '#', 'admin', sysdate(), '', null, '权益模板菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益模板查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益模板新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益模板修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益模板删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('权益模板导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:rights_template:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_channel_auth_apply =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道认证申请', '2200', '1', 'jst_channel_auth_apply', 'jst/organizer/jst_channel_auth_apply/index', 1, 0, 'C', '0', '0', 'jst:organizer:channel_auth_apply:list', '#', 'admin', sysdate(), '', null, '渠道认证申请菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道认证申请查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:channel_auth_apply:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道认证申请新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:channel_auth_apply:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道认证申请修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:channel_auth_apply:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道认证申请删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:channel_auth_apply:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道认证申请导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:organizer:channel_auth_apply:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_level_config =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('等级配置', '2500', '1', 'jst_level_config', 'jst/points/jst_level_config/index', 1, 0, 'C', '0', '0', 'jst:points:level_config:list', '#', 'admin', sysdate(), '', null, '等级配置菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('等级配置查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:level_config:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('等级配置新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:level_config:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('等级配置修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:level_config:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('等级配置删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:level_config:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('等级配置导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:level_config:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_course_learn_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程学习记录', '2200', '1', 'jst_course_learn_record', 'jst/event/jst_course_learn_record/index', 1, 0, 'C', '0', '0', 'jst:event:course_learn_record:list', '#', 'admin', sysdate(), '', null, '课程学习记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程学习记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course_learn_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程学习记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course_learn_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程学习记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course_learn_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程学习记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course_learn_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程学习记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course_learn_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_appointment_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('个人预约记录', '2300', '1', 'jst_appointment_record', 'jst/order/jst_appointment_record/index', 1, 0, 'C', '0', '0', 'jst:order:appointment_record:list', '#', 'admin', sysdate(), '', null, '个人预约记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('个人预约记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('个人预约记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('个人预约记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('个人预约记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('个人预约记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:appointment_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_refund_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单', '2300', '1', 'jst_refund_record', 'jst/order/jst_refund_record/index', 1, 0, 'C', '0', '0', 'jst:order:refund_record:list', '#', 'admin', sysdate(), '', null, '退款/售后单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('退款/售后单导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:refund_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_cert_template =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板', '2200', '1', 'jst_cert_template', 'jst/event/jst_cert_template/index', 1, 0, 'C', '0', '0', 'jst:event:cert_template:list', '#', 'admin', sysdate(), '', null, '证书模板菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('证书模板导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:cert_template:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_order_item =====
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
-- ===== jst_rebate_rule =====
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
-- ===== jst_points_rule =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分/成长值规则', '2500', '1', 'jst_points_rule', 'jst/points/jst_points_rule/index', 1, 0, 'C', '0', '0', 'jst:points:points_rule:list', '#', 'admin', sysdate(), '', null, '积分/成长值规则菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分/成长值规则查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_rule:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分/成长值规则新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_rule:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分/成长值规则修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_rule:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分/成长值规则删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_rule:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分/成长值规则导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_rule:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_invoice_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发票记录', '2900', '1', 'jst_invoice_record', 'jst/finance/jst_invoice_record/index', 1, 0, 'C', '0', '0', 'jst:finance:invoice_record:list', '#', 'admin', sysdate(), '', null, '发票记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发票记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:invoice_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发票记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:invoice_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发票记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:invoice_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发票记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:invoice_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发票记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:finance:invoice_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_channel =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案', '2100', '1', 'jst_channel', 'jst/user/jst_channel/index', 1, 0, 'C', '0', '0', 'jst:user:channel:list', '#', 'admin', sysdate(), '', null, '渠道方档案菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('渠道方档案导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:channel:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_points_ledger =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水', '2500', '1', 'jst_points_ledger', 'jst/points/jst_points_ledger/index', 1, 0, 'C', '0', '0', 'jst:points:points_ledger:list', '#', 'admin', sysdate(), '', null, '积分流水菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('积分流水导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:points_ledger:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_message_log =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志', '2700', '1', 'jst_message_log', 'jst/message/jst_message_log/index', 1, 0, 'C', '0', '0', 'jst:message:message_log:list', '#', 'admin', sysdate(), '', null, '消息发送日志菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('消息发送日志导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:message:message_log:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_user_coupon =====
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
-- ===== jst_student_channel_binding =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）', '2100', '1', 'jst_student_channel_binding', 'jst/user/jst_student_channel_binding/index', 1, 0, 'C', '0', '0', 'jst:user:student_channel_binding:list', '#', 'admin', sysdate(), '', null, '学生-渠道方绑定关系（同一user同时仅1条active）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生-渠道方绑定关系（同一user同时仅1条active）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:user:student_channel_binding:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_order_main =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单主', '2300', '1', 'jst_order_main', 'jst/order/jst_order_main/index', 1, 0, 'C', '0', '0', 'jst:order:order_main:list', '#', 'admin', sysdate(), '', null, '订单主菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单主查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_main:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单主新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_main:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单主修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_main:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单主删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_main:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('订单主导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:order_main:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_user_rights =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有', '2600', '1', 'jst_user_rights', 'jst/marketing/jst_user_rights/index', 1, 0, 'C', '0', '0', 'jst:marketing:user_rights:list', '#', 'admin', sysdate(), '', null, '用户权益持有菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('用户权益持有导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:marketing:user_rights:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_enroll_record =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）', '2200', '1', 'jst_enroll_record', 'jst/event/jst_enroll_record/index', 1, 0, 'C', '1', '0', 'jst:event:enroll_record:list', '#', 'admin', sysdate(), '', null, '报名记录（含动态单快照）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('报名记录（含动态单快照）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:enroll_record:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_team_appointment =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录', '2300', '1', 'jst_team_appointment', 'jst/order/jst_team_appointment/index', 1, 0, 'C', '0', '0', 'jst:order:team_appointment:list', '#', 'admin', sysdate(), '', null, '团队预约主记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('团队预约主记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:order:team_appointment:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_contest =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主', '2200', '1', 'jst_contest', 'jst/event/jst_contest/index', 1, 0, 'C', '1', '0', 'jst:event:contest:list', '#', 'admin', sysdate(), '', null, '赛事主菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('赛事主导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:contest:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_growth_ledger =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成长值流水', '2500', '1', 'jst_growth_ledger', 'jst/points/jst_growth_ledger/index', 1, 0, 'C', '0', '0', 'jst:points:growth_ledger:list', '#', 'admin', sysdate(), '', null, '成长值流水菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成长值流水查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:growth_ledger:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成长值流水新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:growth_ledger:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成长值流水修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:growth_ledger:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成长值流水删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:growth_ledger:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成长值流水导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:points:growth_ledger:export',       '#', 'admin', sysdate(), '', null, '');
-- ===== jst_course =====
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程', '2200', '1', 'jst_course', 'jst/event/jst_course/index', 1, 0, 'C', '1', '0', 'jst:event:course:list', '#', 'admin', sysdate(), '', null, '课程菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'jst:event:course:export',       '#', 'admin', sysdate(), '', null, '');
