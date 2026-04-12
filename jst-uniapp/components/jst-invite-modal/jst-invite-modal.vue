<!-- 中文注释: 邀请绑定弹窗 (E1-CH-3)
     用途: 展示渠道方二维码、海报、短链，供学生扫码绑定
     依赖: utils/qrcode-wrapper.js (D2-1b 已就绪) -->
<template>
  <u-popup :show="visible" mode="bottom" round="32" @close="handleClose">
    <view class="inv-sheet">
      <view class="inv-handle"></view>

      <!-- 顶部渠道信息 -->
      <view class="inv-header">
        <text class="inv-header__title">邀请学生绑定</text>
        <view class="inv-header__close" @tap="handleClose">✕</view>
      </view>

      <!-- 二维码卡片 -->
      <view class="inv-qr-card">
        <view class="inv-qr-card__label">👨‍🏫 渠道方专属 · 仅认证账号可见</view>
        <text class="inv-qr-card__title">我的学生绑定二维码</text>
        <view class="inv-qr-card__content">
          <view class="inv-qr-card__box">
            <canvas canvas-id="inviteQr" class="inv-qr-canvas" style="width: 180rpx; height: 180rpx;"></canvas>
          </view>
          <view class="inv-qr-card__info">
            <text class="inv-qr-card__name">{{ channelName }}</text>
            <text class="inv-qr-card__school">{{ channelSchool || '竞赛通渠道方' }}</text>
          </view>
        </view>
      </view>

      <!-- 绑定码 -->
      <view v-if="bindCode" class="inv-bind-code">
        <text class="inv-bind-code__label">学生手动输入绑定码</text>
        <text class="inv-bind-code__value">{{ bindCode }}</text>
      </view>

      <!-- 操作按钮 -->
      <view class="inv-actions">
        <view class="inv-action-btn" @tap="saveQrToAlbum">
          <text class="inv-action-btn__icon">💾</text>
          <text class="inv-action-btn__text">保存二维码</text>
        </view>
        <view class="inv-action-btn" @tap="copyLink">
          <text class="inv-action-btn__icon">🔗</text>
          <text class="inv-action-btn__text">复制链接</text>
        </view>
        <button class="inv-action-btn inv-action-btn--share" open-type="share">
          <text class="inv-action-btn__icon">📤</text>
          <text class="inv-action-btn__text">微信分享</text>
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { makeQr } from '@/utils/qrcode-wrapper'
import { useUserStore } from '@/store/user'

export default {
  name: 'jst-invite-modal',
  props: {
    visible: { type: Boolean, default: false },
    channelId: { type: [String, Number], default: '' },
    channelName: { type: String, default: '' },
    channelSchool: { type: String, default: '' },
    bindCode: { type: String, default: '' }
  },
  emits: ['close'],
  watch: {
    // 弹窗打开时渲染二维码
    visible(val) {
      if (val) {
        this.$nextTick(() => {
          this.renderQr()
        })
      }
    }
  },
  methods: {
    async renderQr() {
      const payload = `binding?channelId=${this.channelId || ''}`
      try {
        await makeQr({
          canvasId: 'inviteQr',
          text: payload,
          size: 90,
          context: this
        })
      } catch (e) {
        // 二维码渲染失败，静默处理
      }
    },
    // 保存二维码到相册
    saveQrToAlbum() {
      uni.canvasToTempFilePath({
        canvasId: 'inviteQr',
        success: (res) => {
          uni.saveImageToPhotosAlbum({
            filePath: res.tempFilePath,
            success: () => { uni.showToast({ title: '已保存到相册', icon: 'success' }) },
            fail: () => { uni.showToast({ title: '保存失败，请授权相册权限', icon: 'none' }) }
          })
        },
        fail: () => { uni.showToast({ title: '生成图片失败', icon: 'none' }) }
      }, this)
    },
    // 复制绑定链接
    copyLink() {
      const link = `binding?channelId=${this.channelId || ''}`
      uni.setClipboardData({
        data: link,
        success: () => { uni.showToast({ title: '链接已复制', icon: 'success' }) }
      })
    },
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.inv-sheet {
  width: 100%;
  max-height: 85vh;
  background: $jst-bg-card;
  overflow-y: auto;
}

.inv-handle {
  width: 72rpx;
  height: 8rpx;
  background: $jst-border;
  border-radius: $jst-radius-xs;
  margin: $jst-space-lg auto 0;
}

.inv-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg 40rpx $jst-space-md;
}

.inv-header__title {
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.inv-header__close {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: $jst-bg-page;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $jst-font-base;
  color: $jst-text-secondary;
}

/* 二维码卡片 — 设计特例，渐变色保留 */
.inv-qr-card {
  margin: $jst-space-md $jst-space-xl 0;
  padding: $jst-space-xl;
  background: linear-gradient(145deg, #1A237E 0%, #283593 50%, #3949AB 100%);
  border-radius: $jst-radius-lg;
  position: relative;
  overflow: hidden;
  box-shadow: 0 16rpx 56rpx rgba(26, 35, 126, 0.3);
}

.inv-qr-card::before {
  content: '';
  position: absolute;
  top: -60rpx;
  right: -60rpx;
  width: 280rpx;
  height: 280rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}

.inv-qr-card__label {
  display: block;
  font-size: $jst-font-xs;
  color: rgba(255, 255, 255, 0.65);
  margin-bottom: $jst-space-xs;
  position: relative;
  z-index: 1;
}

.inv-qr-card__title {
  display: block;
  font-size: $jst-font-xl;
  font-weight: 900;
  color: $jst-text-inverse;
  margin-bottom: $jst-space-lg;
  position: relative;
  z-index: 1;
}

.inv-qr-card__content {
  display: flex;
  align-items: center;
  gap: $jst-space-xl;
  position: relative;
  z-index: 1;
}

.inv-qr-card__box {
  width: 200rpx;
  height: 200rpx;
  border-radius: $jst-radius-md;
  background: $jst-bg-card;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 $jst-space-xs $jst-space-xl rgba(0, 0, 0, 0.25);
}

.inv-qr-canvas {
  width: 180rpx;
  height: 180rpx;
}

.inv-qr-card__info {
  flex: 1;
}

.inv-qr-card__name {
  display: block;
  font-size: $jst-font-lg;
  font-weight: 800;
  color: $jst-text-inverse;
}

.inv-qr-card__school {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.7);
}

/* 绑定码 */
.inv-bind-code {
  margin: $jst-space-lg $jst-space-xl 0;
  padding: $jst-space-lg;
  background: $jst-brand-light;
  border: 2rpx solid rgba(43, 108, 255, 0.15);
  border-radius: $jst-radius-md;
  text-align: center;
}

.inv-bind-code__label {
  display: block;
  font-size: $jst-font-xs;
  color: $jst-text-regular;
  margin-bottom: $jst-space-xs;
}

.inv-bind-code__value {
  display: block;
  font-size: $jst-font-xl;
  font-weight: 800;
  color: $jst-brand;
  letter-spacing: 2rpx;
}

/* 操作按钮 */
.inv-actions {
  display: flex;
  gap: $jst-space-md;
  padding: $jst-space-xl;
  padding-bottom: calc(#{$jst-space-xl} + env(safe-area-inset-bottom));
}

.inv-action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $jst-space-lg 0;
  border-radius: $jst-radius-md;
  background: $jst-bg-page;
  border: none;
  line-height: normal;
  transition: transform $jst-duration-fast $jst-easing;
}

.inv-action-btn:active {
  transform: scale(0.96);
}

.inv-action-btn--share {
  background: $jst-bg-page;
}

.inv-action-btn::after {
  display: none;
}

.inv-action-btn__icon {
  font-size: $jst-font-xl;
  margin-bottom: $jst-space-xs;
}

.inv-action-btn__text {
  font-size: $jst-font-xs;
  color: $jst-text-regular;
  font-weight: $jst-weight-medium;
}
</style>
