<!-- 中文注释: 证书公开验真页；对应原型 小程序原型图/cert-verify.html；对应接口 GET /jst/public/cert/verify -->
<template>
  <view class="cert-verify-page">
    <view class="cert-verify-page__header">
      <view class="cert-verify-page__back" @tap="goBack">←</view>
      <text class="cert-verify-page__header-title">证书验真结果</text>
    </view>

    <!-- 验真结果 -->
    <view v-if="verified && result" class="cert-verify-page__result">
      <view class="cert-verify-page__result-icon">✅</view>
      <text class="cert-verify-page__result-title">证书验证通过</text>
      <text class="cert-verify-page__result-desc">该证书由竞赛通平台颁发，真实有效</text>

      <view class="cert-verify-page__detail-card">
        <text class="cert-verify-page__detail-section">证书基础信息</text>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">获奖情况</text>
          <text class="cert-verify-page__detail-val">{{ result.awardLevel || '--' }}</text>
        </view>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">获奖人</text>
          <text class="cert-verify-page__detail-val">{{ result.holderName || '--' }}</text>
        </view>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">赛事名称</text>
          <text class="cert-verify-page__detail-val">{{ result.contestName || '--' }}</text>
        </view>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">证书编号</text>
          <text class="cert-verify-page__detail-val">{{ result.certNo || '--' }}</text>
        </view>
      </view>

      <view class="cert-verify-page__detail-card">
        <text class="cert-verify-page__detail-section">完整验证信息</text>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">参赛组别</text>
          <text class="cert-verify-page__detail-val">{{ result.groupLevel || '--' }}</text>
        </view>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">发证机构</text>
          <text class="cert-verify-page__detail-val">{{ result.issueOrg || '--' }}</text>
        </view>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">发证日期</text>
          <text class="cert-verify-page__detail-val">{{ result.issueDate || '--' }}</text>
        </view>
        <view class="cert-verify-page__detail-row">
          <text class="cert-verify-page__detail-key">验真状态</text>
          <text class="cert-verify-page__detail-val" style="color: var(--jst-color-success); font-weight: 700;">真实有效</text>
        </view>
      </view>
    </view>

    <!-- 验真失败 -->
    <view v-else-if="verified && !result" class="cert-verify-page__no-result">
      <text class="cert-verify-page__no-result-icon">❌</text>
      <text class="cert-verify-page__no-result-title">未查询到证书信息</text>
      <text class="cert-verify-page__no-result-desc">请确认证书编号是否正确</text>
    </view>

    <!-- 手动输入查询 -->
    <view class="cert-verify-page__manual">
      <text class="cert-verify-page__manual-title">💡 竞赛通证书伪造说明</text>
      <text class="cert-verify-page__manual-desc">每张证书均有唯一二维码，扫码可验证证书真伪。无法验证的证书请谨慎对待。</text>

      <text class="cert-verify-page__manual-label">手动输入证书编号查询</text>
      <view class="cert-verify-page__manual-row">
        <input
          class="cert-verify-page__manual-input"
          v-model="certNo"
          placeholder="输入证书编号 JST-XXXX-XXXX"
          @confirm="doVerify"
        />
        <view class="cert-verify-page__manual-btn" @tap="doVerify">查询</view>
      </view>
    </view>

    <view class="cert-verify-page__footer-btn" @tap="goHome">返回首页</view>
  </view>
</template>

<script>
import { verifyPublicCert } from '@/api/cert'

export default {
  data() {
    return {
      certNo: '',
      verified: false,
      result: null
    }
  },
  onLoad(query) {
    if (query && query.certNo) {
      this.certNo = decodeURIComponent(query.certNo)
      this.doVerify()
    }
  },
  methods: {
    async doVerify() {
      const no = this.certNo.trim()
      if (!no) {
        uni.showToast({ title: '请输入证书编号', icon: 'none' })
        return
      }
      uni.showLoading({ title: '验证中...' })
      try {
        const res = await verifyPublicCert({ certNo: no })
        this.result = res || null
        this.verified = true
      } catch (e) {
        this.result = null
        this.verified = true
      } finally {
        uni.hideLoading()
      }
    },
    goHome() {
      uni.switchTab({ url: '/pages/index/index' })
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/index/index' })
    }
  }
}
</script>

<style scoped lang="scss">
.cert-verify-page { min-height: 100vh; padding-bottom: 60rpx; background: var(--jst-color-page-bg); }

.cert-verify-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.cert-verify-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.cert-verify-page__header-title { flex: 1; text-align: center; margin-right: 72rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

/* 验真成功 */
.cert-verify-page__result { margin: 24rpx; text-align: center; }
.cert-verify-page__result-icon { display: block; font-size: 80rpx; margin-bottom: 16rpx; }
.cert-verify-page__result-title { display: block; font-size: 36rpx; font-weight: 800; color: var(--jst-color-success); }
.cert-verify-page__result-desc { display: block; margin-top: 12rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); }

/* 详情卡 */
.cert-verify-page__detail-card { margin-top: 24rpx; padding: 24rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); box-shadow: 0 4rpx 16rpx rgba(20,30,60,0.06); text-align: left; }
.cert-verify-page__detail-section { display: block; font-size: 26rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; padding-bottom: 12rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.cert-verify-page__detail-row { display: flex; justify-content: space-between; padding: 12rpx 0; font-size: 26rpx; }
.cert-verify-page__detail-key { color: var(--jst-color-text-tertiary); flex-shrink: 0; width: 160rpx; }
.cert-verify-page__detail-val { color: var(--jst-color-text); font-weight: 500; text-align: right; flex: 1; }

/* 未找到 */
.cert-verify-page__no-result { margin: 48rpx 24rpx; text-align: center; }
.cert-verify-page__no-result-icon { display: block; font-size: 80rpx; margin-bottom: 16rpx; }
.cert-verify-page__no-result-title { display: block; font-size: 32rpx; font-weight: 700; color: var(--jst-color-danger); }
.cert-verify-page__no-result-desc { display: block; margin-top: 12rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); }

/* 手动查询 */
.cert-verify-page__manual { margin: 24rpx; padding: 28rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-success-soft); border: 2rpx solid rgba(39,174,96,0.2); }
.cert-verify-page__manual-title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-success); margin-bottom: 12rpx; }
.cert-verify-page__manual-desc { display: block; font-size: 24rpx; color: var(--jst-color-text-secondary); line-height: 1.6; margin-bottom: 24rpx; }
.cert-verify-page__manual-label { display: block; font-size: 26rpx; font-weight: 600; color: var(--jst-color-text); margin-bottom: 12rpx; }
.cert-verify-page__manual-row { display: flex; gap: 16rpx; }
.cert-verify-page__manual-input { flex: 1; height: 88rpx; padding: 0 24rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-card-bg); border: 2rpx solid var(--jst-color-border); font-size: 28rpx; }
.cert-verify-page__manual-btn { display: flex; align-items: center; justify-content: center; height: 88rpx; padding: 0 32rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-success); color: #fff; font-size: 28rpx; font-weight: 700; flex-shrink: 0; }

/* 返回首页 */
.cert-verify-page__footer-btn { margin: 24rpx; height: 88rpx; display: flex; align-items: center; justify-content: center; border-radius: var(--jst-radius-md); border: 2rpx solid var(--jst-color-border); font-size: 28rpx; font-weight: 600; color: var(--jst-color-text-secondary); }
</style>
