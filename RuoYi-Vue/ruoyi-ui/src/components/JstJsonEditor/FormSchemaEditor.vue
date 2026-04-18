<template>
  <div class="form-schema-editor">
    <div class="editor-head">
      <div>
        <div class="editor-title">Schema 可视化编辑</div>
        <div class="editor-desc">字段排序、摘要、预览与高级 raw 统一收口，保存仍回写原 schemaJson。</div>
      </div>
      <div class="editor-head__meta">
        <el-tag size="mini" type="info">{{ summaryText }}</el-tag>
        <el-tag v-if="mobileReadonly" size="mini" effect="plain">手机端只读</el-tag>
      </div>
    </div>

    <el-alert
      v-if="mobileReadonly"
      title="手机端当前只提供结构查看，建议在 PC 打开后进行字段增删与排序；高级 raw 仍可编辑。"
      type="info"
      :closable="false"
      show-icon
      class="editor-alert"
    />
    <el-alert
      v-if="fallbackReason"
      :title="fallbackReason"
      type="warning"
      :closable="false"
      show-icon
      class="editor-alert"
    />
    <el-alert
      v-if="parseError"
      :title="parseError"
      type="error"
      :closable="false"
      show-icon
      class="editor-alert"
    />

    <div class="editor-toolbar">
      <div class="toolbar-actions">
        <el-button type="primary" size="mini" icon="el-icon-plus" :disabled="disableVisualEdit" @click="openCreateDrawer">新增字段</el-button>
        <el-button size="mini" icon="el-icon-view" :disabled="!canPreview" @click="previewVisible = true">预览表单</el-button>
      </div>
      <el-button type="text" @click="advancedVisible = !advancedVisible">
        {{ advancedVisible ? '收起高级 raw' : '高级 raw' }}
      </el-button>
    </div>

    <div v-if="canVisualize" class="field-board">
      <draggable
        v-model="localFields"
        handle=".drag-handle"
        ghost-class="field-card--ghost"
        :disabled="disableVisualEdit"
        @end="handleDragEnd"
      >
        <transition-group name="field-list" tag="div" class="field-list">
          <div v-for="(field, index) in localFields" :key="field.key || index" class="field-card">
            <div class="field-card__head">
              <div class="field-card__title-wrap">
                <i v-if="!disableVisualEdit" class="el-icon-rank drag-handle" />
                <div>
                  <div class="field-card__title">
                    {{ field.label || field.key || '未命名字段' }}
                    <el-tag v-if="field.required" size="mini" type="danger">必填</el-tag>
                  </div>
                  <div class="field-card__meta">{{ field.key || '--' }} · {{ typeLabel(field.type) }}</div>
                </div>
              </div>
              <div v-if="!disableVisualEdit" class="field-card__actions">
                <el-button type="text" size="mini" @click="openEditDrawer(index)">编辑</el-button>
                <el-button type="text" size="mini" class="danger-text" @click="removeField(index)">删除</el-button>
              </div>
            </div>
            <div v-if="field.placeholder" class="field-card__hint">占位：{{ field.placeholder }}</div>
            <div v-if="field.description" class="field-card__hint">说明：{{ field.description }}</div>
            <div v-if="isOptionType(field.type)" class="field-card__options">
              <el-tag v-for="option in normalizedOptions(field)" :key="option.value" size="mini" effect="plain">
                {{ option.label }}
              </el-tag>
              <span v-if="!normalizedOptions(field).length" class="field-card__empty">未配置选项</span>
            </div>
            <div v-if="isUploadType(field.type)" class="field-card__hint">
              {{ field.accept ? '支持 ' + field.accept : '未限制格式' }}，最多 {{ field.maxCount || 1 }} 个
            </div>
          </div>
        </transition-group>
      </draggable>
      <el-empty v-if="!localFields.length" description="暂无字段，点击新增字段开始配置" :image-size="88" />
    </div>

    <el-collapse-transition>
      <div v-show="advancedVisible || !canVisualize" class="advanced-panel">
        <el-input
          :value="rawText"
          type="textarea"
          :rows="isMobile ? 10 : 12"
          placeholder="原始 schema JSON"
          @input="handleRawInput"
        />
      </div>
    </el-collapse-transition>

    <field-edit-drawer
      :visible="drawerVisible"
      :field="editingField"
      :field-type-options="safeFieldTypeOptions"
      :is-mobile="isMobile"
      @save="handleFieldSave"
      @close="drawerVisible = false"
    />
    <schema-preview
      :visible="previewVisible"
      :schema="outputSchema"
      :is-mobile="isMobile"
      @close="previewVisible = false"
    />
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import FieldEditDrawer from './FieldEditDrawer'
import SchemaPreview from './SchemaPreview'

const OPTION_TYPES = ['radio', 'checkbox', 'select']
const UPLOAD_TYPES = ['image', 'audio', 'video', 'file']
const VISUAL_ALLOWED_TYPES = ['text', 'textarea', 'phone', 'idcard', 'age', 'number', 'radio', 'checkbox', 'select', 'date', 'image', 'audio', 'video', 'file']
const BACKEND_ALLOWED_TYPES = ['text', 'textarea', 'phone', 'idcard', 'radio', 'checkbox', 'select', 'date', 'age', 'number', 'image', 'audio', 'video', 'file', 'group', 'conditional']
const BACKEND_BLOCKED_VISUAL_TYPES = []
const FIELD_TYPE_FALLBACK_OPTIONS = [
  { label: '单行文本', value: 'text' },
  { label: '多行文本', value: 'textarea' },
  { label: '手机号', value: 'phone' },
  { label: '身份证号', value: 'idcard' },
  { label: '年龄', value: 'age' },
  { label: '数字', value: 'number' },
  { label: '单选', value: 'radio' },
  { label: '多选', value: 'checkbox' },
  { label: '下拉选择', value: 'select' },
  { label: '日期', value: 'date' },
  { label: '图片', value: 'image' },
  { label: '音频', value: 'audio' },
  { label: '视频', value: 'video' },
  { label: '文件', value: 'file' },
  { label: '分组(raw)', value: 'group' },
  { label: '条件组(raw)', value: 'conditional' }
]

function deepClone(value) {
  return value == null ? value : JSON.parse(JSON.stringify(value))
}

function buildEmptyField() {
  return {
    key: '',
    label: '',
    type: 'text',
    required: false,
    placeholder: '',
    description: '',
    tip: ''
  }
}

export default {
  name: 'JstFormSchemaEditor',
  components: {
    draggable,
    FieldEditDrawer,
    SchemaPreview
  },
  props: {
    value: {
      type: [Object, Array, String],
      default() {
        return null
      }
    },
    fieldTypeOptions: {
      type: Array,
      default() {
        return []
      }
    },
    readonly: {
      type: Boolean,
      default: false
    },
    mobileReadonly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      localFields: [],
      rawText: '',
      parseError: '',
      fallbackReason: '',
      advancedVisible: false,
      previewVisible: false,
      drawerVisible: false,
      editingIndex: -1,
      editingField: buildEmptyField(),
      sourceFormat: 'object'
    }
  },
  computed: {
    isMobile() {
      return this.mobileReadonly
    },
    safeFieldTypeOptions() {
      return this.fieldTypeOptions && this.fieldTypeOptions.length ? this.fieldTypeOptions : FIELD_TYPE_FALLBACK_OPTIONS
    },
    disableVisualEdit() {
      return this.readonly || this.mobileReadonly || !this.canVisualize
    },
    canVisualize() {
      return !this.parseError && !this.fallbackReason
    },
    canPreview() {
      return this.canVisualize && this.localFields.length > 0
    },
    outputSchema() {
      const fields = deepClone(this.localFields) || []
      return this.sourceFormat === 'array' ? fields : { fields }
    },
    summaryText() {
      const summary = this.countFields(this.localFields)
      return `${summary.total} 字段 · ${summary.required} 必填`
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(nextValue) {
        this.syncFromValue(nextValue)
      }
    }
  },
  methods: {
    isOptionType(type) {
      return OPTION_TYPES.indexOf(type) >= 0
    },
    isUploadType(type) {
      return UPLOAD_TYPES.indexOf(type) >= 0
    },
    normalizedOptions(field) {
      if (!field || !Array.isArray(field.options)) return []
      return field.options.filter(item => item && item.label)
    },
    typeLabel(type) {
      const match = this.safeFieldTypeOptions.find(item => item.value === type)
      return match ? (match.label || match.dictLabel || type) : type
    },
    countFields(fields) {
      return (fields || []).reduce((acc, field) => {
        acc.total += 1
        if (field && field.required) acc.required += 1
        if (field && Array.isArray(field.fields)) {
          const child = this.countFields(field.fields)
          acc.total += child.total
          acc.required += child.required
        }
        return acc
      }, { total: 0, required: 0 })
    },
    syncFromValue(nextValue) {
      const normalized = this.normalizeValue(nextValue)
      this.sourceFormat = normalized.sourceFormat
      this.rawText = normalized.rawText
      this.localFields = normalized.fields
      this.parseError = normalized.parseError
      this.fallbackReason = normalized.fallbackReason
      this.emitState(false)
    },
    normalizeValue(nextValue) {
      const result = {
        fields: [],
        rawText: '',
        parseError: '',
        fallbackReason: '',
        sourceFormat: 'object'
      }
      if (nextValue == null || nextValue === '') {
        return result
      }
      let schema = nextValue
      if (typeof nextValue === 'string') {
        result.rawText = nextValue
        try {
          schema = JSON.parse(nextValue)
        } catch (error) {
          result.parseError = 'Schema JSON 格式错误：' + error.message
          return result
        }
      } else {
        result.rawText = JSON.stringify(nextValue, null, 2)
      }
      if (Array.isArray(schema)) {
        result.sourceFormat = 'array'
        result.fields = deepClone(schema) || []
      } else if (schema && Array.isArray(schema.fields)) {
        result.sourceFormat = 'object'
        result.fields = deepClone(schema.fields) || []
      } else {
        result.parseError = 'schema 顶层必须是字段数组，或包含 fields 数组的对象。'
        return result
      }
      const analysis = this.analyzeFields(result.fields)
      result.parseError = analysis.parseError
      result.fallbackReason = analysis.fallbackReason
      return result
    },
    analyzeFields(fields) {
      const state = { parseError: '', fallbackReason: '' }
      const visit = list => {
        for (let i = 0; i < list.length; i++) {
          const field = list[i]
          if (!field || typeof field !== 'object' || Array.isArray(field)) {
            state.parseError = 'schema 字段项必须是对象。'
            return
          }
          const key = (field.key || '').toString().trim()
          const type = (field.type || '').toString().trim()
          const label = (field.label || '').toString().trim()
          if (!key || !type || !label || typeof field.required === 'undefined') {
            state.parseError = 'schema 字段缺少 key / type / label / required。'
            return
          }
          if (BACKEND_BLOCKED_VISUAL_TYPES.indexOf(type) >= 0) {
            state.parseError = `当前后端白名单暂不支持 ${type} 类型，保存前请改为受支持字段类型。`
            return
          }
          if (BACKEND_ALLOWED_TYPES.indexOf(type) < 0) {
            state.parseError = `检测到未知字段类型 ${type}，保存前请修正 schema。`
            return
          }
          if (Array.isArray(field.fields) && field.fields.length) {
            if (type === 'group' || type === 'conditional') {
              state.fallbackReason = `检测到 ${type} 嵌套结构，已自动切换为高级 raw 模式并保留原文。`
              return
            }
            state.fallbackReason = '检测到嵌套字段结构，当前可视化模式暂不支持，已切换为高级 raw。'
            return
          }
          if (type === 'group' || type === 'conditional') {
            state.fallbackReason = `检测到 ${type} 字段，当前可视化模式暂不支持，已切换为高级 raw。`
            return
          }
          if (VISUAL_ALLOWED_TYPES.indexOf(type) < 0) {
            state.fallbackReason = `字段类型 ${type} 暂不支持可视化编辑，已切换为高级 raw。`
            return
          }
        }
      }
      visit(fields || [])
      return state
    },
    emitState(emitInput = true, overrideParsedValue) {
      const parsedValue = typeof overrideParsedValue !== 'undefined' ? overrideParsedValue : this.outputSchema
      if (emitInput && !this.parseError) {
        this.$emit('input', deepClone(parsedValue))
      }
      const payload = {
        value: deepClone(parsedValue),
        rawText: this.rawText,
        parseError: this.parseError,
        fallbackMode: Boolean(this.fallbackReason || this.parseError),
        reason: this.parseError || this.fallbackReason
      }
      this.$emit('change', payload)
      this.$emit('fallback-change', payload)
    },
    handleDragEnd() {
      this.syncRawFromFields()
      this.emitState(true)
    },
    syncRawFromFields() {
      this.rawText = JSON.stringify(this.outputSchema, null, 2)
    },
    openCreateDrawer() {
      this.editingIndex = -1
      this.editingField = buildEmptyField()
      this.drawerVisible = true
    },
    openEditDrawer(index) {
      this.editingIndex = index
      this.editingField = deepClone(this.localFields[index])
      this.drawerVisible = true
    },
    removeField(index) {
      this.localFields.splice(index, 1)
      this.syncRawFromFields()
      this.emitState(true)
    },
    handleFieldSave(field) {
      if (this.editingIndex >= 0) {
        this.$set(this.localFields, this.editingIndex, field)
      } else {
        this.localFields.push(field)
      }
      this.drawerVisible = false
      this.syncRawFromFields()
      const analysis = this.analyzeFields(this.localFields)
      this.parseError = analysis.parseError
      this.fallbackReason = analysis.fallbackReason
      this.emitState(true)
    },
    handleRawInput(nextText) {
      this.rawText = nextText
      if (!nextText.trim()) {
        this.localFields = []
        this.parseError = ''
        this.fallbackReason = ''
        this.emitState(true, this.sourceFormat === 'array' ? [] : { fields: [] })
        return
      }
      const normalized = this.normalizeValue(nextText)
      this.localFields = normalized.fields
      this.parseError = normalized.parseError
      this.fallbackReason = normalized.fallbackReason
      this.sourceFormat = normalized.sourceFormat
      if (!normalized.parseError) {
        this.emitState(true, this.outputSchema)
      } else {
        this.emitState(false, this.outputSchema)
      }
    }
  }
}
</script>

<style scoped>
.form-schema-editor {
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

.editor-alert,
.editor-toolbar,
.advanced-panel {
  margin-top: 12px;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.toolbar-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.field-board,
.advanced-panel {
  margin-top: 12px;
  padding: 14px;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  background: #fff;
}

.field-list {
  display: grid;
  gap: 12px;
}

.field-card {
  padding: 14px;
  border: 1px solid #dbe5f1;
  border-radius: 10px;
  background: #f8fbff;
}

.field-card--ghost {
  opacity: .65;
}

.field-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.field-card__title-wrap {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.field-card__title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #172033;
}

.field-card__meta,
.field-card__hint,
.field-card__empty {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
  line-height: 1.6;
}

.field-card__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.field-card__options {
  margin-top: 8px;
}

.field-card__options .el-tag {
  margin-right: 6px;
  margin-bottom: 6px;
}

.drag-handle {
  margin-top: 2px;
  color: #94a3b8;
  cursor: move;
}

.danger-text {
  color: #ef4444;
}

@media (max-width: 768px) {
  .form-schema-editor {
    padding: 14px;
  }

  .editor-head,
  .editor-toolbar,
  .field-card__head {
    display: block;
  }

  .editor-head__meta,
  .toolbar-actions,
  .field-card__actions {
    margin-top: 10px;
    justify-content: flex-start;
  }
}
</style>
