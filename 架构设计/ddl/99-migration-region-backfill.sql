-- =====================================================
-- PATCH-5: 历史数据回填 — 自由文本 region → 国标简称
-- 前置：99-migration-region-dict.sql 必须已执行
-- 回滚：
--   UPDATE jst_channel c JOIN _backup_jst_channel_region_20260418 b
--     ON c.channel_id = b.channel_id SET c.region = b.region;
--   UPDATE jst_channel_auth_apply a JOIN _backup_jst_channel_auth_apply_region_20260418 b
--     ON a.apply_id = b.apply_id SET a.region = b.region;
-- =====================================================
SET NAMES utf8mb4;

-- 1) 备份（只备份非空行，便于回滚时精确还原）
CREATE TABLE IF NOT EXISTS _backup_jst_channel_region_20260418 AS
SELECT channel_id, region FROM jst_channel WHERE region IS NOT NULL AND region != '';

CREATE TABLE IF NOT EXISTS _backup_jst_channel_auth_apply_region_20260418 AS
SELECT apply_id, region FROM jst_channel_auth_apply WHERE region IS NOT NULL AND region != '';

-- 2) 智能匹配：jst_channel
-- 直辖市
UPDATE jst_channel SET region='beijing'   WHERE region IN ('北京','北京市','beijing','BJ','Beijing');
UPDATE jst_channel SET region='shanghai'  WHERE region IN ('上海','上海市','shanghai','SH','Shanghai');
UPDATE jst_channel SET region='tianjin'   WHERE region IN ('天津','天津市','tianjin','TJ','Tianjin');
UPDATE jst_channel SET region='chongqing' WHERE region IN ('重庆','重庆市','chongqing','CQ','Chongqing');
-- 省
UPDATE jst_channel SET region='hebei'        WHERE region IN ('河北','河北省','hebei','Hebei');
UPDATE jst_channel SET region='shanxi'       WHERE region IN ('山西','山西省','shanxi','Shanxi');
UPDATE jst_channel SET region='liaoning'     WHERE region IN ('辽宁','辽宁省','liaoning','Liaoning');
UPDATE jst_channel SET region='jilin'        WHERE region IN ('吉林','吉林省','jilin','Jilin');
UPDATE jst_channel SET region='heilongjiang' WHERE region IN ('黑龙江','黑龙江省','heilongjiang','Heilongjiang');
UPDATE jst_channel SET region='jiangsu'      WHERE region IN ('江苏','江苏省','jiangsu','Jiangsu');
UPDATE jst_channel SET region='zhejiang'     WHERE region IN ('浙江','浙江省','zhejiang','Zhejiang');
UPDATE jst_channel SET region='anhui'        WHERE region IN ('安徽','安徽省','anhui','Anhui');
UPDATE jst_channel SET region='fujian'       WHERE region IN ('福建','福建省','fujian','Fujian');
UPDATE jst_channel SET region='jiangxi'      WHERE region IN ('江西','江西省','jiangxi','Jiangxi');
UPDATE jst_channel SET region='shandong'     WHERE region IN ('山东','山东省','shandong','Shandong');
UPDATE jst_channel SET region='henan'        WHERE region IN ('河南','河南省','henan','Henan');
UPDATE jst_channel SET region='hubei'        WHERE region IN ('湖北','湖北省','hubei','Hubei');
UPDATE jst_channel SET region='hunan'        WHERE region IN ('湖南','湖南省','hunan','Hunan');
UPDATE jst_channel SET region='guangdong'    WHERE region IN ('广东','广东省','guangdong','Guangdong');
UPDATE jst_channel SET region='hainan'       WHERE region IN ('海南','海南省','hainan','Hainan');
UPDATE jst_channel SET region='sichuan'      WHERE region IN ('四川','四川省','sichuan','Sichuan');
UPDATE jst_channel SET region='guizhou'      WHERE region IN ('贵州','贵州省','guizhou','Guizhou');
UPDATE jst_channel SET region='yunnan'       WHERE region IN ('云南','云南省','yunnan','Yunnan');
UPDATE jst_channel SET region='shaanxi'      WHERE region IN ('陕西','陕西省','shaanxi','Shaanxi');
UPDATE jst_channel SET region='gansu'        WHERE region IN ('甘肃','甘肃省','gansu','Gansu');
UPDATE jst_channel SET region='qinghai'      WHERE region IN ('青海','青海省','qinghai','Qinghai');
-- 自治区
UPDATE jst_channel SET region='neimenggu' WHERE region IN ('内蒙古','内蒙古自治区','neimenggu','Inner Mongolia');
UPDATE jst_channel SET region='guangxi'   WHERE region IN ('广西','广西壮族自治区','guangxi','Guangxi');
UPDATE jst_channel SET region='xizang'    WHERE region IN ('西藏','西藏自治区','xizang','Tibet');
UPDATE jst_channel SET region='ningxia'   WHERE region IN ('宁夏','宁夏回族自治区','ningxia','Ningxia');
UPDATE jst_channel SET region='xinjiang'  WHERE region IN ('新疆','新疆维吾尔自治区','xinjiang','Xinjiang');
-- 特别行政区
UPDATE jst_channel SET region='xianggang' WHERE region IN ('香港','香港特别行政区','xianggang','Hong Kong','HK');
UPDATE jst_channel SET region='aomen'     WHERE region IN ('澳门','澳门特别行政区','aomen','Macau','MO');
UPDATE jst_channel SET region='taiwan'    WHERE region IN ('台湾','台湾省','taiwan','Taiwan','TW');

-- 3) 智能匹配：jst_channel_auth_apply
UPDATE jst_channel_auth_apply SET region='beijing'   WHERE region IN ('北京','北京市','beijing','BJ','Beijing');
UPDATE jst_channel_auth_apply SET region='shanghai'  WHERE region IN ('上海','上海市','shanghai','SH','Shanghai');
UPDATE jst_channel_auth_apply SET region='tianjin'   WHERE region IN ('天津','天津市','tianjin','TJ','Tianjin');
UPDATE jst_channel_auth_apply SET region='chongqing' WHERE region IN ('重庆','重庆市','chongqing','CQ','Chongqing');
UPDATE jst_channel_auth_apply SET region='hebei'        WHERE region IN ('河北','河北省','hebei','Hebei');
UPDATE jst_channel_auth_apply SET region='shanxi'       WHERE region IN ('山西','山西省','shanxi','Shanxi');
UPDATE jst_channel_auth_apply SET region='liaoning'     WHERE region IN ('辽宁','辽宁省','liaoning','Liaoning');
UPDATE jst_channel_auth_apply SET region='jilin'        WHERE region IN ('吉林','吉林省','jilin','Jilin');
UPDATE jst_channel_auth_apply SET region='heilongjiang' WHERE region IN ('黑龙江','黑龙江省','heilongjiang','Heilongjiang');
UPDATE jst_channel_auth_apply SET region='jiangsu'      WHERE region IN ('江苏','江苏省','jiangsu','Jiangsu');
UPDATE jst_channel_auth_apply SET region='zhejiang'     WHERE region IN ('浙江','浙江省','zhejiang','Zhejiang');
UPDATE jst_channel_auth_apply SET region='anhui'        WHERE region IN ('安徽','安徽省','anhui','Anhui');
UPDATE jst_channel_auth_apply SET region='fujian'       WHERE region IN ('福建','福建省','fujian','Fujian');
UPDATE jst_channel_auth_apply SET region='jiangxi'      WHERE region IN ('江西','江西省','jiangxi','Jiangxi');
UPDATE jst_channel_auth_apply SET region='shandong'     WHERE region IN ('山东','山东省','shandong','Shandong');
UPDATE jst_channel_auth_apply SET region='henan'        WHERE region IN ('河南','河南省','henan','Henan');
UPDATE jst_channel_auth_apply SET region='hubei'        WHERE region IN ('湖北','湖北省','hubei','Hubei');
UPDATE jst_channel_auth_apply SET region='hunan'        WHERE region IN ('湖南','湖南省','hunan','Hunan');
UPDATE jst_channel_auth_apply SET region='guangdong'    WHERE region IN ('广东','广东省','guangdong','Guangdong');
UPDATE jst_channel_auth_apply SET region='hainan'       WHERE region IN ('海南','海南省','hainan','Hainan');
UPDATE jst_channel_auth_apply SET region='sichuan'      WHERE region IN ('四川','四川省','sichuan','Sichuan');
UPDATE jst_channel_auth_apply SET region='guizhou'      WHERE region IN ('贵州','贵州省','guizhou','Guizhou');
UPDATE jst_channel_auth_apply SET region='yunnan'       WHERE region IN ('云南','云南省','yunnan','Yunnan');
UPDATE jst_channel_auth_apply SET region='shaanxi'      WHERE region IN ('陕西','陕西省','shaanxi','Shaanxi');
UPDATE jst_channel_auth_apply SET region='gansu'        WHERE region IN ('甘肃','甘肃省','gansu','Gansu');
UPDATE jst_channel_auth_apply SET region='qinghai'      WHERE region IN ('青海','青海省','qinghai','Qinghai');
UPDATE jst_channel_auth_apply SET region='neimenggu' WHERE region IN ('内蒙古','内蒙古自治区','neimenggu','Inner Mongolia');
UPDATE jst_channel_auth_apply SET region='guangxi'   WHERE region IN ('广西','广西壮族自治区','guangxi','Guangxi');
UPDATE jst_channel_auth_apply SET region='xizang'    WHERE region IN ('西藏','西藏自治区','xizang','Tibet');
UPDATE jst_channel_auth_apply SET region='ningxia'   WHERE region IN ('宁夏','宁夏回族自治区','ningxia','Ningxia');
UPDATE jst_channel_auth_apply SET region='xinjiang'  WHERE region IN ('新疆','新疆维吾尔自治区','xinjiang','Xinjiang');
UPDATE jst_channel_auth_apply SET region='xianggang' WHERE region IN ('香港','香港特别行政区','xianggang','Hong Kong','HK');
UPDATE jst_channel_auth_apply SET region='aomen'     WHERE region IN ('澳门','澳门特别行政区','aomen','Macau','MO');
UPDATE jst_channel_auth_apply SET region='taiwan'    WHERE region IN ('台湾','台湾省','taiwan','Taiwan','TW');

-- 4) 兜底：仍然不在字典内的置 NULL（防脏数据继续污染）
UPDATE jst_channel SET region = NULL
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');

UPDATE jst_channel_auth_apply SET region = NULL
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');

-- 5) 验证：dirty 应为 0
SELECT COUNT(*) AS dirty_channel
  FROM jst_channel
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');

SELECT COUNT(*) AS dirty_apply
  FROM jst_channel_auth_apply
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');
