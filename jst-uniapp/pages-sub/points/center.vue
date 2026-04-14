<!-- 中文注释: 积分中心
     对应原型: 小程序原型图/points-center.png
     调用接口: GET /jst/wx/points/center/summary + /levels + /tasks -->
<template>
  <view class="pc-page">
    <view class="pc-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="pc-hero__badge">Lv.{{ (summary.currentLevel && (summary.currentLevel.levelNo || summary.currentLevel.levelId)) || 0 }}</view>
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
          <text class="pc-level__name">{{ lv.icon || '' }} Lv.{{ lv.levelNo || lv.levelId }} · {{ lv.levelName }}</text>
          <text v-if="isCurrentLevel(lv)" class="pc-level__tag">当前</text>
          <text v-else-if="isLocked(lv)" class="pc-level__tag pc-level__tag--locked">还差 {{ lockedGap(lv) }} 成长值</text>
        </view>
        <text class="pc-level__desc">成长值门槛 {{ lv.growthThreshold || 0 }}</text>
      </view>
    </view>

    <view class="pc-section">
      <text class="pc-section__title">赚积分任务</text>
      <view v-for="t in tasks" :key="t.taskCode" class="pc-task">
        <text class="pc-task__icon">{{ t.icon || '✨' }}</text>
        <view class="pc-task__body">
          <text class="pc-task__name">{{ t.taskName }}</text>
          <text class="pc-task__desc">{{ t.taskDesc || '' }}</text>
        </view>
        <text class="pc-task__reward">{{ t.finished ? '已完成' : (t.rewardText || '') }}</text>
      </view>
      <u-empty v-if="!tasks.length" mode="data" text="暂无任务" />
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
      const next = s.nextLevel && Number(s.nextLevel.growthThreshold || 0)
      if (!next) return 100
      const cur = (s.currentLevel && Number(s.currentLevel.growthThreshold || 0)) || 0
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
      if (lv && lv.unlocked === false) return true
      const cur = this.summary.currentLevel && Number(this.summary.currentLevel.levelNo || this.summary.currentLevel.levelId)
      const lvNo = Number(lv.levelNo || lv.levelId)
      return cur != null && lvNo > cur
    },
    lockedGap(lv) {
      const gv = Number(this.summary.growthValue || 0)
      const need = Number(lv.growthThreshold || 0)
      return Math.max(0, need - gv)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.pc-page { min-height: 100vh; padding-bottom: $jst-space-xxl; background: $jst-bg-subtle; }
.pc-hero { padding: 96rpx $jst-space-xl 64rpx; background: $jst-hero-gradient-dark; color: $jst-text-inverse; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; text-align: center; }
.pc-hero__badge { display: inline-block; padding: $jst-space-sm $jst-space-xl; border-radius: $jst-radius-round; background: rgba(245,166,35,0.28); border: 2rpx solid rgba(245,166,35,0.6); color: $jst-gold; font-size: $jst-font-base; font-weight: $jst-weight-semibold; }
.pc-hero__name { display: block; margin-top: $jst-space-md; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; }
.pc-hero__bar { margin: $jst-space-lg auto 0; width: 520rpx; height: $jst-space-md; border-radius: $jst-radius-sm; background: rgba(255,255,255,0.22); overflow: hidden; }
.pc-hero__bar-fill { height: 100%; background: linear-gradient(90deg, $jst-gold, $jst-amber); border-radius: $jst-radius-sm; transition: width $jst-duration-normal $jst-easing; }
.pc-hero__hint { display: block; margin-top: $jst-space-sm; font-size: $jst-font-xs; color: rgba(255,255,255,0.76); }

.pc-stats { display: flex; margin: -40rpx $jst-space-xl 0; padding: $jst-space-lg 0; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.pc-stats__cell { flex: 1; text-align: center; }
.pc-stats__num { display: block; font-size: 40rpx; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.pc-stats__num--gold { color: $jst-amber; }
.pc-stats__label { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }

.pc-grid { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; display: grid; grid-template-columns: repeat(4, 1fr); gap: $jst-space-lg; }
.pc-grid__item { display: flex; flex-direction: column; align-items: center; padding: $jst-space-md 0; transition: transform $jst-duration-fast $jst-easing; }
.pc-grid__item:active { transform: scale(0.93); }
.pc-grid__icon { font-size: $jst-font-xxl; }
.pc-grid__text { margin-top: $jst-space-sm; font-size: $jst-font-xs; color: $jst-text-regular; }

.pc-section { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.pc-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: $jst-space-md; }

.pc-level { padding: $jst-space-md $jst-space-lg; border-radius: $jst-radius-lg; background: $jst-amber-light; margin-top: $jst-space-md; transition: transform $jst-duration-fast $jst-easing; }
.pc-level:active { transform: scale(0.98); }
.pc-level--active { background: linear-gradient(135deg, $jst-purple, $jst-amber); color: $jst-text-inverse; }
.pc-level--locked { opacity: 0.55; }
.pc-level__head { display: flex; align-items: center; justify-content: space-between; }
.pc-level__name { font-size: $jst-font-base; font-weight: $jst-weight-semibold; }
.pc-level__tag { padding: $jst-space-xs $jst-space-md; border-radius: $jst-radius-round; background: rgba(255,255,255,0.25); font-size: $jst-font-xs; }
.pc-level__tag--locked { background: $jst-bg-grey; color: $jst-text-secondary; }
.pc-level__desc { display: block; margin-top: $jst-space-sm; font-size: $jst-font-xs; opacity: 0.85; }

.pc-task { display: flex; align-items: center; gap: $jst-space-md; padding: $jst-space-md 0; border-bottom: 2rpx solid $jst-border; }
.pc-task:last-child { border-bottom: none; }
.pc-task__icon { font-size: 40rpx; }
.pc-task__body { flex: 1; min-width: 0; }
.pc-task__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.pc-task__desc { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }
.pc-task__reward { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-amber; }
</style>
