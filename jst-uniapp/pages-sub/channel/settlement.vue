<!-- 中文注释: 渠道方 · 提现结算统一页 (E1-CH-5)
     合并 withdraw-list + withdraw-apply 为单页多视图
     对应原型: 小程序原型图/channel-settlement.html
     调用接口: GET /jst/wx/channel/withdraw/list
               GET /jst/wx/channel/rebate/ledger/list?status=withdrawable
               POST /jst/wx/channel/withdraw/apply -->
<template>
  <view class="st-page">
    <!-- Tab 栏 -->
    <scroll-view class="st-tabs" scroll-x>
      <view v-for="tab in tabs" :key="tab.value" :class="['st-tabs__item', activeTab === tab.value && 'st-tabs__item--active']" @tap="onTabChange(tab.value)">{{ tab.label }}</view>
    </scroll-view>

    <!-- 顶部新建提现入口 -->
    <view class="st-create" @tap="showApplyForm = !showApplyForm">
      <text class="st-create__icon">{{ showApplyForm ? '▼' : '▶' }}</text>
      <text class="st-create__text">{{ showApplyForm ? '收起申请表单' : '新建提现申请' }}</text>
    </view>

    <!-- 申请表单（展开） -->
    <view v-if="showApplyForm" class="st-form">
      <view class="st-form__section">
        <text class="st-form__title">选择待提现明细（{{ selectedIds.length }} 项 · ¥{{ expectedAmount }}）</text>
        <view v-for="item in ledgerList" :key="item.ledgerId" :class="['st-form__ledger', selectedSet[item.ledgerId] && 'st-form__ledger--on']" @tap="toggleLedger(item.ledgerId)">
          <view class="st-form__check"><text v-if="selectedSet[item.ledgerId]">✓</text></view>
          <view class="st-form__ledger-body">
            <text class="st-form__ledger-name">{{ item.contestName || '--' }}</text>
            <text class="st-form__ledger-sub">{{ item.orderNo || '--' }}</text>
          </view>
          <text class="st-form__ledger-amount">¥{{ item.rebateAmount || 0 }}</text>
        </view>
        <view v-if="!ledgerList.length" class="st-form__empty">暂无可提现明细</view>
      </view>

      <view class="st-form__section">
        <text class="st-form__title">收款账户</text>
        <view class="st-form__field"><text class="st-form__label">开户行</text><input class="st-form__input" v-model="payee.bankName" placeholder="如 中国工商银行" /></view>
        <view class="st-form__field"><text class="st-form__label">账号</text><input class="st-form__input" v-model="payee.accountNo" placeholder="银行卡号" /></view>
        <view class="st-form__field"><text class="st-form__label">户名</text><input class="st-form__input" v-model="payee.accountName" placeholder="账户户名" /></view>
      </view>

      <view class="st-form__section">
        <text class="st-form__title">发票信息（选填）</text>
        <view class="st-form__field"><text class="st-form__label">抬头</text><input class="st-form__input" v-model="invoice.title" placeholder="发票抬头" /></view>
        <view class="st-form__field"><text class="st-form__label">税号</text><input class="st-form__input" v-model="invoice.taxNo" placeholder="税号" /></view>
        <view class="st-form__tip">合同/发票功能灰标（F-2 批次）</view>
      </view>

      <button class="st-form__submit" :disabled="applySubmitting || !canApply" @tap="onApply">{{ applySubmitting ? '提交中...' : '提交提现申请' }}</button>
    </view>

    <!-- 结算单列表 -->
    <view class="st-list">
      <view v-for="item in withdrawList" :key="item.settlementId" class="st-card" @tap="goDetail(item.settlementId)">
        <view class="st-card__head">
          <text class="st-card__no">{{ item.settlementNo || '--' }}</text>
          <text :class="['st-card__status', 'st-card__status--' + item.status]">{{ statusLabel(item.status) }}</text>
        </view>
        <view class="st-card__body">
          <text class="st-card__amount">¥{{ item.applyAmount || '0.00' }}</text>
          <text class="st-card__time">{{ formatTime(item.applyTime) }}</text>
        </view>
      </view>
      <view v-if="!withdrawList.length && !wLoading" class="st-empty">暂无结算单</view>
      <view v-if="wLoading" class="st-empty">加载中...</view>
      <view v-if="!wHasMore && withdrawList.length" class="st-empty st-empty--end">没有更多了</view>
    </view>
  </view>
</template>

<script>
import { getWithdrawList, getWithdrawDetail, applyWithdraw, getRebateLedgerList } from '@/api/channel'

const TABS = [
  { value: '', label: '全部' },
  { value: 'pending', label: '待提交' },
  { value: 'in_review', label: '审核中' },
  { value: 'cancelled', label: '已撤回' },
  { value: 'rejected', label: '已驳回' },
  { value: 'approved', label: '待打款' },
  { value: 'paid', label: '已打款' }
]
const STATUS_LABEL = { pending: '待审核', in_review: '审核中', paid: '已打款', rejected: '已驳回', cancelled: '已撤回', approved: '待打款' }

export default {
  data() {
    return {
      tabs: TABS, activeTab: '', showApplyForm: false,
      withdrawList: [], wPageNum: 1, wPageSize: 10, wTotal: 0, wLoading: false, wHasMore: true,
      ledgerList: [], selectedSet: {},
      payee: { bankName: '', accountNo: '', accountName: '' },
      invoice: { title: '', taxNo: '' },
      applySubmitting: false
    }
  },
  computed: {
    selectedIds() { return Object.keys(this.selectedSet).filter((k) => this.selectedSet[k]).map(Number) },
    expectedAmount() { return this.ledgerList.filter((i) => this.selectedSet[i.ledgerId]).reduce((s, i) => s + Number(i.rebateAmount || 0), 0).toFixed(2) },
    canApply() { return this.selectedIds.length > 0 && this.payee.bankName && this.payee.accountNo && this.payee.accountName }
  },
  onShow() { this.loadWithdraw(true); if (this.showApplyForm) this.loadLedger() },
  onPullDownRefresh() { this.loadWithdraw(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.wHasMore && !this.wLoading) this.loadWithdraw(false) },
  watch: {
    showApplyForm(v) { if (v) this.loadLedger() }
  },
  methods: {
    async loadWithdraw(reset) {
      if (reset) { this.wPageNum = 1; this.withdrawList = []; this.wHasMore = true }
      if (!this.wHasMore) return
      this.wLoading = true
      try {
        const res = await getWithdrawList({ status: this.activeTab || undefined, pageNum: this.wPageNum, pageSize: this.wPageSize })
        const rows = (res && res.rows) || []; this.wTotal = (res && res.total) || 0
        this.withdrawList = reset ? rows : this.withdrawList.concat(rows)
        this.wHasMore = this.withdrawList.length < this.wTotal; if (this.wHasMore) this.wPageNum += 1
      } finally { this.wLoading = false }
    },
    async loadLedger() {
      try {
        const res = await getRebateLedgerList({ status: 'withdrawable', pageNum: 1, pageSize: 200 })
        this.ledgerList = (res && res.rows) || []
        const set = {}; this.ledgerList.forEach((i) => { set[i.ledgerId] = true }); this.selectedSet = set
      } catch (e) {}
    },
    toggleLedger(id) { const n = { ...this.selectedSet }; if (n[id]) delete n[id]; else n[id] = true; this.selectedSet = n },
    onTabChange(v) { if (this.activeTab === v) return; this.activeTab = v; this.loadWithdraw(true) },
    async onApply() {
      if (!this.canApply) return
      const body = {
        ledgerIds: this.selectedIds,
        expectedAmount: Number(this.expectedAmount),
        payeeAccount: { ...this.payee }
      }
      if (this.invoice.title || this.invoice.taxNo) body.invoiceInfo = { ...this.invoice }
      this.applySubmitting = true
      try {
        const res = await applyWithdraw(body)
        uni.showToast({ title: '申请提交成功', icon: 'success' })
        this.showApplyForm = false; this.loadWithdraw(true)
      } catch (e) {
        if (e && e.code === 40012) uni.showToast({ title: '金额不一致，请刷新', icon: 'none' })
      } finally { this.applySubmitting = false }
    },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/channel/withdraw-detail?id=' + id }) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.st-page { min-height: 100vh; background: #F7F8FA; padding-bottom: 48rpx; }
.st-tabs { white-space: nowrap; background: var(--jst-color-card-bg); border-bottom: 2rpx solid var(--jst-color-border); }
.st-tabs__item { display: inline-block; padding: 0 28rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: var(--jst-color-text-tertiary); position: relative; }
.st-tabs__item--active { color: var(--jst-color-brand); font-weight: 700; }
.st-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 20rpx; right: 20rpx; height: 4rpx; background: var(--jst-color-brand); border-radius: 2rpx; }

.st-create { display: flex; align-items: center; gap: 12rpx; padding: 24rpx 32rpx; margin: 20rpx 32rpx 0; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }
.st-create__icon { font-size: 24rpx; color: var(--jst-color-brand); }
.st-create__text { font-size: 28rpx; font-weight: 700; color: var(--jst-color-brand); }

.st-form { margin: 16rpx 32rpx 0; }
.st-form__section { padding: 24rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); margin-bottom: 16rpx; }
.st-form__title { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.st-form__ledger { display: flex; align-items: center; gap: 16rpx; padding: 20rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.st-form__ledger:last-child { border-bottom: none; }
.st-form__check { width: 36rpx; height: 36rpx; border-radius: 50%; border: 2rpx solid var(--jst-color-border); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24rpx; }
.st-form__ledger--on .st-form__check { background: var(--jst-color-brand); border-color: var(--jst-color-brand); }
.st-form__ledger-body { flex: 1; min-width: 0; }
.st-form__ledger-name { display: block; font-size: 26rpx; font-weight: 600; color: var(--jst-color-text); }
.st-form__ledger-sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.st-form__ledger-amount { font-size: 28rpx; font-weight: 800; color: #F5A623; }
.st-form__empty { padding: 32rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.st-form__field { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.st-form__field:last-of-type { border-bottom: none; }
.st-form__label { width: 140rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); }
.st-form__input { flex: 1; font-size: 26rpx; color: var(--jst-color-text); }
.st-form__tip { margin-top: 12rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.st-form__submit { margin-top: 16rpx; height: 88rpx; line-height: 88rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #1A237E, #3949AB); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.st-form__submit[disabled] { opacity: 0.5; }

.st-list { padding: 8rpx 0 32rpx; }
.st-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }
.st-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.st-card__no { font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.st-card__status { padding: 4rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.st-card__status--pending, .st-card__status--in_review { background: var(--jst-color-warning-soft); color: var(--jst-color-warning); }
.st-card__status--paid { background: var(--jst-color-success-soft); color: var(--jst-color-success); }
.st-card__status--rejected, .st-card__status--cancelled { background: var(--jst-color-gray-soft); color: var(--jst-color-text-tertiary); }
.st-card__body { display: flex; justify-content: space-between; align-items: flex-end; }
.st-card__amount { font-size: 36rpx; font-weight: 800; color: #F5A623; }
.st-card__time { font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.st-empty { padding: 60rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
.st-empty--end { padding: 40rpx; }
</style>
