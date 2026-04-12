<template>
  <!-- 紧凑版：横向滚动卡片 -->
  <view v-if="compact" class="jst-contest-card jst-contest-card--compact" @tap="$emit('item-tap', normalizedContest)">
    <view v-if="normalizedContest.coverImage" class="jst-contest-card__cover-top">
      <image class="jst-contest-card__cover-image" :src="normalizedContest.coverImage" mode="aspectFill" />
      <view class="jst-contest-card__cover-tag">
        <jst-status-badge :text="statusText" :tone="statusTone" />
      </view>
    </view>
    <view v-else class="jst-contest-card__cover-top jst-contest-card__cover--fallback" :class="coverToneClass">
      <text class="jst-contest-card__cover-icon">{{ categoryIcon }}</text>
      <view class="jst-contest-card__cover-tag">
        <jst-status-badge :text="statusText" :tone="statusTone" />
      </view>
    </view>
    <view class="jst-contest-card__body">
      <text class="jst-contest-card__title">{{ normalizedContest.contestName || '赛事名称待补充' }}</text>
      <view class="jst-contest-card__meta">
        <u-tag
          v-if="normalizedContest.category"
          :text="normalizedContest.category"
          type="primary"
          plain
          :plain-fill="true"
          size="mini"
          shape="circle"
        />
      </view>
      <view class="jst-contest-card__footer">
        <text class="jst-contest-card__price" :class="{ 'jst-contest-card__price--free': isFree }">{{ priceText }}</text>
      </view>
    </view>
  </view>

  <!-- 标准版：列表行 -->
  <view v-else class="jst-contest-card" @tap="$emit('item-tap', normalizedContest)">
    <view v-if="normalizedContest.coverImage" class="jst-contest-card__cover">
      <image class="jst-contest-card__cover-image" :src="normalizedContest.coverImage" mode="aspectFill" />
    </view>
    <view v-else class="jst-contest-card__cover jst-contest-card__cover--fallback" :class="coverToneClass">
      <text class="jst-contest-card__cover-icon">{{ categoryIcon }}</text>
    </view>

    <view class="jst-contest-card__body">
      <text class="jst-contest-card__title">{{ normalizedContest.contestName || '赛事名称待补充' }}</text>

      <view class="jst-contest-card__meta">
        <u-tag
          v-if="normalizedContest.category"
          :text="normalizedContest.category"
          type="primary"
          plain
          :plain-fill="true"
          size="mini"
          shape="circle"
        />
        <jst-status-badge :text="statusText" :tone="statusTone" />
      </view>

      <!-- 中文注释: 多彩推荐标签行，数据来自 recommendTags（逗号分隔字符串） -->
      <view v-if="parsedTags.length" class="jst-contest-card__tags">
        <view
          v-for="(tag, ti) in parsedTags"
          :key="ti"
          class="jst-contest-card__tag"
          :class="'jst-contest-card__tag--' + getTagTone(ti)"
        >
          <text class="jst-contest-card__tag-text">{{ tag }}</text>
        </view>
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
    },
    compact: {
      type: Boolean,
      default: false
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
    // 中文注释: 解析 recommendTags 逗号分隔字符串为数组
    parsedTags() {
      const raw = this.normalizedContest.recommendTags
      if (!raw) return []
      return raw.split(',').map(t => t.trim()).filter(Boolean).slice(0, 4)
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
  },
  methods: {
    // 中文注释: 标签颜色循环分配
    getTagTone(index) {
      const tones = ['primary', 'success', 'warning', 'info']
      return tones[index % tones.length]
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-contest-card {
  display: flex;
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-sm;
  transition: transform $jst-duration-fast $jst-easing;
}
.jst-contest-card:active { transform: scale(0.99); }

.jst-contest-card__cover {
  width: 152rpx;
  height: 152rpx;
  border-radius: $jst-radius-md;
  overflow: hidden;
  flex-shrink: 0;
  background: $jst-brand-light;
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
  background: linear-gradient(135deg, $jst-brand 0%, lighten($jst-brand, 15%) 100%);
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
  margin-left: $jst-space-lg;
}

.jst-contest-card__title {
  display: -webkit-box;
  overflow: hidden;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  line-height: 1.5;
  color: $jst-text-primary;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.jst-contest-card__meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: $jst-space-sm;
  margin-top: $jst-space-md;
}

// 多彩标签行
.jst-contest-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-xs;
  margin-top: $jst-space-sm;
}

.jst-contest-card__tag {
  display: inline-flex;
  align-items: center;
  height: 36rpx;
  padding: 0 $jst-space-md;
  border-radius: $jst-radius-round;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-medium;
}

.jst-contest-card__tag--primary {
  background: $jst-brand-light;
  .jst-contest-card__tag-text { color: $jst-brand; }
}
.jst-contest-card__tag--success {
  background: $jst-success-light;
  .jst-contest-card__tag-text { color: $jst-success; }
}
.jst-contest-card__tag--warning {
  background: $jst-warning-light;
  .jst-contest-card__tag-text { color: $jst-warning; }
}
.jst-contest-card__tag--info {
  background: $jst-info-light;
  .jst-contest-card__tag-text { color: $jst-info; }
}

.jst-contest-card__partner {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xs;
  line-height: 1.5;
  color: $jst-text-regular;
}

.jst-contest-card__period {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.jst-contest-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $jst-space-md;
}

.jst-contest-card__price {
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-danger;
  font-feature-settings: "tnum";
  font-variant-numeric: tabular-nums;
}

.jst-contest-card__price--free {
  color: $jst-success;
}

.jst-contest-card__arrow {
  font-size: $jst-font-xs;
  font-weight: $jst-weight-medium;
  color: $jst-text-secondary;
}

// 紧凑版 — 横向滚动卡片
.jst-contest-card--compact {
  display: flex;
  flex-direction: column;
  width: 400rpx;
  min-width: 400rpx;
  padding: 0;
  overflow: hidden;

  .jst-contest-card__cover-top {
    position: relative;
    width: 100%;
    height: 220rpx;
    overflow: hidden;
    background: $jst-brand-light;
  }

  .jst-contest-card__cover-tag {
    position: absolute;
    top: $jst-space-sm;
    left: $jst-space-sm;
    z-index: 1;
  }

  .jst-contest-card__cover-image {
    width: 100%;
    height: 100%;
  }

  .jst-contest-card__body {
    margin-left: 0;
    padding: $jst-space-lg;
  }

  .jst-contest-card__title {
    font-size: $jst-font-base;
    -webkit-line-clamp: 1;
  }

  .jst-contest-card__meta {
    margin-top: $jst-space-sm;
  }

  .jst-contest-card__footer {
    margin-top: $jst-space-sm;
  }
}
</style>
