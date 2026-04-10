# 竞赛通 (JST) - 项目上下文 (CLAUDE.md)

> 会话恢复文件。上次会话时间：2026-04-09 21:30 UTC（阶段 C 全部收尾，批 4 合并，核心闭环 100% 可上线）

---

## 一、项目概述

**竞赛通** 是一个青少年竞赛报名与管理平台，包含三端：
- **管理后台 (PC Web)**：基于 RuoYi-Vue (Spring Boot 4.x + JDK 17 + Vue)
- **用户端 (微信小程序)**：基于 UniApp
- **赛事方 Web 工作台**（H5 审核端）

### 核心业务
- 赛事发布、报名、动态表单、审核
- 订单创建、混合支付（微信支付 + 积分 + 优惠券）
- 退款全流程（学生发起 / admin 特批）
- 渠道返点、提现
- 团队预约、扫码核销
- 课程管理、公告资讯
- 赛事方入驻审核、渠道认证

---

## 二、技术栈（严格锁定）

| 层 | 技术 |
|---|---|
| 后端框架 | RuoYi-Vue 3.9.2, Spring Boot 4.x, JDK 17 |
| 数据库 | MySQL 5.7 (InnoDB, utf8mb4_general_ci) |
| ORM | MyBatis (若依原生) |
| 缓存/锁 | Redis + Redisson 分布式锁 |
| 定时任务 | Quartz (若依自带) |
| 文件存储 | 阿里云 OSS |
| 视频 | 阿里云视频点播 |
| 支付 | 微信支付（Mock + Real 双实现，`jst.wxpay.enabled` 切换） |
| 小程序 | UniApp → 微信小程序 |
| 代码生成 | 若依自带代码生成器（51 张表已生成） |

---

## 三、项目目录结构

```
D:\coding\jst_v1\
├── RuoYi-Vue\                    # 后端主工程
│   ├── ruoyi-admin\              # 启动模块
│   ├── ruoyi-common\             # 若依公共模块
│   ├── ruoyi-system\             # 若依系统模块
│   ├── ruoyi-framework\          # 若依框架模块
│   ├── ruoyi-quartz\             # 定时任务
│   ├── ruoyi-generator\          # 代码生成器
│   ├── jst-common\               # ⭐ 业务公共（锁、加密、OSS、状态机、异常码）
│   ├── jst-user\                 # ⭐ 用户模块（登录、档案、绑定、入驻）
│   ├── jst-event\                # ⭐ 赛事模块（赛事CRUD、报名、表单模板）
│   ├── jst-order\                # ⭐ 订单模块（创建、支付、退款）
│   ├── jst-channel\              # 渠道模块
│   ├── jst-points\               # 积分模块
│   ├── jst-organizer\            # 赛事方模块
│   ├── jst-marketing\            # 营销模块
│   ├── jst-message\              # 消息模块
│   ├── jst-risk\                 # 风控模块
│   └── jst-finance\              # 财务模块
├── jst-uniapp\                   # ⭐ 微信小程序前端
│   ├── api\                      # 接口层 (request.js, auth.js, participant.js)
│   ├── pages\                    # 页面
│   ├── store\                    # Vuex (user.js)
│   └── static\
├── 架构设计\                      # ⭐ 架构文档
│   ├── ddl\                      # SQL 建表（01~10 业务表 + 迁移脚本 + fixtures）
│   ├── 11-状态机定义.md
│   ├── 12-API边界与服务划分.md
│   ├── 13-技术栈与依赖清单.md
│   ├── 14-定时任务清单(Quartz).md
│   ├── 15-Redis-Key与锁规约.md
│   ├── 16-安全与敏感字段.md
│   ├── 23-基础数据初始化.sql
│   ├── 25-从0到1开发流程.md
│   ├── 26-Uniapp用户端架构.md
│   ├── 27-用户端API契约.md
│   ├── 28-H5审核端架构.md
│   ├── 29-赛事方Web工作台架构.md
│   ├── 30-接口测试指南.md
│   ├── 31-API开发流水线.md
│   ├── 32-完整性审计-阶段B+F9.md
│   ├── 33-完整性审计-阶段C.md
│   └── 34-功能闭环决策记录.md  # ⭐ E 阶段 CCB 决策（Q-01~Q-14）
├── 小程序原型图\                  # 91 张 PNG 高保真截图 + HTML 原型
├── 需求文档\
│   ├── 竞赛通-产品需求文档-统一版-V4.1.md   # PRD (Single Source of Truth)
│   └── 竞赛通-管理后台Web端需求文档-V4.0.md
├── subagent\                     # ⭐ 子 Agent 协作体系
│   ├── SUBAGENT_PROMPT.md        # 后端 Agent 系统提示
│   ├── BACKEND_AGENT_PROMPT.md
│   ├── MINIPROGRAM_AGENT_PROMPT.md  # 前端 Agent 系统提示（含 PNG 对齐要求）
│   ├── TASK_CARD_TEMPLATE.md
│   ├── WORKFLOW.md
│   └── tasks\                    # 任务卡 + 任务报告
│       ├── F4-学生渠道绑定.md
│       ├── F5-赛事方入驻审核.md
│       ├── F6-渠道认证申请.md
│       ├── F7-赛事CRUD与查询.md
│       ├── F8-动态表单模板.md
│       ├── F9-报名记录与表单快照.md
│       ├── F-NOTICE-公告与首页接口.md
│       ├── F-COURSE-课程基础接口.md
│       ├── P1-登录与个人中心.md
│       ├── P2-首页与公告tab.md
│       ├── P3-赛事tab与详情.md
│       ├── P4-课程tab与详情.md
│       ├── P5-公开页面集.md
│       ├── P6-学生闭环页面集.md
│       ├── C2-订单混合支付.md
│       ├── C4-退款全流程.md
│       ├── DEBT-1-代码债清理.md
│       ├── P-POLISH-视觉对齐备忘.md
│       ├── README-依赖图.md
│       └── 任务报告\              # 子 Agent 完成报告
├── test\
│   ├── wx-tests.http             # ⭐ 小程序端接口测试（用这个）
│   └── admin-tests.http          # ⭐ 管理端接口测试
└── scripts\
    ├── cut-gen.sh                # 代码生成器裁剪脚本
    ├── fix-gen.sh                # 生成代码修复脚本
    └── restart-admin.ps1         # 重启后端脚本
```

---

## 四、开发阶段与进度

### 开发模式
- **主 Agent（架构师）**：负责架构设计、任务卡拆分、代码 review、BUILD 验证
- **子 Agent（Backend / MiniProgram）**：接收任务卡执行开发，输出任务报告
- 工作流：主 Agent 出任务卡 → 用户派发给子 Agent → 子 Agent 完成后输出报告 → 主 Agent review

### 阶段进度

| 阶段 | 内容 | 状态 |
|---|---|---|
| **A - 架构设计** | DDL 51张表 + 架构文档 12 份 + jst-common 基础设施 | ✅ 完成 |
| **B - 基础 Feature** | F4 绑定、F5 入驻、F6 渠道认证、F7 赛事、F8 表单、F-NOTICE、F-COURSE | ✅ 完成 |
| **B - 基础前端** | P1 登录、P2 首页、P3 赛事、P4 课程、P5 公开页面集 | ✅ 完成 |
| **C - 核心交易** | | |
| C1 - F9 报名 | 报名记录 + 表单快照 | ✅ 完成 |
| C2 - 订单混合支付 | 订单创建 + 积分冻结/消耗 + 优惠券锁定/使用 + Mock支付 | ✅ 完成（.http 全绿） |
| C4 - 退款全流程 | 学生退款 + admin 特批 + 资金回退 + 返点联动 | ✅ 完成（.http 全绿） |
| C5a - 渠道提现申请 | 返点 summary + ledger + withdraw apply/cancel/list/detail | ✅ 完成 |
| C5b - 渠道审核与打款 | admin 审核/驳回/打款 + 负向台账自动抵扣 + mock/real payout | ✅ 完成 |
| C6 - 团队预约+扫码核销 | 团队预约 + 核销引擎 + HMAC QR | ✅ 完成 |
| C7 - 个人预约 | 个人预约 apply/cancel/remaining，复用 C6 核销 | ✅ 完成 |
| C8 - 积分商城兑换 | 商品 CRUD + 兑换下单 + 虚拟发券/权益 + 实物发货 (SM-18) | ✅ 完成 |
| **WX-C1** - 订单与退款前端 | 小程序支付/订单/退款闭环 | ✅ 完成 |
| **WX-C2** - 渠道方前端 | 返点中心 + 提现 + 渠道首页/骨架页 | ✅ 完成 |
| **WX-C3** - 预约与商城前端 | 个人预约/二维码/扫码核销 + 积分商城 | ✅ 完成（遗留 6 条并入 POLISH） |
| **F-CHANNEL-DASHBOARD** | 渠道工作台 4 接口（monthly/students/orders/stats） | ✅ 完成（F-CD 12/12 全绿） |
| **DEBT-2** | my/index 占位清理 + profile 字段白名单 | ✅ 完成 |
| **P6 - 学生闭环前端** | 报名页、我的报名、我的绑定 | ✅ 完成（⚠️ 原表注"教师认证"实为错录，渠道认证前端未做，落到 E-1-CH） |
| **DEBT-1** | 代码债清理 | ✅ 完成 |
| **POLISH** | 视觉对齐（对照 91 张 PNG 截图） | 🟡 批 1/批 2 完成，POLISH-FULL 与 E 阶段并行 |
| **阶段 D 批 2** | POLISH-BATCH2 / DEBT-3 / F-USER-ADDRESS / D2-1a 配置化 / D2-1b uqrcode / D2-2 F-ADDR 联调 / D2-3 字段补齐 / D2-4 基线角色 | ✅ 全部完成 |
| **E-0 后端接口预备** | DDL 3 列 + 2 角色 + 11 新端点（认证/权益/批量/退款配置） | ✅ 完成 |
| **E2-PA-9 PartnerScope** | 赛事方数据隔离切面 + 基类 + 6 单测 + 35 文档 | ✅ 完成 |
| **E1-CH-1 渠道认证前端** | apply-entry/form/status 3 页 + Q-02 锁定 | ✅ 完成 |
| **E1-CH-5 返点+提现** | rebate 7 Tab + settlement 合并页 | ✅ 完成 |
| **E2-PA-1 赛事方入驻** | 3 公开页 + 4 步表单 + 响应式 + 匿名上传接口 | ✅ 完成 |
| **E2-PA-2 赛事方工作台** | home.vue 4 区块 + jst_partner 登录跳转 | ✅ 完成 |
| **E2-PA-4 报名审核** | enroll-manage + 详情抽屉 + 批量审核 | ✅ 完成 |
| **E1-CH-2/3/4/6 + E2-PA-3** | 渠道工作台/学生管理/订单/数据 + 赛事 CRUD | 🏃 执行中（第三波，5 Agent 并行） |
| **E-4-WEB Web 后台** | 65 菜单 PC + 手机响应式（承担 H5 审核端职能） | ⏸️ 等原型出齐后启动（Vue 2） |

---

## 五、关键决策记录

### 编码规范
- Controller 继承 `BaseController`，返回 `AjaxResult`
- 分页用若依原生 `startPage()` + `getDataTable()`
- 权限注解：`@PreAuthorize("@ss.hasPermi('jst:xxx:list')")`
- 参数接收：`ReqDTO` / `Form`（带 JSR-303 校验）
- 返回前端：`ResVO`（**禁止直接返回 Entity**）
- 事务：涉及资金/状态的方法必须 `@Transactional(rollbackFor = Exception.class)`
- 并发：Redis 分布式锁（Redisson）或数据库乐观锁

### C2 订单支付 8 个关键决策
1. 订单在 F9 审核通过后用户**主动**点支付创建，不自动创建
2. 金额**后端重算**，前端传金额仅对比
3. 微信支付 Mock + Real 双实现（`WxPayService` 接口）
4. 积分：pending_pay 阶段**冻结**，paid 阶段**消耗**
5. 优惠券：pending_pay 阶段 **locked**，paid 阶段 **used**
6. 渠道返点：创建订单时**预计算**
7. 零元订单：直接置 paid，不走支付
8. 幂等：order_no 重复创建被拒绝

### C4 退款 11 个关键决策
1. 退款发起方：学生（售后期内）+ admin 特批
2. 审核：赛事方审核 → 驳回后平台特批
3. **本期仅全额退**
4. 资金回退顺序：现金 → 积分 → 优惠券
5. 优惠券回退条件：整单全退 + 原券有效期内
6. enroll 联动：audit_status → cancelled
7. order 联动：refund_status='full' → cancelled
8. 返点联动：未提现 rolled_back，已提现 negative 行
9. 微信退款 Mock + Real 双实现
10. 幂等：refund_no 重复仅 1 次生效
11. 零元订单直接拒绝退款

### 测试规则
- **禁止在前端页面加测试数据**，所有测试数据必须通过 SQL fixtures
- fixtures 文件：`架构设计/ddl/99-test-fixtures.sql`
- 重置脚本：`架构设计/ddl/90-reset-fixtures.sql`
- 测试用 `wx-tests.http` 和 `admin-tests.http`（不要用旧的 `api-tests.http`，已删除）

---

## 六、已知问题与待办

> 最后更新：2026-04-10 阶段 E 第三波派发

### 阶段 E 进度总览（当前）

**已完成**：
- E-0 后端接口预备（11 端点 + DDL + 角色） ✅
- E2-PA-9 PartnerScope 切面 ✅
- E1-CH-1 渠道认证前端 3 页 ✅
- E1-CH-5 返点+提现结算合并 ✅
- E2-PA-1 赛事方入驻申请 3 公开页 ✅
- E2-PA-2 赛事方工作台首页 ✅
- E2-PA-4 报名审核 ✅

**执行中（第三波，5 Agent 并行）**：
- E1-CH-2 渠道工作台 home + my 视角重构
- E1-CH-3 学生管理 + 邀请绑定（含倒计时解绑 Q-01）
- E1-CH-4 渠道订单 + 订单详情 V4.0
- E1-CH-6 数据统计轻量看板
- E2-PA-3 赛事 CRUD 与提审

**待派（第四波）**：
- E1-CH-7 批量报名 + 临时参赛档案 + 团队预约
- E2-PA-5 成绩导入与发布
- E2-PA-6 证书管理
- E2-PA-7 赛事结算中心
- E2-PA-8 现场核销端

**待补充**：
- 赛事方菜单权限注册 SQL（sys_menu + sys_role_menu，第三波完成后补）
- 赛事方后端 `/jst/partner/*` Controller 组（PA-3~7 会逐步补）
- 后端字段缺口（PA-2 dashboard 3 接口、PA-4 partner enroll 路由）

### 待做但暂缓

| 项 | 状态 | 说明 |
|---|---|---|
| **E-4-WEB Web 后台** | ⏸️ 等原型 | 65 菜单 Vue 2 + 响应式（承担 H5 审核端职能） |
| **POLISH-FULL** | 🟡 UI Agent 并行 | 91 页原型对齐，Design Token 基线已落 |
| **F-AI-MAIC** | ⏸️ 缺外部文档 | OpenMAIC API 文档 + 测试账号待确认 |
| **F-ANALYSIS** | ⏸️ F-2 批次 | 经营分析 2 页 |
| **F-CONTRACT-INVOICE** | ⏸️ F-2 批次 | 合同开票 |
| **F-RISK** | ⏸️ P3 | 风控完整化 |

### 技术债

| 项 | 状态 |
|---|---|
| `98-migration DDL` 测试库未执行 | 需手动跑 |
| `lock:channel:unbind` 登记到 15-Redis-Key | 待补 |
| `auto-claim` 查用户手机号逻辑 | E0-1 遗留 #5 |
| `owner_type` 数据清洗 | DEBT-3 遗留脚本，需停服窗口 |
| OSS 上传组件集成（小程序端认证表单用文本框占位） | 待真实 OSS 配置后接入 |
| Spring Boot 4.0+ `spring-boot-starter-aspectj` | 已适配 |

### CCB 决策索引
- **`架构设计/34-功能闭环决策记录.md`**：Q-01~Q-14 全量决策（解绑倒计时/驳回上限/预约退款配置/账号模型等）
- **`架构设计/35-PartnerScope切面使用说明.md`**：赛事方数据隔离接入指南

---

## 七、DDL 文件清单

| 文件 | 模块 |
|---|---|
| 01-jst-user.sql | 用户、档案、绑定 |
| 02-jst-event.sql | 赛事、场次、表单模板、报名 |
| 03-jst-order.sql | 订单、支付、退款 |
| 04-jst-channel.sql | 渠道 |
| 05-jst-points.sql | 积分 |
| 06-jst-organizer.sql | 赛事方 |
| 07-jst-marketing.sql | 营销 |
| 08-jst-message.sql | 消息 |
| 09-jst-risk.sql | 风控 |
| 10-jst-finance.sql | 财务 |
| 23-基础数据初始化.sql | 字典、权限等基础数据 |
| 90-reset-fixtures.sql | 测试 fixture 重置 |
| 95~98-migration-*.sql | 迁移脚本（含 `97-migration-baseline-roles.sql` 基线角色补齐） |
| 99-test-fixtures.sql | 测试数据 |

---

## 八、子 Agent 派发模板

### 后端 Agent
```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴对应任务卡全部内容）

请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### 前端 Agent
```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴对应任务卡全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图获取精确视觉参数。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 九、恢复会话后第一步

1. 读取此 CLAUDE.md
2. `git log --oneline -20` 查看最近代码变更
3. 查看 `subagent/tasks/任务报告/C4-退款全流程-回答.md` 做 review
4. 跑测试验证 C2 全绿
5. 继续出 C5/C6/C7/C8 任务卡

## 十、PRD 版本规则

- V4.1 PRD 是 Single Source of Truth，原型图与 PRD 冲突时以 PRD 为准
- V3.0 及之前版本已废弃，禁止引用
- 修改业务规则/状态/字段时，必须同步更新所有相关附录文档

## 测试信息
- 测试数据库
  - url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
  - username: jst_new
  - password: J8zZpAa4zG8y6a7e
