<!-- 中文注释: 我的权益
     对应原型: 小程序原型图/rights-center.html
     调用接口: GET /jst/wx/rights/my?status= -->
<template>
  <view class="rc-page">
    <view class="rc-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="rc-hero__cell"><text class="rc-hero__num">{{ stats.available || 0 }}</text><text class="rc-hero__lbl">可用权益</text></view>
      <view class="rc-hero__cell"><text class="rc-hero__num">{{ stats.applying || 0 }}</text><text class="rc-hero__lbl">申请中</text></view>
      <view class="rc-hero__cell"><text class="rc-hero__num">{{ (stats.used || 0) + (stats.expired || 0) }}</text><text class="rc-hero__lbl">已失效</text></view>
    </view>

    <scroll-view class="rc-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['rc-tabs__item', activeStatus === tab.value && 'rc-tabs__item--active']"
        @tap="onChange(tab.value)"
      >{{ tab.label }}</view>
    </scroll-view>

    <view class="rc-list">
      <view v-for="item in list" :key="item.userRightsId" class="rc-card" @tap="goDetail(item.userRightsId)">
        <view class="rc-card__head">
          <text class="rc-card__name">{{ item.rightsName || '--' }}</text>
          <text :class="['rc-card__status', 'rc-card__status--' + item.status]">{{ item.status === 'applying' ? '审核中' : statusLabel(item.status) }}</text>
        </view>
        <view class="rc-card__meta">
          <text class="rc-card__m">{{ quotaText(item) }}</text>
          <text class="rc-card__m">有效期至 {{ formatDate(item.validEnd) }}</text>
        </view>
        <view class="rc-card__actions">
          <text class="rc-card__link" @tap.stop="goRecords(item.userRightsId)">查看申请记录</text>
          <button v-if="item.status === 'available' && !item.expired" class="rc-card__btn" @tap.stop="goApply(item.userRightsId)">立即使用</button>
        </view>
      </view>
      <jst-empty
        v-if="!list.length && !loading"
        text="暂无权益"
        icon="📋"
        buttonText="看看热门赛事"
        buttonUrl="switchTab:/pages/contest/index"
      />
      <u-loadmore v-if="loading" status="loading" />
      <u-loadmore v-if="!hasMore && list.length" status="nomore" />
    </view>
  </view>
</template>

<script>
import { getMyRights } from '@/api/rights'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'

// 对齐原型 3 状态: 可用 / 申请中 / 已失效 (已失效合并 used + expired)
const TABS = [
  { value: 'available', label: '可用权益' },
  { value: 'applying', label: '申请中' },
  { value: 'invalid', label: '已失效' }
]
const STATUS_LABEL = { available: '可用', applying: '申请中', used: '已使用', expired: '已过期', invalid: '已失效' }

export default {
  components: { JstEmpty },
  data() { return { tabs: TABS, activeStatus: 'available', list: [], stats: { available: 0, applying: 0, used: 0 }, pageNum: 1, pageSize: 10, total: 0, loading: false, hasMore: true } },
  onShow() { this.load(true) },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.hasMore && !this.loading) this.load(false) },
  methods: {
    async load(reset) {
      if (reset) { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (!this.hasMore) return
      this.loading = true
      try {
        // 'invalid' (已失效) = used + expired, 后端未直接支持, 故不传 status 拉全部后客户端过滤
        // TODO(BE): 可考虑后端 selectMyRights 支持 status='invalid' 直接返回 used+expired 合集, 省去客户端过滤
        const query = { pageNum: this.pageNum, pageSize: this.pageSize }
        if (this.activeStatus !== 'invalid') query.status = this.activeStatus
        const res = await getMyRights(query)
        let rows = (res && res.rows) || []
        this.total = (res && res.total) || 0
        if (res && res.stats) this.stats = res.stats
        if (this.activeStatus === 'invalid') {
          rows = rows.filter(r => r.status === 'used' || r.status === 'expired' || r.expired)
        }
        this.list = reset ? rows : this.list.concat(rows)
        this.hasMore = this.list.length < this.total && this.activeStatus !== 'invalid' // invalid 走全量过滤, 不分页
        if (this.hasMore) this.pageNum += 1
      } finally { this.loading = false }
    },
    onChange(v) { if (this.activeStatus === v) return; this.activeStatus = v; this.load(true) },
    goDetail(id) { uni.navigateTo({ url: '/pages-sub/rights/detail?id=' + id }) },
    goApply(id) { uni.navigateTo({ url: '/pages-sub/rights/writeoff-apply?id=' + id }) },
    goRecords(id) { uni.navigateTo({ url: '/pages-sub/appointment/writeoff-record?rightsId=' + id }) },
    // MyRightsVO: quotaMode('amount'|'count') + quotaValue + remainQuota
    quotaText(item) {
      const mode = item && item.quotaMode
      const remain = item && item.remainQuota != null ? item.remainQuota : '--'
      const total = item && item.quotaValue != null ? item.quotaValue : '--'
      if (mode === 'amount') return `剩余额度 ¥${remain} / 总 ¥${total}`
      return `剩余次数 ${remain} / 总 ${total}`
    },
    formatDate(v) { if (!v) return '--'; return String(v).slice(0, 10) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';
.rc-page { min-height: 100vh; background: $jst-bg-page; padding-bottom: $jst-space-xxl; }
.rc-hero { display: flex; padding: 72rpx $jst-space-xl 56rpx; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; border-bottom-left-radius: 40rpx; border-bottom-right-radius: 40rpx; }
.rc-hero__cell { flex: 1; text-align: center; }
.rc-hero__num { display: block; font-size: $jst-font-xxl; font-weight: $jst-weight-semibold; }
.rc-hero__lbl { display: block; margin-top: 6rpx; font-size: $jst-font-xs; color: rgba(255,255,255,0.76); }

.rc-tabs { white-space: nowrap; margin-top: $jst-space-md; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
.rc-tabs__item { display: inline-block; padding: 0 40rpx; height: 88rpx; line-height: 88rpx; font-size: $jst-font-sm; color: $jst-text-secondary; position: relative; }
.rc-tabs__item--active { color: $jst-success; font-weight: $jst-weight-semibold; }
.rc-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 20rpx; right: 20rpx; height: 4rpx; background: $jst-success; border-radius: 2rpx; }

.rc-list { padding: $jst-space-xs 0 $jst-space-xl; }
.rc-card { margin: 20rpx $jst-space-xl 0; padding: $jst-space-lg $jst-space-xl; background: $jst-bg-card; border-radius: $jst-radius-xl; box-shadow: $jst-shadow-sm; transition: transform $jst-duration-fast $jst-easing; &:active { transform: scale(0.98); } }
.rc-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: $jst-space-md; }
.rc-card__name { font-size: $jst-font-base; font-weight: $jst-weight-semibold; color: $jst-text-primary; flex: 1; min-width: 0; }
.rc-card__status { padding: $jst-space-xs $jst-space-md; border-radius: $jst-radius-round; font-size: $jst-font-xs; background: $jst-success-light; color: $jst-success; }
.rc-card__status--applying { background: $jst-warning-light; color: $jst-warning; }
.rc-card__status--used, .rc-card__status--expired { background: $jst-bg-grey; color: $jst-text-secondary; }
.rc-card__meta { display: flex; flex-wrap: wrap; gap: $jst-space-sm; }
.rc-card__m { padding: 6rpx 14rpx; border-radius: $jst-radius-round; background: $jst-bg-page; font-size: $jst-font-xs; color: $jst-text-regular; }
.rc-card__actions { display: flex; justify-content: space-between; align-items: center; margin-top: 20rpx; }
.rc-card__link { font-size: $jst-font-xs; color: $jst-success; text-decoration: underline; padding: 10rpx 0; }
.rc-card__btn { height: 72rpx; line-height: 72rpx; padding: 0 $jst-space-xl; border-radius: $jst-radius-round; background: linear-gradient(135deg, darken($jst-success, 15%), $jst-success); color: $jst-text-inverse; font-size: $jst-font-sm; font-weight: $jst-weight-semibold; border: none; }
</style>
