# PATCH-5 — region 字段标准化（省级字典 + admin 编辑入口 + 历史回填）

> 优先级：**🟡 P1** | 预估：**M（1 天）** | Agent：**Backend Agent + Web Admin Agent**（跨栈，建议同一个 agent 串做）
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 起因：DASHBOARD-METRICS §8.1/8.2/9.2 三连隐患——region 是自由文本（"北京/北京市/Beijing"碎片化）、旧渠道全是「未设置」、admin 端无编辑入口

---

## 一、问题与目标

**问题**：
1. `jst_channel.region` / `jst_channel_auth_apply.region` 现在是 `varchar(64)` 自由输入
2. 用户能填"北京"也能填"北京市"也能填"Beijing"，热力图打散成 N 行
3. 旧数据 region 为空，热力图大量"未设置"占主导
4. admin 没有"修正某渠道 region"的入口

**目标**：
1. 统一使用**省级 enum**（34 个省/直辖市/自治区/特别行政区，**国标 GB/T 2260 简称**）
2. 小程序申请表单 + admin 编辑表单都改下拉选择
3. 历史数据回填（智能匹配 + 默认值兜底）
4. 热力图用统一 region 后，"未设置"应大幅减少

---

## 二、必读上下文

1. `架构设计/ddl/99-migration-channel-region.sql`（前置 commit `3d17225` 已建字段）
2. `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/AdminSalesDashboardServiceImpl.java`（dashboard 热力图 SQL，了解 region 怎么被消费）
3. `RuoYi-Vue/ruoyi-ui/src/views/jst/channel-auth/index.vue`（admin 渠道认证页，已有 region 展示，**本卡加编辑入口**）
4. `jst-uniapp/pages-sub/channel/apply-form.vue`（小程序申请表单，已有 region 文本输入，**本卡改下拉**）
5. 字典框架参考：`架构设计/ddl/99-migration-admin-polish-dict.sql`（看若依字典 SQL 格式）

---

## 三、四步走

### Step 1：建省级字典

**新建文件**：`架构设计/ddl/99-migration-region-dict.sql`

```sql
-- jst_region_province 字典：34 项 GB/T 2260 国标简称
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

-- 验证
SELECT COUNT(*) AS cnt FROM sys_dict_data WHERE dict_type='jst_region_province' AND status='0';
-- 期望：34
```

**执行**：
```bash
mysql -u root -p123456 jst < 架构设计/ddl/99-migration-region-dict.sql
```

---

### Step 2：历史数据回填脚本

**新建文件**：`架构设计/ddl/99-migration-region-backfill.sql`

```sql
-- 历史数据回填：自由文本 region → 国标简称
SET NAMES utf8mb4;

-- 备份
CREATE TABLE IF NOT EXISTS _backup_jst_channel_region_20260418 AS
SELECT channel_id, region FROM jst_channel WHERE region IS NOT NULL AND region != '';
CREATE TABLE IF NOT EXISTS _backup_jst_channel_auth_apply_region_20260418 AS
SELECT id, region FROM jst_channel_auth_apply WHERE region IS NOT NULL AND region != '';

-- 智能匹配（覆盖常见输入变体）
-- 直辖市
UPDATE jst_channel SET region='beijing'   WHERE region IN ('北京','北京市','beijing','BJ','Beijing');
UPDATE jst_channel SET region='shanghai'  WHERE region IN ('上海','上海市','shanghai','SH','Shanghai');
UPDATE jst_channel SET region='tianjin'   WHERE region IN ('天津','天津市','tianjin','TJ','Tianjin');
UPDATE jst_channel SET region='chongqing' WHERE region IN ('重庆','重庆市','chongqing','CQ','Chongqing');

-- 各省（省/省份/拼音/英文）
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

-- 同样的 UPDATE 全部跑一次 jst_channel_auth_apply（替换表名即可）
-- ... (重复 33 条 UPDATE，把 jst_channel 换成 jst_channel_auth_apply) ...

-- 兜底：仍然不在字典 value 里的，标记为 NULL（让前端"未设置"逻辑生效，而不是让脏数据继续污染热力图）
UPDATE jst_channel SET region = NULL
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');

UPDATE jst_channel_auth_apply SET region = NULL
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');

-- 验证：脏数据应为 0
SELECT COUNT(*) AS dirty_channel
  FROM jst_channel
 WHERE region IS NOT NULL
   AND region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province');
-- 期望：0
```

执行：`mysql -u root -p123456 jst < 架构设计/ddl/99-migration-region-backfill.sql`

回滚方案（**写进 SQL 注释**）：
```sql
-- 回滚：UPDATE jst_channel c JOIN _backup_jst_channel_region_20260418 b ON c.channel_id=b.channel_id SET c.region = b.region;
```

---

### Step 3：admin 编辑入口

**文件**：`RuoYi-Vue/ruoyi-ui/src/views/jst/channel-auth/index.vue`（已有 region 展示）

加 admin 编辑能力：
- 在渠道认证详情/列表行尾加「编辑地区」小按钮（仅 admin/jst_operator 可见）
- 点击弹一个 `<el-dialog>`，里面 `<el-select v-model="form.region">` 用 `getDicts('jst_region_province')` 加载选项
- 提交调一个**新接口**：

**新接口（后端）**：

文件 `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/controller/ChannelAuthApplyController.java`（路径以实际为准，找现有 `@PutMapping` 套路抄）

```java
@PutMapping("/admin/{id}/region")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
@Log(title = "渠道地区修正", businessType = BusinessType.UPDATE)
public AjaxResult updateRegion(@PathVariable Long id, @RequestBody Map<String, String> body) {
    String region = body.get("region");
    if (StringUtils.isBlank(region)) return AjaxResult.error("region 不能为空");
    // 校验在字典内（防绕过前端脏数据）
    if (!regionDictService.isValid(region)) return AjaxResult.error("region 不在字典范围");
    channelAuthApplyService.updateRegion(id, region);
    return AjaxResult.success();
}
```

字典校验可以简单做：
```java
@Service
public class RegionDictService {
    @Autowired private ISysDictTypeService dictTypeService;
    public boolean isValid(String value) {
        return dictTypeService.selectDictDataByType("jst_region_province").stream()
            .anyMatch(d -> d.getDictValue().equals(value));
    }
}
```
（位置：`jst-organizer/.../service/RegionDictService.java`）

Service 实现 + Mapper 写两条 update（一条改 `jst_channel_auth_apply.region`，一条同步 `jst_channel.region`，因为审核通过后两边都要一致）。

---

### Step 4：小程序 + admin 申请表单改下拉

**文件 1**：`jst-uniapp/pages-sub/channel/apply-form.vue`（已有 region 文本输入）

把 `<input type="text" v-model="form.region">` 改成 picker：
```vue
<picker mode="selector" :range="regionOptions" range-key="dictLabel" @change="onRegionChange">
  <view class="picker-display">{{ form.regionLabel || '请选择所在省份 *' }}</view>
</picker>
```

在 onLoad 拉字典：
```js
async onLoad() {
  // ... 已有
  const res = await getDicts('jst_region_province')
  this.regionOptions = res.data || []
}
onRegionChange(e) {
  const idx = e.detail.value
  this.form.region = this.regionOptions[idx].dictValue   // 落 'beijing'
  this.form.regionLabel = this.regionOptions[idx].dictLabel  // 显示 '北京'
}
```

提交时仍然只传 `region` 字段。

**文件 2**：admin 端如有渠道申请表单（grep `jst_channel_auth_apply` 找页面），同步改下拉。

---

### Step 5：dashboard 热力图前端展示用 dict label

**文件**：`RuoYi-Vue/ruoyi-ui/src/views/jst/sales/dashboard.vue`

热力图后端返 `region: 'beijing'`，前端展示要翻成"北京"：
```js
import { getDicts } from '@/api/system/dict/data'

data() { return { regionDict: [] } },
async created() {
  const res = await getDicts('jst_region_province')
  this.regionDict = res.data || []
},
methods: {
  regionLabel(v) {
    if (!v) return '未设置'
    const item = this.regionDict.find(d => d.dictValue === v)
    return item ? item.dictLabel : v
  }
}
```

热力图 yAxis 渲染调用 `regionLabel(row.region)`。

---

## 四、验证

```bash
# 编译
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl jst-organizer,ruoyi-admin -am clean compile -DskipTests
# 前端
cd ruoyi-ui && npm run build:prod

# DB 验证
mysql -u root -p123456 jst -e "
SELECT COUNT(*) AS dict_count FROM sys_dict_data WHERE dict_type='jst_region_province' AND status='0';
SELECT region, COUNT(*) FROM jst_channel WHERE region IS NOT NULL GROUP BY region;
SELECT COUNT(*) AS dirty FROM jst_channel WHERE region NOT IN (SELECT dict_value FROM sys_dict_data WHERE dict_type='jst_region_province') AND region IS NOT NULL;
"
# 期望：dict_count=34；dirty=0
```

### 浏览器手测

1. **小程序申请表单**：picker 能弹出 34 选项 → 选"北京" → 提交 → 后端落"beijing"
2. **admin 渠道认证页**：编辑按钮 → 弹 dialog → 改成"上海" → 提交 → 列表展示更新
3. **admin 销售看板热力图**：region 列从"beijing"显示成"北京"，老数据没"未设置"满屏

---

## 五、DoD

- [ ] 字典 SQL 已执行，dict_count=34
- [ ] 回填 SQL 已执行 + 备份表存在 + dirty=0
- [ ] admin 编辑接口 + 字典校验
- [ ] 小程序申请表单改 picker
- [ ] admin 渠道认证页加编辑按钮
- [ ] dashboard 热力图前端字典翻译
- [ ] mvn + npm build 都绿
- [ ] 3 个浏览器手测过
- [ ] commit 系列：
  - `feat(channel): PATCH-5 region 省级字典 + 回填脚本`
  - `feat(channel): PATCH-5 admin 渠道地区编辑入口`
  - `feat(mp): PATCH-5 申请表单地区改下拉`

---

## 六、红线

- ❌ 回填 SQL **必须先备份**才能改
- ❌ 字典 value 必须 ASCII（避免 SQL 注入和编码问题）
- ❌ admin 编辑接口必须**校验 region 在字典内**（防绕过前端）
- ❌ 不许把 region 拓展到市/区级（本卡只到省级，市级是另一张卡的事）
- ❌ 不许"顺手"删除 dashboard 的 jst_user_address.province fallback——回填后留它兜底没坏处，删它要单独决策

---

## 七、派发附言

如果发现某些历史脏数据（比如填了"广州"这种市级名），回填脚本会把它置 NULL → 显示"未设置"。这是**预期行为**，不是 bug。后续运营发现可以用 admin 编辑入口手动改成"广东"。

派发时间：2026-04-18 | 主 Agent：竞赛通架构师
