<template>
  <div class="base-picker">
    <el-select
      :value="value"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :filterable="true"
      :remote="true"
      :remote-method="doSearch"
      :loading="loading"
      size="medium"
      style="width: 100%;"
      @change="onChange"
      @visible-change="onFocus"
    >
      <el-option
        v-for="item in options"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      >
        <div class="base-picker__option">
          <span class="base-picker__option-name">{{ item.name }}</span>
          <span v-if="item.subtitle" class="base-picker__option-sub">{{ item.subtitle }}</span>
          <el-tag v-if="item.statusTag" size="mini" :type="statusTagType(item.statusTag)">
            {{ item.statusTag }}
          </el-tag>
        </div>
      </el-option>
      <template #empty>
        <div class="base-picker__empty">
          <span>没有匹配的数据</span>
        </div>
      </template>
    </el-select>
    <a
      v-if="showDetailLink && value && selectedName"
      class="base-picker__detail-link"
      @click="onDetailClick"
    >查看详情 →</a>
  </div>
</template>

<script>
import request from '@/utils/request'
import { resolveEntityRoute } from '@/utils/entityRouteMap'

export default {
  name: 'BasePicker',
  props: {
    value: { type: [Number, String], default: null },
    entity: { type: String, required: true },
    placeholder: { type: String, default: '请选择（输入名称搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  },
  data() {
    return {
      options: [],
      loading: false,
      selectedName: ''
    }
  },
  watch: {
    value: { immediate: true, handler: 'hydrateSelected' }
  },
  methods: {
    async hydrateSelected(v) {
      if (!v) { this.selectedName = ''; return }
      const existing = this.options.find(o => o.id === v)
      if (existing) { this.selectedName = existing.name; return }
      try {
        const res = await request({
          url: '/admin/entity/brief',
          method: 'get',
          params: { type: this.entity, id: v }
        })
        if (res.data) {
          this.selectedName = res.data.name
          if (!this.options.find(o => o.id === v)) this.options.unshift(res.data)
        }
      } catch (e) { /* ignore */ }
    },
    async doSearch(kw) {
      this.loading = true
      try {
        const res = await request({
          url: '/admin/entity/search',
          method: 'get',
          params: { type: this.entity, kw, limit: 30 }
        })
        this.options = res.data || []
      } finally { this.loading = false }
    },
    async onFocus(visible) {
      if (visible && !this.options.length) await this.doSearch('')
    },
    onChange(id) {
      this.$emit('input', id)
      const picked = this.options.find(o => o.id === id)
      this.selectedName = picked ? picked.name : ''
      this.$emit('change', picked || null)
    },
    onDetailClick() {
      const r = resolveEntityRoute(this.entity, this.value)
      if (r) this.$router.push(r.path)
    },
    statusTagType(s) {
      const map = { active: 'success', pending: 'warning', rejected: 'danger', '0': 'success', '1': 'info' }
      return map[s] || ''
    }
  }
}
</script>

<style scoped>
.base-picker__option { display: flex; align-items: center; gap: 8px; }
.base-picker__option-name { flex: 1; }
.base-picker__option-sub { color: #909399; font-size: 12px; }
.base-picker__detail-link { display: inline-block; margin-top: 4px; font-size: 12px; color: #409eff; cursor: pointer; }
.base-picker__empty { padding: 16px; text-align: center; color: #909399; font-size: 13px; }
</style>
