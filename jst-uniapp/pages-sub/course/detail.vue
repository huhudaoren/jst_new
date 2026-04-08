<!-- 中文注释: 课程详情页；对应原型 小程序原型图/course-detail.html；对应接口 GET /jst/wx/course/{id} -->
<template>
  <view class="course-detail-page">
    <jst-loading :loading="pageLoading" text="课程详情加载中..." />

    <view class="course-detail-page__hero">
      <image
        v-if="detail.coverImage"
        class="course-detail-page__hero-image"
        :src="detail.coverImage"
        mode="aspectFill"
      />
      <view v-else class="course-detail-page__hero-fallback" :class="[`course-detail-page__hero-fallback--${detail.courseType || 'normal'}`]">
        <text class="course-detail-page__hero-emoji">{{ getCoverEmoji() }}</text>
      </view>

      <view class="course-detail-page__nav">
        <view class="course-detail-page__back" @tap="handleBack">←</view>
      </view>
    </view>

    <view v-if="!loadError" class="course-detail-page__content">
      <view class="course-detail-page__card">
        <view class="course-detail-page__title-row">
          <text class="course-detail-page__title">{{ detail.courseName || '课程名称待完善' }}</text>
          <view class="course-detail-page__badge">
            <jst-status-badge :text="getTypeText()" :tone="getTypeTone()" />
          </view>
        </view>

        <view class="course-detail-page__price-row">
          <text v-if="Number(detail.price || 0) > 0" class="course-detail-page__price">
            {{ formatCashPrice(detail.price) }}
          </text>
          <text v-else class="course-detail-page__price course-detail-page__price--free">免费</text>
          <text v-if="Number(detail.pointsPrice || 0) > 0" class="course-detail-page__points">
            {{ formatPointsPrice(detail.pointsPrice) }}
          </text>
        </view>
      </view>

      <view class="course-detail-page__section">
        <text class="course-detail-page__section-title">课程介绍</text>
        <rich-text class="course-detail-page__rich-text" :nodes="richDescription"></rich-text>
      </view>
    </view>

    <jst-empty
      v-else
      text="课程详情暂不可用，待后端 F-COURSE 完成后联调"
      icon="📖"
    />

    <view v-if="!loadError" class="course-detail-page__bottom">
      <view class="course-detail-page__bottom-price">
        <text class="course-detail-page__bottom-label">当前价格</text>
        <text v-if="Number(detail.price || 0) > 0" class="course-detail-page__bottom-value">
          {{ formatCashPrice(detail.price) }}
        </text>
        <text v-else class="course-detail-page__bottom-value course-detail-page__bottom-value--free">免费</text>
      </view>

      <button class="course-detail-page__buy" @tap="showBuyComingSoon">立即购买</button>
    </view>
  </view>
</template>

<script>
import { getCourseDetail } from '@/api/course'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

export default {
  components: {
    JstEmpty,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      courseId: '',
      pageLoading: false,
      loadError: false,
      detail: {
        courseName: '',
        courseType: 'normal',
        coverImage: '',
        description: '',
        price: 0,
        pointsPrice: 0
      },
      richDescription: ''
    }
  },
  onLoad(options) {
    this.courseId = options.id || ''
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      if (!this.courseId) {
        this.loadError = true
        return
      }

      this.pageLoading = true

      try {
        const detail = await getCourseDetail(this.courseId, { silent: true })
        this.detail = this.normalizeDetail(detail || {})
        this.richDescription = this.buildRichDescription(this.detail.description)
        this.loadError = false
      } catch (error) {
        this.loadError = true
      } finally {
        this.pageLoading = false
      }
    },

    normalizeDetail(detail) {
      return {
        courseId: detail.courseId || this.courseId,
        courseName: detail.courseName || '',
        courseType: detail.courseType || 'normal',
        coverImage: detail.coverImage || '',
        description: detail.description || '',
        price: detail.price || 0,
        pointsPrice: detail.pointsPrice || 0
      }
    },

    // 中文注释: rich-text 直接吃后端 HTML，这里做最小清洗，去掉 script/事件属性，避免页面执行恶意脚本。
    buildRichDescription(content) {
      const source = `${content || ''}`
      const sanitized = source
        .replace(/<\s*(script|style|iframe|object|embed|link|meta)[^>]*>[\s\S]*?<\s*\/\s*\1\s*>/gi, '')
        .replace(/\son[a-z]+\s*=\s*(['"]).*?\1/gi, '')
        .replace(/\son[a-z]+\s*=\s*[^\s>]+/gi, '')
        .replace(/javascript:/gi, '')
        .replace(/<img/gi, '<img style="max-width:100%;height:auto;display:block;border-radius:var(--jst-radius-md);margin:16rpx 0;"')
        .replace(/<p/gi, '<p style="font-size:28rpx;line-height:1.9;color:var(--jst-color-text);margin:0 0 20rpx;"')
        .replace(/<ul/gi, '<ul style="padding-left:32rpx;margin:0 0 20rpx;font-size:28rpx;line-height:1.9;color:var(--jst-color-text);"')
        .replace(/<ol/gi, '<ol style="padding-left:32rpx;margin:0 0 20rpx;font-size:28rpx;line-height:1.9;color:var(--jst-color-text);"')
        .replace(/<li/gi, '<li style="margin-bottom:12rpx;"')

      return sanitized || '<p style="font-size:28rpx;line-height:1.9;color:var(--jst-color-text-secondary);">课程介绍整理中</p>'
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }

      uni.switchTab({ url: '/pages/course/list' })
    },

    showBuyComingSoon() {
      uni.showToast({
        title: '购买功能 F9 完成后开放',
        icon: 'none'
      })
    },

    getTypeText() {
      return this.detail.courseType === 'ai_maic' ? 'AI 课程' : '普通课程'
    },

    getTypeTone() {
      return this.detail.courseType === 'ai_maic' ? 'accent' : 'primary'
    },

    getCoverEmoji() {
      return this.detail.courseType === 'ai_maic' ? 'AI' : '课'
    },

    formatCashPrice(value) {
      const price = Number(value || 0)
      return `¥${price.toFixed(2)}`
    },

    formatPointsPrice(value) {
      return `${Number(value || 0)} 积分`
    }
  }
}
</script>

<style scoped lang="scss">
.course-detail-page {
  min-height: 100vh;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-page-bg);
}

.course-detail-page__hero {
  position: relative;
  height: 420rpx;
  overflow: hidden;
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
}

.course-detail-page__hero-image,
.course-detail-page__hero-fallback {
  width: 100%;
  height: 100%;
}

.course-detail-page__hero-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.course-detail-page__hero-fallback--normal {
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
}

.course-detail-page__hero-fallback--ai_maic {
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
}

.course-detail-page__hero-emoji {
  font-size: 88rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
  letter-spacing: 6rpx;
}

.course-detail-page__nav {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx 0;
}

.course-detail-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: var(--jst-color-white-18);
  font-size: 34rpx;
  color: var(--jst-color-card-bg);
}

.course-detail-page__content {
  padding: 0 32rpx;
}

.course-detail-page__card {
  margin-top: -64rpx;
  padding: 32rpx 24rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.course-detail-page__title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.course-detail-page__title {
  flex: 1;
  font-size: 36rpx;
  font-weight: 700;
  line-height: 1.5;
  color: var(--jst-color-text);
}

.course-detail-page__badge {
  margin-left: 20rpx;
}

.course-detail-page__price-row {
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  margin-top: 24rpx;
}

.course-detail-page__price {
  font-size: 44rpx;
  font-weight: 700;
  color: var(--jst-color-primary);
}

.course-detail-page__price--free {
  color: var(--jst-color-success);
}

.course-detail-page__points {
  margin-left: 16rpx;
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
}

.course-detail-page__section {
  margin-top: 24rpx;
  padding: 32rpx 24rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.course-detail-page__section-title {
  display: block;
  margin-bottom: 20rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.course-detail-page__rich-text {
  font-size: 28rpx;
  line-height: 1.9;
  color: var(--jst-color-text-secondary);
}

.course-detail-page__bottom {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-white-96);
  box-shadow: var(--jst-shadow-card);
}

.course-detail-page__bottom-price {
  flex: 1;
}

.course-detail-page__bottom-label {
  display: block;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.course-detail-page__bottom-value {
  display: block;
  margin-top: 8rpx;
  font-size: 40rpx;
  font-weight: 700;
  color: var(--jst-color-primary);
}

.course-detail-page__bottom-value--free {
  color: var(--jst-color-success);
}

.course-detail-page__buy {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 240rpx;
  height: 88rpx;
  border-radius: var(--jst-radius-md);
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
}
</style>
