<!-- 中文注释: 个人消息页；对应原型 小程序原型图/my-message.html；对应接口 GET /jst/wx/message/my -->
<template>
  <view class="message-page">
    <view class="message-page__header">
      <view class="message-page__back" @tap="goBack">←</view>
      <text class="message-page__header-title">我的消息</text>
      <view class="message-page__read-all" @tap="handleReadAll">全部已读</view>
    </view>

    <!-- 跳转提示 -->
    <view class="message-page__notice-tip" @tap="goNoticeMessage">
      <text class="message-page__notice-tip-icon">📢</text>
      <text class="message-page__notice-tip-text">平台公告、赛事通知、系统消息 → 点击查看公告消息中心</text>
    </view>

    <!-- Tab 筛选 -->
    <scroll-view class="message-page__tabs" scroll-x>
      <view class="message-page__tabs-inner">
        <view
          v-for="tab in tabs"
          :key="tab.value"
          class="message-page__tab"
          :class="{ 'message-page__tab--active': currentTab === tab.value }"
          @tap="switchTab(tab.value)"
        >
          {{ tab.label }}
          <text v-if="tab.dot" class="message-page__tab-dot"></text>
        </view>
      </view>
    </scroll-view>

    <jst-loading :loading="loading" text="消息加载中..." />

    <!-- 消息列表 -->
    <view v-if="filteredList.length" class="message-page__list">
      <view v-for="(group, gIdx) in groupedMessages" :key="gIdx" class="message-page__group">
        <text class="message-page__group-date">{{ group.date }}</text>
        <view
          v-for="msg in group.items"
          :key="msg.messageId"
          class="message-page__item"
          :class="{ 'message-page__item--unread': !msg.readFlag }"
          @tap="openMessage(msg)"
        >
          <view class="message-page__item-icon" :class="'message-page__item-icon--' + getTypeTheme(msg.msgType)">
            {{ getTypeIcon(msg.msgType) }}
          </view>
          <view class="message-page__item-body">
            <text class="message-page__item-title">{{ msg.title || '系统消息' }}</text>
            <text class="message-page__item-content">{{ msg.content || '' }}</text>
            <text v-if="msg.linkText" class="message-page__item-link">{{ msg.linkText }}</text>
          </view>
          <text class="message-page__item-time">{{ formatTime(msg.createTime) }}</text>
        </view>
      </view>
    </view>

    <jst-empty v-else-if="!loading" text="暂无消息" icon="📭" />
  </view>
</template>

<script>
import { getMyMessageList, markMessageRead, markAllMessageRead } from '@/api/message'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstEmpty, JstLoading },
  data() {
    return {
      loading: false,
      messageList: [],
      currentTab: 'all',
      tabs: [
        { label: '全部', value: 'all', dot: false },
        { label: '订单', value: 'order', dot: false },
        { label: '积分', value: 'points', dot: false },
        { label: '权益', value: 'rights', dot: false },
        { label: '物流', value: 'logistics', dot: false },
        { label: '提现结算', value: 'settlement', dot: false }
      ]
    }
  },
  computed: {
    filteredList() {
      if (this.currentTab === 'all') return this.messageList
      return this.messageList.filter(msg => msg.msgType === this.currentTab)
    },
    // 按日期分组
    groupedMessages() {
      const groups = {}
      this.filteredList.forEach(msg => {
        const dateKey = this.getDateGroup(msg.createTime)
        if (!groups[dateKey]) groups[dateKey] = { date: dateKey, items: [] }
        groups[dateKey].items.push(msg)
      })
      return Object.values(groups)
    }
  },
  onLoad() {
    this.loadMessages()
  },
  methods: {
    async loadMessages() {
      this.loading = true
      try {
        const res = await getMyMessageList()
        const rows = Array.isArray(res) ? res : (res && Array.isArray(res.rows) ? res.rows : [])
        this.messageList = rows
      } catch (e) {
        this.messageList = []
      } finally {
        this.loading = false
      }
    },
    switchTab(val) {
      this.currentTab = val
    },
    getDateGroup(time) {
      if (!time) return '未知'
      const d = new Date(time)
      if (isNaN(d.getTime())) return time
      const today = new Date()
      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)
      if (d.toDateString() === today.toDateString()) return '今天'
      if (d.toDateString() === yesterday.toDateString()) return '昨天'
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    formatTime(val) {
      if (!val) return ''
      const d = new Date(val)
      if (isNaN(d.getTime())) return val
      const today = new Date()
      if (d.toDateString() === today.toDateString()) {
        return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
      }
      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)
      if (d.toDateString() === yesterday.toDateString()) {
        return `昨天 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
      }
      return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
    },
    getTypeTheme(type) {
      const map = { order: 'blue', points: 'gold', rights: 'purple', logistics: 'teal', settlement: 'green' }
      return map[type] || 'gray'
    },
    getTypeIcon(type) {
      const map = { order: '💳', points: '💎', rights: '🎖', logistics: '📦', settlement: '💰' }
      return map[type] || '🔔'
    },
    async openMessage(msg) {
      // 标记已读
      if (!msg.readFlag && msg.messageId) {
        try {
          await markMessageRead(msg.messageId)
          msg.readFlag = true
        } catch (e) { /* ignore */ }
      }
      // 跳转对应业务页(按 linkUrl 或 msgType)
      if (msg.linkUrl) {
        uni.navigateTo({ url: msg.linkUrl })
      }
    },
    async handleReadAll() {
      try {
        await markAllMessageRead()
        this.messageList.forEach(msg => { msg.readFlag = true })
        uni.showToast({ title: '已全部标记为已读', icon: 'none' })
      } catch (e) { /* ignore */ }
    },
    goNoticeMessage() {
      uni.navigateTo({ url: '/pages-sub/notice/message' })
    },
    goBack() {
      if (getCurrentPages().length > 1) { uni.navigateBack(); return }
      uni.switchTab({ url: '/pages/my/index' })
    }
  }
}
</script>

<style scoped lang="scss">
.message-page { min-height: 100vh; padding-bottom: 60rpx; background: var(--jst-color-page-bg); }

.message-page__header { display: flex; align-items: center; padding: 24rpx; background: var(--jst-color-card-bg); }
.message-page__back { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 22rpx; background: var(--jst-color-page-bg); font-size: 30rpx; color: var(--jst-color-text); }
.message-page__header-title { flex: 1; margin-left: 16rpx; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }
.message-page__read-all { font-size: 26rpx; font-weight: 600; color: var(--jst-color-brand); }

/* 公告跳转提示 */
.message-page__notice-tip { display: flex; align-items: center; gap: 16rpx; margin: 24rpx 24rpx 0; padding: 24rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-brand-soft); }
.message-page__notice-tip-icon { font-size: 36rpx; flex-shrink: 0; }
.message-page__notice-tip-text { font-size: 24rpx; color: var(--jst-color-brand); line-height: 1.5; }

/* Tabs */
.message-page__tabs { margin-top: 24rpx; padding: 0 24rpx; }
.message-page__tabs-inner { display: inline-flex; gap: 16rpx; }
.message-page__tab { position: relative; padding: 12rpx 24rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-card-bg); font-size: 24rpx; color: var(--jst-color-text-secondary); white-space: nowrap; }
.message-page__tab--active { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); font-weight: 700; }
.message-page__tab-dot { position: absolute; top: 8rpx; right: 8rpx; width: 12rpx; height: 12rpx; border-radius: 50%; background: var(--jst-color-danger); }

/* 列表 */
.message-page__list { padding: 24rpx; }
.message-page__group { margin-bottom: 24rpx; }
.message-page__group-date { display: block; font-size: 24rpx; font-weight: 600; color: var(--jst-color-text-tertiary); margin-bottom: 16rpx; }

.message-page__item { display: flex; gap: 20rpx; padding: 24rpx; border-radius: var(--jst-radius-lg); background: var(--jst-color-card-bg); margin-bottom: 16rpx; box-shadow: 0 2rpx 8rpx rgba(20,30,60,0.04); }
.message-page__item--unread { border-left: 6rpx solid var(--jst-color-brand); }

.message-page__item-icon { display: flex; align-items: center; justify-content: center; width: 72rpx; height: 72rpx; border-radius: 20rpx; font-size: 32rpx; flex-shrink: 0; }
.message-page__item-icon--blue { background: var(--jst-color-brand-soft); }
.message-page__item-icon--gold { background: #FFF8E1; }
.message-page__item-icon--purple { background: var(--jst-color-purple-soft); }
.message-page__item-icon--teal { background: #E9FBF3; }
.message-page__item-icon--green { background: var(--jst-color-success-soft); }
.message-page__item-icon--gray { background: var(--jst-color-page-bg); }

.message-page__item-body { flex: 1; min-width: 0; }
.message-page__item-title { display: block; font-size: 28rpx; font-weight: 600; color: var(--jst-color-text); }
.message-page__item-content { display: block; margin-top: 8rpx; font-size: 24rpx; color: var(--jst-color-text-secondary); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.message-page__item-link { display: block; margin-top: 8rpx; font-size: 24rpx; color: var(--jst-color-brand); font-weight: 500; }
.message-page__item-time { font-size: 22rpx; color: var(--jst-color-text-tertiary); flex-shrink: 0; white-space: nowrap; }
</style>
