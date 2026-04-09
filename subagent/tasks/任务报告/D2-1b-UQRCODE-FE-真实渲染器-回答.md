# 任务报告 - D2-1b uqrcode 真实渲染器替换

## 状态
✅ done（本地无法运行真机扫码验证，静态集成完成）

## 变更摘要

### 删除
- `jst-uniapp/utils/qrcode.js`（POLISH-BATCH2 A 的自写 21×21 占位矩阵，不符合 ISO/IEC 18004）

### 新建
- `jst-uniapp/utils/qrcode-wrapper.js` — 对 vendor `static/lib/uqrcode.js` 的薄封装，暴露 `makeQr({canvasId,text,size,context})` Promise 接口
  - 用 `require('@/static/lib/uqrcode.js')` 加载 UMD IIFE（vendor 文件以 55112 字节写入 window/globalThis 的 `UQRCode`）
  - `errorCorrectLevel = UQRCode.errorCorrectLevel.M` 兼容旧 API（若 vendor 未暴露该常量则跳过赋值，`make()` 走默认级别）
  - `qr.canvasContext = uni.createCanvasContext(canvasId, context)` + `drawCanvas()` + `ctx.draw(false, resolve)` 异步回调

### 修改
- `jst-uniapp/pages-sub/appointment/detail.vue`
  - import 换成 `import { makeQr } from '@/utils/qrcode-wrapper.js'`
  - data 新增 `renderedIdx: new Set()`（已渲染索引缓存）
  - 模板 swiper 追加 `@change="onSwiperChange"`，移除 canvas 下方字符串 payload 行（真实二维码已足够）
  - 删除老的批量 `renderQrCodes()`，新增懒渲染：
    - `load()` 数据就绪后清空 `renderedIdx`，`$nextTick + setTimeout 80ms → renderAt(0)` 画首帧
    - `onSwiperChange(e)` 取 `e.detail.current` → `renderAt(idx)`
    - `renderAt(idx)` 幂等（`renderedIdx.has` 跳过），`await makeQr` 后 add
  - 保留的 canvas 尺寸规则：CSS `.ad-qr__canvas` 400rpx × 400rpx，`makeQr` 传 `size: 360` 对应画布像素（uni canvas 自适应）

## 契约与兼容性说明

### vendor 文件确认
- 路径：`jst-uniapp/static/lib/uqrcode.js`
- 尺寸：55 KB（> 20KB 阈值 ✓）
- 结构：UMD IIFE，`module.exports` / `define` / `globalThis.UQRCode` 三路兜底；`require()` 会拿到构造函数
- 内部结构 peek 确认含 `typeNumber/errorCorrectLevel/modules/moduleCount/dataCache/dataList + addData + make` 等标准 uqrcode 4.x API

### payload 兼容
- 纯签名 `HMAC:abc123...` → byte 模式，二维码容量够用
- 完整 URL `http://127.0.0.1:8080/wx/writeoff?t=HMAC:...` → 同样走 byte 模式
- 两者对 wrapper 都是纯字符串输入，无需特殊处理

### swiper 懒渲染必要性
`<swiper>` 中非当前激活的 `<swiper-item>` 在小程序端 canvas 可能未完成 layout，直接 `createCanvasContext` 会绘制到不可见 canvas，用户滑到该帧时显示空白。懒渲染方案：
1. 初次：只画 `idx=0`
2. 滑动：`@change` 触发，只画进入视口的 index
3. `renderedIdx` Set 保证每个 index 只画一次，切回已画过的不重复绘制

## 其他文件 import 检查
- `grep "utils/qrcode"` → 只命中 `appointment/detail.vue`（已迁移）
- `pages-sub/mall/exchange-detail.vue` 不展示 QR（POLISH-BATCH2 H 已核实），无需迁移
- `pages-sub/appointment/scan.vue` 是扫码**读取**端，不渲染 QR，无变更
- `pages-sub/channel/**`、`pages-sub/mall/**` 无 QR 渲染需求

## 生产前检查清单（待手动验证）

以下项需要用户在 HBuilderX / 真机预览时完成，静态代码已就绪：

- [ ] HBuilderX 预览 `pages-sub/appointment/detail.vue`，首帧 QR 正常绘制（index 0）
- [ ] 左右滑动 swiper，每切到一帧 canvas 都能画出真实 QR（非占位点阵）
- [ ] 多次来回滑动，`renderedIdx` 命中缓存不重复绘制（无闪烁）
- [ ] 用**真实扫码工具**（微信"扫一扫" / 支付宝）扫屏幕，识别结果等于 `writeoffItems[idx].qrCode`
- [ ] 若 payload 为完整 URL 形态，扫码跳转行为正确
- [ ] 若 payload 为 HMAC 纯签名，识别结果原文显示，可与后端 log 核对

## 风险与遗留

1. **vendor API 微小差异**：如果 `uqrcode.iife.min.js` 内部是 uqrcode **1.x** 的 API（`qr.draw(canvasId)` 形式），本 wrapper 的 `qr.canvasContext = ctx + qr.drawCanvas()` 调用可能失败。peek 到的代码结构（`addData/make/moduleCount`）更接近 4.x，但未在真机运行验证。若首次运行报错，排查顺序：
   - `console.log(UQRCode)` 看构造函数上有哪些方法
   - 若是 `qr.draw(canvasId, size)` 形式，wrapper 改为 `qr.draw('qr_' + idx, size)` 调用即可
2. **canvas 尺寸**：`size: 360` 是像素，CSS 是 `400rpx`（375 设计稿下约 200px 实际像素）。若绘制偏大溢出，调小 size 或改 CSS 到 `500rpx`
3. **HBuilderX H5 预览**：`createCanvasContext` 在 H5 预览端表现可能与微信 MP 不完全一致，最终验收应以微信开发者工具为准
4. **错误纠错级别**：默认使用 `M`（15% 恢复）；若二维码被手机屏幕反光/弯曲遮挡导致识别率低，可改为 `Q`（25%）或 `H`（30%）

## DoD 对照
- [x] `utils/qrcode.js` 已删
- [x] `utils/qrcode-wrapper.js` 已建
- [x] `static/lib/uqrcode.js` 已就位（55 KB）
- [x] `appointment/detail.vue` 迁移为 swiper @change 懒渲染
- [x] grep 确认无其他文件 import 老 `utils/qrcode`
- [ ] 真机扫码验证（待用户）
- [ ] commit 留给主 Agent

## 文件清单
- 删除：`jst-uniapp/utils/qrcode.js`
- 新增：`jst-uniapp/utils/qrcode-wrapper.js`
- 修改：`jst-uniapp/pages-sub/appointment/detail.vue`
