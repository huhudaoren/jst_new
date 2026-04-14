# 派发清单 — 收尾卡（证书客户端渲染 + P2P3 清理）

> 2 张任务卡，拆为 4 个 Agent 任务，3 个可立即并行

---

## 依赖与并行关系

```
┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│ A-BE: CERT-RENDER   │  │ A-MP: CERT-RENDER   │  │ B: P2P3-BE          │
│ 后端接口小改         │  │ 小程序渲染引擎(主力) │  │ 评审过滤+Redis+档案  │
│ Backend Agent       │  │ MiniProgram Agent   │  │ Backend Agent       │
│                     │  │                     │  │                     │
│ 改动范围:            │  │ 改动范围:            │  │ 改动范围:            │
│ - WxCertController  │  │ - utils/cert-       │  │ - EnrollRecordSvc   │
│ - CertDetailVO      │  │   renderer.js (新)  │  │ - 15-Redis-Key文档  │
│ - ContestMapperExt  │  │ - cert-detail.vue   │  │ - ReviewerMapper    │
└──────────┬──────────┘  └──────────┬──────────┘  └──────────┬──────────┘
           │ 三者文件无冲突，全部立即并行            │
           ↓                                       ↓
     完成后合并 commit                       完成后接 C
                                                   │
                                                   ↓
                                      ┌─────────────────────┐
                                      │ C: P2P3-MP          │
                                      │ 旧组件清理+微信邀请   │
                                      │ MiniProgram Agent   │
                                      │ 等 B 完成后派发       │
                                      └─────────────────────┘
```

**并行方案：A-BE + A-MP + B 三个立即派发，文件完全不重叠。C 等 B 完成后再派。**

### 文件冲突检查

| Agent | 改动范围 | 与其他冲突 |
|---|---|---|
| A-BE | WxCertController + CertDetailVO + ContestMapperExt | 无 |
| A-MP | utils/cert-renderer.js(新) + cert-detail.vue | 无 |
| B | EnrollRecordServiceImpl + Redis文档 + ReviewerMapper | 无 |
| C | jst-form-render 组件删除 + team-enroll.vue 微信邀请 | 无（等 B 后派） |

---

## Agent A-BE: CERT-RENDER 后端部分（Backend Agent）

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-TODO-CERT-RENDER-证书渲染引擎.md 全部内容）

⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ 只做 §三 Backend 部分（改动很小）：
   1. GET /jst/wx/cert/{id} 返回 layoutJson + bgImage + variables Map
   2. 后端不做渲染，证书图片由客户端 Canvas 实时生成
⭐ variables Map 组装：从 cert_record → enroll_record → contest → score_record 关联查出
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

## Agent A-MP: CERT-RENDER 小程序部分（MiniProgram Agent）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-TODO-CERT-RENDER-证书渲染引擎.md 全部内容）

⭐ 只做 §四 MiniProgram 部分（主力工作）：
   1. 新建 utils/cert-renderer.js — 解析 Fabric.js layoutJson → Canvas 2D 绘制
   2. 支持 text/i-text/textbox/image/rect/line 6 种元素
   3. 动态变量替换（certVarKey + {{xxx}} 双模式）
   4. 坐标系映射（origin/scale/rotate 与 Fabric.js 一致）
   5. cert-detail.vue 集成真实渲染，替换当前模拟样式
   6. 字体映射表（SimHei → sans-serif 等 fallback）
⭐ 核心目标：小程序渲染结果与管理端 Fabric.js 预览视觉一致
⭐ 不改后端代码
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## Agent B: P2P3-BE（Backend Agent）

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-TODO-P2P3-收尾清理.md 全部内容）

⭐ 测试库连接信息同上
⭐ 只做 §二 Backend 部分（3 项）：
   1. 评审老师自动过滤：reviewer 表有记录时追加 contest_id 过滤
   2. Redis Key 补录到 架构设计/15-Redis-Key与锁规约.md
   3. 团队报名 participantId 关联（复用 createTempParticipant 逻辑）
⭐ 前端部分（§三）留给 MiniProgram Agent
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## Agent C: P2P3-MP（MiniProgram Agent，等 Agent B 完成后）

```
你是竞赛通项目的 MiniProgram Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\MINIPROGRAM_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\REFACTOR-TODO-P2P3-收尾清理.md 全部内容）

⭐ 只做 §三 MiniProgram 部分（2 项）：
   1. 全局搜索 jst-form-render 引用，确认无引用后删除组件目录
   2. 微信邀请增强（如后端已提供 team/{teamId}/join 接口则实现，否则跳过）
⭐ 不改后端代码
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## 完成后动作

1. Review 4 份报告（A-BE + A-MP + B + C）
2. 统一 commit
3. 重新编译部署测试服务器（39.107.69.244）
   - mvn package → 上传 JAR → systemctl restart jst
   - npm run build:prod → 上传 dist → Nginx reload
   - npm run build:h5 → 上传 H5 dist → Nginx reload
4. 更新 CLAUDE.md 遗留项清零
5. 全流程验收：管理端创建赛事→设计证书→审核打分→小程序查看证书（Canvas 渲染）→保存相册
