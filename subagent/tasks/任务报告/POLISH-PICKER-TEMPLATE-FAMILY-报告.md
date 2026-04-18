# POLISH-PICKER-TEMPLATE-FAMILY 任务报告 — 4 新 Template Picker + EntityBriefController 扩展

> 执行时间：2026-04-18
> 执行人：Web Admin Agent
> 任务卡：`subagent/tasks/POLISH-PICKER-TEMPLATE-FAMILY.md`
> 工作区：`D:\coding\jst_v1`
> 代码主目录：`D:\coding\jst_v1\RuoYi-Vue`

---

## 一、任务目标与本次边界

本卡目标是补齐管理端缺失的 4 类模板实体 Picker 基础设施，供下一张 `POLISH-PICKER-APPLY` 任务卡把它们真正贴到页面上使用。

本次严格控制在“基础设施补齐”边界内，已完成：

1. 后端统一实体选择器 `EntityBriefController` 扩展 4 类模板的 `search / brief` 支持。
2. 前端新增 4 个薄包装 RelationPicker 组件，风格严格对齐 `ChannelPicker.vue`。
3. `entityRouteMap.js` 增加 4 类模板的跳转映射，并补上 query-fallback 机制。
4. `main.js` 增加 4 个 Picker 的全局注册，保持与现有 7 个 Picker 家族一致。
5. `mvn ... compile` 与 `npm run build:prod` 双绿验证。

本次明确没有做：

1. 没有改任何页面去实际使用这 4 个新 Picker。
2. 没有改 `BasePicker.vue`。
3. 没有改任何业务页面的表单结构、字段绑定或交互。
4. 没有新增 npm / maven 依赖。
5. 没有改数据库结构、权限注解、Controller 签名。

---

## 二、前置核查结论

### 2.1 已读上下文

执行前已读取并核对：

1. `CLAUDE.md`
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`
3. `subagent/tasks/POLISH-PICKER-TEMPLATE-FAMILY.md`
4. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/BasePicker.vue`
5. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/ChannelPicker.vue`
6. `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`
7. `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`
8. 目标模板页与相关路由/权限实现

### 2.2 现有基线确认

确认了以下实现事实：

1. 现有 Picker 家族是“`BasePicker.vue` + 极薄包装组件”模式，不应该发明新组件模式。
2. 现有 7 个 Picker 并不是通过 `RelationPicker/index.js` 注册，而是直接在 `ruoyi-ui/src/main.js` 全局注册。
3. 现有 `resolveEntityRoute()` 默认把目标路径拼成 `/${id}`，如果直接给模板类实体加列表页 path，详情跳转会失真，因此必须补 query fallback。
4. 模板相关目标页当前大多是“列表/抽屉/设计器”模式，不是标准 `/detail/:id` 路由。

---

## 三、数据库 DESCRIBE 实测结果

任务卡明确要求“动手前必须先连本地库 DESCRIBE 4 张模板表”，本次已执行。

### 3.1 连接方式

- 数据库：`jdbc:mysql://127.0.0.1:3306/jst`
- 用户名：`root`
- 密码：`123456`
- 由于本机无 `mysql` CLI，改用本地 Maven 仓库已有的 MySQL JDBC 驱动，通过 `jshell + mysql-connector-java-8.0.29.jar` 连库执行 `DESCRIBE`。

### 3.2 DESCRIBE 结果摘要

#### `jst_enroll_form_template`

| 字段 | 类型 | 备注 |
|---|---|---|
| `template_id` | `bigint(20)` | 主键 |
| `template_name` | `varchar(128)` | 名称字段 |
| `template_version` | `int(11)` | 模板版本 |
| `owner_type` | `varchar(20)` | 归属类型 |
| `owner_id` | `bigint(20)` | 归属 ID |
| `schema_json` | `json` | 字段定义 |
| `audit_status` | `varchar(20)` | 审核状态 |
| `status` | `tinyint(4)` | 启停状态 |
| `effective_time` | `datetime` | 生效时间 |
| `create_time` | `datetime` | 创建时间 |
| `del_flag` | `char(1)` | 逻辑删除 |

#### `jst_cert_template`

| 字段 | 类型 | 备注 |
|---|---|---|
| `template_id` | `bigint(20)` | 主键 |
| `template_name` | `varchar(128)` | 名称字段 |
| `owner_type` | `varchar(10)` | 归属类型 |
| `owner_id` | `bigint(20)` | 归属 ID |
| `bg_image` | `varchar(255)` | 证书底图 |
| `layout_json` | `json` | 布局 JSON |
| `audit_status` | `varchar(20)` | 审核状态 |
| `status` | `tinyint(4)` | 启停状态 |
| `create_time` | `datetime` | 创建时间 |
| `del_flag` | `char(1)` | 逻辑删除 |

#### `jst_coupon_template`

| 字段 | 类型 | 备注 |
|---|---|---|
| `coupon_template_id` | `bigint(20)` | 主键 |
| `coupon_name` | `varchar(128)` | 名称字段，**不是 `template_name`** |
| `coupon_type` | `varchar(20)` | 券类型 |
| `threshold_amount` | `decimal(12,2)` | 门槛金额 |
| `status` | `tinyint(4)` | 启停状态 |
| `create_time` | `datetime` | 创建时间 |
| `del_flag` | `char(1)` | 逻辑删除 |

#### `jst_rights_template`

| 字段 | 类型 | 备注 |
|---|---|---|
| `rights_template_id` | `bigint(20)` | 主键 |
| `rights_name` | `varchar(128)` | 名称字段，**不是 `template_name`** |
| `rights_type` | `varchar(20)` | 权益类型 |
| `quota_mode` | `varchar(10)` | 配额口径 |
| `quota_value` | `decimal(12,2)` | 配额数值 |
| `status` | `tinyint(4)` | 启停状态 |
| `create_time` | `datetime` | 创建时间 |
| `del_flag` | `char(1)` | 逻辑删除 |

### 3.3 与任务卡预期的关键偏差

这一步发现了几个必须纠正的点：

1. `formTemplate` / `certTemplate` 的归属字段不是任务卡示例里的 `partner_id`，而是 `owner_type + owner_id`。
2. `couponTemplate` 的名称字段是 `coupon_name`。
3. `rightsTemplate` 的名称字段是 `rights_name`。

因此本次后端 SQL 没有照抄任务卡示例，而是按真实表结构落地。

---

## 四、实际交付清单

### 4.1 后端文件

#### 文件

- `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`

#### 已完成内容

1. `searchByType()` 新增 4 个 case：
   - `formTemplate`
   - `certTemplate`
   - `couponTemplate`
   - `rightsTemplate`
2. 新增 4 个 private search 方法：
   - `searchFormTemplate`
   - `searchCertTemplate`
   - `searchCouponTemplate`
   - `searchRightsTemplate`
3. `buildBriefSql()` 新增 4 个对应 case。
4. 顶部 Javadoc 补入本次 DESCRIBE 的真实字段说明。

#### SQL 设计说明

##### `formTemplate`

- `id`：`template_id`
- `name`：`template_name`
- `subtitle`：`平台模板 / 赛事方名称 + 版本号`
- `statusTag`：`audit_status`

##### `certTemplate`

- `id`：`template_id`
- `name`：`template_name`
- `subtitle`：`平台模板 / 赛事方名称 + 空白模板/已配底图`
- `statusTag`：`audit_status`

##### `couponTemplate`

- `id`：`coupon_template_id`
- `name`：`coupon_name`
- `subtitle`：`coupon_type + 门槛金额`
- `statusTag`：`status`

##### `rightsTemplate`

- `id`：`rights_template_id`
- `name`：`rights_name`
- `subtitle`：`rights_type + quota_mode/quota_value`
- `statusTag`：`status`

#### 说明

`formTemplate` / `certTemplate` 的归属信息，本次统一保守地解释为：

- `owner_type = 'platform'` → `平台模板`
- `owner_type = 'partner'` → 尝试关联 `jst_event_partner.partner_name`
- 若关联不到 → fallback 为 `赛事方#owner_id`

这样做的原因是：任务卡和当前事件模块里对 `owner_id` 的业务解释存在历史不一致，本卡只做 Picker 基础设施，不顺手扩大业务语义修复面。

---

### 4.2 前端组件文件

#### 新增 4 个薄包装组件

1. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/FormTemplatePicker.vue`
2. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/CertTemplatePicker.vue`
3. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/CouponTemplatePicker.vue`
4. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/RightsTemplatePicker.vue`

#### 实现方式

全部严格照抄 `ChannelPicker.vue` 的风格，只改：

1. `name`
2. `entity`
3. 默认 `placeholder`

未改：

1. props 结构
2. emit 方式
3. 组件层级
4. 样式模式

即保持“显式薄包装”而不是做成新的通用 `type` prop 组件。

---

### 4.3 全局注册

#### 文件

- `RuoYi-Vue/ruoyi-ui/src/main.js`

#### 实际处理

虽然任务卡 Step 4 写的是“若有 `RelationPicker/index.js` 则加进去，否则跳过”，但仓库真实现状是：

1. `RelationPicker/index.js` 不存在。
2. 现有 7 个 Picker 已直接在 `main.js` 全局注册。

为保持家族一致性，本次在 `main.js` 里补了：

1. 4 个 `import`
2. 4 个 `Vue.component(...)`

这是一个与任务卡表述略有不同、但更贴近仓库现状的实施偏差，已在此处说明。

---

### 4.4 路由映射文件

#### 文件

- `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`

#### 已完成内容

新增 4 类模板实体映射：

1. `formTemplate`
2. `certTemplate`
3. `couponTemplate`
4. `rightsTemplate`

并同步调整 `resolveEntityRoute()`，支持 query-based fallback：

- 若配置存在 `queryKey`，则返回：
  - `path: { path: xxx, query: { [queryKey]: String(id) } }`
- 否则保持原先 `path + '/' + id` 逻辑

#### 真实路径与权限点

本次没有照抄任务卡示例里的旧权限命名，而是按仓库现状对齐为：

| 实体 | path | perm |
|---|---|---|
| `formTemplate` | `/jst/form-template` | `jst:event:enroll_form_template:list` |
| `certTemplate` | `/partner/cert-manage` | `jst:event:cert_template:list` |
| `couponTemplate` | `/jst/coupon/template` | `jst:marketing:coupon_template:list` |
| `rightsTemplate` | `/jst/rights/template` | `jst:marketing:rights_template:list` |

#### 为什么必须改 `resolveEntityRoute()`

因为原逻辑一律会把路径拼成：

```javascript
${cfg.path}/${id}
```

但这 4 个目标页都不是标准 `/detail/:id` 页面，若不改 fallback：

1. 路径会拼错
2. “查看详情 →”会直接失效

因此这一步是保证新模板类 Picker 至少能跳到正确列表页的必要前提。

---

## 五、与任务卡的实际偏差说明

本卡实现整体遵循任务卡，但有几处“以仓库真相/数据库真相为准”的偏差：

### 5.1 `partner_id` → `owner_type + owner_id`

任务卡示例把 `formTemplate` / `certTemplate` 写成了 `partner_id` 归属，但实际表结构不是这样。

处理方式：

1. 按 DESCRIBE 真值改写 SQL。
2. Javadoc 中明确记录真实字段，避免后续继续踩坑。

### 5.2 权限点命名按真实实现而非任务卡示例

任务卡示例中的 `perm` 类似：

- `jst:form_template:list`
- `jst:coupon_template:list`

但仓库真实权限命名是：

- `jst:event:enroll_form_template:list`
- `jst:marketing:coupon_template:list`
- `jst:marketing:rights_template:list`
- `jst:event:cert_template:list`

处理方式：

直接按真实代码基线写，避免新映射一落地就权限失真。

### 5.3 全局注册落在 `main.js`

任务卡对“全局注册”的描述是“若有 `RelationPicker/index.js` 则改之”，但真实情况是所有已有 Picker 都在 `main.js` 注册。

处理方式：

在 `main.js` 补齐 4 个新组件注册，保持风格统一。

### 5.4 没有侵入目标模板页实现“自动展开详情”

任务卡 Step 3 提到“查看详情 → 能跳对”，本次已保证“跳到对的页面”。

但这 4 个目标页当前实现大多是：

1. 列表页
2. 编辑抽屉
3. 设计器

并非标准只读详情页，也没有统一的 `autoOpen + queryId` 约定。

因此本次没有进一步侵入这些页面去自动弹开编辑态，否则会带来：

1. 编辑态与只读详情态语义混淆
2. 权限语义扩散
3. 本卡边界被动扩大

这点已在“隐患与 TODO”里单列说明。

---

## 六、验证结果

### 6.1 后端编译

执行命令：

```bash
cd RuoYi-Vue
mvn -pl ruoyi-admin -am clean compile -DskipTests
```

结果：

- `ruoyi-admin` 及其依赖模块全部编译成功
- 最终结果：`BUILD SUCCESS`

### 6.2 前端生产构建

执行命令：

```bash
cd RuoYi-Vue/ruoyi-ui
npm run build:prod
```

结果：

- 构建成功
- 最终结果：`DONE  Build complete`

### 6.3 Warning 情况

`npm run build:prod` 仅出现原有的 bundle size / asset size 警告，例如：

1. `login-background.jpg`
2. `chunk-elementUI`
3. `chunk-libs`
4. `app.js/css`

判断：

- 这些 warning 属于仓库既有体积问题
- 本卡未引入新的 warning 类型
- 本卡新增 4 个极薄包装组件，对 bundle 体积影响极小

### 6.4 本次未完成的验证

以下验证本次未做完：

1. 未在浏览器中实际把某个现有页面临时换成 `FormTemplatePicker` 做手工 smoke。
2. 未逐个点击新 Picker 的“查看详情 →”做真人 UI 录屏确认。
3. 未做页面级联调，因为本卡按红线没有改页面使用层。

因此本卡当前验证强度是：

1. 数据库字段真值已核查
2. Java 编译级通过
3. Vue 生产构建级通过
4. 但未做到最终页面行为级 smoke

---

## 七、交付文件清单

### 7.1 修改文件

1. `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`
2. `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`
3. `RuoYi-Vue/ruoyi-ui/src/main.js`

### 7.2 新增文件

1. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/FormTemplatePicker.vue`
2. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/CertTemplatePicker.vue`
3. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/CouponTemplatePicker.vue`
4. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/RightsTemplatePicker.vue`

### 7.3 明确未改文件

1. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/BasePicker.vue`
2. 各具体业务页面
3. 路由定义文件
4. 任意后端 Service / Mapper / Domain
5. `package.json`
6. 任意 SQL / DDL / migration

---

## 八、隐患点

### 8.1 “查看详情 →”目前是跳到正确模板页，但不保证自动展开具体记录

这是本卡最重要的遗留点。

原因：

1. 这 4 个目标页当前不是统一的 detail 页面。
2. 多数页只有“列表 + 编辑抽屉/设计器”，没有只读详情路由。
3. 直接让 query 触发编辑抽屉，会造成“详情”与“编辑”语义混淆。

当前结果：

1. 新 Picker 的“查看详情 →”不会再拼错路径。
2. 但目标页是否能自动定位到目标记录，仍要看下一步页面层改造。

影响：

- 不影响本卡“基础设施已可用”的判断。
- 会影响用户对“详情跳转”是直接落到具体记录还是仅进入对应模板页的预期。

### 8.2 `formTemplate` / `certTemplate` 的归属语义存在历史不一致

虽然真实表结构是 `owner_type + owner_id`，但当前项目里：

1. 任务卡示例按 `partner_id` 描述
2. 事件模块里部分列表页把 `owner_id` 往 `contest` 方向解释
3. partner 包装层又把它当归属方来使用

因此本卡的 `subtitle` 采用了“保守可读”方案，而不是深度修正 owner 语义。

影响：

- 当前 picker 可用
- 但后续若要统一“模板究竟归赛事方/赛事/平台”的产品语义，仍需要单独做一轮治理

### 8.3 `statusTag` 在模板类实体上不是统一枚举体系

本卡为了复用 `BasePicker`，统一返回了 `statusTag`：

1. `form/cert`：返回 `audit_status`
2. `coupon/rights`：返回 `status`

而 `BasePicker.statusTagType()` 当前只对少量固定值有样式映射：

```javascript
active / pending / rejected / '0' / '1'
```

这意味着：

1. `approved`、`draft` 等状态目前不会有特别丰富的 tag type 映射
2. 但不影响文字展示本身

这不是本卡 blocker，但属于 UI 层可继续优化点。

### 8.4 本卡未做人机级 smoke

虽然编译和 build 通过，但尚未在浏览器里执行：

1. 远程搜索
2. 选中
3. hydrate
4. 跳转
5. 权限可见性

因此严格说，本卡还缺一次页面级冒烟确认。

---

## 九、TODO / 下游建议

### 9.1 给下一卡 `POLISH-PICKER-APPLY` 的建议

下一卡在真正把 Picker 接到页面上时，建议同时处理：

1. 把旧 `_id` 输入框替换为新 Picker。
2. 针对目标模板页补“query 参数 → 自动打开目标记录”的最小能力。
3. 做一次完整 smoke：
   - 搜索
   - 下拉
   - 选中
   - hydrate
   - 查看详情

### 9.2 如果要补详情落点体验

推荐做法不是硬造 `/detail/:id` 路由，而是沿用现有页面模式：

1. 列表页接收 query 参数
2. 页面加载后定位目标记录
3. 若页面已有只读 detail drawer，就打开 detail
4. 若只有 edit drawer，需要先评估是否要新增只读态，避免“查看详情”直接进入编辑

### 9.3 若要继续完善模板类状态标签

可以另起一个小卡，统一扩 `BasePicker.statusTagType()` 的映射，例如：

1. `approved` → `success`
2. `draft` → `info`
3. `pending` → `warning`
4. `rejected` → `danger`

但这不应在本卡里顺手做，因为任务卡明确不许改 `BasePicker.vue`。

### 9.4 如需做最终交付级验证

建议下次在可交互环境里补以下动作：

1. 任找一个已使用 `ChannelPicker` 的页面，临时切为 `FormTemplatePicker`
2. 验证是否能正常远程搜索与回填
3. 点击“查看详情 →”看是否到正确模板页
4. 测完立即回滚临时接线改动

---

## 十、当前工作区状态说明

当前工作区不是干净树，存在一些与本卡无关的并行改动，例如：

1. `jst-uniapp/api/rights.js`
2. 多个 `jst-uniapp/pages-sub/*`
3. `test/wx-tests.http`
4. 若干 `subagent/tasks/*.md`

这些不是本卡产生的，我没有回滚或覆盖它们。

本卡实际新增/修改的代码文件只限于第七节列出的 7 个文件。

---

## 十一、最终结论

本卡已经完成“4 新 Picker + EntityBriefController 扩展”的基础设施交付，满足以下核心目标：

1. 4 张模板表字段已按真实数据库结构核对，不是凭感觉写 SQL。
2. 后端 `search / brief` 已扩到 4 类模板实体。
3. 前端 4 个 Picker 组件已创建，并与现有 Picker 家族风格一致。
4. `entityRouteMap.js` 已补 4 条映射，并解决了列表页 fallback 的路径问题。
5. `mvn` 与 `npm run build:prod` 全部通过。

当前唯一仍需下游补齐的体验项是：

- 目标模板页如何基于 query 自动定位并打开具体记录。

这项建议放到下一卡 `POLISH-PICKER-APPLY` 联动页面接入时一起完成，而不是在本卡越界侵入页面编辑行为。
