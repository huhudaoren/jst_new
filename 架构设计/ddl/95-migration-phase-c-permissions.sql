-- =====================================================================
-- 文件名：95-migration-phase-c-permissions.sql
-- 用途  ：补齐阶段 C 新增接口的权限点与角色菜单绑定
-- 适用  ：MySQL 5.7 / DEV & TEST
-- 说明  ：
--   1. 不修改 23-基础数据初始化.sql，仅通过迁移脚本补数
--   2. 按 perms / menu_id 双重防重，可重复执行
--   3. 平台运营角色优先绑定 jst_platform_op，如不存在则回落到 admin
--   4. 兼容当前源码仍在使用的额外权限：
--      - jst:channel:withdraw:my
--      - jst:event:writeoff:list
-- =====================================================================
SET NAMES utf8mb4;

SELECT @student_role_id := role_id
FROM sys_role
WHERE role_key = 'jst_student'
LIMIT 1;

SELECT @channel_role_id := role_id
FROM sys_role
WHERE role_key = 'jst_channel'
LIMIT 1;

SELECT @platform_role_id := COALESCE(
    (SELECT role_id FROM sys_role WHERE role_key = 'jst_platform_op' LIMIT 1),
    (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1)
);

INSERT INTO sys_menu (
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
)
SELECT
    seed.menu_id,
    seed.menu_name,
    seed.parent_id,
    seed.order_num,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    seed.perms,
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    seed.remark
FROM (
    -- jst-channel
    SELECT 9500 AS menu_id, '渠道提现列表' AS menu_name, 2400 AS parent_id, 1 AS order_num,
           'jst:channel:withdraw:list' AS perms, 'DEBT-3 补齐渠道提现列表权限' AS remark
    UNION ALL
    SELECT 9501, '渠道提现详情', 2400, 2, 'jst:channel:withdraw:query', 'DEBT-3 补齐渠道提现详情权限'
    UNION ALL
    SELECT 9502, '渠道提现申请', 2400, 3, 'jst:channel:withdraw:apply', 'DEBT-3 补齐渠道提现申请权限'
    UNION ALL
    SELECT 9503, '渠道提现取消', 2400, 4, 'jst:channel:withdraw:cancel', 'DEBT-3 补齐渠道提现取消权限'
    UNION ALL
    SELECT 9504, '渠道提现审核', 2400, 5, 'jst:channel:withdraw:audit', 'DEBT-3 补齐渠道提现审核权限'
    UNION ALL
    SELECT 9505, '渠道提现执行打款', 2400, 6, 'jst:channel:withdraw:execute', 'DEBT-3 补齐渠道提现执行打款权限'
    UNION ALL
    SELECT 9506, '我的提现单', 2400, 7, 'jst:channel:withdraw:my', 'DEBT-3 兼容当前源码 withdraw:my'
    UNION ALL
    SELECT 9510, '渠道月度看板', 2400, 10, 'jst:channel:dashboard:monthly', 'DEBT-3 补齐渠道月度看板权限'
    UNION ALL
    SELECT 9511, '渠道新增学生趋势', 2400, 11, 'jst:channel:dashboard:students', 'DEBT-3 补齐渠道新增学生趋势权限'
    UNION ALL
    SELECT 9512, '渠道订单趋势', 2400, 12, 'jst:channel:dashboard:orders', 'DEBT-3 补齐渠道订单趋势权限'
    UNION ALL
    SELECT 9513, '渠道统计概览', 2400, 13, 'jst:channel:dashboard:stats', 'DEBT-3 补齐渠道统计概览权限'

    -- jst-event
    UNION ALL
    SELECT 9520, '个人预约申请', 2200, 20, 'jst:event:appointment:apply', 'DEBT-3 补齐个人预约申请权限'
    UNION ALL
    SELECT 9521, '个人预约取消', 2200, 21, 'jst:event:appointment:cancel', 'DEBT-3 补齐个人预约取消权限'
    UNION ALL
    SELECT 9522, '我的个人预约', 2200, 22, 'jst:event:appointment:my', 'DEBT-3 补齐我的个人预约权限'
    UNION ALL
    SELECT 9523, '个人预约详情', 2200, 23, 'jst:event:appointment:query', 'DEBT-3 补齐个人预约详情权限'
    UNION ALL
    SELECT 9530, '预约核销扫码', 2200, 30, 'jst:event:writeoff:scan', 'DEBT-3 补齐预约核销扫码权限'
    UNION ALL
    SELECT 9531, '预约核销记录', 2200, 31, 'jst:event:writeoff:records', 'DEBT-3 补齐预约核销记录权限'
    UNION ALL
    SELECT 9532, '预约核销记录列表', 2200, 32, 'jst:event:writeoff:list', 'DEBT-3 兼容当前源码 writeoff:list'

    -- jst-points
    UNION ALL
    SELECT 9540, '积分商城商品列表', 2500, 40, 'jst:points:mall:goods:list', 'DEBT-3 补齐积分商城商品列表权限'
    UNION ALL
    SELECT 9541, '积分商城商品新增', 2500, 41, 'jst:points:mall:goods:add', 'DEBT-3 补齐积分商城商品新增权限'
    UNION ALL
    SELECT 9542, '积分商城商品修改', 2500, 42, 'jst:points:mall:goods:edit', 'DEBT-3 补齐积分商城商品修改权限'
    UNION ALL
    SELECT 9543, '积分商城商品删除', 2500, 43, 'jst:points:mall:goods:remove', 'DEBT-3 补齐积分商城商品删除权限'
    UNION ALL
    SELECT 9544, '积分商城商品发布', 2500, 44, 'jst:points:mall:goods:publish', 'DEBT-3 补齐积分商城商品发布权限'
    UNION ALL
    SELECT 9550, '兑换订单列表', 2500, 50, 'jst:points:mall:exchange:list', 'DEBT-3 补齐兑换订单列表权限'
    UNION ALL
    SELECT 9551, '兑换订单发货', 2500, 51, 'jst:points:mall:exchange:ship', 'DEBT-3 补齐兑换订单发货权限'
    UNION ALL
    SELECT 9552, '兑换订单详情', 2500, 52, 'jst:points:mall:exchange:query', 'DEBT-3 补齐兑换订单详情权限'
    UNION ALL
    SELECT 9553, '积分商城兑换申请', 2500, 53, 'jst:points:mall:exchange:apply', 'DEBT-3 补齐积分商城兑换申请权限'
    UNION ALL
    SELECT 9554, '我的兑换订单', 2500, 54, 'jst:points:mall:exchange:my', 'DEBT-3 补齐我的兑换订单权限'
    UNION ALL
    SELECT 9555, '兑换订单取消', 2500, 55, 'jst:points:mall:exchange:cancel', 'DEBT-3 补齐兑换订单取消权限'
    UNION ALL
    SELECT 9560, '积分商城浏览', 2500, 60, 'jst:points:mall:browse', 'DEBT-3 补齐积分商城浏览权限'
    UNION ALL
    SELECT 9561, '商城售后申请', 2500, 61, 'jst:points:mall:aftersale:apply', 'DEBT-3 补齐商城售后申请权限'
    UNION ALL
    SELECT 9562, '我的商城售后', 2500, 62, 'jst:points:mall:aftersale:my', 'DEBT-3 补齐我的商城售后权限'
    UNION ALL
    SELECT 9563, '商城售后审核', 2500, 63, 'jst:points:mall:aftersale:audit', 'DEBT-3 补齐商城售后审核权限'
    UNION ALL
    SELECT 9570, '积分中心概览', 2500, 70, 'jst:points:center:summary', 'DEBT-3 补齐积分中心概览权限'
    UNION ALL
    SELECT 9571, '积分等级列表', 2500, 71, 'jst:points:center:levels', 'DEBT-3 补齐积分等级列表权限'
    UNION ALL
    SELECT 9572, '积分流水列表', 2500, 72, 'jst:points:center:ledger', 'DEBT-3 补齐积分流水列表权限'
    UNION ALL
    SELECT 9573, '积分任务列表', 2500, 73, 'jst:points:center:tasks', 'DEBT-3 补齐积分任务列表权限'

    -- jst-marketing
    UNION ALL
    SELECT 9580, '优惠券模板列表', 2600, 80, 'jst:marketing:coupon:list', 'DEBT-3 补齐优惠券模板列表权限'
    UNION ALL
    SELECT 9581, '优惠券模板详情', 2600, 81, 'jst:marketing:coupon:query', 'DEBT-3 补齐优惠券模板详情权限'
    UNION ALL
    SELECT 9582, '优惠券模板新增', 2600, 82, 'jst:marketing:coupon:add', 'DEBT-3 补齐优惠券模板新增权限'
    UNION ALL
    SELECT 9583, '优惠券模板修改', 2600, 83, 'jst:marketing:coupon:edit', 'DEBT-3 补齐优惠券模板修改权限'
    UNION ALL
    SELECT 9584, '优惠券模板删除', 2600, 84, 'jst:marketing:coupon:remove', 'DEBT-3 补齐优惠券模板删除权限'
    UNION ALL
    SELECT 9585, '优惠券模板发布', 2600, 85, 'jst:marketing:coupon:publish', 'DEBT-3 补齐优惠券模板发布权限'
    UNION ALL
    SELECT 9586, '优惠券模板下架', 2600, 86, 'jst:marketing:coupon:offline', 'DEBT-3 补齐优惠券模板下架权限'
    UNION ALL
    SELECT 9587, '优惠券模板发放', 2600, 87, 'jst:marketing:coupon:issue', 'DEBT-3 补齐优惠券模板发放权限'
    UNION ALL
    SELECT 9588, '我的优惠券', 2600, 88, 'jst:marketing:coupon:my', 'DEBT-3 补齐我的优惠券权限'
    UNION ALL
    SELECT 9589, '领券中心领取', 2600, 89, 'jst:marketing:coupon:claim', 'DEBT-3 补齐领券中心领取权限'
    UNION ALL
    SELECT 9590, '优惠券选券预计算', 2600, 90, 'jst:marketing:coupon:select', 'DEBT-3 补齐优惠券选券预计算权限'
    UNION ALL
    SELECT 9600, '权益模板列表', 2600, 100, 'jst:marketing:rights:list', 'DEBT-3 补齐权益模板列表权限'
    UNION ALL
    SELECT 9601, '权益模板详情', 2600, 101, 'jst:marketing:rights:query', 'DEBT-3 补齐权益模板详情权限'
    UNION ALL
    SELECT 9602, '权益模板新增', 2600, 102, 'jst:marketing:rights:add', 'DEBT-3 补齐权益模板新增权限'
    UNION ALL
    SELECT 9603, '权益模板修改', 2600, 103, 'jst:marketing:rights:edit', 'DEBT-3 补齐权益模板修改权限'
    UNION ALL
    SELECT 9604, '权益模板删除', 2600, 104, 'jst:marketing:rights:remove', 'DEBT-3 补齐权益模板删除权限'
    UNION ALL
    SELECT 9605, '权益模板发布', 2600, 105, 'jst:marketing:rights:publish', 'DEBT-3 补齐权益模板发布权限'
    UNION ALL
    SELECT 9606, '权益模板下架', 2600, 106, 'jst:marketing:rights:offline', 'DEBT-3 补齐权益模板下架权限'
    UNION ALL
    SELECT 9607, '权益模板发放', 2600, 107, 'jst:marketing:rights:issue', 'DEBT-3 补齐权益模板发放权限'
    UNION ALL
    SELECT 9608, '我的权益', 2600, 108, 'jst:marketing:rights:my', 'DEBT-3 补齐我的权益权限'
    UNION ALL
    SELECT 9609, '权益自助核销', 2600, 109, 'jst:marketing:rights:apply', 'DEBT-3 补齐权益自助核销权限'
    UNION ALL
    SELECT 9610, '营销活动列表', 2600, 110, 'jst:marketing:campaign:list', 'DEBT-3 补齐营销活动列表权限'
    UNION ALL
    SELECT 9611, '营销活动详情', 2600, 111, 'jst:marketing:campaign:query', 'DEBT-3 补齐营销活动详情权限'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_menu m
    WHERE m.menu_id = seed.menu_id
       OR m.perms = seed.perms
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
    bind.role_id,
    menu.menu_id
FROM (
    -- jst_student
    SELECT @student_role_id AS role_id, 'jst:event:appointment:apply' AS perms
    UNION ALL SELECT @student_role_id, 'jst:event:appointment:cancel'
    UNION ALL SELECT @student_role_id, 'jst:event:appointment:my'
    UNION ALL SELECT @student_role_id, 'jst:event:appointment:query'
    UNION ALL SELECT @student_role_id, 'jst:points:mall:browse'
    UNION ALL SELECT @student_role_id, 'jst:points:mall:exchange:apply'
    UNION ALL SELECT @student_role_id, 'jst:points:mall:exchange:my'
    UNION ALL SELECT @student_role_id, 'jst:points:mall:exchange:cancel'
    UNION ALL SELECT @student_role_id, 'jst:points:mall:aftersale:apply'
    UNION ALL SELECT @student_role_id, 'jst:points:mall:aftersale:my'
    UNION ALL SELECT @student_role_id, 'jst:points:center:summary'
    UNION ALL SELECT @student_role_id, 'jst:points:center:levels'
    UNION ALL SELECT @student_role_id, 'jst:points:center:ledger'
    UNION ALL SELECT @student_role_id, 'jst:points:center:tasks'
    UNION ALL SELECT @student_role_id, 'jst:marketing:coupon:my'
    UNION ALL SELECT @student_role_id, 'jst:marketing:coupon:claim'
    UNION ALL SELECT @student_role_id, 'jst:marketing:coupon:select'
    UNION ALL SELECT @student_role_id, 'jst:marketing:rights:my'
    UNION ALL SELECT @student_role_id, 'jst:marketing:rights:apply'
    UNION ALL SELECT @student_role_id, 'jst:marketing:campaign:list'
    UNION ALL SELECT @student_role_id, 'jst:marketing:campaign:query'

    -- jst_channel
    UNION ALL SELECT @channel_role_id, 'jst:channel:withdraw:list'
    UNION ALL SELECT @channel_role_id, 'jst:channel:withdraw:query'
    UNION ALL SELECT @channel_role_id, 'jst:channel:withdraw:apply'
    UNION ALL SELECT @channel_role_id, 'jst:channel:withdraw:cancel'
    UNION ALL SELECT @channel_role_id, 'jst:channel:withdraw:my'
    UNION ALL SELECT @channel_role_id, 'jst:channel:dashboard:monthly'
    UNION ALL SELECT @channel_role_id, 'jst:channel:dashboard:students'
    UNION ALL SELECT @channel_role_id, 'jst:channel:dashboard:orders'
    UNION ALL SELECT @channel_role_id, 'jst:channel:dashboard:stats'
    UNION ALL SELECT @channel_role_id, 'jst:event:writeoff:scan'
    UNION ALL SELECT @channel_role_id, 'jst:event:writeoff:records'
    UNION ALL SELECT @channel_role_id, 'jst:event:writeoff:list'
    UNION ALL SELECT @channel_role_id, 'jst:points:center:summary'
    UNION ALL SELECT @channel_role_id, 'jst:points:center:levels'
    UNION ALL SELECT @channel_role_id, 'jst:points:center:ledger'
    UNION ALL SELECT @channel_role_id, 'jst:points:center:tasks'

    -- jst_platform_op / admin
    UNION ALL SELECT @platform_role_id, 'jst:channel:withdraw:list'
    UNION ALL SELECT @platform_role_id, 'jst:channel:withdraw:query'
    UNION ALL SELECT @platform_role_id, 'jst:channel:withdraw:audit'
    UNION ALL SELECT @platform_role_id, 'jst:channel:withdraw:execute'
    UNION ALL SELECT @platform_role_id, 'jst:event:writeoff:scan'
    UNION ALL SELECT @platform_role_id, 'jst:event:writeoff:records'
    UNION ALL SELECT @platform_role_id, 'jst:event:writeoff:list'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:goods:list'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:goods:add'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:goods:edit'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:goods:remove'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:goods:publish'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:exchange:list'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:exchange:ship'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:exchange:query'
    UNION ALL SELECT @platform_role_id, 'jst:points:mall:aftersale:audit'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:list'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:query'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:add'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:edit'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:remove'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:publish'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:offline'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:coupon:issue'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:list'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:query'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:add'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:edit'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:remove'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:publish'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:offline'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:rights:issue'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:campaign:list'
    UNION ALL SELECT @platform_role_id, 'jst:marketing:campaign:query'
) bind
JOIN sys_menu menu
    ON menu.perms = bind.perms
LEFT JOIN sys_role_menu rm
    ON rm.role_id = bind.role_id
   AND rm.menu_id = menu.menu_id
WHERE bind.role_id IS NOT NULL
  AND rm.role_id IS NULL;
