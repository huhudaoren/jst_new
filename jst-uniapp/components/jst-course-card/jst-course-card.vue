<template>
  <view class="jst-course-card" @tap="handleTap">
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
    }
  },
  methods: {
    handleTap() {
      this.$emit('tap', this.course)
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
.jst-course-card {
  overflow: hidden;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.jst-course-card__cover {
  position: relative;
  height: 280rpx;
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
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
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
}

.jst-course-card__cover-fallback--ai_maic {
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
}

.jst-course-card__cover-emoji {
  font-size: 64rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
  letter-spacing: 4rpx;
}

.jst-course-card__badge {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
}

.jst-course-card__body {
  padding: 28rpx 24rpx 24rpx;
}

.jst-course-card__title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.5;
  color: var(--jst-color-text);
}

.jst-course-card__desc {
  display: -webkit-box;
  margin-top: 14rpx;
  overflow: hidden;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.jst-course-card__footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: 24rpx;
}

.jst-course-card__price-block {
  display: flex;
  flex-direction: column;
}

.jst-course-card__price {
  font-size: 34rpx;
  font-weight: 700;
  color: var(--jst-color-primary);
  line-height: 1.2;
}

.jst-course-card__price--free {
  color: var(--jst-color-success);
}

.jst-course-card__points {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.jst-course-card__action {
  font-size: 24rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}
</style>