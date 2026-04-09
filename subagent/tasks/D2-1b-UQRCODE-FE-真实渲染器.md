# 任务卡 D2-1b - uqrcode 真实渲染器替换（前端）

## 阶段 / 端
阶段 D 批 2 / **jst-uniapp**

## 背景
POLISH-BATCH2 A 项遗留：当前 `jst-uniapp/utils/qrcode.js` 是自写占位渲染器（21×21 伪随机矩阵 + 3 个定位方格），**不符合 QR Code ISO/IEC 18004 标准，任何扫码器都无法解码**。本卡替换为真实实现 + 解决 canvas 在 swiper 内的渲染时机问题。

## 前置条件（用户已完成）
- [ ] 用户已把 `uqrcode.iife.min.js` 从 jsDelivr 下载到 `jst-uniapp/static/lib/uqrcode.js`（30~40 KB 左右）
- [ ] 用户已确认 D2-1a 后端侧合并或并行

派发前由主 Agent 确认文件就位。

## 交付物

### 1. 引入真实 uqrcode
**删除**：`jst-uniapp/utils/qrcode.js`（自写占位版本）

**新建**：`jst-uniapp/utils/qrcode-wrapper.js`（薄封装，隔离 IIFE 污染）

```js
// 静态 vendor 文件路径不走 ES import，用 require 加载 IIFE
const UQRCode = require('@/static/lib/uqrcode.js')

/**
 * 渲染 QR 到 canvas
 * @param {Object} opts
 * @param {String} opts.canvasId - canvas 的 canvas-id
 * @param {String} opts.text - QR payload（支持纯签名或完整 URL）
 * @param {Number} opts.size - 画布边长 px
 * @param {Object} opts.context - 调用页 this（uniapp 需要传以获取 canvas context）
 */
export function makeQr({ canvasId, text, size = 360, context }) {
  if (!text) return Promise.resolve()
  const qr = new UQRCode()
  qr.data = text
  qr.size = size
  qr.errorCorrectLevel = UQRCode.errorCorrectLevel.M
  qr.make()
  const ctx = uni.createCanvasContext(canvasId, context)
  qr.canvasContext = ctx
  return new Promise((resolve) => {
    qr.drawCanvas()
    ctx.draw(false, () => resolve())
  })
}
```

> 如 uqrcode 4.x API 与上述不一致，Agent 按下载下来的 js 文件的实际导出 + README 调整。优先保证 drawCanvas 能把矩阵画上去。

### 2. 修正 swiper 内 canvas 渲染时机
**修改**：`jst-uniapp/pages-sub/appointment/detail.vue`

当前问题：页面用 `<swiper>` 展示多张核销卡，每张卡内有 `<canvas canvas-id="qr_{{idx}}">`。在 swiper 非激活态下 canvas 可能尚未挂载，导致画出来空白。

解决：
1. 移除 onLoad → $nextTick → 统一 renderQrCodes() 的批量写法
2. 改为 **懒渲染**：
   - 维护 `renderedIdx: Set<number>`
   - swiper `@change="onSwiperChange"` → 取当前 `current` → 若未渲染则调 `makeQr` 画该 index
   - 初次进入页面时，在 `$nextTick` 画 index 0
3. 使用新 wrapper：
   ```js
   import { makeQr } from '@/utils/qrcode-wrapper.js'

   async renderAt(idx) {
     if (this.renderedIdx.has(idx)) return
     const item = this.writeoffItems[idx]
     if (!item || !item.qrCode) return
     await makeQr({
       canvasId: 'qr_' + idx,
       text: item.qrCode,
       size: 360,
       context: this
     })
     this.renderedIdx.add(idx)
   }
   ```

### 3. payload 形态说明
核销 QR payload 是 HMAC 签名字符串（格式 `{bizType}:{id}:{timestamp}:{sig}`），已包含防伪/防重放，**不需要 URL 包装**。对 `UQRCode` 来说只是 text，直接传即可。**不要**做任何 URL 解析或两种形态适配。

D2-1a 后端侧虽然加了一个 `jst.qrcode.writeoff.base-url` 休眠配置项，默认空字符串不激活，前端完全无感。

### 4. 生产前检查清单（写进报告）
- [ ] HBuilderX 预览页面，swiper 滑动每张卡 QR 都正常渲染
- [ ] 用真实扫码工具（微信扫一扫 / 支付宝扫码）扫屏幕，能识别出 payload 文本（识别为签名字符串即为成功，不跳转任何页面是正常的）

## DoD
- [ ] `utils/qrcode.js` 已删，`qrcode-wrapper.js` 已新建
- [ ] `static/lib/uqrcode.js` 存在且 >20KB（用户已下载）
- [ ] `appointment/detail.vue` 改为 swiper @change 懒渲染
- [ ] grep 确认没有其他文件 import 老的 `utils/qrcode.js`（若有 mall/exchange-detail 等按同样方式迁移）
- [ ] 任务报告 `D2-1b-UQRCODE-FE-回答.md` 附手动验证结果
- [ ] commit: `polish(wx): D2-1b 替换 uqrcode 真实渲染器 + swiper 懒渲染`

## 不许做
- ❌ 不许改后端
- ❌ 不许在 git 里直接修改 `static/lib/uqrcode.js` 内容（vendor 文件原样保留）
- ❌ 不许引入 npm 依赖
- ❌ 不许动 canvas 外层业务逻辑（核销状态、请求等）

## 依赖
- 用户完成 uqrcode.js 下载 ✅（必须）
- D2-1a 可并行

## 优先级：高（阻塞二维码核销 E2E）

---
派发时间：2026-04-09
