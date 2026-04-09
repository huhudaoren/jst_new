<!-- 中文注释: 积分商城 · 商品详情
     对应原型: 小程序原型图/points-goods-detail.png
     调用接口: GET /jst/wx/mall/goods/{id}
               POST /jst/wx/mall/exchange/apply -->
<template>
  <view class="md-page">
    <swiper class="md-swiper" :indicator-dots="(goods.images || []).length > 1" autoplay circular>
      <swiper-item v-for="(img, idx) in galleryImages" :key="idx">
        <image class="md-swiper__img" :src="img" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <view class="md-body">
      <text class="md-name">{{ goods.goodsName || '--' }}</text>
      <view class="md-price">
        <text class="md-price__points">{{ goods.pointsPrice || 0 }} 积分</text>
        <text v-if="hasCash" class="md-price__cash">+¥{{ goods.cashPrice }}</text>
      </view>
      <text class="md-stock">库存 {{ goods.stock != null ? goods.stock : '--' }} · 已兑换 {{ goods.exchangedCount || 0 }}</text>

      <view class="md-section">
        <text class="md-section__title">数量</text>
        <view class="md-stepper">
          <view class="md-stepper__btn" @tap="dec">-</view>
          <text class="md-stepper__val">{{ quantity }}</text>
          <view class="md-stepper__btn" @tap="inc">+</view>
        </view>
      </view>

      <view v-if="isPhysical" class="md-section">
        <text class="md-section__title">收货地址</text>
        <view class="md-address" @tap="pickAddress">
          <block v-if="address.consignee">
            <text class="md-address__name">{{ address.consignee }} · {{ address.phone }}</text>
            <text class="md-address__detail">{{ addressText }}</text>
          </block>
          <text v-else class="md-address__empty">请选择收货地址 ›</text>
        </view>
      </view>

      <view class="md-section">
        <text class="md-section__title">商品说明</text>
        <text class="md-desc">{{ goods.description || '--' }}</text>
      </view>
    </view>

    <view class="md-footer">
      <view class="md-footer__info">
        <text class="md-footer__label">总计</text>
        <text class="md-footer__amount">{{ totalPoints }} 积分<text v-if="hasCash" class="md-footer__cash"> +¥{{ totalCash }}</text></text>
      </view>
      <button class="md-footer__btn" :disabled="submitting || stockInsufficient" @tap="onSubmit">
        {{ submitting ? '提交中...' : (stockInsufficient ? '库存不足' : '立即兑换') }}
      </button>
    </view>
  </view>
</template>

<script>
import { getMallGoodsDetail, applyExchange, mockPayExchange } from '@/api/mall'

export default {
  data() {
    return {
      goodsId: null,
      goods: {},
      quantity: 1,
      address: {}, // { consignee, phone, province, city, district, detail }
      submitting: false
    }
  },
  computed: {
    galleryImages() {
      const imgs = (this.goods && this.goods.images) || []
      if (imgs.length) return imgs
      return this.goods.coverImage ? [this.goods.coverImage] : ['']
    },
    hasCash() { return this.goods && this.goods.cashPrice && Number(this.goods.cashPrice) > 0 },
    isPhysical() { return this.goods && this.goods.goodsType === 'physical' },
    totalPoints() { return (Number(this.goods.pointsPrice || 0) * this.quantity) },
    totalCash() { return (Number(this.goods.cashPrice || 0) * this.quantity).toFixed(2) },
    stockInsufficient() { return this.goods && this.goods.stock != null && this.quantity > this.goods.stock },
    addressText() {
      const a = this.address || {}
      return [a.province, a.city, a.district, a.detail].filter(Boolean).join(' ')
    }
  },
  onLoad(query) {
    this.goodsId = query && query.id
    if (this.goodsId) this.load()
  },
  onShow() {
    const picked = uni.getStorageSync('mall_picked_address')
    if (picked) {
      this.address = picked
      uni.removeStorageSync('mall_picked_address')
    }
  },
  methods: {
    async load() { try { this.goods = (await getMallGoodsDetail(this.goodsId)) || {} } catch (e) {} },
    inc() { if (this.goods.stock == null || this.quantity < this.goods.stock) this.quantity += 1 },
    dec() { if (this.quantity > 1) this.quantity -= 1 },
    pickAddress() {
      // 暂用 uni.chooseAddress 兜底；真实项目应跳地址管理页
      uni.chooseAddress({
        success: (res) => {
          this.address = {
            consignee: res.userName,
            phone: res.telNumber,
            province: res.provinceName,
            city: res.cityName,
            district: res.countyName,
            detail: res.detailInfo
          }
        },
        fail: () => uni.showToast({ title: '请手动完善地址', icon: 'none' })
      })
    },
    async onSubmit() {
      if (this.isPhysical && !this.address.consignee) {
        uni.showToast({ title: '请先选择收货地址', icon: 'none' })
        return
      }
      const body = {
        goodsId: this.goodsId,
        quantity: this.quantity,
        payMethod: this.hasCash ? 'mixed' : 'points'
      }
      if (this.isPhysical) body.addressSnapshot = { ...this.address }
      this.submitting = true
      try {
        const res = await applyExchange(body)
        const status = res && res.status
        if (status === 'pending_pay' && res.exchangeId) {
          // mock 支付立即成功 (开发期)
          try { await mockPayExchange(res.exchangeId) } catch (e) {}
          uni.showToast({ title: '兑换成功', icon: 'success' })
          setTimeout(() => uni.redirectTo({ url: '/pages-sub/mall/exchange-detail?id=' + res.exchangeId }), 600)
        } else if (res && res.exchangeId) {
          uni.showToast({ title: '兑换成功', icon: 'success' })
          setTimeout(() => uni.redirectTo({ url: '/pages-sub/mall/exchange-detail?id=' + res.exchangeId }), 600)
        }
      } finally { this.submitting = false }
    }
  }
}
</script>

<style scoped lang="scss">
.md-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.md-swiper { height: 600rpx; background: var(--jst-color-page-bg); }
.md-swiper__img { width: 100%; height: 100%; }
.md-body { background: var(--jst-color-card-bg); padding: 32rpx; }
.md-name { display: block; font-size: 36rpx; font-weight: 800; color: var(--jst-color-text); }
.md-price { display: flex; align-items: baseline; gap: 16rpx; margin-top: 16rpx; }
.md-price__points { font-size: 48rpx; font-weight: 900; color: #F5A623; }
.md-price__cash { font-size: 26rpx; color: var(--jst-color-text-secondary); }
.md-stock { display: block; margin-top: 12rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

.md-section { margin-top: 32rpx; padding-top: 24rpx; border-top: 2rpx solid var(--jst-color-border); }
.md-section__title { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.md-stepper { display: flex; align-items: center; gap: 24rpx; }
.md-stepper__btn { width: 64rpx; height: 64rpx; line-height: 64rpx; text-align: center; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); font-size: 36rpx; color: var(--jst-color-text); }
.md-stepper__val { font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); min-width: 64rpx; text-align: center; }

.md-address { padding: 20rpx 24rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); }
.md-address__name { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.md-address__detail { display: block; margin-top: 6rpx; font-size: 24rpx; color: var(--jst-color-text-secondary); }
.md-address__empty { font-size: 26rpx; color: var(--jst-color-brand); }
.md-desc { display: block; font-size: 24rpx; line-height: 1.7; color: var(--jst-color-text-secondary); white-space: pre-wrap; }

.md-footer { position: fixed; left: 0; right: 0; bottom: 0; display: flex; align-items: center; gap: 20rpx; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.md-footer__info { flex: 1; }
.md-footer__label { display: block; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.md-footer__amount { display: block; font-size: 36rpx; font-weight: 800; color: #F5A623; }
.md-footer__cash { font-size: 22rpx; color: var(--jst-color-text-secondary); font-weight: 500; }
.md-footer__btn { flex: 1; height: 96rpx; line-height: 96rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #F5A623, #FF9800); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.md-footer__btn[disabled] { opacity: 0.5; }
</style>
