<!-- 中文注释: 成绩公开查询页；对应原型 小程序原型图/score-query.html；对应接口 GET /jst/public/score/query -->
<template>
  <view class="score-query-page">
    <view class="score-query-page__header" :style="{ paddingTop: navPaddingTop }">
      <view class="score-query-page__back" @tap="goBack">←</view>
      <text class="score-query-page__header-title">成绩公开查询</text>
    </view>

    <!-- Hero 区 -->
    <view class="score-query-page__hero">
      <text class="score-query-page__hero-icon">🎯</text>
      <text class="score-query-page__hero-title">成绩查询</text>
      <text class="score-query-page__hero-desc">无需登录，输入手机号/证件号/报名编号即可查询</text>
    </view>

    <!-- 查询表单 -->
    <view class="score-query-page__form">
      <!-- 查询方式 Tab -->
      <view class="score-query-page__mode-tabs">
        <view
          v-for="mode in queryModes"
          :key="mode.value"
          class="score-query-page__mode-tab"
          :class="{ 'score-query-page__mode-tab--active': currentMode === mode.value }"
          @tap="currentMode = mode.value"
        >{{ mode.label }}</view>
      </view>

      <view class="score-query-page__field">
        <text class="score-query-page__field-label">{{ currentModeLabel }} <text style="color: $jst-danger;">*</text></text>
        <input
          class="score-query-page__input"
          v-model="queryValue"
          :placeholder="'请输入' + currentModeLabel"
          @confirm="doQuery"
        />
      </view>

      <view class="score-query-page__field">
        <text class="score-query-page__field-label">选择赛事 <text class="score-query-page__field-optional">选填，不填则查询全部</text></text>
        <input
          class="score-query-page__input"
          v-model="contestName"
          placeholder="全部赛事"
        />
      </view>

      <view class="score-query-page__submit" @tap="doQuery">查询成绩</view>
    </view>

    <!-- 提示 -->
    <view class="score-query-page__tip">
      <text class="score-query-page__tip-text">💡 如需查看完整历史成绩、证书下载，请登录账号后在「我的成绩」中查看</text>
    </view>

    <!-- 查询结果 -->
    <view v-if="queried" class="score-query-page__result">
      <view v-if="resultList.length" class="score-query-page__result-list">
        <view v-for="(item, idx) in resultList" :key="idx" class="score-query-page__result-card">
          <text class="score-query-page__result-name">{{ item.contestName || '未知赛事' }}</text>
          <view class="score-query-page__result-row">
            <text class="score-query-page__result-label">成绩</text>
            <text class="score-query-page__result-value">{{ item.score != null ? item.score : '--' }}</text>
          </view>
          <view v-if="item.rank" class="score-query-page__result-row">
            <text class="score-query-page__result-label">排名</text>
            <text class="score-query-page__result-value">第 {{ item.rank }} 名</text>
          </view>
          <view v-if="item.awardLevel" class="score-query-page__result-row">
            <text class="score-query-page__result-label">获奖</text>
            <text class="score-query-page__result-value">{{ item.awardLevel }}</text>
          </view>
        </view>
      </view>
      <jst-empty v-else text="未查询到相关成绩记录" icon="📊" />
    </view>
  </view>
</template>

<script>
import { queryPublicScore } from '@/api/score'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'

export default {
  components: { JstEmpty },
  data() {
    return {
      currentMode: 'mobile',
      queryModes: [
        { label: '手机号', value: 'mobile' },
        { label: '证件号', value: 'idCard' },
        { label: '报名编号', value: 'enrollNo' }
      ],
      queryValue: '',
      contestName: '',
      queried: false,
      resultList: []
    }
  },
  computed: {
    currentModeLabel() {
      const mode = this.queryModes.find(m => m.value === this.currentMode)
      return mode ? mode.label : '手机号'
    }
  },
  onLoad(query) {
    if (query && query.keyword) {
      this.queryValue = decodeURIComponent(query.keyword)
    }
  },
  methods: {
    async doQuery() {
      const val = this.queryValue.trim()
      if (!val) {
        uni.showToast({ title: '请输入查询信息', icon: 'none' })
        return
      }
      uni.showLoading({ title: '查询中...' })
      try {
        const params = {}
        if (this.currentMode === 'mobile') params.name = val
        if (this.currentMode === 'idCard') params.idCard4 = val
        if (this.currentMode === 'enrollNo') params.name = val
        if (this.contestName.trim()) params.contestId = this.contestName.trim()
        const list = await queryPublicScore(params)
        this.resultList = Array.isArray(list) ? list : []
        this.queried = true
      } catch (e) {
        this.resultList = []
        this.queried = true
      } finally {
        uni.hideLoading()
      }
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/index/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.score-query-page { min-height: 100vh; background: #F7F8FA; }

.score-query-page__header { display: flex; align-items: center; padding: 24rpx; background: $jst-bg-card; }
.score-query-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: #F7F8FA; font-size: 30rpx; color: $jst-text-primary; }
.score-query-page__header-title { flex: 1; margin-left: 16rpx; font-size: 34rpx; font-weight: 600; color: $jst-text-primary; }

/* Hero */
.score-query-page__hero { padding: 48rpx 32rpx 32rpx; background: linear-gradient(135deg, #1A237E, #283593); border-bottom-left-radius: 32rpx; border-bottom-right-radius: 32rpx; }
.score-query-page__hero-icon { display: block; font-size: 56rpx; margin-bottom: 16rpx; }
.score-query-page__hero-title { display: block; font-size: 40rpx; font-weight: 600; color: #fff; }
.score-query-page__hero-desc { display: block; margin-top: 12rpx; font-size: 26rpx; color: rgba(255,255,255,0.7); }

/* 表单 */
.score-query-page__form { margin: 24rpx; padding: 28rpx; border-radius: 32rpx; background: $jst-bg-card; box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04); }

.score-query-page__mode-tabs { display: flex; gap: 12rpx; margin-bottom: 28rpx; }
.score-query-page__mode-tab { flex: 1; height: 72rpx; display: flex; align-items: center; justify-content: center; border-radius: $jst-radius-xl; border: 2rpx solid $jst-border; font-size: 26rpx; color: $jst-text-regular; }
.score-query-page__mode-tab--active { border-color: $jst-brand; background: $jst-brand-light; color: $jst-brand; font-weight: 600; }

.score-query-page__field { margin-bottom: 24rpx; }
.score-query-page__field-label { display: block; margin-bottom: 12rpx; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; }
.score-query-page__field-optional { font-size: 22rpx; font-weight: 400; color: $jst-text-secondary; }
.score-query-page__input { height: 88rpx; padding: 0 24rpx; border-radius: $jst-radius-xl; background: #F7F8FA; border: 2rpx solid $jst-border; font-size: 28rpx; }

.score-query-page__submit { display: flex; align-items: center; justify-content: center; height: 96rpx; border-radius: $jst-radius-xl; background: linear-gradient(135deg, #FF6B35, #FF8C61); font-size: 32rpx; font-weight: 600; color: #fff; margin-top: 8rpx; }

/* 提示 */
.score-query-page__tip { margin: 24rpx; padding: 20rpx 24rpx; border-radius: $jst-radius-xl; background: $jst-brand-light; }
.score-query-page__tip-text { font-size: 24rpx; color: $jst-brand; line-height: 1.6; }

/* 结果 */
.score-query-page__result { margin: 0 24rpx; }
.score-query-page__result-card { padding: 24rpx; border-radius: 32rpx; background: $jst-bg-card; box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04); margin-bottom: 16rpx; }
.score-query-page__result-name { display: block; font-size: 30rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 16rpx; }
.score-query-page__result-row { display: flex; justify-content: space-between; padding: 10rpx 0; border-bottom: 2rpx solid $jst-border; font-size: 26rpx; }
.score-query-page__result-row:last-child { border-bottom: none; }
.score-query-page__result-label { color: $jst-text-secondary; }
.score-query-page__result-value { color: $jst-text-primary; font-weight: 600; }
</style>
