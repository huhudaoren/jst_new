# REFACTOR-TODO-CERT-RENDER — 证书客户端渲染引擎（三端同构）

> 优先级：P1 | 预估：L | Agent：Backend Agent（小改）+ MiniProgram Agent（主力）
> 依赖：REFACTOR-5-CERT（layout_json 格式已定义）、REFACTOR-11-MP-CERT（cert-detail.vue 已有）

---

## 一、背景

证书设计器（Fabric.js）已上线，layout_json 可保存/加载。证书详情页需要展示**真实渲染的证书图片**。

**核心决策：客户端渲染，不走服务端。**
- layout_json 本身就是 Fabric.js Canvas 描述，客户端渲染是原生用法
- 服务端渲染（Java Graphics2D 解析 Fabric JSON）有格式偏差风险且 CPU 密集
- 每个用户只渲染自己的证书，负载天然分散
- 无需存储所有证书图片，节省 OSS/磁盘

## 二、三端同构架构

```
                    layout_json (Fabric.js toJSON)
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
     管理端 (ruoyi-ui)   H5 端 (jst-uniapp)   小程序端 (jst-uniapp)
     Fabric.js           Fabric.js            Canvas 2D
     StaticCanvas        StaticCanvas         uni.createCanvasContext
              │               │               │
              ▼               ▼               ▼
          同一渲染逻辑：cert-render-core.js（共享）
```

### 同构方案

抽取一个**纯 JS 渲染核心模块** `cert-render-core.js`，三端复用：

- **管理端**：Fabric.js `StaticCanvas.loadFromJSON()` → 直接渲染（已有，REFACTOR-5 产出）
- **H5 端**：同管理端，Fabric.js 已在 H5 构建中可用
- **小程序端**：Canvas 2D API 手动绘制（小程序不支持 Fabric.js，需适配层）

关键：**小程序适配层**必须与 Fabric.js 渲染结果像素级一致。

## 三、Backend 部分（改动很小）

### 3.1 证书详情接口增强

`GET /jst/wx/cert/{certRecordId}` 返回值增加：

```json
{
  "certNo": "JST-CERT-20260414-0001",
  "templateName": "全国美术大赛获奖证书",
  "layoutJson": "{...Fabric.js JSON...}",   // ← 新增：从 cert_template 关联查出
  "bgImage": "https://.../cert-bg.png",      // ← 新增：底图 URL
  "variables": {                              // ← 新增：已替换好的变量 Map
    "name": "张小明",
    "contestName": "2026全国青少年美术大赛",
    "awardName": "一等奖",
    "score": "95.5",
    "certNo": "JST-CERT-20260414-0001",
    "date": "2026年4月14日",
    "school": "北京市第一小学",
    "groupLevel": "小学组"
  },
  "issueStatus": "granted",
  "createTime": "2026-04-14 10:00:00"
}
```

**改动文件**：
- `WxCertController` 或对应 Service — 查 cert_record → 关联查 cert_template 的 layoutJson + bgImage → 组装 variables Map
- `CertDetailVO`（或复用已有 VO）— 新增 layoutJson / bgImage / variables 字段

### 3.2 管理端预览接口（可选）

`GET /jst/partner/cert/preview-data/{certRecordId}` — 返回同样的 layoutJson + variables，供管理端预览渲染。

如果管理端已有 Fabric.js loadFromJSON 能力（REFACTOR-5 已实现），只需传数据过去即可。

## 四、MiniProgram 部分（主力工作）

### 4.1 共享渲染核心 `utils/cert-renderer.js`

```javascript
/**
 * 证书渲染核心 —— 解析 Fabric.js layout_json，在 Canvas 2D 上绘制
 * 
 * 设计原则：
 * 1. 与 Fabric.js StaticCanvas 渲染结果保持像素级一致
 * 2. 支持小程序 Canvas 2D API + H5 标准 Canvas API
 * 3. 变量替换在绘制前完成
 */

/**
 * @param {Object} ctx - Canvas 2D context (uni 或 web)
 * @param {Object} layoutData - JSON.parse(layoutJson)
 * @param {Object} variables - {name, contestName, awardName, ...}
 * @param {Object} options - {width, height, pixelRatio, platform:'mp'|'h5'}
 */
export async function renderCert(ctx, layoutData, variables, options) {
  const { width, height, pixelRatio = 2, platform = 'mp' } = options

  // 1. 绘制背景图
  if (layoutData.backgroundImage && layoutData.backgroundImage.src) {
    await drawImage(ctx, layoutData.backgroundImage.src, 0, 0, width, height, platform)
  }

  // 2. 遍历 objects 数组，按 Fabric.js 属性绘制
  for (const obj of (layoutData.objects || [])) {
    ctx.save()

    // 位置与变换（Fabric.js 坐标系）
    const left = obj.left || 0
    const top = obj.top || 0
    const scaleX = obj.scaleX || 1
    const scaleY = obj.scaleY || 1
    const angle = obj.angle || 0

    ctx.translate(left, top)
    if (angle) ctx.rotate(angle * Math.PI / 180)
    ctx.scale(scaleX, scaleY)

    switch (obj.type) {
      case 'text':
      case 'i-text':
      case 'textbox':
        drawText(ctx, obj, variables)
        break
      case 'image':
        await drawImage(ctx, obj.src, 0, 0, obj.width, obj.height, platform)
        break
      case 'rect':
        drawRect(ctx, obj)
        break
      case 'line':
        drawLine(ctx, obj)
        break
    }

    ctx.restore()
  }
}

function drawText(ctx, obj, variables) {
  let text = obj.text || ''
  // 替换动态变量 {{name}} → 实际值
  if (obj.certVarKey && variables[obj.certVarKey]) {
    text = variables[obj.certVarKey]
  } else {
    // 通用替换 {{xxx}}
    text = text.replace(/\{\{(\w+)\}\}/g, (_, key) => variables[key] || '')
  }

  ctx.font = `${obj.fontStyle || 'normal'} ${obj.fontWeight || 'normal'} ${obj.fontSize || 24}px ${obj.fontFamily || 'sans-serif'}`
  ctx.fillStyle = obj.fill || '#000000'
  ctx.textAlign = obj.textAlign || 'left'
  ctx.textBaseline = 'top'
  ctx.fillText(text, 0, 0)
}

function drawRect(ctx, obj) {
  ctx.fillStyle = obj.fill || 'transparent'
  ctx.strokeStyle = obj.stroke || '#000000'
  ctx.lineWidth = obj.strokeWidth || 1
  if (obj.fill && obj.fill !== 'transparent') {
    ctx.fillRect(0, 0, obj.width || 100, obj.height || 50)
  }
  if (obj.stroke) {
    ctx.strokeRect(0, 0, obj.width || 100, obj.height || 50)
  }
}

function drawLine(ctx, obj) {
  ctx.strokeStyle = obj.stroke || '#000000'
  ctx.lineWidth = obj.strokeWidth || 1
  ctx.beginPath()
  ctx.moveTo(obj.x1 || 0, obj.y1 || 0)
  ctx.lineTo(obj.x2 || 100, obj.y2 || 0)
  ctx.stroke()
}

async function drawImage(ctx, src, x, y, w, h, platform) {
  if (platform === 'mp') {
    // 小程序：先 downloadFile → 再 drawImage
    const tempPath = await downloadToTemp(src)
    ctx.drawImage(tempPath, x, y, w, h)
  } else {
    // H5：new Image() → onload → drawImage
    const img = await loadImage(src)
    ctx.drawImage(img, x, y, w, h)
  }
}

function downloadToTemp(url) {
  return new Promise((resolve, reject) => {
    uni.downloadFile({
      url,
      success: (res) => resolve(res.tempFilePath),
      fail: reject
    })
  })
}

function loadImage(src) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = src
  })
}
```

### 4.2 cert-detail.vue 集成

替换当前的"模拟证书样式"为真实 Canvas 渲染：

```vue
<template>
  <view class="cert-detail-page">
    <!-- 证书渲染区 -->
    <view class="cert-canvas-wrap">
      <canvas
        type="2d"
        id="certCanvas"
        class="cert-canvas"
        :style="{ width: canvasW + 'px', height: canvasH + 'px' }"
      />
      <view v-if="rendering" class="cert-loading">
        <u-loading-icon />
        <text>证书生成中...</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="cert-actions">
      <button @tap="saveToAlbum">保存到相册</button>
      <button @tap="generatePoster">生成分享海报</button>
    </view>
  </view>
</template>

<script>
import { renderCert } from '@/utils/cert-renderer'
import { getCertDetail } from '@/api/cert'

export default {
  data() {
    return { certData: null, rendering: false, canvasW: 375, canvasH: 530 }
  },
  onLoad(opts) {
    this.loadAndRender(opts.id)
  },
  methods: {
    async loadAndRender(certId) {
      this.rendering = true
      const res = await getCertDetail(certId)
      this.certData = res.data

      // 解析 layoutJson
      const layoutData = JSON.parse(this.certData.layoutJson)

      // 获取 Canvas context (2D API)
      const query = uni.createSelectorQuery().in(this)
      query.select('#certCanvas').fields({ node: true, size: true }).exec(async (r) => {
        const canvas = r[0].node
        const ctx = canvas.getContext('2d')
        const dpr = uni.getSystemInfoSync().pixelRatio
        canvas.width = this.canvasW * dpr
        canvas.height = this.canvasH * dpr
        ctx.scale(dpr, dpr)

        // 渲染
        await renderCert(ctx, layoutData, this.certData.variables, {
          width: this.canvasW,
          height: this.canvasH,
          pixelRatio: dpr,
          platform: 'mp'
        })
        this.rendering = false
      })
    },

    saveToAlbum() {
      // canvasToTempFilePath → saveImageToPhotosAlbum
    }
  }
}
</script>
```

### 4.3 管理端同构验证

管理端 `cert-manage.vue` 的预览功能（REFACTOR-5 已有）使用 Fabric.js `StaticCanvas.loadFromJSON()`。

**验证一致性方法**：
1. 管理端预览截图 vs 小程序端渲染截图，肉眼对比
2. 重点校验：文字位置/大小/颜色、图片缩放、旋转角度
3. 如有偏差，调整 `cert-renderer.js` 的坐标系映射

**已知偏差点及处理**：
| Fabric.js 特性 | Canvas 2D 处理 |
|---|---|
| originX/originY (center/left) | 根据 origin 偏移 translate |
| textbox 自动换行 | Canvas measureText + 手动换行 |
| fontWeight bold | ctx.font 中拼接 bold |
| shadow | ctx.shadowColor/shadowBlur/shadowOffsetX/Y |
| opacity | ctx.globalAlpha |

### 4.4 字体一致性

**问题**：管理端浏览器有 SimHei/微软雅黑，小程序端没有。

**解决**：
- cert-renderer.js 中维护字体映射表：
  ```javascript
  const FONT_MAP = {
    'SimHei': 'sans-serif',        // 小程序 fallback
    'Microsoft YaHei': 'sans-serif',
    'SimSun': 'serif',
    'KaiTi': 'serif'
  }
  ```
- 管理端证书设计器的字体选择列表中标注"推荐字体"（系统默认字体，三端一致性最好）
- 后续如需精确字体，可在小程序端通过 `uni.loadFontFace` 加载云端字体文件

## 五、DoD

### Backend（改动小）
- [ ] `GET /jst/wx/cert/{id}` 返回 layoutJson + bgImage + variables Map
- [ ] `mvn compile` 通过

### MiniProgram（主力）
- [ ] `utils/cert-renderer.js` 渲染核心（解析 Fabric JSON → Canvas 2D 绘制）
- [ ] 支持 5 种元素类型（text/i-text/textbox/image/rect/line）
- [ ] 动态变量替换（certVarKey 优先，{{xxx}} 通用 fallback）
- [ ] 背景图绘制
- [ ] 坐标系映射（origin/scale/rotate）
- [ ] cert-detail.vue 集成真实渲染（替换模拟样式）
- [ ] 保存到相册流程正常
- [ ] 字体映射表
- [ ] HBuilderX 编译通过

### 一致性验证
- [ ] 管理端 Fabric.js 预览 vs 小程序 Canvas 渲染 → 视觉一致
