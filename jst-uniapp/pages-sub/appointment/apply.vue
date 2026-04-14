<!-- 中文注释: 个人预约 · 申请页（基于预约时间段 slot 选择）
     对应原型: 小程序原型图/reserve.png
     调用接口: GET /jst/wx/contest/{id} (含 appointmentSlotList)
               POST /jst/wx/appointment/apply (传 slotId) -->
<template>
  <view class="ap-page">
    <view class="ap-hero" :style="{ paddingTop: navPaddingTop }">
      <view class="ap-hero__back" @tap="goBack">←</view>
      <text class="ap-hero__label">我要预约</text>
      <text class="ap-hero__title">{{ contestName || '赛事预约' }}</text>
    </view>

    <!-- 日期 Tab 栏 -->
    <view v-if="dateList.length" class="ap-dates">
      <scroll-view scroll-x class="ap-dates__scroll">
        <view
          v-for="d in dateList"
          :key="d"
          :class="['ap-dates__item', activeDate === d && 'ap-dates__item--active']"
          @tap="selectDate(d)"
        >
          <text class="ap-dates__text">{{ formatDateLabel(d) }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 时间段列表 -->
    <view class="ap-slots">
      <view v-if="currentSlots.length === 0 && !loading" class="ap-empty">
        <text class="ap-empty__text">暂无可选时间段</text>
      </view>

      <view
        v-for="slot in currentSlots"
        :key="slot.slotId"
        :class="[
          'ap-slot',
          selectedSlotId === slot.slotId && 'ap-slot--selected',
          isFull(slot) && 'ap-slot--full'
        ]"
        @tap="selectSlot(slot)"
      >
        <view class="ap-slot__time">
          <text class="ap-slot__time-text">{{ slot.startTime }} - {{ slot.endTime }}</text>
        </view>
        <view class="ap-slot__info">
          <text class="ap-slot__venue">{{ slot.venue || '待定' }}</text>
          <view class="ap-slot__capacity">
            <text
              :class="[
                'ap-slot__count',
                isFull(slot) && 'ap-slot__count--full',
                isAlmostFull(slot) && 'ap-slot__count--warn'
              ]"
            >{{ isFull(slot) ? '已满' : '剩余 ' + remainingCount(slot) }}</text>
            <text class="ap-slot__total">/ {{ slot.capacity || 0 }}</text>
          </view>
        </view>
        <view v-if="isFull(slot)" class="ap-slot__badge">
          <text class="ap-slot__badge-text">已满</text>
        </view>
      </view>
    </view>

    <!-- 参赛档案选择 -->
    <view class="ap-section">
      <view class="ap-field">
        <text class="ap-field__label">参赛档案</text>
        <view class="ap-field__value" @tap="navigateParticipant">{{ participantName || '请选择参赛档案 >' }}</view>
      </view>
    </view>

    <!-- 重复预约提示 -->
    <view v-if="repeatWarning" class="ap-warning">
      <text class="ap-warning__text">{{ repeatWarning }}</text>
    </view>

    <!-- 底部提交 -->
    <view class="ap-footer">
      <u-button
        class="ap-footer__btn"
        type="primary"
        :loading="submitting"
        :disabled="submitting || !canSubmit"
        shape="circle"
        @click="showConfirm"
      >
        提交预约
      </u-button>
    </view>

    <!-- 确认弹窗 -->
    <u-popup :show="confirmVisible" mode="bottom" :round="16" @close="confirmVisible = false">
      <view class="ap-confirm">
        <view class="ap-confirm__header">
          <text class="ap-confirm__title">确认预约</text>
          <view class="ap-confirm__close" @tap="confirmVisible = false">
            <text>✕</text>
          </view>
        </view>
        <view class="ap-confirm__body">
          <view class="ap-confirm__row">
            <text class="ap-confirm__key">赛事名称</text>
            <text class="ap-confirm__val">{{ contestName }}</text>
          </view>
          <view v-if="selectedSlot" class="ap-confirm__row">
            <text class="ap-confirm__key">预约日期</text>
            <text class="ap-confirm__val">{{ formatDateLabel(activeDate) }}</text>
          </view>
          <view v-if="selectedSlot" class="ap-confirm__row">
            <text class="ap-confirm__key">时间段</text>
            <text class="ap-confirm__val">{{ selectedSlot.startTime }} - {{ selectedSlot.endTime }}</text>
          </view>
          <view v-if="selectedSlot" class="ap-confirm__row">
            <text class="ap-confirm__key">场地</text>
            <text class="ap-confirm__val">{{ selectedSlot.venue || '待定' }}</text>
          </view>
          <view v-if="selectedSlot" class="ap-confirm__row">
            <text class="ap-confirm__key">剩余容量</text>
            <text class="ap-confirm__val ap-confirm__val--highlight">{{ remainingCount(selectedSlot) }}</text>
          </view>
        </view>
        <view class="ap-confirm__footer">
          <u-button
            type="primary"
            shape="circle"
            :loading="submitting"
            :disabled="submitting"
            @click="onSubmit"
          >确认预约</u-button>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getContestDetail } from '@/api/contest'
import { applyAppointment, getAppointmentSlots } from '@/api/appointment'

export default {
  data() {
    return {
      loading: false,
      contestId: null,
      contestName: '',
      contestDetail: null,
      // 时间段数据
      allSlots: [],
      dateList: [],
      activeDate: '',
      selectedSlotId: null,
      // 参赛档案
      participantName: '',
      participantId: null,
      // 状态
      submitting: false,
      confirmVisible: false,
      repeatWarning: ''
    }
  },
  computed: {
    // 当前日期下的时间段列表
    currentSlots() {
      if (!this.activeDate) return []
      return this.allSlots.filter(s => this.slotDateStr(s) === this.activeDate)
    },
    // 当前选中的 slot 对象
    selectedSlot() {
      if (!this.selectedSlotId) return null
      return this.allSlots.find(s => s.slotId === this.selectedSlotId) || null
    },
    // 是否可提交
    canSubmit() {
      return !!(this.selectedSlotId && this.contestId && !this.repeatWarning)
    },
    navPaddingTop() {
      // #ifdef MP-WEIXIN
      const sysInfo = uni.getSystemInfoSync()
      return (sysInfo.statusBarHeight || 44) + 44 + 'px'
      // #endif
      // #ifndef MP-WEIXIN
      return '88px'
      // #endif
    }
  },
  onLoad(query) {
    this.contestId = query && query.contestId ? Number(query.contestId) : null
    this.contestName = (query && query.contestName) ? decodeURIComponent(query.contestName) : ''
    this.loadSlots()
  },
  onShow() {
    // 从档案选择页返回
    const picked = uni.getStorageSync('ap_picked_participant')
    if (picked && picked.id) {
      this.participantId = picked.id
      this.participantName = picked.name || ''
      uni.removeStorageSync('ap_picked_participant')
    }
  },
  methods: {
    // 加载预约时间段
    async loadSlots() {
      if (!this.contestId) return
      this.loading = true
      try {
        // 优先从赛事详情获取 appointmentSlotList
        const detail = await getContestDetail(this.contestId, { silent: true })
        this.contestDetail = detail || {}
        this.contestName = this.contestName || detail.contestName || ''

        let slots = (detail && detail.appointmentSlotList) || []

        // 兜底：独立接口获取
        if (!slots.length) {
          try {
            const fallback = await getAppointmentSlots(this.contestId)
            slots = Array.isArray(fallback) ? fallback : []
          } catch (e) {
            slots = []
          }
        }

        this.allSlots = slots
        // 提取去重日期列表
        const dateSet = new Set()
        slots.forEach(s => {
          const d = this.slotDateStr(s)
          if (d) dateSet.add(d)
        })
        this.dateList = Array.from(dateSet).sort()
        // 默认选中第一个日期
        if (this.dateList.length) {
          this.activeDate = this.dateList[0]
        }

        // 检查重复预约
        this.checkRepeatAppointment()
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },

    // 检查是否已预约（allowRepeatAppointment == 0）
    checkRepeatAppointment() {
      if (!this.contestDetail) return
      const allow = this.contestDetail.allowRepeatAppointment
      if (allow === 0 || allow === '0' || allow === false) {
        // 后端会在提交时校验，前端先做提示
        // 此处简化：不预查，等后端返回错误码再提示
        this.repeatWarning = ''
      }
    },

    // 从 slot 对象提取日期字符串 yyyy-MM-dd
    slotDateStr(slot) {
      if (!slot.slotDate) return ''
      const d = String(slot.slotDate)
      // 兼容 "2026-04-15" 或 "2026-04-15T00:00:00" 格式
      return d.slice(0, 10)
    },

    // 格式化日期标签（4月15日）
    formatDateLabel(dateStr) {
      if (!dateStr) return ''
      const parts = dateStr.split('-')
      if (parts.length < 3) return dateStr
      return parseInt(parts[1], 10) + '月' + parseInt(parts[2], 10) + '日'
    },

    // 剩余名额
    remainingCount(slot) {
      const cap = slot.capacity || 0
      const booked = slot.bookedCount || 0
      return Math.max(0, cap - booked)
    },

    // 是否已满
    isFull(slot) {
      return this.remainingCount(slot) <= 0
    },

    // 是否快满（剩余 < 20%）
    isAlmostFull(slot) {
      if (this.isFull(slot)) return false
      const cap = slot.capacity || 1
      return this.remainingCount(slot) / cap < 0.2
    },

    selectDate(d) {
      this.activeDate = d
      this.selectedSlotId = null
    },

    selectSlot(slot) {
      if (this.isFull(slot)) {
        uni.showToast({ title: '该时间段已满', icon: 'none' })
        return
      }
      this.selectedSlotId = slot.slotId
    },

    navigateParticipant() {
      uni.navigateTo({ url: '/pages-sub/my/participant?mode=pick&back=/pages-sub/appointment/apply' })
    },

    showConfirm() {
      if (!this.canSubmit) return
      this.confirmVisible = true
    },

    async onSubmit() {
      if (!this.canSubmit || this.submitting) return
      this.submitting = true
      try {
        const body = {
          contestId: this.contestId,
          slotId: this.selectedSlotId
        }
        if (this.participantId) body.participantId = this.participantId

        const res = await applyAppointment(body)
        this.confirmVisible = false

        if (res && res.orderId) {
          uni.showToast({ title: '预约创建，去支付', icon: 'none' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages-sub/my/order-detail?id=' + res.orderId })
          }, 600)
        } else {
          uni.showToast({ title: '预约成功', icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages-sub/appointment/detail?id=' + (res.appointmentId || '') })
          }, 600)
        }
      } catch (e) {
        if (e && e.code === 30092) {
          uni.showToast({ title: '名额已满', icon: 'none' })
        } else if (e && e.code === 30093) {
          uni.showToast({ title: '您已预约过此赛事', icon: 'none' })
          this.repeatWarning = '您已预约过此赛事，不可重复预约'
        } else if (e && e.code === 30094) {
          uni.showToast({ title: '已报名该赛事', icon: 'none' })
        }
      } finally {
        this.submitting = false
      }
    },

    goBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
      } else {
        uni.switchTab({ url: '/pages/contest/list' })
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ap-page {
  min-height: 100vh;
  padding-bottom: calc(200rpx + env(safe-area-inset-bottom));
  background: $jst-bg-page;
}

/* Hero */
.ap-hero {
  padding: 72rpx 32rpx 56rpx;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-light);
  color: $jst-text-inverse;
  position: relative;
}
.ap-hero__back {
  position: absolute;
  top: 0;
  left: 24rpx;
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: $jst-text-inverse;
  margin-top: 8rpx;
}
.ap-hero__label { display: block; font-size: $jst-font-sm; color: rgba(255, 255, 255, 0.76); }
.ap-hero__title { display: block; margin-top: $jst-space-sm; font-size: 40rpx; font-weight: $jst-weight-semibold; }

/* 日期 Tab */
.ap-dates {
  background: $jst-bg-card;
  padding: $jst-space-md $jst-space-xl;
  box-shadow: $jst-shadow-sm;
}
.ap-dates__scroll {
  white-space: nowrap;
}
.ap-dates__item {
  display: inline-block;
  padding: $jst-space-sm $jst-space-lg;
  margin-right: $jst-space-sm;
  border-radius: $jst-radius-round;
  background: $jst-bg-grey;
  font-size: $jst-font-sm;
  color: $jst-text-regular;
  transition: all $jst-duration-fast $jst-easing;
}
.ap-dates__item--active {
  background: $jst-brand;
  color: $jst-text-inverse;
  font-weight: $jst-weight-semibold;
}
.ap-dates__text { font-size: $jst-font-sm; }

/* 时间段列表 */
.ap-slots {
  padding: $jst-space-lg $jst-space-xl;
}
.ap-slot {
  display: flex;
  align-items: center;
  gap: $jst-space-md;
  margin-bottom: $jst-space-sm;
  padding: $jst-space-lg;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
  border: 3rpx solid transparent;
  position: relative;
  overflow: hidden;
  transition: all $jst-duration-fast $jst-easing;
}
.ap-slot--selected {
  border-color: $jst-brand;
  background: $jst-brand-light;
}
.ap-slot--full {
  opacity: 0.55;
}
.ap-slot__time {
  flex-shrink: 0;
  width: 200rpx;
}
.ap-slot__time-text {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-primary;
}
.ap-slot__info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $jst-space-sm;
}
.ap-slot__venue {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}
.ap-slot__capacity {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}
.ap-slot__count {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-semibold;
  color: $jst-success;
}
.ap-slot__count--warn { color: $jst-warning; }
.ap-slot__count--full { color: $jst-danger; }
.ap-slot__total {
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}
.ap-slot__badge {
  position: absolute;
  top: 0;
  right: 0;
  padding: 4rpx 16rpx;
  background: $jst-danger-light;
  border-radius: 0 $jst-radius-md 0 $jst-radius-md;
}
.ap-slot__badge-text {
  font-size: $jst-font-xs;
  color: $jst-danger;
  font-weight: $jst-weight-semibold;
}

/* 参赛档案 */
.ap-section {
  margin: 0 $jst-space-xl;
  padding: $jst-space-md $jst-space-xl;
  background: $jst-bg-card;
  border-radius: $jst-radius-md;
  box-shadow: $jst-shadow-sm;
}
.ap-field {
  display: flex;
  align-items: center;
  padding: $jst-space-lg 0;
}
.ap-field__label {
  width: 180rpx;
  font-size: $jst-font-sm;
  color: $jst-text-regular;
}
.ap-field__value {
  flex: 1;
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

/* 警告提示 */
.ap-warning {
  margin: $jst-space-md $jst-space-xl;
  padding: $jst-space-md $jst-space-lg;
  background: $jst-danger-light;
  border-radius: $jst-radius-sm;
}
.ap-warning__text {
  font-size: $jst-font-sm;
  color: $jst-danger;
}

/* 空状态 */
.ap-empty {
  text-align: center;
  padding: $jst-space-xxl 0;
}
.ap-empty__text {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

/* 底部按钮 */
.ap-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $jst-space-lg $jst-space-xl calc(#{$jst-space-lg} + env(safe-area-inset-bottom));
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}
::v-deep .ap-footer__btn.u-button {
  height: 96rpx;
  font-size: $jst-font-md;
  font-weight: $jst-weight-semibold;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border: none;
}

/* 确认弹窗 */
.ap-confirm {
  padding: $jst-space-xl;
  padding-bottom: calc(#{$jst-space-xl} + env(safe-area-inset-bottom));
}
.ap-confirm__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $jst-space-lg;
}
.ap-confirm__title {
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}
.ap-confirm__close {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: $jst-bg-page;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $jst-font-base;
  color: $jst-text-secondary;
}
.ap-confirm__body {
  padding: $jst-space-md 0;
}
.ap-confirm__row {
  display: flex;
  justify-content: space-between;
  padding: $jst-space-md 0;
  border-bottom: 2rpx solid $jst-border;
}
.ap-confirm__row:last-child { border-bottom: none; }
.ap-confirm__key {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}
.ap-confirm__val {
  font-size: $jst-font-sm;
  color: $jst-text-primary;
  font-weight: $jst-weight-medium;
}
.ap-confirm__val--highlight {
  color: $jst-success;
  font-weight: $jst-weight-bold;
}
.ap-confirm__footer {
  margin-top: $jst-space-lg;
}
::v-deep .ap-confirm__footer .u-button {
  height: 96rpx;
  background: linear-gradient(135deg, $jst-brand, $jst-brand-dark);
  border: none;
}
</style>
