<!-- 中文注释: 积分商城 · 兑换单详情
     对应原型: 小程序原型图/points-order.png
     调用接口: GET /jst/wx/mall/exchange/{id}
               POST /jst/wx/mall/exchange/{id}/cancel
               POST /jst/wx/mall/exchange/{id}/pay/mock-success -->
<template>
  <view class="ed-page">
    <view class="ed-header" :style="{ paddingTop: navPaddingTop }">
      <text class="ed-header__no">{{ detail.exchangeNo || '--' }}</text>
      <text :class="['ed-header__status', 'ed-header__status--' + detail.status]">{{ statusLabel(detail.status) }}</text>
    </view>

    <view class="ed-section">
      <view class="ed-goods">
        <image class="ed-goods__img" :src="detail.coverImage || ''" mode="aspectFill" />
        <view class="ed-goods__info">
          <text class="ed-goods__name">{{ detail.goodsName || '--' }}</text>
          <text class="ed-goods__qty">x {{ detail.quantity || 1 }}</text>
          <view class="ed-goods__price">
            <text class="ed-goods__points">{{ detail.totalPoints || 0 }} 积分</text>
            <text v-if="detail.totalCash && Number(detail.totalCash) > 0" class="ed-goods__cash">+¥{{ detail.totalCash }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="detail.addressSnapshot" class="ed-section">
      <text class="ed-section__title">收货地址</text>
      <text class="ed-addr__name">{{ detail.addressSnapshot.consignee }} · {{ detail.addressSnapshot.phone }}</text>
      <text class="ed-addr__detail">{{ addressText }}</text>
    </view>

    <view class="ed-section">
      <text class="ed-section__title">状态流转</text>
      <view class="ed-timeline">
        <view class="ed-timeline__item ed-timeline__item--done">
          <view class="ed-timeline__dot"></view>
          <view class="ed-timeline__body"><text>提交申请</text><text class="ed-timeline__time">{{ formatTime(detail.createTime) }}</text></view>
        </view>
        <view :class="['ed-timeline__item', paidDone && 'ed-timeline__item--done']">
          <view class="ed-timeline__dot"></view>
          <view class="ed-timeline__body"><text>{{ paidLabel }}</text><text class="ed-timeline__time">{{ formatTime(detail.payTime) }}</text></view>
        </view>
        <view :class="['ed-timeline__item', ['shipped','completed'].includes(detail.status) && 'ed-timeline__item--done']">
          <view class="ed-timeline__dot"></view>
          <view class="ed-timeline__body"><text>{{ shipLabel }}</text><text class="ed-timeline__time">{{ formatTime(detail.shipTime) }}</text></view>
        </view>
      </view>
    </view>

    <view v-if="canCancel || canPay || canView" class="ed-footer">
      <button v-if="canPay" class="ed-footer__primary" @tap="onPay">去支付</button>
      <button v-if="canCancel" class="ed-footer__danger" @tap="onCancel">取消订单</button>
      <button v-if="canView" class="ed-footer__primary" @tap="onView">前往查看</button>
    </view>
  </view>
</template>

<script>
import { getExchangeDetail, cancelExchange, mockPayExchange } from '@/api/mall'

const STATUS_LABEL = { pending_pay: '待支付', paid: '已支付', pending_ship: '待发货', shipped: '已发货', completed: '已完成', cancelled: '已取消' }

export default {
  data() { return { detail: {}, exchangeId: null } },
  computed: {
    canCancel() { return this.detail.status === 'pending_pay' },
    canPay() { return this.detail.status === 'pending_pay' },
    canView() { return this.detail.status === 'completed' && this.detail.goodsType !== 'physical' },
    paidDone() { return !['pending_pay', 'cancelled'].includes(this.detail.status) },
    paidLabel() { return this.detail.status === 'cancelled' ? '已取消' : '已支付' },
    shipLabel() {
      if (this.detail.goodsType === 'physical') return this.detail.status === 'shipped' ? '已发货' : '待发货'
      return '已发放'
    },
    addressText() {
      const a = this.detail.addressSnapshot || {}
      return [a.province, a.city, a.district, a.detail].filter(Boolean).join(' ')
    }
  },
  onLoad(query) {
    this.exchangeId = query && query.id
    if (this.exchangeId) this.load()
  },
  methods: {
    async load() { try { this.detail = (await getExchangeDetail(this.exchangeId)) || {} } catch (e) {} },
    onCancel() {
      uni.showModal({
        title: '取消订单', content: '确认取消本次兑换？', success: async (res) => {
          if (!res.confirm) return
          try { await cancelExchange(this.exchangeId); uni.showToast({ title: '已取消', icon: 'success' }); this.load() } catch (e) {}
        }
      })
    },
    async onPay() {
      try { await mockPayExchange(this.exchangeId); uni.showToast({ title: '支付成功', icon: 'success' }); this.load() } catch (e) {}
    },
    onView() { uni.showToast({ title: '请在「我的优惠券/权益」查看', icon: 'none' }) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.ed-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.ed-header { padding: 64rpx $jst-space-xl $jst-space-xxl; background: $jst-amber-gradient; color: $jst-text-inverse; }
.ed-header__no { display: block; font-size: $jst-font-sm; color: rgba(255,255,255,0.76); }
.ed-header__status { display: inline-block; margin-top: $jst-space-sm; padding: $jst-space-xs $jst-space-md; border-radius: $jst-radius-round; background: rgba(255,255,255,0.18); color: $jst-text-inverse; font-size: $jst-font-xs; font-weight: $jst-weight-semibold; }

.ed-section { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.ed-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; margin-bottom: $jst-space-md; }

.ed-goods { display: flex; gap: $jst-space-md; }
.ed-goods__img { width: 160rpx; height: 160rpx; border-radius: $jst-radius-lg; background: $jst-bg-grey; }
.ed-goods__info { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.ed-goods__name { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ed-goods__qty { margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }
.ed-goods__price { margin-top: auto; display: flex; align-items: baseline; gap: $jst-space-sm; }
.ed-goods__points { font-size: $jst-font-md; font-weight: $jst-weight-semibold; color: $jst-amber; }
.ed-goods__cash { font-size: $jst-font-xs; color: $jst-text-secondary; }

.ed-addr__name { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ed-addr__detail { display: block; margin-top: $jst-space-xs; font-size: $jst-font-sm; color: $jst-text-regular; }

.ed-timeline__item { display: flex; align-items: flex-start; gap: $jst-space-md; padding: $jst-space-md 0; position: relative; }
.ed-timeline__item + .ed-timeline__item::before { content: ''; position: absolute; left: 14rpx; top: -16rpx; width: 2rpx; height: 32rpx; background: $jst-border; }
.ed-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: $jst-border; margin-top: $jst-space-xs; transition: background $jst-duration-normal $jst-easing; }
.ed-timeline__item--done .ed-timeline__dot { background: $jst-amber; }
.ed-timeline__body { flex: 1; display: flex; flex-direction: column; font-size: $jst-font-base; color: $jst-text-primary; }
.ed-timeline__time { margin-top: $jst-space-xs; font-size: $jst-font-xs; color: $jst-text-secondary; }

.ed-footer { position: fixed; left: 0; right: 0; bottom: 0; display: flex; gap: $jst-space-md; padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
.ed-footer__primary { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: $jst-radius-xl; background: $jst-amber-gradient; color: $jst-text-inverse; font-size: $jst-font-md; font-weight: $jst-weight-semibold; border: none; transition: opacity $jst-duration-fast $jst-easing; }
.ed-footer__primary:active { opacity: 0.85; }
.ed-footer__danger { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: $jst-radius-xl; background: $jst-danger-light; color: $jst-danger; font-size: $jst-font-md; font-weight: $jst-weight-semibold; border: none; transition: opacity $jst-duration-fast $jst-easing; }
.ed-footer__danger:active { opacity: 0.85; }
</style>
