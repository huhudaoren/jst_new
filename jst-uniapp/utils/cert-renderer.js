/**
 * 证书渲染核心 — 解析 Fabric.js layoutJson → Canvas 2D 绘制
 * 设计原则：与管理端 Fabric.js StaticCanvas 渲染结果保持视觉一致
 *
 * 支持元素类型：text / i-text / textbox / image / rect / line / group
 * 变量替换：certVarKey 优先，{{xxx}} 通用 fallback
 * 坐标系：与 Fabric.js origin/scale/rotate 一致（center-based 渲染）
 */

// Fabric.js 设计器画布尺寸（A4 比例）
var CERT_WIDTH = 794
var CERT_HEIGHT = 1123

// 字体映射：管理端字体 → 小程序可用 fallback
var FONT_MAP = {
  'SimHei': 'sans-serif',
  'SimSun': 'serif',
  'Microsoft YaHei': 'sans-serif',
  'KaiTi': 'serif',
  'FangSong': 'serif',
  'Arial': 'Arial, sans-serif',
  'Times New Roman': 'Times New Roman, serif',
  'Georgia': 'Georgia, serif',
  'Verdana': 'Verdana, sans-serif'
}

function mapFont(fontFamily) {
  return FONT_MAP[fontFamily] || fontFamily || 'sans-serif'
}

/**
 * 计算 Fabric.js 对象中心点
 * Fabric.js 内部渲染以对象中心为 (0,0)，left/top 是 origin 对应的点
 */
function getCenterPoint(obj) {
  var w = (obj.width || 0) * (obj.scaleX || 1)
  var h = (obj.height || 0) * (obj.scaleY || 1)
  var cx = obj.left || 0
  var cy = obj.top || 0
  var originX = obj.originX || 'left'
  var originY = obj.originY || 'top'

  if (originX === 'left') cx += w / 2
  else if (originX === 'right') cx -= w / 2

  if (originY === 'top') cy += h / 2
  else if (originY === 'bottom') cy -= h / 2

  return { cx: cx, cy: cy }
}

// 变量替换（certVarKey 优先，{{xxx}} 通用 fallback）
function resolveText(text, obj, variables) {
  if (!text) return ''
  if (!variables) return text

  if (obj.certVarKey && variables[obj.certVarKey] !== undefined) {
    return String(variables[obj.certVarKey])
  }

  return text.replace(/\{\{(\w+)\}\}/g, function(match, key) {
    return variables[key] !== undefined ? String(variables[key]) : match
  })
}

// 构建 CSS font 字符串
function buildFont(obj) {
  var style = obj.fontStyle || 'normal'
  var weight = obj.fontWeight || 'normal'
  var size = obj.fontSize || 24
  var family = mapFont(obj.fontFamily)
  return style + ' ' + weight + ' ' + size + 'px ' + family
}

/**
 * 加载图片（小程序 Canvas 2D API：用 canvas.createImage）
 * @param {Object} canvasNode - Canvas 节点（from createSelectorQuery）
 * @param {string} src - 图片路径（data URL / 网络 URL / 本地路径）
 */
function loadImage(canvasNode, src) {
  return new Promise(function(resolve, reject) {
    if (!src) return reject(new Error('图片 src 为空'))

    // data URL 直接加载
    if (src.indexOf('data:') === 0) {
      var img = canvasNode.createImage()
      img.onload = function() { resolve(img) }
      img.onerror = function(e) { reject(e || new Error('data URL 图片加载失败')) }
      img.src = src
      return
    }

    // 网络图片：先下载到临时文件再加载
    if (src.indexOf('http') === 0) {
      uni.downloadFile({
        url: src,
        success: function(res) {
          if (res.statusCode === 200) {
            var img2 = canvasNode.createImage()
            img2.onload = function() { resolve(img2) }
            img2.onerror = function(e) { reject(e || new Error('临时文件图片加载失败')) }
            img2.src = res.tempFilePath
          } else {
            reject(new Error('图片下载失败: ' + res.statusCode))
          }
        },
        fail: reject
      })
      return
    }

    // 本地路径直接加载
    var img3 = canvasNode.createImage()
    img3.onload = function() { resolve(img3) }
    img3.onerror = function(e) { reject(e || new Error('本地图片加载失败')) }
    img3.src = src
  })
}

// 文本自动换行（textbox 专用）
function wrapText(ctx, text, maxWidth) {
  var paragraphs = text.split('\n')
  var result = []

  for (var i = 0; i < paragraphs.length; i++) {
    var para = paragraphs[i]
    if (!para) {
      result.push('')
      continue
    }
    var currentLine = ''
    for (var j = 0; j < para.length; j++) {
      var testLine = currentLine + para[j]
      if (ctx.measureText(testLine).width > maxWidth && currentLine) {
        result.push(currentLine)
        currentLine = para[j]
      } else {
        currentLine = testLine
      }
    }
    if (currentLine) result.push(currentLine)
  }

  return result
}

// 圆角矩形路径
function roundRectPath(ctx, x, y, w, h, r) {
  ctx.beginPath()
  ctx.moveTo(x + r, y)
  ctx.lineTo(x + w - r, y)
  ctx.arcTo(x + w, y, x + w, y + r, r)
  ctx.lineTo(x + w, y + h - r)
  ctx.arcTo(x + w, y + h, x + w - r, y + h, r)
  ctx.lineTo(x + r, y + h)
  ctx.arcTo(x, y + h, x, y + h - r, r)
  ctx.lineTo(x, y + r)
  ctx.arcTo(x, y, x + r, y, r)
  ctx.closePath()
}

// ========== 各类型元素绘制（均以 (0,0) 为中心绘制） ==========

// 绘制文本（text / i-text，支持 \n 多行）
function drawText(ctx, obj, variables) {
  var text = resolveText(obj.text, obj, variables)
  if (!text) return

  var w = obj.width || 0
  var h = obj.height || 0

  ctx.font = buildFont(obj)
  ctx.fillStyle = obj.fill || '#000000'
  ctx.textBaseline = 'top'

  var align = obj.textAlign || 'left'
  ctx.textAlign = align

  // 计算绘制 x（相对于中心 0,0）
  var x = 0
  if (align === 'left') x = -w / 2
  else if (align === 'right') x = w / 2
  // center → x = 0

  var y = -h / 2
  var lines = text.split('\n')
  var lineHeight = (obj.lineHeight || 1.16) * (obj.fontSize || 24)

  for (var i = 0; i < lines.length; i++) {
    ctx.fillText(lines[i], x, y + i * lineHeight)
  }
}

// 绘制 Textbox（自动换行）
function drawTextbox(ctx, obj, variables) {
  var text = resolveText(obj.text, obj, variables)
  if (!text) return

  var w = obj.width || 200
  var h = obj.height || 0

  ctx.font = buildFont(obj)
  ctx.fillStyle = obj.fill || '#000000'
  ctx.textBaseline = 'top'

  var align = obj.textAlign || 'left'
  ctx.textAlign = align

  var lineHeight = (obj.lineHeight || 1.16) * (obj.fontSize || 24)
  var wrapped = wrapText(ctx, text, w)

  var x = 0
  if (align === 'left') x = -w / 2
  else if (align === 'right') x = w / 2

  var startY = -h / 2

  for (var i = 0; i < wrapped.length; i++) {
    ctx.fillText(wrapped[i], x, startY + i * lineHeight)
  }
}

// 绘制矩形
function drawRect(ctx, obj) {
  var w = obj.width || 100
  var h = obj.height || 50
  var x = -w / 2
  var y = -h / 2
  var hasFill = obj.fill && obj.fill !== 'transparent' && obj.fill !== ''
  var hasStroke = obj.stroke && obj.strokeWidth
  var rx = obj.rx || 0

  if (rx > 0) {
    roundRectPath(ctx, x, y, w, h, rx)
    if (hasFill) {
      ctx.fillStyle = obj.fill
      ctx.fill()
    }
    if (hasStroke) {
      ctx.strokeStyle = obj.stroke
      ctx.lineWidth = obj.strokeWidth || 1
      ctx.stroke()
    }
  } else {
    if (hasFill) {
      ctx.fillStyle = obj.fill
      ctx.fillRect(x, y, w, h)
    }
    if (hasStroke) {
      ctx.strokeStyle = obj.stroke
      ctx.lineWidth = obj.strokeWidth || 1
      ctx.strokeRect(x, y, w, h)
    }
  }
}

// 绘制线条（复现 Fabric.js calcLinePoints 逻辑）
function drawLine(ctx, obj) {
  var w = obj.width || 0
  var h = obj.height || 0
  var x1 = obj.x1 !== undefined ? obj.x1 : 0
  var y1 = obj.y1 !== undefined ? obj.y1 : 0
  var x2 = obj.x2 !== undefined ? obj.x2 : 0
  var y2 = obj.y2 !== undefined ? obj.y2 : 0

  // Fabric.js 内部 calcLinePoints 逻辑
  var xMult = x1 <= x2 ? -1 : 1
  var yMult = y1 <= y2 ? -1 : 1

  ctx.strokeStyle = obj.stroke || '#000000'
  ctx.lineWidth = obj.strokeWidth || 1
  ctx.beginPath()
  ctx.moveTo(xMult * w * 0.5, yMult * h * 0.5)
  ctx.lineTo(xMult * w * -0.5, yMult * h * -0.5)
  ctx.stroke()
}

// 绘制 Group（QR 码占位符等复合元素）
function drawGroup(ctx, obj, variables, canvasNode) {
  var subObjects = obj.objects || []

  // QR 码特殊处理：如果变量中提供了 qrcodeUrl，绘制真实二维码图
  if (obj.certVarKey === 'qrcode' && variables && variables.qrcodeUrl) {
    return loadImage(canvasNode, variables.qrcodeUrl).then(function(qrImg) {
      var size = Math.max(obj.width || 80, obj.height || 80)
      ctx.drawImage(qrImg, -size / 2, -size / 2, size, size)
    }).catch(function() {
      // 加载失败则继续绘制占位子元素
      drawGroupChildren(ctx, subObjects, variables)
    })
  }

  drawGroupChildren(ctx, subObjects, variables)
  return Promise.resolve()
}

// 绘制 Group 内子元素
function drawGroupChildren(ctx, subObjects, variables) {
  for (var i = 0; i < subObjects.length; i++) {
    var sub = subObjects[i]
    ctx.save()

    var subLeft = sub.left || 0
    var subTop = sub.top || 0
    ctx.translate(subLeft, subTop)

    if (sub.angle) ctx.rotate(sub.angle * Math.PI / 180)
    if (sub.scaleX || sub.scaleY) ctx.scale(sub.scaleX || 1, sub.scaleY || 1)

    switch (sub.type) {
      case 'rect':
        drawRect(ctx, sub)
        break
      case 'text':
      case 'i-text':
        drawText(ctx, sub, variables)
        break
    }

    ctx.restore()
  }
}

// 应用阴影属性
function applyShadow(ctx, shadow) {
  if (!shadow) return
  if (typeof shadow === 'object') {
    ctx.shadowColor = shadow.color || 'rgba(0,0,0,0.3)'
    ctx.shadowBlur = shadow.blur || 0
    ctx.shadowOffsetX = shadow.offsetX || 0
    ctx.shadowOffsetY = shadow.offsetY || 0
  }
}

/**
 * 主渲染入口 — 将 Fabric.js layoutJson 渲染到 Canvas 2D
 *
 * @param {CanvasRenderingContext2D} ctx - Canvas 2D 上下文
 * @param {Object} layoutData - JSON.parse(layoutJson) 的 Fabric.js 数据
 * @param {Object} variables - 变量替换 Map {name, contestName, awardName, certNo, ...}
 * @param {Object} options - { width, height, canvas(Canvas节点) }
 */
function renderCert(ctx, layoutData, variables, options) {
  var width = options.width || CERT_WIDTH
  var height = options.height || CERT_HEIGHT
  var canvasNode = options.canvas

  // 清空画布
  ctx.clearRect(0, 0, width, height)

  // 1. 背景色
  ctx.fillStyle = layoutData.background || '#ffffff'
  ctx.fillRect(0, 0, width, height)

  // 2. 背景图
  var bgPromise = Promise.resolve()
  if (layoutData.backgroundImage && layoutData.backgroundImage.src) {
    bgPromise = loadImage(canvasNode, layoutData.backgroundImage.src).then(function(bgImg) {
      ctx.drawImage(bgImg, 0, 0, width, height)
    }).catch(function(e) {
      console.warn('[cert-renderer] 背景图加载失败:', e)
    })
  }

  // 背景图加载完成后再绘制前景元素
  return bgPromise.then(function() {
    var objects = layoutData.objects || []
    // 串行绘制（因为图片元素需要异步加载）
    var chain = Promise.resolve()

    for (var i = 0; i < objects.length; i++) {
      ;(function(obj) {
        chain = chain.then(function() {
          return renderObject(ctx, obj, variables, canvasNode)
        })
      })(objects[i])
    }

    return chain
  })
}

// 渲染单个对象
function renderObject(ctx, obj, variables, canvasNode) {
  if (obj.visible === false) return Promise.resolve()

  ctx.save()

  // 透明度
  if (obj.opacity !== undefined && obj.opacity !== 1) {
    ctx.globalAlpha = obj.opacity
  }

  // 阴影
  applyShadow(ctx, obj.shadow)

  // 计算中心点 + 变换
  var center = getCenterPoint(obj)
  var scaleX = obj.scaleX || 1
  var scaleY = obj.scaleY || 1
  var angle = obj.angle || 0

  ctx.translate(center.cx, center.cy)
  if (angle) ctx.rotate(angle * Math.PI / 180)
  ctx.scale(scaleX, scaleY)

  var result = Promise.resolve()

  switch (obj.type) {
    case 'text':
    case 'i-text':
      drawText(ctx, obj, variables)
      break
    case 'textbox':
      drawTextbox(ctx, obj, variables)
      break
    case 'image':
      if (obj.src) {
        var imgW = obj.width || 0
        var imgH = obj.height || 0
        result = loadImage(canvasNode, obj.src).then(function(img) {
          ctx.drawImage(img, -imgW / 2, -imgH / 2, imgW, imgH)
        }).catch(function(e) {
          console.warn('[cert-renderer] 图片元素加载失败:', e)
          // 绘制占位矩形
          ctx.fillStyle = '#f0f0f0'
          ctx.fillRect(-imgW / 2, -imgH / 2, imgW, imgH)
          ctx.strokeStyle = '#cccccc'
          ctx.lineWidth = 1
          ctx.strokeRect(-imgW / 2, -imgH / 2, imgW, imgH)
        })
      }
      break
    case 'rect':
      drawRect(ctx, obj)
      break
    case 'line':
      drawLine(ctx, obj)
      break
    case 'group':
      result = drawGroup(ctx, obj, variables, canvasNode)
      break
  }

  return result.then(function() {
    ctx.restore()
  })
}

export { renderCert, FONT_MAP, CERT_WIDTH, CERT_HEIGHT }
