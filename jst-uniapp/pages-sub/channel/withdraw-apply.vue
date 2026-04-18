<!-- 中文注释: 渠道方 · 申请提现
     对应原型: 小程序原型图/channel-settlement.png
     调用接口: GET /jst/wx/channel/rebate/ledger/list?status=withdrawable (拉取可提现 ledger)
               POST /jst/wx/channel/withdraw/apply -->
<template>
  <view class="apply-page">
    <view class="apply-hero" :style="{ paddingTop: navPaddingTop }">
      <text class="apply-hero__label">可提现余额</text>
      <text class="apply-hero__amount">¥{{ formatAmount(totalWithdrawable) }}</text>
      <text class="apply-hero__hint">勾选本次需提现的返点条目，后端将按勾选项金额校验</text>
      <!-- 信任提示：审核 + 打款时效 -->
      <view class="apply-hero__trust">
        <view class="apply-hero__trust-item">
          <text class="apply-hero__trust-icon">⏱</text>
          <text class="apply-hero__trust-text">平台审核 1-2 工作日</text>
        </view>
        <view class="apply-hero__trust-item">
          <text class="apply-hero__trust-icon">🏦</text>
          <text class="apply-hero__trust-text">银行打款 T+1 到账</text>
        </view>
      </view>
    </view>

    <view class="apply-section">
      <view class="apply-section__title">
        <text>选择返点条目</text>
        <text class="apply-section__action" @tap="toggleAll">{{ isAllSelected ? '取消全选' : '全选' }}</text>
      </view>

      <u-empty v-if="!ledgerList.length" mode="data" text="暂无可提现返点"></u-empty>
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
      <u-button class="apply-footer__btn" type="primary" :loading="submitting" :disabled="submitting || !canSubmit" shape="circle" @click="onSubmit">提交申请</u-button>
    </view>
  </view>
</template>

<script>
import { getRebateLedgerList, applyWithdraw, getRebateSummary } from '@/api/channel'

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
      ledgerList: [],
      selectedSet: {}, // { ledgerId: true }
      preselectIds: [], // 上个页面传入的预选
      showInvoice: false,
      submitting: false,
      // POLISH-BATCH2 E: 顶部 "可提现余额" 直接从 /rebate/summary 取, 而非前端 ledger 求和
      summaryWithdrawable: '0.00',
      form: { bankName: '', accountNo: '', accountName: '', invoiceTitle: '', invoiceTaxNo: '' }
    }
  },
  computed: {
    totalWithdrawable() {
      return this.summaryWithdrawable
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
    this.loadSummary()
    this.loadLedger()
  },
  methods: {
    async loadSummary() {
      try {
        const s = await getRebateSummary()
        this.summaryWithdrawable = (s && s.withdrawableAmount) || '0.00'
      } catch (e) {}
    },
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
@import '@/styles/design-tokens.scss';

.apply-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.apply-hero { padding: 72rpx 32rpx 56rpx; background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light); color: $jst-text-inverse; }
.apply-hero__label { display: block; font-size: 24rpx; color: rgba(255, 255, 255, 0.76); }
.apply-hero__amount { display: block; margin-top: 8rpx; font-size: 64rpx; font-weight: 600; color: $jst-gold; }
.apply-hero__hint { display: block; margin-top: 16rpx; font-size: 22rpx; color: rgba(255, 255, 255, 0.72); }
.apply-hero__trust { display: flex; gap: 24rpx; margin-top: 20rpx; padding: 14rpx 20rpx; background: rgba(255, 255, 255, 0.12); border-radius: $jst-radius-sm; }
.apply-hero__trust-item { display: flex; align-items: center; gap: 8rpx; }
.apply-hero__trust-icon { font-size: 24rpx; }
.apply-hero__trust-text { font-size: 22rpx; color: $jst-gold; }

.apply-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.apply-section__title { display: flex; align-items: center; justify-content: space-between; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 16rpx; }
.apply-section__action { font-size: 24rpx; color: $jst-brand; font-weight: 500; }

.apply-item { display: flex; align-items: center; gap: 20rpx; padding: 24rpx 0; border-bottom: 2rpx solid $jst-border; }
.apply-item:last-child { border-bottom: none; }
.apply-item__check { width: 40rpx; height: 40rpx; border-radius: 50%; border: 2rpx solid $jst-border; display: flex; align-items: center; justify-content: center; color: $jst-text-inverse; font-size: 26rpx; flex-shrink: 0; }
.apply-item--checked .apply-item__check { background: $jst-brand; border-color: $jst-brand; }
.apply-item__body { flex: 1; min-width: 0; }
.apply-item__title { display: block; font-size: 26rpx; font-weight: 600; color: $jst-text-primary; }
.apply-item__sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: $jst-text-secondary; }
.apply-item__amount { font-size: 30rpx; font-weight: 600; color: $jst-warning; }

.apply-field { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid $jst-border; }
.apply-field:last-child { border-bottom: none; }
.apply-field__label { width: 160rpx; font-size: 26rpx; color: $jst-text-regular; }
.apply-field__input { flex: 1; font-size: 26rpx; color: $jst-text-primary; }

.apply-footer { position: fixed; left: 0; right: 0; bottom: 0; display: flex; align-items: center; gap: 20rpx; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
.apply-footer__info { flex: 1; }
.apply-footer__label { display: block; font-size: 22rpx; color: $jst-text-secondary; }
.apply-footer__amount { display: block; font-size: 40rpx; font-weight: 600; color: $jst-warning; }
::v-deep .apply-footer__btn.u-button { flex: 1; height: 88rpx; font-size: $jst-font-md; font-weight: $jst-weight-semibold; background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light); border: none; }
</style>
