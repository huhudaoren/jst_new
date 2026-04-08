-- =====================================================================
-- 文件名：97-disable-captcha-dev.sql
-- 用途  : 开发期关闭若依登录验证码,便于 .http 自动化测试 admin 登录
-- 适用  : DEV/TEST 环境,生产严禁
-- =====================================================================

UPDATE sys_config
SET config_value = 'false',
    update_time = NOW()
WHERE config_key = 'sys.account.captchaEnabled';

-- 验证: 应返回 1 行 false
SELECT config_key, config_value FROM sys_config WHERE config_key = 'sys.account.captchaEnabled';

-- =====================================================================
-- 跑完后重启 ruoyi-admin (若依配置有缓存),然后 admin/admin123 即可不带验证码登录
-- 生产恢复: UPDATE sys_config SET config_value='true' WHERE config_key='sys.account.captchaEnabled';
-- =====================================================================
