<!-- 中文注释: 渠道方 · 我的邀请码（SALES-DISTRIBUTION plan-02 闭环）
     调用接口:
       GET /jst/wx/channel/invite/code        邀请码 + 二维码（二维码 TODO: 后端接 OSS 小程序码后返回 qrcodeUrl）
       GET /jst/wx/channel/invite/list        下级渠道列表（用于 KPI: 累计邀请数 + 本月新增）
       GET /jst/wx/channel/distribution/summary 累计分销收益（KPI）
     规则: 1 级穿透 / 永久绑定 / 单笔分润 15% 上限 / 实付为基数 -->
<template>
  <view class="ic-page">
    <!-- Hero: 深蓝金色渐变 -->
    <view class="ic-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="ic-hero__nav">
        <view class="ic-hero__back" @tap="goBack">←</view>
        <text class="ic-hero__title">我的邀请码</text>
        <view style="width: 64rpx;" />
      </view>

      <!-- 邀请码卡片 -->
      <view class="ic-hero__card">
        <view v-if="loading" class="ic-hero__skeleton">
          <u-skeleton rows="3" :loading="true" animation="wave" />
        </view>
        <template v-else>
          <text class="ic-hero__label">我的专属邀请码</text>
          <view class="ic-hero__code" @tap="onCopyCode">
            <text class="ic-hero__code-text">{{ inviteCode || '- - -' }}</text>
            <view class="ic-hero__code-copy">
              <u-icon name="file-text" size="28" color="#FFD54F" />
              <text class="ic-hero__code-copy-text">点击复制</text>
            </view>
          </view>
          <!-- 二维码区 -->
          <view class="ic-hero__qr" v-if="qrcodeUrl">
            <image :src="qrcodeUrl" class="ic-hero__qr-img" mode="aspectFit" />
          </view>
          <view class="ic-hero__qr ic-hero__qr--placeholder" v-else>
            <u-icon name="grid-fill" size="96" color="#3949AB" />
            <text class="ic-hero__qr-tip">扫码直达渠道申请页</text>
          </view>
        </template>
      </view>
    </view>

    <!-- KPI 3 格 -->
    <view class="ic-kpi">
      <view class="ic-kpi__cell">
        <text class="ic-kpi__num">{{ kpi.totalInvited }}</text>
        <text class="ic-kpi__label">累计邀请</text>
      </view>
      <view class="ic-kpi__cell">
        <text class="ic-kpi__num ic-kpi__num--accent">{{ kpi.monthInvited }}</text>
        <text class="ic-kpi__label">本月新增</text>
      </view>
      <view class="ic-kpi__cell">
        <text class="ic-kpi__num ic-kpi__num--warning">¥{{ formatAmount(kpi.totalAmount) }}</text>
        <text class="ic-kpi__label">累计分销收益</text>
      </view>
    </view>

    <!-- 操作 CTA -->
    <view class="ic-cta">
      <u-button class="ic-cta__btn ic-cta__btn--primary" shape="circle" open-type="share">
        <u-icon name="share-fill" size="32" color="#FFFFFF" />
        <text class="ic-cta__btn-text">分享给朋友</text>
      </u-button>
      <u-button class="ic-cta__btn ic-cta__btn--outline" shape="circle" @click="onSavePoster">
        <u-icon name="photo" size="32" color="#1A237E" />
        <text class="ic-cta__btn-text ic-cta__btn-text--dark">生成邀请海报</text>
      </u-button>
    </view>

    <!-- 快捷入口 -->
    <view class="ic-shortcut">
      <view class="ic-shortcut__item" @tap="goInviteList">
        <view class="ic-shortcut__icon ic-shortcut__icon--blue">
          <u-icon name="account-fill" size="40" color="#1A237E" />
        </view>
        <view class="ic-shortcut__info">
          <text class="ic-shortcut__title">我的下级渠道</text>
          <text class="ic-shortcut__desc">查看谁通过我加入了</text>
        </view>
        <u-icon name="arrow-right" size="28" color="#9CA3AF" />
      </view>
      <view class="ic-shortcut__item" @tap="goDistribution">
        <view class="ic-shortcut__icon ic-shortcut__icon--gold">
          <u-icon name="rmb-circle-fill" size="40" color="#B45309" />
        </view>
        <view class="ic-shortcut__info">
          <text class="ic-shortcut__title">分销收益明细</text>
          <text class="ic-shortcut__desc">查看每笔订单的分润</text>
        </view>
        <u-icon name="arrow-right" size="28" color="#9CA3AF" />
      </view>
    </view>

    <!-- 分销规则（可折叠） -->
    <view class="ic-rules">
      <view class="ic-rules__head" @tap="rulesExpanded = !rulesExpanded">
        <text class="ic-rules__title">分销规则说明</text>
        <u-icon :name="rulesExpanded ? 'arrow-up' : 'arrow-down'" size="28" color="#6B7280" />
      </view>
      <view v-if="rulesExpanded" class="ic-rules__body">
        <view class="ic-rules__item">
          <view class="ic-rules__badge">1</view>
          <view class="ic-rules__content">
            <text class="ic-rules__item-title">1 级穿透</text>
            <text class="ic-rules__item-desc">仅你直接邀请的下级产生分润，不跨级</text>
          </view>
        </view>
        <view class="ic-rules__item">
          <view class="ic-rules__badge">2</view>
          <view class="ic-rules__content">
            <text class="ic-rules__item-title">永久绑定</text>
            <text class="ic-rules__item-desc">下级注册后与你永久绑定，持续享受分销</text>
          </view>
        </view>
        <view class="ic-rules__item">
          <view class="ic-rules__badge">3</view>
          <view class="ic-rules__content">
            <text class="ic-rules__item-title">实付为基数</text>
            <text class="ic-rules__item-desc">按下级订单实际支付金额计算（扣除积分/优惠券抵扣后）</text>
          </view>
        </view>
        <view class="ic-rules__item">
          <view class="ic-rules__badge">4</view>
          <view class="ic-rules__content">
            <text class="ic-rules__item-title">单笔 15% 上限</text>
            <text class="ic-rules__item-desc">单笔分润最高 15%，与渠道返点协同压缩，防止套利</text>
          </view>
        </view>
        <view class="ic-rules__item">
          <view class="ic-rules__badge">5</view>
          <view class="ic-rules__content">
            <text class="ic-rules__item-title">T+7 到账</text>
            <text class="ic-rules__item-desc">下级订单付款后 7 天售后期结束自动入账，退款则撤销</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyInviteCode, listInvited, getDistributionSummary } from '@/api/invite'

export default {
  data() {
    return {
      inviteCode: '',
      qrcodeUrl: null,
      loading: true,
      rulesExpanded: true,
      kpi: { totalInvited: 0, monthInvited: 0, totalAmount: '0.00' }
    }
  },
  computed: {
    navPaddingTop() {
      const si = uni.getSystemInfoSync()
      return (si.statusBarHeight || 20) + 'px'
    }
  },
  onLoad() {
    this.loadAll()
  },
  onPullDownRefresh() {
    this.loadAll().finally(() => uni.stopPullDownRefresh())
  },
  onShareAppMessage() {
    return {
      title: '加入竞赛通渠道，一起推广赛事赚分销收益！',
      path: `/pages-sub/channel/apply-entry?invite_code=${this.inviteCode}`,
      imageUrl: ''
    }
  },
  methods: {
    goBack() {
      if (getCurrentPages().length > 1) uni.navigateBack()
      else uni.switchTab({ url: '/pages/my/index' })
    },
    async loadAll() {
      await Promise.all([this.loadCode(), this.loadKpi()])
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
    async loadKpi() {
      try {
        const [listRes, sumRes] = await Promise.all([
          listInvited().catch(() => []),
          getDistributionSummary().catch(() => null)
        ])
        const list = Array.isArray(listRes) ? listRes : (listRes && listRes.rows) || []
        const now = new Date()
        const ym = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0')
        this.kpi.totalInvited = list.length
        this.kpi.monthInvited = list.filter(i => {
          const t = i.invitedAt || i.createTime
          return t && String(t).startsWith(ym)
        }).length
        this.kpi.totalAmount = (sumRes && sumRes.totalAmount) || '0.00'
      } catch (e) {}
    },
    onCopyCode() {
      if (!this.inviteCode) return
      uni.setClipboardData({
        data: this.inviteCode,
        success: () => uni.showToast({ title: '邀请码已复制', icon: 'success' })
      })
    },
    onSavePoster() {
      // TODO: 海报生成依赖后端提供 OSS 小程序码（qrcodeUrl），先占位提示
      uni.showToast({ title: '海报功能开发中，敬请期待', icon: 'none', duration: 2000 })
    },
    goInviteList() {
      uni.navigateTo({ url: '/pages-sub/channel/invite-list' })
    },
    goDistribution() {
      uni.navigateTo({ url: '/pages-sub/channel/distribution' })
    },
    formatAmount(v) {
      if (v == null || v === '') return '0.00'
      return Number(v).toFixed(2)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ic-page { min-height: 100vh; padding-bottom: 80rpx; background: $jst-bg-page; }

/* Hero 深蓝金色渐变 */
.ic-hero {
  padding: 0 $jst-space-xl 96rpx;
  background: linear-gradient(150deg, $jst-indigo 0%, $jst-indigo-light 55%, $jst-indigo 120%);
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
}
.ic-hero__nav {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 24rpx; margin-bottom: $jst-space-xl;
}
.ic-hero__back {
  width: 64rpx; height: 64rpx; border-radius: $jst-radius-sm;
  background: rgba(255, 255, 255, 0.18); color: $jst-text-inverse;
  text-align: center; line-height: 64rpx; font-size: 36rpx;
}
.ic-hero__title { font-size: 34rpx; font-weight: 600; color: $jst-text-inverse; }

.ic-hero__card {
  margin-top: $jst-space-lg;
  padding: 40rpx 32rpx;
  background: rgba(255, 255, 255, 0.12);
  border: 2rpx solid rgba(255, 213, 79, 0.35);
  border-radius: $jst-radius-lg;
  backdrop-filter: blur(12px);
  text-align: center;
}
.ic-hero__skeleton { min-height: 240rpx; }
.ic-hero__label {
  display: block; font-size: $jst-font-sm;
  color: rgba(255, 255, 255, 0.78);
  margin-bottom: 16rpx;
}
.ic-hero__code {
  display: flex; flex-direction: column; align-items: center;
  padding: $jst-space-md 0;
}
.ic-hero__code-text {
  display: block; font-size: 72rpx; font-weight: 700;
  letter-spacing: 12rpx; color: $jst-gold;
  text-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.3);
}
.ic-hero__code-copy {
  display: flex; align-items: center; gap: 8rpx;
  margin-top: $jst-space-sm; padding: 8rpx 24rpx;
  background: rgba(255, 213, 79, 0.22);
  border-radius: $jst-radius-round;
}
.ic-hero__code-copy-text { font-size: 22rpx; color: $jst-gold; font-weight: 600; }
.ic-hero__qr {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  margin: $jst-space-lg auto 0; width: 260rpx; height: 260rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: $jst-radius-md;
}
.ic-hero__qr-img { width: 240rpx; height: 240rpx; }
.ic-hero__qr--placeholder { gap: $jst-space-xs; }
.ic-hero__qr-tip { font-size: 22rpx; color: $jst-indigo; font-weight: 500; }

/* KPI 3 格 */
.ic-kpi {
  display: flex; margin: -64rpx $jst-space-xl 0;
  padding: $jst-space-lg $jst-space-md;
  background: $jst-bg-card; border-radius: $jst-radius-lg;
  box-shadow: $jst-shadow-md;
}
.ic-kpi__cell {
  flex: 1; text-align: center;
  border-right: 2rpx solid $jst-border;
  &:last-child { border-right: 0; }
}
.ic-kpi__num {
  display: block; font-size: 40rpx; font-weight: 700;
  color: $jst-text-primary;
}
.ic-kpi__num--accent { color: $jst-indigo; }
.ic-kpi__num--warning { color: $jst-warning; font-size: 36rpx; }
.ic-kpi__label {
  display: block; margin-top: 8rpx;
  font-size: 22rpx; color: $jst-text-secondary;
}

/* 操作 CTA */
.ic-cta {
  display: flex; gap: $jst-space-md;
  margin: $jst-space-xl $jst-space-xl 0;
}
::v-deep .ic-cta__btn.u-button {
  flex: 1; height: 96rpx; padding: 0 16rpx;
  border: none; font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
}
::v-deep .ic-cta__btn--primary.u-button {
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  color: $jst-text-inverse;
}
::v-deep .ic-cta__btn--outline.u-button {
  background: $jst-bg-card;
  border: 2rpx solid $jst-indigo;
  color: $jst-indigo;
}
.ic-cta__btn-text { margin-left: 10rpx; color: $jst-text-inverse; }
.ic-cta__btn-text--dark { color: $jst-indigo; }

/* 快捷入口 */
.ic-shortcut {
  margin: $jst-space-lg $jst-space-xl 0;
  background: $jst-bg-card; border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm; overflow: hidden;
}
.ic-shortcut__item {
  display: flex; align-items: center; gap: $jst-space-md;
  padding: $jst-space-lg;
  border-bottom: 2rpx solid $jst-border;
  &:last-child { border-bottom: 0; }
}
.ic-shortcut__icon {
  width: 72rpx; height: 72rpx; border-radius: $jst-radius-round;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.ic-shortcut__icon--blue { background: rgba(26, 35, 126, 0.1); }
.ic-shortcut__icon--gold { background: rgba(255, 213, 79, 0.2); }
.ic-shortcut__info { flex: 1; min-width: 0; }
.ic-shortcut__title {
  display: block; font-size: $jst-font-base;
  font-weight: $jst-weight-semibold; color: $jst-text-primary;
}
.ic-shortcut__desc {
  display: block; margin-top: 4rpx;
  font-size: $jst-font-xs; color: $jst-text-secondary;
}

/* 规则（可折叠） */
.ic-rules {
  margin: $jst-space-lg $jst-space-xl 0;
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
}
.ic-rules__head {
  display: flex; align-items: center; justify-content: space-between;
}
.ic-rules__title {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}
.ic-rules__body {
  margin-top: $jst-space-md;
  padding-top: $jst-space-md;
  border-top: 2rpx dashed $jst-border;
}
.ic-rules__item {
  display: flex; gap: $jst-space-md;
  padding: $jst-space-sm 0;
}
.ic-rules__badge {
  flex-shrink: 0;
  width: 44rpx; height: 44rpx;
  border-radius: $jst-radius-round;
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  color: $jst-text-inverse;
  text-align: center; line-height: 44rpx;
  font-size: 22rpx; font-weight: 700;
}
.ic-rules__content { flex: 1; }
.ic-rules__item-title {
  display: block; font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: 4rpx;
}
.ic-rules__item-desc {
  display: block; font-size: $jst-font-xs;
  color: $jst-text-secondary; line-height: 1.6;
}
</style>
