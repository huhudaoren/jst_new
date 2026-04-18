<!-- 中文注释: 渠道方 · 申请开票
     调用接口: POST /jst/wx/invoice/apply
     辅助接口: GET /jst/wx/channel/withdraw/list?status=paid -->
<template>
  <view class="ia-page">
    <!-- 导航 -->
    <view class="ia-header" :style="{ paddingTop: navPaddingTop }">
      <view class="ia-header__back" @tap="goBack">←</view>
      <text class="ia-header__title">申请开票</text>
    </view>

    <view class="ia-form">
      <!-- 关联结算单（多选） -->
      <view class="ia-field">
        <text class="ia-field__label">关联结算单（可多选）</text>
        <view class="ia-field__picker" @tap="showSettlement = true">
          <text :class="['ia-field__input', !selectedSummary && 'ia-field__input--placeholder']">
            {{ selectedSummary || '选择已打款的结算单' }}
          </text>
          <text class="ia-field__arrow">›</text>
        </view>
        <!-- 实时汇总 -->
        <view v-if="selectedList.length" class="ia-field__sum">
          <text class="ia-field__sum-label">已选 {{ selectedList.length }} 笔</text>
          <text class="ia-field__sum-amount">合计 ¥{{ formatAmount(selectedTotal) }}</text>
        </view>
      </view>

      <!-- 发票抬头 -->
      <view class="ia-field">
        <text class="ia-field__label">发票抬头 <text class="ia-field__required">*</text></text>
        <input
          class="ia-field__input"
          v-model="form.invoiceTitle"
          placeholder="请输入发票抬头"
          :maxlength="100"
        />
      </view>

      <!-- 税号 -->
      <view class="ia-field">
        <text class="ia-field__label">税号 <text class="ia-field__required">*</text></text>
        <input
          class="ia-field__input"
          v-model="form.taxNo"
          placeholder="请输入税号（15-20位字母数字）"
          :maxlength="20"
        />
      </view>

      <!-- 金额（支持自定义，默认取已选结算单合计） -->
      <view class="ia-field">
        <text class="ia-field__label">开票金额</text>
        <input
          class="ia-field__input"
          v-model="form.amount"
          type="digit"
          :placeholder="selectedList.length ? ('最高 ¥' + formatAmount(selectedTotal)) : '请先选择结算单或输入金额'"
        />
        <text v-if="selectedList.length && Number(form.amount) > Number(selectedTotal)" class="ia-field__warn">开票金额不得超过已选结算单合计</text>
      </view>

      <!-- 备注 -->
      <view class="ia-field">
        <text class="ia-field__label">备注</text>
        <textarea
          class="ia-field__textarea"
          v-model="form.remark"
          placeholder="选填"
          :maxlength="200"
        />
      </view>

      <!-- 提交按钮 -->
      <view class="ia-submit">
        <u-button type="primary" text="提交申请" :loading="submitting" :disabled="!canSubmit" @click="handleSubmit" />
      </view>
    </view>

    <!-- 结算单选择弹层（多选） -->
    <u-popup :show="showSettlement" mode="bottom" round="16" @close="showSettlement = false">
      <view class="ia-popup">
        <view class="ia-popup__header">
          <text class="ia-popup__title">选择结算单（可多选）</text>
          <view class="ia-popup__close" @tap="showSettlement = false">✕</view>
        </view>
        <scroll-view class="ia-popup__list" scroll-y>
          <view
            v-for="s in settlements"
            :key="s.settlementId"
            :class="['ia-popup__item', selectedSet[s.settlementId] && 'ia-popup__item--active']"
            @tap="toggleSettlement(s)"
          >
            <view class="ia-popup__check">
              <text v-if="selectedSet[s.settlementId]">✓</text>
            </view>
            <view class="ia-popup__item-body">
              <view class="ia-popup__item-main">
                <text class="ia-popup__item-no">{{ s.settlementNo }}</text>
                <text class="ia-popup__item-amount">¥{{ formatAmount(s.actualPayAmount) }}</text>
              </view>
              <text class="ia-popup__item-time">{{ formatDateTime(s.paidTime || s.applyTime) }}</text>
            </view>
          </view>
          <u-empty v-if="!settlements.length && !settlementLoading" mode="data" text="暂无已打款结算单" />
          <u-loadmore v-if="settlementLoading" status="loading" />
        </scroll-view>
        <view class="ia-popup__footer">
          <text class="ia-popup__footer-text">已选 {{ selectedList.length }} 笔 · 合计 ¥{{ formatAmount(selectedTotal) }}</text>
          <u-button type="primary" text="确定" shape="circle" size="small" @click="confirmSettlement" />
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { applyInvoice } from '@/api/finance'
import { getWithdrawList } from '@/api/channel'

export default {
  data() {
    return {
      form: {
        settlementIds: [], // 多选 ID 列表
        invoiceTitle: '',
        taxNo: '',
        amount: '',
        remark: ''
      },
      submitting: false,
      showSettlement: false,
      settlements: [],
      settlementLoading: false,
      selectedSet: {} // { settlementId: true }
    }
  },
  computed: {
    selectedList() {
      return this.settlements.filter((s) => this.selectedSet[s.settlementId])
    },
    selectedTotal() {
      return this.selectedList.reduce((sum, s) => sum + Number(s.actualPayAmount || 0), 0).toFixed(2)
    },
    selectedSummary() {
      if (!this.selectedList.length) return ''
      if (this.selectedList.length === 1) return this.selectedList[0].settlementNo
      return `${this.selectedList[0].settlementNo} 等 ${this.selectedList.length} 笔`
    },
    // 提交条件: 抬头 + 税号格式 + 至少 1 笔结算单
    canSubmit() {
      if (!this.form.invoiceTitle.trim()) return false
      if (!this.validateTaxNo(this.form.taxNo)) return false
      if (!this.selectedList.length) return false
      if (this.submitting) return false
      if (this.form.amount && Number(this.form.amount) > Number(this.selectedTotal)) return false
      return true
    }
  },
  onLoad() { this.loadSettlements() },
  methods: {
    async loadSettlements() {
      this.settlementLoading = true
      try {
        const res = await getWithdrawList({ status: 'paid', pageNum: 1, pageSize: 50 })
        this.settlements = (res && res.rows) || []
      } finally { this.settlementLoading = false }
    },
    toggleSettlement(s) {
      const next = { ...this.selectedSet }
      if (next[s.settlementId]) delete next[s.settlementId]
      else next[s.settlementId] = true
      this.selectedSet = next
    },
    confirmSettlement() {
      // 确认选择后自动回填合计金额
      this.form.settlementIds = this.selectedList.map((s) => s.settlementId)
      if (this.selectedList.length && !this.form.amount) {
        this.form.amount = this.selectedTotal
      }
      this.showSettlement = false
    },
    // 税号: 15-20位字母数字
    validateTaxNo(v) {
      if (!v) return false
      return /^[A-Za-z0-9]{15,20}$/.test(v.trim())
    },
    async handleSubmit() {
      if (!this.canSubmit) return

      // 校验
      if (!this.form.invoiceTitle.trim()) {
        return uni.showToast({ title: '请输入发票抬头', icon: 'none' })
      }
      if (!this.validateTaxNo(this.form.taxNo)) {
        return uni.showToast({ title: '税号格式不正确（15-20位字母数字）', icon: 'none' })
      }

      this.submitting = true
      try {
        const ids = this.selectedList.map((s) => s.settlementId)
        await applyInvoice({
          // 兼容后端单 ID 字段 + 新增多 ID 数组字段（后端未接入时静默忽略）
          settlementId: ids.length === 1 ? ids[0] : undefined,
          settlementIds: ids.length > 1 ? ids : undefined,
          invoiceTitle: this.form.invoiceTitle.trim(),
          taxNo: this.form.taxNo.trim(),
          amount: this.form.amount ? Number(this.form.amount) : undefined,
          remark: this.form.remark.trim() || undefined
        })
        uni.showToast({ title: '申请已提交', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } finally { this.submitting = false }
    },
    goBack() { uni.navigateBack() },
    formatAmount(v) { if (v == null || v === '') return '0.00'; return Number(v).toFixed(2) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) },
    formatDateTime(v) { if (!v) return '--'; const s = String(v).replace('T', ' '); return s.length >= 16 ? s.slice(0, 16) : s }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ia-page { min-height: 100vh; background: $jst-bg-page; padding-bottom: calc(60rpx + env(safe-area-inset-bottom)); }

.ia-header { display: flex; align-items: center; padding: $jst-space-lg $jst-page-padding $jst-space-md; background: $jst-bg-card; }
.ia-header__back { width: 60rpx; font-size: $jst-font-xl; color: $jst-text-primary; }
.ia-header__title { font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-primary; }

.ia-form { margin: $jst-space-lg $jst-page-padding; background: $jst-bg-card; border-radius: $jst-radius-lg; padding: $jst-space-lg $jst-space-xl; box-shadow: $jst-shadow-sm; }

.ia-field { padding: $jst-space-md 0; border-bottom: 2rpx solid $jst-border; }
.ia-field:last-of-type { border-bottom: none; }
.ia-field__label { display: block; font-size: $jst-font-sm; color: $jst-text-secondary; margin-bottom: $jst-space-xs; }
.ia-field__required { color: $jst-danger; }
.ia-field__input { width: 100%; font-size: $jst-font-base; color: $jst-text-primary; padding: $jst-space-sm 0; }
.ia-field__input--placeholder { color: $jst-text-placeholder; }
.ia-field__picker { display: flex; align-items: center; justify-content: space-between; }
.ia-field__arrow { font-size: $jst-font-lg; color: $jst-text-placeholder; }
.ia-field__textarea { width: 100%; min-height: 120rpx; font-size: $jst-font-base; color: $jst-text-primary; padding: $jst-space-sm 0; }
.ia-field__sum { display: flex; justify-content: space-between; align-items: center; margin-top: $jst-space-sm; padding: $jst-space-sm $jst-space-md; background: $jst-gold-light; border-radius: $jst-radius-sm; }
.ia-field__sum-label { font-size: 22rpx; color: $jst-indigo; }
.ia-field__sum-amount { font-size: 28rpx; font-weight: 700; color: $jst-indigo; }
.ia-field__warn { display: block; margin-top: $jst-space-xs; font-size: 22rpx; color: $jst-danger; }

.ia-submit { margin-top: $jst-space-xl; }

/* 结算单弹层（多选） */
.ia-popup { padding: $jst-space-lg; display: flex; flex-direction: column; max-height: 80vh; }
.ia-popup__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-lg; }
.ia-popup__title { font-size: $jst-font-lg; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.ia-popup__close { font-size: $jst-font-xl; color: $jst-text-secondary; padding: $jst-space-sm; }
.ia-popup__list { max-height: 60vh; }
.ia-popup__item { display: flex; gap: $jst-space-md; padding: $jst-space-lg $jst-space-sm; border-bottom: 2rpx solid $jst-border; }
.ia-popup__item--active { background: $jst-brand-light; border-radius: $jst-radius-sm; }
.ia-popup__check { width: 40rpx; height: 40rpx; border-radius: 50%; border: 2rpx solid $jst-border; display: flex; align-items: center; justify-content: center; color: $jst-text-inverse; font-size: 24rpx; flex-shrink: 0; align-self: center; }
.ia-popup__item--active .ia-popup__check { background: $jst-brand; border-color: $jst-brand; }
.ia-popup__item-body { flex: 1; display: flex; flex-direction: column; gap: $jst-space-xs; min-width: 0; }
.ia-popup__item-main { display: flex; justify-content: space-between; align-items: center; gap: $jst-space-md; }
.ia-popup__item-no { font-size: $jst-font-base; color: $jst-text-primary; }
.ia-popup__item-amount { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-warning; flex-shrink: 0; }
.ia-popup__item-time { font-size: $jst-font-xs; color: $jst-text-secondary; }
.ia-popup__footer { display: flex; justify-content: space-between; align-items: center; gap: $jst-space-md; margin-top: $jst-space-md; padding-top: $jst-space-md; border-top: 2rpx solid $jst-border; }
.ia-popup__footer-text { font-size: 26rpx; color: $jst-text-regular; font-weight: 600; }
</style>
