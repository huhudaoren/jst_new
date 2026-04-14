# 派发清单 — 傻瓜化深度重构（11 卡）

> 管理端 7 卡 + 用户端 4 卡 | 4 个 Wave 推进

---

## 依赖关系图

```
Wave 0 (P0, 基础层)
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ REFACTOR-1-DDL   │  │ REFACTOR-2-DICT  │  │ REFACTOR-6-BIZNO │
│ 数据库去JSON化    │  │ 全局枚举翻译      │  │ 编号规则引擎      │
│ Backend Agent    │  │ Web Admin Agent  │  │ Backend Agent    │
└────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘
         │                     │                      │
         ↓                     ↓                      │
Wave 1 (P1, 管理端+菜单)                               │
┌──────────────────┐  ┌──────────────────┐             │
│ REFACTOR-3       │  │ REFACTOR-7-MENU  │             │
│ 赛事编辑傻瓜化    │  │ 菜单路由清理      │             │
│ Web Admin Agent  │  │ Web Admin Agent  │             │
└────────┬─────────┘  └──────────────────┘             │
         │                                             │
         ↓                                             ↓
Wave 2 (P1, 审核+证书)
┌──────────────────┐  ┌──────────────────┐
│ REFACTOR-4       │  │ REFACTOR-5-CERT  │
│ 报名审核与评审    │  │ 证书拖拽设计器    │
│ Backend+Admin    │  │ Web Admin Agent  │
└────────┬─────────┘  └────────┬─────────┘
         │                     │
         ↓                     ↓
Wave 3 (P1, C端全面升级)
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ REFACTOR-8       │  │ REFACTOR-9       │  │ REFACTOR-10      │
│ 动态表单引擎      │  │ 预约与团队报名    │  │ C端视觉重塑       │
│ MP Agent         │  │ MP Agent         │  │ UI Refresh Agent │
└──────────────────┘  └──────────────────┘  └────────┬─────────┘
                                                      │
                                                      ↓
Wave 4 (P1, C端收尾)
┌──────────────────┐
│ REFACTOR-11      │
│ 成绩与证书展示    │
│ MP Agent         │
└──────────────────┘
```

## 文件冲突检查

| 卡 | 改动范围 | 冲突风险 |
|---|---|---|
| 1-DDL | DDL SQL + jst-event Domain/Mapper/Service/DTO/VO | 无冲突 |
| 2-DICT | ruoyi-ui utils/dict.js + 20+ 列表页 | 无冲突（与 DDL 不重叠） |
| 6-BIZNO | jst-common Service + 新表 | 无冲突 |
| 3-CONTEST | ruoyi-ui views/partner/contest-edit.vue + contest-list.vue | 与 DICT 同文件但不同行 |
| 7-MENU | sys_menu SQL | 无冲突 |
| 4-ENROLL | jst-event + ruoyi-ui views/partner/enroll-manage.vue | 无冲突 |
| 5-CERT | ruoyi-ui components/CertDesigner/ + cert-manage.vue + fabric.js | 无冲突 |
| 8-MP-FORM | jst-uniapp components/jst-dynamic-form/ | 无冲突 |
| 9-MP-BOOKING | jst-uniapp pages-sub/contest/ | 无冲突 |
| 10-MP-VISUAL | jst-uniapp 15 页样式重塑 + styles/ | 与 8/9 可能有冲突 → 建议串行 |
| 11-MP-CERT | jst-uniapp pages-sub/my/ + cert/ | 与 10 可能有冲突 → 建议在 10 之后 |

---

## Wave 0 派发（3 卡并行）

### Agent 1: REFACTOR-1-DDL（Backend Agent）

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-1-DDL-数据库去JSON化.md 全部内容）

⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ 先在测试库建 7 张新表 + ALTER jst_contest 加 15 个字段
⭐ 赛事保存接口改为级联保存子表（先 deleteByContestId 再 batchInsert）
⭐ 赛事详情接口返回子表列表（按 sort_order ASC）
⭐ 旧 JSON 字段不删除，仅标记 @Deprecated
⭐ 新增赛事复制接口 POST /jst/partner/contest/copy/{contestId}
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### Agent 2: REFACTOR-2-DICT（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-2-DICT-全局枚举翻译.md 全部内容）

⭐ 禁止自建 dict.js 工具，使用若依原生字典机制：dicts 声明 + <dict-tag> 组件 + selectDictLabel
⭐ 全局搜索 el-table-column 涉及 category/auditStatus/sourceType/ownerType 等字段，改用 <dict-tag :options="dict.type.xxx" :value="value"/>
⭐ contest-edit.vue 的 categoryOptions 硬编码删除，改为 dicts: ['jst_contest_category'] 声明式加载
⭐ SQL 用 WHERE NOT EXISTS 保证幂等可重复执行
⭐ npm run build:prod 必须通过
```

### Agent 3: REFACTOR-6-BIZNO（Backend Agent）

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-6-BIZNO-统一编号规则引擎.md 全部内容）

⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ BizNoGenerateService 放在 jst-common 模块
⭐ 使用 Redis INCR 保证并发安全
⭐ 预置 3 条规则（cert_no/channel_auth_no/enroll_cert_no）
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```
-- 已经派发 -- 
---

## Wave 1 派发（2 卡并行，等 Wave 0 完成）

### Agent 4: REFACTOR-3-CONTEST（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-3-CONTEST-赛事编辑傻瓜化.md 全部内容）

⭐ 先读已有 contest-edit.vue 理解当前结构
⭐ 删除全部 6 处 el-alert 开发占位
⭐ 所有子表用 el-table 行内编辑，不用 JSON 编辑器
⭐ 报名表单「快速创建」用 el-drawer
⭐ 预约时间段支持"批量生成"
⭐ 赛事列表增加"复制"按钮
⭐ 新增"预览"功能（375px 手机模拟弹窗）
⭐ npm run build:prod 必须通过
```

### Agent 5: REFACTOR-7-MENU（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-7-MENU-菜单路由清理.md 全部内容）

⭐ 先查 sys_menu 表当前全量菜单
⭐ "竞赛通管理" → "平台数据管理"，"赛事方工作台" → "赛事管理中心"
⭐ 重复菜单隐藏（visible='1'），不删除
⭐ 产出 SQL 脚本
```

---

## Wave 2 派发（2 卡并行，等 Wave 1 完成）

### Agent 6: REFACTOR-4-ENROLL（Backend Agent 先行，Web Admin Agent 跟进）

**阶段 A — 后端（Backend Agent）**：
```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-4-ENROLL-报名审核与评审.md 全部内容）

⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ 本阶段只做后端部分（任务卡 §二）：
   1. 新建 jst_contest_reviewer 表
   2. 审核接口增加 scores 参数（打分）
   3. 评审老师 CRUD 接口
   4. 审核通过时自动调用 BizNoGenerateService 生成证书编号
⭐ 前端部分（§三）留给 Web Admin Agent 做
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

**阶段 B — 前端（Web Admin Agent，等后端完成后）**：
```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-4-ENROLL-报名审核与评审.md 全部内容）

⭐ 本阶段只做前端部分（任务卡 §三）：
   1. enroll-manage.vue 列表增强（证书编号、总分、评审老师列）
   2. 审核+打分一体化弹窗（审核通过时弹出打分面板）
   3. 卡片滑动审核视图（左侧详情/右侧操作/键盘快捷键）
   4. 赛事编辑页增加评审老师配置
⭐ 后端接口已就绪：审核接口支持 scores 参数、评审老师 CRUD
⭐ npm run build:prod 必须通过
```

### Agent 7: REFACTOR-5-CERT（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-5-CERT-证书拖拽设计器.md 全部内容）

⭐ 先 npm install fabric@5 安装 Fabric.js
⭐ CertDesigner 组件三栏布局（元素面板/画布/属性面板）
⭐ 动态变量系统：{{name}} {{contestName}} {{awardName}} {{score}} {{certNo}} {{date}} {{qrcode}} 等
⭐ 底图上传 + 拖拽元素 + 属性编辑 + 预览（示例数据替换）
⭐ 保存用 canvas.toJSON() → layout_json，加载用 canvas.loadFromJSON()
⭐ 集成到 cert-manage.vue + contest-edit.vue 快速新建 Drawer
⭐ 渠道授权书复用同一设计器（ownerType='channel' 区分）
⭐ npm run build:prod 必须通过
```

---

## Wave 3 派发（3 卡并行，等 Wave 2 完成）

### Agent 8: REFACTOR-8-MP-FORM（MiniProgram Agent）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-8-MP-FORM-动态表单渲染引擎.md 全部内容）

⭐ jst-dynamic-form 组件必须支持 10+ 字段类型
⭐ 实时校验（blur + submit），不等提交才报错
⭐ 文件上传支持图片/视频/PDF
⭐ readonly 模式复用于报名详情页
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### Agent 9: REFACTOR-9-MP-BOOKING（MiniProgram Agent）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-9-MP-BOOKING-预约与团队报名.md 全部内容）

⭐ 预约时间段从新的 appointmentSlotList API 获取
⭐ 团队人数严格校验（min/max）
⭐ 微信分享邀请成员
⭐ 所有防呆校验清单必须覆盖
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

### Agent 10: REFACTOR-10-MP-VISUAL（UI Refresh Agent）

```
你是竞赛通项目的 UI Refresh Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\UI_REFRESH_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-10-MP-VISUAL-C端视觉重塑.md 全部内容）

⭐ 本卡特许：允许自主设计视觉/动效/交互，不限于原型 PNG
⭐ 目标风格：极简留白 + 高级灰 + 微光过渡 + 卡片悬浮 + 动效克制
⭐ 必须用 uView 组件 + Design Token
⭐ 赛事详情必须接入新结构化数据（scheduleList/awardList/faqList 等）
⭐ 先出 Diff Plan，等我 confirm 再动手
⭐ script 块的业务逻辑尽量不动，只改 template + style
```

---

## Wave 4 派发（1 卡，等 Wave 3 完成）

### Agent 11: REFACTOR-11-MP-CERT（MiniProgram Agent）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-11-MP-CERT-成绩与证书展示.md 全部内容）

⭐ 成绩雷达图用 Canvas 绘制
⭐ 证书海报 Canvas 生成 + uni.saveImageToPhotosAlbum
⭐ 证书验证公开页（无需登录）
⭐ 入口要浅：我的页/赛事详情/报名详情都有入口
```

---

## 总工作量估算

| Wave | 卡数 | Agent 数 | 预计并行时长 |
|---|---|---|---|
| 0 | 3 | 3（2 Backend + 1 Admin） | 中 |
| 1 | 2 | 2（Admin） | 大（赛事编辑是 XL） |
| 2 | 2 | 2（1 Backend+Admin + 1 Admin） | 大（证书设计器是 XL） |
| 3 | 3 | 3（2 MP + 1 UI Refresh） | 大（视觉重塑是 XL） |
| 4 | 1 | 1（MP） | 中 |
| **总计** | **11** | **最多 3 并行** | |
