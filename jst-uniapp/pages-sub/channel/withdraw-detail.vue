<!-- 中文注释: 渠道方 · 提现单详情
     调用接口: GET /jst/wx/channel/withdraw/{id}
              POST /jst/wx/channel/withdraw/{id}/cancel -->
<template>
  <view class="wd-page">
    <view class="wd-hero">
      <text class="wd-hero__no">{{ detail.settlementNo || '--' }}</text>
      <text :class="['wd-hero__status', 'wd-hero__status--' + detail.status]">{{ statusLabel(detail.status) }}</text>
      <view class="wd-hero__amounts">
        <view class="wd-hero__row">
          <text class="wd-hero__label">申请金额</text>
          <text class="wd-hero__value">¥{{ formatAmount(detail.applyAmount) }}</text>
        </view>
        <view class="wd-hero__row">
          <text class="wd-hero__label">历史回退抵扣</text>
          <text class="wd-hero__value">-¥{{ formatAmount(detail.negativeOffsetAmount) }}</text>
        </view>
        <view class="wd-hero__row wd-hero__row--strong">
          <text class="wd-hero__label">实际打款</text>
          <text class="wd-hero__value wd-hero__value--gold">¥{{ formatAmount(detail.actualPayAmount) }}</text>
        </view>
      </view>
    </view>

    <view class="wd-section">
      <view class="wd-section__title"><text>状态流转</text></view>
      <view class="wd-timeline">
        <view class="wd-timeline__item wd-timeline__item--done">
          <view class="wd-timeline__dot"></view>
          <view class="wd-timeline__body">
            <text class="wd-timeline__title">提交申请</text>
            <text class="wd-timeline__time">{{ formatTime(detail.applyTime) }}</text>
          </view>
        </view>
        <view :class="['wd-timeline__item', reviewDone && 'wd-timeline__item--done']">
          <view class="wd-timeline__dot"></view>
          <view class="wd-timeline__body">
            <text class="wd-timeline__title">{{ reviewLabel }}</text>
            <text class="wd-timeline__time" v-if="detail.auditRemark">{{ detail.auditRemark }}</text>
          </view>
        </view>
        <view :class="['wd-timeline__item', detail.status === 'paid' && 'wd-timeline__item--done']">
          <view class="wd-timeline__dot"></view>
          <view class="wd-timeline__body">
            <text class="wd-timeline__title">已打款</text>
            <text class="wd-timeline__time">{{ formatTime(detail.payTime) }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="wd-section">
      <view class="wd-section__title"><text>收款账户</text></view>
      <view class="wd-kv"><text class="wd-kv__k">开户行</text><text class="wd-kv__v">{{ (detail.payeeAccount && detail.payeeAccount.bankName) || '--' }}</text></view>
      <view class="wd-kv"><text class="wd-kv__k">账号</text><text class="wd-kv__v">{{ (detail.payeeAccount && detail.payeeAccount.accountNo) || '--' }}</text></view>
      <view class="wd-kv"><text class="wd-kv__k">户名</text><text class="wd-kv__v">{{ (detail.payeeAccount && detail.payeeAccount.accountName) || '--' }}</text></view>
    </view>

    <view v-if="detail.invoiceInfo" class="wd-section">
      <view class="wd-section__title"><text>发票信息</text></view>
      <view class="wd-kv"><text class="wd-kv__k">抬头</text><text class="wd-kv__v">{{ detail.invoiceInfo.title || '--' }}</text></view>
      <view class="wd-kv"><text class="wd-kv__k">税号</text><text class="wd-kv__v">{{ detail.invoiceInfo.taxNo || '--' }}</text></view>
      <view class="wd-kv"><text class="wd-kv__k">发票状态</text><text class="wd-kv__v">{{ detail.invoiceStatus || '--' }}</text></view>
    </view>

    <view class="wd-section">
      <view class="wd-section__title"><text>关联返点明细 ({{ (detail.ledgerItems || []).length }})</text></view>
      <view v-for="item in (detail.ledgerItems || [])" :key="item.ledgerId" class="wd-ledger">
        <view class="wd-ledger__main">
          <text class="wd-ledger__name">{{ item.contestName || '——' }}</text>
          <text class="wd-ledger__sub">订单 {{ item.orderNo || '--' }}</text>
        </view>
        <text :class="['wd-ledger__amount', item.direction === 'negative' && 'wd-ledger__amount--neg']">
          {{ item.direction === 'negative' ? '-' : '' }}¥{{ formatAmount(item.rebateAmount) }}
        </text>
      </view>
    </view>

    <view v-if="canCancel" class="wd-footer">
      <button class="wd-footer__btn" @tap="onCancel" :disabled="cancelling">{{ cancelling ? '取消中...' : '取消申请' }}</button>
    </view>
  </view>
</template>

<script>
import { getWithdrawDetail, cancelWithdraw } from '@/api/channel'

const STATUS_LABEL = { pending: '待审核', in_review: '审核中', paid: '已打款', rejected: '已驳回', cancelled: '已取消' }

export default {
  data() { return { detail: {}, settlementId: null, cancelling: false } },
  computed: {
    canCancel() { return this.detail.status === 'pending' },
    reviewDone() { return ['in_review', 'paid', 'rejected'].includes(this.detail.status) },
    reviewLabel() {
      if (this.detail.status === 'rejected') return '已驳回'
      if (this.detail.status === 'in_review') return '审核中'
      if (this.detail.status === 'paid') return '审核通过'
      return '等待审核'
    }
  },
  onLoad(query) {
    this.settlementId = query && query.id
    if (this.settlementId) this.load()
  },
  methods: {
    async load() {
      try { this.detail = (await getWithdrawDetail(this.settlementId)) || {} } catch (e) {}
    },
    onCancel() {
      uni.showModal({
        title: '取消申请',
        content: '确认取消本次提现申请？台账将恢复为可提现',
        success: async (res) => {
          if (!res.confirm) return
          this.cancelling = true
          try {
            await cancelWithdraw(this.settlementId)
            uni.showToast({ title: '已取消', icon: 'success' })
            this.load()
          } catch (e) {
            if (e && e.code === 40013) uni.showToast({ title: '状态已变更，无法取消', icon: 'none' })
          } finally { this.cancelling = false }
        }
      })
    },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return String(v) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.wd-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.wd-hero { padding: 64rpx 32rpx 48rpx; background: linear-gradient(135deg, #1A237E, #1058A0); color: #fff; }
.wd-hero__no { display: block; font-size: 24rpx; color: var(--jst-color-white-76); }
.wd-hero__status { display: inline-block; margin-top: 12rpx; padding: 6rpx 20rpx; border-radius: var(--jst-radius-full); background: rgba(245,166,35,0.22); color: #FFD54F; font-size: 22rpx; font-weight: 700; }
.wd-hero__status--paid { background: rgba(39,174,96,0.22); color: #9ef7c3; }
.wd-hero__status--rejected, .wd-hero__status--cancelled { background: var(--jst-color-white-18); color: var(--jst-color-white-76); }
.wd-hero__amounts { margin-top: 24rpx; padding: 24rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-white-18); }
.wd-hero__row { display: flex; justify-content: space-between; align-items: center; padding: 10rpx 0; }
.wd-hero__row--strong { border-top: 2rpx dashed var(--jst-color-white-24); margin-top: 10rpx; padding-top: 20rpx; }
.wd-hero__label { font-size: 24rpx; color: var(--jst-color-white-76); }
.wd-hero__value { font-size: 28rpx; font-weight: 700; color: #fff; }
.wd-hero__value--gold { font-size: 40rpx; color: #FFD54F; }

.wd-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.wd-section__title { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.wd-kv { display: flex; justify-content: space-between; padding: 12rpx 0; font-size: 26rpx; }
.wd-kv__k { color: var(--jst-color-text-tertiary); }
.wd-kv__v { color: var(--jst-color-text); }

.wd-timeline__item { display: flex; align-items: flex-start; gap: 20rpx; padding: 16rpx 0; position: relative; }
.wd-timeline__item + .wd-timeline__item::before { content: ''; position: absolute; left: 14rpx; top: -16rpx; width: 2rpx; height: 32rpx; background: var(--jst-color-border); }
.wd-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: var(--jst-color-border); margin-top: 8rpx; }
.wd-timeline__item--done .wd-timeline__dot { background: var(--jst-color-brand); }
.wd-timeline__body { flex: 1; }
.wd-timeline__title { display: block; font-size: 26rpx; color: var(--jst-color-text); font-weight: 600; }
.wd-timeline__time { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

.wd-ledger { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.wd-ledger:last-child { border-bottom: none; }
.wd-ledger__main { flex: 1; min-width: 0; }
.wd-ledger__name { display: block; font-size: 26rpx; color: var(--jst-color-text); font-weight: 600; }
.wd-ledger__sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.wd-ledger__amount { font-size: 28rpx; font-weight: 700; color: #F5A623; }
.wd-ledger__amount--neg { color: var(--jst-color-danger); }

.wd-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.wd-footer__btn { height: 88rpx; line-height: 88rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-danger-soft); color: var(--jst-color-danger); font-size: 30rpx; font-weight: 800; border: none; }
</style>
