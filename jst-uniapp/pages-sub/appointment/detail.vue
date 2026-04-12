<!-- 中文注释: 个人预约 · 详情 + 二维码 swiper
     对应原型: 小程序原型图/writeoff-apply.png (学生视角)
     调用接口: GET /jst/wx/appointment/{id}
               POST /jst/wx/appointment/{id}/cancel
     二维码渲染: 这里直接展示 qrCode 字符串内容 + MP 可渲染的 canvas 组件
                若后端未返回图片二进制，前端使用 uni 的 qrcode 组件或 base64；此处采用
                可视化占位 (大字符串 + 使用 <image> 预留 hook), 真机可接入 uqrcode 组件 -->
<template>
  <view class="ad-page">
    <view class="ad-header" :style="{ paddingTop: navPaddingTop }">
      <text class="ad-header__title">{{ detail.contestName || '——' }}</text>
      <u-tag
        :text="statusLabel(detail.mainStatus)"
        :type="detail.mainStatus === 'completed' ? 'success' : (detail.mainStatus === 'cancelled' ? 'info' : 'warning')"
        shape="circle"
      ></u-tag>
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
      <!-- D2-1b: 使用 vendor uqrcode 真实渲染, swiper @change 懒渲染避免未挂载 canvas -->
      <swiper
        class="ad-swiper"
        :indicator-dots="writeoffItems.length > 1"
        indicator-color="rgba(0,0,0,.3)"
        @change="onSwiperChange"
      >
        <swiper-item v-for="(item, idx) in writeoffItems" :key="idx">
          <view class="ad-qr">
            <text class="ad-qr__name">{{ item.itemName || ('核销项 ' + (idx + 1)) }}</text>
            <view class="ad-qr__box">
              <canvas :canvas-id="'qr_' + idx" class="ad-qr__canvas" />
            </view>
            <u-tag
              :text="writeoffStatusLabel(item.status)"
              :type="item.status === 'used' ? 'success' : (item.status === 'voided' ? 'info' : 'warning')"
              shape="circle"
            ></u-tag>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <view v-if="canCancel" class="ad-footer">
      <u-button class="ad-footer__btn" type="error" :loading="cancelling" :disabled="cancelling" shape="circle" @click="onCancel">
        取消预约
      </u-button>
    </view>
  </view>
</template>

<script>
import { getAppointmentDetail, cancelAppointment } from '@/api/appointment'
import { makeQr } from '@/utils/qrcode-wrapper.js'

const STATUS_LABEL = { booked: '待使用', in_progress: '使用中', completed: '已使用', cancelled: '已取消', pending_pay: '待支付' }
const WO_LABEL = { unused: '未使用', used: '已核销', voided: '已作废' }

export default {
  data() {
    return {
      skeletonShow: true, // [visual-effect]
      detail: {},
      appointmentId: null,
      cancelling: false,
      // D2-1b: swiper 懒渲染已绘制索引集
      renderedIdx: new Set()
    }
  },
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
      // 数据切换 → 清空已渲索引 + 初次进入画 index 0 (等 swiper DOM 挂载)
      this.renderedIdx = new Set()
      this.$nextTick(() => setTimeout(() => this.renderAt(0), 80))
    },
    // D2-1b: swiper @change 懒渲染, 避免非激活态 canvas 未挂载
    onSwiperChange(e) {
      const idx = (e && e.detail && e.detail.current) || 0
      this.renderAt(idx)
    },
    async renderAt(idx) {
      if (this.renderedIdx.has(idx)) return
      const item = this.writeoffItems[idx]
      if (!item || !item.qrCode) return
      try {
        await makeQr({ canvasId: 'qr_' + idx, text: item.qrCode, size: 360, context: this })
        this.renderedIdx.add(idx)
      } catch (e) {}
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
@import '@/styles/design-tokens.scss';

.ad-page { min-height: 100vh; padding-bottom: calc(200rpx + env(safe-area-inset-bottom)); background: $jst-bg-page; }
.ad-header { padding: 72rpx 32rpx 48rpx; background: linear-gradient(135deg, $jst-brand, $jst-brand-light); color: $jst-text-inverse; }
.ad-header__title { display: block; font-size: 36rpx; font-weight: 600; }

.ad-section { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.ad-kv { display: flex; padding: 14rpx 0; font-size: 26rpx; border-bottom: 2rpx solid $jst-border; }
.ad-kv:last-child { border-bottom: none; }
.ad-kv__k { width: 160rpx; color: $jst-text-secondary; }
.ad-kv__v { flex: 1; color: $jst-text-primary; }

.ad-qrwrap { margin: 24rpx 32rpx 0; padding: 28rpx 32rpx; background: $jst-bg-card; border-radius: $jst-radius-md; box-shadow: $jst-shadow-sm; }
.ad-qrwrap__title { display: block; font-size: 28rpx; font-weight: 600; color: $jst-text-primary; margin-bottom: 20rpx; }
.ad-swiper { height: 720rpx; }
.ad-qr { display: flex; flex-direction: column; align-items: center; padding: 20rpx; }
.ad-qr__name { font-size: 28rpx; font-weight: 600; color: $jst-text-primary; }
.ad-qr__box { width: 500rpx; height: 500rpx; margin-top: 24rpx; background: $jst-bg-card; border: 2rpx solid $jst-border; border-radius: $jst-radius-md; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24rpx; box-sizing: border-box; position: relative; }
.ad-qr__canvas { width: 400rpx; height: 400rpx; }
.ad-qr__payload { margin-top: 12rpx; font-size: 20rpx; color: $jst-text-secondary; word-break: break-all; text-align: center; }

.ad-footer { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom)); background: $jst-bg-card; box-shadow: $jst-shadow-sm; }
::v-deep .ad-footer__btn.u-button { height: 88rpx; font-size: $jst-font-md; font-weight: $jst-weight-semibold; }
</style>
