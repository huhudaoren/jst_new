-- =====================================================
-- 销售管理系统参数（admin 在系统参数页可调）
-- 关联 spec §5.7 / 关联 plan-01 Task 4
-- 命名空间：jst.sales.*（与 Java @Value("${jst.sales.xxx:default}") 对应）
--
-- ⚠️ 一次性迁移文件：config_key 是唯一键，重跑会因 Duplicate 报错。
-- 回滚命令：
--   DELETE FROM sys_config WHERE config_key LIKE 'jst.sales.%';
-- =====================================================
SET NAMES utf8mb4;

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark) VALUES
('销售提成-售后期天数',           'jst.sales.aftersales_days',              '7',     'Y', 'admin', NOW(), '订单付款后多少天无退款，提成才入账'),
('销售-财务过渡期月数',           'jst.sales.transition_months',            '3',     'Y', 'admin', NOW(), '离职后已计提提成结算的财务过渡期'),
('销售-离职申请到执行的最大间隔天数', 'jst.sales.resign_apply_max_days',      '7',     'Y', 'admin', NOW(), '提交离职申请到实际离职日的最大间隔'),
('销售-预录入有效期天数',         'jst.sales.pre_register_valid_days',      '90',    'Y', 'admin', NOW(), '预录入未匹配自动失效'),
('销售-预录入最多续期次数',       'jst.sales.pre_register_renew_max',       '2',     'Y', 'admin', NOW(), '销售可手动续期次数上限'),
('渠道分销-默认费率',             'jst.sales.distribution.default_rate',    '0.0150','Y', 'admin', NOW(), '渠道邀请关系未单独配费率时使用'),
('单笔订单总分润上限',            'jst.sales.order.max_total_share_rate',   '0.1500','Y', 'admin', NOW(), '销售提成+分销+返点 总和不超过订单实付的此比例'),
('销售-高额提成告警阈值',         'jst.sales.high_amount_alert_threshold',  '5000',  'Y', 'admin', NOW(), '单条 ledger 金额超此值触发 admin 告警');
