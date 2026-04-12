<!-- 中文注释: 渠道认证 · 材料填写表单
     调用接口: POST /jst/wx/channel/auth/apply 或 /resubmit/{id}
     后端入参: { channelType, applyName, materialsJson(JSON字符串) } -->
<template>
  <view class="af-page">
    <view class="af-hero" :style="{ paddingTop: navPaddingTop }">
      <text class="af-hero__title">{{ typeLabel }}认证</text>
    </view>

    <view class="af-section">
      <text class="af-section__title">基本信息</text>
      <u--form labelPosition="left" :borderBottom="false" customClass="af-form">
        <u-form-item label="真实姓名 *" customClass="af-field">
          <u--input class="af-field__input" v-model="form.applyName" placeholder="请输入真实姓名" maxlength="128" border="none"></u--input>
        </u-form-item>
        <u-form-item label="手机号 *" customClass="af-field">
          <u--input class="af-field__input" v-model="form.mobile" type="number" placeholder="请输入手机号" maxlength="11" border="none"></u--input>
        </u-form-item>
        <u-form-item label="身份证号" customClass="af-field">
          <u--input class="af-field__input" v-model="form.idCard" placeholder="选填" maxlength="18" border="none"></u--input>
        </u-form-item>
      </u--form>
    </view>

    <view v-if="channelType === 'teacher'" class="af-section">
      <text class="af-section__title">教师材料</text>
      <u--form labelPosition="left" :borderBottom="false" customClass="af-form">
        <u-form-item label="任教学校 *" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.schoolName" placeholder="请输入学校名称" maxlength="128" border="none"></u--input>
        </u-form-item>
        <u-form-item label="教师资格证" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.teacherCertUrl" placeholder="OSS URL (模拟上传)" border="none"></u--input>
        </u-form-item>
      </u--form>
    </view>

    <view v-if="channelType === 'organization'" class="af-section">
      <text class="af-section__title">机构材料</text>
      <u--form labelPosition="left" :borderBottom="false" customClass="af-form">
        <u-form-item label="机构名称 *" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.orgName" placeholder="请输入机构名称" maxlength="128" border="none"></u--input>
        </u-form-item>
        <u-form-item label="营业执照号 *" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.businessLicenseNo" placeholder="请输入营业执照号" maxlength="64" border="none"></u--input>
        </u-form-item>
        <u-form-item label="营业执照照片" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.businessLicenseUrl" placeholder="OSS URL (模拟上传)" border="none"></u--input>
        </u-form-item>
        <u-form-item label="法人姓名" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.legalPersonName" placeholder="请输入法人姓名" maxlength="64" border="none"></u--input>
        </u-form-item>
      </u--form>
    </view>

    <view v-if="channelType === 'individual'" class="af-section">
      <text class="af-section__title">个人材料</text>
      <u--form labelPosition="left" :borderBottom="false" customClass="af-form">
        <u-form-item label="身份证正面" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.idCardFrontUrl" placeholder="OSS URL (模拟上传)" border="none"></u--input>
        </u-form-item>
        <u-form-item label="身份证反面" customClass="af-field">
          <u--input class="af-field__input" v-model="materials.idCardBackUrl" placeholder="OSS URL (模拟上传)" border="none"></u--input>
        </u-form-item>
        <u-form-item label="个人简介" customClass="af-field af-field--textarea">
          <u--textarea class="af-field__textarea" v-model="materials.personalDesc" placeholder="选填 (500字内)" maxlength="500" border="none" count></u--textarea>
        </u-form-item>
      </u--form>
    </view>

    <view class="af-agree" @tap="agreed = !agreed">
      <u-icon :name="agreed ? 'checkmark-circle-fill' : 'checkmark-circle'" size="40rpx" :class="agreed ? 'af-agree__icon--on' : 'af-agree__icon--off'"></u-icon>
      <text class="af-agree__text">我已阅读并同意《渠道方服务协议》</text>
    </view>

    <view class="af-footer">
      <u-button class="af-footer__btn" type="primary" :disabled="submitting || !canSubmit" :loading="submitting" shape="circle" @click="onSubmit">
        提交申请
      </u-button>
    </view>
  </view>
</template>

<script>
import { submitChannelApply, resubmitChannelApply } from '@/api/channel'

const TYPE_LABEL = { teacher: '老师', organization: '机构', individual: '个人' }

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
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
@import '@/styles/design-tokens.scss';

.af-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.af-hero { padding: 72rpx $jst-space-xl $jst-space-xxl; background: linear-gradient(135deg, $jst-brand, $jst-brand-dark); color: $jst-text-inverse; }
.af-hero__title { display: block; font-size: $jst-font-xxl; font-weight: $jst-weight-semibold; }
.af-section { margin: $jst-space-lg $jst-space-xl 0; padding: $jst-space-md $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.af-section__title { display: block; font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; padding: 20rpx 0 $jst-space-xs; }

::v-deep .af-form .u-form-item {
  padding: $jst-space-md 0;
  border-bottom: 2rpx solid $jst-border;
}

::v-deep .af-form .u-form-item:last-child {
  border-bottom: none;
}

::v-deep .af-form .u-form-item__body__left__content__label {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

::v-deep .af-field__input .u-input__content__field {
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

::v-deep .af-field__textarea .u-textarea__field {
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

.af-agree { display: flex; align-items: center; gap: $jst-space-md; margin: $jst-space-xl $jst-space-xl 0; }
.af-agree__icon--on { color: $jst-brand; }
.af-agree__icon--off { color: $jst-text-placeholder; }
.af-agree__text { font-size: $jst-font-sm; color: $jst-text-secondary; }
.af-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }

::v-deep .af-footer__btn.u-button {
  height: 96rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border: none;
}
</style>
