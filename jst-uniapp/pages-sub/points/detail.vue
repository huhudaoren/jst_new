<!-- 中文注释: 积分 / 成长值流水
     对应原型: 小程序原型图/points-detail.png
     调用接口: GET /jst/wx/points/ledger + /jst/wx/points/growth/ledger -->
<template>
  <view class="pd-page">
    <view class="pd-tabs">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['pd-tabs__item', activeTab === tab.value && 'pd-tabs__item--active']"
        @tap="onChangeTab(tab.value)"
      >{{ tab.label }}</view>
    </view>

    <view class="pd-list">
      <view v-for="(item, idx) in list" :key="item.ledgerId || idx" class="pd-card">
        <view class="pd-card__main">
          <text class="pd-card__name">{{ item.sourceAction || item.action || item.description || '积分变动' }}</text>
          <text class="pd-card__time">{{ formatTime(item.createTime) }}</text>
        </view>
        <view class="pd-card__amount">
          <text :class="['pd-card__num', Number(item.changeAmount || 0) >= 0 ? 'pd-card__num--pos' : 'pd-card__num--neg']">
            {{ Number(item.changeAmount || 0) >= 0 ? '+' : '' }}{{ item.changeAmount || 0 }}
          </text>
          <text class="pd-card__balance">余额 {{ item.balanceAfter != null ? item.balanceAfter : '--' }}</text>
        </view>
      </view>
      <view v-if="!list.length && !loading" class="pd-empty">暂无流水</view>
      <view v-if="loading" class="pd-empty">加载中...</view>
      <view v-if="!hasMore && list.length" class="pd-empty pd-empty--end">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { getPointsLedger, getGrowthLedger } from '@/api/points'

const TABS = [
  { value: 'points', label: '积分流水' },
  { value: 'growth', label: '成长值流水' }
]

export default {
  data() { return { tabs: TABS, activeTab: 'points', list: [], pageNum: 1, pageSize: 15, total: 0, loading: false, hasMore: true } },
  onLoad() { this.load(true) },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const fn = this.activeTab === 'points' ? getPointsLedger : getGrowthLedger
        const res = await fn({ pageNum: this.pageNum, pageSize: this.pageSize })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChangeTab(v) { if (this.activeTab === v) return; this.activeTab = v; this.load(true) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
.pd-page { min-height: 100vh; background: var(--jst-color-page-bg); }
.pd-tabs { display: flex; background: var(--jst-color-card-bg); border-bottom: 2rpx solid var(--jst-color-border); }
.pd-tabs__item { flex: 1; height: 88rpx; line-height: 88rpx; text-align: center; font-size: 28rpx; color: var(--jst-color-text-tertiary); position: relative; }
.pd-tabs__item--active { color: #7B1FA2; font-weight: 700; }
.pd-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 30%; right: 30%; height: 4rpx; background: #7B1FA2; border-radius: 2rpx; }

.pd-list { padding: 16rpx 0 32rpx; }
.pd-card { display: flex; justify-content: space-between; align-items: center; margin: 16rpx 32rpx 0; padding: 24rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.pd-card__main { flex: 1; min-width: 0; }
.pd-card__name { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); }
.pd-card__time { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.pd-card__amount { text-align: right; }
.pd-card__num { display: block; font-size: 32rpx; font-weight: 800; }
.pd-card__num--pos { color: var(--jst-color-success); }
.pd-card__num--neg { color: var(--jst-color-danger); }
.pd-card__balance { display: block; margin-top: 4rpx; font-size: 20rpx; color: var(--jst-color-text-tertiary); }
.pd-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.pd-empty--end { padding: 40rpx; }
</style>
