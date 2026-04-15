<template>
  <div class="json-display">
    <!-- 空值 -->
    <span v-if="!parsed" class="json-display__empty">{{ emptyText }}</span>

    <!-- 附件列表（数组且含文件信息） -->
    <div v-else-if="isFileList" class="json-display__files">
      <div v-for="(file, i) in parsed" :key="i" class="json-display__file-item">
        <el-image
          v-if="isImage(file)"
          :src="file.url || file.fileUrl"
          :preview-src-list="imageUrls"
          fit="cover"
          class="json-display__thumb"
        />
        <i v-else class="el-icon-document json-display__file-icon" />
        <span class="json-display__file-name">{{ file.name || file.fileName || file.url || ('附件' + (i + 1)) }}</span>
        <el-link v-if="file.url || file.fileUrl" :href="file.url || file.fileUrl" target="_blank" type="primary" :underline="false" class="json-display__file-link">下载</el-link>
      </div>
    </div>

    <!-- 普通数组 → tags -->
    <div v-else-if="isArray" class="json-display__tags">
      <el-tag v-for="(item, i) in parsed" :key="i" size="small" class="json-display__tag">
        {{ typeof item === 'string' ? item : (item.name || item.label || item.value || JSON.stringify(item)) }}
      </el-tag>
    </div>

    <!-- 对象 → descriptions -->
    <el-descriptions v-else-if="isObject" :column="column" border size="small">
      <el-descriptions-item v-for="(val, key) in parsed" :key="key" :label="getLabel(key)">
        <template v-if="isSimple(val)">{{ formatValue(val) }}</template>
        <template v-else>{{ JSON.stringify(val) }}</template>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 降级：原样字符串 -->
    <span v-else class="json-display__raw">{{ String(parsed) }}</span>
  </div>
</template>

<script>
export default {
  name: 'JsonDisplay',
  props: {
    value: { type: [String, Object, Array], default: null },
    labelMap: { type: Object, default: () => ({}) },
    column: { type: Number, default: 1 },
    emptyText: { type: String, default: '--' }
  },
  computed: {
    parsed() {
      if (this.value === null || this.value === undefined || this.value === '') return null
      if (typeof this.value === 'object') return this.value
      try { return JSON.parse(this.value) } catch (_) { return this.value }
    },
    isArray() {
      return Array.isArray(this.parsed)
    },
    isObject() {
      return this.parsed && typeof this.parsed === 'object' && !Array.isArray(this.parsed)
    },
    isFileList() {
      if (!this.isArray || !this.parsed.length) return false
      return this.parsed.some(item => item && typeof item === 'object' && (item.url || item.fileUrl || item.fileName))
    },
    imageUrls() {
      if (!this.isFileList) return []
      return this.parsed.filter(f => this.isImage(f)).map(f => f.url || f.fileUrl)
    }
  },
  methods: {
    getLabel(key) {
      return this.labelMap[key] || key
    },
    isImage(file) {
      const url = file.url || file.fileUrl || file.name || ''
      return /\.(jpe?g|png|gif|webp|bmp|svg)(\?|$)/i.test(url)
    },
    isSimple(val) {
      return val === null || val === undefined || typeof val !== 'object'
    },
    formatValue(val) {
      if (val === null || val === undefined) return '--'
      if (typeof val === 'boolean') return val ? '是' : '否'
      return String(val)
    }
  }
}
</script>

<style scoped>
.json-display__empty {
  color: #c0c4cc;
}
.json-display__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.json-display__tag {
  max-width: 240px;
  overflow: hidden;
  text-overflow: ellipsis;
}
.json-display__files {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.json-display__file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  background: #f8fbff;
  border: 1px solid #e7eff8;
  border-radius: 6px;
}
.json-display__thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  flex-shrink: 0;
}
.json-display__file-icon {
  font-size: 20px;
  color: #909399;
  flex-shrink: 0;
}
.json-display__file-name {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.json-display__file-link {
  flex-shrink: 0;
  font-size: 12px;
}
.json-display__raw {
  font-size: 13px;
  color: #606266;
  word-break: break-all;
}
</style>
