<!-- 中文注释: 权益核销申请
     对应原型: 小程序原型图/writeoff-apply.html
     调用接口: GET /jst/wx/rights/{id} + POST /jst/wx/rights/{id}/apply-writeoff -->
<template>
  <view class="wa-page">
    <view class="wa-hero" :style="{ paddingTop: navPaddingTop }">
      <text class="wa-hero__label">申请核销</text>
      <text class="wa-hero__name">{{ detail.rightsName || '--' }}</text>
      <text class="wa-hero__remain">
        剩余
        <text v-if="detail.quotaMode === 'amount'">¥{{ detail.remainQuota != null ? detail.remainQuota : '--' }}</text>
        <text v-else>{{ detail.remainQuota != null ? detail.remainQuota : '--' }} 次</text>
      </text>
    </view>

    <view class="wa-section">
      <view class="wa-field">
        <text class="wa-field__label">{{ detail.quotaMode === 'amount' ? '本次核销金额' : '本次核销次数' }}</text>
        <input class="wa-field__input" type="digit" v-model="form.writeoffAmount" :placeholder="detail.quotaMode === 'amount' ? '请输入金额' : '请输入次数'" />
      </view>

      <view class="wa-field wa-field--textarea">
        <text class="wa-field__label">备注</text>
        <textarea class="wa-field__textarea" v-model="form.remark" placeholder="选填：说明使用场景" maxlength="200" />
      </view>
    </view>

    <view class="wa-tip">
      <text class="wa-tip__title">核销说明</text>
      <text class="wa-tip__text">{{ detail.remark || '提交后由平台审核并完成核销，请确保场景属实。' }}</text>
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
      form: { writeoffAmount: '', remark: '' },
      submitting: false
    }
  },
  computed: {
    canSubmit() {
      const n = Number(this.form.writeoffAmount || 0)
      return n > 0 && n <= Number(this.detail.remainQuota || 0)
    }
  },
  onLoad(query) { this.userRightsId = query && query.id; if (this.userRightsId) this.load() },
  methods: {
    async load() { try { this.detail = (await getRightsDetail(this.userRightsId)) || {} } catch (e) {} },
    async onSubmit() {
      if (!this.canSubmit) return
      // 后端 RightsWriteoffApplyDTO 仅接受 writeoffAmount + remark, 次数模式也复用同字段由后端按 quotaMode 解释
      const body = { writeoffAmount: Number(this.form.writeoffAmount), remark: this.form.remark }
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
@import '@/styles/design-tokens.scss';
.wa-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.wa-hero { padding: 72rpx $jst-space-xl $jst-space-xxl; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; }
.wa-hero__label { display: block; font-size: $jst-font-sm; color: rgba(255,255,255,0.76); }
.wa-hero__name { display: block; margin-top: 10rpx; font-size: $jst-font-xl; font-weight: $jst-weight-semibold; }
.wa-hero__remain { display: block; margin-top: $jst-space-md; font-size: $jst-font-sm; color: $jst-gold; font-weight: $jst-weight-semibold; }

.wa-section { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-md $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; }
.wa-field { display: flex; align-items: center; padding: $jst-space-lg 0; border-bottom: 2rpx solid $jst-border; }
.wa-field:last-child { border-bottom: none; }
.wa-field--textarea { align-items: flex-start; }
.wa-field__label { width: 200rpx; font-size: $jst-font-sm; color: $jst-text-regular; }
.wa-field__input { flex: 1; font-size: $jst-font-base; color: $jst-text-primary; }
.wa-field__textarea { flex: 1; height: 160rpx; font-size: $jst-font-sm; color: $jst-text-primary; }
.wa-stepper { display: flex; align-items: center; gap: $jst-space-lg; }
.wa-stepper__btn { width: 64rpx; height: 64rpx; line-height: 64rpx; text-align: center; border-radius: $jst-radius-lg; background: $jst-bg-page; font-size: $jst-font-xl; }
.wa-stepper__val { font-size: $jst-font-md; font-weight: $jst-weight-semibold; min-width: 64rpx; text-align: center; }

.wa-tip { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-success-light; border-radius: $jst-radius-xl; }
.wa-tip__title { display: block; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; color: $jst-success; margin-bottom: $jst-space-xs; }
.wa-tip__text { font-size: $jst-font-xs; line-height: 1.7; color: $jst-text-regular; }

.wa-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: 0 -2rpx 8rpx rgba(20, 30, 60, 0.04); }
.wa-footer__btn { height: 96rpx; line-height: 96rpx; border-radius: $jst-radius-xl; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; font-size: $jst-font-md; font-weight: $jst-weight-semibold; border: none; transition: opacity $jst-duration-fast $jst-easing; &:active { opacity: 0.85; } }
.wa-footer__btn[disabled] { opacity: 0.5; }
</style>
