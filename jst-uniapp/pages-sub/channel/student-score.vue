<!-- 中文注释: 渠道方 · 查看学生成绩 (E1-CH-3)
     数据来源: GET /jst/wx/channel/students/{id}/score -->
<template>
  <view class="score-page">
    <view class="score-header" :style="{ paddingTop: navPaddingTop }">
      <view class="score-header__back" @tap="goBack">←</view>
      <text class="score-header__title">{{ studentName }} 的成绩</text>
    </view>

    <view class="score-list">
      <view v-for="item in list" :key="item.scoreId" class="score-card">
        <view class="score-card__top">
          <text class="score-card__contest">{{ item.contestName || '--' }}</text>
          <u-tag :text="formatDate(item.publishTime)" type="info" size="mini" plain shape="circle"></u-tag>
        </view>
        <view class="score-card__bottom">
          <u-tag :text="'排名 ' + (item.rank || '--')" type="primary" size="mini" plain shape="circle"></u-tag>
          <u-tag :text="item.awardLevel || '暂无奖项'" type="warning" size="mini" plain shape="circle"></u-tag>
          <u-tag :text="item.score != null ? item.score + '分' : '--'" type="success" size="mini" plain shape="circle"></u-tag>
        </view>
      </view>
    </view>

    <u-loadmore v-if="list.length" :status="loading ? 'loading' : 'nomore'" />
    <u-empty v-if="!list.length && !loading" mode="data" text="暂无已发布成绩"></u-empty>
  </view>
</template>

<script>
import { getStudentScore } from '@/api/channel'

export default {
  data() {
    return { loading: false, list: [], studentName: '', studentId: '', skeletonShow: true /* [visual-effect] */ }
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
@import '@/styles/design-tokens.scss';

.score-page { min-height: 100vh; padding-bottom: $jst-space-xxl; background: $jst-bg-page; }
.score-header { display: flex; align-items: center; padding: 0 $jst-space-xl; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.score-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: $jst-font-xl; margin-right: $jst-space-lg; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.score-header__title { flex: 1; font-size: 34rpx; font-weight: $jst-weight-semibold; color: $jst-text-primary; }

.score-list { padding: $jst-space-lg $jst-space-xl 0; }
.score-card { margin-bottom: $jst-space-lg; padding: $jst-space-lg; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; }
.score-card__top { display: flex; align-items: center; justify-content: space-between; gap: $jst-space-md; }
.score-card__contest { flex: 1; min-width: 0; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.score-card__bottom { margin-top: $jst-space-md; display: flex; flex-wrap: wrap; gap: $jst-space-sm; }
</style>
