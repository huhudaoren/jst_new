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
      <u-empty v-if="!list.length && !loading" mode="order" text="暂无兑换记录" />
      <u-loadmore v-if="list.length || loading" :status="hasMore ? (loading ? 'loading' : 'loadmore') : 'nomore'" />
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
.el-page { min-height: 100vh; background: $jst-bg-page; }
.el-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.el-tabs__item { display: inline-block; padding: 0 $jst-space-xl; height: 88rpx; line-height: 88rpx; font-size: $jst-font-base; color: $jst-text-secondary; position: relative; }
.el-tabs__item--active { color: $jst-amber; font-weight: $jst-weight-semibold; }
.el-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: $jst-space-md; right: $jst-space-md; height: 4rpx; background: $jst-amber; border-radius: 2rpx; }
.el-list { padding: $jst-space-xs 0 $jst-space-xl; }
.el-card { margin: $jst-space-md $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; transition: transform $jst-duration-fast $jst-easing; }
.el-card:active { transform: scale(0.98); }
.el-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-md; }
.el-card__no { font-size: $jst-font-sm; color: $jst-text-secondary; }
.el-card__status { padding: $jst-space-xs $jst-space-md; border-radius: $jst-radius-round; font-size: $jst-font-xs; background: $jst-warning-light; color: $jst-warning; }
.el-card__status--paid, .el-card__status--pending_ship, .el-card__status--shipped { background: $jst-brand-light; color: $jst-brand; }
.el-card__status--completed { background: $jst-success-light; color: $jst-success; }
.el-card__status--cancelled { background: $jst-bg-grey; color: $jst-text-secondary; }
.el-card__body { display: flex; gap: $jst-space-md; }
.el-card__img { width: 160rpx; height: 160rpx; border-radius: $jst-radius-lg; background: $jst-bg-grey; }
.el-card__info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.el-card__name { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.el-card__qty { margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }
.el-card__price { margin-top: auto; display: flex; align-items: baseline; gap: $jst-space-sm; }
.el-card__points { font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-amber; }
.el-card__cash { font-size: $jst-font-xs; color: $jst-text-secondary; }
</style>
