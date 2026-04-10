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
        <view class="score-page__query-btn" @tap="doPublicQuery">查询</view>
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
          <view class="score-page__card-cert-btn" @tap.stop="viewCert(item)">查看证书</view>
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
.score-page { min-height: 100vh; padding-bottom: 60rpx; background: var(--jst-color-page-bg); }

.score-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.score-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.score-page__header-title { flex: 1; margin-left: 16rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

/* 查询卡 */
.score-page__query-card { position: relative; margin: 24rpx 24rpx 0; padding: 32rpx; border-radius: var(--jst-radius-lg); background: linear-gradient(135deg, #1058A0 0%, #0D3F7A 100%); overflow: hidden; }
.score-page__query-orb { position: absolute; right: -40rpx; top: -40rpx; width: 200rpx; height: 200rpx; border-radius: 50%; background: rgba(255,255,255,0.06); }
.score-page__query-title { display: block; font-size: 30rpx; font-weight: 700; color: #fff; margin-bottom: 20rpx; }
.score-page__query-row { display: flex; gap: 16rpx; }
.score-page__query-input { flex: 1; height: 88rpx; padding: 0 24rpx; border-radius: var(--jst-radius-md); background: rgba(255,255,255,0.15); border: 2rpx solid rgba(255,255,255,0.3); font-size: 28rpx; color: #fff; }
.score-page__query-placeholder { color: rgba(255,255,255,0.5); }
.score-page__query-btn { display: flex; align-items: center; justify-content: center; height: 88rpx; padding: 0 32rpx; border-radius: var(--jst-radius-md); background: #fff; font-size: 28rpx; font-weight: 700; color: #0D3F7A; flex-shrink: 0; }
.score-page__query-tips { display: flex; gap: 12rpx; flex-wrap: wrap; margin-top: 16rpx; }
.score-page__query-tip { font-size: 22rpx; color: rgba(255,255,255,0.6); background: rgba(255,255,255,0.1); padding: 6rpx 16rpx; border-radius: var(--jst-radius-full); }

/* section bar */
.score-page__section-bar { display: flex; justify-content: space-between; align-items: center; margin: 32rpx 24rpx 20rpx; }
.score-page__section-title { font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); display: flex; align-items: center; gap: 12rpx; }
.score-page__section-title::before { content: ''; display: inline-block; width: 8rpx; height: 32rpx; background: var(--jst-color-brand); border-radius: 4rpx; }
.score-page__section-count { font-size: 24rpx; color: var(--jst-color-text-tertiary); padding: 6rpx 16rpx; background: var(--jst-color-page-bg); border-radius: var(--jst-radius-full); }

/* 成绩卡片 */
.score-page__list { padding: 0 24rpx; }
.score-page__card { background: var(--jst-color-card-bg); border-radius: var(--jst-radius-lg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); margin-bottom: 24rpx; overflow: hidden; }
.score-page__card-header { display: flex; align-items: flex-start; gap: 20rpx; padding: 28rpx 28rpx 20rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.score-page__card-icon { display: flex; align-items: center; justify-content: center; width: 88rpx; height: 88rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #1058A0, #0D3F7A); font-size: 44rpx; flex-shrink: 0; }
.score-page__card-info { flex: 1; min-width: 0; }
.score-page__card-name { display: block; font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); line-height: 1.4; }
.score-page__card-meta { display: block; margin-top: 8rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

/* 成绩展示 */
.score-page__card-display { display: flex; align-items: center; justify-content: space-between; padding: 28rpx; }
.score-page__card-main { display: flex; flex-direction: column; gap: 8rpx; }
.score-page__card-label { font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.score-page__card-value { font-size: 64rpx; font-weight: 900; color: var(--jst-color-brand); line-height: 1; letter-spacing: -2rpx; }
.score-page__card-rank { font-size: 24rpx; color: var(--jst-color-text-tertiary); }

/* 奖项徽章 */
.score-page__card-award { display: flex; flex-direction: column; align-items: center; padding: 20rpx 32rpx; border-radius: var(--jst-radius-md); gap: 6rpx; }
.score-page__card-award--gold { background: #FFF8E1; }
.score-page__card-award--silver { background: var(--jst-color-page-bg); }
.score-page__card-award--bronze { background: #FFF5EE; }
.score-page__card-award--normal { background: var(--jst-color-brand-soft); }
.score-page__card-award-icon { font-size: 56rpx; }
.score-page__card-award-text { font-size: 26rpx; font-weight: 800; color: var(--jst-color-text); }

/* 待公布 */
.score-page__card-pending { padding: 28rpx; text-align: center; background: var(--jst-color-page-bg); margin: 20rpx; border-radius: var(--jst-radius-md); }
.score-page__card-pending-icon { display: block; font-size: 72rpx; margin-bottom: 16rpx; }
.score-page__card-pending-text { display: block; font-size: 28rpx; font-weight: 600; color: var(--jst-color-text-secondary); }
.score-page__card-pending-sub { display: block; margin-top: 8rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

/* 证书入口 */
.score-page__card-footer { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 28rpx; border-top: 2rpx solid var(--jst-color-border); }
.score-page__card-cert-tag { font-size: 24rpx; color: var(--jst-color-success); }
.score-page__card-cert-btn { padding: 12rpx 24rpx; border-radius: var(--jst-radius-md); border: 2rpx solid var(--jst-color-brand); font-size: 24rpx; font-weight: 600; color: var(--jst-color-brand); }

/* 统计汇总 */
.score-page__summary { margin: 32rpx 24rpx 0; padding: 28rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); }
.score-page__summary-title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 24rpx; }
.score-page__summary-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx; }
.score-page__summary-item { text-align: center; padding: 20rpx 8rpx; border-radius: var(--jst-radius-md); }
.score-page__summary-item--brand { background: var(--jst-color-brand-soft); }
.score-page__summary-item--gold { background: #FFF8E1; }
.score-page__summary-item--success { background: var(--jst-color-success-soft); }
.score-page__summary-num { display: block; font-size: 40rpx; font-weight: 900; color: var(--jst-color-brand); }
.score-page__summary-item--gold .score-page__summary-num { color: #9A6300; }
.score-page__summary-item--success .score-page__summary-num { color: var(--jst-color-success); }
.score-page__summary-label { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
</style>
