<!-- 中文注释: 扫码核销 · 沉浸式深色 UI
     对应原型: 小程序原型图/checkin-scan.html
     权限: jst_partner / jst_platform_op (POLISH-D 修复: 不再依赖 isChannelUser)
     变更:
       - 核销类型 chip 3 个 (到场/礼品/颁奖), 切换影响 /writeoff/scan 入参 scanType
       - 活动/场次切换 sheet (本地 mock, 后端暂无 active sessions 接口)
       - 今日统计 4 格 (本地 state 累加)
       - 最近核销记录 (本地 state 最近 10 条)
       - 扫码结果三态: 成功/失败/已使用
       - 背景 #0D1B2A 沉浸式 -->
<template>
  <view class="sc-page">
    <view class="sc-topbar" :style="{ paddingTop: navPaddingTop }" @tap="showSessionSheet = true">
      <view class="sc-topbar__info">
        <text class="sc-topbar__label">当前活动场次</text>
        <text class="sc-topbar__name">{{ currentSession.name }}</text>
      </view>
      <view class="sc-topbar__switch">
        <u-tag text="切换" type="primary" plain size="mini" shape="circle"></u-tag>
      </view>
    </view>

    <view class="sc-chips">
      <view
        v-for="chip in scanTypes"
        :key="chip.value"
        :class="['sc-chips__item', scanType === chip.value && 'sc-chips__item--active']"
        @tap="scanType = chip.value"
      >
        <text class="sc-chips__icon">{{ chip.icon }}</text>
        <text class="sc-chips__label">{{ chip.label }}</text>
      </view>
    </view>

    <view class="sc-stats">
      <view class="sc-stats__cell"><text class="sc-stats__num">{{ todayStats.total }}</text><text class="sc-stats__lbl">今日核销</text></view>
      <view class="sc-stats__cell"><text class="sc-stats__num">{{ todayStats.entry }}</text><text class="sc-stats__lbl">入场</text></view>
      <view class="sc-stats__cell"><text class="sc-stats__num">{{ todayStats.gift }}</text><text class="sc-stats__lbl">礼品</text></view>
      <view class="sc-stats__cell"><text class="sc-stats__num">{{ todayStats.award }}</text><text class="sc-stats__lbl">颁奖</text></view>
    </view>

    <view class="sc-scanbtn" @tap="startScan">
      <text class="sc-scanbtn__icon">📷</text>
      <text class="sc-scanbtn__text">点击开始扫码</text>
      <text class="sc-scanbtn__sub">对准学生出示的核销二维码</text>
    </view>

    <view v-if="lastResult" :class="['sc-result', 'sc-result--' + lastResult.state]">
      <text class="sc-result__icon">{{ lastResult.icon }}</text>
      <text class="sc-result__title">{{ lastResult.title }}</text>
      <text class="sc-result__sub" v-if="lastResult.sub">{{ lastResult.sub }}</text>
    </view>

    <view class="sc-recent">
      <text class="sc-recent__title">最近核销 ({{ recentList.length }})</text>
      <view v-for="(r, idx) in recentList" :key="idx" class="sc-recent__item">
        <text :class="['sc-recent__dot', 'sc-recent__dot--' + r.state]"></text>
        <view class="sc-recent__body">
          <text class="sc-recent__name">{{ r.itemName || r.qrCode || '核销' }}</text>
          <text class="sc-recent__time">{{ r.time }}</text>
        </view>
        <text class="sc-recent__type">{{ typeLabel(r.scanType) }}</text>
      </view>
      <view v-if="!recentList.length" class="sc-recent__empty">暂无记录，请扫码</view>
    </view>

    <!-- 场次切换 sheet -->
    <view v-if="showSessionSheet" class="sc-sheet" @tap="showSessionSheet = false">
      <view class="sc-sheet__panel" @tap.stop>
        <text class="sc-sheet__title">切换活动场次</text>
        <view
          v-for="s in sessionList"
          :key="s.id"
          :class="['sc-sheet__item', currentSession.id === s.id && 'sc-sheet__item--active']"
          @tap="pickSession(s)"
        >
          <text>{{ s.name }}</text>
          <text v-if="currentSession.id === s.id" class="sc-sheet__tick">✓</text>
        </view>
        <view class="sc-sheet__tip">注: 场次列表本期为本地示例数据, 待后端提供 active sessions 接口后接入</view>
      </view>
    </view>
  </view>
</template>

<script>
import { scanWriteoff } from '@/api/appointment'
import { useUserStore } from '@/store/user'

const SCAN_TYPES = [
  { value: 'entry', label: '到场入场', icon: '🚪' },
  { value: 'gift', label: '礼品领取', icon: '🎁' },
  { value: 'award', label: '颁奖典礼', icon: '🏆' }
]
const MOCK_SESSIONS = [
  { id: 'sess-1', name: '2026 春季人民艺术全国展演 · 4/24 上午' },
  { id: 'sess-2', name: '2026 春季人民艺术全国展演 · 4/24 下午' },
  { id: 'sess-3', name: '"鸟巢杯"文化艺术活动 · 4/25 全天' }
]

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
      scanTypes: SCAN_TYPES,
      scanType: 'entry',
      sessionList: MOCK_SESSIONS,
      currentSession: MOCK_SESSIONS[0],
      showSessionSheet: false,
      todayStats: { total: 0, entry: 0, gift: 0, award: 0 },
      recentList: [],
      lastResult: null
    }
  },
  onLoad() {
    // POLISH-D 权限门: jst_partner / jst_platform_op
    const store = useUserStore()
    const roles = store.roles || []
    const allowed = roles.includes('jst_partner') || roles.includes('jst_platform_op')
    if (!allowed) {
      uni.showToast({ title: '无扫码权限', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 800)
    }
  },
  methods: {
    startScan() {
      uni.scanCode({
        scanType: ['qrCode'],
        success: async (res) => {
          const payload = res.result
          try {
            const data = await scanWriteoff({ qrCode: payload, scanType: this.scanType, terminal: 'wx' })
            this.handleResult('success', data || {}, payload)
          } catch (e) {
            // 根据业务码区分"已使用"和其他失败
            const code = e && e.code
            if (code === 30095 || code === 30096) this.handleResult('used', e || {}, payload)
            else this.handleResult('fail', e || {}, payload)
          }
        },
        fail: () => {}
      })
    },
    handleResult(state, data, payload) {
      const icons = { success: '✅', fail: '❌', used: '⚠️' }
      const titles = { success: '核销成功', fail: '核销失败', used: '该券已使用' }
      this.lastResult = {
        state,
        icon: icons[state],
        title: titles[state],
        sub: (data && data.itemName) || (data && data.msg) || ''
      }
      if (state === 'success') {
        this.todayStats.total += 1
        if (this.scanType === 'entry') this.todayStats.entry += 1
        else if (this.scanType === 'gift') this.todayStats.gift += 1
        else if (this.scanType === 'award') this.todayStats.award += 1
        uni.showToast({ title: '核销成功', icon: 'success' })
      } else if (state === 'used') {
        uni.showToast({ title: '该券已使用', icon: 'none' })
      }
      this.recentList.unshift({
        state,
        qrCode: payload,
        itemName: data && data.itemName,
        scanType: this.scanType,
        time: this.nowText()
      })
      if (this.recentList.length > 10) this.recentList.length = 10
    },
    pickSession(s) {
      if (this.currentSession.id !== s.id) {
        this.currentSession = s
        this.todayStats = { total: 0, entry: 0, gift: 0, award: 0 }
        this.recentList = []
        this.lastResult = null
      }
      this.showSessionSheet = false
    },
    typeLabel(v) { const t = SCAN_TYPES.find((i) => i.value === v); return t ? t.label : '' },
    nowText() { const d = new Date(); return `${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}:${d.getSeconds().toString().padStart(2,'0')}` }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

$dark-bg: $jst-bg-dark-page;
$dark-card: $jst-bg-dark-card;
$dark-text: $jst-text-dark-primary;
$dark-sub: $jst-text-dark-secondary;

.sc-page { min-height: 100vh; background: $dark-bg; color: $dark-text; padding: 32rpx 32rpx 80rpx; box-sizing: border-box; }
.sc-topbar { display: flex; align-items: center; padding: 24rpx 28rpx; background: $dark-card; border-radius: $jst-radius-md; }
.sc-topbar__info { flex: 1; min-width: 0; }
.sc-topbar__label { display: block; font-size: 22rpx; color: $dark-sub; }
.sc-topbar__name { display: block; margin-top: 6rpx; font-size: 28rpx; font-weight: 600; color: $jst-gold; }
.sc-topbar__switch { display: flex; align-items: center; }

.sc-chips { display: flex; gap: 16rpx; margin-top: 24rpx; }
.sc-chips__item { flex: 1; padding: 24rpx 0; background: $dark-card; border-radius: $jst-radius-sm; text-align: center; border: 2rpx solid transparent; }
.sc-chips__item--active { border-color: $jst-gold; background: $jst-gold-light; }
.sc-chips__icon { display: block; font-size: 44rpx; }
.sc-chips__label { display: block; margin-top: 6rpx; font-size: 22rpx; color: $dark-sub; }
.sc-chips__item--active .sc-chips__label { color: $jst-gold; font-weight: 600; }

.sc-stats { display: flex; gap: 16rpx; margin-top: 24rpx; padding: 24rpx; background: $dark-card; border-radius: $jst-radius-md; }
.sc-stats__cell { flex: 1; text-align: center; }
.sc-stats__num { display: block; font-size: 44rpx; font-weight: 600; color: $jst-gold; }
.sc-stats__lbl { display: block; margin-top: 6rpx; font-size: 22rpx; color: $dark-sub; }

.sc-scanbtn { margin-top: 32rpx; padding: 64rpx 0; background: linear-gradient(135deg, $jst-indigo, $jst-indigo-light); border-radius: $jst-radius-lg; text-align: center; box-shadow: $jst-shadow-float; }
.sc-scanbtn__icon { display: block; font-size: 96rpx; }
.sc-scanbtn__text { display: block; margin-top: 16rpx; font-size: 32rpx; font-weight: 600; color: $jst-text-inverse; }
.sc-scanbtn__sub { display: block; margin-top: 8rpx; font-size: 22rpx; color: rgba(255,255,255,0.7); }

.sc-result { margin-top: 24rpx; padding: 32rpx; border-radius: $jst-radius-md; text-align: center; }
.sc-result--success { background: rgba($jst-success, 0.18); border: 2rpx solid $jst-success; }
.sc-result--fail { background: rgba($jst-danger, 0.18); border: 2rpx solid $jst-danger; }
.sc-result--used { background: rgba($jst-warning, 0.18); border: 2rpx solid $jst-warning; }
.sc-result__icon { display: block; font-size: 64rpx; }
.sc-result__title { display: block; margin-top: 8rpx; font-size: 32rpx; font-weight: 600; color: $dark-text; }
.sc-result__sub { display: block; margin-top: 6rpx; font-size: 22rpx; color: $dark-sub; }

.sc-recent { margin-top: 32rpx; padding: 24rpx; background: $dark-card; border-radius: $jst-radius-md; }
.sc-recent__title { display: block; font-size: 26rpx; font-weight: 600; color: $dark-text; margin-bottom: 16rpx; }
.sc-recent__item { display: flex; align-items: center; gap: 16rpx; padding: 16rpx 0; border-bottom: 2rpx solid rgba(255,255,255,0.06); }
.sc-recent__item:last-child { border-bottom: none; }
.sc-recent__dot { width: 16rpx; height: 16rpx; border-radius: 50%; background: $jst-success; }
.sc-recent__dot--fail { background: $jst-danger; }
.sc-recent__dot--used { background: $jst-warning; }
.sc-recent__body { flex: 1; min-width: 0; }
.sc-recent__name { display: block; font-size: 24rpx; color: $dark-text; }
.sc-recent__time { display: block; margin-top: 4rpx; font-size: 20rpx; color: $dark-sub; }
.sc-recent__type { font-size: 20rpx; color: $jst-gold; }
.sc-recent__empty { padding: 24rpx 0; text-align: center; font-size: 22rpx; color: $dark-sub; }

.sc-sheet { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 999; display: flex; align-items: flex-end; }
.sc-sheet__panel { width: 100%; padding: 32rpx 32rpx calc(32rpx + env(safe-area-inset-bottom)); background: $dark-card; border-top-left-radius: 32rpx; border-top-right-radius: 32rpx; }
.sc-sheet__title { display: block; font-size: 28rpx; font-weight: 600; color: $dark-text; margin-bottom: 20rpx; }
.sc-sheet__item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 16rpx; font-size: 26rpx; color: $dark-text; border-bottom: 2rpx solid rgba(255,255,255,0.06); }
.sc-sheet__item--active { color: $jst-gold; font-weight: 600; }
.sc-sheet__tick { color: $jst-gold; }
.sc-sheet__tip { margin-top: 16rpx; font-size: 20rpx; color: $dark-sub; text-align: center; }
</style>
