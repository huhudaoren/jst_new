<!-- 中文注释: 我的档案页；无独立原型，卡片样式参考 小程序原型图/my-binding.html；对应接口 GET /jst/wx/participant/my、POST /jst/wx/participant/claim、GET /jst/wx/participant/{id} -->
<template>
  <view class="participant-page">
    <jst-loading :loading="pageLoading" text="档案加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="participant-page__header">
      <view class="participant-page__back" @tap="handleBack">←</view>
      <text class="participant-page__title">我的档案</text>
      <view class="participant-page__actions">
        <u-button class="participant-page__action participant-page__action--primary" size="mini" shape="circle" @click="openCreateDialog">+ 新建</u-button>
        <u-button class="participant-page__action" size="mini" shape="circle" @click="openClaimDialog">认领</u-button>
      </view>
    </view>

    <view class="participant-page__intro">
      <text class="participant-page__intro-title">已认领参赛档案</text>
      <text class="participant-page__intro-text">通过老师或赛事方分享的档案编号，可主动完成认领并在后续报名中复用。</text>
    </view>

    <view v-if="participants.length" class="participant-page__list">
      <view
        v-for="item in participants"
        :key="item.participantId"
        class="participant-page__card"
        @tap="openDetail(item)"
      >
        <view class="participant-page__card-top">
          <view>
            <text class="participant-page__card-name">{{ item.name || '未命名档案' }}</text>
            <text class="participant-page__card-id">档案编号：{{ item.participantId }}</text>
          </view>
          <u-tag class="participant-page__card-tag" :text="getClaimMethodText(item.claimMethod)" type="success" plain />
        </view>

        <view class="participant-page__meta">
          <text class="participant-page__meta-text">学校：{{ item.school || '未填写' }}</text>
          <text class="participant-page__meta-text">班级：{{ item.className || '未填写' }}</text>
          <text class="participant-page__meta-text">年龄：{{ item.age || '未填写' }}</text>
          <text class="participant-page__meta-text">监护人：{{ item.guardianName || '未填写' }}</text>
        </view>

        <view class="participant-page__footer">
          <text class="participant-page__footer-text">认领时间：{{ formatDate(item.claimTime) }}</text>
          <text class="participant-page__footer-link">查看详情</text>
        </view>
      </view>
    </view>

    <jst-empty
      v-else
      text="暂无已认领档案，通过老师或赛事方分享的档案编号完成认领"
      icon="📭"
    />

    <view v-if="createDialogVisible" class="participant-page__mask" @tap="closeCreateDialog">
      <view class="participant-page__dialog" @tap.stop="">
        <text class="participant-page__dialog-title">新建参赛档案</text>
        <text class="participant-page__dialog-text">用于赛事报名时选择的参赛人信息，创建后可在报名流程中复用。</text>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">姓名<text class="participant-page__form-required">*</text></text>
          <input
            v-model="createForm.name"
            class="participant-page__dialog-input"
            maxlength="32"
            placeholder="请输入参赛人真实姓名"
            placeholder-class="participant-page__placeholder"
          />
        </view>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">性别</text>
          <view class="participant-page__gender-group">
            <view
              v-for="opt in genderOptions"
              :key="opt.value"
              class="participant-page__gender-item"
              :class="{ 'participant-page__gender-item--active': createForm.gender === opt.value }"
              @tap="createForm.gender = opt.value"
            >{{ opt.label }}</view>
          </view>
        </view>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">生日</text>
          <picker mode="date" :value="createForm.birthday" :end="todayStr" @change="onBirthdayChange">
            <view class="participant-page__picker">
              <text :class="createForm.birthday ? 'participant-page__picker-value' : 'participant-page__placeholder'">
                {{ createForm.birthday || '选择出生日期' }}
              </text>
            </view>
          </picker>
        </view>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">学校</text>
          <input
            v-model="createForm.school"
            class="participant-page__dialog-input"
            maxlength="64"
            placeholder="选填，如 XX小学"
            placeholder-class="participant-page__placeholder"
          />
        </view>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">班级</text>
          <input
            v-model="createForm.className"
            class="participant-page__dialog-input"
            maxlength="32"
            placeholder="选填，如 三年级2班"
            placeholder-class="participant-page__placeholder"
          />
        </view>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">监护人姓名</text>
          <input
            v-model="createForm.guardianName"
            class="participant-page__dialog-input"
            maxlength="32"
            placeholder="选填"
            placeholder-class="participant-page__placeholder"
          />
        </view>

        <view class="participant-page__form-row">
          <text class="participant-page__form-label">监护人手机号</text>
          <input
            v-model="createForm.guardianMobile"
            class="participant-page__dialog-input"
            type="number"
            maxlength="11"
            placeholder="选填"
            placeholder-class="participant-page__placeholder"
          />
        </view>

        <view class="participant-page__dialog-actions">
          <u-button class="participant-page__dialog-button participant-page__dialog-button--ghost" @click="closeCreateDialog">
            取消
          </u-button>
          <u-button class="participant-page__dialog-button participant-page__dialog-button--primary" :loading="createSubmitting" @click="submitCreate">确认创建</u-button>
        </view>
      </view>
    </view>

    <view v-if="claimDialogVisible" class="participant-page__mask" @tap="closeClaimDialog">
      <view class="participant-page__dialog" @tap.stop="">
        <text class="participant-page__dialog-title">认领新档案</text>
        <text class="participant-page__dialog-text">请输入老师或赛事方提供的 participantId。</text>
        <input
          v-model="claimForm.participantId"
          class="participant-page__dialog-input"
          type="number"
          maxlength="10"
          placeholder="请输入档案编号"
          placeholder-class="participant-page__placeholder"
        />
        <view class="participant-page__dialog-actions">
          <u-button class="participant-page__dialog-button participant-page__dialog-button--ghost" @click="closeClaimDialog">
            取消
          </u-button>
          <u-button class="participant-page__dialog-button participant-page__dialog-button--primary" :loading="claimSubmitting" @click="submitClaim">确认认领</u-button>
        </view>
      </view>
    </view>

    <view v-if="detailVisible" class="participant-page__mask" @tap="closeDetail">
      <view class="participant-page__detail" @tap.stop="">
        <text class="participant-page__detail-title">{{ detail.name || currentParticipant.name || '档案详情' }}</text>
        <view v-if="detailLoading" class="participant-page__detail-loading">详情加载中...</view>
        <view v-else class="participant-page__detail-content">
          <view class="participant-page__detail-row">
            <text class="participant-page__detail-label">档案编号</text>
            <text class="participant-page__detail-value">{{ detail.participantId || currentParticipant.participantId }}</text>
          </view>
          <view class="participant-page__detail-row">
            <text class="participant-page__detail-label">性别</text>
            <text class="participant-page__detail-value">{{ getGenderText(detail.gender) }}</text>
          </view>
          <view class="participant-page__detail-row">
            <text class="participant-page__detail-label">生日</text>
            <text class="participant-page__detail-value">{{ formatDate(detail.birthday) }}</text>
          </view>
          <view class="participant-page__detail-row">
            <text class="participant-page__detail-label">学校</text>
            <text class="participant-page__detail-value">{{ detail.school || currentParticipant.school || '未填写' }}</text>
          </view>
          <view class="participant-page__detail-row">
            <text class="participant-page__detail-label">班级</text>
            <text class="participant-page__detail-value">{{ detail.className || currentParticipant.className || '未填写' }}</text>
          </view>
          <view class="participant-page__detail-row">
            <text class="participant-page__detail-label">认领状态</text>
            <text class="participant-page__detail-value">{{ getClaimStatusText(detail.claimStatus) }}</text>
          </view>
        </view>
        <u-button class="participant-page__detail-close" @click="closeDetail">关闭</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { claimParticipant, getParticipantDetail, myParticipants, selfCreateParticipant } from '@/api/participant'
import { useUserStore } from '@/store/user'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: {
    JstEmpty,
    JstLoading
  },
  data() {
    return {
      pageLoading: false,
      participants: [],
      claimDialogVisible: false,
      claimSubmitting: false,
      claimForm: {
        participantId: ''
      },
      createDialogVisible: false,
      createSubmitting: false,
      createForm: {
        name: '',
        gender: 0,
        birthday: '',
        school: '',
        className: '',
        guardianName: '',
        guardianMobile: ''
      },
      genderOptions: [
        { value: 0, label: '未设置' },
        { value: 1, label: '男' },
        { value: 2, label: '女' }
      ],
      detailVisible: false,
      detailLoading: false,
      currentParticipant: {},
      detail: {},
      // POLISH-E: pick 模式, 由外部页面 (如 appointment/apply) 通过 ?mode=pick 进入, 选中后回传
      pickMode: false
    }
  },
  computed: {
    todayStr() {
      const d = new Date()
      const y = d.getFullYear()
      const m = `${d.getMonth() + 1}`.padStart(2, '0')
      const day = `${d.getDate()}`.padStart(2, '0')
      return `${y}-${m}-${day}`
    }
  },
  onLoad(query) {
    if (query && query.mode === 'pick') {
      this.pickMode = true
      uni.showToast({ title: '请选择参赛档案', icon: 'none' })
    }
  },
  onShow() {
    this.ensureLogin()
    this.loadParticipants()
  },
  methods: {
    ensureLogin() {
      const userStore = useUserStore()
      if (!userStore.token) {
        uni.reLaunch({ url: '/pages/login/login' })
      }
    },

    handleBack() {
      uni.navigateBack()
    },

    // 中文注释: 档案列表必须实时从接口拉取，认领成功后也通过重新请求刷新，不在前端拼接假数据。
    async loadParticipants() {
      this.pageLoading = true

      try {
        const list = await myParticipants()
        this.participants = Array.isArray(list) ? list : []
      } finally {
        this.pageLoading = false
      }
    },

    openClaimDialog() {
      this.claimForm.participantId = ''
      this.claimDialogVisible = true
    },

    closeClaimDialog() {
      if (this.claimSubmitting) {
        return
      }

      this.claimDialogVisible = false
    },

    openCreateDialog() {
      this.createForm = {
        name: '',
        gender: 0,
        birthday: '',
        school: '',
        className: '',
        guardianName: '',
        guardianMobile: ''
      }
      this.createDialogVisible = true
    },

    closeCreateDialog() {
      if (this.createSubmitting) {
        return
      }
      this.createDialogVisible = false
    },

    onBirthdayChange(e) {
      this.createForm.birthday = e.detail.value
    },

    async submitCreate() {
      if (this.createSubmitting) {
        return
      }

      const name = (this.createForm.name || '').trim()
      if (!name) {
        uni.showToast({ title: '请输入参赛人姓名', icon: 'none' })
        return
      }
      const mobile = (this.createForm.guardianMobile || '').trim()
      if (mobile && !/^1\d{10}$/.test(mobile)) {
        uni.showToast({ title: '监护人手机号格式不正确', icon: 'none' })
        return
      }

      const payload = {
        name,
        gender: this.createForm.gender || 0,
        birthday: this.createForm.birthday || null,
        school: (this.createForm.school || '').trim() || null,
        className: (this.createForm.className || '').trim() || null,
        guardianName: (this.createForm.guardianName || '').trim() || null,
        guardianMobile: mobile || null
      }

      this.createSubmitting = true

      try {
        const res = await selfCreateParticipant(payload)
        // 中文注释: 明确提示档案已保存并可用于报名, 让用户理解下一步
        uni.showToast({ title: '创建成功，可用于赛事报名', icon: 'success', duration: 1800 })
        this.createDialogVisible = false

        if (this.pickMode && res && res.participantId) {
          uni.setStorageSync('ap_picked_participant', { id: res.participantId, name })
          setTimeout(() => uni.navigateBack(), 500)
          return
        }

        await this.loadParticipants()
      } finally {
        this.createSubmitting = false
      }
    },

    async submitClaim() {
      if (this.claimSubmitting) {
        return
      }

      const participantId = `${this.claimForm.participantId}`.trim()
      if (!participantId) {
        uni.showToast({
          title: '请输入档案编号',
          icon: 'none'
        })
        return
      }

      this.claimSubmitting = true

      try {
        await claimParticipant(participantId)
        uni.showToast({
          title: '认领成功',
          icon: 'success'
        })
        this.claimDialogVisible = false
        await this.loadParticipants()
      } finally {
        this.claimSubmitting = false
      }
    },

    async openDetail(item) {
      // POLISH-E: pick 模式下, 点击卡片直接回传并返回, 不打开详情
      if (this.pickMode) {
        uni.setStorageSync('ap_picked_participant', { id: item.participantId, name: item.name })
        uni.navigateBack()
        return
      }
      this.currentParticipant = item
      this.detail = {}
      this.detailVisible = true
      this.detailLoading = true

      try {
        this.detail = await getParticipantDetail(item.participantId)
      } finally {
        this.detailLoading = false
      }
    },

    closeDetail() {
      this.detailVisible = false
      this.detail = {}
      this.currentParticipant = {}
    },

    getClaimMethodText(value) {
      const mapper = {
        auto_phone_name: '自动认领',
        manual_admin: '管理员认领',
        manual_user: '主动认领',
        self_create: '自建档案'
      }
      return mapper[value] || '已认领'
    },

    getClaimStatusText(value) {
      const mapper = {
        unclaimed: '未认领',
        auto_claimed: '自动认领',
        manual_claimed: '手动认领',
        pending_manual: '待人工确认'
      }
      return mapper[value] || '已认领'
    },

    getGenderText(value) {
      const mapper = {
        0: '未设置',
        1: '男',
        2: '女'
      }
      return mapper[value] || '未设置'
    },

    formatDate(value) {
      if (!value) {
        return '未记录'
      }

      if (typeof value === 'string') {
        return value.slice(0, 10)
      }

      const date = new Date(value)
      if (Number.isNaN(date.getTime())) {
        return '未记录'
      }

      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.participant-page {
  min-height: 100vh;
  padding-bottom: $jst-space-xl;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.participant-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.participant-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: $jst-radius-sm;
  background: $jst-bg-page;
  font-size: $jst-font-lg;
  color: $jst-text-secondary;
}

.participant-page__title {
  flex: 1;
  margin-left: $jst-space-md;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.participant-page__intro {
  margin: $jst-space-lg $jst-page-padding;
  padding: 28rpx $jst-page-padding;
  border-radius: $jst-radius-lg;
  background: $jst-brand-gradient;
  box-shadow: $jst-shadow-md;
}

.participant-page__intro-title {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.participant-page__intro-text {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-brand-light;
}

.participant-page__list {
  padding: 0 $jst-page-padding;
}

.participant-page__card {
  margin-bottom: $jst-space-lg;
  padding: 28rpx $jst-page-padding;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.participant-page__card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: $jst-space-md;
}

.participant-page__card-name {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.participant-page__card-id {
  display: block;
  margin-top: 10rpx;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.participant-page__meta {
  margin-top: $jst-space-lg;
}

.participant-page__meta-text {
  display: block;
  font-size: $jst-font-base;
  line-height: 1.8;
  color: $jst-text-secondary;
}

.participant-page__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $jst-space-lg;
  padding-top: 20rpx;
  border-top: 2rpx solid $jst-border;
}

.participant-page__footer-text {
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.participant-page__footer-link {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-brand;
}

.participant-page__mask {
  position: fixed;
  inset: 0;
  z-index: 99;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $jst-page-padding;
  background: rgba($jst-text-primary, 0.45);
}

.participant-page__dialog,
.participant-page__detail {
  width: 100%;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-float;
}

.participant-page__dialog {
  max-height: 85vh;
  padding: $jst-space-xl $jst-page-padding;
  overflow-y: auto;
}

.participant-page__dialog-title,
.participant-page__detail-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.participant-page__dialog-text {
  display: block;
  margin-top: $jst-space-md;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.participant-page__dialog-input {
  width: 100%;
  height: 88rpx;
  margin-top: $jst-space-lg;
  padding: 0 $jst-page-padding;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-md;
  font-size: $jst-font-base;
  color: $jst-text-primary;
  background: $jst-bg-page;
}

.participant-page__placeholder {
  color: $jst-text-placeholder;
}

.participant-page__dialog-actions {
  display: flex;
  margin-top: $jst-space-lg;
  gap: $jst-space-md;
}

.participant-page__dialog-button {
  flex: 1;
  height: 84rpx;
  border-radius: $jst-radius-md;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

.participant-page__detail {
  padding: $jst-space-xl $jst-page-padding;
}

.participant-page__detail-loading {
  margin-top: $jst-space-lg;
  font-size: $jst-font-base;
  color: $jst-text-placeholder;
}

.participant-page__detail-content {
  margin-top: $jst-space-lg;
}

.participant-page__detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 76rpx;
  border-top: 2rpx solid $jst-border;
  gap: $jst-space-md;
}

.participant-page__detail-label {
  font-size: $jst-font-base;
  color: $jst-text-placeholder;
}

.participant-page__detail-value {
  max-width: 60%;
  font-size: $jst-font-base;
  line-height: 1.6;
  color: $jst-text-primary;
  text-align: right;
}

.participant-page__detail-close {
  width: 100%;
  height: 84rpx;
  margin-top: $jst-space-xl;
  border-radius: $jst-radius-md;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

.participant-page__actions {
  display: flex;
  gap: $jst-space-sm;
}

::v-deep .participant-page__action.u-button {
  min-width: 120rpx;
  height: 62rpx;
  padding: 0 $jst-space-md;
  border: none;
  background: $jst-brand-light;
  color: $jst-brand;
}

::v-deep .participant-page__action--primary.u-button {
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
}

.participant-page__form-row {
  margin-top: $jst-space-lg;
}

.participant-page__form-label {
  display: block;
  margin-bottom: 12rpx;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.participant-page__form-required {
  margin-left: 6rpx;
  color: $jst-danger;
}

.participant-page__gender-group {
  display: flex;
  gap: $jst-space-sm;
}

.participant-page__gender-item {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  text-align: center;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-md;
  font-size: $jst-font-base;
  color: $jst-text-secondary;
  background: $jst-bg-page;
}

.participant-page__gender-item--active {
  border-color: $jst-brand;
  color: $jst-brand;
  background: $jst-brand-light;
}

.participant-page__picker {
  width: 100%;
  height: 88rpx;
  padding: 0 $jst-page-padding;
  line-height: 88rpx;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-md;
  background: $jst-bg-page;
}

.participant-page__picker-value {
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

::v-deep .participant-page__card-tag .u-tag {
  border-color: $jst-success;
  color: $jst-success;
}

::v-deep .participant-page__dialog-button.u-button {
  min-height: 84rpx;
}

::v-deep .participant-page__dialog-button--ghost.u-button {
  border-color: $jst-border;
  background: $jst-bg-page;
  color: $jst-text-secondary;
}

::v-deep .participant-page__dialog-button--primary.u-button {
  border: none;
  background: $jst-brand-gradient;
  color: $jst-text-inverse;
}

::v-deep .participant-page__detail-close.u-button {
  border-color: $jst-brand;
  background: $jst-brand-light;
  color: $jst-brand;
}
</style>
