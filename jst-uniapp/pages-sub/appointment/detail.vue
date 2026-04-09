<!-- 中文注释: 个人预约 · 详情 + 二维码 swiper
     对应原型: 小程序原型图/writeoff-apply.png (学生视角)
     调用接口: GET /jst/wx/appointment/{id}
               POST /jst/wx/appointment/{id}/cancel
     二维码渲染: 这里直接展示 qrCode 字符串内容 + MP 可渲染的 canvas 组件
                若后端未返回图片二进制，前端使用 uni 的 qrcode 组件或 base64；此处采用
                可视化占位 (大字符串 + 使用 <image> 预留 hook), 真机可接入 uqrcode 组件 -->
<template>
  <view class="ad-page">
    <view class="ad-header">
      <text class="ad-header__title">{{ detail.contestName || '——' }}</text>
      <text :class="['ad-header__status', 'ad-header__status--' + detail.mainStatus]">{{ statusLabel(detail.mainStatus) }}</text>
    </view>

    <view class="ad-section">
      <view class="ad-kv"><text class="ad-kv__k">预约人</text><text class="ad-kv__v">{{ detail.participantName || '--' }}</text></view>
      <view class="ad-kv"><text class="ad-kv__k">日期</text><text class="ad-kv__v">{{ formatDate(detail.appointmentDate) }}</text></view>
      <view class="ad-kv"><text class="ad-kv__k">场次</text><text class="ad-kv__v">{{ detail.sessionCode || '--' }}</text></view>
      <view v-if="detail.teamAppointment" class="ad-kv"><text class="ad-kv__k">所属团队</text><text class="ad-kv__v">{{ detail.teamName || detail.teamNo || '--' }}</text></view>
      <view class="ad-kv"><text class="ad-kv__k">类型</text><text class="ad-kv__v">{{ detail.individualAppointment ? '个人预约' : (detail.teamAppointment ? '团队预约' : '--') }}</text></view>
    </view>

    <view v-if="writeoffItems.length" class="ad-qrwrap">
      <text class="ad-qrwrap__title">核销二维码 ({{ writeoffItems.length }})</text>
      <swiper class="ad-swiper" :indicator-dots="writeoffItems.length > 1" indicator-color="rgba(0,0,0,.3)" indicator-active-color="#F5A623">
        <swiper-item v-for="(item, idx) in writeoffItems" :key="item.writeoffItemId || idx">
          <view class="ad-qr">
            <text class="ad-qr__name">{{ item.itemName || ('核销项 ' + (idx + 1)) }}</text>
            <view class="ad-qr__box">
              <text class="ad-qr__payload">{{ item.qrCode || '--' }}</text>
            </view>
            <text :class="['ad-qr__status', 'ad-qr__status--' + item.status]">{{ writeoffStatusLabel(item.status) }}</text>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <view v-if="canCancel" class="ad-footer">
      <button class="ad-footer__btn" @tap="onCancel" :disabled="cancelling">{{ cancelling ? '取消中...' : '取消预约' }}</button>
    </view>
  </view>
</template>

<script>
import { getAppointmentDetail, cancelAppointment } from '@/api/appointment'

const STATUS_LABEL = { booked: '待使用', in_progress: '使用中', completed: '已使用', cancelled: '已取消', pending_pay: '待支付' }
const WO_LABEL = { unused: '未使用', used: '已核销', voided: '已作废' }

export default {
  data() { return { detail: {}, appointmentId: null, cancelling: false } },
  computed: {
    writeoffItems() { return (this.detail && this.detail.writeoffItems) || [] },
    canCancel() { return ['booked', 'pending_pay'].includes(this.detail.mainStatus) }
  },
  onLoad(query) {
    this.appointmentId = query && query.id
    if (this.appointmentId) this.load()
  },
  methods: {
    async load() {
      try { this.detail = (await getAppointmentDetail(this.appointmentId)) || {} } catch (e) {}
    },
    onCancel() {
      uni.showModal({
        title: '取消预约',
        content: '确认取消本次预约？已支付单本期不自动退款，请联系客服。',
        success: async (res) => {
          if (!res.confirm) return
          this.cancelling = true
          try {
            await cancelAppointment(this.appointmentId)
            uni.showToast({ title: '已取消', icon: 'success' })
            this.load()
          } catch (e) {
            if (e && e.code === 30093) uni.showToast({ title: '已有核销项，无法取消', icon: 'none' })
          } finally { this.cancelling = false }
        }
      })
    },
    formatDate(v) { if (!v) return '--'; return String(v).slice(0, 10) },
    statusLabel(s) { return STATUS_LABEL[s] || s || '--' },
    writeoffStatusLabel(s) { return WO_LABEL[s] || s || '--' }
  }
}
</script>

<style scoped lang="scss">
.ad-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: var(--jst-color-page-bg); }
.ad-header { padding: 72rpx 32rpx 48rpx; background: linear-gradient(135deg, var(--jst-color-brand), var(--jst-color-brand-light)); color: #fff; }
.ad-header__title { display: block; font-size: 36rpx; font-weight: 800; }
.ad-header__status { display: inline-block; margin-top: 16rpx; padding: 6rpx 20rpx; border-radius: var(--jst-radius-full); background: var(--jst-color-white-18); color: #fff; font-size: 22rpx; font-weight: 700; }
.ad-header__status--completed { background: rgba(39,174,96,0.24); }
.ad-header__status--cancelled { background: var(--jst-color-white-18); }

.ad-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.ad-kv { display: flex; padding: 14rpx 0; font-size: 26rpx; border-bottom: 2rpx solid var(--jst-color-border); }
.ad-kv:last-child { border-bottom: none; }
.ad-kv__k { width: 160rpx; color: var(--jst-color-text-tertiary); }
.ad-kv__v { flex: 1; color: var(--jst-color-text); }

.ad-qrwrap { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: var(--jst-color-card-bg); border-radius: var(--jst-radius-md); box-shadow: var(--jst-shadow-card); }
.ad-qrwrap__title { display: block; font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); margin-bottom: 20rpx; }
.ad-swiper { height: 720rpx; }
.ad-qr { display: flex; flex-direction: column; align-items: center; padding: 20rpx; }
.ad-qr__name { font-size: 28rpx; font-weight: 700; color: var(--jst-color-text); }
.ad-qr__box { width: 500rpx; height: 500rpx; margin-top: 24rpx; background: #fff; border: 2rpx solid var(--jst-color-border); border-radius: var(--jst-radius-md); display: flex; align-items: center; justify-content: center; padding: 24rpx; box-sizing: border-box; }
.ad-qr__payload { font-size: 22rpx; color: var(--jst-color-text-secondary); word-break: break-all; text-align: center; }
.ad-qr__status { margin-top: 20rpx; padding: 6rpx 24rpx; border-radius: var(--jst-radius-full); font-size: 22rpx; background: var(--jst-color-warning-soft); color: var(--jst-color-warning); }
.ad-qr__status--used { background: var(--jst-color-success-soft); color: var(--jst-color-success); }
.ad-qr__status--voided { background: var(--jst-color-gray-soft); color: var(--jst-color-text-tertiary); }

.ad-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: var(--jst-color-card-bg); box-shadow: 0 -8rpx 24rpx rgba(16,88,160,0.08); }
.ad-footer__btn { height: 88rpx; line-height: 88rpx; border-radius: var(--jst-radius-md); background: var(--jst-color-danger-soft); color: var(--jst-color-danger); font-size: 30rpx; font-weight: 800; border: none; }
</style>
