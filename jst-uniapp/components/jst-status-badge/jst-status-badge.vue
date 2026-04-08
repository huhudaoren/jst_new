<template>
  <view class="jst-status-badge" :class="[`jst-status-badge--${tone}`]">
    <text class="jst-status-badge__text">{{ text }}</text>
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
  }
}
</script>

<style scoped lang="scss">
.jst-status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 40rpx;
  padding: 0 16rpx;
  border-radius: var(--jst-radius-full);
}

.jst-status-badge__text {
  font-size: 22rpx;
  font-weight: 600;
  line-height: 1.4;
}

.jst-status-badge--primary {
  background: var(--jst-color-brand-soft);
}

.jst-status-badge--primary .jst-status-badge__text {
  color: var(--jst-color-brand);
}

.jst-status-badge--accent {
  background: var(--jst-color-primary-soft);
}

.jst-status-badge--accent .jst-status-badge__text {
  color: var(--jst-color-primary);
}

.jst-status-badge--success {
  background: var(--jst-color-success-soft);
}

.jst-status-badge--success .jst-status-badge__text {
  color: var(--jst-color-success);
}

.jst-status-badge--warning {
  background: var(--jst-color-warning-soft);
}

.jst-status-badge--warning .jst-status-badge__text {
  color: var(--jst-color-warning);
}

.jst-status-badge--danger {
  background: var(--jst-color-danger-soft);
}

.jst-status-badge--danger .jst-status-badge__text {
  color: var(--jst-color-danger);
}

.jst-status-badge--gray {
  background: var(--jst-color-gray-soft);
}

.jst-status-badge--gray .jst-status-badge__text {
  color: var(--jst-color-text-tertiary);
}
</style>