<template>
  <view class="notice-card" @tap="handleTap">
    <view class="notice-card__header">
      <image v-if="notice.coverImage" class="notice-card__cover" :src="notice.coverImage" mode="aspectFill" />
      <view v-else class="notice-card__cover notice-card__cover--fallback">
        <text class="notice-card__cover-icon">📰</text>
      </view>

      <view class="notice-card__content">
        <view class="notice-card__tags">
          <u-tag
            v-if="notice.topFlag"
            text="置顶"
            type="error"
            plain
            :plain-fill="true"
            size="mini"
            shape="circle"
          />
          <u-tag
            :text="notice.categoryLabel || '公告资讯'"
            type="primary"
            plain
            :plain-fill="true"
            size="mini"
            shape="circle"
          />
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
      this.$emit('item-tap', this.notice)
    },
    formatDate(value) {
      if (!value) {
        return '--'
      }
      const date = new Date(String(value).replace(/ /g, 'T'))
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
@import '@/styles/design-tokens.scss';

.notice-card {
  overflow: hidden;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  transition: transform $jst-duration-fast $jst-easing;
}
.notice-card:active { transform: scale(0.99); }

.notice-card__header {
  display: flex;
  align-items: flex-start;
  padding: $jst-space-lg;
}

.notice-card__cover {
  flex-shrink: 0;
  width: 132rpx;
  height: 132rpx;
  border-radius: $jst-radius-lg;
  background: $jst-border;
}

.notice-card__cover--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: $jst-brand-gradient;
}

.notice-card__cover-icon {
  font-size: 52rpx;
}

.notice-card__content {
  flex: 1;
  min-width: 0;
  margin-left: $jst-space-lg;
}

.notice-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-xs;
}

.notice-card__title {
  display: block;
  margin-top: $jst-space-md;
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
  line-height: 1.5;
  color: $jst-text-primary;
}

.notice-card__summary {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xs;
  line-height: 1.7;
  color: $jst-text-regular;
}

.notice-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg;
  border-top: 1rpx solid $jst-border;
}

.notice-card__time {
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.notice-card__arrow {
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}
</style>
