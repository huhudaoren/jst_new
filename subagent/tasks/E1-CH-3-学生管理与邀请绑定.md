# 任务卡 E1-CH-3 - 学生管理 + 邀请绑定（含倒计时解绑确认）

## 阶段 / 端
阶段 E 批 1 / **jst-uniapp**

## 背景
`pages-sub/channel/students.vue` 当前是骨架列表。原型 `channel-students.html` 要求：学生卡片 + 代报名/查成绩/查证书/解绑 4 个操作 + 邀请绑定入口（二维码/海报/短链）。

## CCB 决策
- **Q-01**：渠道方可**自助解绑**，但必须有**5 秒倒计时确认框**防止误操作

## PRD 依据
- §7.2 学生管理
- §8.2 绑定关系
- 原型：`channel-students.html` + `binding.html`

## 交付物

### 1. 重构 `pages-sub/channel/students.vue`

顶部：
- 搜索栏（按学生姓名/手机号）
- Tab：[当前绑定] [历史绑定]（历史走 `GET /channel/students?status=history`）
- 右上"邀请绑定"按钮 → 弹窗展示二维码/海报

卡片：
- 头像 + 姓名 + 脱敏手机号
- 绑定时间
- 关联订单数 / 代报名数
- 4 个操作按钮：**代报名** / **查成绩** / **查证书** / **解绑**（红色）

### 2. 解绑倒计时确认弹窗（核心，Q-01 要求）

**组件**：封装 `components/jst-countdown-confirm.vue`

交互：
1. 点击"解绑"按钮 → 弹出半屏弹窗
2. 标题：**"确认解除与 {学生姓名} 的绑定关系？"**
3. 警告区：
   - "⚠️ 解绑后该学生可被其他渠道方绑定"
   - "⚠️ 已计提的历史返点不受影响"
   - "⚠️ 未计提的后续订单将不再归属您"
4. 主按钮初始状态：**"确认解绑（5）"**（灰色禁用）
5. 每秒倒计时 -1，5→4→3→2→1→0
6. 倒计时归零后按钮变红色可点击："确认解绑"
7. 点击确认 → `POST /channel/binding/{id}/unbind` → 成功回列表
8. 取消按钮始终可用

### 3. 邀请绑定弹窗

内容：
- 顶部：渠道方姓名 + 渠道类型标签
- 中间：二维码大图（canvas 渲染，payload 为 `binding.html?channelId=xxx`）
- 下方：分享海报预览（canvas 绘制含渠道信息 + 二维码 + 品牌）
- 操作按钮：
  - 保存二维码到相册（`uni.canvasToTempFilePath` + `uni.saveImageToPhotosAlbum`）
  - 保存海报到相册
  - 复制绑定链接（短链）
  - 微信分享（`open-type="share"` button）

**二维码渲染**：复用 `utils/qrcode-wrapper.js`（D2-1b 已就绪）

### 4. 代报名 / 查成绩 / 查证书 三个操作

**代报名**：跳 `/pages-sub/channel/batch-enroll?singleStudentId={id}`（CH-7 会补完整页，本卡只需能跳且带 query）

**查成绩**：跳 `/pages-sub/channel/student-score?studentId={id}`，新建页展示该学生所有已发布成绩（`GET /channel/students/{id}/score`）

**查证书**：跳 `/pages-sub/channel/student-cert?studentId={id}`，新建页展示证书列表（`GET /channel/students/{id}/cert`），每项含下载链接

### 5. API 补充
**修改**：`jst-uniapp/api/channel.js`

```js
export function getChannelStudents(params) { return request({ url: '/jst/wx/channel/students', method: 'GET', params }) }
export function unbindStudent(bindingId) { return request({ url: `/jst/wx/channel/binding/${bindingId}/unbind`, method: 'POST' }) }
export function getStudentScore(studentId) { return request({ url: `/jst/wx/channel/students/${studentId}/score`, method: 'GET' }) }
export function getStudentCert(studentId) { return request({ url: `/jst/wx/channel/students/${studentId}/cert`, method: 'GET' }) }
```

### 6. 页面文件清单
- 重构：`pages-sub/channel/students.vue`
- 新建：`pages-sub/channel/student-score.vue`
- 新建：`pages-sub/channel/student-cert.vue`
- 新建：`components/jst-countdown-confirm.vue`
- 新建：`components/jst-invite-modal.vue`
- 修改：`api/channel.js` / `pages.json`

## DoD
- [ ] students.vue 完整（搜索/Tab/卡片/4 操作）
- [ ] 倒计时解绑确认弹窗 5 秒严格生效
- [ ] 邀请绑定弹窗含二维码 + 海报 + 分享
- [ ] 查成绩/查证书 2 个子页面
- [ ] api/channel.js 补 4 方法
- [ ] 任务报告 `E1-CH-3-回答.md`

## 不许做
- ❌ 不许跳过倒计时（Q-01 强制要求）
- ❌ 不许改后端
- ❌ 海报样式不要求像素级对齐原型，但必须含渠道信息和二维码

## 依赖：E0-1 + E1-CH-1 + E1-CH-2
## 优先级：P1

---
派发时间：2026-04-10
