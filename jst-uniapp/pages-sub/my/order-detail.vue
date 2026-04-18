<template>
  <view class="order-detail-page">
    <jst-loading :loading="pageLoading" text="订单详情加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="order-detail-page__nav">
      <view class="order-detail-page__back" @tap="handleBack">返回</view>
      <text class="order-detail-page__nav-title">订单详情</text>
      <view class="order-detail-page__nav-placeholder"></view>
    </view>

    <!-- Q-03: 临时档案合并标注 -->
    <view v-if="originalParticipantName" class="order-detail-page__merge-tag">
      <text class="order-detail-page__merge-tag-text">📎 此订单原以"{{ originalParticipantName }}（临时档案）"名义报名</text>
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

      <!-- 中文注释: 退款规则折叠卡 (仅 paid/refunding + refundEnabled 时) -->
      <view v-if="showRefundRules" class="order-detail-page__card order-detail-page__refund-rules">
        <view class="order-detail-page__refund-rules-head" @tap="refundRulesOpen = !refundRulesOpen">
          <text class="order-detail-page__refund-rules-icon">📜</text>
          <text class="order-detail-page__refund-rules-title">退款规则说明</text>
          <text class="order-detail-page__refund-rules-toggle">{{ refundRulesOpen ? '收起 ▲' : '展开 ▼' }}</text>
        </view>
        <view v-if="refundRulesOpen" class="order-detail-page__refund-rules-body">
          <text class="order-detail-page__refund-rules-item">• 本期仅支持<text class="order-detail-page__refund-rules-strong">全额退款</text>，暂不支持部分退</text>
          <text class="order-detail-page__refund-rules-item">• 全额退款时，使用的<text class="order-detail-page__refund-rules-strong">积分原路返回</text>（冻结→可用）</text>
          <text class="order-detail-page__refund-rules-item">• 全额退款时，使用的<text class="order-detail-page__refund-rules-strong">优惠券原路返回</text>（状态 used→voided，有效期内）</text>
          <text class="order-detail-page__refund-rules-item">• 现金部分按原支付路径退回（微信支付 1~7 个工作日）</text>
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
      text="暂无订单详情"
    />

    <view v-if="showActions" class="order-detail-page__bottom">
      <u-button
        v-if="canCancel"
        class="order-detail-page__bottom-btn order-detail-page__bottom-btn--ghost"
        @click="handleCancel"
      >
        取消订单
      </u-button>
      <u-button
        v-if="canApplyRefund"
        class="order-detail-page__bottom-btn order-detail-page__bottom-btn--ghost"
        @click="openRefundPopup"
      >
        申请退款
      </u-button>
      <u-button
        v-if="canPay"
        class="order-detail-page__bottom-btn order-detail-page__bottom-btn--primary"
        @click="handlePay"
      >
        去支付
      </u-button>
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
          <u-button class="order-detail-page__popup-btn order-detail-page__popup-btn--ghost" @click="closeRefundPopup">
            取消
          </u-button>
          <u-button class="order-detail-page__popup-btn order-detail-page__popup-btn--primary" @click="submitRefund">
            提交申请
          </u-button>
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
      originalParticipantName: '',
      refundPopupVisible: false,
      refundReason: '',
      refundSubmitting: false,
      refundRulesOpen: false
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
      // 中文注释: 积分扣减节点 — 仅当订单使用了积分时显示 (审核通过后触发消耗)
      if (this.hasPointsUsed) {
        const pointsNode = {
          label: `积分扣减（${this.pointsUsedLabel}）`,
          time: this.detail.auditTime
            ? this.formatDateTime(this.detail.auditTime)
            : '审核通过后触发'
        }
        list.push(pointsNode)
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
    },
    // 中文注释: 判断订单是否使用了积分（pointsUsed / pointsDeductAmount 任一 > 0）
    hasPointsUsed() {
      const used = Number(this.detail.pointsUsed) || 0
      const deduct = Number(this.detail.pointsDeductAmount) || 0
      return used > 0 || deduct > 0
    },
    pointsUsedLabel() {
      const used = Number(this.detail.pointsUsed) || 0
      if (used > 0) {
        return `-${used} 积分`
      }
      // TODO(backend): 优先返回 pointsUsed（整数积分数），否则回退到金额
      return `-¥${this.formatAmount(this.detail.pointsDeductAmount)}`
    },
    // 中文注释: 退款规则卡显示条件：refundEnabled && status ∈ [paid, refunding]
    showRefundRules() {
      const status = this.detail.orderStatus
      const eligibleStatus = status === 'paid' || this.isRefunding
      // TODO(backend): refundEnabled 字段后端需返回，缺失时按 true 兜底展示规则
      const refundEnabled = this.detail.refundEnabled !== false
      return eligibleStatus && refundEnabled
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
        // Q-03: 临时档案合并标注
        const snap = (data && (data.participantSnapshot || data.participant_snapshot)) || {}
        this.originalParticipantName = snap.originalParticipantName || ''
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
        await this.fetchDetail()
        // 中文注释: 支付成功后给出明确的"下一步"指引，避免用户不知道该去哪里看进度
        setTimeout(() => {
          uni.showModal({
            title: '支付成功',
            content: '您的订单已支付成功，是否前往"我的报名"查看审核进度？',
            confirmText: '去查看',
            cancelText: '留在当前页',
            success: (res) => {
              if (res.confirm) {
                uni.redirectTo({ url: '/pages-sub/my/enroll' })
              }
            }
          })
        }, 800)
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
@import '@/styles/design-tokens.scss';

.order-detail-page {
  min-height: 100vh;
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.order-detail-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.order-detail-page__back,
.order-detail-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.order-detail-page__back {
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.order-detail-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.order-detail-page__hero,
.order-detail-page__card {
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-xl $jst-page-padding;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
}

.order-detail-page__hero {
  background: $jst-hero-gradient;
}

.order-detail-page__hero-title {
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  line-height: 1.45;
  color: $jst-text-inverse;
}

.order-detail-page__hero-amount {
  display: block;
  margin-top: 18rpx;
  font-size: 56rpx;
  font-weight: $jst-weight-bold;
  line-height: 1;
  color: $jst-text-inverse;
}

.order-detail-page__hero-subtitle {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: rgba($jst-text-inverse, 0.8);
}

.order-detail-page__card-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.order-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  gap: $jst-space-lg;
  padding: 18rpx 0;
  border-top: 2rpx solid $jst-border;
}

.order-detail-page__info-row:first-of-type {
  border-top: none;
  padding-top: 0;
}

.order-detail-page__info-key,
.order-detail-page__info-value {
  font-size: $jst-font-sm;
  line-height: 1.6;
}

.order-detail-page__info-key {
  color: $jst-text-secondary;
}

.order-detail-page__info-value {
  flex: 1;
  text-align: right;
  color: $jst-text-primary;
}

.order-detail-page__info-value--highlight {
  color: $jst-warning;
}

.order-detail-page__info-value--price {
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-warning;
}

.order-detail-page__link-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $jst-space-md;
}

.order-detail-page__link-text {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}

.order-detail-page__link-desc {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xs;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.order-detail-page__timeline-item {
  position: relative;
  display: flex;
  gap: $jst-space-sm;
  padding: 0 0 $jst-space-lg 4rpx;
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
  background: $jst-border;
}

.order-detail-page__timeline-dot {
  position: relative;
  z-index: 1;
  width: 18rpx;
  height: 18rpx;
  margin-top: 6rpx;
  border-radius: $jst-radius-round;
  background: $jst-brand;
}

.order-detail-page__timeline-main {
  flex: 1;
}

.order-detail-page__timeline-label {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.order-detail-page__timeline-time {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.order-detail-page__bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: $jst-space-sm;
  padding: 20rpx $jst-page-padding calc(20rpx + env(safe-area-inset-bottom));
  background: rgba($jst-bg-card, 0.98);
  box-shadow: $jst-shadow-sm;
}

.order-detail-page__bottom-btn {
  min-width: 200rpx;
  height: 88rpx;
  padding: 0 28rpx;
  border-radius: $jst-radius-lg;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  line-height: 88rpx;
}

.order-detail-page__bottom-btn--ghost {
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

.order-detail-page__bottom-btn--primary {
  background: linear-gradient(135deg, $jst-brand 0%, $jst-brand-dark 100%);
  color: $jst-text-inverse;
}

.order-detail-page__popup-mask {
  position: fixed;
  inset: 0;
  z-index: 99;
  display: flex;
  align-items: flex-end;
  background: rgba($jst-text-primary, 0.42);
}

.order-detail-page__popup {
  width: 100%;
  padding: $jst-space-xl $jst-page-padding calc($jst-space-lg + env(safe-area-inset-bottom));
  border-top-left-radius: 32rpx;
  border-top-right-radius: 32rpx;
  background: $jst-bg-card;
}

.order-detail-page__popup-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.order-detail-page__popup-desc {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.order-detail-page__popup-field {
  margin-top: $jst-space-lg;
}

.order-detail-page__popup-label {
  display: block;
  margin-bottom: $jst-space-sm;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.order-detail-page__popup-textarea {
  width: 100%;
  min-height: 220rpx;
  padding: $jst-page-padding;
  box-sizing: border-box;
  border-radius: $jst-radius-xl;
  background: $jst-bg-page;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-primary;
}

.order-detail-page__popup-actions {
  display: flex;
  justify-content: flex-end;
  gap: $jst-space-sm;
  margin-top: $jst-space-lg;
}

.order-detail-page__popup-btn {
  min-width: 180rpx;
  height: 80rpx;
  padding: 0 $jst-page-padding;
  border-radius: $jst-radius-round;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  line-height: 80rpx;
}

.order-detail-page__popup-btn--ghost {
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  color: $jst-brand;
}

.order-detail-page__popup-btn--primary {
  background: $jst-brand;
  color: $jst-text-inverse;
}

.order-detail-page__merge-tag {
  margin: $jst-space-lg $jst-page-padding 0;
  padding: 20rpx $jst-page-padding;
  border-left: 8rpx solid $jst-warning;
  border-radius: $jst-radius-lg;
  background: $jst-warning-light;
}

.order-detail-page__merge-tag-text {
  font-size: $jst-font-sm;
  line-height: 1.6;
  color: $jst-warning;
}

/* 退款规则折叠卡 */
.order-detail-page__refund-rules {
  border-left: 6rpx solid $jst-brand;
}

.order-detail-page__refund-rules-head {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
}

.order-detail-page__refund-rules-icon {
  font-size: $jst-font-md;
}

.order-detail-page__refund-rules-title {
  flex: 1;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.order-detail-page__refund-rules-toggle {
  font-size: $jst-font-xs;
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

.order-detail-page__refund-rules-body {
  margin-top: $jst-space-md;
  padding-top: $jst-space-md;
  border-top: 2rpx solid $jst-border;
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.order-detail-page__refund-rules-item {
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.order-detail-page__refund-rules-strong {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

::v-deep .order-detail-page__bottom-btn.u-button,
::v-deep .order-detail-page__popup-btn.u-button {
  border: none;
}
</style>
