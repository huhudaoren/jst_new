<template>
  <view class="jst-course-card" :class="{ 'jst-course-card--compact': compact }" @tap="handleTap">
    <view class="jst-course-card__cover">
      <image v-if="course.coverImage" class="jst-course-card__cover-image" :src="course.coverImage" mode="aspectFill" />
      <view v-else class="jst-course-card__cover-fallback" :class="[`jst-course-card__cover-fallback--${course.courseType || 'normal'}`]">
        <text class="jst-course-card__cover-emoji">{{ getCoverEmoji() }}</text>
      </view>

      <view class="jst-course-card__badge">
        <jst-status-badge :text="getTypeText()" :tone="getTypeTone()" />
      </view>
    </view>

    <view class="jst-course-card__body">
      <text class="jst-course-card__title">{{ course.courseName || '课程名称待完善' }}</text>
      <text class="jst-course-card__desc">{{ course.description || '课程简介整理中' }}</text>

      <view class="jst-course-card__footer">
        <view class="jst-course-card__price-block">
          <text v-if="Number(course.price || 0) > 0" class="jst-course-card__price">{{ formatCashPrice(course.price) }}</text>
          <text v-else class="jst-course-card__price jst-course-card__price--free">免费</text>
          <text v-if="Number(course.pointsPrice || 0) > 0" class="jst-course-card__points">{{ formatPointsPrice(course.pointsPrice) }}</text>
        </view>
        <text class="jst-course-card__action">查看详情</text>
      </view>
    </view>
  </view>
</template>

<script>
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

export default {
  name: 'JstCourseCard',
  components: { JstStatusBadge },
  props: {
    course: {
      type: Object,
      default() {
        return {}
      }
    },
    compact: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleTap() {
      this.$emit('item-tap', this.course)
    },
    getTypeText() {
      return this.course.courseType === 'ai_maic' ? 'AI课程' : '普通课程'
    },
    getTypeTone() {
      return this.course.courseType === 'ai_maic' ? 'accent' : 'primary'
    },
    getCoverEmoji() {
      return this.course.courseType === 'ai_maic' ? 'AI' : '课'
    },
    formatCashPrice(value) {
      return `¥${Number(value || 0).toFixed(2)}`
    },
    formatPointsPrice(value) {
      return `${Number(value || 0)} 积分`
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-course-card {
  overflow: hidden;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  transition: transform $jst-duration-fast $jst-easing;
}
.jst-course-card:active { transform: scale(0.98); }

.jst-course-card__cover {
  position: relative;
  height: 280rpx;
  background: $jst-brand-gradient;
}

.jst-course-card__cover-image,
.jst-course-card__cover-fallback {
  width: 100%;
  height: 100%;
}

.jst-course-card__cover-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.jst-course-card__cover-fallback--normal {
  background: $jst-brand-gradient;
}

.jst-course-card__cover-fallback--ai_maic {
  background: linear-gradient(135deg, $jst-danger 0%, lighten($jst-danger, 15%) 100%);
}

.jst-course-card__cover-emoji {
  font-size: 64rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  letter-spacing: 4rpx;
}

.jst-course-card__badge {
  position: absolute;
  top: $jst-space-lg;
  left: $jst-space-lg;
}

.jst-course-card__body {
  padding: $jst-space-lg;
}

.jst-course-card__title {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  line-height: 1.5;
  color: $jst-text-primary;
}

.jst-course-card__desc {
  display: -webkit-box;
  margin-top: $jst-space-md;
  overflow: hidden;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-regular;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.jst-course-card__footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: $jst-space-lg;
}

.jst-course-card__price-block {
  display: flex;
  flex-direction: column;
}

.jst-course-card__price {
  font-size: $jst-font-xl;
  font-weight: $jst-weight-bold;
  color: $jst-danger;
  line-height: 1.2;
}

.jst-course-card__price--free {
  color: $jst-success;
}

.jst-course-card__points {
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.jst-course-card__action {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}

// 紧凑版 — 横向滚动卡片
.jst-course-card--compact {
  width: 320rpx;
  min-width: 320rpx;

  .jst-course-card__cover {
    height: 200rpx;
  }

  .jst-course-card__body {
    padding: $jst-space-md;
  }

  .jst-course-card__title {
    font-size: $jst-font-sm;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .jst-course-card__desc {
    display: none;
  }

  .jst-course-card__footer {
    margin-top: $jst-space-sm;
  }

  .jst-course-card__price {
    font-size: $jst-font-md;
  }

  .jst-course-card__action {
    display: none;
  }
}
</style>
