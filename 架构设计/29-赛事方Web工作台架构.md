# 29. 赛事方 Web 工作台架构

> PRD V4.1 第 5 个终端形态：「赛事方 Web 工作台」
> 用户：赛事方运营/审核人员（角色 `jst_partner`）
> 数据范围：**只能看自己的赛事**（@PartnerScope 强制过滤）

---

## 1. 工程选型决策

### 三种实现方式

| 方案 | 实现 | 优 | 缺 | 推荐度 |
|---|---|---|---|---|
| **A. 复用 RuoYi-Vue admin 端** ⭐ | 在已有 PC 后台通过角色 `jst_partner` 隔离菜单与数据 | 工程零新增；登录/权限/UI 全套复用 | 与平台运营共用域名,品牌区分需自定义主题 | **推荐** |
| B. 独立 Vue3 工程 | 新建 jst-partner-web,Vue3+Vite,独立部署 | 视觉品牌完全独立 | 新工程维护;需重复实现表格/表单等组件 | 不推荐 |
| C. 嵌入小程序 | Uniapp 内 admin 风格分包 | 移动办公友好 | 复杂表单/导入导出体验差 | 不推荐 |

**决策：方案 A**。理由：
1. 已生成的 50 张表 RuoYi 后台已可看到
2. 赛事方需求（赛事 CRUD/报名审核/成绩录入/结算确认）都是表格 + 表单,PC 体验最好
3. 通过 `@PartnerScope` 切面已能做到数据隔离
4. 后期如需独立品牌,可以做 SaaS 子域名 + Nginx 路由

---

## 2. 落地实现细节（基于方案 A）

### 2.1 角色与权限

数据库已在 `23-基础数据初始化.sql` 中定义：
```sql
INSERT INTO sys_role VALUES (101, '赛事方账号', 'jst_partner', 20, '1', ...);
```

赛事方账号通过 `jst_event_partner` 表 → `user_id` 字段关联到 `sys_user`,登录后自动持有 `jst_partner` 角色。

### 2.2 数据权限切面

`jst-common/scope/PartnerScope.java` 注解 + `DataScopeAspect.java` 切面已实现。
所有赛事方相关查询接口加注解：

```java
@PreAuthorize("@ss.hasPermi('jst:event:contest:list')")
@PartnerScope(field = "partnerId")     // 强制 partnerId = 当前用户的 partnerId
@GetMapping("/jst/event/contest/list")
public TableDataInfo list(ContestQueryReqDTO query) { ... }
```

切面会从 `JstLoginContext.currentPartnerId()` 取当前赛事方 ID,反射注入到 ReqDTO 的 partnerId 字段。

### 2.3 菜单隔离

通过 `sys_role_menu` 表关联,赛事方角色只关联赛事方需要的菜单：

```sql
-- 赛事方可见菜单 (在 23-基础数据初始化.sql 已部分定义)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(101, 2000),   -- 竞赛通业务总目录
(101, 2200),   -- 赛事与报名
(101, 2300),   -- 订单与资金 (只看与自己赛事相关的)
(101, 2900);   -- 财务中心 (结算单)
```

各二级菜单(具体表)的权限通过代码生成器自动入库后,运维在管理后台「角色管理 → 菜单权限」勾选即可。

### 2.4 赛事方可见的功能清单

| 一级目录 | 子功能 | Controller | 数据范围 |
|---|---|---|---|
| 赛事与报名 | 我的赛事列表/编辑/上下线 | JstContestController | partner_id = 当前 |
| 赛事与报名 | 报名记录列表/详情/审核 | JstEnrollRecordController | contest_id IN (我的赛事) |
| 赛事与报名 | 动态表单模板配置 | JstEnrollFormTemplateController | owner_id = partner_id |
| 赛事与报名 | 成绩录入/审核/发布 | JstScoreRecordController | contest_id IN |
| 赛事与报名 | 证书发放 | JstCertRecordController | contest_id IN |
| 订单与资金 | 我的赛事相关订单 | JstOrderMainController | partner_id = 当前 |
| 订单与资金 | 退款审核 | JstRefundRecordController | order.partner_id = 当前 |
| 订单与资金 | 团队预约/扫码核销 | JstTeamAppointmentController | contest_id IN |
| 财务中心 | 我的结算单 | JstEventSettlementController | partner_id = 当前 |
| 财务中心 | 我的合同 | JstContractRecordController | target=partner & target_id |
| 财务中心 | 我的发票 | JstInvoiceRecordController | target=partner & target_id |

**赛事方不可见**：
- 用户管理 / 渠道方管理
- 渠道返点 / 提现
- 积分商城
- 风控 / 审计
- 系统监控 / 系统工具

### 2.5 登录入口

PC 端 RuoYi 默认登录页 `/index` 即可,赛事方与平台运营**共用同一登录页**,通过角色隔离菜单。

如需要独立登录页(品牌区分):
```nginx
# nginx 路由
location /partner {
  proxy_pass http://localhost:8080/index;  # 内部还是同一个登录,前端显示赛事方 logo
}
```

未来如要独立子域名 partner.jst.example.com,前端 ruoyi-ui 加路由判断 + 替换主题色即可,后端零改动。

---

## 3. 数据隔离的关键点（强约束）

### 3.1 SQL 层兜底
所有 Mapper.xml 在 WHERE 中**不**写死 partnerId 过滤,由切面自动注入。但为了防御性,审计阶段必须人工 review 所有 Mapper.xml,确认敏感查询不会泄漏数据。

### 3.2 入参强校验
`@PartnerScope(field = "partnerId")` 切面会**强制覆盖** ReqDTO 的 partnerId 字段为当前用户的 partnerId,即使前端传了别的也无效。

### 3.3 详情接口越权防御
按 ID 取详情/编辑/删除时,Service 层**第一行**必须做归属校验：
```java
public ContestDetailVO getDetail(Long contestId) {
    Contest c = mapper.selectById(contestId);
    if (c == null) throw notFound();
    // 强制校验
    if (JstLoginContext.hasRole("jst_partner")) {
        Long currentPartner = JstLoginContext.currentPartnerId();
        if (!c.getPartnerId().equals(currentPartner)) {
            throw new ServiceException("无权访问该赛事", 403);
        }
    }
    return convert(c);
}
```

---

## 4. 赛事方账号生命周期

```
1. 公开申请页提交入驻申请 (jst_event_partner_apply)
   ├── 走小程序公开页 / H5 公开链接
   └── 不需要登录

2. 平台运营审核 → 通过
   ├── 创建 jst_event_partner 记录
   ├── 创建 sys_user (用户名 partner_xxx,初始密码邮件发送)
   ├── jst_event_partner.user_id = sys_user.user_id
   ├── 赋角色 jst_partner
   └── 通知审核结果(短信/邮件)

3. 赛事方首次登录 → 修改密码 → 进入工作台
   ├── 工作台首页显示待办(待审核报名/凭证)
   └── 引导上传公司资质 / 银行账户

4. 赛事方运营操作
   ├── 创建赛事 (contest 状态:draft → pending → approved → online → offline)
   ├── 配置动态表单
   ├── 审核报名材料
   ├── 录入成绩 / 发放证书
   ├── 接收结算单 → 确认 → 等打款
   └── 查看合同/发票
```

---

## 5. 与 jst-uniapp 的边界

**不重叠**：
- jst-uniapp 是面向**学生/家长/老师**的小程序
- 赛事方工作台是面向**赛事方运营员工**的 PC 端,在 RuoYi 后台

**联动点**：
- 赛事方在 PC 端创建/上线赛事 → 学生在小程序看得到
- 学生在小程序提交报名 → 赛事方在 PC 端看到待审核
- 赛事方在 PC 端发布成绩 → 学生在小程序收到消息

接口走同一套 admin Controller (`/jst/event/contest/...`),通过 `@PartnerScope` 切面分流数据可见范围。

---

## 6. 第一阶段交付清单（赛事方功能 MVP）

按方案 A,**没有任何前端工程改动**,只需:

- [ ] jst_event_partner 表加 user_id 字段（当前 DDL 没有）→ **需要加字段**
- [ ] JstLoginEnricherImpl 补上 partnerId 查找(目前是 TODO)
- [ ] 入驻审核流程：F5 (在方案 2 第 5 个 feature)
- [ ] 给赛事方创建 sys_user 时自动赋角色 jst_partner
- [ ] 各业务 Controller 加 @PartnerScope 注解(配合 jst-common 已实现的切面)
- [ ] 给 jst_partner 角色配置菜单权限(在管理后台勾选)

**预计工时**：1-2 天(主要是 F5 入驻审核 + 加注解 + 角色权限配置)

---

## 7. 待补的 DDL 变更（重要）

`jst_event_partner` 表需要加 `user_id` 字段。我会在写 F5 入驻审核时一并处理：

```sql
ALTER TABLE jst_event_partner ADD COLUMN `user_id` BIGINT(20) NULL COMMENT '关联sys_user.user_id'
  AFTER `partner_id`;
ALTER TABLE jst_event_partner ADD KEY `idx_user_id` (`user_id`);
```
