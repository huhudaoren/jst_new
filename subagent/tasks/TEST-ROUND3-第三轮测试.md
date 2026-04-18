# 任务卡 TEST-ROUND3 — 第三轮全量回归测试

> 派发日期：2026-04-16
> 基线：TEST-ROUND2 健康度 admin 93% / wx 98%
> 目标：验证 **傻瓜化重构 11 卡 + 收尾 4 卡 + 管理端精品化 40 页 + 菜单重构 + 8 角色体系** 全部落地后的系统健康度

---

## 零、背景

自 TEST-ROUND2 以来，系统发生了大规模变更：

| 批次 | 内容 |
|---|---|
| **REFACTOR-1~11** | DDL 重构（7 张子表 + 16 新字段）/ 字典扩充 / 赛事编辑 8 Tab / 证书拖拽设计器 / 审核打分一体化 / 编号引擎 / 菜单精简 / 动态表单渲染 / 预约三态容量 / C 端视觉重塑 / 成绩证书渲染 |
| **REFACTOR 收尾** | 后端 6 接口补齐（团队报名 / VO 字段 / 预约 / 表单 / 证书 / 小程序码）+ 证书客户端渲染引擎 + 评审过滤 + 旧组件清理 + NOT NULL 默认值 + Mapper 字段映射 |
| **ADMIN-POLISH 1/2/3** | 管理端 40 页全量精品化 + 16 字典类型 99 条数据 + JSON 可视化 |
| **ADMIN-FIX-FINAL** | 路由末段匹配 + 功能导航 + 渠道链路 + 参赛档案接口 |
| **菜单重构** | 赛事管理中心(9700) 与 平台数据管理(9800) 职责分离 + 首页重定向运营看板 |
| **角色体系** | 8 角色（admin/operator/finance/support/marketing/risk/analyst/partner）|
| **Pinia 修复** | `store/index.js` 显式 `setActivePinia` 解决小程序 onShow 早于根实例问题（2026-04-16） |
| **WXSS 修复** | `order-detail.vue` 删除 `:deep()` 死规则（2026-04-16） |

---

## 一、系统提示词

使用 `subagent/TEST_AGENT_PROMPT.md`（含反幻觉准则 + Layer 1/2/3）

---

## 二、本轮重点验证项

### 2.1 REFACTOR 核心链路验证（Layer 1 + Layer 2 + DB 回查）

#### A. 赛事编辑 8 Tab 傻瓜化（REFACTOR-1/3）

| # | 验证点 | 方法 |
|---|---|---|
| A1 | `jst_contest` 16 新字段 DB 列存在 | `DESC jst_contest` 确认 banner / capacity / team_size / verify_mode / organizer 等字段 |
| A2 | 7 子表 DDL 存在 | `SHOW TABLES LIKE 'jst_contest_%'` 预期返回 schedule/awards/faq/recommend/prize/attachment/venue |
| A3 | admin 创建赛事时 8 Tab 数据级联保存 | POST `/jst/admin/contest` + 验证所有子表 INSERT |
| A4 | 赛事复制接口 | POST `/jst/admin/contest/{id}/copy` 验证子表全量复制 |
| A5 | `JstContestMapper.xml` 21 字段映射 | Layer 1 grep 确认 `<result column=...>` 条数 |
| A6 | wx 赛事详情返回 recommend/awards/faq/schedule 子表数据 | GET `/jst/wx/contest/{id}` JSON 含 4 个子数组 |

#### B. 证书设计器 + 客户端渲染（REFACTOR-5 + REFACTOR-TODO-CERT-RENDER）

| # | 验证点 | 方法 |
|---|---|---|
| B1 | 证书模板 CRUD | POST/PUT/GET `/jst/admin/cert/template` |
| B2 | 保存 `layoutJson`（Fabric.js 序列化） | POST 时 layoutJson 字段长度 > 100 |
| B3 | wx 获取证书返回 `layoutJson` | GET `/jst/wx/cert/{id}` 响应含 `layoutJson` 字段 |
| B4 | `cert-renderer.js` 存在 | Layer 1 确认 `jst-uniapp/utils/cert-renderer.js` 非空 |
| B5 | `pages-sub/my/cert-detail.vue` 调用 cert-renderer | Layer 1 grep `import.*cert-renderer` |

#### C. 审核打分一体化（REFACTOR-4）

| # | 验证点 | 方法 |
|---|---|---|
| C1 | 评审老师 CRUD | POST/GET `/jst/admin/contest/reviewer` |
| C2 | 评审老师过滤：仅返回本赛事 reviewer | GET `/jst/admin/enroll/review/reviewers?contestId={id}` |
| C3 | 审核 + 打分接口合并 | POST `/jst/admin/enroll/{id}/review-with-score` body 含 `status + score + comment` |
| C4 | `jst_contest_reviewer` 表存在 | `DESC jst_contest_reviewer` |

#### D. 编号引擎（REFACTOR-6）

| # | 验证点 | 方法 |
|---|---|---|
| D1 | 2 编号表存在 | `SHOW TABLES LIKE 'jst_biz_no_%'` 预期 rule + sequence |
| D2 | 3 预置规则存在 | `SELECT rule_code FROM jst_biz_no_rule` 预期含 cert/auth/enroll |
| D3 | 证书生成时调用编号引擎 | POST `/jst/admin/cert/generate` 返回 `certNo` 格式匹配规则 |
| D4 | Redis INCR 并发安全 | 模拟 3 次连续请求，确认 certNo 递增无重复 |

#### E. 动态表单渲染（REFACTOR-8）

| # | 验证点 | 方法 |
|---|---|---|
| E1 | wx 报名页调用动态表单组件 | Layer 1 grep `jst-form-render` in `pages-sub/contest/enroll.vue` |
| E2 | 旧 `jst-form-render` 组件已删 | Layer 1 `ls components/jst-form-render` 预期不存在旧版本 |
| E3 | 11 字段类型支持 | 读 `components/jst-form-render/jst-form-render.vue` 确认 switch/case 覆盖 11 类 |
| E4 | 文件上传字段走 OSS 或本地缓存 | 找 `upload` 字段 handler |

#### F. 预约三态容量 + 团队报名（REFACTOR-9 + REFACTOR-TODO-P0）

| # | 验证点 | 方法 |
|---|---|---|
| F1 | 预约时间段接口 | GET `/jst/wx/appointment/slots?contestId={id}` |
| F2 | 团队报名接口 | POST `/jst/wx/enroll/team` body 含 teamName + members[] |
| F3 | fixture 含团队赛事 8206 + 时间段数据 | DB 查 `jst_appointment_slot WHERE contest_id=8206` |
| F4 | 容量三态（未开/可约/约满） | GET slots 返回 `status` 字段 |

#### G. C 端视觉重塑（REFACTOR-10）

| # | 验证点 | 方法 |
|---|---|---|
| G1 | 15 页全部通过 Layer 1 扫描无硬编码色 | `grep -rn "#[0-9a-fA-F]\{6\}" pages-sub/` 预期低于 10 条 |
| G2 | Design Token 15 变量生效 | 读 `styles/design-tokens.scss` 行数 ≥ 65 |
| G3 | 8 组件齐全 | `ls components/` 核心组件不缺 |

#### H. 成绩证书（REFACTOR-11）

| # | 验证点 | 方法 |
|---|---|---|
| H1 | 成绩雷达图页面存在 | `pages-sub/my/score-detail.vue` |
| H2 | 证书海报 Canvas | `pages-sub/my/cert-detail.vue` 含 canvas 节点 |
| H3 | 成绩查询公开页 | GET `/jst/public/score/query` + `/jst/public/cert/verify` |

---

### 2.2 ADMIN-POLISH 链路（Layer 1 + Layer 3）

| # | 验证点 |
|---|---|
| I1 | 40 页精品化清单存在且非空（参照任务报告 ADMIN-POLISH-BATCH1/2/3） |
| I2 | 10 Mapper LEFT JOIN 生效（ADMIN-POLISH-BE）：admin 列表页返回的 `xxxName` 字段非空 |
| I3 | 管理端首页重定向到 `/index/dashboard`（登录后默认落地页） |
| I4 | 菜单分组：赛事管理中心(9700) + 平台数据管理(9800) 在数据库 `sys_menu` 中分别挂根 |
| I5 | 16 字典类型 99 条数据：`SELECT COUNT(*) FROM sys_dict_type WHERE dict_type LIKE 'jst_%'` ≥ 预期 |
| I6 | JSON 可视化编辑器组件存在：`ruoyi-ui/src/views/jst/**/JsonEditor.vue` 或类似 |
| I7 | 参赛档案 Controller：GET `/jst/admin/participant/list` 可访问 |
| I8 | 路由末段匹配：访问 `/partner/contest/list` 不再 404 |

---

### 2.3 8 角色体系权限矩阵（Layer 2）

**重要**：管理端 6 业务角色用若依标准登录（`POST /login`，username + password），**不是** MOCK_xxx（MOCK 只给小程序 wx 侧）。

测试账号已通过 `99-migration-business-roles.sql` 写入，密码统一 `admin123`：

| # | 角色 | 登录方式 | 用户名/密码 | 必须可访问 | 必须被拒(403) |
|---|---|---|---|---|---|
| 1 | admin | POST /login | admin / admin123 | 全部 | — |
| 2 | jst_operator | POST /login | jst_operator / admin123 | /jst/admin/contest/*, /order/*, /user/*, /notice/*, /message/* | /jst/admin/finance/*, /risk/* |
| 3 | jst_finance | POST /login | jst_finance / admin123 | /jst/admin/finance/*, /payout/*, /settlement/*, /order/*/list | /contest/create, /marketing/* |
| 4 | jst_support | POST /login | jst_support / admin123 | /jst/admin/user/list, /message/*, /participant/list | /contest/delete, /finance/* |
| 5 | jst_marketing | POST /login | jst_marketing / admin123 | /jst/admin/coupon/*, /benefit/*, /points/*, /mall/* | /finance/*, /risk/* |
| 6 | jst_risk | POST /login | jst_risk / admin123 | /jst/admin/risk/*, /user/list | /finance/*, /marketing/* |
| 7 | jst_analyst | POST /login | jst_analyst / admin123 | 全部 /list, /query, /export | 全部 POST/PUT/DELETE (变更操作) |
| 8 | jst_partner | POST /login | partner_f7_a / admin123 | /jst/partner/* | /jst/admin/* |

> 若有角色返回 403 与预期不符，记录差异（可能是菜单权限绑定不完整），不要改数据修 fix

### 2.3b 小程序端 MOCK 登录验证（Layer 2）

wx 端用 `POST /jst/wx/auth/wx-login` body `{ code: "MOCK_xxx" }`：

| MOCK code | 对应 fixture user_id | 角色 | 场景 |
|---|---|---|---|
| MOCK_1001 | 1001（parent，已绑渠道 2001） | jst_student | 学生侧全链路 |
| MOCK_1003 | 1003（channel，认证已通过） | jst_student + jst_channel | 渠道侧返点/学生管理 |
| MOCK_1004 | 1004（channel） | jst_student + jst_channel | 第二渠道侧

---

### 2.4 回归：wx-tests.http + admin-tests.http 全量

- 按 TEST-ROUND2 同样分段，逐 `###` 执行
- 对比 ROUND2 通过率（admin 93% / wx 98%），标记**新增失败**和**新增通过**
- 特别关注：
  - 所有涉及 `jst_contest` 的接口（因 16 新字段 + Mapper 重构）
  - 所有涉及 enroll 审核的接口（因打分一体化）
  - 所有涉及 cert 的接口（因 layoutJson 新增）

---

### 2.5 Layer 1 静态扫描（低成本高收益）

```bash
# 1. 小程序端 :deep() 残留（上轮修复验证）
grep -rn ":deep(" jst-uniapp/ --include="*.vue"  # 预期 0

# 2. 中文引号 / 全角字符在代码中
grep -rn "“\|”\|‘\|’" jst-uniapp/src jst-uniapp/api --include="*.js"  # 预期 0

# 3. Toast 占位 "后续开放"
grep -rn "后续开放" jst-uniapp/ --include="*.vue" --include="*.js" | grep -v unpackage  # 预期 0

# 4. 硬编码色 (排除 design-tokens.scss)
grep -rn "#[0-9a-fA-F]\{6\}" jst-uniapp/pages --include="*.vue" | wc -l  # 预期 < 20

# 5. admin 端 TODO / FIXME
grep -rn "TODO\|FIXME" ruoyi-ui/src/views/jst --include="*.vue"  # 记录条数

# 6. select * 残留
grep -rn "SELECT \*\|select \*" RuoYi-Vue/jst-*/src --include="*.xml"  # 预期 ≤ 15 条（已有标注）
```

---

### 2.6 Layer 3 前端访问（若启动）

- H5 预览：`http://localhost:8081/#/pages/login/login` 不再报 Pinia 错（本轮重点！）
- admin 管理端：登录后自动跳 `/index/dashboard`
- 随机抽 10 个精品化页面截屏验证无白屏

---

## 三、运行环境（本轮全部使用**本地**，不用测试服务器）

- 后端：`http://127.0.0.1:8080`（启动前先 `mvn clean install -DskipTests && 重启`，profile=dev）
- 小程序前端：`http://localhost:8081/#/`（HBuilderX H5 预览）
- 管理端：`http://localhost:80` 或 `http://localhost:1024`（ruoyi-ui）
- 测试文件：`test/wx-tests.http` + `test/admin-tests.http`
- **DB**：本地 MySQL `jdbc:mysql://127.0.0.1:3306/jst_new`
  - username: `root`
  - password: `123456`
  - 验证类查询可以直连（只读），禁止对业务表 UPDATE/DELETE
- Redis：本地 `localhost:6379`（无密码）
- 前置确认：
  1. 本地 DB `jst` 已按 `架构设计/ddl/00-rebuild-local-db.sql` **重建完毕**（包含所有 REFACTOR 子表 + 字典 + 6 业务角色 + 对应 sys_user 测试账号）
  2. 本地 Redis 已启动（编号引擎 INCR 依赖）
  3. 本地后端以 `dev` profile 启动（连本地 MySQL 127.0.0.1，不连测试服务器）
  4. 管理端登录账号密码统一 `admin123`（详见 §2.3）

---

## 四、输出

报告路径：`subagent/tasks/任务报告/TEST-ROUND3-回答.md`

必须包含：

1. **§总览**：健康度 admin X% / wx Y% + 与 ROUND2 对比
2. **§REFACTOR 验证矩阵**：A/B/C/D/E/F/G/H 8 组 × 3~6 项，逐条 ✅/❌/⏸️
3. **§ADMIN-POLISH 验证矩阵**：I1~I8 逐条
4. **§角色权限矩阵**：8 角色 × N 接口表格
5. **§回归差异**：ROUND2 ↔ ROUND3 新增失败 / 新增通过
6. **§静态扫描结果**：§2.5 六项 grep 输出数
7. **§链路闭环判定**：学生闭环 / 渠道闭环 / 赛事方闭环 各出结论
8. **§Top N 阻塞问题**：按严重度排序，每项给修复建议

---

## 五、不许做

- ❌ 不许改任何代码（只测不改，发现问题写报告）
- ❌ 不许编造任何测试结果（反幻觉准则）
- ❌ 不许跳过 REFACTOR 任一组验证
- ❌ 不许对无 fixture 的角色伪造测试结果（标 ⏸️ 即可）

---

派发时间：2026-04-16
