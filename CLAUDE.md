# 竞赛通 (JST) - 项目上下文 (CLAUDE.md)

> 会话恢复文件。上次更新：2026-04-13（全量完成 + 安全审计 + 性能优化进行中）

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
│   ├── BACKEND_AGENT_PROMPT.md   # 后端 Agent 系统提示
│   ├── MINIPROGRAM_AGENT_PROMPT.md  # 前端 Agent 系统提示（含 PNG 对齐要求）
│   ├── UI_POLISH_AGENT_PROMPT.md    # UI 视觉优化 Agent（直连用户对话模式，已升级为 UI Refresh）
│   ├── UI_REFRESH_AGENT_PROMPT.md   # ⭐ UI Refresh Agent（uView 2.0 + Design Token 体系化刷新）
│   ├── TEST_AGENT_PROMPT.md         # ⭐ 测试审计 Agent（Layer1 静态 + Layer2 运行态 + Layer3 前端访问）
│   ├── WEB_ADMIN_AGENT_PROMPT.md    # ⭐ Web Admin Agent（Vue 2 + Element UI ruoyi-ui）
│   ├── TASK_CARD_TEMPLATE.md
│   ├── WORKFLOW.md
│   ├── ui-feedback\              # UI Agent 字段缺口反馈文档
│   └── tasks\                    # 任务卡 + 任务报告（B~F 阶段全量）
│       ├── (B 阶段) F4~F9, F-NOTICE, F-COURSE
│       ├── (B 前端) P1~P6
│       ├── (C 阶段) C2, C4, DEBT-1
│       ├── (D 阶段) D2-*, POLISH-*
│       ├── (E 阶段) E0-1, E1-CH-1~7, E2-PA-1~9
│       ├── (修复) FIX-1~5, TEST-ROUND1
│       ├── (F 阶段) MSG-TRIGGER, PARTNER-WRAPPER, QUARTZ-TASKS, ADMIN-SCAFFOLD, TEST-ROUND2
│       ├── (FEAT) FEAT-CONTEST-DETAIL, FEAT-COURSE-DETAIL, FEAT-INDEX-ENRICH, FEAT-FILTER, FEAT-MY-ENROLL-FIX (各 BE/FE)
│       ├── (UI 刷新) UI-INFRA, UI-REFRESH-1~4
│       ├── (Web管理) WEB-ADMIN-1~4
│       ├── (编译修复) FIX-COMPILE
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
| **E1-CH-2/3/4/6** | 渠道工作台/学生管理/订单/数据统计 | ✅ 完成 |
| **E2-PA-3** | 赛事 CRUD 与提审 | ✅ 完成 |
| **E1-CH-7** | 批量报名 + 临时档案 + 团队预约 | ✅ 完成 |
| **E2-PA-5/6** | 成绩导入 + 证书管理（后端+前端） | ✅ 完成 |
| **E2-PA-7/8** | 赛事结算 + 现场核销 | ✅ 完成 |
| **菜单 SQL** | 99-migration-partner-menus.sql（9700-9799 段） | ✅ 完成 |
| **入口修复** | my 老师→申请渠道方 + home 批量报名/团队预约跳转 | ✅ 完成 |
| **测试审计（第一轮）** | Test Agent Layer 1/2/3 | ✅ 完成（admin 81% / wx 95%） |
| **FIX-1~5** | 权限/fixture/接口补齐/断链修复/前端页面补全 | ✅ 完成 |
| **阶段 F — 后端 4 卡已完成** | | |
| F-MSG-TRIGGER | Spring Event 消息触发（7 类事件 + 7 监听器） | ✅ 完成 |
| F-PARTNER-WRAPPER | 赛事方接口包装层（3 Controller + PW-1~7 全绿） | ✅ 完成 |
| F-QUARTZ-TASKS | 4 个定时任务（订单超时/退款关闭/返点计提/预约过期） | ✅ 完成 |
| F-ADMIN-SCAFFOLD | ruoyi-ui 脚手架（后端菜单驱动/responsive/公共组件） | ✅ 完成 |
| **TEST-ROUND2** | 第二轮全量测试 | ✅ 完成（admin 93% / wx 98%） |
| **FEAT 9 卡** | 赛事详情/课程详情/首页丰富/筛选/报名修复（BE+FE 各拆分） | ✅ 完成 |
| **FIX-COMPILE** | HBuilderX 编译修复（中文引号/模板语法/iOS Date/import 缺失） | ✅ 完成 |
| **UI-INFRA** | uView 2.0 + Design Token 65+ 变量 + 动画库 | ✅ 完成 |
| **UI-REFRESH-1** | 核心 5 页刷新（首页/赛事/我的/公告/课程） | ✅ 完成 |
| **UI-REFRESH-2** | 交易与个人中心 23 页刷新 | ✅ 完成 |
| **UI-REFRESH-3** | 渠道与预约 23 页刷新 | ✅ 完成 |
| **UI-REFRESH-4** | 商城/公开页/剩余 21 页刷新 + 全量一致性扫描 | ✅ 完成（全局零硬编码） |
| **WEB-ADMIN-1** | 用户/参赛档案/赛事/报名/入驻管理（8 页 + JSON 可视化编辑器） | ✅ 完成 |
| **WEB-ADMIN-2** | 订单/退款/提现/返点/结算管理 | ✅ 完成 |
| **WEB-ADMIN-3** | 公告/课程/表单模板/优惠券/权益/商城管理 | ✅ 完成 |
| **WEB-ADMIN-4** | 渠道认证/渠道/绑定/预约/数据看板 + 菜单 SQL | ✅ 完成 |
| **断链修复** | 19 处断链全部修复（toast占位/API 404/路径不一致） | ✅ 完成 |
| **E-4-WEB 扩展** | | |
| WEB-ADMIN-5~8 | 40 页代码生成页精品化增强 + 2 页 partner 补齐 + 9 页 deprecated | ✅ 完成 |
| **P2 — 合同发票+运营聚合** | | |
| F-CONTRACT-INVOICE-BE | wx 合同/发票 Controller 5 端点（13/13 测试全绿） | ✅ 完成 |
| F-CONTRACT-INVOICE-FE | 小程序 4 新页面 + API + my/index 入口恢复 | ✅ 完成 |
| F-ANALYSIS-BE | Admin 聚合 Controller 5 端点 + 前端 dashboard 适配 | ✅ 完成 |
| **UX 友好化** | | |
| WEB-ADMIN-UX | Dashboard 快捷操作 + 菜单隐藏 SQL + 6 页帮助 + JSON 可视化 | ✅ 完成 |
| WEB-ADMIN-POLISH | course/edit 5-Tab + 9 页列表 Hero/drawer/响应式 | ✅ 完成 |
| **品牌与路由修复** | | |
| 品牌替换 | 若依→竞赛通（登录页改版 + 前后端文案 + 页面标题） | ✅ 完成 |
| WEB-ADMIN-FIX | 菜单注册 SQL 55 条(9800段) + 路由 404 修复 + 404 页优化 | ✅ 完成 |
| 导航优化 | 菜单分组（6 组）+ 防溢出 + 去重 + 不可达禁用 | ✅ 完成 |
| **ID→名称** | | |
| ID2NAME-BE | 16 Mapper XML LEFT JOIN + 15 Domain 追加名称属性 | ✅ 完成 |
| ID2NAME-FE | 17 页 ID→名称+跳转 + 4 页 autoOpen | ✅ 完成 |
| **质量修复** | | |
| QUALITY-FIX-ADMIN | 开发残留清理 + 11 页 catch toast + 权限补齐 + formatMoney 统一 | ✅ 完成 |
| QUALITY-FIX-MP | Mock 隐藏 + 技术文案清理 + catch toast + 硬编码色修复 | ✅ 完成 |
| 编码修复 | 7 文件 BOM 清除 + 1 文件 GBK 乱码 + 13 文件 Unicode 转义 + Agent 编码规范 | ✅ 完成 |
| **操作文档** | 小程序手册(612行) + 管理端手册(907行) | ✅ 完成 |
| **安全与性能** | | |
| SECURITY-FIX | 多环境 yml 拆分(dev/test/prod) + 5 项🔴安全修复 + 31 @Log 补齐 | ✅ 完成 |
| PERF-INDEX | 7 组合索引 + 连接池优化 + EXPLAIN 验证 | ✅ 完成 |
| PERF-CACHE | JstCacheService + 13 热点缓存 + 穿透/雪崩防护 | ✅ 完成 |
| **傻瓜化深度重构（11 卡）** | | |
| REFACTOR-1-DDL | 7 张子表 + jst_contest 16 新字段 + 级联保存 + 赛事复制 | ✅ 完成 |
| REFACTOR-2-DICT | 7 字典类型 30 条数据 + 若依 dict-tag 全局翻译 10 页 | ✅ 完成 |
| REFACTOR-3-CONTEST | 赛事编辑傻瓜化重构（8 Tab + 复制 + 预览 + JSON 清零） | ✅ 完成 |
| REFACTOR-4-ENROLL | 评审老师 + 审核打分一体化 + 卡片滑动审核 + 键盘快捷键 | ✅ 完成 |
| REFACTOR-5-CERT | Fabric.js 证书拖拽设计器（三栏 + 13 变量 + 撤销/重做） | ✅ 完成 |
| REFACTOR-6-BIZNO | 统一编号规则引擎（Redis INCR + 3 预置规则） | ✅ 完成 |
| REFACTOR-7-MENU | 菜单清理（重命名 + 隐藏 5 重复 + 合并渠道 + 根级 4→2） | ✅ 完成 |
| REFACTOR-8-MP-FORM | 动态表单渲染引擎（11 字段类型 + 实时校验 + 文件上传） | ✅ 完成 |
| REFACTOR-9-MP-BOOKING | 预约时间段选择 + 团队报名 3 步向导 + 7 项防呆 | ✅ 完成 |
| REFACTOR-10-MP-VISUAL | C 端视觉重塑 15 页 + 8 组件 + 15 Token + 8 动画 | ✅ 完成 |
| REFACTOR-11-MP-CERT | 成绩雷达图 + 证书海报 Canvas + 一键保存相册 + 3 入口 | ✅ 完成 |
| **傻瓜化收尾** | | |
| REFACTOR-TODO-P0 | 后端 6 接口补齐（团队报名/VO 字段/预约/表单/证书/小程序码） | ✅ 完成 |
| REFACTOR-TODO-CERT-RENDER | 证书客户端渲染引擎（cert-renderer.js + 后端 layoutJson 返回） | ✅ 完成 |
| REFACTOR-TODO-P2P3 | 评审过滤 + Redis Key + participantId 关联 + 旧组件清理 | ✅ 完成 |
| NOT NULL 默认值修复 | 7 表 NOT NULL 字段加 DEFAULT 防 INSERT 失败 | ✅ 完成 |
| Mapper 映射修复 | JstContestMapper.xml 补齐 21 个字段映射 | ✅ 完成 |
| **管理端全量精品化** | | |
| ADMIN-POLISH-BE | 10 Mapper LEFT JOIN 补齐关联名称 | ✅ 完成 |
| ADMIN-POLISH-BATCH1 | 17 页精品化（订单/渠道/营销 + 编号规则页） | ✅ 完成 |
| ADMIN-POLISH-BATCH2 | 13 页精品化（积分/赛事数据 + 商品缩略图/分数高亮） | ✅ 完成 |
| ADMIN-POLISH-BATCH3 | 10 页精品化（财务/风控/消息 + 16 字典 SQL） | ✅ 完成 |
| ADMIN-FIX-FINAL | 路由末段匹配 + 功能导航 + 渠道链路 + JSON 可视化 + 参赛档案接口 | ✅ 完成 |
| 菜单架构优化 | 赛事管理中心(操作) + 平台数据管理(数据) 职责分离 | ✅ 完成 |
| 首页重定向 | 登录→运营看板 + 系统菜单隐藏 | ✅ 完成 |
| 角色体系 | 6 个业务角色（运营主管/财务/客服/营销/风控/分析师） | ✅ 完成 |
| DATA-MIGRATION | 旧数据迁移方案与脚本 | ⏸️ 搁置至 5 月中旬 |

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

> 最后更新：2026-04-15 全量精品化 + 菜单架构优化 + 角色体系完成

### 已完成总览

| 批次 | 内容 | 产出 |
|---|---|---|
| A~E | 架构 + 51 表 + 18 模块后端 + 74 页小程序 + 17 张 E 阶段卡 | 全部 ✅ |
| F 后端 | MSG-TRIGGER / PARTNER-WRAPPER / QUARTZ-TASKS / ADMIN-SCAFFOLD | 全部 ✅ |
| FEAT | 9 卡（赛事详情/课程详情/首页丰富/筛选/报名修复） | 全部 ✅ |
| UI-REFRESH | 4 批 74 页全量刷新（uView 2.0 + Design Token，全局零硬编码） | 全部 ✅ |
| WEB-ADMIN 1~8 | 28 手工页 + 40 增强页 + 2 partner 补齐 + 9 deprecated | 全部 ✅ |
| P2 功能 | 合同发票(BE+FE) + 运营聚合看板 | 全部 ✅ |
| UX 友好化 | Dashboard 快捷操作 + 课程 5-Tab + 10 页精品化 + JSON 可视化 | 全部 ✅ |
| 品牌与路由 | 若依→竞赛通 + 菜单注册 55 条 + 路由 404 修复 + 导航分组 | 全部 ✅ |
| ID→名称 | 16 Mapper JOIN + 17 页前端名称展示+跳转 | 全部 ✅ |
| 质量修复 | 双端质量审计 61 项修复 + 编码修复(BOM/GBK/Unicode) | 全部 ✅ |
| 文档 | 小程序手册(612 行) + 管理端手册(907 行) + 操作文档 | 全部 ✅ |
| 修复 | FIX-1~5 + FIX-COMPILE + 断链 + 权限 + 样式 | 全部 ✅ |
| 测试 | TEST-ROUND1 (81%/95%) → TEST-ROUND2 (93%/98%) | 全部 ✅ |
| 安全+性能 | SECURITY-FIX(11项) + PERF-INDEX(7索引) + PERF-CACHE(13缓存) | 全部 ✅ |
| **傻瓜化重构** | **11 卡 + 收尾 4 卡：DDL/字典/赛事编辑/审核/证书/编号/菜单/表单/预约/视觉/成绩** | **全部 ✅** |
| **管理端精品化** | 40 页全量精品化 + JSON 可视化 + 渠道链路 + 功能导航 + 16 字典 | 全部 ✅ |
| **菜单与角色** | 菜单职责分离 + 首页重定向 + 6 业务角色 + admin 全量权限 | 全部 ✅ |

### 傻瓜化重构成果汇总（2026-04-14）

| 维度 | 数量 |
|---|---|
| 新建数据库表 | 10 张（7 子表 + reviewer + 2 编号表） |
| jst_contest 新增字段 | 16 个（banner/名额/团队/核销/主办方等） |
| 字典补齐 | 7 类型 30 条（若依 dict-tag 全局翻译） |
| 赛事编辑重构 | 8 Tab 全量傻瓜化（JSON→表格、Drawer 快建、批量生成） |
| 证书设计器 | Fabric.js 三栏拖拽（13 变量 + 撤销/重做 + 渠道复用） |
| 审核系统 | 打分一体化 + 卡片滑动审核 + 评审老师分配 + 键盘快捷键 |
| 编号引擎 | Redis INCR 并发安全 + 3 预置规则（cert/auth/enroll） |
| 菜单精简 | 根级 4→2 + 隐藏 5 重复 + 合并渠道 |
| 动态表单引擎 | 11 字段类型 + 实时校验 + 文件上传（图片/视频/PDF） |
| 预约改造 | 日期 Tab + 容量三态 + 批量生成 + 7 项防呆 |
| 团队报名 | 3 步向导 + 人数校验 + 微信分享邀请 |
| C 端视觉重塑 | 15 页极简高级灰 + 8 组件 + 15 Token + 8 动画 |
| 成绩证书 | 雷达图 + Canvas 海报 + 一键保存相册 + 验证公开页 |

### 已解决的遗留项

所有 P0/P1 遗留项已在收尾卡中解决：
- ✅ 后端 VO 字段（scorePublished/hasCert 等）
- ✅ 团队报名接口 + 预约时间段接口 + 表单模板 partner 端点 + 证书生成接口 + 小程序码
- ✅ 证书客户端渲染引擎（cert-renderer.js，零服务器压力）
- ✅ 评审老师自动过滤 + Redis Key 登记 + participantId 关联
- ✅ 旧 jst-form-render 组件删除
- ✅ JstContestMapper 字段映射补齐 + NOT NULL 默认值修复
- ✅ 参赛档案 Admin Controller + 路由末段匹配 + JSON 可视化
- ✅ 菜单职责分离 + 首页重定向 + 系统菜单隐藏

### 仍待处理（非阻塞）

| 优先级 | 项 | 说明 |
|---|---|---|
| **P1** | 真机兼容性验证 | backdrop-filter/视差/sticky 在 iOS/Android 微信实测 |
| **P2** | 微信邀请自动加入团队 | 需新建 pending_team 状态机 + join 接口（功能设计） |
| **P3** | 证书对齐辅助线 | Fabric.js guideline 插件（锦上添花） |
| **P3** | OSS 真实上传 | 底图/印章目前 dataURL，待 OSS 配置后接入 |

### 其他待办

| 优先级 | 项 | 状态 | 说明 |
|---|---|---|---|
| **P2** | DATA-MIGRATION | ⏸️ 5 月中旬 | 旧数据迁移（等旧库信息） |
| **P2** | TEST-ROUND3 | ⏳ | 全量回归测试（等遗留 TODO 补齐后） |
| **P3** | F-RISK | ⏸️ | 风控规则引擎（CRUD 已有，缺评估逻辑） |
| **远期** | F-AI-MAIC | ⏸️ | 缺 OpenMAIC 外部文档 |

### 技术债

| 项 | 状态 |
|---|---|
| `owner_type` 数据清洗 | DEBT-3 遗留脚本，需停服窗口 |
| OSS 上传组件集成 | 待真实 OSS 配置后接入 |
| select * 替换（15 处） | 性能审计建议，非阻塞 |

### 管理端菜单架构（2026-04-15 最终）

```
赛事管理中心 (9700) ← 操作入口（精品页，admin 看全量/partner 看自己的）
├── 工作台首页、赛事管理、报名审核、成绩管理
├── 证书管理（Fabric.js 拖拽设计器）
├── 结算中心、现场核销、账号设置

平台数据管理 (9800) ← 数据查看（40 页全量精品化）
├── 运营数据看板（登录默认首页 + 8 分组导航）
├── 订单交易（9761）：订单/退款/支付/预约/团队/核销
├── 用户渠道（9762）：用户/档案/渠道/认证/提现/绑定
├── 营销权益（9753）：优惠券/权益/批次/核销记录
├── 积分商城（9754）：积分/规则/等级/商城/兑换
├── 公告/消息
├── 赛事数据（9755）：成绩/证书/模板/赛事方/学习记录
├── 财务管理（9756）：打款/合同/发票
└── 风控审计（9757）：规则/告警/黑名单/案例/审计
```

### 角色体系（8 个）

| 角色 | role_key | 说明 |
|---|---|---|
| 超级管理员 | admin | 全部菜单 |
| 运营主管 | jst_operator | 赛事+订单+用户+渠道+公告+消息+赛事管理中心 |
| 财务专员 | jst_finance | 看板+订单+财务 |
| 客服专员 | jst_support | 看板+用户+赛事+订单+消息（只读为主） |
| 营销专员 | jst_marketing | 看板+优惠券+权益+积分+商城 |
| 风控专员 | jst_risk | 看板+风控+用户渠道 |
| 数据分析师 | jst_analyst | 看板+全部页面只读 |
| 赛事方 | jst_partner | 仅赛事管理中心（PartnerScope 数据隔离） |

### 生产部署就绪清单

| 项 | 状态 | 说明 |
|---|---|---|
| 多环境配置 | ✅ | dev/test/prod 三环境 yml |
| 数据库索引 | ✅ | 7 组合索引 + 连接池优化 |
| 缓存层 | ✅ | 13 热点缓存 + 穿透/雪崩防护 |
| 测试服务器 | ✅ | 39.107.69.244（jstadmin.esget.cn + jstapp.esget.cn） |
| 管理端精品化 | ✅ | 40 页全量精品化 + JSON 可视化 + 功能导航 |
| 小程序端重塑 | ✅ | 15 页视觉重塑 + 动态表单 + 团队报名 + 证书 |
| 微信支付 | ⏳ | 需商户账号 + SDK 接入 |
| 微信登录 | ⏳ | 需 appid/secret |
| 短信服务 | ⏳ | 需阿里云 SMS 配置 |
| OSS 文件上传 | ⏳ | 需阿里云 OSS 配置 |
| 旧数据迁移 | ⏸️ 5 月中旬 | 脚本框架已就绪 |
| 域名 + SSL | ⏳ | 需运维配置（已有 HTTP 域名） |

### CCB 决策索引
- **`架构设计/34-功能闭环决策记录.md`**：Q-01~Q-14 全量决策
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
| 95-migration-phase-c-permissions.sql | C 阶段权限点补齐（60 菜单 + 71 角色绑定） |
| 95-migration-unify-owner-type.sql | owner_type 清洗脚本（未执行，需停服窗口） |
| 96-migration-user-address.sql | jst_user_address 建表 |
| 96-grant-jst-partner-permissions.sql | jst_partner 角色权限绑定 |
| 97-migration-baseline-roles.sql | 基线角色补齐（student/partner/channel/platform_op） |
| 98-migration-phase-e-prep.sql | E 阶段 DDL（reject_count/allow_appointment_refund/新角色） |
| 99-migration-partner-menus.sql | ⭐ 赛事方菜单树（9700-9799 段，8 菜单 + 22 按钮） |
| 99-migration-quartz-jobs.sql | ⭐ 定时任务注册（QUARTZ-TASKS 产出） |
| 99-migration-jst-message.sql | jst_message 建表（MSG-TRIGGER 产出） |
| 99-migration-contest-detail-fields.sql | jst_contest 追加 schedule_json/awards_json/faq_json/recommend_tags |
| 99-migration-course-detail-fields.sql | jst_course 追加 chapters_json/teacher 等字段 |
| 99-migration-admin-menus.sql | ⭐ Web 管理端菜单注册（9900-9999 段） |
| 99-migration-refactor-dejson.sql | ⭐ 傻瓜化：7 子表 + jst_contest 16 新字段 |
| 99-migration-refactor-dict.sql | ⭐ 傻瓜化：7 字典类型 30 条数据 |
| 99-migration-refactor-menus.sql | ⭐ 傻瓜化：菜单重命名+隐藏+合并 |
| 99-migration-biz-no-rule.sql | ⭐ 傻瓜化：编号规则表+序列表+3预置规则 |
| 99-migration-contest-reviewer.sql | ⭐ 傻瓜化：评审老师表 |
| 99-migration-admin-polish-dict.sql | ⭐ 精品化：16 字典类型 99 条数据 |
| 99-migration-fix-not-null-defaults.sql | 修复：7 表 NOT NULL 加 DEFAULT |
| 99-test-fixtures.sql | 测试数据（含团队赛事 8206 + 预约时间段） |

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

### 前端 Agent（小程序）
```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴对应任务卡全部内容）

⭐ PNG 优先：先看 D:/coding/jst_v1/小程序原型图/ 下的 PNG 截图获取精确视觉参数。
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### Web Admin Agent（管理端前端）
```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴对应任务卡内容）

⭐ 先读已有 views/partner/ 下的页面了解项目风格。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ 必须响应式适配手机浏览器。
⭐ npm run build:prod 必须通过。
```

### UI Refresh Agent（小程序视觉刷新）
```
你是竞赛通项目的 UI Refresh Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\UI_REFRESH_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴对应任务卡内容）

⭐ 原型是下限不是上限，你的目标是做得比原型更好。
⭐ 必须用 uView 组件 + Design Token，禁止手写硬编码 CSS。
⭐ 先出 Diff Plan，等我 confirm 再动手。
⭐ script 块的业务逻辑一行不动。
```

---

## 九、恢复会话后第一步

1. 读取此 CLAUDE.md
2. `git log --oneline -20` 查看最近代码变更
3. 检查 §六 当前执行中任务 + 待办
4. 查看 `subagent/tasks/任务报告/` 是否有新报告需要 review
5. 当前阶段：傻瓜化重构 + 管理端精品化全部完成 → 生产部署准备（等外部配置：微信/SMS/OSS）

## 十、PRD 版本规则

- V4.1 PRD 是 Single Source of Truth，原型图与 PRD 冲突时以 PRD 为准
- V3.0 及之前版本已废弃，禁止引用
- 修改业务规则/状态/字段时，必须同步更新所有相关附录文档

## 测试信息
- 测试数据库
  - url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
  - username: jst_new
  - password: J8zZpAa4zG8y6a7e
