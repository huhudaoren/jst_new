<!-- 中文注释: 渠道方 · 我邀请的渠道列表
     调用接口: GET /jst/wx/channel/invite/list -->
<template>
  <view class="il-page">
    <!-- 顶部 hero -->
    <view class="il-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="il-hero__nav">
        <view class="il-hero__back" @tap="goBack">←</view>
        <text class="il-hero__title">我邀请的渠道</text>
        <view style="width: 64rpx;" />
      </view>
      <!-- 统计 -->
      <view class="il-hero__stats">
        <view class="il-hero__stat">
          <text class="il-hero__stat-num">{{ total }}</text>
          <text class="il-hero__stat-label">已邀请</text>
        </view>
        <view class="il-hero__stat">
          <text class="il-hero__stat-num">{{ activeCount }}</text>
          <text class="il-hero__stat-label">活跃渠道</text>
        </view>
      </view>
    </view>

    <!-- 列表 -->
    <view class="il-list">
      <view v-for="item in list" :key="item.channelId || item.inviteId" class="il-item">
        <view class="il-item__info">
          <text class="il-item__name">{{ item.channelName || item.applyName || '渠道' + item.channelId }}</text>
          <text class="il-item__time">邀请时间 {{ formatTime(item.inviteTime || item.createTime) }}</text>
        </view>
        <view class="il-item__right">
          <text class="il-item__rate" v-if="item.commissionRate != null">费率 {{ (item.commissionRate * 100).toFixed(1) }}%</text>
          <u-tag :text="statusLabel(item.status)" :type="statusTagType(item.status)" size="mini" shape="circle" />
        </view>
      </view>

      <u-empty v-if="!loading && !list.length" mode="data" text="暂无邀请记录"></u-empty>
      <u-loadmore v-if="list.length" :status="'nomore'" />
    </view>
  </view>
</template>

<script>
import { listInvited } from '@/api/invite'

const STATUS_LABEL = {
  active: '活跃',
  pending: '待认证',
  approved: '已认证',
  rejected: '被拒绝',
  suspended: '已暂停'
}
const STATUS_TYPE = {
  active: 'success',
  approved: 'success',
  pending: 'warning',
  rejected: 'error',
  suspended: 'info'
}

export default {
  data() {
    return {
      list: [],
      loading: true
    }
  },
  computed: {
    navPaddingTop() {
      const si = uni.getSystemInfoSync()
      return (si.statusBarHeight || 20) + 'px'
    },
    total() { return this.list.length },
    activeCount() {
      return this.list.filter(i => i.status === 'active' || i.status === 'approved').length
    }
  },
  onLoad() {
    this.loadList()
  },
  onPullDownRefresh() {
    this.loadList().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    goBack() {
      if (getCurrentPages().length > 1) uni.navigateBack()
      else uni.switchTab({ url: '/pages/my/index' })
    },
    async loadList() {
      this.loading = true
      try {
        const data = await listInvited()
        this.list = Array.isArray(data) ? data : (data && data.rows) || []
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '未知' },
    statusTagType(s) { return STATUS_TYPE[s] || 'info' },
    formatTime(v) {
      if (!v) return '--'
      return String(v).replace('T', ' ').slice(0, 10)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.il-page { min-height: 100vh; padding-bottom: 60rpx; background: $jst-bg-page; }

.il-hero {
  padding: 0 $jst-space-xl 48rpx;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
}
.il-hero__nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 24rpx;
  margin-bottom: $jst-space-xl;
}
.il-hero__back {
  width: 64rpx;
  height: 64rpx;
  border-radius: $jst-radius-sm;
  background: rgba(255, 255, 255, 0.18);
  color: $jst-text-inverse;
  text-align: center;
  line-height: 64rpx;
  font-size: 36rpx;
}
.il-hero__title { font-size: 34rpx; font-weight: 600; color: $jst-text-inverse; }

.il-hero__stats { display: flex; gap: $jst-space-xxl; }
.il-hero__stat { text-align: center; }
.il-hero__stat-num { display: block; font-size: 52rpx; font-weight: 700; color: $jst-text-inverse; }
.il-hero__stat-label { display: block; font-size: $jst-font-sm; color: rgba(255, 255, 255, 0.75); margin-top: 6rpx; }

.il-list { padding: $jst-space-lg $jst-space-xl 0; }

.il-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg $jst-space-xl;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
  margin-bottom: $jst-space-md;
}
.il-item__info { flex: 1; min-width: 0; }
.il-item__name {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  margin-bottom: 8rpx;
}
.il-item__time { font-size: $jst-font-xs; color: $jst-text-secondary; }
.il-item__right { display: flex; flex-direction: column; align-items: flex-end; gap: 8rpx; flex-shrink: 0; }
.il-item__rate { font-size: $jst-font-sm; color: $jst-brand; font-weight: 600; }
</style>
