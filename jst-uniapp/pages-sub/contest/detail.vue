<!-- 中文注释: 赛事详情页；对应原型 contest-detail.html；对应接口 GET /jst/wx/contest/{id} -->
<template>
  <view class="contest-detail-page">
    <jst-loading :loading="pageLoading" text="赛事详情加载中..." />

    <view class="contest-detail-page__nav">
      <view class="contest-detail-page__back" @tap="handleBack">←</view>
      <text class="contest-detail-page__nav-title">赛事详情</text>
      <view class="contest-detail-page__nav-placeholder"></view>
    </view>

    <template v-if="detail">
      <view class="contest-detail-page__hero">
        <image
          v-if="detail.coverImage"
          class="contest-detail-page__hero-image"
          :src="detail.coverImage"
          mode="aspectFill"
        />
        <view v-else class="contest-detail-page__hero-fallback">
          <text class="contest-detail-page__hero-icon">{{ categoryIcon }}</text>
        </view>
      </view>

      <view class="contest-detail-page__content">
        <view class="contest-detail-page__summary">
          <view class="contest-detail-page__summary-tags">
            <text v-if="detail.category" class="contest-detail-page__category">{{ detail.category }}</text>
            <jst-status-badge :status="detail.status" :enroll-open="detail.enrollOpen" />
          </view>

          <text class="contest-detail-page__title">{{ detail.contestName }}</text>
          <text v-if="detail.partnerName" class="contest-detail-page__partner">{{ detail.partnerName }}</text>
          <text class="contest-detail-page__price">{{ priceText }}</text>
        </view>

        <view class="contest-detail-page__card">
          <view class="contest-detail-page__card-header">
            <text class="contest-detail-page__card-title">基本信息</text>
          </view>

          <view class="contest-detail-page__info-row">
            <text class="contest-detail-page__info-key">报名时间</text>
            <text class="contest-detail-page__info-value">{{ enrollPeriodText }}</text>
          </view>
          <view class="contest-detail-page__info-row">
            <text class="contest-detail-page__info-key">比赛时间</text>
            <text class="contest-detail-page__info-value">{{ eventPeriodText }}</text>
          </view>
          <view class="contest-detail-page__info-row">
            <text class="contest-detail-page__info-key">参赛组别</text>
            <text class="contest-detail-page__info-value">{{ detail.groupLevels || '待公布' }}</text>
          </view>
          <view class="contest-detail-page__info-row">
            <text class="contest-detail-page__info-key">支持渠道</text>
            <text class="contest-detail-page__info-value">{{ detail.supportChannelEnroll === 1 ? '支持' : '不支持' }}</text>
          </view>
          <view class="contest-detail-page__info-row">
            <text class="contest-detail-page__info-key">积分抵扣</text>
            <text class="contest-detail-page__info-value">{{ detail.supportPointsDeduct === 1 ? '支持' : '不支持' }}</text>
          </view>
          <view class="contest-detail-page__info-row">
            <text class="contest-detail-page__info-key">线下预约</text>
            <text class="contest-detail-page__info-value">{{ detail.supportAppointment === 1 ? '支持' : '不支持' }}</text>
          </view>
        </view>

        <view class="contest-detail-page__card">
          <view class="contest-detail-page__card-header">
            <text class="contest-detail-page__card-title">赛事介绍</text>
          </view>

          <view v-if="detailDescription" class="contest-detail-page__description">
            <rich-text :nodes="detailDescription"></rich-text>
          </view>

          <jst-empty
            v-else
            icon="📖"
            text="赛事介绍待补充"
          />
        </view>
      </view>

      <view class="contest-detail-page__cta">
        <view class="contest-detail-page__cta-main">
          <text class="contest-detail-page__cta-label">报名费用</text>
          <text class="contest-detail-page__cta-price">{{ priceText }}</text>
        </view>

        <view class="contest-detail-page__cta-actions">
          <button
            v-if="showAppointmentEntry"
            class="contest-detail-page__cta-secondary"
            @tap="handleAppointmentTap"
          >我要预约</button>
          <button
            class="contest-detail-page__cta-button"
            :class="{ 'contest-detail-page__cta-button--disabled': enrollAction.disabled }"
            :disabled="enrollAction.disabled"
            @tap="handleEnrollTap"
          >
            {{ enrollAction.text }}
          </button>
        </view>
      </view>
    </template>

    <jst-empty
      v-else-if="!pageLoading"
      icon="📭"
      text="赛事详情暂未返回，待后端 F7 联调完成后自动展示。"
    />
  </view>
</template>

<script>
import { getContestDetail } from '@/api/contest'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'
import {
  normalizeContestCard,
  formatContestPeriod,
  formatContestPrice,
  sanitizeRichText,
  getContestEnrollAction,
  getContestCategoryIcon
} from '@/utils/contest'

export default {
  components: {
    JstEmpty,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      pageLoading: false,
      contestId: '',
      detail: null
    }
  },
  computed: {
    enrollPeriodText() {
      return this.detail
        ? formatContestPeriod(this.detail.enrollStartTime, this.detail.enrollEndTime)
        : '--'
    },
    eventPeriodText() {
      return this.detail
        ? formatContestPeriod(this.detail.eventStartTime, this.detail.eventEndTime)
        : '--'
    },
    priceText() {
      return this.detail ? formatContestPrice(this.detail.price) : '--'
    },
    detailDescription() {
      return this.detail ? sanitizeRichText(this.detail.description) : ''
    },
    enrollAction() {
      return getContestEnrollAction(this.detail || {})
    },
    // POLISH-G: 收敛为单一字段 supportAppointment (WxContestDetailVO/ContestDetailVO 已确认)
    showAppointmentEntry() {
      const v = this.detail && this.detail.supportAppointment
      return v === 1 || v === true || v === '1'
    },
    categoryIcon() {
      return getContestCategoryIcon(this.detail && this.detail.category)
    }
  },
  onLoad(query) {
    this.contestId = query.id || ''
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      if (!this.contestId) {
        this.detail = null
        return
      }

      this.pageLoading = true

      try {
        const response = await getContestDetail(this.contestId, { silent: true })
        this.detail = normalizeContestCard(response || {})
      } catch (error) {
        this.detail = null
      } finally {
        this.pageLoading = false
      }
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }

      uni.switchTab({ url: '/pages/contest/list' })
    },

    handleAppointmentTap() {
      uni.navigateTo({
        url: `/pages-sub/appointment/apply?contestId=${this.contestId}&contestName=${encodeURIComponent((this.detail && this.detail.name) || '')}`
      })
    },

    handleEnrollTap() {
      if (this.enrollAction.disabled) {
        return
      }

      uni.navigateTo({
        url: `/pages-sub/contest/enroll?contestId=${this.contestId}`
      })
      return
      uni.showToast({
        title: 'F9 完成后开放',
        icon: 'none'
      })
    }
  }
}
</script>

<style scoped lang="scss">
.contest-detail-page {
  min-height: 100vh;
  padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-page-bg);
}

.contest-detail-page__nav {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: var(--jst-color-card-bg);
  box-shadow: 0 8rpx 16rpx rgba(16, 88, 160, 0.06);
}

.contest-detail-page__back,
.contest-detail-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.contest-detail-page__back {
  font-size: 36rpx;
  color: var(--jst-color-text-secondary);
}

.contest-detail-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.contest-detail-page__hero {
  height: 360rpx;
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
}

.contest-detail-page__hero-image,
.contest-detail-page__hero-fallback {
  width: 100%;
  height: 100%;
}

.contest-detail-page__hero-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.contest-detail-page__hero-icon {
  font-size: 88rpx;
}

.contest-detail-page__content {
  padding: 24rpx;
}

.contest-detail-page__summary,
.contest-detail-page__card {
  margin-bottom: 20rpx;
  padding: 28rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.contest-detail-page__summary-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
}

.contest-detail-page__category {
  padding: 8rpx 18rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-brand-soft);
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}

.contest-detail-page__title {
  display: block;
  margin-top: 18rpx;
  font-size: 40rpx;
  font-weight: 800;
  line-height: 1.4;
  color: var(--jst-color-text);
}

.contest-detail-page__partner {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: var(--jst-color-text-secondary);
}

.contest-detail-page__price {
  display: block;
  margin-top: 18rpx;
  font-size: 44rpx;
  font-weight: 800;
  color: var(--jst-color-primary);
}

.contest-detail-page__card-header {
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid var(--jst-color-border);
}

.contest-detail-page__card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.contest-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 24rpx 0;
  border-bottom: 2rpx solid var(--jst-color-border);
}

.contest-detail-page__info-row:last-child {
  border-bottom: 0;
}

.contest-detail-page__info-key {
  width: 132rpx;
  flex-shrink: 0;
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
}

.contest-detail-page__info-value {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.7;
  text-align: right;
  color: var(--jst-color-text);
}

.contest-detail-page__description {
  padding-top: 24rpx;
  font-size: 24rpx;
  line-height: 1.8;
  color: var(--jst-color-text-secondary);
}

.contest-detail-page__cta {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-card-bg);
  box-shadow: 0 -10rpx 24rpx rgba(16, 88, 160, 0.08);
}

.contest-detail-page__cta-main {
  flex: 1;
}

.contest-detail-page__cta-label {
  display: block;
  font-size: 22rpx;
  color: var(--jst-color-text-tertiary);
}

.contest-detail-page__cta-price {
  display: block;
  margin-top: 8rpx;
  font-size: 40rpx;
  font-weight: 800;
  color: var(--jst-color-primary);
}

.contest-detail-page__cta-button {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 220rpx;
  height: 88rpx;
  padding: 0 32rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-primary);
  font-size: 28rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
}

.contest-detail-page__cta-button--disabled {
  background: var(--jst-color-border);
  color: var(--jst-color-text-tertiary);
}

.contest-detail-page__cta-actions {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.contest-detail-page__cta-secondary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 180rpx;
  height: 88rpx;
  padding: 0 24rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-brand-soft);
  color: var(--jst-color-brand);
  font-size: 26rpx;
  font-weight: 700;
  border: none;
}
</style>
