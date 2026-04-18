<template>
  <div class="message-template-editor">
    <div class="editor-head">
      <div>
        <div class="editor-title">模板可视化编辑</div>
        <div class="editor-desc">变量插入、占位预览和示例渲染统一在这里完成。</div>
      </div>
      <div class="editor-head__meta">
        <el-tag size="mini" type="info">{{ sceneLabel }}</el-tag>
        <el-tag size="mini" effect="plain">{{ channelLabel }}</el-tag>
      </div>
    </div>

    <el-alert
      v-if="!sceneMeta.variables.length"
      title="当前场景还没有配置变量清单，仍可直接编辑模板原文。"
      type="warning"
      :closable="false"
      show-icon
      class="editor-alert"
    />

    <div class="variable-panel">
      <div class="panel-label">可用变量</div>
      <div v-if="sceneMeta.variables.length" class="variable-chip-list">
        <el-button
          v-for="item in sceneMeta.variables"
          :key="item.key"
          size="mini"
          round
          @click="insertVariable(item.key)"
        >
          {{ item.label }} · {{ tokenOf(item.key) }}
        </el-button>
      </div>
      <div v-else class="panel-empty">切换到已配置变量的业务场景后，这里会展示可插入占位符。</div>
    </div>

    <div class="editor-grid">
      <div class="editor-main">
        <div class="panel-label">模板正文</div>
        <el-input
          ref="visualInput"
          :value="value"
          type="textarea"
          :rows="isMobile ? 6 : 7"
          placeholder="请输入模板内容，支持 ${变量名} 占位符"
          @input="handleInput"
          @click.native="captureCursor"
          @keyup.native="captureCursor"
        />
        <div class="editor-tip">点击上方变量按钮可按光标位置插入，占位语法保持 ${var} 不变。</div>
      </div>

      <div class="editor-preview">
        <div class="preview-card">
          <div class="panel-label">占位预览</div>
          <div v-if="segments.length" class="token-preview">
            <template v-for="(segment, index) in segments">
              <el-tag v-if="segment.isVar" :key="'token-' + index" size="mini" type="warning" class="token-tag">
                {{ segment.text }}
              </el-tag>
              <span v-else :key="'text-' + index">{{ segment.text }}</span>
            </template>
          </div>
          <div v-else class="panel-empty">开始输入后，这里会高亮模板中的变量占位符。</div>
        </div>

        <div class="preview-card">
          <div class="panel-label">示例效果</div>
          <div v-if="renderedPreview" class="render-preview">{{ renderedPreview }}</div>
          <div v-else class="panel-empty">示例数据替换结果会显示在这里。</div>
        </div>
      </div>
    </div>

    <div class="advanced-toggle">
      <el-button type="text" @click="advancedVisible = !advancedVisible">
        {{ advancedVisible ? '收起高级 raw' : '高级 raw' }}
      </el-button>
      <span class="advanced-hint">保留原始 textarea 兜底入口，便于快速粘贴和排查。</span>
    </div>
    <el-collapse-transition>
      <div v-show="advancedVisible" class="advanced-panel">
        <el-input
          :value="value"
          type="textarea"
          :rows="5"
          placeholder="原始模板文本"
          @input="handleInput"
        />
      </div>
    </el-collapse-transition>
  </div>
</template>

<script>
import { getSceneVariableMeta, insertVariableAtCursor, parseTemplateVars, renderTemplatePreview } from './variable-map'

const CHANNEL_LABEL_MAP = {
  inner: '站内信',
  sms: '短信',
  wechat_template: '微信模板消息',
  wechat: '微信订阅消息'
}

export default {
  name: 'JstMessageTemplateEditor',
  props: {
    value: {
      type: String,
      default: ''
    },
    scene: {
      type: String,
      default: ''
    },
    channel: {
      type: String,
      default: ''
    },
    sceneOptions: {
      type: Array,
      default() {
        return []
      }
    },
    isMobile: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      advancedVisible: false,
      selectionStart: null,
      selectionEnd: null
    }
  },
  computed: {
    sceneMeta() {
      return getSceneVariableMeta(this.scene)
    },
    segments() {
      return parseTemplateVars(this.value)
    },
    renderedPreview() {
      return renderTemplatePreview(this.value, this.sceneMeta.demoValues)
    },
    sceneLabel() {
      const match = (this.sceneOptions || []).find(item => item.value === this.scene)
      return match ? match.label : (this.sceneMeta.label || this.scene || '未选择场景')
    },
    channelLabel() {
      return CHANNEL_LABEL_MAP[this.channel] || this.channel || '未选择通道'
    }
  },
  methods: {
    handleInput(nextValue) {
      this.$emit('input', nextValue)
    },
    tokenOf(key) {
      return '${' + key + '}'
    },
    captureCursor() {
      this.$nextTick(() => {
        const textarea = this.getTextarea()
        if (!textarea) return
        this.selectionStart = textarea.selectionStart
        this.selectionEnd = textarea.selectionEnd
      })
    },
    getTextarea() {
      return this.$refs.visualInput && this.$refs.visualInput.$refs
        ? this.$refs.visualInput.$refs.textarea
        : null
    },
    insertVariable(key) {
      const token = this.tokenOf(key)
      const textarea = this.getTextarea()
      const start = textarea ? textarea.selectionStart : this.selectionStart
      const end = textarea ? textarea.selectionEnd : this.selectionEnd
      const result = insertVariableAtCursor(this.value, token, start, end)
      this.$emit('input', result.value)
      this.$nextTick(() => {
        const nextTextarea = this.getTextarea()
        if (nextTextarea) {
          nextTextarea.focus()
          nextTextarea.setSelectionRange(result.selectionStart, result.selectionEnd)
          this.selectionStart = result.selectionStart
          this.selectionEnd = result.selectionEnd
        }
      })
    }
  }
}
</script>

<style scoped>
.message-template-editor {
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #d9e2f1;
  border-radius: 12px;
  background: linear-gradient(180deg, #fbfdff 0%, #f6f8fc 100%);
}

.editor-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.editor-head__meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.editor-title {
  font-size: 15px;
  font-weight: 600;
  color: #172033;
}

.editor-desc {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.editor-alert {
  margin-bottom: 12px;
}

.variable-panel,
.preview-card,
.advanced-panel,
.editor-main {
  padding: 14px;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  background: #fff;
}

.panel-label {
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  letter-spacing: .04em;
}

.variable-chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.editor-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(260px, .95fr);
  gap: 12px;
  margin-top: 12px;
}

.editor-preview {
  display: grid;
  gap: 12px;
}

.editor-tip,
.advanced-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
}

.token-preview,
.render-preview,
.panel-empty {
  min-height: 54px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.token-tag {
  margin: 0 4px 4px 0;
  vertical-align: baseline;
}

.advanced-toggle {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.advanced-panel {
  margin-top: 8px;
}

@media (max-width: 768px) {
  .message-template-editor {
    padding: 14px;
  }

  .editor-head,
  .advanced-toggle {
    display: block;
  }

  .editor-head__meta {
    margin-top: 10px;
    justify-content: flex-start;
  }

  .editor-grid {
    grid-template-columns: 1fr;
  }

  .advanced-hint {
    display: block;
    margin-top: 4px;
  }
}
</style>
