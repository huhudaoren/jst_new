<!-- 中文注释: 渠道方 · 申请提现
     对应原型: 小程序原型图/channel-settlement.png
     调用接口: GET /jst/wx/channel/rebate/ledger/list?status=withdrawable (拉取可提现 ledger)
               POST /jst/wx/channel/withdraw/apply -->
<template>
  <view class="apply-page">
    <view class="apply-hero">
      <text class="apply-hero__label">可提现余额</text>
      <text class="apply-hero__amount">¥{{ formatAmount(totalWithdrawable) }}</text>
      <text class="apply-hero__hint">勾选本次需提现的返点条目，后端将按勾选项金额校验</text>
    </view>

    <view class="apply-section">
      <view class="apply-section__title">
        <text>选择返点条目</text>
        <text class="apply-section__action" @tap="toggleAll">{{ isAllSelected ? '取消全选' : '全选' }}</text>
      </view>

      <view v-if="!ledgerList.length" class="apply-empty">暂无可提现返点</view>
      <view
        v-for="item in ledgerList"
        :key="item.ledgerId"
        :class="['apply-item', selectedSet[item.ledgerId] && 'apply-item--checked']"
        @tap="toggleItem(item.ledgerId)"
      >
        <view class="apply-item__check">
          <text v-if="selectedSet[item.ledgerId]">✓</text>
        </view>
        <view class="apply-item__body">
          <text class="apply-item__title">{{ item.contestName || '——' }}</text>
          <text class="apply-item__sub">订单 {{ item.orderNo || '--' }}</text>
        </view>
        <text class="apply-item__amount">¥{{ formatAmount(item.rebateAmount) }}</text>
      </view>
    </view>

    <view class="apply-section">
      <view class="apply-section__title"><text>收款账户</text></view>
      <view class="apply-field">
        <text class="apply-field__label">开户行</text>
        <input class="apply-field__input" v-model="form.bankName" placeholder="如 中国工商银行" maxlength="64" />
      </view>
      <view class="apply-field">
        <text class="apply-field__label">银行账号</text>
        <input class="apply-field__input" v-model="form.accountNo" placeholder="请输入收款卡号" maxlength="64" />
      </view>
      <view class="apply-field">
        <text class="apply-field__label">账户户名</text>
        <input class="apply-field__input" v-model="form.accountName" placeholder="请输入账户户名" maxlength="64" />
      </view>
    </view>

    <view class="apply-section">
      <view class="apply-section__title" @tap="toggleInvoice">
        <text>发票信息（选填）</text>
        <text class="apply-section__action">{{ showInvoice ? '收起' : '展开' }}</text>
      </view>
      <block v-if="showInvoice">
        <view class="apply-field">
          <text class="apply-field__label">抬头</text>
          <input class="apply-field__input" v-model="form.invoiceTitle" placeholder="请输入发票抬头" maxlength="128" />
        </view>
        <view class="apply-field">
          <text class="apply-field__label">税号</text>
          <input class="apply-field__input" v-model="form.invoiceTaxNo" placeholder="请输入税号" maxlength="64" />
        </view>
      </block>
    </view>

    <view class="apply-footer">
      <view class="apply-footer__info">
        <text class="apply-footer__label">本次申请</text>
        <text class="apply-footer__amount">¥{{ formatAmount(expectedAmount) }}</text>
      </view>
      <button class="apply-footer__btn" :disabled="submitting || !canSubmit" @tap="onSubmit">
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getRebateLedgerList, applyWithdraw } from '@/api/channel'

export default {
  data() {
    return {
      ledgerList: [],
      selectedSet: {}, // { ledgerId: true }
      preselectIds: [], // 上个页面传入的预选
      showInvoice: false,
      submitting: false,
      form: { bankName: '', accountNo: '', accountName: '', invoiceTitle: '', invoiceTaxNo: '' }
    }
  },
  computed: {
    totalWithdrawable() {
      return this.ledgerList.reduce((s, i) => s + Number(i.rebateAmount || 0), 0).toFixed(2)
    },
    selectedItems() {
      return this.ledgerList.filter((i) => this.selectedSet[i.ledgerId])
    },
    expectedAmount() {
      return this.selectedItems.reduce((s, i) => s + Number(i.rebateAmount || 0), 0).toFixed(2)
    },
    isAllSelected() {
      return this.ledgerList.length > 0 && this.selectedItems.length === this.ledgerList.length
    },
    canSubmit() {
      return this.selectedItems.length > 0 && this.form.bankName && this.form.accountNo && this.form.accountName
    }
  },
  onLoad(query) {
    if (query && query.ledgerIds) {
      try { this.preselectIds = JSON.parse(decodeURIComponent(query.ledgerIds)) } catch (e) { this.preselectIds = [] }
    }
    this.loadLedger()
  },
  methods: {
    async loadLedger() {
      try {
        // 分页给大点, 一次捞完 withdrawable, 不分页以简化勾选
        const res = await getRebateLedgerList({ status: 'withdrawable', pageNum: 1, pageSize: 200 })
        this.ledgerList = (res && res.rows) || []
        // 应用预选
        const map = {}
        const pre = new Set(this.preselectIds.map(String))
        this.ledgerList.forEach((i) => {
          if (pre.has(String(i.ledgerId))) map[i.ledgerId] = true
        })
        // 若无预选则默认全选
        if (!this.preselectIds.length) {
          this.ledgerList.forEach((i) => { map[i.ledgerId] = true })
        }
        this.selectedSet = map
      } catch (e) {}
    },
    toggleItem(id) {
      const next = { ...this.selectedSet }
      if (next[id]) delete next[id]
      else next[id] = true
      this.selectedSet = next
    },
    toggleAll() {
      if (this.isAllSelected) { this.selectedSet = {}; return }
      const next = {}
      this.ledgerList.forEach((i) => { next[i.ledgerId] = true })
      this.selectedSet = next
    },
    toggleInvoice() { this.showInvoice = !this.showInvoice },
    async onSubmit() {
      if (!this.canSubmit) {
        uni.showToast({ title: '请完善收款账户并至少勾选 1 条', icon: 'none' })
        return
      }
      const body = {
        ledgerIds: this.selectedItems.map((i) => i.ledgerId),
        expectedAmount: Number(this.expectedAmount),
        payeeAccount: {
          bankName: this.form.bankName.trim(),
          accountNo: this.form.accountNo.trim(),
          accountName: this.form.accountName.trim()
        }
      }
      if (this.form.invoiceTitle || this.form.invoiceTaxNo) {
        body.invoiceInfo = { title: this.form.invoiceTitle, taxNo: this.form.invoiceTaxNo }
      }
      this.submitting = true
      try {
        const res = await applyWithdraw(body)
        uni.showToast({ title: '申请提交成功', icon: 'success' })
        setTimeout(() => {
          uni.redirectTo({ url: '/pages-sub/channel/withdraw-detail?id=' + (res && res.settlementId) })
        }, 600)
      } catch (e) {
        // request.js 已弹 toast; 这里根据业务码做可选映射
        const code = e && e.code
        if (code === 40012) uni.showToast({ title: '金额不一致，请刷新后重试', icon: 'none' })
        else if (code === 40011) uni.showToast({ title: '返点条目状态变更，请刷新', icon: 'none' })
        else if (code === 40013) uni.showToast({ title: '状态非法，无法申请', icon: 'none' })
        else if (code === 40014) uni.showToast({ title: '操作繁忙，请稍后重试', icon: 'none' })
      } finally {
        this.submitting = false
      }
    },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return String(v) }
  }
}
</script>

<style scoped lang="scss">
.apply-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.apply-hero { padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, #1A237E, #1058A0); color: #fff; }
.apply-hero__label { display: block; font-size: 24rpx; color: var(--jst-color-white-76); }
.apply-hero__amount { display: block; margin-top: 8rpx; font-size: 64rpx; font-weight: 800; color: #FFD54F; }
.apply-hero__hint { display: block; margin-top: 16rpx; font-size: 22rpx; color: var(--jst-color-white-72); }

.apply-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.apply-section__title { display: flex; align-items: center; justify-content: space-between; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.apply-section__action { font-size: 24rpx; color: var(--jst-color-brand); font-weight: 500; }

.apply-empty { padding: 40rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

.apply-item { display: flex; align-items: center; gap: 20rpx; padding: 24rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.apply-item:last-child { border-bottom: none; }
.apply-item__check { width: 40rpx; height: 40rpx; border-radius: 50%; border: 2rpx solid var(--jst-color-border); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 26rpx; flex-shrink: 0; }
.apply-item--checked .apply-item__check { background: var(--jst-color-brand); border-color: var(--jst-color-brand); }
.apply-item__body { flex: 1; min-width: 0; }
.apply-item__title { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); }
.apply-item__sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.apply-item__amount { font-size: 30rpx; font-weight: 800; color: #F5A623; }

.apply-field { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.apply-field:last-child { border-bottom: none; }
.apply-field__label { width: 160rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); }
.apply-field__input { flex: 1; font-size: 26rpx; color: var(--jst-color-text); }

.apply-footer { position: fixed; left: 0; right: 0; bottom: 0; display: flex; align-items: center; gap: 20rpx; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.apply-footer__info { flex: 1; }
.apply-footer__label { display: block; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.apply-footer__amount { display: block; font-size: 40rpx; font-weight: 800; color: #F5A623; }
.apply-footer__btn { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #1A237E, #3949AB); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.apply-footer__btn[disabled] { opacity: 0.5; }
</style>
