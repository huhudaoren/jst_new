<!-- 中文注释: 赛事方入驻申请页；对应原型 小程序原型图/event-apply.html + event-apply.png；对应接口 POST /jst/public/organizer/partner/apply -->
<template>
  <view class="partner-apply-page">
    <jst-loading :loading="submitting" text="申请提交中..." />

    <view class="partner-apply-page__nav">
      <view class="partner-apply-page__back" @tap="handleBack">←</view>
      <text class="partner-apply-page__nav-title">赛事方入驻申请</text>
      <view class="partner-apply-page__nav-placeholder"></view>
    </view>

    <view class="partner-apply-page__steps">
      <view
        v-for="(step, index) in steps"
        :key="step.key"
        class="partner-apply-page__step"
        :class="{ 'partner-apply-page__step--active': index === 0 }"
      >
        <view class="partner-apply-page__step-circle">{{ index + 1 }}</view>
        <text class="partner-apply-page__step-label">{{ step.label }}</text>
      </view>
    </view>

    <view class="partner-apply-page__body">
      <view class="partner-apply-page__card">
        <text class="partner-apply-page__card-title">公司基本信息</text>

        <view class="partner-apply-page__field">
          <text class="partner-apply-page__label">公司名称</text>
          <input
            v-model.trim="form.partnerName"
            class="partner-apply-page__input"
            maxlength="128"
            placeholder="请输入机构名称"
            placeholder-class="partner-apply-page__placeholder"
          />
        </view>

        <view class="partner-apply-page__field">
          <text class="partner-apply-page__label">联系人姓名</text>
          <input
            v-model.trim="form.contactName"
            class="partner-apply-page__input"
            maxlength="64"
            placeholder="请输入联系人姓名"
            placeholder-class="partner-apply-page__placeholder"
          />
        </view>

        <view class="partner-apply-page__field">
          <text class="partner-apply-page__label">联系手机</text>
          <input
            v-model.trim="form.contactMobile"
            class="partner-apply-page__input"
            type="number"
            maxlength="11"
            placeholder="请输入 11 位手机号码"
            placeholder-class="partner-apply-page__placeholder"
          />
        </view>

        <view class="partner-apply-page__field">
          <text class="partner-apply-page__label partner-apply-page__label--optional">统一社会信用代码 / 营业执照号</text>
          <input
            v-model.trim="form.businessLicenseNo"
            class="partner-apply-page__input"
            maxlength="64"
            placeholder="选填，可填写营业执照号"
            placeholder-class="partner-apply-page__placeholder"
          />
        </view>
      </view>

      <view class="partner-apply-page__card">
        <view class="partner-apply-page__card-head">
          <text class="partner-apply-page__card-title">上传资质材料</text>
          <text class="partner-apply-page__card-desc">本期为前端占位上传，提交时保存为 mock 文件地址</text>
        </view>

        <view class="partner-apply-page__upload" @tap="chooseQualificationImages">
          <text class="partner-apply-page__upload-icon">☁</text>
          <text class="partner-apply-page__upload-title">上传资质材料图片</text>
          <text class="partner-apply-page__upload-desc">最多 5 张，支持从相册或拍照选择</text>
        </view>

        <view v-if="qualificationFiles.length" class="partner-apply-page__file-list">
          <view
            v-for="(file, index) in qualificationFiles"
            :key="file.url"
            class="partner-apply-page__file-item"
          >
            <image
              class="partner-apply-page__file-thumb"
              :src="file.localPath"
              mode="aspectFill"
              @tap="previewQualification(index)"
            />
            <view class="partner-apply-page__file-body">
              <text class="partner-apply-page__file-name">{{ file.name }}</text>
              <text class="partner-apply-page__file-meta">mock 地址：{{ file.url }}</text>
            </view>
            <text class="partner-apply-page__file-delete" @tap.stop="removeQualification(index)">删除</text>
          </view>
        </view>

        <jst-empty
          v-else
          icon="🗂"
          text="可选上传资质材料，便于后台审核时快速判断"
        />
      </view>

      <view class="partner-apply-page__card">
        <view class="partner-apply-page__card-head">
          <text class="partner-apply-page__card-title">结算 / 发票备注</text>
          <text class="partner-apply-page__card-desc">当前版本按一个备注框收集信息，并同步映射到 settlement / invoice JSON</text>
        </view>

        <textarea
          v-model.trim="form.financeRemark"
          class="partner-apply-page__textarea"
          maxlength="200"
          placeholder="选填，可填写结算方式、开票抬头、税号或其他补充说明"
          placeholder-class="partner-apply-page__placeholder"
        />
      </view>

      <view class="partner-apply-page__agreement" @tap="toggleAgree">
        <view class="partner-apply-page__agreement-check" :class="{ 'partner-apply-page__agreement-check--active': form.agree }">
          {{ form.agree ? '✓' : '' }}
        </view>
        <text class="partner-apply-page__agreement-text">
          我已确认提交信息真实有效，并同意平台在审核过程中联系我。
        </text>
      </view>

      <text class="partner-apply-page__status-link" @tap="goStatusPage()">已有申请单号，去查询审核状态</text>

      <view v-if="lastApplyNo" class="partner-apply-page__last-apply">
        最近申请单号：{{ lastApplyNo }}
      </view>
    </view>

    <view class="partner-apply-page__footer">
      <button class="partner-apply-page__footer-secondary" @tap="handleSaveDraft">保存草稿</button>
      <button class="partner-apply-page__submit" :disabled="submitting" @tap="submitForm">
        提交入驻申请
      </button>
    </view>
  </view>
</template>

<script>
import { submitPartnerApply } from '@/api/public'

const MOBILE_REGEXP = /^1[3-9]\d{9}$/

export default {
  data() {
    return {
      steps: [
        { key: 'company', label: '公司信息' },
        { key: 'qualification', label: '资质材料' },
        { key: 'finance', label: '结算信息' },
        { key: 'submit', label: '提交审核' }
      ],
      submitting: false,
      lastApplyNo: '',
      form: {
        partnerName: '',
        contactName: '',
        contactMobile: '',
        businessLicenseNo: '',
        financeRemark: '',
        agree: false
      },
      qualificationFiles: []
    }
  },
  methods: {
    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }

      uni.switchTab({ url: '/pages/index/index' })
    },

    toggleAgree() {
      this.form.agree = !this.form.agree
    },

    handleSaveDraft() {
      uni.showToast({
        title: '当前版本暂不支持草稿保存',
        icon: 'none'
      })
    },

    chooseQualificationImages() {
      const remainCount = 5 - this.qualificationFiles.length

      if (remainCount <= 0) {
        uni.showToast({
          title: '最多上传 5 张资质材料',
          icon: 'none'
        })
        return
      }

      uni.chooseImage({
        count: remainCount,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const nextFiles = (res.tempFilePaths || []).map((localPath, index) => ({
            name: `资质材料-${this.qualificationFiles.length + index + 1}.jpg`,
            url: `mock://qualification/${Date.now()}-${index + 1}.jpg`,
            localPath
          }))

          this.qualificationFiles = this.qualificationFiles.concat(nextFiles).slice(0, 5)
        }
      })
    },

    previewQualification(index) {
      uni.previewImage({
        current: index,
        urls: this.qualificationFiles.map(item => item.localPath)
      })
    },

    removeQualification(index) {
      this.qualificationFiles.splice(index, 1)
    },

    validateForm() {
      if (!this.form.partnerName) {
        return '请输入机构名称'
      }
      if (this.form.partnerName.length > 128) {
        return '机构名称不能超过 128 个字符'
      }
      if (!this.form.contactName) {
        return '请输入联系人姓名'
      }
      if (!this.form.contactMobile) {
        return '请输入联系手机'
      }
      if (!MOBILE_REGEXP.test(this.form.contactMobile)) {
        return '联系手机格式不正确'
      }
      if (!this.form.agree) {
        return '请先勾选同意说明'
      }
      return ''
    },

    buildPayload() {
      const qualificationList = this.qualificationFiles.map(item => ({
        name: item.name,
        url: item.url
      }))
      const financeRemark = this.form.financeRemark
        ? { remark: this.form.financeRemark }
        : null

      return {
        partnerName: this.form.partnerName,
        contactName: this.form.contactName,
        contactMobile: this.form.contactMobile,
        businessLicenseNo: this.form.businessLicenseNo || '',
        qualificationJson: qualificationList.length ? JSON.stringify(qualificationList) : '',
        settlementInfoJson: financeRemark ? JSON.stringify(financeRemark) : '',
        invoiceInfoJson: financeRemark ? JSON.stringify(financeRemark) : '',
        contractFilesJson: ''
      }
    },

    async submitForm() {
      const errorMessage = this.validateForm()

      if (errorMessage) {
        uni.showToast({
          title: errorMessage,
          icon: 'none'
        })
        return
      }

      this.submitting = true

      try {
        const response = await submitPartnerApply(this.buildPayload())
        const applyNo = response && response.applyNo ? response.applyNo : ''

        this.lastApplyNo = applyNo

        uni.showModal({
          title: '提交成功',
          content: applyNo
            ? `您的申请单号为 ${applyNo}，可前往状态查询页查看审核进度。`
            : '申请已提交成功，可前往状态查询页查看审核进度。',
          showCancel: false,
          success: () => {
            this.goStatusPage(applyNo)
          }
        })
      } catch (error) {
      } finally {
        this.submitting = false
      }
    },

    goStatusPage(applyNo) {
      const targetApplyNo = applyNo || this.lastApplyNo || ''
      const url = targetApplyNo
        ? `/pages-sub/public/apply-status?applyNo=${targetApplyNo}`
        : '/pages-sub/public/apply-status'

      uni.navigateTo({ url })
    }
  }
}
</script>

<style scoped lang="scss">
.partner-apply-page {
  min-height: 100vh;
  padding-bottom: calc(128rpx + env(safe-area-inset-bottom));
  background: #f5f6fa;
}

.partner-apply-page__nav {
  display: flex;
  align-items: center;
  height: 100rpx;
  padding: 0 24rpx;
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%);
}

.partner-apply-page__back,
.partner-apply-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.partner-apply-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  background: rgba(255, 255, 255, 0.12);
  color: var(--jst-color-card-bg);
  font-size: 30rpx;
}

.partner-apply-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 800;
  color: var(--jst-color-card-bg);
}

.partner-apply-page__steps {
  display: flex;
  gap: 0;
  margin: 0;
  padding: 20rpx 12rpx 18rpx;
  background: var(--jst-color-card-bg);
  border-bottom: 2rpx solid #eef1f6;
}

.partner-apply-page__step {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.partner-apply-page__step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 14rpx;
  left: calc(50% + 28rpx);
  width: calc(100% - 56rpx);
  height: 4rpx;
  border-radius: 999rpx;
  background: #e7ebf3;
}

.partner-apply-page__step--active::after {
  background: rgba(39, 174, 96, 0.18);
}

.partner-apply-page__step-circle {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  border: 4rpx solid #d8deea;
  background: var(--jst-color-card-bg);
  font-size: 22rpx;
  font-weight: 700;
  color: #8f98ab;
}

.partner-apply-page__step--active .partner-apply-page__step-circle {
  border-color: var(--jst-color-success);
  color: var(--jst-color-success);
}

.partner-apply-page__step-label {
  font-size: 20rpx;
  color: #8f98ab;
}

.partner-apply-page__step--active .partner-apply-page__step-label {
  font-weight: 700;
  color: var(--jst-color-success);
}

.partner-apply-page__body {
  padding: 24rpx;
}

.partner-apply-page__card {
  margin-bottom: 20rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: var(--jst-color-card-bg);
  box-shadow: 0 10rpx 24rpx rgba(35, 52, 116, 0.06);
}

.partner-apply-page__card-title {
  display: flex;
  align-items: center;
  font-size: 30rpx;
  font-weight: 700;
  color: #2c3448;
}

.partner-apply-page__card-title::before {
  content: '';
  width: 6rpx;
  height: 28rpx;
  margin-right: 10rpx;
  border-radius: 999rpx;
  background: var(--jst-color-success);
}

.partner-apply-page__card-head {
  margin-bottom: 20rpx;
}

.partner-apply-page__card-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.6;
  color: #8d97aa;
}

.partner-apply-page__field + .partner-apply-page__field {
  margin-top: 18rpx;
}

.partner-apply-page__label {
  display: block;
  margin-bottom: 10rpx;
  font-size: 22rpx;
  font-weight: 600;
  color: #677489;
}

.partner-apply-page__label::after {
  content: ' *';
  color: var(--jst-color-danger);
}

.partner-apply-page__label--optional::after {
  content: '（选填）';
  color: var(--jst-color-text-tertiary);
  font-weight: 400;
}

.partner-apply-page__input,
.partner-apply-page__textarea {
  width: 100%;
  padding: 22rpx 24rpx;
  border: 2rpx solid #e7ebf3;
  border-radius: 20rpx;
  background: #fbfcff;
  font-size: 26rpx;
  color: #2c3448;
}

.partner-apply-page__textarea {
  min-height: 168rpx;
  line-height: 1.7;
}

.partner-apply-page__placeholder {
  color: #a3acbc;
}

.partner-apply-page__upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 38rpx 24rpx;
  border: 2rpx dashed #dce3ef;
  border-radius: 20rpx;
  background: #f8fafc;
}

.partner-apply-page__upload-icon {
  font-size: 42rpx;
  color: var(--jst-color-success);
}

.partner-apply-page__upload-title {
  margin-top: 14rpx;
  font-size: 26rpx;
  font-weight: 700;
  color: #2c3448;
}

.partner-apply-page__upload-desc {
  margin-top: 10rpx;
  font-size: 20rpx;
  color: #8d97aa;
}

.partner-apply-page__file-list {
  margin-top: 20rpx;
}

.partner-apply-page__file-item {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 18rpx 0;
  border-bottom: 2rpx solid var(--jst-color-border);
}

.partner-apply-page__file-item:last-child {
  border-bottom: 0;
}

.partner-apply-page__file-thumb {
  width: 100rpx;
  height: 100rpx;
  border-radius: 20rpx;
  flex-shrink: 0;
}

.partner-apply-page__file-body {
  flex: 1;
  min-width: 0;
}

.partner-apply-page__file-name {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: var(--jst-color-text);
}

.partner-apply-page__file-meta {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.6;
  color: var(--jst-color-text-tertiary);
  word-break: break-all;
}

.partner-apply-page__file-delete {
  font-size: 22rpx;
  color: var(--jst-color-danger);
}

.partner-apply-page__agreement {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin: 8rpx 6rpx 0;
}

.partner-apply-page__agreement-check {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36rpx;
  height: 36rpx;
  margin-top: 4rpx;
  border: 2rpx solid #d8deea;
  border-radius: 10rpx;
  background: var(--jst-color-card-bg);
  font-size: 20rpx;
  color: var(--jst-color-card-bg);
  flex-shrink: 0;
}

.partner-apply-page__agreement-check--active {
  border-color: var(--jst-color-success);
  background: var(--jst-color-success);
}

.partner-apply-page__agreement-text {
  flex: 1;
  font-size: 22rpx;
  line-height: 1.7;
  color: #677489;
}

.partner-apply-page__status-link {
  display: block;
  margin: 20rpx 6rpx 0;
  font-size: 22rpx;
  color: #2e7d32;
}

.partner-apply-page__last-apply {
  margin: 14rpx 6rpx 0;
  font-size: 22rpx;
  color: #7c879c;
}

.partner-apply-page__footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 18rpx 24rpx calc(18rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-card-bg);
  box-shadow: 0 -10rpx 24rpx rgba(35, 52, 116, 0.08);
}

.partner-apply-page__footer-secondary,
.partner-apply-page__submit {
  height: 88rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  font-weight: 700;
}

.partner-apply-page__footer-secondary {
  width: 30%;
  background: #eff2f7;
  color: #4f5d78;
}

.partner-apply-page__submit {
  flex: 1;
  background: linear-gradient(135deg, #1b5e20 0%, #43a047 100%);
  color: var(--jst-color-card-bg);
}

.partner-apply-page__submit[disabled] {
  background: var(--jst-color-border);
  color: var(--jst-color-text-tertiary);
}
</style>
