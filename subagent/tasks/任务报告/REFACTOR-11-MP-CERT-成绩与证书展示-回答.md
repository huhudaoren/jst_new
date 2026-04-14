# 任务报告 - REFACTOR-11-MP-CERT 成绩与证书展示

> Agent: MiniProgram Agent | 日期: 2026-04-14

## A. 任务前自检（Step 2 答题）

1. **对应原型**: 小程序原型图/my-score.html / my-score.png, my-cert.html / my-cert.png
2. **调用接口**:
   - `GET /jst/wx/score/my` (api/score.js, 已有)
   - `GET /jst/wx/score/${id}` (api/score.js `getScoreDetail`)
   - `GET /jst/wx/cert/my` (api/cert.js, 已有)
   - `GET /jst/wx/cert/${id}` (api/cert.js `getCertDetail`)
   - `GET /jst/public/cert/verify` (api/cert.js, 已有)
3. **复用 store/api**: `api/score.js`, `api/cert.js`, `useUserStore`
4. **新建文件**: `pages-sub/my/score-detail.vue`, `pages-sub/my/cert-detail.vue`
5. **数据流**: onLoad(id) → getScoreDetail/getCertDetail → 渲染 → Canvas海报 → saveImageToPhotosAlbum
6. **双视角**: 否
7. **复用样板**: score.vue / cert.vue 的页面结构和样式
8. **核心 Token**: `$jst-brand`, `$jst-bg-subtle`, `$jst-bg-card`, `$jst-shadow-ring/$jst-shadow-ambient/$jst-shadow-lift`, `$jst-radius-card`, `$jst-hero-gradient`, `$jst-gold-light`

## B. 交付物清单

### 新增文件

| 文件 | 说明 |
|------|------|
| `jst-uniapp/pages-sub/my/score-detail.vue` | 成绩详情页（雷达图+明细表+总分+获奖+排名） |
| `jst-uniapp/pages-sub/my/cert-detail.vue` | 证书详情页（全图预览+Canvas海报+保存相册+分享） |

### 修改文件

| 文件 | 改动 |
|------|------|
| `jst-uniapp/pages.json` | 追加 `score-detail` 和 `cert-detail` 两条路由 |
| `jst-uniapp/pages-sub/my/score.vue` | `openDetail` 跳转 score-detail; `viewCert` 跳转 cert-detail |
| `jst-uniapp/pages-sub/my/cert.vue` | `viewCert`/`downloadCert` 跳转 cert-detail（替换 toast 占位） |
| `jst-uniapp/pages-sub/my/enroll-detail.vue` | 新增成绩/证书入口区块 + `goScoreDetail`/`goCertDetail` 方法 |
| `jst-uniapp/pages-sub/contest/detail.vue` | 新增"成绩查询"入口卡片 + `goScoreQuery` 方法 + 对应样式 |

## C. 功能实现详情

### C1. 成绩详情页 (`score-detail.vue`)

- **Hero 总分区**: 深蓝渐变背景 + 96rpx 大字总分 + 排名信息
- **获奖信息卡**: 金色底卡，显示奖项等级 + 证书入口按钮
- **雷达图**: 复用 `jst-score-radar` 组件，dimensions >= 3 时 Canvas 渲染
- **明细表**: 四列表格（项目/得分/满分/权重），交替行背景
- **参赛信息**: 参赛人/组别/报名编号/发布时间

### C2. 证书详情页 (`cert-detail.vue`)

- **全图预览**: 有 `certImageUrl` 时 image 展示（点击 previewImage）；无图时模拟证书样式
- **证书信息卡**: 编号/获奖人/赛事/等级/组别/机构/日期
- **操作按钮 2x2 Grid**:
  - 保存到相册: downloadFile → saveImageToPhotosAlbum（含权限拒绝处理 + openSetting 引导）
  - 生成分享海报: Canvas 2D 绘制（品牌渐变条+证书缩略+恭喜语+编号+底部品牌）→ previewImage
  - 验证证书: 跳转 cert-verify 公开页
  - 分享给好友: button open-type="share" + onShareAppMessage

### C3. Canvas 海报布局

```
┌─────────────────────────────┐
│  [品牌渐变条] 竞赛通          │
│  一站式竞赛服务平台            │
├─────────────────────────────┤
│  ┌────────────────────┐     │
│  │  获 奖 证 书         │     │
│  │  {姓名}              │     │
│  │  在{赛事}中荣获{奖项} │     │
│  └────────────────────┘     │
│                              │
│  恭喜 {姓名} 同学             │
│  荣获 {赛事名称} {奖项}       │
│  证书编号：JST-CERT-xxx      │
│  颁发日期：2026-04-14        │
│                              │
│  扫描小程序码查看完整证书      │
│  — 竞赛通 · 一站式竞赛平台 — │
└─────────────────────────────┘
```

### C4. 入口浅化

| 入口位置 | 实现方式 |
|----------|----------|
| 我的页 | ✅ 已有：Summary 4格 + "我的服务"栏，无需改动 |
| 赛事详情页 | ✅ 新增：`scorePublished` 时显示金色"成绩查询"入口卡 |
| 报名详情页 | ✅ 新增：`scorePublished` 时显示成绩/证书区块 |
| 成绩列表页 | ✅ 修复：`openDetail` 跳转 score-detail |
| 证书列表页 | ✅ 修复：`viewCert`/`downloadCert` 跳转 cert-detail |

## D. 视觉对比

- ✅ 样式全部使用 `design-tokens.scss` 变量
- ✅ Canvas 海报颜色与项目品牌色一致（Hero gradient `#1A3A6E → #2B6CFF`）
- ✅ DOM 标签已转为 view/text/image
- ✅ BEM 命名规范（`score-detail-page__xxx` / `cert-detail-page__xxx`）
- ⚠️ Canvas API 需要硬编码颜色值（JS 运行时无法读取 SCSS 变量，与 jst-score-radar 一致）

## E. 遗留 TODO

| 项 | 说明 | 阻塞方 |
|----|------|--------|
| 小程序码 | 海报中的小程序码需后端 wxacode 接口生成，当前用文字提示代替 | Backend |
| VO 字段 | `scorePublished` / `hasCert` / `totalScore` / `certImageUrl` 等字段依赖后端 VO 返回 | Backend |
| 证书全图 | `certImageUrl` 依赖后端证书渲染引擎（REFACTOR-5-CERT）产出 | Backend |

## F. 我做了任务卡之外的什么

- 无额外改动

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js
- [x] 所有金额/手机号用接口返回的脱敏字段
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码
- [x] pages.json JSON 验证通过
- [x] 文件编码 UTF-8 无 BOM

## H. DoD 对照

- [x] 成绩列表页 — 已有 `score.vue`，修复了 `openDetail` 导航
- [x] 成绩详情页 + 雷达图 — 新建 `score-detail.vue`，复用 `jst-score-radar`
- [x] 证书列表页 — 已有 `cert.vue`，修复了 `viewCert`/`downloadCert`
- [x] 证书详情页（全图预览）— 新建 `cert-detail.vue`
- [x] 一键保存到相册 — `downloadFile` + `saveImageToPhotosAlbum` + 权限处理
- [x] 分享海报生成（Canvas）— `drawPoster` 方法 + `canvasToTempFilePath`
- [x] 证书验证公开页 — 已有 `cert-verify.vue`，cert-detail 正确跳转
- [x] 入口浅化（我的页 + 赛事详情 + 报名详情）— 三处入口均已实现
- [x] HBuilderX 编译检查 — pages.json 合法、无 BOM、无 mock
- [x] 报告交付
