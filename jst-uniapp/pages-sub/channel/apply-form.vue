<!-- 中文注释: 渠道认证 · 材料填写表单
     调用接口: POST /jst/wx/channel/auth/apply 或 /resubmit/{id}
     后端入参: { channelType, applyName, materialsJson(JSON字符串) } -->
<template>
  <view class="af-page">
    <view class="af-hero">
      <text class="af-hero__title">{{ typeLabel }}认证</text>
    </view>

    <view class="af-section">
      <text class="af-section__title">基本信息</text>
      <view class="af-field"><text class="af-field__label">真实姓名 *</text><input class="af-field__input" v-model="form.applyName" placeholder="请输入真实姓名" maxlength="128" /></view>
      <view class="af-field"><text class="af-field__label">手机号 *</text><input class="af-field__input" v-model="form.mobile" type="number" placeholder="请输入手机号" maxlength="11" /></view>
      <view class="af-field"><text class="af-field__label">身份证号</text><input class="af-field__input" v-model="form.idCard" placeholder="选填" maxlength="18" /></view>
    </view>

    <view v-if="channelType === 'teacher'" class="af-section">
      <text class="af-section__title">教师材料</text>
      <view class="af-field"><text class="af-field__label">任教学校 *</text><input class="af-field__input" v-model="materials.schoolName" placeholder="请输入学校名称" maxlength="128" /></view>
      <view class="af-field"><text class="af-field__label">教师资格证</text><input class="af-field__input" v-model="materials.teacherCertUrl" placeholder="OSS URL (模拟上传)" /></view>
    </view>

    <view v-if="channelType === 'organization'" class="af-section">
      <text class="af-section__title">机构材料</text>
      <view class="af-field"><text class="af-field__label">机构名称 *</text><input class="af-field__input" v-model="materials.orgName" placeholder="请输入机构名称" maxlength="128" /></view>
      <view class="af-field"><text class="af-field__label">营业执照号 *</text><input class="af-field__input" v-model="materials.businessLicenseNo" placeholder="请输入营业执照号" maxlength="64" /></view>
      <view class="af-field"><text class="af-field__label">营业执照照片</text><input class="af-field__input" v-model="materials.businessLicenseUrl" placeholder="OSS URL (模拟上传)" /></view>
      <view class="af-field"><text class="af-field__label">法人姓名</text><input class="af-field__input" v-model="materials.legalPersonName" placeholder="请输入法人姓名" maxlength="64" /></view>
    </view>

    <view v-if="channelType === 'individual'" class="af-section">
      <text class="af-section__title">个人材料</text>
      <view class="af-field"><text class="af-field__label">身份证正面</text><input class="af-field__input" v-model="materials.idCardFrontUrl" placeholder="OSS URL (模拟上传)" /></view>
      <view class="af-field"><text class="af-field__label">身份证反面</text><input class="af-field__input" v-model="materials.idCardBackUrl" placeholder="OSS URL (模拟上传)" /></view>
      <view class="af-field af-field--textarea"><text class="af-field__label">个人简介</text><textarea class="af-field__textarea" v-model="materials.personalDesc" placeholder="选填 (500字内)" maxlength="500" /></view>
    </view>

    <view class="af-agree" @tap="agreed = !agreed">
      <view :class="['af-agree__check', agreed && 'af-agree__check--on']">{{ agreed ? '✓' : '' }}</view>
      <text class="af-agree__text">我已阅读并同意《渠道方服务协议》</text>
    </view>

    <view class="af-footer">
      <button class="af-footer__btn" :disabled="submitting || !canSubmit" @tap="onSubmit">
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </view>
  </view>
</template>

<script>
import { submitChannelApply, resubmitChannelApply } from '@/api/channel'

const TYPE_LABEL = { teacher: '老师', organization: '机构', individual: '个人' }

export default {
  data() {
    return {
      channelType: 'teacher',
      resubmitId: null,
      form: { applyName: '', mobile: '', idCard: '' },
      materials: {},
      agreed: false,
      submitting: false
    }
  },
  computed: {
    typeLabel() { return TYPE_LABEL[this.channelType] || '渠道方' },
    canSubmit() { return this.agreed && this.form.applyName && this.form.mobile && this.form.mobile.length === 11 }
  },
  onLoad(query) {
    this.channelType = (query && query.channelType) || 'teacher'
    this.resubmitId = (query && query.resubmitId) || null
  },
  methods: {
    async onSubmit() {
      if (!this.canSubmit) return
      const materialsObj = { ...this.materials, mobile: this.form.mobile, idCard: this.form.idCard }
      const body = {
        channelType: this.channelType,
        applyName: this.form.applyName.trim(),
        materialsJson: JSON.stringify(materialsObj)
      }
      this.submitting = true
      try {
        if (this.resubmitId) await resubmitChannelApply(this.resubmitId, body)
        else await submitChannelApply(body)
        uni.showToast({ title: '申请已提交', icon: 'success' })
        setTimeout(() => uni.redirectTo({ url: '/pages-sub/channel/apply-status' }), 600)
      } finally { this.submitting = false }
    }
  }
}
</script>

<style scoped lang="scss">
.af-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: #F7F8FA; }
.af-hero { padding: 72rpx 32rpx 48rpx; background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; }
.af-hero__title { display: block; font-size: 40rpx; font-weight: 800; }
.af-section { margin: 24rpx 32rpx 0; padding: 16rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }
.af-section__title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); padding: 20rpx 0 8rpx; }
.af-field { display: flex; align-items: center; padding: 24rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.af-field:last-child { border-bottom: none; }
.af-field--textarea { align-items: flex-start; }
.af-field__label { width: 200rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); flex-shrink: 0; }
.af-field__input { flex: 1; font-size: 28rpx; color: var(--jst-color-text); }
.af-field__textarea { flex: 1; height: 200rpx; font-size: 26rpx; color: var(--jst-color-text); }
.af-agree { display: flex; align-items: center; gap: 16rpx; margin: 32rpx 32rpx 0; }
.af-agree__check { width: 40rpx; height: 40rpx; border-radius: 50%; border: 2rpx solid var(--jst-color-border); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 26rpx; }
.af-agree__check--on { background: var(--jst-color-brand); border-color: var(--jst-color-brand); }
.af-agree__text { font-size: 24rpx; color: var(--jst-color-text-secondary); }
.af-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.af-footer__btn { height: 96rpx; line-height: 96rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; font-size: 30rpx; font-weight: 800; border: none; }
.af-footer__btn[disabled] { opacity: 0.5; }
</style>
