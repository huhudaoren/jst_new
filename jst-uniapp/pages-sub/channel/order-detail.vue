<!-- 中文注释: 渠道方 · 订单详情 V4.0 (E1-CH-4)
     对应原型: order-detail.html
     5区块: 基础信息 + 费用明细 + 归属渠道 + 时间轴 + 操作区
     数据来源: GET /jst/wx/channel/orders/{id}
     ⚠️ 若后端 VO 缺字段, 前端用 '--' 占位, 不编造假数据 -->
<template>
  <view class="od-page">
    <view class="od-header">
      <view class="od-header__back" @tap="goBack">←</view>
      <text class="od-header__title">订单详情</text>
    </view>

    <!-- Q-03: 临时档案合并标注 -->
    <view v-if="originalParticipantName" class="od-merge-tag">
      <text class="od-merge-tag__text">📎 此订单原以"{{ originalParticipantName }}（临时档案）"名义报名</text>
    </view>

    <!-- A. 基础信息 -->
    <view class="od-section">
      <view class="od-info-row">
        <text class="od-info-label">订单号</text>
        <view class="od-info-val od-info-val--copy" @tap="copyText(detail.orderNo)">
          <text>{{ detail.orderNo || '--' }}</text>
          <text class="od-copy-icon">📋</text>
        </view>
      </view>
      <view class="od-info-row">
        <text class="od-info-label">创建时间</text>
        <text class="od-info-val">{{ formatTime(detail.createTime) }}</text>
      </view>
      <view class="od-info-row" v-if="detail.payTime">
        <text class="od-info-label">支付时间</text>
        <text class="od-info-val">{{ formatTime(detail.payTime) }}</text>
      </view>
      <view class="od-info-row">
        <text class="od-info-label">学生</text>
        <text class="od-info-val">{{ detail.studentName || '--' }}{{ detail.mobileMasked ? ' · ' + detail.mobileMasked : '' }}</text>
      </view>
      <view class="od-info-row" @tap="goContest">
        <text class="od-info-label">赛事</text>
        <text class="od-info-val od-info-val--link">{{ detail.contestName || '--' }} ›</text>
      </view>
      <view class="od-info-row">
        <text class="od-info-label">订单状态</text>
        <view class="od-status-badge" :class="'od-status-badge--' + getStatusType(detail.orderStatus)">
          {{ detail.statusText || getStatusText(detail.orderStatus) }}
        </view>
      </view>
      <view v-if="detail.refundStatus" class="od-info-row">
        <text class="od-info-label">退款状态</text>
        <text class="od-info-val" style="color: #E74C3C;">{{ detail.refundStatusText || detail.refundStatus }}</text>
      </view>
    </view>

    <!-- B. 费用明细 (V4.0 核心) -->
    <view class="od-section">
      <view class="od-section__title">费用明细</view>
      <view class="od-fee">
        <view class="od-fee__row">
          <text class="od-fee__label">报名费标价</text>
          <text class="od-fee__val">¥{{ detail.priceOriginal || detail.totalAmount || '--' }}</text>
        </view>
        <view v-if="detail.couponDiscount" class="od-fee__row od-fee__row--discount">
          <text class="od-fee__label">优惠券抵扣</text>
          <text class="od-fee__val">-¥{{ detail.couponDiscount }}{{ detail.couponDesc ? ' (' + detail.couponDesc + ')' : '' }}</text>
        </view>
        <view v-if="detail.pointsDiscount" class="od-fee__row od-fee__row--discount">
          <text class="od-fee__label">积分抵扣</text>
          <text class="od-fee__val">-¥{{ detail.pointsDiscount }}{{ detail.pointsUsed ? ' (' + detail.pointsUsed + '积分)' : '' }}</text>
        </view>
        <view class="od-fee__divider"></view>
        <view class="od-fee__row od-fee__row--total">
          <text class="od-fee__label">用户净实付</text>
          <text class="od-fee__val od-fee__val--accent">¥{{ detail.userNetPay || detail.payAmount || '--' }}</text>
        </view>

        <template v-if="detail.platformFee != null || detail.rebateAmount != null">
          <view class="od-fee__row">
            <text class="od-fee__label">平台服务费</text>
            <text class="od-fee__val">¥{{ detail.platformFee || '--' }}</text>
          </view>
          <view class="od-fee__divider"></view>
          <view class="od-fee__row">
            <text class="od-fee__label">渠道返点基数</text>
            <text class="od-fee__val">¥{{ detail.rebateBase || '--' }}</text>
          </view>
          <view class="od-fee__row">
            <text class="od-fee__label">返点比例</text>
            <text class="od-fee__val">× {{ detail.rebateRate ? (detail.rebateRate * 100).toFixed(0) + '%' : '--' }}</text>
          </view>
          <view class="od-fee__row od-fee__row--total">
            <text class="od-fee__label">返点金额</text>
            <text class="od-fee__val" style="color: #3F51B5; font-weight: 800;">¥{{ detail.rebateAmount || '--' }}</text>
          </view>
        </template>
      </view>
    </view>

    <!-- C. 归属渠道方 -->
    <view v-if="detail.channelOwner" class="od-section">
      <view class="od-section__title">归属渠道方</view>
      <view class="od-channel-owner">
        <text class="od-channel-owner__icon">👨‍🏫</text>
        <text class="od-channel-owner__name">{{ detail.channelOwner.name || '--' }}（{{ detail.channelOwner.channelType || '渠道方' }}）</text>
      </view>
      <text class="od-channel-owner__hint">返点锁定依据：订单创建时的有效绑定关系</text>
    </view>

    <!-- D. 时间轴 -->
    <view v-if="timeline.length" class="od-section">
      <view class="od-section__title">订单进度</view>
      <view class="od-timeline">
        <view v-for="(t, idx) in timeline" :key="idx" class="od-tl-item">
          <view class="od-tl-dot" :class="{ 'od-tl-dot--active': idx === 0 }"></view>
          <view v-if="idx < timeline.length - 1" class="od-tl-line"></view>
          <view class="od-tl-content">
            <text class="od-tl-step">{{ t.step || t.description || '--' }}</text>
            <text class="od-tl-time">{{ formatTime(t.time) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- E. 操作区 -->
    <view class="od-actions">
      <view class="od-action-btn" @tap="copyText(detail.orderNo)">复制订单号</view>
      <view class="od-action-btn od-action-btn--primary" @tap="goContest">查看赛事详情</view>
    </view>
  </view>
</template>

<script>
import { getChannelOrderDetail } from '@/api/channel'

const STATUS_MAP = {
  pending_pay: '待支付', paid: '已支付', voucher_review: '对公转账审核中',
  refunded: '已退款', cancelled: '已关闭', completed: '已完成'
}

export default {
  data() {
    return { detail: {}, timeline: [], originalParticipantName: '' }
  },
  onLoad(opts) {
    this.orderId = opts.id || ''
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      if (!this.orderId) return
      try {
        const res = await getChannelOrderDetail(this.orderId)
        this.detail = res || {}
        this.timeline = res.timeline || []
        // Q-03: 临时档案合并标注
        const snap = res.participantSnapshot || res.participant_snapshot || {}
        this.originalParticipantName = snap.originalParticipantName || ''
      } catch (e) {}
    },

    getStatusText(s) { return STATUS_MAP[s] || s || '--' },
    getStatusType(s) {
      if (s === 'pending_pay') return 'warning'
      if (s === 'paid' || s === 'completed') return 'success'
      if (s === 'voucher_review') return 'info'
      return 'gray'
    },

    formatTime(v) { return v ? String(v).replace('T', ' ').slice(0, 16) : '--' },

    copyText(text) {
      if (!text) return
      uni.setClipboardData({ data: text, success: () => { uni.showToast({ title: '已复制', icon: 'success' }) } })
    },

    goContest() {
      if (this.detail.contestId) {
        uni.navigateTo({ url: '/pages-sub/contest/detail?id=' + this.detail.contestId })
      }
    },

    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.od-page { min-height: 100vh; padding-bottom: calc(48rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.od-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); }
.od-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.od-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

.od-section { margin: 24rpx 32rpx 0; background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; }
.od-section__title { padding: 28rpx 28rpx 16rpx; font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); display: flex; align-items: center; gap: 12rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.od-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: #3F51B5; border-radius: 4rpx; }

/* 信息行 */
.od-info-row { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 28rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.od-info-row:last-child { border-bottom: none; }
.od-info-label { font-size: 26rpx; color: var(--jst-color-text-tertiary); flex-shrink: 0; margin-right: 24rpx; }
.od-info-val { font-size: 26rpx; color: var(--jst-color-text); text-align: right; flex: 1; }
.od-info-val--copy { display: flex; align-items: center; justify-content: flex-end; gap: 8rpx; }
.od-info-val--link { color: #3F51B5; font-weight: 500; }
.od-copy-icon { font-size: 24rpx; }

.od-status-badge { padding: 6rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; font-weight: 600; }
.od-status-badge--warning { background: var(--jst-color-warning-soft); color: #B26A00; }
.od-status-badge--success { background: var(--jst-color-success-soft); color: #0F7B3F; }
.od-status-badge--info { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.od-status-badge--gray { background: #ECECEC; color: #6B6B6B; }

/* 费用明细 */
.od-fee { padding: 20rpx 28rpx; }
.od-fee__row { display: flex; justify-content: space-between; padding: 12rpx 0; }
.od-fee__row--discount .od-fee__val { color: var(--jst-color-success); }
.od-fee__row--total { padding: 16rpx 0; }
.od-fee__label { font-size: 26rpx; color: var(--jst-color-text-secondary); }
.od-fee__val { font-size: 26rpx; color: var(--jst-color-text); font-weight: 500; font-feature-settings: "tnum"; }
.od-fee__val--accent { font-size: 32rpx; font-weight: 800; color: #FF5722; }
.od-fee__divider { height: 2rpx; background: var(--jst-color-border); margin: 8rpx 0; }

/* 归属渠道 */
.od-channel-owner { display: flex; align-items: center; gap: 16rpx; padding: 24rpx 28rpx 8rpx; }
.od-channel-owner__icon { font-size: 36rpx; }
.od-channel-owner__name { font-size: 28rpx; font-weight: 600; color: var(--jst-color-text); }
.od-channel-owner__hint { display: block; padding: 0 28rpx 24rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

/* 时间轴 */
.od-timeline { padding: 24rpx 28rpx; }
.od-tl-item { display: flex; position: relative; padding-bottom: 24rpx; }
.od-tl-item:last-child { padding-bottom: 0; }
.od-tl-dot { width: 16rpx; height: 16rpx; border-radius: 50%; background: var(--jst-color-border); margin-top: 8rpx; flex-shrink: 0; z-index: 1; }
.od-tl-dot--active { background: #3F51B5; box-shadow: 0 0 0 6rpx rgba(63,81,181,0.2); }
.od-tl-line { position: absolute; left: 7rpx; top: 28rpx; bottom: 0; width: 2rpx; background: var(--jst-color-border); }
.od-tl-content { margin-left: 20rpx; flex: 1; }
.od-tl-step { display: block; font-size: 26rpx; color: var(--jst-color-text); font-weight: 500; }
.od-tl-time { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

/* 操作区 */
.od-actions { display: flex; gap: 20rpx; padding: 32rpx; }
.od-action-btn { flex: 1; height: 88rpx; border-radius: var(--jst-radius-md); display: flex; align-items: center; justify-content: center; font-size: 28rpx; font-weight: 600; background: var(--jst-color-page-bg); color: var(--jst-color-text-secondary); }
.od-action-btn--primary { background: #3F51B5; color: #fff; }

/* Q-03 临时档案标注 */
.od-merge-tag { margin: 24rpx 32rpx 0; padding: 20rpx 28rpx; background: var(--jst-color-warning-soft); border-radius: var(--jst-radius-md); border-left: 8rpx solid var(--jst-color-warning); }
.od-merge-tag__text { font-size: 26rpx; color: #B26A00; line-height: 1.6; }
</style>
