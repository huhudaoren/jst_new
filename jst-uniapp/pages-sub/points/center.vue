<!-- 中文注释: 积分中心
     对应原型: 小程序原型图/points-center.png
     调用接口: GET /jst/wx/points/center/summary + /levels + /tasks -->
<template>
  <view class="pc-page">
    <view class="pc-hero">
      <view class="pc-hero__badge">Lv.{{ summary.currentLevel && summary.currentLevel.levelId || 0 }}</view>
      <text class="pc-hero__name">{{ (summary.currentLevel && summary.currentLevel.levelName) || '普通学员' }}</text>
      <view class="pc-hero__bar">
        <view class="pc-hero__bar-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
      <text class="pc-hero__hint">
        距 {{ (summary.nextLevel && summary.nextLevel.levelName) || '下一级' }}
        还差 {{ summary.pointsToNextLevel != null ? summary.pointsToNextLevel : '--' }} 成长值
      </text>
    </view>

    <view class="pc-stats">
      <view class="pc-stats__cell"><text class="pc-stats__num pc-stats__num--gold">{{ summary.availablePoints || 0 }}</text><text class="pc-stats__label">可用积分</text></view>
      <view class="pc-stats__cell"><text class="pc-stats__num">{{ summary.frozenPoints || 0 }}</text><text class="pc-stats__label">冻结积分</text></view>
      <view class="pc-stats__cell"><text class="pc-stats__num">{{ summary.totalEarn || 0 }}</text><text class="pc-stats__label">累计获取</text></view>
    </view>

    <view class="pc-grid">
      <view class="pc-grid__item" @tap="go('/pages-sub/mall/list')"><text class="pc-grid__icon">🛍</text><text class="pc-grid__text">积分商城</text></view>
      <view class="pc-grid__item" @tap="go('/pages-sub/points/detail')"><text class="pc-grid__icon">📒</text><text class="pc-grid__text">积分明细</text></view>
      <view class="pc-grid__item" @tap="go('/pages-sub/mall/exchange-list')"><text class="pc-grid__icon">🧾</text><text class="pc-grid__text">兑换订单</text></view>
      <view class="pc-grid__item" @tap="go('/pages-sub/coupon/center')"><text class="pc-grid__icon">🎫</text><text class="pc-grid__text">优惠券</text></view>
    </view>

    <view class="pc-section">
      <text class="pc-section__title">等级权益</text>
      <view v-for="lv in levels" :key="lv.levelId" :class="['pc-level', isCurrentLevel(lv) && 'pc-level--active', isLocked(lv) && 'pc-level--locked']">
        <view class="pc-level__head">
          <text class="pc-level__name">Lv.{{ lv.levelId }} · {{ lv.levelName }}</text>
          <text v-if="isCurrentLevel(lv)" class="pc-level__tag">当前</text>
          <text v-else-if="isLocked(lv)" class="pc-level__tag pc-level__tag--locked">还差 {{ lockedGap(lv) }} 成长值</text>
        </view>
        <text class="pc-level__desc">{{ lv.description || lv.benefitText || '-' }}</text>
      </view>
    </view>

    <view class="pc-section">
      <text class="pc-section__title">赚积分任务</text>
      <view v-for="t in tasks" :key="t.taskId || t.code" class="pc-task">
        <text class="pc-task__icon">{{ t.icon || '✨' }}</text>
        <view class="pc-task__body">
          <text class="pc-task__name">{{ t.name || t.title }}</text>
          <text class="pc-task__desc">{{ t.description || '' }}</text>
        </view>
        <text class="pc-task__reward">+{{ t.pointsReward || t.reward || 0 }}</text>
      </view>
      <view v-if="!tasks.length" class="pc-empty">暂无任务</view>
    </view>
  </view>
</template>

<script>
import { getPointsSummary, getPointsLevels, getPointsTasks } from '@/api/points'

export default {
  data() { return { summary: {}, levels: [], tasks: [] } },
  computed: {
    progressPercent() {
      const s = this.summary || {}
      const gv = Number(s.growthValue || 0)
      const next = s.nextLevel && Number(s.nextLevel.growthValueRequired || 0)
      if (!next) return 100
      const cur = s.currentLevel && Number(s.currentLevel.growthValueRequired || 0)
      const total = next - cur
      const done = gv - cur
      if (total <= 0) return 100
      return Math.max(0, Math.min(100, Math.round((done / total) * 100)))
    }
  },
  onShow() { this.load() },
  methods: {
    async load() {
      try {
        const [summary, levels, tasks] = await Promise.all([
          getPointsSummary(),
          getPointsLevels('student'),
          getPointsTasks()
        ])
        this.summary = summary || {}
        this.levels = (levels && (levels.rows || levels)) || []
        this.tasks = (tasks && (tasks.rows || tasks)) || []
      } catch (e) {}
    },
    go(url) { uni.navigateTo({ url }) },
    isCurrentLevel(lv) {
      return this.summary.currentLevel && Number(this.summary.currentLevel.levelId) === Number(lv.levelId)
    },
    isLocked(lv) {
      const cur = this.summary.currentLevel && Number(this.summary.currentLevel.levelId)
      return cur != null && Number(lv.levelId) > cur
    },
    lockedGap(lv) {
      const gv = Number(this.summary.growthValue || 0)
      const need = Number(lv.growthValueRequired || 0)
      return Math.max(0, need - gv)
    }
  }
}
</script>

<style scoped lang="scss">
.pc-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }
.pc-hero { padding: 96rpx 32rpx 64rpx; background: linear-gradient(150deg, #4A0072 0%, #7B1FA2 55%, #F5A623 130%); color: #fff; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; text-align: center; }
.pc-hero__badge { display: inline-block; padding: 12rpx 32rpx; border-radius: var(--jst-radius-full); background: rgba(245,166,35,0.28); border: 2rpx solid rgba(245,166,35,0.6); color: #FFD54F; font-size: 28rpx; font-weight: 800; }
.pc-hero__name { display: block; margin-top: 20rpx; font-size: 36rpx; font-weight: 800; }
.pc-hero__bar { margin: 24rpx auto 0; width: 520rpx; height: 16rpx; border-radius: 8rpx; background: rgba(255,255,255,0.22); overflow: hidden; }
.pc-hero__bar-fill { height: 100%; background: linear-gradient(90deg, #FFD54F, #F5A623); border-radius: 8rpx; transition: width .3s; }
.pc-hero__hint { display: block; margin-top: 14rpx; font-size: 22rpx; color: var(--jst-color-white-76); }

.pc-stats { display: flex; margin: -40rpx 32rpx 0; padding: 24rpx 0; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.pc-stats__cell { flex: 1; text-align: center; }
.pc-stats__num { display: block; font-size: 40rpx; font-weight: 800; color: var(--jst-color-text); }
.pc-stats__num--gold { color: #F5A623; }
.pc-stats__label { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

.pc-grid { margin: 24rpx 32rpx 0; padding: 28rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); display: grid; grid-template-columns: repeat(4, 1fr); gap: 24rpx; }
.pc-grid__item { display: flex; flex-direction: column; align-items: center; padding: 20rpx 0; }
.pc-grid__icon { font-size: 48rpx; }
.pc-grid__text { margin-top: 12rpx; font-size: 22rpx; color: var(--jst-color-text-secondary); }

.pc-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.pc-section__title { display: block; font-size: 28rpx; font-weight: 800; color: var(--jst-color-text); margin-bottom: 16rpx; }

.pc-level { padding: 20rpx 24rpx; border-radius: var(--jst-radius-sm); background: #F3E8FF; margin-top: 16rpx; }
.pc-level--active { background: linear-gradient(135deg, #7B1FA2, #F5A623); color: #fff; }
.pc-level--locked { opacity: 0.55; }
.pc-level__head { display: flex; align-items: center; justify-content: space-between; }
.pc-level__name { font-size: 26rpx; font-weight: 700; }
.pc-level__tag { padding: 4rpx 16rpx; border-radius: var(--jst-radius-full); background: rgba(255,255,255,0.25); font-size: 20rpx; }
.pc-level__tag--locked { background: var(--jst-color-gray-soft); color: var(--jst-color-text-tertiary); }
.pc-level__desc { display: block; margin-top: 10rpx; font-size: 22rpx; opacity: 0.85; }

.pc-task { display: flex; align-items: center; gap: 20rpx; padding: 20rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.pc-task:last-child { border-bottom: none; }
.pc-task__icon { font-size: 40rpx; }
.pc-task__body { flex: 1; min-width: 0; }
.pc-task__name { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); }
.pc-task__desc { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.pc-task__reward { font-size: 28rpx; font-weight: 800; color: #F5A623; }
.pc-empty { padding: 40rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
</style>
