# REFACTOR-8-MP-FORM — 动态表单渲染引擎

> 优先级：P1 | 预估：L | Agent：MiniProgram Agent
> 依赖：REFACTOR-1-DDL（后端表单模板 API 稳定）

---

## 一、背景

管理端支持了动态报名表单配置（jst_enroll_form_template.schema_json），小程序端需要实现一套健壮的动态表单渲染引擎，根据 schema_json 配置精准渲染表单，支持实时校验，让用户填写零门槛。

## 二、schema_json 结构

```json
{
  "version": 1,
  "fields": [
    {"key": "name", "type": "text", "label": "姓名", "required": true, "placeholder": "请输入真实姓名"},
    {"key": "gender", "type": "radio", "label": "性别", "required": true, "options": ["男", "女"]},
    {"key": "age", "type": "age", "label": "年龄", "required": true},
    {"key": "school", "type": "text", "label": "学校", "required": true},
    {"key": "grade", "type": "select", "label": "年级", "required": false, "options": ["一年级","二年级","三年级","四年级","五年级","六年级","初一","初二","初三"]},
    {"key": "phone", "type": "phone", "label": "联系电话", "required": true},
    {"key": "idCard", "type": "idcard", "label": "身份证号", "required": false},
    {"key": "workFile", "type": "file", "label": "作品文件", "required": true, "accept": "image/*,video/*,.pdf", "maxCount": 5},
    {"key": "remark", "type": "textarea", "label": "备注", "required": false, "maxlength": 500}
  ]
}
```

## 三、核心组件

### 3.1 JstDynamicForm 组件

路径：`components/jst-dynamic-form/jst-dynamic-form.vue`

Props：
- `schema` (Array): schema_json.fields 数组
- `value` (Object): 表单数据 v-model
- `readonly` (Boolean): 只读模式（查看报名详情时用）
- `showValidation` (Boolean): 是否展示校验状态

Events：
- `@input(formData)`: 数据变更
- `@validate(isValid)`: 整体校验结果

### 3.2 字段类型映射

| schema type | 渲染组件 | 说明 |
|---|---|---|
| `text` | `u-input` | 单行文本 |
| `textarea` | `u-textarea` | 多行文本 |
| `phone` | `u-input type="number"` + 手机号正则校验 | 11位手机号 |
| `idcard` | `u-input` + 身份证正则校验 | 18位身份证 |
| `age` | `u-number-box` 或 `u-input type="number"` | 1-99 范围 |
| `radio` | `u-radio-group` + `u-radio` | 单选 |
| `checkbox` | `u-checkbox-group` + `u-checkbox` | 多选 |
| `select` | `picker mode="selector"` | 下拉选择 |
| `date` | `u-datetime-picker` | 日期 |
| `file` | 自定义上传组件 | 图片/视频/文件上传 |

### 3.3 实时校验

**校验时机**：字段 blur 时 + 表单提交前

**校验规则**：
- `required`: 非空检查（文件类型检查数组长度）
- `phone`: `/^1[3-9]\d{9}$/`
- `idcard`: 18位 + 最后一位校验码
- `age`: 1-99 整数
- `maxlength`: 文本长度限制
- 自定义 `validators`（schema 中可扩展）

**校验 UI**：
- 未填必填项：字段下方红色提示 + 字段边框变红
- 格式错误：具体提示（如"请输入正确的手机号"）
- **实时反馈**，不等到提交才报错

### 3.4 文件上传

- 图片：`uni.chooseImage` → 压缩 → 上传 OSS/后端
- 视频：`uni.chooseVideo` → 上传
- 文件：`uni.chooseFile`（仅 H5 支持，小程序用 chooseMessageFile）
- 上传进度条展示
- 多文件支持（maxCount 限制）
- 预览：图片点击放大、视频点击播放

## 四、页面集成

### 4.1 报名页（pages-sub/contest/enroll.vue）

```vue
<jst-dynamic-form
  :schema="formTemplate.fields"
  v-model="formData"
  :show-validation="submitted"
  @validate="onValidate"
/>
```

流程：
1. 进入报名页 → 获取赛事详情（含 formTemplateId）
2. 获取表单模板 schema
3. 渲染动态表单
4. 用户填写 → 实时校验
5. 提交 → 全量校验 → 调用报名接口（传 formData + 文件 URL）

### 4.2 报名详情页（pages-sub/my/enroll-detail.vue）

同一组件 readonly 模式展示已提交的表单快照：

```vue
<jst-dynamic-form
  :schema="enrollDetail.formSnapshot.fields"
  :value="enrollDetail.formSnapshot.formData"
  :readonly="true"
/>
```

## 五、DoD

- [ ] jst-dynamic-form 组件开发
- [ ] 10+ 字段类型渲染
- [ ] 实时校验（blur + submit）
- [ ] 文件上传（图片/视频/文件）
- [ ] 报名页集成
- [ ] 报名详情页只读集成
- [ ] HBuilderX 编译通过
- [ ] 报告交付
