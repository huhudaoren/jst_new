<!-- 中文注释: 个人消息页；对应原型 小程序原型图/my-message.html；对应接口 GET /jst/wx/message/my -->
<template>
  <view class="message-page">
    <view class="message-page__header">
      <view class="message-page__back" @tap="goBack">←</view>
      <text class="message-page__header-title">我的消息</text>
      <u-button class="message-page__read-all" size="mini" plain @click="handleReadAll">全部已读</u-button>
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
    <u-skeleton v-if="loading" :loading="true" :rows="8" title :avatar="false" class="jst-page-skeleton" />

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
@import '@/styles/design-tokens.scss';

.message-page {
  min-height: 100vh;
  padding-bottom: 60rpx;
  background: $jst-bg-page;
}

.jst-page-skeleton {
  margin: 0 $jst-page-padding $jst-space-lg;
}

.message-page__header {
  display: flex;
  align-items: center;
  padding: $jst-space-lg $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.message-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: $jst-bg-page;
  font-size: $jst-font-md;
  color: $jst-text-primary;
}

.message-page__header-title {
  flex: 1;
  margin-left: $jst-space-md;
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.message-page__notice-tip {
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-brand-light;
}

.message-page__notice-tip-icon {
  flex-shrink: 0;
  font-size: $jst-font-xl;
}

.message-page__notice-tip-text {
  font-size: $jst-font-sm;
  line-height: 1.5;
  color: $jst-brand;
}

.message-page__tabs {
  margin-top: $jst-space-lg;
  padding: 0 $jst-page-padding;
}

.message-page__tabs-inner {
  display: inline-flex;
  gap: $jst-space-md;
}

.message-page__tab {
  position: relative;
  padding: $jst-space-sm $jst-space-lg;
  border-radius: $jst-radius-round;
  background: $jst-bg-card;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  white-space: nowrap;
}

.message-page__tab--active {
  background: $jst-brand-light;
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
}

.message-page__tab-dot {
  position: absolute;
  top: $jst-space-xs;
  right: $jst-space-xs;
  width: 12rpx;
  height: 12rpx;
  border-radius: $jst-radius-round;
  background: $jst-danger;
}

.message-page__list {
  padding: $jst-space-lg $jst-page-padding 0;
}

.message-page__group {
  margin-bottom: $jst-space-lg;
}

.message-page__group-date {
  display: block;
  margin-bottom: $jst-space-md;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-text-placeholder;
}

.message-page__item {
  display: flex;
  gap: 20rpx;
  margin-bottom: $jst-space-md;
  padding: $jst-space-lg;
  border-radius: $jst-radius-lg;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.message-page__item--unread {
  border-left: 6rpx solid $jst-brand;
}

.message-page__item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  font-size: $jst-font-lg;
  flex-shrink: 0;
}

.message-page__item-icon--blue {
  background: $jst-brand-light;
}

.message-page__item-icon--gold {
  background: $jst-gold-light;
}

.message-page__item-icon--purple {
  background: $jst-brand-light;
}

.message-page__item-icon--teal {
  background: $jst-success-light;
}

.message-page__item-icon--green {
  background: $jst-success-light;
}

.message-page__item-icon--gray {
  background: $jst-bg-page;
}

.message-page__item-body {
  flex: 1;
  min-width: 0;
}

.message-page__item-title {
  display: block;
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}

.message-page__item-content {
  display: -webkit-box;
  margin-top: $jst-space-xs;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  font-size: $jst-font-sm;
  line-height: 1.5;
  color: $jst-text-secondary;
}

.message-page__item-link {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-brand;
}

.message-page__item-time {
  flex-shrink: 0;
  white-space: nowrap;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

::v-deep .message-page__read-all.u-button {
  min-width: 138rpx;
  height: 58rpx;
  border-color: $jst-brand;
  border-radius: $jst-radius-round;
  color: $jst-brand;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
}
</style>
