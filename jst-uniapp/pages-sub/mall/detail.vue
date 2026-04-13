<!-- 中文注释: 积分商城 · 商品详情
     对应原型: 小程序原型图/points-goods-detail.png
     调用接口: GET /jst/wx/mall/goods/{id}
               POST /jst/wx/mall/exchange/apply -->
<template>
  <view class="md-page" :style="{ paddingTop: navPaddingTop }">
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
          <block v-if="displayReceiverName">
            <text class="md-address__name">{{ displayReceiverName }} · {{ displayReceiverMobile }}</text>
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
    displayReceiverName() {
      return this.address.receiverName || this.address.consignee || ''
    },
    displayReceiverMobile() {
      return this.address.receiverMobileMasked || this.address.phoneMasked || this.address.phone || this.address.mobile || ''
    },
    hasCash() { return this.goods && this.goods.cashPrice && Number(this.goods.cashPrice) > 0 },
    isPhysical() { return this.goods && this.goods.goodsType === 'physical' },
    totalPoints() { return (Number(this.goods.pointsPrice || 0) * this.quantity) },
    totalCash() { return (Number(this.goods.cashPrice || 0) * this.quantity).toFixed(2) },
    stockInsufficient() { return this.goods && this.goods.stock != null && this.quantity > this.goods.stock },
    addressText() {
      const a = this.address || {}
      return [a.province, a.city, a.district, a.addressDetail || a.detailAddress || a.detail].filter(Boolean).join(' ')
    }
  },
  onLoad(query) {
    this.goodsId = query && query.id
    if (this.goodsId) this.load()
  },
  onShow() {
    const picked = uni.getStorageSync('ua_picked_address') || uni.getStorageSync('mall_picked_address')
    if (picked) {
      this.address = picked
      uni.removeStorageSync('ua_picked_address')
      uni.removeStorageSync('mall_picked_address')
    }
  },
  methods: {
    async load() { try { this.goods = (await getMallGoodsDetail(this.goodsId)) || {} } catch (e) {} },
    inc() { if (this.goods.stock == null || this.quantity < this.goods.stock) this.quantity += 1 },
    dec() { if (this.quantity > 1) this.quantity -= 1 },
    pickAddress() {
      uni.navigateTo({ url: '/pages-sub/my/address-list?mode=pick' })
    },
    async onSubmit() {
      if (this.isPhysical && !this.displayReceiverName) {
        uni.showToast({ title: '请先选择收货地址', icon: 'none' })
        return
      }
      const body = {
        goodsId: this.goodsId,
        quantity: this.quantity,
        payMethod: this.hasCash ? 'mixed' : 'points'
      }
      if (this.isPhysical) {
        body.addressSnapshot = {
          receiverName: this.address.receiverName || this.address.consignee,
          mobile: this.address.receiverMobile || this.address.phone || this.address.mobile,
          province: this.address.province,
          city: this.address.city,
          district: this.address.district,
          detailAddress: this.address.addressDetail || this.address.detailAddress || this.address.detail,
          postalCode: this.address.postalCode || ''
        }
      }
      this.submitting = true
      try {
        const res = await applyExchange(body)
        const status = res && res.status
        if (status === 'pending_pay' && res.exchangeId) {
          // 支付成功回调
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
@import '@/styles/design-tokens.scss';
.md-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.md-swiper { height: 600rpx; background: $jst-bg-grey; }
.md-swiper__img { width: 100%; height: 100%; }
.md-body { background: $jst-bg-card; padding: $jst-space-xl; }
.md-name { display: block; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.md-price { display: flex; align-items: baseline; gap: $jst-space-md; margin-top: $jst-space-md; }
.md-price__points { font-size: $jst-font-xxl; font-weight: $jst-weight-semibold; color: $jst-amber; }
.md-price__cash { font-size: $jst-font-base; color: $jst-text-regular; }
.md-stock { display: block; margin-top: $jst-space-sm; font-size: $jst-font-xs; color: $jst-text-secondary; }

.md-section { margin-top: $jst-space-xl; padding-top: $jst-space-lg; border-top: 2rpx solid $jst-border; }
.md-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: $jst-space-md; }
.md-stepper { display: flex; align-items: center; gap: $jst-space-lg; }
.md-stepper__btn { width: 64rpx; height: 64rpx; line-height: 64rpx; text-align: center; border-radius: $jst-radius-lg; background: $jst-bg-grey; font-size: $jst-font-xl; color: $jst-text-primary; transition: background $jst-duration-fast $jst-easing; }
.md-stepper__btn:active { background: $jst-border; }
.md-stepper__val { font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-text-primary; min-width: 64rpx; text-align: center; }

.md-address { padding: $jst-space-md $jst-space-lg; border-radius: $jst-radius-lg; background: $jst-bg-grey; }
.md-address__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.md-address__detail { display: block; margin-top: $jst-space-xs; font-size: $jst-font-sm; color: $jst-text-regular; }
.md-address__empty { font-size: $jst-font-base; color: $jst-brand; }
.md-desc { display: block; font-size: $jst-font-sm; line-height: 1.7; color: $jst-text-regular; white-space: pre-wrap; }

.md-footer { position: fixed; left: 0; right: 0; bottom: 0; display: flex; align-items: center; gap: $jst-space-md; padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
.md-footer__info { flex: 1; }
.md-footer__label { display: block; font-size: $jst-font-xs; color: $jst-text-secondary; }
.md-footer__amount { display: block; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; color: $jst-amber; }
.md-footer__cash { font-size: $jst-font-xs; color: $jst-text-regular; font-weight: $jst-weight-medium; }
.md-footer__btn { flex: 1; height: 96rpx; line-height: 96rpx; border-radius: $jst-radius-xl; background: $jst-amber-gradient; color: $jst-text-inverse; font-size: $jst-font-md; font-weight: $jst-weight-semibold; border: none; transition: opacity $jst-duration-fast $jst-easing; }
.md-footer__btn:active { opacity: 0.85; }
.md-footer__btn[disabled] { opacity: 0.5; }
</style>
