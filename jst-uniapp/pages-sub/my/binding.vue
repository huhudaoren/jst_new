<template>
  <view class="binding-page">
    <jst-loading :loading="pageLoading" text="绑定关系加载中..." />
    <u-skeleton v-if="pageLoading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <view class="binding-page__nav">
      <view class="binding-page__back" @tap="handleBack">返回</view>
      <text class="binding-page__nav-title">我的绑定</text>
      <view class="binding-page__nav-placeholder"></view>
    </view>

    <view class="binding-page__content">
      <view v-if="currentBinding" class="binding-page__hero">
        <text class="binding-page__hero-label">当前绑定渠道方</text>
        <view class="binding-page__hero-row">
          <view class="binding-page__avatar">{{ getAvatarText(currentBinding.channelName) }}</view>
          <view class="binding-page__hero-main">
            <text class="binding-page__hero-name">{{ currentBinding.channelName || '未命名渠道方' }}</text>
            <text class="binding-page__hero-subtitle">{{ getChannelTypeText(currentBinding.channelType) }}</text>
            <text class="binding-page__hero-time">绑定时间：{{ formatDateTime(currentBinding.bindTime) }}</text>
          </view>
        </view>

        <view class="binding-page__hero-actions">
          <u-button class="binding-page__hero-btn binding-page__hero-btn--ghost" @click="openSwitchDialog">
            更换渠道方
          </u-button>
          <u-button class="binding-page__hero-btn binding-page__hero-btn--danger" @click="confirmUnbind">
            解除绑定
          </u-button>
        </view>
      </view>

      <view v-else class="binding-page__empty">
        <text class="binding-page__empty-icon">🔗</text>
        <text class="binding-page__empty-title">当前还没有绑定渠道方</text>
        <text class="binding-page__empty-desc">绑定后，新的报名可以自动归属到老师/机构侧服务链路。</text>
        <u-button class="binding-page__empty-btn" @click="openSwitchDialog">绑定新老师</u-button>
      </view>

      <view class="binding-page__card">
        <text class="binding-page__card-title">绑定说明</text>
        <text class="binding-page__rule">1. 每位学生同一时间仅能保留一个 active 绑定。</text>
        <text class="binding-page__rule">2. 更换渠道方时，调用真实 `/binding/switch` 接口直接切换。</text>
        <text class="binding-page__rule">3. 解除绑定后，仅影响后续新报名，不清空历史记录。</text>
      </view>

      <view class="binding-page__card">
        <text class="binding-page__card-title">历史绑定记录</text>

        <view
          v-for="item in historyBindings"
          :key="item.bindingId"
          class="binding-page__history-item"
        >
          <view class="binding-page__history-avatar">{{ getAvatarText(item.channelName) }}</view>
          <view class="binding-page__history-main">
            <text class="binding-page__history-name">{{ item.channelName || '未命名渠道方' }}</text>
            <text class="binding-page__history-desc">
              {{ formatDateTime(item.bindTime) }} {{ item.unbindTime ? `至 ${formatDateTime(item.unbindTime)}` : '' }}
            </text>
          </view>
          <jst-status-badge :text="getBindingStatusText(item.status)" :tone="getBindingStatusTone(item.status)" />
        </view>

        <jst-empty
          v-if="!historyBindings.length"
          icon="📚"
          text="暂时没有历史绑定记录。"
        />
      </view>

      <view class="binding-page__card">
        <text class="binding-page__card-title">常见问题</text>
        <view class="binding-page__faq">
          <text class="binding-page__faq-title">解绑后历史报名会丢失吗？</text>
          <text class="binding-page__faq-desc">不会。解绑只影响后续新报名的归属关系，历史订单与报名记录继续保留。</text>
        </view>
        <view class="binding-page__faq">
          <text class="binding-page__faq-title">更换绑定渠道方需要先解绑吗？</text>
          <text class="binding-page__faq-desc">不需要，直接输入新的 `channelId` 即可走切换接口。</text>
        </view>
      </view>
    </view>

    <view v-if="dialogVisible" class="binding-page__mask" @tap="closeSwitchDialog">
      <view class="binding-page__dialog" @tap.stop="">
        <text class="binding-page__dialog-title">{{ currentBinding ? '更换渠道方' : '绑定新老师' }}</text>
        <text class="binding-page__dialog-desc">本期简化为手动输入 `channelId`，直接调用真实切换接口。</text>
        <input
          v-model="switchForm.channelId"
          class="binding-page__dialog-input"
          type="number"
          maxlength="12"
          placeholder="请输入渠道方 channelId"
          placeholder-class="binding-page__placeholder"
        />

        <view class="binding-page__dialog-actions">
          <u-button class="binding-page__dialog-btn binding-page__dialog-btn--ghost" @click="closeSwitchDialog">取消</u-button>
          <u-button class="binding-page__dialog-btn binding-page__dialog-btn--primary" :loading="submitLoading" @click="submitSwitch">确认绑定</u-button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyBindings, switchBinding, unbindCurrent } from '@/api/binding'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'
import JstStatusBadge from '@/components/jst-status-badge/jst-status-badge.vue'

export default {
  components: {
    JstEmpty,
    JstLoading,
    JstStatusBadge
  },
  data() {
    return {
      pageLoading: false,
      submitLoading: false,
      dialogVisible: false,
      bindings: [],
      switchForm: {
        channelId: ''
      }
    }
  },
  computed: {
    currentBinding() {
      return this.bindings.find((item) => item.status === 'active') || null
    },
    historyBindings() {
      return this.bindings.filter((item) => item.status !== 'active')
    }
  },
  onShow() {
    this.fetchBindings()
  },
  methods: {
    async fetchBindings() {
      this.pageLoading = true
      try {
        const list = await getMyBindings({ silent: true })
        this.bindings = Array.isArray(list) ? list : []
      } catch (error) {
        this.bindings = []
      } finally {
        this.pageLoading = false
      }
    },

    openSwitchDialog() {
      this.switchForm.channelId = ''
      this.dialogVisible = true
    },

    closeSwitchDialog() {
      if (this.submitLoading) {
        return
      }
      this.dialogVisible = false
    },

    async submitSwitch() {
      if (this.submitLoading) {
        return
      }

      const channelId = `${this.switchForm.channelId || ''}`.trim()
      if (!channelId) {
        uni.showToast({
          title: '请输入 channelId',
          icon: 'none'
        })
        return
      }

      this.submitLoading = true
      try {
        await switchBinding(Number(channelId), { silent: true })
        uni.showToast({
          title: '绑定已更新',
          icon: 'success'
        })
        this.dialogVisible = false
        await this.fetchBindings()
      } catch (error) {
        uni.showToast({
          title: '绑定失败',
          icon: 'none'
        })
      } finally {
        this.submitLoading = false
      }
    },

    confirmUnbind() {
      uni.showModal({
        title: '解除绑定',
        content: '确认解除当前渠道绑定？解除后只影响后续新报名。',
        success: async (res) => {
          if (!res.confirm) {
            return
          }
          try {
            await unbindCurrent('student_manual_unbind', { silent: true })
            uni.showToast({
              title: '已解除绑定',
              icon: 'success'
            })
            await this.fetchBindings()
          } catch (error) {
            uni.showToast({
              title: '解绑失败',
              icon: 'none'
            })
          }
        }
      })
    },

    handleBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.switchTab({ url: '/pages/my/index' })
    },

    getAvatarText(name) {
      return `${name || '渠道'}`.slice(0, 1)
    },

    getChannelTypeText(type) {
      const mapper = {
        teacher: '老师',
        agency: '机构',
        channel_partner: '渠道方'
      }
      return mapper[type] || '渠道方'
    },

    getBindingStatusText(status) {
      const mapper = {
        active: '当前',
        unbound: '已解绑',
        switched: '已切换'
      }
      return mapper[status] || '历史'
    },

    getBindingStatusTone(status) {
      const mapper = {
        active: 'success',
        unbound: 'gray',
        switched: 'accent'
      }
      return mapper[status] || 'gray'
    },

    formatDateTime(value) {
      if (!value) {
        return '--'
      }
      return `${value}`.slice(0, 16).replace('T', ' ')
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.binding-page {
  min-height: 100vh;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: $jst-space-lg;
}

.binding-page__nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 0 $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.binding-page__back,
.binding-page__nav-placeholder {
  width: 72rpx;
  flex-shrink: 0;
}

.binding-page__back {
  font-size: 34rpx;
  color: $jst-text-secondary;
}

.binding-page__nav-title {
  flex: 1;
  text-align: center;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.binding-page__content {
  padding: $jst-space-lg $jst-page-padding $jst-space-xl;
}

.binding-page__hero,
.binding-page__empty,
.binding-page__card {
  margin-bottom: $jst-space-lg;
  padding: 28rpx $jst-page-padding;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.binding-page__hero {
  background: linear-gradient(145deg, $jst-brand-dark 0%, $jst-brand 100%);
}

.binding-page__hero-label {
  display: block;
  font-size: $jst-font-xs;
  color: rgba($jst-text-inverse, 0.72);
}

.binding-page__hero-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-top: 18rpx;
}

.binding-page__avatar,
.binding-page__history-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $jst-radius-round;
  font-weight: $jst-weight-bold;
}

.binding-page__avatar {
  width: 96rpx;
  height: 96rpx;
  background: linear-gradient(135deg, $jst-warning, $jst-danger);
  color: $jst-text-inverse;
  font-size: $jst-font-lg;
  border: 4rpx solid rgba($jst-text-inverse, 0.28);
}

.binding-page__hero-main {
  flex: 1;
}

.binding-page__hero-name {
  display: block;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-inverse;
}

.binding-page__hero-subtitle,
.binding-page__hero-time {
  display: block;
  margin-top: 10rpx;
  font-size: $jst-font-xs;
  color: rgba($jst-text-inverse, 0.82);
}

.binding-page__hero-actions {
  display: flex;
  gap: $jst-space-sm;
  margin-top: $jst-space-xl;
}

.binding-page__hero-btn {
  flex: 1;
  height: 80rpx;
  border-radius: $jst-radius-round;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
}

.binding-page__empty {
  text-align: center;
}

.binding-page__empty-icon {
  display: block;
  font-size: 72rpx;
}

.binding-page__empty-title {
  display: block;
  margin-top: 18rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.binding-page__empty-desc {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.binding-page__empty-btn {
  margin-top: $jst-space-lg;
  border-radius: $jst-radius-round;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

.binding-page__card-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.binding-page__rule,
.binding-page__faq-desc {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.binding-page__history-item {
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  min-height: 104rpx;
  padding: 18rpx 0;
  border-top: 2rpx solid $jst-border;
}

.binding-page__history-avatar {
  width: 72rpx;
  height: 72rpx;
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  color: $jst-text-inverse;
  font-size: $jst-font-base;
}

.binding-page__history-main {
  flex: 1;
}

.binding-page__history-name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.binding-page__history-desc,
.binding-page__faq-title {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  line-height: 1.6;
  color: $jst-text-secondary;
}

.binding-page__faq {
  padding: 18rpx 0;
  border-top: 2rpx solid $jst-border;
}

.binding-page__faq:first-of-type {
  border-top: none;
  padding-top: 0;
}

.binding-page__faq-title {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.binding-page__mask {
  position: fixed;
  inset: 0;
  z-index: 99;
  display: flex;
  align-items: flex-end;
  background: rgba($jst-text-primary, 0.46);
}

.binding-page__dialog {
  width: 100%;
  padding: $jst-space-xl $jst-page-padding calc($jst-space-lg + env(safe-area-inset-bottom));
  border-radius: 32rpx 32rpx 0 0;
  background: $jst-bg-card;
}

.binding-page__dialog-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.binding-page__dialog-desc {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-sm;
  line-height: 1.7;
  color: $jst-text-secondary;
}

.binding-page__dialog-input {
  width: 100%;
  height: 92rpx;
  margin-top: 22rpx;
  padding: 0 $jst-page-padding;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-xl;
  font-size: $jst-font-base;
  color: $jst-text-primary;
  background: $jst-bg-page;
  box-sizing: border-box;
}

.binding-page__placeholder {
  color: $jst-text-placeholder;
}

.binding-page__dialog-actions {
  display: flex;
  gap: $jst-space-sm;
  margin-top: $jst-space-lg;
}

.binding-page__dialog-btn {
  flex: 1;
  height: 84rpx;
  border-radius: $jst-radius-xl;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}

::v-deep .binding-page__hero-btn.u-button {
  min-height: 80rpx;
}

::v-deep .binding-page__hero-btn--ghost.u-button {
  border-color: rgba($jst-text-inverse, 0.45);
  background: rgba($jst-text-inverse, 0.14);
  color: $jst-text-inverse;
}

::v-deep .binding-page__hero-btn--danger.u-button {
  border: none;
  background: linear-gradient(135deg, $jst-danger 0%, $jst-warning 100%);
  color: $jst-text-inverse;
}

::v-deep .binding-page__empty-btn.u-button {
  border: none;
  background: $jst-brand;
  color: $jst-text-inverse;
}

::v-deep .binding-page__dialog-btn.u-button {
  min-height: 84rpx;
}

::v-deep .binding-page__dialog-btn--ghost.u-button {
  border-color: $jst-border;
  background: $jst-bg-grey;
  color: $jst-text-secondary;
}

::v-deep .binding-page__dialog-btn--primary.u-button {
  border: none;
  background: $jst-brand;
  color: $jst-text-inverse;
}
</style>
