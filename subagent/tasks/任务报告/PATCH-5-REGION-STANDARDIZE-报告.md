# PATCH-5 region 字段标准化 — 任务报告

> 派发时间：2026-04-18 | 完成时间：2026-04-18 | Agent：全栈（Backend + Web Admin + MP）
> 分支：main | 三 commit 已落地，**未 push**

---

## 一、产出概览

| Step | 内容 | 状态 |
|---|---|---|
| Step 1 | 字典 SQL 新建 + 执行（34 项） | ✅ |
| Step 2 | 回填 SQL 新建 + 执行（含备份、兜底 NULL） | ✅ |
| Step 3 | 后端 PUT {applyId}/region 端点 + 字典校验 Service + Mapper/XML/Service/Controller | ✅ |
| Step 4 | 小程序 apply-form picker + admin channel-auth 编辑入口 | ✅ |
| Step 5 | dashboard.vue 热力图 + 热力表格 region 字典翻译 | ✅ |

---

## 二、逐步执行结果

### Step 1：字典（`99-migration-region-dict.sql`）

```bash
mysql -u root -p123456 jst < 架构设计/ddl/99-migration-region-dict.sql
# 输出：cnt=34 ✅
```

字典类型 `jst_region_province`，34 项 ASCII 拼音 value：
- 4 直辖市（primary 色）
- 22 省（default）
- 5 自治区（warning）
- 3 特别行政区（info）

### Step 2：回填（`99-migration-region-backfill.sql`）

**红线合规**：先备份再 UPDATE。
- 备份表 `_backup_jst_channel_region_20260418`（channel_id + region）
- 备份表 `_backup_jst_channel_auth_apply_region_20260418`（apply_id + region；PK 名由文档的 `id` 修正为实际 `apply_id`）
- 智能匹配 33 条 UPDATE × 2 表，覆盖「北京 / 北京市 / beijing / BJ / Beijing」等常见变体
- 兜底：仍不在字典 value 内的置 NULL

```bash
mysql -u root -p123456 jst < 架构设计/ddl/99-migration-region-backfill.sql
# 输出：dirty_channel=0, dirty_apply=0 ✅
```

**当前库态**：`jst_channel` 7 条全部 NULL、`jst_channel_auth_apply` 4 条全部 NULL（本来就没有历史脏数据），备份表 0 行；回滚脚本写在 SQL 注释内。

### Step 3：后端编辑端点

**新增文件**
- `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/RegionDictService.java`

**改动文件**
- `controller/ChannelAuthApplyAdminController.java` — 加 `PUT /{applyId}/region` 方法，`@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")`，调用 `RegionDictService.isValid` 校验，再 delegate 到 Service
- `service/ChannelAuthApplyService.java` — 接口加 `updateRegion(Long,String)`
- `service/impl/ChannelAuthApplyServiceImpl.java` — 实现 `updateRegion`：`@Transactional` + `@OperateLog`，调 mapper 改 apply 行，若 `channelId != null` 同步改 `jst_channel.region`
- `mapper/ChannelAuthApplyMapperExt.java` + `resources/mapper/organizer/ChannelAuthApplyMapperExt.xml` — 加 `updateApplyRegion`

**字典校验**：`RegionDictService.isValid` 走 `ISysDictTypeService.selectDictDataByType("jst_region_province")`，在字典内才放行；Controller 先校 `StringUtils.isBlank` 返 400 语义，再校字典。

**编译验证**
```bash
mvn -pl jst-organizer,ruoyi-admin -am clean compile -DskipTests  # BUILD SUCCESS
mvn -pl jst-organizer,... -am install -DskipTests                # 为 spring-boot:run 更新 ~/.m2 jar
```

**运行态验证**（admin token 同 admin-tests.http A1 的 admin/admin123 方式）
```
PUT /jst/organizer/channel/apply/6003/region {"region":"beijing"}
→ {"msg":"操作成功","code":200} ✅
PUT ... {"region":"atlantis"}
→ {"msg":"region 不在字典范围（jst_region_province）","code":500} ✅
PUT ... {"region":""}
→ {"msg":"region 不能为空","code":500} ✅
```

DB 侧：apply 6003 + 其关联 channel 9205 同步落为 `beijing`，验证后已重置 NULL。

### Step 4：前端三处改造

**小程序 `jst-uniapp/pages-sub/channel/apply-form.vue`**
- region 字段：`u--input` → `<picker mode="selector">`
- data 新增 `regionOptions` + `form.regionLabel`（显示用），`form.region` 仍存 dict_value（落库值不变）
- `onLoad` 调用 `loadRegionDict()`；edit 模式 `loadRejectedApply` 回填后调 `syncRegionLabel()` 兼容字典后到达
- 提交 body 不变（只传 `region` 拼音）

**小程序 `jst-uniapp/api/dict.js`** — 新加 `getDict(dictType)` 通用拉取（走 `GET /jst/wx/dict/{dictType}`，`WxDictController` 已有 `@Anonymous` 白名单，`NoticeServiceImpl.isAllowedDictType` 只需 `jst_` 前缀）。

**管理端 `views/jst/channel-auth/index.vue`**
- `dicts: ['jst_region_province']` 注入
- region 列用 `<dict-tag :options="dict.type.jst_region_province" :value="row.region">`；空值显示灰化「未设置」
- 抽屉区同样用 `dict-tag`
- 操作列加「编辑地区」按钮（`v-if="canEditRegion"`，基于 `$store.getters.roles ∈ [admin, jst_operator]`）
- 编辑弹窗：`el-select filterable` + `el-option v-for dict.type.jst_region_province`，提交调 `updateChannelAuthRegion`

**管理端 `api/admin/channel-auth.js`** — 新加 `updateChannelAuthRegion(applyId, region)`。

### Step 5：dashboard.vue 字典翻译

- data 加 `regionDict: []`
- `created()` 追加 `getDicts('jst_region_province')`
- `methods.regionLabel(value)` 字典查找 + 兜底（空 → 「未设置」、未匹配 → 返回原值）
- `renderHeatmapChart`：`yAxis.data` 用 `regionAxis.map(r => this.regionLabel(r))` 渲染中文；tooltip 也用 `regionLabel`
- top 表格「地区」列用 slot `{{ regionLabel(scope.row.region) }}`
- **未动** `getHeatmapRegionAxis()` 里 `jst_user_address.province` 相关逻辑（红线合规）

---

## 三、编译 / 构建结果

| 命令 | 结果 |
|---|---|
| `mvn -pl jst-organizer,ruoyi-admin -am clean compile -DskipTests` | ✅ BUILD SUCCESS |
| `mvn -pl jst-organizer,jst-common,... -am install -DskipTests` | ✅ BUILD SUCCESS（更新 ~/.m2 jar 后 backend 重启识别新端点） |
| `cd ruoyi-ui && npm run build:prod` | ✅ Build complete |

---

## 四、提交记录

```
e67c6bb feat(mp): PATCH-5 申请表单地区改下拉 + dashboard 字典翻译
e61f912 feat(channel): PATCH-5 admin 渠道地区编辑入口
52f5171 feat(channel): PATCH-5 region 省级字典 + 回填脚本
```

未 push（按任务要求）。

---

## 五、DoD 清单复核

- [x] 字典 SQL 已执行，dict_count=34
- [x] 回填 SQL 已执行 + 备份表存在 + dirty=0
- [x] admin 编辑接口 + 字典校验 — curl 三态（valid / invalid / empty）全过
- [x] 小程序申请表单改 picker（edit 模式回显兼容）
- [x] admin 渠道认证页加编辑按钮 + dict-tag 翻译
- [x] dashboard 热力图 + 热力表格字典翻译（未动 jst_user_address.province fallback）
- [x] mvn 全编译绿 + `npm run build:prod` 绿
- [x] 3 个 commit 按规范落地

---

## 六、红线合规

| 红线 | 合规情况 |
|---|---|
| 回填 SQL 必须先备份 | ✅ CREATE TABLE IF NOT EXISTS _backup_* |
| 字典 value 必须 ASCII | ✅ 34 项全部拼音 |
| admin 编辑接口校验 region 在字典内 | ✅ RegionDictService.isValid |
| 不许拓展到市/区级 | ✅ 只做省级 |
| 不许删 dashboard 的 jst_user_address.province fallback | ✅ 未动 |

---

## 七、隐患与后续建议

| 优先级 | 项 | 说明 |
|---|---|---|
| P3 | `jst_channel_auth_apply` PK 名在任务卡示例 SQL 中写的是 `id`，实际是 `apply_id`。本次已修正回填脚本但未改任务卡；若后续有脚本参考需留意。 |
| P3 | `RegionDictService.isValid` 每次调 `dictTypeService.selectDictDataByType`，若依该接口底层走 Redis 缓存，性能 OK；若热点需要再评估 `@Cacheable`。 |
| P3 | 当前小程序 picker 无搜索过滤（34 项纵向滚动）。若产品反馈难选，可升级为 uView `u-picker` 或 WeUI `<picker-view>`；非本卡范围。 |
| P3 | 回填脚本兜底将非字典值置 NULL，该策略属 CCB 预期；运营可用 admin 编辑入口手动改。 |
| P2 | 未做浏览器/真机自动化手测（环境非图形化）。代码级逻辑已覆盖：接口 3 态 curl 通过 + 前端 build:prod 无警告；小程序 picker 视觉/交互需真机走查。 |

---

报告完成时间：2026-04-18 23:10（本地）
