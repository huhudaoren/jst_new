<template>
  <div class="cert-designer">
    <!-- Header bar -->
    <div class="designer-header">
      <span class="designer-title">{{ title }}</span>
      <div class="header-actions">
        <el-button size="small" icon="el-icon-view" @click="showPreview">预览</el-button>
        <el-button size="small" type="primary" icon="el-icon-check" :loading="saving" @click="handleSave">保存</el-button>
      </div>
    </div>

    <!-- Three-column layout -->
    <div class="designer-body">
      <ElementPanel :owner-type="ownerType" />
      <CanvasArea
        ref="canvasArea"
        :layout-json="layoutJson"
        @select="onObjectSelect"
        @objectModified="onObjectModified"
      />
      <PropertyPanel
        ref="propPanel"
        :active-object="activeObject"
        @modified="onPropModified"
        @layer="onLayerOp"
        @delete="onDelete"
      />
    </div>

    <!-- Preview Dialog -->
    <el-dialog
      title="证书预览"
      :visible.sync="previewVisible"
      width="820px"
      append-to-body
      class="preview-dialog"
    >
      <div class="preview-sample-bar">
        <span class="preview-hint">以下为示例数据，实际生成时将替换为真实信息</span>
      </div>
      <div class="preview-canvas-wrap">
        <canvas ref="previewCanvas"></canvas>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fabric } from 'fabric'
import ElementPanel from './ElementPanel.vue'
import CanvasArea from './CanvasArea.vue'
import PropertyPanel from './PropertyPanel.vue'

const CANVAS_W = 794
const CANVAS_H = 1123

const CONTEST_SAMPLE = {
  name: '张小明',
  contestName: '2026 年全国青少年编程竞赛',
  awardName: '一等奖',
  score: '96.5',
  certNo: 'CERT-2026-000001',
  date: '2026 年 4 月 15 日',
  qrcode: 'QR',
  school: '北京市海淀区实验中学',
  groupLevel: '初中组'
}

const CHANNEL_SAMPLE = {
  channelName: '优学教育科技有限公司',
  authNo: 'AUTH-2026-001',
  authDate: '2026 年 4 月 15 日',
  scope: '华北地区赛事推广'
}

export default {
  name: 'CertDesigner',
  components: { ElementPanel, CanvasArea, PropertyPanel },
  props: {
    layoutJson: { type: [String, Object], default: null },
    bgImage: { type: String, default: '' },
    ownerType: { type: String, default: 'contest' },
    title: { type: String, default: '证书模板设计器' }
  },
  data() {
    return {
      activeObject: null,
      saving: false,
      previewVisible: false,
      previewCanvasInstance: null
    }
  },
  watch: {
    bgImage: {
      handler(val) {
        if (val && this.$refs.canvasArea) {
          this.$refs.canvasArea.setBgFromUrl(val)
        }
      },
      immediate: false
    }
  },
  mounted() {
    if (this.bgImage) {
      this.$nextTick(() => {
        if (this.$refs.canvasArea) {
          this.$refs.canvasArea.setBgFromUrl(this.bgImage)
        }
      })
    }
  },
  beforeDestroy() {
    if (this.previewCanvasInstance) {
      this.previewCanvasInstance.dispose()
      this.previewCanvasInstance = null
    }
  },
  methods: {
    onObjectSelect(obj) {
      this.activeObject = obj
    },
    onObjectModified() {
      if (this.$refs.propPanel && this.activeObject) {
        this.$refs.propPanel.refresh()
      }
    },
    onPropModified() {
      if (this.$refs.canvasArea) {
        this.$refs.canvasArea.refreshCanvas()
        this.$refs.canvasArea.saveHistory()
      }
    },
    onLayerOp(op) {
      if (this.$refs.canvasArea) {
        this.$refs.canvasArea.layerOp(op)
      }
    },
    onDelete() {
      if (this.$refs.canvasArea) {
        this.$refs.canvasArea.deleteActive()
        this.activeObject = null
      }
    },

    handleSave() {
      const layoutJson = this.$refs.canvasArea.toJSON()
      const thumbnail = this.$refs.canvasArea.toDataURL({ quality: 0.5 })
      this.$emit('save', { layoutJson, thumbnail })
    },

    setSaving(val) {
      this.saving = val
    },

    getLayoutJson() {
      return this.$refs.canvasArea.toJSON()
    },

    getThumbnail() {
      return this.$refs.canvasArea.toDataURL({ quality: 0.5 })
    },

    showPreview() {
      this.previewVisible = true
      this.$nextTick(() => this.renderPreview())
    },

    renderPreview() {
      const sampleData = this.ownerType === 'channel' ? CHANNEL_SAMPLE : CONTEST_SAMPLE
      const previewJson = this.$refs.canvasArea.renderPreview(sampleData)

      if (this.previewCanvasInstance) {
        this.previewCanvasInstance.dispose()
      }

      const pCanvas = new fabric.StaticCanvas(this.$refs.previewCanvas, {
        width: CANVAS_W,
        height: CANVAS_H
      })
      this.previewCanvasInstance = pCanvas

      pCanvas.loadFromJSON(previewJson, () => {
        pCanvas.renderAll()
        // Fit within dialog
        const wrapEl = this.$refs.previewCanvas && this.$refs.previewCanvas.parentElement
        if (wrapEl) {
          const maxW = wrapEl.clientWidth - 20
          if (maxW < CANVAS_W) {
            const scale = maxW / CANVAS_W
            pCanvas.setZoom(scale)
            pCanvas.setWidth(CANVAS_W * scale)
            pCanvas.setHeight(CANVAS_H * scale)
            pCanvas.renderAll()
          }
        }
      })
    },

    loadLayout(json) {
      if (this.$refs.canvasArea) {
        this.$refs.canvasArea.loadLayout(json)
      }
    }
  }
}
</script>

<style scoped>
.cert-designer {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 500px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}
.designer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafbfc;
}
.designer-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.designer-body {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
.preview-sample-bar {
  padding: 8px 0;
  margin-bottom: 12px;
}
.preview-hint {
  font-size: 13px;
  color: #909399;
}
.preview-canvas-wrap {
  display: flex;
  justify-content: center;
  overflow: auto;
  background: #f5f5f5;
  border-radius: 4px;
  padding: 12px;
}
.preview-canvas-wrap canvas {
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
}
@media (max-width: 768px) {
  .designer-body {
    flex-direction: column;
  }
  .designer-header {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>
