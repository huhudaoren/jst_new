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

      <!-- 中文注释: 复刻 order-detail 的费用明细结构 — 5 行清晰分解 -->
      <view class="refund-detail-page__card">
        <text class="refund-detail-page__card-title">退款费用明细</text>
        <!-- 行1: 订单实付 -->
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">① 订单实付</text>
          <text class="refund-detail-page__info-value">
            ¥{{ formatAmount(detail.orderNetPay || detail.originalOrderAmount) }}
          </text>
        </view>
        <!-- 行2: 积分原路回 (v-if) -->
        <!-- TODO(backend): 需要 pointsRefund 字段（整数） -->
        <view v-if="hasPointsRefund" class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">② 积分原路回</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--return">
            +{{ pointsRefundValue }} 积分（冻结→可用）
          </text>
        </view>
        <!-- 行3: 券原路回 (v-if) -->
        <!-- TODO(backend): 需要 couponRefund: { couponName, faceAmount, couponStatus } -->
        <view v-if="hasCouponRefund" class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">③ 券原路回</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--return">
            {{ couponRefundLabel }}（状态 voided）
          </text>
        </view>
        <!-- 行4: 现金退款 -->
        <view class="refund-detail-page__info-row">
          <text class="refund-detail-page__info-key">④ 现金退款</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--price">
            ¥{{ formatAmount(cashRefundValue) }}
          </text>
        </view>
        <view class="refund-detail-page__info-row refund-detail-page__info-row--sub">
          <text class="refund-detail-page__info-sub">（原支付路径 · {{ getPayMethodText(detail.payMethod) }}）</text>
        </view>
        <!-- 行5: 合计退款 -->
        <view class="refund-detail-page__info-row refund-detail-page__info-row--total">
          <text class="refund-detail-page__info-key refund-detail-page__info-key--total">⑤ 合计退款</text>
          <text class="refund-detail-page__info-value refund-detail-page__info-value--total">
            ¥{{ formatAmount(cashRefundValue) }}
            <text v-if="hasPointsRefund || hasCouponRefund" class="refund-detail-page__info-extra">
              + {{ extraLabel }}
            </text>
          </text>
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
        <view
          v-for="(item, index) in timeline"
          :key="index"
          class="refund-detail-page__timeline-item"
          :class="{
            'refund-detail-page__timeline-item--done': item.done,
            'refund-detail-page__timeline-item--danger': item.tone === 'danger',
            'refund-detail-page__timeline-item--success': item.tone === 'success'
          }"
        >
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
      // 中文注释: 5 态 chip 与 refund-list 对齐
      const mapper = {
        pending: '申请中',
        reviewing: '审核中',
        approved: '已通过',
        rejected: '已驳回',
        refunding: '退款中',
        completed: '已到账',
        closed: '已关闭'
      }
      return mapper[this.detail.status] || '处理中'
    },
    statusTone() {
      const mapper = {
        pending: 'pending',
        reviewing: 'pending',
        approved: 'active',
        rejected: 'danger',
        refunding: 'refund',
        completed: 'success',
        closed: 'gray'
      }
      return mapper[this.detail.status] || 'active'
    },
    // 中文注释: 积分/券/现金 字段归一化（缺字段走 TODO 降级）
    hasPointsRefund() {
      return this.pointsRefundValue > 0
    },
    pointsRefundValue() {
      // TODO(backend): 优先 pointsRefund（整数），其次 actualPoints，最后 applyPoints
      const refund = Number(this.detail.pointsRefund)
      if (!Number.isNaN(refund) && refund > 0) {
        return refund
      }
      const actual = Number(this.detail.actualPoints)
      if (!Number.isNaN(actual) && actual > 0) {
        return actual
      }
      return Number(this.detail.applyPoints) || 0
    },
    hasCouponRefund() {
      // TODO(backend): 推荐 couponRefund: { couponName, faceAmount }，兼容 couponReturned 标志
      const cr = this.detail.couponRefund
      if (cr && (cr.couponName || cr.faceAmount)) {
        return true
      }
      return !!this.detail.couponReturned
    },
    couponRefundLabel() {
      const cr = this.detail.couponRefund
      if (cr && cr.couponName) {
        const face = Number(cr.faceAmount)
        if (!Number.isNaN(face) && face > 0) {
          return `${cr.couponName} · 面额 ¥${face.toFixed(2)}`
        }
        return cr.couponName
      }
      return '原券已返还'
    },
    cashRefundValue() {
      const actual = Number(this.detail.actualCash)
      if (!Number.isNaN(actual) && actual > 0) {
        return actual
      }
      return this.detail.applyCash
    },
    extraLabel() {
      const parts = []
      if (this.hasCouponRefund) {
        parts.push('券 1 张')
      }
      if (this.hasPointsRefund) {
        parts.push(`${this.pointsRefundValue} 积分`)
      }
      return parts.join(' + ')
    },
    // 中文注释: 预计到账时间 — 已到账显示实际时间；进行中显示 T+1~7 工作日文案
    arrivalEtaText() {
      // TODO(backend): 推荐 expectedArrivalTime 字段；缺字段时根据状态显示兜底文案
      if (this.detail.expectedArrivalTime) {
        return this.formatDateTime(this.detail.expectedArrivalTime)
      }
      if (this.detail.status === 'completed' && this.detail.completeTime) {
        return this.formatDateTime(this.detail.completeTime)
      }
      if (this.detail.status === 'refunding') {
        return '预计 1~7 个工作日到账'
      }
      return '--'
    },
    // 中文注释: 4 阶段时间轴 申请 → 审核 → 驳回/通过 → 到账（含预计到账）
    timeline() {
      const list = []
      const status = this.detail.status
      if (this.detail.createTime) {
        list.push({
          label: '① 申请退款',
          time: this.formatDateTime(this.detail.createTime),
          done: true
        })
      }
      // 审核阶段
      const reviewing = ['reviewing', 'approved', 'rejected', 'refunding', 'completed'].includes(status)
      list.push({
        label: '② 平台审核',
        time: reviewing
          ? (this.detail.auditTime ? this.formatDateTime(this.detail.auditTime) : '审核中...')
          : '等待受理',
        done: reviewing
      })
      // 审核结果
      if (status === 'rejected') {
        list.push({
          label: '③ 审核驳回',
          time: this.formatDateTime(this.detail.auditTime || this.detail.completeTime),
          done: true,
          tone: 'danger'
        })
      } else if (['approved', 'refunding', 'completed'].includes(status)) {
        list.push({
          label: '③ 审核通过',
          time: this.formatDateTime(this.detail.auditTime || this.detail.completeTime || this.detail.createTime),
          done: true
        })
      } else {
        list.push({
          label: '③ 审核结果',
          time: '待审核',
          done: false
        })
      }
      // 到账
      if (status === 'completed') {
        list.push({
          label: '④ 退款到账',
          time: this.arrivalEtaText,
          done: true,
          tone: 'success'
        })
      } else if (['approved', 'refunding'].includes(status)) {
        list.push({
          label: '④ 退款到账',
          time: this.arrivalEtaText,
          done: false
        })
      } else if (status !== 'rejected' && status !== 'closed') {
        list.push({
          label: '④ 退款到账',
          time: '--',
          done: false
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

/* 费用明细 5 行样式 */
.refund-detail-page__info-value--return {
  color: $jst-success;
  font-weight: $jst-weight-semibold;
}

.refund-detail-page__info-row--sub {
  padding: 2rpx 0 14rpx;
  border-top: none;
}

.refund-detail-page__info-sub {
  flex: 1;
  text-align: right;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

.refund-detail-page__info-row--total {
  border-top: 2rpx dashed $jst-border;
  padding-top: 18rpx;
  margin-top: 6rpx;
}

.refund-detail-page__info-key--total {
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.refund-detail-page__info-value--total {
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-warning;
}

.refund-detail-page__info-extra {
  display: inline-block;
  margin-left: $jst-space-xs;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-regular;
  color: $jst-text-secondary;
}

/* 时间轴状态变体 */
.refund-detail-page__timeline-item--done .refund-detail-page__timeline-dot {
  background: $jst-brand;
}

.refund-detail-page__timeline-item:not(.refund-detail-page__timeline-item--done) .refund-detail-page__timeline-dot {
  background: $jst-text-placeholder;
}

.refund-detail-page__timeline-item--success .refund-detail-page__timeline-dot {
  background: $jst-success;
}

.refund-detail-page__timeline-item--danger .refund-detail-page__timeline-dot {
  background: $jst-danger;
}

.refund-detail-page__timeline-item--success .refund-detail-page__timeline-label {
  color: $jst-success;
}

.refund-detail-page__timeline-item--danger .refund-detail-page__timeline-label {
  color: $jst-danger;
}

.refund-detail-page__timeline-item:not(.refund-detail-page__timeline-item--done) .refund-detail-page__timeline-label {
  color: $jst-text-secondary;
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
