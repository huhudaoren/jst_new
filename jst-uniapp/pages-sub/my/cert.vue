<!-- 中文注释: 我的证书页；对应原型 小程序原型图/my-cert.html；对应接口 GET /jst/wx/cert/my -->
<template>
  <view class="cert-page">
    <view class="cert-page__header">
      <view class="cert-page__back" @tap="goBack">←</view>
      <text class="cert-page__header-title">我的证书</text>
    </view>

    <jst-loading :loading="loading" text="证书加载中..." />
    <u-skeleton v-if="loading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

    <!-- 统计 -->
    <view v-if="certList.length" class="cert-page__stats">
      <view class="cert-page__stat-item">
        <text class="cert-page__stat-num cert-page__stat-num--brand">{{ certList.length }}</text>
        <text class="cert-page__stat-label">证书数量</text>
      </view>
      <view class="cert-page__stat-item">
        <text class="cert-page__stat-num cert-page__stat-num--gold">{{ firstPrizeCount }}</text>
        <text class="cert-page__stat-label">一等奖</text>
      </view>
      <view class="cert-page__stat-item">
        <text class="cert-page__stat-num cert-page__stat-num--success">✓</text>
        <text class="cert-page__stat-label">均已验真</text>
      </view>
    </view>

    <!-- 证书列表 -->
    <view v-if="certList.length" class="cert-page__list">
      <view v-for="item in certList" :key="item.certId" class="cert-page__card">
        <!-- 证书预览区 -->
        <view class="cert-page__card-preview" :class="'cert-page__card-preview--' + getAwardTheme(item.awardLevel)">
          <view class="cert-page__card-mock">
            <text class="cert-page__card-mock-title">获 奖 证 书</text>
            <text class="cert-page__card-mock-name">{{ item.holderName || '—' }}</text>
            <text class="cert-page__card-mock-contest">在{{ item.contestName || '—' }}{{ '\n' }}荣获 {{ item.awardLevel || '—' }}</text>
          </view>
          <text class="cert-page__card-badge">{{ getAwardIcon(item.awardLevel) }}</text>
        </view>

        <!-- 证书信息 -->
        <view class="cert-page__card-info">
          <text class="cert-page__card-name">{{ item.contestName }} · {{ item.certName || '获奖证书' }}</text>
          <view class="cert-page__card-tags">
            <u-tag class="cert-page__card-tag cert-page__card-tag--award" :text="`${getAwardIcon(item.awardLevel)} ${item.awardLevel}`" plain />
            <u-tag v-if="item.category" class="cert-page__card-tag" :text="item.category" plain />
          </view>
          <text class="cert-page__card-no">证书编号：{{ item.certNo || '—' }} · 发证：{{ formatDate(item.issueDate) }}</text>
        </view>

        <!-- 操作 -->
        <view class="cert-page__card-actions">
          <u-button class="cert-page__card-action" @click="verifyCert(item)">🔍 验真</u-button>
          <u-button class="cert-page__card-action" @click="downloadCert(item)">⬇ 下载</u-button>
          <u-button class="cert-page__card-action cert-page__card-action--primary" @click="viewCert(item)">查看</u-button>
        </view>
      </view>
    </view>

    <jst-empty v-else-if="!loading" text="暂无证书记录" icon="🏅" />

    <!-- 公开验真入口 -->
    <view class="cert-page__verify-entry">
      <text class="cert-page__verify-title">🔍 证书公开验真</text>
      <text class="cert-page__verify-desc">输入证书编号，验证证书真实性。适用于学校或单位查验证书。</text>
      <view class="cert-page__verify-row">
        <input
          class="cert-page__verify-input"
          v-model="verifyNo"
          placeholder="输入证书编号"
        />
        <u-button class="cert-page__verify-btn" @click="goVerify">验真</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { getMyCertList } from '@/api/cert'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstEmpty, JstLoading },
  data() {
    return {
      loading: false,
      certList: [],
      verifyNo: ''
    }
  },
  computed: {
    firstPrizeCount() {
      return this.certList.filter(item => item.awardLevel && item.awardLevel.includes('一等')).length
    }
  },
  onLoad() {
    this.loadCertList()
  },
  methods: {
    async loadCertList() {
      this.loading = true
      try {
        const list = await getMyCertList()
        this.certList = Array.isArray(list) ? list : []
      } catch (e) {
        this.certList = []
      } finally {
        this.loading = false
      }
    },
    formatDate(val) {
      if (!val) return '--'
      const d = new Date(val)
      if (isNaN(d.getTime())) return val
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    getAwardTheme(level) {
      if (!level) return 'blue'
      if (level.includes('一等')) return 'gold'
      if (level.includes('二等')) return 'silver'
      return 'blue'
    },
    getAwardIcon(level) {
      if (!level) return '🏅'
      if (level.includes('一等')) return '🥇'
      if (level.includes('二等')) return '🥈'
      if (level.includes('三等')) return '🥉'
      return '🏅'
    },
    viewCert(item) {
      if (item.certId) {
        uni.navigateTo({ url: '/pages-sub/my/cert-detail?id=' + item.certId })
      }
    },
    downloadCert(item) {
      if (item.certId) {
        uni.navigateTo({ url: '/pages-sub/my/cert-detail?id=' + item.certId })
      }
    },
    verifyCert(item) {
      if (item.certNo) {
        uni.navigateTo({ url: `/pages-sub/public/cert-verify?certNo=${encodeURIComponent(item.certNo)}` })
      }
    },
    goVerify() {
      const no = this.verifyNo.trim()
      if (!no) {
        uni.showToast({ title: '请输入证书编号', icon: 'none' })
        return
      }
      uni.navigateTo({ url: `/pages-sub/public/cert-verify?certNo=${encodeURIComponent(no)}` })
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.cert-page {
  min-height: 100vh;
  padding-bottom: 60rpx;
  background: $jst-bg-subtle;
}

.jst-page-skeleton {
  margin: 0 $jst-page-padding $jst-space-lg;
}

.cert-page__header {
  display: flex;
  align-items: center;
  padding: $jst-space-lg $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.cert-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: $jst-bg-page;
  font-size: $jst-font-md;
  color: $jst-text-primary;
}

.cert-page__header-title {
  flex: 1;
  margin-left: $jst-space-md;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.cert-page__stats {
  display: flex;
  gap: $jst-space-md;
  margin: $jst-space-lg $jst-page-padding 0;
}

.cert-page__stat-item {
  flex: 1;
  padding: $jst-space-lg;
  border-radius: $jst-radius-md;
  text-align: center;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.cert-page__stat-num {
  display: block;
  font-size: 44rpx;
  font-weight: $jst-weight-bold;
}

.cert-page__stat-num--brand {
  color: $jst-brand;
}

.cert-page__stat-num--gold {
  color: $jst-warning;
}

.cert-page__stat-num--success {
  color: $jst-success;
}

.cert-page__stat-label {
  display: block;
  margin-top: 6rpx;
  font-size: $jst-font-sm;
  color: $jst-text-placeholder;
}

.cert-page__list {
  padding: $jst-space-lg $jst-page-padding 0;
}

.cert-page__card {
  margin-bottom: 28rpx;
  border-radius: $jst-radius-lg;
  overflow: hidden;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.cert-page__card-preview {
  position: relative;
  height: 320rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.cert-page__card-preview--gold {
  background: linear-gradient(135deg, $jst-warning 0%, $jst-gold 55%, $jst-warning-light 100%);
}

.cert-page__card-preview--silver {
  background: linear-gradient(135deg, $jst-text-secondary 0%, $jst-info 55%, $jst-info-light 100%);
}

.cert-page__card-preview--blue {
  background: linear-gradient(135deg, $jst-brand 0%, $jst-brand-dark 100%);
}

.cert-page__card-mock {
  width: 80%;
  padding: 28rpx;
  border-radius: $jst-radius-lg;
  border: 2rpx solid rgba($jst-text-inverse, 0.35);
  background: rgba($jst-text-inverse, 0.12);
  text-align: center;
}

.cert-page__card-mock-title {
  display: block;
  font-size: $jst-font-xs;
  font-weight: $jst-weight-semibold;
  letter-spacing: 3rpx;
  color: rgba($jst-text-inverse, 0.8);
}

.cert-page__card-mock-name {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xl;
  font-weight: $jst-weight-bold;
  letter-spacing: 4rpx;
  color: $jst-text-inverse;
}

.cert-page__card-mock-contest {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  line-height: 1.5;
  color: rgba($jst-text-inverse, 0.76);
}

.cert-page__card-badge {
  position: absolute;
  top: 20rpx;
  right: $jst-page-padding;
  font-size: 56rpx;
  opacity: 0.62;
}

.cert-page__card-info {
  padding: 28rpx;
  border-bottom: 2rpx solid $jst-border;
}

.cert-page__card-name {
  display: block;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  line-height: 1.4;
  color: $jst-text-primary;
}

.cert-page__card-tags {
  display: flex;
  gap: $jst-space-sm;
  flex-wrap: wrap;
  margin-top: $jst-space-md;
}

.cert-page__card-no {
  display: block;
  margin-top: $jst-space-sm;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

.cert-page__card-actions {
  display: flex;
  gap: $jst-space-md;
  justify-content: flex-end;
  padding: 20rpx 28rpx;
}

.cert-page__card-action {
  min-height: 62rpx;
  padding: 0 $jst-space-lg;
  border-radius: $jst-radius-md;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.cert-page__verify-entry {
  margin: $jst-space-lg $jst-page-padding 0;
  padding: 28rpx;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-md;
}

.cert-page__verify-title {
  display: block;
  margin-bottom: $jst-space-md;
  font-size: $jst-font-base;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.cert-page__verify-desc {
  display: block;
  margin-bottom: 20rpx;
  font-size: $jst-font-base;
  line-height: 1.6;
  color: $jst-text-placeholder;
}

.cert-page__verify-row {
  display: flex;
  gap: $jst-space-md;
}

.cert-page__verify-input {
  flex: 1;
  height: 88rpx;
  padding: 0 $jst-page-padding;
  border-radius: $jst-radius-md;
  border: 2rpx solid $jst-border;
  background: $jst-bg-page;
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

::v-deep .cert-page__card-tag .u-tag {
  border-color: $jst-brand;
  color: $jst-brand;
  background: $jst-brand-light;
}

::v-deep .cert-page__card-tag--award .u-tag {
  border-color: $jst-warning;
  color: $jst-warning;
  background: $jst-gold-light;
}

::v-deep .cert-page__card-action.u-button {
  border-color: $jst-border;
  background: $jst-bg-card;
  color: $jst-text-secondary;
}

::v-deep .cert-page__card-action--primary.u-button {
  border-color: $jst-brand;
  background: $jst-brand;
  color: $jst-text-inverse;
}

::v-deep .cert-page__verify-btn.u-button {
  height: 88rpx;
  padding: 0 $jst-space-xl;
  border: none;
  border-radius: $jst-radius-md;
  background: $jst-brand;
  color: $jst-text-inverse;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}
</style>
