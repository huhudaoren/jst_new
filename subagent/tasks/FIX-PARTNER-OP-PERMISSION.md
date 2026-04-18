# FIX-PARTNER-OP-PERMISSION — 运营/admin 代操作赛事方数据权限修复

> **Agent**: Backend Agent + 少量前端调整（可由 Backend Agent 兼顾）
> **预估工作量**: S（单服务 + 单 DTO + 单前端页面）
> **优先级**: P0 — 阻塞运营主管日常代运营动作
> **派发时间**: 2026-04-17
> **并行关系**: 与 ADMIN-UX-B1 并行，无代码交集（B1 动 `schemaJson` 可视化编辑器，本卡动 `ownerType/ownerId` 权限）

---

## 一、背景与问题

公司运营（`jst_operator` 角色）被规划为"拥有赛事方所有操作权限"，日常需代运营多个赛事方，**包括代为创建/修改表单模板**。目前实测：

- admin / 运营 登录管理端 → 表单模板管理 → 新建（ownerType=partner）→ ❌ `AUTH_DENIED`
- admin / 运营 登录 → 编辑属于某赛事方的模板 → ❌ `AUTH_DENIED`

**根因**（已定位）：
`jst-event/src/main/java/com/ruoyi/jst/event/service/impl/FormTemplateServiceImpl.java` 第 438-456 行 `resolveOwnerId()` 方法硬依赖 `JstLoginContext.currentPartnerId()`，该值在 admin/运营 登录时为 `null`，导致 `ownerType=partner` 路径直接抛异常。同时 `FormTemplateSaveReqDTO` **没有 `ownerId` 字段**，admin 即使想指定目标赛事方也无处传入。

## 二、全仓库扫描结论

主 Agent 已完成全仓库扫描：**只有 `FormTemplateServiceImpl` 一处真实 bug**，其他 partner 相关服务均已正确处理 admin/运营 代操作场景。

| 服务 | 处理方式 | 状态 |
|---|---|---|
| `ContestServiceImpl` | `requirePartnerId(req.getPartnerId())` — admin 可通过 DTO 指定 | ✅ 正确 |
| `EnrollRecordServiceImpl` | `if (!isPartnerUser()) return;` 守卫 | ✅ 正确 |
| `RefundServiceImpl` | `isPartnerUser()` 守卫 | ✅ 正确 |
| `WriteoffServiceImpl` | `isPartnerUser()` 守卫 | ✅ 正确 |
| `ContestReviewerServiceImpl` | `if (partnerId == null) return;` 早退 | ✅ 正确 |
| `PartnerScoreServiceImpl` | `if (!hasRole(ROLE_PARTNER)) return;` 守卫 | ✅ 正确 |
| `PartnerSettlementServiceImpl` | 仅 `BasePartnerController` 调用 | ✅ 设计使然 |
| `PartnerCertServiceImpl` | 仅 `BasePartnerController` 调用 | ✅ 设计使然 |
| **`FormTemplateServiceImpl`** | **硬取 `currentPartnerId()`，DTO 无 `ownerId`** | **❌ 待修复** |

**次要隐患**（本卡顺手修一处）：
`PartnerScopeUtils.isPlatformOp()` 白名单仅认 `admin` + `jst_platform_op`，不认 6 个新业务角色。当前不爆雷是因为现有守卫用 `isPartnerUser()`（要求 `hasRole("jst_partner")`），这些角色天然不持有该角色。但为防未来误伤，**本卡把 `jst_operator` 加入白名单**（其他 5 个业务角色按 RBAC 各看各的菜单，不需要代操作能力）。

---

## 三、改造范围

### 3.1 后端

#### 文件 1: `FormTemplateSaveReqDTO.java`（新增字段）

路径：`RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/FormTemplateSaveReqDTO.java`

新增：
```java
/**
 * 目标赛事方 ID（仅 ownerType=partner 且当前为 admin/运营 时需要）。
 * - partner 登录：忽略此字段，强制取 currentPartnerId
 * - admin/运营 登录 + ownerType=partner：必填
 * - admin/运营 登录 + ownerType=platform：忽略
 */
private Long ownerId;

public Long getOwnerId() { return ownerId; }
public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
```

> ⚠️ **不要加 `@NotNull`** —— 它对 partner/platform 路径是可选的。校验放在 Service 层。

#### 文件 2: `FormTemplateServiceImpl.java`（核心修复）

路径：`RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/FormTemplateServiceImpl.java`

**改法 1 — `resolveOwnerId(String)` 改为 `resolveOwnerId(String, Long)`**

```java
private Long resolveOwnerId(String ownerType, Long reqOwnerId) {
    boolean isPartner = isPartnerUser();

    if ("partner".equals(ownerType)) {
        if (isPartner) {
            // partner 登录：强制取自己的 partnerId，忽略前端传入（防越权）
            return JstLoginContext.currentPartnerId();
        }
        // admin / 运营 登录：必须由前端指定目标 partnerId
        if (reqOwnerId == null || reqOwnerId <= 0) {
            throw new ServiceException("创建赛事方模板时必须指定 ownerId",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        // （可选）校验 partnerId 真实存在 — 查询 jst_event_partner
        // 如果工程里有 JstEventPartnerMapper，注入后 selectById 判空，不存在抛 PARAM_INVALID
        return reqOwnerId;
    }

    if ("platform".equals(ownerType)) {
        if (isPartner) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return null;
    }

    throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.message(),
            BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
}
```

**改法 2 — `createTemplate()` 第 296 行**

```java
// BEFORE:
Long ownerId = resolveOwnerId(req.getOwnerType());

// AFTER:
Long ownerId = resolveOwnerId(req.getOwnerType(), req.getOwnerId());
```

**改法 3 — `updateTemplate()` 第 316-345 行**

```java
private Long updateTemplate(FormTemplateSaveReqDTO req, String normalizedSchema) {
    JstEnrollFormTemplate template = requireTemplate(req.getTemplateId());
    assertTemplateOwnership(template);   // partner 仍只能改自己的

    // admin / 运营 允许修改归属（partner 登录会被下面 isPartnerUser 分支锁死）
    Long expectedOwnerId = resolveOwnerId(req.getOwnerType(), req.getOwnerId());
    boolean isPartner = isPartnerUser();

    if (isPartner) {
        // partner 登录不得修改归属类型或 ID
        if (!Objects.equals(template.getOwnerType(), req.getOwnerType())
                || !Objects.equals(template.getOwnerId(), expectedOwnerId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }
    // admin / 运营 允许改归属，直接覆盖
    template.setOwnerType(req.getOwnerType());
    template.setOwnerId(expectedOwnerId);

    // SM-25（保持原逻辑）
    FormTemplateAuditStatus currentStatus = FormTemplateAuditStatus.fromDb(template.getAuditStatus());
    currentStatus.assertCanTransitTo(FormTemplateAuditStatus.DRAFT);

    template.setTemplateName(req.getTemplateName());
    template.setSchemaJson(normalizedSchema);
    template.setTemplateVersion(nextVersion(template.getTemplateVersion()));
    template.setAuditStatus(FormTemplateAuditStatus.DRAFT.dbValue());
    template.setEffectiveTime(null);
    template.setUpdateBy(currentOperatorName());
    template.setUpdateTime(DateUtils.getNowDate());

    int updated = formTemplateMapperExt.updateAfterSave(template, currentStatus.dbValue());
    if (updated == 0) {
        throw new ServiceException("模板保存失败，状态已变更",
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }
    return template.getTemplateId();
}
```

> ⚠️ **注意保留 SM-25 状态机校验**，不要在重构中误删。

#### 文件 3: `PartnerScopeUtils.java`（扩展白名单）

路径：`RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/PartnerScopeUtils.java`

第 19-20 行附近新增常量：
```java
public static final String ROLE_OPERATOR = "jst_operator";
```

第 71-79 行 `isPlatformOp()` 改为：
```java
public static boolean isPlatformOp(LoginUser loginUser) {
    if (loginUser == null) {
        return false;
    }
    if (loginUser.getUser() != null && loginUser.getUser().isAdmin()) {
        return true;
    }
    return hasRole(loginUser, ROLE_PLATFORM_OP)
            || hasRole(loginUser, ROLE_OPERATOR);
}
```

> ⚠️ **只加 `jst_operator`，其他 5 个业务角色（jst_finance/jst_support/jst_marketing/jst_risk/jst_analyst）不加白名单** —— 它们按 RBAC 各看各的菜单，不承担代操作能力。

### 3.2 前端

#### 文件 4: `ruoyi-ui/src/views/jst/form-template/index.vue`

路径：`RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue`

**改造点**：
1. 表单弹窗中，当 `form.ownerType === 'partner'` 时，展示「所属赛事方」选择器
2. 选择器用 `el-select` + `filterable` + `remote` + `remote-method`，调用 `listJst_event_partner({ partnerName: 搜索词 })`
3. 用户登录如果是 partner（前端可判断：`this.$store.state.user.roles.includes('jst_partner') && !this.$store.state.user.roles.includes('admin')`），**隐藏整个归属配置区（ownerType + ownerId）** —— partner 自己不需要选
4. 编辑场景：回填 `form.ownerId = row.ownerId`，并把赛事方名称填到 options 里（调 `getJst_event_partner(ownerId)` 取名字）

**关键片段参考**：
```vue
<el-form-item v-if="!isPartnerUser" label="归属类型" prop="ownerType">
  <el-select v-model="form.ownerType" placeholder="请选择" class="full-width">
    <el-option label="平台" value="platform" />
    <el-option label="赛事方" value="partner" />
  </el-select>
</el-form-item>
<el-form-item
  v-if="!isPartnerUser && form.ownerType === 'partner'"
  label="所属赛事方"
  prop="ownerId"
>
  <el-select
    v-model="form.ownerId"
    filterable remote
    :remote-method="searchPartners"
    :loading="partnerLoading"
    placeholder="搜索赛事方名称"
    class="full-width"
  >
    <el-option
      v-for="p in partnerOptions"
      :key="p.partnerId"
      :label="p.partnerName"
      :value="p.partnerId"
    />
  </el-select>
</el-form-item>
```

**data() 新增**：
```js
partnerOptions: [],
partnerLoading: false,
```

**computed 新增**：
```js
isPartnerUser() {
  const roles = this.$store.state.user.roles || []
  return roles.includes('jst_partner') && !roles.includes('admin')
},
```

**methods 新增**：
```js
async searchPartners(keyword) {
  if (!keyword || keyword.trim().length < 1) return
  this.partnerLoading = true
  try {
    const res = await listJst_event_partner({ partnerName: keyword, pageSize: 20 })
    this.partnerOptions = res.rows || []
  } finally {
    this.partnerLoading = false
  }
},
```

**校验规则补充**（rules）：
```js
ownerId: [
  {
    validator: (rule, value, callback) => {
      if (this.form.ownerType === 'partner' && !this.isPartnerUser && !value) {
        callback(new Error('请选择所属赛事方'))
      } else {
        callback()
      }
    },
    trigger: 'change'
  }
]
```

**resetForm 和回填逻辑**：
- reset 时重置 `form.ownerId = null`，`partnerOptions = []`
- 编辑回填时，如果 `d.ownerId`，调 `getJst_event_partner(d.ownerId)` 拿到 `partnerName` 推入 `partnerOptions` 作为预选项

import 新增：
```js
import { listJst_event_partner, getJst_event_partner } from '@/api/jst/event/jst_event_partner'
```

---

## 四、验证步骤（必须全绿）

### 后端单元级验证（手工跑一次或写单测）

| # | 场景 | 期望 |
|---|---|---|
| V1 | partner 登录 → 创建 partner 模板（不传 ownerId）| 成功，ownerId = 自己的 partnerId |
| V2 | partner 登录 → 尝试传 ownerId=别人的 | 忽略传入值，仍用自己的（不越权） |
| V3 | admin 登录 → 创建 partner 模板 + 指定 ownerId=8001 | 成功，ownerId=8001 |
| V4 | admin 登录 → 创建 partner 模板 + 不传 ownerId | 400 `JST_COMMON_PARAM_INVALID` "创建赛事方模板时必须指定 ownerId" |
| V5 | 运营（jst_operator）登录 → 创建 partner 模板 + 指定 ownerId | 成功 |
| V6 | admin 登录 → 创建 platform 模板 | 成功，ownerId=null |
| V7 | partner 登录 → 创建 platform 模板 | `AUTH_DENIED` |
| V8 | admin 登录 → 编辑 partner 模板，改 ownerId 为另一个赛事方 | 成功（admin 可改归属） |
| V9 | partner 登录 → 尝试编辑别人的模板 | `AUTH_DENIED`（被 assertTemplateOwnership 拦截） |
| V10 | partner 登录 → 编辑自己的模板 + 尝试改 ownerType=platform | `AUTH_DENIED` |

### 前端验证（浏览器）

| # | 场景 | 期望 |
|---|---|---|
| F1 | admin 登录 → 新建模板 → 选 ownerType=partner → 出现赛事方选择器 → 搜索名称 → 选择 → 保存 | 成功，列表出现新模板 |
| F2 | admin 登录 → 编辑一个 partner 模板 → 归属配置正确回填（名称可见） | 成功 |
| F3 | admin 登录 → 选 ownerType=partner 但不选赛事方 → 保存 | 前端表单校验拦截，提示"请选择所属赛事方" |
| F4 | partner 登录 → 进入模板管理 → 新建 → 不显示归属配置区 | ✅ |
| F5 | 运营（jst_operator）登录 → 体验同 admin | ✅ |

### 编译验证
- `mvn compile -pl jst-event,jst-common -am` 通过
- `cd ruoyi-ui && npm run build:prod` 通过

---

## 五、交付物

1. 代码修改：上述 4 个文件
2. 任务报告：`subagent/tasks/任务报告/FIX-PARTNER-OP-PERMISSION-报告.md`
   - 列出改动文件清单（行号范围）
   - V1-V10 + F1-F5 的验证截图或接口响应
   - 已知风险/待办（如有）

---

## 六、不做的事

- ❌ 不动其他 partner 相关服务（已扫描确认正确）
- ❌ 不扩展 `isPlatformOp()` 到 jst_finance/jst_support/jst_marketing/jst_risk/jst_analyst（它们不是代操作角色）
- ❌ 不改 `BasePartnerController` 下任何代码（赛事方专用路径）
- ❌ 不动 ADMIN-UX-B1 正在重构的 `FormSchemaEditor` 相关前端代码（与本卡无交集）
- ❌ 不引入新的 DDL / 菜单 / 字典
- ❌ 不改 `resolveOwnerId` 以外的 `currentPartnerId()` 调用点

---

## 七、开始前的自检（Backend Agent 必答）

请在开工前用这 3 个问题自检（答在报告开头）：

1. 当 partner 登录时，前端若恶意传 `ownerId=999`（别人的），后端应怎样处理？为什么？
2. admin 编辑 partner 模板时把 ownerType 从 partner 改成 platform，后端允许吗？会影响已引用该模板的赛事吗？（答允不允许即可，不要求真去做联动）
3. `isPlatformOp()` 加上 `jst_operator` 后，会不会影响现有 `PartnerScope` 切面的行为？具体是哪段代码会受影响？（提示：看 `DataScopeAspect.aroundPartner`）

答完再动手。
