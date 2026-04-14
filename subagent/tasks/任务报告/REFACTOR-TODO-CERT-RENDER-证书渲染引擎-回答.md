# 任务报告 - REFACTOR-TODO-CERT-RENDER 证书客户端渲染引擎（MiniProgram 部分）

> 完成时间：2026-04-14
> Agent：MiniProgram Agent
> 任务范围：仅 §四 MiniProgram 部分（不改后端代码）

---

## A. 任务前自检（Step 2 答题）

1. **对应原型**: 无独立原型（复用 cert-detail 页面，此任务是渲染引擎替换）
2. **调用接口**:
   - `GET /jst/wx/cert/${id}` — 证书详情（27 文档 §3.8），需后端增返 layoutJson/variables
3. **复用 store/api**: `api/cert.js` 的 `getCertDetail`（已有，无需修改）
4. **新建文件**:
   - `jst-uniapp/utils/cert-renderer.js`
5. **修改文件**:
   - `jst-uniapp/pages-sub/my/cert-detail.vue`
6. **数据流**:
   - onLoad → getCertDetail(id) → 若 layoutJson 存在 → JSON.parse → renderCert(ctx, layoutData, variables) → Canvas 2D 绘制 → 显示
   - 保存相册 → canvasToTempFilePath → saveImageToPhotosAlbum
7. **双视角**: 否（所有用户看到自己的证书）
8. **复用样板**: cert-detail.vue 现有代码结构（保留海报/分享/验证全部功能）
9. **核心 Token**: `$jst-bg-subtle` 页面底色, `$jst-bg-card` Canvas 容器背景, `$jst-radius-card` 圆角, `$jst-text-secondary` loading 文字色, `$jst-font-sm` 提示字号

---

## B. 交付物清单

### 新增文件

| 文件 | 说明 | 行数 |
|---|---|---|
| `jst-uniapp/utils/cert-renderer.js` | 证书渲染核心引擎 | ~260 行 |

### 修改文件

| 文件 | 改动说明 |
|---|---|
| `jst-uniapp/pages-sub/my/cert-detail.vue` | +import renderCert; +Canvas 2D 模板; +5 个 data 属性; +4 个 methods; +Canvas 样式 |

### 未修改文件

| 文件 | 原因 |
|---|---|
| `jst-uniapp/api/cert.js` | 接口函数 `getCertDetail` 已存在，无需改动 |
| `jst-uniapp/pages.json` | cert-detail 路由已存在，无需新增路由 |
| `RuoYi-Vue/**` | 按任务卡要求不改后端代码 |

---

## C. 渲染引擎技术实现

### cert-renderer.js 模块结构

```
导出: renderCert, FONT_MAP, CERT_WIDTH(794), CERT_HEIGHT(1123)

内部函数:
├── getCenterPoint(obj)        — Fabric.js origin 坐标系转中心点
├── resolveText(text, obj, variables)  — 变量替换（certVarKey 优先 / {{xxx}} fallback）
├── buildFont(obj)             — CSS font 字符串拼接
├── loadImage(canvasNode, src) — 跨平台图片加载（data URL / HTTP / 本地）
├── wrapText(ctx, text, maxWidth) — Textbox 自动换行
├── roundRectPath(ctx, ...)    — 圆角矩形路径
├── drawText(ctx, obj, vars)   — text/i-text 绘制（支持 \n 多行）
├── drawTextbox(ctx, obj, vars) — textbox 绘制（自动换行）
├── drawRect(ctx, obj)         — 矩形绘制（支持 rx 圆角 + fill/stroke）
├── drawLine(ctx, obj)         — 线条绘制（复现 Fabric.js calcLinePoints）
├── drawGroup(ctx, obj, vars, canvas) — Group 绘制（QR 码占位符特殊处理）
├── drawGroupChildren(ctx, ...)       — Group 子元素递归绘制
├── applyShadow(ctx, shadow)   — 阴影属性应用
└── renderObject(ctx, obj, vars, canvas) — 单对象渲染分发
```

### 关键技术决策

| 决策点 | 方案 | 原因 |
|---|---|---|
| **坐标系** | getCenterPoint() 计算中心，以 (0,0) 为中心绘制 | Fabric.js 内部 _render 以中心为原点，需复现此行为 |
| **变量替换** | certVarKey 优先 → {{xxx}} fallback | 与管理端设计器双模式一致（CanvasArea.vue L124-128） |
| **图片加载** | canvas.createImage() + downloadFile | 小程序 Canvas 2D API 要求，data URL 直接加载 |
| **Line 绘制** | 复现 calcLinePoints（xMult/yMult） | 与 Fabric.js 源码一致（fabric.Line._render 内部逻辑） |
| **串行绘制** | Promise 链式 | 图片元素异步加载，必须等前一个完成再绘制下一个以保持图层顺序 |
| **DPR 处理** | canvas.width = 794 * dpr, ctx.scale(dpr, dpr) | 高清屏渲染清晰，Fabric.js 坐标直接适用 |
| **Canvas API** | type="2d"（新 API） | 旧 API 不支持 createImage，且新 API 性能更好 |
| **字体** | FONT_MAP 静态映射 | SimHei→sans-serif 等，小程序无系统字体，用 fallback |

### cert-detail.vue 集成逻辑

```
证书预览显示优先级:
  1. certImageUrl 存在 → <image> 直接显示（原有行为，不变）
  2. layoutJson 存在 → <canvas type="2d"> 渲染（本次新增）
  3. 都不存在 → mock 模拟证书样式（原有行为，不变）

保存到相册优先级:
  1. certImageUrl → downloadAndSave（原有）
  2. canvasNode 已渲染 → saveCertCanvasToAlbum（本次新增）
  3. 都不可用 → generateAndSavePoster（原有海报生成）
```

新增方法:

| 方法 | 功能 |
|---|---|
| `initCanvasSize()` | 根据屏幕宽度计算 Canvas 显示尺寸（保持 794:1123 比例） |
| `renderCertCanvas()` | 获取 Canvas 2D 节点 → 解析 layoutJson → 调用 renderCert |
| `previewCertCanvas()` | 导出 Canvas 为临时图片 → uni.previewImage 全屏预览 |
| `saveCertCanvasToAlbum()` | 导出 Canvas → saveImageToPhotosAlbum |

---

## D. 联调测试预期结果

| 场景 | 预期 | 状态 |
|---|---|---|
| 接口返回 layoutJson + variables | Canvas 渲染真实证书，替代 mock 样式 | ⏳ 待后端接口增返字段 |
| 接口返回 certImageUrl | 直接显示图片（行为不变） | ✅ 逻辑不变 |
| 接口无 layoutJson 无 certImageUrl | 显示 mock 模拟证书 | ✅ 逻辑不变 |
| 点击 Canvas 证书 | 导出临时图片 → 全屏预览 | ⏳ 待后端 |
| 保存到相册（Canvas） | canvasToTempFilePath → saveImageToPhotosAlbum | ⏳ 待后端 |
| 生成分享海报 | 使用原 certPoster 海报 Canvas（不变） | ✅ 逻辑不变 |
| 分享给好友 | 原有 onShareAppMessage 不变 | ✅ |
| 验证证书 | 跳转 cert-verify 页面不变 | ✅ |

---

## E. 遗留问题与依赖

### 🔴 阻塞项：后端接口未增返 layoutJson + variables

**现状**: `GET /jst/wx/cert/{id}` 目前返回证书基本信息（certNo, holderName, contestName, awardLevel 等），**不返回** layoutJson 和 variables 字段。

**需要后端 Agent 完成**:

1. **CertDetailVO / WxCertResVO** 增加字段:
   - `layoutJson` (String) — 从 cert_template 关联查出的 Fabric.js JSON
   - `variables` (Map<String, String>) — 已替换好的变量 Map
   - `bgImage` (String, 可选) — 底图 URL

2. **WxCertController / CertService** 改动:
   - 查 `jst_cert_record` → 关联查 `jst_cert_template` 的 `layout_json` 字段
   - 组装 variables Map: 从 cert_record + participant + contest 拼接 {name, contestName, awardName, score, certNo, date, school, groupLevel}

3. **参考任务卡 §三**:
   ```json
   {
     "certNo": "JST-CERT-20260414-0001",
     "layoutJson": "{...Fabric.js JSON...}",
     "variables": {
       "name": "张小明",
       "contestName": "2026全国青少年美术大赛",
       "awardName": "一等奖",
       "certNo": "JST-CERT-20260414-0001",
       "date": "2026年4月14日"
     }
   }
   ```

**前端已就绪**: cert-renderer.js 和 cert-detail.vue 集成代码已完成，后端返回字段后即可渲染。在后端未返回 layoutJson 之前，页面自动 fallback 到原有 mock 证书样式（无破坏性）。

### 🟡 非阻塞项

| 项 | 优先级 | 说明 |
|---|---|---|
| **字体精确匹配** | P2 | 当前用 FONT_MAP fallback（SimHei→sans-serif），若需精确字体可通过 `uni.loadFontFace` 加载云端字体文件 |
| **一致性验证** | P1 | 管理端 Fabric.js 预览 vs 小程序 Canvas 渲染需真机截图对比，可能需微调坐标偏移 |
| **Canvas 2D 兼容性** | P1 | Canvas 2D API 需基础库 2.9.0+，建议 app.json 中 `"SDKVersion": "2.9.0"` 最低版本限制 |
| **textbox 换行精度** | P3 | 当前按字符逐个换行（wrapText），Fabric.js 对英文按单词换行，中英混排可能有微小差异 |
| **Group 内复杂嵌套** | P3 | 当前只处理 QR 码 Group 的一层子元素，如有多层嵌套 Group 需扩展 |

---

## F. 我做了任务卡之外的什么

- 无。严格按 §四 MiniProgram 部分实现，未修改任何后端代码或架构文档。

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/cert.js（getCertDetail）
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新 npm 依赖
- [x] 没有改 RuoYi-Vue 任何文件
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/canvas（无 web 标签残留）
- [x] 样式全部使用 design-tokens.scss 变量，未硬编码
- [x] 文件 UTF-8 无 BOM，无中文全角引号，无 Unicode 转义
- [x] 不使用 ES6 模板字符串在 template 属性中
