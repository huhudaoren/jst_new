<template>
  <el-row :gutter="16" class="stat-card-group">
    <el-col v-for="card in cards" :key="card.label" :xs="12" :sm="6">
      <div class="stat-card" :class="[`stat-card--${card.theme || 'default'}`]">
        <div class="stat-card__label">{{ card.label }}</div>
        <div class="stat-card__value">
          <span v-if="card.prefix">{{ card.prefix }}</span>{{ formatValue(card.value, card.format) }}
        </div>
        <div v-if="card.trend !== undefined" class="stat-card__trend"
             :class="card.trend > 0 ? 'stat-card__trend--up' : 'stat-card__trend--down'">
          <i :class="card.trend > 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'"></i>
          {{ Math.abs(card.trend) }}%
        </div>
        <div v-if="card.hint" class="stat-card__hint">{{ card.hint }}</div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'StatCardGroup',
  props: { cards: { type: Array, required: true } },
  methods: {
    formatValue(v, format) {
      if (v == null) return '-'
      if (format === 'money') return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
      if (format === 'percent') return (Number(v) * 100).toFixed(1) + '%'
      if (format === 'number') return Number(v).toLocaleString('zh-CN')
      return v
    }
  }
}
</script>

<style scoped>
.stat-card-group { margin-bottom: 16px; }
.stat-card { padding: 20px; background: #fff; border-radius: 4px; border-left: 4px solid #409eff; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.stat-card--green { border-left-color: #67c23a; }
.stat-card--orange { border-left-color: #e6a23c; }
.stat-card--red { border-left-color: #f56c6c; }
.stat-card__label { color: #909399; font-size: 13px; }
.stat-card__value { font-size: 24px; font-weight: 600; color: #303133; margin-top: 6px; }
.stat-card__trend { margin-top: 4px; font-size: 12px; }
.stat-card__trend--up { color: #67c23a; }
.stat-card__trend--down { color: #f56c6c; }
.stat-card__hint { margin-top: 4px; color: #c0c4cc; font-size: 12px; }
</style>
