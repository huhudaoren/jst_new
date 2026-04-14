# REFACTOR-5-CERT — 证书拖拽设计器 (Fabric.js)

> 优先级：P1 | 预估：XL | Agent：Web Admin Agent
> 依赖：REFACTOR-1-DDL、REFACTOR-6-BIZ-NO

---

## 一、背景

当前证书模板的 `layout_json` 只能通过 textarea 手写 JSON，用户无法使用。需要基于 Fabric.js 构建一套"傻瓜式"拖拽证书设计器。

## 二、技术选型

**Fabric.js v5**（Canvas 2D 库）
- NPM: `npm install fabric@5`
- 优势：成熟稳定、API 丰富、支持 JSON 序列化/反序列化、支持文本/图片/形状
- 输出：`canvas.toJSON()` → 存入 `layout_json`；`canvas.toDataURL()` → 预览图

## 三、功能设计

### 3.1 设计器布局（三栏）

```
┌──────────────┬─────────────────────────┬──────────────┐
│  元素面板     │      画布区域            │  属性面板     │
│  (左侧 200px) │  (中间，证书尺寸 A4)     │ (右侧 280px) │
│              │                         │              │
│ [静态文本]    │  ┌───────────────────┐   │ 字体: ___    │
│ [动态变量]    │  │ 底图              │   │ 大小: ___    │
│  {姓名}      │  │                   │   │ 颜色: ___    │
│  {赛事名称}   │  │   {姓名}          │   │ 对齐: ___    │
│  {奖项}      │  │       {赛事名称}   │   │ 旋转: ___    │
│  {成绩}      │  │                   │   │ 位置: x,y    │
│  {证书编号}   │  │   {证书编号}       │   │              │
│  {日期}      │  │              印章 →│   │ [删除元素]    │
│  {二维码}    │  └───────────────────┘   │ [图层↑↓]     │
│ [图片元素]    │                         │              │
│ [形状/线条]   │  缩放: [- 100% +]       │              │
└──────────────┴─────────────────────────┴──────────────┘
```

### 3.2 元素类型

| 类型 | 说明 | Fabric 对象 |
|---|---|---|
| 静态文本 | 自由文本，用户手动输入 | `fabric.Text` |
| 动态变量 | 系统占位符，生成时替换 | `fabric.Text` + `data-var` 属性 |
| 图片 | 上传印章/Logo/装饰 | `fabric.Image` |
| 矩形/线条 | 装饰边框 | `fabric.Rect` / `fabric.Line` |

**动态变量清单**：
- `{{name}}` — 获奖者姓名
- `{{contestName}}` — 赛事名称
- `{{awardName}}` — 奖项名称
- `{{score}}` — 总成绩
- `{{certNo}}` — 证书编号
- `{{date}}` — 颁发日期
- `{{qrcode}}` — 验证二维码（图片占位）
- `{{school}}` — 学校名称
- `{{groupLevel}}` — 参赛组别

### 3.3 操作功能

- **底图上传**：上传背景图 → `canvas.setBackgroundImage()`
- **拖拽添加**：从左侧面板拖拽元素到画布
- **选中编辑**：点击元素 → 右侧属性面板展示属性
- **对齐辅助**：自动吸附对齐线（Fabric.js guideline 插件）
- **图层管理**：上移/下移/置顶/置底
- **撤销/重做**：基于 canvas 状态栈
- **缩放/平移**：鼠标滚轮缩放，空格+拖拽平移

### 3.4 预览与保存

- **实时预览**：右上角「预览」按钮，弹出对话框，用示例数据替换所有动态变量，渲染最终效果
- **保存**：`canvas.toJSON()` → 调用 API 保存到 `layout_json`
- **缩略图**：`canvas.toDataURL({quality: 0.5})` → 保存为模板封面

### 3.5 证书生成接口（Backend）

新增 `POST /jst/partner/cert/generate`：
- 入参：`{contestId, templateId, enrollIds[]}`
- 后端读取 `layout_json` + 各报名记录数据
- 服务端渲染 PDF（Java 用 `itext` 或 `openpdf`）或 PNG（Java AWT Graphics2D）
- 生成后关联 `jst_cert_record`，状态 = draft

### 3.6 组件封装

- `components/CertDesigner/index.vue` — 设计器主组件
- `components/CertDesigner/ElementPanel.vue` — 左侧元素面板
- `components/CertDesigner/PropertyPanel.vue` — 右侧属性面板
- `components/CertDesigner/CanvasArea.vue` — 中间画布区域

设计器作为独立组件，可在以下位置复用：
1. cert-manage.vue（证书管理页的模板编辑）
2. contest-edit.vue（快速新建证书模板 Drawer）
3. 渠道授权书编辑（channel-auth 模块）

## 四、渠道授权书复用

渠道授权证书与赛事证书共享同一设计器，区别：
- 动态变量不同：`{{channelName}}` `{{authNo}}` `{{authDate}}` `{{scope}}`
- 底图不同
- 通过 `ownerType: 'channel'` 区分

## 五、DoD

- [ ] Fabric.js 集成到项目
- [ ] CertDesigner 组件开发（三栏布局）
- [ ] 元素添加/拖拽/编辑/删除
- [ ] 动态变量系统（9+ 变量）
- [ ] 底图上传
- [ ] 预览（示例数据替换）
- [ ] 保存/加载（toJSON / loadFromJSON）
- [ ] cert-manage.vue 集成设计器
- [ ] contest-edit.vue 快速新建模板 Drawer 集成
- [ ] 后端证书生成接口
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
