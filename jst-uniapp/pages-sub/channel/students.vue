<!-- 中文注释: 渠道方 · 学生管理 (E1-CH-3 完整重构)
     对应原型: 小程序原型图/channel-students.png + channel-students.html
     功能: 搜索/Tab/学生卡片/4操作(代报名/查成绩/查证书/解绑) + 邀请绑定弹窗
     数据来源: GET /jst/wx/channel/students + POST unbind -->
<template>
  <view class="stu-page">
    <!-- 顶部导航 -->
    <view class="stu-header" :style="{ paddingTop: navPaddingTop }">
      <view class="stu-header__back" @tap="goBack">←</view>
      <text class="stu-header__title">学生管理</text>
      <text class="stu-header__action" @tap="showInvite = true">手动绑定</text>
    </view>

    <!-- 搜索栏 -->
    <view class="stu-search">
      <u-search
        v-model="keyword"
        placeholder="搜索学生姓名、手机号..."
        searchIconSize="30rpx"
        :showAction="false"
        bgColor="transparent"
        @search="search"
      ></u-search>
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
      <view v-for="stu in list" :key="stu.bindingId" class="stu-card" :class="{ 'stu-card--checked': isChecked(stu) }">
        <view class="stu-card__main">
          <!-- 中文注释: 批量选择 checkbox -->
          <view class="stu-card__checkbox" @tap.stop="toggleCheck(stu)">
            <view class="stu-card__checkbox-inner" :class="{ 'stu-card__checkbox-inner--on': isChecked(stu) }">
              <text v-if="isChecked(stu)" class="stu-card__checkbox-tick">✓</text>
            </view>
          </view>
          <view class="stu-card__avatar" :style="{ background: getAvatarColor(stu.studentName) }">
            <text class="stu-card__avatar-text">{{ (stu.studentName || '').slice(0, 1) }}</text>
          </view>
          <view class="stu-card__info">
            <text class="stu-card__name">{{ stu.studentName || '--' }}</text>
            <text class="stu-card__meta">{{ stu.schoolName || '' }} · {{ stu.gradeName || '' }} · {{ stu.mobileMasked || '' }}</text>
            <text class="stu-card__time">绑定于 {{ formatDate(stu.bindTime) }} · 共报名 {{ stu.enrollCount || 0 }} 次</text>
          </view>
          <u-tag v-if="stu.statusTag" :text="stu.statusTag.text" :type="stu.statusTag.type || 'info'" size="mini" shape="circle"></u-tag>
        </view>
        <view class="stu-card__actions">
          <view class="stu-card__action stu-card__action--primary" @tap="goEnroll(stu)">📋 代报名</view>
          <view class="stu-card__action" @tap="goScore(stu)">🎯 成绩</view>
          <view class="stu-card__action" @tap="goCert(stu)">🏅 证书</view>
          <view class="stu-card__action stu-card__action--danger" @tap="confirmUnbind(stu)">🔓 解绑</view>
        </view>
      </view>

      <u-loadmore v-if="list.length" :status="loading ? 'loading' : (hasMore ? 'loadmore' : 'nomore')" @loadmore="loadMore" />
      <u-empty v-if="!list.length && !loading" mode="data" text="暂无绑定学生"></u-empty>
    </view>

    <!-- 底部统计 -->
    <view v-if="list.length && checkedCount === 0" class="stu-footer">
      <text class="stu-footer__info">共 <text class="stu-footer__bold">{{ total }}</text> 位学生</text>
    </view>

    <!-- 中文注释: 批量操作 footer — count>0 时 translateY 滑入 -->
    <view
      class="stu-batch-footer"
      :class="{ 'stu-batch-footer--visible': checkedCount > 0 }"
    >
      <view class="stu-batch-footer__left">
        <text class="stu-batch-footer__count">已选 <text class="stu-batch-footer__count-num">{{ checkedCount }}</text> 人</text>
        <text class="stu-batch-footer__sep">·</text>
        <text class="stu-batch-footer__link" @tap="selectAll">全选</text>
        <text class="stu-batch-footer__sep">/</text>
        <text class="stu-batch-footer__link" @tap="clearChecked">清空</text>
      </view>
      <view class="stu-batch-footer__right" @tap="goBatchEnroll">
        <text class="stu-batch-footer__btn-text">📝 批量代报名</text>
      </view>
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
  'linear-gradient(135deg, #283593, #5C6BC0)',
  'linear-gradient(135deg, #E65100, #FF7043)',
  'linear-gradient(135deg, #1B5E20, #66BB6A)',
  'linear-gradient(135deg, #880E4F, #F06292)',
  'linear-gradient(135deg, #4527A0, #7E57C2)'
]

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
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
      bindCode: '',
      // 中文注释: 批量选择 — 存 studentId（fallback bindingId）
      checkedIds: []
    }
  },
  computed: {
    checkedCount() { return this.checkedIds.length }
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
      uni.navigateTo({ url: '/pages-sub/channel/batch-enroll?singleStudentId=' + (stu.studentId || stu.bindingId) })
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

    // 中文注释: 批量选择 helpers
    stuKey(stu) { return stu.studentId || stu.bindingId },
    isChecked(stu) {
      const k = this.stuKey(stu)
      return k != null && this.checkedIds.includes(k)
    },
    toggleCheck(stu) {
      const k = this.stuKey(stu)
      if (k == null) return
      const idx = this.checkedIds.indexOf(k)
      if (idx >= 0) this.checkedIds.splice(idx, 1)
      else this.checkedIds.push(k)
    },
    selectAll() {
      const all = this.list.map(s => this.stuKey(s)).filter(k => k != null)
      // 中文注释: 去重合并（保留跨页已选，若在当前页上）
      const set = new Set(this.checkedIds.concat(all))
      this.checkedIds = Array.from(set)
    },
    clearChecked() { this.checkedIds = [] },
    goBatchEnroll() {
      if (!this.checkedCount) return
      // 中文注释: 跳转传 studentIds（逗号分隔）
      const ids = this.checkedIds.join(',')
      uni.navigateTo({ url: `/pages-sub/channel/batch-enroll?studentIds=${ids}` })
    },

    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.stu-page { min-height: 100vh; padding-bottom: calc(160rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }

/* 导航 */
.stu-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 0; z-index: 40; }
.stu-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.stu-header__title { flex: 1; font-size: 34rpx; font-weight: 600; color: $jst-text-primary; }
.stu-header__action { font-size: 26rpx; color: $jst-brand; font-weight: 600; padding: 12rpx 0 12rpx 24rpx; }

/* 搜索 */
.stu-search { padding: 24rpx 32rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; }
::v-deep .stu-search .u-search__content { background-color: $jst-bg-page !important; }

/* 二维码卡片 */
.stu-qr { padding: 28rpx 32rpx 0; }
.stu-qr__card { background: linear-gradient(145deg, $jst-indigo 0%, $jst-brand-dark 50%, $jst-indigo-light 100%); border-radius: $jst-radius-lg; padding: 32rpx; position: relative; overflow: hidden; box-shadow: $jst-shadow-md; }
.stu-qr__card::before { content: ''; position: absolute; top: -60rpx; right: -60rpx; width: 280rpx; height: 280rpx; border-radius: 50%; background: rgba($jst-text-inverse, 0.06); }
.stu-qr__label { display: block; font-size: 22rpx; color: rgba($jst-text-inverse, 0.65); position: relative; z-index: 1; }
.stu-qr__title { display: block; font-size: 36rpx; font-weight: 600; color: $jst-text-inverse; margin: 8rpx 0 24rpx; position: relative; z-index: 1; }
.stu-qr__content { display: flex; align-items: center; gap: 32rpx; position: relative; z-index: 1; }
.stu-qr__box { width: 200rpx; height: 200rpx; border-radius: $jst-radius-md; background: $jst-bg-card; display: flex; align-items: center; justify-content: center; font-size: 120rpx; box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.25); }
.stu-qr__info { flex: 1; }
.stu-qr__name { display: block; font-size: 32rpx; font-weight: 600; color: $jst-text-inverse; }
.stu-qr__school { display: block; margin-top: 8rpx; font-size: 24rpx; color: rgba($jst-text-inverse, 0.7); }
.stu-qr__btns { display: flex; gap: 16rpx; margin-top: 20rpx; }
.stu-qr__btn { flex: 1; height: 68rpx; background: rgba($jst-text-inverse, 0.18); border: 2rpx solid rgba($jst-text-inverse, 0.25); border-radius: $jst-radius-sm; color: $jst-text-inverse; font-size: 24rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 8rpx; }

/* Tab */
.stu-tabs { display: flex; gap: 16rpx; padding: 28rpx 32rpx 0; overflow-x: auto; }
.stu-tab { flex-shrink: 0; height: 64rpx; padding: 0 28rpx; border: 3rpx solid $jst-border; border-radius: $jst-radius-round; background: $jst-bg-card; font-size: 26rpx; font-weight: 500; color: $jst-text-regular; display: flex; align-items: center; white-space: nowrap; }
.stu-tab--active { border-color: $jst-brand; background: $jst-brand-light; color: $jst-brand; font-weight: 600; }

/* 学生列表 */
.stu-list { padding: 28rpx 32rpx 0; }
.stu-card { background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; margin-bottom: 20rpx; overflow: hidden; }
.stu-card__main { display: flex; align-items: center; gap: 24rpx; padding: 28rpx 28rpx 20rpx; }
.stu-card__avatar { width: 88rpx; height: 88rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.stu-card__avatar-text { font-size: 40rpx; font-weight: 600; color: $jst-text-inverse; }
.stu-card__info { flex: 1; min-width: 0; }
.stu-card__name { display: block; font-size: 30rpx; font-weight: 600; color: $jst-text-primary; }
.stu-card__meta { display: block; margin-top: 4rpx; font-size: 24rpx; color: $jst-text-secondary; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.stu-card__time { display: block; margin-top: 6rpx; font-size: 22rpx; color: $jst-text-placeholder; }
.stu-card__actions { display: flex; border-top: 2rpx solid $jst-border; }
.stu-card__action { flex: 1; height: 80rpx; display: flex; align-items: center; justify-content: center; gap: 8rpx; font-size: 24rpx; font-weight: 600; color: $jst-text-regular; border-right: 2rpx solid $jst-border; }
.stu-card__action:last-child { border-right: none; }
.stu-card__action--primary { color: $jst-brand; }
.stu-card__action--danger { color: $jst-danger; }

/* 底部统计 */
.stu-footer { position: fixed; bottom: 0; left: 0; right: 0; background: rgba($jst-bg-card, 0.97); border-top: 2rpx solid $jst-border; padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); box-shadow: $jst-shadow-sm; display: flex; align-items: center; z-index: 50; }
.stu-footer__info { font-size: 26rpx; color: $jst-text-secondary; }
.stu-footer__bold { font-weight: 600; color: $jst-text-primary; }

/* 批量选择 checkbox */
.stu-card { transition: background 0.2s ease; }
.stu-card--checked { background: $jst-brand-light; }
.stu-card__checkbox {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stu-card__checkbox-inner {
  width: 40rpx;
  height: 40rpx;
  border-radius: $jst-radius-xs;
  border: 3rpx solid $jst-border;
  background: $jst-bg-card;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}
.stu-card__checkbox-inner--on {
  background: $jst-brand;
  border-color: $jst-brand;
}
.stu-card__checkbox-tick {
  color: $jst-text-inverse;
  font-size: 28rpx;
  font-weight: $jst-weight-bold;
  line-height: 1;
}

/* 批量操作 footer — translateY 滑入 */
.stu-batch-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 60;
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  padding: $jst-space-md $jst-space-xl;
  padding-bottom: calc(#{$jst-space-md} + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  border-top: 2rpx solid $jst-border;
  box-shadow: 0 -6rpx 24rpx rgba(0, 0, 0, 0.08);
  transform: translateY(120%);
  transition: transform 0.25s ease;
}
.stu-batch-footer--visible { transform: translateY(0); }
.stu-batch-footer__left {
  flex: 1;
  display: flex;
  align-items: center;
  gap: $jst-space-xs;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}
.stu-batch-footer__count-num {
  color: $jst-brand;
  font-weight: $jst-weight-bold;
}
.stu-batch-footer__sep {
  color: $jst-text-placeholder;
  margin: 0 4rpx;
}
.stu-batch-footer__link {
  color: $jst-brand;
  font-weight: $jst-weight-semibold;
  padding: 6rpx 10rpx;
}
.stu-batch-footer__right {
  display: flex;
  align-items: center;
  padding: 0 $jst-space-lg;
  height: 80rpx;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border-radius: $jst-radius-round;
  color: $jst-text-inverse;
  box-shadow: $jst-shadow-md;
}
.stu-batch-footer__btn-text {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
}
</style>
