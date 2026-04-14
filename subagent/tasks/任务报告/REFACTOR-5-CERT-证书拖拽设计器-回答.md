# REFACTOR-5-CERT 证书拖拽设计器 — 交付报告

> Agent: Web Admin Agent | 完成时间: 2026-04-14

---

## 一、DoD 完成情况

| DoD 项 | 状态 | 说明 |
|---|---|---|
| Fabric.js 集成到项目 | ✅ | `fabric@5` 已安装到 dependencies |
| CertDesigner 组件开发（三栏布局） | ✅ | 4 个组件文件，左/中/右三栏 |
| 元素添加/拖拽/编辑/删除 | ✅ | 从左侧面板拖入画布、选中编辑、Delete 键删除 |
| 动态变量系统（9+ 变量） | ✅ | 赛事 9 个 + 渠道 4 个，按 ownerType 切换 |
| 底图上传 | ✅ | 工具栏「底图」按钮上传 + props bgImage URL 加载 |
| 预览（示例数据替换） | ✅ | 预览按钮 → Dialog，StaticCanvas 渲染替换后效果 |
| 保存/加载（toJSON / loadFromJSON） | ✅ | 含 certVarKey/certVarLabel 自定义属性序列化 |
| cert-manage.vue 集成设计器 | ✅ | 新增/编辑模板均打开设计器 Drawer（95% 宽度） |
| contest-edit.vue 快速新建模板 Drawer 集成 | ✅ | 旧表单 Drawer 替换为设计器 Drawer |
| 渠道授权书复用（ownerType='channel'） | ✅ | 组件 props 支持，变量自动切换 |
| 后端证书生成接口 | ⏭️ | 属于 Backend Agent 范畴，本卡不含 |
| `npm run build:prod` 通过 | ✅ | 构建零错误 |

---

## 二、新增文件

| 文件 | 说明 |
|---|---|
| `src/views/partner/components/CertDesigner/index.vue` | 设计器主容器（三栏布局协调 + 预览 Dialog） |
| `src/views/partner/components/CertDesigner/ElementPanel.vue` | 左侧元素面板（静态文本/动态变量/图片/矩形/线条） |
| `src/views/partner/components/CertDesigner/PropertyPanel.vue` | 右侧属性面板（位置/旋转/字体/颜色/对齐/图层操作） |
| `src/views/partner/components/CertDesigner/CanvasArea.vue` | 中间画布区域（Fabric.js Canvas、底图、缩放、撤销/重做） |

---

## 三、修改文件

| 文件 | 改动摘要 |
|---|---|
| `src/api/partner/cert.js` | 新增 `getCertTemplate(id)` / `updateCertTemplate(id, data)` |
| `src/views/partner/cert-manage.vue` | 新增模板库表格 + 设计器 Drawer（替换旧 textarea dialog） |
| `src/views/partner/contest-edit.vue` | 快速新建模板 Drawer 替换为设计器 + import CertDesigner |
| `package.json` | 新增 `fabric@5` 依赖 |

---

## 四、设计要点

### 4.1 组件接口

```js
// CertDesigner props
{
  layoutJson: String|Object,  // 已有布局 JSON（编辑时传入）
  bgImage: String,            // 底图 URL
  ownerType: String,          // 'contest' | 'channel'（控制动态变量集）
  title: String               // 设计器标题
}

// CertDesigner events
@save -> { layoutJson: String, thumbnail: String(dataURL) }
```

### 4.2 动态变量

**赛事证书**（ownerType='contest'）：
`{{name}}` `{{contestName}}` `{{awardName}}` `{{score}}` `{{certNo}}` `{{date}}` `{{qrcode}}` `{{school}}` `{{groupLevel}}`

**渠道授权书**（ownerType='channel'）：
`{{channelName}}` `{{authNo}}` `{{authDate}}` `{{scope}}`

### 4.3 序列化

- 保存：`canvas.toJSON(['certVarKey', 'certVarLabel'])` → 存入 `layout_json`
- 加载：`canvas.loadFromJSON(json)` → 还原画布
- 缩略图：`canvas.toDataURL({quality: 0.5})` → 可存为模板封面

### 4.4 功能亮点

- **撤销/重做**：Ctrl+Z / Ctrl+Shift+Z，最多 30 步状态栈
- **缩放**：工具栏 +/- 按钮 + 「适应」一键缩放到容器
- **键盘快捷键**：Delete/Backspace 删除选中元素（编辑文本时不触发）
- **响应式**：手机端三栏改为垂直折叠布局
- **关闭确认**：Drawer 关闭前弹窗确认，防止误操作丢失设计
- **预览**：示例数据自动替换所有动态变量，StaticCanvas 渲染最终效果

---

## 五、验证

- [x] `npm run build:prod` — 零错误通过
- [x] cert-manage.vue 新增模板 → 打开设计器 Drawer
- [x] cert-manage.vue 编辑模板 → 加载已有 layoutJson
- [x] contest-edit.vue Tab D 新建模板 → 打开设计器 Drawer
- [x] 响应式：三栏在窄屏下垂直折叠

---

## 六、遗留事项

| 项 | 说明 |
|---|---|
| 后端证书生成接口 | `POST /jst/partner/cert/generate`，需 Backend Agent 实现（读取 layout_json + 数据 → 渲染 PDF/PNG） |
| 对齐辅助线 | Fabric.js guideline 插件可后续集成，当前版本依赖手动对齐 |
| OSS 图片上传 | 底图/印章目前为本地 FileReader → dataURL，待 OSS 配置后接入真实上传 |
