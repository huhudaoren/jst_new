<!-- 动态表单渲染引擎：根据 schema_json 渲染表单，支持 10+ 字段类型、实时校验、文件上传 -->
<template>
  <view class="jst-dynamic-form">
    <block v-for="field in normalizedFields" :key="getFieldId(field)">
      <template v-if="isFieldVisible(field)">
        <!-- 分组/条件组 -->
        <view
          v-if="field.type === 'group' || field.type === 'conditional'"
          class="jst-dynamic-form__group"
        >
          <view v-if="getFieldLabel(field)" class="jst-dynamic-form__group-header">
            <text class="jst-dynamic-form__group-title">{{ getFieldLabel(field) }}</text>
            <text v-if="field.description" class="jst-dynamic-form__group-desc">{{ field.description }}</text>
          </view>
          <jst-dynamic-form
            v-if="getNestedFields(field).length"
            :schema="{ fields: getNestedFields(field) }"
            :value="value"
            :readonly="readonly"
            :show-validation="showValidation"
            @input="forwardInput"
            @validate="forwardValidate"
          />
        </view>

        <!-- 普通字段 -->
        <view v-else class="jst-dynamic-form__field">
          <view class="jst-dynamic-form__label-row">
            <text class="jst-dynamic-form__label">
              <text v-if="Boolean(field.required)" class="jst-dynamic-form__required">*</text>
              {{ getFieldLabel(field) }}
            </text>
            <text v-if="field.tip" class="jst-dynamic-form__tip">{{ field.tip }}</text>
          </view>
          <text v-if="field.description" class="jst-dynamic-form__desc">{{ field.description }}</text>

          <!-- ========== readonly 模式 ========== -->
          <view v-if="readonly" class="jst-dynamic-form__readonly">
            <!-- 上传类字段：只读展示文件列表 -->
            <template v-if="isUploadType(field.type)">
              <view class="jst-dynamic-form__file-list jst-dynamic-form__file-list--readonly">
                <view
                  v-for="(item, idx) in normalizeFileList(getFieldValue(field))"
                  :key="idx"
                  class="jst-dynamic-form__file-item"
                  @tap="previewFile(item, normalizeFileList(getFieldValue(field)))"
                >
                  <image
                    v-if="isImageUrl(item.url || item)"
                    class="jst-dynamic-form__file-thumb"
                    :src="item.url || item"
                    mode="aspectFill"
                  />
                  <view v-else class="jst-dynamic-form__file-icon">
                    <text class="jst-dynamic-form__file-icon-text">{{ getFileIcon(item.url || item) }}</text>
                  </view>
                  <text class="jst-dynamic-form__file-name">{{ getFileName(item) }}</text>
                </view>
                <text v-if="!normalizeFileList(getFieldValue(field)).length" class="jst-dynamic-form__placeholder">
                  暂无上传内容
                </text>
              </view>
            </template>
            <text v-else class="jst-dynamic-form__readonly-text">
              {{ getDisplayText(field) || '未填写' }}
            </text>
          </view>

          <!-- ========== 编辑模式 ========== -->
          <template v-else>
            <!-- text 单行文本 -->
            <view
              v-if="field.type === 'text'"
              class="jst-dynamic-form__input-wrap"
              :class="{ 'jst-dynamic-form__input-wrap--error': hasFieldError(field) }"
            >
              <input
                class="jst-dynamic-form__input"
                type="text"
                :value="stringValue(getFieldValue(field))"
                :placeholder="field.placeholder || '请输入' + getFieldLabel(field)"
                placeholder-class="jst-dynamic-form__placeholder"
                :maxlength="field.maxlength || -1"
                @input="handleTextInput(field, $event)"
                @blur="handleBlur(field)"
              />
            </view>

            <!-- textarea 多行文本 -->
            <view
              v-else-if="field.type === 'textarea'"
              class="jst-dynamic-form__textarea-wrap"
              :class="{ 'jst-dynamic-form__textarea-wrap--error': hasFieldError(field) }"
            >
              <textarea
                class="jst-dynamic-form__textarea"
                :value="stringValue(getFieldValue(field))"
                :placeholder="field.placeholder || '请输入' + getFieldLabel(field)"
                placeholder-class="jst-dynamic-form__placeholder"
                :maxlength="field.maxlength || 500"
                @input="handleTextInput(field, $event)"
                @blur="handleBlur(field)"
              />
              <text v-if="field.maxlength" class="jst-dynamic-form__textarea-count">
                {{ (stringValue(getFieldValue(field))).length }}/{{ field.maxlength }}
              </text>
            </view>

            <!-- phone 手机号 -->
            <view
              v-else-if="field.type === 'phone'"
              class="jst-dynamic-form__input-wrap"
              :class="{ 'jst-dynamic-form__input-wrap--error': hasFieldError(field) }"
            >
              <input
                class="jst-dynamic-form__input"
                type="number"
                :value="stringValue(getFieldValue(field))"
                :placeholder="field.placeholder || '请输入11位手机号'"
                placeholder-class="jst-dynamic-form__placeholder"
                :maxlength="11"
                @input="handleTextInput(field, $event)"
                @blur="handleBlur(field)"
              />
            </view>

            <!-- idcard 身份证号 -->
            <view
              v-else-if="field.type === 'idcard'"
              class="jst-dynamic-form__input-wrap"
              :class="{ 'jst-dynamic-form__input-wrap--error': hasFieldError(field) }"
            >
              <input
                class="jst-dynamic-form__input"
                type="idcard"
                :value="stringValue(getFieldValue(field))"
                :placeholder="field.placeholder || '请输入18位身份证号'"
                placeholder-class="jst-dynamic-form__placeholder"
                :maxlength="18"
                @input="handleTextInput(field, $event)"
                @blur="handleBlur(field)"
              />
            </view>

            <!-- age 年龄 -->
            <view
              v-else-if="field.type === 'age' || field.type === 'number'"
              class="jst-dynamic-form__input-wrap"
              :class="{ 'jst-dynamic-form__input-wrap--error': hasFieldError(field) }"
            >
              <input
                class="jst-dynamic-form__input"
                type="number"
                :value="stringValue(getFieldValue(field))"
                :placeholder="field.placeholder || (field.type === 'age' ? '请输入年龄(1-99)' : '请输入' + getFieldLabel(field))"
                placeholder-class="jst-dynamic-form__placeholder"
                @input="handleTextInput(field, $event)"
                @blur="handleBlur(field)"
              />
            </view>

            <!-- radio 单选 -->
            <view v-else-if="field.type === 'radio'" class="jst-dynamic-form__option-list">
              <view
                v-for="option in getFieldOptions(field)"
                :key="option.value"
                class="jst-dynamic-form__option"
                :class="{ 'jst-dynamic-form__option--active': getFieldValue(field) === option.value }"
                @tap="handleOptionTap(field, option.value)"
              >
                <view class="jst-dynamic-form__radio-dot" :class="{ 'jst-dynamic-form__radio-dot--active': getFieldValue(field) === option.value }"></view>
                <text class="jst-dynamic-form__option-text">{{ option.label }}</text>
              </view>
            </view>

            <!-- checkbox 多选 -->
            <view v-else-if="field.type === 'checkbox'" class="jst-dynamic-form__option-list">
              <view
                v-for="option in getFieldOptions(field)"
                :key="option.value"
                class="jst-dynamic-form__option"
                :class="{ 'jst-dynamic-form__option--active': normalizeCheckboxValue(getFieldValue(field)).includes(option.value) }"
                @tap="toggleCheckbox(field, option.value)"
              >
                <view class="jst-dynamic-form__check-box" :class="{ 'jst-dynamic-form__check-box--active': normalizeCheckboxValue(getFieldValue(field)).includes(option.value) }">
                  <text v-if="normalizeCheckboxValue(getFieldValue(field)).includes(option.value)" class="jst-dynamic-form__check-icon">✓</text>
                </view>
                <text class="jst-dynamic-form__option-text">{{ option.label }}</text>
              </view>
            </view>

            <!-- select 下拉选择 -->
            <picker
              v-else-if="field.type === 'select'"
              class="jst-dynamic-form__picker"
              :range="getFieldOptions(field)"
              range-key="label"
              :value="getSelectedIndex(field)"
              @change="handleSelectChange(field, $event)"
            >
              <view
                class="jst-dynamic-form__picker-inner"
                :class="{ 'jst-dynamic-form__picker-inner--error': hasFieldError(field) }"
              >
                <text :class="getDisplayText(field) ? 'jst-dynamic-form__picker-text' : 'jst-dynamic-form__placeholder'">
                  {{ getDisplayText(field) || field.placeholder || '请选择' + getFieldLabel(field) }}
                </text>
                <text class="jst-dynamic-form__picker-arrow">&#xe65e;</text>
              </view>
            </picker>

            <!-- date 日期 -->
            <picker
              v-else-if="field.type === 'date'"
              mode="date"
              class="jst-dynamic-form__picker"
              :value="stringValue(getFieldValue(field))"
              @change="handleDateChange(field, $event)"
            >
              <view
                class="jst-dynamic-form__picker-inner"
                :class="{ 'jst-dynamic-form__picker-inner--error': hasFieldError(field) }"
              >
                <text :class="getDisplayText(field) ? 'jst-dynamic-form__picker-text' : 'jst-dynamic-form__placeholder'">
                  {{ getDisplayText(field) || field.placeholder || '请选择' + getFieldLabel(field) }}
                </text>
                <text class="jst-dynamic-form__picker-arrow">&#xe65e;</text>
              </view>
            </picker>

            <!-- file/image/video 上传类 -->
            <view v-else-if="isUploadType(field.type)" class="jst-dynamic-form__upload">
              <view class="jst-dynamic-form__file-list">
                <view
                  v-for="(item, idx) in normalizeFileList(getFieldValue(field))"
                  :key="idx"
                  class="jst-dynamic-form__file-item"
                >
                  <image
                    v-if="isImageUrl(item.url || item)"
                    class="jst-dynamic-form__file-thumb"
                    :src="item.url || item"
                    mode="aspectFill"
                    @tap="previewFile(item, normalizeFileList(getFieldValue(field)))"
                  />
                  <view v-else class="jst-dynamic-form__file-icon" @tap="previewFile(item, normalizeFileList(getFieldValue(field)))">
                    <text class="jst-dynamic-form__file-icon-text">{{ getFileIcon(item.url || item) }}</text>
                  </view>
                  <text class="jst-dynamic-form__file-name">{{ getFileName(item) }}</text>
                  <text class="jst-dynamic-form__file-remove" @tap.stop="removeFile(field, idx)">×</text>
                </view>
              </view>

              <!-- 上传中进度 -->
              <view v-if="uploadingState[getFieldKey(field)]" class="jst-dynamic-form__upload-progress">
                <view class="jst-dynamic-form__progress-bar">
                  <view
                    class="jst-dynamic-form__progress-inner"
                    :style="{ width: (uploadingState[getFieldKey(field)] || 0) + '%' }"
                  ></view>
                </view>
                <text class="jst-dynamic-form__progress-text">上传中 {{ uploadingState[getFieldKey(field)] || 0 }}%</text>
              </view>

              <!-- 添加按钮 -->
              <view
                v-if="canAddMore(field)"
                class="jst-dynamic-form__upload-action"
                :class="{ 'jst-dynamic-form__upload-action--error': hasFieldError(field) }"
                @tap="chooseFile(field)"
              >
                <text class="jst-dynamic-form__upload-action-icon">+</text>
                <text class="jst-dynamic-form__upload-action-text">{{ getUploadActionText(field) }}</text>
                <text class="jst-dynamic-form__upload-action-sub">
                  {{ getUploadHint(field) }}
                </text>
              </view>
            </view>

            <!-- 兜底：未知类型按 text 处理 -->
            <view
              v-else
              class="jst-dynamic-form__input-wrap"
              :class="{ 'jst-dynamic-form__input-wrap--error': hasFieldError(field) }"
            >
              <input
                class="jst-dynamic-form__input"
                :value="stringValue(getFieldValue(field))"
                :placeholder="field.placeholder || '请输入' + getFieldLabel(field)"
                placeholder-class="jst-dynamic-form__placeholder"
                @input="handleTextInput(field, $event)"
                @blur="handleBlur(field)"
              />
            </view>
          </template>

          <!-- 校验错误提示 -->
          <view v-if="!readonly && hasFieldError(field)" class="jst-dynamic-form__error">
            <text class="jst-dynamic-form__error-text">{{ getFieldError(field) }}</text>
          </view>
        </view>
      </template>
    </block>
  </view>
</template>

<script>
import { uploadFile } from '@/api/upload'

// 手机号正则
const PHONE_REG = /^1[3-9]\d{9}$/
// 身份证校验（18位 + 校验码）
const IDCARD_REG = /^\d{17}[\dXx]$/
const IDCARD_WEIGHTS = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
const IDCARD_CHECK_CODES = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

function validateIdCard(value) {
  if (!value || value.length !== 18) return false
  if (!IDCARD_REG.test(value)) return false
  // 校验码验证
  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += parseInt(value.charAt(i), 10) * IDCARD_WEIGHTS[i]
  }
  const checkCode = IDCARD_CHECK_CODES[sum % 11]
  return value.charAt(17).toUpperCase() === checkCode
}

// 图片后缀判断
const IMAGE_EXTS = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg']
const VIDEO_EXTS = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', '3gp']

function getExtension(url) {
  if (!url) return ''
  const clean = url.split('?')[0].split('#')[0]
  const dot = clean.lastIndexOf('.')
  return dot >= 0 ? clean.substring(dot + 1).toLowerCase() : ''
}

export default {
  name: 'JstDynamicForm',
  props: {
    schema: {
      type: [Object, Array],
      default() { return { fields: [] } }
    },
    value: {
      type: Object,
      default() { return {} }
    },
    readonly: {
      type: Boolean,
      default: false
    },
    showValidation: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 每个字段是否已触发过 blur
      touchedFields: {},
      // 上传进度 { [fieldKey]: percent }
      uploadingState: {}
    }
  },
  computed: {
    normalizedFields() {
      if (Array.isArray(this.schema)) return this.schema
      return Array.isArray(this.schema && this.schema.fields) ? this.schema.fields : []
    }
  },
  methods: {
    // ─── 公开方法：供父组件调用 ───
    /**
     * 全量校验，返回错误数组
     * @returns {Array<{key, label, message}>}
     */
    validateForm() {
      const errors = []
      this.collectErrors(this.normalizedFields, errors)
      // 标记所有字段为已触碰
      this.normalizedFields.forEach((f) => {
        if (f.type !== 'group' && f.type !== 'conditional') {
          this.$set(this.touchedFields, this.getFieldKey(f), true)
        }
      })
      this.$emit('validate', errors.length === 0)
      return errors
    },

    // ─── 校验逻辑 ───
    collectErrors(fields, errors) {
      fields.forEach((field) => {
        if (!this.isFieldVisible(field)) return
        if (field.type === 'group' || field.type === 'conditional') {
          this.collectErrors(this.getNestedFields(field), errors)
          return
        }
        const err = this.validateField(field)
        if (err) errors.push(err)
      })
    },

    /** 校验单个字段，返回错误对象或 null */
    validateField(field) {
      const key = this.getFieldKey(field)
      const label = this.getFieldLabel(field)
      const val = this.getFieldValue(field)

      // 必填检查
      if (field.required && !this.hasFieldValue(field)) {
        return { key, label, message: label + '不能为空' }
      }

      // 有值时才做格式校验
      if (!this.hasFieldValue(field)) return null

      const strVal = this.stringValue(val).trim()

      // 手机号
      if (field.type === 'phone' && !PHONE_REG.test(strVal)) {
        return { key, label, message: '请输入正确的11位手机号' }
      }

      // 身份证
      if (field.type === 'idcard' && !validateIdCard(strVal)) {
        return { key, label, message: '请输入正确的18位身份证号' }
      }

      // 年龄
      if (field.type === 'age') {
        const num = parseInt(strVal, 10)
        if (isNaN(num) || num < 1 || num > 99) {
          return { key, label, message: '年龄应在1-99之间' }
        }
      }

      // maxlength（textarea 等）
      if (field.maxlength && strVal.length > field.maxlength) {
        return { key, label, message: label + '最多' + field.maxlength + '个字符' }
      }

      return null
    },

    /** blur 时校验当前字段 */
    handleBlur(field) {
      const key = this.getFieldKey(field)
      this.$set(this.touchedFields, key, true)
      // 触发整体校验事件（让父组件知道当前状态）
      const errors = []
      this.collectErrors(this.normalizedFields, errors)
      this.$emit('validate', errors.length === 0)
    },

    /** 当前字段是否有错误（仅在 touched 或 showValidation 时展示） */
    hasFieldError(field) {
      const key = this.getFieldKey(field)
      if (!this.touchedFields[key] && !this.showValidation) return false
      return !!this.validateField(field)
    },

    /** 获取字段错误消息 */
    getFieldError(field) {
      const err = this.validateField(field)
      return err ? err.message : ''
    },

    // ─── 字段操作 ───
    getFieldId(field) {
      return this.getFieldKey(field) || (field.type || 'field') + '-' + (field.label || '')
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
      if (!field || !field.visibleIf) return true
      var rule = field.visibleIf
      var target = this.value && rule.key ? this.value[rule.key] : undefined
      if (Array.isArray(target)) return target.includes(rule.value)
      return target === rule.value
    },

    getFieldValue(field) {
      var key = this.getFieldKey(field)
      return key && this.value ? this.value[key] : undefined
    },

    stringValue(val) {
      if (val === null || val === undefined) return ''
      return '' + val
    },

    emitFieldInput(field, val) {
      this.$emit('input', this.getFieldKey(field), val)
    },

    forwardInput(key, val) {
      this.$emit('input', key, val)
    },

    forwardValidate(isValid) {
      this.$emit('validate', isValid)
    },

    handleTextInput(field, event) {
      this.emitFieldInput(field, event.detail.value)
    },

    // ─── 选项类 ───
    getFieldOptions(field) {
      var source = Array.isArray(field.options) ? field.options : []
      return source.map(function(item) {
        if (typeof item === 'object') {
          return {
            label: item.label || item.name || item.text || ('' + (item.value || '')),
            value: item.value !== undefined ? item.value : (item.code !== undefined ? item.code : item.label)
          }
        }
        return { label: '' + item, value: item }
      })
    },

    handleOptionTap(field, optionValue) {
      this.emitFieldInput(field, optionValue)
      // 选择后也算 touched
      this.$set(this.touchedFields, this.getFieldKey(field), true)
    },

    normalizeCheckboxValue(val) {
      return Array.isArray(val) ? val : []
    },

    toggleCheckbox(field, optionValue) {
      var current = this.normalizeCheckboxValue(this.getFieldValue(field))
      var next = current.includes(optionValue)
        ? current.filter(function(item) { return item !== optionValue })
        : current.concat(optionValue)
      this.emitFieldInput(field, next)
      this.$set(this.touchedFields, this.getFieldKey(field), true)
    },

    getSelectedIndex(field) {
      var current = this.getFieldValue(field)
      return Math.max(
        this.getFieldOptions(field).findIndex(function(item) { return item.value === current }),
        0
      )
    },

    handleSelectChange(field, event) {
      var index = Number(event.detail.value || 0)
      var option = this.getFieldOptions(field)[index]
      this.emitFieldInput(field, option ? option.value : '')
      this.$set(this.touchedFields, this.getFieldKey(field), true)
    },

    handleDateChange(field, event) {
      this.emitFieldInput(field, event.detail.value)
      this.$set(this.touchedFields, this.getFieldKey(field), true)
    },

    getDisplayText(field) {
      var val = this.getFieldValue(field)
      if (field.type === 'radio' || field.type === 'select') {
        var opt = this.getFieldOptions(field).find(function(item) { return item.value === val })
        return opt ? opt.label : this.stringValue(val)
      }
      if (field.type === 'checkbox') {
        var self = this
        return this.normalizeCheckboxValue(val)
          .map(function(item) {
            var found = self.getFieldOptions(field).find(function(t) { return t.value === item })
            return found ? found.label : item
          })
          .join(' / ')
      }
      if (Array.isArray(val)) return val.join(' / ')
      return this.stringValue(val)
    },

    hasFieldValue(field) {
      var val = this.getFieldValue(field)
      if (field.type === 'checkbox') return this.normalizeCheckboxValue(val).length > 0
      if (this.isUploadType(field.type)) return this.normalizeFileList(val).length > 0
      if (Array.isArray(val)) return val.length > 0
      if (typeof val === 'number') return true
      return !!('' + (val || '')).trim()
    },

    // ─── 上传类 ───
    isUploadType(type) {
      return ['image', 'audio', 'video', 'file'].includes(type)
    },

    normalizeFileList(val) {
      if (Array.isArray(val)) {
        return val.filter(Boolean).map(function(item) {
          if (typeof item === 'string') return { url: item, name: '' }
          return item
        })
      }
      if (!val) return []
      if (typeof val === 'string') return [{ url: val, name: '' }]
      return [val]
    },

    canAddMore(field) {
      var max = field.maxCount || 5
      return this.normalizeFileList(this.getFieldValue(field)).length < max
    },

    getUploadActionText(field) {
      var textMap = { image: '添加图片', audio: '添加音频', video: '添加视频', file: '添加文件' }
      return textMap[field.type] || '添加附件'
    },

    getUploadHint(field) {
      var max = field.maxCount || 5
      var current = this.normalizeFileList(this.getFieldValue(field)).length
      var parts = []
      if (field.accept) parts.push('支持 ' + field.accept)
      parts.push('已上传 ' + current + '/' + max)
      return parts.join('，')
    },

    /** 选择文件并上传 */
    chooseFile(field) {
      var self = this
      var fieldType = field.type
      var accept = field.accept || ''

      // 根据字段类型和 accept 决定选择方式
      if (fieldType === 'image' || (fieldType === 'file' && accept.indexOf('image') >= 0 && accept.indexOf('video') < 0 && accept.indexOf('.pdf') < 0)) {
        uni.chooseImage({
          count: (field.maxCount || 5) - self.normalizeFileList(self.getFieldValue(field)).length,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera'],
          success: function(res) {
            self.uploadFiles(field, res.tempFilePaths)
          }
        })
      } else if (fieldType === 'video') {
        uni.chooseVideo({
          sourceType: ['album', 'camera'],
          compressed: true,
          success: function(res) {
            self.uploadFiles(field, [res.tempFilePath])
          }
        })
      } else {
        // file 类型：支持多种格式，小程序用 chooseMessageFile
        // #ifdef MP-WEIXIN
        uni.chooseMessageFile({
          count: (field.maxCount || 5) - self.normalizeFileList(self.getFieldValue(field)).length,
          type: 'all',
          success: function(res) {
            var paths = res.tempFiles.map(function(f) { return f.path })
            self.uploadFiles(field, paths)
          }
        })
        // #endif
        // #ifndef MP-WEIXIN
        uni.chooseImage({
          count: (field.maxCount || 5) - self.normalizeFileList(self.getFieldValue(field)).length,
          success: function(res) {
            self.uploadFiles(field, res.tempFilePaths)
          }
        })
        // #endif
      }
    },

    /** 逐个上传文件 */
    async uploadFiles(field, paths) {
      var key = this.getFieldKey(field)
      var currentList = this.normalizeFileList(this.getFieldValue(field)).slice()
      var self = this

      for (var i = 0; i < paths.length; i++) {
        var filePath = paths[i]
        self.$set(self.uploadingState, key, 0)

        try {
          var result = await uploadFile(filePath, {
            onProgress: function(percent) {
              self.$set(self.uploadingState, key, percent)
            }
          })

          currentList.push({
            url: result.url,
            name: result.originalFilename || result.fileName || ''
          })
        } catch (e) {
          // uploadFile 内部已 toast
        }
      }

      self.$set(self.uploadingState, key, 0)
      self.$delete(self.uploadingState, key)

      // 提交文件列表
      var urls = currentList.map(function(item) { return item.url || item })
      self.$emit('input', key, urls)
      self.$set(self.touchedFields, key, true)
    },

    removeFile(field, index) {
      var key = this.getFieldKey(field)
      var list = this.normalizeFileList(this.getFieldValue(field))
      var next = list.filter(function(_, i) { return i !== index }).map(function(item) { return item.url || item })
      this.$emit('input', key, next)
    },

    /** 预览文件 */
    previewFile(item, allFiles) {
      var url = item.url || item
      var ext = getExtension(url)

      if (IMAGE_EXTS.indexOf(ext) >= 0) {
        var urls = allFiles
          .map(function(f) { return f.url || f })
          .filter(function(u) { return IMAGE_EXTS.indexOf(getExtension(u)) >= 0 })
        uni.previewImage({
          current: url,
          urls: urls
        })
      } else if (VIDEO_EXTS.indexOf(ext) >= 0) {
        // #ifdef MP-WEIXIN
        uni.openDocument && uni.previewMedia && uni.previewMedia({
          sources: [{ url: url, type: 'video' }]
        })
        // #endif
        // #ifndef MP-WEIXIN
        uni.navigateTo({ url: '/pages-sub/common/video-player?url=' + encodeURIComponent(url) })
        // #endif
      } else {
        // PDF 或其他文件：下载后打开
        uni.showLoading({ title: '打开中...' })
        uni.downloadFile({
          url: url,
          success: function(res) {
            uni.hideLoading()
            uni.openDocument({
              filePath: res.tempFilePath,
              showMenu: true,
              fail: function() {
                uni.showToast({ title: '无法打开该文件', icon: 'none' })
              }
            })
          },
          fail: function() {
            uni.hideLoading()
            uni.showToast({ title: '下载失败', icon: 'none' })
          }
        })
      }
    },

    isImageUrl(url) {
      return IMAGE_EXTS.indexOf(getExtension(url)) >= 0
    },

    getFileIcon(url) {
      var ext = getExtension(url)
      if (VIDEO_EXTS.indexOf(ext) >= 0) return '🎬'
      if (ext === 'pdf') return '📄'
      if (['doc', 'docx'].indexOf(ext) >= 0) return '📝'
      if (['xls', 'xlsx'].indexOf(ext) >= 0) return '📊'
      if (['mp3', 'wav', 'aac'].indexOf(ext) >= 0) return '🎵'
      return '📎'
    },

    getFileName(item) {
      if (item.name) return item.name
      var url = item.url || item
      var parts = url.split('/')
      return parts[parts.length - 1] || '文件'
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-dynamic-form__group {
  margin-bottom: $jst-space-lg;
}

.jst-dynamic-form__group-header {
  margin-bottom: $jst-space-md;
}

.jst-dynamic-form__group-title {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.jst-dynamic-form__group-desc,
.jst-dynamic-form__desc {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.jst-dynamic-form__field {
  margin-bottom: $jst-space-lg;
}

.jst-dynamic-form__field:last-child {
  margin-bottom: 0;
}

.jst-dynamic-form__label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $jst-space-md;
  margin-bottom: $jst-space-sm;
}

.jst-dynamic-form__label {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.jst-dynamic-form__required {
  margin-right: 6rpx;
  color: $jst-danger;
}

.jst-dynamic-form__tip {
  font-size: $jst-font-xs;
  color: $jst-brand;
}

// ── 输入框 ──
.jst-dynamic-form__input-wrap,
.jst-dynamic-form__textarea-wrap,
.jst-dynamic-form__picker-inner,
.jst-dynamic-form__readonly {
  box-sizing: border-box;
  width: 100%;
  padding: 0 $jst-space-lg;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-xl;
  background: $jst-bg-card;
  transition: border-color $jst-duration-fast $jst-easing;
}

.jst-dynamic-form__input-wrap,
.jst-dynamic-form__picker-inner,
.jst-dynamic-form__readonly {
  min-height: 92rpx;
  display: flex;
  align-items: center;
}

.jst-dynamic-form__input-wrap--error,
.jst-dynamic-form__textarea-wrap--error,
.jst-dynamic-form__picker-inner--error {
  border-color: $jst-danger;
  background: $jst-danger-light;
}

.jst-dynamic-form__input {
  flex: 1;
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

.jst-dynamic-form__textarea-wrap {
  position: relative;
  padding: $jst-space-lg;
}

.jst-dynamic-form__textarea {
  width: 100%;
  min-height: 200rpx;
  font-size: $jst-font-base;
  line-height: 1.7;
  color: $jst-text-primary;
}

.jst-dynamic-form__textarea-count {
  display: block;
  text-align: right;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
  margin-top: $jst-space-xs;
}

.jst-dynamic-form__readonly-text,
.jst-dynamic-form__picker-text {
  flex: 1;
  font-size: $jst-font-base;
  line-height: 1.7;
  color: $jst-text-primary;
}

.jst-dynamic-form__placeholder {
  color: $jst-text-placeholder;
  font-size: $jst-font-base;
}

.jst-dynamic-form__picker-arrow {
  margin-left: $jst-space-sm;
  color: $jst-text-placeholder;
  font-size: $jst-font-sm;
  font-family: sans-serif;
}

// ── 选项类（radio/checkbox）──
.jst-dynamic-form__option-list {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-md;
}

.jst-dynamic-form__option {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  min-width: 160rpx;
  padding: 20rpx $jst-space-lg;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-xl;
  background: $jst-bg-card;
  transition: border-color $jst-duration-fast $jst-easing, background $jst-duration-fast $jst-easing;
}

.jst-dynamic-form__option--active {
  border-color: $jst-brand;
  background: $jst-brand-light;
}

.jst-dynamic-form__radio-dot {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid $jst-border;
  border-radius: 50%;
  flex-shrink: 0;
  transition: border-color $jst-duration-fast $jst-easing;
}

.jst-dynamic-form__radio-dot--active {
  border-color: $jst-brand;
  background: radial-gradient(circle, $jst-brand 0, $jst-brand 44%, transparent 46%);
}

.jst-dynamic-form__check-box {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-sm;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $jst-duration-fast $jst-easing;
}

.jst-dynamic-form__check-box--active {
  border-color: $jst-brand;
  background: $jst-brand;
}

.jst-dynamic-form__check-icon {
  font-size: 22rpx;
  color: $jst-text-inverse;
  font-weight: $jst-weight-bold;
}

.jst-dynamic-form__option-text {
  font-size: $jst-font-sm;
  line-height: 1.5;
  color: $jst-text-primary;
}

// ── 文件上传 ──
.jst-dynamic-form__upload {
  margin-top: $jst-space-xs;
}

.jst-dynamic-form__file-list {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-md;
  margin-bottom: $jst-space-md;
}

.jst-dynamic-form__file-list--readonly {
  margin-bottom: 0;
}

.jst-dynamic-form__file-item {
  position: relative;
  width: 180rpx;
  border-radius: $jst-radius-lg;
  overflow: hidden;
  background: $jst-bg-grey;
}

.jst-dynamic-form__file-thumb {
  display: block;
  width: 180rpx;
  height: 180rpx;
  border-radius: $jst-radius-lg;
}

.jst-dynamic-form__file-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 180rpx;
  height: 180rpx;
}

.jst-dynamic-form__file-icon-text {
  font-size: 64rpx;
}

.jst-dynamic-form__file-name {
  display: block;
  padding: $jst-space-xs $jst-space-sm;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.jst-dynamic-form__file-remove {
  position: absolute;
  top: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  color: $jst-text-inverse;
  font-size: $jst-font-md;
  border-bottom-left-radius: $jst-radius-md;
}

// ── 上传进度 ──
.jst-dynamic-form__upload-progress {
  margin-bottom: $jst-space-md;
}

.jst-dynamic-form__progress-bar {
  height: 8rpx;
  background: $jst-bg-grey;
  border-radius: 4rpx;
  overflow: hidden;
}

.jst-dynamic-form__progress-inner {
  height: 100%;
  background: $jst-brand;
  border-radius: 4rpx;
  transition: width $jst-duration-fast linear;
}

.jst-dynamic-form__progress-text {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-brand;
}

// ── 上传按钮 ──
.jst-dynamic-form__upload-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 160rpx;
  border: 2rpx dashed $jst-border;
  border-radius: $jst-radius-xl;
  background: $jst-bg-grey;
  transition: border-color $jst-duration-fast $jst-easing;
}

.jst-dynamic-form__upload-action--error {
  border-color: $jst-danger;
}

.jst-dynamic-form__upload-action-icon {
  font-size: 56rpx;
  color: $jst-text-placeholder;
  line-height: 1;
}

.jst-dynamic-form__upload-action-text {
  margin-top: $jst-space-xs;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}

.jst-dynamic-form__upload-action-sub {
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

// ── 校验错误 ──
.jst-dynamic-form__error {
  margin-top: $jst-space-xs;
  padding: 0 $jst-space-sm;
}

.jst-dynamic-form__error-text {
  font-size: $jst-font-xs;
  color: $jst-danger;
  line-height: 1.5;
}
</style>
