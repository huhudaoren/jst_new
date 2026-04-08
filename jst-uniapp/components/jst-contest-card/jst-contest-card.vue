<template>
  <view class="jst-contest-card" @tap="$emit('tap', normalizedContest)">
    <view v-if="normalizedContest.coverImage" class="jst-contest-card__cover">
      <image class="jst-contest-card__cover-image" :src="normalizedContest.coverImage" mode="aspectFill" />
    </view>
    <view v-else class="jst-contest-card__cover jst-contest-card__cover--fallback" :class="coverToneClass">
      <text class="jst-contest-card__cover-icon">{{ categoryIcon }}</text>
    </view>

    <view class="jst-contest-card__body">
      <text class="jst-contest-card__title">{{ normalizedContest.contestName || '赛事名称待补充' }}</text>

      <view class="jst-contest-card__meta">
        <text v-if="normalizedContest.category" class="jst-contest-card__category">{{ normalizedContest.category }}</text>
        <jst-status-badge :text="statusText" :tone="statusTone" />
      </view>

      <text v-if="normalizedContest.partnerName" class="jst-contest-card__partner">{{ normalizedContest.partnerName }}</text>
      <text class="jst-contest-card__period">报名时间：{{ enrollPeriodText }}</text>

      <view class="jst-contest-card__footer">
        <text class="jst-contest-card__price" :class="{ 'jst-contest-card__price--free': isFree }">{{ priceText }}</text>
        <text class="jst-contest-card__arrow">查看详情</text>
      </view>
    </view>
  </view>
</template>

<script>
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'
import {
  normalizeContestCard,
  getContestCategoryIcon,
  formatContestPeriod,
  formatContestPrice,
  isFreeContest,
  getContestStatusInfo
} from '@/utils/contest'

export default {
  name: 'JstContestCard',
  components: { JstStatusBadge },
  props: {
    contest: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    normalizedContest() {
      return normalizeContestCard(this.contest)
    },
    categoryIcon() {
      return getContestCategoryIcon(this.normalizedContest.category)
    },
    enrollPeriodText() {
      return formatContestPeriod(this.normalizedContest.enrollStartTime, this.normalizedContest.enrollEndTime)
    },
    priceText() {
      return formatContestPrice(this.normalizedContest.price)
    },
    isFree() {
      return isFreeContest(this.normalizedContest.price)
    },
    statusInfo() {
      return getContestStatusInfo(this.normalizedContest.status, this.normalizedContest.enrollOpen)
    },
    statusText() {
      return this.statusInfo.text
    },
    statusTone() {
      const toneMap = {
        brand: 'primary',
        muted: 'gray'
      }
      return toneMap[this.statusInfo.tone] || this.statusInfo.tone
    },
    coverToneClass() {
      const toneMap = {
        艺术: 'jst-contest-card__cover--purple',
        音乐: 'jst-contest-card__cover--pink',
        舞蹈: 'jst-contest-card__cover--gold',
        美术: 'jst-contest-card__cover--teal',
        朗诵: 'jst-contest-card__cover--slate',
        戏剧: 'jst-contest-card__cover--purple',
        文化: 'jst-contest-card__cover--brand',
        科技: 'jst-contest-card__cover--brand',
        体育: 'jst-contest-card__cover--green'
      }
      return toneMap[this.normalizedContest.category] || 'jst-contest-card__cover--brand'
    }
  }
}
</script>

<style scoped lang="scss">
.jst-contest-card {
  display: flex;
  padding: 28rpx 32rpx;
  background: var(--jst-color-card-bg);
  border-bottom: 2rpx solid var(--jst-color-border);
}

.jst-contest-card__cover {
  width: 152rpx;
  height: 152rpx;
  border-radius: var(--jst-radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--jst-color-brand-soft);
}

.jst-contest-card__cover-image {
  width: 100%;
  height: 100%;
}

.jst-contest-card__cover--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.jst-contest-card__cover--brand {
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
}

.jst-contest-card__cover--purple {
  background: linear-gradient(135deg, #4a0e8f 0%, #9c27b0 100%);
}

.jst-contest-card__cover--pink {
  background: linear-gradient(135deg, #880e4f 0%, #e91e63 100%);
}

.jst-contest-card__cover--gold {
  background: linear-gradient(135deg, #f57f17 0%, #ffb300 100%);
}

.jst-contest-card__cover--teal {
  background: linear-gradient(135deg, #00695c 0%, #26a69a 100%);
}

.jst-contest-card__cover--green {
  background: linear-gradient(135deg, #1b5e20 0%, #66bb6a 100%);
}

.jst-contest-card__cover--slate {
  background: linear-gradient(135deg, #37474f 0%, #78909c 100%);
}

.jst-contest-card__cover-icon {
  font-size: 56rpx;
}

.jst-contest-card__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
}

.jst-contest-card__title {
  display: -webkit-box;
  overflow: hidden;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.5;
  color: var(--jst-color-text);
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.jst-contest-card__meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 14rpx;
}

.jst-contest-card__category {
  padding: 8rpx 18rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-brand-soft);
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}

.jst-contest-card__partner {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: var(--jst-color-text-secondary);
}

.jst-contest-card__period {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: var(--jst-color-text-tertiary);
}

.jst-contest-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16rpx;
}

.jst-contest-card__price {
  font-size: 30rpx;
  font-weight: 800;
  color: var(--jst-color-primary);
}

.jst-contest-card__price--free {
  color: var(--jst-color-success);
}

.jst-contest-card__arrow {
  font-size: 22rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}
</style>