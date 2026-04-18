<template>
  <el-drawer
    title="模拟学生填表预览"
    :visible="visible"
    direction="rtl"
    size="460px"
    append-to-body
    @close="$emit('close')"
  >
    <div class="schema-preview" style="padding:16px 20px">
      <el-alert
        v-if="!fields.length"
        type="info"
        title="该表单模板暂无字段可预览"
        :closable="false"
        style="margin-bottom:16px"
      />

      <el-form
        v-else
        label-position="top"
        size="small"
        class="schema-preview__form"
      >
        <el-form-item
          v-for="(field, idx) in fields"
          :key="idx"
          :label="field.label || field.fieldLabel || field.name || ('字段 ' + (idx + 1))"
          :required="!!field.required"
        >
          <!-- text / textarea / number -->
          <el-input
            v-if="['text', 'textarea', 'number', 'phone', 'idcard'].includes(field.fieldType || field.type)"
            :type="(field.fieldType || field.type) === 'textarea' ? 'textarea' : 'text'"
            :placeholder="field.placeholder || '请输入'"
            :rows="3"
            disabled
          />

          <!-- select -->
          <el-select
            v-else-if="['select', 'radio', 'checkbox'].includes(field.fieldType || field.type)"
            disabled
            placeholder="请选择"
            style="width:100%"
          >
            <el-option
              v-for="(opt, oi) in (field.options || [])"
              :key="oi"
              :label="typeof opt === 'string' ? opt : opt.label"
              :value="typeof opt === 'string' ? opt : opt.value"
            />
          </el-select>

          <!-- date -->
          <el-date-picker
            v-else-if="['date', 'datetime'].includes(field.fieldType || field.type)"
            disabled
            style="width:100%"
            :type="field.fieldType || field.type"
            placeholder="请选择日期"
          />

          <!-- file / image / video -->
          <div
            v-else-if="['file', 'image', 'video'].includes(field.fieldType || field.type)"
            class="schema-preview__upload-placeholder"
          >
            <i class="el-icon-upload" />
            <span>{{ field.fieldType === 'image' ? '上传图片' : field.fieldType === 'video' ? '上传视频' : '上传文件' }}（预览模式）</span>
          </div>

          <!-- fallback -->
          <el-input v-else disabled placeholder="（预览模式）" />
        </el-form-item>

        <div class="schema-preview__footer">
          <el-button type="primary" size="small" disabled>提交（预览模式）</el-button>
        </div>
      </el-form>
    </div>
  </el-drawer>
</template>

<script>
export default {
  name: 'SchemaPreview',
  props: {
    /** el-drawer visible */
    visible: {
      type: Boolean,
      default: false
    },
    /**
     * 表单 schema，支持以下两种结构：
     * - { fields: [...] }
     * - [...]
     */
    schema: {
      type: [Object, Array],
      default: null
    },
    isMobile: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    fields() {
      if (!this.schema) return []
      if (Array.isArray(this.schema)) return this.schema
      if (this.schema.fields && Array.isArray(this.schema.fields)) return this.schema.fields
      return []
    }
  }
}
</script>

<style scoped>
.schema-preview__form {
  max-width: 420px;
}
.schema-preview__upload-placeholder {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  color: #909399;
  font-size: 13px;
}
.schema-preview__upload-placeholder .el-icon-upload {
  font-size: 20px;
}
.schema-preview__footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}
</style>
