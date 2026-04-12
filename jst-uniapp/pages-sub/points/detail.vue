<!-- 中文注释: 积分 / 成长值流水
     对应原型: 小程序原型图/points-detail.png
     调用接口: GET /jst/wx/points/ledger + /jst/wx/points/growth/ledger -->
<template>
  <view class="pd-page">
    <view class="pd-tabs" :style="{ paddingTop: navPaddingTop }">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['pd-tabs__item', activeTab === tab.value && 'pd-tabs__item--active']"
        @tap="onChangeTab(tab.value)"
      >{{ tab.label }}</view>
    </view>

    <view class="pd-list">
      <view v-for="(item, idx) in list" :key="idx" class="pd-card">
        <view class="pd-card__main">
          <text class="pd-card__name">{{ item.remark || item.sourceType || '变动' }}</text>
          <text class="pd-card__time">{{ formatTime(item.createTime) }}</text>
        </view>
        <view class="pd-card__amount">
          <text :class="['pd-card__num', changeValue(item) >= 0 ? 'pd-card__num--pos' : 'pd-card__num--neg']">
            {{ changeValue(item) >= 0 ? '+' : '' }}{{ changeValue(item) }}
          </text>
          <text class="pd-card__balance">余额 {{ item.balanceAfter != null ? item.balanceAfter : '--' }}</text>
        </view>
      </view>
      <u-empty v-if="!list.length && !loading" mode="data" text="暂无流水" />
      <u-loadmore v-if="list.length || loading" :status="hasMore ? (loading ? 'loading' : 'loadmore') : 'nomore'" />
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
    // 积分流水字段 pointsChange / 成长值流水字段 growthChange, 二者都可能为 null
    changeValue(item) {
      const raw = this.activeTab === 'points' ? item.pointsChange : item.growthChange
      return Number(raw || 0)
    },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.pd-page { min-height: 100vh; background: $jst-bg-page; }
.pd-tabs { display: flex; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.pd-tabs__item { flex: 1; height: 88rpx; line-height: 88rpx; text-align: center; font-size: $jst-font-base; color: $jst-text-secondary; position: relative; }
.pd-tabs__item--active { color: $jst-brand; font-weight: $jst-weight-semibold; }
.pd-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 30%; right: 30%; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }

.pd-list { padding: $jst-space-md 0 $jst-space-xl; }
.pd-card { display: flex; justify-content: space-between; align-items: center; margin: $jst-space-md $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.pd-card__main { flex: 1; min-width: 0; }
.pd-card__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.pd-card__time { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }
.pd-card__amount { text-align: right; }
.pd-card__num { display: block; font-size: $jst-font-lg; font-weight: $jst-weight-semibold; }
.pd-card__num--pos { color: $jst-success; }
.pd-card__num--neg { color: $jst-danger; }
.pd-card__balance { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }
</style>
