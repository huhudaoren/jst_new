# 派发清单 — 管理端全面修复（2 卡并行）

> BE + FE 同时派发，文件无冲突

---

## Agent BE: 后端接口补齐（Backend Agent）

```
你是竞赛通项目的 Backend Agent。请按以下两份文档的要求开始工作:

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\ADMIN-FIX-FINAL-全面修复.md 全部内容）

⭐ 测试库连接信息：
   url: jdbc:mysql://59.110.53.165:3306/jst_new
   username: jst_new
   password: J8zZpAa4zG8y6a7e
⭐ 只做 §二.2.1 后端部分：
   1. 新增 ParticipantAdminController（jst-user 模块）
      - GET /jst/admin/participant/list — 参赛档案列表（分页+模糊搜索 name/phone）
      - GET /jst/admin/participant/{id} — 参赛档案详情
      - 权限：@PreAuthorize("@ss.hasPermi('jst:participant:list')")
   2. 新增 participant-user-map 查询接口
      - GET /jst/admin/participant-user-map/list — 认领关系列表
      - 支持按 participantId 或 userId 过滤
   3. 渠道绑定用户查询（如果现有 binding/list 接口不支持 channelId 过滤）
      - 确认 GET /jst/admin/binding/list 或类似接口支持 ?channelId=xxx 参数
⭐ 参照已有 JstParticipantMapper.xml（gen/ 目录下有模板）但放到 jst-user 模块
⭐ 列表接口要 LEFT JOIN jst_user 返回 userName（如已认领）
⭐ mvn compile 必须通过
请开始 Step 1 阅读文档,然后 Step 2 自检答题,最后实现并交付报告。
```

---

## Agent FE: 前端全面修复（Web Admin Agent）

```
你是竞赛通项目的 Web Admin Agent。请按以下两份文档工作：

【文档 1: SYSTEM PROMPT】
（粘贴 D:\coding\jst_v1\subagent\WEB_ADMIN_AGENT_PROMPT.md 全部内容）

【文档 2: 任务卡】
（粘贴 D:\coding\jst_v1\subagent\tasks\ADMIN-FIX-FINAL-全面修复.md 全部内容）

⭐ 只做前端部分（§二.2.2 ~ §二.2.4），共 4 大项：

一、修复"无权限"问题（最紧急）：
   修改 src/utils/route-access.js 的 resolveFirstAvailablePath 函数，
   增加末段匹配逻辑：当精确匹配失败时，用候选路径的最后一段（如 'contest'）
   在 availableSet 中查找以 '/contest' 结尾的路径。
   这样 candidates: ['/jst/contest'] 能匹配到 '/jst/group-contest/contest'。

二、Dashboard 功能导航增强：
   在 src/views/jst/dashboard/index.vue 增加"功能导航"区域：
   - 8 个分组入口卡片（赛事运营/订单交易/用户渠道/营销权益/积分商城/赛事数据/财务管理/风控审计）
   - 每个卡片：图标 + 标题 + 一句话描述 + 点击跳转
   - 路径用 resolveFirstAvailablePath 动态匹配（兼容分组变化）
   - 同时更新 todoItemConfigs 和 quickActionConfigs 的 candidates 为多路径兼容

三、渠道→用户链路：
   在 src/views/jst/channel/index.vue 的详情 drawer 中增加"绑定用户"Tab：
   - el-tabs 切换：渠道信息 | 绑定用户
   - 绑定用户列表：学生姓名/手机号/绑定时间/操作（查看档案跳转）
   - 数据来源：GET /jst/admin/binding/list?channelId=xxx（如接口不存在，先用空表+TODO注释）

四、JSON 展示可视化：
   排查所有详情页中直接展示 JSON 的地方，改为可视化：
   - qualification_json → 附件列表（文件名 + 图片预览/下载链接）
   - settlement_info_json → el-descriptions（户名/账号/开户行）
   - invoice_info_json → el-descriptions（发票类型/税号）
   - materials_json → 附件列表
   - form_snapshot_json → el-descriptions 按 key-value 渲染
   - threshold_json → key=value 表格（已在 BATCH3 改为 popover，确认生效）
   - target_json → 目标标签列表
   新建通用组件 src/components/JsonDisplay/index.vue：
   - 数组 → el-tag 列表
   - 对象 → el-descriptions
   - 支持 labelMap 中文映射

五、修复 admin-participant.js API 路径：
   将 /system/jst_participant/ 全部改为 /jst/admin/participant/
   将 /system/jst_participant_user_map/ 全部改为 /jst/admin/participant-user-map/

⭐ npm run build:prod 必须通过
```

---

## 完成后动作

1. Review 2 份报告
2. 统一 commit
3. 重新编译部署测试服务器
4. 验收：
   - 运营看板快捷入口可点击（不再提示无权限）
   - 功能导航 8 分组入口可跳转
   - 参赛档案页正常加载（不再 404）
   - 渠道详情有"绑定用户"Tab
   - 详情页 JSON 改为可视化展示
