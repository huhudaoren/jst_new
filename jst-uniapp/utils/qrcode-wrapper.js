/**
 * jst-uniapp / utils/qrcode-wrapper.js
 *
 * D2-1b: 真实 uqrcode 薄封装
 * 依赖 vendor 文件: jst-uniapp/static/lib/uqrcode.js (UMD IIFE)
 */
const UQRCode = require('@/static/lib/uqrcode.js')

/**
 * 渲染 QR 到 canvas
 * @param {Object} opts
 * @param {String} opts.canvasId  canvas 的 canvas-id
 * @param {String} opts.text      QR payload (签名字符串或完整 URL)
 * @param {Number} [opts.size=360] 画布边长 px
 * @param {Object} opts.context   调用页 this (uniapp 需传以获取 canvas context)
 * @returns {Promise<void>}
 */
export function makeQr({ canvasId, text, size = 360, context }) {
  if (!text || !canvasId) return Promise.resolve()
  const qr = new UQRCode()
  qr.data = text
  qr.size = size
  if (UQRCode.errorCorrectLevel && UQRCode.errorCorrectLevel.M != null) {
    qr.errorCorrectLevel = UQRCode.errorCorrectLevel.M
  }
  try { qr.make() } catch (e) { return Promise.reject(e) }

  const ctx = uni.createCanvasContext(canvasId, context)
  qr.canvasContext = ctx

  return new Promise((resolve) => {
    try { qr.drawCanvas() } catch (e) {}
    ctx.draw(false, () => resolve())
  })
}

export default { makeQr }
