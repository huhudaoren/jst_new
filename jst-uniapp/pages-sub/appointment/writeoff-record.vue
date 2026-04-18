<!-- 中文注释: 核销记录页
     角色分流:
       - 学生 (默认): 展示自己的权益核销申请记录 (jst_rights_writeoff_record 聚合)
       - 赛事方 / 平台运营: 保留旧行为, 拉 /jst/wx/writeoff/records (预约核销)
     路由参数:
       - anchor: 新建的记录 ID, 加载后定位 + 高亮 2 秒
       - rightsId: 仅查看某个权益的历史 -->
<template>
  <view class="wr-page" :style="{ paddingTop: navPaddingTop }">
    <!-- 学生侧: 状态 Tab -->
    <scroll-view v-if="mode === 'user'" class="wr-tabs" scroll-x>
      <view
        v-for="tab in tabs"
        :key="tab.value"
        :class="['wr-tabs__item', activeStatus === tab.value && 'wr-tabs__item--active']"
        @tap="onSwitchTab(tab.value)"
      >
        {{ tab.label }}
        <text v-if="countByStatus(tab.value)" class="wr-tabs__badge">{{ countByStatus(tab.value) }}</text>
      </view>
    </scroll-view>

    <view class="wr-list">
      <!-- 学生: 权益申请卡 -->
      <block v-if="mode === 'user'">
        <view
          v-for="item in filteredList"
          :key="'user-' + item.recordId"
          :id="'record-' + item.recordId"
          :class="['wr-card', 'wr-card--' + (item.status || 'pending'), highlightId === item.recordId && 'wr-card--highlight']"
        >
          <view class="wr-card__head">
            <text class="wr-card__title">{{ item.rightsName || ('核销单 ' + (item.writeoffNo || '')) }}</text>
            <text :class="['wr-card__tag', 'wr-card__tag--' + (item.status || 'pending')]">{{ statusLabel(item.status) }}</text>
          </view>
          <view class="wr-card__row">
            <text class="wr-card__k">核销金额</text>
            <text class="wr-card__v wr-card__v--amount">¥{{ item.useAmount != null ? item.useAmount : '0' }}</text>
          </view>
          <view class="wr-card__row">
            <text class="wr-card__k">单号</text>
            <text class="wr-card__v">{{ item.writeoffNo || '--' }}</text>
          </view>
          <view class="wr-card__row" v-if="item.applyRemark">
            <text class="wr-card__k">申请备注</text>
            <text class="wr-card__v">{{ item.applyRemark }}</text>
          </view>
          <view class="wr-card__row" v-if="item.auditRemark">
            <text class="wr-card__k">审核说明</text>
            <text class="wr-card__v">{{ item.auditRemark }}</text>
          </view>
          <view class="wr-card__row">
            <text class="wr-card__k">时间</text>
            <text class="wr-card__v">{{ formatTime(item.writeoffTime || item.createTime) }}</text>
          </view>
        </view>
      </block>

      <!-- 赛事方/运营: 扫码核销记录卡 (旧行为) -->
      <block v-if="mode === 'staff'">
        <view
          v-for="item in list"
          :key="'staff-' + item.writeoffItemId"
          class="wr-card"
        >
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
      </block>

      <jst-empty
        v-if="!loading && (mode === 'user' ? !filteredList.length : !list.length)"
        :text="emptyText"
        icon="📋"
        buttonText="查看我的权益"
        buttonUrl="/pages-sub/rights/center"
      />
      <u-loadmore v-if="list.length || loading" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')"></u-loadmore>
    </view>
  </view>
</template>

<script>
import { getWriteoffRecords } from '@/api/appointment'
import { getMyRightsWriteoffRecords } from '@/api/rights'
import { useUserStore } from '@/store/user'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'

const TABS = [
  { value: 'all', label: '全部' },
  { value: 'pending', label: '申请中' },
  { value: 'approved', label: '已核销' },
  { value: 'rejected', label: '已驳回' },
  { value: 'rolled_back', label: '已回退' }
]
const STATUS_LABEL = { pending: '申请中', approved: '已核销', rejected: '已驳回', rolled_back: '已回退' }

export default {
  components: { JstEmpty },
  data() {
    return {
      mode: 'user', // 'user' | 'staff'
      tabs: TABS,
      activeStatus: 'all',
      list: [], // staff 分页列表 / user 全量列表 (前端分 Tab)
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true,
      anchor: null,
      rightsId: null,
      highlightId: null,
      highlightTimer: null
    }
  },
  computed: {
    filteredList() {
      if (this.activeStatus === 'all') return this.list
      return this.list.filter(x => (x.status || 'pending') === this.activeStatus)
    },
    emptyText() {
      if (this.mode === 'user') {
        return this.activeStatus === 'all' ? '暂无核销记录' : ('暂无' + this.statusLabel(this.activeStatus) + '记录')
      }
      return '暂无核销记录'
    }
  },
  onLoad(query) {
    query = query || {}
    this.anchor = query.anchor ? Number(query.anchor) : null
    this.rightsId = query.rightsId ? Number(query.rightsId) : null
    const store = useUserStore()
    const roles = store.roles || []
    const isStaff = roles.includes('jst_partner') || roles.includes('jst_platform_op')
    this.mode = isStaff ? 'staff' : 'user'
    this.load(true)
  },
  onPullDownRefresh() { this.load(true).finally(() => uni.stopPullDownRefresh()) },
  onReachBottom() { if (this.mode === 'staff' && this.hasMore && !this.loading) this.load(false) },
  beforeDestroy() { if (this.highlightTimer) clearTimeout(this.highlightTimer) },
  methods: {
    async load(reset) {
      if (reset && this.mode === 'staff') { this.pageNum = 1; this.list = []; this.hasMore = true }
      if (this.mode === 'staff' && !this.hasMore && !reset) return
      this.loading = true
      try {
        if (this.mode === 'user') {
          // 2026-04-18: 改用后端统一端点 /jst/wx/rights/writeoff-records
          const res = await getMyRightsWriteoffRecords({
            rightsId: this.rightsId,
            pageNum: 1,
            pageSize: 200
          })
          // 兼容字段差异：后端新字段 applyTime/auditTime/amount/rightsId
          //                 旧页面字段 writeoffTime/createTime/useAmount/userRightsId
          const rows = ((res && res.rows) || []).map(r => Object.assign({}, r, {
            writeoffTime: r.auditTime || r.writeoffTime,
            createTime: r.applyTime || r.createTime,
            useAmount: r.amount != null ? r.amount : r.useAmount,
            applyRemark: r.remark != null ? r.remark : r.applyRemark,
            userRightsId: r.rightsId != null ? r.rightsId : r.userRightsId
          }))
          this.list = rows
          this.total = (res && res.total) || rows.length
          this.hasMore = false
          // 如有 anchor: 切换到对应状态 Tab + 滚动定位 + 高亮
          if (this.anchor) this.$nextTick(() => this.locateAnchor())
        } else {
          const res = await getWriteoffRecords({ pageNum: this.pageNum, pageSize: this.pageSize })
          const rows = (res && res.rows) || []
          this.total = (res && res.total) || 0
          this.list = reset ? rows : this.list.concat(rows)
          this.hasMore = this.list.length < this.total
          if (this.hasMore) this.pageNum += 1
        }
      } finally { this.loading = false }
    },
    locateAnchor() {
      const target = this.list.find(x => x.recordId === this.anchor)
      if (!target) return
      // 1) 切到该状态 Tab
      const st = target.status || 'pending'
      if (this.activeStatus !== 'all' && this.activeStatus !== st) this.activeStatus = st
      // 2) 等列表渲染后滚动定位
      setTimeout(() => {
        const query = uni.createSelectorQuery().in(this)
        query.select('#record-' + this.anchor).boundingClientRect((rect) => {
          if (rect && rect.top != null) {
            uni.pageScrollTo({ scrollTop: Math.max(0, rect.top - 120), duration: 300 })
          }
        }).exec()
        // 3) 高亮 2 秒
        this.highlightId = this.anchor
        this.highlightTimer = setTimeout(() => { this.highlightId = null }, 2000)
      }, 200)
    },
    onSwitchTab(v) { if (this.activeStatus === v) return; this.activeStatus = v },
    countByStatus(v) {
      if (v === 'all') return 0
      return this.list.filter(x => (x.status || 'pending') === v).length
    },
    statusLabel(s) { return STATUS_LABEL[s] || s || '申请中' },
    formatTime(v) { if (!v) return '--'; return String(v).replace('T', ' ').slice(0, 16) }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.wr-page { min-height: 100vh; background: $jst-bg-page; padding: 0 0 $jst-space-xl; }

.wr-tabs { white-space: nowrap; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 0; z-index: 10; }
.wr-tabs__item { display: inline-block; padding: 0 32rpx; height: 80rpx; line-height: 80rpx; font-size: $jst-font-sm; color: $jst-text-secondary; position: relative; }
.wr-tabs__item--active { color: $jst-brand; font-weight: $jst-weight-semibold; }
.wr-tabs__item--active::after { content: ''; position: absolute; bottom: 0; left: 16rpx; right: 16rpx; height: 4rpx; background: $jst-brand; border-radius: 2rpx; }
.wr-tabs__badge { display: inline-block; margin-left: 8rpx; padding: 0 10rpx; height: 28rpx; line-height: 28rpx; border-radius: 14rpx; background: $jst-brand-light; color: $jst-brand; font-size: 20rpx; font-weight: $jst-weight-semibold; }

.wr-list { padding: $jst-space-xs 0 $jst-space-xl; }
.wr-card { margin: 20rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; transition: background $jst-duration-normal $jst-easing, box-shadow $jst-duration-normal $jst-easing; }
.wr-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.wr-card__title { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; flex: 1; min-width: 0; }
.wr-card__time { font-size: 22rpx; color: $jst-text-secondary; }
.wr-card__tag { padding: 4rpx 16rpx; border-radius: $jst-radius-round; font-size: 22rpx; font-weight: $jst-weight-semibold; }
.wr-card__tag--pending { background: $jst-warning-light; color: $jst-warning; }
.wr-card__tag--approved { background: $jst-success-light; color: $jst-success; }
.wr-card__tag--rejected { background: rgba(235, 82, 82, 0.12); color: #EB5252; }
.wr-card__tag--rolled_back { background: $jst-bg-grey; color: $jst-text-secondary; }
.wr-card__row { display: flex; padding: 6rpx 0; font-size: 24rpx; }
.wr-card__k { width: 140rpx; color: $jst-text-secondary; }
.wr-card__v { flex: 1; color: $jst-text-primary; }
.wr-card__v--amount { color: $jst-brand; font-weight: $jst-weight-semibold; }

/* 高亮动画: 黄色光晕脉冲 2 秒 */
.wr-card--highlight { animation: jst-highlight-pulse 2s ease; box-shadow: 0 0 0 4rpx rgba(250, 173, 20, 0.32), $jst-shadow-md; }
@keyframes jst-highlight-pulse {
  0%   { box-shadow: 0 0 0 0 rgba(250, 173, 20, 0.5), $jst-shadow-sm; background: #FFF8E6; }
  40%  { box-shadow: 0 0 0 12rpx rgba(250, 173, 20, 0.3), $jst-shadow-md; background: #FFF4D6; }
  100% { box-shadow: 0 0 0 0 rgba(250, 173, 20, 0), $jst-shadow-sm; background: $jst-bg-card; }
}
</style>
