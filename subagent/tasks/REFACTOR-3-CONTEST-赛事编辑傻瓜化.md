# REFACTOR-3-CONTEST — 赛事编辑页傻瓜化重构

> 优先级：P1 | 预估：XL | Agent：Web Admin Agent
> 依赖：REFACTOR-1-DDL 完成后开始

---

## 一、背景

当前 contest-edit.vue 有 6 处开发者占位 el-alert、JSON 编辑器、占位 Tab、缺失字段。需要重构为完整的傻瓜式赛事配置器，所有配置通过表单化控件完成，用户不接触任何 JSON。

## 二、前置条件

REFACTOR-1-DDL 已完成：
- 后端新增 7 张子表（schedule/award/faq/scoreItem/appointmentSlot/chapter/lesson）
- jst_contest 新增 15 个结构化字段
- 赛事保存接口支持级联保存子表
- 赛事详情接口返回子表列表

## 三、改造清单

### 3.1 全局清理

- 删除 **所有 6 处 el-alert** 警告框
- 删除所有 "缺少结构化字段"、"等待后端协议"、"已在字段反馈中列出" 文案
- 删除 "Q-07 开关已随保存 payload 附带" 等技术备注

### 3.2 Tab A — 基本信息增强

新增字段（在现有表单中合理安排位置）：
- **Banner 大图**：`ImageUpload v-model="form.bannerImage" :limit="1"`，推荐 16:9，提示"首页轮播展示图"
- **主办方**：`el-input v-model="form.organizer"` 文本
- **协办方**：`el-input v-model="form.coOrganizer"` 文本
- **比赛地址**：`el-input v-model="form.eventAddress"` 文本
- **总名额**：`el-input-number v-model="form.totalQuota" :min="0"` 0=不限
- **单人限购**：`el-input-number v-model="form.perUserLimit" :min="1"`
- **分类** 改为从字典 API 加载（不再硬编码 categoryOptions）

### 3.3 Tab B — 赛程管理（表格化替换）

删除原有的时间字段 + el-alert 占位，改为**可视化赛程表格**：

```
保留：报名开始/结束、比赛开始/结束、售后宽限天数（这些是 jst_contest 本身的字段）
新增：赛程阶段管理表格
```

- el-table 展示 `scheduleList`：阶段名称 | 时间范围 | 场地 | 说明 | 操作
- 表格上方"添加阶段"按钮
- 行内编辑：点击单元格直接编辑（el-input / el-date-picker）
- 删除按钮带确认
- 拖拽排序（el-table-draggable 或 sortablejs）
- 新增字段：`成绩发布时间`（score_publish_time）、`售后截止时间`（aftersale_deadline）

### 3.4 Tab C — 报名规则增强

保留现有字段（价格、渠道报名、积分抵扣、预约可退），新增：

**报名表单快速创建**：
- 在 el-select 右侧加「+ 新建表单」按钮
- 点击弹出 **el-drawer** (宽度 80%)，内嵌表单模板编辑器
- 编辑器内容：
  - 模板名称输入
  - 字段列表 el-table：字段名(label) | 字段类型(下拉: text/radio/checkbox/file/age/number) | 必填(switch) | 操作
  - "添加字段"按钮，新增一行
  - 保存按钮：调用表单模板创建 API，成功后自动选中新模板
- Drawer 关闭后刷新表单模板下拉列表

### 3.5 Tab D — 成绩与证书增强

**成绩项**（替代 tag 编辑器）：
- el-table 展示 `scoreItemList`：项目名称 | 满分 | 权重 | 操作
- 行内编辑
- "添加成绩项"按钮
- 删除"会自动转为可提交 JSON"的技术提示
- 成绩发布模式保留（手动/自动 select）

**奖项设置**（新增区域）：
- el-table 展示 `awardList`：奖项名称 | 等级 | 说明 | 名额 | 操作
- 预设快速填充按钮：「常规模板」（一/二/三等奖 + 优秀奖）、「金银铜模板」
- 行内编辑 + 添加/删除

**证书模板**（增强）：
- 保留 el-select 多选
- 旁加「+ 新建模板」按钮 → 弹出 el-drawer（证书设计器，REFACTOR-5 完成后接入，先做简化版）
- 简化版：模板名称 + 底图上传 + 保存
- 证书颁发模式保留（手动/自动 select）

### 3.6 Tab E — 预约配置结构化

保留基础开关（开启预约、预约容量、重复预约），新增：

**时间段管理**：
- el-table 展示 `appointmentSlotList`：日期 | 开始时间 | 结束时间 | 场地 | 容量 | 操作
- 「添加时间段」按钮（单条添加）
- 「批量生成」按钮：弹出对话框
  - 日期范围选择（el-date-picker type="daterange"）
  - 时间模板：开始时间 + 结束时间
  - 间隔分钟数
  - 每段容量
  - 场地名称
  - 点击"生成"→ 自动计算并填充多行时间段
- 核销配置：从 JSON 改为直接 select（writeoff_mode）+ switch（need_sign_in）

### 3.7 Tab F — 团队预约配置

替换占位内容为功能化表单：
- **团队开关**：`el-switch`（对应 team_min_size > 0）
- **团队人数**：最小 `el-input-number` / 最大 `el-input-number`
- **队长需填字段**：el-checkbox-group（姓名/手机/身份证/学校/年级），多选存为逗号分隔
- **队员需填字段**：同上
- **说明文案**：简短描述团队报名流程

### 3.8 Tab G — 常见问题（新增）

- el-table 展示 `faqList`：问题 | 回答 | 操作
- "添加问题"按钮
- 行内编辑（问题用 el-input，回答用 el-input type="textarea"）
- 拖拽排序

### 3.9 新功能 — 复制新增

在 contest-list.vue 的操作列增加「复制」按钮：
- 调用 `POST /jst/partner/contest/copy/{contestId}`（REFACTOR-1-DDL 已实现）
- 成功后跳转到新赛事编辑页
- 提示"已复制，请修改后保存"

### 3.10 新功能 — 赛事预览

在 contest-edit.vue 顶部操作栏增加「预览」按钮：
- 点击弹出 el-dialog（宽 375px，模拟手机屏幕）
- Dialog 内渲染赛事详情的简化版（标题、封面/Banner、时间、价格、描述、赛程、奖项、FAQ）
- 样式参照小程序端赛事详情页
- **注意**：预览组件 `ContestPreview.vue` 放在 `views/partner/components/` 下，后续 C 端可复用渲染逻辑

### 3.11 数据流改造

```javascript
// 表单保存时
const payload = {
  ...this.form,               // 基础字段（含新增 15 个）
  scheduleList: this.scheduleList,     // 子表
  awardList: this.awardList,
  faqList: this.faqList,
  scoreItemList: this.scoreItemList,
  appointmentSlotList: this.appointmentSlotList
}

// 表单加载时
const detail = await getPartnerContest(contestId)
this.form = detail.data           // 基础字段
this.scheduleList = detail.data.scheduleList || []
this.awardList = detail.data.awardList || []
// ...
```

- 删除 `scoreRuleForm`、`certRuleForm`、`writeoffForm` 中间对象
- 删除 `syncScoreRuleJson`、`syncCertRuleJson`、`syncWriteoffConfig` 同步方法
- 删除 `parseJsonObject`、`hydrateRuleFormsFromJson` 等 JSON 处理方法

## 四、DoD

- [ ] 6 处 el-alert 全部删除
- [ ] Tab A 新增 6 个字段（banner/主办方/协办方/地址/名额/限购）
- [ ] Tab B 赛程表格化管理
- [ ] Tab C 报名表单快速创建 Drawer
- [ ] Tab D 成绩项/奖项表格化 + 证书快速创建
- [ ] Tab E 时间段管理 + 批量生成
- [ ] Tab F 团队配置功能化
- [ ] Tab G FAQ 管理
- [ ] 复制新增功能
- [ ] 预览功能
- [ ] 所有 JSON 中间层代码删除
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
