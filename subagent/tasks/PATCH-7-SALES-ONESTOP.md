# PATCH-7 — 销售管理一站式（事务建账号 + 业务字段）

> 优先级：**🟡 P1** | 预估：**S（4-6h）** | Agent：**Web Admin Agent**（跨栈）
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 起因：用户反馈"为什么销售管理要单独建一个菜单，而不是用若依用户管理？销售就是一个部门"——经评估**表结构正确不动**，但操作体验确实割裂（运营要双开 `/system/user` 和 `/group-user/sales` 两个页面）。本卡修体验。

---

## 一、问题与目标

**问题**：当前销售管理后台 `POST /admin/sales` 的 `SalesCreateReqDTO` 要求传 `sysUserId`——即**前提是销售对应的 sys_user 已经在若依用户管理页建好**。运营实际工作要：

1. 跳到 `/system/user` → 新建账号（输用户名/手机/初始密码/绑销售角色）→ 记下 user_id
2. 跳回 `/group-user/sales` → 「新增销售」→ 手输刚才的 user_id + 业务字段

**两个页面、要记 ID、易出错**。

**目标**：销售管理页**一站完成**「新建销售（自动建 sys_user）」+ 「编辑销售（业务字段在主表，账号字段在折叠区）」+ 「停用销售（同步停 sys_user）」。

**不动**：jst_sales 表结构 / SalesScope 切面 / 提成管线 / 离职 4 状态机 / 已落地的 84 单测。

---

## 二、必读上下文

1. **后端**：
   - `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/admin/AdminSalesController.java`（**新加 onestop 端点**）
   - `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/SalesCreateReqDTO.java`（保留兼容；新加 `SalesCreateOnestopReqDTO`）
   - `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/SalesService.java` + `impl/SalesServiceImpl.java`（**加 createOnestop 方法**）
   - `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/util/SalesScopeUtils.java`（看 `ROLE_SALES`/`ROLE_SALES_MANAGER` 常量）
   - 跨模块依赖 sys_user：用 `com.ruoyi.system.service.ISysUserService`（jst-channel pom 已经依赖 ruoyi-system，grep 验证）
2. **前端**：
   - `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/index.vue`（**销售管理列表页，主战场**）
   - `RuoYi-Vue/ruoyi-ui/src/api/...`（grep 找 `admin/sales` 的 API 文件，加 `createOnestop`）
3. **跨模块**：
   ```bash
   # jst-channel 是否已依赖 ruoyi-system
   grep -A2 "ruoyi-system" RuoYi-Vue/jst-channel/pom.xml
   ```
   如果没有，**不要加**（架构红线 jst 模块禁止跨依赖 ruoyi-system）。改用 `ISysUserService` 的反射或直接 SQL 兜底。先验证。

---

## 三、Step-by-Step

### Step 0：跨模块依赖确认

```bash
cd D:/coding/jst_v1
# 看 jst-channel 是否能直接 import ISysUserService
grep -rn "ISysUserService" RuoYi-Vue/jst-channel/src/main/java 2>&1 | head -3
# 如果有现成 import，可以照抄；如果没有，改用 jdbcTemplate 直接 INSERT sys_user
```

**Plan-02 SalesResignationService 已经为停用 sys_user 做过类似工作**（CLAUDE.md 提到「ISysUserService 跨模块问题：移除 sys_user 禁用代码 + TODO admin 手动」）。本卡就是来补完这个 TODO 的。

→ 推荐方案：**用 JdbcTemplate 直接 INSERT/UPDATE sys_user**（避开跨模块依赖，与 EntityBriefController 同款），保持 jst-channel 不依赖 ruoyi-system。

### Step 1：后端新 DTO

新文件 `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/SalesCreateOnestopReqDTO.java`：

```java
package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 销售管理一站式新建：同时建 sys_user 账号 + jst_sales 业务记录。
 * 事务边界在 SalesServiceImpl.createOnestop()。
 */
public class SalesCreateOnestopReqDTO {
    /** 账号字段 */
    @NotBlank(message = "登录用户名不能为空")
    @Size(min = 4, max = 30) @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母数字下划线")
    private String userName;

    @NotBlank(message = "初始密码不能为空")
    @Size(min = 6, max = 20)
    private String initPassword;

    /** 业务字段 */
    @NotBlank @Size(max = 64)
    private String salesName;        // 同时写 sys_user.nick_name + jst_sales.sales_name

    @NotBlank @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;            // 同时写 sys_user.phonenumber + jst_sales.phone

    private Long managerId;          // 直属主管 sales_id（可空）

    @NotNull
    @DecimalMin(value = "0.0000") @DecimalMax(value = "1.0000")
    private BigDecimal commissionRateDefault;  // 默认费率

    private Boolean asManager = Boolean.FALSE; // 是否设为主管

    // getters/setters 略...
}
```

### Step 2：后端 Service

`SalesService.java` 加方法：
```java
/**
 * 一站式新建：同事务里 INSERT sys_user + jst_sales + sys_user_role 绑销售角色。
 * @return 新建的 sales_id
 */
Long createOnestop(SalesCreateOnestopReqDTO req);
```

`SalesServiceImpl.java` 实现（**完整事务**）：
```java
@Autowired private JdbcTemplate jdbcTemplate;
@Autowired private PasswordEncoder passwordEncoder;  // 若依已有 bean

private static final String ROLE_KEY_SALES = "jst_sales";
private static final String ROLE_KEY_SALES_MANAGER = "jst_sales_manager";

@Override
@Transactional(rollbackFor = Exception.class)
public Long createOnestop(SalesCreateOnestopReqDTO req) {
    // 1) 校验 user_name 唯一
    Integer existCount = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM sys_user WHERE user_name = ? AND del_flag = '0'",
        Integer.class, req.getUserName());
    if (existCount != null && existCount > 0) {
        throw new ServiceException("用户名 " + req.getUserName() + " 已存在");
    }
    // 2) 校验手机号在 jst_sales 唯一（避免重复销售）
    Integer phoneCount = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM jst_sales WHERE phone = ?",
        Integer.class, req.getPhone());
    if (phoneCount != null && phoneCount > 0) {
        throw new ServiceException("手机号 " + req.getPhone() + " 已被其他销售占用");
    }
    // 3) INSERT sys_user
    String hashedPwd = passwordEncoder.encode(req.getInitPassword());
    Long currentUserId = SecurityUtils.getUserId();
    String currentUserName = SecurityUtils.getUsername();
    jdbcTemplate.update(
        "INSERT INTO sys_user (user_name, nick_name, phonenumber, password, status, del_flag, create_by, create_time) " +
        "VALUES (?, ?, ?, ?, '0', '0', ?, NOW())",
        req.getUserName(), req.getSalesName(), req.getPhone(), hashedPwd, currentUserName);
    // 取新 user_id
    Long newUserId = jdbcTemplate.queryForObject(
        "SELECT user_id FROM sys_user WHERE user_name = ? AND del_flag = '0'",
        Long.class, req.getUserName());

    // 4) INSERT sys_user_role 绑销售角色
    String roleKey = Boolean.TRUE.equals(req.getAsManager()) ? ROLE_KEY_SALES_MANAGER : ROLE_KEY_SALES;
    Long roleId = jdbcTemplate.queryForObject(
        "SELECT role_id FROM sys_role WHERE role_key = ? AND status = '0' AND del_flag = '0' LIMIT 1",
        Long.class, roleKey);
    if (roleId == null) {
        throw new ServiceException("销售角色 " + roleKey + " 不存在，请先初始化角色");
    }
    jdbcTemplate.update("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)", newUserId, roleId);

    // 5) INSERT jst_sales（复用现有 create()）
    JstSales row = new JstSales();
    row.setSysUserId(newUserId);
    row.setSalesName(req.getSalesName());
    row.setPhone(req.getPhone());
    row.setManagerId(req.getManagerId());
    row.setCommissionRateDefault(req.getCommissionRateDefault());
    row.setIsManager(Boolean.TRUE.equals(req.getAsManager()) ? 1 : 0);
    return create(row);  // 复用既有逻辑（生成 sales_no 雪花/默认 status='active'/校验等）
}
```

**额外的停用销售联动**：在 `SalesServiceImpl` 找现有的 `executeResign(salesId)` 实现（plan-02 Task 11 落地的），看它是否真的禁用 sys_user。如果还是 TODO，**本卡顺手补上**：

```java
// executeResign 末尾追加（如果尚未做）
JstSales sales = getById(salesId);
if (sales != null && sales.getSysUserId() != null) {
    jdbcTemplate.update(
        "UPDATE sys_user SET status = '1', update_time = NOW() WHERE user_id = ?",
        sales.getSysUserId());
}
```

> ⚠️ 如果 plan-02 已经做了，**不要重复**——grep `SalesResignationService` 看实际实现。本卡只补 onestop 创建，不强制扩到 resign 流程（避免越界）。

### Step 3：后端 Controller

`AdminSalesController.java` 加端点：
```java
@PostMapping("/onestop")
@PreAuthorize("@ss.hasPermi('jst:sales:add')")
@Log(title = "销售一站式新建", businessType = BusinessType.INSERT)
public AjaxResult createOnestop(@Valid @RequestBody SalesCreateOnestopReqDTO req) {
    Long salesId = salesService.createOnestop(req);
    return AjaxResult.success(salesId);
}
```

**保留**原 `POST /admin/sales`（基于已有 sysUserId 创建）—— 避免破坏 plan-02 的测试和外部脚本。

### Step 4：前端列表页

文件 `RuoYi-Vue/ruoyi-ui/src/views/jst/sales/index.vue`。

#### 4.1 改「新增销售」按钮的 dialog

把现有的 `sysUserId` 输入字段**整个删掉**，改成账号字段：

```vue
<el-dialog title="新增销售" :visible.sync="onestopOpen" width="560px">
  <el-form ref="onestopForm" :model="onestopForm" :rules="onestopRules" label-width="120px">
    <el-divider content-position="left">账号信息（自动建 sys_user）</el-divider>
    <el-form-item label="登录用户名" prop="userName">
      <el-input v-model="onestopForm.userName" placeholder="字母/数字/下划线 4-30 位" />
    </el-form-item>
    <el-form-item label="初始密码" prop="initPassword">
      <el-input v-model="onestopForm.initPassword" show-password placeholder="6-20 位" />
      <div style="color:#909399;font-size:12px;">新销售首次登录后请提示修改密码</div>
    </el-form-item>

    <el-divider content-position="left">销售业务字段</el-divider>
    <el-form-item label="销售姓名" prop="salesName">
      <el-input v-model="onestopForm.salesName" placeholder="将同时写入 sys_user.昵称" />
    </el-form-item>
    <el-form-item label="手机号" prop="phone">
      <el-input v-model="onestopForm.phone" placeholder="11 位手机号" />
    </el-form-item>
    <el-form-item label="默认费率" prop="commissionRateDefault">
      <el-input-number v-model="onestopForm.commissionRateDefault" :min="0" :max="1" :step="0.01" :precision="4" />
      <span style="margin-left:8px;color:#909399;font-size:12px;">{{ (onestopForm.commissionRateDefault * 100).toFixed(2) }}%</span>
    </el-form-item>
    <el-form-item label="是否设为主管" prop="asManager">
      <el-switch v-model="onestopForm.asManager" />
      <div style="color:#909399;font-size:12px;">主管自动绑 jst_sales_manager 角色，能看下属团队</div>
    </el-form-item>
    <el-form-item v-if="!onestopForm.asManager" label="所属主管" prop="managerId">
      <sales-picker v-model="onestopForm.managerId" placeholder="可选，选择直属销售主管" />
    </el-form-item>
  </el-form>
  <span slot="footer">
    <el-button @click="onestopOpen=false">取消</el-button>
    <el-button type="primary" @click="submitOnestop" :loading="onestopSubmitting">确定</el-button>
  </span>
</el-dialog>
```

#### 4.2 script 加表单校验和提交

```javascript
data() {
  return {
    // ... 已有
    onestopOpen: false,
    onestopSubmitting: false,
    onestopForm: { userName: '', initPassword: '', salesName: '', phone: '',
                   commissionRateDefault: 0.05, asManager: false, managerId: null },
    onestopRules: {
      userName: [{ required: true, message: '请输入用户名', trigger: 'blur' },
                 { pattern: /^[a-zA-Z0-9_]{4,30}$/, message: '4-30 位字母数字下划线', trigger: 'blur' }],
      initPassword: [{ required: true, message: '请输入初始密码', trigger: 'blur' },
                     { min: 6, max: 20, message: '6-20 位', trigger: 'blur' }],
      salesName: [{ required: true, message: '请输入销售姓名', trigger: 'blur' }],
      phone: [{ required: true, message: '请输入手机号', trigger: 'blur' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
      commissionRateDefault: [{ required: true, message: '请设置默认费率', trigger: 'blur' }]
    }
  }
},
methods: {
  // ... 已有
  openOnestop() { this.onestopOpen = true; this.$nextTick(() => this.$refs.onestopForm?.resetFields()) },
  async submitOnestop() {
    await this.$refs.onestopForm.validate()
    this.onestopSubmitting = true
    try {
      const res = await createSalesOnestop(this.onestopForm)
      this.$modal.msgSuccess(`销售已创建（ID: ${res.data}），账号 ${this.onestopForm.userName} 可立即登录`)
      this.onestopOpen = false
      this.getList()
    } catch (e) {
      // toast 由 request 拦截器处理
    } finally {
      this.onestopSubmitting = false
    }
  }
}
```

#### 4.3 把列表页原「新增」按钮改 click 到 openOnestop

```vue
<el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openOnestop"
           v-hasPermi="['jst:sales:add']">新增销售</el-button>
```

### Step 5：前端 API 文件

找现有 `admin/sales` API 文件（grep `admin/sales` 在 `src/api/` 下）。在末尾追加：
```javascript
export function createSalesOnestop(data) {
  return request({ url: '/admin/sales/onestop', method: 'post', data })
}
```

### Step 6：列表页加「账号状态」列 + 「停用」联动（可选，本卡推荐做）

列表加一列显示 sys_user.status，并在销售操作菜单里加「停用账号」按钮调若依现有 `PUT /system/user/changeStatus`。

如果时间紧，本步可跳过——核心是 Step 1-5。

---

## 四、验证

### V1 编译
```bash
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl jst-channel,ruoyi-admin -am clean compile -DskipTests
cd ruoyi-ui && npm run build:prod
```
全绿。

### V2 真实接口测（重启后端后）

```bash
TOKEN=$(curl -s -X POST "http://localhost:8080/login" -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","captchaEnabled":false}' \
  | python -c "import sys,json; print(json.load(sys.stdin).get('token',''))")

# happy path
curl -X POST "http://localhost:8080/admin/sales/onestop" \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{
    "userName": "sales_test_001",
    "initPassword": "Test@123",
    "salesName": "测试销售一号",
    "phone": "13800000001",
    "commissionRateDefault": 0.05,
    "asManager": false
  }'
# 期望：{"msg":"操作成功","code":200,"data":<salesId>}

# 验证 sys_user / sys_user_role / jst_sales 三张表都有数据
mysql -u root -p123456 jst -e "
  SELECT user_id, user_name, nick_name, phonenumber, status FROM sys_user WHERE user_name='sales_test_001';
  SELECT ur.user_id, r.role_key FROM sys_user_role ur JOIN sys_role r ON ur.role_id=r.role_id WHERE ur.user_id=(SELECT user_id FROM sys_user WHERE user_name='sales_test_001');
  SELECT sales_id, sys_user_id, sales_name, sales_no, status FROM jst_sales WHERE phone='13800000001';
"

# 反例：重名
curl -X POST "http://localhost:8080/admin/sales/onestop" -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{...同上...}'
# 期望：500 + msg "用户名 sales_test_001 已存在"

# 反例：手机号重复
curl -X POST "http://localhost:8080/admin/sales/onestop" -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "userName":"sales_test_002", "initPassword":"Test@123", "salesName":"测试二号",
        "phone":"13800000001", "commissionRateDefault":0.05, "asManager":false }'
# 期望：500 + msg "手机号 13800000001 已被其他销售占用"

# 验证事务回滚：用错误数据触发 jst_sales 校验失败，看 sys_user 是否回滚
# （比如 managerId 指向不存在的 sales_id 时 SalesServiceImpl.create 会抛）
```

测完用 mysql 清理测试数据。

### V3 浏览器手测

启动 dev server 进 admin 后台：
1. 登录 → 进 `/group-user/sales`
2. 点「新增销售」→ 表单填全 → 提交 → toast 成功 + 列表刷新出现新销售
3. 退出 admin，用 `sales_test_001 / Test@123` 登录 → 应能进入销售工作台
4. 验证下「主管」开关 on/off 角色绑定不同
5. 用错误数据测重名/手机重复，看错误提示

---

## 五、DoD

- [ ] Step 0 跨模块依赖确认，决定用 JdbcTemplate 还是 ISysUserService（推荐 JdbcTemplate）
- [ ] 后端：DTO + Service.createOnestop + Controller `/admin/sales/onestop`
- [ ] 前端：列表页「新增销售」改一站式 dialog + API 函数
- [ ] mvn + npm build:prod 都绿
- [ ] V2 happy + 2 反例 curl 全过 + mysql 验三表数据正确
- [ ] V3 浏览器手测：新建 → 用新账号能登录 → 进销售工作台
- [ ] 测试数据清理（DELETE FROM jst_sales/sys_user_role/sys_user WHERE phone=测试号 / user_name=测试名）
- [ ] commit：`feat(admin): PATCH-7 销售管理一站式（事务建 sys_user + jst_sales）`
- [ ] 报告写到 `subagent/tasks/任务报告/PATCH-7-SALES-ONESTOP-报告.md`

---

## 六、红线

- ❌ **不许动 jst_sales 表结构**
- ❌ **不许动 SalesScope 切面 / 提成管线 / 离职 4 状态机**
- ❌ **不许删 / 改原 `POST /admin/sales` 端点**（保留兼容性）
- ❌ **不许引入 jst-channel → ruoyi-system 的 maven 依赖**（如未存在），用 JdbcTemplate
- ❌ **不许跳过手机号唯一性校验**（避免重复销售）
- ❌ **不许把初始密码明文存库**，必须 `passwordEncoder.encode()`
- ❌ **不许在 onestop 接口里塞业务逻辑爆炸**（角色绑定 / 默认密码策略 / 部门归属等扩展，统一走 Service 内部，Controller 只收参）
- ❌ V2/V3 测试数据**必须清理**

---

## 七、派发附言

如果 Step 0 发现 jst-channel 已经依赖 ruoyi-system（PR 历史中可能加过），优先用 `ISysUserService.insertUserAuth(...)` 之类的现成方法（更标准，会走若依的 password encoder + 角色绑定逻辑）。但若没依赖**坚决不加**，本卡用 JdbcTemplate 完全够用。

如果发现 `executeResign` 已经禁用了 sys_user（plan-02 Task 11/15 已实现），本卡 Step 2 末尾的"额外停用联动"**跳过**。

派发时间：2026-04-18 | 主 Agent：竞赛通架构师
