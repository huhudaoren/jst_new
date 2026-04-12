<template>
  <el-tag :type="tagType" size="small">{{ label }}</el-tag>
</template>

<script>
export default {
  name: 'JstStatusBadge',
  props: {
    status: { type: String, default: '' },
    statusMap: { type: Object, default: () => ({}) }
  },
  computed: {
    tagType() {
      return (this.statusMap[this.status] || {}).type || 'info'
    },
    label() {
      return (this.statusMap[this.status] || {}).label || this.status
    }
  }
}
</script>
