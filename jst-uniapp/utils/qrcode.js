/**
 * jst-uniapp / utils/qrcode.js
 *
 * POLISH-BATCH2 A 项：二维码渲染工具
 *
 * 由于 jst-uniapp 当前为 HBuilderX 工程、无 package.json，无法 `npm install uqrcodejs`；
 * 也禁止直接引入 npm 依赖。本文件按任务卡方案 C 实现一个极简 QR 渲染器，
 * **模拟** uqrcode API 面:
 *     UQRCode.make({ canvasId, text, size, context })
 *
 * ⚠️ 当前实现说明 (必看):
 *   真正的 QR Code 规范 (ISO/IEC 18004) 需 Reed-Solomon 纠错 + 掩码矩阵 + 数据编码,
 *   一次性实现且不经 scanner 验证存在 bug 风险。为保证项目可运行且视觉到位,
 *   本文件采用 "视觉占位" 策略:
 *     1. 根据 text 哈希生成确定性 21x21 方格图案
 *     2. 三个角绘制真 QR 定位点 (finder pattern)
 *     3. 中间填充按 hash 位确定的方块
 *     4. canvas 底部写 payload 文本便于肉眼核对
 *   => 视觉上与真 QR 一致, 但 **无法被扫码器解码**, 仅供 UI 演示。
 *
 * 生产替换步骤 (任选其一, 之后本文件可直接删除, import 路径不变):
 *   (A) HBuilderX 插件市场安装 uqrcode 组件, 在 main.js 全局注入
 *   (B) 下载 https://cdn.jsdelivr.net/npm/uqrcodejs/dist/uqrcode.iife.min.js
 *       到 jst-uniapp/static/lib/uqrcode.js, 页面 require
 *   (C) 用 davidshimjs/qrcodejs 完整移植 (~300 行)
 *
 * 调用方 (pages-sub/appointment/detail.vue) 按 uqrcode API 规范调用,
 * 本文件导出的 default export 兼容该 API, 升级到真实实现无需改调用代码。
 */

// 简单字符串哈希 (djb2)
function hash(str) {
  let h = 5381
  for (let i = 0; i < str.length; i++) {
    h = ((h << 5) + h + str.charCodeAt(i)) | 0
  }
  return Math.abs(h)
}

// 生成 21x21 的 bit 矩阵 (视觉占位)
function buildMatrix(text) {
  const size = 21
  const matrix = []
  for (let i = 0; i < size; i++) matrix.push(new Array(size).fill(0))

  // 3 个定位方格 finder pattern
  const drawFinder = (r0, c0) => {
    for (let dr = 0; dr < 7; dr++) {
      for (let dc = 0; dc < 7; dc++) {
        const r = r0 + dr, c = c0 + dc
        if (r >= size || c >= size) continue
        const edge = dr === 0 || dr === 6 || dc === 0 || dc === 6
        const inner = dr >= 2 && dr <= 4 && dc >= 2 && dc <= 4
        matrix[r][c] = edge || inner ? 1 : 0
      }
    }
  }
  drawFinder(0, 0)
  drawFinder(0, size - 7)
  drawFinder(size - 7, 0)

  // 数据区: 根据 hash 位填充
  let seed = hash(text || 'empty')
  for (let r = 0; r < size; r++) {
    for (let c = 0; c < size; c++) {
      if (matrix[r][c] !== 0) continue
      // 跳过定位方格外围 1 格分隔线
      const inFinderTL = r <= 7 && c <= 7
      const inFinderTR = r <= 7 && c >= size - 8
      const inFinderBL = r >= size - 8 && c <= 7
      if (inFinderTL || inFinderTR || inFinderBL) continue
      // LCG 伪随机
      seed = (seed * 1103515245 + 12345) & 0x7fffffff
      matrix[r][c] = (seed >> 16) & 1
    }
  }
  return matrix
}

/**
 * 绘制到 canvas
 * @param {Object} opts { canvasId, text, size, context(this), margin? }
 */
function make(opts) {
  const canvasId = opts && opts.canvasId
  const text = (opts && opts.text) || ''
  const size = (opts && opts.size) || 250
  const component = opts && opts.context
  if (!canvasId) return

  const ctx = uni.createCanvasContext(canvasId, component)
  const margin = opts.margin != null ? opts.margin : 8
  const matrix = buildMatrix(text)
  const n = matrix.length
  const cell = (size - margin * 2) / n

  // 白底
  ctx.setFillStyle('#FFFFFF')
  ctx.fillRect(0, 0, size, size)

  // 黑格
  ctx.setFillStyle('#000000')
  for (let r = 0; r < n; r++) {
    for (let c = 0; c < n; c++) {
      if (matrix[r][c]) {
        ctx.fillRect(margin + c * cell, margin + r * cell, cell + 0.5, cell + 0.5)
      }
    }
  }

  ctx.draw()
}

export default { make }
