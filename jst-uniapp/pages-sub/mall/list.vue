<!-- 中文注释: 积分商城 · 商品列表
     对应原型: 小程序原型图/points-mall.png
     调用接口: GET /jst/wx/mall/goods/list -->
<template>
  <view class="ml-page">
    <view class="ml-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="ml-hero__left">
        <text class="ml-hero__label">我的积分</text>
        <text class="ml-hero__num">{{ availablePoints }}</text>
      </view>
      <view class="ml-hero__right" @tap="goMyExchanges">
        <text>我的兑换 ›</text>
      </view>
    </view>

    <scroll-view class="ml-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['ml-tabs__item', activeType === tab.value && 'ml-tabs__item--active']"
        @tap="onChangeType(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <view class="ml-grid">
      <view
        v-for="item in list"
        :key="item.goodsId"
        class="ml-card"
        @tap="goDetail(item.goodsId)"
      >
        <image v-if="item.coverImage" class="ml-card__img" :src="item.coverImage" mode="aspectFill" />
        <view v-else class="ml-card__img ml-card__img--placeholder"><text>暂无图片</text></view>
        <view class="ml-card__body">
          <text class="ml-card__name">{{ item.goodsName || '--' }}</text>
          <view class="ml-card__price">
            <text class="ml-card__points">{{ item.pointsPrice || 0 }} 积分</text>
            <text v-if="item.cashPrice && Number(item.cashPrice) > 0" class="ml-card__cash">+¥{{ item.cashPrice }}</text>
          </view>
          <text class="ml-card__stock">库存 {{ item.stock != null ? item.stock : '--' }}</text>
        </view>
      </view>
      <view v-if="!list.length && !loading" class="ml-empty">暂无商品</view>
      <view v-if="loading" class="ml-empty">加载中...</view>
      <view v-if="!hasMore && list.length" class="ml-empty ml-empty--end">没有更多了</view>
    </view>

    <view class="ml-fab" @tap="goMyExchanges">🎁</view>
  </view>
</template>

<script>
import { getMallGoodsList } from '@/api/mall'
import { useUserStore } from '@/store/user'

const TABS = [
  { value: '', label: '全部' },
  { value: 'coupon', label: '优惠券' },
  { value: 'right', label: '权益' },
  { value: 'physical', label: '实物' }
]

export default {
  data() {
    return {
      tabs: TABS,
      activeType: '',
      list: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true
    }
  },
  computed: {
    availablePoints() {
      const store = useUserStore()
      return (store.userInfo && store.userInfo.availablePoints) || 0
    }
  },
  onShow() {
    this.load(true)
    const store = useUserStore()
    if (store.token) store.fetchProfile()
  },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getMallGoodsList({
          goodsType: this.activeType || undefined,
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
    onChangeType(v) { if (this.activeType === v) return; this.activeType = v; this.load(true) },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/mall/detail?id=' + id }) },
    goMyExchanges() { uni.navigateTo({ url: '/pages-sub/mall/exchange-list' }) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.ml-page { min-height: 100vh; background: #F7F8FA; padding-bottom: 80rpx; }
.ml-hero { display: flex; align-items: center; justify-content: space-between; padding: 72rpx 32rpx 40rpx; background: linear-gradient(135deg, #F5A623, #FF9800); color: #fff; }
.ml-hero__left { display: flex; flex-direction: column; }
.ml-hero__label { font-size: 24rpx; color: rgba(255,255,255,0.76); }
.ml-hero__num { margin-top: 8rpx; font-size: 56rpx; font-weight: 600; color: #fff; }
.ml-hero__right { padding: 12rpx 24rpx; border-radius: $jst-radius-round; background: rgba(255,255,255,0.18); font-size: 24rpx; color: #fff; }

.ml-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.ml-tabs__item { display: inline-block; padding: 0 40rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.ml-tabs__item--active { color: #F5A623; font-weight: 600; }
.ml-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 24rpx; right: 24rpx; height: 4rpx; background: #F5A623; border-radius: 2rpx; }

.ml-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24rpx; padding: 24rpx 24rpx 48rpx; }
.ml-card { background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: 0 2rpx 8rpx rgba(20, 30, 60, 0.04); overflow: hidden; }
.ml-card__img { width: 100%; height: 320rpx; background: #F7F8FA; }
.ml-card__img--placeholder { display: flex; align-items: center; justify-content: center; font-size: 24rpx; color: $jst-text-secondary; }
.ml-card__body { padding: 20rpx 24rpx; }
.ml-card__name { display: block; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; min-height: 68rpx; }
.ml-card__price { display: flex; align-items: baseline; gap: 12rpx; margin-top: 10rpx; }
.ml-card__points { font-size: 30rpx; font-weight: 600; color: #F5A623; }
.ml-card__cash { font-size: 22rpx; color: $jst-text-secondary; }
.ml-card__stock { display: block; margin-top: 6rpx; font-size: 20rpx; color: $jst-text-secondary; }
.ml-empty { grid-column: 1 / -1; padding: 80rpx; text-align: center; font-size: 24rpx; color: $jst-text-secondary; }
.ml-empty--end { padding: 40rpx; }

.ml-fab { position: fixed; right: 32rpx; bottom: calc(64rpx + env(safe-area-inset-bottom)); width: 96rpx; height: 96rpx; border-radius: 50%; background: linear-gradient(135deg, #F5A623, #FF9800); color: #fff; font-size: 44rpx; display: flex; align-items: center; justify-content: center; box-shadow: 0 12rpx 32rpx rgba(245,166,35,0.4); }
</style>
