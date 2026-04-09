<!-- 中文注释: 权益核销申请
     对应原型: 小程序原型图/writeoff-apply.html
     调用接口: GET /jst/wx/rights/{id} + POST /jst/wx/rights/{id}/apply-writeoff -->
<template>
  <view class="wa-page">
    <view class="wa-hero">
      <text class="wa-hero__label">申请核销</text>
      <text class="wa-hero__name">{{ detail.rightsName || '--' }}</text>
      <text class="wa-hero__remain">
        剩余
        <text v-if="detail.remainingAmount != null">¥{{ detail.remainingAmount }}</text>
        <text v-else-if="detail.remainingCount != null">{{ detail.remainingCount }} 次</text>
        <text v-else>--</text>
      </text>
    </view>

    <view class="wa-section">
      <view v-if="useAmountMode" class="wa-field">
        <text class="wa-field__label">本次核销金额</text>
        <input class="wa-field__input" type="digit" v-model="form.writeoffAmount" placeholder="请输入金额" />
      </view>
      <view v-else class="wa-field">
        <text class="wa-field__label">本次核销次数</text>
        <view class="wa-stepper">
          <view class="wa-stepper__btn" @tap="dec">-</view>
          <text class="wa-stepper__val">{{ form.writeoffCount }}</text>
          <view class="wa-stepper__btn" @tap="inc">+</view>
        </view>
      </view>

      <view class="wa-field wa-field--textarea">
        <text class="wa-field__label">备注</text>
        <textarea class="wa-field__textarea" v-model="form.remark" placeholder="选填：说明使用场景" maxlength="200" />
      </view>
    </view>

    <view class="wa-tip">
      <text class="wa-tip__title">核销说明</text>
      <text class="wa-tip__text">{{ detail.writeoffRule || detail.description || '提交后由平台审核并完成核销，请确保场景属实。' }}</text>
    </view>

    <view class="wa-footer">
      <button class="wa-footer__btn" :disabled="submitting || !canSubmit" @tap="onSubmit">
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<script>
import { getRightsDetail, applyRightsWriteoff } from '@/api/rights'

export default {
  data() {
    return {
      userRightsId: null,
      detail: {},
      form: { writeoffAmount: '', writeoffCount: 1, remark: '' },
      submitting: false
    }
  },
  computed: {
    useAmountMode() { return this.detail && this.detail.remainingAmount != null },
    canSubmit() {
      if (this.useAmountMode) {
        const n = Number(this.form.writeoffAmount || 0)
        return n > 0 && n <= Number(this.detail.remainingAmount || 0)
      }
      return this.form.writeoffCount > 0 && this.form.writeoffCount <= Number(this.detail.remainingCount || 0)
    }
  },
  onLoad(query) { this.userRightsId = query && query.id; if (this.userRightsId) this.load() },
  methods: {
    async load() { try { this.detail = (await getRightsDetail(this.userRightsId)) || {} } catch (e) {} },
    inc() { if (this.form.writeoffCount < Number(this.detail.remainingCount || 0)) this.form.writeoffCount += 1 },
    dec() { if (this.form.writeoffCount > 1) this.form.writeoffCount -= 1 },
    async onSubmit() {
      if (!this.canSubmit) return
      const body = { remark: this.form.remark }
      if (this.useAmountMode) body.writeoffAmount = Number(this.form.writeoffAmount)
      else body.writeoffCount = this.form.writeoffCount
      this.submitting = true
      try {
        await applyRightsWriteoff(this.userRightsId, body)
        uni.showToast({ title: '申请已提交', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 600)
      } finally { this.submitting = false }
    }
  }
}
</script>

<style scoped lang="scss">
.wa-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.wa-hero { padding: 72rpx 32rpx 48rpx; background: linear-gradient(135deg, #1B5E20, #2E7D32); color: #fff; }
.wa-hero__label { display: block; font-size: 24rpx; color: var(--jst-color-white-76); }
.wa-hero__name { display: block; margin-top: 10rpx; font-size: 36rpx; font-weight: 800; }
.wa-hero__remain { display: block; margin-top: 16rpx; font-size: 26rpx; color: #FFD54F; font-weight: 700; }

.wa-section { margin: 24rpx 32rpx 0; padding: 16rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.wa-field { display: flex; align-items: center; padding: 28rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.wa-field:last-child { border-bottom: none; }
.wa-field--textarea { align-items: flex-start; }
.wa-field__label { width: 200rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); }
.wa-field__input { flex: 1; font-size: 28rpx; color: var(--jst-color-text); }
.wa-field__textarea { flex: 1; height: 160rpx; font-size: 26rpx; color: var(--jst-color-text); }
.wa-stepper { display: flex; align-items: center; gap: 24rpx; }
.wa-stepper__btn { width: 64rpx; height: 64rpx; line-height: 64rpx; text-align: center; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); font-size: 36rpx; }
.wa-stepper__val { font-size: 30rpx; font-weight: 700; min-width: 64rpx; text-align: center; }

.wa-tip { margin: 24rpx 32rpx 0; padding: 24rpx 32rpx; background: var(--jst-color-success-soft); border-radius: var(--jst-radius-md); }
.wa-tip__title { display: block; font-size: 24rpx; font-weight: 700; color: #1B5E20; margin-bottom: 8rpx; }
.wa-tip__text { font-size: 22rpx; line-height: 1.7; color: var(--jst-color-text-secondary); }

.wa-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.wa-footer__btn { height: 96rpx; line-height: 96rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, #1B5E20, #2E7D32); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.wa-footer__btn[disabled] { opacity: 0.5; }
</style>
