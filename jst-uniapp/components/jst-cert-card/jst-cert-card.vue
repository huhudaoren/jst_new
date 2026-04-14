<!-- jst-cert-card: 证书大卡组件，模拟证书样式 + 操作按钮 -->
<template>
  <view class="jst-cert-card jst-anim-lift" :class="'jst-cert-card--' + awardTheme">
    <!-- 证书预览区 -->
    <view class="jst-cert-card__preview">
      <view class="jst-cert-card__border"></view>
      <view class="jst-cert-card__inner">
        <text class="jst-cert-card__cert-title">{{ certTitle }}</text>
        <text class="jst-cert-card__holder">{{ cert.holderName || '--' }}</text>
        <text class="jst-cert-card__contest">{{ cert.contestName || '--' }}</text>
        <text class="jst-cert-card__award">{{ cert.awardLevel || '--' }}</text>
      </view>
      <view class="jst-cert-card__badge-wrap">
        <text class="jst-cert-card__badge">{{ awardIcon }}</text>
      </view>
    </view>

    <!-- 证书信息 -->
    <view class="jst-cert-card__info">
      <text class="jst-cert-card__name">{{ cert.contestName }} · {{ cert.certName || '获奖证书' }}</text>
      <view class="jst-cert-card__meta">
        <u-tag :text="cert.awardLevel || '获奖'" :type="tagType" plain size="mini" shape="circle" />
        <text class="jst-cert-card__no">{{ cert.certNo || '--' }}</text>
      </view>
    </view>

    <!-- 操作栏 -->
    <view class="jst-cert-card__actions">
      <view class="jst-cert-card__action" @tap="$emit('verify', cert)">
        <text class="jst-cert-card__action-icon">&#128269;</text>
        <text class="jst-cert-card__action-text">验真</text>
      </view>
      <view class="jst-cert-card__action" @tap="$emit('download', cert)">
        <text class="jst-cert-card__action-icon">&#11015;</text>
        <text class="jst-cert-card__action-text">下载</text>
      </view>
      <view class="jst-cert-card__action jst-cert-card__action--primary" @tap="$emit('view', cert)">
        <text class="jst-cert-card__action-text">查看</text>
      </view>
    </view>
  </view>
</template>

<script>
var AWARD_MAP = {
  gold: { icon: '🥇', theme: 'gold', tag: 'warning' },
  silver: { icon: '🥈', theme: 'silver', tag: 'primary' },
  bronze: { icon: '🥉', theme: 'bronze', tag: 'info' }
}

function getAwardKey(level) {
  if (!level) return 'default'
  var l = level.toLowerCase()
  if (l.indexOf('一等') !== -1 || l.indexOf('金') !== -1 || l.indexOf('first') !== -1 || l.indexOf('gold') !== -1) return 'gold'
  if (l.indexOf('二等') !== -1 || l.indexOf('银') !== -1 || l.indexOf('second') !== -1 || l.indexOf('silver') !== -1) return 'silver'
  if (l.indexOf('三等') !== -1 || l.indexOf('铜') !== -1 || l.indexOf('third') !== -1 || l.indexOf('bronze') !== -1) return 'bronze'
  return 'default'
}

export default {
  name: 'JstCertCard',
  props: {
    cert: { type: Object, default: function() { return {} } },
    certTitle: { type: String, default: '获 奖 证 书' }
  },
  computed: {
    awardKey: function() { return getAwardKey(this.cert.awardLevel) },
    awardTheme: function() { return (AWARD_MAP[this.awardKey] || {}).theme || 'default' },
    awardIcon: function() { return (AWARD_MAP[this.awardKey] || {}).icon || '🏅' },
    tagType: function() { return (AWARD_MAP[this.awardKey] || {}).tag || 'info' }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-cert-card {
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient, $jst-shadow-lift;
  overflow: hidden;
  margin-bottom: $jst-space-lg;
}

.jst-cert-card__preview {
  position: relative;
  padding: $jst-space-xl $jst-space-lg;
  background: linear-gradient(135deg, #FAFBFD 0%, #F0F3F8 100%);
  min-height: 260rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.jst-cert-card--gold .jst-cert-card__preview {
  background: linear-gradient(135deg, #FFFDF5 0%, #FFF7E0 100%);
}

.jst-cert-card--silver .jst-cert-card__preview {
  background: linear-gradient(135deg, #F8FAFB 0%, #E8EEF2 100%);
}

.jst-cert-card--bronze .jst-cert-card__preview {
  background: linear-gradient(135deg, #FDF8F3 0%, #F5EAE0 100%);
}

.jst-cert-card__border {
  position: absolute;
  top: 12rpx;
  left: 12rpx;
  right: 12rpx;
  bottom: 12rpx;
  border: 2rpx solid rgba(0, 0, 0, 0.06);
  border-radius: $jst-radius-md;
  pointer-events: none;
}

.jst-cert-card--gold .jst-cert-card__border {
  border-color: rgba(255, 213, 79, 0.4);
}

.jst-cert-card__inner {
  text-align: center;
  z-index: 1;
}

.jst-cert-card__cert-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  letter-spacing: 8rpx;
  margin-bottom: $jst-space-md;
}

.jst-cert-card__holder {
  display: block;
  font-size: $jst-font-xl;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  margin-bottom: $jst-space-sm;
}

.jst-cert-card__contest,
.jst-cert-card__award {
  display: block;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  line-height: 1.8;
}

.jst-cert-card__badge-wrap {
  position: absolute;
  top: $jst-space-lg;
  right: $jst-space-lg;
}

.jst-cert-card__badge {
  font-size: 48rpx;
}

.jst-cert-card__info {
  padding: $jst-space-lg;
}

.jst-cert-card__name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
  margin-bottom: $jst-space-sm;
}

.jst-cert-card__meta {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
}

.jst-cert-card__no {
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.jst-cert-card__actions {
  display: flex;
  border-top: 1rpx solid $jst-border;
}

.jst-cert-card__action {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  height: 88rpx;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  transition: background $jst-duration-fast;
  &:active {
    background: $jst-bg-grey;
  }
}

.jst-cert-card__action--primary {
  color: $jst-brand;
  font-weight: $jst-weight-medium;
}

.jst-cert-card__action-icon {
  font-size: $jst-font-base;
}

.jst-cert-card__action-text {
  font-size: $jst-font-sm;
}
</style>
