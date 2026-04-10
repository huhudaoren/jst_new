<!-- 中文注释: 我的证书页；对应原型 小程序原型图/my-cert.html；对应接口 GET /jst/wx/cert/my -->
<template>
  <view class="cert-page">
    <view class="cert-page__header">
      <view class="cert-page__back" @tap="goBack">←</view>
      <text class="cert-page__header-title">我的证书</text>
    </view>

    <jst-loading :loading="loading" text="证书加载中..." />

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
            <text class="cert-page__card-tag cert-page__card-tag--award">{{ getAwardIcon(item.awardLevel) }} {{ item.awardLevel }}</text>
            <text v-if="item.category" class="cert-page__card-tag">{{ item.category }}</text>
          </view>
          <text class="cert-page__card-no">证书编号：{{ item.certNo || '—' }} · 发证：{{ formatDate(item.issueDate) }}</text>
        </view>

        <!-- 操作 -->
        <view class="cert-page__card-actions">
          <view class="cert-page__card-action" @tap="verifyCert(item)">🔍 验真</view>
          <view class="cert-page__card-action" @tap="downloadCert(item)">⬇ 下载</view>
          <view class="cert-page__card-action cert-page__card-action--primary" @tap="viewCert(item)">查看</view>
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
        <view class="cert-page__verify-btn" @tap="goVerify">验真</view>
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
      uni.showToast({ title: '证书查看功能完善中', icon: 'none' })
    },
    downloadCert(item) {
      uni.showToast({ title: '证书下载功能完善中', icon: 'none' })
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
.cert-page { min-height: 100vh; padding-bottom: 60rpx; background: var(--jst-color-page-bg); }

.cert-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.cert-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.cert-page__header-title { flex: 1; margin-left: 16rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

/* 统计 */
.cert-page__stats { display: flex; gap: 16rpx; margin: 24rpx 24rpx 0; }
.cert-page__stat-item { flex: 1; padding: 24rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); text-align: center; }
.cert-page__stat-num { display: block; font-size: 44rpx; font-weight: 900; }
.cert-page__stat-num--brand { color: var(--jst-color-brand); }
.cert-page__stat-num--gold { color: #9A6300; }
.cert-page__stat-num--success { color: var(--jst-color-success); }
.cert-page__stat-label { display: block; margin-top: 6rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); }

/* 证书列表 */
.cert-page__list { padding: 24rpx; }
.cert-page__card { background: var(--jst-color-card-bg); border-radius: var(--jst-radius-lg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); margin-bottom: 28rpx; overflow: hidden; }

/* 预览区 */
.cert-page__card-preview { height: 320rpx; position: relative; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.cert-page__card-preview--gold { background: linear-gradient(135deg, #7C4F00 0%, #C8820A 40%, #F5C842 70%, #E8A800 100%); }
.cert-page__card-preview--silver { background: linear-gradient(135deg, #3A3A3A 0%, #6B6B6B 40%, #B0B0B0 70%, #888 100%); }
.cert-page__card-preview--blue { background: linear-gradient(135deg, #1058A0 0%, #1A7CD9 100%); }
.cert-page__card-mock { width: 80%; padding: 28rpx; border-radius: 20rpx; background: rgba(255,255,255,0.12); border: 2rpx solid rgba(255,255,255,0.35); text-align: center; }
.cert-page__card-mock-title { display: block; font-size: 22rpx; font-weight: 600; color: rgba(255,255,255,0.8); letter-spacing: 3rpx; }
.cert-page__card-mock-name { display: block; margin-top: 12rpx; font-size: 36rpx; font-weight: 900; color: #fff; letter-spacing: 4rpx; }
.cert-page__card-mock-contest { display: block; margin-top: 8rpx; font-size: 22rpx; color: rgba(255,255,255,0.75); line-height: 1.5; }
.cert-page__card-badge { position: absolute; top: 20rpx; right: 24rpx; font-size: 56rpx; opacity: 0.6; }

/* 信息区 */
.cert-page__card-info { padding: 28rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.cert-page__card-name { display: block; font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); line-height: 1.4; }
.cert-page__card-tags { display: flex; gap: 12rpx; flex-wrap: wrap; margin-top: 16rpx; }
.cert-page__card-tag { padding: 8rpx 18rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-brand-soft); font-size: 22rpx; color: var(--jst-color-brand); }
.cert-page__card-tag--award { background: #FFF8E1; color: #9A6300; }
.cert-page__card-no { display: block; margin-top: 12rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }

/* 操作区 */
.cert-page__card-actions { display: flex; gap: 16rpx; padding: 20rpx 28rpx; justify-content: flex-end; }
.cert-page__card-action { padding: 12rpx 24rpx; border-radius: var(--jst-radius-md); border: 2rpx solid var(--jst-color-border); font-size: 24rpx; color: var(--jst-color-text-secondary); }
.cert-page__card-action--primary { background: var(--jst-color-brand); border-color: var(--jst-color-brand); color: #fff; font-weight: 600; }

/* 公开验真入口 */
.cert-page__verify-entry { margin: 24rpx; padding: 28rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); }
.cert-page__verify-title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.cert-page__verify-desc { display: block; font-size: 26rpx; color: var(--jst-color-text-tertiary); line-height: 1.6; margin-bottom: 20rpx; }
.cert-page__verify-row { display: flex; gap: 16rpx; }
.cert-page__verify-input { flex: 1; height: 88rpx; padding: 0 24rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-page-bg); border: 2rpx solid var(--jst-color-border); font-size: 28rpx; }
.cert-page__verify-btn { display: flex; align-items: center; justify-content: center; height: 88rpx; padding: 0 32rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-brand); color: #fff; font-size: 28rpx; font-weight: 700; flex-shrink: 0; }
</style>
