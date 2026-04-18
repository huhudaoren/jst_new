# PATCH-2 — Picker「查看详情 →」自动定位记录

> 优先级：**🔴 P0** | 预估：**S（4-6h）** | Agent：**Web Admin Agent**
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 起因：POLISH-PICKER-TEMPLATE-FAMILY §8.1 隐患——Picker 的「查看详情 →」只跳到正确列表页，**不会自动定位到具体记录**，运营点完一脸懵

---

## 一、用户场景（产品视角）

> 运营在赛事编辑页选了"星光报名表单"作为报名表单 → 想确认这个表单长啥样 → 点 Picker 的「查看详情 →」→ 进入 `/jst/form-template` 列表 → **找不到刚选的那条** → 以为系统坏了

本卡修这条链路：进入目标页后**自动打开/定位**对应记录。

---

## 二、必读上下文

1. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/BasePicker.vue`（已用 `entityRouteMap.resolveEntityRoute` 跳转，**不动**）
2. `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`（**已支持 query fallback**，看 `queryKey` 分支）
3. **4 个目标页**（本卡主战场）：
   - `RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue`
   - `RuoYi-Vue/ruoyi-ui/src/views/partner/cert-manage.vue`
   - `RuoYi-Vue/ruoyi-ui/src/views/jst/coupon/template/index.vue`（路径以实际 grep 为准）
   - `RuoYi-Vue/ruoyi-ui/src/views/jst/rights/template/index.vue`（路径以实际 grep 为准）
4. **已有 autoOpen 模式参考**（沿用，不要发明新模式）：
   ```bash
   grep -rn "autoOpen\|this\.\$route\.query" RuoYi-Vue/ruoyi-ui/src/views/jst | head -10
   ```
   找到 plan-06 已用过的 4 个 autoOpen 页面，**严格照抄它们的 mounted 钩子结构**。

---

## 三、设计

### 3.1 entityRouteMap 加 queryKey

**文件**：`RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`

把模板类 4 条改成带 queryKey：

```javascript
formTemplate:   { path: '/jst/form-template',     perm: 'jst:event:enroll_form_template:list', queryKey: 'autoOpenId' },
certTemplate:   { path: '/partner/cert-manage',   perm: 'jst:event:cert_template:list',         queryKey: 'autoOpenId' },
couponTemplate: { path: '/jst/coupon/template',   perm: 'jst:marketing:coupon_template:list',   queryKey: 'autoOpenId' },
rightsTemplate: { path: '/jst/rights/template',   perm: 'jst:marketing:rights_template:list',   queryKey: 'autoOpenId' }
```

**验证**：随便写一个测试触发 `resolveEntityRoute('formTemplate', 123)` 应返回 `{ path: { path: '/jst/form-template', query: { autoOpenId: '123' } }, perm: ... }`。

### 3.2 4 个目标页 mounted 加 autoOpen 处理

**统一模式**（每页都加这段，照抄）：

```javascript
mounted() {
  const autoId = this.$route.query.autoOpenId
  if (autoId) {
    this.$nextTick(() => this.handleAutoOpen(Number(autoId)))
  }
},
methods: {
  // ... 已有
  async handleAutoOpen(id) {
    // Step 1: 把目标记录滚到列表顶部高亮（如果分页未在第一页就加 highlight）
    try {
      // 调用现有 detail 接口拿数据
      const res = await getXxxDetail(id)  // 用页面已有的 detail API
      if (res.code === 200 && res.data) {
        // Step 2: 优先打开只读详情 drawer/dialog；如无只读态，打开编辑态并加只读 hint
        this.handleView ? this.handleView(res.data) : this.handleUpdate(res.data)
      } else {
        this.$message.warning('未找到指定记录，可能已删除')
      }
    } catch (e) {
      this.$message.error('打开详情失败：' + (e.message || e))
    }
  }
}
```

**实施细节**：
- 4 个页面里的 `handleView` / `handleUpdate` 函数名以页面实际为准
- 如果页面有"详情"和"编辑"两个按钮，优先调"详情"（只读）的那个
- 如果只有"编辑"按钮（如 cert-manage 是 Fabric 设计器），就调编辑入口，不要硬造只读态
- `getXxxDetail` 用页面已 import 的 API，不要新建

### 3.3 cert-manage 特殊处理

cert-manage 是 Fabric.js 设计器，可能是 `/partner/cert-manage?templateId=xxx` 直接 load 设计器。看页面已有的 `templateId` query 处理逻辑：
- 如果已经支持 `?templateId=xxx`，把 entityRouteMap 的 queryKey 改成 `templateId` 而非 `autoOpenId`
- 如果支持其他 key，跟着改
- **不要在 cert-manage 里写新 autoOpen 逻辑**（设计器有自己的初始化流程）

---

## 四、验证

### Step V1：构建

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui && npm run build:prod
```

期望：无新 Error。

### Step V2：浏览器手测 4 条链路（**必须全部跑**）

依赖：PATCH-1 CORS 修复完成 + 后端跑起来。

测试方式：用 admin 账号登录后端管理界面。

| # | 触发链路 | 期望 |
|---|---|---|
| V2.1 | 进 `/jst/sales/dashboard` 或任意有 FormTemplatePicker 的页 → 选一个表单 → 点「查看详情」 | 跳到 form-template 页 + 自动弹开该记录 detail/edit |
| V2.2 | 任意有 CertTemplatePicker 的页 → 选证书 → 点「查看详情」 | 跳到 cert-manage + 自动加载该模板到设计器 |
| V2.3 | CouponTemplatePicker 同 V2.1 套路 | 跳 coupon/template + 自动弹开 |
| V2.4 | RightsTemplatePicker 同 V2.1 套路 | 跳 rights/template + 自动弹开 |

**反例验证**：手动访问 `/jst/form-template?autoOpenId=999999999`（不存在的 ID）→ 期望 toast「未找到指定记录」+ 列表正常加载，**不许白屏或报错**。

把 4 条链路 + 反例的浏览器截图（或文字描述「✅ V2.x 通过：跳转后自动打开，标题显示 'XX 模板'」）贴进报告。

---

## 五、DoD

- [ ] entityRouteMap.js 4 条加 queryKey
- [ ] 4 个目标页加 mounted autoOpen 处理（不发明新模式，照抄已有 autoOpen 页）
- [ ] `npm run build:prod` ✅
- [ ] V2.1-V2.4 + 反例 5 条手测全过
- [ ] 报告附 5 段验证证据
- [ ] commit：`feat(admin): PATCH-2 模板 Picker 查看详情自动定位记录`

---

## 六、红线

- ❌ 不许改 BasePicker.vue 或新增组件
- ❌ 不许改 4 个目标页的列表/编辑/设计器主流程，只加 mounted autoOpen
- ❌ 不许新建只读路由 `/detail/:id`（沿用现有页面模式）
- ❌ 不许 V2 用「肉眼看像跳过去了」交付，必须确认**目标记录真的弹开**
- ❌ 不许在 cert-manage 里硬造非设计器的"只读详情态"

---

## 七、派发附言

如果某个目标页（特别是 coupon/rights template）实际路径不是 `/jst/coupon/template` 而是 `/jst/marketing/jst_coupon_template/index` 之类的，**以 grep 找到的真实路由为准**修 entityRouteMap，不要凭印象写。

```bash
grep -rn "name: 'CouponTemplate'\|/coupon/template\|/coupon-template" RuoYi-Vue/ruoyi-ui/src/router | head -10
grep -rn "name: 'RightsTemplate'\|/rights/template\|/rights-template" RuoYi-Vue/ruoyi-ui/src/router | head -10
```

派发时间：2026-04-18 | 主 Agent：竞赛通架构师
