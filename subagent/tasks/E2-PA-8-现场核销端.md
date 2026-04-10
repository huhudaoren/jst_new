# 任务卡 E2-PA-8 - 现场核销工作人员端

## 阶段 / 端
阶段 E 批 2 / **ruoyi-ui**（手机响应式）

## 背景
赛事方现场工作人员用手机浏览器打开扫码核销预约子码。后端 C6/C7 核销引擎已完成，本卡纯前端。

## PRD 依据
- §7.6 现场核销
- §7.7 扫码核销
- 原型：`event-writeoff.html`

## 交付物

### 1. 新建 `ruoyi-ui/src/views/partner/writeoff.vue`

**手机优先设计**（Q-04 决策的响应式）：

#### 顶部
- 当前赛事 + 日期 + 场次
- 核销统计：已核销 / 总人数 / 剩余

#### 中间
- 大按钮："扫码核销"
- 点击唤起浏览器相机扫码（`navigator.mediaDevices.getUserMedia` + `jsQR` 纯前端库）
  - ⚠️ 如果不能引入 npm，用 HTML5 BarcodeDetector API 检测（部分浏览器支持）
  - 降级：手动输入 QR payload 字符串
- 识别成功 → 调 `POST /jst/wx/writeoff/scan`（复用 C6 后端）

#### 核销结果
- 成功：学生姓名 + 项目名 + "✅ 核销成功"
- 重复核销：提示"已核销过"
- 无效：显示错误码

#### 底部
- 核销记录 tab：最近 20 条核销记录
- 每条：时间 + 学生 + 项目 + 操作人
- 下拉刷新

### 2. 手动输入兜底
- 若扫码不可用（PC 无摄像头），提供文本框手动输入 payload
- 点"确认核销"走同一接口

### 3. API

直接复用 `@/api/wx/writeoff.js`（若不存在则新建封装）：
```js
export function scanWriteoff(payload) { return request({ url: '/jst/wx/writeoff/scan', method: 'post', data: { qrCode: payload } }) }
export function getWriteoffRecent(params) { return request({ url: '/jst/wx/writeoff/recent', method: 'get', params }) }
```

## DoD
- [ ] 页面手机优先响应式
- [ ] 扫码识别 + 手动输入兜底
- [ ] 核销记录实时更新
- [ ] 任务报告 `E2-PA-8-回答.md`

## 不许做
- ❌ 不许改 C6/C7 后端
- ❌ 不许引入 npm 依赖（扫码库用原生 API 或手动输入）
- ❌ 不许做复杂动画（现场用，速度优先）

## 依赖：C6/C7 ✅ + PA-9
## 优先级：P2

---
派发时间：2026-04-10
