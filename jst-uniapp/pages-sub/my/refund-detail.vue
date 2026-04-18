<template>
  <view class="refund-detail-page">
    <jst-loading :loading="pageLoading" text="退款详情加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="refund-detail-page__nav">
      <view class="refund-detail-page__back" @tap="handleBack">返回</view>
      <text class="refund-detail-page__nav-title">退款详情</text>
      <view class="refund-detail-page__nav-placeholder"></view>
    </view>

    <template v-if="detail.refundId">
      <view class="refund-detail-page__hero">
        <jst-status-badge :text="statusText" :tone="statusTone" />
        <text class="refund-detail-page__hero-title">{{ detail.itemName || detail.contestName || '退款单' }}</text>
        <text class="refund-detail-page__hero-amount">¥{{ formatAmount(detail.applyCash) }}</text>
        <text class="refund-detail-page__hero-subtitle">
          退款单号：{{ detail.refundNo || '--' }}
        </text>
      </view>

      <view class="refund-detail-page__card">
        <text class="refund-detail-page__card-title">退款信息</text>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">退款类型</text>
          <text class="refund-detail-page__info-value">{{ getRefundTypeText(detail.refundType) }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">申请来源</text>
          <text class="refund-detail-page__info-value">{{ getApplySourceText(detail.applySource) }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">退款原因</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--multiline">
            {{ detail.reason || '--' }}
          </text>
        </view>
      </view>

      <view class="refund-detail-page__card">
        <text class="refund-detail-page__card-title">原订单快照</text>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">订单号</text>
          <text class="refund-detail-page__info-value">{{ detail.orderNo || '--' }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">赛事名称</text>
          <text class="refund-detail-page__info-value">{{ detail.contestName || '--' }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">参赛人</text>
          <text class="refund-detail-page__info-value">{{ detail.participantName || '--' }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">支付方式</text>
          <text class="refund-detail-page__info-value">{{ getPayMethodText(detail.payMethod) }}</text>
        </view>
      </view>

      <view class="refund-detail-page__card">
        <text class="refund-detail-page__card-title">退款金额拆分</text>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">申请退现金</text>
          <text class="refund-detail-page__info-value">¥{{ formatAmount(detail.applyCash) }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">申请退积分</text>
          <text class="refund-detail-page__info-value">{{ formatPoints(detail.applyPoints) }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">实际退现金</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--price">
            ¥{{ formatAmount(detail.actualCash) }}
          </text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">实际退积分</text>
          <text class="refund-detail-page__info-value">{{ formatPoints(detail.actualPoints) }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">优惠券返还</text>
          <text class="refund-detail-page__info-value">{{ detail.couponReturned ? '已返还' : '未返还' }}</text>
        </view>
      </view>

      <view class="refund-detail-page__card">
        <text class="refund-detail-page__card-title">审核记录</text>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">审核结果</text>
          <text class="refund-detail-page__info-value">{{ statusText }}</text>
        </view>
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">审核备注</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--multiline">
            {{ detail.auditRemark || '暂无审核备注' }}
          </text>
        </view>
      </view>

      <view class="refund-detail-page__card">
        <text class="refund-detail-page__card-title">状态时间轴</text>
        <view v-for="(item, index) in timeline" :key="`${item.label}-${index}`" class="refund-detail-page__timeline-item">
          <view class="refund-detail-page__timeline-dot"></view>
          <view class="refund-detail-page__timeline-main">
            <text class="refund-detail-page__timeline-label">{{ item.label }}</text>
            <text class="refund-detail-page__timeline-time">{{ item.time }}</text>
          </view>
        </view>
      </view>
    </template>

    <jst-empty
      v-else-if="!pageLoading"
      icon="💳"
      text="暂无退款详情"
    />

    <view v-if="detail.status === 'pending'" class="refund-detail-page__bottom">
      <u-button class="refund-detail-page__bottom-btn" @click="handleCancel">撤销申请</u-button>
    </view>
  </view>
</template>

<script>
import { cancelRefund, getRefundDetail } from '@/api/refund'
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
      refundId: '',
      detail: {}
    }
  },
  computed: {
    statusText() {
      const mapper = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已驳回',
        refunding: '退款中',
        completed: '已完成',
        closed: '已关闭'
      }
      return mapper[this.detail.status] || '处理中'
    },
    statusTone() {
      const mapper = {
        pending: 'pending',
        approved: 'active',
        rejected: 'danger',
        refunding: 'refund',
        completed: 'success',
        closed: 'gray'
      }
      return mapper[this.detail.status] || 'active'
    },
    timeline() {
      const list = []
      if (this.detail.createTime) {
        list.push({
          label: '发起退款',
          time: this.formatDateTime(this.detail.createTime)
        })
      }
      if (this.detail.status === 'approved') {
        list.push({
          label: '审核通过',
          time: this.formatDateTime(this.detail.completeTime || this.detail.createTime)
        })
      }
      if (this.detail.status === 'rejected') {
        list.push({
          label: '审核驳回',
          time: this.formatDateTime(this.detail.completeTime || this.detail.createTime)
        })
      }
      if (this.detail.status === 'refunding') {
        list.push({
          label: '退款执行中',
          time: this.formatDateTime(this.detail.completeTime || this.detail.createTime)
        })
      }
      if (this.detail.completeTime) {
        list.push({
          label: '退款完成',
          time: this.formatDateTime(this.detail.completeTime)
        })
      }
      if (!list.length) {
        list.push({
          label: '处理中',
          time: '--'
        })
      }
      return list
    }
  },
  onLoad(query) {
    this.refundId = query.refundId || ''
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      if (!this.refundId) {
        return
      }

      this.pageLoading = true
      try {
        const data = await getRefundDetail(this.refundId, { silent: true })
        this.detail = data || {}
      } catch (error) {
        this.detail = {}
      } finally {
        this.pageLoading = false
      }
    },

    handleCancel() {
      uni.showModal({
        title: '撤销退款',
        content: '确认撤销当前退款申请吗？',
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          try {
            await cancelRefund(this.refundId)
            uni.showToast({ title: '已撤销', icon: 'success' })
            this.fetchDetail()
          } catch (error) {}
        }
      })
    },

    getRefundTypeText(value) {
      const mapper = {
        refund_only: '仅退款',
        return_refund: '退货退款'
      }
      return mapper[value] || '仅退款'
    },

    getApplySourceText(value) {
      const mapper = {
        user: '用户发起',
        admin: '平台发起',
        system: '系统发起'
      }
      return mapper[value] || '用户发起'
    },

    getPayMethodText(value) {
      const mapper = {
        wechat: '微信支付',
        bank_transfer: '线下转账',
        points: '积分支付'
      }
      return mapper[value] || '待确认'
    },

    formatAmount(value) {
      const amount = Number(value)
      if (Number.isNaN(amount)) {
        return '0.00'
      }
      return amount.toFixed(2)
    },

    formatPoints(value) {
      const points = Number(value)
      if (Number.isNaN(points)) {
        return '0'
      }
      return `${points}`
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.navigateTo({ url: '/pages-sub/my/refund-list' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.refund-detail-page {
  min-height: 100vh;
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.refund-detail-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.refund-detail-page__back,
.refund-detail-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.refund-detail-page__back {
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.refund-detail-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.refund-detail-page__hero,
.refund-detail-page__card {
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-xl $jst-page-padding;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
}

.refund-detail-page__hero {
  background: linear-gradient(135deg, #B84A00 0%, $jst-warning 100%);
}

.refund-detail-page__hero-title {
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 800;
  line-height: 1.45;
  color: $jst-text-inverse;
}

.refund-detail-page__hero-amount {
  display: block;
  margin-top: 18rpx;
  font-size: 56rpx;
  font-weight: 800;
  line-height: 1;
  color: $jst-text-inverse;
}

.refund-detail-page__hero-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba($jst-text-inverse, 0.8);
}

.refund-detail-page__card-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.refund-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  gap: 24rpx;
  padding: 18rpx 0;
  border-top: 2rpx solid $jst-border;
}

.refund-detail-page__info-row:first-of-type {
  border-top: none;
  padding-top: 0;
}

.refund-detail-page__info-key,
.refund-detail-page__info-value {
  font-size: 24rpx;
  line-height: 1.6;
}

.refund-detail-page__info-key {
  color: $jst-text-secondary;
}

.refund-detail-page__info-value {
  flex: 1;
  text-align: right;
  color: $jst-text-primary;
}

.refund-detail-page__info-value--price {
  font-size: 30rpx;
  font-weight: 800;
  color: $jst-warning;
}

.refund-detail-page__info-value--multiline {
  white-space: pre-wrap;
}

.refund-detail-page__timeline-item {
  position: relative;
  display: flex;
  gap: 18rpx;
  padding: 0 0 24rpx 4rpx;
}

.refund-detail-page__timeline-item:last-child {
  padding-bottom: 0;
}

.refund-detail-page__timeline-item:last-child::after {
  display: none;
}

.refund-detail-page__timeline-item::after {
  content: '';
  position: absolute;
  left: 11rpx;
  top: 26rpx;
  bottom: 0;
  width: 2rpx;
  background: $jst-border;
}

.refund-detail-page__timeline-dot {
  position: relative;
  z-index: 1;
  width: 18rpx;
  height: 18rpx;
  margin-top: 6rpx;
  border-radius: 50%;
  background: $jst-brand;
}

.refund-detail-page__timeline-main {
  flex: 1;
}

.refund-detail-page__timeline-label {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: $jst-text-primary;
}

.refund-detail-page__timeline-time {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: $jst-text-secondary;
}

.refund-detail-page__bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
  background: rgba($jst-bg-card, 0.98);
  box-shadow: $jst-shadow-md;
}

.refund-detail-page__bottom-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 28rpx;
  background: $jst-brand;
  color: $jst-text-inverse;
  font-size: 28rpx;
  font-weight: 700;
}

::v-deep .refund-detail-page__bottom-btn.u-button {
  border: none;
}
</style>
