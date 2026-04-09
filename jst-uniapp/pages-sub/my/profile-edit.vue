<template>
  <view class="profile-edit-page">
    <jst-loading :loading="pageLoading" text="资料加载中..." />

    <view class="profile-edit-page__header">
      <view class="profile-edit-page__back" @tap="handleBack">‹</view>
      <text class="profile-edit-page__title">编辑资料</text>
      <text class="profile-edit-page__save" @tap="submitProfile">保存</text>
    </view>

    <view class="profile-edit-page__avatar-card">
      <view class="profile-edit-page__avatar" @tap="handleAvatarTap">
        <image
          v-if="profile.avatar"
          class="profile-edit-page__avatar-image"
          :src="profile.avatar"
          mode="aspectFill"
        />
        <text v-else class="profile-edit-page__avatar-text">{{ getAvatarText() }}</text>
      </view>
      <text class="profile-edit-page__avatar-hint">点击头像可更换，当前为占位能力</text>
    </view>

    <view class="profile-edit-page__section">
      <text class="profile-edit-page__section-title">基础信息</text>

      <view class="profile-edit-page__row">
        <text class="profile-edit-page__label">昵称</text>
        <input
          v-model="form.nickname"
          class="profile-edit-page__input"
          maxlength="20"
          placeholder="请输入昵称"
          placeholder-class="profile-edit-page__placeholder"
        />
      </view>

      <view class="profile-edit-page__row">
        <text class="profile-edit-page__label">真实姓名</text>
        <input
          v-model="form.realName"
          class="profile-edit-page__input"
          maxlength="20"
          placeholder="请输入真实姓名"
          placeholder-class="profile-edit-page__placeholder"
        />
      </view>

      <picker mode="selector" :range="genderLabels" :value="getGenderIndex()" @change="handleGenderChange">
        <view class="profile-edit-page__row profile-edit-page__row--picker">
          <text class="profile-edit-page__label">性别</text>
          <text class="profile-edit-page__value">{{ getGenderText(form.gender) }}</text>
        </view>
      </picker>

      <picker mode="date" :value="form.birthday" start="1970-01-01" end="2099-12-31" @change="handleBirthdayChange">
        <view class="profile-edit-page__row profile-edit-page__row--picker">
          <text class="profile-edit-page__label">出生日期</text>
          <text class="profile-edit-page__value">{{ form.birthday || '请选择出生日期' }}</text>
        </view>
      </picker>

      <view class="profile-edit-page__row profile-edit-page__row--readonly">
        <text class="profile-edit-page__label">手机号</text>
        <text class="profile-edit-page__value">{{ profile.mobileMasked || '未绑定手机号' }}</text>
        <text class="profile-edit-page__badge">不可修改</text>
      </view>
    </view>

    <view class="profile-edit-page__section">
      <text class="profile-edit-page__section-title">监护人信息</text>

      <view class="profile-edit-page__row">
        <text class="profile-edit-page__label">监护人姓名</text>
        <input
          v-model="form.guardianName"
          class="profile-edit-page__input"
          maxlength="20"
          placeholder="请输入监护人姓名"
          placeholder-class="profile-edit-page__placeholder"
        />
      </view>

      <view class="profile-edit-page__row">
        <text class="profile-edit-page__label">监护人手机</text>
        <input
          v-model="form.guardianMobile"
          class="profile-edit-page__input"
          maxlength="11"
          type="number"
          placeholder="请输入监护人手机号"
          placeholder-class="profile-edit-page__placeholder"
          @focus="handleGuardianMobileFocus"
        />
      </view>

      <text v-if="profile.guardianMobileMasked" class="profile-edit-page__hint">
        当前已保存手机号：{{ profile.guardianMobileMasked }}
      </text>
    </view>

    <view class="profile-edit-page__notice">
      <text class="profile-edit-page__notice-title">资料完善提示</text>
      <text class="profile-edit-page__notice-text">
        保存后资料会同步到“我的”页面展示；手机号只显示接口返回的脱敏字段，避免前端暴露敏感信息。
      </text>
    </view>

    <view class="profile-edit-page__bottom">
      <button class="profile-edit-page__submit" :loading="submitting" @tap="submitProfile">保存资料</button>
    </view>
  </view>
</template>

<script>
import { getProfile, updateProfile } from '@/api/auth'
import { useUserStore } from '@/store/user'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstLoading },
  data() {
    return {
      fromLogin: false,
      pageLoading: false,
      submitting: false,
      profile: {
        avatar: '',
        mobileMasked: '',
        guardianMobileMasked: ''
      },
      form: {
        nickname: '',
        realName: '',
        gender: 0,
        birthday: '',
        guardianName: '',
        guardianMobile: ''
      },
      genderOptions: [
        { label: '未设置', value: 0 },
        { label: '男', value: 1 },
        { label: '女', value: 2 }
      ]
    }
  },
  computed: {
    genderLabels() {
      return this.genderOptions.map((item) => item.label)
    }
  },
  onLoad(options) {
    this.fromLogin = options.fromLogin === '1'
    this.loadProfile()
  },
  methods: {
    handleBack() {
      if (this.fromLogin) {
        uni.switchTab({ url: '/pages/my/index' })
        return
      }
      uni.navigateBack()
    },
    async loadProfile() {
      this.pageLoading = true
      try {
        const profile = await getProfile()
        this.profile = {
          avatar: profile.avatar || '',
          mobileMasked: profile.mobileMasked || '',
          guardianMobileMasked: profile.guardianMobileMasked || ''
        }
        this.form.nickname = profile.nickname || ''
        this.form.realName = profile.realName || ''
        this.form.gender = typeof profile.gender === 'number' ? profile.gender : 0
        this.form.birthday = this.normalizeDate(profile.birthday)
        this.form.guardianName = profile.guardianName || ''
        this.form.guardianMobile = profile.guardianMobileMasked || ''
      } finally {
        this.pageLoading = false
      }
    },
    normalizeDate(value) {
      if (!value) return ''
      if (typeof value === 'string') return value.slice(0, 10)
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) return ''
      return `${date.getFullYear()}-${`${date.getMonth() + 1}`.padStart(2, '0')}-${`${date.getDate()}`.padStart(2, '0')}`
    },
    getAvatarText() {
      return (this.form.nickname || this.form.realName || '我').slice(0, 1)
    },
    getGenderIndex() {
      const index = this.genderOptions.findIndex((item) => item.value === this.form.gender)
      return index === -1 ? 0 : index
    },
    getGenderText(value) {
      const target = this.genderOptions.find((item) => item.value === value)
      return target ? target.label : '未设置'
    },
    handleGenderChange(event) {
      const current = this.genderOptions[event.detail.value]
      this.form.gender = current ? current.value : 0
    },
    handleBirthdayChange(event) {
      this.form.birthday = event.detail.value
    },
    handleAvatarTap() {
      uni.showToast({ title: '头像上传能力暂未接入', icon: 'none' })
    },
    handleGuardianMobileFocus() {
      if (this.form.guardianMobile.indexOf('*') !== -1) {
        this.form.guardianMobile = ''
      }
    },
    async submitProfile() {
      if (this.submitting) return

      const payload = {
        nickname: this.form.nickname.trim(),
        realName: this.form.realName.trim(),
        gender: this.form.gender,
        birthday: this.form.birthday,
        guardianName: this.form.guardianName.trim()
      }

      if (this.form.guardianMobile && this.form.guardianMobile.indexOf('*') === -1) {
        if (!/^1\d{10}$/.test(this.form.guardianMobile)) {
          uni.showToast({ title: '请输入正确的监护人手机号', icon: 'none' })
          return
        }
        payload.guardianMobile = this.form.guardianMobile
      }

      this.submitting = true
      try {
        await updateProfile(payload)
        await useUserStore().fetchProfile().catch(() => null)
        uni.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => {
          if (this.fromLogin) {
            uni.switchTab({ url: '/pages/my/index' })
            return
          }
          uni.navigateBack()
        }, 400)
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.profile-edit-page {
  min-height: 100vh;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
  background: var(--jst-color-page-bg);
}

.profile-edit-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.profile-edit-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: var(--jst-radius-sm);
  background: var(--jst-color-page-bg);
  font-size: 36rpx;
  color: var(--jst-color-text-secondary);
}

.profile-edit-page__title {
  flex: 1;
  margin-left: 16rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.profile-edit-page__save {
  font-size: 26rpx;
  font-weight: 700;
  color: var(--jst-color-brand);
}

.profile-edit-page__avatar-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 20rpx;
  padding: 36rpx 0;
  background: var(--jst-color-card-bg);
}

.profile-edit-page__avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
}

.profile-edit-page__avatar-image {
  width: 100%;
  height: 100%;
}

.profile-edit-page__avatar-text {
  font-size: 50rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
}

.profile-edit-page__avatar-hint {
  margin-top: 16rpx;
  font-size: 22rpx;
  color: var(--jst-color-brand);
}

.profile-edit-page__section {
  margin-top: 20rpx;
  padding: 0 24rpx 8rpx;
  background: var(--jst-color-card-bg);
}

.profile-edit-page__section-title {
  display: block;
  padding: 24rpx 0 12rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.profile-edit-page__row {
  display: flex;
  align-items: center;
  min-height: 88rpx;
  border-bottom: 2rpx solid var(--jst-color-border);
}

.profile-edit-page__row:last-child {
  border-bottom: none;
}

.profile-edit-page__label {
  width: 180rpx;
  font-size: 24rpx;
  color: var(--jst-color-text-secondary);
}

.profile-edit-page__input,
.profile-edit-page__value {
  flex: 1;
  text-align: right;
  font-size: 24rpx;
  color: var(--jst-color-text);
}

.profile-edit-page__placeholder {
  color: var(--jst-color-text-tertiary);
}

.profile-edit-page__row--picker::after {
  content: '>';
  margin-left: 12rpx;
  color: var(--jst-color-text-tertiary);
}

.profile-edit-page__row--readonly {
  gap: 12rpx;
}

.profile-edit-page__badge {
  padding: 4rpx 12rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-gray-soft);
  font-size: 18rpx;
  color: var(--jst-color-text-tertiary);
}

.profile-edit-page__hint {
  display: block;
  padding: 12rpx 0 8rpx;
  font-size: 20rpx;
  color: var(--jst-color-text-tertiary);
}

.profile-edit-page__notice {
  margin: 20rpx 24rpx 0;
  padding: 20rpx 24rpx;
  border-radius: var(--jst-radius-md);
  background: var(--jst-color-primary-soft);
}

.profile-edit-page__notice-title {
  display: block;
  font-size: 24rpx;
  font-weight: 700;
  color: var(--jst-color-brand);
}

.profile-edit-page__notice-text {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.profile-edit-page__bottom {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 -8rpx 18rpx rgba(15, 52, 96, 0.08);
}

.profile-edit-page__submit {
  height: 88rpx;
  border-radius: var(--jst-radius-md);
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
  color: var(--jst-color-card-bg);
  font-size: 28rpx;
  font-weight: 700;
}
</style>
