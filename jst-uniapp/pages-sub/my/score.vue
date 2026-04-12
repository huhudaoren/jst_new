<!-- 中文注释: 我的成绩页；对应原型 小程序原型图/my-score.html；对应接口 GET /jst/wx/score/my -->
<template>
  <view class="score-page">
    <!-- 头部 -->
    <view class="score-page__header">
      <view class="score-page__back" @tap="goBack">←</view>
      <text class="score-page__header-title">成绩查询</text>
    </view>

    <!-- 公开查询入口卡 -->
    <view class="score-page__query-card">
      <view class="score-page__query-orb"></view>
      <text class="score-page__query-title">🔍 公开成绩查询</text>
      <view class="score-page__query-row">
        <input
          class="score-page__query-input"
          v-model="queryKeyword"
          placeholder="输入手机号 / 证件号 / 报名编号"
          placeholder-class="score-page__query-placeholder"
          @confirm="doPublicQuery"
        />
        <u-button class="score-page__query-btn" @click="doPublicQuery">查询</u-button>
      </view>
      <view class="score-page__query-tips">
        <text class="score-page__query-tip">支持手机号查询</text>
        <text class="score-page__query-tip">支持身份证号</text>
        <text class="score-page__query-tip">支持报名编号</text>
      </view>
    </view>

    <!-- 我的成绩列表 -->
    <view class="score-page__section-bar">
      <text class="score-page__section-title">我的参赛成绩</text>
      <text class="score-page__section-count" v-if="scoreList.length">共{{ scoreList.length }}条</text>
    </view>

    <jst-loading :loading="loading" text="成绩加载中..." />
    <u-skeleton v-if="loading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view v-if="scoreList.length" class="score-page__list">
      <view
        v-for="item in scoreList"
        :key="item.scoreId || item.enrollId"
        class="score-page__card"
        @tap="openDetail(item)"
      >
        <view class="score-page__card-header">
          <view class="score-page__card-icon">🔢</view>
          <view class="score-page__card-info">
            <text class="score-page__card-name">{{ item.contestName || '未知赛事' }}</text>
            <text class="score-page__card-meta">{{ item.category || '' }} · 成绩发布：{{ formatDate(item.publishTime) }}</text>
          </view>
        </view>

        <!-- 已出成绩 -->
        <view v-if="item.score != null" class="score-page__card-display">
          <view class="score-page__card-main">
            <text class="score-page__card-label">{{ item.scoreLabel || '总分' }}</text>
            <text class="score-page__card-value">{{ item.score }}</text>
            <text class="score-page__card-rank" v-if="item.rank">排名：第 {{ item.rank }} 名</text>
          </view>
          <view v-if="item.awardLevel" class="score-page__card-award" :class="'score-page__card-award--' + getAwardTheme(item.awardLevel)">
            <text class="score-page__card-award-icon">{{ getAwardIcon(item.awardLevel) }}</text>
            <text class="score-page__card-award-text">{{ item.awardLevel }}</text>
          </view>
        </view>

        <!-- 成绩待公布 -->
        <view v-else class="score-page__card-pending">
          <text class="score-page__card-pending-icon">⏳</text>
          <text class="score-page__card-pending-text">成绩待公布</text>
          <text class="score-page__card-pending-sub">发布后将推送消息通知</text>
        </view>

        <!-- 证书入口 -->
        <view v-if="item.hasCert" class="score-page__card-footer">
          <text class="score-page__card-cert-tag">✓ 证书已生成</text>
          <u-button class="score-page__card-cert-btn" @click.stop="viewCert(item)">查看证书</u-button>
        </view>
      </view>
    </view>

    <jst-empty v-else-if="!loading" text="暂无成绩记录" icon="📊" />

    <!-- 统计汇总 -->
    <view v-if="scoreList.length" class="score-page__summary">
      <text class="score-page__summary-title">📊 参赛数据汇总</text>
      <view class="score-page__summary-grid">
        <view class="score-page__summary-item score-page__summary-item--brand">
          <text class="score-page__summary-num">{{ scoreList.length }}</text>
          <text class="score-page__summary-label">参赛次数</text>
        </view>
        <view class="score-page__summary-item score-page__summary-item--gold">
          <text class="score-page__summary-num">{{ awardCount }}</text>
          <text class="score-page__summary-label">获奖次数</text>
        </view>
        <view class="score-page__summary-item score-page__summary-item--success">
          <text class="score-page__summary-num">{{ certCount }}</text>
          <text class="score-page__summary-label">获奖证书</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyScoreList } from '@/api/score'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstEmpty, JstLoading },
  data() {
    return {
      loading: false,
      scoreList: [],
      queryKeyword: ''
    }
  },
  computed: {
    // 有奖项的数量
    awardCount() {
      return this.scoreList.filter(item => item.awardLevel).length
    },
    // 有证书的数量
    certCount() {
      return this.scoreList.filter(item => item.hasCert).length
    }
  },
  onLoad() {
    this.loadScoreList()
  },
  methods: {
    async loadScoreList() {
      this.loading = true
      try {
        const list = await getMyScoreList()
        this.scoreList = Array.isArray(list) ? list : []
      } catch (e) {
        this.scoreList = []
      } finally {
        this.loading = false
      }
    },
    formatDate(val) {
      if (!val) return '--'
      const d = new Date(val)
      if (isNaN(d.getTime())) return val
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    getAwardTheme(level) {
      if (!level) return 'normal'
      if (level.includes('一等')) return 'gold'
      if (level.includes('二等')) return 'silver'
      if (level.includes('三等')) return 'bronze'
      return 'normal'
    },
    getAwardIcon(level) {
      if (!level) return '🏅'
      if (level.includes('一等')) return '🥇'
      if (level.includes('二等')) return '🥈'
      if (level.includes('三等')) return '🥉'
      return '🏅'
    },
    openDetail(item) {
      if (item.scoreId) {
        // 若后端有详情接口, 跳详情页; 暂未实现详情页, 先不跳
      }
    },
    viewCert(item) {
      uni.navigateTo({ url: '/pages-sub/my/cert' })
    },
    // 公开查询跳到独立页面
    doPublicQuery() {
      const kw = this.queryKeyword.trim()
      if (!kw) {
        uni.showToast({ title: '请输入查询信息', icon: 'none' })
        return
      }
      uni.navigateTo({ url: `/pages-sub/public/score-query?keyword=${encodeURIComponent(kw)}` })
    },
    goBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.score-page {
  min-height: 100vh;
  padding-bottom: 60rpx;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: 0 $jst-page-padding $jst-space-lg;
}

.score-page__header {
  display: flex;
  align-items: center;
  padding: $jst-space-lg $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.score-page__back {
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

.score-page__header-title {
  flex: 1;
  margin-left: $jst-space-md;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-page__query-card {
  position: relative;
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-xl;
  border-radius: $jst-radius-lg;
  background: linear-gradient(135deg, $jst-brand 0%, $jst-brand-dark 100%);
  overflow: hidden;
}

.score-page__query-orb {
  position: absolute;
  right: -40rpx;
  top: -40rpx;
  width: 200rpx;
  height: 200rpx;
  border-radius: $jst-radius-round;
  background: rgba($jst-text-inverse, 0.08);
}

.score-page__query-title {
  display: block;
  margin-bottom: 20rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.score-page__query-row {
  display: flex;
  gap: $jst-space-md;
}

.score-page__query-input {
  flex: 1;
  height: 88rpx;
  padding: 0 $jst-page-padding;
  border-radius: $jst-radius-md;
  border: 2rpx solid rgba($jst-text-inverse, 0.34);
  background: rgba($jst-text-inverse, 0.16);
  font-size: $jst-font-base;
  color: $jst-text-inverse;
}

.score-page__query-placeholder {
  color: rgba($jst-text-inverse, 0.58);
}

.score-page__query-tips {
  display: flex;
  gap: $jst-space-sm;
  flex-wrap: wrap;
  margin-top: $jst-space-md;
}

.score-page__query-tip {
  padding: 6rpx 16rpx;
  border-radius: $jst-radius-round;
  font-size: $jst-font-xs;
  color: rgba($jst-text-inverse, 0.72);
  background: rgba($jst-text-inverse, 0.14);
}

.score-page__section-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: $jst-space-xl $jst-page-padding $jst-space-lg;
}

.score-page__section-title {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-page__section-title::before {
  content: '';
  display: inline-block;
  width: 8rpx;
  height: 32rpx;
  border-radius: $jst-radius-xs;
  background: $jst-brand;
}

.score-page__section-count {
  padding: 6rpx 16rpx;
  border-radius: $jst-radius-round;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
  background: $jst-bg-grey;
}

.score-page__list {
  padding: 0 $jst-page-padding;
}

.score-page__card {
  margin-bottom: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
  overflow: hidden;
}

.score-page__card-header {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  padding: 28rpx 28rpx 20rpx;
  border-bottom: 2rpx solid $jst-border;
}

.score-page__card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  border-radius: $jst-radius-md;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  font-size: 44rpx;
  flex-shrink: 0;
}

.score-page__card-info {
  flex: 1;
  min-width: 0;
}

.score-page__card-name {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  line-height: 1.4;
  color: $jst-text-primary;
}

.score-page__card-meta {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.score-page__card-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx;
}

.score-page__card-main {
  display: flex;
  flex-direction: column;
  gap: $jst-space-xs;
}

.score-page__card-label {
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.score-page__card-value {
  font-size: 64rpx;
  line-height: 1;
  letter-spacing: -2rpx;
  font-weight: $jst-weight-bold;
  color: $jst-brand;
}

.score-page__card-rank {
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.score-page__card-award {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 20rpx 32rpx;
  border-radius: $jst-radius-md;
}

.score-page__card-award--gold {
  background: $jst-gold-light;
}

.score-page__card-award--silver {
  background: $jst-bg-page;
}

.score-page__card-award--bronze {
  background: $jst-warning-light;
}

.score-page__card-award--normal {
  background: $jst-brand-light;
}

.score-page__card-award-icon {
  font-size: 56rpx;
}

.score-page__card-award-text {
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-page__card-pending {
  margin: 20rpx;
  padding: 28rpx;
  border-radius: $jst-radius-md;
  text-align: center;
  background: $jst-bg-page;
}

.score-page__card-pending-icon {
  display: block;
  margin-bottom: $jst-space-md;
  font-size: 72rpx;
}

.score-page__card-pending-text {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-secondary;
}

.score-page__card-pending-sub {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.score-page__card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 28rpx;
  border-top: 2rpx solid $jst-border;
}

.score-page__card-cert-tag {
  font-size: $jst-font-sm;
  color: $jst-success;
}

.score-page__summary {
  margin: $jst-space-xl $jst-page-padding 0;
  padding: 28rpx;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.score-page__summary-title {
  display: block;
  margin-bottom: $jst-space-lg;
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.score-page__summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $jst-space-md;
}

.score-page__summary-item {
  padding: 20rpx 8rpx;
  border-radius: $jst-radius-md;
  text-align: center;
}

.score-page__summary-item--brand {
  background: $jst-brand-light;
}

.score-page__summary-item--gold {
  background: $jst-gold-light;
}

.score-page__summary-item--success {
  background: $jst-success-light;
}

.score-page__summary-num {
  display: block;
  font-size: 40rpx;
  font-weight: $jst-weight-bold;
  color: $jst-brand;
}

.score-page__summary-item--gold .score-page__summary-num {
  color: $jst-warning;
}

.score-page__summary-item--success .score-page__summary-num {
  color: $jst-success;
}

.score-page__summary-label {
  display: block;
  margin-top: 4rpx;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

::v-deep .score-page__query-btn.u-button {
  height: 88rpx;
  padding: 0 $jst-space-xl;
  border: none;
  border-radius: $jst-radius-md;
  background: $jst-bg-card;
  color: $jst-brand-dark;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .score-page__card-cert-btn.u-button {
  min-height: 62rpx;
  padding: 0 $jst-space-lg;
  border-color: $jst-brand;
  border-radius: $jst-radius-md;
  color: $jst-brand;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
}
</style>
