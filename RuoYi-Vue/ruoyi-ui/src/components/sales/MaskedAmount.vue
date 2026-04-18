<template>
  <span v-if="value != null" class="masked-amount">{{ prefix }}{{ formatted }}</span>
  <span v-else class="masked-amount masked-amount--hidden">由 HR 发放</span>
</template>

<script>
export default {
  name: 'MaskedAmount',
  props: {
    /** 金额数值；null 表示后端已脱敏（@Sensitive 字段） */
    value: {
      type: [Number, String],
      default: null
    },
    /** 货币前缀，默认 '¥' */
    prefix: {
      type: String,
      default: '¥'
    }
  },
  computed: {
    formatted() {
      if (this.value == null) return ''
      const n = parseFloat(this.value)
      if (isNaN(n)) return this.value
      return n.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }
  }
}
</script>

<style scoped>
.masked-amount {
  font-variant-numeric: tabular-nums;
}
.masked-amount--hidden {
  color: #909399;
  font-style: italic;
  font-size: 12px;
}
</style>
