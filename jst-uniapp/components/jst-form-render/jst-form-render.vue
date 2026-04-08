<template>
  <view class="jst-form-render">
    <block v-for="field in normalizedFields" :key="getFieldId(field)">
      <template v-if="isFieldVisible(field)">
        <view
          v-if="field.type === 'group' || field.type === 'conditional'"
          class="jst-form-render__group"
        >
          <view v-if="getFieldLabel(field)" class="jst-form-render__group-header">
            <text class="jst-form-render__group-title">{{ getFieldLabel(field) }}</text>
            <text v-if="field.description" class="jst-form-render__group-desc">{{ field.description }}</text>
          </view>

          <jst-form-render
            v-if="getNestedFields(field).length"
            :schema="{ fields: getNestedFields(field) }"
            :value="value"
            :readonly="readonly"
            @input="forwardInput"
            @validate="forwardValidate"
          />
        </view>

        <view v-else class="jst-form-render__field">
          <view class="jst-form-render__label-row">
            <text class="jst-form-render__label">
              <text v-if="Boolean(field.required)" class="jst-form-render__required">*</text>
              {{ getFieldLabel(field) }}
            </text>
            <text v-if="field.tip" class="jst-form-render__tip">{{ field.tip }}</text>
          </view>

          <text v-if="field.description" class="jst-form-render__desc">{{ field.description }}</text>

          <view v-if="readonly" class="jst-form-render__readonly">
            <template v-if="isUploadType(field.type)">
              <view
                v-for="(item, index) in normalizeAssetList(getFieldValue(field))"
                :key="`${getFieldKey(field)}-${index}`"
                class="jst-form-render__asset jst-form-render__asset--readonly"
              >
                <text class="jst-form-render__asset-text">{{ item }}</text>
              </view>
              <text v-if="!normalizeAssetList(getFieldValue(field)).length" class="jst-form-render__placeholder">
                暂无上传内容
              </text>
            </template>

            <text v-else class="jst-form-render__readonly-text">
              {{ getDisplayText(field) || '未填写' }}
            </text>
          </view>

          <template v-else>
            <input
              v-if="field.type === 'text' || field.type === 'age' || field.type === 'number'"
              class="jst-form-render__input"
              :type="field.type === 'text' ? 'text' : 'number'"
              :value="stringValue(getFieldValue(field))"
              :placeholder="field.placeholder || `请输入${getFieldLabel(field)}`"
              placeholder-class="jst-form-render__placeholder"
              @input="handleTextInput(field, $event)"
            />

            <textarea
              v-else-if="field.type === 'textarea'"
              class="jst-form-render__textarea"
              :value="stringValue(getFieldValue(field))"
              :placeholder="field.placeholder || `请输入${getFieldLabel(field)}`"
              placeholder-class="jst-form-render__placeholder"
              :maxlength="field.maxlength || 500"
              @input="handleTextInput(field, $event)"
            />

            <view v-else-if="field.type === 'radio'" class="jst-form-render__option-list">
              <view
                v-for="option in getFieldOptions(field)"
                :key="`${getFieldKey(field)}-${option.value}`"
                class="jst-form-render__option"
                :class="{ 'jst-form-render__option--active': getFieldValue(field) === option.value }"
                @tap="emitFieldInput(field, option.value)"
              >
                <text class="jst-form-render__option-text">{{ option.label }}</text>
              </view>
            </view>

            <view v-else-if="field.type === 'checkbox'" class="jst-form-render__option-list">
              <view
                v-for="option in getFieldOptions(field)"
                :key="`${getFieldKey(field)}-${option.value}`"
                class="jst-form-render__option"
                :class="{ 'jst-form-render__option--active': normalizeCheckboxValue(getFieldValue(field)).includes(option.value) }"
                @tap="toggleCheckbox(field, option.value)"
              >
                <text class="jst-form-render__option-text">{{ option.label }}</text>
              </view>
            </view>

            <picker
              v-else-if="field.type === 'select'"
              class="jst-form-render__picker"
              :range="getFieldOptions(field)"
              range-key="label"
              :value="getSelectedIndex(field)"
              @change="handleSelectChange(field, $event)"
            >
              <view class="jst-form-render__picker-inner">
                <text :class="getDisplayText(field) ? 'jst-form-render__picker-text' : 'jst-form-render__placeholder'">
                  {{ getDisplayText(field) || field.placeholder || `请选择${getFieldLabel(field)}` }}
                </text>
                <text class="jst-form-render__picker-arrow">></text>
              </view>
            </picker>

            <picker
              v-else-if="field.type === 'date'"
              mode="date"
              class="jst-form-render__picker"
              :value="stringValue(getFieldValue(field))"
              @change="handleDateChange(field, $event)"
            >
              <view class="jst-form-render__picker-inner">
                <text :class="getDisplayText(field) ? 'jst-form-render__picker-text' : 'jst-form-render__placeholder'">
                  {{ getDisplayText(field) || field.placeholder || `请选择${getFieldLabel(field)}` }}
                </text>
                <text class="jst-form-render__picker-arrow">></text>
              </view>
            </picker>

            <view v-else-if="isUploadType(field.type)" class="jst-form-render__upload">
              <view
                v-for="(item, index) in normalizeAssetList(getFieldValue(field))"
                :key="`${getFieldKey(field)}-${index}`"
                class="jst-form-render__asset"
              >
                <text class="jst-form-render__asset-text">{{ item }}</text>
                <text class="jst-form-render__asset-remove" @tap.stop="removeAsset(field, index)">删除</text>
              </view>

              <view class="jst-form-render__upload-action" @tap="appendMockAsset(field)">
                <text class="jst-form-render__upload-action-text">{{ getUploadActionText(field.type) }}</text>
                <text class="jst-form-render__upload-action-sub">本期使用 mock URL 占位</text>
              </view>
            </view>

            <input
              v-else
              class="jst-form-render__input"
              :value="stringValue(getFieldValue(field))"
              :placeholder="field.placeholder || `请输入${getFieldLabel(field)}`"
              placeholder-class="jst-form-render__placeholder"
              @input="handleTextInput(field, $event)"
            />
          </template>
        </view>
      </template>
    </block>
  </view>
</template>

<script>
export default {
  name: 'JstFormRender',
  props: {
    schema: {
      type: [Object, Array],
      default() {
        return { fields: [] }
      }
    },
    value: {
      type: Object,
      default() {
        return {}
      }
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    normalizedFields() {
      if (Array.isArray(this.schema)) {
        return this.schema
      }
      return Array.isArray(this.schema && this.schema.fields) ? this.schema.fields : []
    }
  },
  methods: {
    validateForm() {
      const errors = []
      this.collectErrors(this.normalizedFields, errors)
      this.$emit('validate', errors)
      return errors
    },

    collectErrors(fields, errors) {
      fields.forEach((field) => {
        if (!this.isFieldVisible(field)) {
          return
        }

        if (field.type === 'group' || field.type === 'conditional') {
          this.collectErrors(this.getNestedFields(field), errors)
          return
        }

        if (!field.required) {
          return
        }

        if (!this.hasFieldValue(field)) {
          errors.push({
            key: this.getFieldKey(field),
            label: this.getFieldLabel(field),
            message: `${this.getFieldLabel(field)}未填写`
          })
        }
      })
    },

    forwardInput(key, value) {
      this.$emit('input', key, value)
    },

    forwardValidate(errors) {
      this.$emit('validate', errors)
    },

    getFieldId(field) {
      return this.getFieldKey(field) || `${field.type || 'field'}-${field.label || ''}`
    },

    getFieldKey(field) {
      return field.key || field.name || field.code || field.id || ''
    },

    getFieldLabel(field) {
      return field.label || field.name || '未命名字段'
    },

    getNestedFields(field) {
      return Array.isArray(field.fields) ? field.fields : []
    },

    isFieldVisible(field) {
      if (!field || !field.visibleIf) {
        return true
      }
      const rule = field.visibleIf
      const target = this.value && rule.key ? this.value[rule.key] : undefined
      if (Array.isArray(target)) {
        return target.includes(rule.value)
      }
      return target === rule.value
    },

    getFieldValue(field) {
      const key = this.getFieldKey(field)
      return key && this.value ? this.value[key] : undefined
    },

    stringValue(value) {
      if (value === null || value === undefined) {
        return ''
      }
      return `${value}`
    },

    emitFieldInput(field, fieldValue) {
      this.$emit('input', this.getFieldKey(field), fieldValue)
    },

    handleTextInput(field, event) {
      this.emitFieldInput(field, event.detail.value)
    },

    getFieldOptions(field) {
      const source = Array.isArray(field.options) ? field.options : []
      return source.map((item) => {
        if (typeof item === 'object') {
          return {
            label: item.label || item.name || item.text || `${item.value || ''}`,
            value: item.value !== undefined ? item.value : (item.code !== undefined ? item.code : item.label)
          }
        }
        return {
          label: `${item}`,
          value: item
        }
      })
    },

    getSelectedIndex(field) {
      const current = this.getFieldValue(field)
      return Math.max(
        this.getFieldOptions(field).findIndex((item) => item.value === current),
        0
      )
    },

    handleSelectChange(field, event) {
      const index = Number(event.detail.value || 0)
      const option = this.getFieldOptions(field)[index]
      this.emitFieldInput(field, option ? option.value : '')
    },

    handleDateChange(field, event) {
      this.emitFieldInput(field, event.detail.value)
    },

    normalizeCheckboxValue(value) {
      return Array.isArray(value) ? value : []
    },

    toggleCheckbox(field, optionValue) {
      const current = this.normalizeCheckboxValue(this.getFieldValue(field))
      const next = current.includes(optionValue)
        ? current.filter((item) => item !== optionValue)
        : current.concat(optionValue)
      this.emitFieldInput(field, next)
    },

    getDisplayText(field) {
      const value = this.getFieldValue(field)
      if (field.type === 'radio' || field.type === 'select') {
        const option = this.getFieldOptions(field).find((item) => item.value === value)
        return option ? option.label : this.stringValue(value)
      }
      if (field.type === 'checkbox') {
        return this.normalizeCheckboxValue(value)
          .map((item) => {
            const option = this.getFieldOptions(field).find((target) => target.value === item)
            return option ? option.label : item
          })
          .join(' / ')
      }
      if (Array.isArray(value)) {
        return value.join(' / ')
      }
      return this.stringValue(value)
    },

    isUploadType(type) {
      return ['image', 'audio', 'video', 'file'].includes(type)
    },

    normalizeAssetList(value) {
      if (Array.isArray(value)) {
        return value.filter(Boolean)
      }
      if (!value) {
        return []
      }
      return [value]
    },

    appendMockAsset(field) {
      const key = this.getFieldKey(field)
      const extMap = {
        image: 'jpg',
        audio: 'mp3',
        video: 'mp4',
        file: 'pdf'
      }
      const current = this.normalizeAssetList(this.value[key])
      const next = current.concat(`mock://${field.type}/${Date.now()}.${extMap[field.type] || 'dat'}`)
      this.$emit('input', key, next)
    },

    removeAsset(field, index) {
      const key = this.getFieldKey(field)
      const next = this.normalizeAssetList(this.value[key]).filter((_, targetIndex) => targetIndex !== index)
      this.$emit('input', key, next)
    },

    getUploadActionText(type) {
      const textMap = {
        image: '添加图片',
        audio: '添加音频',
        video: '添加视频',
        file: '添加文件'
      }
      return textMap[type] || '添加附件'
    },

    hasFieldValue(field) {
      const value = this.getFieldValue(field)
      if (field.type === 'checkbox') {
        return this.normalizeCheckboxValue(value).length > 0
      }
      if (this.isUploadType(field.type)) {
        return this.normalizeAssetList(value).length > 0
      }
      if (Array.isArray(value)) {
        return value.length > 0
      }
      if (typeof value === 'number') {
        return true
      }
      return !!`${value || ''}`.trim()
    }
  }
}
</script>

<style scoped lang="scss">
.jst-form-render__group {
  margin-bottom: 24rpx;
}

.jst-form-render__group-header {
  margin-bottom: 18rpx;
}

.jst-form-render__group-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.jst-form-render__group-desc,
.jst-form-render__desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--jst-color-text-tertiary);
}

.jst-form-render__field {
  margin-bottom: 24rpx;
}

.jst-form-render__field:last-child {
  margin-bottom: 0;
}

.jst-form-render__label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.jst-form-render__label {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--jst-color-text);
}

.jst-form-render__required {
  margin-right: 6rpx;
  color: #eb5757;
}

.jst-form-render__tip {
  font-size: 22rpx;
  color: var(--jst-color-primary);
}

.jst-form-render__input,
.jst-form-render__textarea,
.jst-form-render__picker-inner,
.jst-form-render__readonly {
  box-sizing: border-box;
  width: 100%;
  margin-top: 16rpx;
  padding: 0 24rpx;
  border: 2rpx solid #dce6f7;
  border-radius: 24rpx;
  background: #ffffff;
}

.jst-form-render__input,
.jst-form-render__picker-inner,
.jst-form-render__readonly {
  min-height: 92rpx;
  display: flex;
  align-items: center;
}

.jst-form-render__textarea {
  min-height: 220rpx;
  padding: 24rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: var(--jst-color-text);
}

.jst-form-render__readonly-text,
.jst-form-render__picker-text {
  flex: 1;
  font-size: 28rpx;
  line-height: 1.7;
  color: var(--jst-color-text);
}

.jst-form-render__placeholder {
  color: #a2afc6;
  font-size: 28rpx;
}

.jst-form-render__picker-arrow {
  margin-left: 12rpx;
  color: #9aa8bf;
  font-size: 24rpx;
}

.jst-form-render__option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 16rpx;
}

.jst-form-render__option {
  min-width: 176rpx;
  padding: 22rpx 24rpx;
  border: 2rpx solid #dce6f7;
  border-radius: 24rpx;
  background: #ffffff;
}

.jst-form-render__option--active {
  border-color: #2f7de1;
  background: #eef4ff;
  box-shadow: 0 8rpx 18rpx rgba(47, 125, 225, 0.12);
}

.jst-form-render__option-text {
  font-size: 26rpx;
  line-height: 1.5;
  color: var(--jst-color-text);
}

.jst-form-render__upload {
  margin-top: 16rpx;
}

.jst-form-render__asset {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  min-height: 84rpx;
  margin-bottom: 12rpx;
  padding: 0 24rpx;
  border-radius: 20rpx;
  background: #f5f8ff;
}

.jst-form-render__asset--readonly {
  margin-top: 12rpx;
  margin-bottom: 0;
}

.jst-form-render__asset-text {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.6;
  color: var(--jst-color-text-secondary);
  word-break: break-all;
}

.jst-form-render__asset-remove {
  font-size: 24rpx;
  color: #ff6a3d;
}

.jst-form-render__upload-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 120rpx;
  border: 2rpx dashed #bcd2f5;
  border-radius: 24rpx;
  background: #f8fbff;
}

.jst-form-render__upload-action-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #2f7de1;
}

.jst-form-render__upload-action-sub {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #95a4bf;
}
</style>
