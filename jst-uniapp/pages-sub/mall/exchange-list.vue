<!-- 中文注释: 积分商城 · 我的兑换订单
     调用接口: GET /jst/wx/mall/exchange/my?status=&pageNum= -->
<template>
  <view class="el-page">
    <scroll-view class="el-tabs" scroll-x :style="{ paddingTop: navPaddingTop }">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['el-tabs__item', activeStatus === tab.value && 'el-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>
    <view class="el-list">
      <view v-for="item in list" :key="item.exchangeId" class="el-card" @tap="goDetail(item.exchangeId)">
        <view class="el-card__head">
          <text class="el-card__no">{{ item.exchangeNo || '--' }}</text>
          <text :class="['el-card__status', 'el-card__status--' + item.status]">{{ statusLabel(item.status) }}</text>
        </view>
        <view class="el-card__body">
          <image class="el-card__img" :src="item.coverImage || ''" mode="aspectFill" />
          <view class="el-card__info">
            <text class="el-card__name">{{ item.goodsName || '--' }}</text>
            <text class="el-card__qty">x {{ item.quantity || 1 }}</text>
            <view class="el-card__price">
              <text class="el-card__points">{{ item.totalPoints || 0 }} 积分</text>
              <text v-if="item.totalCash && Number(item.totalCash) > 0" class="el-card__cash">+¥{{ item.totalCash }}</text>
            </view>
          </view>
        </view>
      </view>
      <view v-if="!list.length && !loading" class="el-empty">暂无兑换记录</view>
      <view v-if="loading" class="el-empty">加载中...</view>
      <view v-if="!hasMore && list.length" class="el-empty el-empty--end">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { getMyExchanges } from '@/api/mall'

const TABS = [
  { value: '', label: '全部' },
  { value: 'pending_pay', label: '待支付' },
  { value: 'pending_ship', label: '待发货' },
  { value: 'shipped', label: '已发货' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]
const STATUS_LABEL = { pending_pay: '待支付', paid: '已支付', pending_ship: '待发货', shipped: '已发货', completed: '已完成', cancelled: '已取消' }

export default {
  data() { return { tabs: TABS, activeStatus: '', list: [], pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true } },
  onShow() { this.load(true) },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getMyExchanges({
          status: this.activeStatus || undefined,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChange(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.load(true) },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/mall/exchange-detail?id=' + id }) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.el-page { min-height: 100vh; background: #F7F8FA; }
.el-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.el-tabs__item { display: inline-block; padding: 0 32rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.el-tabs__item--active { color: #F5A623; font-weight: 600; }
.el-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 20rpx; right: 20rpx; height: 4rpx; background: #F5A623; border-radius: 2rpx; }
.el-list { padding: 8rpx 0 32rpx; }
.el-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04); }
.el-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.el-card__no { font-size: 24rpx; color: $jst-text-secondary; }
.el-card__status { padding: 4rpx 16rpx; border-radius: $jst-radius-round; font-size: 22rpx; background: $jst-warning-light; color: $jst-warning; }
.el-card__status--paid, .el-card__status--pending_ship, .el-card__status--shipped { background: $jst-brand-light; color: $jst-brand; }
.el-card__status--completed { background: $jst-success-light; color: $jst-success; }
.el-card__status--cancelled { background: $jst-bg-grey; color: $jst-text-secondary; }
.el-card__body { display: flex; gap: 20rpx; }
.el-card__img { width: 160rpx; height: 160rpx; border-radius: $jst-radius-lg; background: #F7F8FA; }
.el-card__info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.el-card__name { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.el-card__qty { margin-top: 6rpx; font-size: 22rpx; color: $jst-text-secondary; }
.el-card__price { margin-top: auto; display: flex; align-items: baseline; gap: 12rpx; }
.el-card__points { font-size: 30rpx; font-weight: 600; color: #F5A623; }
.el-card__cash { font-size: 22rpx; color: $jst-text-secondary; }
.el-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: $jst-text-secondary; }
.el-empty--end { padding: 40rpx; }
</style>
