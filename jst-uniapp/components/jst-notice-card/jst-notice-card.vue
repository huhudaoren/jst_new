<template>
  <view class="notice-card" @tap="handleTap">
    <view class="notice-card__header">
      <image v-if="notice.coverImage" class="notice-card__cover" :src="notice.coverImage" mode="aspectFill" />
      <view v-else class="notice-card__cover notice-card__cover--fallback">
        <text class="notice-card__cover-icon">📰</text>
      </view>

      <view class="notice-card__content">
        <view class="notice-card__tags">
          <text v-if="notice.topFlag" class="notice-card__tag notice-card__tag--top">置顶</text>
          <text class="notice-card__tag">{{ notice.categoryLabel || '公告资讯' }}</text>
        </view>
        <text class="notice-card__title">{{ notice.title }}</text>
        <text class="notice-card__summary">{{ notice.summary || '点击查看公告详情' }}</text>
      </view>
    </view>

    <view class="notice-card__footer">
      <text class="notice-card__time">{{ formatDate(notice.publishTime) }}</text>
      <text class="notice-card__arrow">查看详情</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstNoticeCard',
  props: {
    notice: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    handleTap() {
      this.$emit('tap', this.notice)
    },
    formatDate(value) {
      if (!value) {
        return '--'
      }
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return value
      }
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped lang="scss">
.notice-card {
  overflow: hidden;
  border-radius: var(--jst-radius-md);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.notice-card__header {
  display: flex;
  align-items: flex-start;
  padding: 24rpx;
}

.notice-card__cover {
  flex-shrink: 0;
  width: 132rpx;
  height: 132rpx;
  border-radius: 20rpx;
  background: var(--jst-color-border);
}

.notice-card__cover--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(140deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
}

.notice-card__cover-icon {
  font-size: 52rpx;
}

.notice-card__content {
  flex: 1;
  min-width: 0;
  margin-left: 20rpx;
}

.notice-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.notice-card__tag {
  padding: 6rpx 14rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-brand-soft);
  font-size: 20rpx;
  color: var(--jst-color-brand);
}

.notice-card__tag--top {
  background: var(--jst-color-primary-soft);
  color: var(--jst-color-primary);
}

.notice-card__title {
  display: block;
  margin-top: 16rpx;
  font-size: 28rpx;
  font-weight: 700;
  line-height: 1.5;
  color: var(--jst-color-text);
}

.notice-card__summary {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.notice-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  border-top: 2rpx solid var(--jst-color-border);
}

.notice-card__time {
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.notice-card__arrow {
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}
</style>