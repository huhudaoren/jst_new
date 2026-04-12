<!-- 中文注释: 个人预约 · 我的预约
     对应原型: 小程序原型图/my-reserve.png
     调用接口: GET /jst/wx/appointment/my -->
<template>
  <view class="al-page">
    <scroll-view class="al-tabs" scroll-x :style="{ paddingTop: navPaddingTop }">
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
          <u-tag
            :text="statusLabel(item.mainStatus)"
            :type="item.mainStatus === 'booked' ? 'warning' : (item.mainStatus === 'completed' ? 'success' : 'info')"
            size="mini"
            shape="circle"
          ></u-tag>
        </view>
        <view class="al-card__row"><text class="al-card__k">日期</text><text class="al-card__v">{{ formatDate(item.appointmentDate) }} · {{ item.sessionCode || '--' }}</text></view>
        <view v-if="item.participantName" class="al-card__row"><text class="al-card__k">参赛者</text><text class="al-card__v">{{ item.participantName }}</text></view>
        <!-- POLISH-BATCH2 F: writeoffProgress 后端 AppointmentListVO 未提供, 暂隐藏核销进度行, 待后端补 writeoffDoneCount/writeoffTotalCount 字段 -->
        <view v-if="item.teamName" class="al-card__row"><text class="al-card__k">团队</text><text class="al-card__v">{{ item.teamName }}</text></view>
      </view>
      <u-empty v-if="!filteredList.length && !loading" mode="data" text="暂无预约记录"></u-empty>
      <u-loadmore v-if="loading" status="loading"></u-loadmore>
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
  data() { return { skeletonShow: true, /* [visual-effect] */ tabs: TABS, activeStatus: '', list: [], loading: false } },
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
@import '@/styles/design-tokens.scss';

.al-page { min-height: 100vh; background: $jst-bg-page; }
.al-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.al-tabs__item { display: inline-block; padding: 0 40rpx; height: 88rpx; line-height: 88rpx; font-size: 26rpx; color: $jst-text-secondary; position: relative; }
.al-tabs__item--active { color: $jst-brand; font-weight: 600; }
.al-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 24rpx; right: 24rpx; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }

.al-list { padding: 8rpx 0 32rpx; }
.al-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.al-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.al-card__title { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; flex: 1; min-width: 0; }
.al-card__row { display: flex; padding: 8rpx 0; font-size: 24rpx; }
.al-card__k { width: 140rpx; color: $jst-text-secondary; }
.al-card__v { flex: 1; color: $jst-text-primary; }
.al-empty { padding: 80rpx; text-align: center; font-size: 24rpx; color: $jst-text-secondary; }
</style>
