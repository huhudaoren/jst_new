# 派发清单 — 平台数据管理全量精品化（4 卡并行）

> 40+ 页面傻瓜化改造 | BE 1 卡 + FE 3 卡 | 全部可同时派发

---

## 依赖与并行关系

```
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ BE: 名称补齐      │  │ BATCH1: 订单渠道  │  │ BATCH2: 积分赛事  │  │ BATCH3: 财务风控  │
│ Backend Agent    │  │ Web Admin Agent  │  │ Web Admin Agent  │  │ Web Admin Agent  │
│                  │  │                  │  │                  │  │                  │
│ 10 Mapper JOIN   │  │ 17 页精品化       │  │ 13 页精品化       │  │ 10 页 + 14 字典   │
│ 10 Domain 字段   │  │                  │  │                  │  │                  │
└──────────────────┘  └──────────────────┘  └──────────────────┘  └──────────────────┘
         │                    │                    │                    │
         └────────────────────┴────────────────────┴────────────────────┘
                              全部可并行（文件无冲突）
```

### 文件冲突检查

| 卡 | 改动范围 | 冲突风险 |
|---|---|---|
| BE | MapperExt.xml + Domain.java（后端） | 无冲突 |
| BATCH1 | views/jst/order/ + appointment/ + binding/ + channel/ + marketing/ + coupon/ + rights/ + form-template/ + dashboard/ | 无冲突 |
| BATCH2 | views/jst/points/ + event/ + mall/ | 无冲突 |
| BATCH3 | views/jst/finance/ + risk/ + message/ + SQL 文件 | 无冲突 |

---

## Agent BE: 后端名称补齐（Backend Agent）

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\ADMIN-POLISH-BE-后端名称补齐.md 全部内容）

⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ 只改 selectList 查询，追加 LEFT JOIN 和名称字段
⭐ 不改 insert/update/delete
⭐ Domain 新增字段用 transient（非持久化），不影响现有逻辑
⭐ 使用 LEFT JOIN 不用 INNER JOIN（关联表无数据时主记录不能丢）
⭐ ownerName 需要根据 owner_type 条件 JOIN 不同表（用 CASE WHEN 或 COALESCE）
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## Agent BATCH1: 订单交易 + 用户渠道 + 营销权益（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\ADMIN-POLISH-BATCH1-订单用户渠道精品化.md 全部内容）

⭐ 本卡改造 17 个页面，全部在以下目录：
   - views/jst/order/ (6页)
   - views/jst/appointment/ (1页)
   - views/jst/form-template/ (1页)
   - views/jst/binding/ (1页)
   - views/jst/channel/admin-withdraw/ (1页)
   - views/jst/marketing/ (4页)
   - views/jst/coupon/ (1页)
   - views/jst/rights/ (1页)
   - views/jst/dashboard/ (1页验证)
⭐ 每页必须应用 S1-S6 全部 6 项标准（Hero区/搜索精简/表格优化/弹窗优化/操作确认/空状态）
⭐ 使用若依原生 dicts 声明 + <dict-tag> 组件翻译枚举（不自建翻译工具）
⭐ 金额格式化用已有的 formatMoney 工具函数（import { formatMoney } from '@/utils/format'）
⭐ 关联字段如果后端还没返回 xxxName，先用 ID 展示但加 TODO 注释，等 BE 卡完成后自动生效
⭐ 删除/审核操作必须有 this.$modal.confirm 确认弹窗
⭐ npm run build:prod 必须通过
```

---

## Agent BATCH2: 积分商城 + 赛事数据（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\ADMIN-POLISH-BATCH2-积分赛事数据精品化.md 全部内容）

⭐ 本卡改造 13 个页面，全部在以下目录：
   - views/jst/points/ (7页)
   - views/jst/event/ (6页)
⭐ 每页必须应用 S1-S6 全部 6 项标准
⭐ 积分流水特别要求：收入行绿色文字、支出行红色文字（用 CSS class 区分）
⭐ 商城商品要展示商品缩略图（el-image + lazy loading）
⭐ 证书模板要展示底图缩略图
⭐ 成绩记录分数用大字高亮
⭐ 使用若依原生 dicts + dict-tag
⭐ npm run build:prod 必须通过
```

---

## Agent BATCH3: 财务 + 风控 + 消息 + 字典补齐（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\ADMIN-POLISH-BATCH3-财务风控消息精品化.md 全部内容）

⭐ 本卡改造 10 个页面 + 产出 14 个字典 SQL：
   - views/jst/finance/ (3页)
   - views/jst/risk/ (5页)
   - views/jst/message/ (2页)
   - 架构设计/ddl/99-migration-admin-polish-dict.sql (新建)
⭐ 每页必须应用 S1-S6 全部 6 项标准
⭐ 风险等级三色标签：低=success(绿)、中=warning(橙)、高=danger(红)
⭐ 审计日志操作结果：成功=success(绿)、失败=danger(红)
⭐ 黑白名单：黑=danger(红)、白=success(绿)
⭐ 字典 SQL 必须幂等（WHERE NOT EXISTS），可重复执行
⭐ 风控规则的 threshold_json 至少展示为只读格式化 JSON（不需要可视化编辑器）
⭐ 消息模板的 content 中 ${var} 变量用 <el-tag size="mini"> 高亮展示
⭐ 先执行字典 SQL 到测试库，再改页面（dict-tag 依赖字典数据）
⭐ npm run build:prod 必须通过

⭐ 测试库连接信息（用于执行字典 SQL）：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
```

---

## 完成后动作

1. Review 4 份报告
2. 统一 commit
3. 重新编译部署测试服务器（39.107.69.244）
   - mvn package → 上传 JAR → systemctl restart jst（BE 卡有后端改动）
   - npm run build:prod → 上传 dist → Nginx reload
4. 逐页验收：
   - Hero 标题 ✓
   - 搜索 placeholder ✓
   - 枚举 dict-tag ✓
   - 金额 ¥ 格式 ✓
   - 状态彩色标签 ✓
   - 关联显示名称 ✓
   - 删除/审核确认弹窗 ✓
5. 更新 CLAUDE.md
