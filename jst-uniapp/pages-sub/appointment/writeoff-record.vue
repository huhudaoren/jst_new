<!-- 中文注释: 核销记录 (赛事方/运营)
     对应原型: 小程序原型图/writeoff-record.png
     调用接口: GET /jst/wx/writeoff/records?pageNum=&pageSize= -->
<template>
  <view class="wr-page" :style="{ paddingTop: navPaddingTop }">
    <view class="wr-list">
      <view v-for="item in list" :key="item.writeoffItemId" class="wr-card">
        <view class="wr-card__head">
          <text class="wr-card__title">{{ item.itemName || item.contestName || '——' }}</text>
          <text class="wr-card__time">{{ formatTime(item.writeoffTime || item.createTime) }}</text>
        </view>
        <view class="wr-card__row">
          <text class="wr-card__k">预约人</text>
          <text class="wr-card__v">{{ item.participantName || '--' }}</text>
        </view>
        <view class="wr-card__row">
          <text class="wr-card__k">赛事</text>
          <text class="wr-card__v">{{ item.contestName || '--' }}</text>
        </view>
      </view>
      <u-empty v-if="!list.length && !loading" mode="data" text="暂无核销记录"></u-empty>
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')"></u-loadmore>
    </view>
  </view>
</template>

<script>
import { getWriteoffRecords } from '@/api/appointment'
import { useUserStore } from '@/store/user'

export default {
  data() { return { skeletonShow: true, /* [visual-effect] */ list: [], pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true } },
  onLoad() {
    // POLISH-D 权限门: jst_partner / jst_platform_op
    const store = useUserStore()
    const roles = store.roles || []
    const allowed = roles.includes('jst_partner') || roles.includes('jst_platform_op')
    if (!allowed) {
      uni.showToast({ title: '无查看权限', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 800)
      return
    }
    this.load(true)
  },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        const res = await getWriteoffRecords({ pageNum: this.pageNum, pageSize: this.pageSize })
        const rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.wr-page { min-height: 100vh; background: $jst-bg-page; padding: $jst-space-md 0 $jst-space-xl; }
.wr-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.wr-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.wr-card__title { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; flex: 1; min-width: 0; }
.wr-card__time { font-size: 22rpx; color: $jst-text-secondary; }
.wr-card__row { display: flex; padding: 6rpx 0; font-size: 24rpx; }
.wr-card__k { width: 140rpx; color: $jst-text-secondary; }
.wr-card__v { flex: 1; color: $jst-text-primary; }
</style>
