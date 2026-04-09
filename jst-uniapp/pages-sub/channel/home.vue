<!-- 中文注释: 渠道方 · 首页工作台
     对应原型: 小程序原型图/channel-home.png
     数据来源: rebate/summary (预计返点) + withdraw/list (最近单据)
     本月学生/订单数暂无独立接口, 展示 "--" 并标注 TODO -->
<template>
  <view class="ch-page">
    <view class="ch-hero">
      <text class="ch-hero__title">渠道方工作台</text>
      <text class="ch-hero__sub">{{ nickname }}</text>
      <view class="ch-hero__summary">
        <view class="ch-hero__stat"><text class="ch-hero__num">--</text><text class="ch-hero__lbl">本月新增学生</text></view>
        <view class="ch-hero__stat"><text class="ch-hero__num">--</text><text class="ch-hero__lbl">本月订单</text></view>
        <view class="ch-hero__stat"><text class="ch-hero__num">¥{{ summary.withdrawableAmount }}</text><text class="ch-hero__lbl">可提现返点</text></view>
      </view>
    </view>

    <view class="ch-grid">
      <view class="ch-tile" @tap="go('/pages-sub/channel/rebate')"><text class="ch-tile__icon">💰</text><text class="ch-tile__text">返点中心</text></view>
      <view class="ch-tile" @tap="go('/pages-sub/channel/withdraw-list')"><text class="ch-tile__icon">📄</text><text class="ch-tile__text">提现记录</text></view>
      <view class="ch-tile" @tap="go('/pages-sub/channel/students')"><text class="ch-tile__icon">👥</text><text class="ch-tile__text">我的学生</text></view>
      <view class="ch-tile" @tap="go('/pages-sub/channel/orders')"><text class="ch-tile__icon">🧾</text><text class="ch-tile__text">渠道订单</text></view>
      <view class="ch-tile" @tap="go('/pages-sub/channel/data')"><text class="ch-tile__icon">📊</text><text class="ch-tile__text">渠道数据</text></view>
    </view>

    <view class="ch-section">
      <view class="ch-section__title">
        <text>最近提现</text>
        <text class="ch-section__more" @tap="go('/pages-sub/channel/withdraw-list')">查看全部 ></text>
      </view>
      <view v-for="item in recentList" :key="item.settlementId" class="ch-row" @tap="goDetail(item.settlementId)">
        <view class="ch-row__main">
          <text class="ch-row__title">{{ item.settlementNo }}</text>
          <text class="ch-row__sub">{{ formatTime(item.applyTime) }}</text>
        </view>
        <text class="ch-row__amount">¥{{ item.applyAmount || '0.00' }}</text>
      </view>
      <view v-if="!recentList.length" class="ch-empty">暂无提现单</view>
    </view>
  </view>
</template>

<script>
import { getRebateSummary, getWithdrawList } from '@/api/channel'
import { useUserStore } from '@/store/user'

export default {
  data() {
    return {
      summary: { withdrawableAmount: '0.00' },
      recentList: []
    }
  },
  computed: {
    nickname() {
      const store = useUserStore()
      return (store.userInfo && (store.userInfo.nickname || store.userInfo.channelName)) || '渠道方'
    }
  },
  onShow() { this.load() },
  methods: {
    async load() {
      try {
        const [summary, list] = await Promise.all([
          getRebateSummary(),
          getWithdrawList({ pageNum: 1, pageSize: 3 })
        ])
        this.summary = { withdrawableAmount: (summary && summary.withdrawableAmount) || '0.00' }
        this.recentList = (list && list.rows) || []
      } catch (e) {}
    },
    go(url) { uni.navigateTo({ url }) },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/channel/withdraw-detail?id=' + id }) },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
.ch-page { min-height: 100vh; padding-bottom: 48rpx; background: var(--jst-color-page-bg); }
.ch-hero { padding: 96rpx 32rpx 56rpx; background: linear-gradient(135deg, #1A237E, #1058A0); color: #fff; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; }
.ch-hero__title { display: block; font-size: 40rpx; font-weight: 800; }
.ch-hero__sub { display: block; margin-top: 10rpx; font-size: 24rpx; color: var(--jst-color-white-76); }
.ch-hero__summary { display: flex; gap: 20rpx; margin-top: 28rpx; padding: 24rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-white-18); }
.ch-hero__stat { flex: 1; text-align: center; }
.ch-hero__num { display: block; font-size: 36rpx; font-weight: 800; color: #FFD54F; }
.ch-hero__lbl { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--jst-color-white-76); }

.ch-grid { margin: 24rpx 32rpx 0; padding: 28rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); display: grid; grid-template-columns: repeat(3, 1fr); gap: 24rpx; }
.ch-tile { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24rpx 0; }
.ch-tile__icon { font-size: 48rpx; }
.ch-tile__text { margin-top: 12rpx; font-size: 24rpx; color: var(--jst-color-text-secondary); }

.ch-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.ch-section__title { display: flex; justify-content: space-between; align-items: center; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 16rpx; }
.ch-section__more { font-size: 24rpx; color: var(--jst-color-brand); font-weight: 500; }
.ch-row { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.ch-row:last-child { border-bottom: none; }
.ch-row__main { flex: 1; min-width: 0; }
.ch-row__title { display: block; font-size: 26rpx; color: var(--jst-color-text); font-weight: 600; }
.ch-row__sub { display: block; margin-top: 4rpx; font-size: 22rpx; color: var(--jst-color-text-tertiary); }
.ch-row__amount { font-size: 30rpx; font-weight: 800; color: #F5A623; }
.ch-empty { padding: 40rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
</style>
