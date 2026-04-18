# POLISH-PICKER-TEMPLATE-FAMILY — Template/Coupon/Rights Picker 族

> 优先级：**P1** | 预估：**S**（4-6 h） | Agent：**Web Admin Agent**
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 依赖：plan-06 已产出的 `RelationPicker/BasePicker.vue` + `EntityBriefController.java` + `entityRouteMap.js`
> **下游**：POLISH-PICKER-APPLY 会复用本卡产出。本卡先做，下一卡再用。

---

## 一、业务背景

Plan-06 已交付 7 个 RelationPicker（Channel/User/Sales/Contest/Participant/Partner/Order），替换了 56 处 `_id` 输入框。但管理端还有 4 类业务表的 `_id` 字段没有 Picker 可用：

| 实体 | 表 | 示例页面 |
|---|---|---|
| **表单模板** `formTemplate` | `jst_enroll_form_template` | 赛事编辑 `/partner/contest-edit` 报名 Tab、`/jst/form-template` |
| **证书模板** `certTemplate` | `jst_cert_template` | 赛事编辑证书 Tab、`/partner/cert-manage` |
| **优惠券模板** `couponTemplate` | `jst_coupon_template` | `/jst/coupon`、营销配置 |
| **权益模板** `rightsTemplate` | `jst_rights_template` | `/jst/rights`、积分商城配置 |

运营填这些字段时，现在只能手输 ID（运营根本不知道 ID），或者从下拉里看不清楚模板内容。本卡造 4 个新 Picker + 后端 search/brief 支持，下一张任务卡再把它们贴到页面上。

---

## 二、必读上下文

1. `CLAUDE.md` § 四 / § 五（编码规范）
2. `subagent/WEB_ADMIN_AGENT_PROMPT.md`
3. **已有框架**（**严格照抄风格，不要发明新模式**）：
   - `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/BasePicker.vue`（核心逻辑，复用即可）
   - `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/ChannelPicker.vue`（薄包装示例）
   - `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/index.js`（如存在，注册全局组件；如不存在忽略）
   - `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`（实体 → 详情路由映射）
   - `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`（统一 search/brief 端点，**只需扩 switch case**）
4. 数据表字段核对（以 DESCRIBE 为准，字段不匹配时**先运行 `DESCRIBE 表名` 验证**，不要凭感觉）：
   - `jst_enroll_form_template`：主键 `template_id`，名称 `template_name`，归属 `partner_id`（可能为 null 代表平台通用）
   - `jst_cert_template`：主键 `template_id`，名称 `template_name`，归属 `partner_id`，可能有 `bg_image` 字段
   - `jst_coupon_template`：主键 `coupon_template_id`，名称字段需 DESCRIBE 确认（一般是 `coupon_name` 或 `template_name`），状态 `status`
   - `jst_rights_template`：主键 `rights_template_id`，名称 `rights_name` 或 `template_name`（DESCRIBE 确认）

> ⚠️ **字段名风险提示**：竞赛通历史表命名不统一，EntityBriefController 里已有类似踩坑案例（见其 Javadoc L23-34）。**动手前务必连库 DESCRIBE 这 4 张表**，用实际字段名写 SQL。本地库连接：
> ```
> url: jdbc:mysql://127.0.0.1:3306/jst
> user: root / pass: 123456
> ```

---

## 三、交付物清单（精确路径 + 执行顺序）

### Step 1：后端扩 EntityBriefController（**1 个文件，只扩 switch**）

**文件**：`RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`

**改动点**：`searchByType` + `buildBriefSql` 两个派发方法各加 4 个 case。**不要碰其他方法**。

#### 1.1 `searchByType` switch 补 4 个 case

```java
case "formTemplate":    return searchFormTemplate(kw, limit);
case "certTemplate":    return searchCertTemplate(kw, limit);
case "couponTemplate":  return searchCouponTemplate(kw, limit);
case "rightsTemplate":  return searchRightsTemplate(kw, limit);
```

#### 1.2 新增 4 个 private search 方法（照抄 `searchChannel` 风格）

**⚠️ 字段名要 DESCRIBE 核对后再填**。以下是预期模板，subtitle 选有信息量的字段（比如模板创建时间、归属 partner 名、数量上限等）：

```java
/**
 * jst_enroll_form_template: template_id, template_name, partner_id, create_time
 * partner_id=NULL 表示平台通用模板
 */
private List<Map<String, Object>> searchFormTemplate(String kw, int limit) {
    String base = "SELECT t.template_id AS id, t.template_name AS name, "
                + "IFNULL(p.partner_name, '平台通用') AS subtitle "
                + "FROM jst_enroll_form_template t "
                + "LEFT JOIN jst_event_partner p ON t.partner_id = p.partner_id ";
    if (StringUtils.isNotBlank(kw)) {
        String like = "%" + kw + "%";
        return jdbcTemplate.queryForList(
                base + "WHERE t.template_name LIKE ? ORDER BY t.template_id DESC LIMIT ?",
                like, limit);
    }
    return jdbcTemplate.queryForList(base + "ORDER BY t.template_id DESC LIMIT ?", limit);
}

/**
 * jst_cert_template: template_id, template_name, partner_id
 * 如有 bg_image 字段，subtitle 可额外标记「已配置背景」/「空白模板」
 */
private List<Map<String, Object>> searchCertTemplate(String kw, int limit) {
    // 类似结构，DESCRIBE 确认 bg_image / layout_json 字段后决定 subtitle 内容
}

/**
 * jst_coupon_template: coupon_template_id, ? (DESCRIBE 确认名称字段), status
 */
private List<Map<String, Object>> searchCouponTemplate(String kw, int limit) {
    // DESCRIBE 确认字段名后写
}

/**
 * jst_rights_template: rights_template_id, ? (DESCRIBE 确认), status
 */
private List<Map<String, Object>> searchRightsTemplate(String kw, int limit) {
    // DESCRIBE 确认字段名后写
}
```

#### 1.3 `buildBriefSql` switch 也补 4 个 case

对应写 4 个 SELECT（单行 WHERE `xxx_id = ?`）。**字段别名必须是 `id / name / subtitle / statusTag`**（前端 BasePicker 靠这些别名渲染）。

#### 1.4 编译验证

```bash
cd RuoYi-Vue && mvn -pl ruoyi-admin -am clean compile -DskipTests
```

期望 `BUILD SUCCESS`。

---

### Step 2：前端 4 个 Picker 薄包装组件

**风格严格照抄 `ChannelPicker.vue`**，只改 3 处：`name` / `entity` / `placeholder` 默认值。

#### 2.1 新文件 1：`RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/FormTemplatePicker.vue`

```vue
<template>
  <base-picker :value="value" entity="formTemplate" :placeholder="placeholder" :clearable="clearable" :disabled="disabled" :show-detail-link="showDetailLink" @input="$emit('input', $event)" @change="$emit('change', $event)" />
</template>
<script>
import BasePicker from './BasePicker.vue'
export default {
  name: 'FormTemplatePicker',
  components: { BasePicker },
  props: {
    value: { type: [Number, String], default: null },
    placeholder: { type: String, default: '请选择表单模板（输入名称搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  }
}
</script>
```

#### 2.2 新文件 2：`CertTemplatePicker.vue`（entity='certTemplate'，placeholder='请选择证书模板'）

#### 2.3 新文件 3：`CouponTemplatePicker.vue`（entity='couponTemplate'，placeholder='请选择优惠券模板'）

#### 2.4 新文件 4：`RightsTemplatePicker.vue`（entity='rightsTemplate'，placeholder='请选择权益模板'）

---

### Step 3：扩 `entityRouteMap.js`（让"查看详情 →"能跳对）

**文件**：`RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`

```javascript
export const ENTITY_ROUTE_MAP = {
  // ... 已有 ...
  formTemplate:   { path: '/jst/form-template/detail',    perm: 'jst:form_template:list' },
  certTemplate:   { path: '/partner/cert-manage/detail',  perm: 'jst:cert_template:list' },
  couponTemplate: { path: '/jst/coupon/detail',           perm: 'jst:coupon_template:list' },
  rightsTemplate: { path: '/jst/rights/detail',           perm: 'jst:rights_template:list' }
}
```

> ⚠️ 具体 path 要和项目已有路由对齐。如果页面没有 `/detail/:id` 形式的明细路由，用列表页 + query 参数 fallback：`{ path: '/jst/form-template', perm: 'jst:form_template:list' }`，并在 resolve 时追加 `?id=`。**先 grep 已有路由：**
> ```bash
> grep -r "form-template" RuoYi-Vue/ruoyi-ui/src/router/
> grep -r "cert-manage" RuoYi-Vue/ruoyi-ui/src/router/
> ```
> 找到实际路由再写。

---

### Step 4：（可选）全局注册

如果项目有 `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/index.js` 统一导出，就把 4 个新组件加进去。如果没有，**跳过本步**，下一卡贴页面时按需 import。

---

### Step 5：手工冒烟测试（1 个页面即可）

随便找一个现有用 `ChannelPicker` 的页面（`git log --all -S 'ChannelPicker' --oneline` 找改过的页面），临时把 import 改成 `FormTemplatePicker` 看能不能搜、能不能选、"查看详情"链接对不对。**测完回滚这个临时改动**。

---

## 四、DoD 验收标准

- [ ] `EntityBriefController.java` 扩 8 个方法（4 search + 4 brief）并通过编译
- [ ] `mvn -pl ruoyi-admin -am clean compile -DskipTests` ✅ BUILD SUCCESS
- [ ] 4 个 Picker 组件文件创建，内容和 `ChannelPicker.vue` 结构一致（仅 3 处差异）
- [ ] `entityRouteMap.js` 补 4 条
- [ ] `npm run build:prod` ✅ 无 Error（允许 Warning，但不要引入新 Warning）
- [ ] 冒烟：换页面上临时 import 验证「搜索 / 下拉 / 选中 / hydrate / 详情跳转」5 个行为
- [ ] DESCRIBE 4 张表的字段名**写进 commit message 或代码注释**（Javadoc 照 EntityBriefController 现有风格追加）
- [ ] commit message 格式：`feat(admin): POLISH-PICKER-TEMPLATE-FAMILY 4 新 Picker (FormTemplate/CertTemplate/CouponTemplate/RightsTemplate)`

---

## 五、红线（不许做的事）

- ❌ **不许改 `BasePicker.vue`**（已经够通用，扩 case 而不是改 base）
- ❌ 不许新增 npm / maven 依赖
- ❌ 不许"顺手"改其他 Picker 的默认 props
- ❌ 不许改 `EntityBriefController` 的签名 / 注解 / 权限（`@PreAuthorize("isAuthenticated()")` 保留）
- ❌ **不许跳过 DESCRIBE 环节凭感觉写 SQL**——历史上已经因为 `nickname` vs `nick_name` 踩过坑
- ❌ 不许本卡顺手替换页面上的 `_id` 输入框（那是下一卡 POLISH-PICKER-APPLY 的工作）
- ❌ 不许把 4 个 Picker 合并成一个带 `type` prop 的通用组件（阅读成本高，显式优于隐式）

---

## 六、派发附言

如果 subagent 在 DESCRIBE 阶段发现字段名和任务卡预期不一致（比如 `jst_coupon_template` 名称字段其实是 `coupon_name` 而非 `template_name`），**按实际字段写 SQL 即可，在 commit message 注明「字段修正：XXX」**，不用回头问我。

派发时间：2026-04-18 | 主 Agent 签名：竞赛通架构师
