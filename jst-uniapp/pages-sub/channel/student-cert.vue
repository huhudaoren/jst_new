<!-- 中文注释: 渠道方 · 查看学生证书 (E1-CH-3)
     数据来源: GET /jst/wx/channel/students/{id}/cert -->
<template>
  <view class="cert-page">
    <view class="cert-header" :style="{ paddingTop: navPaddingTop }">
      <view class="cert-header__back" @tap="goBack">←</view>
      <text class="cert-header__title">{{ studentName }} 的证书</text>
    </view>

    <view v-for="item in list" :key="item.certId" class="cert-card">
      <view class="cert-card__top">
        <text class="cert-card__name">{{ item.certName || item.contestName || '--' }}</text>
        <u-tag :text="item.awardLevel || '未分级'" type="warning" size="mini" plain shape="circle"></u-tag>
      </view>
      <view class="cert-card__bottom">
        <text class="cert-card__no">证书编号：{{ item.certNo || '--' }}</text>
        <text class="cert-card__date">{{ formatDate(item.issueDate) }}</text>
      </view>
      <view v-if="item.certUrl" class="cert-card__download" @tap="downloadCert(item.certUrl)">
        <u-button text="下载证书" type="primary" plain size="mini" shape="circle" icon="download"></u-button>
      </view>
    </view>

    <u-loadmore v-if="list.length" :status="loading ? 'loading' : 'nomore'" />
    <u-empty v-if="!list.length && !loading" mode="data" text="暂无证书"></u-empty>
  </view>
</template>

<script>
import { getStudentCert } from '@/api/channel'

export default {
  data() {
    return { loading: false, list: [], studentName: '', studentId: '', skeletonShow: true /* [visual-effect] */ }
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
@import '@/styles/design-tokens.scss';

.cert-page { min-height: 100vh; padding-bottom: $jst-space-xxl; background: $jst-bg-page; }
.cert-header { display: flex; align-items: center; padding: 0 $jst-space-xl; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.cert-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: $jst-font-xl; margin-right: $jst-space-lg; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.cert-header__title { flex: 1; font-size: 34rpx; font-weight: $jst-weight-semibold; color: $jst-text-primary; }
.cert-card { margin: $jst-space-lg $jst-space-xl 0; padding: 28rpx; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; }
.cert-card__top { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-sm; }
.cert-card__name { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; flex: 1; }
.cert-card__bottom { display: flex; justify-content: space-between; align-items: center; }
.cert-card__no { font-size: 22rpx; color: $jst-text-secondary; }
.cert-card__date { font-size: 22rpx; color: $jst-text-secondary; }
.cert-card__download { margin-top: $jst-space-md; padding-top: $jst-space-md; border-top: 2rpx solid $jst-border; display: flex; justify-content: center; }
</style>
