# REFACTOR-11-MP-CERT — 成绩与证书的一键获取

> 优先级：P1 | 预估：M | Agent：MiniProgram Agent
> 依赖：REFACTOR-5-CERT（证书生成引擎）、REFACTOR-6-BIZ-NO（证书编号）、REFACTOR-10-MP-VISUAL（视觉组件）

---

## 一、背景

参赛者查询成绩和下载证书的入口要浅，操作要简单。需要实现一键查看成绩、一键保存证书海报到手机相册。

## 二、成绩查询

### 2.1 入口设计

- **我的页**：增加"我的成绩"入口（icon + 未读角标）
- **赛事详情页**：已出成绩的赛事显示"查看成绩"按钮
- **报名详情页**：已出成绩时显示成绩区域

### 2.2 成绩详情页

页面：`pages-sub/my/score-detail.vue`（新增或改造）

**展示内容**：
- 赛事名称 + 参赛组别
- **成绩雷达图**（jst-score-radar 组件）：各成绩项分数可视化
  - 使用 Canvas 绘制雷达图（各维度：技巧/表现力/创意/... 来自 jst_score_item）
  - 每个维度显示：实际分 / 满分
- **成绩明细表**：各成绩项列表（项目名 | 得分 | 满分 | 权重）
- **加权总分**：大字突出显示
- **获奖信息**：如果有奖项 → 显示奖项名称 + 等级 + 证书入口
- **排名**（如后端支持）：第 N 名 / 共 M 人

### 2.3 成绩列表页

页面：`pages-sub/my/score-list.vue`

- 按赛事分组展示所有成绩
- 卡片形式：赛事名称 | 总分 | 奖项 | 日期
- 点击进入详情

## 三、证书展示与下载

### 3.1 证书列表

页面：`pages-sub/my/cert-list.vue`

- 证书大卡列表（jst-cert-card 组件）
- 每张卡片：证书缩略图 + 赛事名称 + 证书编号 + 颁发日期
- 点击进入证书详情

### 3.2 证书详情页

页面：`pages-sub/my/cert-detail.vue`

**展示内容**：
- 证书全图预览（后端已渲染的 PNG/PDF URL）
- 证书信息：编号、赛事、奖项、颁发日期
- 验证二维码（扫码可验证证书真伪）

**操作**：
- 「保存到相册」按钮：直接保存证书图片
- 「生成分享海报」按钮：生成包含证书 + 个人信息的分享海报
- 「分享给好友」按钮：微信分享证书页面

### 3.3 证书海报生成

使用 Canvas 2D 在端侧生成分享海报：

**海报布局**：
```
┌─────────────────────────────┐
│        竞赛通 LOGO           │
│                              │
│   ┌────────────────────┐    │
│   │                    │    │
│   │    证书图片缩略     │    │
│   │                    │    │
│   └────────────────────┘    │
│                              │
│   恭喜 {姓名} 同学           │
│   荣获 {赛事名称}             │
│   {奖项名称}                  │
│                              │
│   证书编号：JST-CERT-xxx     │
│   颁发日期：2026年4月         │
│                              │
│   ┌──────┐                   │
│   │ 小程 │  扫码查看完整证书  │
│   │ 序码 │                   │
│   └──────┘                   │
│                              │
│   — 竞赛通 · 一站式竞赛平台 — │
└─────────────────────────────┘
```

**技术实现**：
- 使用 `uni.createCanvasContext('poster')` 或 Canvas 2D API
- 绘制背景 → 证书图 → 文字 → 二维码 → 品牌信息
- `canvas.toTempFilePath()` → `uni.saveImageToPhotosAlbum()`
- 保存前请求相册权限

### 3.4 证书验证页

页面：`pages-sub/cert/verify.vue`（新增，公开页面无需登录）

- 扫描证书上的二维码进入
- 展示：证书编号、姓名、赛事、奖项、颁发日期、状态（有效/已作废）
- 用于第三方验证证书真伪

## 四、API 对接

```javascript
// api/cert.js 新增
export function getMyScoreList(params) {
  return request({ url: '/jst/wx/score/my-list', method: 'get', params })
}
export function getScoreDetail(enrollId) {
  return request({ url: `/jst/wx/score/detail/${enrollId}`, method: 'get' })
}
export function getMyCertList(params) {
  return request({ url: '/jst/wx/cert/my-list', method: 'get', params })
}
export function getCertDetail(certId) {
  return request({ url: `/jst/wx/cert/detail/${certId}`, method: 'get' })
}
export function verifyCert(certNo) {
  return request({ url: `/jst/wx/cert/verify/${certNo}`, method: 'get' })
}
```

如果后端尚未实现这些接口，需要在报告中列出，由 Backend Agent 补充。

## 五、DoD

- [ ] 成绩列表页
- [ ] 成绩详情页 + 雷达图
- [ ] 证书列表页
- [ ] 证书详情页（全图预览）
- [ ] 一键保存到相册
- [ ] 分享海报生成（Canvas）
- [ ] 证书验证公开页
- [ ] 入口浅化（我的页 + 赛事详情 + 报名详情）
- [ ] HBuilderX 编译通过
- [ ] 报告交付
