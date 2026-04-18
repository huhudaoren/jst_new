<template>
  <div class="threshold-editor">
    <div class="editor-head">
      <div>
        <div class="editor-title">阈值可视化编辑</div>
        <div class="editor-desc">仅生成扁平 JSON，继续兼容现有 thresholdSummary() 摘要逻辑。</div>
      </div>
      <el-tag size="mini" type="info">{{ ruleType || '未选择规则类型' }}</el-tag>
    </div>

    <div v-if="isMobile" class="mode-panel">
      <div class="panel-label">阈值模式</div>
      <el-select v-model="mode" class="full-width" @change="handleModeChange">
        <el-option v-for="item in modeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>
    <div v-else class="mode-panel">
      <div class="panel-label">阈值模式</div>
      <el-radio-group v-model="mode" size="small" @change="handleModeChange">
        <el-radio-button v-for="item in modeOptions" :key="item.value" :label="item.value">
          {{ item.label }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <el-alert
      v-if="fallbackReason"
      :title="fallbackReason"
      type="warning"
      :closable="false"
      show-icon
      class="editor-alert"
    />

    <div v-if="mode === 'frequency'" class="visual-card">
      <el-row :gutter="12">
        <el-col :xs="24" :sm="12">
          <el-form-item label="时间窗口(分)">
            <el-input-number v-model="visualForm.window_minutes" :min="1" :controls-position="isMobile ? '' : 'right'" class="full-width" @change="emitVisualValue" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="最大次数">
            <el-input-number v-model="visualForm.max_count" :min="1" :controls-position="isMobile ? '' : 'right'" class="full-width" @change="emitVisualValue" />
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <div v-else-if="mode === 'amount'" class="visual-card">
      <el-row :gutter="12">
        <el-col :xs="24" :sm="12">
          <el-form-item label="时间窗口(分)">
            <el-input-number v-model="visualForm.window_minutes" :min="1" :controls-position="isMobile ? '' : 'right'" class="full-width" @change="emitVisualValue" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12">
          <el-form-item label="阈值金额">
            <el-input-number v-model="visualForm.threshold_amount" :min="0" :precision="2" :controls-position="isMobile ? '' : 'right'" class="full-width" @change="emitVisualValue" />
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <div v-else-if="mode === 'list'" class="visual-card">
      <el-form-item label="名单类型">
        <el-select v-model="visualForm.list_type" class="full-width" placeholder="请选择" @change="emitVisualValue">
          <el-option label="黑名单" value="blacklist" />
          <el-option label="白名单" value="whitelist" />
          <el-option label="灰名单" value="graylist" />
        </el-select>
      </el-form-item>
    </div>

    <div class="advanced-toggle">
      <el-button type="text" @click="advancedVisible = !advancedVisible">
        {{ advancedVisible ? '收起高级 raw' : '高级 raw' }}
      </el-button>
      <span class="advanced-hint">保留原始 textarea 入口，支持直接粘贴历史 JSON。</span>
    </div>
    <el-collapse-transition>
      <div v-show="advancedVisible || mode === 'raw'" class="advanced-panel">
        <el-input
          :value="rawText"
          type="textarea"
          :rows="5"
          placeholder='JSON 格式，如 {"window_minutes":1,"max_count":3}'
          @input="handleRawInput"
        />
      </div>
    </el-collapse-transition>
  </div>
</template>

<script>
const MODE_OPTIONS = [
  { value: 'frequency', label: '频次' },
  { value: 'amount', label: '金额' },
  { value: 'list', label: '名单' },
  { value: 'raw', label: '高级 raw' }
]

const EMPTY_VISUAL_FORM = () => ({
  window_minutes: null,
  max_count: null,
  threshold_amount: null,
  list_type: ''
})

export default {
  name: 'JstThresholdEditor',
  props: {
    value: {
      type: String,
      default: ''
    },
    ruleType: {
      type: String,
      default: ''
    },
    isMobile: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      mode: 'frequency',
      rawText: '',
      advancedVisible: false,
      fallbackReason: '',
      visualForm: EMPTY_VISUAL_FORM()
    }
  },
  computed: {
    modeOptions() {
      return MODE_OPTIONS
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(nextValue) {
        this.syncFromValue(nextValue)
      }
    },
    ruleType(nextRuleType, prevRuleType) {
      if (!this.value && nextRuleType !== prevRuleType && this.mode !== 'raw') {
        this.mode = this.defaultMode(nextRuleType)
      }
    }
  },
  methods: {
    defaultMode(ruleType) {
      if ((ruleType || '').indexOf('freq') >= 0) return 'frequency'
      return 'amount'
    },
    handleModeChange(nextMode) {
      this.fallbackReason = nextMode === 'raw' ? this.fallbackReason : ''
      if (nextMode === 'raw') {
        this.$emit('fallback-change', { rawMode: true, reason: this.fallbackReason || '手动切换到高级 raw' })
        return
      }
      if (!this.rawText) {
        this.visualForm = EMPTY_VISUAL_FORM()
      }
      this.emitVisualValue()
    },
    handleRawInput(nextValue) {
      this.rawText = nextValue
      this.$emit('input', nextValue)
      this.syncFromValue(nextValue, true)
    },
    syncFromValue(nextValue, fromRawInput = false) {
      this.rawText = nextValue || ''
      if (!nextValue) {
        this.fallbackReason = ''
        this.visualForm = EMPTY_VISUAL_FORM()
        if (!fromRawInput) {
          this.mode = this.defaultMode(this.ruleType)
        }
        this.$emit('fallback-change', { rawMode: false, reason: '' })
        return
      }
      let parsed = null
      try {
        parsed = JSON.parse(nextValue)
      } catch (error) {
        this.mode = 'raw'
        this.fallbackReason = '阈值 JSON 暂未闭合，已自动切换为高级 raw 模式。'
        this.$emit('fallback-change', { rawMode: true, reason: this.fallbackReason })
        return
      }
      if (!parsed || Array.isArray(parsed) || typeof parsed !== 'object') {
        this.mode = 'raw'
        this.fallbackReason = '仅支持顶层对象结构，当前内容已回落到高级 raw 模式。'
        this.$emit('fallback-change', { rawMode: true, reason: this.fallbackReason })
        return
      }
      const keys = Object.keys(parsed)
      const hasNestedValue = keys.some(key => parsed[key] !== null && typeof parsed[key] === 'object')
      if (hasNestedValue) {
        this.mode = 'raw'
        this.fallbackReason = '检测到嵌套结构，已回落到高级 raw 模式并保留原文。'
        this.$emit('fallback-change', { rawMode: true, reason: this.fallbackReason })
        return
      }
      const detectedMode = this.detectMode(parsed)
      if (!detectedMode) {
        this.mode = 'raw'
        this.fallbackReason = '检测到未覆盖的阈值字段，已回落到高级 raw 模式并保留原文。'
        this.$emit('fallback-change', { rawMode: true, reason: this.fallbackReason })
        return
      }
      this.mode = detectedMode
      this.fallbackReason = ''
      this.visualForm = {
        window_minutes: parsed.window_minutes != null ? Number(parsed.window_minutes) : null,
        max_count: parsed.max_count != null ? Number(parsed.max_count) : null,
        threshold_amount: parsed.threshold_amount != null ? Number(parsed.threshold_amount) : null,
        list_type: parsed.list_type || ''
      }
      this.$emit('fallback-change', { rawMode: false, reason: '' })
    },
    detectMode(parsed) {
      const keys = Object.keys(parsed).sort()
      const normalized = keys.join('|')
      if (normalized === 'max_count|window_minutes' || normalized === 'max_count' || normalized === 'window_minutes') {
        return 'frequency'
      }
      if (normalized === 'threshold_amount|window_minutes' || normalized === 'threshold_amount' || normalized === 'window_minutes') {
        return parsed.threshold_amount != null ? 'amount' : this.defaultMode(this.ruleType)
      }
      if (normalized === 'list_type') {
        return 'list'
      }
      return ''
    },
    emitVisualValue() {
      if (this.mode === 'raw') return
      const payload = {}
      if (this.visualForm.window_minutes != null && this.visualForm.window_minutes !== '') {
        payload.window_minutes = Number(this.visualForm.window_minutes)
      }
      if (this.mode === 'frequency' && this.visualForm.max_count != null && this.visualForm.max_count !== '') {
        payload.max_count = Number(this.visualForm.max_count)
      }
      if (this.mode === 'amount' && this.visualForm.threshold_amount != null && this.visualForm.threshold_amount !== '') {
        payload.threshold_amount = Number(this.visualForm.threshold_amount)
      }
      if (this.mode === 'list' && this.visualForm.list_type) {
        payload.list_type = this.visualForm.list_type
      }
      const nextValue = Object.keys(payload).length ? JSON.stringify(payload) : ''
      this.rawText = nextValue
      this.$emit('input', nextValue)
      this.$emit('fallback-change', { rawMode: false, reason: '' })
    }
  }
}
</script>

<style scoped>
.threshold-editor {
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

.mode-panel,
.visual-card,
.advanced-panel {
  padding: 14px;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  background: #fff;
}

.editor-alert,
.visual-card,
.advanced-toggle {
  margin-top: 12px;
}

.panel-label {
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  letter-spacing: .04em;
}

.advanced-toggle {
  display: flex;
  align-items: center;
  gap: 12px;
}

.advanced-hint {
  font-size: 12px;
  color: #6b7280;
}

.advanced-panel {
  margin-top: 8px;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .threshold-editor {
    padding: 14px;
  }

  .editor-head,
  .advanced-toggle {
    display: block;
  }

  .advanced-hint {
    display: block;
    margin-top: 4px;
  }
}
</style>
