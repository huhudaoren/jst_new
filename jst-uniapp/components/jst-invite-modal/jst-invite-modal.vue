<!-- 中文注释: 邀请绑定弹窗 (E1-CH-3)
     用途: 展示渠道方二维码、海报、短链，供学生扫码绑定
     依赖: utils/qrcode-wrapper.js (D2-1b 已就绪) -->
<template>
  <view v-if="visible" class="inv-mask" @tap.stop="handleClose">
    <view class="inv-sheet" @tap.stop>
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
  </view>
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
.inv-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: var(--jst-color-mask-dark); z-index: 200; display: flex; align-items: flex-end; justify-content: center; }
.inv-sheet { width: 100%; max-height: 85vh; background: var(--jst-color-card-bg); border-radius: 32rpx 32rpx 0 0; overflow-y: auto; animation: inv-slide-up 0.3s ease; }
@keyframes inv-slide-up { from { transform: translateY(100%); } to { transform: translateY(0); } }
.inv-handle { width: 72rpx; height: 8rpx; background: var(--jst-color-border); border-radius: 4rpx; margin: 24rpx auto 0; }
.inv-header { display: flex; align-items: center; justify-content: space-between; padding: 24rpx 40rpx 16rpx; }
.inv-header__title { font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); }
.inv-header__close { width: 56rpx; height: 56rpx; border-radius: 50%; background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 28rpx; color: var(--jst-color-text-tertiary); }

/* 二维码卡片 */
.inv-qr-card { margin: 16rpx 32rpx 0; padding: 32rpx; background: linear-gradient(145deg, #1A237E 0%, #283593 50%, #3949AB 100%); border-radius: var(--jst-radius-lg); position: relative; overflow: hidden; box-shadow: 0 16rpx 56rpx rgba(26,35,126,0.3); }
.inv-qr-card::before { content: ''; position: absolute; top: -60rpx; right: -60rpx; width: 280rpx; height: 280rpx; border-radius: 50%; background: rgba(255,255,255,0.06); }
.inv-qr-card__label { display: block; font-size: 22rpx; color: rgba(255,255,255,0.65); margin-bottom: 8rpx; position: relative; z-index: 1; }
.inv-qr-card__title { display: block; font-size: 36rpx; font-weight: 900; color: #fff; margin-bottom: 24rpx; position: relative; z-index: 1; }
.inv-qr-card__content { display: flex; align-items: center; gap: 32rpx; position: relative; z-index: 1; }
.inv-qr-card__box { width: 200rpx; height: 200rpx; border-radius: var(--jst-radius-md); background: #fff; display: flex; align-items: center; justify-content: center; box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.25); }
.inv-qr-canvas { width: 180rpx; height: 180rpx; }
.inv-qr-card__info { flex: 1; }
.inv-qr-card__name { display: block; font-size: 32rpx; font-weight: 800; color: #fff; }
.inv-qr-card__school { display: block; margin-top: 8rpx; font-size: 24rpx; color: rgba(255,255,255,0.7); }

/* 绑定码 */
.inv-bind-code { margin: 24rpx 32rpx 0; padding: 24rpx; background: var(--jst-color-brand-soft); border: 2rpx solid rgba(16,88,160,0.15); border-radius: var(--jst-radius-md); text-align: center; }
.inv-bind-code__label { display: block; font-size: 22rpx; color: var(--jst-color-text-secondary); margin-bottom: 8rpx; }
.inv-bind-code__value { display: block; font-size: 36rpx; font-weight: 800; color: var(--jst-color-brand); letter-spacing: 2rpx; }

/* 操作按钮 */
.inv-actions { display: flex; gap: 16rpx; padding: 32rpx 32rpx calc(32rpx + env(safe-area-inset-bottom)); }
.inv-action-btn { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24rpx 0; border-radius: var(--jst-radius-md); background: var(--jst-color-page-bg); border: none; line-height: normal; }
.inv-action-btn--share { background: var(--jst-color-page-bg); }
.inv-action-btn::after { display: none; }
.inv-action-btn__icon { font-size: 36rpx; margin-bottom: 8rpx; }
.inv-action-btn__text { font-size: 22rpx; color: var(--jst-color-text-secondary); font-weight: 500; }
</style>
