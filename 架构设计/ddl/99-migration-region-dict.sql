-- =====================================================
-- PATCH-5: 省级行政区字典 jst_region_province
-- 34 项 GB/T 2260 国标简称（ASCII 拼音）
-- =====================================================
SET NAMES utf8mb4;

-- 1) 字典类型
INSERT IGNORE INTO sys_dict_type (dict_name, dict_type, status, create_by, remark)
VALUES ('省级行政区', 'jst_region_province', '0', 'system', '渠道地区标准化，34 项国标简称');

-- 2) 字典数据
INSERT IGNORE INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, remark) VALUES
(1,'北京','beijing','jst_region_province','','primary','N','0','system','直辖市'),
(2,'天津','tianjin','jst_region_province','','primary','N','0','system','直辖市'),
(3,'上海','shanghai','jst_region_province','','primary','N','0','system','直辖市'),
(4,'重庆','chongqing','jst_region_province','','primary','N','0','system','直辖市'),
(5,'河北','hebei','jst_region_province','','default','N','0','system',''),
(6,'山西','shanxi','jst_region_province','','default','N','0','system',''),
(7,'辽宁','liaoning','jst_region_province','','default','N','0','system',''),
(8,'吉林','jilin','jst_region_province','','default','N','0','system',''),
(9,'黑龙江','heilongjiang','jst_region_province','','default','N','0','system',''),
(10,'江苏','jiangsu','jst_region_province','','default','N','0','system',''),
(11,'浙江','zhejiang','jst_region_province','','default','N','0','system',''),
(12,'安徽','anhui','jst_region_province','','default','N','0','system',''),
(13,'福建','fujian','jst_region_province','','default','N','0','system',''),
(14,'江西','jiangxi','jst_region_province','','default','N','0','system',''),
(15,'山东','shandong','jst_region_province','','default','N','0','system',''),
(16,'河南','henan','jst_region_province','','default','N','0','system',''),
(17,'湖北','hubei','jst_region_province','','default','N','0','system',''),
(18,'湖南','hunan','jst_region_province','','default','N','0','system',''),
(19,'广东','guangdong','jst_region_province','','default','N','0','system',''),
(20,'海南','hainan','jst_region_province','','default','N','0','system',''),
(21,'四川','sichuan','jst_region_province','','default','N','0','system',''),
(22,'贵州','guizhou','jst_region_province','','default','N','0','system',''),
(23,'云南','yunnan','jst_region_province','','default','N','0','system',''),
(24,'陕西','shaanxi','jst_region_province','','default','N','0','system',''),
(25,'甘肃','gansu','jst_region_province','','default','N','0','system',''),
(26,'青海','qinghai','jst_region_province','','default','N','0','system',''),
(27,'内蒙古','neimenggu','jst_region_province','','warning','N','0','system','自治区'),
(28,'广西','guangxi','jst_region_province','','warning','N','0','system','自治区'),
(29,'西藏','xizang','jst_region_province','','warning','N','0','system','自治区'),
(30,'宁夏','ningxia','jst_region_province','','warning','N','0','system','自治区'),
(31,'新疆','xinjiang','jst_region_province','','warning','N','0','system','自治区'),
(32,'香港','xianggang','jst_region_province','','info','N','0','system','特别行政区'),
(33,'澳门','aomen','jst_region_province','','info','N','0','system','特别行政区'),
(34,'台湾','taiwan','jst_region_province','','info','N','0','system','特别行政区');

-- 验证：期望 34
SELECT COUNT(*) AS cnt FROM sys_dict_data WHERE dict_type='jst_region_province' AND status='0';
