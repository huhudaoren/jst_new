# REFACTOR-3-CONTEST 赛事编辑傻瓜化 — 交付报告

> Agent: Web Admin Agent | 完成时间: 2026-04-14

---

## 一、改动文件清单

| 文件 | 操作 | 说明 |
|---|---|---|
| `ruoyi-ui/src/api/partner/contest.js` | 编辑 | +`copyPartnerContest(id)` +`createFormTemplate(data)` |
| `ruoyi-ui/src/views/partner/components/ContestPreview.vue` | **新建** | 375px 手机模拟预览组件 |
| `ruoyi-ui/src/views/partner/contest-edit.vue` | **重写** | 全量傻瓜化重构（953行 → 新版） |
| `ruoyi-ui/src/views/partner/contest-list.vue` | 编辑 | +复制按钮, -toolbar el-alert |

---

## 二、DoD 对照

| # | 检查项 | 状态 |
|---|---|---|
| 1 | 6 处 el-alert 全部删除 | ✅ |
| 2 | Tab A 新增 6 个字段（banner/主办方/协办方/地址/名额/限购） | ✅ |
| 3 | Tab B 赛程表格化管理 + scorePublishTime + aftersaleDeadline | ✅ |
| 4 | Tab C 报名表单快速创建 Drawer（80% 宽，字段 el-table 编辑器） | ✅ |
| 5 | Tab D 成绩项 el-table（名称/满分/权重）+ 奖项 el-table + 预设快填 | ✅ |
| 6 | Tab E 时间段 el-table + 批量生成 Dialog（日期范围/时间模板/间隔/容量） | ✅ |
| 7 | Tab F 团队配置功能化（开关/人数/字段 checkbox-group） | ✅ |
| 8 | Tab G FAQ el-table（问题/回答行内编辑 + 上下移动排序） | ✅ |
| 9 | 复制新增功能（contest-list 操作列 + mobile 卡片） | ✅ |
| 10 | 预览功能（375px 手机模拟 el-dialog） | ✅ |
| 11 | 所有 JSON 中间层代码删除 | ✅ |
| 12 | `npm run build:prod` 通过 | ✅ |

---

## 三、删除的代码（JSON 中间层清理）

以下旧代码已全部删除，不再存在：

- `scoreRuleForm` / `certRuleForm` / `writeoffForm` 中间对象
- `createDefaultScoreRule()` / `createDefaultCertRule()` / `createDefaultWriteoffConfig()` 工厂函数
- `syncScoreRuleJson()` / `syncCertRuleJson()` / `syncWriteoffConfig()` / `syncAllJsonFields()` 同步方法
- `hydrateRuleFormsFromJson()` 反序列化方法
- `parseJsonObject()` / `normalizeStringArray()` / `normalizeNumberArray()` 工具方法
- `normalizeScoreRule()` / `normalizeCertRule()` / `normalizeWriteoffConfig()` 规范化方法
- `validateJsonFields()` / `isValidJson()` JSON 校验方法
- `scoreItemInputVisible` / `scoreItemInputValue` tag 编辑器状态
- `showScoreItemInput()` / `confirmScoreItem()` / `removeScoreItem()` tag 交互方法
- 3 个 `watch` 监听器（scoreRuleForm / certRuleForm / writeoffForm）

---

## 四、新增功能详情

### 4.1 赛程阶段管理（Tab B）

- el-table 行内编辑：阶段名称、开始/结束时间（datetime-picker）、场地、说明
- 添加/删除/上移/下移操作
- 保存时自动设置 `sortOrder`

### 4.2 报名表单快速创建（Tab C）

- el-drawer 80% 宽，内嵌：
  - 模板名称输入
  - 字段列表 el-table：字段名 | 类型(text/radio/checkbox/file/age/number) | 必填(switch) | 操作
  - 保存调用 `POST /jst/event/jst_enroll_form_template`，成功后自动刷新下拉列表

### 4.3 奖项预设模板（Tab D）

- el-dropdown 快速填充：
  - 常规模板：一等奖/二等奖/三等奖/优秀奖
  - 金银铜模板：金奖/银奖/铜奖

### 4.4 预约时间段批量生成（Tab E）

- el-dialog 参数表单：日期范围、开始时间、结束时间、间隔分钟数、每段容量、场地
- 算法：遍历日期范围内每天，按间隔切分时间段，追加到已有列表

### 4.5 团队配置（Tab F）

- 团队开关：computed `teamEnabled`，开启时默认 min=2, max=5
- 队长/队员字段：checkbox-group，逗号分隔存储（对应 DTO 的 teamLeaderFields/teamMemberFields）

### 4.6 赛事复制（contest-list）

- 操作列新增「复制」按钮（桌面表格 + 移动卡片）
- 调用 `POST /jst/partner/contest/copy/{id}`
- 成功后跳转到新赛事编辑页

### 4.7 手机预览（ContestPreview.vue）

- 375x667 手机模拟器（黑色边框 + 圆角）
- 渲染区块：Banner/封面、标题、基础信息行、价格、赛程时间线、奖项列表、赛事详情摘要、FAQ、底部报名按钮
- Props: `visible`/`contestData`/`scheduleList`/`awardList`/`faqList`

---

## 五、关键设计决策

| 决策 | 原因 |
|---|---|
| 排序用上移/下移按钮替代拖拽 | 避免引入 sortablejs 新依赖（硬性约束：不许引入新 npm 依赖） |
| writeoffMode/needSignIn 直接作为 form 顶层字段 | DTO 已有独立字段，不再需要 writeoffConfig JSON 包装 |
| certRuleJson 在 buildPayload 时从 certTemplateIds + certIssueMode 合成 | 保持后端协议兼容，前端用户无感知 JSON |
| 表单模板创建复用 admin 级 API | partner 端无独立 create 接口，admin API 可用 |
| contest-list el-alert 删除 | 属于开发期提示，不应暴露给最终用户 |

---

## 六、验证结果

```
npm run build:prod → DONE Build complete. ✅
```

---

## 七、已知限制

1. **表单模板创建**依赖 admin 级 API 权限，partner 角色可能需要后端补齐 `@PreAuthorize` 或新增 partner 级 create 端点
2. **证书模板快速创建**为简化版（名称 + 底图），完整证书设计器待 REFACTOR-5 接入
3. **拖拽排序**未实现，当前用上移/下移按钮替代
