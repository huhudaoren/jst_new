<template>
  <div class="cert-property-panel">
    <div class="panel-title">属性面板</div>

    <template v-if="activeObject">
      <div class="prop-section">
        <div class="section-label">基础属性</div>
        <el-form label-width="56px" size="mini">
          <el-form-item label="X">
            <el-input-number v-model="props.left" :step="1" controls-position="right" class="full-width" @change="applyProp('left')" />
          </el-form-item>
          <el-form-item label="Y">
            <el-input-number v-model="props.top" :step="1" controls-position="right" class="full-width" @change="applyProp('top')" />
          </el-form-item>
          <el-form-item label="宽度">
            <el-input-number v-model="props.width" :min="10" :step="1" controls-position="right" class="full-width" @change="applyScaleX" />
          </el-form-item>
          <el-form-item label="高度">
            <el-input-number v-model="props.height" :min="10" :step="1" controls-position="right" class="full-width" @change="applyScaleY" />
          </el-form-item>
          <el-form-item label="旋转">
            <el-input-number v-model="props.angle" :min="-360" :max="360" :step="1" controls-position="right" class="full-width" @change="applyProp('angle')" />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="isText" class="prop-section">
        <div class="section-label">文本属性</div>
        <el-form label-width="56px" size="mini">
          <el-form-item label="内容">
            <el-input v-model="props.text" type="textarea" :rows="2" @change="applyProp('text')" />
          </el-form-item>
          <el-form-item label="字体">
            <el-select v-model="props.fontFamily" class="full-width" @change="applyProp('fontFamily')">
              <el-option v-for="f in fontOptions" :key="f" :label="f" :value="f" />
            </el-select>
          </el-form-item>
          <el-form-item label="字号">
            <el-input-number v-model="props.fontSize" :min="8" :max="200" :step="1" controls-position="right" class="full-width" @change="applyProp('fontSize')" />
          </el-form-item>
          <el-form-item label="颜色">
            <el-color-picker v-model="props.fill" @change="applyProp('fill')" />
          </el-form-item>
          <el-form-item label="粗体">
            <el-switch v-model="props.bold" @change="applyBold" />
          </el-form-item>
          <el-form-item label="对齐">
            <el-radio-group v-model="props.textAlign" size="mini" @change="applyProp('textAlign')">
              <el-radio-button label="left">左</el-radio-button>
              <el-radio-button label="center">中</el-radio-button>
              <el-radio-button label="right">右</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="isRect" class="prop-section">
        <div class="section-label">形状属性</div>
        <el-form label-width="56px" size="mini">
          <el-form-item label="填充">
            <el-color-picker v-model="props.fill" show-alpha @change="applyProp('fill')" />
          </el-form-item>
          <el-form-item label="边框色">
            <el-color-picker v-model="props.stroke" @change="applyProp('stroke')" />
          </el-form-item>
          <el-form-item label="边框宽">
            <el-input-number v-model="props.strokeWidth" :min="0" :max="20" :step="1" controls-position="right" class="full-width" @change="applyProp('strokeWidth')" />
          </el-form-item>
        </el-form>
      </div>

      <div class="prop-section">
        <div class="section-label">图层操作</div>
        <div class="layer-actions">
          <el-button size="mini" icon="el-icon-top" @click="$emit('layer', 'bringForward')">上移</el-button>
          <el-button size="mini" icon="el-icon-bottom" @click="$emit('layer', 'sendBackwards')">下移</el-button>
          <el-button size="mini" icon="el-icon-upload2" @click="$emit('layer', 'bringToFront')">置顶</el-button>
          <el-button size="mini" icon="el-icon-download" @click="$emit('layer', 'sendToBack')">置底</el-button>
        </div>
        <el-button type="danger" plain size="mini" icon="el-icon-delete" class="delete-btn" @click="$emit('delete')">删除元素</el-button>
      </div>
    </template>

    <div v-else class="empty-hint">
      <i class="el-icon-mouse" />
      <p>选中画布上的元素后，在此编辑属性</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PropertyPanel',
  props: {
    activeObject: { type: Object, default: null }
  },
  data() {
    return {
      fontOptions: [
        'SimSun', 'SimHei', 'Microsoft YaHei', 'KaiTi', 'FangSong',
        'Arial', 'Times New Roman', 'Georgia', 'Verdana'
      ],
      props: this.defaultProps()
    }
  },
  computed: {
    isText() {
      if (!this.activeObject) return false
      return this.activeObject.type === 'text' || this.activeObject.type === 'i-text'
    },
    isRect() {
      if (!this.activeObject) return false
      return this.activeObject.type === 'rect'
    }
  },
  watch: {
    activeObject: {
      handler(obj) {
        if (!obj) {
          this.props = this.defaultProps()
          return
        }
        this.syncFromObject(obj)
      },
      immediate: true
    }
  },
  methods: {
    defaultProps() {
      return {
        left: 0, top: 0, width: 100, height: 40, angle: 0,
        text: '', fontFamily: 'SimHei', fontSize: 24, fill: '#333333',
        bold: false, textAlign: 'left',
        stroke: '#000000', strokeWidth: 1
      }
    },
    syncFromObject(obj) {
      this.props.left = Math.round(obj.left || 0)
      this.props.top = Math.round(obj.top || 0)
      this.props.width = Math.round((obj.width || 100) * (obj.scaleX || 1))
      this.props.height = Math.round((obj.height || 40) * (obj.scaleY || 1))
      this.props.angle = Math.round(obj.angle || 0)
      if (this.isText) {
        this.props.text = obj.text || ''
        this.props.fontFamily = obj.fontFamily || 'SimHei'
        this.props.fontSize = obj.fontSize || 24
        this.props.fill = obj.fill || '#333333'
        this.props.bold = obj.fontWeight === 'bold'
        this.props.textAlign = obj.textAlign || 'left'
      }
      if (this.isRect) {
        this.props.fill = obj.fill || 'transparent'
        this.props.stroke = obj.stroke || '#000000'
        this.props.strokeWidth = obj.strokeWidth || 1
      }
    },
    applyProp(key) {
      if (!this.activeObject) return
      this.activeObject.set(key, this.props[key])
      this.activeObject.setCoords()
      this.$emit('modified')
    },
    applyScaleX() {
      if (!this.activeObject) return
      const origW = this.activeObject.width || 1
      this.activeObject.set('scaleX', this.props.width / origW)
      this.activeObject.setCoords()
      this.$emit('modified')
    },
    applyScaleY() {
      if (!this.activeObject) return
      const origH = this.activeObject.height || 1
      this.activeObject.set('scaleY', this.props.height / origH)
      this.activeObject.setCoords()
      this.$emit('modified')
    },
    applyBold() {
      if (!this.activeObject) return
      this.activeObject.set('fontWeight', this.props.bold ? 'bold' : 'normal')
      this.$emit('modified')
    },
    refresh() {
      if (this.activeObject) {
        this.syncFromObject(this.activeObject)
      }
    }
  }
}
</script>

<style scoped>
.cert-property-panel {
  width: 280px;
  min-width: 280px;
  border-left: 1px solid #e4e7ed;
  padding: 12px;
  overflow-y: auto;
  background: #fafbfc;
}
.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.prop-section {
  margin-bottom: 16px;
}
.section-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.full-width {
  width: 100%;
}
.layer-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
}
.delete-btn {
  width: 100%;
  margin-top: 4px;
}
.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  color: #c0c4cc;
  text-align: center;
}
.empty-hint i {
  font-size: 36px;
  margin-bottom: 12px;
}
.empty-hint p {
  font-size: 13px;
}
@media (max-width: 768px) {
  .cert-property-panel {
    width: 100%;
    min-width: 0;
    border-left: none;
    border-top: 1px solid #e4e7ed;
    max-height: 260px;
  }
}
</style>
