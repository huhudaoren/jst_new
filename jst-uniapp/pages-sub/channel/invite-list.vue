<!-- 中文注释: 渠道方 · 我邀请的渠道列表（SALES-DISTRIBUTION plan-02）
     调用接口:
       GET /jst/wx/channel/invite/list              下级渠道列表（全量，不分页）
       GET /jst/wx/channel/distribution/ledger      下级渠道分销明细（按 invitee 聚合月度分润）
     空态 CTA: 引导去「邀请码」分享 -->
<template>
  <view class="il-page">
    <!-- Hero -->
    <view class="il-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="il-hero__nav">
        <view class="il-hero__back" @tap="goBack">←</view>
        <text class="il-hero__title">我邀请的渠道</text>
        <view class="il-hero__action" @tap="goInviteCode">我的邀请码</view>
      </view>
      <view class="il-hero__stats">
        <view class="il-hero__stat">
          <text class="il-hero__stat-num">{{ stats.total }}</text>
          <text class="il-hero__stat-label">下级总数</text>
        </view>
        <view class="il-hero__stat">
          <text class="il-hero__stat-num il-hero__stat-num--gold">{{ stats.monthNew }}</text>
          <text class="il-hero__stat-label">本月新增</text>
        </view>
        <view class="il-hero__stat">
          <text class="il-hero__stat-num">{{ stats.activeRecent }}</text>
          <text class="il-hero__stat-label">近 3 月活跃</text>
        </view>
      </view>
    </view>

    <!-- Tab 筛选 -->
    <view class="il-tabs">
      <view
        v-for="tab in tabs" :key="tab.value"
        :class="['il-tabs__item', activeTab === tab.value && 'il-tabs__item--active']"
        @tap="onChangeTab(tab.value)"
      >{{ tab.label }}</view>
    </view>

    <!-- 骨架屏 -->
    <view v-if="loading && !allList.length" class="il-list">
      <view v-for="i in 4" :key="i" class="il-item">
        <u-skeleton rows="2" :loading="true" animation="wave" />
      </view>
    </view>

    <!-- 列表 -->
    <view v-else class="il-list">
      <view
        v-for="item in visibleList"
        :key="item.inviteId"
        class="il-item"
      >
        <view class="il-item__avatar">
          <text class="il-item__avatar-text">{{ avatarText(item) }}</text>
        </view>
        <view class="il-item__info">
          <view class="il-item__name-row">
            <!-- TODO(backend): 后端 invite/list 需补 inviteeChannelName/authType/avatarUrl 字段（当前 JstChannelInvite 仅有 inviteeChannelId） -->
            <text class="il-item__name">{{ item.inviteeChannelName || '渠道 #' + item.inviteeChannelId }}</text>
            <u-tag :text="statusLabel(item.status)" :type="statusTagType(item.status)" size="mini" shape="circle" plain />
          </view>
          <text class="il-item__meta">
            绑定于 {{ formatDate(item.invitedAt || item.createTime) }}
            <text v-if="item.commissionRate != null"> · 费率 {{ (item.commissionRate * 100).toFixed(1) }}%</text>
          </text>
          <view class="il-item__amount-row">
            <text class="il-item__amount-label">本月为我带来</text>
            <text class="il-item__amount">¥{{ formatAmount(amountMap[item.inviteeChannelId] || '0.00') }}</text>
          </view>
        </view>
      </view>

      <jst-empty
        v-if="!loading && !visibleList.length"
        :text="emptyText"
        icon="📭"
        button-text="去分享邀请码"
        button-url="/pages-sub/channel/invite-code"
      />
      <u-loadmore v-if="visibleList.length" :status="'nomore'" margin-top="20" />
    </view>
  </view>
</template>

<script>
import { listInvited, listDistributionLedger } from '@/api/invite'

const STATUS_LABEL = {
  active: '生效中',
  pending: '待认证',
  approved: '已认证',
  rejected: '被拒绝',
  suspended: '已暂停',
  unbound: '已解绑'
}
const STATUS_TYPE = {
  active: 'success',
  approved: 'success',
  pending: 'warning',
  rejected: 'error',
  suspended: 'info',
  unbound: 'info'
}

const TABS = [
  { value: 'all', label: '全部' },
  { value: 'month', label: '本月新增' },
  { value: 'recent', label: '近 3 月活跃' }
]

export default {
  data() {
    return {
      allList: [],
      amountMap: {}, // { inviteeChannelId: 本月分销金额 }
      activeTab: 'all',
      tabs: TABS,
      loading: true
    }
  },
  computed: {
    navPaddingTop() {
      const si = uni.getSystemInfoSync()
      return (si.statusBarHeight || 20) + 'px'
    },
    stats() {
      const now = new Date()
      const ym = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0')
      const threeMonthAgo = new Date(now.getTime() - 90 * 24 * 3600 * 1000)
      return {
        total: this.allList.length,
        monthNew: this.allList.filter(i => String(i.invitedAt || i.createTime || '').startsWith(ym)).length,
        activeRecent: this.allList.filter(i => {
          if (i.status !== 'active' && i.status !== 'approved') return false
          const t = new Date(String(i.invitedAt || i.createTime || '').replace('T', ' '))
          return t >= threeMonthAgo
        }).length
      }
    },
    visibleList() {
      const now = new Date()
      const ym = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0')
      if (this.activeTab === 'month') {
        return this.allList.filter(i => String(i.invitedAt || i.createTime || '').startsWith(ym))
      }
      if (this.activeTab === 'recent') {
        const threeMonthAgo = new Date(now.getTime() - 90 * 24 * 3600 * 1000)
        return this.allList.filter(i => {
          if (i.status !== 'active' && i.status !== 'approved') return false
          const t = new Date(String(i.invitedAt || i.createTime || '').replace('T', ' '))
          return t >= threeMonthAgo
        })
      }
      return this.allList
    },
    emptyText() {
      if (this.activeTab === 'month') return '本月还没有新增下级'
      if (this.activeTab === 'recent') return '近 3 月没有活跃下级'
      return '你还没有邀请下级渠道'
    }
  },
  onLoad() {
    this.loadAll()
  },
  onPullDownRefresh() {
    this.loadAll().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    goBack() {
      if (getCurrentPages().length > 1) uni.navigateBack()
      else uni.switchTab({ url: '/pages/my/index' })
    },
    goInviteCode() {
      uni.navigateTo({ url: '/pages-sub/channel/invite-code' })
    },
    async loadAll() {
      await Promise.all([this.loadList(), this.loadMonthAmounts()])
    },
    async loadList() {
      this.loading = true
      try {
        const data = await listInvited()
        this.allList = Array.isArray(data) ? data : (data && data.rows) || []
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    async loadMonthAmounts() {
      // 拉本月分销 ledger 聚合「本月为我带来」金额；一次拉 100 条，足以覆盖活跃 invitee
      try {
        const res = await listDistributionLedger({ pageNum: 1, pageSize: 100 })
        const rows = (res && res.rows) || []
        const now = new Date()
        const ym = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0')
        const map = {}
        rows.forEach(r => {
          if (!String(r.createTime || '').startsWith(ym)) return
          if (r.status === 'cancelled') return
          const id = r.inviteeChannelId
          if (!id) return
          map[id] = (Number(map[id] || 0) + Number(r.amount || 0)).toFixed(2)
        })
        this.amountMap = map
      } catch (e) {}
    },
    onChangeTab(v) { this.activeTab = v },
    avatarText(item) {
      const name = item.inviteeChannelName || ''
      if (name) return name.slice(0, 1)
      return '#'
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '未知' },
    statusTagType(s) { return STATUS_TYPE[s] || 'info' },
    formatDate(v) {
      if (!v) return '--'
      return String(v).replace('T', ' ').slice(0, 10)
    },
    formatAmount(v) {
      if (v == null || v === '') return '0.00'
      return Number(v).toFixed(2)
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.il-page { min-height: 100vh; padding-bottom: 60rpx; background: $jst-bg-page; }

/* Hero */
.il-hero {
  padding: 0 $jst-space-xl 56rpx;
  background: linear-gradient(150deg, $jst-indigo 0%, $jst-indigo-light 55%, $jst-indigo 120%);
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
}
.il-hero__nav {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 24rpx; margin-bottom: $jst-space-xl;
}
.il-hero__back {
  width: 64rpx; height: 64rpx; border-radius: $jst-radius-sm;
  background: rgba(255, 255, 255, 0.18);
  color: $jst-text-inverse; text-align: center;
  line-height: 64rpx; font-size: 36rpx;
}
.il-hero__title {
  flex: 1; text-align: center;
  font-size: 34rpx; font-weight: 600;
  color: $jst-text-inverse;
}
.il-hero__action {
  padding: 10rpx 24rpx;
  border-radius: $jst-radius-round;
  background: rgba(255, 213, 79, 0.22);
  border: 2rpx solid rgba(255, 213, 79, 0.5);
  color: $jst-gold; font-size: 24rpx; font-weight: 600;
}

.il-hero__stats {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 16rpx;
}
.il-hero__stat {
  padding: $jst-space-md 0;
  text-align: center;
  background: rgba(255, 255, 255, 0.12);
  border: 2rpx solid rgba(255, 255, 255, 0.18);
  border-radius: $jst-radius-md;
}
.il-hero__stat-num {
  display: block; font-size: 44rpx; font-weight: 700;
  color: $jst-text-inverse;
}
.il-hero__stat-num--gold { color: $jst-gold; }
.il-hero__stat-label {
  display: block; margin-top: 6rpx;
  font-size: 22rpx; color: rgba(255, 255, 255, 0.78);
}

/* Tabs */
.il-tabs {
  display: flex; padding: 0 $jst-space-xl;
  margin-top: $jst-space-md;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  margin-left: $jst-space-xl;
  margin-right: $jst-space-xl;
  box-shadow: $jst-shadow-sm;
}
.il-tabs__item {
  flex: 1; padding: 24rpx 0;
  text-align: center;
  font-size: 26rpx; color: $jst-text-secondary;
  position: relative;
}
.il-tabs__item--active {
  color: $jst-indigo; font-weight: $jst-weight-semibold;
}
.il-tabs__item--active::after {
  content: ''; position: absolute;
  bottom: 8rpx; left: 36%; right: 36%;
  height: 4rpx; background: $jst-indigo;
  border-radius: 2rpx;
}

/* List */
.il-list { padding: $jst-space-md $jst-space-xl 0; }

.il-item {
  display: flex; gap: $jst-space-md;
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
  margin-bottom: $jst-space-md;
}
.il-item__avatar {
  width: 88rpx; height: 88rpx; flex-shrink: 0;
  border-radius: $jst-radius-round;
  background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light);
  display: flex; align-items: center; justify-content: center;
}
.il-item__avatar-text {
  font-size: 36rpx; font-weight: 700;
  color: $jst-text-inverse;
}
.il-item__info { flex: 1; min-width: 0; }
.il-item__name-row {
  display: flex; align-items: center; gap: $jst-space-sm;
  margin-bottom: 8rpx;
}
.il-item__name {
  flex: 1; min-width: 0;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.il-item__meta {
  display: block; font-size: $jst-font-xs;
  color: $jst-text-secondary;
}
.il-item__amount-row {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: $jst-space-sm;
  padding-top: $jst-space-sm;
  border-top: 2rpx dashed $jst-border;
}
.il-item__amount-label {
  font-size: $jst-font-xs; color: $jst-text-secondary;
}
.il-item__amount {
  font-size: $jst-font-md;
  font-weight: 700;
  color: $jst-warning;
}
</style>
