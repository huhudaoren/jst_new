<template>
  <view class="order-detail-page">
    <jst-loading :loading="pageLoading" text="订单详情加载中..." />

    <view class="order-detail-page__nav">
      <view class="order-detail-page__back" @tap="handleBack"><</view>
      <text class="order-detail-page__nav-title">订单详情</text>
      <view class="order-detail-page__nav-placeholder"></view>
    </view>

    <template v-if="detail.orderId">
      <view class="order-detail-page__hero">
        <jst-status-badge :text="statusText" :tone="statusTone" />
        <text class="order-detail-page__hero-title">{{ detail.itemName || detail.contestName || '订单' }}</text>
        <text class="order-detail-page__hero-amount">¥{{ formatAmount(detail.netPayAmount) }}</text>
        <text class="order-detail-page__hero-subtitle">
          订单号：{{ detail.orderNo || '--' }}
        </text>
      </view>

      <view class="order-detail-page__card">
        <text class="order-detail-page__card-title">金额明细</text>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">标价金额</text>
          <text class="order-detail-page__info-value">¥{{ formatAmount(detail.listAmount) }}</text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">优惠券减免</text>
          <text class="order-detail-page__info-value order-detail-page__info-value--highlight">
            -¥{{ formatAmount(detail.couponAmount) }}
          </text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">积分抵扣</text>
          <text class="order-detail-page__info-value order-detail-page__info-value--highlight">
            -¥{{ formatAmount(detail.pointsDeductAmount) }}
          </text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">实付金额</text>
          <text class="order-detail-page__info-value order-detail-page__info-value--price">
            ¥{{ formatAmount(detail.netPayAmount) }}
          </text>
        </view>
      </view>

      <view class="order-detail-page__card">
        <text class="order-detail-page__card-title">订单信息</text>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">赛事名称</text>
          <text class="order-detail-page__info-value">{{ detail.contestName || '--' }}</text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">参赛人</text>
          <text class="order-detail-page__info-value">{{ detail.participantName || '--' }}</text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">支付方式</text>
          <text class="order-detail-page__info-value">{{ getPayMethodText(detail.payMethod) }}</text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">支付单号</text>
          <text class="order-detail-page__info-value">{{ detail.paymentNo || '--' }}</text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">第三方流水</text>
          <text class="order-detail-page__info-value">{{ detail.thirdPartyNo || '--' }}</text>
        </view>
        <view class="order-detail-page__info-row">
          <text class="order-detail-page__info-key">售后状态</text>
          <text class="order-detail-page__info-value">{{ getRefundStatusText(detail.refundStatus) }}</text>
        </view>
      </view>

      <view v-if="enrollId" class="order-detail-page__card">
        <view class="order-detail-page__link-row">
          <text class="order-detail-page__card-title">关联报名</text>
          <text class="order-detail-page__link-text" @tap="navigateEnrollDetail">查看报名详情</text>
        </view>
        <text class="order-detail-page__link-desc">
          该入口来自报名详情页透传。当前订单接口未返回 enrollId，列表进入时无法直接反查报名详情。
        </text>
      </view>

      <view class="order-detail-page__card">
        <text class="order-detail-page__card-title">进度时间轴</text>
        <view v-for="(item, index) in timeline" :key="`${item.label}-${index}`" class="order-detail-page__timeline-item">
          <view class="order-detail-page__timeline-dot"></view>
          <view class="order-detail-page__timeline-main">
            <text class="order-detail-page__timeline-label">{{ item.label }}</text>
            <text class="order-detail-page__timeline-time">{{ item.time }}</text>
          </view>
        </view>
      </view>
    </template>

    <jst-empty
      v-else-if="!pageLoading"
      icon="🧾"
      text="订单详情暂未返回。"
    />

    <view v-if="showActions" class="order-detail-page__bottom">
      <button
        v-if="canCancel"
        class="order-detail-page__bottom-btn order-detail-page__bottom-btn--ghost"
        @tap="handleCancel"
      >
        取消订单
      </button>
      <button
        v-if="canApplyRefund"
        class="order-detail-page__bottom-btn order-detail-page__bottom-btn--ghost"
        @tap="openRefundPopup"
      >
        申请退款
      </button>
      <button
        v-if="canPay"
        class="order-detail-page__bottom-btn order-detail-page__bottom-btn--primary"
        @tap="handlePay"
      >
        去支付
      </button>
    </view>

    <view v-if="refundPopupVisible" class="order-detail-page__popup-mask" @tap="closeRefundPopup">
      <view class="order-detail-page__popup" @tap.stop>
        <text class="order-detail-page__popup-title">申请退款</text>
        <text class="order-detail-page__popup-desc">本期固定为仅退款，提交后将进入退款审核流程。</text>

        <view class="order-detail-page__popup-field">
          <text class="order-detail-page__popup-label">退款原因</text>
          <textarea
            v-model="refundReason"
            class="order-detail-page__popup-textarea"
            maxlength="100"
            placeholder="请填写退款原因，便于平台审核"
          />
        </view>

        <view class="order-detail-page__popup-actions">
          <button class="order-detail-page__popup-btn order-detail-page__popup-btn--ghost" @tap="closeRefundPopup">
            取消
          </button>
          <button class="order-detail-page__popup-btn order-detail-page__popup-btn--primary" @tap="submitRefund">
            提交申请
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { cancelOrder, getOrderDetail, mockPaySuccess } from '@/api/order'
import { applyRefund } from '@/api/refund'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

export default {
  components: {
    JstEmpty,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      pageLoading: false,
      orderId: '',
      enrollId: '',
      detail: {},
      refundPopupVisible: false,
      refundReason: '',
      refundSubmitting: false
    }
  },
  computed: {
    statusText() {
      if (this.isRefunding) {
        return '退款中'
      }
      const mapper = {
        created: '已创建',
        pending_pay: '待支付',
        paid: '已支付',
        reviewing: '审核中',
        in_service: '进行中',
        aftersale: '售后中',
        completed: '已完成',
        cancelled: '已取消',
        closed: '已关闭'
      }
      return mapper[this.detail.orderStatus] || '处理中'
    },
    statusTone() {
      if (this.isRefunding) {
        return 'refund'
      }
      const mapper = {
        created: 'gray',
        pending_pay: 'pending',
        paid: 'active',
        reviewing: 'active',
        in_service: 'active',
        aftersale: 'refund',
        completed: 'success',
        cancelled: 'gray',
        closed: 'gray'
      }
      return mapper[this.detail.orderStatus] || 'active'
    },
    canPay() {
      return this.detail.orderStatus === 'pending_pay'
    },
    canCancel() {
      return this.detail.orderStatus === 'pending_pay'
    },
    canApplyRefund() {
      return this.detail.orderStatus === 'paid'
        && this.detail.refundStatus === 'none'
        && this.isWithinAftersale
    },
    isRefunding() {
      return this.detail.orderStatus === 'aftersale' || ['partial', 'full'].includes(this.detail.refundStatus)
    },
    isWithinAftersale() {
      if (!this.detail.aftersaleDeadline) {
        return true
      }
      const deadline = this.parseTime(this.detail.aftersaleDeadline)
      if (!deadline) {
        return true
      }
      return deadline.getTime() >= Date.now()
    },
    showActions() {
      return this.canPay || this.canCancel || this.canApplyRefund
    },
    timeline() {
      const list = []
      if (this.detail.createTime) {
        list.push({
          label: '订单创建',
          time: this.formatDateTime(this.detail.createTime)
        })
      }
      if (this.detail.payTime) {
        list.push({
          label: '支付成功',
          time: this.formatDateTime(this.detail.payTime)
        })
      }
      if (this.detail.aftersaleDeadline) {
        list.push({
          label: '售后截止',
          time: this.formatDateTime(this.detail.aftersaleDeadline)
        })
      }
      if (!list.length) {
        list.push({
          label: '订单处理中',
          time: '--'
        })
      }
      return list
    }
  },
  onLoad(query) {
    this.orderId = query.orderId || ''
    this.enrollId = query.enrollId || ''
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      if (!this.orderId) {
        return
      }

      this.pageLoading = true
      try {
        const data = await getOrderDetail(this.orderId, { silent: true })
        this.detail = data || {}
      } catch (error) {
        this.detail = {}
      } finally {
        this.pageLoading = false
      }
    },

    async handlePay() {
      if (!this.canPay) {
        return
      }
      try {
        // TODO: 接入真实微信 JSAPI 支付
        await mockPaySuccess(this.orderId)
        uni.showToast({ title: '支付成功', icon: 'success' })
        this.fetchDetail()
      } catch (error) {}
    },

    handleCancel() {
      if (!this.canCancel) {
        return
      }
      uni.showModal({
        title: '取消订单',
        content: '确认取消当前待支付订单吗？',
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          try {
            await cancelOrder(this.orderId)
            uni.showToast({ title: '已取消', icon: 'success' })
            this.fetchDetail()
          } catch (error) {}
        }
      })
    },

    openRefundPopup() {
      this.refundPopupVisible = true
    },

    closeRefundPopup() {
      if (this.refundSubmitting) {
        return
      }
      this.refundPopupVisible = false
      this.refundReason = ''
    },

    async submitRefund() {
      if (!this.refundReason.trim() || this.refundSubmitting) {
        uni.showToast({ title: '请填写退款原因', icon: 'none' })
        return
      }

      this.refundSubmitting = true
      try {
        const result = await applyRefund(
          this.orderId,
          {
            refundType: 'refund_only',
            reason: this.refundReason.trim()
          }
        )
        uni.showToast({ title: '申请已提交', icon: 'success' })
        this.refundPopupVisible = false
        this.refundReason = ''
        this.fetchDetail()
        if (result && result.refundId) {
          setTimeout(() => {
            uni.navigateTo({
              url: `/pages-sub/my/refund-detail?refundId=${result.refundId}`
            })
          }, 300)
        }
      } catch (error) {
        this.refundSubmitting = false
      } finally {
        this.refundSubmitting = false
      }
    },

    navigateEnrollDetail() {
      if (!this.enrollId) {
        return
      }
      uni.navigateTo({
        url: `/pages-sub/my/enroll-detail?id=${this.enrollId}`
      })
    },

    getPayMethodText(value) {
      const mapper = {
        wechat: '微信支付',
        bank_transfer: '线下转账',
        points: '积分支付'
      }
      return mapper[value] || '待确认'
    },

    getRefundStatusText(value) {
      const mapper = {
        none: '暂无退款',
        partial: '部分退款',
        full: '全额退款'
      }
      return mapper[value] || '处理中'
    },

    formatAmount(value) {
      const amount = Number(value)
      if (Number.isNaN(amount)) {
        return '0.00'
      }
      return amount.toFixed(2)
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    },

    parseTime(value) {
      if (!value) {
        return null
      }
      const normalized = `${value}`.replace(/-/g, '/').replace('T', ' ')
      const date = new Date(normalized)
      if (Number.isNaN(date.getTime())) {
        return null
      }
      return date
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.navigateTo({ url: '/pages-sub/my/order-list' })
    }
  }
}
</script>

<style scoped lang="scss">
.order-detail-page {
  min-height: 100vh;
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
  background: #f4f7fc;
}

.order-detail-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 22rpx rgba(14, 58, 113, 0.05);
}

.order-detail-page__back,
.order-detail-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.order-detail-page__back {
  font-size: 34rpx;
  color: #66768f;
}

.order-detail-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
}

.order-detail-page__hero,
.order-detail-page__card {
  margin: 20rpx 24rpx 0;
  padding: 28rpx 24rpx;
  border-radius: 28rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 28rpx rgba(14, 58, 113, 0.06);
}

.order-detail-page__hero {
  background: linear-gradient(135deg, #0c3d6b 0%, #2f7de1 100%);
}

.order-detail-page__hero-title {
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 800;
  line-height: 1.45;
  color: #ffffff;
}

.order-detail-page__hero-amount {
  display: block;
  margin-top: 18rpx;
  font-size: 56rpx;
  font-weight: 800;
  line-height: 1;
  color: #ffffff;
}

.order-detail-page__hero-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.8);
}

.order-detail-page__card-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #1f2937;
}

.order-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 18rpx 0;
  border-top: 2rpx solid #edf1f7;
}

.order-detail-page__info-row:first-of-type {
  border-top: none;
  padding-top: 0;
}

.order-detail-page__info-key,
.order-detail-page__info-value {
  font-size: 24rpx;
  line-height: 1.6;
}

.order-detail-page__info-key {
  color: #7a869d;
}

.order-detail-page__info-value {
  flex: 1;
  text-align: right;
  color: #1f2937;
}

.order-detail-page__info-value--highlight {
  color: #ff6a3d;
}

.order-detail-page__info-value--price {
  font-size: 32rpx;
  font-weight: 800;
  color: #ff6a3d;
}

.order-detail-page__link-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.order-detail-page__link-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #2f7de1;
}

.order-detail-page__link-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: #7a869d;
}

.order-detail-page__timeline-item {
  position: relative;
  display: flex;
  gap: 18rpx;
  padding: 0 0 24rpx 4rpx;
}

.order-detail-page__timeline-item:last-child {
  padding-bottom: 0;
}

.order-detail-page__timeline-item:last-child::after {
  display: none;
}

.order-detail-page__timeline-item::after {
  content: '';
  position: absolute;
  left: 11rpx;
  top: 26rpx;
  bottom: 0;
  width: 2rpx;
  background: #dbe5f5;
}

.order-detail-page__timeline-dot {
  position: relative;
  z-index: 1;
  width: 18rpx;
  height: 18rpx;
  margin-top: 6rpx;
  border-radius: 50%;
  background: #2f7de1;
}

.order-detail-page__timeline-main {
  flex: 1;
}

.order-detail-page__timeline-label {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #1f2937;
}

.order-detail-page__timeline-time {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #7a869d;
}

.order-detail-page__bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 -12rpx 28rpx rgba(14, 58, 113, 0.08);
}

.order-detail-page__bottom-btn {
  min-width: 200rpx;
  height: 88rpx;
  padding: 0 28rpx;
  border-radius: 28rpx;
  font-size: 28rpx;
  font-weight: 700;
  line-height: 88rpx;
}

.order-detail-page__bottom-btn--ghost {
  border: 2rpx solid #d6e4fb;
  background: #ffffff;
  color: #2f7de1;
}

.order-detail-page__bottom-btn--primary {
  background: linear-gradient(135deg, #2f7de1 0%, #0c3d6b 100%);
  color: #ffffff;
}

.order-detail-page__popup-mask {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: flex-end;
  background: rgba(15, 23, 42, 0.42);
  z-index: 99;
}

.order-detail-page__popup {
  width: 100%;
  padding: 32rpx 24rpx calc(24rpx + env(safe-area-inset-bottom));
  border-top-left-radius: 32rpx;
  border-top-right-radius: 32rpx;
  background: #ffffff;
}

.order-detail-page__popup-title {
  display: block;
  font-size: 32rpx;
  font-weight: 800;
  color: #1f2937;
}

.order-detail-page__popup-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #7a869d;
}

.order-detail-page__popup-field {
  margin-top: 24rpx;
}

.order-detail-page__popup-label {
  display: block;
  margin-bottom: 12rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: #1f2937;
}

.order-detail-page__popup-textarea {
  width: 100%;
  min-height: 220rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: #f6f9ff;
  font-size: 24rpx;
  line-height: 1.7;
  color: #1f2937;
  box-sizing: border-box;
}

.order-detail-page__popup-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12rpx;
  margin-top: 24rpx;
}

.order-detail-page__popup-btn {
  min-width: 180rpx;
  height: 80rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 80rpx;
}

.order-detail-page__popup-btn--ghost {
  border: 2rpx solid #d6e4fb;
  background: #ffffff;
  color: #2f7de1;
}

.order-detail-page__popup-btn--primary {
  background: #2f7de1;
  color: #ffffff;
}

:deep(.jst-status-badge--pending) {
  background: rgba(255, 138, 0, 0.18);
}

:deep(.jst-status-badge--pending .jst-status-badge__text) {
  color: #ff8a00;
}

:deep(.jst-status-badge--active) {
  background: rgba(46, 125, 255, 0.18);
}

:deep(.jst-status-badge--active .jst-status-badge__text) {
  color: #2e7dff;
}

:deep(.jst-status-badge--refund) {
  background: rgba(139, 92, 246, 0.18);
}

:deep(.jst-status-badge--refund .jst-status-badge__text) {
  color: #8b5cf6;
}

:deep(.jst-status-badge--success) {
  background: rgba(82, 196, 26, 0.18);
}

:deep(.jst-status-badge--success .jst-status-badge__text) {
  color: #52c41a;
}
</style>
