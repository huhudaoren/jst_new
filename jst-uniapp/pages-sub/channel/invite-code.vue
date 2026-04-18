<!-- 中文注释: 渠道方 · 我的邀请码
     调用接口: GET /jst/wx/channel/invite/code -->
<template>
  <view class="ic-page">
    <!-- 顶部 hero -->
    <view class="ic-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="ic-hero__nav">
        <view class="ic-hero__back" @tap="goBack">←</view>
        <text class="ic-hero__title">我的邀请码</text>
        <view style="width: 64rpx;" />
      </view>
    </view>

    <!-- 邀请码卡片 -->
    <view class="ic-card">
      <view v-if="loading" class="ic-card__skeleton">
        <u-skeleton rows="3" :loading="true" animation="wave" />
      </view>
      <template v-else>
        <text class="ic-card__label">我的邀请码</text>
        <text class="ic-card__code">{{ inviteCode || '--' }}</text>
        <!-- 二维码区 -->
        <view class="ic-card__qr" v-if="qrcodeUrl">
          <image :src="qrcodeUrl" class="ic-card__qr-img" mode="aspectFit" />
        </view>
        <view class="ic-card__qr ic-card__qr--placeholder" v-else>
          <text class="ic-card__qr-tip">扫码即可注册成为我的下级渠道</text>
        </view>
        <!-- 操作按钮 -->
        <view class="ic-actions">
          <u-button class="ic-actions__btn" type="primary" shape="circle" @click="onShare">微信分享</u-button>
          <u-button class="ic-actions__btn ic-actions__btn--outline" plain shape="circle" @click="onCopyLink">复制邀请链接</u-button>
        </view>
      </template>
    </view>

    <!-- 分销规则 -->
    <view class="ic-rules">
      <text class="ic-rules__title">分销规则</text>
      <view class="ic-rules__item">
        <text class="ic-rules__dot">·</text>
        <text class="ic-rules__text">邀请新渠道通过你的邀请码注册，成为你的下级渠道</text>
      </view>
      <view class="ic-rules__item">
        <text class="ic-rules__dot">·</text>
        <text class="ic-rules__text">下级渠道每完成一笔订单，你将按约定比例获得分销收益</text>
      </view>
      <view class="ic-rules__item">
        <text class="ic-rules__dot">·</text>
        <text class="ic-rules__text">分销收益累积到账后可查看「分销收益」页面</text>
      </view>
      <view class="ic-rules__item">
        <text class="ic-rules__dot">·</text>
        <text class="ic-rules__text">邀请码为你的专属码，请妥善保管，不要随意转让</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyInviteCode } from '@/api/invite'

export default {
  data() {
    return {
      inviteCode: '',
      qrcodeUrl: null,
      loading: true
    }
  },
  computed: {
    navPaddingTop() {
      const si = uni.getSystemInfoSync()
      return (si.statusBarHeight || 20) + 'px'
    }
  },
  onLoad() {
    this.loadCode()
  },
  onShareAppMessage() {
    return {
      title: '加入竞赛通渠道，一起推广赛事赚收益！',
      path: `/pages-sub/channel/apply-entry?invite_code=${this.inviteCode}`
    }
  },
  methods: {
    goBack() {
      if (getCurrentPages().length > 1) uni.navigateBack()
      else uni.switchTab({ url: '/pages/my/index' })
    },
    async loadCode() {
      this.loading = true
      try {
        const data = await getMyInviteCode()
        this.inviteCode = (data && data.inviteCode) || ''
        this.qrcodeUrl = (data && data.qrcodeUrl) || null
      } catch (e) {
        uni.showToast({ title: '获取邀请码失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    onShare() {
      uni.showShareMenu({ withShareTicket: true })
    },
    onCopyLink() {
      if (!this.inviteCode) return
      uni.setClipboardData({
        data: `我正在使用竞赛通，用我的邀请码 ${this.inviteCode} 申请成为渠道方，一起推广赛事赚收益！`,
        success: () => uni.showToast({ title: '已复制邀请链接', icon: 'success' })
      })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ic-page { min-height: 100vh; padding-bottom: 60rpx; background: $jst-bg-page; }

.ic-hero {
  padding: 0 $jst-space-xl 40rpx;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
}
.ic-hero__nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 24rpx;
}
.ic-hero__back {
  width: 64rpx;
  height: 64rpx;
  border-radius: $jst-radius-sm;
  background: rgba(255, 255, 255, 0.18);
  color: $jst-text-inverse;
  text-align: center;
  line-height: 64rpx;
  font-size: 36rpx;
}
.ic-hero__title {
  font-size: 34rpx;
  font-weight: 600;
  color: $jst-text-inverse;
}

.ic-card {
  margin: -24rpx $jst-space-xl 0;
  padding: $jst-space-xl;
  background: $jst-bg-card;
  border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-md;
  text-align: center;
}
.ic-card__skeleton { min-height: 200rpx; }
.ic-card__label {
  display: block;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  margin-bottom: 16rpx;
}
.ic-card__code {
  display: block;
  font-size: 72rpx;
  font-weight: 700;
  letter-spacing: 12rpx;
  color: $jst-brand;
  margin-bottom: $jst-space-xl;
}
.ic-card__qr {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto $jst-space-xl;
  width: 280rpx;
  height: 280rpx;
  border: 2rpx solid $jst-border;
  border-radius: $jst-radius-md;
}
.ic-card__qr-img { width: 260rpx; height: 260rpx; }
.ic-card__qr--placeholder { background: $jst-bg-grey; }
.ic-card__qr-tip { font-size: $jst-font-sm; color: $jst-text-secondary; padding: 0 $jst-space-md; text-align: center; line-height: 1.6; }

.ic-actions { display: flex; flex-direction: column; gap: $jst-space-md; }
::v-deep .ic-actions__btn.u-button {
  height: 88rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border: none;
}
::v-deep .ic-actions__btn--outline.u-button {
  background: transparent;
  border: 2rpx solid $jst-brand;
  color: $jst-brand;
}

.ic-rules {
  margin: $jst-space-xl $jst-space-xl 0;
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
}
.ic-rules__title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: $jst-space-md;
}
.ic-rules__item { display: flex; gap: $jst-space-xs; margin-bottom: $jst-space-sm; }
.ic-rules__dot { color: $jst-brand; font-size: $jst-font-base; flex-shrink: 0; }
.ic-rules__text { font-size: $jst-font-sm; color: $jst-text-secondary; line-height: 1.7; }
</style>
