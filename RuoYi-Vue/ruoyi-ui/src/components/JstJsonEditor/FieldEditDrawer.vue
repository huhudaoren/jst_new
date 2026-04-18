<template>
  <el-drawer
    title="字段配置"
    :visible.sync="drawerVisible"
    :size="isMobile ? '100%' : '460px'"
    append-to-body
    custom-class="jst-field-edit-drawer"
    @close="handleClose"
  >
    <div class="drawer-body">
      <el-form ref="drawerForm" :model="draft" :rules="rules" label-width="88px">
        <el-form-item label="字段标识" prop="key">
          <el-input v-model.trim="draft.key" placeholder="例如：studentName" />
        </el-form-item>
        <el-form-item label="字段名称" prop="label">
          <el-input v-model.trim="draft.label" placeholder="例如：学生姓名" />
        </el-form-item>
        <el-form-item label="字段类型" prop="type">
          <el-select v-model="draft.type" class="full-width" placeholder="请选择">
            <el-option
              v-for="item in fieldTypeOptions"
              :key="item.value"
              :label="typeOptionLabel(item)"
              :value="item.value"
              :disabled="isUnsupportedType(item.value)"
            />
          </el-select>
          <div v-if="isUnsupportedType(draft.type)" class="field-help danger-text">该类型当前仅支持 raw 保底，不建议在可视化模式新增。</div>
        </el-form-item>
        <el-form-item label="必填">
          <el-switch v-model="draft.required" />
        </el-form-item>
        <el-form-item label="占位提示">
          <el-input v-model.trim="draft.placeholder" placeholder="可选" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model.trim="draft.description" type="textarea" :rows="2" placeholder="展示在字段下方的补充说明" />
        </el-form-item>
        <el-form-item label="小提示">
          <el-input v-model.trim="draft.tip" placeholder="展示在字段标题右侧的小提示" />
        </el-form-item>

        <template v-if="supportsMaxLength">
          <el-form-item label="最大长度">
            <el-input-number v-model="draft.maxlength" :min="1" :max="500" class="full-width" />
          </el-form-item>
        </template>

        <template v-if="isOptionType">
          <div class="section-head">
            <span>选项配置</span>
            <el-button type="text" @click="addOption">新增选项</el-button>
          </div>
          <div v-if="draft.options.length" class="option-list">
            <div v-for="(option, index) in draft.options" :key="index" class="option-row">
              <el-input v-model.trim="option.label" placeholder="显示文案" />
              <el-input v-model.trim="option.value" placeholder="提交值" />
              <el-button type="text" class="danger-text" @click="removeOption(index)">删除</el-button>
            </div>
          </div>
          <div v-else class="field-help">单选、多选、下拉字段建议至少配置 1 个选项。</div>
        </template>

        <template v-if="isUploadType">
          <el-form-item label="支持格式">
            <el-input v-model.trim="draft.accept" placeholder="例如：image/* 或 .pdf,.docx" />
          </el-form-item>
          <el-form-item label="最大数量">
            <el-input-number v-model="draft.maxCount" :min="1" :max="9" class="full-width" />
          </el-form-item>
        </template>
      </el-form>
    </div>
    <div class="drawer-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSave">保存字段</el-button>
    </div>
  </el-drawer>
</template>

<script>
const UNSUPPORTED_VISUAL_TYPES = ['group', 'conditional']
const OPTION_TYPES = ['radio', 'checkbox', 'select']
const UPLOAD_TYPES = ['image', 'audio', 'video', 'file']
const MAX_LENGTH_TYPES = ['text', 'textarea']

function buildDraft(field) {
  const source = field || {}
  return {
    key: source.key || '',
    label: source.label || '',
    type: source.type || 'text',
    required: Boolean(source.required),
    placeholder: source.placeholder || '',
    description: source.description || '',
    tip: source.tip || '',
    maxlength: source.maxlength || null,
    options: Array.isArray(source.options) ? source.options.map(item => ({ label: item.label || '', value: item.value || '' })) : [],
    accept: source.accept || '',
    maxCount: source.maxCount || null
  }
}

export default {
  name: 'JstFieldEditDrawer',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    field: {
      type: Object,
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
    isMobile: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      draft: buildDraft(),
      rules: {
        key: [{ required: true, message: '请输入字段标识', trigger: 'blur' }],
        label: [{ required: true, message: '请输入字段名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择字段类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    drawerVisible: {
      get() {
        return this.visible
      },
      set(val) {
        if (!val) this.handleClose()
      }
    },
    isOptionType() {
      return OPTION_TYPES.indexOf(this.draft.type) >= 0
    },
    isUploadType() {
      return UPLOAD_TYPES.indexOf(this.draft.type) >= 0
    },
    supportsMaxLength() {
      return MAX_LENGTH_TYPES.indexOf(this.draft.type) >= 0
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler(nextVisible) {
        if (nextVisible) {
          this.resetDraft()
        }
      }
    },
    field: {
      deep: true,
      handler() {
        if (this.visible) {
          this.resetDraft()
        }
      }
    },
    'draft.type'(nextType) {
      if (!this.supportsMaxLength) {
        this.draft.maxlength = null
      }
      if (!this.isOptionType) {
        this.draft.options = []
      } else if (!this.draft.options.length) {
        this.draft.options = [{ label: '', value: '' }]
      }
      if (!this.isUploadType) {
        this.draft.accept = ''
        this.draft.maxCount = null
      } else if (!this.draft.maxCount) {
        this.draft.maxCount = 1
      }
      if (UNSUPPORTED_VISUAL_TYPES.indexOf(nextType) >= 0) {
        this.$nextTick(() => {
          this.$message.warning('该字段类型建议使用高级 raw 维护，避免前后端白名单不一致。')
        })
      }
    }
  },
  methods: {
    typeOptionLabel(item) {
      if (!item) return ''
      return item.label || item.dictLabel || item.value
    },
    isUnsupportedType(type) {
      return UNSUPPORTED_VISUAL_TYPES.indexOf(type) >= 0
    },
    resetDraft() {
      this.draft = buildDraft(this.field)
      this.$nextTick(() => {
        this.$refs.drawerForm && this.$refs.drawerForm.clearValidate()
      })
    },
    addOption() {
      this.draft.options.push({ label: '', value: '' })
    },
    removeOption(index) {
      this.draft.options.splice(index, 1)
    },
    handleClose() {
      this.$emit('close')
    },
    sanitizeDraft() {
      const payload = {
        key: (this.draft.key || '').trim(),
        label: (this.draft.label || '').trim(),
        type: this.draft.type,
        required: Boolean(this.draft.required)
      }
      ;['placeholder', 'description', 'tip', 'accept'].forEach(key => {
        const value = (this.draft[key] || '').trim()
        if (value) payload[key] = value
      })
      if (this.supportsMaxLength && this.draft.maxlength) {
        payload.maxlength = Number(this.draft.maxlength)
      }
      if (this.isOptionType) {
        const options = this.draft.options
          .map(item => ({ label: (item.label || '').trim(), value: (item.value || '').trim() }))
          .filter(item => item.label && item.value)
        if (options.length) {
          payload.options = options
        }
      }
      if (this.isUploadType) {
        if (this.draft.maxCount) payload.maxCount = Number(this.draft.maxCount)
      }
      return payload
    },
    handleSave() {
      this.$refs.drawerForm.validate(valid => {
        if (!valid) return
        this.$emit('save', this.sanitizeDraft())
      })
    }
  }
}
</script>

<style scoped>
.drawer-body {
  padding: 20px 20px 92px;
}

.drawer-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #e5eaf2;
  background: #fff;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 10px 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}

.option-list {
  display: grid;
  gap: 10px;
}

.option-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr) auto;
  gap: 8px;
  align-items: center;
}

.field-help {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}

.danger-text {
  color: #f56c6c;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .option-row {
    grid-template-columns: 1fr;
  }
}
</style>
