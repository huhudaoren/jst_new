# ADMIN-UX-B1 报告

## 1. 任务结果

本卡已完成以下交付：

- 3 个页面完成 JSON 可视化改造：
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/risk/jst_risk_rule/index.vue`
  - `RuoYi-Vue/ruoyi-ui/src/views/jst/message/jst_message_template/index.vue`
- 新增 `JstJsonEditor` 组件族 6 个文件：
  - `FormSchemaEditor.vue`
  - `FieldEditDrawer.vue`
  - `SchemaPreview.vue`
  - `ThresholdEditor.vue`
  - `MessageTemplateEditor.vue`
  - `variable-map.js`
- 新增字典 SQL：
  - `架构设计/ddl/99-migration-admin-ux-b1-dict.sql`
- 本地执行：
  - `99-migration-admin-ux-b1-dict.sql` 已成功写入本地 `jst` 数据库
  - `npm run build:prod` 已通过

说明：

- 当前终端环境不适合稳定产出页面截图，本报告用“代码落点 + 本地构建 + 程序化校验 + 原始 JSON 文本证据”代替截图。
- 这次没有改后端 DTO / VO / Controller，也没有碰 `views/partner/*`、`router/index.js`、B2 范围页面。

## 2. 组件交付

### 2.1 `FormSchemaEditor.vue`

文件：`RuoYi-Vue/ruoyi-ui/src/components/JstJsonEditor/FormSchemaEditor.vue`

职责：

- 承接 `schemaJson` 的可视化编辑、字段排序、字段概览、raw 兜底
- PC 端支持拖拽排序
- 手机端视觉区只读，并明确提示建议 PC 编辑
- 对 `group / conditional` 自动 raw fallback

核心 props：

- `value`
- `fieldTypeOptions`
- `readonly`
- `mobileReadonly`

核心 emit：

- `input`
- `change`
- `fallback-change`

### 2.2 `FieldEditDrawer.vue`

文件：`RuoYi-Vue/ruoyi-ui/src/components/JstJsonEditor/FieldEditDrawer.vue`

职责：

- 编辑字段基础信息：`key / label / type / required / placeholder / description / tip`
- 编辑选项类字段配置：`options`
- 编辑上传类字段配置：`accept / maxCount`

核心 props：

- `visible`
- `field`
- `fieldTypeOptions`
- `isMobile`

说明：

- `phone / idcard / group / conditional` 在视觉新增编辑中做了禁用提示，避免前端可选但后端白名单不接受。

### 2.3 `SchemaPreview.vue`

文件：`RuoYi-Vue/ruoyi-ui/src/components/JstJsonEditor/SchemaPreview.vue`

职责：

- 模拟学生填表的结构预览
- 只展示字段顺序、必填、占位提示、选项、上传提示
- 不复刻小程序端 `phone / idcard / age` 校验逻辑

### 2.4 `ThresholdEditor.vue`

文件：`RuoYi-Vue/ruoyi-ui/src/components/JstJsonEditor/ThresholdEditor.vue`

职责：

- 提供频次 / 金额 / 名单 / raw 四种编辑模式
- 输出仍为扁平 JSON 字符串
- 遇到嵌套对象或未知键时自动切 raw

核心 props：

- `value`
- `ruleType`
- `isMobile`

核心 emit：

- `input`
- `fallback-change`

### 2.5 `MessageTemplateEditor.vue`

文件：`RuoYi-Vue/ruoyi-ui/src/components/JstJsonEditor/MessageTemplateEditor.vue`

职责：

- 变量 chips 插入
- `${var}` token 高亮
- 场景 demo 数据渲染预览
- 高级 raw 兜底

核心 props：

- `value`
- `scene`
- `channel`
- `sceneOptions`
- `isMobile`

### 2.6 `variable-map.js`

文件：`RuoYi-Vue/ruoyi-ui/src/components/JstJsonEditor/variable-map.js`

职责：

- 统一输出 `parseTemplateVars`
- 统一输出 `insertVariableAtCursor`
- 统一输出 `getSceneVariableMeta`
- 统一输出 `renderTemplatePreview`

当前内置场景：

- `auth_result`
- `withdraw_result`
- `settle_result`
- `points_change`
- `ship`
- `aftersale`

## 3. 页面改造说明

### 3.1 表单模板页

文件：`RuoYi-Vue/ruoyi-ui/src/views/jst/form-template/index.vue`

改前：

- Drawer 内左侧 raw textarea + 右侧简单 preview
- 列表页没有字段概览

改后：

- 列表页新增“字段概览”：统一显示 `N 字段 · M 必填`
- 编辑 Drawer 改为挂载 `JstFormSchemaEditor`
- 原 textarea 沉入组件内部“高级 raw”
- 手机端视觉区只读，raw 仍可编辑

### 3.2 风控规则页

文件：`RuoYi-Vue/ruoyi-ui/src/views/jst/risk/jst_risk_rule/index.vue`

改前：

- `thresholdJson` 仅为 Dialog 内 textarea
- `ruleType / dimension` 都是普通 `el-select`

改后：

- `thresholdJson` 改为 `JstThresholdEditor`
- `ruleType / dimension` 在 PC 端改为卡片选择，手机端回落 `el-select`
- `thresholdSummary()` 保持不动，保存格式继续兼容

### 3.3 消息模板页

文件：`RuoYi-Vue/ruoyi-ui/src/views/jst/message/jst_message_template/index.vue`

改前：

- `form.content` 为原始 textarea
- `SCENE_OPTIONS` / `parseTemplateVars` 为页面内硬编码

改后：

- `form.content` 改为 `JstMessageTemplateEditor`
- `scene` 改接 `jst_message_scene` 字典
- `channel` 在 PC 端改为分段按钮，手机端回落 `el-select`
- 列表 preview / 编辑器 preview 共用 `variable-map.js`

## 4. 字典 SQL 执行结果

文件：`架构设计/ddl/99-migration-admin-ux-b1-dict.sql`

本地执行结果：

```text
OK: executed 99-migration-admin-ux-b1-dict.sql
```

落库后的关键字典值：

### 4.1 `jst_form_field_type`

```text
text / textarea / phone / idcard / age / number / radio / checkbox / select / date / image / audio / video / file / group / conditional
```

### 4.2 `jst_message_scene`

```text
auth_result / withdraw_result / settle_result / points_change / ship / aftersale
```

## 5. 验收记录

### B1-1 表单模板 happy path

结果：通过

说明：

- 表单页已支持新增 `text / age / radio / file` 等当前后端允许且前端视觉覆盖的字段
- 列表页可直接看到 `N 字段 · M 必填`
- 视觉编辑结果仍同步回 `schemaJson`

补充：

- `phone / idcard` 没有直接开放在视觉新增编辑中，因为当前后端 `FormTemplateServiceImpl` 白名单未包含这两种类型；继续开放会导致“前端可选、保存时报非法类型”。

### B1-2 表单模板预览

结果：通过

说明：

- `SchemaPreview` 已能展示字段顺序、必填、placeholder、选项和上传限制
- 未复制小程序端校验逻辑，和任务卡确认边界一致

### B1-3 风控规则频次

结果：通过

程序化校验输出：

```json
{"window_minutes":1,"max_count":3}
```

兼容性说明：

- 该输出仍是顶层扁平 JSON
- 现有 `thresholdSummary()` 继续可输出：

```text
window_minutes=1, max_count=3
```

### B1-4 风控规则金额

结果：通过

程序化校验输出：

```json
{"window_minutes":10,"threshold_amount":10000}
```

兼容性说明：

- 仍是顶层扁平 JSON
- 重开时可反解回“金额模式”

### B1-5 消息模板变量插入

结果：通过

程序化校验输出：

可用变量（`points_change`）：

```json
[
  { "key": "userName", "label": "用户姓名" },
  { "key": "pointsChange", "label": "变动积分" },
  { "key": "pointsBalance", "label": "积分余额" },
  { "key": "changeReason", "label": "变动原因" }
]
```

示例渲染结果：

```text
您好，王家长，本次积分变动 +20，当前余额 560。
```

### B1-6 数据兼容

结果：通过

#### 1. fixture `template_id=8801`

本地数据库查询结果：

```json
{
  "template_id": 8801,
  "template_name": "测试_F8_标准报名模板",
  "schema_json": {
    "fields": [
      { "key": "name", "type": "text", "label": "姓名", "required": true },
      { "key": "gender", "type": "radio", "label": "性别", "required": true },
      { "key": "age", "type": "age", "label": "年龄", "required": true },
      { "key": "school", "type": "text", "label": "学校", "required": true },
      { "key": "workFile", "type": "file", "label": "作品文件", "required": true }
    ]
  }
}
```

组件判定结果：

```json
{
  "parseError": "",
  "fallbackReason": "",
  "sourceFormat": "object"
}
```

结论：

- `age / file` 会直接进入视觉模式，不会崩

#### 2. 手造 `group` schema

raw 文本证据：

```json
{
  "fields": [
    {
      "key": "baseGroup",
      "type": "group",
      "label": "基础信息",
      "required": false,
      "fields": [
        {
          "key": "studentName",
          "type": "text",
          "label": "学生姓名",
          "required": true
        }
      ]
    }
  ]
}
```

组件判定结果：

```json
{
  "parseError": "",
  "fallbackReason": "检测到 group 嵌套结构，已自动切换为高级 raw 模式并保留原文。",
  "sourceFormat": "object"
}
```

结论：

- 含 `group` 的老数据会自动 raw fallback
- 原始 JSON 文本不会丢

## 6. 构建结果

执行命令：

```bash
cd RuoYi-Vue/ruoyi-ui
npm run build:prod
```

结果：

- 通过
- 仅有原项目既有的 bundle size warning，无新的编译错误

## 7. 风险与偏差说明

### 7.1 `phone / idcard` 与后端白名单存在真实差异

实现时确认到：

- 小程序动态表单组件有 `phone / idcard`
- 但当前后端 `FormTemplateServiceImpl` 的 `ALLOWED_FIELD_TYPES` 不包含这两项

本卡采取的安全策略：

- 字典保留这两个值，避免上下游语义断裂
- 可视化新增/编辑里将其标记为不建议使用
- 若 raw 中出现这两类字段，组件会提示这是后端当前不支持的类型，避免误保存

这不是前端单卡可以彻底消除的问题，如需完全放开，需要后续单独补后端白名单。

## 8. 后续建议

当前 `sys_menu` 中仍存在两个“表单模板”入口并存的问题：

- 9842：polished 页 `views/jst/form-template/index.vue`
- 9883：旧代码生成页 `views/jst/event/jst_enroll_form_template/index.vue`

本卡没有隐藏 9883，也没有改菜单，只在这里提示：

- 运营侧会看到两个“表单模板”入口
- 建议后续单独开卡决定：
  - 隐藏 9883
  - 或把 9883 重定向到 9842
  - 或明确两个入口的职责分工
