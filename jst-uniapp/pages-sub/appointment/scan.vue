<!-- 中文注释: 扫码核销 (仅赛事方/运营)
     对应原型: 小程序原型图/checkin-scan.png
     权限: 后端 @PreAuthorize jst_partner / jst_platform_op
     前端权限门: 路由进入前先检查 roles, 非授权用户 toast + navigateBack -->
<template>
  <view class="sc-page">
    <view class="sc-hero">
      <text class="sc-hero__title">扫码核销</text>
      <text class="sc-hero__sub">对准学生出示的核销二维码</text>
    </view>

    <view class="sc-actions">
      <button class="sc-actions__primary" @tap="startScan">📷 开始扫码</button>
    </view>

    <view v-if="lastResult" class="sc-result">
      <text class="sc-result__title">核销结果</text>
      <view class="sc-kv"><text class="sc-kv__k">项目</text><text class="sc-kv__v">{{ lastResult.itemName || '--' }}</text></view>
      <view class="sc-kv"><text class="sc-kv__k">预约人</text><text class="sc-kv__v">{{ lastResult.participantName || '--' }}</text></view>
      <view class="sc-kv"><text class="sc-kv__k">赛事</text><text class="sc-kv__v">{{ lastResult.contestName || '--' }}</text></view>
      <view class="sc-kv"><text class="sc-kv__k">状态</text><text class="sc-kv__v">{{ lastResult.status || 'ok' }}</text></view>
      <button class="sc-result__again" @tap="startScan">继续扫码</button>
    </view>
  </view>
</template>

<script>
import { scanWriteoff } from '@/api/appointment'
import { useUserStore } from '@/store/user'

export default {
  data() { return { lastResult: null } },
  onLoad() {
    const store = useUserStore()
    const roles = (store.userInfo && store.userInfo.roles) || []
    const allowed = roles.includes('jst_partner') || roles.includes('jst_platform_op')
    if (!allowed) {
      uni.showToast({ title: '无扫码权限', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 800)
    }
  },
  methods: {
    startScan() {
      uni.scanCode({
        scanType: ['qrCode'],
        success: async (res) => {
          try {
            const payload = res.result
            const data = await scanWriteoff({ qrCode: payload, terminal: 'wx' })
            this.lastResult = data || { status: 'ok' }
            uni.showToast({ title: '核销成功', icon: 'success' })
          } catch (e) {
            // request.js 已弹 toast
          }
        },
        fail: () => {}
      })
    }
  }
}
</script>

<style scoped lang="scss">
.sc-page { min-height: 100vh; background: var(--jst-color-page-bg); padding-bottom: 80rpx; }
.sc-hero { padding: 96rpx 32rpx 64rpx; background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; text-align: center; }
.sc-hero__title { display: block; font-size: 48rpx; font-weight: 800; }
.sc-hero__sub { display: block; margin-top: 16rpx; font-size: 24rpx; color: var(--jst-color-white-76); }
.sc-actions { margin: 48rpx 32rpx 0; }
.sc-actions__primary { height: 104rpx; line-height: 104rpx; border-radius: var(--jst-radius-md); background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; font-size: 32rpx; font-weight: 800; border: none; }
.sc-result { margin: 24rpx 32rpx 0; padding: 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.sc-result__title { display: block; font-size: 30rpx; font-weight: 800; color: var(--jst-color-success); margin-bottom: 20rpx; }
.sc-kv { display: flex; padding: 12rpx 0; font-size: 26rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.sc-kv:last-of-type { border-bottom: none; }
.sc-kv__k { width: 160rpx; color: var(--jst-color-text-tertiary); }
.sc-kv__v { flex: 1; color: var(--jst-color-text); }
.sc-result__again { margin-top: 24rpx; height: 88rpx; line-height: 88rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-brand-soft); color: var(--jst-color-brand); font-size: 28rpx; font-weight: 700; border: none; }
</style>
