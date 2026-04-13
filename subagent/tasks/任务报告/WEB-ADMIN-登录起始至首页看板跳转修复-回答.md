# WEB-ADMIN 阶段报告（从登录改版开始）
## 1. 报告范围
- 起点：`登录界面优化（换风格 + 手机端适配）`
- 终点：`主页/看板跳转 404 修复（路径兜底 + 按钮禁用）`
- 目标对象：主 Agent（用于汇总阶段交付、风险和后续资源）

## 2. 阶段目标与完成度
- 登录页视觉升级：**完成**
- 登录页手机端白屏占满修复：**完成**
- 全站“若依”品牌文案替换为“竞赛通”：**完成（用户可见面）**
- 首页从纯文字改为流程化导航：**完成**
- 首页与看板入口 404 修复：**完成（已做路由兜底）**
- 构建门槛 `npm run build:prod`：**通过**

## 3. 详细改动（按里程碑）
### 3.1 登录页第一版改版（新风格 + 响应式）
- 文件：
  - `RuoYi-Vue/ruoyi-ui/src/views/login.vue`
- 内容：
  - 重新设计登录页视觉层（品牌区 + 表单区 + 渐变背景）。
  - 保持登录业务逻辑不变（验证码、记住密码、登录流程无业务改动）。
  - 增加移动端断点样式（`1024/768/420`）。

### 3.2 品牌替换（若依 -> 竞赛通）
- 前端标题与文案：
  - `RuoYi-Vue/ruoyi-ui/.env.development`
  - `RuoYi-Vue/ruoyi-ui/.env.production`
  - `RuoYi-Vue/ruoyi-ui/.env.staging`
  - `RuoYi-Vue/ruoyi-ui/vue.config.js`
  - `RuoYi-Vue/ruoyi-ui/src/settings.js`
  - `RuoYi-Vue/ruoyi-ui/src/layout/components/Navbar.vue`
  - `RuoYi-Vue/ruoyi-ui/src/components/RuoYi/Git/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/components/RuoYi/Doc/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/index.vue`（首页介绍整体替换为竞赛通内容）
  - `RuoYi-Vue/ruoyi-ui/package.json`（描述/作者）
- 后端可见文案：
  - `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/core/config/SwaggerConfig.java`
  - `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java`
  - `RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml`
  - `RuoYi-Vue/pom.xml`
- 说明：
  - 本次未做包名/目录名级别重构（如 `com.ruoyi`、`utils/ruoyi`），避免高风险连锁改动。

### 3.3 登录页第二版修正（手机白色铺满问题）
- 文件：
  - `RuoYi-Vue/ruoyi-ui/src/views/login.vue`
- 内容：
  - 修复移动端 `white panel full screen` 视觉问题，改为居中卡片。
  - 保留背景层次与底部版权可读性。
  - 增强禁用/聚焦/按钮触感体验。

### 3.4 首页“流程化 + 可跳转”重做
- 文件：
  - `RuoYi-Vue/ruoyi-ui/src/views/index.vue`
- 内容：
  - 去掉“适用角色”区块，压缩纯文字密度。
  - 新增“快捷入口 + 管理端流程导航”。
  - 所有流程动作改为可点击跳转入口。

### 3.5 主页/看板 404 修复（核心）
- 新增工具：
  - `RuoYi-Vue/ruoyi-ui/src/utils/route-access.js`
  - 提供：
    - `collectAvailablePaths(permissionRoutes)`
    - `resolveFirstAvailablePath(candidates, availableSet)`
- 首页改造：
  - `RuoYi-Vue/ruoyi-ui/src/views/index.vue`
  - 每个入口由单路径改为候选路径数组，按当前账号可访问路由自动匹配第一个可用路径。
  - 无可用路径时：按钮灰化 + 点击提示“当前账号未配置该菜单或无权限”。
- 看板改造：
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/dashboard/index.vue`
  - 待办卡与快速操作同样接入候选路径兜底。
  - 禁用态样式与提示逻辑与首页统一。

## 4. 构建与验证
- 执行目录：`D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui`
- 命令：`npm run build:prod`
- 结果：**多次执行均通过**
- 备注：仅存在体积 warning（asset/entrypoint size limit），无阻断错误。

## 5. 当前已知缺口 / 风险
1. 路径候选表是“前端兜底映射”，不是后端菜单单一真理源。
   - 若后续菜单路径再调整，仍需同步更新候选数组。
2. 未建立自动化 E2E 覆盖“入口可达性”。
   - 当前主要依赖人工点击验证与构建通过。
3. 品牌替换仍保留技术基座命名（`ruoyi` 包名/目录名）。
   - 这是刻意控制风险，不是遗漏，但需主 Agent 决策是否做二期深改。
4. 当前工作区仍有无关未跟踪目录（如 `logs/` 和历史产物目录）。
   - 建议主 Agent 在合并前统一清理策略。

## 6. 需要主 Agent 协同的后续资源
1. **菜单真值表资源**（强需求）
   - 需要一份“生产环境菜单 path + 权限点”基线（SQL 或导出 JSON），用于固化候选路径映射，减少后续漂移。
2. **测试账号矩阵**（强需求）
   - 至少提供：超级管理员、运营、审核、财务、渠道管理员 5 类账号，用于验证禁用态是否符合权限预期。
3. **验收脚本资源**（建议）
   - 需要主 Agent 指派 E2E 冒烟用例（登录 -> 首页入口 -> 看板入口 -> 返回），沉淀为回归脚本。
4. **品牌资源包**（建议）
   - 如需继续统一视觉，建议提供竞赛通 Logo/品牌色规范，避免后续页面风格再次分叉。

## 7. 建议下一步（供主 Agent 排期）
- P1：固化“菜单 path/权限点映射配置”到单独常量模块，减少页面内散落候选。
- P1：新增入口可达性冒烟用例（至少覆盖首页 + 看板）。
- P2：评估是否进行“ruoyi 技术命名”深度品牌化改造（高风险需单独任务卡）。

---
交付结论：本阶段从登录改版到首页/看板 404 修复已闭环，当前可稳定构建，用户侧关键体验问题已解决；后续重点转入“路径真值治理 + 自动化回归”。
