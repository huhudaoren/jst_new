<!-- 中文注释: 渠道方 · 提现单详情（金融信任感增强版）
     调用接口: GET /jst/wx/channel/withdraw/{id}
              POST /jst/wx/channel/withdraw/{id}/cancel
     信任感补齐: 审核轨迹时间轴 + 驳回原因高亮 + 打款凭证预览 + 负向台账说明 -->
<template>
  <view class="wd-page">
    <view class="wd-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="wd-hero__top">
        <text class="wd-hero__no">{{ detail.settlementNo || '--' }}</text>
        <u-tag
          :text="statusLabel(detail.status)"
          :type="statusTagType(detail.status)"
          shape="circle"
        ></u-tag>
      </view>
      <view class="wd-hero__amounts">
        <view class="wd-hero__row">
          <text class="wd-hero__label">申请金额</text>
          <text class="wd-hero__value">¥{{ formatAmount(detail.applyAmount) }}</text>
        </view>
        <view v-if="hasNegativeOffset" class="wd-hero__row">
          <text class="wd-hero__label">历史回退抵扣</text>
          <text class="wd-hero__value">-¥{{ formatAmount(detail.negativeOffsetAmount) }}</text>
        </view>
        <view class="wd-hero__row wd-hero__row--strong">
          <text class="wd-hero__label">实际打款</text>
          <text class="wd-hero__value wd-hero__value--gold">¥{{ formatAmount(detail.actualPayAmount) }}</text>
        </view>
      </view>
    </view>

    <!-- 负向台账说明卡（如存在退款回退） -->
    <view v-if="hasNegativeOffset" class="wd-notice">
      <text class="wd-notice__icon">!</text>
      <view class="wd-notice__body">
        <text class="wd-notice__title">因近期退款产生 ¥{{ formatAmount(detail.negativeOffsetAmount) }} 回退</text>
        <text class="wd-notice__desc">已从本次提现自动抵扣，实际打款 = 申请金额 - 回退抵扣</text>
      </view>
    </view>

    <!-- 审核轨迹时间轴（inline 实现，复用 jst-timeline 风格） -->
    <view class="wd-section">
      <view class="wd-section__title"><text>审核轨迹</text></view>
      <view class="wd-timeline">
        <view
          v-for="(node, idx) in timelineNodes"
          :key="idx"
          :class="['wd-timeline__item', `wd-timeline__item--${node.state}`]"
        >
          <view class="wd-timeline__rail">
            <view :class="['wd-timeline__dot', `wd-timeline__dot--${node.state}`]">
              <text v-if="node.state === 'done'" class="wd-timeline__check">✓</text>
              <text v-else-if="node.state === 'reject'" class="wd-timeline__check">✕</text>
            </view>
            <view v-if="idx < timelineNodes.length - 1" :class="['wd-timeline__line', `wd-timeline__line--${node.state}`]"></view>
          </view>
          <view class="wd-timeline__content">
            <text :class="['wd-timeline__label', `wd-timeline__label--${node.state}`]">{{ node.label }}</text>
            <text v-if="node.time" class="wd-timeline__time">{{ formatDateTime(node.time) }}</text>
            <text v-if="node.desc" :class="['wd-timeline__desc', node.state === 'reject' && 'wd-timeline__desc--danger']">{{ node.desc }}</text>
          </view>
        </view>
      </view>
      <!-- 驳回态: 重新编辑按钮 -->
      <view v-if="detail.status === 'rejected'" class="wd-reject-cta">
        <u-button type="primary" shape="circle" size="normal" @click="goReapply">重新编辑申请</u-button>
      </view>
    </view>

    <!-- 打款凭证（已打款） -->
    <view v-if="detail.status === 'paid'" class="wd-section">
      <view class="wd-section__title"><text>打款凭证</text></view>
      <view v-if="detail.payVoucherUrl" class="wd-voucher" @tap="previewVoucher">
        <image class="wd-voucher__img" :src="detail.payVoucherUrl" mode="aspectFill"></image>
        <view class="wd-voucher__mask">
          <text class="wd-voucher__hint">点击查看大图</text>
        </view>
      </view>
      <view v-else class="wd-voucher-empty">
        <text class="wd-voucher-empty__text">凭证暂未上传，如有疑问请联系平台客服</text>
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
      <view class="wd-kv"><text class="wd-kv__k">发票状态</text><text class="wd-kv__v">{{ invoiceStatusLabel(detail.invoiceStatus) }}</text></view>
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
      <u-button class="wd-footer__btn" type="error" :loading="cancelling" :disabled="cancelling" shape="circle" @click="onCancel">取消申请</u-button>
    </view>
  </view>
</template>

<script>
import { getWithdrawDetail, cancelWithdraw } from '@/api/channel'

const STATUS_LABEL = { pending: '待审核', in_review: '审核中', approved: '待打款', paid: '已打款', rejected: '已驳回', cancelled: '已取消' }
const INVOICE_STATUS_LABEL = { none: '未开票', pending_apply: '待申请开票', reviewing: '审核中', issuing: '开票中', issued: '已开票', voided: '已作废', red_offset: '红冲' }

export default {
  data() { return { detail: {}, settlementId: null, cancelling: false, skeletonShow: true /* [visual-effect] */ } },
  computed: {
    canCancel() { return this.detail.status === 'pending' },
    hasNegativeOffset() {
      const v = this.detail.negativeOffsetAmount
      return v != null && Number(v) > 0
    },
    // 审核轨迹时间轴节点 - 根据当前 status 动态计算每节点 state: done | current | reject | pending
    timelineNodes() {
      const d = this.detail || {}
      const s = d.status
      // 节点 1: 提交申请 - 永远 done
      const n1 = { label: '提交申请', state: 'done', time: d.applyTime, desc: '已提交至平台审核' }
      // 节点 2: 平台审核
      let n2State = 'pending'
      let n2Label = '平台审核'
      let n2Desc = ''
      let n2Time = null
      if (s === 'pending') { n2State = 'current'; n2Desc = '等待平台审核' }
      else if (s === 'in_review') { n2State = 'current'; n2Desc = '审核进行中' }
      else if (s === 'rejected') {
        n2State = 'reject'
        n2Label = '平台驳回'
        n2Desc = d.auditRemark || '审核未通过'
        n2Time = d.updateTime
      } else if (s === 'cancelled') {
        n2State = 'reject'
        n2Label = '已撤销'
        n2Desc = '申请已取消'
        n2Time = d.updateTime
      } else if (['approved', 'paid'].includes(s)) {
        n2State = 'done'
        n2Label = '审核通过'
        n2Desc = d.auditRemark || '平台审核通过'
        n2Time = d.updateTime
      }
      // 节点 3: 待打款 / 已打款
      let n3State = 'pending'
      let n3Label = '等待打款'
      let n3Desc = ''
      let n3Time = null
      if (s === 'approved') { n3State = 'current'; n3Desc = '审核通过，等待财务打款' }
      else if (s === 'paid') {
        n3State = 'done'
        n3Label = '打款成功'
        n3Desc = '款项已汇至收款账户'
        n3Time = d.payTime
      }
      // rejected / cancelled 下把 n3 隐藏也可，这里保留灰态作为对比
      const nodes = [n1, n2]
      if (!['rejected', 'cancelled'].includes(s)) nodes.push(n3)
      return nodes
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
    goReapply() {
      // 驳回后重新编辑：跳 withdraw-apply，带 ledgerIds 以便预选（后端返回 ledgerItems 含 ledgerId）
      const ids = (this.detail.ledgerItems || []).map((i) => i.ledgerId)
      const q = ids.length ? ('?ledgerIds=' + encodeURIComponent(JSON.stringify(ids))) : ''
      uni.navigateTo({ url: '/pages-sub/channel/withdraw-apply' + q })
    },
    previewVoucher() {
      if (!this.detail.payVoucherUrl) return
      uni.previewImage({ urls: [this.detail.payVoucherUrl], current: this.detail.payVoucherUrl })
    },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return String(v) },
    // 统一金融时间格式：YYYY-MM-DD HH:mm
    formatDateTime(v) {
      if (!v) return '--'
      const s = String(v).replace('T', ' ')
      return s.length >= 16 ? s.slice(0, 16) : s
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    statusTagType(s) {
      if (s === 'paid') return 'success'
      if (s === 'rejected') return 'error'
      if (['pending', 'in_review', 'approved'].includes(s)) return 'warning'
      return 'info'
    },
    invoiceStatusLabel(s) { return INVOICE_STATUS_LABEL[s] || s || '未开票' }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.wd-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.wd-hero { padding: 64rpx 32rpx 48rpx; background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light); color: $jst-text-inverse; }
.wd-hero__top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12rpx; }
.wd-hero__no { font-size: 24rpx; color: rgba(255, 255, 255, 0.76); }
.wd-hero__amounts { margin-top: 24rpx; padding: 24rpx; border-radius: $jst-radius-md; background: rgba(255, 255, 255, 0.18); }
.wd-hero__row { display: flex; justify-content: space-between; align-items: center; padding: 10rpx 0; }
.wd-hero__row--strong { border-top: 2rpx dashed rgba(255, 255, 255, 0.24); margin-top: 10rpx; padding-top: 20rpx; }
.wd-hero__label { font-size: 24rpx; color: rgba(255, 255, 255, 0.76); }
.wd-hero__value { font-size: 28rpx; font-weight: 600; color: $jst-text-inverse; }
.wd-hero__value--gold { font-size: 40rpx; color: $jst-gold; }

.wd-notice { display: flex; gap: 16rpx; margin: 24rpx 32rpx 0; padding: 20rpx 24rpx; background: $jst-warning-light; border-radius: $jst-radius-md; border-left: 6rpx solid $jst-warning; }
.wd-notice__icon { width: 40rpx; height: 40rpx; line-height: 40rpx; text-align: center; border-radius: 50%; background: $jst-warning; color: $jst-text-inverse; font-size: 26rpx; font-weight: 700; flex-shrink: 0; }
.wd-notice__body { flex: 1; display: flex; flex-direction: column; }
.wd-notice__title { font-size: 26rpx; font-weight: 600; color: $jst-warning; }
.wd-notice__desc { margin-top: 4rpx; font-size: 22rpx; color: $jst-text-regular; line-height: 1.5; }

.wd-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.wd-section__title { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 16rpx; }
.wd-kv { display: flex; justify-content: space-between; padding: 12rpx 0; font-size: 26rpx; }
.wd-kv__k { color: $jst-text-secondary; }
.wd-kv__v { color: $jst-text-primary; }

/* 审核轨迹时间轴（inline 风格） */
.wd-timeline { padding: 8rpx 0; }
.wd-timeline__item { display: flex; gap: 20rpx; min-height: 108rpx; }
.wd-timeline__rail { display: flex; flex-direction: column; align-items: center; width: 40rpx; flex-shrink: 0; }
.wd-timeline__dot { width: 28rpx; height: 28rpx; border-radius: 50%; background: $jst-text-placeholder; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 4rpx; }
.wd-timeline__dot--done { background: $jst-success; }
.wd-timeline__dot--current { background: $jst-warning; box-shadow: 0 0 0 6rpx rgba(255, 149, 0, 0.18); }
.wd-timeline__dot--reject { background: $jst-danger; }
.wd-timeline__check { font-size: 18rpx; color: $jst-text-inverse; line-height: 1; font-weight: 700; }
.wd-timeline__line { flex: 1; width: 3rpx; min-height: 56rpx; background: $jst-border; margin: 4rpx 0; }
.wd-timeline__line--done { background: $jst-success; }
.wd-timeline__line--reject { background: $jst-danger; }
.wd-timeline__content { flex: 1; padding-bottom: 20rpx; }
.wd-timeline__label { display: block; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; line-height: 1.4; }
.wd-timeline__label--current { color: $jst-warning; }
.wd-timeline__label--reject { color: $jst-danger; }
.wd-timeline__label--pending { color: $jst-text-placeholder; font-weight: 400; }
.wd-timeline__time { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.wd-timeline__desc { display: block; margin-top: 6rpx; font-size: 22rpx; color: $jst-text-secondary; line-height: 1.5; }
.wd-timeline__desc--danger { color: $jst-danger; padding: 8rpx 12rpx; background: $jst-danger-light; border-radius: $jst-radius-sm; }

.wd-reject-cta { margin-top: 24rpx; padding-top: 24rpx; border-top: 2rpx dashed $jst-border; }

/* 打款凭证 */
.wd-voucher { position: relative; width: 100%; height: 360rpx; border-radius: $jst-radius-md; overflow: hidden; background: $jst-bg-page; }
.wd-voucher__img { width: 100%; height: 100%; }
.wd-voucher__mask { position: absolute; bottom: 0; left: 0; right: 0; padding: 16rpx; background: rgba(0, 0, 0, 0.5); }
.wd-voucher__hint { font-size: 22rpx; color: $jst-text-inverse; text-align: center; display: block; }
.wd-voucher-empty { padding: 32rpx; text-align: center; background: $jst-bg-page; border-radius: $jst-radius-md; }
.wd-voucher-empty__text { font-size: 24rpx; color: $jst-text-secondary; }

.wd-ledger { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 2rpx solid $jst-border; }
.wd-ledger:last-child { border-bottom: none; }
.wd-ledger__main { flex: 1; min-width: 0; }
.wd-ledger__name { display: block; font-size: 26rpx; color: $jst-text-primary; font-weight: 600; }
.wd-ledger__sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.wd-ledger__amount { font-size: 28rpx; font-weight: 600; color: $jst-warning; }
.wd-ledger__amount--neg { color: $jst-danger; }

.wd-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
::v-deep .wd-footer__btn.u-button { height: 88rpx; font-size: $jst-font-md; font-weight: $jst-weight-semibold; }
</style>
