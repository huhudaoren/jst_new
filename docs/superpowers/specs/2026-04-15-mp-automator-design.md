# 竞赛通小程序自动化测试 — 设计文档

> 版本：V1.0 | 日期：2026-04-15
> 状态：已确认，待实施

---

## 一、目标

构建一个 MCP Server（`jst-mp-automator`），让 Claude Code 能通过微信开发者工具对小程序执行：

1. **全链路功能闭环测试** — 28 条业务链路自动执行
2. **产品体验审计** — 以高级产品经理和用户视角审视每个页面
3. **截图 + 数据库双重校验** — UI 表现与后端数据一致性验证

非目标：像素级视觉对比、性能压测、安全渗透测试。

---

## 二、架构

```
┌──────────────────────────────────────────────────┐
│                  Claude Code                      │
│  "跑一下报名支付链路" / "全量回归"                    │
└──────────┬───────────────────────────────────────┘
           │ MCP 协议 (stdio)
           ▼
┌──────────────────────────────────────────────────┐
│          jst-mp-automator (MCP Server)            │
│  Node.js 进程                                      │
│                                                    │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────┐ │
│  │ Tool 层      │  │ Flow 引擎    │  │ 报告生成  │ │
│  │ 14 个 MCP    │  │ YAML 链路    │  │ 截图+JSON │ │
│  │ 工具         │  │ 脚本解释器   │  │ → 报告    │ │
│  └──────┬──────┘  └──────┬───────┘  └────┬─────┘ │
│         │                │                │       │
│         ▼                ▼                ▼       │
│  ┌────────────────────────────────────────────┐   │
│  │         automator 适配层                     │   │
│  │  miniprogram-automator + 重试 + 超时         │   │
│  └──────────────────┬─────────────────────────┘   │
└─────────────────────┼─────────────────────────────┘
                      │ WebSocket (端口 24466)
                      ▼
┌──────────────────────────────────────────────────┐
│            微信开发者工具                           │
│            运行编译后的小程序                        │
└──────────────┬───────────────────────────────────┘
               │ HTTP (127.0.0.1:8080)
               ▼
┌──────────────────────────────────────────────────┐
│            Spring Boot 后端                        │
│            (Mock 模式运行)                          │
└──────────────────────────────────────────────────┘
```

### 运行环境前提

| 项 | 要求 |
|---|---|
| 微信开发者工具 | 已打开，服务端口 24466 已启用 |
| 小程序编译产物 | `npm run dev:mp-weixin` 已执行，产物在 `dist/dev/mp-weixin/` |
| 后端服务 | Spring Boot 在 `127.0.0.1:8080` 运行（dev profile，mock 模式） |
| 测试数据库 | `59.110.53.165:3306/jst_new`（MCP Server 直连，用于查询和补数据） |
| Node.js | >= 16 |

---

## 三、MCP 工具清单（14 个）

### 3.1 基础操控（5 个）

| 工具 | 参数 | 说明 |
|---|---|---|
| `mp_launch` | `project_path?` | 启动 automator 连接微信开发者工具，默认 `dist/dev/mp-weixin/` |
| `mp_navigate` | `path`, `query?` | 跳转页面，如 `/pages/index/index?tab=contest` |
| `mp_tap` | `selector` | 点击元素（支持 CSS 选择器） |
| `mp_input` | `selector`, `value` | 输入文本到表单字段 |
| `mp_scroll` | `direction`, `distance?`, `selector?` | 滚动页面或指定容器 |

### 3.2 观察检测（4 个）

| 工具 | 参数 | 说明 |
|---|---|---|
| `mp_screenshot` | `full_page?` | 截图。`full_page=true` 时滚动分段截图，返回多张带标注的图 |
| `mp_get_page_data` | `path?` | 获取当前页面 Vue 组件的 data 对象 |
| `mp_get_element` | `selector`, `properties?` | 获取元素属性（文本/位置/尺寸/可见性） |
| `mp_get_console` | `level?` | 获取 console 日志，可按 error/warn/log 过滤 |

### 3.3 登录与数据（3 个）

| 工具 | 参数 | 说明 |
|---|---|---|
| `mp_mock_login` | `mock_code` | 调用 `POST /jst/wx/auth/login` 获取 token，注入小程序 storage，刷新页面 |
| `mp_query_db` | `sql` | 查询测试数据库（仅 SELECT） |
| `mp_exec_db` | `sql` | 写入测试数据库（INSERT/UPDATE），所有执行的 SQL 记日志 |

### 3.4 链路执行（2 个）

| 工具 | 参数 | 说明 |
|---|---|---|
| `mp_run_flow` | `flow_name`, `params?` | 执行预定义 YAML 链路脚本 |
| `mp_run_flow_list` | — | 列出所有可用链路及其描述 |

### 3.5 关键行为说明

**mp_mock_login 流程：**
1. HTTP `POST http://127.0.0.1:8080/jst/wx/auth/login` body `{ "code": "MOCK_1001" }`
2. 从响应中提取 `token`
3. 通过 automator 执行 `wx.setStorageSync('jst-token', token)`
4. 导航到首页触发已登录状态

**mp_screenshot 全页模式：**
1. 注入 JS 获取 `document.documentElement.scrollHeight`
2. 获取视口高度
3. 循环：`scrollTo(0, n * viewportHeight)` → 等待 500ms → 截图 → 标注 `[n/N]`
4. 返回所有截图供 Claude 分析

**重试与超时（全局）：**
- 操作超时：10s（可通过 config.js 调整）
- 失败重试：3 次，指数退避（2s → 4s → 8s）
- 截图前等待：1s（确保渲染完成）
- 页面跳转后等待：2s（等待数据加载）

---

## 四、链路脚本格式（YAML）

### 4.1 脚本结构

```yaml
name: 链路名称
description: 一句话描述
login: MOCK_1001              # 使用哪个测试账号
precondition:                 # 前置数据检查（可选）
  - sql: "SELECT ..."
    expect: "条件表达式"
    fix_sql: "INSERT ..."     # 不满足时自动修复

steps:
  - name: 步骤名称
    action: navigate | tap | input | scroll | wait | login
    path: /pages/xxx          # navigate 时用
    selector: ".btn-xxx"      # tap/input 时用
    value: "输入内容"          # input 时用
    wait: 2000                # 操作后等待 ms
    retry: 3                  # 本步骤重试次数（覆盖全局）
    on_fail: continue | abort # 失败时继续还是中断（默认 continue）
    check:
      # 截图检查
      - screenshot: { full_page: false }
      # 元素断言
      - element: ".some-class"
        expect: exists | not_exists | text_contains("xxx")
      # 数据库断言
      - db: "SELECT status FROM jst_order_main WHERE order_id = ..."
        expect: "status = 'paid'"
      # 页面数据断言
      - page_data: "orderInfo.totalAmount"
        expect: "> 0"
      # 产品体验审查（Claude 看截图时作为评审指引）
      - product_review:
          - "审查问题1"
          - "审查问题2"
```

### 4.2 示例：报名支付全链路

```yaml
name: 报名支付全链路
description: 学生浏览赛事 → 进入详情 → 报名 → 填表 → 支付 → 查看订单
login: MOCK_1001
precondition:
  - sql: "SELECT COUNT(*) as cnt FROM jst_contest WHERE status='enrolling' AND del_flag='0'"
    expect: "cnt >= 1"
  - sql: "SELECT COUNT(*) as cnt FROM jst_participant WHERE user_id=1001 AND del_flag='0'"
    expect: "cnt >= 1"
    fix_sql: >
      INSERT INTO jst_participant (user_id, real_name, id_card, gender, school, grade, create_by, create_time, del_flag)
      VALUES (1001, '测试_小明', '110101200801011234', 'M', '测试小学', '三年级', 'automator', NOW(), '0')

steps:
  - name: 进入赛事列表
    action: navigate
    path: /pages/contest/index
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - element: ".contest-card"
        expect: exists
      - product_review:
          - "用户能否快速找到想报名的赛事？搜索和筛选入口是否醒目？"
          - "赛事卡片是否展示了关键决策信息（价格、报名截止时间、剩余名额）？"
          - "列表为空时是否有引导提示？"

  - name: 点击第一个可报名赛事
    action: tap
    selector: ".contest-card:first-child"
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - page_data: "contestInfo.contestName"
        expect: not_empty
      - product_review:
          - "3 秒内用户能否理解：这是什么赛事、多少钱、什么时候截止报名？"
          - "报名按钮是否固定底部且足够醒目？"
          - "赛程安排/奖项/FAQ 信息层次是否清晰？"
          - "已过期或名额已满时，报名按钮是否禁用并说明原因？"

  - name: 点击立即报名
    action: tap
    selector: ".btn-enroll"
    wait: 2000
    check:
      - screenshot: true
      - product_review:
          - "进入报名流程后，用户是否知道总共几步、当前在第几步？"
          - "参赛人选择是否直观？没有档案时是否引导创建？"

  - name: 选择参赛人并进入表单
    action: tap
    selector: ".participant-card:first-child"
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - product_review:
          - "表单字段标签是否清晰？必填项是否有标记？"
          - "文件上传区域是否有格式和大小说明？"
          - "表单是否支持实时校验，还是等提交才报错？"

  - name: 提交报名
    action: tap
    selector: ".btn-submit"
    wait: 3000
    check:
      - screenshot: true
      - db: "SELECT audit_status FROM jst_enroll_record WHERE user_id=1001 ORDER BY create_time DESC LIMIT 1"
        expect: "audit_status = 'pending'"
      - product_review:
          - "提交后是否有明确的成功反馈？"
          - "用户是否知道下一步该做什么（等审核）？"
          - "是否引导跳转到'我的报名'查看进度？"

  - name: 查看我的报名
    action: navigate
    path: /pages-sub/my/enroll-list
    wait: 2000
    check:
      - screenshot: { full_page: true }
      - element: ".enroll-card"
        expect: exists
      - product_review:
          - "报名状态是否醒目（待审核/已通过/已驳回用不同颜色区分）？"
          - "每条报名是否展示关键信息（赛事名、提交时间、状态）？"
          - "已通过的报名是否有明确的'去支付'入口？"
```

---

## 五、产品体验审计框架

Claude 在分析每张截图时，按以下 5 个维度评审：

### 5.1 第一感受（3 秒法则）

- 页面打开后用户能否立即理解这是什么、该做什么
- 核心操作按钮是否醒目、位置是否合理
- 空状态是否有引导（不是空白一片）
- 加载过程是否有反馈（骨架屏 / loading / 过渡动画）

### 5.2 信息层次与可读性

- 标题/正文/辅助文字的层级是否清晰
- 关键信息是否第一眼能看到（价格、状态、截止时间）
- 列表页的筛选和排序是否有效
- 信息密度是否合理（不拥挤也不空旷）
- 长文本处理（截断 + 省略号 vs 溢出）

### 5.3 流程连贯性

- 用户是否始终知道自己在哪、怎么回去
- 操作完成后是否有明确反馈（成功提示 / 跳转 / 状态更新）
- 异常情况是否有友好提示（不是技术报错或空白页）
- 中途退出再回来是否能恢复（草稿保存）
- 上下文衔接（从列表进详情再返回，列表位置是否保留）

### 5.4 业务完整性

- 该有的数据字段是否都展示了
- 金额 / 时间 / 状态与数据库是否一致（截图 + 查库双校验）
- 角色权限是否正确（学生看不到渠道功能）
- 边界场景展示：0 元订单、过期赛事、已满名额、被驳回状态

### 5.5 体验细节

- 触发区域是否够大（适合手指操作）
- 页面间跳转是否有过渡动画
- 返回导航是否正确（不会返回到错误页面）
- 底部 TabBar 当前页高亮是否正确
- 交互反馈（点击态、禁用态、加载态）

### 5.6 问题严重等级

| 等级 | 定义 | 举例 |
|---|---|---|
| 🔴 严重 | 阻塞用户完成核心任务 | 提交报名后无反馈、支付按钮不可点、页面白屏 |
| 🟡 中等 | 不阻塞但明显影响体验 | 空状态无引导、报错文案是技术语言、关键信息缺失 |
| 🟢 轻微 | 优化建议 | 动画缺失、间距不均、辅助文案可改善 |

---

## 六、28 条链路脚本清单

### 游客链路（2 条）

| # | 文件 | 描述 | 测试账号 |
|---|---|---|---|
| 25 | `25-public-query.yaml` | 公开成绩查询 + 证书验真 | 无需登录 |
| 26 | `26-partner-apply.yaml` | 赛事方入驻申请（4 步表单） | 无需登录 |

### 学生链路（19 条）

| # | 文件 | 描述 | 测试账号 |
|---|---|---|---|
| 01 | `01-browse-home.yaml` | 首页浏览：Banner / 公告条 / 宫格 / 推荐赛事 / 课程 | MOCK_1001 |
| 02 | `02-browse-contest.yaml` | 赛事列表：搜索 / 分类 Tab / 多级筛选 | MOCK_1001 |
| 03 | `03-contest-detail.yaml` | 赛事详情：基本信息 / 赛程 / 奖项 / FAQ | MOCK_1001 |
| 04 | `04-enroll-pay.yaml` | 报名 → 选参赛人 → 填表 → 提交 → 支付 | MOCK_1001 |
| 05 | `05-my-enroll.yaml` | 我的报名：状态筛选 / 编辑草稿 / 重新提交 | MOCK_1001 |
| 06 | `06-order-manage.yaml` | 我的订单：状态筛选 / 订单详情 / 金额校验 | MOCK_1001 |
| 07 | `07-refund.yaml` | 退款申请 → 选原因 → 提交 → 查看进度 | MOCK_1001 |
| 08 | `08-browse-course.yaml` | 课程列表：搜索 / 类型筛选 / 课程详情 / 目录 | MOCK_1001 |
| 09 | `09-browse-notice.yaml` | 公告列表 → 公告详情 → 富文本展示 | MOCK_1001 |
| 10 | `10-profile-manage.yaml` | 个人资料编辑：头像 / 昵称 / 学校 / 年级 | MOCK_1001 |
| 11 | `11-address-manage.yaml` | 收货地址：新增 / 编辑 / 删除 / 设默认 | MOCK_1001 |
| 12 | `12-points-center.yaml` | 积分中心：余额 / 等级 / 成长值 / 积分明细 | MOCK_9001 |
| 13 | `13-points-mall.yaml` | 积分商城：浏览 → 详情 → 兑换 → 我的兑换 | MOCK_9001 |
| 14 | `14-coupon.yaml` | 优惠券：领券中心 / 我的优惠券 / 状态筛选 | MOCK_1001 |
| 15 | `15-rights.yaml` | 权益中心：查看权益 / 申请核销 / 核销记录 | MOCK_1001 |
| 16 | `16-appointment.yaml` | 个人预约：选时段 → 确认 → 我的预约 → 二维码 | MOCK_1001 |
| 24 | `24-score-cert.yaml` | 我的成绩（雷达图）→ 我的证书（Canvas 海报）| MOCK_1001 |
| 28 | `28-binding-manage.yaml` | 绑定关系查看与管理 | MOCK_9212 |

### 渠道方链路（7 条）

| # | 文件 | 描述 | 测试账号 |
|---|---|---|---|
| 17 | `17-channel-auth.yaml` | 渠道认证申请：选类型 → 填材料 → 提交 | MOCK_1001 |
| 18 | `18-channel-workspace.yaml` | 工作台首页：KPI 统计 / 快捷入口 / 近期订单 | MOCK_1003 |
| 19 | `19-channel-rebate.yaml` | 返点中心 7 Tab + 提现结算申请 | MOCK_1003 |
| 20 | `20-channel-student.yaml` | 学生管理：绑定学生列表 / 成绩 / 证书 | MOCK_1003 |
| 21 | `21-channel-order.yaml` | 渠道订单：多维筛选 / 订单详情 / 返点金额 | MOCK_1003 |
| 22 | `22-channel-batch-enroll.yaml` | 批量报名：选赛事 → 添加学生 → 提交 | MOCK_1003 |
| 23 | `23-team-booking.yaml` | 团队预约：3 步向导 → 成员管理 → 签到状态 | MOCK_1003 |
| 27 | `27-contract-invoice.yaml` | 合同查看 + 发票申请 | MOCK_1003 |

### 全量回归

| 文件 | 描述 |
|---|---|
| `_full-regression.yaml` | 按依赖顺序串联全部 28 条链路，先游客 → 学生 → 渠道方 |

---

## 七、测试数据策略

### 数据源

直连测试数据库 `59.110.53.165:3306/jst_new`，不依赖本地 fixture SQL。

### 链路执行前自动检查

每条链路的 `precondition` 会查库检查所需数据，不足时通过 `fix_sql` 自动补充：

| 检查项 | 预期 | 不足时自动补充 |
|---|---|---|
| 可报名赛事 | >= 1 条 `status='enrolling'` | INSERT 赛事 |
| 参赛人档案 | 登录用户有 >= 1 条 | INSERT 档案 |
| 课程 | >= 2 条 | INSERT 课程 |
| 公告 | >= 3 条 | INSERT 公告 |
| 积分余额 | MOCK_9001 有余额 | INSERT/UPDATE 积分账户 |
| 优惠券 | MOCK_1001 有可用券 | INSERT 优惠券 |
| 商城商品 | >= 2 条在售 | INSERT 商品 |
| 已支付订单 | 退款链路需要 | INSERT 订单 |

### 数据隔离

- 自动插入的数据 `create_by = 'automator'`
- 测试结束可选清理：`DELETE FROM xxx WHERE create_by = 'automator'`

---

## 八、报告格式

### 单条链路报告

```json
{
  "flow": "04-enroll-pay",
  "name": "报名支付全链路",
  "login": "MOCK_1001",
  "timestamp": "2026-04-15T14:30:00",
  "duration_ms": 45000,
  "result": "partial_pass",
  "steps": [
    {
      "name": "进入赛事列表",
      "status": "pass",
      "screenshots": ["screenshots/04/step1-1.png"],
      "assertions": [
        { "type": "element", "selector": ".contest-card", "result": "pass" }
      ],
      "product_issues": []
    },
    {
      "name": "提交报名",
      "status": "fail",
      "screenshots": ["screenshots/04/step5-1.png"],
      "error": "元素 .btn-submit 点击后页面无变化",
      "assertions": [
        { "type": "db", "sql": "SELECT ...", "result": "fail", "actual": "无记录" }
      ],
      "product_issues": [
        {
          "severity": "red",
          "description": "提交报名后无任何反馈，用户不知道是否成功",
          "suggestion": "提交后弹出成功提示并自动跳转到我的报名"
        }
      ]
    }
  ],
  "summary": {
    "total_steps": 7,
    "passed": 5,
    "failed": 2,
    "product_issues": { "red": 1, "yellow": 2, "green": 1 },
    "experience_score": 6
  }
}
```

### 全量回归报告

```
━━ 全量回归报告 — {日期} ━━

总览：
  链路通过: 24/28
  产品体验问题: 17 条
    🔴 严重(阻塞用户): 3 条
    🟡 中等(影响体验): 8 条
    🟢 轻微(优化建议): 6 条
  总耗时: 12 分 34 秒

🔴 严重问题清单:
  #1 [04-enroll-pay/步骤5] 提交报名后无反馈
  #2 [07-refund/步骤2] 退款原因校验报错是技术语言
  #3 [18-channel-workspace/步骤1] 工作台数据全显示 0

🟡 中等问题清单:
  #4 [01-browse-home/步骤1] Banner 图加载慢时区域空白
  ...

各链路结果:
  01-browse-home:        ✅ 通过 (体验 8/10)
  02-browse-contest:     ✅ 通过 (体验 7/10, 2 条建议)
  03-contest-detail:     ✅ 通过 (体验 9/10)
  04-enroll-pay:         ❌ 失败 (步骤5断开, 体验 6/10)
  ...
```

---

## 九、目录结构

```
D:\coding\jst_v1\
├── jst-mp-automator\                  # MCP Server 项目
│   ├── package.json                   # 依赖：miniprogram-automator, mysql2, js-yaml, @modelcontextprotocol/sdk
│   ├── index.js                       # MCP Server 入口 (stdio)
│   ├── src\
│   │   ├── automator.js               # automator 封装（连接/重试/超时）
│   │   ├── tools\                     # 14 个工具实现
│   │   │   ├── launch.js
│   │   │   ├── navigate.js
│   │   │   ├── tap.js
│   │   │   ├── input.js
│   │   │   ├── scroll.js
│   │   │   ├── screenshot.js
│   │   │   ├── get-page-data.js
│   │   │   ├── get-element.js
│   │   │   ├── get-console.js
│   │   │   ├── mock-login.js
│   │   │   ├── query-db.js
│   │   │   ├── exec-db.js
│   │   │   ├── run-flow.js
│   │   │   └── run-flow-list.js
│   │   ├── flow-engine.js             # YAML 链路解释器
│   │   ├── reporter.js                # 报告生成器
│   │   ├── db.js                      # MySQL 连接池
│   │   └── config.js                  # 配置（超时/重试/数据库）
│   ├── flows\                         # 28 条 YAML 链路 + 全量回归
│   │   ├── 01-browse-home.yaml
│   │   ├── ...
│   │   ├── 28-binding-manage.yaml
│   │   └── _full-regression.yaml
│   ├── reports\                       # 测试报告输出
│   └── screenshots\                   # 截图输出
```

---

## 十、MCP 注册配置

在 `.claude/settings.local.json` 或项目 `.mcp.json` 中注册：

```json
{
  "mcpServers": {
    "jst-mp-automator": {
      "command": "node",
      "args": ["D:/coding/jst_v1/jst-mp-automator/index.js"],
      "env": {
        "DB_HOST": "59.110.53.165",
        "DB_PORT": "3306",
        "DB_NAME": "jst_new",
        "DB_USER": "jst_new",
        "DB_PASS": "J8zZpAa4zG8y6a7e",
        "WX_DEVTOOLS_PORT": "24466",
        "BACKEND_URL": "http://127.0.0.1:8080",
        "PROJECT_PATH": "D:/coding/jst_v1/jst-uniapp/dist/dev/mp-weixin"
      }
    }
  }
}
```

---

## 十一、实施步骤概览

| 步骤 | 内容 | 产出 |
|---|---|---|
| 1 | 搭建 MCP Server 骨架 + automator 连接 | `index.js` + `automator.js` |
| 2 | 实现 5 个基础操控工具 | `launch/navigate/tap/input/scroll` |
| 3 | 实现 4 个观察检测工具 | `screenshot/get-page-data/get-element/get-console` |
| 4 | 实现 3 个登录与数据工具 | `mock-login/query-db/exec-db` |
| 5 | 实现 YAML Flow 引擎 + 报告生成 | `flow-engine.js` + `reporter.js` |
| 6 | 编写 28 条链路脚本 | `flows/*.yaml` |
| 7 | MCP 注册 + 端到端验证 | 跑通一条完整链路 |
| 8 | 全量回归 + 产品体验审计 | 首份全量报告 |

---

## 十二、测试账号矩阵

| Mock Code | 用户名 | 角色 | 用于链路 |
|---|---|---|---|
| `MOCK_1001` | 测试_张妈妈 | student | 01-11, 14-17, 24 |
| `MOCK_1003` | 测试_王老师 | student + channel | 18-23, 27 |
| `MOCK_9001` | 积分中心用户 | student | 12, 13 |
| `MOCK_9212` | FCD_STUDENT_B | student（已绑渠道） | 28 |
| 无需登录 | — | 游客 | 25, 26 |

---

## 十三、风险与缓解

| 风险 | 缓解措施 |
|---|---|
| miniprogram-automator 对 UniApp 编译产物的选择器不稳定 | 优先用 class 选择器；备选用 `page.$$` 遍历 |
| 网速慢导致超时 | 全局 10s 超时 + 3 次指数退避重试 |
| 微信开发者工具版本兼容 | automator SDK 有版本适配层，启动时检测版本 |
| 长页面截图不全 | 分段滚动截图 + 每段带标注 |
| 测试数据被其他操作污染 | `create_by = 'automator'` 隔离 + 链路前 precondition 检查 |
| 动态表单字段不确定 | 链路脚本中用 `get_page_data` 读取表单 schema 后动态填充 |
