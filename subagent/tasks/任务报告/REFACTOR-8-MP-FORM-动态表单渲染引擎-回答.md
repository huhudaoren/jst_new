# 任务报告 - REFACTOR-8-MP-FORM 动态表单渲染引擎

## A. 任务前自检（Step 2 答题）

1. **对应原型**: 无专属原型，复用报名页 `小程序原型图/enroll.png` / `enroll.html`
2. **调用接口**:
   - `GET /jst/wx/enroll/template` (api/enroll.js `getEnrollTemplate`) — 获取 schema
   - `POST /common/upload` (api/upload.js `uploadFile`) — 文件上传
   - `GET /jst/wx/enroll/{id}` (api/enroll.js `getEnrollDetail`) — 报名详情 readonly
3. **复用 store/api**: `api/enroll.js`, `api/request.js`（getToken/BASE_URL）
4. **新建文件**:
   - `jst-uniapp/components/jst-dynamic-form/jst-dynamic-form.vue`
   - `jst-uniapp/api/upload.js`
5. **数据流**: 页面 onLoad → getEnrollTemplate → schema.fields 传入组件 → 用户填写 → blur 实时校验 → @input(key, value) 回传 → submit 时 validateForm() 全量校验
6. **双视角**: 否，所有用户统一
7. **复用样板**: `jst-form-render` 的字段结构 + `api/channel.js` 的 `uni.uploadFile` 模式
8. **核心 Token**: `$jst-border`(输入框边框), `$jst-danger`/`$jst-danger-light`(错误态), `$jst-brand`/`$jst-brand-light`(选中态/上传按钮), `$jst-bg-card`/`$jst-bg-grey`(背景), `$jst-radius-xl`(圆角)

## B. 交付物清单

**新增文件**:
- `jst-uniapp/api/upload.js` — 通用文件上传 API（封装 `POST /common/upload`，带进度回调）
- `jst-uniapp/components/jst-dynamic-form/jst-dynamic-form.vue` — 动态表单渲染引擎

**修改文件**:
- `jst-uniapp/pages-sub/contest/enroll.vue` — 替换 jst-form-render → jst-dynamic-form，增加 showValidation 控制
- `jst-uniapp/pages-sub/my/enroll-detail.vue` — 替换 jst-form-render → jst-dynamic-form(readonly)

## C. 功能清单

### 支持的字段类型（11 种）

| # | type | 渲染 | 校验 |
|---|------|------|------|
| 1 | `text` | input text | required + maxlength |
| 2 | `textarea` | textarea + 字数计数 | required + maxlength |
| 3 | `phone` | input number, maxlength=11 | `/^1[3-9]\d{9}$/` |
| 4 | `idcard` | input idcard, maxlength=18 | 18位 + 校验码算法 |
| 5 | `age` | input number | 1-99 整数 |
| 6 | `number` | input number | required |
| 7 | `radio` | 单选按钮组 (带 dot 指示器) | required |
| 8 | `checkbox` | 多选按钮组 (带 ✓ 图标) | required (至少选1) |
| 9 | `select` | picker selector | required |
| 10 | `date` | picker date | required |
| 11 | `file`/`image`/`video`/`audio` | 真实上传 + 缩略图/图标 | required (检查文件数) |

### 校验能力

- **blur 实时校验**: 字段失焦时立即校验并展示红框 + 错误文案
- **submit 全量校验**: `validateForm()` 返回错误数组，同时标记所有字段为已触碰
- **showValidation prop**: 父组件可强制展示所有校验错误
- 错误 UI: 输入框红色边框 + 浅红背景 + 字段下方红色错误文案

### 文件上传能力

- **图片**: `uni.chooseImage` → 压缩 → 上传 `/common/upload`
- **视频**: `uni.chooseVideo` → 上传
- **文件(小程序)**: `uni.chooseMessageFile` (微信端从聊天文件选择)
- **文件(非小程序)**: fallback 到 `uni.chooseImage`
- **进度条**: `task.onProgressUpdate` → 百分比 + 进度条 UI
- **maxCount**: 限制文件数量，到达上限隐藏添加按钮
- **预览**: 图片→`uni.previewImage` 多图预览，视频→`uni.previewMedia`，PDF→下载后 `uni.openDocument`
- **删除**: 单个文件可删除（× 按钮）

### readonly 模式

- 文件类字段展示缩略图网格（图片直接显示，其他文件显示图标）
- 文本类字段显示"未填写"兜底
- 点击文件可预览

## D. 视觉一致性

- ✅ 所有样式使用 design-tokens.scss 变量，零硬编码色值
- ✅ 单位全部使用 rpx
- ✅ BEM 命名规范: `.jst-dynamic-form__field`
- ✅ DOM 标签全部使用 view/text/image，无 web 标签残留
- ✅ Flexbox 布局，安全区处理由父页面负责

## E. 遗留 TODO

- `jst-form-render` 组件仍保留在 `components/` 目录（其他页面可能引用，未做全局扫描清除）
- 视频预览在非微信端 fallback 到 navigateTo 视频播放页（该页面需存在）
- 上传大文件（>10MB）的体验未优化（后端有文件大小限制）

## F. 我做了任务卡之外的什么

- 无额外修改

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js 或 api/upload.js
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码
- [x] 文件编码 UTF-8 无 BOM，无中文全角引号
- [x] 无 ES6 模板字符串在 template 属性中
