<!-- 中文注释: 渠道方 · 查看学生成绩 (E1-CH-3)
     数据来源: GET /jst/wx/channel/students/{id}/score -->
<template>
  <view class="score-page">
    <view class="score-header">
      <view class="score-header__back" @tap="goBack">←</view>
      <text class="score-header__title">{{ studentName }} 的成绩</text>
    </view>

    <view v-for="item in list" :key="item.scoreId" class="score-card">
      <view class="score-card__top">
        <text class="score-card__contest">{{ item.contestName || '--' }}</text>
        <text class="score-card__time">{{ formatDate(item.publishTime) }}</text>
      </view>
      <view class="score-card__bottom">
        <text class="score-card__rank">{{ item.rank || '--' }}</text>
        <text class="score-card__award">{{ item.awardLevel || '暂无' }}</text>
        <text class="score-card__score">{{ item.score != null ? item.score + '分' : '--' }}</text>
      </view>
    </view>

    <view v-if="!list.length && !loading" class="score-empty">
      <text class="score-empty__icon">🎯</text>
      <text class="score-empty__text">暂无已发布成绩</text>
    </view>
  </view>
</template>

<script>
import { getStudentScore } from '@/api/channel'

export default {
  data() {
    return { loading: false, list: [], studentName: '', studentId: '' }
  },
  onLoad(opts) {
    this.studentId = opts.studentId || ''
    this.studentName = decodeURIComponent(opts.name || '')
    this.loadData()
  },
  methods: {
    async loadData() {
      if (!this.studentId) return
      this.loading = true
      try {
        const res = await getStudentScore(this.studentId)
        this.list = Array.isArray(res) ? res : (res && res.rows) || []
      } catch (e) {
        this.list = []
      }
      this.loading = false
    },
    formatDate(v) { return v ? String(v).slice(0, 10) : '--' },
    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.score-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }
.score-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); }
.score-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.score-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }
.score-card { margin: 24rpx 32rpx 0; padding: 28rpx; background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); }
.score-card__top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.score-card__contest { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); flex: 1; }
.score-card__time { font-size: 22rpx; color: var(--jst-color-text-tertiary); flex-shrink: 0; }
.score-card__bottom { display: flex; gap: 32rpx; }
.score-card__rank { font-size: 26rpx; color: #3F51B5; font-weight: 600; }
.score-card__award { font-size: 26rpx; color: #F5A623; font-weight: 600; }
.score-card__score { font-size: 26rpx; color: var(--jst-color-text-secondary); }
.score-empty { text-align: center; padding: 120rpx 48rpx; }
.score-empty__icon { display: block; font-size: 80rpx; margin-bottom: 24rpx; }
.score-empty__text { font-size: 28rpx; color: var(--jst-color-text-tertiary); }
</style>
