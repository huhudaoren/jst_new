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

    <!-- 骨架屏 -->
    <view v-if="loading && !list.length" class="ml-grid">
      <view v-for="i in 4" :key="i" class="ml-card">
        <u-skeleton rows="0" loading :title="false">
          <view class="ml-card__img ml-card__img--placeholder"></view>
        </u-skeleton>
        <view class="ml-card__body">
          <u-skeleton rows="2" rowsWidth="['80%','50%']" loading :title="false"></u-skeleton>
        </view>
      </view>
    </view>

    <view v-else class="ml-grid">
      <view
        v-for="(item, idx) in list"
        :key="item.goodsId"
        class="ml-card jst-anim-slide-up"
        :class="'jst-anim-stagger-' + Math.min(idx % 4 + 1, 8)"
        @tap="goDetail(item.goodsId)"
      >
        <image v-if="item.coverImage" class="ml-card__img jst-anim-fade-in" :src="item.coverImage" mode="aspectFill" />
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
    </view>

    <u-empty v-if="!list.length && !loading" mode="data" text="暂无商品" />
    <u-loadmore v-if="list.length" :status="hasMore ? (loading ? 'loading' : 'loadmore') : 'nomore'" />

    <view class="ml-fab jst-anim-scale-in" @tap="goMyExchanges">🎁</view>
  </view>
</template>

<script>
import { getMallGoodsList } from '@/api/mall'
import { useUserStore } from '@/store/user'

// 中文注释: 对齐 V4.0 原型 points-mall.html 的 4 Tab 分类
// 虚拟 = 优惠券 + 权益（后端支持 goodsType='virtual' 等价于 [coupon, rights]）
// 实物 = 实体奖品（后端 goodsType = physical）
// 我可兑换 = 所需积分 ≤ 当前可用积分（前端根据 availablePoints 过滤）
const TABS = [
  { value: 'all', label: '全部' },
  { value: 'virtual', label: '虚拟' },
  { value: 'physical', label: '实物' },
  { value: 'affordable', label: '我可兑换' }
]

export default {
  data() {
    return {
      tabs: TABS,
      activeType: 'all',
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
    // 中文注释: 把前端 Tab 值映射成后端支持的 goodsType 参数
    // physical → 后端直接过滤; virtual → 后端 goodsType='virtual' (等价于 [coupon, rights])
    // 若后端尚未合入 virtual 支持, applyClientFilter 会兜底前端过滤
    buildLoadParams() {
      const params = { pageNum: this.pageNum, pageSize: this.pageSize }
      if (this.activeType === 'physical') {
        params.goodsType = 'physical'
      } else if (this.activeType === 'virtual') {
        params.goodsType = 'virtual'
      }
      return params
    },
    // 中文注释: Tab = 虚拟 → 前端过滤 type ∈ [coupon, rights, right] 兜底；Tab = 我可兑换 → 过滤所需积分 ≤ 可用积分
    applyClientFilter(rows) {
      if (!Array.isArray(rows)) return []
      if (this.activeType === 'virtual') {
        // 兜底: 即使后端未支持 virtual 过滤, 前端仍能按 goodsType 筛出
        return rows.filter((r) => {
          const t = (r && r.goodsType) || ''
          return t === 'coupon' || t === 'rights' || t === 'right'
        })
      }
      if (this.activeType === 'affordable') {
        const pts = this.availablePoints
        return rows.filter((r) => Number((r && r.pointsPrice) || 0) <= pts)
      }
      return rows
    },
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getMallGoodsList(this.buildLoadParams())
        const rawRows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        const rows = this.applyClientFilter(rawRows)
        this.list = reset ? rows : this.list.concat(rows)
        // 中文注释: 由于前端做了过滤，hasMore 仍按原始 total 判断，过滤后列表可能短于 total
        this.hasMore = (reset ? rawRows.length : this.list.length) < this.total
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
.ml-page { min-height: 100vh; background: $jst-bg-page; padding-bottom: 80rpx; }
.ml-hero { display: flex; align-items: center; justify-content: space-between; padding: 72rpx $jst-space-xl 40rpx; background: $jst-amber-gradient; color: $jst-text-inverse; }
.ml-hero__left { display: flex; flex-direction: column; }
.ml-hero__label { font-size: $jst-font-sm; color: rgba(255,255,255,0.76); }
.ml-hero__num { margin-top: $jst-space-xs; font-size: 56rpx; font-weight: $jst-weight-semibold; color: $jst-text-inverse; }
.ml-hero__right { padding: $jst-space-sm $jst-space-lg; border-radius: $jst-radius-round; background: rgba(255,255,255,0.18); font-size: $jst-font-sm; color: $jst-text-inverse; }

.ml-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.ml-tabs__item { display: inline-block; padding: 0 40rpx; height: 88rpx; line-height: 88rpx; font-size: $jst-font-base; color: $jst-text-secondary; position: relative; }
.ml-tabs__item--active { color: $jst-amber; font-weight: $jst-weight-semibold; }
.ml-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: $jst-space-lg; right: $jst-space-lg; height: 4rpx; background: $jst-amber; border-radius: 2rpx; }

.ml-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: $jst-space-lg; padding: $jst-space-lg $jst-space-lg $jst-space-xxl; }
.ml-card { background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; overflow: hidden; transition: transform $jst-duration-fast $jst-easing; }
.ml-card:active { transform: scale(0.97); }
.ml-card__img { width: 100%; height: 320rpx; background: $jst-bg-grey; }
.ml-card__img--placeholder { display: flex; align-items: center; justify-content: center; font-size: $jst-font-sm; color: $jst-text-secondary; }
.ml-card__body { padding: $jst-space-md $jst-space-lg; }
.ml-card__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; min-height: 68rpx; }
.ml-card__price { display: flex; align-items: baseline; gap: $jst-space-sm; margin-top: $jst-space-sm; }
.ml-card__points { font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-amber; }
.ml-card__cash { font-size: $jst-font-xs; color: $jst-text-secondary; }
.ml-card__stock { display: block; margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }

.ml-fab { position: fixed; right: $jst-space-xl; bottom: calc(64rpx + env(safe-area-inset-bottom)); width: 96rpx; height: 96rpx; border-radius: 50%; background: $jst-amber-gradient; color: $jst-text-inverse; font-size: $jst-font-xxl; display: flex; align-items: center; justify-content: center; box-shadow: $jst-shadow-lg; transition: transform $jst-duration-fast $jst-easing; }
.ml-fab:active { transform: scale(0.9); }
</style>
