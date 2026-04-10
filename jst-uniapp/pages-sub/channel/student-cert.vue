<!-- 中文注释: 渠道方 · 查看学生证书 (E1-CH-3)
     数据来源: GET /jst/wx/channel/students/{id}/cert -->
<template>
  <view class="cert-page">
    <view class="cert-header">
      <view class="cert-header__back" @tap="goBack">←</view>
      <text class="cert-header__title">{{ studentName }} 的证书</text>
    </view>

    <view v-for="item in list" :key="item.certId" class="cert-card">
      <view class="cert-card__top">
        <text class="cert-card__name">{{ item.certName || item.contestName || '--' }}</text>
        <text class="cert-card__level">{{ item.awardLevel || '' }}</text>
      </view>
      <view class="cert-card__bottom">
        <text class="cert-card__no">证书编号：{{ item.certNo || '--' }}</text>
        <text class="cert-card__date">{{ formatDate(item.issueDate) }}</text>
      </view>
      <view v-if="item.certUrl" class="cert-card__download" @tap="downloadCert(item.certUrl)">
        <text>📥 下载证书</text>
      </view>
    </view>

    <view v-if="!list.length && !loading" class="cert-empty">
      <text class="cert-empty__icon">🏅</text>
      <text class="cert-empty__text">暂无证书</text>
    </view>
  </view>
</template>

<script>
import { getStudentCert } from '@/api/channel'

export default {
  data() {
    return { loading: false, list: [], studentName: '', studentId: '' }
  },
  onLoad(opts) {
    this.studentId = opts.studentId || ''
    this.studentName = decodeURIComponent(opts.name || '')
    this.loadData()
  },
  methods: {
    async loadData() {
      if (!this.studentId) return
      this.loading = true
      try {
        const res = await getStudentCert(this.studentId)
        this.list = Array.isArray(res) ? res : (res && res.rows) || []
      } catch (e) {
        this.list = []
      }
      this.loading = false
    },
    formatDate(v) { return v ? String(v).slice(0, 10) : '--' },
    downloadCert(url) {
      if (!url) return
      uni.downloadFile({
        url: url,
        success: (res) => {
          uni.saveImageToPhotosAlbum({
            filePath: res.tempFilePath,
            success: () => { uni.showToast({ title: '已保存', icon: 'success' }) },
            fail: () => { uni.showToast({ title: '保存失败', icon: 'none' }) }
          })
        },
        fail: () => { uni.showToast({ title: '下载失败', icon: 'none' }) }
      })
    },
    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.cert-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }
.cert-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); }
.cert-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.cert-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }
.cert-card { margin: 24rpx 32rpx 0; padding: 28rpx; background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); }
.cert-card__top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.cert-card__name { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); flex: 1; }
.cert-card__level { font-size: 24rpx; color: #F5A623; font-weight: 700; flex-shrink: 0; padding: 4rpx 16rpx; background: #FEF9EC; border-radius: var(--jst-radius-full); }
.cert-card__bottom { display: flex; justify-content: space-between; align-items: center; }
.cert-card__no { font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.cert-card__date { font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.cert-card__download { margin-top: 16rpx; padding-top: 16rpx; border-top: 2rpx solid var(--jst-color-border); text-align: center; font-size: 26rpx; color: #3F51B5; font-weight: 600; }
.cert-empty { text-align: center; padding: 120rpx 48rpx; }
.cert-empty__icon { display: block; font-size: 80rpx; margin-bottom: 24rpx; }
.cert-empty__text { font-size: 28rpx; color: var(--jst-color-text-tertiary); }
</style>
