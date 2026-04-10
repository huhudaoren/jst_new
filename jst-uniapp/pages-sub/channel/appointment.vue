<!-- 中文注释: 渠道方 · 团队预约 (E1-CH-7 Part C)
     对应原型: 无独立 PNG (参考 channel-home.png 视觉风格 + appointment/apply.vue 交互)
     功能: 选活动+日期场次 → 选学生+额外人数 → 创建团队预约 → 详情页(含成员QR)
     数据来源: POST /jst/wx/appointment/team/create + GET /jst/wx/appointment/team/{id} -->
<template>
  <view class="ta-page">
    <!-- 导航 -->
    <view class="ta-header">
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
          <text>剩余名额: </text>
          <text :class="{ 'ta-remaining--danger': remaining.remainingCount <= 0 }">{{ remaining.remainingCount != null ? remaining.remainingCount : '--' }}</text>
          <text> / {{ remaining.totalCapacity || '--' }}</text>
        </view>
      </view>

      <!-- 成员名单 -->
      <view class="ta-section">
        <view class="ta-section__title">成员名单</view>

        <!-- 从已绑定学生选择 -->
        <view class="ta-subsection-title">从已绑定学生选择</view>
        <view v-for="stu in boundStudents" :key="stu.bindingId || stu.studentId" class="ta-check-item" @tap="toggleStudent(stu)">
          <view :class="['ta-checkbox', isStudentSelected(stu) ? 'ta-checkbox--checked' : '']">
            <text v-if="isStudentSelected(stu)">✓</text>
          </view>
          <text class="ta-check-name">{{ stu.studentName || '--' }}</text>
          <text class="ta-check-meta">{{ stu.schoolName || '' }}</text>
        </view>
        <view v-if="!boundStudents.length" class="ta-empty-hint">
          <text>暂无绑定学生</text>
        </view>

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
        <view class="ta-footer__btn" :class="{ 'ta-footer__btn--disabled': !canSubmit || submitting }" @tap="onSubmit">
          {{ submitting ? '提交中...' : '确认预约' }}
        </view>
      </view>
    </template>

    <!-- 详情模式 -->
    <template v-if="isDetail && detailData">
      <view class="ta-section">
        <view class="ta-section__title">预约信息</view>
        <view class="ta-detail-row">
          <text class="ta-detail-label">团队名称</text>
          <text class="ta-detail-val">{{ detailData.teamName || '--' }}</text>
        </view>
        <view class="ta-detail-row">
          <text class="ta-detail-label">预约日期</text>
          <text class="ta-detail-val">{{ detailData.appointmentDate || '--' }}</text>
        </view>
        <view class="ta-detail-row">
          <text class="ta-detail-label">预约总人数</text>
          <text class="ta-detail-val">{{ detailData.totalCount || '--' }}</text>
        </view>
        <view class="ta-detail-row">
          <text class="ta-detail-label">成员人数</text>
          <text class="ta-detail-val">{{ detailData.memberCount || '--' }}</text>
        </view>
        <view class="ta-detail-row">
          <text class="ta-detail-label">额外人数</text>
          <text class="ta-detail-val">{{ detailData.extraCount || 0 }}</text>
        </view>
        <view class="ta-detail-row">
          <text class="ta-detail-label">已核销</text>
          <text class="ta-detail-val">{{ detailData.writeoffCount || 0 }} / {{ detailData.totalCount || '--' }}</text>
        </view>
      </view>

      <!-- 成员二维码列表 -->
      <view v-if="detailData.members && detailData.members.length" class="ta-section">
        <view class="ta-section__title">成员二维码</view>
        <view v-for="(m, idx) in detailData.members" :key="'m-' + idx" class="ta-qr-card">
          <view class="ta-qr-info">
            <text class="ta-qr-name">{{ m.studentName || '成员 ' + (idx + 1) }}</text>
            <text class="ta-qr-status" :class="{ 'ta-qr-status--done': m.writeoffTime }">
              {{ m.writeoffTime ? '已核销' : '待核销' }}
            </text>
          </view>
          <canvas v-if="m.qrCode && !m.writeoffTime" :canvas-id="'qr-' + idx" class="ta-qr-canvas"></canvas>
        </view>
      </view>
    </template>

    <!-- 赛事选择弹窗 -->
    <view v-if="showContestPicker" class="ta-mask" @tap="showContestPicker = false">
      <view class="ta-picker" @tap.stop>
        <view class="ta-picker__header">
          <text class="ta-picker__title">选择活动</text>
          <text class="ta-picker__close" @tap="showContestPicker = false">✕</text>
        </view>
        <scroll-view scroll-y class="ta-picker__body">
          <view v-for="c in contestList" :key="c.contestId" class="ta-picker__item" @tap="selectContest(c)">
            <text class="ta-picker__name">{{ c.contestName }}</text>
          </view>
          <view v-if="!contestList.length" class="ta-picker__empty">暂无可预约活动</view>
        </scroll-view>
      </view>
    </view>
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
.ta-page { min-height: 100vh; padding-bottom: calc(140rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }

.ta-header { display: flex; align-items: center; padding: 0 32rpx; height: 112rpx; padding-top: 88rpx; background: #fff; border-bottom: 2rpx solid var(--jst-color-border); position: sticky; top: 0; z-index: 40; }
.ta-header__back { width: 72rpx; height: 72rpx; border-radius: var(--jst-radius-sm); background: var(--jst-color-page-bg); display: flex; align-items: center; justify-content: center; font-size: 36rpx; margin-right: 24rpx; }
.ta-header__title { flex: 1; font-size: 34rpx; font-weight: 700; color: var(--jst-color-text); }

.ta-section { margin: 24rpx 32rpx 0; background: #fff; border-radius: var(--jst-radius-lg); box-shadow: var(--jst-shadow-card); overflow: hidden; padding: 28rpx; }
.ta-section__title { font-size: 30rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 20rpx; display: flex; align-items: center; gap: 12rpx; }
.ta-section__title::before { content: ''; width: 6rpx; height: 30rpx; background: #3F51B5; border-radius: 4rpx; }

/* 赛事选择 */
.ta-contest { padding: 20rpx; background: var(--jst-color-page-bg); border-radius: var(--jst-radius-md); }
.ta-contest__name { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.ta-contest__placeholder { font-size: 28rpx; color: var(--jst-color-text-tertiary); }

/* 表单 */
.ta-field { display: flex; align-items: center; margin-bottom: 16rpx; }
.ta-field__label { width: 160rpx; font-size: 26rpx; color: var(--jst-color-text-secondary); flex-shrink: 0; }
.ta-field__value { flex: 1; height: 64rpx; padding: 0 16rpx; background: var(--jst-color-page-bg); border-radius: var(--jst-radius-sm); font-size: 26rpx; color: var(--jst-color-text); display: flex; align-items: center; }
.ta-field__input { flex: 1; height: 64rpx; padding: 0 16rpx; background: var(--jst-color-page-bg); border-radius: var(--jst-radius-sm); font-size: 26rpx; color: var(--jst-color-text); }
.ta-field__textarea { flex: 1; min-height: 120rpx; padding: 16rpx; background: var(--jst-color-page-bg); border-radius: var(--jst-radius-sm); font-size: 26rpx; color: var(--jst-color-text); }

.ta-remaining { padding: 12rpx 16rpx; background: var(--jst-color-brand-soft); border-radius: var(--jst-radius-sm); font-size: 24rpx; color: var(--jst-color-brand); margin-top: 8rpx; }
.ta-remaining--danger { color: var(--jst-color-danger); font-weight: 700; }

.ta-subsection-title { font-size: 26rpx; font-weight: 600; color: var(--jst-color-text-secondary); margin-bottom: 12rpx; }

/* 学生勾选 */
.ta-check-item { display: flex; align-items: center; gap: 16rpx; padding: 16rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.ta-check-item:last-child { border-bottom: none; }
.ta-checkbox { width: 40rpx; height: 40rpx; border: 3rpx solid var(--jst-color-border); border-radius: var(--jst-radius-sm); display: flex; align-items: center; justify-content: center; font-size: 24rpx; color: #fff; flex-shrink: 0; }
.ta-checkbox--checked { background: #3F51B5; border-color: #3F51B5; }
.ta-check-name { font-size: 28rpx; font-weight: 600; color: var(--jst-color-text); }
.ta-check-meta { flex: 1; font-size: 22rpx; color: var(--jst-color-text-tertiary); text-align: right; }

.ta-empty-hint { padding: 24rpx; text-align: center; font-size: 26rpx; color: var(--jst-color-text-tertiary); }

.ta-total-count { margin-top: 16rpx; padding: 16rpx; background: var(--jst-color-brand-soft); border-radius: var(--jst-radius-sm); font-size: 28rpx; color: var(--jst-color-brand); }
.ta-total-count__num { font-size: 36rpx; font-weight: 800; }

/* 提交 */
.ta-footer { position: fixed; bottom: 0; left: 0; right: 0; background: rgba(255,255,255,0.97); border-top: 2rpx solid var(--jst-color-border); padding: 24rpx 32rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); z-index: 50; }
.ta-footer__btn { height: 96rpx; background: #3F51B5; border-radius: var(--jst-radius-md); color: #fff; font-size: 32rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; }
.ta-footer__btn--disabled { opacity: 0.5; }

/* 详情 */
.ta-detail-row { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.ta-detail-row:last-child { border-bottom: none; }
.ta-detail-label { font-size: 26rpx; color: var(--jst-color-text-tertiary); }
.ta-detail-val { font-size: 26rpx; color: var(--jst-color-text); font-weight: 600; }

/* QR */
.ta-qr-card { display: flex; align-items: center; justify-content: space-between; padding: 20rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.ta-qr-card:last-child { border-bottom: none; }
.ta-qr-info { flex: 1; }
.ta-qr-name { display: block; font-size: 28rpx; font-weight: 600; color: var(--jst-color-text); }
.ta-qr-status { display: block; margin-top: 8rpx; font-size: 24rpx; color: var(--jst-color-warning); font-weight: 600; }
.ta-qr-status--done { color: var(--jst-color-success); }
.ta-qr-canvas { width: 180rpx; height: 180rpx; flex-shrink: 0; }

/* 弹窗 */
.ta-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 100; display: flex; align-items: flex-end; }
.ta-picker { width: 100%; max-height: 70vh; background: #fff; border-radius: var(--jst-radius-lg) var(--jst-radius-lg) 0 0; overflow: hidden; }
.ta-picker__header { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 32rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ta-picker__title { font-size: 32rpx; font-weight: 700; color: var(--jst-color-text); }
.ta-picker__close { font-size: 36rpx; color: var(--jst-color-text-tertiary); padding: 8rpx; }
.ta-picker__body { max-height: 60vh; padding: 0 32rpx; }
.ta-picker__item { padding: 28rpx 0; border-bottom: 2rpx solid var(--jst-color-border); }
.ta-picker__name { font-size: 28rpx; font-weight: 600; color: var(--jst-color-text); }
.ta-picker__empty { padding: 64rpx; text-align: center; font-size: 28rpx; color: var(--jst-color-text-tertiary); }
</style>
