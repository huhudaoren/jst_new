<!-- 中文注释: 渠道方 · 团队预约 (E1-CH-7 Part C)
     对应原型: 无独立 PNG (参考 channel-home.png 视觉风格 + appointment/apply.vue 交互)
     功能: 选活动+日期场次 → 选学生+额外人数 → 创建团队预约 → 详情页(含成员QR)
     数据来源: POST /jst/wx/appointment/team/create + GET /jst/wx/appointment/team/{id} -->
<template>
  <view class="ta-page">
    <!-- 导航 -->
    <view class="ta-header" :style="{ paddingTop: navPaddingTop }">
      <view class="ta-header__back" @tap="goBack">←</view>
      <text class="ta-header__title">{{ isDetail ? '团队预约详情' : '团队预约' }}</text>
    </view>

    <!-- 创建模式 -->
    <template v-if="!isDetail">
      <!-- 选择赛事 -->
      <view class="ta-section">
        <view class="ta-section__title">选择活动</view>
        <view class="ta-contest" @tap="showContestPicker = true">
          <text v-if="selectedContest" class="ta-contest__name">{{ selectedContest.contestName }}</text>
          <text v-else class="ta-contest__placeholder">请选择支持团队预约的赛事 ›</text>
        </view>
      </view>

      <!-- 日期和场次 -->
      <view class="ta-section">
        <view class="ta-section__title">预约时间</view>
        <view class="ta-field">
          <text class="ta-field__label">预约日期</text>
          <picker mode="date" :value="form.appointmentDate" @change="onDateChange">
            <view class="ta-field__value">{{ form.appointmentDate || '请选择日期' }}</view>
          </picker>
        </view>
        <view class="ta-field">
          <text class="ta-field__label">场次</text>
          <picker mode="selector" :range="sessionList" range-key="label" @change="onSessionChange">
            <view class="ta-field__value">{{ currentSessionLabel || '请选择场次' }}</view>
          </picker>
        </view>
        <view v-if="remaining" class="ta-remaining">
          <text>剩余名额: {{ remaining.remainingCount != null ? remaining.remainingCount : '--' }} / {{ remaining.totalCapacity || '--' }}</text>
          <u-tag
            :text="remaining.remainingCount <= 0 ? '已满' : '可预约'"
            :type="remaining.remainingCount <= 0 ? 'error' : 'success'"
            size="mini"
            plain
            shape="circle"
          ></u-tag>
        </view>
      </view>

      <!-- 成员名单 -->
      <view class="ta-section">
        <view class="ta-section__title">成员名单</view>

        <!-- 从已绑定学生选择 -->
        <view class="ta-subsection-title">从已绑定学生选择</view>
        <view v-for="stu in boundStudents" :key="stu.bindingId" class="ta-check-item" @tap="toggleStudent(stu)">
          <view :class="['ta-checkbox', isStudentSelected(stu) ? 'ta-checkbox--checked' : '']">
            <text v-if="isStudentSelected(stu)">✓</text>
          </view>
          <text class="ta-check-name">{{ stu.studentName || '--' }}</text>
          <text class="ta-check-meta">{{ stu.schoolName || '' }}</text>
        </view>
        <u-empty v-if="!boundStudents.length" mode="data" text="暂无绑定学生"></u-empty>

        <!-- 额外人数 -->
        <view class="ta-subsection-title" style="margin-top: 24rpx;">额外参赛人</view>
        <view class="ta-field">
          <text class="ta-field__label">额外人数</text>
          <input class="ta-field__input" v-model="form.extraCount" type="number" placeholder="0" />
        </view>
        <view v-if="Number(form.extraCount) > 0" class="ta-field">
          <text class="ta-field__label">备注名单</text>
          <textarea class="ta-field__textarea" v-model="form.extraRemark" placeholder="请输入额外参赛人姓名，每行一个" />
        </view>

        <!-- 总人数 -->
        <view class="ta-total-count">
          <text>总人数: </text>
          <text class="ta-total-count__num">{{ totalCount }}</text>
        </view>
      </view>

      <!-- 提交 -->
      <view class="ta-footer">
        <u-button
          :text="submitting ? '提交中...' : '确认预约'"
          type="primary"
          shape="circle"
          :disabled="!canSubmit || submitting"
          @click="onSubmit"
        ></u-button>
      </view>
    </template>

    <!-- 详情模式 -->
    <template v-if="isDetail && detailData">
      <view class="ta-section">
        <view class="ta-section__title">预约信息</view>
        <u-cell-group :border="false">
          <u-cell title="团队名称" :value="detailData.teamName || '--'" :border="false"></u-cell>
          <u-cell title="预约日期" :value="detailData.appointmentDate || '--'" :border="false"></u-cell>
          <u-cell title="预约总人数" :value="String(detailData.totalCount || '--')" :border="false"></u-cell>
          <u-cell title="成员人数" :value="String(detailData.memberCount || '--')" :border="false"></u-cell>
          <u-cell title="额外人数" :value="String(detailData.extraCount || 0)" :border="false"></u-cell>
          <u-cell title="已核销" :value="(detailData.writeoffCount || 0) + ' / ' + (detailData.totalCount || '--')" :border="false"></u-cell>
        </u-cell-group>
      </view>

      <!-- 成员二维码列表 -->
      <view v-if="detailData.members && detailData.members.length" class="ta-section">
        <view class="ta-section__title">成员二维码</view>
        <view v-for="(m, idx) in detailData.members" :key="idx" class="ta-qr-card">
          <view class="ta-qr-info">
            <text class="ta-qr-name">{{ m.studentName || '成员 ' + (idx + 1) }}</text>
            <u-tag :text="m.writeoffTime ? '已核销' : '待核销'" :type="m.writeoffTime ? 'success' : 'warning'" size="mini" plain shape="circle"></u-tag>
          </view>
          <canvas v-if="m.qrCode && !m.writeoffTime" :canvas-id="'qr-' + idx" class="ta-qr-canvas"></canvas>
        </view>
      </view>
    </template>

    <!-- 赛事选择弹窗 -->
    <u-popup :show="showContestPicker" mode="bottom" :round="16" @close="showContestPicker = false" @open="showContestPicker = true">
      <view class="ta-picker">
        <view class="ta-picker__header">
          <text class="ta-picker__title">选择活动</text>
          <text class="ta-picker__close" @tap="showContestPicker = false">✕</text>
        </view>
        <scroll-view scroll-y class="ta-picker__body">
          <view v-for="c in contestList" :key="c.contestId" class="ta-picker__item" @tap="selectContest(c)">
            <text class="ta-picker__name">{{ c.contestName }}</text>
          </view>
          <u-empty v-if="!contestList.length" mode="data" text="暂无可预约活动"></u-empty>
        </scroll-view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { getChannelStudents } from '@/api/channel'
import { getContestList } from '@/api/contest'
import { getRemaining, createTeamAppointment, getTeamAppointmentDetail } from '@/api/appointment'
import { makeQr } from '@/utils/qrcode-wrapper'

const SESSIONS = [
  { value: 'AM', label: '上午场 (AM)' },
  { value: 'PM', label: '下午场 (PM)' },
  { value: 'EVE', label: '晚场 (EVE)' }
]

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
      isDetail: false,
      detailId: null,
      detailData: null,
      // 创建表单
      showContestPicker: false,
      contestList: [],
      selectedContest: null,
      form: {
        appointmentDate: '',
        sessionCode: '',
        extraCount: '',
        extraRemark: ''
      },
      sessionList: SESSIONS,
      remaining: null,
      boundStudents: [],
      selectedStudentIds: [],
      submitting: false
    }
  },
  computed: {
    currentSessionLabel() {
      const item = SESSIONS.find(s => s.value === this.form.sessionCode)
      return item ? item.label : ''
    },
    totalCount() {
      return this.selectedStudentIds.length + (Number(this.form.extraCount) || 0)
    },
    canSubmit() {
      return this.selectedContest && this.form.appointmentDate && this.form.sessionCode && this.totalCount > 0
    }
  },
  onLoad(opts) {
    if (opts.teamId) {
      this.isDetail = true
      this.detailId = opts.teamId
      this.loadDetail()
    } else {
      this.loadContests()
      this.loadBoundStudents()
    }
  },
  methods: {
    async loadContests() {
      try {
        const res = await getContestList({ pageNum: 1, pageSize: 50, status: 'enrolling' })
        this.contestList = (res && res.rows) || (Array.isArray(res) ? res : [])
      } catch (e) {
        this.contestList = []
      }
    },

    async loadBoundStudents() {
      try {
        const res = await getChannelStudents({ pageNum: 1, pageSize: 100 })
        this.boundStudents = (res && res.rows) || (Array.isArray(res) ? res : [])
      } catch (e) {
        this.boundStudents = []
      }
    },

    selectContest(c) {
      this.selectedContest = c
      this.showContestPicker = false
      this.checkRemaining()
    },

    onDateChange(e) {
      this.form.appointmentDate = e.detail.value
      this.checkRemaining()
    },
    onSessionChange(e) {
      this.form.sessionCode = SESSIONS[e.detail.value].value
      this.checkRemaining()
    },

    async checkRemaining() {
      if (!this.selectedContest || !this.form.appointmentDate || !this.form.sessionCode) return
      try {
        const res = await getRemaining(this.selectedContest.contestId, {
          appointmentDate: this.form.appointmentDate,
          sessionCode: this.form.sessionCode
        })
        this.remaining = res
      } catch (e) {
        this.remaining = null
      }
    },

    toggleStudent(stu) {
      const id = stu.studentId || stu.bindingId
      const idx = this.selectedStudentIds.indexOf(id)
      if (idx >= 0) {
        this.selectedStudentIds.splice(idx, 1)
      } else {
        this.selectedStudentIds.push(id)
      }
    },
    isStudentSelected(stu) {
      const id = stu.studentId || stu.bindingId
      return this.selectedStudentIds.includes(id)
    },

    async onSubmit() {
      if (!this.canSubmit || this.submitting) return
      this.submitting = true
      try {
        const res = await createTeamAppointment({
          contestId: this.selectedContest.contestId,
          sessionCode: this.form.sessionCode,
          appointmentDate: this.form.appointmentDate,
          studentIds: this.selectedStudentIds,
          extraCount: Number(this.form.extraCount) || 0,
          extraRemark: this.form.extraRemark || ''
        })
        uni.showToast({ title: '预约成功', icon: 'success' })
        // 跳到详情
        if (res && res.teamAppointmentId) {
          setTimeout(() => {
            uni.redirectTo({ url: '/pages-sub/channel/appointment?teamId=' + res.teamAppointmentId })
          }, 1000)
        } else {
          setTimeout(() => { uni.navigateBack() }, 1500)
        }
      } catch (e) {}
      this.submitting = false
    },

    async loadDetail() {
      try {
        const res = await getTeamAppointmentDetail(this.detailId)
        this.detailData = res || {}
        // 懒渲染成员 QR
        this.$nextTick(() => {
          if (this.detailData.members) {
            this.detailData.members.forEach((m, idx) => {
              if (m.qrCode && !m.writeoffTime) {
                makeQr({ canvasId: 'qr-' + idx, text: m.qrCode, size: 180, context: this })
              }
            })
          }
        })
      } catch (e) {}
    },

    goBack() { uni.navigateBack() }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.ta-page { min-height: 100vh; padding-bottom: calc(140rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }

.ta-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: $jst-bg-card; border-bottom: 2rpx solid $jst-border; position: sticky; top: 0; z-index: 40; }
.ta-header__back { width: 72rpx; height: 72rpx; border-radius: $jst-radius-sm; background: $jst-bg-page; display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.ta-header__title { flex: 1; font-size: 34rpx; font-weight: 600; color: $jst-text-primary; }

.ta-section { margin: 24rpx 32rpx 0; background: $jst-bg-card; border-radius: $jst-radius-lg; box-shadow: $jst-shadow-sm; overflow: hidden; padding: 28rpx; }
.ta-section__title { font-size: 30rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 20rpx; display: flex; align-items: center; gap: 12rpx; }
.ta-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: $jst-brand; border-radius: 4rpx; }

/* 赛事选择 */
.ta-contest { padding: 20rpx; background: $jst-bg-page; border-radius: $jst-radius-md; }
.ta-contest__name { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.ta-contest__placeholder { font-size: 28rpx; color: $jst-text-secondary; }

/* 表单 */
.ta-field { display: flex; align-items: center; margin-bottom: 16rpx; }
.ta-field__label { width: 160rpx; font-size: 26rpx; color: $jst-text-regular; flex-shrink: 0; }
.ta-field__value { flex: 1; height: 64rpx; padding: 0 16rpx; background: $jst-bg-page; border-radius: $jst-radius-sm; font-size: 26rpx; color: $jst-text-primary; display: flex; align-items: center; }
.ta-field__input { flex: 1; height: 64rpx; padding: 0 16rpx; background: $jst-bg-page; border-radius: $jst-radius-sm; font-size: 26rpx; color: $jst-text-primary; }
.ta-field__textarea { flex: 1; min-height: 120rpx; padding: 16rpx; background: $jst-bg-page; border-radius: $jst-radius-sm; font-size: 26rpx; color: $jst-text-primary; }

.ta-remaining { display: flex; align-items: center; justify-content: space-between; gap: $jst-space-sm; padding: 12rpx 16rpx; background: $jst-brand-light; border-radius: $jst-radius-sm; font-size: 24rpx; color: $jst-brand; margin-top: 8rpx; }

.ta-subsection-title { font-size: 26rpx; font-weight: 600; color: $jst-text-regular; margin-bottom: 12rpx; }

/* 学生勾选 */
.ta-check-item { display: flex; align-items: center; gap: 16rpx; padding: 16rpx 0; border-bottom: 2rpx solid $jst-border; }
.ta-check-item:last-child { border-bottom: none; }
.ta-checkbox { width: 40rpx; height: 40rpx; border: 3rpx solid $jst-border; border-radius: $jst-radius-sm; display: flex; align-items: center; justify-content: center; font-size: 24rpx; color: $jst-text-inverse; flex-shrink: 0; }
.ta-checkbox--checked { background: $jst-brand; border-color: $jst-brand; }
.ta-check-name { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.ta-check-meta { flex: 1; font-size: 22rpx; color: $jst-text-secondary; text-align: right; }

.ta-total-count { margin-top: 16rpx; padding: 16rpx; background: $jst-brand-light; border-radius: $jst-radius-sm; font-size: 28rpx; color: $jst-brand; }
.ta-total-count__num { font-size: 36rpx; font-weight: 600; }

/* 提交 */
.ta-footer { position: fixed; bottom: 0; left: 0; right: 0; background: rgba($jst-bg-card, 0.97); border-top: 2rpx solid $jst-border; padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); z-index: 50; }
::v-deep .ta-footer .u-button { height: 96rpx; }

/* 详情 */
/* QR */
.ta-qr-card { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 0; border-bottom: 2rpx solid $jst-border; }
.ta-qr-card:last-child { border-bottom: none; }
.ta-qr-info { flex: 1; }
.ta-qr-name { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.ta-qr-canvas { width: 180rpx; height: 180rpx; flex-shrink: 0; }

/* 弹窗 */
.ta-picker { width: 100%; max-height: 70vh; background: $jst-bg-card; border-radius: $jst-radius-lg $jst-radius-lg 0 0; overflow: hidden; }
.ta-picker__header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx; border-bottom: 2rpx solid $jst-border; }
.ta-picker__title { font-size: 32rpx; font-weight: 600; color: $jst-text-primary; }
.ta-picker__close { font-size: 36rpx; color: $jst-text-secondary; padding: 8rpx; }
.ta-picker__body { max-height: 60vh; padding: 0 32rpx; }
.ta-picker__item { padding: 28rpx 0; border-bottom: 2rpx solid $jst-border; }
.ta-picker__name { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
</style>
