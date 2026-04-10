<!-- 中文注释: 渠道方 · 学生管理 (E1-CH-3 完整重构)
     对应原型: 小程序原型图/channel-students.png + channel-students.html
     功能: 搜索/Tab/学生卡片/4操作(代报名/查成绩/查证书/解绑) + 邀请绑定弹窗
     数据来源: GET /jst/wx/channel/students + POST unbind -->
<template>
  <view class="stu-page">
    <!-- 顶部导航 -->
    <view class="stu-header">
      <view class="stu-header__back" @tap="goBack">←</view>
      <text class="stu-header__title">学生管理</text>
      <text class="stu-header__action" @tap="showInvite = true">手动绑定</text>
    </view>

    <!-- 搜索栏 -->
    <view class="stu-search">
      <view class="stu-search__bar">
        <text class="stu-search__icon">🔍</text>
        <input class="stu-search__input" type="text" placeholder="搜索学生姓名、手机号..." v-model="keyword" @confirm="search" />
      </view>
    </view>

    <!-- 绑定二维码卡片 -->
    <view class="stu-qr" @tap="showInvite = true">
      <view class="stu-qr__card">
        <text class="stu-qr__label">👨‍🏫 渠道方专属 · 仅认证账号可见</text>
        <text class="stu-qr__title">我的学生绑定二维码</text>
        <view class="stu-qr__content">
          <view class="stu-qr__box">🔲</view>
          <view class="stu-qr__info">
            <text class="stu-qr__name">{{ channelName }}</text>
            <text class="stu-qr__school">{{ channelSchool || '竞赛通渠道方' }}</text>
            <view class="stu-qr__btns">
              <view class="stu-qr__btn" @tap.stop="copyLink">🔗 复制链接</view>
              <view class="stu-qr__btn" @tap.stop="showInvite = true">💾 保存图片</view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 筛选 Tab -->
    <view class="stu-tabs">
      <view v-for="tab in tabs" :key="tab.key" :class="['stu-tab', activeTab === tab.key ? 'stu-tab--active' : '']" @tap="switchTab(tab.key)">
        {{ tab.label }}({{ tab.count }})
      </view>
    </view>

    <!-- 学生列表 -->
    <view class="stu-list">
      <view v-for="stu in list" :key="stu.bindingId" class="stu-card">
        <view class="stu-card__main">
          <view class="stu-card__avatar" :style="{ background: getAvatarColor(stu.studentName) }">
            <text class="stu-card__avatar-text">{{ (stu.studentName || '').slice(0, 1) }}</text>
          </view>
          <view class="stu-card__info">
            <text class="stu-card__name">{{ stu.studentName || '--' }}</text>
            <text class="stu-card__meta">{{ stu.schoolName || '' }} · {{ stu.gradeName || '' }} · {{ stu.mobileMasked || '' }}</text>
            <text class="stu-card__time">绑定于 {{ formatDate(stu.bindTime) }} · 共报名 {{ stu.enrollCount || 0 }} 次</text>
          </view>
          <view v-if="stu.statusTag" class="stu-card__tag" :class="'stu-card__tag--' + stu.statusTag.type">
            {{ stu.statusTag.text }}
          </view>
        </view>
        <view class="stu-card__actions">
          <view class="stu-card__action stu-card__action--primary" @tap="goEnroll(stu)">📋 代报名</view>
          <view class="stu-card__action" @tap="goScore(stu)">🎯 成绩</view>
          <view class="stu-card__action" @tap="goCert(stu)">🏅 证书</view>
          <view class="stu-card__action stu-card__action--danger" @tap="confirmUnbind(stu)">🔓 解绑</view>
        </view>
      </view>

      <view v-if="hasMore" class="stu-loadmore" @tap="loadMore">
        <text>加载更多</text>
      </view>
      <view v-if="!list.length && !loading" class="stu-empty">
        <text class="stu-empty__icon">👥</text>
        <text class="stu-empty__title">暂无绑定学生</text>
        <text class="stu-empty__desc">邀请学生扫码绑定，即可为其代报名</text>
      </view>
    </view>

    <!-- 底部统计 -->
    <view v-if="list.length" class="stu-footer">
      <text class="stu-footer__info">共 <text class="stu-footer__bold">{{ total }}</text> 位学生</text>
    </view>

    <!-- 解绑倒计时弹窗 -->
    <jst-countdown-confirm
      :visible="showUnbind"
      :student-name="unbindTarget.studentName"
      @confirm="doUnbind"
      @cancel="showUnbind = false"
    />

    <!-- 邀请绑定弹窗 -->
    <jst-invite-modal
      :visible="showInvite"
      :channel-id="channelId"
      :channel-name="channelName"
      :channel-school="channelSchool"
      :bind-code="bindCode"
      @close="showInvite = false"
    />
  </view>
</template>

<script>
import { getChannelStudents, unbindStudent, getMyChannelApply } from '@/api/channel'
import { useUserStore } from '@/store/user'

const AVATAR_COLORS = [
  'linear-gradient(135deg, #3F51B5, #5C6BC0)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

export default {
  data() {
    return {
      loading: false,
      keyword: '',
      activeTab: 'all',
      tabs: [
        { key: 'all', label: '全部学生', count: 0 },
        { key: 'pending', label: '待支付', count: 0 },
        { key: 'enrolled', label: '已报名', count: 0 }
      ],
      list: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      hasMore: false,
      showUnbind: false,
      unbindTarget: {},
      showInvite: false,
      channelId: '',
      channelName: '',
      channelSchool: '',
      bindCode: ''
    }
  },
  onShow() { this.initChannel(); this.pageNum = 1; this.loadList() },
  methods: {
    async initChannel() {
      const store = useUserStore()
      const info = store.userInfo || {}
      this.channelName = info.nickname || info.channelName || '渠道方'
      try {
        const apply = await getMyChannelApply()
        if (apply) {
          this.channelId = apply.channelId || apply.id || ''
          this.channelName = apply.applyName || this.channelName
          this.channelSchool = apply.school || ''
          this.bindCode = apply.bindCode || ''
        }
      } catch (e) {}
    },

    async loadList() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize }
        if (this.keyword) params.keyword = this.keyword
        if (this.activeTab === 'pending') params.status = 'pending_pay'
        else if (this.activeTab === 'enrolled') params.status = 'enrolled'
        // history Tab 用 status=history
        const res = await getChannelStudents(params)
        const rows = (res && res.rows) || (Array.isArray(res) ? res : [])
        const total = (res && res.total) || rows.length
        if (this.pageNum === 1) {
          this.list = rows
        } else {
          this.list = this.list.concat(rows)
        }
        this.total = total
        this.hasMore = this.list.length < total
        // 更新 tab count
        this.tabs[0].count = total
      } catch (e) {
        if (this.pageNum === 1) this.list = []
      }
      this.loading = false
    },

    search() { this.pageNum = 1; this.loadList() },
    switchTab(key) { this.activeTab = key; this.pageNum = 1; this.loadList() },
    loadMore() { this.pageNum++; this.loadList() },

    getAvatarColor(name) {
      if (!name) return AVATAR_COLORS[0]
      return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
    },

    formatDate(v) {
      if (!v) return '--'
      return String(v).replace('T', ' ').slice(0, 10)
    },

    goEnroll(stu) {
      // CH-7 会补完整页面，本卡只需能跳且带 query
      uni.showToast({ title: '批量报名 CH-7 后续开放', icon: 'none' })
    },
    goScore(stu) {
      uni.navigateTo({ url: '/pages-sub/channel/student-score?studentId=' + (stu.studentId || stu.bindingId) + '&name=' + encodeURIComponent(stu.studentName || '') })
    },
    goCert(stu) {
      uni.navigateTo({ url: '/pages-sub/channel/student-cert?studentId=' + (stu.studentId || stu.bindingId) + '&name=' + encodeURIComponent(stu.studentName || '') })
    },

    // 解绑确认 (Q-01: 5秒倒计时)
    confirmUnbind(stu) {
      this.unbindTarget = stu
      this.showUnbind = true
    },
    async doUnbind() {
      this.showUnbind = false
      try {
        await unbindStudent(this.unbindTarget.bindingId)
        uni.showToast({ title: '已解绑', icon: 'success' })
        this.pageNum = 1
        this.loadList()
      } catch (e) {}
    },

    copyLink() {
      const link = `binding?channelId=${this.channelId || ''}`
      uni.setClipboardData({ data: link, success: () => { uni.showToast({ title: '链接已复制', icon: 'success' }) } })
    },

    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
.stu-page { min-height: 100vh; padding-bottom: calc(120rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }

/* 导航 */
.stu-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 0; z-index: 40; }
.stu-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.stu-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }
.stu-header__action { font-size: 26rpx; color: #3F51B5; font-weight: 600; padding: 12rpx 0 12rpx 24rpx; }

/* 搜索 */
.stu-search { padding: 24rpx 32rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); }
.stu-search__bar { display: flex; align-items: center; padding: 0 24rpx; height: 72rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-page-bg); }
.stu-search__icon { font-size: 28rpx; margin-right: 12rpx; }
.stu-search__input { flex: 1; font-size: 26rpx; color: var(--jst-color-text); }

/* 二维码卡片 */
.stu-qr { padding: 28rpx 32rpx 0; }
.stu-qr__card { background: linear-gradient(145deg, #1A237E 0%, #283593 50%, #3949AB 100%); border-radius: var(--jst-radius-lg); padding: 32rpx; position: relative; overflow: hidden; box-shadow: 0 16rpx 56rpx rgba(26,35,126,0.3); }
.stu-qr__card::before { content: ''; position: absolute; top: -60rpx; right: -60rpx; width: 280rpx; height: 280rpx; border-radius: 50%; background: rgba(255,255,255,0.06); }
.stu-qr__label { display: block; font-size: 22rpx; color: rgba(255,255,255,0.65); position: relative; z-index: 1; }
.stu-qr__title { display: block; font-size: 36rpx; font-weight: 900; color: #fff; margin: 8rpx 0 24rpx; position: relative; z-index: 1; }
.stu-qr__content { display: flex; align-items: center; gap: 32rpx; position: relative; z-index: 1; }
.stu-qr__box { width: 200rpx; height: 200rpx; border-radius: var(--jst-radius-md); background: #fff; display: flex; align-items: center; justify-content: center; font-size: 120rpx; box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.25); }
.stu-qr__info { flex: 1; }
.stu-qr__name { display: block; font-size: 32rpx; font-weight: 800; color: #fff; }
.stu-qr__school { display: block; margin-top: 8rpx; font-size: 24rpx; color: rgba(255,255,255,0.7); }
.stu-qr__btns { display: flex; gap: 16rpx; margin-top: 20rpx; }
.stu-qr__btn { flex: 1; height: 68rpx; background: rgba(255,255,255,0.18); border: 2rpx solid rgba(255,255,255,0.25); border-radius: var(--jst-radius-sm); color: #fff; font-size: 24rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 8rpx; }

/* Tab */
.stu-tabs { display: flex; gap: 16rpx; padding: 28rpx 32rpx 0; overflow-x: auto; }
.stu-tab { flex-shrink: 0; height: 64rpx; padding: 0 28rpx; border: 3rpx solid var(--jst-color-border); border-radius: var(--jst-radius-full); background: #fff; font-size: 26rpx; font-weight: 500; color: var(--jst-color-text-secondary); display: flex; align-items: center; white-space: nowrap; }
.stu-tab--active { border-color: #3F51B5; background: #EEF0FF; color: #3F51B5; font-weight: 700; }

/* 学生列表 */
.stu-list { padding: 28rpx 32rpx 0; }
.stu-card { background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); margin-bottom: 20rpx; overflow: hidden; }
.stu-card__main { display: flex; align-items: center; gap: 24rpx; padding: 28rpx 28rpx 20rpx; }
.stu-card__avatar { width: 88rpx; height: 88rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.stu-card__avatar-text { font-size: 40rpx; font-weight: 700; color: #fff; }
.stu-card__info { flex: 1; min-width: 0; }
.stu-card__name { display: block; font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); }
.stu-card__meta { display: block; margin-top: 4rpx; font-size: 24rpx; color: var(--jst-color-text-tertiary); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.stu-card__time { display: block; margin-top: 6rpx; font-size: 22rpx; color: #ccc; }
.stu-card__tag { flex-shrink: 0; padding: 6rpx 16rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; font-weight: 600; }
.stu-card__tag--warning { background: var(--jst-color-warning-soft); color: #B26A00; }
.stu-card__tag--success { background: var(--jst-color-success-soft); color: #0F7B3F; }
.stu-card__tag--info { background: var(--jst-color-brand-soft); color: var(--jst-color-brand); }
.stu-card__actions { display: flex; border-top: 2rpx solid var(--jst-color-border); }
.stu-card__action { flex: 1; height: 80rpx; display: flex; align-items: center; justify-content: center; gap: 8rpx; font-size: 24rpx; font-weight: 600; color: var(--jst-color-text-secondary); border-right: 2rpx solid var(--jst-color-border); }
.stu-card__action:last-child { border-right: none; }
.stu-card__action--primary { color: #3F51B5; }
.stu-card__action--danger { color: var(--jst-color-danger); }

.stu-loadmore { padding: 32rpx; text-align: center; font-size: 26rpx; color: #3F51B5; font-weight: 600; }
.stu-empty { text-align: center; padding: 96rpx 48rpx; }
.stu-empty__icon { display: block; font-size: 112rpx; margin-bottom: 32rpx; opacity: 0.6; }
.stu-empty__title { display: block; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text-secondary); }
.stu-empty__desc { display: block; margin-top: 16rpx; font-size: 28rpx; color: var(--jst-color-text-tertiary); }

/* 底部统计 */
.stu-footer { position: fixed; bottom: 0; left: 0; right: 0; background: rgba(255,255,255,0.97); border-top: 2rpx solid var(--jst-color-border); padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); box-shadow: 0 -8rpx 40rpx rgba(12,61,107,0.08); display: flex; align-items: center; z-index: 50; }
.stu-footer__info { font-size: 26rpx; color: var(--jst-color-text-tertiary); }
.stu-footer__bold { font-weight: 700; color: var(--jst-color-text); }
</style>
