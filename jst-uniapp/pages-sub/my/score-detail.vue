<!-- 中文注释: 成绩详情页；对应接口 GET /jst/wx/score/${id}；包含雷达图+明细表+奖项 -->
<template>
  <view class="score-detail-page">
    <!-- 头部导航 -->
    <view class="score-detail-page__header">
      <view class="score-detail-page__back" @tap="goBack">返回</view>
      <text class="score-detail-page__header-title">成绩详情</text>
      <view class="score-detail-page__header-placeholder"></view>
    </view>

    <jst-loading :loading="loading" text="成绩加载中..." />

    <template v-if="detail && !loading">
      <!-- Hero 总分区 -->
      <view class="score-detail-page__hero">
        <view class="score-detail-page__hero-orb"></view>
        <view class="score-detail-page__hero-inner">
          <text class="score-detail-page__hero-contest">{{ detail.contestName || '未知赛事' }}</text>
          <text v-if="detail.category" class="score-detail-page__hero-category">{{ detail.category }}</text>
          <view class="score-detail-page__hero-score">
            <text class="score-detail-page__hero-label">{{ detail.scoreLabel || '加权总分' }}</text>
            <text class="score-detail-page__hero-value">{{ detail.totalScore != null ? detail.totalScore : '--' }}</text>
          </view>
          <view v-if="detail.rank" class="score-detail-page__hero-rank">
            <text class="score-detail-page__hero-rank-text">排名：第 {{ detail.rank }} 名</text>
            <text v-if="detail.totalParticipants" class="score-detail-page__hero-rank-total"> / 共 {{ detail.totalParticipants }} 人</text>
          </view>
        </view>
      </view>

      <!-- 获奖信息 -->
      <view v-if="detail.awardLevel" class="score-detail-page__award-card">
        <view class="score-detail-page__award-icon">{{ awardIcon }}</view>
        <view class="score-detail-page__award-info">
          <text class="score-detail-page__award-level">{{ detail.awardLevel }}</text>
          <text v-if="detail.awardName" class="score-detail-page__award-name">{{ detail.awardName }}</text>
        </view>
        <view v-if="detail.hasCert" class="score-detail-page__award-cert" @tap="viewCert">
          <text class="score-detail-page__award-cert-text">查看证书 ›</text>
        </view>
      </view>

      <!-- 成绩雷达图 -->
      <view v-if="radarDimensions.length >= 3" class="score-detail-page__card">
        <text class="score-detail-page__card-title">成绩分布</text>
        <jst-score-radar
          :dimensions="radarDimensions"
          :size="480"
          canvas-id="detailRadar"
        />
      </view>

      <!-- 成绩明细表 -->
      <view v-if="scoreItems.length" class="score-detail-page__card">
        <text class="score-detail-page__card-title">成绩明细</text>
        <!-- 表头 -->
        <view class="score-detail-page__table-header">
          <text class="score-detail-page__table-col score-detail-page__table-col--name">项目</text>
          <text class="score-detail-page__table-col score-detail-page__table-col--score">得分</text>
          <text class="score-detail-page__table-col score-detail-page__table-col--max">满分</text>
          <text class="score-detail-page__table-col score-detail-page__table-col--weight">权重</text>
        </view>
        <!-- 数据行 -->
        <view
          v-for="(item, idx) in scoreItems"
          :key="idx"
          class="score-detail-page__table-row"
          :class="{ 'score-detail-page__table-row--alt': idx % 2 === 1 }"
        >
          <text class="score-detail-page__table-col score-detail-page__table-col--name">{{ item.itemName || '--' }}</text>
          <text class="score-detail-page__table-col score-detail-page__table-col--score score-detail-page__table-col--highlight">{{ item.score != null ? item.score : '--' }}</text>
          <text class="score-detail-page__table-col score-detail-page__table-col--max">{{ item.maxScore != null ? item.maxScore : '--' }}</text>
          <text class="score-detail-page__table-col score-detail-page__table-col--weight">{{ item.weight != null ? (item.weight + '%') : '--' }}</text>
        </view>
      </view>

      <!-- 参赛信息 -->
      <view class="score-detail-page__card">
        <text class="score-detail-page__card-title">参赛信息</text>
        <view class="score-detail-page__info-row">
          <text class="score-detail-page__info-key">参赛人</text>
          <text class="score-detail-page__info-val">{{ detail.participantName || '--' }}</text>
        </view>
        <view class="score-detail-page__info-row">
          <text class="score-detail-page__info-key">参赛组别</text>
          <text class="score-detail-page__info-val">{{ detail.category || '--' }}</text>
        </view>
        <view class="score-detail-page__info-row">
          <text class="score-detail-page__info-key">报名编号</text>
          <text class="score-detail-page__info-val">{{ detail.enrollNo || '--' }}</text>
        </view>
        <view class="score-detail-page__info-row">
          <text class="score-detail-page__info-key">成绩发布</text>
          <text class="score-detail-page__info-val">{{ formatDate(detail.publishTime) }}</text>
        </view>
      </view>
    </template>

    <jst-empty v-else-if="!loading" text="暂无成绩信息" icon="📊" />
  </view>
</template>

<script>
import { getScoreDetail } from '@/api/score'
import JstScoreRadar from '@/components/jst-score-radar/jst-score-radar.vue'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstScoreRadar, JstEmpty, JstLoading },
  data() {
    return {
      loading: false,
      scoreId: '',
      enrollId: '',
      detail: null
    }
  },
  computed: {
    // 雷达图维度数据（来自 scoreItems）
    radarDimensions() {
      if (!this.detail || !this.detail.scoreItems) return []
      return this.detail.scoreItems
        .filter(function(item) { return item.itemName && item.score != null })
        .map(function(item) {
          return {
            label: item.itemName,
            value: item.score,
            max: item.maxScore || 100
          }
        })
    },
    // 成绩明细项
    scoreItems() {
      if (!this.detail || !this.detail.scoreItems) return []
      return this.detail.scoreItems
    },
    // 奖项图标
    awardIcon() {
      var level = this.detail && this.detail.awardLevel
      if (!level) return '🏅'
      if (level.indexOf('一等') !== -1) return '🥇'
      if (level.indexOf('二等') !== -1) return '🥈'
      if (level.indexOf('三等') !== -1) return '🥉'
      return '🏅'
    }
  },
  onLoad(query) {
    this.scoreId = query.scoreId || query.id || ''
    this.enrollId = query.enrollId || ''
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      var id = this.scoreId || this.enrollId
      if (!id) return
      this.loading = true
      try {
        var res = await getScoreDetail(id)
        this.detail = res || null
      } catch (e) {
        this.detail = null
      } finally {
        this.loading = false
      }
    },
    formatDate(val) {
      if (!val) return '--'
      var d = new Date(val)
      if (isNaN(d.getTime())) return val
      return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0')
    },
    // 跳转证书页
    viewCert() {
      if (this.detail && this.detail.certId) {
        uni.navigateTo({ url: '/pages-sub/my/cert-detail?id=' + this.detail.certId })
      } else {
        uni.navigateTo({ url: '/pages-sub/my/cert' })
      }
    },
    goBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.navigateTo({ url: '/pages-sub/my/score' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.score-detail-page {
  min-height: 100vh;
  padding-bottom: calc(60rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

// 头部导航
.score-detail-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.score-detail-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: $jst-bg-page;
  font-size: $jst-font-md;
  color: $jst-text-primary;
}

.score-detail-page__header-title {
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-detail-page__header-placeholder {
  width: 72rpx;
}

// Hero 总分区
.score-detail-page__hero {
  position: relative;
  padding: $jst-space-xxl $jst-page-padding $jst-space-xl;
  background: $jst-hero-gradient;
  overflow: hidden;
}

.score-detail-page__hero-orb {
  position: absolute;
  right: -40rpx;
  top: -40rpx;
  width: 220rpx;
  height: 220rpx;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.06);
}

.score-detail-page__hero-inner {
  position: relative;
  z-index: 1;
}

.score-detail-page__hero-contest {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  line-height: 1.4;
}

.score-detail-page__hero-category {
  display: inline-block;
  margin-top: $jst-space-sm;
  padding: 4rpx 16rpx;
  border-radius: $jst-radius-round;
  background: rgba(255, 255, 255, 0.16);
  font-size: $jst-font-xs;
  color: rgba(255, 255, 255, 0.8);
}

.score-detail-page__hero-score {
  margin-top: $jst-space-xl;
  text-align: center;
}

.score-detail-page__hero-label {
  display: block;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.7);
}

.score-detail-page__hero-value {
  display: block;
  margin-top: $jst-space-sm;
  font-size: 96rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
  line-height: 1;
  letter-spacing: -2rpx;
  font-feature-settings: "tnum";
  font-variant-numeric: tabular-nums;
}

.score-detail-page__hero-rank {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: $jst-space-md;
}

.score-detail-page__hero-rank-text {
  font-size: $jst-font-base;
  color: rgba(255, 255, 255, 0.85);
  font-weight: $jst-weight-medium;
}

.score-detail-page__hero-rank-total {
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.6);
}

// 获奖卡
.score-detail-page__award-card {
  display: flex;
  align-items: center;
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-gold-light;
  border: 2rpx solid rgba(255, 213, 79, 0.3);
}

.score-detail-page__award-icon {
  font-size: 56rpx;
  flex-shrink: 0;
}

.score-detail-page__award-info {
  flex: 1;
  margin-left: $jst-space-md;
}

.score-detail-page__award-level {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-detail-page__award-name {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.score-detail-page__award-cert {
  flex-shrink: 0;
  padding: $jst-space-xs $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-brand;
}

.score-detail-page__award-cert-text {
  font-size: $jst-font-sm;
  color: $jst-text-inverse;
  font-weight: $jst-weight-medium;
}

// 通用卡片
.score-detail-page__card {
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
}

.score-detail-page__card-title {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  margin-bottom: $jst-space-lg;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-detail-page__card-title::before {
  content: '';
  display: inline-block;
  width: 6rpx;
  height: 28rpx;
  border-radius: $jst-radius-xs;
  background: $jst-brand;
}

// 明细表
.score-detail-page__table-header {
  display: flex;
  padding: $jst-space-sm $jst-space-md;
  border-radius: $jst-radius-sm;
  background: $jst-bg-grey;
}

.score-detail-page__table-row {
  display: flex;
  padding: $jst-space-md;
  border-bottom: 1rpx solid $jst-border;
}

.score-detail-page__table-row:last-child {
  border-bottom: none;
}

.score-detail-page__table-row--alt {
  background: rgba($jst-bg-grey, 0.5);
}

.score-detail-page__table-col {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.score-detail-page__table-col--name {
  flex: 2;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
}

.score-detail-page__table-col--score {
  flex: 1;
  text-align: center;
}

.score-detail-page__table-col--max {
  flex: 1;
  text-align: center;
}

.score-detail-page__table-col--weight {
  flex: 1;
  text-align: right;
}

.score-detail-page__table-col--highlight {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

.score-detail-page__table-header .score-detail-page__table-col {
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
  color: $jst-text-secondary;
}

// 信息行
.score-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $jst-space-md 0;
  border-bottom: 1rpx solid $jst-border;
}

.score-detail-page__info-row:last-child {
  border-bottom: none;
}

.score-detail-page__info-key {
  flex-shrink: 0;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.score-detail-page__info-val {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
  text-align: right;
}
</style>
