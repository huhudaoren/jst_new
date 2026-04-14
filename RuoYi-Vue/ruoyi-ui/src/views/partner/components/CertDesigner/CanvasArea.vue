<template>
  <div class="cert-canvas-area" ref="canvasWrap">
    <div class="canvas-toolbar">
      <el-button-group>
        <el-button size="mini" icon="el-icon-refresh-left" :disabled="!canUndo" @click="undo">撤销</el-button>
        <el-button size="mini" icon="el-icon-refresh-right" :disabled="!canRedo" @click="redo">重做</el-button>
      </el-button-group>
      <span class="zoom-label">{{ Math.round(zoom * 100) }}%</span>
      <el-button-group>
        <el-button size="mini" icon="el-icon-minus" @click="zoomOut" />
        <el-button size="mini" @click="zoomReset">适应</el-button>
        <el-button size="mini" icon="el-icon-plus" @click="zoomIn" />
      </el-button-group>
      <el-button size="mini" icon="el-icon-upload" @click="$refs.bgInput.click()">底图</el-button>
      <input ref="bgInput" type="file" accept="image/*" style="display:none" @change="onBgUpload" />
    </div>
    <div
      class="canvas-scroll"
      ref="canvasScroll"
      @dragover.prevent
      @drop="onDrop"
    >
      <canvas ref="fabricCanvas"></canvas>
    </div>
  </div>
</template>

<script>
import { fabric } from 'fabric'

const CANVAS_W = 794
const CANVAS_H = 1123
const MAX_HISTORY = 30

export default {
  name: 'CanvasArea',
  props: {
    layoutJson: { type: [String, Object], default: null }
  },
  data() {
    return {
      canvas: null,
      zoom: 1,
      history: [],
      historyIndex: -1,
      ignoreHistory: false
    }
  },
  computed: {
    canUndo() { return this.historyIndex > 0 },
    canRedo() { return this.historyIndex < this.history.length - 1 }
  },
  mounted() {
    this.initCanvas()
    if (this.layoutJson) {
      this.$nextTick(() => this.loadLayout(this.layoutJson))
    }
  },
  beforeDestroy() {
    if (this.canvas) {
      this.canvas.dispose()
      this.canvas = null
    }
  },
  methods: {
    initCanvas() {
      const c = new fabric.Canvas(this.$refs.fabricCanvas, {
        width: CANVAS_W,
        height: CANVAS_H,
        backgroundColor: '#ffffff',
        preserveObjectStacking: true,
        selection: true
      })

      c.on('selection:created', (e) => this.onSelect(e))
      c.on('selection:updated', (e) => this.onSelect(e))
      c.on('selection:cleared', () => this.$emit('select', null))
      c.on('object:modified', () => {
        this.saveHistory()
        this.$emit('objectModified')
      })
      c.on('object:added', () => {
        if (!this.ignoreHistory) this.saveHistory()
      })
      c.on('object:removed', () => {
        if (!this.ignoreHistory) this.saveHistory()
      })

      // Keyboard shortcuts
      document.addEventListener('keydown', this.onKeyDown)

      this.canvas = c
      this.fitZoom()
      this.saveHistory()
    },

    onSelect(e) {
      const target = e.selected && e.selected.length === 1 ? e.selected[0] : null
      this.$emit('select', target)
    },

    // ==================== Drag & Drop from element panel ====================
    onDrop(event) {
      event.preventDefault()
      const raw = event.dataTransfer.getData('application/cert-element')
      if (!raw) return
      const data = JSON.parse(raw)
      const pointer = this.canvas.getPointer(event)
      this.addElement(data, pointer)
    },

    addElement(data, pointer) {
      const left = pointer ? pointer.x : CANVAS_W / 2
      const top = pointer ? pointer.y : CANVAS_H / 2

      switch (data.type) {
        case 'staticText':
          this.addText('双击编辑文本', left, top, {})
          break
        case 'variable':
          if (data.varKey === 'qrcode') {
            this.addQrcodePlaceholder(left, top)
          } else {
            this.addText('{{' + data.varKey + '}}', left, top, {
              certVarKey: data.varKey,
              certVarLabel: data.varLabel
            })
          }
          break
        case 'image':
          this.promptImageUpload(left, top)
          break
        case 'rect':
          this.addRect(left, top)
          break
        case 'line':
          this.addLine(left, top)
          break
      }
    },

    addText(content, left, top, extraData) {
      const text = new fabric.IText(content, {
        left, top,
        fontFamily: 'SimHei',
        fontSize: 24,
        fill: '#333333',
        ...extraData
      })
      this.canvas.add(text)
      this.canvas.setActiveObject(text)
      this.canvas.renderAll()
    },

    addQrcodePlaceholder(left, top) {
      const group = new fabric.Group([
        new fabric.Rect({ width: 80, height: 80, fill: '#f0f0f0', stroke: '#ccc', strokeWidth: 1 }),
        new fabric.Text('QR', { fontSize: 20, fill: '#999', originX: 'center', originY: 'center', left: 40, top: 40 })
      ], {
        left, top,
        certVarKey: 'qrcode',
        certVarLabel: '验证二维码'
      })
      this.canvas.add(group)
      this.canvas.setActiveObject(group)
      this.canvas.renderAll()
    },

    addRect(left, top) {
      const rect = new fabric.Rect({
        left, top,
        width: 150, height: 80,
        fill: 'transparent',
        stroke: '#333333',
        strokeWidth: 2
      })
      this.canvas.add(rect)
      this.canvas.setActiveObject(rect)
      this.canvas.renderAll()
    },

    addLine(left, top) {
      const line = new fabric.Line([left, top, left + 200, top], {
        stroke: '#333333',
        strokeWidth: 2
      })
      this.canvas.add(line)
      this.canvas.setActiveObject(line)
      this.canvas.renderAll()
    },

    promptImageUpload(left, top) {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = 'image/*'
      input.onchange = (e) => {
        const file = e.target.files[0]
        if (!file) return
        const reader = new FileReader()
        reader.onload = (ev) => {
          fabric.Image.fromURL(ev.target.result, (img) => {
            const maxW = 200
            if (img.width > maxW) {
              img.scaleToWidth(maxW)
            }
            img.set({ left, top })
            this.canvas.add(img)
            this.canvas.setActiveObject(img)
            this.canvas.renderAll()
          })
        }
        reader.readAsDataURL(file)
      }
      input.click()
    },

    // ==================== Background Image ====================
    onBgUpload(event) {
      const file = event.target.files[0]
      if (!file) return
      const reader = new FileReader()
      reader.onload = (ev) => {
        fabric.Image.fromURL(ev.target.result, (img) => {
          const scaleX = CANVAS_W / img.width
          const scaleY = CANVAS_H / img.height
          this.canvas.setBackgroundImage(img, this.canvas.renderAll.bind(this.canvas), {
            scaleX, scaleY
          })
          this.saveHistory()
        })
      }
      reader.readAsDataURL(file)
      this.$refs.bgInput.value = ''
    },

    setBgFromUrl(url) {
      if (!url) return
      fabric.Image.fromURL(url, (img) => {
        const scaleX = CANVAS_W / img.width
        const scaleY = CANVAS_H / img.height
        this.canvas.setBackgroundImage(img, this.canvas.renderAll.bind(this.canvas), {
          scaleX, scaleY
        })
      }, { crossOrigin: 'anonymous' })
    },

    // ==================== Zoom ====================
    fitZoom() {
      if (!this.$refs.canvasScroll) return
      const wrapW = this.$refs.canvasScroll.clientWidth - 40
      const wrapH = this.$refs.canvasScroll.clientHeight - 40
      const scale = Math.min(wrapW / CANVAS_W, wrapH / CANVAS_H, 1)
      this.setZoom(scale)
    },
    zoomIn() { this.setZoom(Math.min(this.zoom + 0.1, 3)) },
    zoomOut() { this.setZoom(Math.max(this.zoom - 0.1, 0.2)) },
    zoomReset() { this.fitZoom() },
    setZoom(z) {
      this.zoom = z
      if (this.canvas) {
        this.canvas.setZoom(z)
        this.canvas.setWidth(CANVAS_W * z)
        this.canvas.setHeight(CANVAS_H * z)
        this.canvas.renderAll()
      }
    },

    // ==================== History (Undo/Redo) ====================
    saveHistory() {
      const json = JSON.stringify(this.canvas.toJSON([
        'certVarKey', 'certVarLabel'
      ]))
      if (this.historyIndex < this.history.length - 1) {
        this.history = this.history.slice(0, this.historyIndex + 1)
      }
      this.history.push(json)
      if (this.history.length > MAX_HISTORY) {
        this.history.shift()
      }
      this.historyIndex = this.history.length - 1
    },
    undo() {
      if (!this.canUndo) return
      this.historyIndex--
      this.restoreHistory()
    },
    redo() {
      if (!this.canRedo) return
      this.historyIndex++
      this.restoreHistory()
    },
    restoreHistory() {
      this.ignoreHistory = true
      const json = this.history[this.historyIndex]
      this.canvas.loadFromJSON(json, () => {
        this.canvas.renderAll()
        this.ignoreHistory = false
        this.$emit('select', null)
      })
    },

    // ==================== Keyboard ====================
    onKeyDown(e) {
      if (!this.canvas) return
      // Don't capture when typing in inputs
      if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return

      if ((e.ctrlKey || e.metaKey) && e.key === 'z') {
        e.preventDefault()
        if (e.shiftKey) { this.redo() } else { this.undo() }
      }
      if (e.key === 'Delete' || e.key === 'Backspace') {
        const active = this.canvas.getActiveObject()
        if (active && !active.isEditing) {
          e.preventDefault()
          this.canvas.remove(active)
          this.canvas.renderAll()
        }
      }
    },

    // ==================== Layer Operations ====================
    layerOp(op) {
      const active = this.canvas.getActiveObject()
      if (!active) return
      switch (op) {
        case 'bringForward': this.canvas.bringForward(active); break
        case 'sendBackwards': this.canvas.sendBackwards(active); break
        case 'bringToFront': this.canvas.bringToFront(active); break
        case 'sendToBack': this.canvas.sendToBack(active); break
      }
      this.canvas.renderAll()
      this.saveHistory()
    },

    deleteActive() {
      const active = this.canvas.getActiveObject()
      if (!active) return
      this.canvas.remove(active)
      this.canvas.discardActiveObject()
      this.canvas.renderAll()
    },

    // ==================== Serialize ====================
    toJSON() {
      if (!this.canvas) return '{}'
      return JSON.stringify(this.canvas.toJSON([
        'certVarKey', 'certVarLabel'
      ]))
    },

    toDataURL(opts) {
      if (!this.canvas) return ''
      const z = this.canvas.getZoom()
      this.canvas.setZoom(1)
      this.canvas.setWidth(CANVAS_W)
      this.canvas.setHeight(CANVAS_H)
      const url = this.canvas.toDataURL({ format: 'png', quality: 0.8, ...opts })
      this.canvas.setZoom(z)
      this.canvas.setWidth(CANVAS_W * z)
      this.canvas.setHeight(CANVAS_H * z)
      return url
    },

    loadLayout(json) {
      if (!this.canvas) return
      const data = typeof json === 'string' ? json : JSON.stringify(json)
      if (!data || data === '{}' || data === 'null') return
      this.ignoreHistory = true
      this.canvas.loadFromJSON(data, () => {
        this.canvas.renderAll()
        this.ignoreHistory = false
        this.history = [data]
        this.historyIndex = 0
      })
    },

    renderPreview(sampleData) {
      const json = this.toJSON()
      const parsed = JSON.parse(json)
      if (parsed.objects) {
        parsed.objects.forEach(obj => {
          if (obj.certVarKey && obj.text) {
            const val = sampleData[obj.certVarKey]
            if (val !== undefined) {
              obj.text = val
            }
          }
        })
      }
      return parsed
    },

    refreshCanvas() {
      if (this.canvas) {
        this.canvas.renderAll()
      }
    }
  }
}
</script>

<style scoped>
.cert-canvas-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}
.canvas-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
  flex-wrap: wrap;
}
.zoom-label {
  font-size: 12px;
  color: #909399;
  min-width: 40px;
  text-align: center;
}
.canvas-scroll {
  flex: 1;
  overflow: auto;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 20px;
  background: #e8eaed;
}
.canvas-scroll canvas {
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
}
@media (max-width: 768px) {
  .canvas-scroll {
    padding: 8px;
  }
  .canvas-toolbar {
    gap: 6px;
    padding: 6px 8px;
  }
}
</style>
