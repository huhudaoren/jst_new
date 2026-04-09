<!-- 中文注释: 个人预约 · 我的预约
     对应原型: 小程序原型图/my-reserve.png
     调用接口: GET /jst/wx/appointment/my -->
<template>
  <view class="al-page">
    <scroll-view class="al-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['al-tabs__item', activeStatus === tab.value && 'al-tabs__item--active']"
        @tap="activeStatus = tab.value"
      >{{ tab.label }}</view>
    </scroll-view>

    <view class="al-list">
      <view
        v-for="item in filteredList"
        :key="item.appointmentId"
        class="al-card"
        @tap="goDetail(item.appointmentId)"
      >
        <view class="al-card__head">
          <text class="al-card__title">{{ item.contestName || '——' }}</text>
          <text :class="['al-card__status', 'al-card__status--' + item.mainStatus]">{{ statusLabel(item.mainStatus) }}</text>
        </view>
        <view class="al-card__row"><text class="al-card__k">日期</text><text class="al-card__v">{{ formatDate(item.appointmentDate) }} · {{ item.sessionCode || '--' }}</text></view>
        <view class="al-card__row"><text class="al-card__k">参赛者</text><text class="al-card__v">{{ item.participantName || '--' }}</text></view>
        <view class="al-card__row" v-if="item.writeoffProgress">
          <text class="al-card__k">核销进度</text>
          <text class="al-card__v">{{ item.writeoffProgress }}</text>
        </view>
      </view>
      <view v-if="!filteredList.length && !loading" class="al-empty">暂无预约记录</view>
      <view v-if="loading" class="al-empty">加载中...</view>
    </view>
  </view>
</template>

<script>
import { getMyAppointments } from '@/api/appointment'

const TABS = [
  { value: '', label: '全部' },
  { value: 'booked', label: '待使用' },
  { value: 'completed', label: '已使用' },
  { value: 'cancelled', label: '已取消' }
]
const STATUS_LABEL = { booked: '待使用', in_progress: '使用中', completed: '已使用', cancelled: '已取消', pending_pay: '待支付' }

export default {
  data() { return { tabs: TABS, activeStatus: '', list: [], loading: false } },
  computed: {
    filteredList() {
      if (!this.activeStatus) return this.list
      return this.list.filter((i) => i.mainStatus === this.activeStatus)
    }
  },
  onShow() { this.load() },
  onPullDownRefresh() { this.load().finally(() => uni.stopPullDownRefresh()) },
  methods: {
    async load() {
      this.loading = true
      try { this.list = (await getMyAppointments()) || [] }
      finally { this.loading = false }
    },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/appointment/detail?id=' + id }) },
    formatDate(v) { if (!v) return '--'; return String(v).slice(0, 10) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.al-page { min-height: 100vh; background: var(--jst-color-page-bg); }
.al-tabs { white-space: nowrap; background: var(--jst-color-card-bg); border-bottom: 2rpx solid var(--jst-color-border); }
.al-tabs__item { display: inline-block; padding: 0 40rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: var(--jst-color-text-tertiary); position: relative; }
.al-tabs__item--active { color: var(--jst-color-brand); font-weight: 700; }
.al-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 24rpx; right: 24rpx; height: 4rpx; background: var(--jst-color-brand); border-radius: 2rpx; }

.al-list { padding: 8rpx 0 32rpx; }
.al-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.al-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.al-card__title { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); flex: 1; min-width: 0; }
.al-card__status { padding: 4rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.al-card__status--booked { background: var(--jst-color-warning-soft); color: var(--jst-color-warning); }
.al-card__status--completed { background: var(--jst-color-success-soft); color: var(--jst-color-success); }
.al-card__status--cancelled { background: var(--jst-color-gray-soft); color: var(--jst-color-text-tertiary); }
.al-card__row { display: flex; padding: 8rpx 0; font-size: 24rpx; }
.al-card__k { width: 140rpx; color: var(--jst-color-text-tertiary); }
.al-card__v { flex: 1; color: var(--jst-color-text); }
.al-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: var(--jst-color-text-tertiary); }
</style>
