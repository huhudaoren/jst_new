# FIX-PARTNER-OP-PERMISSION 任务报告

> 派发时间：2026-04-17
> 完成时间：2026-04-17
> Agent：Backend Agent（兼顾前端一处视图改造）
> 并行关系：与 ADMIN-UX-B1 并行，本卡仅动"归属配置区"，不改 `schemaJson` 渲染

---

## A. 任务前自检（Step 2 答题）

**Q1. 当 partner 登录时，前端若恶意传 `ownerId=999`（别人的），后端应怎样处理？为什么？**

后端**强制忽略**前端传入的 `reqOwnerId`。在 `resolveOwnerId("partner", 999)` 里，只要
`isPartnerUser()==true` 就直接 `return JstLoginContext.currentPartnerId()`，不读
`reqOwnerId`。这是**防越权的硬边界**：partner 的 owner_id 由后端登录上下文唯一决定，
前端怎么传都不影响。同时 `updateTemplate` 里 partner 登录还额外校验 "传入 ownerType/ownerId
必须与原记录一致"，否则 `AUTH_DENIED`，双重保险。

**Q2. admin 编辑 partner 模板时把 ownerType 从 partner 改成 platform，后端允许吗？会影响已引用该模板的赛事吗？**

**允许**。改造后的 `updateTemplate()` 对 admin/运营 直接 `template.setOwnerType(req.getOwnerType())`
+ `template.setOwnerId(expectedOwnerId)` 覆盖，不再抛 AUTH_DENIED。这是任务卡明确的设计
（admin 代运营时可能需要把一个 partner 的私有模板收归平台通用）。

已引用该模板的赛事**不受影响**：`jst_contest.form_template_id` 存的是 template_id，与
owner_type/owner_id 解耦。修改 owner 只改模板自身归属与可见性，不波及历史赛事的绑定。任务卡
也说明不要求真做联动。

**Q3. `isPlatformOp()` 加上 `jst_operator` 后，会不会影响现有 `PartnerScope` 切面的行为？具体是哪段代码会受影响？**

**不会影响 `DataScopeAspect.aroundPartner`**。该切面有自己的私有 `hasRole()` 实现，只认
`ROLE_PLATFORM_OP="jst_platform_op"` 一个常量，**完全不调用** `PartnerScopeUtils.isPlatformOp()`
（参见 `DataScopeAspect.java` L37、L43）。

**真正受影响的调用点**（`PartnerScopeUtils.isPlatformOp` 的消费者）：
- `PartnerDataScopeAspect.java`（`jst-common/aspectj/`）里用它判断是否跳过 SQL 级数据隔离。
  加上 `jst_operator` 后，运营主管能看到所有赛事方的数据，这正是代运营所需的行为，符合任务意图。
- 其他模块 Service 用 `PartnerScopeUtils.isPlatformOp()` 做守卫的地方（如 Contest/Refund/Writeoff
  的 `isPartnerUser()` 守卫间接走到）——这些地方原本就用 "partner 才收紧、非 partner 放行" 的设计，
  `jst_operator` 天然不持 `jst_partner` 角色，加白名单前后行为一致，无副作用。

白名单不扩张到 `jst_finance/jst_support/jst_marketing/jst_risk/jst_analyst`：这 5 个角色按 RBAC
各看各的菜单，不承担代操作能力，符合任务卡第六节约束。

---

## B. 改动文件清单

### 后端

| # | 路径 | 行号范围 | 改动摘要 |
|---|---|---|---|
| 1 | `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/dto/FormTemplateSaveReqDTO.java` | 新增 L26-L33（ownerId 字段 + javadoc）+ L61-L67（getter/setter） | 新增 `private Long ownerId` 字段，**不加 `@NotNull`**（业务校验放 Service） |
| 2 | `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/FormTemplateServiceImpl.java` | L296（createTemplate 调用点）、L316-L355（updateTemplate 重构）、L455-L495（resolveOwnerId 签名改造） | ①`resolveOwnerId(String)` → `resolveOwnerId(String, Long reqOwnerId)`：partner 登录强制取自己的 partnerId，admin/运营 必须传 `reqOwnerId`。②`createTemplate` 传入 `req.getOwnerId()`。③`updateTemplate` 对 admin/运营 允许改归属，partner 仍锁死原值。SM-25 状态机校验完整保留 |
| 3 | `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/PartnerScopeUtils.java` | L21 新增 `ROLE_OPERATOR` 常量，L74-L85 `isPlatformOp()` 追加 `ROLE_OPERATOR` 白名单 | 运营主管与 admin/jst_platform_op 并列，共享代运营数据隔离白名单 |

### 前端

| # | 路径 | 行号范围 | 改动摘要 |
|---|---|---|---|
| 4 | `RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue` | 模板区 L110-L132（归属配置区），script 区 L188（import）、L198-L225（data）、L236-L243（computed `isPartnerUser`）、L271-L325（handleAdd/handleEdit/onOwnerTypeChange/searchPartners） | ①ownerType 加 `v-if="!isPartnerUser"` 隐藏；②新增 `ownerId` 远程搜索下拉；③partner 登录默认 `ownerType=partner` 并跳过归属区；④编辑时回填 `ownerId` 并调 `getJst_event_partner(id)` 拿名称作为候选项；⑤表单校验 `ownerId` 必填；⑥切换 ownerType 时清空 ownerId。**完全不动 `schemaJson` 渲染块**（B1 领地） |

**未改动**（验证隔离）：
- `BasePartnerController` 及其子类 — 按任务卡约束
- `FormSchemaEditor.vue` / `FieldEditDrawer.vue` / `SchemaPreview.vue` — B1 的领地
- `schemaJson` 相关的模板/脚本代码（仅在 form-template/index.vue 内保留 B1 已有结构）
- `PartnerScopeUtils` 的其他方法与常量
- 所有其他 `currentPartnerId()` 调用点

---

## C. 编译输出

### C.1 后端 `mvn compile -pl jst-event,jst-common -am`

```
[INFO] --- compiler:3.13.0:compile (default-compile) @ jst-event ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 246 source files with javac [debug parameters target 17] to target\classes
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for ruoyi 3.9.2:
[INFO]
[INFO] ruoyi .............................................. SUCCESS [  0.434 s]
[INFO] ruoyi-common ....................................... SUCCESS [  9.897 s]
[INFO] jst-common ......................................... SUCCESS [  6.738 s]
[INFO] jst-order .......................................... SUCCESS [  5.335 s]
[INFO] jst-event .......................................... SUCCESS [ 13.418 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  36.974 s
```

### C.2 前端 `npm run build:prod`

```
 DONE  Build complete. The dist directory is ready to be deployed.
 INFO  Check out deployment instructions at https://cli.vuejs.org/guide/deployment.html
```

> 说明：第一次 build 遭遇 2 次瞬态错误（FieldEditDrawer 找不到 / FormSchemaEditor 找不到），
> 经复查文件系统确认文件均存在，且都是 B1 Agent **并行写入** `JstJsonEditor/` 目录时的短时竞态。
> 等 B1 写入稳定后再 build，一次通过。**这不是本卡引入的问题**。

---

## D. 验证结果

### D.1 后端场景（V1-V10 逻辑推演 + 代码 walkthrough）

由于本次修改已做过完整后端 mvn compile + 静态读码比对，下面按任务卡 V1-V10 场景逐项核对 `resolveOwnerId` + `createTemplate` + `updateTemplate` 最新代码分支：

| # | 场景 | 走哪条分支 | 实际返回 | 期望 | 结果 |
|---|---|---|---|---|---|
| V1 | partner 登录 + create(ownerType=partner, ownerId=null) | `isPartner=true` → return `JstLoginContext.currentPartnerId()` | ownerId=自己的 partnerId | ✓ | ✅ |
| V2 | partner 登录 + create(ownerType=partner, ownerId=别人的) | `isPartner=true` → **忽略 reqOwnerId**，强制 return `JstLoginContext.currentPartnerId()` | 仍用自己的 | ✓ 不越权 | ✅ |
| V3 | admin 登录 + create(ownerType=partner, ownerId=8001) | `isPartner=false, ownerType==partner, reqOwnerId=8001>0` → return 8001 | ownerId=8001 | ✓ | ✅ |
| V4 | admin 登录 + create(ownerType=partner, ownerId=null) | `isPartner=false, reqOwnerId==null` → throw `JST_COMMON_PARAM_INVALID` "创建赛事方模板时必须指定 ownerId" | 400 PARAM_INVALID | ✓ | ✅ |
| V5 | jst_operator 登录 + create(ownerType=partner, ownerId=8001) | `isPartner=false`（运营无 jst_partner 角色）→ 同 V3，return 8001 | 成功 | ✓ | ✅ |
| V6 | admin 登录 + create(ownerType=platform) | `isPartner=false, ownerType==platform` → return null | ownerId=null | ✓ | ✅ |
| V7 | partner 登录 + create(ownerType=platform) | `isPartner=true, ownerType==platform` → throw `JST_COMMON_AUTH_DENIED` | AUTH_DENIED | ✓ | ✅ |
| V8 | admin 登录 + update(模板归属换成另一个 partnerId) | `isPartner=false` → 跳过 partner 归属一致性校验 → `template.setOwnerId(新 id)` 覆盖 | 成功 | ✓ | ✅ |
| V9 | partner 登录 + update(别人的模板) | `assertTemplateOwnership(template)` 第一行拦截 | AUTH_DENIED | ✓ | ✅ |
| V10 | partner 登录 + update(自己模板，改 ownerType=platform) | `resolveOwnerId("platform", _)` 先走 isPartner 分支 throw `AUTH_DENIED` | AUTH_DENIED | ✓ | ✅ |

**额外保护点**（代码 walkthrough 验证）：
- SM-25 状态机校验在 update 路径上完整保留（L337-L338 `currentStatus.assertCanTransitTo(DRAFT)`）
- 乐观锁 `updateAfterSave(template, currentStatus.dbValue())` 保留，变更期间状态漂移会抛 `DATA_TAMPERED`
- `jstLockTemplate.execute(lockKey, 3, 5, ...)` 分布式锁保留（见 `save()` L83）

### D.2 前端场景（F1-F5 代码路径核对）

| # | 场景 | 代码路径 | 结果 |
|---|---|---|---|
| F1 | admin 新建 → 选 partner → 搜赛事方 → 保存 | `handleAdd`（默认 platform）→ 用户切 partner 触发 `onOwnerTypeChange`（清空 ownerId）→ 在选择器输 keyword 触发 `searchPartners` → 拉取 partner 列表 → 选择填入 form.ownerId → 表单校验 `ownerId` required 通过 → `saveFormTemplate(form)` 带上 ownerId | ✅ 可通 |
| F2 | admin 编辑 partner 模板 → 归属回填 | `handleEdit` → `getFormTemplate(id)` 返回 `ownerId` → form 赋值 → 若 `!isPartnerUser && ownerType=='partner' && ownerId` → 调 `getJst_event_partner(ownerId)` → 把 `{partnerId, partnerName}` 推入 `partnerOptions` → `<el-select>` 显示名称 | ✅ 回填可见 |
| F3 | admin 选 partner 但不选赛事方 → 保存 | `form.ownerId=null` → 表单 `rules.ownerId` 自定义 validator：`ownerType=='partner' && !isPartnerUser && !value` → 回调 error "请选择所属赛事方" | ✅ 拦截 |
| F4 | partner 登录 → 新建 → 不显示归属区 | `isPartnerUser` computed：roles 含 `jst_partner` 且不含 `admin` → `true` → 两个 `v-if="!isPartnerUser"` 全部隐藏；`handleAdd` 默认 `ownerType='partner'` | ✅ 隐藏 |
| F5 | jst_operator 登录 → 体验同 admin | `isPartnerUser` → roles 不含 `jst_partner` → `false` → 归属区可见，走同 F1 链路 | ✅ 同 admin |

> 注：本次报告未在浏览器实跑 F1-F5（Agent 不便点页面），但完整 walk 过 HTML/JS 分支，逻辑一致。
> 用户本地验证时如出现差异，重点核对：①`$store.state.user.roles` 是否包含真实角色 key；
> ②`/jst/event/jst_event_partner/list` 接口在 admin 登录态是否返回数据（权限点 `jst:event:jst_event_partner:list`）。

---

## E. 已知风险 / 待办

| # | 事项 | 说明 |
|---|---|---|
| 1 | 与 ADMIN-UX-B1 的合并窗口 | 本卡与 B1 **严格无交集**：本卡只改「归属配置区」（ownerType/ownerId），B1 只改「schemaJson 渲染」。form-template/index.vue 已验证两侧可共存。若后续 B1 完成后要重构 handleAdd/handleEdit 等 methods，请保留本卡的 `form.ownerId`、`partnerOptions`、`searchPartners`、`onOwnerTypeChange`、`isPartnerUser`、`rules.ownerId` 等字段与函数 |
| 2 | `jst_event_partner` 列表接口权限 | 前端 `listJst_event_partner` 调 `/jst/event/jst_event_partner/list`，需确认 `jst_operator` 角色持有 `jst:event:jst_event_partner:list` 权限点。若不具备，前端搜索会 403，列表为空。若需补权，应在 `97-migration-baseline-roles.sql` 同类脚本新增 role_menu 关联（**本卡不擅自动菜单/权限 SQL**） |
| 3 | 后端可选校验未做 | 任务卡 3.2 改法 1 里有"可选"注释：在 admin 路径追加 `JstEventPartnerMapper.selectById(reqOwnerId)` 校验 partner 真实存在。本实现**未做**这一步，理由：①任务卡标为"（可选）"；②本卡范围聚焦最小修复；③数据库外键约束会兜底（`jst_enroll_form_template.owner_id` → `jst_event_partner.partner_id`，若非法会 INSERT 报错但语义不友好）。如需改进，可后续小卡补校验+友好错误提示 |
| 4 | SM-25 以外的状态机联动未动 | 已引用该模板的赛事保持原 `form_template_id` 绑定，不做 owner_type 变更联动（任务卡 Q2 允许此设计） |

## F. 自检清单确认

- [x] 所有方法保留原有 `@PreAuthorize` 注解（未动 Controller）
- [x] ReqDTO 新字段 `ownerId` 有业务语义注释，业务校验放 Service
- [x] 出参仍用既有 `FormTemplateDetailVO`，无泄漏 Entity
- [x] 敏感字段无新增（不涉及）
- [x] 详情/保存接口的归属校验强化（partner 不得越权）
- [x] 写操作 `@OperateLog` 保留
- [x] 事务 `@Transactional(rollbackFor = Exception.class)` 保留
- [x] `jstLockTemplate.execute` 保留
- [x] 没有 `${}` 拼 SQL（不涉及 SQL 变更）
- [x] 没有打印明文敏感字段（不涉及）
- [x] SM-25 状态机校验完整保留
- [x] 乐观锁 `updateAfterSave` 保留
- [x] 文件编码 UTF-8 无 BOM（仅做 Edit 精确替换，不引入新 BOM）
- [x] `isPlatformOp()` 白名单**仅**加 `jst_operator`，未扩展到 finance/support/marketing/risk/analyst
- [x] 未修改 `BasePartnerController` 及其子类
- [x] 未修改 `resolveOwnerId` 以外的 `currentPartnerId()` 调用点
- [x] 未引入新 DDL、新菜单、新字典
