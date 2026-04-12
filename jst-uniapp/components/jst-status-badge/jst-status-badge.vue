<template>
  <view class="jst-status-badge">
    <u-tag
      :text="text"
      :type="uTagType"
      plain
      size="mini"
      shape="circle"
      :plain-fill="true"
    />
  </view>
</template>

<script>
export default {
  name: 'JstStatusBadge',
  props: {
    text: {
      type: String,
      default: ''
    },
    tone: {
      type: String,
      default: 'primary'
    }
  },
  computed: {
    uTagType() {
      const map = {
        primary: 'primary',
        accent: 'error',
        success: 'success',
        warning: 'warning',
        danger: 'error',
        gray: 'info'
      }
      return map[this.tone] || 'primary'
    }
  }
}
</script>

<style scoped lang="scss">
.jst-status-badge {
  display: inline-flex;
  align-items: center;
}
</style>
