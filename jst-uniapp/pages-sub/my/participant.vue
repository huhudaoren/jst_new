<!-- 中文注释: 我的档案页；无独立原型，卡片样式参考 小程序原型图/my-binding.html；对应接口 GET /jst/wx/participant/my、POST /jst/wx/participant/claim、GET /jst/wx/participant/{id} -->
<template>
  <view class="participant-page">
    <jst-loading :loading="pageLoading" text="档案加载中..." />

    <view class="participant-page__header">
      <view class="participant-page__back" @tap="handleBack">←</view>
      <text class="participant-page__title">我的档案</text>
      <view class="participant-page__action" @tap="openClaimDialog">+ 认领新档案</view>
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
          <text class="participant-page__card-tag">{{ getClaimMethodText(item.claimMethod) }}</text>
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
          <button class="participant-page__dialog-button participant-page__dialog-button--ghost" @tap="closeClaimDialog">
            取消
          </button>
          <button
            class="participant-page__dialog-button participant-page__dialog-button--primary"
            :loading="claimSubmitting"
            @tap="submitClaim"
          >
            确认认领
          </button>
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
        <button class="participant-page__detail-close" @tap="closeDetail">关闭</button>
      </view>
    </view>
  </view>
</template>

<script>
import { claimParticipant, getParticipantDetail, myParticipants } from '@/api/participant'
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
      detailVisible: false,
      detailLoading: false,
      currentParticipant: {},
      detail: {}
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
        manual_user: '主动认领'
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
.participant-page {
  min-height: 100vh;
  padding-bottom: 40rpx;
  background: var(--jst-color-page-bg);
}

.participant-page__header {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 24rpx;
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.participant-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: var(--jst-radius-sm);
  background: var(--jst-color-page-bg);
  font-size: 32rpx;
  color: var(--jst-color-text-secondary);
}

.participant-page__title {
  flex: 1;
  margin-left: 16rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.participant-page__action {
  padding: 14rpx 24rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-primary-soft);
  font-size: 24rpx;
  font-weight: 600;
  color: var(--jst-color-primary);
}

.participant-page__intro {
  margin: 24rpx;
  padding: 28rpx 24rpx;
  border-radius: var(--jst-radius-md);
  background: linear-gradient(135deg, var(--jst-color-brand) 0%, var(--jst-color-brand-light) 100%);
  box-shadow: var(--jst-shadow-card);
}

.participant-page__intro-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--jst-color-card-bg);
}

.participant-page__intro-text {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--jst-color-white-78);
}

.participant-page__list {
  padding: 0 24rpx;
}

.participant-page__card {
  margin-bottom: 24rpx;
  padding: 28rpx 24rpx;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-card);
}

.participant-page__card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.participant-page__card-name {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.participant-page__card-id {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
}

.participant-page__card-tag {
  padding: 10rpx 18rpx;
  border-radius: var(--jst-radius-full);
  background: var(--jst-color-success-soft);
  font-size: 22rpx;
  color: var(--jst-color-success);
}

.participant-page__meta {
  margin-top: 24rpx;
}

.participant-page__meta-text {
  display: block;
  font-size: 26rpx;
  line-height: 1.8;
  color: var(--jst-color-text-secondary);
}

.participant-page__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid var(--jst-color-border);
}

.participant-page__footer-text {
  font-size: 24rpx;
  color: var(--jst-color-text-tertiary);
}

.participant-page__footer-link {
  font-size: 24rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}

.participant-page__mask {
  position: fixed;
  inset: 0;
  z-index: 99;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24rpx;
  background: var(--jst-color-mask-dark);
}

.participant-page__dialog,
.participant-page__detail {
  width: 100%;
  border-radius: var(--jst-radius-lg);
  background: var(--jst-color-card-bg);
  box-shadow: var(--jst-shadow-strong);
}

.participant-page__dialog {
  padding: 32rpx 24rpx;
}

.participant-page__dialog-title,
.participant-page__detail-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--jst-color-text);
}

.participant-page__dialog-text {
  display: block;
  margin-top: 16rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: var(--jst-color-text-secondary);
}

.participant-page__dialog-input {
  width: 100%;
  height: 88rpx;
  margin-top: 24rpx;
  padding: 0 24rpx;
  border: 2rpx solid var(--jst-color-border);
  border-radius: var(--jst-radius-md);
  font-size: 28rpx;
  color: var(--jst-color-text);
  background: var(--jst-color-page-bg);
}

.participant-page__placeholder {
  color: var(--jst-color-text-tertiary);
}

.participant-page__dialog-actions {
  display: flex;
  margin-top: 24rpx;
}

.participant-page__dialog-button {
  flex: 1;
  height: 84rpx;
  border-radius: var(--jst-radius-md);
  font-size: 28rpx;
  font-weight: 600;
}

.participant-page__dialog-button + .participant-page__dialog-button {
  margin-left: 16rpx;
}

.participant-page__dialog-button--ghost {
  background: var(--jst-color-page-bg);
  color: var(--jst-color-text-secondary);
}

.participant-page__dialog-button--primary {
  background: linear-gradient(135deg, var(--jst-color-primary) 0%, var(--jst-color-primary-light) 100%);
  color: var(--jst-color-card-bg);
}

.participant-page__detail {
  padding: 32rpx 24rpx;
}

.participant-page__detail-loading {
  margin-top: 24rpx;
  font-size: 26rpx;
  color: var(--jst-color-text-tertiary);
}

.participant-page__detail-content {
  margin-top: 24rpx;
}

.participant-page__detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 76rpx;
  border-top: 2rpx solid var(--jst-color-border);
}

.participant-page__detail-label {
  font-size: 26rpx;
  color: var(--jst-color-text-tertiary);
}

.participant-page__detail-value {
  max-width: 60%;
  font-size: 26rpx;
  line-height: 1.6;
  color: var(--jst-color-text);
  text-align: right;
}

.participant-page__detail-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 84rpx;
  margin-top: 28rpx;
  border-radius: var(--jst-radius-md);
  background: var(--jst-color-brand-soft);
  font-size: 28rpx;
  font-weight: 600;
  color: var(--jst-color-brand);
}
</style>
